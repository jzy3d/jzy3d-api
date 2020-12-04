package org.jzy3d.plot3d.rendering.canvas;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jzy3d.chart.factories.NativeChartFactory;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import org.jzy3d.chart.Animator;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A Newt canvas wrapped in an AWT {@link Panel}.
 * 
 * Newt is supposed to be faster than any other canvas, either for AWT or Swing.
 * 
 * If a non AWT panel where required, follow the guidelines given in
 * {@link IScreenCanvas} documentation.
 */
public class CanvasNewtAwt extends Panel implements IScreenCanvas, INativeScreenCanvas {
    static Logger LOGGER = Logger.getLogger(CanvasNewtAwt.class);
    
    public CanvasNewtAwt(NativeChartFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci) {
        this(factory, scene, quality, glci, false, false);
    }

    public CanvasNewtAwt(NativeChartFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL) {
        window = GLWindow.create(glci);
        canvas = new NewtCanvasAWT(window);
        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view, traceGL, debugGL);
        window.addGLEventListener(renderer);
        
        if(quality.isPreserveViewportSize())
            setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE });
        
        // swing specific
        setFocusable(true);
        requestFocusInWindow();
        window.setAutoSwapBufferMode(quality.isAutoSwapBuffer());
        if (quality.isAnimated()) {
            animator = factory.newAnimator((ICanvas)window);//new Animator(window);
            animator.start();
        }

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
    }

    @Override
    public Animator getAnimation() {
    	return animator;
    }

    @Override
    public void setPixelScale(float[] scale) {
        //LOGGER.info("setting scale " + scale);
        if (scale != null)
            window.setSurfaceScale(scale);
        else
            window.setSurfaceScale(new float[] { 1f, 1f });
    }

    public GLWindow getWindow() {
        return window;
    }

    public NewtCanvasAWT getCanvas() {
        return canvas;
    }

    @Override
    public GLAutoDrawable getDrawable() {
        return window;
    }

    @Override
    public void dispose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (animator != null) {
                    animator.stop();
                }
                if (renderer != null) {
                    renderer.dispose(window);
                }
                canvas.destroy();
                window = null;
                renderer = null;
                view = null;
                animator = null;
            }
        }).start();
    }

    @Override
    public void display() {
        window.display();
    }

    @Override
    public void forceRepaint() {
        display();
    }

    @Override
    public TextureData screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        display();
        return renderer.getLastScreenshot();
    }

    @Override
    public void screenshot(File file) throws IOException {
        if (!file.getParentFile().exists())
            file.mkdirs();

        TextureData screen = screenshot();
        TextureIO.write(screen, file);
    }

    @Override
    public String getDebugInfo() {
    	GL gl = ((NativeDesktopPainter)getView().getPainter()).getCurrentGL(this);

        StringBuffer sb = new StringBuffer();
        sb.append("Chosen GLCapabilities: " + window.getChosenGLCapabilities() + "\n");
        sb.append("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR) + "\n");
        sb.append("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) + "\n");
        sb.append("GL_VERSION: " + gl.glGetString(GL.GL_VERSION) + "\n");
        // sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
        return sb.toString();
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

    /** Provide a reference to the View that renders into this canvas. */
    @Override
    public View getView() {
        return view;
    }

    /* */

    public synchronized void addKeyListener(KeyListener l) {
        getWindow().addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        getWindow().addMouseListener(l);
    }

    public void removeMouseListener(com.jogamp.newt.event.MouseListener l) {
        getWindow().removeMouseListener(l);
    }

    public void removeKeyListener(com.jogamp.newt.event.KeyListener l) {
        getWindow().removeKeyListener(l);
    }

    @Override
    public void addMouseController(Object o) {
        addMouseListener((MouseListener) o);
    }

    @Override
    public void addKeyController(Object o) {
        addKeyListener((KeyListener) o);
    }

    @Override
    public void removeMouseController(Object o) {
        removeMouseListener((MouseListener) o);
    }

    @Override
    public void removeKeyController(Object o) {
        removeKeyListener((KeyListener) o);
    }

    protected View view;
    protected Renderer3d renderer;
    protected Animator animator;
    protected GLWindow window;
    protected NewtCanvasAWT canvas;
    private static final long serialVersionUID = 8578690050666237742L;
}
