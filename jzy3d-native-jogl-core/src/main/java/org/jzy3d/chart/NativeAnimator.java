package org.jzy3d.chart;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.FPSAnimator;

public class NativeAnimator implements IAnimator {
  protected com.jogamp.opengl.util.AnimatorBase animator;

  public NativeAnimator(GLAutoDrawable canvas) {
    // animator = new com.jogamp.opengl.util.Animator(canvas);
    animator = new FPSAnimator(canvas, 10);
  }

  @Override
  public void start() {
    animator.start();
  }

  @Override
  public void stop() {
    animator.stop();
  }
  
  public FPSAnimator getAnimator() {
    return (FPSAnimator)animator;
  }
}
