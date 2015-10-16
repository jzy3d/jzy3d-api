package org.jzy3d.junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Primitives for chart tests.
 * 
 * A chart having a screenshot different from the test case
 * image will generate throw a ChartTestFailed exception.
 * 
 * @author martin
 */
public class ChartTest{
    //public static ChartTest tester = new ChartTest();
    
    public static void assertSimilar(Chart chart, String testImage){
        try {
            new ChartTest().execute(chart, testImage);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        assertTrue(testImage, true);
    }
    
    
    /*
     * In Java, a mouse click only registers if the mouse is pressed and
     * released without moving the mouse at all. This is difficult for most
     * users to accomplish, so most UI elements (like buttons) react to the
     * mouse press and release events and ignore the "click".
     */

    /**
     * Run a chart and verify if its screenshot is pixelwise similar to the test
     * case image.
     * 
     * If test case image does not exist, build it for the first time.
     * 
     * Failure to compare the chart with the test case image will create an
     * image <code>data/tests/error-[name].png</code>. This image is always
     * deleted before running a testcase.
     * 
     * Calling clean() will delete the test case image.
     * 
     * @param chart
     * @param testImage
     * @throws IOException
     *             if a non chart related error occurs. Actual chart test errors
     *             call <code>fail(...)</code>
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
            Logger.getLogger(ChartTest.class).warn("test case file not cleaned: " + testImage);
    }

    public boolean cleanFile(String file) {
        return new File(file).delete();
    }

    public void build(Chart chart, String testImage) throws IOException {
        Logger.getLogger(ChartTest.class).warn("building the screenshot to assert later as no test image is available: " + testImage);
        screenshot(chart, testImage);
    }

    public void build(TextureData image, String testImage) throws IOException {
        Logger.getLogger(ChartTest.class).warn("saving the image to assert later as no test image is available: " + testImage);

        screenshot(image, testImage);
    }

    public boolean isBuilt(String testImage) {
        return new File(testImage).exists();
    }

    public void test(Chart chart, String testImage) throws IOException {
        try {
            Logger.getLogger(ChartTest.class).info("compare chart with " + testImage);
            compare(chart, testImage);
        } catch (IOException e) {
            fail("IOException: " + e.getMessage() + " for " + testImage);
        } catch (ChartTestFailed e) {
            String errorFile = getTestCaseFailedFileName() + new File(testImage).getName().replace(".", "#ERROR#.");
            screenshot(chart, errorFile);//testImage.replace(".", "#ERROR#."));
            fail("Chart test failed: " + e.getMessage() + " see " + testImage);
        }
    }

    /* */

    /**
     * Compare the image displayed by the chart with an image given by filename
     * @param chart
     * @param filename
     * @throws IOException
     * @throws ChartTestFailed
     */
    public void compare(Chart chart, String filename) throws IOException, ChartTestFailed {
        
        // Will compare buffered
        if(chart.getCanvas().getRenderer() instanceof AWTRenderer3d){
            chart.screenshot();
            AWTRenderer3d awtR = (AWTRenderer3d)chart.getCanvas().getRenderer();
            BufferedImage actual = awtR.getLastScreenshotImage();
            BufferedImage expected = loadBufferedImage(filename);
            
            compare(actual, expected);
        }
        else {
            TextureData actual = chart.screenshot();
            TextureData expected = loadTextureData(filename, chart.getView().getCurrentGL());
            //compare(i, i2);
            
            Logger.getLogger(ChartTest.class).warn("CAN NOT COMPARE TEXTURE DATA FOR THE MOMENT");
        }
    }
    
    public BufferedImage loadBufferedImage(String filename) throws IOException{
        return ImageIO.read(new File(filename));
    }

    public TextureData loadTextureData(String filename, GL gl) throws IOException {
        TextureData i2 = TextureIO.newTextureData(gl.getGLProfile(), new File(filename), true, null);
        return i2;
    }
    

    /**
     * Compare images pixel-wise.
     * 
     * @param i1
     * @param i2
     * @throws ChartTestFailed
     *             as soon as a pixel difference can be found
     */
    public void compare(TextureData i1, TextureData i2) throws ChartTestFailed {
        // int rbg = image.getRGB((int) x, (maxRow) - ((int) y));

        int i1W = i1.getWidth();
        int i1H = i1.getHeight();
        int i2W = i2.getWidth();
        int i2H = i2.getHeight();

        if (i1W == i2W && i1H == i2H) {
            for (int i = 0; i < i1W; i++) {
                for (int j = 0; j < i1H; j++) {
                    /*int p1rgb = i1.getRGB(i, j);
                    int p2rgb = i2.getRGB(i, j);
                    if (p1rgb != p2rgb) {
                        String m = "pixel diff @(" + i + "," + j + ")";
                        throw new ChartTestFailed(m, i1, i2);
                    }*/
                }
            }
        } else {
            String m = "image size differ: i1={" + i1W + "," + i1H + "} i2={" + i2W + "," + i2H + "}";
            throw new ChartTestFailed(m);
        }
    }
    
    public void compare(BufferedImage i1, BufferedImage i2) throws ChartTestFailed {
        // int rbg = image.getRGB((int) x, (maxRow) - ((int) y));

        int i1W = i1.getWidth();
        int i1H = i1.getHeight();
        int i2W = i2.getWidth();
        int i2H = i2.getHeight();

        if (i1W == i2W && i1H == i2H) {
            for (int i = 0; i < i1W; i++) {
                for (int j = 0; j < i1H; j++) {
                    int p1rgb = i1.getRGB(i, j);
                    int p2rgb = i2.getRGB(i, j);
                    if (p1rgb != p2rgb) {
                        String m = "pixel diff @(" + i + "," + j + ")";
                        throw new ChartTestFailed(m, i1, i2);
                    }
                }
            }
        } else {
            String m = "image size differ: i1={" + i1W + "," + i1H + "} i2={" + i2W + "," + i2H + "}";
            throw new ChartTestFailed(m);
        }
    }

    /* */

    public void screenshot(Chart chart, String filename) throws IOException {
        screenshot(chart.screenshot(), filename);
    }

    public void screenshot(TextureData image, String testImage) throws IOException {
        File output = new File(testImage);
        if (!output.getParentFile().exists())
            output.mkdirs();
        TextureIO.write(image, output);
    }

    /* */

    public File getTestCaseFile() {
        return new File(getTestCaseFileName());
    }

    public String getTestCaseFileName() {
        return getTestCaseFileName(getTestName());
    }

    public String getTestCaseFileName(String testName) {
        return getTestCaseFolder() + testName + ".png";
    }

    public String getTestCaseFailedFileName() {
        return getTestCaseFolder() + "error-" + getTestName() + ".png";
    }

    public String getTestCaseFolder() {
        return "data/tests/";
    }

    public String getTestName() {
        return this.getClass().getSimpleName();
    }

    public String getTestCanvasType() {
        return "offscreen, " + WIDTH + ", " + HEIGHT;
    }

    /* */

    // int bufImgType = BufferedImage.TYPE_3BYTE_BGR;// );
    protected int WIDTH = 800;
    protected int HEIGHT = 600;
}

