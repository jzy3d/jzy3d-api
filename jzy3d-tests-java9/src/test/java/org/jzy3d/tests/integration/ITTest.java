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
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.junit.NativePlatform;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest_2D.ITTest2DInstance;

public class ITTest {
  public static final String SEP_TITLE = " ";
  public static final String SEP_PROP = "_";
  public static final String SEP_CASE = "_";
  public static final String KV = "=";

  static NativePlatform platform = new NativePlatform();

  static Rectangle offscreenDimension = new Rectangle(800,600);
  
  // running offscreen may prevent to get a HiDPI image
  // hence not testing HiDPI/text layout
  static boolean runOffscreen = false;
  
  public enum WT{
    EmulGL_AWT, Native_AWT, Native_Swing
  }
  
  
  protected String indexFileName = "BASELINE_" + platform.getLabel() + ".md";
  
  protected WT[] toolkits = {WT.EmulGL_AWT, WT.Native_AWT, WT.Native_Swing};
  protected HiDPI[] resolutions = {HiDPI.ON, HiDPI.OFF};
  
  protected void applyToAllTookitAndResolutions(ITTestInstance task) {
    for(WT toolkit: toolkits) {
      for(HiDPI resolution: resolutions) {
        task.run(toolkit, resolution);
      }
    }
  }
  
  public static interface ITTestInstance{
    public void run(WT toolkit, HiDPI resolution);
  }
  
  
  // ---------------------------------------------------------------------------------------------- //
  
