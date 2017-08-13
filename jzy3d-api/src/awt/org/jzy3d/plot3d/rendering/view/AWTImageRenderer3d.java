package org.jzy3d.plot3d.rendering.view;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

/** A renderer generating AWT {@link BufferedImage}s and notifying a {@link DisplayListener} when
 * it is updated.
 * 
 * @author Martin Pernollet
 */
public class AWTImageRenderer3d extends AWTRenderer3d {

    public interface DisplayListener{
        public void onDisplay(Object image);
    }

    protected List<DisplayListener> listeners = new ArrayList<DisplayListener>();

    public AWTImageRenderer3d() {
        super();
    }

    public AWTImageRenderer3d(View view, boolean traceGL, boolean debugGL, GLU glu) {
        super(view, traceGL, debugGL, glu);
    }
    
    @Override
    public void display(GLAutoDrawable canvas) {
        GL gl = canvas.getGL();

        if (view != null) {
            view.clear(gl);
            view.render(gl, glu);

            // Convert as JavaFX Image and notify all listeners
            BufferedImage image = makeScreenshotAsBufferedImage(gl);
            fireDisplay(image);
            
            if (doScreenshotAtNextDisplay) {
                //makeScreenshotAsJavaFXImage(gl);
                doScreenshotAtNextDisplay = false;
            }
        }
    }

    protected BufferedImage makeScreenshotAsBufferedImage(GL gl) {
        AWTGLReadBufferUtil screenshot = makeScreenshot(gl);
        return screenshot.readPixelsToBufferedImage(gl, true);
    }

    private AWTGLReadBufferUtil makeScreenshot(GL gl) {
        AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
        screenshot.readPixels(gl, true);
        image = screenshot.getTextureData();
        return screenshot;
    }

    public AWTImageRenderer3d(View view, boolean traceGL, boolean debugGL) {
        super(view, traceGL, debugGL);
    }

    public void addDisplayListener(DisplayListener listener) {
        listeners.add(listener);
    }

    protected void fireDisplay(Object image) {
        for(DisplayListener listener : listeners){
            listener.onDisplay(image);
        }
    }

    public AWTImageRenderer3d(View view) {
        super(view);
    }

}