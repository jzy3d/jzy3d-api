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
PanamaGL modules on JDK 22+. PanamaGL modules depend on `org.jzy3d:panama-gl-*` 1.3.0.

## Classes moved to another module

These classes keep their package and name. They are still available through the JOGL modules, which depend
on the modules they moved to.

| From | To | Classes |
|---|---|---|
| `jzy3d-native-jogl-core` | `jzy3d-core` | VBO drawables, buffers, builders and loaders (`DrawableVBO`, `DrawableVBO2`, `BarVBO`, `PolygonVBO`, `ScatterVBO`, `SphereVBO`, `TriangleVBO`, `TriangleStripVBO`, `VBOBuilder`, `VBOBufferLoader*`...), shader drawables (`ShaderMeshDrawableVBO`, `ShaderWaterfallDrawableVBO`, `ColormapTexture`...), volumes (`Volume`, `Texture3D`, `CubeVBO`, `CubeVBOBuilder`), textures (`SharedTexture`, `TextureFactory`, `NativeDrawableImage`, `TexturedCube`, `TexturedCylinder`, `TranslucentQuad`, `PickableTexture`, `MaskImageSymbolHandler`, `TextureGraph2d`), `GLSLProgram`, `ShaderFilePair`, `GPUInfo`, `OBJFileLoader`, `NativeDrawableFactory`, `FeedbackBufferAxisBox` |
| `jzy3d-native-jogl-awt` | `jzy3d-core-awt` | 2D charts (`Chart2d`, `View2d`, `AxisBox2d`, `SerieManager`), contours (`org.jzy3d.contour.*`, `ContourAxisBox`, `ContourLevel`, `ContourLevels`, `ContourMesh`...), `AWTMousePickingController`, `AWTNativeSymbolHandler`, `AWTNativeDrawableImage`, `DrawableTextTexture`, `TextImageRenderer`, `DrawableTextCell`, `TextCellRenderer` |
| `jzy3d-native-jogl-awt` | `jzy3d-core` | `BufferedImageTexture` |
| `jzy3d-native-jogl-javafx` | `jzy3d-core-javafx` | `JavaFXMousePickingController` |

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
- New module `jzy3d-depthpeeling-panamagl` : `PanamaGLDepthPeeling.enable(factory, method)` renders charts
  of any PanamaGL chart factory with depth peeling.
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
