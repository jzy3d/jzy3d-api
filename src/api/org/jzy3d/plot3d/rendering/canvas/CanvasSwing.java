package org.jzy3d.plot3d.rendering.canvas;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.awt.GLJPanel;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.opengl.util.Animator;



/**
 * A {@link CanvasSwing} owns an OpenGL {@link Renderer3d} 
 * for handling GL events, and link it to the scene's {@link View}.
 * <p>
 * The {@link CanvasSwing} provides an animator that is stopped 
 * when it disposes.
 * 
 * @author Martin Pernollet
 */
public class CanvasSwing extends GLJPanel implements IScreenCanvas{
    public CanvasSwing(IChartComponentFactory factory, Scene scene, Quality quality){
        this(factory, scene, quality, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }
    
	/** Initialize a Canvas3d attached to a {@link Scene}, with a given rendering {@link Quality}.*/
	public CanvasSwing(IChartComponentFactory factory, Scene scene, Quality quality, GLCapabilitiesImmutable glci){
		super(glci);
		
		view     = scene.newView(this, quality);
		renderer = factory.newRenderer(view, false, false);
		addGLEventListener(renderer);
		
		// swing specific
		setFocusable(true);
		requestFocusInWindow();
		
        setAutoSwapBufferMode(quality.isAutoSwapBuffer());

        if(quality.isAnimated()){
            animator = new Animator(this);
            getAnimator().start();
        }
	}
	
	public void dispose(){
	    if(animator!=null)
            animator.stop();
	    if(renderer!=null)
	        renderer.dispose(this);
		renderer = null;
		view = null; 
	}
	
	
	/*********************************************************/

	/** Force repaint and ensure that GL2 rendering will occur in the GUI thread, wherever the caller stands.*/
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
			repaint(); 
		}
	}
	
	public BufferedImage screenshot(){
		renderer.nextDisplayUpdateScreenshot();
		display();
		return renderer.getLastScreenshot();
	}
	
	/* */
	
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
	
	@Override
	public Renderer3d getRenderer(){
		return renderer;
	}
	
	public String getDebugInfo(){
		GL gl = getView().getCurrentGL();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Chosen GLCapabilities: " + getChosenGLCapabilities() + "\n");
		sb.append("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR) + "\n");
		sb.append("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER) + "\n");
		sb.append("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION) + "\n");
		//sb.append("INIT GL IS: " + gl.getClass().getName() + "\n");
		return sb.toString();
	}
	
	/* */
	
	protected View       view;
	protected Renderer3d renderer;
	protected Animator   animator;

	private static final long serialVersionUID = 980088854683562436L;
}
