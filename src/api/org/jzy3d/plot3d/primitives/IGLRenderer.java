package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot3d.rendering.view.Camera;

public interface IGLRenderer {
	public void draw(GL2 gl, GLU glu, Camera cam);
}
