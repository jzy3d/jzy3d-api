package org.jzy3d.tests.integration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;
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
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class ITTest {
  public static final String SEP_TITLE = " ";
  public static final String SEP_PROP = "_";
  public static final String SEP_CASE = "_";
  public static final String KV = "=";

  static Rectangle offscreenDimension = new Rectangle(800,600);
  
  public enum WT{
    EmulGL_AWT, Native_AWT
  }
  
  protected String indexFileName = "README_GENERATED.md";
  
  // ---------------------------------------------------------------------------------------------- //
  
  /** Generate markdown summary of test expectations. */
  @Test
  public void markdownAllTests() throws IOException {
    StringBuffer sb = new StringBuffer();
    line(sb, "jzy3d-test-java9-generated");
    line(sb, "==========================");
    line(sb, "This is a summary of existing baseline images for tests.");
    
    
    section(sb, "Surface");
    
    section(sb, "Scatter");
    
    section(sb, "Text", null, "Font=AppleChancery24");
    section(sb, "AxisLabelRotateLayout");
    
    section(sb, "Colorbar", "Shrink", null);
    section(sb, "Colorbar", "ShrinkBigFont", null);
    section(sb, "Colorbar", "IsModifiedByCustomFont", null);
    section(sb, "Colorbar", "HasMinimumWidth", null);
    
    
    BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(indexFileName)));
    bwr.write(sb.toString());
    bwr.flush();
    bwr.close();
  }

  public static void section(StringBuffer sb, String testName) {
    section(sb, testName, null, null);
  }
  
  public static void section(StringBuffer sb, String testName, String caseName, String info) {
    if(caseName!=null)
      line(sb, "# " + testName + " : " + caseName);
    else
      line(sb, "# " + testName);
      
    line(sb, "<table markdown=1>");

    line(sb, "<tr>");
    line(sb, "<td>"+ title(WT.EmulGL_AWT, HiDPI.ON) +"</td>");
    line(sb, "<td>"+ title(WT.EmulGL_AWT, HiDPI.OFF) +"</td>");
    line(sb, "<td>"+ title(WT.Native_AWT, HiDPI.OFF) +"</td>");
    line(sb, "</tr>");

    
    line(sb, "<tr>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.EmulGL_AWT, HiDPI.ON, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.EmulGL_AWT, HiDPI.OFF, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.Native_AWT, HiDPI.OFF, info))+ "</td>");
    line(sb, "</tr>");
    line(sb, "</table>");
    line(sb);
  }
  
  public static String imgTest(String name) {
    return "<img src=\"src/test/resources/" + name +".png\">";
  }

  public static String title(WT wt, HiDPI hidpi) {
    return wt + SEP_TITLE + "HiDPI:"+hidpi;
  }

  public static String title(String name, WT wt, HiDPI hidpi) {
    return name+ SEP_TITLE + wt + SEP_TITLE + "HiDPI:"+hidpi;
  }
  
  static void line(StringBuffer sb, String line) {
    sb.append(line + "\n");
  }

  static void line(StringBuffer sb) {
    sb.append("\n");
  }

  // ---------------------------------------------------------------------------------------------- //  
  public static void open(Chart c) {
    c.open(ITTest.offscreenDimension.width, ITTest.offscreenDimension.height);
    c.getMouse();
    c.getKeyboard();
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
  
  // ---------------------------------------------------------------------------------------------- //

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
      
      // intentionnaly add a few render to verify that the margin and offset
      // processing remain stable (I observed bugs where a margin may reduce
      // after each rendering, hence the colorbar moves)
      int nRefresh = 6;
      for (int i = 0; i < nRefresh; i++) {
        chart.render();
      }
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
  
  // ---------------------------------------------------------------------------------------------- //
  
  public static String name(ITTest test, WT wt, HiDPI hidpi) {
    return name(test, null, wt, hidpi, null);
  }

  public static String name(String test, WT wt, HiDPI hidpi) {
    return name(test, null, wt, hidpi, null);
  }

  public static String name(ITTest test, String caseName, WT wt, HiDPI hidpi) {
    return name(test, caseName, wt, hidpi, null);
  }
  
  public static String name(String test, String caseName, WT wt, HiDPI hidpi) {
    return name(test, caseName, wt, hidpi, null);
  }


  public static String name(ITTest test, WT wt, HiDPI hidpi, String info) {
    return name(test, null, wt, hidpi, info);
  }

  public static String name(String test, WT wt, HiDPI hidpi, String info) {
    return name(test, null, wt, hidpi, info);
  }

  public static String name(ITTest test, String caseName, WT wt, HiDPI hidpi, String info) {
    return name(className(test), caseName, wt, hidpi, info);
  }

  public static String name(String test, String caseName, WT wt, HiDPI hidpi, String info) {
    if(caseName!=null)
      return (test + SEP_CASE + caseName+ SEP_PROP + wt + SEP_PROP + hidpi(hidpi) + (info!=null?SEP_PROP+info:"")); 
    else
      return (test + SEP_PROP + wt + SEP_PROP + hidpi(hidpi) + (info!=null?SEP_PROP+info:"")); 
  }

  public static String hidpi(HiDPI hidpi) {
    return "HiDPI" + KV + hidpi;
  }
  
  public static String className(Object test) {
    return test.getClass().getSimpleName().replace("ITTest_", "");
  }

  // ---------------------------------------------------------------------------------------------- //
  
  public static void sleep(int mili) {
    try {
      Thread.sleep(mili);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  // ---------------------------------------------------------------------------------------------- //
  
  // CONTENT
  
  public static Shape surface() {
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);

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
