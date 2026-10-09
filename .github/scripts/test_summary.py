#!/usr/bin/env python3
"""Summarize the Surefire reports of all modules and fail if a test failed.

Usage : test_summary.py <title> [root dir]

Writes a Markdown table of the test results per module, followed by the failed tests, to the
GitHub job summary when available, and exits with 1 if a test failed or errored.
"""
import glob
import os
import sys
import xml.etree.ElementTree as ET


def main():
    title = sys.argv[1] if len(sys.argv) > 1 else "Tests"
    root = sys.argv[2] if len(sys.argv) > 2 else "."

    modules = {}
    failed = []
    for path in sorted(glob.glob(os.path.join(root, "**/target/surefire-reports/TEST-*.xml"),
                                 recursive=True)):
        module = os.path.relpath(path, root).split(os.sep)[0]
        try:
            suite = ET.parse(path).getroot()
        except ET.ParseError:
            continue
        counts = modules.setdefault(module, {"tests": 0, "failed": 0, "skipped": 0})
        counts["tests"] += int(suite.get("tests", "0"))
        counts["failed"] += int(suite.get("failures", "0")) + int(suite.get("errors", "0"))
        counts["skipped"] += int(suite.get("skipped", "0"))
        for case in suite.iter("testcase"):
            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            if problem is not None:
                message = (problem.get("message") or problem.get("type") or "").splitlines()
                failed.append((module, case.get("classname", "").rsplit(".", 1)[-1],
                               case.get("name", ""), message[0][:200] if message else ""))

    lines = ["## " + title, "", "| Module | Tests | Failed | Skipped |", "|---|---|---|---|"]
    for module, counts in modules.items():
        lines.append("| {} {} | {} | {} | {} |".format("❌" if counts["failed"] else "✅", module,
                     counts["tests"], counts["failed"], counts["skipped"]))
    if failed:
        lines += ["", "### Failed tests", "", "| Module | Test | Message |", "|---|---|---|"]
        for module, clazz, name, message in failed:
            lines.append("| {} | {}.{} | {} |".format(module, clazz, name,
                                                      message.replace("|", "\\|")))

    markdown = "\n".join(lines) + "\n"
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as f:
            f.write(markdown)
    print(markdown)
    return 1 if failed or not modules else 0


if __name__ == "__main__":
    sys.exit(main())
