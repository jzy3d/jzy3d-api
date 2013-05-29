package org.jzy3d.plot3d.rendering.canvas;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.jzy3d.bridge.newt.controllers.keyboard.NewtToAWTKeyListener;
import org.jzy3d.bridge.newt.controllers.mouse.NewtToAWTMouseListener;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.TextureData;

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
	public CanvasAWT(IChartComponentFactory factory, Scene scene,
			Quality quality) {
		this(factory, scene, quality, org.jzy3d.global.Settings.getInstance()
				.getGLCapabilities());
	}

	public CanvasAWT(IChartComponentFactory factory, Scene scene,
			Quality quality, GLCapabilitiesImmutable glci) {
		this(factory, scene, quality, glci, false, false);
	}

	/**
	 * Initialize a {@link CanvasAWT} attached to a {@link Scene}, with a given
	 * rendering {@link Quality}.
	 */
	public CanvasAWT(IChartComponentFactory factory, Scene scene,
			Quality quality, GLCapabilitiesImmutable glci, boolean traceGL,
			boolean debugGL) {
		super(glci);

		view = scene.newView(this, quality);
		renderer = factory.newRenderer(view, traceGL, debugGL);
		addGLEventListener(renderer);

		setAutoSwapBufferMode(quality.isAutoSwapBuffer());

		if (quality.isAnimated()) {
			animator = new Animator(this);
			getAnimator().start();
		}
	}

	public void dispose() {
		new Thread(new Runnable() {
			public void run() {
				// System.err.println("stopping canvas animator");
				if (animator != null)
					animator.stop();
				renderer = null;
				view = null;
			}
		}).start();
	}

	public Renderer3d getRenderer() {
		return renderer;
	}

	public String getDebugInfo() {
		GL gl = getView().getCurrentGL();

		StringBuffer sb = new StringBuffer();
		sb.append("Chosen GLCapabilities: " + getChosenGLCapabilities() + "\n");
		sb.append("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR) + "\n");
		sb.append("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER) + "\n");
		sb.append("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION) + "\n");
		// sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
		return sb.toString();
	}

	/*********************************************************/

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

	public TextureData screenshot() {
		renderer.nextDisplayUpdateScreenshot();
		display();
		return renderer.getLastScreenshot();
	}

	public void triggerMouseEvent(java.awt.event.MouseEvent e) {
		// System.out.println("trigger mouse " + e);
		processMouseEvent(e);

	}

	public void triggerMouseMotionEvent(java.awt.event.MouseEvent e) {
		// System.out.println("trigger mouse motion " + e);
		processMouseMotionEvent(e);
	}

	public void triggerMouseWheelEvent(java.awt.event.MouseWheelEvent e) {
		// System.out.println("trigger mouse wheel " + e);
		processMouseWheelEvent(e);
	}

	@Override
    public void addMouseListener(Object o) {
	    addMouseListener((java.awt.event.MouseListener)o);
    }

    @Override
    public void addKeyListener(Object o) {
        addKeyListener((java.awt.event.KeyListener)o);
    }

	@Override
	public GLDrawable getDrawable() {
		return this;
	}

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
	public void addMouseListener(MouseListener listener) {
		addMouseListener(new NewtToAWTMouseListener(null, listener));
		addMouseMotionListener(new NewtToAWTMouseListener(null, listener));
		addMouseWheelListener(new NewtToAWTMouseListener(null, listener));
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

	protected View view;
	protected Renderer3d renderer;
	protected Animator animator;
	private static final long serialVersionUID = 980088854683562436L;


}
