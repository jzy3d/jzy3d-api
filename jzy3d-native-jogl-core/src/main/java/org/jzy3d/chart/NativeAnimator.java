package org.jzy3d.chart;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.FPSAnimator;

public class NativeAnimator implements IAnimator {
  protected com.jogamp.opengl.util.AnimatorBase animator;
  

  public NativeAnimator(GLAutoDrawable canvas) {
    // animator = new com.jogamp.opengl.util.Animator(canvas);
    animator = new FPSAnimator(canvas, DEFAULT_FRAME_PER_SECOND);
  }

  @Override
  public void start() {
    animator.start();
  }

  @Override
  public void stop() {
    animator.stop();
  }
  
  public com.jogamp.opengl.util.AnimatorBase getAnimator() {
    return animator;
  }
  
  public void setAnimator(com.jogamp.opengl.util.Animator animator) {
    this.animator = animator;
  }
}
