package org.jzy3d.plot3d.rendering.view;

import java.awt.Rectangle;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;


/** A {@link AbstractViewport} describes an element that occupies the whole 
 * rendering {@link ICanvas} or only a vertical slice of it.
 * 
 * The {@link AbstractViewport} also provides a utility function for debugging the slices, that is
 * the ability to display a 10*10 grid for checking the space occupied by the actual
 * viewport definition.
 * 
 * @author Martin Pernollet
 */
abstract class AbstractViewport {
	/** Set the status of the stretching mode (activated or not). Stretching consist in
	 * displaying the projection into the full screen slice (i.e. viewport).
	 * 
	 * @param status
	 */
	public void setStretchToFill(boolean status){
		stretchToFill = status;
	}
	
	public boolean getStretchToFill(){
		return stretchToFill;
	}
	
	/** Set the view port (size of the renderer).
	 * 
	 * @param width the width of the target window.
	 * @param height the height of the target window.
	 */
	public void setViewPort(int width, int height){
		setViewPort(width, height, 0, 1);
	}
	
	
	/** Set the view port (size of the renderer).
	 * 
	 * @param width the width of the target window.
	 * @param height the height of the target window.
	 * @param left the width's ratio where this subscreen starts in the target window.
	 * @param right the width's ratio where this subscreen stops in the target window.
	 * 
	 * @throws an IllegalArgumentException if right is not greater than left.
	 */
	public void setViewPort(int width, int height, float left, float right){
		if(left>=right)
			throw new IllegalArgumentException("left must be inferior to right");
		
		this.screenWidth  = (int)((right-left)*(float)width);
		this.screenHeight = height;
		this.screenLeft   = (int)(left*width);
		this.screenRight  = screenLeft + screenWidth;
	}
	
	public void setViewPort(ViewPort viewport){
		this.screenWidth  = viewport.getWidth();
		this.screenHeight = viewport.getHeight();
		this.screenLeft   = viewport.getX();
		this.screenRight  = viewport.getY();
	}
	
	public ViewPort getLastViewPort(){
		return lastViewPort;
	}
	
	protected ViewPort lastViewPort;
	
	/** Apply the GL viewport according to the settings given to setViewPort.
	 * If the screen grid was activated, the grid is rendered prior to the viewport
	 * definition.
	 * 
	 * After a call to {@link applyViewPort}, one can query the offset that was
	 * applied to the viewport relative to the canvas. Indeed, when the stretchToFill
	 * option is disabled, the viewport is computed so that the content appears in
	 * a squared subpart of the canvas.
	 * 
	 * @param gl
	 * @param glu
	 */
	protected void applyViewPort(GL2 gl, GLU glu){		
		// Stretch projection on the whole viewport
		if(stretchToFill){
			screenXOffset = screenLeft;
			screenYOffset = 0;

			gl.glViewport(screenXOffset, screenYOffset, screenWidth, screenHeight);			
			lastViewPort = new ViewPort(screenWidth, screenHeight, screenXOffset, screenYOffset);
		}
		// Set the projection into the largest square area centered in the window slice
		else{
			screenSquaredDim = Math.min(screenWidth, screenHeight);
			screenXOffset = screenLeft + screenWidth /2 - screenSquaredDim/2;
			screenYOffset =              screenHeight/2 - screenSquaredDim/2;
			
			gl.glViewport(screenXOffset, screenYOffset, screenSquaredDim, screenSquaredDim);			
			lastViewPort = new ViewPort(screenSquaredDim, screenSquaredDim, screenXOffset, screenYOffset);
		}
		
		// Render the screen grid if required
		if(screenGridDisplayed)
			renderSubScreenGrid(gl, glu);
	}
		
	/** Returns the (x,y) offset that was applied to make this {@link AbstractViewport} stand in the appropriate canvas part. 
	 * and the actual width and height of the viewport.
	 * Only relevant after a call to {@link applyViewPort}.*/
	public Rectangle getRectangle(){
		if(stretchToFill)
			return new Rectangle(screenXOffset, screenYOffset, screenWidth, screenHeight);
		else{
			return new Rectangle(screenXOffset, screenYOffset, screenSquaredDim, screenSquaredDim);
		}
	}
	
	/********************************************************************************/

	/** Set the status of the screen grid.
	 * 
	 * @param status the grid is displayed if status is set to true
	 */
	public void setScreenGridDisplayed(boolean status){
		screenGridDisplayed = status;
	}

	/** Renders a grid on the defined sub screen.*/
	private void renderSubScreenGrid(GL2 gl, GLU glu){
		if(screenWidth<=0)
			return;
		
		// Set a 2d projection
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		if(stretchToFill){
			int screenXoffset = screenLeft;
			int screenYoffset = 0;

			gl.glViewport(screenXoffset, screenYoffset, screenWidth, screenHeight);
		}
		
		// Set the projection into the largest square area centered in the window slice
		else{
			int dimension = Math.min(screenWidth, screenHeight);
			int screenXoffset = screenLeft + screenWidth /2 - dimension/2;
			int screenYoffset =              screenHeight/2 - dimension/2;
			
			gl.glViewport(screenXoffset, screenYoffset, dimension, dimension);
		}
		
		
		gl.glOrtho(AREA_LEFT, AREA_RIGHT, AREA_DOWN, AREA_TOP, -1, 1);
		
		// Set a grid
		gl.glMatrixMode( GL2.GL_MODELVIEW );
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glColor3f(1f, 0.5f, 0.5f);
		gl.glLineWidth(1f);
		
		float step;
		
		step = (AREA_RIGHT-AREA_LEFT)/(GRID_STEPS+0);
		for(float i=AREA_LEFT; i<=AREA_RIGHT; i+=step){
		    float x = i;
		    if(x==AREA_LEFT)
		        x+=OFFSET;
		    
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x, AREA_DOWN, 1);
			gl.glVertex3f(x, AREA_TOP, 1);
			gl.glEnd();
		}

		step = (AREA_TOP-AREA_DOWN)/(GRID_STEPS+0);
		for(float j=AREA_DOWN; j<=AREA_TOP; j+=step){
		    float y = j;
            if(y==AREA_TOP)
                y-=OFFSET;
            
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(AREA_LEFT, y, 1);
			gl.glVertex3f(AREA_RIGHT, y, 1);
			gl.glEnd();
		}
		
		// Restore matrices
		gl.glPopMatrix();
		gl.glMatrixMode( GL2.GL_PROJECTION );
		gl.glPopMatrix(); 
	}
	
	private static final float AREA_LEFT  = -100;
	private static final float AREA_RIGHT = +100;
	private static final float AREA_TOP   = +100;
	private static final float AREA_DOWN  = -100;
	private static final float GRID_STEPS =  10;
	private static final float OFFSET     = 0.1f;
	
	/********************************************************************************/
	
	protected int screenLeft       = 0;
	protected int screenRight      = 0;
	protected int screenXOffset    = 0;
	protected int screenYOffset    = 0;
	protected int screenWidth      = 0;
	protected int screenHeight     = 0;
	protected int screenSquaredDim = 0;
	
	protected boolean screenGridDisplayed = false;
	protected boolean stretchToFill = false;
	
	protected float ratioWidth;
	protected float ratioHeight;

}
