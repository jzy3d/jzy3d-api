package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.Component;
import java.awt.event.MouseEvent;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.EmulGLPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.lod.LODCandidates;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.FaceColor;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.WireColor;

/**
 * This verifies if the {@link AdaptiveMouseController} works without regression according to
 * multiple cases
 * <ul>
 * <li>Repaint continuously or on demand
 * <li>HiDPI active or not (only test case of active HiDPI to ensure it is re-activated when mouse
 * release)
 * <li>Rendering has good performance or not (mocked)
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class TestAdaptiveMouseController {
  /**
   * Ensure a mouse/keyboard rate limiter are set even if we have not defined an optimization policy
   */
  @Test
  public void whenNoPolicyDefined_ThenThereIsStillMouseRateLimiter() {
    // Given
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart();

    // When
    AWTCameraMouseController m = (AWTCameraMouseController) chart.addMouseCameraController();
    AWTCameraKeyController k = (AWTCameraKeyController) chart.addKeyboardCameraController();

    // Then
    Assert.assertNotNull(m.getRateLimiter());
    Assert.assertNotNull(k.getRateLimiter());
  }

  @Test
  public void whenRepaintOnDemand_onHiDPIChart_ThenOptimizationTriggersIfPerformanceIsBad() {
    // Given
    boolean repaintContinuously = false;
    boolean allowHiDPI = true;


    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart =
        mockChartWithAdaptiveMouse_AddSurface(repaintContinuously, allowHiDPI, mockRenderingPerf);

    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();

    // -------------------------------------
    // When : fast rendering
    mockRenderingPerf.value = 10;

    // Then : no optimization triggered
    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    Assert.assertFalse(mouse.mustOptimizeMouseDrag);

    mouse.mouseReleased(mouseEvent(canvas, 100, 100));


    // -------------------------------------
    // When : slow rendering
    mockRenderingPerf.value = 1000;

    // Then : optimization IS set to ON at MOUSE PRESS
    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    Assert.assertTrue(mouse.mustOptimizeMouseDrag);

    // Then : HiDPI is disabled DURING mouse DRAGGED
    Assert.assertTrue("Just check test properly configured HiDPI",
        mouse.policy.optimizeByDroppingHiDPI);
    Assert.assertTrue("Did NOT start drag already", mouse.isFirstDrag);

    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertFalse("Did start drag already", mouse.isFirstDrag);
    Assert.assertFalse("GL properly configured", canvas.getGL().isAutoAdaptToHiDPI());

    // Then : HiDPI is reset to intial state (true) AFTER mouse RELEASED
    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    Assert.assertEquals(false, chart.getQuality().isPreserveViewportSize());
    Assert.assertEquals(allowHiDPI, canvas.getGL().isAutoAdaptToHiDPI());

    Assert.assertFalse("Optim flag disabled after mouse release", mouse.mustOptimizeMouseDrag);

  }

  @Test
  public void whenRepaintContinuously_ThenOptimizationTriggers() {
    // Given
    boolean repaintContinuously = true; // THIS IS THE IMPORTANT SETTING
    boolean allowHiDPI = true;


    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart =
        mockChartWithAdaptiveMouse_AddSurface(repaintContinuously, allowHiDPI, mockRenderingPerf);

    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();

    // -------------------------------------
    // When : fast rendering
    mockRenderingPerf.value = 10;
    mouse.mousePressed(mouseEvent(canvas, 100, 100));

    // Then : no optimization triggered
    Assert.assertFalse(mouse.mustOptimizeMouseDrag);

    // -------------------------------------
    // When : slow rendering
    mockRenderingPerf.value = 1000;

    // Then : optimization IS set to ON at MOUSE PRESS
    mouse.mousePressed(mouseEvent(canvas, 100, 100));

    Assert.assertTrue(mouse.mustOptimizeMouseDrag);

    // Then : HiDPI is disabled DURING mouse DRAGGED
    Assert.assertTrue("Just check test properly configured HiDPI",
        mouse.policy.optimizeByDroppingHiDPI);
    Assert.assertTrue("Did NOT start drag already", mouse.isFirstDrag);

    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertFalse("Did start drag already", mouse.isFirstDrag);
    Assert.assertFalse("GL properly configured", canvas.getGL().isAutoAdaptToHiDPI());


    // Then : HiDPI remains configured as before
    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    Assert.assertFalse(mouse.mustOptimizeMouseDrag);
    Assert.assertEquals(allowHiDPI, canvas.getGL().isAutoAdaptToHiDPI());
  }


  @Test
  public void givenWireframeOff_whenOptimizeByDroppingFace_thenDrawableHaveWireframeHidden() {
    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart = mockChartWithAdaptiveMouse_AddSurface(false, true, mockRenderingPerf);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();


    // -------------------------------------
    // Given

    boolean WIREFRAME_STATUS_BEFORE_OPTIM = false;

    Shape surface = (Shape) chart.getScene().getGraph().getAll().get(0);
    surface.setWireframeDisplayed(WIREFRAME_STATUS_BEFORE_OPTIM);

    mouse.getPolicy().optimizeByDroppingFaceAndKeepingWireframe = true;


    // -------------------------------------
    // When : slow rendering on a mouse drag sequence

    mockRenderingPerf.value = 1000;

    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertTrue("Wireframe enabled during drag", surface.isWireframeDisplayed());
    Assert.assertFalse("Face hidden during drag", surface.isFaceDisplayed());


    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    // -------------------------------------
    // Then the wireframe status is back to original value

    Assert.assertEquals(WIREFRAME_STATUS_BEFORE_OPTIM, surface.isWireframeDisplayed());

  }

  @Test
  public void givenWireframeOn_whenOptimizeByDroppingFace_thenDrawableHaveWireframeDisplayed() {
    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart = mockChartWithAdaptiveMouse_AddSurface(false, true, mockRenderingPerf);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();


    // -------------------------------------
    // Given

    boolean WIREFRAME_STATUS_BEFORE_OPTIM = true;

    Shape surface = (Shape) chart.getScene().getGraph().getAll().get(0);
    surface.setWireframeDisplayed(WIREFRAME_STATUS_BEFORE_OPTIM);

    mouse.getPolicy().optimizeByDroppingFaceAndKeepingWireframe = true;


    // -------------------------------------
    // When : slow rendering on a mouse drag sequence

    mockRenderingPerf.value = 1000;

    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertTrue("Wireframe enabled during drag", surface.isWireframeDisplayed());
    Assert.assertFalse("Face hidden during drag", surface.isFaceDisplayed());


    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    // -------------------------------------
    // Then the wireframe status is back to original value

    Assert.assertEquals(WIREFRAME_STATUS_BEFORE_OPTIM, surface.isWireframeDisplayed());
  }

  @Test
  public void givenDrawable_whenOptimizeByDrawingBoundsOnly_thenDrawableHasFaceAndWireframeHidden() {
    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart = mockChartWithAdaptiveMouse_AddSurface(false, true, mockRenderingPerf);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();


    // -------------------------------------
    // Given

    boolean WIREFRAME_STATUS_BEFORE_OPTIM = true;

    Shape surface = (Shape) chart.getScene().getGraph().getAll().get(0);
    surface.setWireframeDisplayed(WIREFRAME_STATUS_BEFORE_OPTIM);
    surface.setFaceDisplayed(WIREFRAME_STATUS_BEFORE_OPTIM);

    mouse.getPolicy().optimizeByDrawingBoundingBoxOnly = true;


    // -------------------------------------
    // When : slow rendering on a mouse drag sequence

    mockRenderingPerf.value = 1000;

    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertFalse("Wireframe hidden during drag", surface.isWireframeDisplayed());
    Assert.assertFalse("Face hidden during drag", surface.isFaceDisplayed());
    Assert.assertTrue("Bounds displayed during drag", surface.isBoundingBoxDisplayed());


    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    // -------------------------------------
    // Then the wireframe status is back to original value

    Assert.assertEquals(WIREFRAME_STATUS_BEFORE_OPTIM, surface.isWireframeDisplayed());
    Assert.assertEquals(WIREFRAME_STATUS_BEFORE_OPTIM, surface.isFaceDisplayed());
    Assert.assertFalse("Bounds are not displayed any more", surface.isBoundingBoxDisplayed());
  }


  @Test
  public void givenSmoothColoring_whenOptimizeByDroppingSmoothColoring_thenChartQualityIsReconfiguredToFlatColoring() {
    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms
    Chart chart = mockChartWithAdaptiveMouse_AddSurface(false, true, mockRenderingPerf);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();


    Assert.assertTrue(chart.getQuality().isSmoothColor());


    // -------------------------------------
    // Given

    // Shape surface = (Shape) chart.getScene().getGraph().getAll().get(0);

    mouse.getPolicy().optimizeByDroppingSmoothColor = true;


    // -------------------------------------
    // When : slow rendering on a mouse drag sequence

    mockRenderingPerf.value = 1000;

    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    Assert.assertFalse("Chart quality is now configured for flat coloring",
        chart.getQuality().isSmoothColor());

    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    // -------------------------------------
    // Then the wireframe status is back to original value

    Assert.assertTrue("Chart quality is now configured for smooth coloring",
        chart.getQuality().isSmoothColor());
  }

  @Test
  public void whenOptimizeAdaptiveLOD_thenChartQualityIsReconfiguredToALearnedLOD() {
    MockRenderingTime mockRenderingPerf = new MockRenderingTime();// ms

    // -------------------------------------
    // Given - Progressive addition of surface with only two LOD
    
    LODCandidates c = new LODCandidates(
        new LODSetting("0", FaceColor.ON, WireColor.OFF), 
        LODCandidates.BOUNDS_ONLY);

    Chart chart = mockChartWithAdaptiveMouse_AddProgressiveSurface(c, false, true, mockRenderingPerf);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    AdaptiveMouseController mouse = skin.getMouse();
    EmulGLCanvas canvas = skin.getCanvas();

    // And with a force score of 1ms for rendering with bounds only, 100ms for everything
    mouse.getLODPerf().setScore(c.getRank().get(1), 001);
    mouse.getLODPerf().setScore(c.getRank().get(0), 100);
    
    // And with a configuration for enabling dynamic LOD when lag larger than 40ms
    mouse.getPolicy().optimizeForRenderingTimeLargerThan = 40;//ms
    mouse.getPolicy().optimizeByPerformanceKnowledge = true;

    Shape surface = (Shape) chart.getScene().getGraph().getAll().get(0);
    
    Assert.assertFalse("Bounds NOT displayed before optimizing", surface.isBoundingBoxDisplayed());
    
    // -------------------------------------
    // When : slow rendering on a mouse drag sequence

    mockRenderingPerf.value = 1000;

    mouse.mousePressed(mouseEvent(canvas, 100, 100));
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));

    // Then select a configuration that is below max accepted rendering time

    LODPerf perf = mouse.adaptByPerformanceKnowledge.getPerf();
    LODSetting lodSetting = mouse.adaptByPerformanceKnowledge.getSelectedLODSetting();
    Assert.assertNotNull(lodSetting);
    System.out.println("LOD:" + lodSetting.getName());

    double settingScore = perf.getScore(lodSetting);
    Assert.assertTrue(settingScore < mouse.getPolicy().optimizeForRenderingTimeLargerThan);
    Assert.assertTrue("Bounds displayed while optimizing", surface.isBoundingBoxDisplayed());
    Assert.assertFalse("Faces NOT displayed while optimizing", surface.isFaceDisplayed());
    //Assert.assertFalse("Wires NOT displayed while optimizing", surface.isWireframeDisplayed());
    
    
    
    // -------------------------------------
    // When releasing mouse, then surface is configured back to original settings
    
    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    
    // Then the wireframe status is back to original value

    Assert.assertFalse("Bounds NOT displayed after optimizing", surface.isBoundingBoxDisplayed());
    Assert.assertTrue("Faces displayed while optimizing", surface.isFaceDisplayed());
    //Assert.assertTrue("Wires displayed while optimizing", surface.isWireframeDisplayed());

  }

  // --------------------------------------------------------------------------------- //
  // --------------------------------------------------------------------------------- //
  // --------------------------------------------------------------------------------- //

  class MockRenderingTime {
    double value = 10;
  }

  protected Chart mockChartWithAdaptiveMouse_AddSurface(boolean repaintContinuously,
      boolean allowHiDPI, MockRenderingTime mockRenderingPerf) {
    Chart chart =
        mockChartWithAdaptiveMouse_Empty(repaintContinuously, allowHiDPI, mockRenderingPerf);

    Shape surface = surface();
    chart.add(surface);
    surface.setLegend(new AWTColorbarLegend(surface, chart));

    return chart;
  }

  protected Chart mockChartWithAdaptiveMouse_AddProgressiveSurface(LODCandidates candidates,
      boolean repaintContinuously, boolean allowHiDPI, MockRenderingTime mockRenderingPerf) {
    Chart chart =
        mockChartWithAdaptiveMouse_Empty(repaintContinuously, allowHiDPI, mockRenderingPerf);

    Shape surface = surface();
    chart.add(surface, candidates);
    surface.setLegend(new AWTColorbarLegend(surface, chart));

    return chart;
  }

  /** Create a chart with an adaptive mouse that has a mock on canvas performance retrieval. */
  protected Chart mockChartWithAdaptiveMouse_Empty(boolean repaintContinuously, boolean allowHiDPI,
      MockRenderingTime mockRenderingPerf) {

    // --------------------------------------------------------
    // Configure quality optimization when slow rendering

    EmulGLPainterFactory painter = new EmulGLPainterFactory() {

      @Override
      public AdaptiveMouseController newMouseCameraController(Chart chart) {

        // THIS IS THE OBJECT UNDER TEST!!
        AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
        policy.renderingRateLimiter =
            new RateLimiterAdaptsToRenderTime((EmulGLCanvas) chart.getCanvas()) {

              // THIS IS EQUIVALENT TO PARTIAL MOCKING THE RENDERING OPTIMIZER
              protected double getLastRenderingTimeFromCanvas() {
                return mockRenderingPerf.value;
              }
            };
        policy.optimizeForRenderingTimeLargerThan = 100;// ms
        policy.optimizeByDroppingHiDPI = true;
        policy.optimizeByDroppingWireframeOnly = false;
        policy.optimizeByDroppingFaceAndKeepingWireframeWithColor = false;

        return new AdaptiveMouseController(chart, policy) {

          // THIS IS EQUIVALENT TO PARTIAL MOCKING THE MOUSE CONTROLLER
          protected double getLastRenderingTimeFromCanvas() {
            return mockRenderingPerf.value;
          }
        };
      }
    };

    // --------------------------------------------------------
    // Configure base quality for standard case

    EmulGLChartFactory factory = new EmulGLChartFactory(painter);
    Quality q = Quality.Advanced();
    q.setAnimated(repaintContinuously);
    q.setPreserveViewportSize(!allowHiDPI); // prevent HiDPI/Retina to apply hence reduce the number
                                            // of pixel to process

    // --------------------------------------------------------
    // Configure chart content


    Chart chart = factory.newChart(q);
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);


    // --------------------------------------------------------
    // Enable visible profiling

    EmulGLCanvas c = (EmulGLCanvas) chart.getCanvas();
    c.setProfileDisplayMethod(true);

    // --------------------------------------------------------
    // Open and enable controllers

    chart.open(1264, 812); // need to open chart to have a Graphics2D instance
    return chart;
  }

  protected static MouseEvent mouseEvent(Component sourceCanvas, int x, int y) {
    return new MouseEvent(sourceCanvas, 0, 0, 0, x, y, 100, 100, 1, false, 0);
  }


  protected static Shape surface() {
    SurfaceBuilder builder = new SurfaceBuilder();

    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps), mapper);

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
