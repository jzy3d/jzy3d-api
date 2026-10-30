#!/usr/bin/env python3
"""Lists every Jzy3d commit a project does not have yet.

Usage : jzy3d-changes.py --api <jzy3d-api-clone> [--to <git-ref>] [--as-of <timestamp>] [--recent-days N] [--offline] <project-dir>

1. Finds the Jzy3d artifacts the project resolves (mvn dependency:list) and, for each, the exact build it
   uses, to the minute :
   * a release : date of its tag jzy3d-all-X.Y.Z (or of its release branch before 2.0.0),
   * a SNAPSHOT : the timestamp of the snapshot downloaded from the Maven repository, or the date of a
     local `mvn install` when it is newer (Maven then uses the local copy).
   Also gives the latest snapshot and release published on the Jzy3d Maven repository.
2. Finds the commit of the jzy3d-api clone matching the oldest build in use : the tag of a release, or the
   last commit of master before the snapshot timestamp (snapshots are deployed by the CI after each push).
3. Lists all commits since that commit up to --to (default origin/master), with the modules they touch,
   marking the ones touching modules the project uses.
4. Lists the changes made in the last --recent-days days before --to in modules the project does not use :
   innovations it could adopt, even if its version already contains them.
"""
import argparse
import datetime as dt
import glob
import os
import re
import subprocess
import sys
import urllib.request

MAVEN = "https://maven.jzy3d.org"
M2 = os.path.expanduser("~/.m2/repository/org/jzy3d")


def run(cmd, cwd=None):
    return subprocess.run(cmd, cwd=cwd, capture_output=True, text=True).stdout


def git(api, *args):
    return run(["git", "-C", api] + list(args)).strip()


def utc(stamp):
    """Maven timestamps, 20261025.202815 or 20261025202815, are UTC."""
    digits = stamp.replace(".", "")
    return dt.datetime.strptime(digits[:14], "%Y%m%d%H%M%S").replace(tzinfo=dt.timezone.utc)


def metadata_value(path, name):
    m = re.search(r"<%s>([^<]+)</%s>" % (name, name), open(path).read()) if os.path.exists(path) else None
    return m.group(1) if m else None


def resolved_artifacts(project, offline):
    out = run(["mvn", "dependency:list", "-DincludeGroupIds=org.jzy3d"] + (["-o"] if offline else []), cwd=project)
    found = {}
    for m in re.finditer(r"org\.jzy3d:([\w.-]+):(?:jar|pom|test-jar)(?::[\w-]+)?:([\w.-]+):(\w+)", out):
        found[m.group(1)] = m.group(2)
    return found


def snapshot_build(artifact, version):
    """(build time, origin) of the snapshot Maven uses : the newest of the local install and the downloads."""
    folder = os.path.join(M2, artifact, version)
    candidates = []
    local = metadata_value(os.path.join(folder, "maven-metadata-local.xml"), "lastUpdated")
    if local:
        candidates.append((utc(local), "local mvn install"))
    for f in glob.glob(os.path.join(folder, "maven-metadata-*.xml")):
        repo = re.search(r"maven-metadata-(.+)\.xml", f).group(1)
        stamp = metadata_value(f, "timestamp")
        if repo != "local" and stamp:
            candidates.append((utc(stamp), "snapshot %s-%s from repository %s" % (
                version.replace("-SNAPSHOT", ""), stamp, repo) + "-" + (metadata_value(f, "buildNumber") or "?")))
    return max(candidates) if candidates else (None, "not in ~/.m2")


def published(artifact, version):
    try:
        if version.endswith("-SNAPSHOT"):
            url = "%s/snapshots/org/jzy3d/%s/%s/maven-metadata.xml" % (MAVEN, artifact, version)
            xml = urllib.request.urlopen(url, timeout=20).read().decode()
            stamp = re.search(r"<timestamp>([^<]+)", xml)
            return "latest snapshot on %s : %s (%s UTC)" % (MAVEN, stamp.group(1), utc(stamp.group(1))) if stamp else None
        url = "%s/releases/org/jzy3d/%s/maven-metadata.xml" % (MAVEN, artifact)
        xml = urllib.request.urlopen(url, timeout=20).read().decode()
        return "latest release on %s : %s" % (MAVEN, re.search(r"<release>([^<]+)", xml).group(1))
    except Exception as e:
        return "%s not reachable (%s)" % (MAVEN, e)


