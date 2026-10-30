#!/usr/bin/env python3
"""Checks that projects use the latest Jzy3d version and, if not, upgrades them.

Usage : jzy3d-upgrade.py [--api <jzy3d-api-clone>] [--to <version>] [--apply] [--build] [--offline] <dir>...

For each pom.xml found under the given directories (target folders excepted), it
* sets the version of Jzy3d dependencies and of a jzy3d-all parent to the target version,
* applies the dependency and class changes of ../migrations.tsv,
then prints the changes as a diff. Files are only written with --apply. With --build, each given
directory holding a pom.xml is then built (mvn test-compile) to reveal what the migration table does not
cover (changed methods, removed classes) : look for them in the release notes, doc/release-X.Y.Z.md.

The target version is --to, or the version of the root pom.xml of the jzy3d-api clone given with --api,
or the latest release of the Jzy3d Maven repository.
"""
import argparse
import difflib
import fnmatch
import os
import re
import subprocess
import sys
import urllib.request

SKILL_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JZY3D_GROUPS = ("org.jzy3d", "${project.groupId}")
DEPENDENCY = re.compile(r"[ \t]*<dependency>(?:(?!</dependency>).)*?</dependency>[ \t]*\n?", re.S)
COMMENT = re.compile(r"<!--.*?-->", re.S)
# APIs removed from recent JDKs, which break projects built with a newer JDK than before
JDK_REMOVALS = {
    "java.applet": "the Applet API is removed from the JDK (absent from JDK 26) : remove or rewrite the applet code",
    "method stop()": "Thread.stop() is removed from the JDK (absent from JDK 26) : interrupt the thread instead",
}


def tag(xml, name):
    m = re.search(r"<%s>\s*([^<]*?)\s*</%s>" % (name, name), xml)
    return m.group(1) if m else None


def read_migrations():
    artifacts, classes, requires = [], [], []
    with open(os.path.join(SKILL_DIR, "migrations.tsv")) as f:
        for line in f:
            if not line.strip() or line.startswith("#"):
                continue
            kind, old, new, since, note = (line.rstrip("\n").split("\t") + [""] * 5)[:5]
            {"artifact": artifacts, "class": classes, "require": requires}[kind].append((old, new, since, note))
    return artifacts, classes, requires


def api_artifacts_and_version(api):
    """Artifact ids of the modules of a jzy3d-api clone, and the version of its root pom."""
    artifacts = set()
    for root, dirs, files in os.walk(api):
        dirs[:] = [d for d in dirs if d not in ("target", ".git", "node_modules")]
        if "pom.xml" in files:
            xml = COMMENT.sub("", open(os.path.join(root, "pom.xml")).read())
            xml = re.sub(r"<parent>.*?</parent>", "", xml, flags=re.S)
            artifact = tag(xml, "artifactId")
            if artifact:
                artifacts.add(artifact)
    root_pom = re.sub(r"<parent>.*?</parent>", "", open(os.path.join(api, "pom.xml")).read(), flags=re.S)
    return artifacts, tag(root_pom, "version")


def latest_release():
    url = "https://maven.jzy3d.org/releases/org/jzy3d/jzy3d-all/maven-metadata.xml"
    with urllib.request.urlopen(url, timeout=20) as r:
        return tag(r.read().decode(), "release")


def find_poms(dirs):
    for d in dirs:
        for root, sub, files in os.walk(d):
            sub[:] = sorted(s for s in sub if s not in ("target", ".git", "node_modules", "bin"))
            if "pom.xml" in files:
                yield os.path.join(root, "pom.xml")


def in_comment(xml, pos):
    return any(m.start() <= pos < m.end() for m in COMMENT.finditer(xml))


def declared(xml, group, artifact):
    for m in DEPENDENCY.finditer(xml):
        if in_comment(xml, m.start()):
            continue
        if tag(m.group(0), "artifactId") == artifact and tag(m.group(0), "groupId") in (group, "${project.groupId}"):
            return True
    return False


def declared_keys(xml):
    keys = []
    for m in DEPENDENCY.finditer(xml):
        if not in_comment(xml, m.start()):
            group = tag(m.group(0), "groupId")
            keys.append("%s:%s" % ("org.jzy3d" if group == "${project.groupId}" else group, tag(m.group(0), "artifactId")))
    return keys


