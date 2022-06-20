package org.jzy3d.emulgl.unit;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.os.OperatingSystem;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

public class TestCamera_EmulGL_Onscreen {

  @Test
  public void whenViewShoot_thenCameraIsProperlySet() throws InterruptedException {
    /*if (new OperatingSystem().isUnix()) {
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("IGNORE TEST THAT FAIL ON CLI & LINUX");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("  !!!   ");
      System.out.println("-------------------------------------------");
      return;
    }*/
    // LoggerUtils.minimal();

    // ---------------------
    // JZY3D CONTENT

    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced();

    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());

    // -----------------------------------
    // When Trigger canvas


    EmulGLCanvas canvas = (EmulGLCanvas) chart.getCanvas();
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

    ResizeBarrier resizeBarrier = new ResizeBarrier(canvas);

    IFrame frame = chart.open(this.getClass().getSimpleName(), FRAME_SIZE);
    Frame awtFrame = (Frame) frame;
    
    // let time to resize from default 500 to 800 before testing
    resizeBarrier.await(); 
    
    // Check target size was reached
    Rectangle CANVAS_SIZE = FRAME_SIZE.clone();
    sub(CANVAS_SIZE, awtFrame.getInsets());
    Assert.assertEquals(FRAME_SIZE.width, awtFrame.getSize().width);
    Assert.assertEquals(FRAME_SIZE.height, awtFrame.getSize().height);

    // Then viewport size is set to occupy the full canvas
    Assert.assertEquals(canvas.getSize().width, cam.getLastViewPort().getWidth());
    Assert.assertEquals(canvas.getSize().height, cam.getLastViewPort().getHeight());

    // ----------------------------------------
    // When change canvas size and update view

    CANVAS_SIZE = new Rectangle(100, 300);
    FRAME_SIZE = CANVAS_SIZE.clone();
    add(FRAME_SIZE, awtFrame.getInsets());

    // prepare a new latch
    resizeBarrier = new ResizeBarrier(canvas);

    // perform resize
    awtFrame.setBounds(0, 0, FRAME_SIZE.width, FRAME_SIZE.height);

    // let time to resize from default 500 to 800 before testing
    resizeBarrier.await();

    // print debug info
    System.out.println("TestCamEmulGLOnScreen : viewport : " + cam.getLastViewPort());
    System.out.println("TestCamEmulGLOnScreen : scale : " + chart.getView().getPixelScale());
    System.out.println("TestCamEmulGLOnScreen : frame.bounds : " + awtFrame.getBounds());
    System.out.println("TestCamEmulGLOnScreen : frame.insets : " + awtFrame.getInsets());
    System.out.println("TestCamEmulGLOnScreen : canvas.size : " + canvas.getSize());
    System.out.println("TestCamEmulGLOnScreen : viewport.size : " + cam.getLastViewPort());

    // Then viewport on the complete canvas
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, cam.getLastViewPort().getMode());
    Assert.assertEquals(canvas.getSize().width, cam.getLastViewPort().getWidth());
    Assert.assertEquals(canvas.getSize().height, cam.getLastViewPort().getHeight());

    // ----------------------------------------
    // When set STRETCH_TO_FILL, results must similar

    cam.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    chart.render();


    // Then viewport on the complete canvas
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, cam.getLastViewPort().getMode());
    Assert.assertEquals(canvas.getSize().height, cam.getLastViewPort().getHeight());
    Assert.assertEquals(canvas.getSize().width, cam.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set SQUARE

    // SQUARE MODE NOT SUPPORTED YET

  }

  private static void add(Rectangle initialFrameSize, Insets insets) {
    initialFrameSize.width += insets.left + insets.right;
    initialFrameSize.height += insets.top + insets.bottom;
  }

  private static void sub(Rectangle initialFrameSize, Insets insets) {
    initialFrameSize.width -= insets.left + insets.right;
    initialFrameSize.height -= insets.top + insets.bottom;
  }

  /** A helper allowing to pause code execution until a canvas resize is finished. */
  class ResizeBarrier {
    protected Component canvas;
    protected CountDownLatch latch;

    ComponentAdapter resizeBarrier;

    public ResizeBarrier(Component canvas) {
      this.canvas = canvas;
      this.latch = new CountDownLatch(1);

      this.resizeBarrier = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          System.out.println("TestCamera_EmulGL_Onscreen : Resized! Going ahead");
          latch.countDown();
          canvas.removeComponentListener(resizeBarrier);
          System.out.println("TestCamera_EmulGL_Onscreen : Removed listener");
        }
      };

      this.canvas.addComponentListener(resizeBarrier);
    }

    public void await() throws InterruptedException {
      System.out.println("TestCamera_EmulGL_Onscreen : Now waiting on the resize latch");
      latch.await();
    }
  }

}
