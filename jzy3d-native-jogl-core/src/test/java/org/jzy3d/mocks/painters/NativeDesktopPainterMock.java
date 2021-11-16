package org.jzy3d.mocks.painters;

import org.jzy3d.mocks.jogl.GLMock;
import org.jzy3d.mocks.jogl.GLUMock;
import org.jzy3d.painters.NativeDesktopPainter;

/**
 * This painter uses {@link GLMock} and {@link GLUMock} which are implementations of JOGL {@link GL}
 * and {@link GLU} that simply do nothing.
 * 
 * This allows building test that runs through all painter methods, providing a JOGL {@link GL} or
 * {@link GLU} without doing anything really, in order to check non graphic behaviour of objects.
 * 
 * @author Martin Pernollet
 *
 */
public class NativeDesktopPainterMock extends NativeDesktopPainter {

  public NativeDesktopPainterMock() {
    super();

    setGL(new GLMock());
    setGLU(new GLUMock());


  }

}