def upgrade_pom(path, target, api_artifacts, artifact_migrations, report, requires=()):
    xml = original = open(path).read()
    renames = {old: (new, since, note) for old, new, since, note in artifact_migrations}

    # Dependencies renamed, replaced or removed
    out, last = [], 0
    for m in DEPENDENCY.finditer(xml):
        block = m.group(0)
        group, artifact = tag(block, "groupId"), tag(block, "artifactId")
        key = "%s:%s" % ("org.jzy3d" if group == "${project.groupId}" else group, artifact)
        if in_comment(xml, m.start()) or key not in renames:
            continue
        new, since, note = renames[key]
        out.append(xml[last:m.start()])
        last = m.end()
        if new == "-":
            report.append("remove %s (%s : %s)" % (key, since, note))
        elif declared(xml, *new.split(":")):
            report.append("remove %s, %s already declared (%s : %s)" % (key, new, since, note))
        else:
            new_group, new_artifact = new.split(":")
            block = block.replace("<artifactId>%s</artifactId>" % artifact, "<artifactId>%s</artifactId>" % new_artifact)
            if group != "${project.groupId}":
                block = block.replace("<groupId>%s</groupId>" % group, "<groupId>%s</groupId>" % new_group)
            out.append(block)
            report.append("replace %s by %s (%s : %s)" % (key, new, since, note))
    xml = "".join(out) + xml[last:]

    # Versions of Jzy3d dependencies and of a jzy3d-all parent
    properties = {}
    for m in DEPENDENCY.finditer(xml):
        block = m.group(0)
        if in_comment(xml, m.start()) or tag(block, "groupId") not in JZY3D_GROUPS:
            continue
        if tag(block, "artifactId") not in api_artifacts:
            continue
        version = tag(block, "version")
        if version is None or version == target:
            continue
        if version == "${project.version}":
            own = tag(re.sub(r"<parent>.*?</parent>", "", xml, flags=re.S), "version")
            if own != target:
                properties["project.version"] = own
            continue
        if version.startswith("${"):
            properties[version[2:-1]] = None
            continue
        new_block = block.replace("<version>%s</version>" % version, "<version>%s</version>" % target)
        xml = xml.replace(block, new_block, 1)
        report.append("%s %s -> %s" % (tag(block, "artifactId"), version, target))
    for prop, own in properties.items():
        if prop == "project.version":
            report.append("Jzy3d dependencies follow the project version %s : change the project version to %s, or set them to %s" % (own, target, target))
            continue
        m = re.search(r"<%s>([^<$]+)</%s>" % (re.escape(prop), re.escape(prop)), xml)
        if m and m.group(1) != target:
            xml = xml[:m.start(1)] + target + xml[m.end(1):]
            report.append("property %s %s -> %s" % (prop, m.group(1), target))
        elif not m:
            report.append("Jzy3d version given by property %s, defined outside this pom : set it to %s" % (prop, target))
    parent = re.search(r"<parent>.*?</parent>", xml, re.S)
    if parent and tag(parent.group(0), "artifactId") == "jzy3d-all" and tag(parent.group(0), "version") != target:
        old = tag(parent.group(0), "version")
        xml = xml.replace(parent.group(0), parent.group(0).replace("<version>%s</version>" % old, "<version>%s</version>" % target))
        report.append("parent jzy3d-all %s -> %s" % (old, target))

    # Dependencies the project must add by itself
    keys = declared_keys(xml)
    for needs, required, since, note in requires:
        if any(fnmatch.fnmatch(k, needs) for k in keys) and not any(fnmatch.fnmatch(k, required) for k in keys):
            report.append("add a dependency %s (%s : %s)" % (required, since, note))

    # Exclusions of artifacts that changed can not be migrated blindly
    for old in renames:
        if re.search(r"<exclusion>\s*<groupId>[^<]*</groupId>\s*<artifactId>%s</artifactId>" % re.escape(old.split(":")[1]), xml):
            report.append("check the exclusion of %s by hand" % old)
    return original, xml


