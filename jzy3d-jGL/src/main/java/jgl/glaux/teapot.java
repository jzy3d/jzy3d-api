/*
 * @(#)teapot.java 0.3 01/03/15
 *
 * jGL 3-D graphics library for Java Copyright (c) i1996-2001 Robin Bing-Yu Chen
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

package jgl.glaux;

import jgl.GL;

/**
 * teapot is the teapot class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class teapot {

  /** Constants of teapot */
  private static final int TEAPOTSOLID = 0;
  private static final int TEAPOTWIRE = 1;

  /**
   * Rim, body, lid, and bottom data must be reflected in x and y; handle and spout data across the
   * y axis only.
   */
  private static final int patchdata[][] =
      {{102, 103, 104, 105, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, /* rim */
          {12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27}, /* body */
          {24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40}, /* body */
          {96, 96, 96, 96, 97, 98, 99, 100, 101, 101, 101, 101, 0, 1, 2, 3,}, /* lid */
          {0, 1, 2, 3, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117}, /* lid */
          {118, 118, 118, 118, 124, 122, 119, 121, 123, 126, 125, 120, 40, 39, 38, 37}, /* bottom */
          {41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56}, /* handle */
          {53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 28, 65, 66, 67}, /* handle */
          {68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83}, /* spout */
          {80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95} /* spout */
      };

  private static final float cpdata[][] = {{0.2f, 0.0f, 2.7f}, {0.2f, -0.112f, 2.7f},
      {0.112f, -0.2f, 2.7f}, {0.0f, -0.2f, 2.7f}, {1.3375f, 0.0f, 2.53125f},
      {1.3375f, -0.749f, 2.53125f}, {0.749f, -1.3375f, 2.53125f}, {0.0f, -1.3375f, 2.53125f},
      {1.4375f, 0.0f, 2.53125f}, {1.4375f, -0.805f, 2.53125f}, {0.805f, -1.4375f, 2.53125f},
      {0.0f, -1.4375f, 2.53125f}, {1.5f, 0.0f, 2.4f}, {1.5f, -0.84f, 2.4f}, {0.84f, -1.5f, 2.4f},
      {0.0f, -1.5f, 2.4f}, {1.75f, 0.0f, 1.875f}, {1.75f, -0.98f, 1.875f}, {0.98f, -1.75f, 1.875f},
      {0.0f, -1.75f, 1.875f}, {2, 0.0f, 1.35f}, {2, -1.12f, 1.35f}, {1.12f, -2, 1.35f},
      {0.0f, -2, 1.35f}, {2, 0.0f, 0.9f}, {2, -1.12f, 0.9f}, {1.12f, -2, 0.9f}, {0.0f, -2, 0.9f},
      {-2, 0.0f, 0.9f}, {2, 0.0f, 0.45f}, {2, -1.12f, 0.45f}, {1.12f, -2, 0.45f}, {0.0f, -2, 0.45f},
      {1.5f, 0.0f, 0.225f}, {1.5f, -0.84f, 0.225f}, {0.84f, -1.5f, 0.225f}, {0.0f, -1.5f, 0.225f},
      {1.5f, 0.0f, 0.15f}, {1.5f, -0.84f, 0.15f}, {0.84f, -1.5f, 0.15f}, {0.0f, -1.5f, 0.15f},
      {-1.6f, 0.0f, 2.025f}, {-1.6f, -0.3f, 2.025f}, {-1.5f, -0.3f, 2.25f}, {-1.5f, 0.0f, 2.25f},
      {-2.3f, 0.0f, 2.025f}, {-2.3f, -0.3f, 2.025f}, {-2.5f, -0.3f, 2.25f}, {-2.5f, 0.0f, 2.25f},
      {-2.7f, 0.0f, 2.025f}, {-2.7f, -0.3f, 2.025f}, {-3, -0.3f, 2.25f}, {-3, 0.0f, 2.25f},
      {-2.7f, 0.0f, 1.8f}, {-2.7f, -0.3f, 1.8f}, {-3, -0.3f, 1.8f}, {-3, 0.0f, 1.8f},
      {-2.7f, 0.0f, 1.575f}, {-2.7f, -0.3f, 1.575f}, {-3, -0.3f, 1.35f}, {-3, 0.0f, 1.35f},
      {-2.5f, 0.0f, 1.125f}, {-2.5f, -0.3f, 1.125f}, {-2.65f, -0.3f, 0.9375f},
      {-2.65f, 0.0f, 0.9375f}, {-2, -0.3f, 0.9f}, {-1.9f, -0.3f, 0.6f}, {-1.9f, 0.0f, 0.6f},
      {1.7f, 0.0f, 1.425f}, {1.7f, -0.66f, 1.425f}, {1.7f, -0.66f, 0.6f}, {1.7f, 0.0f, 0.6f},
      {2.6f, 0.0f, 1.425f}, {2.6f, -0.66f, 1.425f}, {3.1f, -0.66f, 0.825f}, {3.1f, 0.0f, 0.825f},
      {2.3f, 0.0f, 2.1f}, {2.3f, -0.25f, 2.1f}, {2.4f, -0.25f, 2.025f}, {2.4f, 0.0f, 2.025f},
      {2.7f, 0.0f, 2.4f}, {2.7f, -0.25f, 2.4f}, {3.3f, -0.25f, 2.4f}, {3.3f, 0.0f, 2.4f},
      {2.8f, 0.0f, 2.475f}, {2.8f, -0.25f, 2.475f}, {3.525f, -0.25f, 2.49375f},
      {3.525f, 0.0f, 2.49375f}, {2.9f, 0.0f, 2.475f}, {2.9f, -0.15f, 2.475f},
      {3.45f, -0.15f, 2.5125f}, {3.45f, 0.0f, 2.5125f}, {2.8f, 0.0f, 2.4f}, {2.8f, -0.15f, 2.4f},
      {3.2f, -0.15f, 2.4f}, {3.2f, 0.0f, 2.4f}, {0.0f, 0.0f, 3.15f}, {0.8f, 0.0f, 3.15f},
      {0.8f, -0.45f, 3.15f}, {0.45f, -0.8f, 3.15f}, {0.0f, -0.8f, 3.15f}, {0.0f, 0.0f, 2.85f},
      {1.4f, 0.0f, 2.4f}, {1.4f, -0.784f, 2.4f}, {0.784f, -1.4f, 2.4f}, {0.0f, -1.4f, 2.4f},
      {0.4f, 0.0f, 2.55f}, {0.4f, -0.224f, 2.55f}, {0.224f, -0.4f, 2.55f}, {0.0f, -0.4f, 2.55f},
      {1.3f, 0.0f, 2.55f}, {1.3f, -0.728f, 2.55f}, {0.728f, -1.3f, 2.55f}, {0.0f, -1.3f, 2.55f},
      {1.3f, 0.0f, 2.4f}, {1.3f, -0.728f, 2.4f}, {0.728f, -1.3f, 2.4f}, {0.0f, -1.3f, 2.4f},
      {0.0f, 0.0f, 0.0f}, {1.425f, -0.798f, 0.0f}, {1.5f, 0.0f, 0.075f}, {1.425f, 0.0f, 0.0f},
      {0.798f, -1.425f, 0.0f}, {0.0f, -1.5f, 0.075f}, {0.0f, -1.425f, 0.0f}, {1.5f, -0.84f, 0.075f},
      {0.84f, -1.5f, 0.075f}};

  // private static int GRD;
  private static int teapots[] = {0, 0};
  private static float tex[][][] = {{{0, 0}, {1, 0}}, {{0, 1}, {1, 1}}};

  public static void Teapot(GL JavaGL, int grid, float scale, int type) {
    float p[][][] = new float[4][4][3];
    float q[][][] = new float[4][4][3];
    float r[][][] = new float[4][4][3];
    float s[][][] = new float[4][4][3];
    int i, j, k, l;

    // if (grid < 2) { grid = 7; }
    // GRD = grid;

    // JavaGL.glPushAttrib (GL.GL_ENABLE_BIT | GL.GL_EVAL_BIT);
    // JavaGL.glEnable (GL.GL_AUTO_NORMAL);
    // JavaGL.glEnable (GL.GL_NORMALIZE);
    // JavaGL.glEnable (GL.GL_MAP2_VERTEX_3);
    // JavaGL.glEnable (GL.GL_MAP2_TEXTURE_COORD_2);

    JavaGL.glPushMatrix();
    JavaGL.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
    JavaGL.glScalef(0.5f * scale, 0.5f * scale, 0.5f * scale);
    JavaGL.glTranslatef(0.0f, 0.0f, -1.5f);
    for (i = 0; i < 10; i++) {
      for (j = 0; j < 4; j++) {
        for (k = 0; k < 4; k++) {
          for (l = 0; l < 3; l++) {
            p[j][k][l] = cpdata[patchdata[i][j * 4 + k]][l];
            q[j][k][l] = cpdata[patchdata[i][j * 4 + (3 - k)]][l];
            if (l == 1) {
              q[j][k][l] *= -1.0f;
            }
            if (i < 6) {
              r[j][k][l] = cpdata[patchdata[i][j * 4 + (3 - k)]][l];
              if (l == 0) {
                r[j][k][l] *= -1.0f;
              }
              s[j][k][l] = cpdata[patchdata[i][j * 4 + k]][l];
              if (l == 0) {
                s[j][k][l] *= -1.0f;
              }
              if (l == 1) {
                s[j][k][l] *= -1.0f;
              }
            }
          }
        }
      }
      JavaGL.glMap2f(GL.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2, tex);
      JavaGL.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, p);
      JavaGL.glEnable(GL.GL_MAP2_VERTEX_3);
      JavaGL.glEnable(GL.GL_MAP2_TEXTURE_COORD_2);
      JavaGL.glMapGrid2f(grid, 0.0f, 1.0f, grid, 0.0f, 1.0f);
      JavaGL.glEvalMesh2(type, 0, grid, 0, grid);
      JavaGL.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, q);
      JavaGL.glEvalMesh2(type, 0, grid, 0, grid);
      if (i < 6) {
        JavaGL.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, r);
        JavaGL.glEvalMesh2(type, 0, grid, 0, grid);
        JavaGL.glMap2f(GL.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, s);
        JavaGL.glEvalMesh2(type, 0, grid, 0, grid);
      }
    }
    JavaGL.glDisable(GL.GL_MAP2_VERTEX_3);
    JavaGL.glDisable(GL.GL_MAP2_TEXTURE_COORD_2);
    JavaGL.glPopMatrix();

    // JavaGL.glPushAttrib ();
  }

  public static void aux_wire_teapot(GL JavaGL, double scale) {
    if (!JavaGL.glIsList(teapots[TEAPOTWIRE])) {
      teapots[TEAPOTWIRE] = JavaGL.glGenLists(1);
      JavaGL.glNewList(teapots[TEAPOTWIRE], GL.GL_COMPILE);
      Teapot(JavaGL, 10, (float) scale, GL.GL_LINE);
      JavaGL.glEndList();
    }
    JavaGL.glCallList(teapots[TEAPOTWIRE]);
  }

  public static void aux_solid_teapot(GL JavaGL, double scale) {
    if (!JavaGL.glIsList(teapots[TEAPOTSOLID])) {
      teapots[TEAPOTSOLID] = JavaGL.glGenLists(1);
      JavaGL.glNewList(teapots[TEAPOTSOLID], GL.GL_COMPILE);
      // Teapot (JavaGL, 14, (float)scale, GL.GL_FILL);
      Teapot(JavaGL, 7, (float) scale, GL.GL_FILL);
      JavaGL.glEndList();
    }
    JavaGL.glCallList(teapots[TEAPOTSOLID]);
  }

}
