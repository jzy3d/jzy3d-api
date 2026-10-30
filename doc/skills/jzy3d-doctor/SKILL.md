---
name: jzy3d-doctor
description: Checks that projects using Jzy3d are up to date and upgrades them to the latest Jzy3d version. Finds the exact Jzy3d build a project uses (to the minute, from the Maven repository or a local install), lists every Jzy3d commit since then, applies the dependency and class changes, builds the project, and summarizes what is new plus recent innovations in modules the project does not use. Also explains how to submit an improvement to Jzy3d as a pull request. Use when a user asks to upgrade, migrate, update or check Jzy3d in one or several projects (/jzy3d-doctor upgrade <dirs>), asks what changed since a Jzy3d version, or has build errors after changing the Jzy3d version.
---

# jzy3d-doctor

Migrate projects from the Jzy3d version they use to the latest Jzy3d version, and tell the user what they gain by doing so.

## Commands

The arguments of the skill tell what to do :

* `upgrade <dir>...` : check the projects under each directory and upgrade the outdated ones, following the procedure below. Directories may hold several projects (a folder of projects, an aggregator pom), they are processed in one pass.
* `check <dir>...` : same as `upgrade`, without writing anything (stop after step U3).
* no argument, or a question : answer it with the sources of sections 1 to 6.

Scripts next to this file, written to be run from anywhere :

| Script | Does |
|---|---|
| `scripts/jzy3d-changes.py --api <jzy3d-api-clone> [--to <ref>] [--as-of <timestamp>] [--recent-days N] [--offline] <project>` | Exact Jzy3d build used by the project, all commits since then, recent changes in modules the project does not use |
| `scripts/jzy3d-upgrade.py --api <jzy3d-api-clone> [--to <version>] [--apply] [--build] [--offline] <dir>...` | Upgrades Jzy3d versions and applies `migrations.tsv` to poms and Java sources, shows a diff, writes only with `--apply`, builds with `--build` |
| `scripts/jzy3d-versions.sh <project> [jzy3d-api-clone]` | Published versions, tags, release branches, website pages, and links to what is new |

They need a clone of `https://github.com/jzy3d/jzy3d-api` (the scripts fetch it) : use the one of the user if there is one, else clone it in a temporary folder.

## Upgrade procedure

**U1. Inventory.** List the projects : each directory given, and the projects below it holding a `pom.xml` (skip `target/`). Read the root `pom.xml` of a folder of projects to know which are built together, and in which order : a project depending on another one of the pass must be upgraded and installed (`mvn install`) after it, otherwise it keeps resolving the old artifacts of its dependency.

**U2. Exact build in use and commits since.** For each project, run `jzy3d-changes.py`. It resolves the Jzy3d artifacts with Maven and dates the build used to the minute :
* a release : date of its tag `jzy3d-all-X.Y.Z` ;
* a SNAPSHOT : timestamp of the snapshot downloaded from the Maven repository (`3.0.0-20261025.202815-15` is 2026-10-25 20:28:15 UTC), or date of a local `mvn install` when it is newer, since Maven then uses the local copy.

It maps the oldest build to a commit of `jzy3d-api` (tag, or last commit of `master` before the snapshot timestamp, as the CI deploys a snapshot after each push), then lists **all commit messages since that commit**, marking the ones touching modules the project uses. A project is up to date when this list is empty and step U3 changes nothing.

**U3. Changes to make.** Run `jzy3d-upgrade.py` without `--apply` on all directories at once. It shows, per file, the Jzy3d versions to change and the changes of `migrations.tsv` (renamed or removed modules, moved classes), and the dependencies the project must add by itself, such as the PanamaGL wrappers of the OS it runs on : ask the user which ones. Read the commits of U2 and the release notes `doc/release-X.Y.Z.md` for breaking changes the table does not cover yet.

**U4. Apply.** Show the planned changes to the user and apply them once they agree : `jzy3d-upgrade.py --apply --build`. The build reveals what remains :
* compilation errors on Jzy3d classes : find the replacement in the release notes, or where the class went with `git -C <clone> log --oneline -S "class Name" -- '*.java'` ;
* APIs removed from the JDK (the script names the ones it knows, such as `java.applet` and `Thread.stop()` absent from JDK 26) : they are not Jzy3d changes, report them apart ;
* outdated Jzy3d artifacts coming from a dependency of the project (`transitive ...` lines) : upgrade that dependency first, or exclude the artifact.
Fix, rebuild, and run `jzy3d-upgrade.py` again until it answers "Up to date" and the build passes. When a change is general, add it to `migrations.tsv` so that the next upgrades apply it.

