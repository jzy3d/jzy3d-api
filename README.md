jzy3d-api
=========

Jzy3d is a framework for easily drawing 3d and 2d charts in Java, using either fast native GPU rendering or CPU based rendering. The framework targets simplicity and portability (AWT, SWT, NEWT, Swing, GPU/CPU, etc).


# How to use

Refer to the [tutorial README](jzy3d-tutorials/README.md) file to get help on creating your first chart project with the help of example code.

<table markdown=1>
  <tr>
    <td><img src="jzy3d-tutorials/doc/demo-scatter.png"></td>
    <td><img src="jzy3d-tutorials/doc/demo-surface.png"></td>
    <td><img src="jzy3d-tutorials/doc/demo-waterfall.png"></td>
    <!--<td colspan="2"><img src="jzy3d-tutorials/doc/demo-volume.png"></td>-->

  </tr>
  <tr>
<!--      <td><img src="jzy3d-tutorials/doc/demo-line-2d.png"></td>
    <td><img src="jzy3d-tutorials/doc/demo-debug-gl.png"></td>
-->
<td colspan="2"><img src="jzy3d-tutorials/doc/demo-volume-rotated.png"></td>
<td><img src="jzy3d-tutorials/doc/demo-debug-gl.png" width="500"></td>
  </tr>
</table>

As this library focuses on portability of the charts, the Surface tutorial package demonstrates the same chart running on AWT, SWT, Swing using native OpenGL powered by JOGL. In addition, `SurfaceDemoEmulGL` shows how to use CPU based rendering as a fallback, which only vary to the native demo by using a dedicated `EmulGLPainterFactory` :

```java
public class SurfaceDemoEmulGL {
  public static void main(String[] args) {
    Shape surface = surface();

    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced;
    Chart chart = factory.newChart(q);
    chart.add(surface);
    chart.open();

    // --------------------------------
    // Controllers

    CameraThreadController rotation = new CameraThreadController(chart);
    rotation.setStep(0.025f);
    rotation.setUpdateViewDefault(true);

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
    mouse.addSlaveThreadController(rotation);

    rotation.setUpdateViewDefault(true);
    mouse.setUpdateViewDefault(false);
    chart.setAnimated(true);
  }


  private static Shape surface() {
    SurfaceBuilder builder = new SurfaceBuilder();

    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);

    ColorMapper colorMapper =
        new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, 0.65f));

    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }
}
```

# Architecture

Creating a chart implies building and wiring the below high-level components.

<img src="doc/Components.png"/>


## Customize chart with factories

The ```IChartFactory``` builds all objects that will define how the chart will look (```Axis```, ```View```, ```Camera```, ```Chart```).

The ```IPainterFactory``` builds every objects that allow compatibility across windowing toolkits and GPU/CPU. The chart factories and drawable have no knowledge of concrete AWT, SWT, Swing, etc. This is all powered by the painter factory introduced in Jzy3d 2.0.

The ```Drawable``` class hierarchy defines geometries able to use a ```IPainter``` to draw something.

<img src="doc/Factories.png"/>


### Native and emulated elements

<img src="doc/Interop.png"/>

## Dependent libraries

Jzy3d depends on the following libraries that are available on [Jzy3d Maven repository](http://maven.jzy3d.org) (See tutorials for recommended Maven setup)
* [jGL](https://github.com/jzy3d/jGL) provides a pure Java implementation of OpenGL hence allowing CPU rendering as an alternative to GPU rendering (JOGL)
* [jPLY](https://github.com/jzy3d/jPLY) supports the PLY format for loading 3d objects
* [vecmath](https://github.com/jzy3d/vecmath) is a clone of a former java package


# Changes in 2.0 version

Version 2.0 is a major refactor to allow using multiple OpenGL implementations, which opened the door to EmulGL. To ease porting your 1.* charts, we add the following cheatsheet.

## Renamings

| Class name in 1.* | Class name in 2.0 |
|-------------------|-------------------|
| AbstractDrawable | Drawable |
| AbstractWireframeable | Wireframeable |
| AxeBox | AxisBox |
| DrawableTexture | NativeDrawableImage & EmulGLDrawableImage |
|||
| _IChartComponentFactory_ | _IChartFactory_ |
| AWTChartComponentFactory | AWTChartFactory |
| NewtChartComponentFactory | NewtChartFactory |
| JavaFXChartComponentFactory | :exclamation: JavaFXChartFactory |
| SwingChartComponentFactory | SwingChartFactory |
| SWTChartComponentFactory | SWTChartFactory |
|  | FallbackChartFactory |
|||
| ColorbarViewportLayout | ViewAndColorbarsLayout |
| ViewMouseController | NewtViewCameraController |

:exclamation: work in progress.

SurfaceBuilder is not static anymore to be overridable.


## Additions

* `IPainter`
* `IPainterFactory`
* `EmulGLPainterFactory`
* `IAnimator`
* `IImageWrapper` and `SymbolHandler`


## Deletions


# Extensions

Additional modules kept separated demonstrate side works on Jzy3d
- <a href="https://github.com/jzy3d/bigpicture">jzy3d-bigpicture</a> : drivers to few big data storage to draw massive amount of points
- <a href="https://github.com/jzy3d/jzy3d-graphs">jzy3d-graph</a> : 3d graphs layout and rendering using Gephi toolkit
- <a href="https://github.com/jzy3d/jzy3d-spectro">jzy3d-spectro</a> : 3d spectrogram


# Build

```
mvn install
```

# License

New BSD

# More information

http://www.jzy3d.org

<!--Travis build status : [![Build Status](https://travis-ci.org/jzy3d/jzy3d-api.svg?branch=master)](https://travis-ci.org/jzy3d/jzy3d-api)-->
