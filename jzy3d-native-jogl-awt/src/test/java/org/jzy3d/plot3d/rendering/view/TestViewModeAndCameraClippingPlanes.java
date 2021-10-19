package org.jzy3d.plot3d.rendering.view;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestViewModeAndCameraClippingPlanes {
  @Test
  public void givenNativeOffscreenChart_whenAddSimpleSurface_thenBoundsAreCorrect() {

    // Given

    ChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);

    Chart chart = factory.newChart();

    // --------------------------
    // When

    chart.add(surface());

    // --------------------------
    // Then
    

    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
    Assert.assertEquals("Bounds are as expected", new BoundingBox3d(-3,3,-3,3,-2.9970994f,2.9970994f), chart.getView().getBounds());
    Assert.assertFalse("Near clipping plane != 0", 0 == chart.getView().getCamera().getNear());
    Assert.assertFalse("Far clipping plane != 0", 0 == chart.getView().getCamera().getFar());
  }
  
  @Ignore
  @Test
  public void givenNativeOffscreenChart_whenAddVBOSurface_BeforeOpen_thenBoundsAreCorrect() {

    // Given

    ChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);

    Chart chart = factory.newChart();

    // --------------------------
    // When
    
    chart.add(new DrawableVBO2(surface()));

    // --------------------------
    // Then
    

    Assert.assertEquals(new BoundingBox3d(-3,3,-3,3,-2.9970994f,2.9970994f), chart.getView().getBounds());
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
  }

  @Ignore
  @Test
  public void givenNativeOnscreenChart_whenAddVBOSurface_BeforeOpen_thenBoundsAreCorrect() {

    // Given
    ChartFactory factory = new AWTChartFactory();
    Chart chart = factory.newChart();

    // --------------------------
    // When
    
    chart.add(new DrawableVBO2(surface()));
    
    chart.open();

    // --------------------------
    // Then
    

    Assert.assertEquals(new BoundingBox3d(-3,3,-3,3,-2.9970994f,2.9970994f), chart.getView().getBounds());
    Assert.assertEquals("BoundMode is auto fit", ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
  }


  protected Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));
    surface.setColorMapper(colorMapper);
    return surface;
  }
}