**U5. Report.** Write the report of each project in a file at its root, named `jzy3d-doctor-<yyyyMMdd-HHmm>.md` after the start time of the run, so that runs do not overwrite each other. Ask the user to ignore these files in all their repositories once, with the global gitignore of git (`~/.config/git/ignore`) :

```
# Reports of the jzy3d-doctor skill
*-doctor.md
*-doctor-*.md
```

The report of a project gives : build used before (version, date, origin) and the commits it missed, changes applied by the script and by hand (with diffs), uncommitted changes found before the run and kept, build result, commits still missing after, what remains to do. Then, in each report and in the answer to the user :
1. **What is new** since the oldest build in use : a summary of the commits of U2 grouped by theme (rendering backends, charts, toolkits, fixes, build), the ones touching modules the projects use first, each with its date and short hash.
2. **Innovations not used yet** : the recent changes of modules the projects do not use, listed by `jzy3d-changes.py` (last 180 days before the latest version by default), summarized by module with what they would bring, so the user knows the state of the art of Jzy3d.
3. Sources to read further : release notes, website download pages, compare links of `jzy3d-versions.sh`.

Example, checking then upgrading all private projects of a workspace in one pass :

```
/jzy3d-doctor check ~/Dev/jzy3d/private
/jzy3d-doctor upgrade ~/Dev/jzy3d/private
```

or with the scripts alone :

```
S=jzy3d-api/doc/skills/jzy3d-doctor/scripts
for p in private/*/; do [ -f $p/pom.xml ] && $S/jzy3d-changes.py --api jzy3d-api $p; done
$S/jzy3d-upgrade.py --api jzy3d-api private                  # show the changes
$S/jzy3d-upgrade.py --api jzy3d-api --apply --build private/*/  # write and build
```

The sections below give the sources the procedure relies on.

## 1. Find the version the project uses

Search the build files of the project :

* Maven : `grep -rn "org.jzy3d" --include=pom.xml <project>`, then resolve properties such as `${jzy3d.version}`.
  `mvn dependency:tree -Dincludes=org.jzy3d` gives the effective versions, transitive ones included.
* Gradle : `grep -rn "org.jzy3d" --include="*.gradle*" <project>`.
* No build tool : jar names in `lib/` folders (`jzy3d-api-1.0.2.jar`, `jzy3d-core-2.1.0.jar`...).

The artifact names tell the generation of Jzy3d, even without a version :

| Artifacts used | Generation |
|---|---|
| `jzy3d-api` (single jar, with `jzy3d-javafx`, `jzy3d-swt` aside) | 0.9.x to 1.0.x |
| `jzy3d-core`, `jzy3d-native-jogl-*`, `jzy3d-emul-gl-*`, parent `jzy3d-all` | 2.x and later |
| `jzy3d-native-panama-gl-*` | 3.0.0 and later |
| `jzy3d-depthpeeling`, `jzy3d-jdt-core`, `jzy3d-svm-mapper`, `jzy3d-io-reports`, `jzy3d-io-xls` | before 3.0.0 (removed or renamed in 3.0.0) |

## 2. Find the latest version

Jzy3d is **not on Maven Central**, it is deployed on the Jzy3d Maven repository. Compare these sources, they are not always in sync (e.g. tag `jzy3d-all-2.2.2` exists while the last deployed release is 2.2.1) :

| Source | Where | Gives |
|---|---|---|
| Released artifacts | `https://maven.jzy3d.org/releases/org/jzy3d/<artifact>/maven-metadata.xml` (artifact `jzy3d-all` for 2.x+, `jzy3d-api` for 1.0.x) | Versions that a build can download |
| Snapshot artifacts | `https://maven.jzy3d.org/snapshots/org/jzy3d/<artifact>/maven-metadata.xml` | Version under development (`X.Y.Z-SNAPSHOT`) |
| Git tags | `https://github.com/jzy3d/jzy3d-api/tags`, or `git tag` in a clone. Named `jzy3d-all-X.Y.Z` since 2.0.0 | Released versions and their commit |
| Release branches | `https://github.com/jzy3d/jzy3d-api/branches/all`, or `git branch -r` : `0.9`, `0.9.1`, `1.0.0` to `1.0.3`, `2.0.0`, `2.1.0`, `2.2.0`, `3.0.0` | Code of each version, including fixes made after the release |
| Current development | branch `master`, version in its root `pom.xml` | Next version |
| Website | `http://www.jzy3d.org/download.php`, then `http://www.jzy3d.org/download-X.Y.Z.php` | Announced versions |
| Javadoc | `https://doc.jzy3d.org/javadoc/` (one folder per version : 0.8.4 to 3.0.0) | Versions with a published API documentation |

