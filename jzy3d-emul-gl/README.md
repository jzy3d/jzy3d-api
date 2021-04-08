EmulGL
=========

EmulGL is a Java implementation of OpenGL running *C*PU, hence allowing to avoid using native dependencies to *G*PU.

Traditional GPU rendering offers great performance but sometimes hit a compatibility issue for some rare {OS, JDK, GPU} combination. EmulGL processes the 3D scene inside the JVM with Java code only with rendering time below 40ms for most surface and scatter charts. This makes EmulGL a good fallback renderer for both offscreen chart and onscreen interactive chart that may be animated without visible lag (and without flooding CPU either).

EmulGL relies on [jGL](https://github.com/jzy3d/jGL), a pure Java implementation of the OpenGL 1 specification. EmulGL remains relevant for simple charts.

Native charts (using JOGL) remain the preferred option for fast rendering of very large geometries, volumes or rendering involving shaders. Native charts also tend to better handle alpha blending (using translucent objects).

# Performance

Despite not exhaustive at all, I was able to have the following performance on a MacBook Pro (Retina 15 inches, 2013), 2,7 GHz Intel Core i7, RAM 16 Go 1600 MHz DDR3

[EmulGL Surface charts](https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials/src/main/java/org/jzy3d/demos/surface/SurfaceDemoEmulGL.java)
* A 60x60 polygon 3D surface in a 500x500 pixels frame is rendered in ~30ms
* A 60x60 polygon 3D surface in a 1440x800 pixel frame is rendered in ~45ms

[EmulGL Scatter charts](https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials/src/main/java/org/jzy3d/demos/scatter/ScatterDemoEmulGL.java)
* A 50.000 points 3D scatter in a 500x500 pixels frame is rendered in ~10ms
* A 500.000 points 3D scatter in a 500x500 pixels frame is rendered in ~90ms

Please report here the performance you encounter while running EmulGL charts by [adding comments to this issue](https://github.com/jzy3d/jzy3d-api/issues/149). 

# Implementation

[jGL readme](https://github.com/jzy3d/jGL/blob/master/README.md) is the best place to better understand how OpenGL is implemented and how the framework is structured.

# Remarks

## HiDPI

EmulGL supports HiDPI rendering by enabling a chart with `Quality.setPreserveViewportSize(false);` 
(which actually forbids to preserve the usual pixel ratio in case a HiDPI configuration is detected)


We noticed the following limitations with HiDPI on EmulGL as it is currently implemented
* We noticed that HiDPI may not trigger on Java 8, whereas it works on Java 9. This is highlighted by `ITTestHiDPI` that is kept 
as a program with main() rather than junit test.
* HiDPI in jGL is detected at runtime and that chart will properly scale to HiDPI after a first rendering. A chart configured with `chart.setAnimated(true)` and `Quality.setPreserveViewportSize(false);` will automatically turn to HiDPI at the second frame.
* Offscreen charts currently do not seem to support HiDPI. 


# Further work

## Fail : Performance with multithreading

We explored multithreaded rendering in SurfaceDemoEmulGL_Multithreaded which is a complete failure. jGL  
won't support multithreading easily. OpenGL indeed requires a consistent call to a serie of commands (glBegin, glVertex, glEnd, etc) that prevent multiple
threads to deal with a sub group of geometries to render since the GL context will receive commands from multiple interlaced geometries.
Note that JOGL has the same limitation. We explored Newt canvas in JOGL supposed to allow such multithreaded access to the same OpenGL context but did not succeed.

Performance studies shows that handling all geometries draw() method is where an optimization may be done. Handling copy of colorbuffer to the canvas is negligeable compared
to handling all OpenGL drawing primitives.

   