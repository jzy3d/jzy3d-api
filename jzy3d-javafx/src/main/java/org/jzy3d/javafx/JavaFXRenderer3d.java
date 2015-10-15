package org.jzy3d.javafx;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

public class JavaFXRenderer3d extends AWTRenderer3d{
    public JavaFXRenderer3d() {
        super();
    }

    public JavaFXRenderer3d(View view, boolean traceGL, boolean debugGL, GLU glu) {
        super(view, traceGL, debugGL, glu);
    }

    public JavaFXRenderer3d(View view, boolean traceGL, boolean debugGL) {
        super(view, traceGL, debugGL);
    }

    public JavaFXRenderer3d(View view) {
        super(view);
    }

    @Override
    public void display(GLAutoDrawable canvas) {
        GL gl = canvas.getGL();

        if (view != null) {
            view.clear(gl);
            view.render(gl, glu);

            // Convert as JavaFX Image and notify all listeners
            Image image = makeScreenshotAsJavaFXImage(gl);
            fireDisplay(image);
            
            if (doScreenshotAtNextDisplay) {
                makeScreenshotAsJavaFXImage(gl);
                doScreenshotAtNextDisplay = false;
            }
        }
    }

    private Image makeScreenshotAsJavaFXImage(GL gl) {
        BufferedImage i = makeScreenshotAsBufferedImage(gl);
        return SwingFXUtils.toFXImage(i, null);
    }

    private BufferedImage makeScreenshotAsBufferedImage(GL gl) {
        AWTGLReadBufferUtil screenshot = makeScreenshot(gl);
        return screenshot.readPixelsToBufferedImage(gl, true);
    }

    private AWTGLReadBufferUtil makeScreenshot(GL gl) {
        AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
        screenshot.readPixels(gl, true);
        image = screenshot.getTextureData();
        return screenshot;
    }
    
    public interface DisplayListener{
        public void onDisplay(Image image);
    }
    
    public void addDisplayListener(DisplayListener listener){
        listeners.add(listener);
    }
    
    protected void fireDisplay(Image image){
        for(DisplayListener listener : listeners){
            listener.onDisplay(image);
        }
    }
    
    protected List<DisplayListener> listeners = new ArrayList<DisplayListener>();
}