`www.jzy3d.org` only answers in **http** (https fails), `maven.jzy3d.org` and `doc.jzy3d.org` answer in https.

The latest version to migrate to is the highest released version, unless the user wants the version under development : then use the snapshot, with the snapshot repository declared in the project.

## 3. List what is new since the version in use

Gather every source covering the versions **after** the one in use, up to the target version, and summarize them for the user grouped by theme (new rendering backends, new chart types, toolkits, fixes, performance), each item with the version it appeared in.

1. **Release notes in the repository** : `doc/release-X.Y.Z.md` of the `jzy3d-api` repository (on `master` and on the release branch `X.Y.Z`). They describe new features and the breaking changes, with the classes moved between modules.
2. **Website download pages** : `http://www.jzy3d.org/download-X.Y.Z.php` for each version after the one in use. Pages exist for 0.9, 0.9.1, 1.0.0, 1.0.1, 2.0.0, 2.1.0 and 3.0.0 (cumulative since 2.1.0, so it also covers 2.2.x). Each page gives the new features of its version with code samples, and the Maven setup.
3. **Commits** between the tag of the version in use and the target, in a clone of `jzy3d-api` :
   ```
   git log --oneline --no-merges jzy3d-all-2.1.0..jzy3d-all-2.2.1
   git log --oneline --merges jzy3d-all-2.2.1..origin/master    # merged pull requests
   ```
   Before 2.0.0 there are no tags : use the release branches (`git log origin/1.0.2..origin/1.0.3`).
   On GitHub : `https://github.com/jzy3d/jzy3d-api/compare/jzy3d-all-2.1.0...jzy3d-all-2.2.1`.
4. **Pull requests and issues** merged or closed since the release date of the version in use (date of its tag or release branch) :
   ```
   gh pr list -R jzy3d/jzy3d-api --state merged --search "merged:>2022-05-24" --limit 200
   gh issue list -R jzy3d/jzy3d-api --state closed --search "closed:>2022-05-24" --limit 200
   ```
5. **Module READMEs** of the target version, for features documented only there (e.g. `jzy3d-bindings/jzy3d-emul-gl-awt/README.md` for EmulGL, `jzy3d-core/jzy3d-core/doc/depthpeeling.md`, the root `README.md` sections on PanamaGL and on testing).
6. **Tutorials** : `jzy3d-tutorials` of the target version shows the recommended way to build each chart, compare with what the project does.

Only list features the project can use : check its toolkit (AWT, Swing, SWT, JavaFX, NEWT, offscreen) and backend (JOGL, EmulGL, PanamaGL) in its code before recommending a feature.

## 4. Find what the project must change

For each version crossed, read the breaking changes of the release notes and of the download page, then search the project for each of them :

* **Dependencies** : renamed, merged or removed modules (`doc/release-3.0.0.md`, section "Modules and dependencies"). Update the `artifactId`s, remove the ones that disappeared.
* **Moved classes** : the package usually stays the same, only the module changes. Make sure the project depends on the module now holding the class (section "Classes moved to another module").
* **Changed or removed methods** : search the project for each method listed, and apply the replacement given by the release notes.
* **JDK** : the minimum JDK of each module (e.g. PanamaGL modules require JDK 22, SWT and JavaFX modules JDK 11).
* **Maven repositories** : declare `https://maven.jzy3d.org/releases/` (and `/snapshots/` for a snapshot) in the project, since Jzy3d is not on Maven Central.

Then build the project and fix the remaining compilation errors, searching each missing class in the target version : `git grep -l "class MissingClass" jzy3d-all-X.Y.Z` in a clone of `jzy3d-api`.

## 5. Report to the user

Give, in this order :

1. Version in use, latest released version, version under development, with the source of each.
2. What is new since the version in use, by theme, with the version of each item and a link to its documentation.
3. Changes to make in the project, as a checklist with files and lines.
4. The sources read, as links, so the user can go further.
5. Sources that disagree or look stale (see below), so they can be fixed.

## 6. Submit an improvement to Jzy3d as a pull request

