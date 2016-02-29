package org.jzy3d.plot3d.primitives;

import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public interface IGLRenderer {
	public void draw(GL gl, GLU glu, Camera cam);
}