  /** Generate markdown summary of test expectations. */
  @Test
  public void markdownAllTests() throws IOException {
    StringBuffer sb = new StringBuffer();
    line(sb, "jzy3d-test-java9-generated");
    line(sb, "==========================");
    line(sb, "This is a summary of existing baseline images for tests, which was generated on ");
    line(sb, "* OS Name : " + platform.getOs().getName());
    line(sb, "* OS Version : " + platform.getOs().getVersion());
    line(sb, "* Java Version : " + platform.getOs().getJavaVersion());
    line(sb, "* CPU : " + platform.getOs().getArch());
    line(sb, "* GPU : " + platform.getGpuName());
    line(sb, "");
    
    // Content test
    section(sb, "Surface");
    section(sb, "Scatter");

    // Text and layout tests
    section(sb, "Text", null, "Font=AppleChancery24");
    section(sb, "Text", "whenDrawableTextRenderer", null);
    section(sb, "AxisLabelRotateLayout");
    
    // Colorbar tests
    section(sb, "Colorbar", "Shrink", null);
    section(sb, "Colorbar", "ShrinkBigFont", null);
    section(sb, "Colorbar", "IsModifiedByCustomFont", null);
    section(sb, "Colorbar", "HasMinimumWidth", null);
    
    // 2D tests
    line(sb, "# 2D Layout");
    
    ITTest_2D.forAll_2D_Parameters(new ITTest2DInstance() {
      @Override
      public void run(int margin, int tickLabel, boolean textVisible) {
        String properties = ITTest_2D.properties(margin, tickLabel, textVisible);
        section(1, sb, className(ITTest_2D.class), null, properties);
      }
    });
    
    

    
    BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(indexFileName)));
    bwr.write(sb.toString());
    bwr.flush();
    bwr.close();
  }

  public static void section(StringBuffer sb, String testName) {
    section(sb, testName, null, null);
  }

  public static void section(StringBuffer sb, String testName, String caseName, String info) {
    section(0, sb, testName, caseName, info);
  }
  
  public static void section(int level, StringBuffer sb, String testName, String caseName, String info) {
    // Write section name with markdown header style
    
    String header = "#".repeat(level+1);
    if(caseName!=null)
      line(sb, header + " " + testName + " : " + caseName);
    else
      line(sb, header + " " + testName);
    
    if(info!=null) {
      multiline(sb, info);
    }
    line(sb, "<table markdown=1>");

    line(sb, "<tr>");
    line(sb, "<td>"+ title(WT.EmulGL_AWT, HiDPI.ON) +"</td>");
    line(sb, "<td>"+ title(WT.EmulGL_AWT, HiDPI.OFF) +"</td>");
    line(sb, "<td>"+ title(WT.Native_AWT, HiDPI.ON) +"</td>");
    line(sb, "<td>"+ title(WT.Native_AWT, HiDPI.OFF) +"</td>");
    line(sb, "<td>"+ title(WT.Native_Swing, HiDPI.ON) +"</td>");
    line(sb, "<td>"+ title(WT.Native_Swing, HiDPI.OFF) +"</td>");
    line(sb, "</tr>");

    
    line(sb, "<tr>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.EmulGL_AWT, HiDPI.ON, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.EmulGL_AWT, HiDPI.OFF, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.Native_AWT, HiDPI.ON, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.Native_AWT, HiDPI.OFF, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.Native_Swing, HiDPI.ON, info))+ "</td>");
    line(sb, "<td>" + imgTest(name(testName, caseName, WT.Native_Swing, HiDPI.OFF, info))+ "</td>");
    line(sb, "</tr>");

    if(true) {
      line(sb, "<tr>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.EmulGL_AWT, HiDPI.ON, info))+ "</td>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.EmulGL_AWT, HiDPI.OFF, info))+ "</td>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.Native_AWT, HiDPI.ON, info))+ "</td>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.Native_AWT, HiDPI.OFF, info))+ "</td>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.Native_Swing, HiDPI.ON, info))+ "</td>");
      line(sb, "<td>" + imgDiff(name(testName, caseName, WT.Native_Swing, HiDPI.OFF, info))+ "</td>");
      line(sb, "</tr>");
    }
    
    line(sb, "</table>");
    line(sb);
  }
  
  public static String imgTest(String name) {
    return "<img src=\"src/test/resources/" + platform.getLabel() + "/" +name +".png\">";
  }

  public static String imgDiff(String name) {
    String path = "target/error-" + name + ChartTester.FILE_LABEL_DIFF + ".png";
    
    if(new File(path).exists()) {
      return "<img src=\""+path+"\">";      
    }
    else {
      
      return "";//"<img src=\""+OKFLAG_ICON+"\">";
    }
  }
  
  static String OKFLAG_ICON = "src/test/resources/icons/greentick.jpg";

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

  static void multiline(StringBuffer sb, String properties) {
    for(String property: properties.split(SEP_PROP)) {
      line(sb, "* " + property);
    }
  }

  
  // ---------------------------------------------------------------------------------------------- //  
  
  public static void open(Chart c) {
    c.open(ITTest.offscreenDimension.width, ITTest.offscreenDimension.height);
    c.getMouse();
    c.getKeyboard();
  }

  public static Chart chart(WT windowingToolkit, HiDPI hidpi) {
    return chart(windowingToolkit, hidpi, offscreenDimension);
  }
  
  public static Chart chart(WT windowingToolkit, HiDPI hidpi, Rectangle offscreenDimension) {
    System.out.println(" ITTest : " + windowingToolkit + " " + hidpi);
    
    if(WT.EmulGL_AWT.equals(windowingToolkit)) {
      return chart(new EmulGLChartFactory(), hidpi, offscreenDimension);
    }
    else if(WT.Native_AWT.equals(windowingToolkit)) {
      return chart(new AWTChartFactory(), hidpi, offscreenDimension);
    }
    else if(WT.Native_Swing.equals(windowingToolkit)) {
      return chart(new SwingChartFactory(), hidpi, offscreenDimension);
    }
    else {
      throw new IllegalArgumentException("Unsupported toolkit : " + windowingToolkit);
    }
  }
  
  public static Chart chart(ChartFactory factory, HiDPI hidpi, Rectangle offscreenDimension) {
    if(runOffscreen) {
      factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
      System.err.println(" ITTest will run offscreen, which may not enable HiDPI hence produce inaccurate layout with texts");

    }
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

  // ---------------------------------------------------------------------------------------------- //

  @SuppressWarnings("rawtypes")
  public static void assertChart(Chart chart, Class c) {
    assertChart(chart, c.getSimpleName());
  }
  
  public static void assertChart(Chart chart, String name) {
    IPainterFactory painter = chart.getFactory().getPainterFactory();

    Dimension dim ;
    
    if(painter.isOffscreen()) {
      dim = painter.getOffscreenDimension();
      //System.out.println(" ITTest.assertChart OFFSCREEN " + dim.width + "x"+ dim.height);
    }
    else {
      dim = new Dimension(offscreenDimension.width, offscreenDimension.height);
      //System.out.println(" ITTest.assertChart OPEN frame " + dim.width + "x"+ dim.height);
    }

    chart.open(name, dim.width, dim.height);

    
    // trigger a first render since the first generated may not consider the appropriate
    // pixel ratio
    chart.render();
    
    // intentionnaly add a few render to verify that the margin and offset
    // processing remain stable (I observed bugs where a margin may reduce
    // after each rendering, hence the colorbar moves)
    int nRefresh = 6;
    for (int i = 0; i < nRefresh; i++) {
      chart.render();
    }

    // Verify
    ChartTester tester = new ChartTester();
    
    if(!(chart.getFactory() instanceof EmulGLChartFactory)) {
      tester = new NativeChartTester();
    }
    
    tester.setTestCaseInputFolder("src/test/resources/" + new NativePlatform().getLabel() + "/");

    
    tester.assertSimilar(chart, tester.path(name));
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
    return className(test.getClass());//.getSimpleName().replace("ITTest_", "");
  }

  public static String className(Class<?> test) {
    return test.getSimpleName().replace("ITTest_", "");
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
