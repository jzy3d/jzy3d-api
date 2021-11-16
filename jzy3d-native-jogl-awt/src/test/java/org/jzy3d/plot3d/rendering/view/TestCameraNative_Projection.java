package org.jzy3d.plot3d.rendering.view;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.maths.BoundingBox3d.Corners;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Possible improvement : mock canvas (avoid displaying for faster test, independent from OS)
 * 
 * @author martin
 */
public class TestCameraNative_Projection {
  static int APP_BAR_HEIGHT = 22; // pixel number of Application bar on top
  static Rectangle CANVAS_SIZE = new Rectangle(800, 600);


  @Test
  public void whenOptimalViewpoint_thenCameraProjectAxisCornersToTheYminAndYmaxCoordinates()
      throws InterruptedException, IOException {
    // GIVEN
    AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(CANVAS_SIZE);

    Quality q = Quality.Advanced();

    // ATTENTION : viewport of a retina display has double number of pixel
    // Also, the Y value is 600, whereas the height is 578
    q.setPreserveViewportSize(true);

    Chart chart = factory.newChart(q);
    chart.add(SampleGeom.surface());
    factory.getPainterFactory().setOffscreen(CANVAS_SIZE);
    //chart.open(this.getClass().getSimpleName(), FRAME_SIZE); // requires considering OS dependent frame top bar
    //chart.addMouseCameraController();


    // -----------------------------------------------------------------------
    // When Viewpoint is supposed to project AxisBox so that corner touch canvas border
    chart.getView().setViewPoint(View.VIEWPOINT_AXIS_CORNER_TOUCH_BORDER, true);


    //Thread.sleep(500);

    // -----------------------------------------------------------------------
    // Then at least one corner of AxisBox touch canvas top and bottom border

    chart.screenshot(new File("target/" + this.getClass().getSimpleName() + ".png"));

    assertAxisCornersTouchCanvasTopAndBottomBorder(chart, CANVAS_SIZE);

  }

  private void assertAxisCornersTouchCanvasTopAndBottomBorder(Chart chart, Rectangle FRAME_SIZE) {
    NativeDesktopPainter p = (NativeDesktopPainter) chart.getPainter();
    p.getCurrentContext(chart.getCanvas()).makeCurrent();


    int MAX_PIXEL_NUMBER_TO_FRAME_BORDER = 3;

    boolean atLeastOneCornerNearTop = false;
    boolean atLeastOneCornerNearBottom = false;


    Corners corners = ((AxisBox) chart.getView().getAxis()).getCorners();
    Camera camera = chart.getView().getCamera();

    for (Coord3d corner : corners.getAll()) {
      // System.out.println("3d : " + corner);
      Coord3d corner2d = camera.modelToScreen(chart.getPainter(), corner);

      if (corner2d.y < MAX_PIXEL_NUMBER_TO_FRAME_BORDER) {
        atLeastOneCornerNearBottom = true;
      }

      if (corner2d.y > (FRAME_SIZE.height /*- APP_BAR_HEIGHT*/) - MAX_PIXEL_NUMBER_TO_FRAME_BORDER) {
        atLeastOneCornerNearTop = true;
      }
      //System.out.println(" 2d : " + corner2d);
    }
    //System.out.println(FRAME_SIZE.height - APP_BAR_HEIGHT);

    Assert.assertTrue("At least one corner is near to canvas top border (tolerance "
        + MAX_PIXEL_NUMBER_TO_FRAME_BORDER + " pixels)", atLeastOneCornerNearTop);
    Assert.assertTrue("At least one corner is near to canvas bottom border (tolerance "
        + MAX_PIXEL_NUMBER_TO_FRAME_BORDER + " pixels)", atLeastOneCornerNearBottom);

    p.getCurrentContext(chart.getCanvas()).release();
  }
}
