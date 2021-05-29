package org.jzy3d.plot3d.rendering.view;

import java.awt.Insets;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

/**
 * TODO : MUST ALSO TEST WITH COLORBARS TODO : MUST ALSO VERIFY APPROPRIATE LOOK_TO AND ORTHO CALL
 * OF THE CAMERA!!
 * 
 * Possible improvement : mock canvas (avoid displaying for faster test, independent from OS)
 * 
 * @author martin
 */
public class TestCameraNative_Viewport {

  @Test
  public void whenResize_thenCameraViewportUpdatesAccordingToMode() throws InterruptedException {
    // GIVEN
    AWTChartFactory factory = new AWTChartFactory();

    Quality q = Quality.Advanced();

    // ATTENTION : viewport of a retina display has double number of pixel
    // Also, the Y value is 600, whereas the height is 578
    q.setPreserveViewportSize(true);

    Chart chart = factory.newChart(q);
    chart.add(surface());
    Camera camera = chart.getView().getCamera();

    // ----------------------------------------
    // When opening window to a chosen size

    Rectangle FRAME_SIZE = new Rectangle(800, 600);

    // factory.getPainterFactory().setOffscreen(FRAME_SIZE);

    FrameAWT frame = (FrameAWT) chart.open(this.getClass().getSimpleName(), FRAME_SIZE);

    chart.render(); // ensure we have rendered one to get latest layout later

    Thread.sleep(10); // let time for opening window otherwise following assertions may fail

    // Then scene viewport size is set to occupy the full frame
    ViewAndColorbarsLayout layout =
        (ViewAndColorbarsLayout) ((ChartView) chart.getView()).getLayout();
    ViewportConfiguration sceneViewport = layout.getSceneViewport();

    considerFrameBorder(FRAME_SIZE, frame);

    Assert.assertEquals(FRAME_SIZE.width, sceneViewport.getWidth());
    Assert.assertEquals(FRAME_SIZE.height, sceneViewport.getHeight());
    Assert.assertEquals(0, sceneViewport.getX());
    Assert.assertEquals(FRAME_SIZE.height, sceneViewport.getY());

    // Then camera viewport size is set to occupy the full frame
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, camera.getLastViewPort().getMode());
    Assert.assertEquals(FRAME_SIZE.width, camera.getLastViewPort().getWidth());
    Assert.assertEquals(FRAME_SIZE.height, camera.getLastViewPort().getHeight());

    // ----------------------------------------
    // When change canvas size and update view

    Rectangle CANVAS_SIZE_V = new Rectangle(200, 400); // Frame may have a minimum size!
    frame.setBounds(0, 0, CANVAS_SIZE_V.width, CANVAS_SIZE_V.height);
    frame.repaint();

    Thread.sleep(300); // let time for resize and redraw otherwise following assertions may fail

    considerFrameBorder(CANVAS_SIZE_V, frame);

    // Then viewport on the complete canvas
    Assert.assertEquals(CANVAS_SIZE_V.height, camera.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE_V.width, camera.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set STRETCH_TO_FILL

    camera.setViewportMode(ViewportMode.STRETCH_TO_FILL);
    chart.getView().shoot();

    // Then viewport on the complete canvas
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, camera.getLastViewPort().getMode());
    Assert.assertEquals(CANVAS_SIZE_V.height, camera.getLastViewPort().getHeight());
    Assert.assertEquals(CANVAS_SIZE_V.width, camera.getLastViewPort().getWidth());

    // ----------------------------------------
    // When set SQUARE in vertical rectangle frame

    camera.setViewportMode(ViewportMode.SQUARE);
    chart.getView().shoot();

    // Then viewport is SQUARE
    Assert.assertEquals(ViewportMode.SQUARE, camera.getLastViewPort().getMode());

    int sideLength = CANVAS_SIZE_V.width;
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

    Thread.sleep(300); // let time for resize and redraw otherwise following assertions may fail

    considerFrameBorder(CANVAS_SIZE_H, frame);
    sideLength = CANVAS_SIZE_H.height;
    Assert.assertEquals(sideLength, camera.getLastViewPort().getHeight());
    Assert.assertEquals(sideLength, camera.getLastViewPort().getWidth());
    // check viewport is shifted to the right so that square viewport is centered
    Assert.assertEquals(CANVAS_SIZE_H.width / 2 - sideLength / 2, camera.getLastViewPort().getX());

  }

  private static void considerFrameBorder(Rectangle initialFrameSize, FrameAWT frame) {
    // consider frame border
    Insets insets = frame.getInsets();
    initialFrameSize.width -= insets.left + insets.right;
    initialFrameSize.height -= insets.top + insets.bottom;
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