def module_of(path, modules):
    """Module of a file : the deepest of its first folders that is a module (modules moved into folders)."""
    parts = path.split("/")[:3]
    found = [p for p in parts if p in modules]
    return found[-1] if found else "(root)"


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("project")
    p.add_argument("--api", required=True, help="clone of jzy3d-api, fetched to know the latest commits")
    p.add_argument("--to", default="origin/master", help="git ref of the version to upgrade to")
    p.add_argument("--recent-days", type=int, default=180)
    p.add_argument("--offline", action="store_true")
    p.add_argument("--as-of", help="Maven timestamp of the build in use (20261013.195450), instead of detecting it")
    a = p.parse_args()

    git(a.api, "fetch", "-q", "--tags", "origin")
    modules = set(re.findall(r"<artifactId>(jzy3d-[\w.-]+)</artifactId>",
                             run(["git", "-C", a.api, "grep", "-h", "<artifactId>jzy3d-", "--", "*pom.xml"])))
    modules |= {d for d in git(a.api, "log", "--format=", "--name-only", "--diff-filter=A", "--", "*/pom.xml").split()
                for d in [d.split("/")[-2] if "/" in d else ""] if d.startswith("jzy3d-")}

    # 1. Builds in use
    resolved = resolved_artifacts(a.project, a.offline)
    if not resolved:
        sys.exit("No org.jzy3d artifact resolved in %s (run mvn dependency:list there to see why)" % a.project)
    # Other org.jzy3d artifacts (extensions, products) are not built from jzy3d-api
    used = {k: v for k, v in resolved.items() if k in modules}
    others = sorted(k for k in resolved if k not in modules)
    if others:
        print("Other org.jzy3d artifacts, not from jzy3d-api : %s\n" % ", ".join(others))
    if not used:
        sys.exit("No jzy3d-api artifact resolved in %s" % a.project)
    print("# Jzy3d builds used by %s\n" % a.project)
    builds = []
    for artifact, version in sorted(used.items()):
        if version.endswith("-SNAPSHOT"):
            when, origin = snapshot_build(artifact, version)
        else:
            ref = "jzy3d-all-%s" % version if git(a.api, "tag", "-l", "jzy3d-all-%s" % version) else "origin/%s" % version
            iso = git(a.api, "log", "-1", "--format=%cI", ref)
            when, origin = (dt.datetime.fromisoformat(iso) if iso else None), "release, %s" % ref
        print("%-36s %-16s %s  (%s)" % (artifact, version, when.strftime("%Y-%m-%d %H:%M UTC") if when else "?", origin))
        if when:
            builds.append((when, artifact, version, origin))
    oldest = min(builds)
    if a.as_of:
        oldest = (utc(a.as_of), oldest[1], oldest[2], "given with --as-of")
    core = used.get("jzy3d-core") and "jzy3d-core" or oldest[1]
    print("\n%s" % published(core, used[core]))
    tags = git(a.api, "tag", "-l", "jzy3d-all-*", "--sort=-creatordate").split()
    print("latest release tag : %s (%s)" % (tags[0], git(a.api, "log", "-1", "--format=%cs", tags[0])) if tags else "")

    # 2. Commit matching the oldest build
    when, artifact, version, origin = oldest
    if origin.startswith("release"):
        base = origin.split(", ")[1]
    else:
        base = git(a.api, "rev-list", "-1", "--before=%s" % when.isoformat(), a.to)
    print("\nOldest build in use : %s %s, %s UTC -> commit %s" % (
        artifact, version, when.strftime("%Y-%m-%d %H:%M"), git(a.api, "log", "-1", "--format=%h %cI %s", base)))
    if origin == "local mvn install":
        print("Built locally : the commit is the last one of %s before the build, local uncommitted changes are unknown" % a.to)
    print("Upgrading to %s : %s" % (a.to, git(a.api, "log", "-1", "--format=%h %cI %s", a.to)))

    # 3. Commits since
    log = git(a.api, "log", "--no-merges", "--reverse", "--format=@@%h|%cI|%s", "--name-only", "%s..%s" % (base, a.to))
    commits = []
    for chunk in log.split("@@")[1:]:
        head, *files = [l for l in chunk.splitlines() if l.strip()]
        sha, date, subject = head.split("|", 2)
        touched = sorted({module_of(f, modules) for f in files})
        commits.append((sha, date[:10], subject, touched))
    used_modules = set(used)
    print("\n# %d commits since the build in use\n" % len(commits))
    for sha, date, subject, touched in commits:
        mark = "*" if used_modules & set(touched) or touched == ["(root)"] else " "
        print("%s %s %s %s  [%s]" % (mark, sha, date, subject, ", ".join(touched)))
    print("\n(* : touches a module the project uses, or the build)")

    end = dt.datetime.fromisoformat(git(a.api, "log", "-1", "--format=%cI", a.to))
    window = git(a.api, "log", "--no-merges", "--reverse", "--format=@@%h|%cI|%s", "--name-only",
                 "--since=%s" % (end - dt.timedelta(days=a.recent_days)).isoformat(), a.to)
    recent = []
    for chunk in window.split("@@")[1:]:
        head, *files = [l for l in chunk.splitlines() if l.strip()]
        sha, date, subject = head.split("|", 2)
        touched = sorted({module_of(f, modules) for f in files} - {"(root)"})
        if touched and not used_modules & set(touched):
            recent.append((sha, date[:10], subject, touched))
    print("\n# Recent changes in modules the project does not use (%d days before %s)\n" % (a.recent_days, a.to))
    by_module = {}
    for sha, date, subject, touched in recent:
        for m in touched:
            by_module.setdefault(m, []).append("%s %s %s" % (sha, date, subject))
    for m in sorted(by_module):
        print("%s (%d)" % (m, len(by_module[m])))
        for line in by_module[m]:
            print("   " + line)


if __name__ == "__main__":
    main()
