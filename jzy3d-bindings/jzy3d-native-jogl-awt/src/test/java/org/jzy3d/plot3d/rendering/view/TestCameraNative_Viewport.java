package org.jzy3d.plot3d.rendering.view;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

import com.jogamp.opengl.awt.GLCanvas;

/**
 * TODO : MUST ALSO TEST WITH COLORBARS TODO : MUST ALSO VERIFY APPROPRIATE LOOK_TO AND ORTHO CALL
 * OF THE CAMERA!!
 *
 * Possible improvement : mock canvas (avoid displaying for faster test, independent from OS)
 *
 * @author martin
 */

//@Ignore("NOT ABLE TO MAKE A CONSISTENT TEST OVER ALL OS YET")
public class TestCameraNative_Viewport {

  private static final int WAIT_WINDOW_CHANGE_300MS = 1000;


  public static void main(String[] args) {
    // GIVEN
    AWTChartFactory factory = new AWTChartFactory();

    Quality q = Quality.Advanced();

    // ATTENTION : viewport of a retina display has double number of pixel
    // Also, the Y value is 600, whereas the height is 578
    q.setPreserveViewportSize(true);

    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    Camera camera = chart.getView().getCamera();

    // ----------------------------------------
    // When opening window to a chosen size

    Rectangle FRAME_SIZE = new Rectangle(800, 600);
    int APP_BAR_HEIGHT = 22; // pixel number of Application bar on top

    // factory.getPainterFactory().setOffscreen(FRAME_SIZE);

    FrameAWT frame = (FrameAWT) chart.open("", FRAME_SIZE);
    // ((java.awt.Frame)frame).setUndecorated(true);
    // System.out.println(frame.getInsets());


    try {
      Thread.sleep(WAIT_WINDOW_CHANGE_300MS);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // let time for opening window otheriwse follwing assertions may fail

    // Then scene viewport size is set to occupy the full frame
    ViewAndColorbarsLayout layout =
        (ViewAndColorbarsLayout) ((ChartView) chart.getView()).getLayout();
    ViewportConfiguration sceneViewport = layout.getSceneViewport();


    System.out.println(
        chart.getCanvas().getRendererWidth() + "," + chart.getCanvas().getRendererHeight());

    GLCanvas cnv = (GLCanvas) chart.getCanvas();
    System.out.println(cnv.getSurfaceWidth() + "," + cnv.getSurfaceHeight());



    // System.out.println(sceneViewport.getWidth() + "," + sceneViewport.getHeight());
  }


  @Test
  public void whenResize_thenCameraViewportUpdatesAccordingToMode() throws InterruptedException {
    // GIVEN
    AWTChartFactory factory = new AWTChartFactory();

    Quality q = Quality.Advanced();

    // ATTENTION : viewport of a retina display has double number of pixel
    // Also, the Y value is 600, whereas the height is 578
    q.setPreserveViewportSize(true);

    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    Camera camera = chart.getView().getCamera();

    // ----------------------------------------
    // When opening window to a chosen size

    Rectangle FRAME_SIZE = new Rectangle(800, 600);

    FrameAWT frame = (FrameAWT) chart.open(this.getClass().getSimpleName(), FRAME_SIZE);
    chart.render(); // ensure we have rendered one to get latest layout later

    // number of pixel that the frame keeps for non drawing area (title bar, etc)
    int HEIGHT_DEC = frame.getInsets().top + frame.getInsets().bottom;
    int WIDTH_DEC = frame.getInsets().left + frame.getInsets().right;

    System.out.println("Frame size   : " + FRAME_SIZE.width + "x" + FRAME_SIZE.height);
    System.out.println("Frame insets : " + frame.getInsets());


    Thread.sleep(WAIT_WINDOW_CHANGE_300MS); // let time for opening window otheriwse follwing assertions may fail

    // Then scene viewport size is set to occupy the full frame
    ViewAndColorbarsLayout layout =
        (ViewAndColorbarsLayout) ((ChartView) chart.getView()).getLayout();
    ViewportConfiguration sceneViewport = layout.getSceneViewport();

    Assert.assertEquals(FRAME_SIZE.width - WIDTH_DEC, sceneViewport.getWidth());
    Assert.assertEquals(FRAME_SIZE.height - HEIGHT_DEC, sceneViewport.getHeight());


    Assert.assertEquals(0, sceneViewport.getX());
    Assert.assertEquals(FRAME_SIZE.height - HEIGHT_DEC, sceneViewport.getY());

    // Then camera viewport size is set to occupy the full frame
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, camera.getLastViewPort().getMode());
    Assert.assertEquals(FRAME_SIZE.width - WIDTH_DEC, camera.getLastViewPort().getWidth());
    Assert.assertEquals(FRAME_SIZE.height - HEIGHT_DEC, camera.getLastViewPort().getHeight());

    // ----------------------------------------
    // When change canvas size and update view

    Rectangle CANVAS_SIZE_V = new Rectangle(200, 300);
    frame.setBounds(0, 0, CANVAS_SIZE_V.width, CANVAS_SIZE_V.height);
    frame.repaint();

    Thread.sleep(300); // let time for resize and redraw otherwise following assertions may fail

    // number of pixel that the frame keeps for non drawing area (title bar, etc)
    HEIGHT_DEC = frame.getInsets().top + frame.getInsets().bottom;
    WIDTH_DEC = frame.getInsets().left + frame.getInsets().right;

    System.out.println(frame.getInsets());
    System.out.println(chart.getCanvas().getPixelScale());

    // Then viewport on the complete canvas
    Assert.assertEquals(CANVAS_SIZE_V.height - HEIGHT_DEC, camera.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE_V.width - WIDTH_DEC, camera.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set STRETCH_TO_FILL

    camera.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    chart.getView().shoot();

    // Then viewport on the complete canvas
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, camera.getLastViewPort().getMode());
    Assert.assertEquals(CANVAS_SIZE_V.height - HEIGHT_DEC, camera.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE_V.width - WIDTH_DEC, camera.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set SQUARE in vertical rectangle frame

    camera.setViewportMode(ViewportMode.SQUARE);
    chart.getView().shoot();
    // chart.render();

    System.out.println(camera.getLastViewPort());

    // Then viewport is SQUARE
    Assert.assertEquals(ViewportMode.SQUARE, camera.getLastViewPort().getMode());

    int sideLength = CANVAS_SIZE_V.width - WIDTH_DEC;
    Assert.assertEquals(sideLength, camera.getLastViewPort().getHeight());
    Assert.assertEquals(sideLength, camera.getLastViewPort().getWidth());
    Assert.assertEquals(0, camera.getLastViewPort().getX());
    // Assert.assertEquals(0, camera.getLastViewPort().getY());

    // ----------------------------------------
    // When set SQUARE in a horizontal rectangle frame

    camera.setViewportMode(ViewportMode.SQUARE);

    Rectangle CANVAS_SIZE_H = new Rectangle(400, 100);
    frame.setBounds(0, 0, CANVAS_SIZE_H.width, CANVAS_SIZE_H.height);
    frame.repaint();

    Thread.sleep(WAIT_WINDOW_CHANGE_300MS); // let time for resize and redraw otherwise following assertions may fail

    sideLength = CANVAS_SIZE_H.height - HEIGHT_DEC;
    Assert.assertEquals(sideLength, camera.getLastViewPort().getHeight());
    Assert.assertEquals(sideLength, camera.getLastViewPort().getWidth());

    // check viewport is shifted to the right so that square viewport is centered
    // Assert.assertEquals(CANVAS_SIZE_H.width / 2 - sideLength / 2,
    // camera.getLastViewPort().getX());

  }
}
