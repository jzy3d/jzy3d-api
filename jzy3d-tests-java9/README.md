jzy3d-tests-java9
=================

This module holds the integration tests of Jzy3D. They render charts with each toolkit (JOGL with AWT
and Swing, EmulGL, PanamaGL) and verify the screenshots. It requires Java 9+ to detect HiDPI
displays, and Java 22+ to also test PanamaGL (see the `panamagl` profile of this module).

See the `BASELINE_<platform>.md` files to watch the baseline images of a platform, e.g.
[BASELINE_macosx_26.6_AppleM3.md](BASELINE_macosx_26.6_AppleM3.md).

## Run the tests

```
# All integration tests (and unit tests) of the project
mvn clean install -Pintegration-tests

# Integration tests of this module only, once the other modules are installed
mvn verify -Pintegration-tests -pl jzy3d-tests-java9

# A single test
mvn verify -Pintegration-tests -pl jzy3d-tests-java9 -Dit.test=ITTest_Surface
```

Tests named `ITTest*` are excluded from the default build. [Some tests](src/test/java/org/jzy3d/tests/manual)
require a manual verification.

## Two kinds of tests

**Tests comparing charts to baseline images** (`ITTest_Surface`, `ITTest_Scatter`, `ITTest_Text`,
`ITTest_Colorbar`, `ITTest_2D*`, `ITTest_Overlay`, `ITTest_AxisLabelRotateLayout`) render each case for each
toolkit and each HiDPI mode, then call `ITTest.assertChart(chart, name)`.

* Baselines are stored per platform in `src/test/resources/<platform>/`, where the platform label is the OS
  name, OS version and GPU name given by `NativePlatform` (e.g. `macosx_26.6_AppleM3`). Rendering differs
  between OS, GPU and drivers, hence a baseline is only valid for its platform.
* A baseline missing for the current platform is built from the chart at the first run, then the test
  passes : **look at new baselines before committing them**, and regenerate the `BASELINE_<platform>.md`
  index (test `ITTest.markdownAllTests`, run with any ITTest).
* JOGL and EmulGL charts must match their baseline exactly. On failure, the actual, expected, diff and
  zoom images are written to `target/error-<name>_*.png`.
* **PanamaGL charts have no baseline.** They are compared to the JOGL baseline of the same case
  (`Native_Swing`, or `Native_AWT`), since both render with the native OpenGL library. Text and points are
  rasterized slightly differently, hence a tolerance : at most 1% of pixels may have a channel differing by
  more than 32 (2% for `ITTest_Text`, which is mostly made of text). See
  `ITTest.assertSimilarToJOGL`.
* `ITTest_Scatter` does not compare PanamaGL to JOGL : JOGL axis text alters the rendering of the points
  drawn after it (35% of the points pixels differ), which PanamaGL does not.

**Tests comparing PanamaGL to JOGL** (`ITTest_PanamaGLParity`, `ITTest_GPUDrawables`,
`ITTest_DepthPeeling`, `ITTest_Picking`) render the same chart with both toolkits on the same computer and
compare the images with the same tolerance. They need no baseline, hence run anywhere, including CI. On
failure, images are written to `target/panamagl-parity/`.

## Continuous integration

CI runners (Linux with Mesa llvmpipe, macOS VM, Windows with Mesa D3D12) have no baseline in the repository,
and their platform labels change with runner images. CI hence runs all `ITTest_*` with
`-Djzy3d.ittest.baselines=target/ittest-baselines/` : baselines are built in this empty folder by JOGL and
EmulGL charts, and PanamaGL charts are compared to the JOGL ones built on the same runner. CI thus verifies
PanamaGL against JOGL on each OS, but does not detect regressions of JOGL or EmulGL themselves, which
requires running the tests on a computer having baselines.

CI GPUs are virtual or software renderers : a bug depending on an actual GPU (e.g. depth precision on Apple
Silicon) is only detected on a real computer.

## Known limitations

* **HiDPI** : on a HiDPI display, JOGL and PanamaGL ignore `HiDPI.OFF` and render at the physical
  resolution (e.g. 1600x1144 for a 800x572 chart), while EmulGL honours it.
* **Text** : JOGL draws glyphs from a glyph cache and PanamaGL from AWT images. Both scale fonts with the
  pixel scale of the display, but glyph edges are antialiased differently.
* **Fonts** : a font missing on a platform (e.g. Apple Chancery on Linux) is replaced by another one.

## Weight of baseline images

Baselines are about 300 MB in the working tree (1100 PNG, mostly 1600x1144 HiDPI images of 200 KB to
700 KB), and about 600 MB in the git history, which makes most of the repository size. Each new platform
adds 30 MB to 80 MB. Options to limit this :

* **Do not store what can be compared** : PanamaGL charts are compared to JOGL baselines instead of having
  their own.
* **Remove obsolete platforms** : baselines of computers that are no longer used (macOS 10.12, 10.15,
  11.4, 14.4, Linux 5.13) can not be verified anymore. Removing them shrinks clones of later commits, but
  not the history.
* **Compress losslessly** : e.g. `zopflipng -y in.png out.png` saved 18% and 34% on two baselines (it is
  slow : several seconds per image).
* **Store baselines out of this repository** : a separate repository (git submodule, or an archive
  downloaded by the tests) keeps this repository small. Git LFS is another option, but the free GitHub plan
  allows 1 GB of storage and 1 GB of monthly bandwidth, which CI checkouts would consume quickly.
* Only rewriting the history (e.g. `git filter-repo`) shrinks existing clones, at the cost of changing all
  commit hashes.
