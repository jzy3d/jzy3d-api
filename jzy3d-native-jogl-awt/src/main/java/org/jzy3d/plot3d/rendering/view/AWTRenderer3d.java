package org.jzy3d.plot3d.rendering.view;

import java.awt.image.BufferedImage;
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

  public AWTRenderer3d() {
    super();
  }

  public AWTRenderer3d(View view) {
    super(view);
  }

  public AWTRenderer3d(View view, boolean traceGL, boolean debugGL) {
    super(view, traceGL, debugGL);
  }

  /********************* SCREENSHOTS ***********************/
  
  public BufferedImage getLastScreenshotImage() {
    return bufferedImage;
  }

  @Override
  protected void renderScreenshotIfRequired(GL gl) {
    if (doScreenshotAtNextDisplay) {
      AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
      screenshot.readPixels(gl, true);
      image = screenshot.getTextureData();
      bufferedImage = screenshot.readPixelsToBufferedImage(gl, true);

      doScreenshotAtNextDisplay = false;
    }
  }

}
