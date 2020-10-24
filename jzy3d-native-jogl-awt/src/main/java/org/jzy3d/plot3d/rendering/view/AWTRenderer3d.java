package org.jzy3d.plot3d.rendering.view;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

public class AWTRenderer3d extends Renderer3d{
    public AWTRenderer3d() {
        super();
    }
    public AWTRenderer3d(View view, boolean traceGL, boolean debugGL, GLU glu) {
        super(view, traceGL, debugGL, glu);
    }
    public AWTRenderer3d(View view, boolean traceGL, boolean debugGL) {
        super(view, traceGL, debugGL);
    }
    public AWTRenderer3d(View view) {
        super(view);
    }

    /**
     * Uses a dedicated {@link AWTGLReadBufferUtil} to read a buffered image.
     * @see {@link getLastScreenshotImage()} to retrieve the image
     */
    @Override
    public void display(GLAutoDrawable canvas) {
        GL gl = canvas.getGL();

        if (view != null) {
            view.clear();
            view.render();

            if (doScreenshotAtNextDisplay) {
                AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
                screenshot.readPixels(gl, true);
                image = screenshot.getTextureData();
                bufferedImage = screenshot.readPixelsToBufferedImage(gl, true);

                doScreenshotAtNextDisplay = false;
            }
        }
    }
    
    public BufferedImage getLastScreenshotImage() {
        return bufferedImage;
    }
    
    protected BufferedImage bufferedImage;

}
