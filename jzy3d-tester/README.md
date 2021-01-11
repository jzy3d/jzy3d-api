# jzy3d-tester

A set of tools to test charts

## ChartTester

ChartTester is a non regression test tool allowing to compare screenshots of charts.

Works as follow
- Generates an image first time the test is ran. The developer should verify the image is correct.
- Compare a chart screenshot with the reference image. Test succeed if the two images are similar. If image differ, a DIFF image is generated indicating where pixel differ.

```java
Chart chart = ...;
ChartTester.assertSimilar(chart, "path/to/reference.png");

```



## Mocks

A simple implementation of GL interface is provided to keep in memory all calls to GL primitives (glVertex3f, glColor, etc).

It is used to ensure Jzy3d geometries and datamodel properly lead to expected GL calls :

```java
// Given
Point p = new Point();
p.setData(new Coord3d(3, 30, 1000));

// When
GLMock glMock = new GLMock();
p.draw(glMock, null, null);

// Then
Assert.assertTrue(glMock.vertex3f_contains(3, 30, 1000));
```


## DebugGL

These are 2D and 3D charts displaying properties of a 3D chart that should be debugged.
I created it while I was banging my head understanding why Logarithmic charts where not working as expected.

<img src="doc/debug_gl.png"/>

* The big chart on the right is the one currently being debugged
* The top left chart shows the main chart axisbox in blue, and the camera with dots.
* The bottom left chart shows properties of the camera.

Watching chart properties for debugging is as easy as follow

```java
DebugGLChart2d debugChart2d = new DebugGLChart2d(d.getChart());
debugChart2d.watch("far", Color.BLUE, c->c.getView().getCamera().getFar());
debugChart2d.watch("radius", Color.GREEN, c->c.getView().getCamera().getRenderingSphereRadius());
debugChart2d.watch("viewpoint.x", Color.RED, c->c.getView().getViewPoint().x);
```



## Replay

Replay is a tool to record a sequence of mouse actions on a chart and then replay the mouse scenario to ensure the final chart image is the same as the reference scenario.