When the migration reveals a bug, a missing feature or a documentation error in Jzy3d itself, fix it in Jzy3d rather than working around it in the project, and submit the fix as a pull request on `https://github.com/jzy3d/jzy3d-api`.

1. **Check it is not already done or discussed** : search `master`, the open pull requests (`gh pr list -R jzy3d/jzy3d-api`) and the issues (`gh issue list -R jzy3d/jzy3d-api --search "<keywords>"`). For a large change, open an issue first with the templates of `.github/ISSUE_TEMPLATE` (bug report, feature request) to agree on the design.
2. **Get the code** : fork `jzy3d/jzy3d-api` (`gh repo fork jzy3d/jzy3d-api --clone`), or clone it without the baseline images of integration tests as explained in `doc/repository-size.md`. Branch from `master`, which holds the version under development : `git switch -c feat/<topic>` for a feature, `bug/<topic>` for a fix (names used by existing branches).
3. **Make the change** in the module holding the code (see the module layout below), in the style of the surrounding code. Add a unit test named `Test*.java` next to the code. For rendering changes, add or update an integration test `ITTest*.java` in `jzy3d-tests/jzy3d-tester-integration`, which compares charts to baseline images and JOGL to PanamaGL.
4. **Build and test** from the root of the repository, as described in the "How to build" section of `README.md` :
   ```
   mvn clean install                       # unit tests
   mvn clean install -Pintegration-tests   # unit and integration tests
   ```
   A JDK 22+ also builds and tests the PanamaGL modules (profile activated by the JDK), a JDK 11+ builds the SWT and JavaFX modules.
5. **Document the change** : add it to `doc/release-<next version>.md` (new feature, or breaking change with its replacement, or class moved to another module), and update the README of the modules concerned. For a renamed or removed module, or a class changing package, add a line to `doc/skills/jzy3d-doctor/migrations.tsv` so that `jzy3d-doctor upgrade` migrates projects automatically.
6. **Open the pull request** against `master` : `gh pr create -R jzy3d/jzy3d-api --base master`. Describe what changes, why, and how it was tested. The CI (`.github/workflows/ci.yml`) builds and tests every pull request on Linux, macOS and Windows : check it passes, and look at the images it attaches when an integration test fails.
7. **Use the fix in the project before it is released** : `mvn install` the branch locally and depend on the `-SNAPSHOT` version, or wait for the merge, after which `master` deploys the snapshot to `https://maven.jzy3d.org/snapshots/` (a maintainer can also deploy the snapshot of a pull request by labeling it `deploy-snapshot`).

Module layout of `jzy3d-api` since 3.0.0 :

| Folder | Content |
|---|---|
| `jzy3d-core/` : `jzy3d-core`, `jzy3d-core-awt`, `jzy3d-core-swing`, `jzy3d-core-swt`, `jzy3d-core-javafx` | API independent of the rendering backend, per windowing toolkit |
| `jzy3d-bindings/` | Rendering backends : `jzy3d-native-jogl-*`, `jzy3d-native-panama-gl-*`, `jzy3d-emul-gl-*`, `jzy3d-jGL` |
| `jzy3d-tests/` | `jzy3d-tester`, `jzy3d-tester-native`, `jzy3d-tester-integration` |
| `jzy3d-tools/` | Third party libraries maintained by Jzy3d and their integration : `jzy3d-tools-jdt`, `jzy3d-tools-jply`, `jzy3d-tools-libsvm`, `jzy3d-tools-svm-mapper`, `jzy3d-tools-xls` |
| `jzy3d-everything`, `jzy3d-tutorials` | Bundle of the main modules, tutorials |

## Sources known to be stale

Do not trust the version written in these files, check it against the Maven repository :

* `jzy3d-tutorials/README.md` : Maven setup with `2.0.0-SNAPSHOT`.
* `jzy3d-bindings/jzy3d-jGL/README.md` : Maven setup with `2.0.1-SNAPSHOT`.
* `http://www.jzy3d.org/documentation.php` : "Maven configuration for 1.0.0" and Javadoc 1.0.0 links listing modules of 1.0.x (`jzy3d-depthpeeling`, `jzy3d-jdt-core`, `jzy3d-svm-mapper`...).
* `http://www.jzy3d.org/download.php` : lists versions 0.8.4 to 0.9a4 only, newer versions are in the links of `download-3.0.0.php`.
* Links of the website to `https://github.com/jzy3d/jzy3d-api/tree/master/<module>` : modules moved to `jzy3d-bindings/`, `jzy3d-tests/` and `jzy3d-tools/` in 3.0.0.
