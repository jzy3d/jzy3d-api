package org.jzy3d.plot3d.transform;

import javax.media.opengl.GL2;

import org.jzy3d.maths.Coord3d;


/**
 * Translate is a {@link Transformer} that stores the offset
 * required to perform the effective OpenGL2 translation in the 
 * ModelView Matrix.
 * @author Martin Pernollet
 */
public class Translate implements Transformer {
    protected Translate(){
        
    }
	/**
	 * Initialize a Translate.
	 * @param shift translation offset.
	 */
	public Translate(Coord3d shift){
		this.shift = shift;
	}
	
	public void execute(GL2 gl){
		gl.glTranslatef(shift.x, shift.y, shift.z);
	}
	
	public Coord3d compute(Coord3d input) {
		return input.add(shift);
	}	
	
	public String toString(){
		return "(Translate)" + shift;
	}
	
	/**************************************************/
	
	private Coord3d shift;
}
