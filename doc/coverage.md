# Coverage of unit tests

## Measure it

```
mvn clean install -Pcoverage
python3 .github/scripts/coverage-by-zone.py
```

The `coverage` profile of the root `pom.xml` runs unit tests (`Test*.java`, `*Test.java`) with the JaCoCo
agent. Each module gets its report in `target/site/jacoco`.

The script merges the JaCoCo data of all modules, so a class counts as covered when the tests of any module
run it (e.g. `jzy3d-core` classes exercised by tests of the JOGL and PanamaGL modules). It writes the merged
report in `target/coverage` (`html/index.html` and `coverage.csv`) and prints the coverage by zone and by
module, with a level : line coverage >= 70 % good, >= 50 % fair, >= 30 % weak, else poor.

Integration tests (`ITTest*.java`) are not measured by default. To include them, run
`mvn clean install -Pcoverage,integration-tests` before the script : this matters for the bindings and the
testers, mostly exercised by integration tests.

Modules adding their own JVM options to tests must start them with `@{argLine}`, which receives the JaCoCo
agent (see `jzy3d-core-swt` and `jzy3d-native-panama-gl-swt`).

## State on 2026-10-30

Commit `134dc194` (branch `feat/modules-layout`), JDK 26 on macOS, unit tests only : 189 tests, 51 573
lines in 1 048 classes, **28.7 %** of lines covered.

### By zone

| Zone | Lines | Line | Branch | Classes run | Test classes | Satisfaction |
|---|---|---|---|---|---|---|
| core (`jzy3d-core*`) | 21 686 | 38.3 % | 31.7 % | 42.9 % | 37 | Medium : the base of the API, should reach 60 % |
| bindings / PanamaGL | 1 623 | 60.1 % | 43.6 % | 92.9 % | 12 | Good : the GPU conformance tests of `jzy3d-tester` cover most of the painter |
| bindings / JOGL | 3 865 | 23.6 % | 16.9 % | 26.8 % | 33 | Low in unit tests, higher with integration tests which mostly render with JOGL |
| bindings / EmulGL and jGL | 17 037 | 17.2 % | 12.5 % | 41.5 % | 19 | Low : `jzy3d-jGL-awt` is the largest untested code of the repository |
| tools | 4 777 | 30.0 % | 23.0 % | 46.2 % | 11 | Uneven : jPLY well tested, libsvm, svm-mapper and xls not at all |
| tests (testers) | 1 154 | 22.2 % | 10.3 % | 16.7 % | 3 | Not a concern : test tooling, exercised by integration tests |
| xls (`jzy3d-io-xls`, now `jzy3d-tools-xls` in the tools zone) | 363 | 0 % | 0 % | 0 % | 0 | No test, its only test classes are demos |
| tutorials | 1 068 | 0 % | 0 % | 0 % | 0 | Not relevant : examples without tests |

### By module

| Module | Lines | Line | Branch | Classes run | Test classes |
|---|---|---|---|---|---|
| `jzy3d-core` | 17 507 | 38.4 % | 31.1 % | 42.2 % | 28 |
| `jzy3d-core-awt` | 3 601 | 41.3 % | 37.1 % | 43.3 % | 6 |
| `jzy3d-core-javafx` | 209 | 37.3 % | 15.2 % | 90.0 % | 1 |
| `jzy3d-core-swt` | 336 | 9.2 % | 7.4 % | 30.8 % | 2 |
| `jzy3d-core-swing` | 33 | 0 % | 0 % | 0 % | 0 |
| `jzy3d-native-panama-gl-core` | 1 286 | 65.3 % | 47.7 % | 92.9 % | 4 |
| `jzy3d-native-panama-gl-javafx` | 80 | 43.8 % | 50.0 % | 100 % | 2 |
| `jzy3d-native-panama-gl-swing` | 117 | 41.0 % | 45.5 % | 100 % | 5 |
| `jzy3d-native-panama-gl-swt` | 140 | 37.1 % | 10.0 % | 75.0 % | 1 |
| `jzy3d-native-jogl-core` | 1 594 | 33.2 % | 21.0 % | 58.3 % | 5 |
| `jzy3d-native-jogl-awt` | 779 | 29.0 % | 26.1 % | 32.5 % | 20 |
| `jzy3d-native-jogl-javafx` | 154 | 39.0 % | 22.2 % | 38.5 % | 1 |
| `jzy3d-native-jogl-swing` | 593 | 11.1 % | 12.2 % | 11.4 % | 3 |
| `jzy3d-native-jogl-swt` | 179 | 16.2 % | 0 % | 62.5 % | 3 |
| `jzy3d-native-jogl-newt` | 566 | 0 % | 0 % | 0 % | 1 |
| `jzy3d-emul-gl-awt` | 1 476 | 51.8 % | 40.6 % | 88.9 % | 15 |
| `jzy3d-jGL-awt` | 15 506 | 13.9 % | 10.4 % | 34.0 % | 4 |
| `jzy3d-jGL-swt` | 55 | 0 % | 0 % | 0 % | 0 |
| `jzy3d-tools-jply` | 1 229 | 77.9 % | 65.9 % | 100 % | 10 |
| `jzy3d-tools-jdt` | 1 275 | 37.4 % | 25.2 % | 60.0 % | 1 |
| `jzy3d-tools-libsvm` | 1 946 | 0 % | 0 % | 0 % | 0 |
| `jzy3d-tools-svm-mapper` | 327 | 0 % | 0 % | 0 % | 0 |
| `jzy3d-tester` | 1 053 | 23.1 % | 9.7 % | 14.3 % | 1 |
| `jzy3d-tester-native` | 101 | 12.9 % | 18.8 % | 25.0 % | 2 |
| `jzy3d-io-xls` (now `jzy3d-tools-xls`) | 363 | 0 % | 0 % | 0 % | 0 |
| `jzy3d-tutorials-*` (10 modules) | 1 068 | 0 % | 0 % | 0 % | 0 |

Line, Branch : covered lines and branches. Classes run : classes with at least one covered line. Test
classes : unit test classes of the module.

## Next steps

1. Measure with integration tests as well (`-Pcoverage,integration-tests`) to know the actual coverage of the
   bindings and testers.
2. Raise `jzy3d-core` from 38 % to 60 % : every chart relies on it, before jGL.
3. Test `jzy3d-core-swing` (`FrameSwing`) and `jzy3d-native-jogl-newt`, which have no covered line.
4. Decide what to do with `jzy3d-tools-libsvm` and `jzy3d-tools-svm-mapper`, not tested at all.
5. Track the coverage on CI : the script can run after the build of `.github/workflows/ci.yml`.
