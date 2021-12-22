package org.jzy3d.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.IntegerCoord2d;

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

  public static int TEST_IMG_SIZE = 500;

  protected boolean textInvisible = false;

  protected String testCaseOutputFolder = ERROR_IMAGE_FOLDER_DEFAULT;
  protected String testCaseInputFolder = EXPECTED_IMAGE_FOLDER;

  public static final String EXPECTED_IMAGE_FOLDER = "src/test/resources/";
  public static final String ERROR_IMAGE_FOLDER_DEFAULT = "target/";

  // int bufImgType = BufferedImage.TYPE_3BYTE_BGR;// );
  protected int WIDTH = 800;
  protected int HEIGHT = 600;

  public boolean isTextInvisible() {
    return textInvisible;
  }

  public void setTextInvisible(boolean textInvisible) {
    this.textInvisible = textInvisible;
  }

  public void assertSimilar(Chart chart, String testImage) {
    if(isTextInvisible()){
      chart.getAxisLayout().setXTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setYTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setZTickColor(chart.getView().getBackgroundColor());
      chart.getAxisLayout().setXTickLabelDisplayed(false);
      chart.getAxisLayout().setYTickLabelDisplayed(false);
      chart.getAxisLayout().setZTickLabelDisplayed(false);
      chart.render();
      //chart.getAxisLayout().setMainColor(chart.getView().getBackgroundColor());
    }
    if(!chart.getFactory().getPainterFactory().isOffscreen()) {
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
   * Perform a chart comparison to image and output 3 images in case of failure
   * <ul>
   * <li>Expected image
   * <li>Actual image
   * <li>Diff image
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

      String actualFile =
          getTestCaseFailedFileName() + new File(testImage).getName().replace(".", "#ACTUAL#.");
      chart.screenshot(new File(actualFile));
      logger.error("ACTUAL IMAGE : " + actualFile);

      // -----------------------------
      // Writing EXPECTED file

      String expectFile =
          getTestCaseFailedFileName() + new File(testImage).getName().replace(".", "#EXPECT#.");
      BufferedImage expected = e.getExpectedImage();
      ImageIO.write(expected, "png", new File(expectFile));
      logger.error("EXPECTED IMAGE : " + expectFile);

      // -----------------------------
      // Writing DIFF file

      String diffFile =
          getTestCaseFailedFileName() + new File(testImage).getName().replace(".", "#DIFF#.");

      BufferedImage diffImage = copyImage(expected);
      pixelHighlight(diffImage, e.getDiffCoordinates(), Highlight.RED);

      ImageIO.write(diffImage, "png", new File(diffFile));
      logger.error("DIFF IMAGE : " + diffFile);

      // LET TEST FAIL
      
      fail("Chart test failed: " + e.getMessage() + " see " + diffFile);

    } catch (IOException e) {
      // -----------------------------
      // OTHER FAILURES

      fail("IOException: " + e.getMessage() + " for " + testImage);
    }
  }

  protected BufferedImage copyImage(BufferedImage source) {
    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    Graphics g = b.createGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }

  /**
   * Invert the pixel color identified by the input coordinates.
   */
  protected void pixelHighlight(BufferedImage expected, List<IntegerCoord2d> diffs,
      Highlight highlight) {
    for (IntegerCoord2d diff : diffs) {
      pixelHighlight(expected, diff, highlight);
    }
  }

  /**
   * Invert the pixel color identified by the input coordinates ({@link IntegerCoord2d})
   */
  protected void pixelHighlight(BufferedImage expected, IntegerCoord2d diffs, Highlight highlight) {
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

  /* */

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

    if (i1W == i2W && i1H == i2H) {
      ChartTestFailed potentialFailure = new ChartTestFailed("pixel diff", actual, expected);
      boolean ok = true;

      for (int i = 0; i < i1W; i++) {
        for (int j = 0; j < i1H; j++) {
          int p1rgb = actual.getRGB(i, j);
          int p2rgb = expected.getRGB(i, j);

          if (p1rgb != p2rgb) {
            ok = false;
            potentialFailure.addDiffCoordinates(i, j);
            potentialFailure.setActualPixel(p1rgb);
            potentialFailure.setExpectedPixel(p2rgb);
            // String m = "pixel diff start @(" + i + "," + j + ")";
            // throw new ChartTestFailed(m, i1, i2);
          }
        }
      }

      if (!ok) {
        throw potentialFailure;
      }

    } else {
      String m = "image size differ: i1={" + i1W + "," + i1H + "} i2={" + i2W + "," + i2H + "}";
      throw new ChartTestFailed(m, actual, expected);
    }
  }


  /* */

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


}
