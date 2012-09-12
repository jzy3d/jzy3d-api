package org.jzy3d.plot3d.rendering.canvas;

import java.awt.image.BufferedImage;

import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.awt.GLCanvas;

import org.jzy3d.factories.JzyFactories;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.util.Animator;



/**
 * A {@link ICanvas} embed a {@link Renderer3d} for handling GL events,
 * a mouse and keyboard controllers for setting the viewpoint
 * (inheriting {@link ViewPointController}), and a {@link Scene} storing 
 * the actual {@link Graph} and {@link View}s.
 * <p>
 * The {@link CanvasAWT} allows getting rid of GL and AWT events by offering a 
 * direct registration of a {@link View} from the referenced Scene. 
 * 
 * The {@link View} may be retrieved in order to attach it to a ViewController,
 * either one of those held by the current canvas, or an other controller
 * (autonomous, or held by another Canvas).
 * 
 * The {@link CanvasAWT} silently adds a {@link Renderer3d} as its GLEventListener and
 * hide its management for the user.
 * <p>
 * 
 * The {@link CanvasAWT} last provide an animator that is explicitely stopped 
 * when {@link CanvasAWT} disposes.
 * This offers the alternative of repaint-on-demand-model based on
 * Controllers, and repaint-continuously model based on the Animator.
 * 
 * @author Martin Pernollet
 */
public class CanvasAWT extends GLCanvas implements IScreenCanvas {
	public CanvasAWT(Scene scene, Quality quality){
		this(scene, quality, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
	}
	
	public CanvasAWT(Scene scene, Quality quality, GLCapabilitiesImmutable glci){
	    this(scene, quality, glci, false, false);
	}
	
	/** Initialize a {@link CanvasAWT} attached to a {@link Scene}, with a given rendering {@link Quality}.*/
	public CanvasAWT(Scene scene, Quality quality, GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL){
		super(glci);
		
		view     = scene.newView(this, quality);
		renderer = JzyFactories.renderer3d.getInstance(view, traceGL, debugGL);
		addGLEventListener(renderer);
		
        setAutoSwapBufferMode(quality.isAutoSwapBuffer());
        
		if(quality.isAnimated()){
    		animator = new Animator(this);
            getAnimator().start();
		}
	}	
		
	public void dispose(){
		new Thread(new Runnable() {
            public void run() {
            	//System.err.println("stopping canvas animator");
                animator.stop();
				renderer = null;
				view = null;
            }
          }).start();
	}
	
	/*********************************************************/
	
	public void forceRepaint(){
		if(true){
			// -- Method1 --
			// Display() is required to use the GLCanvas procedure and to ensure that GL2 rendering occurs in the 
			// GUI thread.
			// Actually it seems to be a bad idea, because this call implies a rendering out of the excepted GL2 thread,
			// which:
			//  - is slower than rendering in GL2 Thread
			//  - throws java.lang.InterruptedException when rendering occurs while closing the window
			display(); 
		}
		else{
			// -- Method2 --
			// Composite.repaint() is required with post/pre rendering, for triggering PostRenderer rendering
			// at each frame (instead of ). The counterpart is that OpenGL2 rendering will occurs in the caller thread
			// and thus in the thread where the shoot() method was invoked (such as AWT if shoot() is triggered
			// by a mouse event.
			//
			// Implies blinking with some JRE version (6.18, 6.17) but not with some other (6.5)
			repaint(); 
		}
	}
		
	public BufferedImage screenshot(){
		renderer.nextDisplayUpdateScreenshot();
		display();
		return renderer.getLastScreenshot();
	}
	
	/*********************************************************/
	
	/** Provide a reference to the View that renders into this canvas.*/
	public View getView(){
		return view;
	}
	
	/** Provide the actual renderer width for the open gl camera settings, 
	 * which is obtained after a resize event.*/
	public int getRendererWidth(){
		return (renderer!=null?renderer.getWidth():0);
	}
	
	/** Provide the actual renderer height for the open gl camera settings, 
	 * which is obtained after a resize event.*/
	public int getRendererHeight(){
		return (renderer!=null?renderer.getHeight():0);
	}
	
	protected View       view;
	protected Renderer3d renderer;
	protected Animator   animator;
	private static final long serialVersionUID = 980088854683562436L;
}
