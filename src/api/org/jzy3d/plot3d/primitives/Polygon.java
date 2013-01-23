package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;



/** 
 * Supports additional settings
 * @author Martin Pernollet
 */
public class Polygon extends AbstractGeometry{
    
    
	/** Initializes an empty {@link Polygon} with face status defaulting to true,
	 * and wireframe status defaulting to false.*/
	public Polygon(){
		super();
	}
	
    protected void begin(GL2 gl) {
        gl.glBegin(GL2.GL_POLYGON);
    }
}
