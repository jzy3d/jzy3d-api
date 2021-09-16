package org.jzy3d.emulgl.unit;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;


public class TestCamera_EmulGL_Onscreen {
@Ignore("Unstable from command line")
  @Test
  public void whenViewShoot_thenCameraIsProperlySet() throws InterruptedException {
    // LoggerUtils.minimal();

    // ---------------------
    // JZY3D CONTENT
    // EmulGLChartFactory factory = new SpyEmulGLChartFactory();

    EmulGLChartFactory factory = new EmulGLChartFactory() {
      @Override
      public Camera newCamera(Coord3d center) {
        // Camera camera = Mockito.spy((Camera) super.newCamera(center));
        Camera camera = new Camera(center);
        return camera;
      }

    };

    Quality q = Quality.Advanced();

    Chart chart = factory.newChart(q);
    chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); // INVESTIGUER POURQUOI AUTO_FIT!!!
    chart.add(surface());


    // -----------------------------------
    // When Trigger canvas
    EmulGLCanvas canvas = (EmulGLCanvas) chart.getCanvas();

    /// needed 7-10 sec up to there



    Camera cam = chart.getView().getCamera();

    // ----------------------------------------
    // Before opening window
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, cam.getLastViewPort().getMode());
    Assert.assertEquals(500, cam.getLastViewPort().getHeight());
    Assert.assertEquals(500, cam.getLastViewPort().getWidth());
    Assert.assertEquals(0, cam.getLastViewPort().getX());
    Assert.assertEquals(0, cam.getLastViewPort().getX());

    // ----------------------------------------
    // After opening window to a chosen size

    Rectangle FRAME_SIZE = new Rectangle(800, 600);
    int APP_BAR_HEIGHT = 22;


    chart.open(this.getClass().getSimpleName(), FRAME_SIZE);

    Thread.sleep(500);

    // Then viewport size is set to occupy the full frame
    Assert.assertEquals(FRAME_SIZE.width, cam.getLastViewPort().getWidth());
    Assert.assertEquals(FRAME_SIZE.height - APP_BAR_HEIGHT, cam.getLastViewPort().getHeight());

    // ----------------------------------------
    // When change canvas size and update view

    Rectangle CANVAS_SIZE = new Rectangle(100, 300);
    canvas.setSize(CANVAS_SIZE.width, CANVAS_SIZE.height);
    chart.getView().shoot();

    // Then viewport on the complete canvas
    Assert.assertEquals(CANVAS_SIZE.height, cam.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE.width, cam.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set STRETCH_TO_FILL

    cam.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    chart.getView().shoot();

    // Then viewport on the complete canvas
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, cam.getLastViewPort().getMode());
    Assert.assertEquals(CANVAS_SIZE.height, cam.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE.width, cam.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set SQUARE

    // SQUARE MODE NOT SUPPORTED YET

    /*
     * cam.setViewportMode(ViewportMode.SQUARE); chart.getView().shoot();
     * 
     * // Then viewport is SQUARE Assert.assertEquals(ViewportMode.SQUARE,
     * cam.getLastViewPort().getMode());
     * 
     * int sideLength = 100; Assert.assertEquals(sideLength, cam.getLastViewPort().getHeight());
     * Assert.assertEquals(sideLength, cam.getLastViewPort().getWidth());
     */



  }

  private static Shape surface() {

    // ---------------------------
    // DEFINE SURFACE MATHS
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 60;

    // ---------------------------
    // CUSTOMIZE SURFACE BUILDER FOR JGL
    SurfaceBuilder builder = new SurfaceBuilder();

    // ---------------------------
    // MAKE SURFACE
    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setPolygonOffsetFillEnable(false);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, 0.650f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }
}
