package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class OffscreenCanvas implements ICanvas {
    public OffscreenCanvas(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilities capabilities, int width, int height) {
        this(factory, scene, quality, capabilities, width, height, false, false);
    }

    public OffscreenCanvas(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilities capabilities, int width, int height, boolean traceGL, boolean debugGL) {
        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view, traceGL, debugGL);
        initGLPBuffer(capabilities, width, height);
    }

    protected void initGLPBuffer(GLCapabilities capabilities, int width, int height) {
        GLProfile profile = capabilities.getGLProfile();
        capabilities.setDoubleBuffered(false);
        if (!GLDrawableFactory.getFactory(profile).canCreateGLPbuffer(null))
            throw new RuntimeException("No pbuffer support");
        GLDrawableFactory factory = GLDrawableFactory.getFactory(profile);
        glpBuffer = factory.createGLPbuffer(null, capabilities, null, width, height, null);
        glpBuffer.addGLEventListener(renderer);
    }

    protected void initGLPBuffer(int width, int height) {
        GLCapabilities caps = org.jzy3d.chart.Settings.getInstance().getGLCapabilities();
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
    public GLDrawable getDrawable() {
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
    public TextureData screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        glpBuffer.display();
        return renderer.getLastScreenshot();
    }
    
    @Override
    public TextureData screenshot(File file) throws IOException {
        TextureData screen = screenshot();
        TextureIO.write(screen, file);
        return screen;
    }

    /** Provide a reference to the View that renders into this canvas. */
    @Override
    public View getView() {
        return view;
    }

    /**
     * Provide the actual renderer width for the open gl camera settings, which
     * is obtained after a resize event.
     */
    @Override
    public int getRendererWidth() {
        return (renderer != null ? renderer.getWidth() : 0);
    }

    /**
     * Provide the actual renderer height for the open gl camera settings, which
     * is obtained after a resize event.
     */
    @Override
    public int getRendererHeight() {
        return (renderer != null ? renderer.getHeight() : 0);
    }

    @Override
    public Renderer3d getRenderer() {
        return renderer;
    }

    @Override
    public String getDebugInfo() {
        GL gl = getView().getCurrentGL();

        StringBuffer sb = new StringBuffer();
        sb.append("Chosen GLCapabilities: " + glpBuffer.getChosenGLCapabilities() + "\n");
        sb.append("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR) + "\n");
        sb.append("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER) + "\n");
        sb.append("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION) + "\n");
        // sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
        return sb.toString();
    }

    @Override
    public void addMouseController(Object o) {}
    @Override
    public void addKeyController(Object o) {}
    @Override
    public void removeMouseController(Object o) {}
    @Override
    public void removeKeyController(Object o) {}

    protected View view;
    protected Renderer3d renderer;
    protected GLPbuffer glpBuffer;
    
}
