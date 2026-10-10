/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.painters;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import panamagl.opengl.GL;

/**
 * The GLU functions emitting OpenGL commands, implemented in Java on top of the given {@link GL},
 * as JOGL does.
 * 
 * Native GLU libraries (glu32.dll, libGLU) issue their OpenGL commands through the OpenGL library
 * they are linked to, which is not always the one holding the current context : on Windows,
 * glu32.dll calls the system opengl32.dll even when another OpenGL implementation (e.g. Mesa) was
 * loaded, and then draws nothing. Implementing these functions with the GL instance of the painter
 * avoids this, and the need of a GLU library to render.
 * 
 * Quadrics are drawn as the default GLU quadric does : filled, with smooth normals pointing
 * outside, without texture coordinates.
 */
public class PanamaGLU {

  // ---------------------------------------------------------------------------------------------
  // MATRICES

  /** As gluOrtho2D : an orthographic projection with near and far planes at -1 and 1. */
  public static void ortho2D(GL gl, double left, double right, double bottom, double top) {
    gl.glOrtho(left, right, bottom, top, -1, 1);
  }

  /** As gluPerspective : a perspective projection with a vertical field of view in degrees. */
  public static void perspective(GL gl, double fovy, double aspect, double zNear, double zFar) {
    double ymax = zNear * Math.tan(Math.toRadians(fovy) / 2);
    double xmax = ymax * aspect;
    gl.glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
  }

  /** As gluLookAt : a viewing transformation from the eye to the center, with the up direction. */
  public static void lookAt(GL gl, double eyeX, double eyeY, double eyeZ, double centerX,
      double centerY, double centerZ, double upX, double upY, double upZ) {
    double[] forward = normalize(centerX - eyeX, centerY - eyeY, centerZ - eyeZ);
    double[] side = normalize(cross(forward, new double[] {upX, upY, upZ}));
    double[] up = cross(side, forward);

    // column major
    double[] m = {side[0], up[0], -forward[0], 0, //
        side[1], up[1], -forward[1], 0, //
        side[2], up[2], -forward[2], 0, //
        0, 0, 0, 1};

    try (Arena arena = Arena.ofConfined()) {
      gl.glMultMatrixd(arena.allocateFrom(ValueLayout.JAVA_DOUBLE, m));
    }
    gl.glTranslated(-eyeX, -eyeY, -eyeZ);
  }

  /** As gluPickMatrix : restrict drawing to a region of the viewport, centered at x,y. */
  public static void pickMatrix(GL gl, double x, double y, double deltaX, double deltaY,
      int[] viewport, int viewportOffset) {
    if (deltaX <= 0 || deltaY <= 0) {
      return;
    }
    int vx = viewport[viewportOffset];
    int vy = viewport[viewportOffset + 1];
    int vw = viewport[viewportOffset + 2];
    int vh = viewport[viewportOffset + 3];

    gl.glTranslated((vw - 2 * (x - vx)) / deltaX, (vh - 2 * (y - vy)) / deltaY, 0);
    gl.glScaled(vw / deltaX, vh / deltaY, 1);
  }

  // ---------------------------------------------------------------------------------------------
  // QUADRICS

  /** As gluSphere : a sphere centered at the origin, its axis along Z. */
  public static void sphere(GL gl, double radius, int slices, int stacks) {
    for (int i = 0; i < stacks; i++) {
      double rho0 = Math.PI * i / stacks;
      double rho1 = Math.PI * (i + 1) / stacks;

      gl.glBegin(GL.GL_QUAD_STRIP);
      for (int j = 0; j <= slices; j++) {
        double theta = (j == slices) ? 0 : 2 * Math.PI * j / slices;
        sphereVertex(gl, radius, rho0, theta);
        sphereVertex(gl, radius, rho1, theta);
      }
      gl.glEnd();
    }
  }

  protected static void sphereVertex(GL gl, double radius, double rho, double theta) {
    double x = -Math.sin(theta) * Math.sin(rho);
    double y = Math.cos(theta) * Math.sin(rho);
    double z = Math.cos(rho);
    gl.glNormal3d(x, y, z);
    gl.glVertex3d(x * radius, y * radius, z * radius);
  }

  /** As gluDisk : a disk in the Z=0 plane, facing +Z, with a hole of the inner radius. */
  public static void disk(GL gl, double inner, double outer, int slices, int loops) {
    gl.glNormal3d(0, 0, 1);

    double dr = (outer - inner) / loops;

    for (int l = 0; l < loops; l++) {
      double r0 = inner + l * dr;
      double r1 = r0 + dr;

      gl.glBegin(GL.GL_QUAD_STRIP);
      for (int s = 0; s <= slices; s++) {
        double angle = (s == slices) ? 0 : 2 * Math.PI * s / slices;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        gl.glVertex3d(r1 * sin, r1 * cos, 0);
        gl.glVertex3d(r0 * sin, r0 * cos, 0);
      }
      gl.glEnd();
    }
  }

  /**
   * As gluCylinder : a cylinder along Z from 0 to height, with the base radius at Z=0 and the top
   * radius at Z=height, without caps.
   */
  public static void cylinder(GL gl, double base, double top, double height, int slices,
      int stacks) {
    // normals are tilted when radii differ
    double nz = (base - top) / height;
    double nlength = Math.sqrt(1 + nz * nz);

    for (int i = 0; i < stacks; i++) {
      double z0 = height * i / stacks;
      double z1 = height * (i + 1) / stacks;
      double r0 = base + (top - base) * i / stacks;
      double r1 = base + (top - base) * (i + 1) / stacks;

      gl.glBegin(GL.GL_QUAD_STRIP);
      for (int s = 0; s <= slices; s++) {
        double angle = (s == slices) ? 0 : 2 * Math.PI * s / slices;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        gl.glNormal3d(sin / nlength, cos / nlength, nz / nlength);
        gl.glVertex3d(r0 * sin, r0 * cos, z0);
        gl.glVertex3d(r1 * sin, r1 * cos, z1);
      }
      gl.glEnd();
    }
  }

  // ---------------------------------------------------------------------------------------------

  protected static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  protected static double[] normalize(double... v) {
    double length = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    if (length == 0) {
      return v;
    }
    return new double[] {v[0] / length, v[1] / length, v[2] / length};
  }
}
