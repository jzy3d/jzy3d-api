package org.jzy3d.plot3d.rendering.view;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestViewModeAndCameraClippingPlanes {
  /**
   * WARNING!!!
   * 
   * This is a poor way of checking if a view is in initialized state after a call to open, since
   * actual required wait time will depend on the computer executing the test.
   * 
   * We could use a CountDownLatch to test this but fear the test becomes harder to read.
   */
  private static final int WAIT_FOR_GL_INIT_MS = 250;

  /////////////////////////////////////////////////////////////////////////////////////
  //
  // EMPTY CHARTS
  //
  /////////////////////////////////////////////////////////////////////////////////////


  @Test
  public void givenNativeOnscreenChart_whenEmpty_thenBoundsAreCorrect() {

    // Given
    ChartFactory factory = new AWTChartFactory();
    // factory.getPainterFactory().setOffscreen(500, 500);

    // --------------------------
    // When init a chart

    Chart chart = factory.newChart();

    // --------------------------
    // Then view is not initialized yet

    Assert.assertFalse("An onscreen chart won't have view initialized until the chart is open",
        chart.getView().isInitialized());

    // The 0,0 init comes from scene.getGraph.getBounds() which returns 0,0 if graph is empty
    // Which is not cool : view should decide which bound to apply to empty graph, not being slaved
    // by graph, n
    // neither by AxisBox which will setup to 0,1 if the input box isReset()
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(0, 0, 0, 0, 0, 0),
        chart.getView().getBounds()); // <<<<<<<<<<<<<<<<<<<<<<

    // --------------------------
    // When open and wait for GL initialization
    
    chart.open();
    chart.sleep(WAIT_FOR_GL_INIT_MS);

    
    
    
    // --------------------------
    // Then view is considered initialized

    Assert.assertTrue(chart.getView().isInitialized());
    
    
    
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(0, 0, 0, 0, 0, 0),
        chart.getView().getBounds()); // <<<<<<<<<<<<<<<<<<<<<<


  }

  @Test
  public void givenNativeOffscreenChart_whenEmpty_thenBoundsAreCorrect() {

    // Given
    ChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then


    Assert.assertFalse(
        "An offscreen chart has its view is non initialized state before adding any drawable",
        chart.getView().isInitialized());

    // The 0,0 init comes from scene.getGraph.getBounds() which returns 0,0 if graph is empty
    // Which is not cool : view should decide which bound to apply to empty graph, not being slaved
    // by graph, n
    // neither by AxisBox which will setup to 0,1 if the input box isReset()
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(0, 0, 0, 0, 0, 0),
        chart.getView().getBounds()); // <<<<<<<<<<<<<<<<<<<<<<


    // --------------------------
    // Need to call render (or add(..) or view.updateBounds)
    // to get the view in initialized state

    chart.render();
    // chart.getView().updateBounds();

    Assert.assertTrue(
        "An offscreen chart has its view is initialized after render() or updateBounds(), without needing to call open() or add(drawable)",
        chart.getView().isInitialized());

  }

  /////////////////////////////////////////////////////////////////////////////////////
  //
  // CHART WITH A SINGLE SIMPLE SURFACE
  //
  /////////////////////////////////////////////////////////////////////////////////////



  @Test
  public void givenNativeOffscreenChart_whenAddSimpleSurface_thenBoundsAreCorrect() {

    // Given

    ChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then

    Assert.assertFalse(
        "An offscreen chart has its view is non initialized state before adding any drawable",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.add(SampleGeom.surface());

    // --------------------------
    // Then

    Assert.assertTrue(
        "An offscreen chart has its view is initialized after add(drawable), without needing to call open()",
        chart.getView().isInitialized());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertFalse("Near clipping plane != 0", 0 == chart.getView().getCamera().getNear());
    Assert.assertFalse("Far clipping plane != 0", 0 == chart.getView().getCamera().getFar());
  }

  /** Pass from IDE but not from CLI even when growing INIT TIME ??!! */
@Ignore
  @Test
  public void givenNativeOnscreenChart_whenAddSimpleSurface_thenBoundsAreCorrect() {

    // Given

    ChartFactory factory = new AWTChartFactory();

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then

    Assert.assertFalse(
        "An onscreen chart has its view is non initialized state until open is called",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.add(SampleGeom.surface());

    // --------------------------
    // Then

    Assert.assertFalse(
        "An onscreen chart has its view is non initialized state until open is called",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.open();
    chart.sleep(WAIT_FOR_GL_INIT_MS);
    
    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart has its view is initialized after add(drawable) and to call open()",
        chart.getView().isInitialized());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertFalse("Near clipping plane != 0", 0 == chart.getView().getCamera().getNear());
    Assert.assertFalse("Far clipping plane != 0", 0 == chart.getView().getCamera().getFar());
  }


  /////////////////////////////////////////////////////////////////////////////////////
  //
  // CHART WITH A SINGLE VBO SURFACE (I.E LATE BOUNDING BOX DEFINITION)
  //
  /////////////////////////////////////////////////////////////////////////////////////

  @Test
  public void givenNativeOffscreenChart_whenAddVBOSurface_BeforeOpen_thenBoundsAreCorrect() {

    // Given

    ChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then

    Assert.assertFalse(
        "An offscreen chart has its view is non initialized state before adding any drawable",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.add(new DrawableVBO2(SampleGeom.surface()));

    // --------------------------
    // Then

    Assert.assertTrue(
        "An offscreen chart has its view is initialized after add(drawable), without needing to call open()",
        chart.getView().isInitialized());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
  }

  // @Ignore
  @Test
  public void givenNativeOnscreenChart_whenAddVBOSurface_BeforeOpen_thenBoundsAreCorrect() {

    // Given
    ChartFactory factory = new AWTChartFactory();

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then

    Assert.assertFalse(chart.getView().isInitialized());

    // --------------------------
    // When

    chart.add(new DrawableVBO2(SampleGeom.surface()));

    // --------------------------
    // Then

    Assert.assertFalse(
        "An onscreen chart won't have view initialized until the chart is open, even with a add(drawable)",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.open();
    chart.sleep(WAIT_FOR_GL_INIT_MS);

    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart will have its view initialized after calling open() AND waiting a bit for the GL context intialization",
        chart.getView().isInitialized());


    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
  }


}
