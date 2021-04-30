package org.jzy3d.chart.controllers;

import java.awt.Component;
import java.awt.event.MouseEvent;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
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

/**
 * This verifies if the {@link AdaptiveMouseController} works without regression according to multiple cases
 * <ul>
 * <li>Repaint continuously or on demand
 * <li>HiDPI active or not (only test case of active HiDPI to ensure it is re-activated when mouse release)
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
    AWTCameraMouseController m = (AWTCameraMouseController)chart.addMouseCameraController();
    AWTCameraKeyController k = (AWTCameraKeyController)chart.addKeyboardCameraController();
    
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
    Chart chart = mockChartWithAdaptiveMouse(repaintContinuously, allowHiDPI, mockRenderingPerf);
    
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
    Assert.assertTrue("Just check test properly configured HiDPI", mouse.policy.optimizeByDroppingHiDPI);
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
    Chart chart = mockChartWithAdaptiveMouse(repaintContinuously, allowHiDPI, mockRenderingPerf);

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
    Assert.assertTrue("Just check test properly configured HiDPI", mouse.policy.optimizeByDroppingHiDPI);
    Assert.assertTrue("Did NOT start drag already", mouse.isFirstDrag);
    
    mouse.mouseDragged(mouseEvent(canvas, 100, 100));
    
    Assert.assertFalse("Did start drag already", mouse.isFirstDrag);
    Assert.assertFalse("GL properly configured", canvas.getGL().isAutoAdaptToHiDPI());

    
    // Then : HiDPI remains configured as before 
    mouse.mouseReleased(mouseEvent(canvas, 100, 100));

    Assert.assertFalse(mouse.mustOptimizeMouseDrag);
    Assert.assertEquals(allowHiDPI, canvas.getGL().isAutoAdaptToHiDPI());
  }

  // --------------------------------------------------------------------------------- //
  // --------------------------------------------------------------------------------- //
  // --------------------------------------------------------------------------------- //
  
  class MockRenderingTime {
    double value = 10;
  }

  /** Create a chart with an adaptive mouse that has a mock on canvas performance retrieval. */
  protected Chart mockChartWithAdaptiveMouse(boolean repaintContinuously, boolean allowHiDPI,
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
        policy.optimizeByDroppingFaceAndColoringWireframe = false;

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

    Shape surface = surface();

    Chart chart = factory.newChart(q);
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.add(surface);
    surface.setLegend(new AWTColorbarLegend(surface, chart));

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
