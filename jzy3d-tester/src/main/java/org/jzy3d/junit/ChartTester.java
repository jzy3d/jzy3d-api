package org.jzy3d.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.axis.layout.providers.StaticTickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.DefaultDecimalTickRenderer;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;

/**
 * Primitives for chart tests.
 * 
 * A chart having a screenshot different from the test case image will generate throw a
 * ChartTestFailed exception.
 * 
 * @author martin
 */
public class ChartTester {
  private static Logger logger = LogManager.getLogger(ChartTester.class);

  public static final String FILE_LABEL_ACTUAL = "_ACTUAL";
  public static final String FILE_LABEL_EXPECT = "_EXPECT";
  public static final String FILE_LABEL_DIFF = "_DIFF";
  public static final String FILE_LABEL_ZOOM = "_ZOOM";

  public static int TEST_IMG_SIZE = 500;

  protected boolean textInvisible = false;

  protected String testCaseOutputFolder = MAVEN_TARGET_PATH;
  protected String testCaseInputFolder = MAVEN_TEST_RESOURCES_PATH;

  public static final String MAVEN_TEST_RESOURCES_PATH = "src/test/resources/";
  public static final String MAVEN_TARGET_PATH = "target/";

  protected Dimension colorbarDimension = new Dimension(200, 600);

  protected int WIDTH = 800;
  protected int HEIGHT = 600;
  

  // ----------------------------------------------------------------------------------- //
  //
  // THE ASSERTION METHODS ALLOW TO
  //
  // (1) CLEAN
  // (2) GENERATE A REFERENCE IMAGE IF IS MISSING
  // (3) COMPARE A CHART TO A REFERENCE IMAGE
  // (4) GENERATE REPORTS TO SHOW ERRORS (DIFF, ACTUAL, EXPECTED, ZOOM)
  //
  // ----------------------------------------------------------------------------------- //



