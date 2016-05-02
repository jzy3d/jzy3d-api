package org.jzy3d.plot3d.transform;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

import com.jogamp.opengl.GL;

/**
 * A {@link Transform} stores a sequence of {@link Transformer}s, that are of
 * concrete type {@link Rotate}, {@link Scale}, {@link Translate}.
 * 
 * When a Transform is executed by default, it first loads the identity matrix
 * before executing the sequence of Transformers.
 * 
 * @author Martin Pernollet
 */
public class Transform {

	/** Initialize a list of Transformer. */
	public Transform() {
		sequence = new ArrayList<Transformer>();
	}

	/**
	 * Initialize a list of Transformer with a single Transformer as sequence.
	 */
	public Transform(Transformer transformer) {
		sequence = new ArrayList<Transformer>();
		sequence.add(transformer);

	}

	/**
	 * Initialize a list of Transformer with the sequence hold by the given
	 * Transform.
	 */
	public Transform(Transform transform) {
		sequence = new ArrayList<Transformer>();
		for (Transformer next : transform.sequence)
			sequence.add(next);
	}

	/* */

	/**
	 * Appends a Transformer to the sequence that this Transform must performs.
	 * 
	 * @param next
	 */
	public void add(Transformer next) {
		sequence.add(next);
	}

	/**
	 * Appends a Transform to the current Transform.
	 * 
	 * @param next
	 */
	public void add(Transform transform) {
		for (Transformer next : transform.sequence)
			sequence.add(next);
	}

	/* */

	/**
	 * Load the identity matrix and executes the stored sequence of Transformer.
	 * 
	 * @param gl
	 *            OpenGL2 context
	 */
	public void execute(GL gl) {
		execute(gl, true);
	}

	public void execute(GL gl, boolean loadIdentity) {
		if (loadIdentity) {
			if (gl.isGL2()) {
				gl.getGL2().glLoadIdentity();
			} else {
				GLES2CompatUtils.glLoadIdentity();
			}
		}

		for (Transformer t : sequence)
			t.execute(gl);
	}

	/** Apply the transformations to the input coordinate */
	public Coord3d compute(Coord3d input) {
		Coord3d output = input.clone();
		for (Transformer t : sequence) {
			output = t.compute(output);
		}
		return output;
	}

	@Override
    public String toString() {
		String txt = "";
		for (Transformer t : sequence)
			txt += " * " + t.toString();
		return txt;
	}

	/***********************************************************/

	private List<Transformer> sequence;
}
