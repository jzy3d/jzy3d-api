package org.jzy3d.plot3d.rendering.canvas;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * {@link CanvasAWT} is a base implementation that primarily allows to integrate
 * a Jzy3d chart in an AWT application.
 * 
 * Relying on JOGL's {@link GLPanel}, this canvas can actually be used in AWT,
 * Swing, as well as SWT through
 * <code>org.jzy3d.bridge.swt.Bridge.adapt(swt,awt)</code>.
 * 
 * 
 * @author Martin Pernollet
 */
public class CanvasAWT extends GLCanvas implements IScreenCanvas {
    public CanvasAWT(IChartComponentFactory factory, Scene scene, Quality quality) {
        this(factory, scene, quality, org.jzy3d.chart.Settings.getInstance().getGLCapabilities());
    }

    public CanvasAWT(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci) {
        this(factory, scene, quality, glci, false, false);
    }

    /**
     * Initialize a {@link CanvasAWT} attached to a {@link Scene}, with a given
     * rendering {@link Quality}.
     */
    public CanvasAWT(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL) {
        super(glci);

        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view, traceGL, debugGL);
        addGLEventListener(renderer);

        setAutoSwapBufferMode(quality.isAutoSwapBuffer());

        if (quality.isAnimated()) {
            animator = new Animator(this);
            getAnimator().start();
        }

        if (quality.isPreserveViewportSize())
            setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE });
    }

    public void setPixelScale(float[] scale) {
        if (scale != null)
            setSurfaceScale(scale);
        else
            setSurfaceScale(new float[] { 1f, 1f });
    }

    @Override
    public void dispose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (animator != null)
                    animator.stop();
                renderer = null;
                view = null;
            }
        }).start();
    }

    @Override
    public String getDebugInfo() {
        GL gl = getView().getCurrentGL();

        StringBuffer sb = new StringBuffer();
        sb.append("Chosen GLCapabilities: " + getChosenGLCapabilities() + "\n");
        sb.append("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR) + "\n");
        sb.append("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) + "\n");
        sb.append("GL_VERSION: " + gl.glGetString(GL.GL_VERSION) + "\n");
        // sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
        return sb.toString();
    }

    @Override
    public void forceRepaint() {
        if (true) {
            // -- Method1 --
            // Display() is required to use the GLCanvas procedure and to ensure
            // that GL2 rendering occurs in the
            // GUI thread.
            // Actually it seems to be a bad idea, because this call implies a
            // rendering out of the excepted GL2 thread,
            // which:
            // - is slower than rendering in GL2 Thread
            // - throws java.lang.InterruptedException when rendering occurs
            // while closing the window
            display();
        } else {
            // -- Method2 --
            // Composite.repaint() is required with post/pre rendering, for
            // triggering PostRenderer rendering
            // at each frame (instead of ). The counterpart is that OpenGL2
            // rendering will occurs in the caller thread
            // and thus in the thread where the shoot() method was invoked (such
            // as AWT if shoot() is triggered
            // by a mouse event.
            //
            // Implies blinking with some JRE version (6.18, 6.17) but not with
            // some other (6.5)
            repaint();
        }
    }

    @Override
    public TextureData screenshot(File file) throws IOException {
        TextureData screen = screenshot();
        TextureIO.write(screen, file);
        return screen;
    }

    @Override
    public TextureData screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        display();
        return renderer.getLastScreenshot();
    }

    public void triggerMouseEvent(java.awt.event.MouseEvent e) {
        processMouseEvent(e);
    }

    public void triggerMouseMotionEvent(java.awt.event.MouseEvent e) {
        processMouseMotionEvent(e);
    }

    public void triggerMouseWheelEvent(java.awt.event.MouseWheelEvent e) {
        processMouseWheelEvent(e);
    }

    @Override
    public void addMouseController(Object o) {
        addMouseListener((java.awt.event.MouseListener) o);
        if (o instanceof MouseWheelListener)
            addMouseWheelListener((MouseWheelListener) o);
        if (o instanceof MouseMotionListener)
            addMouseMotionListener((MouseMotionListener) o);
    }

    @Override
    public void addKeyController(Object o) {
        addKeyListener((java.awt.event.KeyListener) o);
    }

    @Override
    public void removeMouseController(Object o) {
        removeMouseListener((java.awt.event.MouseListener) o);
        if (o instanceof MouseWheelListener)
            removeMouseWheelListener((MouseWheelListener) o);
        if (o instanceof MouseMotionListener)
            removeMouseMotionListener((MouseMotionListener) o);
    }

    @Override
    public void removeKeyController(Object o) {
        removeKeyListener((java.awt.event.KeyListener) o);
    }

    @Override
    public GLDrawable getDrawable() {
        return this;
    }

    @Override
    public Renderer3d getRenderer() {
        return renderer;
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

    protected View view;
    protected Renderer3d renderer;
    protected Animator animator;
    private static final long serialVersionUID = 980088854683562436L;
}
