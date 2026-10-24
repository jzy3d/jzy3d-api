package org.jzy3d.tests.integration;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IFrame;
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

public class ITTest {
  public static final String SEP_TITLE = " ";
  public static final String SEP_PROP = "_";
  public static final String SEP_CASE = "_";
  public static final String KV = "=";

  static NativePlatform platform = new NativePlatform();

  /**
   * Folder holding a sub folder of baseline images per platform. Baselines missing for the current
   * platform are built in it at first run. CI builds them in an empty folder of <code>target</code>
   * so that PanamaGL is compared to JOGL on the same runner, as no baseline exists for CI runners.
   */
  public static final String BASELINES =
      System.getProperty("jzy3d.ittest.baselines", "src/test/resources/");

  /**
   * Maximum ratio of pixels of a PanamaGL chart that may differ from the JOGL baseline, a pixel
   * differing if one of its channel differ by more than
   * {@link ITTest_PanamaGLParity#PIXEL_TOLERANCE}. Raised from 1% to 2% for the CI renderers,
   * where text makes 1% to 1.6% of pixels differ (see README).
   */
  public static final double PANAMAGL_MAX_DIFF_RATIO = 0.02;

  protected String indexFileName = "BASELINE_" + platform.getLabel() + ".md";

  static Rectangle offscreenDimension = new Rectangle(800, 600);

  // running offscreen may prevent to get a HiDPI image
  // hence not testing HiDPI/text layout
  static boolean runOffscreen = false;

  public enum WT {
    EmulGL_AWT, JOGL_AWT, JOGL_Swing, PanamaGL_Swing
  }

  /**
   * PanamaGL requires Java 22+ : its chart factory is loaded by name, and PanamaGL toolkits are only
   * tested if available in classpath (see the <code>panamagl</code> profile of this module).
   */
  public static final String PANAMAGL_SWING_FACTORY =
      "org.jzy3d.chart.factories.PanamaGLSwingChartFactory";

