package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;

public class TestAWTChartControllers {

  @Test
  public void givenAWTChart_whenAddMouseCameraController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new AWTChartFactory().newChart();
    chart.add(surface());
    CanvasAWT canvas = (CanvasAWT) chart.getCanvas();

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
  public void givenAWTChart_whenAddMousePickingController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new AWTChartFactory().newChart();
    chart.add(surface());
    CanvasAWT canvas = (CanvasAWT) chart.getCanvas();

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
  public void givenAWTChart_whenAddKeyboardCameraController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new AWTChartFactory().newChart();
    chart.add(surface());
    CanvasAWT canvas = (CanvasAWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getKeyListeners().length);

    // When
    chart.addKeyboardCameraController();

    // Then
    Assert.assertEquals(1, canvas.getKeyListeners().length);
  }

  @Test
  public void givenAWTChart_whenAddKeyboardScreenshotController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new AWTChartFactory().newChart();
    chart.add(surface());
    CanvasAWT canvas = (CanvasAWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getKeyListeners().length);

    // When
    chart.addKeyboardScreenshotController();

    // Then
    Assert.assertEquals(1, canvas.getKeyListeners().length);
  }



  private Shape surface() {
    // Define a function to plot
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);
    return surface;
  }

}
