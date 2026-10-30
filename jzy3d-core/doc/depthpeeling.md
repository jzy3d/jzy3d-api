Depth peeling
=============

Depth peeling renders charts with order independent transparency, without sorting the scene graph.

`jzy3d-core` holds the depth peeling algorithms (`org.jzy3d.plot3d.rendering.ddp.algorithms`) and their shaders, which rely on `IPainter` and are thus independent of the OpenGL binding. See [DualDepthPeeling.pdf](DualDepthPeeling.pdf). The renderers and factories are provided by the native modules :

* JOGL : `DepthPeelingChartFactory`, `DepthPeelingPainterFactory` and `DepthPeelingRenderer3d` in `jzy3d-native-jogl-awt`.
* PanamaGL : `PanamaGLDepthPeeling` and `PanamaGLDepthPeelingRenderer3D` in `jzy3d-native-panama-gl-core`, `PanamaGLDepthPeelingChartFactory` and `PanamaGLDepthPeelingPainterFactory` in `jzy3d-native-panama-gl-swing`.

See http://www.jzy3d.org/plugins-depthpeeling.php


# Known problem on MacOSX 10.8.5 and beyond:

Can not find an existing GLProfile compatible with GL2 and supporting ARB buffer. Did comment ARB Buffer usage in shaders and it seams OK in most demos. Using GL2 instead of maxProgrammable(true) will make things "work" (start). 



# See also

https://forum.jogamp.org/Fail-to-run-a-depth-peeling-example-td3715480.html
https://coderedirect.com/questions/518899/writing-a-portable-java-application-using-jogl-and-android-opengl
https://stackoverflow.com/questions/35469370/opengl-performing-depth-peeling-in-a-scene-with-opaque-objects


