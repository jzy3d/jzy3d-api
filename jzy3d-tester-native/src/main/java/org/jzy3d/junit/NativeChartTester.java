package org.jzy3d.junit;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class NativeChartTester extends ChartTester {
    private static Logger logger = Logger.getLogger(NativeChartTester.class);

    public static AWTChart offscreen(Drawable... drawables) {
        // Initialize chart
        Quality q = Quality.Intermediate;

        AWTChartFactory f = new AWTChartFactory();
        f.setOffscreen(TEST_IMG_SIZE, TEST_IMG_SIZE);
        
        AWTNativeChart chart = (AWTNativeChart)f.newChart(q);
        AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();

        // Optimise processor
        chart.setAnimated(false);// keep animated otherwise mouse wheel not properly updating
        mouse.setUpdateViewDefault(!chart.getQuality().isAnimated());

        for (Drawable d : drawables)
            chart.add(d);
        return chart;
    }

    /* *********************************************************************** */
    
    public void build(TextureData image, String testImage) throws IOException {
        logger.warn("saving the image to assert later as no test image is available: " + testImage);
        screenshot(image, testImage);
    }


	/**
     * Compare the image displayed by the chart with an image given by filename
     * @param chart
     * @param filename
     * @throws IOException
     * @throws ChartTestFailed
     */
    @Override
    public void compare(Chart chart, String filename) throws IOException, ChartTestFailed {
    	NativeDesktopPainter painter = (NativeDesktopPainter)chart.getPainter();
    	GLContext glContext = painter.getCurrentContext(chart.getCanvas());
    	
    	glContext.makeCurrent();
    	
    	// make screenshot
    	AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
        screenshot.readPixels(painter.getGL(), true);
        BufferedImage actual = screenshot.readPixelsToBufferedImage(painter.getGL(), true);
    	
        glContext.release();
        
        BufferedImage expected = loadBufferedImage(filename);
        
        compare(actual, expected);

        
        /*// Will compare buffered image
        if(((INativeCanvas)chart.getCanvas()).getRenderer() instanceof AWTRenderer3d){
            chart.screenshot();
            AWTRenderer3d awtR = (AWTRenderer3d)((INativeCanvas)chart.getCanvas()).getRenderer();
            BufferedImage actual = awtR.getLastScreenshotImage();
            BufferedImage expected = loadBufferedImage(filename);
            
            compare(actual, expected);
        }
        else {
            TextureData actual = (TextureData)chart.screenshot();
            TextureData expected = loadTextureData(filename, ((NativeDesktopPainter)chart.getView().getPainter()).getCurrentGL(chart.getCanvas()));
            fail("CAN NOT COMPARE TEXTURE DATA FOR THE MOMENT");
        }*/
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
    
    public void screenshot(TextureData image, String testImage) throws IOException {
        File output = new File(testImage);
        if (!output.getParentFile().exists())
            output.getParentFile().mkdirs();
        TextureIO.write(image, output);
    }

}