def upgrade_sources(dirs, class_migrations):
    changes = []
    for d in dirs:
        for root, sub, files in os.walk(d):
            sub[:] = [s for s in sub if s not in ("target", ".git", "node_modules", "bin")]
            for f in files:
                if not f.endswith(".java"):
                    continue
                path = os.path.join(root, f)
                original = text = open(path, encoding="utf-8", errors="surrogateescape").read()
                notes = []
                for old, new, since, note in class_migrations:
                    if re.search(r"\b%s\b" % re.escape(old), text):
                        text = re.sub(r"\b%s\b" % re.escape(old), new, text)
                        notes.append("%s -> %s (%s : %s)" % (old, new, since, note))
                if text != original:
                    changes.append((path, original, text, notes))
    return changes


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("dirs", nargs="+")
    p.add_argument("--api", help="clone of jzy3d-api, giving the module list and the target version")
    p.add_argument("--to", help="target Jzy3d version")
    p.add_argument("--apply", action="store_true", help="write the changes (default : only show them)")
    p.add_argument("--build", action="store_true", help="build each project with mvn test-compile after --apply")
    p.add_argument("--offline", action="store_true", help="build with mvn -o")
    a = p.parse_args()

    artifact_migrations, class_migrations, requires = read_migrations()
    api_artifacts, api_version = api_artifacts_and_version(a.api) if a.api else (set(), None)
    if not api_artifacts:
        api_artifacts = {"jzy3d-api", "jzy3d-all"} | {re.sub("^org.jzy3d:", "", n) for _, n, _, _ in artifact_migrations}
        print("No --api clone : only versions of jzy3d-api, jzy3d-all and of the migration targets are checked\n")
    target = a.to or api_version or latest_release()
    print("Target Jzy3d version : %s\n" % target)

    outdated, changed = 0, 0
    for pom in find_poms(a.dirs):
        report = []
        original, upgraded = upgrade_pom(pom, target, api_artifacts, artifact_migrations, report, requires)
        if not report:
            continue
        outdated += 1
        print("== %s" % pom)
        for r in report:
            print("   - %s" % r)
        if upgraded != original:
            changed += 1
            sys.stdout.writelines(difflib.unified_diff(original.splitlines(True), upgraded.splitlines(True), pom, pom, n=1))
            if a.apply:
                open(pom, "w").write(upgraded)
        print()

    for path, original, text, notes in upgrade_sources(a.dirs, class_migrations):
        outdated += 1
        changed += 1
        print("== %s" % path)
        for n in notes:
            print("   - %s" % n)
        if a.apply:
            open(path, "w", encoding="utf-8", errors="surrogateescape").write(text)
        print()

    if not outdated:
        print("Up to date with Jzy3d %s" % target)
    else:
        print("%d file(s) %s, %d file(s) with notes to handle by hand" % (
            changed, "upgraded" if a.apply else "to upgrade (run again with --apply to write them)", outdated - changed))

    if a.build:
        if not a.apply and changed:
            print("\nNot building : the changes above are not written (add --apply)")
            return 1
        failed = []
        for d in a.dirs:
            if not os.path.exists(os.path.join(d, "pom.xml")):
                continue
            offline = ["-o"] if a.offline else []
            r = subprocess.run(["mvn", "-q", "test-compile"] + offline, cwd=d, capture_output=True, text=True)
            errors = [l for l in r.stdout.splitlines() if l.startswith("[ERROR]")][:8]
            print("\n== build %s : %s" % (d, "OK" if r.returncode == 0 else "FAILED"))
            for l in errors:
                print("   " + l)
            for api, hint in JDK_REMOVALS.items():
                if any(api in l for l in errors):
                    print("   -> %s" % hint)
            if r.returncode:
                failed.append(d)
            # Outdated artifacts can also come from a dependency of the project
            tree = subprocess.run(["mvn", "dependency:tree"] + offline, cwd=d, capture_output=True, text=True).stdout
            for old, new, since, note in artifact_migrations:
                for l in tree.splitlines():
                    if ":%s:" % old.split(":")[1] in l and old.split(":")[0] in l:
                        print("   transitive %s : exclude it from the dependency bringing it, or upgrade that dependency" % l.split("- ")[-1])
        return 1 if failed else 0
    return 1 if changed and not a.apply else 0


if __name__ == "__main__":
    sys.exit(main())
