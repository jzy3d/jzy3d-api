/*
 * @(#)GLAUX.java 0.2 99/06/22
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-1999 Robin Bing-Yu Chen
 * (robin@csie.ntu.edu.tw)
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

package jgl.wt.awt;

import java.applet.Applet;
import java.awt.Component;

import jgl.glaux.MODELPTR;
import jgl.glaux.mat_t;
import jgl.glaux.teapot;
import jgl.glu.GLUquadricObj;

/**
 * GLAUX is the aux class of JavaGL 2.1.
 *
 * @version 0.2, 22 Jun 1999
 * @author Robin Bing-Yu Chen
 */

public class GLAUX {

  /**
   * Aux Functions Java is not C language, not all the auxiliary functions can be implemented, only
   * implement some useful functions.
   */

  /** Constants of Aux */
  private static final int SPHEREWIRE = 0;
  private static final int CUBEWIRE = 1;
  private static final int BOXWIRE = 2;
  private static final int TORUSWIRE = 3;
  private static final int CYLINDERWIRE = 4;
  private static final int ICOSAWIRE = 5;
  private static final int OCTAWIRE = 6;
  private static final int TETRAWIRE = 7;
  private static final int DODECAWIRE = 8;
  private static final int CONEWIRE = 9;

  private static final int SPHERESOLID = 10;
  private static final int CUBESOLID = 11;
  private static final int BOXSOLID = 12;
  private static final int TORUSSOLID = 13;
  private static final int CYLINDERSOLID = 14;
  private static final int ICOSASOLID = 15;
  private static final int OCTASOLID = 16;
  private static final int TETRASOLID = 17;
  private static final int DODECASOLID = 18;
  private static final int CONESOLID = 19;

  /** Private Data Members */
	private GL JavaGL;
  private GLU JavaGLU;
  // private Applet JavaApplet;
  // private Component JavaComponent;

  private MODELPTR lists[] = new MODELPTR[25];

  private int WindowX;
  private int WindowY;
  private int WindowWidth;
  private int WindowHeight;

  private static final int STACKDEPTH = 10;
  private static final mat_t matstack[] = new mat_t[STACKDEPTH];

