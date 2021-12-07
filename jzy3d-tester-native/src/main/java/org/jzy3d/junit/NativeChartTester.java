package org.jzy3d.junit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
  protected BufferedImage getBufferedImage(Chart chart) throws IOException {
    if (classicScreenshotGen) {
      // This screenshot generation is working well
      //
      //

      chart.screenshot();
      AWTRenderer3d awtR = (AWTRenderer3d) ((INativeCanvas) chart.getCanvas()).getRenderer();
      return awtR.getLastScreenshotImage();
    } else {
      // This screenshot generation performed OUT of renderer is not working well yet
      //
      //

      NativeDesktopPainter painter = (NativeDesktopPainter) chart.getPainter();
      GLContext glContext = painter.getCurrentContext(chart.getCanvas());

      glContext.makeCurrent();

      // make screenshot
      AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
      screenshot.readPixels(painter.getGL(), true);
      BufferedImage actual = screenshot.readPixelsToBufferedImage(painter.getGL(), true);

      glContext.release();

      return actual;
    }
  }

  boolean classicScreenshotGen = true;


  /* *********************************************************************** */

  /** A helper to build an offscreen chart simply out of a list of {@link Drawable} */
  public static AWTChart offscreen(Drawable... drawables) {
    // Initialize chart
    Quality q = Quality.Intermediate();

    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(TEST_IMG_SIZE, TEST_IMG_SIZE);

    AWTNativeChart chart = (AWTNativeChart) f.newChart(q);
    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();

    // Optimise processor
    chart.setAnimated(false);// keep animated otherwise mouse wheel not properly updating
    mouse.setUpdateViewDefault(!chart.getQuality().isAnimated());

    for (Drawable d : drawables)
      chart.add(d);
    return chart;
  }

  /* *********************************************************************** */

  public TextureData loadTextureData(String filename, GL gl) throws IOException {
    TextureData i2 = TextureIO.newTextureData(gl.getGLProfile(), new File(filename), true, null);
    return i2;
  }

  public void screenshot(TextureData image, String testImage) throws IOException {
    File output = new File(testImage);
    if (!output.getParentFile().exists())
      output.getParentFile().mkdirs();
    TextureIO.write(image, output);
  }


}
