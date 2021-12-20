package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class ITTestNativeSurfaceChart {
  @Test
  public void surfaceTest() {

    // When
    GLCapabilities c = new GLCapabilities(GLProfile.get(GLProfile.GL2));
    c.setOnscreen(false);
    c.setDoubleBuffered(false);
    c.setAlphaBits(8);
    c.setRedBits(8);
    c.setBlueBits(8);
    c.setGreenBits(8);



    IPainterFactory p = new AWTPainterFactory(c);
    IChartFactory factory = new AWTChartFactory(p);

    // AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(700, 600);

    Chart chart = factory.newChart(Quality.Advanced());

    chart.add(surface());

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
        surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

  static final float ALPHA_FACTOR = 0.75f;


}
