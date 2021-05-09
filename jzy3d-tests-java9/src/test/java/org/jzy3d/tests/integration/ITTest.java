package org.jzy3d.tests.integration;

import java.util.Random;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest.WT;

public class ITTest {
  static Rectangle offscreenDimension = new Rectangle(800,600);
  
  public enum WT{
    EmulGL_AWT, Native_AWT
  }

  public static Chart chart(WT windowingToolkit, HiDPI hidpi) {
    if(WT.EmulGL_AWT.equals(windowingToolkit)) {
      return chartEmulGL(hidpi);
    }
    else if(WT.Native_AWT.equals(windowingToolkit)) {
      return chartNative(hidpi);
    }
    else {
      throw new IllegalArgumentException("Unsupported toolkit : " + windowingToolkit);
    }
  }
  
  public static Chart chartEmulGL(HiDPI hidpi) {
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
    Quality q = quality(hidpi);
    Chart chart = factory.newChart(q);
    return chart;
  }

  public static Quality quality(HiDPI hidpi) {
    Quality q = Quality.Advanced(); 
    q.setHiDPIEnabled(HiDPI.ON.equals(hidpi)?true:false); // Enable HiDPI if available on computer
    q.setAnimated(false);
    return q;
  }
  
  public static Chart chartNative(HiDPI hidpi) {
    AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
    Quality q = quality(hidpi);
    Chart chart = factory.newChart(q);
    return chart;
  }

  @SuppressWarnings("rawtypes")
  public static void assertChart(Chart chart, Class c) {
    assertChart(chart, c.getSimpleName());
  }
  
  public static void assertChart(Chart chart, String name) {
    if(chart.getQuality().isHiDPIEnabled()) {
      IPainterFactory painter = chart.getFactory().getPainterFactory();
      chart.open(painter.getOffscreenDimension().width, painter.getOffscreenDimension().height);

      //sleep(300); // need to wait a little before hidpi occurs
      // or may chart.render() as well and ensure colorbar is reset
      chart.render();
    }
    
    // EMULGL
    if(chart.getFactory() instanceof EmulGLChartFactory) {
      ChartTester tester = new ChartTester();
      tester.assertSimilar(chart, ChartTester.EXPECTED_IMAGE_FOLDER + name + ".png");
    }
    // NATIVE
    else {
      NativeChartTester tester = new NativeChartTester();
      tester.assertSimilar(chart, ChartTester.EXPECTED_IMAGE_FOLDER + name + ".png");
    }
  }
  
  public static String name(Object o, WT wt, HiDPI hidpi) {
    return name(o, wt, hidpi, null);
  }
  public static String name(Object o, WT wt, HiDPI hidpi, String info) {
    return (o.getClass().getSimpleName() + ", " + wt + ", HiDPI=" + hidpi + (info!=null?", "+info:"")); 
  }

  
  public static void sleep(int mili) {
    try {
      Thread.sleep(mili);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public static Shape surface() {
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

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, 0.75f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }
  
  public static Scatter scatter(int size) {
    float x;
    float y;
    float z;
    float a;

    Coord3d[] points = new Coord3d[size];
    Color[] colors = new Color[size];

    Random r = new Random();
    r.setSeed(0);
    for (int i = 0; i < size; i++) {
      x = r.nextFloat() - 0.5f;
      y = r.nextFloat() - 0.5f;
      z = r.nextFloat() - 0.5f;
      points[i] = new Coord3d(x, y, z);
      a = 0.75f;
      colors[i] = new Color(x, y, z, a);
    }

    Scatter scatter = new Scatter(points, colors);
    scatter.setWidth(3);
    return scatter;
  }

}
