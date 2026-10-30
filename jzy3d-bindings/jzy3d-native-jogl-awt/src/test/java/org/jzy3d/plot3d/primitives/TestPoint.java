package org.jzy3d.plot3d.primitives;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.mocks.jogl.GLMock;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.view.Camera;
import com.jogamp.opengl.glu.GLU;

public class TestPoint {
  GLU glu = new GLU();
  Camera cam = new Camera();

  @Test
  public void glCoordinatesConform() {
    // Given
    Point p = new Point();
    p.setData(new Coord3d(3, 30, 1000));

    // When
    GLMock glMock = new GLMock();

    NativeDesktopPainter painter = new NativeDesktopPainter();
    painter.setGL(glMock);

    p.draw(painter);

    // Then
    Assert.assertTrue(glMock.vertex3f_contains(3, 30, 1000));
  }

}
