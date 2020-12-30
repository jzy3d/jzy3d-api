package org.jzy3d.chart;

import com.jogamp.opengl.GLAutoDrawable;

public class NativeAnimator implements IAnimator{
	protected com.jogamp.opengl.util.Animator animator;
	
	public NativeAnimator(GLAutoDrawable canvas) {
        animator = new com.jogamp.opengl.util.Animator(canvas);
	}

	@Override
	public void start() {
		animator.start();
	}

	@Override
	public void stop() {
		animator.stop();
	}
	
	public com.jogamp.opengl.util.Animator getAnimator(){
		return animator;
	}

}
