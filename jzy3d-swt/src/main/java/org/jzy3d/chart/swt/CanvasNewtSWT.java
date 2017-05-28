package org.jzy3d.chart.swt;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A Newt canvas wrapped in an AWT 
 * 
 * Newt is supposed to be faster than any other canvas, either for AWT or Swing.
 * 
 * If a non AWT panel where required, follow the guidelines given in
 * {@link IScreenCanvas} documentation.
 */
public class CanvasNewtSWT extends Composite implements IScreenCanvas {
    static Logger LOGGER = Logger.getLogger(CanvasNewtSWT.class);
    
    public CanvasNewtSWT(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci) {
        this(factory, scene, quality, glci, false, false);
    }

    public CanvasNewtSWT(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL) {
    	super(((SWTChartComponentFactory)factory).getComposite(), SWT.NONE);
    	this.setLayout(new FillLayout());
        window = GLWindow.create(glci);
        canvas = new NewtCanvasSWT(this, SWT.NONE,window);
        view = scene.newView(this, quality);
        renderer = factory.newRenderer(view, traceGL, debugGL);
        window.addGLEventListener(renderer);
        
        if(quality.isPreserveViewportSize())
            setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE });
        
        window.setAutoSwapBufferMode(quality.isAutoSwapBuffer());
        if (quality.isAnimated()) {
            animator = new Animator(window);
            getAnimator().start();
        }
        
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e)
            {
                dispose();
            }
        });

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

    public NewtCanvasSWT getCanvas() {
        return canvas;
    }

    @Override
    public GLDrawable getDrawable() {
        return window;
    }

    @Override
    public void dispose() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (animator != null && animator.isStarted()) {
                    animator.stop();
                }
                if (renderer != null) {
                    renderer.dispose(window);
                }
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
    public GLAnimatorControl getAnimator() {
        return window.getAnimator();
    }

    @Override
    public TextureData screenshot() {
        renderer.nextDisplayUpdateScreenshot();
        display();
        return renderer.getLastScreenshot();
    }

    @Override
    public TextureData screenshot(File file) throws IOException {
        TextureData screen = screenshot();
        TextureIO.write(screen, file);
        return screen;
    }

    @Override
    public String getDebugInfo() {
        GL gl = getView().getCurrentGL();

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
    protected NewtCanvasSWT canvas;
}
