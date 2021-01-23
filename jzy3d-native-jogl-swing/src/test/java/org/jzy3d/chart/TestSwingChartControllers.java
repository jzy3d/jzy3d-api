package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;

public class TestSwingChartControllers {

  @Test
  public void givenSwingChart_whenAddMouseCameraController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new SwingChartFactory().newChart();
    chart.add(surface());
    CanvasSwing canvas = (CanvasSwing) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getMouseListeners().length);
    Assert.assertEquals(0, canvas.getMouseWheelListeners().length);
    Assert.assertEquals(0, canvas.getMouseMotionListeners().length);

    // When
    chart.addMouseCameraController();

    // Then
    Assert.assertEquals(1, canvas.getMouseListeners().length);
    Assert.assertEquals(1, canvas.getMouseWheelListeners().length);
    Assert.assertEquals(1, canvas.getMouseMotionListeners().length);

  }

  @Test
  public void givenSwingChart_whenAddMousePickingController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new SwingChartFactory().newChart();
    chart.add(surface());
    CanvasSwing canvas = (CanvasSwing) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getMouseListeners().length);
    Assert.assertEquals(0, canvas.getMouseWheelListeners().length);
    Assert.assertEquals(0, canvas.getMouseMotionListeners().length);

    // When
    chart.addMousePickingController(10);

    // Then
    Assert.assertEquals(1, canvas.getMouseListeners().length);
    Assert.assertEquals(1, canvas.getMouseWheelListeners().length);
  }

  @Test
  public void givenSwingChart_whenAddKeyboardCameraController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new SwingChartFactory().newChart();
    chart.add(surface());
    CanvasSwing canvas = (CanvasSwing) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getKeyListeners().length);

    // When
    chart.addKeyboardCameraController();

    // Then
    Assert.assertEquals(1, canvas.getKeyListeners().length);
  }

  @Test
  public void givenSwingChart_whenAddKeyboardScreenshotController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new SwingChartFactory().newChart();
    chart.add(surface());
    CanvasSwing canvas = (CanvasSwing) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getKeyListeners().length);

    // When
    chart.addKeyboardScreenshotController();

    // Then
    Assert.assertEquals(1, canvas.getKeyListeners().length);
  }



  private Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 80;

    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);
    return surface;
  }

}
