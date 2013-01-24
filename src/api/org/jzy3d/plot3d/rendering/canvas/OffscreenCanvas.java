package org.jzy3d.plot3d.rendering.canvas;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class OffscreenCanvas implements ICanvas {
    public OffscreenCanvas(IChartComponentFactory factory, Scene scene, Quality quality, GLProfile profile, int width, int height) {
        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view, false, false);

        initGLPBuffer(width, height);
    }

    protected void initGLPBuffer(int width, int height) {
        GLCapabilities caps = org.jzy3d.global.Settings.getInstance().getGLCapabilities();
        caps.setDoubleBuffered(false);
        if (!GLDrawableFactory.getFactory(caps.getGLProfile()).canCreateGLPbuffer(null))
            throw new RuntimeException("No pbuffer support");

        glpBuffer = GLDrawableFactory.getFactory(caps.getGLProfile()).createGLPbuffer(null, caps, null, width, height, null);
        glpBuffer.addGLEventListener(renderer);
    }

    public GLPbuffer getGlpBuffer() {
        return glpBuffer;
    }

    @Override
    public void dispose() {
        glpBuffer.destroy();
        renderer = null;
        view = null;
    }

    @Override
    public void forceRepaint() {
        glpBuffer.display();
    }

    @Override
    public BufferedImage screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        glpBuffer.display();
        return renderer.getLastScreenshot();
    }

    /*********************************************************/

    /** Provide a reference to the View that renders into this canvas. */
    public View getView() {
        return view;
    }

    /**
     * Provide the actual renderer width for the open gl camera settings, which
     * is obtained after a resize event.
     */
    public int getRendererWidth() {
        return (renderer != null ? renderer.getWidth() : 0);
    }

    /**
     * Provide the actual renderer height for the open gl camera settings, which
     * is obtained after a resize event.
     */
    public int getRendererHeight() {
        return (renderer != null ? renderer.getHeight() : 0);
    }
    
    @Override
	public Renderer3d getRenderer(){
		return renderer;
	}
    
    public String getDebugInfo(){
		GL gl = getView().getCurrentGL();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Chosen GLCapabilities: " + glpBuffer.getChosenGLCapabilities() + "\n");
		sb.append("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR) + "\n");
		sb.append("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER) + "\n");
		sb.append("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION) + "\n");
		//sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
		return sb.toString();
	}
    public void removeKeyListener(KeyListener listener) {
    }
    public void removeMouseListener(MouseListener listener) {
    }
    public void removeMouseMotionListener(MouseMotionListener listener) {
    }
    public void removeMouseWheelListener(MouseWheelListener listener) {
    }
    public void addKeyListener(KeyListener listener) {
    }
    public void addMouseListener(MouseListener listener) {
    }
    public void addMouseMotionListener(MouseMotionListener listener) {
    }
    public void addMouseWheelListener(MouseWheelListener listener) {
    }

    /*********************************************************/

    protected View view;
    protected Renderer3d renderer;
    protected GLPbuffer glpBuffer;
}