  public void assertSimilar(Chart chart, String testImage) {
    if (isTextInvisible()) {
      chart.getAxisLayout().setXTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setYTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setZTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setXTickLabelDisplayed(false);
      chart.getAxisLayout().setYTickLabelDisplayed(false);
      chart.getAxisLayout().setZTickLabelDisplayed(false);
      chart.render();
      // chart.getAxisLayout().setMainColor(chart.getView().getBackgroundColor());
    }
    if (!chart.getFactory().getPainterFactory().isOffscreen()) {
      logger.warn("Making assertions on a non offscreen chart");
    }
    try {
      execute(chart, testImage);
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    assertTrue(testImage, true);
  }

  /*
   * In Java, a mouse click only registers if the mouse is pressed and released without moving the
   * mouse at all. This is difficult for most users to accomplish, so most UI elements (like
   * buttons) react to the mouse press and release events and ignore the "click".
   */

  /**
   * Run a chart and verify if its screenshot is pixelwise similar to the test case image.
   * 
   * If test case image does not exist, build it for the first time.
   * 
   * Failure to compare the chart with the test case image will create an image
   * <code>data/tests/error-[name].png</code>. This image is always deleted before running a
   * testcase.
   * 
   * Calling clean() will delete the test case image.
   * 
   * @param chart
   * @param testImage
   * @throws IOException if a non chart related error occurs. Actual chart test errors call
   *         <code>fail(...)</code>
   */
  public void execute(Chart chart, String testImage) throws IOException {
    clean(getTestCaseFailedFileName());

    if (!isBuilt(testImage))
      build(chart, testImage);
    test(chart, testImage);
  }

  public void execute(Chart chart) throws IOException {
    execute(chart, getTestCaseFileName());
  }

  /* Primitives to test images */

  public void clean(String testImage) {
    if (!cleanFile(testImage) && isBuilt(testImage))
      logger.warn("test case file not cleaned: " + testImage);
  }

  public boolean cleanFile(String file) {
    return new File(file).delete();
  }

  public void build(Chart chart, String testImage) throws IOException {
    logger.warn(
        "building the screenshot to assert later as no test image is available: " + testImage);

    chart.screenshot(new File(testImage));
  }

  public boolean isBuilt(String testImage) {
    return new File(testImage).exists();
  }


  /**
   * Perform a chart comparison to image and output multiple diagnostic images in case of failure
   * <ul>
   * <li>Expected image
   * <li>Actual image
   * <li>Diff image
   * <li>Zoom image
   * </ul>
   * 
   * @param chart
   * @param testImage
   * @throws IOException
   */
  public void test(Chart chart, String testImage) throws IOException {
    try {
      logger.info("compare chart with " + testImage);
      compare(chart, testImage);
      logger.info("compared chart OK  " + testImage);

    } catch (ChartTestFailed e) {

      // -----------------------------
      // Writing ACTUAL file

      String actualFile = getActualFilename(testImage);
      chart.screenshot(new File(actualFile));
      logger.error("ACTUAL IMAGE : " + actualFile);

      // -----------------------------
      // Writing EXPECTED file

      String expectFile = getExpectedFilename(testImage);
      BufferedImage expected = e.getExpectedImage();
      ImageIO.write(expected, "png", new File(expectFile));
      logger.error("EXPECTED IMAGE : " + expectFile);

      // -----------------------------
      // Writing ZOOM file

      String zoomFile = getZoomFilename(testImage);
      BufferedImage zoom = getErroneousArea(expected, e.getDiffCoordinates());
      ImageIO.write(zoom, "png", new File(zoomFile));
      logger.error("ZOOM IMAGE : " + zoomFile);


      // -----------------------------
      // Writing DIFF file

      String diffFile = getDiffFilename(testImage);
      BufferedImage diffImage = copy(expected);
      highlightPixel(diffImage, e.getDiffCoordinates(), Highlight.RED);


      // Case of different image size written to DIFF
      if (!e.isSameImageSize()) {
        String m = e.getImageSizeDifferenceMessage();

        logger.error(m);

        m += " - Diff will only show expected image. See the actual image separately";

        highlightSize(diffImage, m);
      }


      ImageIO.write(diffImage, "png", new File(diffFile));
      logger.error("DIFF IMAGE : " + diffFile);

      // -----------------------------
      // LET TEST FAIL

      fail("Chart test failed: " + e.getMessage() + " see " + diffFile + "\n"
          + e.getDiffCoordinates().size() + " pixel(s) differ");

    } catch (IOException e) {

      // -----------------------------
      // OTHER FAILURES

      fail("IOException: " + e.getMessage() + " for " + testImage);
    }
  }
  
  public void assertSimilar(IColorMap colormap, String testImage) {
    try {
      execute(colormap, testImage);
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    assertTrue(testImage, true);
  }

  public void execute(IColorMap colormap, String testImage) throws IOException {
    clean(getTestCaseFailedFileName());

    if (!isBuilt(testImage))
      build(colormap, testImage);
    test(colormap, testImage);
  }

  public void execute(IColorMap chart) throws IOException {
    execute(chart, getTestCaseFileName());
  }

  public void build(IColorMap colormap, String testImage) throws IOException {
    logger.warn(
        "building the screenshot to assert later as no test image is available: " + testImage);

    BufferedImage b = getBufferedImage(getImageGenerator(colormap));

    ImageIO.write(b, "png", new File(testImage));
  }



  /**
   * Perform a chart comparison to image and output multiple diagnostic images in case of failure
   * <ul>
   * <li>Expected image
   * <li>Actual image
   * <li>Diff image
   * <li>Zoom image
   * </ul>
   * 
   * @param chart
   * @param testImage
   * @throws IOException
   */
  public void test(IColorMap chart, String testImage) throws IOException {
    try {
      logger.info("compare chart with " + testImage);
      compare(chart, testImage);
      logger.info("compared chart OK  " + testImage);

    } catch (ChartTestFailed e) {

      fail("ChartTestFailed: " + e.getMessage() + " for " + testImage);
      
    } catch (IOException e) {

      fail("IOException: " + e.getMessage() + " for " + testImage);
    }
  }
  
  // -----------------------------------------------------------------//
  //
  // HIGHLIGHT ERRORS
  //
  //

  protected void highlightSize(BufferedImage diffImage, String m) {
    Graphics g = diffImage.createGraphics();

    int fontSize = 16;

    g.setColor(java.awt.Color.RED);
    g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, fontSize));
    g.drawString(m, 10, 10 + fontSize);
    g.dispose();
  }

  protected BufferedImage copy(BufferedImage source) {
    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    Graphics g = b.createGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }

