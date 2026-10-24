package org.jzy3d.painters;

import org.junit.Assume;
import org.junit.Test;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.junit.PainterGPUConformance;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;

/** Verify the GPU resources API of the JOGL painter in an offscreen GL context. */
public class TestNativeDesktopPainter_GPU {

  @Test
  public void gpuResources() {
    GLOffscreenAutoDrawable drawable;
    try {
      GLProfile profile = NativePainterFactory.detectGLProfile();
      GLCapabilities caps = NativePainterFactory.getOffscreenCapabilities(profile);
      drawable = GLDrawableFactory.getFactory(profile).createOffscreenAutoDrawable(null, caps,
          null, PainterGPUConformance.WIDTH, PainterGPUConformance.HEIGHT);
      drawable.display();
    } catch (Throwable t) {
      Assume.assumeNoException("No OpenGL context available", t);
      return;
    }

    GLContext context = drawable.getContext();
    context.makeCurrent();

    try {
      NativeDesktopPainter painter = new NativeDesktopPainter();
      painter.setGL(context.getGL());

      new PainterGPUConformance(painter).checkAll();
    } finally {
      context.release();
      ((GLAutoDrawable) drawable).destroy();
    }
  }
}
