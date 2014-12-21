package org.jzy3d.plot3d.rendering.view;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

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

    @Override
    public void display(GLAutoDrawable canvas) {
        GL gl = canvas.getGL();

        if (view != null) {
            view.clear(gl);
            view.render(gl, glu);

            if (doScreenshotAtNextDisplay) {
                AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
                screenshot.readPixels(gl, true);
                image = screenshot.getTextureData();
                doScreenshotAtNextDisplay = false;
            }
        }
    }
}
