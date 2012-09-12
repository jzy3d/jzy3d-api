package org.jzy3d.plot3d.rendering.canvas;

import javax.media.opengl.GLAnimatorControl;

public interface IScreenCanvas extends ICanvas{
	public void display();
	public GLAnimatorControl getAnimator();
}
