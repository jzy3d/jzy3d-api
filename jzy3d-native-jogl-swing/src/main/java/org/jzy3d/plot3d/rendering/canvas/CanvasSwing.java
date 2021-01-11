package org.jzy3d.plot3d.rendering.canvas;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.NativeChartFactory;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * @author Martin Pernollet
 */
public class CanvasSwing extends GLJPanel implements IScreenCanvas, INativeCanvas {
	public CanvasSwing(NativeChartFactory factory, Scene scene,
			Quality quality) {
		this(factory, scene, quality, org.jzy3d.chart.Settings.getInstance()
				.getGLCapabilities());
	}

	/**
	 * Initialize a Canvas3d attached to a {@link Scene}, with a given rendering
	 * {@link Quality}.
	 */
	public CanvasSwing(NativeChartFactory factory, Scene scene,
			Quality quality, GLCapabilitiesImmutable glci) {
		this(factory, scene, quality, glci, false, false);
	}

	/**
	 * Initialize a Canvas3d attached to a {@link Scene}, with a given rendering
	 * {@link Quality}.
	 */
	public CanvasSwing(NativeChartFactory factory, Scene scene,
			Quality quality, GLCapabilitiesImmutable glci, boolean traceGL,
			boolean debugGL) {
		super(glci);

		view = scene.newView(this, quality);
		renderer = factory.newRenderer3D(view, traceGL, debugGL);
		addGLEventListener(renderer);

		// swing specific
		setFocusable(true);
		requestFocusInWindow();

		setAutoSwapBufferMode(quality.isAutoSwapBuffer());

		animator = factory.getPainterFactory().newAnimator(this);
		if (quality.isAnimated()) {
			animator.start();
		}
		
		if(quality.isPreserveViewportSize())
	        setPixelScale(new float[] { ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE });

	}
	
	@Override
	public IAnimator getAnimation() {
		return animator;
	}

	@Override
    public void dispose() {
		if (animator != null)
			animator.stop();
		if (renderer != null)
			renderer.dispose(this);
		renderer = null;
		view = null;
	}

	/**
	 * Force repaint and ensure that GL2 rendering will occur in the GUI thread,
	 * wherever the caller stands.
	 */
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
			repaint();
		}
	}

	
	@Override
    public void screenshot(File file) throws IOException {
        if (!file.getParentFile().exists())
            file.mkdirs();

        TextureData screen = screenshot();
        TextureIO.write(screen, file);
    }
	
	@Override
	public TextureData screenshot() {
		renderer.nextDisplayUpdateScreenshot();
		display();
		return renderer.getLastScreenshot();
	}

	/* */

	@Override
	public GLAutoDrawable getDrawable() {
		return this;
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
		GL gl = ((NativeDesktopPainter)getView().getPainter()).getCurrentGL(this);

		StringBuffer sb = new StringBuffer();
		sb.append("Chosen GLCapabilities: " + getChosenGLCapabilities() + "\n");
		sb.append("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR) + "\n");
		sb.append("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER) + "\n");
		sb.append("GL_VERSION: " + gl.glGetString(GL.GL_VERSION) + "\n");
		// sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
		return sb.toString();
	}

/*	@Override
	public void addMouseListener(MouseListener listener) {
		addMouseListener(new NewtToAWTMouseListener(null, listener));
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		// TODO
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		addKeyListener(new NewtToAWTKeyListener(null, listener));

	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		// TODO
	}
*/
	
	
	
	/* */

	protected View view;
	protected Renderer3d renderer;
	protected IAnimator animator;

	private static final long serialVersionUID = 980088854683562436L;

    @Override
    public void addMouseController(Object o) {
        addMouseListener((MouseListener)o);
    }

    @Override
    public void addKeyController(Object o) {
        addKeyListener((KeyListener)o);
    }

    @Override
    public void removeMouseController(Object o) {
        removeMouseListener((MouseListener)o);
    }

    @Override
    public void removeKeyController(Object o) {
        removeKeyListener((KeyListener)o);
    }

    @Override
    public void setPixelScale(float[] scale) {
        setSurfaceScale(scale);
    }
}
