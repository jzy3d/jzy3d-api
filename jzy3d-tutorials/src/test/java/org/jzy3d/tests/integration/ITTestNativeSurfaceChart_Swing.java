package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ITTestNativeSurfaceChart_Swing {
 // @Test
  public void surfaceTest() {

    // When

    IChartFactory factory = new SwingChartFactory();

    factory.getPainterFactory().setOffscreen(700, 600);

    Chart chart = factory.newChart(Quality.Advanced());

    chart.add(surface());

    //chart.open();
    
    // Then
    ChartTester tester = new NativeChartTester();
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
    surface.setPolygonOffsetFillEnable(false); // VERY IMPORTANT FOR JGL TO WORK !!

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, 0.75f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
