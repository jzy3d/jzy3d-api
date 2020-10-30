package org.jzy3d.plot3d.rendering.canvas;

import com.jogamp.opengl.GLAnimatorControl;

public interface INativeScreenCanvas extends INativeCanvas{
	public GLAnimatorControl getAnimator();
}