  static {
    matstack[0] = new mat_t(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
  }

  private static final boolean identitymat = true;
  private static final int mattop = 0;

  /** Private Member Functions */
  private void error(String s) {
    // Cancel the "errfunc" in aux of OpenGL
    System.out.println(s);
    System.exit(1);
  }

  private void diff3(double p[], double q[], double diff[]) {
    diff[0] = p[0] - q[0];
    diff[1] = p[1] - q[1];
    diff[2] = p[2] - q[2];
  }

  private void crossprod(double v1[], double v2[], double prod[]) {
    double p[] = new double[3]; /* in case prod == v1 or v2 */

    p[0] = v1[1] * v2[2] - v2[1] * v1[2];
    p[1] = v1[2] * v2[0] - v2[2] * v1[0];
    p[2] = v1[0] * v2[1] - v2[0] * v1[1];
    prod[0] = p[0];
    prod[1] = p[1];
    prod[2] = p[2];
  }

  private void normalize(double v[]) {
    double d;

    d = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    if (d == 0.0) {
      error("normalize: zero length vector");
      v[0] = d = 1.0;
    }
    d = 1 / d;
    v[0] *= d;
    v[1] *= d;
    v[2] *= d;
  }

  private void m_xformpt(double pin[], double pout[], double nin[], double nout[]) {
    int i;
    double ptemp[] = new double[3];
    double ntemp[] = new double[3];
    mat_t m = matstack[mattop];

    if (identitymat) {
      for (i = 0; i < 3; i++) {
        pout[i] = pin[i];
        nout[i] = nin[i];
      }
      return;
    }
    for (i = 0; i < 3; i++) {
      ptemp[i] = pin[0] * m.mat[0][i] + pin[1] * m.mat[1][i] + pin[2] * m.mat[2][i] + m.mat[3][i];
      ntemp[i] = nin[0] * m.norm[0][i] + nin[1] * m.norm[1][i] + nin[2] * m.norm[2][i];
    }
    for (i = 0; i < 3; i++) {
      pout[i] = ptemp[i];
      nout[i] = ntemp[i];
    }
    normalize(nout);
  }

  private void m_xformptonly(double pin[], double pout[]) {
    int i;
    double ptemp[] = new double[3];
    mat_t m = matstack[mattop];

    if (identitymat) {
      for (i = 0; i < 3; i++) {
        pout[i] = pin[i];
      }
      return;
    }
    for (i = 0; i < 3; i++) {
      ptemp[i] = pin[0] * m.mat[0][i] + pin[1] * m.mat[1][i] + pin[2] * m.mat[2][i] + m.mat[3][i];
    }
    for (i = 0; i < 3; i++) {
      pout[i] = ptemp[i];
    }
  }

  private void recorditem(double n1[], double n2[], double n3[], double center[], double radius,
      int shadeType, int avnormal) {
    double p1[] = new double[3];
    double p2[] = new double[3];
    double p3[] = new double[3];
    double q0[] = new double[3];
    double q1[] = new double[3];
    double n11[] = new double[3];
    double n22[] = new double[3];
    double n33[] = new double[3];
    int i;

    for (i = 0; i < 3; i++) {
      p1[i] = n1[i] * radius + center[i];
      p2[i] = n2[i] * radius + center[i];
      p3[i] = n3[i] * radius + center[i];
    }
    if (avnormal == 0) {
      diff3(p1, p2, q0);
      diff3(p2, p3, q1);
      crossprod(q0, q1, q1);
      normalize(q1);
      m_xformpt(p1, p1, q1, n11);
      m_xformptonly(p2, p2);
      m_xformptonly(p3, p3);

      JavaGL.glBegin(shadeType);
      JavaGL.glNormal3dv(n11);
      JavaGL.glVertex3dv(p1);
      JavaGL.glVertex3dv(p2);
      JavaGL.glVertex3dv(p3);
      JavaGL.glEnd();
      return;
    }
    m_xformpt(p1, p1, n1, n11);
    m_xformpt(p2, p2, n2, n22);
    m_xformpt(p3, p3, n3, n33);

    JavaGL.glBegin(shadeType);
    JavaGL.glNormal3dv(n11);
    JavaGL.glVertex3dv(p1);
    JavaGL.glNormal3dv(n22);
    JavaGL.glVertex3dv(p2);
    JavaGL.glNormal3dv(n33);
    JavaGL.glVertex3dv(p3);
    JavaGL.glEnd();
  }

  private void subdivide(int depth, double v0[], double v1[], double v2[], double p0[],
      double radius, int shadeType, int avnormal) {
    double w0[] = new double[3];
    double w1[] = new double[3];
    double w2[] = new double[3];
    double l;
    int i, j, k, n;

    for (i = 0; i < depth; i++) {
      for (j = 0; i + j < depth; j++) {
        k = depth - i - j;
        for (n = 0; n < 3; n++) {
          w0[n] = (i * v0[n] + j * v1[n] + k * v2[n]) / depth;
          w1[n] = ((i + 1) * v0[n] + j * v1[n] + (k - 1) * v2[n]) / depth;
          w2[n] = (i * v0[n] + (j + 1) * v1[n] + (k - 1) * v2[n]) / depth;
        }
        l = Math.sqrt(w0[0] * w0[0] + w0[1] * w0[1] + w0[2] * w0[2]);
        w0[0] /= l;
        w0[1] /= l;
        w0[2] /= l;
        l = Math.sqrt(w1[0] * w1[0] + w1[1] * w1[1] + w1[2] * w1[2]);
        w1[0] /= l;
        w1[1] /= l;
        w1[2] /= l;
        l = Math.sqrt(w2[0] * w2[0] + w2[1] * w2[1] + w2[2] * w2[2]);
        w2[0] /= l;
        w2[1] /= l;
        w2[2] /= l;
        recorditem(w1, w0, w2, p0, radius, shadeType, avnormal);
      }
    }
    for (i = 0; i < depth - 1; i++) {
      for (j = 0; i + j < depth - 1; j++) {
        k = depth - i - j;
        for (n = 0; n < 3; n++) {
          w0[n] = ((i + 1) * v0[n] + (j + 1) * v1[n] + (k - 2) * v2[n]) / depth;
          w1[n] = ((i + 1) * v0[n] + j * v1[n] + (k - 1) * v2[n]) / depth;
          w2[n] = (i * v0[n] + (j + 1) * v1[n] + (k - 1) * v2[n]) / depth;
        }
        l = Math.sqrt(w0[0] * w0[0] + w0[1] * w0[1] + w0[2] * w0[2]);
        w0[0] /= l;
        w0[1] /= l;
        w0[2] /= l;
        l = Math.sqrt(w1[0] * w1[0] + w1[1] * w1[1] + w1[2] * w1[2]);
        w1[0] /= l;
        w1[1] /= l;
        w1[2] /= l;
        l = Math.sqrt(w2[0] * w2[0] + w2[1] * w2[1] + w2[2] * w2[2]);
        w2[0] /= l;
        w2[1] /= l;
        w2[2] /= l;
        recorditem(w0, w1, w2, p0, radius, shadeType, avnormal);
      }
    }
  }

  /**
   * Routines to build 3 dimensional solids, including:
   *
   * drawbox, doughnut, icosahedron, octahedron, tetrahedron, dodecahedron.
   */
  /**
   * drawbox:
   *
   * draws a rectangular box with the given x, y, and z ranges. The box is axis-aligned.
   */
  private void drawbox(double x0, double x1, double y0, double y1, double z0, double z1, int type) {
    double n[][] = {{-1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {1.0, 0.0, 0.0}, {0.0, -1.0, 0.0},
        {0.0, 0.0, 1.0}, {0.0, 0.0, -1.0}};
    int faces[][] =
        {{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4}, {4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3}};
    double v[][] = new double[8][3];
    double tmp;
    int i;

    if (x0 > x1) {
      tmp = x0;
      x0 = x1;
      x1 = tmp;
    }
    if (y0 > y1) {
      tmp = y0;
      y0 = y1;
      y1 = tmp;
    }
    if (z0 > z1) {
      tmp = z0;
      z0 = z1;
      z1 = tmp;
    }

    v[0][0] = v[1][0] = v[2][0] = v[3][0] = x0;
    v[4][0] = v[5][0] = v[6][0] = v[7][0] = x1;
    v[0][1] = v[1][1] = v[4][1] = v[5][1] = y0;
    v[2][1] = v[3][1] = v[6][1] = v[7][1] = y1;
    v[0][2] = v[3][2] = v[4][2] = v[7][2] = z0;
    v[1][2] = v[2][2] = v[5][2] = v[6][2] = z1;

    for (i = 0; i < 6; i++) {
      JavaGL.glBegin(type);
      JavaGL.glNormal3dv(n[i]);
      JavaGL.glVertex3dv(v[faces[i][0]]);
      JavaGL.glNormal3dv(n[i]);
      JavaGL.glVertex3dv(v[faces[i][1]]);
      JavaGL.glNormal3dv(n[i]);
      JavaGL.glVertex3dv(v[faces[i][2]]);
      JavaGL.glNormal3dv(n[i]);
      JavaGL.glVertex3dv(v[faces[i][3]]);
      JavaGL.glEnd();
    }
  }

  /**
   * doughnut:
   *
   * draws a doughnut, centered at (0, 0, 0) whose axis is aligned with the z-axis. The doughnut's
   * major radius is R, and minor radius is r.
   */
  private void doughnut(double r, double R, int nsides, int rings, int type) {
    int i, j;
    double theta, phi, theta1, phi1;
    double p0[] = new double[3];
    double p1[] = new double[3];
    double p2[] = new double[3];
    double p3[] = new double[3];
    double n0[] = new double[3];
    double n1[] = new double[3];
    double n2[] = new double[3];
    double n3[] = new double[3];

    for (i = 0; i < rings; i++) {
      theta = (double) i * 2.0 * Math.PI / rings;
      theta1 = (double) (i + 1) * 2.0 * Math.PI / rings;
      for (j = 0; j < nsides; j++) {
        phi = (double) j * 2.0 * Math.PI / nsides;
        phi1 = (double) (j + 1) * 2.0 * Math.PI / nsides;

        p0[0] = Math.cos(theta) * (R + r * Math.cos(phi));
        p0[1] = -Math.sin(theta) * (R + r * Math.cos(phi));
        p0[2] = r * Math.sin(phi);

        p1[0] = Math.cos(theta1) * (R + r * Math.cos(phi));
        p1[1] = -Math.sin(theta1) * (R + r * Math.cos(phi));
        p1[2] = r * Math.sin(phi);

        p2[0] = Math.cos(theta1) * (R + r * Math.cos(phi1));
        p2[1] = -Math.sin(theta1) * (R + r * Math.cos(phi1));
        p2[2] = r * Math.sin(phi1);

        p3[0] = Math.cos(theta) * (R + r * Math.cos(phi1));
        p3[1] = -Math.sin(theta) * (R + r * Math.cos(phi1));
        p3[2] = r * Math.sin(phi1);

        n0[0] = Math.cos(theta) * (Math.cos(phi));
        n0[1] = -Math.sin(theta) * (Math.cos(phi));
        n0[2] = Math.sin(phi);

        n1[0] = Math.cos(theta1) * (Math.cos(phi));
        n1[1] = -Math.sin(theta1) * (Math.cos(phi));
        n1[2] = Math.sin(phi);

        n2[0] = Math.cos(theta1) * (Math.cos(phi1));
        n2[1] = -Math.sin(theta1) * (Math.cos(phi1));
        n2[2] = Math.sin(phi1);

        n3[0] = Math.cos(theta) * (Math.cos(phi1));
        n3[1] = -Math.sin(theta) * (Math.cos(phi1));
        n3[2] = Math.sin(phi1);

        m_xformpt(p0, p0, n0, n0);
        m_xformpt(p1, p1, n1, n1);
        m_xformpt(p2, p2, n2, n2);
        m_xformpt(p3, p3, n3, n3);

        JavaGL.glBegin(type);
        JavaGL.glNormal3dv(n3);
        JavaGL.glVertex3dv(p3);
        JavaGL.glNormal3dv(n2);
        JavaGL.glVertex3dv(p2);
        JavaGL.glNormal3dv(n1);
        JavaGL.glVertex3dv(p1);
        JavaGL.glNormal3dv(n0);
        JavaGL.glVertex3dv(p0);
        JavaGL.glEnd();
      }
    }
  }

  /**
   * icosahedron:
   *
   * Draws an icosahedron with center at p0 having the given radius.
   */
  private void icosahedron(double p0[], double radius, int shadeType) {
    int i;

    for (i = 0; i < 20; i++) {
      // drawtriangle (i, 0, 1, p0, radius, shadeType, 0);
    }
  }

  /**
   * linked lists--display lists for each different type of geometric objects. The linked list is
   * searched, until an object of the requested size is found. If no geometric object of that size
   * has been previously made, a new one is created.
   */
  /** GLuint findList (int index, GLdouble *paramArray, int size) */
  private int findList(int index, double paramArray[], int size) {
    MODELPTR endList;

    endList = lists[index];
    while (endList != null) {
      if (compareParams(endList.params, paramArray, size)) {
        return (endList.list);
      }
      endList = endList.ptr;
    }

    /* if not found, return 0 and calling routine should make a new list */
    return (0);
  }

  /** int compareParams (GLdouble *oneArray, GLdouble *twoArray, int size) */
  private boolean compareParams(double oneArray[], double twoArray[], int size) {
    int i;
    boolean matches = true;

    for (i = 0; (i < size) && matches; i++) {
      if (oneArray[i] != twoArray[i]) {
        matches = false;
      }
    }
    return (matches);
  }

  /** GLuint makeModelPtr (int index, GLdouble *sizeArray, int count) */
  private int makeModelPtr(int index, double sizeArray[], int count) {
    MODELPTR newModel;

    newModel = new MODELPTR();
    newModel.list = JavaGL.glGenLists(1);
    newModel.numParam = count;
    newModel.params = sizeArray;
    newModel.ptr = lists[index];
    lists[index] = newModel;

    return (newModel.list);
  }

  public GL auxGetGL() {
    return JavaGL;
  }

  public GLU auxGetGLU() {
    return JavaGLU;
  }

  /** void auxInitPosition (int x, int y, int width, int height) */
  public void auxInitPosition(int x, int y, int width, int height) {
    WindowX = x;
    WindowY = y;
    WindowWidth = width;
    WindowHeight = height;
  }

  /** GLenum auxInitWindow (char *title) */
  // public void auxInitWindow (Applet o) {
  public void auxInitWindow(Component o) {
    // JavaApplet = o;
    // JavaComponent = o;
    // call resize to make the correct size
    // WindowX and WindowY have no meanings in applet
    // o.resize (WindowWidth, WindowHeight);
    o.setSize(WindowWidth, WindowHeight);
    JavaGL.glXMakeCurrent(o, WindowX, WindowY);
    // reshape has not been used in Java2, and seems redundant here
    // o.reshape (WindowX, WindowY, WindowWidth, WindowHeight);
  }

  public void auxInitWindow(Applet o) {
    auxInitWindow((Component) o);
  }

  /**
   * Render wire frame or solid sphere. If no display list with the current model size exists,
   * create a new display list.
   */
  /** void auxWireSphere (GLdouble radius) */
  public void auxWireSphere(double radius) {
    GLUquadricObj quadObj;
    double sizeArray[] = {radius};
    int displayList = findList(SPHEREWIRE, sizeArray, 1);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(SPHEREWIRE, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
      JavaGLU.gluSphere(quadObj, radius, 16, 16);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidSphere (GLdouble radius) */
  public void auxSolidSphere(double radius) {
    GLUquadricObj quadObj;
    double sizeArray[] = {radius};
    int displayList = findList(SPHERESOLID, sizeArray, 1);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(SPHERESOLID, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
      JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
      JavaGLU.gluSphere(quadObj, radius, 16, 16);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid cube. If no display list with the current model size exists, create
   * a new display list.
   */
  /** void auxWireCube (GLdouble size) */
  public void auxWireCube(double size) {
    double sizeArray[] = {size};
    int displayList = findList(CUBEWIRE, sizeArray, 1);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CUBEWIRE, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      drawbox(-size / 2.0, size / 2.0, -size / 2.0, size / 2.0, -size / 2.0, size / 2.0,
          GL.GL_LINE_LOOP);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidCube (GLdouble size) */
  public void auxSolidCube(double size) {
    double sizeArray[] = {size};
    int displayList = findList(CUBESOLID, sizeArray, 1);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CUBESOLID, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      drawbox(-size / 2.0, size / 2.0, -size / 2.0, size / 2.0, -size / 2.0, size / 2.0,
          GL.GL_QUADS);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid cube. If no display list with the current model size exists, create
   * a new display list.
   */
  /** void auxWireBox (GLdouble width, GLdouble height, GLdouble depth) */
  public void auxWireBox(double width, double height, double depth) {
    double sizeArray[] = {width, height, depth};
    int displayList = findList(BOXWIRE, sizeArray, 3);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(BOXWIRE, sizeArray, 3), GL.GL_COMPILE_AND_EXECUTE);
      drawbox(-width / 2.0, width / 2.0, -height / 2.0, height / 2.0, -depth / 2.0, depth / 2.0,
          GL.GL_LINE_LOOP);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidBox (GLdouble width, GLdouble height, GLdouble depth) */
  public void auxSolidBox(double width, double height, double depth) {
    double sizeArray[] = {width, height, depth};
    int displayList = findList(BOXSOLID, sizeArray, 3);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(BOXSOLID, sizeArray, 3), GL.GL_COMPILE_AND_EXECUTE);
      drawbox(-width / 2.0, width / 2.0, -height / 2.0, height / 2.0, -depth / 2.0, depth / 2.0,
          GL.GL_QUADS);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid tori. If no display list with the current model size exists, create
   * a new display list.
   */
  /** void auxWireTorus (GLdouble innerRadius, GLdouble outerRadius) */
  public void auxWireTorus(double innerRadius, double outerRadius) {
    double sizeArray[] = {innerRadius, outerRadius};
    int displayList = findList(TORUSWIRE, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(TORUSWIRE, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      doughnut(innerRadius, outerRadius, 5, 10, GL.GL_LINE_LOOP);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidTorus (GLdouble innerRadius, GLdouble outerRadius) */
  public void auxSolidTorus(double innerRadius, double outerRadius) {
    double sizeArray[] = {innerRadius, outerRadius};
    int displayList = findList(TORUSSOLID, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(TORUSSOLID, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      doughnut(innerRadius, outerRadius, 8, 15, GL.GL_QUADS);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid cylinders. If no display list with the current model size exists,
   * create a new display list.
   */
  /** void auxWireCylinder (GLdouble radius, GLdouble height) */
  public void auxWireCylinder(double radius, double height) {
    GLUquadricObj quadObj;
    double sizeArray[] = {radius, height};
    int displayList = findList(CYLINDERWIRE, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CYLINDERWIRE, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      JavaGL.glPushMatrix();
      JavaGL.glRotatef((float) 90.0, (float) 1.0, (float) 0.0, (float) 0.0);
      JavaGL.glTranslatef((float) 0.0, (float) 0.0, (float) -1.0);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
      JavaGLU.gluCylinder(quadObj, radius, radius, height, 12, 2);
      JavaGL.glPopMatrix();
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidCylinder (GLdouble radius, GLdouble height) */
  public void auxSolidCylinder(double radius, double height) {
    GLUquadricObj quadObj;
    double sizeArray[] = {radius, height};
    int displayList = findList(CYLINDERSOLID, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CYLINDERSOLID, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      JavaGL.glPushMatrix();
      JavaGL.glRotatef((float) 90.0, (float) 1.0, (float) 0.0, (float) 0.0);
      JavaGL.glTranslatef((float) 0.0, (float) 0.0, (float) -1.0);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
      JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
      JavaGLU.gluCylinder(quadObj, radius, radius, height, 12, 2);
      JavaGL.glPopMatrix();
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid icosahedra. If no display list with the current model size exists,
   * create a new display list.
   */
  /** void auxWireIcosahedron (GLdouble radius) */
  public void auxWireIcosahedron(double radius) {
    double sizeArray[] = {radius};
    int displayList = findList(ICOSAWIRE, sizeArray, 1);
    double center[] = {0.0, 0.0, 0.0};

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(ICOSAWIRE, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      icosahedron(center, radius, GL.GL_LINE_LOOP);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidIcosahedron (GLdouble radius) */
  public void auxSolidIcosahedron(double radius) {
    double sizeArray[] = {radius};
    int displayList = findList(ICOSASOLID, sizeArray, 1);
    double center[] = {0.0, 0.0, 0.0};

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(ICOSASOLID, sizeArray, 1), GL.GL_COMPILE_AND_EXECUTE);
      icosahedron(center, radius, GL.GL_TRIANGLES);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /**
   * Render wire frame or solid cones. If no display list with the current model size exists, create
   * a new display list.
   */
  /** void auxWireCone (GLdouble base, GLdouble height) */
  public void auxWireCone(double base, double height) {
    GLUquadricObj quadObj;
    double sizeArray[] = {base, height};
    int displayList = findList(CONEWIRE, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CONEWIRE, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
      JavaGLU.gluCylinder(quadObj, base, 0.0, height, 15, 10);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxSolidCone (GLdouble base, GLdouble height) */
  public void auxSolidCone(double base, double height) {
    GLUquadricObj quadObj;
    double sizeArray[] = {base, height};
    int displayList = findList(CONESOLID, sizeArray, 2);

    if (displayList == 0) {
      JavaGL.glNewList(makeModelPtr(CONESOLID, sizeArray, 2), GL.GL_COMPILE_AND_EXECUTE);
      quadObj = JavaGLU.gluNewQuadric();
      JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
      JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
      JavaGLU.gluCylinder(quadObj, base, 0.0, height, 15, 10);
      JavaGL.glEndList();
    } else {
      JavaGL.glCallList(displayList);
    }
  }

  /** void auxWireTeapot (GLdouble scale) */
  public void auxWireTeapot(double scale) {
    teapot.aux_wire_teapot(JavaGL, scale);
  }

  /** void auxSolidTeapot (GLdouble scale) */
  public void auxSolidTeapot(double scale) {
    teapot.aux_solid_teapot(JavaGL, scale);
  }

  public GLAUX() {
    System.out.println("Please call new GLAUX (yourGL)");
  }

	public GLAUX(GL myGL) {
    JavaGL = myGL;
    JavaGLU = new GLU(JavaGL);
  }

}
