package org.jzy3d.plot3d.rendering.canvas;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.GLProfile;

import org.jzy3d.factories.JzyFactories;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class OffscreenCanvas implements ICanvas {
    public OffscreenCanvas(Scene scene, Quality quality, GLProfile profile, int width, int height) {
        view = scene.newView(this, quality);
        renderer = JzyFactories.renderer3d.getInstance(view, false, false);

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

    public void removeKeyListener(KeyListener listener) {
        throw new NotImplementedException();
    }

    public void removeMouseListener(MouseListener listener) {
        throw new NotImplementedException();
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        throw new NotImplementedException();
    }

    public void removeMouseWheelListener(MouseWheelListener listener) {
        throw new NotImplementedException();
    }

    public void addKeyListener(KeyListener listener) {
        throw new NotImplementedException();
    }

    public void addMouseListener(MouseListener listener) {
        throw new NotImplementedException();
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        throw new NotImplementedException();
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        throw new NotImplementedException();
    }

    /*********************************************************/

    protected View view;
    protected Renderer3d renderer;
    protected GLPbuffer glpBuffer;
}
