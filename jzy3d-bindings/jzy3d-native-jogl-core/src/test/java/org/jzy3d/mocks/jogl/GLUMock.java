package org.jzy3d.mocks.jogl;

import com.jogamp.opengl.glu.GLU;

public class GLUMock extends GLU {
  @Override
  public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY,
      float centerZ, float upX, float upY, float upZ) {
    // project.gluLookAt(getCurrentGL().getGL2ES1(), eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
    // upX, upY, upZ);
  }

}
