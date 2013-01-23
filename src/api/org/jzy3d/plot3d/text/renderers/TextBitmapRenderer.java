package org.jzy3d.plot3d.text.renderers;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.AbstractTextRenderer;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * could enhance using http://www.angelcode.com/products/bmfont/
 * @author Martin
 */
public class TextBitmapRenderer extends AbstractTextRenderer implements ITextRenderer{
	/**
	 * The TextBitmap class provides support for drawing ASCII characters
	 * Any non ascii caracter will be replaced by a square.
	 */
	public TextBitmapRenderer(){	
		super();
		font = GLUT.BITMAP_HELVETICA_10;
		fontHeight = 10;
	}
	
	@Override
	public void drawSimpleText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Color color){
		gl.glColor3f(color.r, color.g, color.b);
		gl.glRasterPos3f(position.x, position.y, position.z);
        glut.glutBitmapString(font, s);
	}
	
	/** Draw a string at the specified position and compute the 3d volume occupied by the string 
	 * according to the current Camera configuration.*/
	@Override
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color, Coord2d screenOffset, Coord3d sceneOffset){
		gl.glColor3f(color.r, color.g, color.b);
		
		if(cam==null)
		    return null;
		
		Coord3d posScreen = cam.modelToScreen(gl, glu, position);
		
		//System.out.println(posScreen);
		
		// compute a corrected position according to layout
        float  strlen = glut.glutBitmapLength(font, s);
		float  x      = 0.0f;
        float  y      = 0.0f;
        
        if(halign==Halign.RIGHT)
        	x = posScreen.x;
        else if(halign==Halign.CENTER)
			x = posScreen.x - strlen/2;
        else if(halign==Halign.LEFT)
			x = posScreen.x - strlen;
        
        if(valign==Valign.TOP)
			y = posScreen.y;
        else if(valign==Valign.GROUND)
        	y = posScreen.y;
        else if(valign==Valign.CENTER)
			y = posScreen.y - fontHeight/2;
        else if(valign==Valign.BOTTOM)
			y = posScreen.y - fontHeight;
        
        Coord3d posScreenShifted = new Coord3d(x+screenOffset.x, y+screenOffset.y, posScreen.z);
        Coord3d posReal;
        
        try{
        	posReal = cam.screenToModel(gl, glu, posScreenShifted);
        }
        catch(RuntimeException e){ // TODO: really solve this bug due to a Camera.PERSPECTIVE mode.
        	System.err.println("TextBitmap.drawText(): could not process text position: " + posScreen + " " + posScreenShifted);
        	return new BoundingBox3d();
        }
        
        // Draws actual string
        gl.glRasterPos3f(posReal.x+sceneOffset.x, posReal.y+sceneOffset.y, posReal.z+sceneOffset.z);
        glut.glutBitmapString(font, s);

        // Compute bounds of text
		Coord3d botLeft   = new Coord3d();
		Coord3d topRight  = new Coord3d();		
		botLeft.x  = posScreenShifted.x;
		botLeft.y  = posScreenShifted.y;
		botLeft.z  = posScreenShifted.z;		
		topRight.x = botLeft.x + strlen;
		topRight.y = botLeft.y + fontHeight;
		topRight.z = botLeft.z;		
		
		BoundingBox3d txtBounds = new BoundingBox3d();
		txtBounds.add( cam.screenToModel(gl, glu, botLeft) );
		txtBounds.add( cam.screenToModel(gl, glu, topRight) );		
		return txtBounds;
	}
	
	/********************************************************************/
	
	protected static GLUT glut = new GLUT();	
	protected int  fontHeight;  
	protected int font;
}
