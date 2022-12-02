package org.jzy3d.plot3d.rendering.view;

import java.awt.image.BufferedImage;
import org.jzy3d.io.AWTImageExporter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

/**
 * This {@link GLEventListener} overrides {@link Renderer3d} for the sole purpose of generating a {@link BufferedImage}.
 * 
 * @see {@link getLastScreenshotImage()} to retrieve the image.
 */
public class AWTRenderer3d extends Renderer3d {
  
  protected BufferedImage bufferedImage;
  protected AWTImageExporter exporter;
  protected AWTGLReadBufferUtil screenshotMaker;

  public AWTRenderer3d() {
    super();
  }

  public AWTRenderer3d(View view) {
    super(view);
  }

  public AWTRenderer3d(View view, boolean traceGL, boolean debugGL) {
    super(view, traceGL, debugGL);
    
    screenshotMaker = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);

  }

  /********************* SCREENSHOTS ***********************/
  
  public BufferedImage getLastScreenshotImage() {
    return bufferedImage;
  }

  @Override
  protected void renderScreenshotIfRequired(GL gl) {
    if (doScreenshotAtNextDisplay) {
      // Get JOGL Image
      screenshotMaker.readPixels(gl, true);
      image = screenshotMaker.getTextureData();
      
      // Get AWT Image
      bufferedImage = screenshotMaker.readPixelsToBufferedImage(gl, true);

      doScreenshotAtNextDisplay = false;
    }
  }
  
  @Override
  protected void exportImageIfRequired(GL gl) {
    if(exporter!=null) {
      //synchronized(this) {
        BufferedImage image = screenshotMaker.readPixelsToBufferedImage(gl, /*0, 0, width, height, */true);
        exporter.export(image);
      //}
    }
  }
  
  public AWTImageExporter getExporter() {
    return exporter;
  }

  public void setExporter(AWTImageExporter exporter) {
    this.exporter = exporter;
  }


}
