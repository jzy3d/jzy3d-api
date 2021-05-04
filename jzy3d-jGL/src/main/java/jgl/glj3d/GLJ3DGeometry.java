/*
 * @(#)GLJ3DGeometry.java 0.1 00/01/20
 *
 * jGL 3-D graphics library for Java Copyright (c) 2000 Robin Bing-Yu Chen
 * (robin@is.s.u-tokyo.ac.jp)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or any later version. the GNU Lesser General Public License should be
 * included with this distribution in the file LICENSE.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package jgl.glj3d;

import jgl.GL;

/**
 * GLJ3DGeometry is the Java3d geometry like class of JavaGL 2.2.
 *
 * @version 0.1, 20 Jan 2000
 * @author Robin Bing-Yu Chen
 */

public class GLJ3DGeometry {

  /**
   * Draws a color cube with the specified scale. The corners of the color cube are
   * [-scale,-scale,-scale] and [scale,scale,scale].
   * 
   * @param JavaGL the JavaGL instance
   * @param scale the scale of the cube
   */
  public static final void drawColorCube(GL JavaGL, double scale) {
    int faces[][] =
        {{5, 6, 2, 1}, {7, 4, 0, 3}, {7, 6, 5, 4}, {0, 1, 2, 3}, {3, 2, 6, 7}, {4, 5, 1, 0},};

    float colors[][] = {
        // front face (red)
        {1.0f, 0.0f, 0.0f},
        // back face (green)
        {0.0f, 1.0f, 0.0f},
        // right face (blue)
        {0.0f, 0.0f, 1.0f},
        // left face (yellow)
        {1.0f, 1.0f, 0.0f},
        // top face (magenta)
        {1.0f, 0.0f, 1.0f},
        // bottom face (cyan)
        {0.0f, 1.0f, 1.0f}};

    double v[][] = new double[8][3];

    v[0][0] = v[1][0] = v[2][0] = v[3][0] = -scale;
    v[4][0] = v[5][0] = v[6][0] = v[7][0] = scale;
    v[0][1] = v[1][1] = v[4][1] = v[5][1] = -scale;
    v[2][1] = v[3][1] = v[6][1] = v[7][1] = scale;
    v[0][2] = v[3][2] = v[4][2] = v[7][2] = -scale;
    v[1][2] = v[2][2] = v[5][2] = v[6][2] = scale;

    for (int i = 0; i < 6; i++) {
      JavaGL.glBegin(GL.GL_QUADS);
      JavaGL.glColor3fv(colors[i]);
      JavaGL.glVertex3dv(v[faces[i][0]]);
      JavaGL.glVertex3dv(v[faces[i][1]]);
      JavaGL.glVertex3dv(v[faces[i][2]]);
      JavaGL.glVertex3dv(v[faces[i][3]]);
      JavaGL.glEnd();
    }
  }

}
