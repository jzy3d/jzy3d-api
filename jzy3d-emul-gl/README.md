EmulGL
=========

EmulGL is a Java implementation of OpenGL running *C*PU, hence allowing to avoid using native dependencies to *G*PU.

Traditional GPU rendering offers great performance but sometimes hit a compatibility issue for some rare {OS, JDK, GPU} combination. EmulGL processes the 3D scene inside the JVM with Java code only with rendering time below 40ms for most surface and scatter charts. This makes EmulGL a good fallback renderer for both offscreen chart and onscreen interactive chart that may be animated without visible lag (and without flooding CPU either).

EmulGL relies on [jGL](https://github.com/jzy3d/jzy3d-api/tree/master/jzy3d-jGL), a pure Java implementation of the OpenGL 1 specification. EmulGL remains relevant for simple charts.

Native charts (using [JOGL](https://jogamp.org/jogl/www/)) remains the preferred option for fast rendering of very large geometries, volumes or rendering involving shaders. Native charts also tend to better handle alpha blending (using translucent objects).

# EmulGL examples in integration tests

The below image are baseline for pixel-wise non regression tests that can be found [there](src/test/java/org/jzy3d/emulgl/integration).
<table>
<tr>
<td><img src="src/test/resources/ITTestEmulGLScatterChart.png"/></td>
<td><img src="src/test/resources/ITTestEmulGLSurfaceChart.png"/></td>
</tr>
</table>

# EmulGL code architecture

Making a chart CPU based for rendering is easy :
```java
IPainterFactory painter = new EmulGLPainterFactory();
ChartFactory factory = new AnyChartFactory(painter);
Chart chart = factory.newChart();
// then edit chart as usual
```

The base entry point for exploring EmulGL component is starting with `EmulGLPainterFactory` which handles all compatibility related objects, such as `EmulGLPainter`, `EmulGLCanvas`, `EmulGLViewOverlay`, `EmulGLViewAndColorbarsLayout`.

Using `EmulGLChartFactory` is not mandatory : making a chart EmulGL based only requires passing a `EmulGLPainterFactory` to any `ChartFactory`. `EmulGLChartFactory` is usefull to get a `CameraThreadControllerWithTime` which differs to native in that it make the chart rotation speed independent of the rendering speed.

To easily access implementations of EmulGL chart components without lot of downcasts, we added the below class :

```java
EmulGLSkin skin = EmulGLSkin.on(chart);
// returns the Emulgl Canvas to call its own methods
skin.getCanvas().setProfileDisplayMethod(true);
// returns the EmulGL Mouse to call its own methods
skin.getMouse().setPolicy(policy);
```

The next sections depict what is particular in EmulGL overrides of Jzy3D core.

## EmulGL Canvas

The below map describes how EmulGL canvas replies to all events.

<img src="doc/emulgl-canvas.png">

[Edit schema](https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=IG_tq9NVLe03#)


## EmulGL EmulGLViewAndColorbarsLayout (containing images)

<img src="../jzy3d-core-awt/src/main/java/org/jzy3d/plot3d/rendering/legends/colorbars/doc-files/colorbar-object-model.png"/>

# High pixel density screens (HiDPI, Retina)

HiDPI is supported by EmulGL on all JVM above Java 9. Until Java 8, AWT Canvas do not expose the pixel scale, so charts will simply render without HiDPI. Appart of this limitation, EmulGL will deal with multiple screens configuration on all OS whatever the pixel scales (100%, 150%, 200%, ...). It has also been tested successfully on 32' 4K screens.

EmulGL has the nice property of detecting pixel scale change over time and will handle resolution change of a chart when the application change screens at runtime. This allows handling all resolution dependent items (text size, image layout and scale, 2D post rendering, etc).

The below configuration asks for using HiDPI if it is available.

```java
Quality q = Quality.Advanced();
q.setHiDPI(HiDPI.ON);
```

This allows getting the current pixel scale of the chart.
```java
Coord2d pixelScale = chart.getView().getPixelScale();
```

Usefull links about HiDPI and java
* https://bugs.openjdk.java.net/browse/JDK-8055212
* https://intellij-support.jetbrains.com/hc/en-us/articles/360007994999-HiDPI-configuration
* https://cwiki.apache.org/confluence/display/NETBEANS/HiDPI+%28Retina%29+improvements


# Performance

I was able to have the following performance on a MacBook Pro (Retina 15 inches, 2013), 2,7 GHz Intel Core i7, RAM 16 Go 1600 MHz DDR3

[EmulGL Surface charts](https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials/src/main/java/org/jzy3d/demos/surface/SurfaceDemoEmulGL.java)
* A 60x60 polygon 3D surface in a 500x500 pixels frame is rendered in ~30ms
* A 60x60 polygon 3D surface in a 1440x800 pixel frame is rendered in ~45ms

[EmulGL Scatter charts](https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-tutorials/src/main/java/org/jzy3d/demos/scatter/ScatterDemoEmulGL.java)
* A 50.000 points 3D scatter in a 500x500 pixels frame is rendered in ~10ms
* A 500.000 points 3D scatter in a 500x500 pixels frame is rendered in ~90ms

Please report here the performance you encounter while running EmulGL charts by [adding comments to this issue](https://github.com/jzy3d/jzy3d-api/issues/149).

## Tuning performance

The quick hints to play with performance are below, if you are willing to understand how these parameters affect performance, read the next section.

### Reducing rendering quality in favor of faster repaint in all cases

```java
Quality q = Quality.Advanced;
q.setHiDPI(HiDPI.OFF); // prevent HiDPI/Retina to apply hence reduce the number of pixel to process

Chart chart = factory.newChart(q);
```

### Reducing rendering quality in favor of faster repaint only in case of rotation AND low performance

Visible rendering lag mainly occur when the chart rotates. This may happen according to two situations
* canvas is large
* hidpi is active on a computer that has the ability to enable HiDPI

This is a complex but usefull configuration as it will lower dynamically the rendering quality only if rendering performance drops (which will depend mainly on the computer running your program) to ensure rotation remains fluid.

```java
// Configure base quality for standard case
EmulGLChartFactory factory = new EmulGLChartFactory();
Quality q = Quality.Advanced;
q.setAnimated(false); // enable repaint on demand to minimize CPU usage when chart do not change
q.setHiDPI(HiDPI.ON); // enable HiDPI if hardware provides it

Chart chart = factory.newChart(q);
chart.open(1264, 812);

// Configure adaptive quality optimization upon slow rendering
AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
// The rate limiter will ensure mouse events won't flood AWT with repaints
policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
// This is the rendering time above which dynamic optimizer will trigger according to options
policy.optimizeForRenderingTimeLargerThan = 100;//ms
// If optimizer active, allow -or not- to disable faces of the surface polygons and only draw wireframe colored with face edge colors. Very good looking, fast rendering. Keep default value
policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = true;

// Apply this policy (or your override of this policy) to the adaptive mouse controller
EmulGLSkin skin = EmulGLSkin.on(chart);
skin.getMouse().setPolicy(policy);
skin.getThread().setSpeed(15);

```

Which will turn the chart in the below mode if _by default_ mouse drag leads to a rendering time above  `policy.optimizeForRenderingTimeLargerThan`. In that case, rendering drops face rendering to go faster and reach ~30ms to render which lead to a fluid chart rotation.

<img src="doc/adaptive-render-mouse-drag.png">


### Reducing liveness in favor of less CPU usage

```java
Quality q = Quality.Advanced;
q.setAnimated(false); // avoid rendering continuously, especially when chart does not need to change (most often)
// controllers (mouse, thread, etc) will have to update the chart themselves and won't rely on an animator.

Chart chart = factory.newChart(q);
```


### Improving rendering quality with possible liveness issues

```java
Quality q = Quality.Advanced;
q.setHiDPI(HiDPI.ON); // Let HiDPI/Retina inform their pixel ratio to process GL image on more pixels
q.setAnimated(true); // the chart repaints every 100ms, without knowing if hardware can handle it

// Possible liveness issue : mouse drag looks slow on large HiDPI screens as rendering is slower than usual
// Possible solution at runtime : diminish canvas size (make it smaller in your application)

Chart chart = factory.newChart(q);
```


# Repaint on demand VS repaint continuously

Repaint on demand means refreshing the canvas if at least one pixel changes. This is less CPU intensive in the long term as
chart are only changed if user interacts with it, add elements, or if the application resizes. Repainting on demand requires
however a carefull management of events that is exposed below and shows liveness issues in some rare conditions.

Repainting continuously allows getting rid off all these issues : a CPU thread will run [at a mean rate](https://github.com/jzy3d/jzy3d-api/blob/master/jzy3d-emul-gl/src/main/java/org/jzy3d/chart/EmulGLAnimator.java#L6) and ensure the chart always appear in the correct position without needing to handle refresh upon mouse, key or other thread events.

Setting repaint on demand is handled by
```java
Quality q = Quality.Advanced;
q.setAnimated(false);

Chart chart = factory.newChart(q);
chart.addMouseCameraController();
chart.addKeyboardCameraController();
```
Mouse, keyboard, and rotation thread controllers will behave according to the configuration (trigger repaint is repaint is
  on demand, do nothing if repaint continuously).


# Handling slow rendering

EmulGL/jGL run in CPU, hence the rendering performance remains sensitive to
* the number of pixels, hence the canvas size (the same processing unit (*C*PU) thread must handle all pixels)
* the hardware capacity (HiDMI & Retina display may multiply number of actual physicial pixels to draw if maximal quality is required)
* the number of drawables to draw (a 60x60 surface leads to 3600 polygons to render, that are sorted and lead to pixel change requests)
* the number of rendering requests (canvas resize, mouse, keyboard, or external rendering requests)

In the worse conditions, rendering time may reach a visible duration (e.g. 200ms). In that case, policies must
be defined to avoid freezing AWT either for rendering or for handling interactions. The below sections explain why the JVM
brings limitations, and how EmulGL resolves them.

## Integrating in AWT

### Unpredictability of AWT

Integrating in AWT is tricky because of how AWT works and how it will react to multiple events. Such events may be
* canvas display & resize occuring in the embedding application
* mouse events
* keyboard events
* application events (e.g. a NON GUI class allows edit a 3D parameter triggering a new rendering outside the AWT Thread)

I discovered a few thing about non predictability of rendering upon reaction to these events. These are my understanding but *I may wrong*.
Please correct me via [an issue](https://github.com/jzy3d/jzy3d-api/issues) if I am!

#### You don't know when AWT will really render

AWT send rendering or interaction events (mouse, ...) to the EventQueue.
The JVM will decide when it will be actually displayed.
I found no event or hook to get notified when an update happens.

<img src="doc/awt-event-queue.png">

[Edit schema](https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=wz_twV5gX99-#)

#### You can't be sure that all events will all be handled independently

The [EventQueue has the ability to coalesce multiple mouse or paint event](https://docs.oracle.com/javase/8/docs/api/java/awt/EventQueue.html#postEvent-java.awt.AWTEvent-)
in case it becomes overwhelmed by similar queries that it had no time to process. A mouse event or a canvas repaint on the
whole canvas (as EmulGL does) are events considered coalescable : the JVM is right dropping images display if a new image
rendering has been queried.

As a consequence, in the case multiple rotation command triggered by a mouse drag event -
and if these events lead to slow rendering, then you may only see the last rendering and not all intermediate images. This will
let the user feel that rotating is slow whereas intermidate rotation updates were just drop for display.

In that case, it is necessary to limit the event rate to ensure not too many rendering are triggered (said
differently, that repaint query are not arriving faster than the ability to compute what should
be drawn). ```EmulGLPainterFactory``` adds an adaptive rate limiter to the controllers (mouse, keyboard) to ensure they do not
flood AWT Event Queue. To ensure the rate limiter is optimal with the current canvas size/hidpi requirements  (e.g. not compromising liveness of a mouse rotation), it keeps an history
of past rendering time and fits to the poorest case plus a time margin of 20ms. I did this because I observed peaks in rendering time that may not be relevant for performance (optimal performance & liveness tradeoff in the beginning of chart lifetime may not relevant 1 minute later if the chart get resized). With this liveness is very fluid for small charts, and acceptable for fullscreen
canvas with HiDPI/Retina enabled, both holding high quality and alpha.

<img src="doc/awt-coalesce.png">

[Edit schema](https://lucid.app/lucidchart/78ec260b-d2d1-430d-a363-a95089dae86d/edit?page=IG_tq9NVLe03#)

All windowing toolkit event are not coalesced! For example the mouse wheel events do not seam to be coalesced, whereas mouse dragged events are.

#### Actual rendering in AWT

Rendering of AWT components is made with... OpenGL! But it is limited to 2D graphics only.



# Further work

## Fail : Performance with multithreading

We explored multithreaded rendering in `SurfaceDemoEmulGL_Multithreaded` which is a complete failure. jGL  won't support multithreading easily. OpenGL indeed requires a consistent call to a serie of commands (glBegin, glVertex, glEnd, etc) that prevent multiple
threads to deal with a sub group of geometries to render since the GL context will receive commands from multiple interlaced geometries.

Note that JOGL has the same limitation. We explored Newt canvas in JOGL supposed to allow such multithreaded access to the same OpenGL context but did not succeed. This attempt was made with `MultithreadedGraph` wich may not be designed properly for Newt.

Performance studies shows that handling all geometries draw() method is where an optimization may be done. Handling copy of colorbuffer to the canvas is negligeable compared
to handling all OpenGL drawing primitives.
