package org.jzy3d.plot3d.rendering.canvas;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

import jgl.GLCanvas;
import jgl.context.gl_pointer;

public class EmulGLCanvas extends GLCanvas implements IScreenCanvas {
	Logger log = Logger.getLogger(EmulGLCanvas.class);

	private static final long serialVersionUID = 980088854683562436L;
	
	/**
	 * if true 
	 * if false : call full component.resize to force resize + view.render + glFlush + swap image
	 */
	public static boolean TO_BE_CHOOSEN_REPAINT_WITH_FLUSH = false;
	
	/** set to TRUE to overlay performance info on top left corner */
	protected boolean profileDisplayMethod = false;
	/** set to TRUE to show in console events of the component (to debug GLUT) */
	protected boolean debugEvents = false;


	
	protected View view;
	protected EmulGLPainter painter;
	protected IAnimator animator;

	public EmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
		super();
		view = scene.newView(this, quality);
		painter = (EmulGLPainter) view.getPainter();
		init();
		animator = factory.getPainterFactory().newAnimator(this);

		// FROM NATIVE
		//
		//renderer = factory.newRenderer(view, traceGL, debugGL);
        //addGLEventListener(renderer);
	}
	
	@Override
	public IAnimator getAnimation() {
		return animator;
	}


	@Override
	public void processEvent(AWTEvent e) {
		if (debugEvents && shouldPrintEvent(e)) {
			System.out.println("EMulGLCanvas.processEvent:" + e);
		}
		super.processEvent(e);
	}

	protected boolean shouldPrintEvent(AWTEvent e) {
		return !(e.getID() == MouseEvent.MOUSE_MOVED);
	}

	// ******************* VIEW ******************* //

	// Equivalent to registering a Renderer3d
	protected void init() {
		updatePainterWithGL(); // painter can call this canvas GL

		int width = getWidth();
		int height = getHeight();
		
		myUT.glutInitWindowSize(width, height);
		myUT.glutInitWindowPosition(getX(), getY());

		myUT.glutCreateWindow(this); // this canvas GLUT register this canvas
		myUT.glutDisplayFunc("doDisplay"); // on this canvas GLUT register this display method
		myUT.glutReshapeFunc("doReshape"); // on ComponentEvent.RESIZE TODO: double render car GLUT.resize invoque
											// reshape + display
		//myUT.glutMotionFunc("doMotion"); // TODO : double render car GLUT.motion invoque display PUIS le listener
		// TODO : RESIZED semble emis par le composant quand on fait un mouse dragg!!
		
		
		myUT.glutMainLoop();

		// CLARIFIER comment le composant se met à jour :
		// en autonome sur paint (quel cycle de vie?)
		// sur demande quand on fait "updateView" dans les mouse / thread controller etc
		// pourquoi est il nécessaire de le faire pendant mouse dragged?

		view.init();
	}


	/**
	 * Method is synchronized : 
	 * <ul>
	 * <li>to avoid multiple concurrent calls to doDisplay which might make jGL get
	 * crazy with GL state consistency : GL states must be consistent during a
	 * complete rendering pass, and should not be modified by a second rendering
	 * pass in the middle of the first one.
	 * <li>to ensure rendered image is painted in GLCanvas immediately after being
	 * generated
	 * </ul>
	 */
	public synchronized void doDisplay() {
		TicToc t = new TicToc();
		
		if (view != null) {

			if (profileDisplayMethod) {
				resetCountGLBegin();
				t.tic();
			}

			view.clear();
			view.render();
			
			// Ask opengl to provide an image for display
			myGL.glFlush();
			
			// Ask the GLCanvas to SWAP current image with 
			// the latest built with glFlush
			repaint(); 
			
			
			// checkAlphaChannelOfColorBuffer(painter);

			if (profileDisplayMethod) {
				// printCountGLBegin();
				//t.tocShow("Rendering took");
				t.toc();

				postRenderProfiling(t);
			
			}

			
			kDisplay++;
		}
	}

	protected void postRenderProfiling(TicToc t) {
		int x = 30;
		int y = 12;
		
		postRenderString("FrameID    : " + kDisplay, x, y, Color.BLACK);
		postRenderString("Render in  : " + t.elapsedMilisecond() + "ms", x, y*2, Color.BLACK);
		postRenderString("Surf size  : " + view.getScene().getGraph().getDecomposition().size(), x, y*3, Color.BLACK);
		postRenderString("Frame Size : " + getWidth() + "x" + getHeight(), x, y*4, Color.BLACK);
	}
	
	Font profileFont = new Font("Arial", Font.PLAIN, 12);
	int kDisplay = 0;

	
	/** Draw a 2d text at the given position */
	void postRenderString(String message, int x, int y, Color color){
		painter.getGL().appendTextToDraw(profileFont, message, x, y, color.r, color.g, color.b);
	}


	// TODO : GLUT.processComponentEvent is calling reshape THEN display, so no need
	// to call view.render here (double rendering otherwise).
	public synchronized void doReshape(int w, int h) {
		// System.out.println("doReshape " + w);
		myUT.glutInitWindowSize(w, h);

		if (view != null) {
			view.markDimensionDirty();
			// then doDisplay will be called by glutReshapeFunc
		}

	}

	public synchronized void doMotion(int x, int y) {
		doDisplay();
		// repaint();
		//paint
		System.out.println("EmulGLCanvas.doMotion!" + kDisplay);
		
	}


	/* *************************************************** */

	/**
	 * Can be used to update image if camera has changed position. (usually called
	 * by {@link View#shoot()})
	 * 
	 * FIXME : Warning if this is invoked by a thread external to AWT, maybe this
	 * will require to redraw GL while GL is already used by AWT.
	 */
	@Override
	public void forceRepaint() {
		// This makes GLUT invoke the myReshape function

		// SHOULD NOT BE CALLED IF ANIMATOR IS ACTIVE
		if(TO_BE_CHOOSEN_REPAINT_WITH_FLUSH)
			painter.getGL().glFlush();
		else
			processEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
		// INTRODUCE A UNDESIRED RESIZE EVENT (WE ARE NOT RESHAPING VIEWPORT 
		// WAS JUST USED TO FORCE REPAINT
		

		// This triggers copy of newly generated picture to the GLCanvas
		repaint();
	}

	protected void updatePainterWithGL() {
		if(view!=null && view.getPainter()!=null && getGL()!=null) {
			EmulGLPainter painter = (EmulGLPainter) view.getPainter();
			painter.setGL(getGL());
		}
	}

	/* ******************************************************* */

	@Override
	public View getView() {
		return view;
	}

	@Override
	public int getRendererWidth() {
		return this.getWidth();
	}

	@Override
	public int getRendererHeight() {
		return this.getHeight();
	}

	@Override
	public BufferedImage screenshot() {
		EmulGLPainter painter = (EmulGLPainter) getView().getPainter();
		return (BufferedImage) painter.getGL().getRenderedImage();
	}

	@Override
	public void screenshot(File file) throws IOException {
		if (!file.getParentFile().exists())
			file.mkdirs();

		ImageIO.write(screenshot(), "png", file);
	}

	@Override
	public void dispose() {

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
	public void removeMouseController(Object o) {
		removeMouseListener((java.awt.event.MouseListener) o);
		if (o instanceof MouseWheelListener)
			removeMouseWheelListener((MouseWheelListener) o);
		if (o instanceof MouseMotionListener)
			removeMouseMotionListener((MouseMotionListener) o);
	}

	@Override
	public void addKeyController(Object o) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeKeyController(Object o) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getDebugInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPixelScale(float[] scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display() {
		forceRepaint();
	}
	
	

	/* ******************* DEBUG ********************* */

	public boolean isProfileDisplayMethod() {
		return profileDisplayMethod;
	}
	public void setProfileDisplayMethod(boolean profileDisplayMethod) {
		this.profileDisplayMethod = profileDisplayMethod;
	}
	
	protected void checkAlphaChannelOfColorBuffer(EmulGLPainter painter) {
		int[] colorBuffer = painter.getGL().getContext().ColorBuffer.Buffer;

		for (int i = 0; i < colorBuffer.length; i++) {
			Color c = jGLUtil.glIntToColor(colorBuffer[i]);

			if (c.a == 0) {
				// check gl_render_pixel.debug
				// il y a des pixel qui ont un alpha 127
				System.err.println("alpha " + c.a + " at " + i);
			}
		}

		System.out.println("AlphaEnable:" + painter.getGL().getContext().ColorBuffer.AlphaEnable);
		System.out.println("AlphaFunc:" + painter.getGL().getContext().ColorBuffer.AlphaFunc);
	}
	
	protected void printCountGLBegin() {
		gl_pointer pointer = painter.getGL().getPointer();
		System.out.println("#gl_begin() : " + pointer.geometry.countBegin);
	}

	protected void resetCountGLBegin() {
		painter.getGL().getPointer().geometry.countBegin = 0;
	}

}
