package org.jzy3d.emulgl.unit;

import java.awt.Insets;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestCamera_EmulGL_Onscreen {

  @Test
  public void whenViewShoot_thenCameraIsProperlySet() throws InterruptedException {
    if(new OperatingSystem().isUnix()) {
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("IGNORE TEST THAT FAIL ON CLI & LINUX");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("-------------------------------------------");
      return;
    }
    // LoggerUtils.minimal();

    // ---------------------
    // JZY3D CONTENT

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
    chart.add(SampleGeom.surface());


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

    IFrame frame = chart.open(this.getClass().getSimpleName(), FRAME_SIZE);

    Thread.sleep(1000);

    considerFrameBorder(FRAME_SIZE, (FrameAWT) frame);

    //chart.render();
    //chart.render();
    //System.out.println(cam.getLastViewPort());
    
    // Then viewport size is set to occupy the full frame
    Assert.assertEquals(FRAME_SIZE.width, cam.getLastViewPort().getWidth());
    Assert.assertEquals(FRAME_SIZE.height, cam.getLastViewPort().getHeight());

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

  private static void considerFrameBorder(Rectangle initialFrameSize, FrameAWT frame) {
    // consider frame border
    Insets insets = frame.getInsets();
    
    System.out.println("TestCamera_EmulGL_OnScreen : Insets : L:" + insets.left + ", R:" + insets.right + " T:" + insets.top  + " B:" + insets.bottom);
    
    initialFrameSize.width -= insets.left + insets.right;
    initialFrameSize.height -= insets.top + insets.bottom;
  }
}