  /**
   * Invert the pixel color identified by the input coordinates.
   */
  protected void highlightPixel(BufferedImage expected, List<IntegerCoord2d> diffs,
      Highlight highlight) {

    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = -1, maxY = -1;

    for (IntegerCoord2d diff : diffs) {

      if (minX > diff.x)
        minX = diff.x;
      if (maxX < diff.x)
        maxX = diff.x;
      if (minY > diff.y)
        minY = diff.y;
      if (maxY < diff.y)
        maxY = diff.y;

      doHighlightPixels(expected, diff, highlight);
    }

    if(minX<=maxX && minY<=maxY) {
      //expected.getSubimage(minX, minY, maxX - minX, maxY - minY);
  
      Graphics2D g2d = (Graphics2D) expected.createGraphics();
      g2d.setColor(java.awt.Color.RED);
      g2d.setStroke(new BasicStroke(1f));
      g2d.drawRect(minX, minY, maxX - minX, maxY - minY);
  
      System.err
          .println("Erroneous Area : x[" + minX + ", " + maxX + "] y[" + minY + " " + maxY + "]");
    }
  }


  /**
   * Invert the pixel color identified by the input coordinates ({@link IntegerCoord2d})
   */
  protected void doHighlightPixels(BufferedImage expected, IntegerCoord2d diffs, Highlight highlight) {
    int rgb = expected.getRGB(diffs.x, diffs.y);

    int alpha = ((rgb >> 24) & 0xff);
    int red = ((rgb >> 16) & 0xff);
    int green = ((rgb >> 8) & 0xff);
    int blue = ((rgb) & 0xff);

    if (Highlight.INVERT.equals(highlight)) {
      rgb = (alpha << 24) | (255 - red << 16) | (255 - green << 8) | 255 - blue;
    } else if (Highlight.RED.equals(highlight)) {
      rgb = (255 << 24) | (255 << 16);
    }

    expected.setRGB(diffs.x, diffs.y, rgb);
  }

  enum Highlight {
    INVERT, RED
  }
  
  protected BufferedImage getErroneousArea(BufferedImage expected, List<IntegerCoord2d> diffs) {
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = -1, maxY = -1;

    for (IntegerCoord2d diff : diffs) {
      if (minX > diff.x)
        minX = diff.x;
      if (maxX < diff.x)
        maxX = diff.x;
      if (minY > diff.y)
        minY = diff.y;
      if (maxY < diff.y)
        maxY = diff.y;
    }

    
    if(minX<=maxX && minY<=maxY) {
    
      System.err
          .println("Erroneous Area : x[" + minX + ", " + maxX + "] y[" + minY + " " + maxY + "]");
  
      return expected.getSubimage(minX, minY, maxX - minX, maxY - minY);
    }
    else {
      return expected;
    }

  }



  // ----------------------------------------------------------------------------------- //
  //
  // THE COMPARISON METHODS SIMPLY CHECK THE DIFFERENCE BETWEEN AN ELEMENT AND ITS IMAGE //
  //
  // ----------------------------------------------------------------------------------- //


  /**
   * Compare the image displayed by the chart with an image given by filename.
   * 
   * @param chart
   * @param filename
   * @throws IOException
   * @throws ChartTestFailed is thrown if at least one pixel differ. The exception holds all pixel
   *         coordinates where a difference exists.
   */
  public void compare(Chart chart, String filename) throws IOException, ChartTestFailed {
    BufferedImage actual = getBufferedImage(chart);
    BufferedImage expected = loadBufferedImage(filename);

    compare(actual, expected);
  }

  /**
   * Compare a colorbar image with a reference image given by filename.
   * 
   * @param chart
   * @param filename
   * @throws IOException
   * @throws ChartTestFailed is thrown if at least one pixel differ. The exception holds all pixel
   *         coordinates where a difference exists.
   */
  public void compare(AWTColorbarImageGenerator colorbar, String filename)
      throws IOException, ChartTestFailed {
    BufferedImage actual = getBufferedImage(colorbar);
    BufferedImage expected = loadBufferedImage(filename);

    compare(actual, expected);
  }

  public void compare(IColorMap colormap, String filename) throws IOException, ChartTestFailed {
    compare(getImageGenerator(colormap), filename);
  }


  // --------------------------------------------

  protected AWTColorbarImageGenerator getImageGenerator(IColorMap colormap) throws IOException {

    // Configuration
    colormap.setDirection(false);


    double[] values = {-2, -1, 0, 1, 2};

    // Tools
    ITickRenderer r = new DefaultDecimalTickRenderer();
    StaticTickProvider p = new StaticTickProvider(values);
    ColorMapper mapper = new ColorMapper(colormap, -2, 2);

    // Generator
    AWTColorbarImageGenerator g = new AWTColorbarImageGenerator(mapper, p, r);
    g.setBackgroundColor(Color.BLUE);
    g.setHasBackground(true);
    g.setPixelScale(new Coord2d(3, 3));
    return g;
  }
  
