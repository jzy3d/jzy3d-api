# Jzy3D 3.0.0 : API changes

Jzy3D 3.0.0 renders charts with JOGL or with [PanamaGL](https://github.com/panama-gl/panama-gl), which
calls OpenGL through the Java Foreign Function & Memory API. To let the same drawables work with both,
classes that only needed OpenGL now use `IPainter` instead of JOGL types (`GL`, `GL2`, `PointerBuffer`,
JOGL `Texture`). This page lists what changes for code written against Jzy3D 2.x.

No package or class is renamed. Code that only builds charts with factories and adds drawables is not
affected.

## Java versions

| Modules | Minimum JDK |
|---|---|
| Core, AWT, Swing, EmulGL, JOGL (AWT, Swing, NEWT), depth peeling, tools | 8 |
| SWT, JavaFX | 11 |
| PanamaGL (core, Swing, SWT, JavaFX, depth peeling) | 22 |

The build selects modules according to the JDK running Maven : SWT and JavaFX modules are built on JDK 11+,
PanamaGL modules on JDK 22+. PanamaGL modules depend on `org.jzy3d:panama-gl-*` 1.4.1.

Since PanamaGL 1.4.0, the PanamaGL modules of Jzy3D do not bring the native wrappers of all OS anymore :
an application adds the wrappers of the OS it runs on (e.g. `org.jzy3d:panama-gl-wrappers-windows`), or
`org.jzy3d:panama-gl-wrappers-all` (`<type>pom</type>`) for all OS. A Windows application then ships 6.8 MB
of bindings instead of 40 MB. Without the wrapper of the OS, `PanamaGLFactory.select()` fails with a
message naming the missing dependency.

## Modules and dependencies

* The integration tests module `jzy3d-tests-java9` is renamed `jzy3d-tester-integration`.
* Module `jzy3d-depthpeeling` is removed : its algorithms and shaders moved to `jzy3d-core` and its JOGL
  renderer and factories to `jzy3d-native-jogl-awt`. Replace a dependency to `jzy3d-depthpeeling` by a
  dependency to `jzy3d-native-jogl-awt`.
* Modules are grouped in folders : `jzy3d-core` (`jzy3d-core` and the toolkit modules `jzy3d-core-awt`, `-swing`,
  `-swt`, `-javafx`), `jzy3d-bindings` (EmulGL, jGL, JOGL,
  PanamaGL), `jzy3d-tests` (`jzy3d-tester`, `jzy3d-tester-native`, `jzy3d-tester-integration`) and
  `jzy3d-tools` (third party libraries maintained by jzy3d and their jzy3d integration : `jzy3d-tools-jdt`, `jzy3d-tools-jply`, `jzy3d-tools-libsvm`, `jzy3d-tools-svm-mapper`, `jzy3d-tools-xls`).
* Modules `jzy3d-jdt-core`, `jzy3d-svm-mapper` and `jzy3d-io-xls` are renamed `jzy3d-tools-jdt`,
  `jzy3d-tools-svm-mapper` and `jzy3d-tools-xls`.
* `org.smurn:jply` 0.2.1, which was only available on the jzy3d Maven repository, is replaced by module
  `jzy3d-tools-jply` holding its source code (package `org.smurn.jply` is unchanged).
* Module `jzy3d-io-reports` is removed : its demos moved to the tests of `jzy3d-tools-xls`.
* `jzy3d-io-xls` is renamed `jzy3d-tools-xls` and moved to `jzy3d-tools`. Its package `org.jzy3d.io.psy4j` is
  removed, it only depends on `jzy3d-core`, `poi` and `poi-ooxml` (it does not bring guava, poi-scratchpad,
  poi-excelant, commons-csv, commons-cli, joda-time and commons-logging anymore) and `jzy3d-everything` does
  not include it anymore.
* `logback-classic` 1.3.16 (was 1.2.13, which did not bind slf4j 2 and had vulnerabilities in logback-core).
* `org.eclipse.jface` 3.29.0 (was 3.12.1, whose dependency ranges resolved to Eclipse bundles requiring
  Java 17+).

## Classes moved to another module

These classes keep their package and name. They are still available through the JOGL modules, which depend
on the modules they moved to.

| From | To | Classes |
|---|---|---|
| `jzy3d-native-jogl-core` | `jzy3d-core` | VBO drawables, buffers, builders and loaders (`DrawableVBO`, `DrawableVBO2`, `BarVBO`, `PolygonVBO`, `ScatterVBO`, `SphereVBO`, `TriangleVBO`, `TriangleStripVBO`, `VBOBuilder`, `VBOBufferLoader*`...), shader drawables (`ShaderMeshDrawableVBO`, `ShaderWaterfallDrawableVBO`, `ColormapTexture`...), volumes (`Volume`, `Texture3D`, `CubeVBO`, `CubeVBOBuilder`), textures (`SharedTexture`, `TextureFactory`, `NativeDrawableImage`, `TexturedCube`, `TexturedCylinder`, `TranslucentQuad`, `PickableTexture`, `MaskImageSymbolHandler`, `TextureGraph2d`), `GLSLProgram`, `ShaderFilePair`, `GPUInfo`, `OBJFileLoader`, `NativeDrawableFactory`, `FeedbackBufferAxisBox` |
| `jzy3d-native-jogl-awt` | `jzy3d-core-awt` | 2D charts (`Chart2d`, `View2d`, `AxisBox2d`, `SerieManager`), contours (`org.jzy3d.contour.*`, `ContourAxisBox`, `ContourLevel`, `ContourLevels`, `ContourMesh`...), `AWTMousePickingController`, `AWTNativeSymbolHandler`, `AWTNativeDrawableImage`, `DrawableTextTexture`, `TextImageRenderer`, `DrawableTextCell`, `TextCellRenderer` |
| `jzy3d-native-jogl-awt` | `jzy3d-core` | `BufferedImageTexture` |
| `jzy3d-native-jogl-javafx` | `jzy3d-core-javafx` | `JavaFXMousePickingController` |
| `jzy3d-depthpeeling` | `jzy3d-core` | Depth peeling algorithms (`org.jzy3d.plot3d.rendering.ddp.algorithms.*`) and their shaders |
| `jzy3d-depthpeeling` | `jzy3d-native-jogl-awt` | `DepthPeelingChartFactory`, `DepthPeelingPainterFactory`, `DepthPeelingRenderer3d` |

## Methods taking `IPainter` instead of JOGL `GL` or `GL2`

Methods keep their name, the JOGL parameter is replaced by the `IPainter` already given to `draw(IPainter)`.

| Class | Methods |
|---|---|
| `GLSLProgram` | all methods taking `GL2` : `bind`, `unbind`, `link`, `destroy`, `setUniform`, `setTextureUnit`, `bindTexture*`, `compile*Shader`, `loadAndCompile*`, `validateProgram`, `verify*`, `readErrors`, `checkShaderLogInfo`, `warnLink`, `warnScript` |
| `DrawableVBO` | `setData`, `doLoadArrayFloatBuffer`, `doLoadElementIntBuffer`, `configure(IPainter)` (was `configure(IPainter, GL)`), and protected `applyQuality`, `applyVertices`, `applyWidth`, `applyPolygonModeFillGL2`, `color`, `enable`, `disable`, `disableColor`, `doBindGL2`, `doDrawElements`, `pointers`, `polygonOffseFillEnable`, `polygonOffsetFillDisable` |
| `DrawableVBO2` | protected `registerVertices`, `registerNormals`, `registerColors`, `registerElements`, `doDrawGeometries`, `applyPrimitiveRestartIfEnabled` |
| `ShaderMeshDrawableVBO` | protected `configure(IPainter)` (was `configure(IPainter, GL)`), `pointers` |
| `ShaderWaterfallDrawableVBO` | protected `applyVertices`, `pointers` |
| `SphereVBO` | protected `drawSphereDirectWithDataFromArrays`, `drawSphereWithDrawArrays` |
| `Texture3D`, `ColormapTexture` | `bind`, `unbind`, `setTextureData`, `validateTexID`, and `ColormapTexture.update` |
| `Volume` | `bind` |
| `TextureFactory` | `init(IPainter)` (was `init(IPainter, GL)`) |
| Depth peeling algorithms (`AbstractDepthPeelingAlgorithm`, `AbstractAccumulationDepthPeeling`, `DualDepthPeelingAlgorithm`, `FrontToBackPeelingAlgorithm`, `WeightedAveragePeelingAlgorithm`, `WeightedSumPeelingAlgorithm`) | `buildFinish`, `buildShaders`, `destroyShaders`, `reloadShaders`, `buildFullScreenQuad`, `init*RenderTargets`, `delete*RenderTargets`, and `doRender(IPainter)` (was `doRender(IPainter, GL2)`) |

Subclasses of depth peeling algorithms must now implement the abstract `buildShaders(IPainter)` and
`destroyShaders(IPainter)`.

## Removed or replaced

| Removed | Replacement |
|---|---|
| `GPUInfo.load()`, `GPUInfo.load(GL)`, `GPUInfo.main(String[])` | `GPUInfo.load(IPainter)`, or `NativeGPUInfo.load()`, `NativeGPUInfo.load(GL)` and `NativeGPUInfo.main(String[])` in `jzy3d-native-jogl-core` |
| `SharedTexture.getTexture(IPainter)` returning a JOGL `Texture`, field `texture`, `load(GL, String)` | `SharedTexture.bind(IPainter)`, `getTextureId(IPainter)`, `getWidth()`, `getHeight()` |
| `BufferedImageTexture.getTexture(IPainter)`, `load(GL, BufferedImage)` | `getImage()`, and the methods inherited from `SharedTexture` |
| `DrawableVBO2.getGL(IPainter)`, `AbstractDepthPeelingAlgorithm.getGL(IPainter)` and `getGLU(IPainter)` | the GPU methods of `IPainter` |
| Fields `glu` (JOGL `GLU`) of `JavaFXMousePickingController` and `AbstractDepthPeelingAlgorithm` | the GLU methods of `IPainter` (`gluOrtho2D`, `gluProject`, `gluUnProject`...) |
| `PanamaGLViewAndColorbarsLayout` | the default `ViewAndColorbarsLayout`, which PanamaGL charts now use to render legends and colorbars |

## Changed types

| Element | Before | After |
|---|---|---|
| `DrawableVBO2` field `elementsIndices` and `getElementsIndices()` | JOGL `PointerBuffer` | `java.nio.IntBuffer` |
| `DrawableVBO2.setData(IPainter, IntBuffer, PointerBuffer, FloatBuffer, FloatBuffer, FloatBuffer, BoundingBox3d)` | | `setData(IPainter, IntBuffer, LongBuffer, IntBuffer, FloatBuffer, FloatBuffer, FloatBuffer, BoundingBox3d)` : element counts, element byte offsets, then element indices |
| `SharedTexture` field `coords` and `getCoords()` | JOGL `TextureCoords` | `org.jzy3d.plot3d.rendering.textures.TextureCoords` |
| `Chart2d` | extends `AWTNativeChart` | extends `AWTChart` |
| `Chart2dFactory` | extends `AWTChartFactory` | extends `ChartFactory` |

## Additions

- `IPainter` provides the OpenGL functions used by GPU drawables : buffers (`glGenBuffers`, `glBindBuffer`,
  `glBufferData`...), vertex arrays, shaders and programs (`glCreateShader`, `glUseProgram`, `glUniform*`...),
  textures (`glTexImage1D/2D/3D`...), framebuffers, queries and `glReadPixels`. They are default methods
  throwing `UnsupportedOperationException`, so existing `IPainter` implementations still compile. JOGL
  (`NativeDesktopPainter`) and PanamaGL (`PanamaGLPainter`) implement them. `GLConstants` holds the matching
  OpenGL constants.
- `new Chart2d()` uses the first 2D chart factory found in the classpath : `Chart2dFactory` (JOGL), then
  `PanamaGLChart2dFactory` (PanamaGL). `Chart2d(IChartFactory, Quality)` chooses the toolkit explicitly.
- PanamaGL : `PanamaGLOffscreenCanvas`, `PanamaGLChart2dFactory`, `PanamaGLContourChartFactory`,
  `PanamaGLU` (GLU functions in Java), `PanamaGLDebug` (OpenGL error checking and tracing, as JOGL `DebugGL`,
  enabled with the painter factory `setDebugGL` and `setTraceGL`).
- Depth peeling with PanamaGL : `PanamaGLDepthPeeling.enable(factory, method)` (in `jzy3d-native-panama-gl-core`)
  renders charts of any PanamaGL chart factory with depth peeling, `PanamaGLDepthPeelingChartFactory` (in
  `jzy3d-native-panama-gl-swing`) does it for Swing.
- Mouse picking with SWT (`SWTMousePickingController`) and screenshots with JavaFX
  (`JavaFXScreenshotKeyController`).
- `jzy3d-tester` : `PainterGPUConformance` checks that an `IPainter` implements the GPU methods as expected.

## Migration

A drawable that called JOGL directly :

```java
public void draw(IPainter painter) {
  GL2 gl = ((NativeDesktopPainter) painter).getGL().getGL2();
  program.bind(gl);
  program.setUniform(gl, "alpha", 0.5f);
  ...
}
```

now gives the painter, and works with JOGL and PanamaGL :

```java
public void draw(IPainter painter) {
  program.bind(painter);
  program.setUniform(painter, "alpha", 0.5f);
  ...
}
```
