package org.jzy3d.plot3d.rendering.view;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPipelineFactory;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;

import com.jogamp.opengl.util.awt.Screenshot;



/**
 * The {@link Renderer3d} object is a {@link GLEventListener},
 * that makes openGL calls necessary to initialize and render 
 * a {@link Scene} for an {@link ICanvas}. 
 *
 * One can activate OpenGl errors in console by setting debugGL to true in the constructor
 * One can activate OpenGl feedback in console by setting traceGL to true in the constructor
 *
 * @author Martin Pernollet
 */
public class Renderer3d implements GLEventListener{
	
	/** Initialize a Renderer attached to the given View.*/
	public Renderer3d(View view){
		this(view, false, false);
	}
	
	/** Initialize a Renderer attached to the given View, and activate GL trace and errors to console.*/
	public Renderer3d(View view, boolean traceGL, boolean debugGL){
		this.view = view;
		this.traceGL = traceGL;
		this.debugGL = debugGL;
		this.glu = new GLU();
	}
	

	/***************************************************************/
	
	/** 
	 * Called when the GLDrawable is rendered for the first time.
	 * When one calls Scene.init() function, this function is called and makes 
	 * the OpenGL buffers initialization.
	 * 
	 * Note: in this implementation, GL Exceptions are not triggered.
	 * To do so, make te following call at the beginning of the init()
	 * body:
	 * <code>
	 * canvas.setGL( new DebugGL(canvas.getGL()) );
	 * </code>
	 */
	@Override
	public void init(GLAutoDrawable canvas){
		//System.err.println("init");
		if(canvas!=null && canvas.getGL()!=null && canvas.getGL().getGL2()!=null && view!=null){
			if(debugGL) 
			    canvas.getGL().getContext().setGL( GLPipelineFactory.create("javax.media.opengl.Debug", null, canvas.getGL(), null) );
	        if(traceGL) 
	            canvas.getGL().getContext().setGL( GLPipelineFactory.create("javax.media.opengl.Trace", null, canvas.getGL(), new Object[] { System.err } ) );
		    
	        view.init(canvas.getGL().getGL2());
		}
	}
	/** 
	 * Called when the GLDrawable requires a rendering. All call to
	 * rendering methods should appear here.
	 */
	@Override
	public void display(GLAutoDrawable canvas){
		GL2 gl = canvas.getGL().getGL2();

		if(view!=null){
		    view.clear(gl);
			view.render(gl, glu);

			if(doScreenshotAtNextDisplay){
	            image = Screenshot.readToBufferedImage(width, height);
	            doScreenshotAtNextDisplay = false;
	        }
		}
	}
	
	/** Called when the GLDrawable is resized.*/
	@Override
	public void reshape(GLAutoDrawable canvas, int x, int y, int width, int height){
		//System.err.println("reshape with " + width + " " + height);
		this.width  = width;
		this.height = height;
		
		if(view != null){
	        view.dimensionDirty = true;
	        
	        if(canvas!=null){
	            //GL gl1 = canvas.getGL();
    	        GL2 gl = canvas.getGL().getGL2();
    			view.clear(gl);
    			view.render(gl, glu);
	        }
		}
	}
	
	//protected boolean first = true;
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		view = null;
		glu  = null;
	}
	
	/***************************************************************/	

	public void nextDisplayUpdateScreenshot(){
		doScreenshotAtNextDisplay = true;
	}
	
	public BufferedImage getLastScreenshot(){
		return image;
	}
	
	/***************************************************************/	
	
	/** Return the width that was given after the last resize event.*/
	public int getWidth(){
		return width;
	}
	
	/** Return the height that was given after the last resize event.*/
	public int getHeight(){
		return height;
	}
	
	/***************************************************************/	
	
	protected GLU  glu;
	
	protected View    	  view;
	protected int     	  width     = 0; 
	protected int     	  height    = 0;
	protected boolean 	  doScreenshotAtNextDisplay = false;
	protected BufferedImage image = null;
	protected boolean 	  traceGL = false;
	protected boolean 	  debugGL = false;
	
}
