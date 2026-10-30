#!/usr/bin/env python3
"""Coverage of unit tests by zone of the repository (core, bindings, tools, tests, tutorials...).

Run from the root of the repository, after the tests ran with the JaCoCo agent :

    mvn clean install -Pcoverage
    python3 .github/scripts/coverage-by-zone.py

It merges the JaCoCo data of all modules, so a class is covered by the tests of any module (e.g.
jzy3d-core classes exercised by tests of the JOGL modules), writes the merged report in
target/coverage (HTML and CSV), and prints the coverage by zone and by module.
"""
import csv
import glob
import os
import subprocess
import sys

JACOCO = "0.8.14"
CLI = os.path.expanduser("~/.m2/repository/org/jacoco/org.jacoco.cli/%s/org.jacoco.cli-%s-nodeps.jar" % (JACOCO, JACOCO))
OUT = "target/coverage"

ZONES = [  # first matching prefix of the module folder wins
    ("core", "jzy3d-core"),
    ("bindings/jogl", "jzy3d-bindings/jzy3d-native-jogl"),
    ("bindings/panamagl", "jzy3d-bindings/jzy3d-native-panama-gl"),
    ("bindings/emulgl", "jzy3d-bindings/jzy3d-emul-gl"),
    ("bindings/emulgl", "jzy3d-bindings/jzy3d-jGL"),
    ("tools", "jzy3d-tools/"),
    ("tests", "jzy3d-tests/"),
    ("tutorials", "jzy3d-tutorials/"),
]


def zone_of(module):
    return next((z for z, prefix in ZONES if module.startswith(prefix)), "other")


def grade(ratio):
    return "good" if ratio >= 0.7 else "fair" if ratio >= 0.5 else "weak" if ratio >= 0.3 else "poor"


def pct(covered, missed):
    total = covered + missed
    return covered / total if total else 0.0


def main():
    modules = sorted(os.path.dirname(os.path.dirname(d)) for d in glob.glob("**/target/classes", recursive=True)
                     if os.path.exists(os.path.join(os.path.dirname(os.path.dirname(d)), "pom.xml")))
    modules = [m for m in modules if m and not m.startswith("target")]
    execs = glob.glob("**/target/jacoco.exec", recursive=True)
    if not execs:
        sys.exit("No target/jacoco.exec : run mvn clean install -Pcoverage first")

    if not os.path.exists(CLI):
        subprocess.run(["mvn", "-q", "dependency:get", "-Dartifact=org.jacoco:org.jacoco.cli:%s:jar:nodeps" % JACOCO],
                       check=True)

    # Index classes by module, to give each line of the merged report back to its module
    module_of_class = {}
    for m in modules:
        root = os.path.join(m, "target", "classes")
        for f in glob.glob(os.path.join(root, "**", "*.class"), recursive=True):
            name = os.path.relpath(f, root)[:-len(".class")]
            if "$" not in name:
                module_of_class[name] = m

    os.makedirs(OUT, exist_ok=True)
    cmd = ["java", "-jar", CLI, "report"] + execs
    for m in modules:
        cmd += ["--classfiles", os.path.join(m, "target", "classes")]
        for src in ("src/main/java", "src"):
            if os.path.isdir(os.path.join(m, src)):
                cmd += ["--sourcefiles", os.path.join(m, src)]
                break
    cmd += ["--csv", os.path.join(OUT, "coverage.csv"), "--html", os.path.join(OUT, "html"), "--name", "Jzy3d unit tests"]
    r = subprocess.run(cmd, capture_output=True, text=True)
    if r.returncode:
        sys.exit(r.stderr or r.stdout)

    # module -> [line covered, line missed, branch covered, branch missed, classes, classes with a covered line]
    stats = {m: [0, 0, 0, 0, 0, 0] for m in modules}
    with open(os.path.join(OUT, "coverage.csv")) as f:
        for row in csv.DictReader(f):
            name = (row["PACKAGE"].replace(".", "/") + "/" if row["PACKAGE"] else "") + row["CLASS"].split("$")[0].split(".")[0]
            s = stats[module_of_class.get(name, "?")] if name in module_of_class else None
            if s is None:
                continue
            lc, lm = int(row["LINE_COVERED"]), int(row["LINE_MISSED"])
            s[0] += lc; s[1] += lm
            s[2] += int(row["BRANCH_COVERED"]); s[3] += int(row["BRANCH_MISSED"])
            s[4] += 1; s[5] += 1 if lc else 0

    tests = {m: len(glob.glob(os.path.join(m, "src/test/**/Test*.java"), recursive=True)
                   + glob.glob(os.path.join(m, "src/test/**/*Test.java"), recursive=True)) for m in modules}

    zones = {}
    for m, s in stats.items():
        z = zones.setdefault(zone_of(m), [0] * 7)
        for i in range(6):
            z[i] += s[i]
        z[6] += tests[m]

    def line(label, s, n_tests):
        return "%-52s %6d %6.1f%% %6.1f%% %6.1f%% %6d  %s" % (
            label, s[0] + s[1], 100 * pct(s[0], s[1]), 100 * pct(s[2], s[3]), 100 * pct(s[5], s[4] - s[5]), n_tests,
            grade(pct(s[0], s[1])))

    header = "%-52s %6s %7s %7s %7s %6s  %s" % ("", "lines", "line", "branch", "class", "tests", "level")
    print("# Coverage by zone\n\n" + header)
    for z in sorted(zones, key=lambda z: [x for x, _ in ZONES].index(z) if z in [x for x, _ in ZONES] else 99):
        print(line(z, zones[z], zones[z][6]))
    print("\n# Coverage by module\n\n" + header)
    for m in sorted(stats, key=lambda m: (zone_of(m), m)):
        if stats[m][0] + stats[m][1]:
            print(line(m, stats[m], tests[m]))
    print("\nline/branch : covered lines/branches, class : classes with at least a covered line, "
          "tests : unit test classes in the module")
    print("level : line coverage >= 70% good, >= 50% fair, >= 30% weak, else poor")
    print("Merged report : %s/html/index.html" % OUT)


if __name__ == "__main__":
    main()