  protected BufferedImage getBufferedImage(AWTColorbarImageGenerator colorbar) throws IOException {
    return colorbar.toImage(colorbarDimension.width, colorbarDimension.height);
  }

  protected BufferedImage getBufferedImage(Chart chart) throws IOException {
    return (BufferedImage) chart.screenshot();
  }

  protected BufferedImage loadBufferedImage(String filename) throws IOException {
    return ImageIO.read(new File(filename));
  }

  /**
   * Perform the comparison between two {@link BufferedImage}.
   * 
   * @param actual
   * @param expected
   * @throws ChartTestFailed is thrown if at least one pixel differ. The exception holds all pixel
   *         coordinates where a difference exists.
   */
  public void compare(BufferedImage actual, BufferedImage expected) throws ChartTestFailed {
    // int rbg = image.getRGB((int) x, (maxRow) - ((int) y));

    int i1W = actual.getWidth();
    int i1H = actual.getHeight();
    int i2W = expected.getWidth();
    int i2H = expected.getHeight();

    // if same size
    if (i1W == i2W && i1H == i2H) {
      ChartTestFailed chartFailure = new ChartTestFailed("pixel diff", actual, expected);
      boolean ok = true;

      // check difference pixel-wise
      for (int i = 0; i < i1W; i++) {
        for (int j = 0; j < i1H; j++) {
          int p1rgb = actual.getRGB(i, j);
          int p2rgb = expected.getRGB(i, j);

          if (p1rgb != p2rgb) {
            ok = false;
            chartFailure.addDiffCoordinates(i, j);
          }
        }
      }

      if (!ok) {
        throw chartFailure;
      }
    }

    // if size differ
    else {
      String m =
          "image size differ: actual={" + i1W + "," + i1H + "} expected={" + i2W + "," + i2H + "}";
      throw new ChartTestFailed(m, actual, expected);
    }
  }

  // ----------------------------------------------------------------------------------- //
  //
  // PATH BUILDER //
  //
  // ----------------------------------------------------------------------------------- //


  public String path(Object obj) {
    return path(obj.getClass());
  }

  public String path(Class<?> clazz) {
    return path(clazz.getSimpleName());
  }

  public String path(String filename) {
    if (!filename.contains("."))
      return testCaseInputFolder + filename + ".png";
    else
      return testCaseInputFolder + filename;
  }


  private String getDiffFilename(String image) {
    return getTestCaseFailedFileName()
        + new File(image).getName().replace(".", FILE_LABEL_DIFF + ".");
  }

  private String getZoomFilename(String image) {
    return getTestCaseFailedFileName()
        + new File(image).getName().replace(".", FILE_LABEL_ZOOM + ".");
  }

  private String getExpectedFilename(String image) {
    return getTestCaseFailedFileName()
        + new File(image).getName().replace(".", FILE_LABEL_EXPECT + ".");
  }

  private String getActualFilename(String image) {
    return getTestCaseFailedFileName()
        + new File(image).getName().replace(".", FILE_LABEL_ACTUAL + ".");
  }

  // ----------------------------------------------------------------------------------- //
  //
  // CUSTOMIZE TESTER //
  //
  // ----------------------------------------------------------------------------------- //

  public File getTestCaseFile() {
    return new File(getTestCaseFileName());
  }

  public String getTestCaseFileName() {
    return getTestCaseFileName(getTestName());
  }

  public String getTestCaseFileName(String testName) {
    return getTestCaseInputFolder() + testName + ".png";
  }

  public String getTestCaseFailedFileName() {
    return getTestCaseOutputFolder() + "error-";
  }

  public String getTestName() {
    return this.getClass().getSimpleName();
  }

  public String getTestCanvasType() {
    return "offscreen, " + WIDTH + ", " + HEIGHT;
  }

  public String getTestCaseInputFolder() {
    return testCaseInputFolder;
  }

  public void setTestCaseInputFolder(String testCaseFolder) {
    this.testCaseInputFolder = testCaseFolder;
  }

  public String getTestCaseOutputFolder() {
    return testCaseOutputFolder;
  }

  public void setTestCaseOutputFolder(String testCaseOutputFolder) {
    this.testCaseOutputFolder = testCaseOutputFolder;
  }

  public boolean isTextInvisible() {
    return textInvisible;
  }

  public void setTextInvisible(boolean textInvisible) {
    this.textInvisible = textInvisible;
  }


}