  public static boolean isPanamaGLAvailable() {
    try {
      Class.forName(PANAMAGL_SWING_FACTORY);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  protected static WT[] withPanamaGL(WT... toolkits) {
    if (!isPanamaGLAvailable()) {
      return toolkits;
    }
    WT[] all = java.util.Arrays.copyOf(toolkits, toolkits.length + 1);
    all[toolkits.length] = WT.PanamaGL_Swing;
    return all;
  }

  // ----------------------------------------------------------------------------------------------
  // //
  // Toolkit and resolutions to apply to all tests using the forEach operator

  protected WT[] toolkits = withPanamaGL(WT.JOGL_AWT, WT.JOGL_Swing, WT.EmulGL_AWT);

  /** Toolkits of tests that do not compare PanamaGL to the JOGL baselines (see ITTest_Scatter) */
  protected WT[] toolkitsWithoutPanamaGL = {WT.JOGL_AWT, WT.JOGL_Swing, WT.EmulGL_AWT};

  protected WT[] toolkitsAWT = withPanamaGL(WT.JOGL_AWT, WT.EmulGL_AWT);

  /**
   * HiDPI modes of tests whose rendering depends on the pixel scale (text size, margins, layout
   * computed in pixels). These tests keep both modes.
   */
  public static final HiDPI[] HIDPI_ON_OFF = {HiDPI.ON, HiDPI.OFF};

  /**
   * HiDPI mode of tests whose rendering does not depend on the pixel scale (surface, scatter, axis
   * flip). Testing <code>HiDPI.OFF</code> verifies nothing more : on a HiDPI display JOGL ignores it
   * and renders the same image, and on other displays all toolkits render the same image in both
   * modes. The baseline names of these tests have no HiDPI mode.
   */
  public static final HiDPI[] HIDPI_ON = {HiDPI.ON};

  protected HiDPI[] resolutions = HIDPI_ON_OFF;

  /**
   * Run a test for each possible registered toolkit and resolution.
   */
  protected void forEach(ITTestInstance task) {
    forEach(task, toolkits);
  }

  protected void forEach(ITTestInstance task, WT[] toolkits) {
    forEach(task, toolkits, resolutions);
  }

  /**
   * Run a test for each toolkit and resolution. The windows opened by a case are closed once it is
   * done, otherwise they pile up and slow down the following cases.
   */
  protected void forEach(ITTestInstance task, WT[] toolkits, HiDPI[] resolutions) {
    for (WT toolkit : toolkits) {
      for (HiDPI resolution : resolutions) {
        try {
          task.run(toolkit, resolution);
        } finally {
          closeCharts();
        }
      }
    }
  }

  // ----------------------------------------------------------------------------------------------
  // //
  // Close the windows opened by the tests

  /** Frame of each chart opened by {@link #assertChart(Chart, String, double)} and not closed yet */
  protected static final Map<Chart, IFrame> openedCharts = new IdentityHashMap<>();

  /** Close the windows left open by a test, including the ones of charts it disposed itself. */
  @After
  public void closeWindows() {
    closeCharts();
    for (Window window : Window.getWindows()) {
      if (window.isDisplayable()) {
        onEDT(window::dispose);
      }
    }
  }

  /** Dispose the charts opened by {@link #assertChart(Chart, String, double)} and close their frame. */
  public static void closeCharts() {
    for (Chart chart : new ArrayList<>(openedCharts.keySet())) {
      close(chart);
    }
  }

  /**
   * Dispose a chart and close its frame. Disposing a chart only releases its OpenGL resources and
   * leaves its window open.
   */
  public static void close(Chart chart) {
    IFrame frame = openedCharts.remove(chart);
    // Close the window first, otherwise it may repaint the disposed chart
    if (frame instanceof Window) {
      onEDT(((Window) frame)::dispose);
    }
    chart.dispose();
  }

  protected static void onEDT(Runnable r) {
    if (EventQueue.isDispatchThread()) {
      r.run();
      return;
    }
    try {
      EventQueue.invokeAndWait(r);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  
  public static interface ITTestInstance {
    public void run(WT toolkit, HiDPI resolution);
  }


  // ----------------------------------------------------------------------------------------------
  // //

  /**
   * Generate markdown summary of test expectations.
   */
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
    section(0, sb, "Surface", null, null, toolkits, HIDPI_ON);
    section(0, sb, "Scatter", null, null, toolkitsWithoutPanamaGL, HIDPI_ON);

    // Text and layout tests
    section(sb, "Text", null, "Font=AppleChancery24");
    section(sb, "Text", "whenDrawableTextRenderer", null);
    section(sb, "AxisLabelRotateLayout");

    // Colorbar tests
    //section(sb, "Colorbar", "Shrink", null);
    //section(sb, "Colorbar", "ShrinkBigFont", null);
    section(sb, "Colorbar", "IsModifiedByCustomFont", null);
    //section(sb, "Colorbar", "HasMinimumWidth", null);

    //section(sb, "2D_Colorbar", "View"+KV+"XY", null);
    //section(sb, "2D_Colorbar", "View"+KV+"XZ", null);
    //section(sb, "2D_Colorbar", "View"+KV+"YZ", null);

    section(0, sb, "2D_FlipAxis", "View"+KV+"XY"+SEP_PROP+"Flip"+KV+"None", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XY"+SEP_PROP+"Flip"+KV+"X", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XY"+SEP_PROP+"Flip"+KV+"Y", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XY"+SEP_PROP+"Flip"+KV+"Both", null, toolkitsAWT, HIDPI_ON);

    section(0, sb, "2D_FlipAxis", "View"+KV+"YZ"+SEP_PROP+"Flip"+KV+"None", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"YZ"+SEP_PROP+"Flip"+KV+"X", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"YZ"+SEP_PROP+"Flip"+KV+"Y", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"YZ"+SEP_PROP+"Flip"+KV+"Both", null, toolkitsAWT, HIDPI_ON);

    section(0, sb, "2D_FlipAxis", "View"+KV+"XZ"+SEP_PROP+"Flip"+KV+"None", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XZ"+SEP_PROP+"Flip"+KV+"X", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XZ"+SEP_PROP+"Flip"+KV+"Y", null, toolkitsAWT, HIDPI_ON);
    section(0, sb, "2D_FlipAxis", "View"+KV+"XZ"+SEP_PROP+"Flip"+KV+"Both", null, toolkitsAWT, HIDPI_ON);

    
    // 2D tests
    line(sb, "# 2D Layout");

    // 2D tests sub sections of the Margin caes
    ITTest_2D.forEach((margin, tickLabel, axisLabel, textVisible) -> {
      String properties = ITTest_2D.properties(margin, tickLabel, axisLabel, textVisible);
      section(1, sb, className(ITTest_2D.class), ITTest_2D.caseMargin, properties);
    });

    // 2D tests sub sections of the axis orientation cases
    ITTest_2D.forEach((yOrientation) -> {
      String properties = ITTest_2D.properties(yOrientation);
      section(1, sb, className(ITTest_2D.class), ITTest_2D.caseAxisRotated, properties);
    });
    
    // OVerlay tests
    line(sb, "# Overlays");
    
    ITTest_Overlay.forEach((cornerSet)->{
      String properties = ITTest_Overlay.properties(cornerSet);      
      section(1, sb, className(ITTest_Overlay.class), null, properties);

    });

    // Export in a markdown file
    BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(indexFileName)));
    bwr.write(sb.toString());
    bwr.flush();
    bwr.close();
  }

  public void section(StringBuffer sb, String testName) {
    section(sb, testName, null, null);
  }

  public void section(StringBuffer sb, String testName, String caseName, String info) {
    section(0, sb, testName, caseName, info);
  }

  public void section(int level, StringBuffer sb, String testName, String caseName, String info) {
    section(level, sb, testName, caseName, info, toolkits, resolutions);
  }

  /**
   * Write a table with a column per toolkit and a line of images per HiDPI mode, each followed by a
   * line of diff images if a test of this line failed. Sections of tests having a single HiDPI mode
   * show baselines named without HiDPI mode.
   */
  public void section(int level, StringBuffer sb, String testName, String caseName, String info,
      WT[] toolkits, HiDPI[] resolutions) {

    // -------------------------------
    // Write section name with markdown header style

    String header = Collections.nCopies(level + 1, "#").stream().collect(Collectors.joining());
    if (caseName != null)
      line(sb, header + " " + testName + " : " + caseName);
    else
      line(sb, header + " " + testName);

    // -------------------------------
    // May show parameters as bullet point list

    if (info != null) {
      multiline(sb, info);
    }

    // -------------------------------
    // Initiate baseline table

    line(sb, "<table markdown=1>");

    // -------------------------------
    // Table header with toolkits, after the column of HiDPI modes

    line(sb, "<tr>");
    line(sb, "<td></td>");
    for (WT toolkit : toolkits) {
      line(sb, "<td>" + toolkit + "</td>");
    }
    line(sb, "</tr>");

    for (HiDPI resolution : resolutions) {
      HiDPI hidpiInName = resolutions.length > 1 ? resolution : null;

      // -------------------------------
      // A line with the image of each toolkit for this HiDPI mode

      line(sb, "<tr>");
      line(sb, "<td>HiDPI:" + resolution + "</td>");
      for (WT toolkit : toolkits) {
        line(sb, "<td>" + imgTest(name(testName, caseName, toolkit, hidpiInName, info)) + "</td>");
      }
      line(sb, "</tr>");

      // -------------------------------
      // A line with diff images if a test of this HiDPI mode failed

      StringBuffer diffs = new StringBuffer();
      boolean hasDiff = false;
      for (WT toolkit : toolkits) {
        String diff = imgDiff(name(testName, caseName, toolkit, hidpiInName, info));
        hasDiff |= !diff.isEmpty();
        line(diffs, "<td>" + diff + "</td>");
      }
      if (hasDiff) {
        line(sb, "<tr>");
        line(sb, "<td></td>");
        sb.append(diffs);
        line(sb, "</tr>");
      }
    }

    // -------------------------------
    // End baseline table

    line(sb, "</table>");
    line(sb);
  }

  /**
   * PanamaGL charts have no baseline : the index shows the image they rendered, exported by
   * {@link #assertSimilarToJOGL(Chart, ChartTester, String, double)} at the last run.
   */
  public String imgTest(String name) {
    if (isToolkit(name, WT.PanamaGL_Swing)) {
      return "<img src=\"" + PANAMAGL_EXPORT + name + ".png\">";
    }
    return "<img src=\"src/test/resources/" + platform.getLabel() + "/" + name + ".png\">";
  }

  /** Folder where the images rendered by PanamaGL charts are exported. */
  public static final String PANAMAGL_EXPORT = ChartTester.MAVEN_TARGET_PATH + "panamagl/";

  /** True if a test case name is the one of the given toolkit. */
  public static boolean isToolkit(String name, WT toolkit) {
    return name.matches(".*" + SEP_PROP + toolkit + "(" + SEP_PROP + ".*)?");
  }

  /** The name of the same test case for another toolkit. */
  public static String replaceToolkit(String name, WT from, WT to) {
    return name.replaceFirst(SEP_PROP + from + "(" + SEP_PROP + "|$)", SEP_PROP + to + "$1");
  }

  public String imgDiff(String name) {
    String pathDiff = "target/error-" + name + ChartTester.FILE_LABEL_DIFF + ".png";
    String pathErr = "target/error-" + name + ChartTester.FILE_LABEL_ACTUAL + ".png";
    String pathZoom = "target/error-" + name + ChartTester.FILE_LABEL_ZOOM + ".png";

    boolean exists = new File(pathDiff).exists();

    if (exists) {
      return "Diff chart:<br><img src=\"" + pathDiff + "\">" + "<br>Actual chart:<br>"
          + "<img src=\"" + pathErr + "\">" + "<br>Zoom on error:<br>" + "<img src=\"" + pathZoom
          + "\"><br>Following tests of the same section have been skipped.";
    } else {
      return "";
    }
  }

  static String OKFLAG_ICON = "src/test/resources/icons/greentick.jpg";

  public String title(WT wt, HiDPI hidpi) {
    return wt + SEP_TITLE + "HiDPI:" + hidpi;
  }

  public String title(String name, WT wt, HiDPI hidpi) {
    return name + SEP_TITLE + wt + SEP_TITLE + "HiDPI:" + hidpi;
  }

  public void line(StringBuffer sb, String line) {
    sb.append(line + "\n");
  }

  public void line(StringBuffer sb) {
    sb.append("\n");
  }

  public void multiline(StringBuffer sb, String properties) {
    for (String property : properties.split(SEP_PROP)) {
      line(sb, "* " + property);
    }
  }


  // ----------------------------------------------------------------------------------------------
  // //

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

    if (WT.EmulGL_AWT.equals(windowingToolkit)) {
      return chart(new EmulGLChartFactory(), hidpi, offscreenDimension);
    } else if (WT.JOGL_AWT.equals(windowingToolkit)) {
      return chart(new AWTChartFactory(), hidpi, offscreenDimension);
    } else if (WT.JOGL_Swing.equals(windowingToolkit)) {
      return chart(new SwingChartFactory(), hidpi, offscreenDimension);
    } else if (WT.PanamaGL_Swing.equals(windowingToolkit)) {
      return chart(newChartFactory(PANAMAGL_SWING_FACTORY), hidpi, offscreenDimension);
    } else {
      throw new IllegalArgumentException("Unsupported toolkit : " + windowingToolkit);
    }
  }

  protected static ChartFactory newChartFactory(String className) {
    try {
      return (ChartFactory) Class.forName(className).getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Can not create " + className, e);
    }
  }

  public static Chart chart(ChartFactory factory, HiDPI hidpi, Rectangle offscreenDimension) {
    if (runOffscreen) {
      factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
      System.err.println(
          " ITTest will run offscreen, which may not enable HiDPI hence produce inaccurate layout with texts");

    }
    Quality q = quality(hidpi);
    Chart chart = factory.newChart(q);
    return chart;
  }

  public static Quality quality(HiDPI hidpi) {
    Quality q = Quality.Advanced();
    q.setHiDPI(hidpi);
    q.setAnimated(false);
    return q;
  }

  // ----------------------------------------------------------------------------------------------
  // //

  @SuppressWarnings("rawtypes")
  public static void assertChart(Chart chart, Class c) {
    assertChart(chart, c.getSimpleName());
  }

  public static void assertChart(Chart chart, String name) {
    assertChart(chart, name, PANAMAGL_MAX_DIFF_RATIO);
  }

  /**
   * Verify a chart matches its baseline image. PanamaGL charts are compared to the JOGL baseline of
   * the same case and may differ on the given ratio of pixels.
   */
  public static void assertChart(Chart chart, String name, double panamaglMaxDiffRatio) {
    IPainterFactory painter = chart.getFactory().getPainterFactory();

    Dimension dim;

    if (painter.isOffscreen()) {
      dim = painter.getOffscreenDimension();
      // System.out.println(" ITTest.assertChart OFFSCREEN " + dim.width + "x"+ dim.height);
    } else {
      dim = new Dimension(offscreenDimension.width, offscreenDimension.height);
      // System.out.println(" ITTest.assertChart OPEN frame " + dim.width + "x"+ dim.height);
    }

    IFrame frame = chart.open(name, dim.width, dim.height);
    if (frame != null) {
      openedCharts.put(chart, frame);
    }


    // trigger a first render since the first generated may not consider the appropriate
    // pixel ratio
    chart.render();

    // intentionnaly add a few render to verify that the margin and offset
    // processing remain stable (I observed bugs where a margin may reduce
    // after each rendering, hence the colorbar moves)
    int nRefresh = 6;
    chart.render(nRefresh);

    // Verify
    ChartTester tester = new ChartTester();

    // JOGL charts are read with the JOGL renderer, other charts give a BufferedImage screenshot
    if (!(chart.getFactory() instanceof EmulGLChartFactory) && !isPanamaGL(chart)) {
      tester = new NativeChartTester();
    }

    tester.setTestCaseInputFolder(BASELINES + platform.getLabel() + "/");

    if (isPanamaGL(chart)) {
      assertSimilarToJOGL(chart, tester, name, panamaglMaxDiffRatio);
    } else {
      tester.assertSimilar(chart, tester.path(name));
    }
  }

  /**
   * PanamaGL charts have no baseline : they are compared to the JOGL baseline of the same test
   * case, as both render with the native OpenGL library. Text and points are rasterized slightly
   * differently, hence a tolerance. The rendered image is exported in {@link #PANAMAGL_EXPORT} to be
   * shown by the baseline index.
   */
  protected static void assertSimilarToJOGL(Chart chart, ChartTester tester, String name,
      double maxDiffRatio) {
    File baseline = null;
    for (WT jogl : new WT[] {WT.JOGL_Swing, WT.JOGL_AWT}) {
      File file = new File(tester.path(replaceToolkit(name, WT.PanamaGL_Swing, jogl)));
      if (file.exists()) {
        baseline = file;
        break;
      }
    }
    Assert.assertNotNull("No JOGL baseline to compare " + name + " with", baseline);

    try {
      BufferedImage expected = ImageIO.read(baseline);
      BufferedImage actual = (BufferedImage) chart.screenshot();

      File export = new File(PANAMAGL_EXPORT + name + ".png");
      export.getAbsoluteFile().getParentFile().mkdirs();
      ImageIO.write(actual, "png", export);

      boolean sameSize = expected.getWidth() == actual.getWidth()
          && expected.getHeight() == actual.getHeight();
      double ratio = sameSize ? ITTest_PanamaGLParity.diffRatio(actual, expected) : 1;

      System.out.println(" ITTest : " + name + " differs from " + baseline.getName() + " by "
          + String.format("%.3f", 100 * ratio) + "%");

      if (ratio > maxDiffRatio) {
        String error = tester.getTestCaseOutputFolder() + "error-" + name;
        new File(error).getAbsoluteFile().getParentFile().mkdirs();
        ImageIO.write(actual, "png", new File(error + ChartTester.FILE_LABEL_ACTUAL + ".png"));
        ImageIO.write(expected, "png", new File(error + ChartTester.FILE_LABEL_EXPECT + ".png"));
        if (sameSize) {
          ImageIO.write(ITTest_PanamaGLParity.diffImage(actual, expected), "png",
              new File(error + ChartTester.FILE_LABEL_DIFF + ".png"));
        }

        Assert.fail("PanamaGL chart differs from " + baseline + " : "
            + (sameSize ? String.format("%.3f", 100 * ratio) + "% of pixels differ"
                : "image size " + actual.getWidth() + "x" + actual.getHeight() + " instead of "
                    + expected.getWidth() + "x" + expected.getHeight())
            + ", see " + error + ChartTester.FILE_LABEL_ACTUAL + ".png");
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static boolean isPanamaGL(Chart chart) {
    return chart.getFactory().getClass().getName().contains("PanamaGL");
  }

  // ----------------------------------------------------------------------------------------------
  // //

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

  /**
   * Name of a test case, e.g. <code>Text_JOGL_AWT_HiDPI=ON_Font=AppleChancery24</code>. A null HiDPI
   * mode is omitted, for tests run with a single HiDPI mode (see {@link #HIDPI_ON}).
   */
  public static String name(String test, String caseName, WT wt, HiDPI hidpi, String info) {
    return test + (caseName != null ? SEP_CASE + caseName : "") + SEP_PROP + wt
        + (hidpi != null ? SEP_PROP + hidpi(hidpi) : "") + (info != null ? SEP_PROP + info : "");
  }

  public static String hidpi(HiDPI hidpi) {
    return "HiDPI" + KV + hidpi;
  }

  public static String className(Object test) {
    return className(test.getClass());// .getSimpleName().replace("ITTest_", "");
  }

  public static String className(Class<?> test) {
    return test.getSimpleName().replace("ITTest_", "");
  }



  // ----------------------------------------------------------------------------------------------
  // //

  public static void sleep(int mili) {
    try {
      Thread.sleep(mili);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------------------------------
  // //

  // CONTENT

  public static Shape surface() {
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);

    ColorMapper colorMapper =
        new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, 0.75f));
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
