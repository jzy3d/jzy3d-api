package org.jzy3d.plot3d.primitives.axes;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import com.jogamp.common.nio.Buffers;

/**
 * This {@link AxeBox} implementation was the first to appear in Jzy3d.
 * It computes hidden faces using the OpenGL feedback buffer, which seems
 * to fail on some hardware (especially Lenovo notepads).
 * 
 * The parent {@link AxeBox} doesn't rely anymore one feedback buffer, and
 * this implementation has been kept in order to provide an example of
 * feedback buffer use.
 * 
 * @see http://code.google.com/p/jzy3d/issues/detail?id=10
 * @author Martin Pernollet
 */

public class FeedbackBufferAxeBox extends AxeBox implements IAxe{
	public FeedbackBufferAxeBox(BoundingBox3d bbox){
		super(bbox);
	}
	public FeedbackBufferAxeBox(BoundingBox3d bbox, IAxeLayout layout){
		super(bbox, layout);
	}
	
	@Override
	public void draw(GL2 gl, GLU glu, Camera camera){
		// Set scaling
		gl.glLoadIdentity();
		gl.glScalef(scale.x, scale.y, scale.z);
		
		// Set culling
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glCullFace(GL2.GL_FRONT);
		
		// Draw cube in feedback buffer for computing hidden quads
		quadIsHidden = getHiddenQuads(gl);	
		
		// Plain part of quad making the surrounding box
		if( layout.isFaceDisplayed() ){
			Color quadcolor = layout.getQuadColor();
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
			gl.glColor4f(quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
			gl.glLineWidth(1.0f);
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1.0f, 1.0f); // handle stippling
			drawCube(gl, GL2.GL_RENDER);
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		}
		
		// Edge part of quads making the surrounding box
		Color gridcolor = layout.getGridColor();
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(1);			
		drawCube(gl, GL2.GL_RENDER);	
				
		// Draw grids on non hidden quads
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(1);
		gl.glLineStipple(1, (short)0xAAAA);
		gl.glEnable(GL2.GL_LINE_STIPPLE);		
		for(int quad=0; quad<6; quad++)
			if(!quadIsHidden[quad])		
				drawGridOnQuad(gl, quad);
		gl.glDisable(GL2.GL_LINE_STIPPLE);
		
		// Draw ticks on the closest axes
		wholeBounds.reset();
		wholeBounds.add(boxBounds);
		
		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		// Display x axis ticks
		if(xrange>0 && layout.isXTickLabelDisplayed()){
			
			// If we are on top, we make direct axe placement
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_X, layout.getXTickColor(), Halign.LEFT, Valign.TOP); // setup tick labels for X on the bottom 
				wholeBounds.add(bbox);
			}
			// otherwise computed placement
			else{
				int xselect = findClosestXaxe(camera);
				if(xselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, xselect, AXE_X, layout.getXTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no x axe selected: " + Arrays.toString(quadIsHidden));
					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
					BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_X, layout.getXTickColor(), Halign.CENTER, Valign.TOP); 
					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display y axis ticks
		if(yrange>0 && layout.isYTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_Y, layout.getYTickColor(), Halign.LEFT, Valign.GROUND); // setup tick labels for Y on the left 
				wholeBounds.add(bbox);
			}
			else{
				int yselect = findClosestYaxe(camera);
				if(yselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, yselect, AXE_Y, layout.getYTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no y axe selected: " + Arrays.toString(quadIsHidden));
					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
					BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_Y, layout.getYTickColor(), Halign.RIGHT, Valign.GROUND);
					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display z axis ticks
		if(zrange>0 && layout.isZTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				
			}
			else{
				int zselect = findClosestZaxe(camera);
				if(zselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, zselect, AXE_Z, layout.getZTickColor());
					wholeBounds.add(bbox);
				}				
			}
		}
		
		// Unset culling
		gl.glDisable(GL2.GL_CULL_FACE);
	}
	
	/******************************************************************/
	/**                    DRAW AXEBOX ELEMENTS                      **/
	
	/**
	 * Make all GL calls allowing to build a cube with 6 separate quads.
	 * Each quad is indexed from 0.0f to 5.0f using glPassThrough,
	 * and may be traced in feedback mode when mode=GL.GL_FEEDBACK 
	 */
	protected void drawCube(GL2 gl, int mode){
		for(int q=0; q<6; q++){
			if(mode==GL2.GL_FEEDBACK)
				gl.glPassThrough((float)q);
			gl.glBegin(GL2.GL_QUADS);
				for(int v=0; v<4; v++){
					gl.glVertex3f( quadx[q][v], quady[q][v], quadz[q][v]);
				}
			gl.glEnd();
		}
	}
	
	/******************************************************************/
    /**                COMPUTATION OF HIDDEN QUADS                   **/
    
	/*
	 * Render the cube into the feedback buffer, in order to parse feedback
	 * and determine which quad where displayed or not.
	 */
	protected boolean [] getHiddenQuads(GL2 gl){
		int feedbacklength = 1024;
		FloatBuffer floatbuffer = Buffers.newDirectFloatBuffer(feedbacklength);
		float [] feedback = new float[feedbacklength];
		
		// Draw the cube into feedback buffer
		gl.glFeedbackBuffer(feedbacklength, GL2.GL_3D_COLOR, floatbuffer);
		gl.glRenderMode(GL2.GL_FEEDBACK);
		drawCube(gl, GL2.GL_FEEDBACK);
		gl.glRenderMode(GL2.GL_RENDER);
		
		// Parse feedback buffer and return hidden quads
		floatbuffer.get(feedback);
		return getEmptyTokens(6, feedback, feedbacklength);
	}
	
	/**
	 * This utility function inform wether a GL_PASS_THROUGH_TOKEN was followed
	 * by other OpenGL tokens (GL_POLYGON_TOKEN, GL_LINE_TOKEN, etc), or not.
	 * In the first case, the given token is considered as non empty, in the other
	 * case, it is considered as empty.
	 * The expected use of this function is to inform the user wether a polygon
	 * has been displayed or not in the case Culling was activated.
	 * 
	 * Note: this function is only intended to work in the case where gl.glFeedbackBuffer
	 * is called with GL.GL_3D_COLOR as second argument, i.e. vertices are made of
	 * x, y, z, alpha values and a color. Thus, no texture may be used, and trying
	 * to parse a GL_BITMAP_TOKEN, GL_DRAW_PIXEL_TOKEN, or GL_COPY_PIXEL_TOKEN
	 * will throw a RuntimeException.
	 * 
	 * Note: this function only works with PASS_THROUGH_TOKEN with a 
	 * positive integer value, since this value will be used as an index in
	 * the boolean array returned by the function
	 * 
	 * TODO: use a map to associate a float token to a polygon with an id?
	 * 
	 * @throws a RuntimeException if a parsed token is either GL_BITMAP_TOKEN, GL_DRAW_PIXEL_TOKEN, or GL_COPY_PIXEL_TOKEN.
	 */
	protected boolean[] getEmptyTokens(int ntokens, float[] buffer, int size){
		boolean[] isempty = new boolean[ntokens];
		boolean  printout = false; // for debugging parsing
		
		int   count = size;
		float token_type;
		float prevtoken_type = Float.NaN;
		float passthrough_value = Float.NaN;
		float prevpassthrough_value = Float.NaN;
		int   prevtoken_id;
		
		float EMPTY_TOKEN = 0.0f;
		
		while(count>0){
			token_type = buffer[size-count]; 
			count--;
			
			// Case of a PASS THROUGH token
            if (token_type == GL2.GL_PASS_THROUGH_TOKEN) { // can't use switch cause we have floats
            	passthrough_value = buffer[size - count];
            	count--;
            	
                if(printout)
                	System.out.println("GL.GL_PASS_THROUGH_TOKEN: " + passthrough_value);

                // If the preceding token is also a GL_PASS_THROUGH_TOKEN
                // we consider it as an empty token
                if(!Float.isNaN(prevpassthrough_value)){
	                prevtoken_id = (int)prevpassthrough_value;
	                if( token_type == prevtoken_type )
	            		isempty[prevtoken_id] = true;
	                else
	                	isempty[prevtoken_id] = false;
                }
                
                // Store current token value as previous
                prevpassthrough_value = passthrough_value;
            } 
            
            // Other cases: just consume buffer
            else if (token_type == GL2.GL_POINT_TOKEN) {
            	if(printout){
            		System.out.println(" GL.GL_POINT_TOKEN");
                	count = print3DcolorVertex(size, count, buffer);
            	}else{
            		count = count - 7;
            	}
            } 
            else if (token_type == GL2.GL_LINE_TOKEN) {
            	if(printout){
            		System.out.println(" GL.GL_LINE_TOKEN ");
            		count = print3DcolorVertex(size, count, buffer);
            		count = print3DcolorVertex(size, count, buffer);
            	}else{
            		count = count - 14;
            	}
            } 
            else if (token_type == GL2.GL_LINE_RESET_TOKEN) {
            	if(printout){
            		System.out.println(" GL.GL_LINE_RESET_TOKEN ");
            		count = print3DcolorVertex(size, count, buffer);
            		count = print3DcolorVertex(size, count, buffer);
            	}else{
            		count = count - 14;
            	}
            } 
            else if (token_type == GL2.GL_POLYGON_TOKEN) {
            	int n = (int)buffer[size - count];
            	count--;
            	
            	if(printout)
            		System.out.println(" GL.GL_POLYGON_TOKEN: " + n + " vertices");
            	
                for(int i=0; i<n; i++){
                	if(printout)
                		count = print3DcolorVertex(size, count, buffer);
                	else
                		count = count - 7;
                }
            }
            
            // Case of an empty token
            else if (token_type == EMPTY_TOKEN){ //MAYBE THE CONTENT IS 0.0?? DEPENDING ON THE VALUE OF THE TOKEN CONSTANTS!!!
            	; // this is a non token: array was not filled
            
            	// If the prev token is a GL_PASS_THROUGH_TOKEN, it was empty 
	            if(prevtoken_type==GL2.GL_PASS_THROUGH_TOKEN){
	            	prevtoken_id = (int)prevpassthrough_value;
	                isempty[prevtoken_id] = true;
	            }
	            break;
            }
            // Case of an unknown token
            else if (token_type == GL2.GL_BITMAP_TOKEN)
            	throw new RuntimeException("Unknown token:" + token_type + ". This function is not intended to work with GL_BITMAP_TOKEN.");
            else if (token_type == GL2.GL_DRAW_PIXEL_TOKEN)
            	throw new RuntimeException("Unknown token:" + token_type + ". This function is not intended to work with GL_DRAW_PIXEL_TOKEN.");
            else if (token_type == GL2.GL_COPY_PIXEL_TOKEN)
            	throw new RuntimeException("Unknown token:" + token_type + ". This function is not intended to work with GL_COPY_PIXEL_TOKEN.");
            else{
            	throw new RuntimeException("Unknown token type: " + token_type + "\n count= " + count);
            }
            // Update previous token
            prevtoken_type = token_type;
		}
		return isempty;
	}
	
	/******************************************************************/

	/**
	 *  Print out parameters of a gl call in 3dColor mode.
	 */
    protected int print3DcolorVertex(int size, int count, float[] buffer) {
        int i;
        int id = size - count;
        int veclength = 7;
        
        System.out.print("  [" + id + "]");
        for (i = 0; i < veclength; i++) {
            System.out.print(" " + buffer[size - count]);
            count = count - 1;
        }
        System.out.println();
        return count;
    }
	
	/**
	 * Print out display status of quads.
	 */
	protected void printHiddenQuads(){
		for(int t=0; t<quadIsHidden.length; t++)
			if(quadIsHidden[t])
				System.out.println("Quad[" + t + "] is not displayed");
			else
				System.out.println("Quad[" + t + "] is displayed");
	}
}
