package org.jzy3d.plot3d.rendering.view;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.events.ViewLifecycleAdapter;
import org.jzy3d.events.ViewLifecycleEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

/**
 * Test behaviours of {@link View} and {@link Camera}.
 * 
 * Especially foster on verifying that view.getBounds() always return something relevant in case of
 * <ul>
 * <li>Empty scene OR Scene made of geometry with known bounds OR geometry with bounds that are known later (VBO).
 * <li>Check bounds wether object are added before or after opening an onscreen chart, or while using an offscreen one (which never opens).
 * </ul>
 * @author martin
 *
 */
public class TestViewModeAndCameraClippingPlanes {
  /** Max time to wait before considering initialization failed.*/
  private static final int GL_INIT_MAX_WAIT_SECONDS = 10;
  
  /////////////////////////////////////////////////////////////////////////////////////
  //
  // EMPTY CHARTS
  //
  /////////////////////////////////////////////////////////////////////////////////////

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

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(-1, 1, -1, 1, -1, 1),
        chart.getView().getBounds()); 


    // --------------------------
    // Need to call render (or add(..) or view.updateBounds)
    // to get the view in initialized state
    // Bounds mode and value must not change

    chart.render();
    // chart.getView().updateBounds();

    Assert.assertTrue(
        "An offscreen chart has its view is initialized after render() or updateBounds(), without needing to call open() or add(drawable)",
        chart.getView().isInitialized());
    
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(-1, 1, -1, 1, -1, 1),
        chart.getView().getBounds()); 


  }

  @Test
  public void givenNativeOnscreenChart_whenEmpty_thenBoundsAreCorrect() throws InterruptedException {

    // Given
    ChartFactory factory = new AWTChartFactory();

    // --------------------------
    // When init a chart

    Chart chart = factory.newChart();

    // --------------------------
    // Then view is not initialized yet

    Assert.assertFalse("An onscreen chart won't have view initialized until the chart is open",
        chart.getView().isInitialized());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    
    Assert.assertEquals(new BoundingBox3d(-1, 1, -1, 1, -1, 1),
        chart.getView().getBounds());

    // --------------------------
    // When open and wait for GL initialization

    openAndWaitForInitializationCompletion(chart, GL_INIT_MAX_WAIT_SECONDS, TimeUnit.SECONDS);


    // --------------------------
    // Then view is considered initialized

    Assert.assertTrue(chart.getView().isInitialized());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
    
    Assert.assertEquals(new BoundingBox3d(-1, 1, -1, 1, -1, 1),
        chart.getView().getBounds());


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


  @Test
  public void givenNativeOnscreenChart_whenAddSimpleSurface_BeforeOpen_thenBoundsAreCorrect() throws InterruptedException {

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

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertFalse("Near clipping plane != 0", 0 == chart.getView().getCamera().getNear());
    Assert.assertFalse("Far clipping plane != 0", 0 == chart.getView().getCamera().getFar());


    // --------------------------
    // When

    openAndWaitForInitializationCompletion(chart, GL_INIT_MAX_WAIT_SECONDS, TimeUnit.SECONDS);

    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart has its view is initialized after add(drawable) and to call open()",
        chart.getView().isInitialized());
    
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());



  }

  @Test
  public void givenNativeOnscreenChart_whenAddSimpleSurface_AfterOpen_thenBoundsAreCorrect() throws InterruptedException {

    // Given

    ChartFactory factory = new AWTChartFactory();

    // --------------------------
    // When

    Chart chart = factory.newChart();

    // --------------------------
    // Then

    Assert.assertFalse(
        "An onscreen chart has its view in non initialized state until open is called",
        chart.getView().isInitialized());

    // --------------------------
    // When

    openAndWaitForInitializationCompletion(chart, GL_INIT_MAX_WAIT_SECONDS, TimeUnit.SECONDS);


    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart has its view in initialized state once open has been called",
        chart.getView().isInitialized());
    
    Assert.assertEquals(new BoundingBox3d(-1, 1, -1, 1, -1, 1),
        chart.getView().getBounds());


    // --------------------------
    // When

    chart.add(SampleGeom.surface());

    // --------------------------
    // Then

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


    chart.render();

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());
  }


  @Test
  public void givenNativeOnscreenChart_whenAddVBOSurface_BeforeOpen_thenBoundsAreCorrect()
      throws InterruptedException {

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

    openAndWaitForInitializationCompletion(chart, GL_INIT_MAX_WAIT_SECONDS, TimeUnit.SECONDS);


    // --------------------------
    // Then

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

    Assert.assertTrue(
        "An onscreen chart will have its view initialized after calling open() AND waiting a bit for the GL context intialization",
        chart.getView().isInitialized());

  }
  
  @Test
  public void givenNativeOnscreenChart_whenAddVBOSurface_AfterOpen_thenBoundsAreCorrect() throws InterruptedException {

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

    openAndWaitForInitializationCompletion(chart, GL_INIT_MAX_WAIT_SECONDS, TimeUnit.SECONDS);

    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart won't have view initialized until the chart is open, even with a add(drawable)",
        chart.getView().isInitialized());

    // --------------------------
    // When

    chart.add(new DrawableVBO2(SampleGeom.surface()));

    // --------------------------
    // Then

    Assert.assertTrue(
        "An onscreen chart will have its view initialized after calling open() AND waiting a bit for the GL context intialization",
        chart.getView().isInitialized());

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT,
        chart.getView().getBoundsMode());

    Assert.assertEquals(new BoundingBox3d(-3, 3, -3, 3, -2.9970994f, 2.9970994f),
        chart.getView().getBounds());

  }
  
  
  /////////////////////////////////////////////////////////////////////////////////////
  //
  // UTILS
  //
  /////////////////////////////////////////////////////////////////////////////////////


  /** Open the chart and wait until it gets completely initialized, unless a timeout is reached */
  protected void openAndWaitForInitializationCompletion(Chart chart, long timeout, TimeUnit unit) throws InterruptedException {
    AtomicBoolean viewInitStatus = new AtomicBoolean(false);

    CountDownLatch latch = new CountDownLatch(1);

    TicToc t = new TicToc();

    /**
     * Here we will first state what should be measured and verified upon completion of chart.open(),
     * and then start measuring time and actually open the chart. This allow the test to not be
     * sensitive to the duration of VBO mount which may be slow and unpredictable among executing
     * computers.
     */
    
    chart.getView().addViewLifecycleChangedListener(new ViewLifecycleAdapter() {
      @Override
      public void viewHasInit(ViewLifecycleEvent e) {
        t.toc();
        viewInitStatus.set(true);
        latch.countDown(); // free
      }

    });


    t.tic();
    chart.open();

    // maximum wait time
    // latch.countDown() will free await() before max wait time
    latch.await(timeout, unit);

    if (!viewInitStatus.get()) {
      Assert.fail(
          "Did not complete opening chart and mounting VBO after " + t.elapsedMilisecond() + " ms");
    } else {
      System.out.println("TestViewModeAndCameraClippingPlanes - NEEDED " + t.elapsedMilisecond() + " ms to open with VBO mount");
    }
  }




}
