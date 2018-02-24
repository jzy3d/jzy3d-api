# jzy3d-tester

A set of tools to test charts

## ChartTester

ChartTester is A non regression test tool allowing to compare screenshots of charts.

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


## Replay

Replay is a tool to record a sequence of mouse actions on a chart and then replay the mouse scenario to ensure the final chart image is the same as the reference scenario. 

