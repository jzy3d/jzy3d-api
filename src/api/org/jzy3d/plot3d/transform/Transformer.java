package org.jzy3d.plot3d.transform;

import javax.media.opengl.GL2;

import org.jzy3d.maths.Coord3d;


/** A {@link Transformer} is able to execute a transformation into
 * the OpenGL ModelView Matrix.
 * A {@link Transformer} is basically a Functor object that stores a transformation,
 * and performs the effective GL2 call to perform the transformation.
 * 
 * @author Martin Pernollet
 */
interface Transformer {	
	/**
	 * Execute the effective GL transformation held by this {@link Transformer}.
	 * @param gl OpenGL context.
	 */
	public void execute(GL2 gl);	
	
	/**
	 * Apply the transformations to the input coordinates.
	 * 
	 * Warning: this method is a utility that may not be implemented.
	 */
	public Coord3d compute(Coord3d input);	
}
