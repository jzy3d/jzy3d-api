package org.jzy3d.emulgl.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ITTestEmulGLSurfaceChart {
  @Test
  public void whenSurfaceChart_ThenMatchBaselineImagePixelwise() {

    // When
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(Quality.Advanced());
    
    //System.out.println(chart.getQuality().isAlphaActivated());

    EmulGLCanvas c = (EmulGLCanvas) chart.getCanvas();
    c.setProfileDisplayMethod(false);
    c.getGL().setAutoAdaptToHiDPI(false);

    chart.add(surface());

    // Then
    ChartTester tester = new ChartTester();
    tester.assertSimilar(chart,
        ChartTester.EXPECTED_IMAGE_FOLDER + this.getClass().getSimpleName() + ".png");
  }



  private static Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    SurfaceBuilder builder = new SurfaceBuilder();
    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

  static final float ALPHA_FACTOR = 0.75f;


}
