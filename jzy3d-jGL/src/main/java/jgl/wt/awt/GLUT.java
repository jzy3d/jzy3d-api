/*
 * @(#)GLUT.java 0.3 03/05/10
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001-2003 Robin Bing-Yu Chen
 * (robin@nis-lab.is.s.u-tokyo.ac.jp)
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
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import jgl.glaux.teapot;
import jgl.glu.GLUquadricObj;
import jgl.glut.glut_menu;

/**
 * GLUT is the glut class of jGL 2.4.
 *
 * @version 0.3, 10 May 2003
 * @author Robin Bing-Yu Chen
 */

public class GLUT implements Runnable {

	public GLUT(GL myGL) {
    JavaGL = myGL;
    JavaGLU = new GLU(JavaGL);
  }



  /** Private Data Members */
	private GL JavaGL;
  private GLU JavaGLU;
  // private GLCanvas JavaCanvas;

  private int WindowX = 0;
  private int WindowY = 0;
  private int WindowWidth = 300;
  private int WindowHeight = 300;

  private GLUquadricObj quadObj;

  private Method reshapeMethod = null;
  private Method mouseMethod = null;
  private Method motionMethod = null;
  private Method keyMethod = null;
  private Method keyUpMethod = null;
  private Method specialKeyMethod = null;
  private Method specialKeyUpMethod = null;
  private Method displayMethod = null;
  private Method idleMethod = null;
  private Thread JavaThread = null;
  private Component JavaComponent = null;

  private Vector<glut_menu> JavaMenus = null;
  private int JavaMenuSize = 0;
  private glut_menu currentMenu = null;
  private int JavaMenuButton = -1;
  private int keyModifiers = 0;

  /**
   * Print string at the specified 2d position.
   * 
   * This method is not following exactly the GLUT interface. Printing text in OpenGL usually
   * requires such code :
   * 
   * <pre>
   * <code>
   * gl.glColor3f(r, g, b);
   * gl.glRasterPos3f(x, y, z);
   * glut.glutBitmapString(font, string);
   * </code>
   * </pre>
   * 
   * In our case, one need to first perform model-to-screen projection to get x,y coordinates, and
   * then call glutBitmapString.
   * 
   * @see {@link #glutBitmapString(Font, String, float, float, float, float, float, float) to avoid
   *      doing the model-to-screen projection.
   */
  public void glutBitmapString(Font font, String string, float x, float y) {
    JavaGL.appendTextToDraw(font, string, (int) x, (int) y);
  }

  /**
   * Print string at the specified 3d position.
   * 
   * This method is not following exactly the GLUT interface. Printing text in OpenGL usually
   * requires such code :
   * 
   * <pre>
   * <code>
   * gl.glColor3f(r, g, b);
   * gl.glRasterPos3f(x, y, z);
   * glut.glutBitmapString(int font, string);
   * </code>
   * </pre>
   * 
   * Here we provide a convenient function that does all in one pass
   * 
   * <pre>
   * <code>
   * glut.glutBitmapString(java.awt.Font, java.lang.String, x, y, z, r, g, b);
   * </code>
   * </pre>
   * 
   * Behind the scene it makes the model-to-screen conversion and then provide all data to
   * {@link GL#appendTextToDraw(Font, String, int, int, float, float, float)} that will handle the
   * text rendering in {@link GL#glFlush()}
   */
  public void glutBitmapString(Font font, String string, float x, float y, float z, float r,
      float g, float b, float rotate) {
    double[] win = modelToScreen(x, y, z);

    double winX = win[0];
	double winY = JavaGL.getContext().Viewport.Height - win[1];


    JavaGL.appendTextToDraw(font, string, (int) winX, (int) winY, r, g, b, rotate);
  }

  protected double[] modelToScreen(float x, float y, float z) {
    int viewport[] = getViewPortAsInt();

    double winx[] = new double[1];
    double winy[] = new double[1];
    double winz[] = new double[1];

    if (!JavaGLU.gluProject(x, y, z, getModelViewAsDouble(), getProjectionAsDouble(), viewport,
        winx, winy, winz))
      System.err.println("GLUT.modelToScreen : Could not retrieve model coordinates in screen for "
          + x + ", " + y + ", " + z);

    double[] win = new double[3];
    win[0] = winx[0];
    win[1] = winy[0];
    win[2] = winz[0];

    return win;
  }

  protected int[] getViewPortAsInt() {
    int viewport[] = new int[4];
    JavaGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);
    return viewport;
  }

  protected double[] getModelViewAsDouble() {
    double modelview[] = new double[16];
    JavaGL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
    return modelview;
  }

  protected double[] getProjectionAsDouble() {
    double projection[] = new double[16];
    JavaGL.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);
    return projection;
  }
  // </pre>


  /** Private Member Functions */
  /*
   * private void error (String s) { // Cancel the "errfunc" in aux of OpenGL System.out.println
   * (s); System.exit (1); }
   * 
   * private void DIFF3 (double p[], double q[], double diff[]) { diff[0] = p[0] - q[0]; diff[1] =
   * p[1] - q[1]; diff[2] = p[2] - q[2]; }
   * 
   * private void crossprod (double v1[], double v2[], double prod[]) { double p[] = new double [3];
   */
  /* in case prod == v1 or v2 */
  /*
   * p[0] = v1[1]*v2[2] - v2[1]*v1[2]; p[1] = v1[2]*v2[0] - v2[2]*v1[0]; p[2] = v1[0]*v2[1] -
   * v2[0]*v1[1]; prod[0] = p[0]; prod[1] = p[1]; prod[2] = p[2]; }
   * 
   * private void normalize (double v[]) { double d;
   * 
   * d = Math.sqrt (v [0] * v [0] + v [1] * v [1] + v [2] * v [2]); if (d == 0.0) { error
   * ("normalize: zero length vector"); v [0] = d = 1.0; } d = 1/d; v[0] *= d; v[1] *= d; v[2] *= d;
   * }
   * 
   * private void m_xformpt (double pin [], double pout [], double nin [], double nout []) { int i;
   * double ptemp [] = new double [3]; double ntemp [] = new double [3]; mat_t m = matstack
   * [mattop];
   * 
   * if (identitymat) { for (i = 0; i < 3; i++) { pout [i] = pin [i]; nout [i] = nin [i]; } return;
   * } for (i = 0; i < 3; i++) { ptemp [i] = pin [0] * m.mat [0][i] + pin [1] * m.mat [1][i] + pin
   * [2] * m.mat [2][i] + m.mat[3][i]; ntemp [i] = nin [0] * m.norm [0][i] + nin [1] * m.norm [1][i]
   * + nin [2] * m.norm [2][i]; } for (i = 0; i < 3; i++) { pout [i] = ptemp [i]; nout [i] = ntemp
   * [i]; } normalize(nout); }
   * 
   * private void m_xformptonly (double pin[], double pout[]) { int i; double ptemp[] = new double
   * [3]; mat_t m = matstack [mattop];
   * 
   * if (identitymat) { for (i = 0; i < 3; i++) { pout[i] = pin[i]; } return; } for (i = 0; i < 3;
   * i++) { ptemp[i] = pin[0]*m.mat[0][i] + pin[1]*m.mat[1][i] + pin[2]*m.mat[2][i] + m.mat[3][i]; }
   * for (i = 0; i < 3; i++) { pout[i] = ptemp[i]; } }
   * 
   * private void recorditem (double n1 [], double n2 [], double n3 [], double center [], double
   * radius, int shadeType, int avnormal) { double p1 [] = new double [3]; double p2 [] = new double
   * [3]; double p3 [] = new double [3]; double q0 [] = new double [3]; double q1 [] = new double
   * [3]; double n11 [] = new double [3]; double n22 [] = new double [3]; double n33 [] = new double
   * [3]; int i;
   * 
   * for (i = 0; i < 3; i++) { p1[i] = n1[i]*radius + center[i]; p2[i] = n2[i]*radius + center[i];
   * p3[i] = n3[i]*radius + center[i]; } if (avnormal == 0) { diff3(p1, p2, q0); diff3(p2, p3, q1);
   * crossprod(q0, q1, q1); normalize(q1); m_xformpt(p1, p1, q1, n11); m_xformptonly(p2, p2);
   * m_xformptonly(p3, p3);
   * 
   * JavaGL.glBegin (shadeType); JavaGL.glNormal3dv(n11); JavaGL.glVertex3dv(p1);
   * JavaGL.glVertex3dv(p2); JavaGL.glVertex3dv(p3); JavaGL.glEnd(); return; } m_xformpt(p1, p1, n1,
   * n11); m_xformpt(p2, p2, n2, n22); m_xformpt(p3, p3, n3, n33);
   * 
   * JavaGL.glBegin (shadeType); JavaGL.glNormal3dv(n11); JavaGL.glVertex3dv(p1);
   * JavaGL.glNormal3dv(n22); JavaGL.glVertex3dv(p2); JavaGL.glNormal3dv(n33);
   * JavaGL.glVertex3dv(p3); JavaGL.glEnd(); }
   * 
   * private void subdivide (int depth, double v0 [], double v1 [], double v2 [], double p0 [],
   * double radius, int shadeType, int avnormal) { double w0 [] = new double [3]; double w1 [] = new
   * double [3]; double w2 [] = new double [3]; double l; int i, j, k, n;
   * 
   * for (i = 0; i < depth; i++) { for (j = 0; i + j < depth; j++) { k = depth - i - j; for (n = 0;
   * n < 3; n++) { w0[n] = (i*v0[n] + j*v1[n] + k*v2[n])/depth; w1[n] = ((i+1)*v0[n] + j*v1[n] +
   * (k-1)*v2[n])/depth; w2[n] = (i*v0[n] + (j+1)*v1[n] + (k-1)*v2[n])/depth; } l =
   * Math.sqrt(w0[0]*w0[0] + w0[1]*w0[1] + w0[2]*w0[2]); w0[0] /= l; w0[1] /= l; w0[2] /= l; l =
   * Math.sqrt(w1[0]*w1[0] + w1[1]*w1[1] + w1[2]*w1[2]); w1[0] /= l; w1[1] /= l; w1[2] /= l; l =
   * Math.sqrt(w2[0]*w2[0] + w2[1]*w2[1] + w2[2]*w2[2]); w2[0] /= l; w2[1] /= l; w2[2] /= l;
   * recorditem(w1, w0, w2, p0, radius, shadeType, avnormal); } } for (i = 0; i < depth-1; i++) {
   * for (j = 0; i + j < depth-1; j++) { k = depth - i - j; for (n = 0; n < 3; n++) { w0[n] =
   * ((i+1)*v0[n] + (j+1)*v1[n] + (k-2)*v2[n])/depth; w1[n] = ((i+1)*v0[n] + j*v1[n] +
   * (k-1)*v2[n])/depth; w2[n] = (i*v0[n] + (j+1)*v1[n] + (k-1)*v2[n])/depth; } l =
   * Math.sqrt(w0[0]*w0[0] + w0[1]*w0[1] + w0[2]*w0[2]); w0[0] /= l; w0[1] /= l; w0[2] /= l; l =
   * Math.sqrt(w1[0]*w1[0] + w1[1]*w1[1] + w1[2]*w1[2]); w1[0] /= l; w1[1] /= l; w1[2] /= l; l =
   * Math.sqrt(w2[0]*w2[0] + w2[1]*w2[1] + w2[2]*w2[2]); w2[0] /= l; w2[1] /= l; w2[2] /= l;
   * recorditem(w0, w1, w2, p0, radius, shadeType, avnormal); } } }
   */

  private void drawBox(float size, int type) {
    float n[][] = {{-1.0f, 0.0f, 0.0f}, {0.0f, 1.0f, 0.0f}, {1.0f, 0.0f, 0.0f}, {0.0f, -1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f}, {0.0f, 0.0f, -1.0f}};
    int faces[][] =
        {{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4}, {4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3}};
    float v[][] = new float[8][3];
    int i;

    v[0][0] = v[1][0] = v[2][0] = v[3][0] = -size / 2;
    v[4][0] = v[5][0] = v[6][0] = v[7][0] = size / 2;
    v[0][1] = v[1][1] = v[4][1] = v[5][1] = -size / 2;
    v[2][1] = v[3][1] = v[6][1] = v[7][1] = size / 2;
    v[0][2] = v[3][2] = v[4][2] = v[7][2] = -size / 2;
    v[1][2] = v[2][2] = v[5][2] = v[6][2] = size / 2;

    for (i = 5; i >= 0; i--) {
      JavaGL.glBegin(type);
      JavaGL.glNormal3fv(n[i]);
      JavaGL.glVertex3fv(v[faces[i][0]]);
      JavaGL.glVertex3fv(v[faces[i][1]]);
      JavaGL.glVertex3fv(v[faces[i][2]]);
      JavaGL.glVertex3fv(v[faces[i][3]]);
      JavaGL.glEnd();
    }
  }

  private void doughnut(float r, float R, int nsides, int rings) {
    int i, j;
    float theta, phi, theta1;
    float cosTheta, sinTheta;
    float cosTheta1, sinTheta1;
    float ringDelta, sideDelta;

    ringDelta = 2.0f * (float) Math.PI / rings;
    sideDelta = 2.0f * (float) Math.PI / nsides;

    theta = 0.0f;
    cosTheta = 1.0f;
    sinTheta = 0.0f;

    for (i = rings - 1; i >= 0; i--) {
      theta1 = theta + ringDelta;
      cosTheta1 = (float) Math.cos(theta1);
      sinTheta1 = (float) Math.sin(theta1);
      JavaGL.glBegin(GL.GL_QUAD_STRIP);
      phi = 0.0f;
      for (j = nsides; j >= 0; j--) {
        float cosPhi, sinPhi, dist;

        phi += sideDelta;
        cosPhi = (float) Math.cos(phi);
        sinPhi = (float) Math.sin(phi);
        dist = R + r * cosPhi;

        JavaGL.glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
        JavaGL.glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
        JavaGL.glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
        JavaGL.glVertex3f(cosTheta * dist, -sinTheta * dist, r * sinPhi);
      }
      JavaGL.glEnd();
      theta = theta1;
      cosTheta = cosTheta1;
      sinTheta = sinTheta1;
    }
  }

  private void icosahedron(int shadeType) {
    int i;

    for (i = 19; i >= 0; i--) {
      // drawtriangle (i, idata, index, shadeType);
    }
  }

  public GL auxGetGL() {
    return JavaGL;
  }

  public GLU auxGetGLU() {
    return JavaGLU;
  }

  /**
   * GLUT initialization sub-API.
   */

  /** void glutInitWindowPosition (int x, int y) */
  public void glutInitWindowPosition(int x, int y) {
    WindowX = x;
    WindowY = y;
  }

  /** void glutInitWindowSize (int width, int height) */
  public void glutInitWindowSize(int width, int height) {
    WindowWidth = width;
    WindowHeight = height;
  }

  /** void glutMainLoop () */
  public void glutMainLoop() {
    if (displayMethod != null) {
      try {
        displayMethod.invoke(JavaComponent, (Object[]) null);
      } catch (IllegalAccessException e) {
        System.out.println("IllegalAccessException while DisplayFunc");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        System.out.println("InvocationTargetException while DisplayFunc");
        e.printStackTrace();
      }
    }
  }

  /**
   * GLUT window sub-API.
   */

  /** int glutCreateWindow (const char *title) */
  public void glutCreateWindow(Component o) {
    o.setSize(WindowWidth, WindowHeight);
    JavaGL.glXMakeCurrent(o, WindowX, WindowY);
    JavaComponent = o;
  }

  public void glutCreateWindow(Applet o) {
    glutCreateWindow((Component) o);
  }

  /** void glutPostRedisplay () */
  public void glutPostRedisplay() {
    try {
      displayMethod.invoke(JavaComponent, (Object[]) null);
    } catch (IllegalAccessException e) {
      System.out.println("IllegalAccessException while DisplayFunc");
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      System.out.println("InvocationTargetException while DisplayFunc");
      e.printStackTrace();
    } catch (NullPointerException ee) {
      ee.printStackTrace();
      // ignore
    }
    JavaComponent.repaint();
  }

  /** void glutSwapBuffers () */
  public void glutSwapBuffers(Graphics g, ImageObserver o) {
    JavaGL.glXSwapBuffers(g, o);
  }

  public void glutSwapBuffers(Graphics g, Applet o) {
    glutSwapBuffers(g, (ImageObserver) o);
  }

  private void QUAD_OBJ_INIT() {
    if (quadObj != null)
      return;
    quadObj = JavaGLU.gluNewQuadric();
  }

  /** void glutWireSphere (GLdouble radius, GLint slices, GLint stacks) */
  public void glutWireSphere(double radius, int slices, int stacks) {
    QUAD_OBJ_INIT();
    JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
    JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
    /*
     * If we ever changed/used the texture or orientation state of quadObj, we'd need to change it
     * to the defaults here with gluQuadricTexture and/or gluQuadricOrientation.
     */
    JavaGLU.gluSphere(quadObj, radius, slices, stacks);
  }

  /** void glutSolidSphere (GLdouble radius, GLint slices, GLint stacks) */
  public void glutSolidSphere(double radius, int slices, int stacks) {
    QUAD_OBJ_INIT();
    JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
    JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
    /*
     * If we ever changed/used the texture or orientation state of quadObj, we'd need to change it
     * to the defaults here with gluQuadricTexture and/or gluQuadricOrientation.
     */
    JavaGLU.gluSphere(quadObj, radius, slices, stacks);
  }

  /**
   * void glutWireCone (GLdouble base, GLdouble height, GLint slices, GLint stacks)
   */
  public void glutWireCone(double base, double height, int slices, int stacks) {
    QUAD_OBJ_INIT();
    JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
    JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
    /*
     * If we ever changed/used the texture or orientation state of quadObj, we'd need to change it
     * to the defaults here with gluQuadricTexture and/or gluQuadricOrientation.
     */
    JavaGLU.gluCylinder(quadObj, base, 0.0, height, slices, stacks);
  }

  /**
   * void glutSolidCone (GLdouble base, GLdouble height, GLint slices, GLint stacks)
   */
  public void glutSolidCone(double base, double height, int slices, int stacks) {
    QUAD_OBJ_INIT();
    JavaGLU.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
    JavaGLU.gluQuadricNormals(quadObj, GLU.GLU_SMOOTH);
    /*
     * If we ever changed/used the texture or orientation state of quadObj, we'd need to change it
     * to the defaults here with gluQuadricTexture and/or gluQuadricOrientation.
     */
    JavaGLU.gluCylinder(quadObj, base, 0.0, height, slices, stacks);
  }

  /** void glutWireCube (GLdouble size) */
  public void glutWireCube(double size) {
    drawBox((float) size, GL.GL_LINE_LOOP);
  }

  /** void glutSolidCube (GLdouble size) */
  public void glutSolidCube(double size) {
    drawBox((float) size, GL.GL_QUADS);
  }

  /**
   * void glutWireTorus (GLdouble innerRadius, GLdouble outerRadius, GLint nsides, GLint rings)
   */
  public void glutWireTorus(double innerRadius, double outerRadius, int nsides, int rings) {
    JavaGL.glPushAttrib(GL.GL_POLYGON_BIT);
    JavaGL.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
    doughnut((float) innerRadius, (float) outerRadius, nsides, rings);
    JavaGL.glPopAttrib();
  }

  /**
   * void glutSolidTorus (GLdouble innerRadius, GLdouble outerRadius, GLint nsides, GLint rings)
   */
  public void glutSolidTorus(double innerRadius, double outerRadius, int nsides, int rings) {
    doughnut((float) innerRadius, (float) outerRadius, nsides, rings);
  }

  /** void glutWireIcosahedron (void) */
  public void glutWireIcosahedron() {
    icosahedron(GL.GL_LINE_LOOP);
  }

  /** void glutWireIcosahedron (void) */
  public void glutSolidIcosahedron() {
    icosahedron(GL.GL_TRIANGLES);
  }

  /** void glutWireTeapot (GLdouble scale) */
  public void glutWireTeapot(double scale) {
    teapot.Teapot(JavaGL, 10, (float) scale, GL.GL_LINE);
  }

  /** void glutSolidTeapot (GLdouble scale) */
  public void glutSolidTeapot(double scale) {
    teapot.Teapot(JavaGL, 14, (float) scale, GL.GL_FILL);
  }

  // -------------- GLUT MENU ----------------- //
  /**
   * GLUT menu sub-API.
   */

  /** int glutCreateMenu (void (*)(int)) */
  public int glutCreateMenu(String func) {
    if (JavaMenus == null)
      JavaMenus = new Vector<glut_menu>();
    currentMenu = new glut_menu(func, JavaMenus.size(), JavaComponent);
    JavaMenus.addElement(currentMenu);
    JavaMenuSize++;
    return JavaMenus.size() - 1;
  }

  /** void glutDestroyMenu (int menu) */
  public void glutDestroyMenu(int menu) {
    ((glut_menu) JavaMenus.elementAt(menu)).glutDestroyMenu();
    if ((JavaMenuSize--) == 0)
      JavaMenus = null;
  }

  /** int glutGetMenu (void) */
  public int glutGetMenu() {
    if (currentMenu == null)
      return -1;
    else
      return currentMenu.glutGetMenuID();
  }

  /** void glutSetMenu (int menu) */
  public void glutSetMenu(int menu) {
    JavaComponent.remove(currentMenu.glutGetMenu());
    currentMenu = (glut_menu) JavaMenus.elementAt(menu);
    JavaComponent.add(currentMenu.glutGetMenu());
  }

  /** void glutAddMenuEntry (const char *label, int value) */
  public void glutAddMenuEntry(String label, int value) {
    currentMenu.glutAddMenuEntry(label, value);
  }

  /** void glutAddSubMenu (const char *label, int submenu) */
  public void glutAddSubMenu(String label, int submenu) {
    glut_menu subMenu = (glut_menu) JavaMenus.elementAt(submenu);
    if ((subMenu.glutGetMenu().getLabel() == null)
        || (subMenu.glutGetMenu().getLabel().length() == 0)) {
      currentMenu.glutAddSubMenu(label, subMenu);
    } else {
      glut_menu newSubMenu = subMenu.glutDupMenu();
      newSubMenu.glutSetMenuID(JavaMenus.size());
      JavaMenus.addElement(newSubMenu);
      JavaMenuSize++;
      currentMenu.glutAddSubMenu(label, newSubMenu);
    }
  }

  /** void glutChangeToMenuEntry (int item, const char *label, int value) */
  public void glutChangeToMenuEntry(int item, String label, int value) {
    currentMenu.glutChangeToMenuEntry(item - 1, label, value);
  }

  /** void glutChangeToSubMenu (int item, const char *label, int submenu) */
  public void glutChangeToSubMenu(int item, String label, int submenu) {
    glut_menu subMenu = (glut_menu) JavaMenus.elementAt(submenu);
    if ((subMenu.glutGetMenu().getLabel() == null)
        || (subMenu.glutGetMenu().getLabel().length() == 0)) {
      currentMenu.glutChangeToSubMenu(item - 1, label, subMenu);
    } else {
      glut_menu newSubMenu = subMenu.glutDupMenu();
      newSubMenu.glutSetMenuID(JavaMenus.size());
      JavaMenus.addElement(newSubMenu);
      JavaMenuSize++;
      currentMenu.glutChangeToSubMenu(item - 1, label, newSubMenu);
    }
  }

  /** void glutRemoveMenuItem (int item) */
  public void glutRemoveMenuItem(int item) {
    currentMenu.glutRemoveMenuItem(item - 1);
  }

  /** void glutAttachMenu (int button) */
  public void glutAttachMenu(int button) {
    JavaComponent.add(currentMenu.glutGetMenu());
    // in Java, the menu can only attach on right button
    JavaMenuButton = button;
    glut_enable_events(AWTEvent.MOUSE_EVENT_MASK, true);
  }

  /** void glutDetachMenu (int button) */
  public void glutDetachMenu(int button) {
    // in Java, the menu can only attach on right button
    if (JavaMenuButton == button) {
      if (mouseMethod == null)
        glut_enable_events(AWTEvent.MOUSE_EVENT_MASK, false);
      JavaMenuButton = -1;
    }
    JavaComponent.remove(currentMenu.glutGetMenu());
  }


  // * ************************************************************************ * //

  // ----- GLUT CALLBACKS INVOCATION UPON CANVAS, MOUSE AND KEYBOARD EVENTS ----- //

  // * ************************************************************************ * //

  /**
   * GLUT window callback sub-API to handle events from canvas, mouse or keyboard.
   */
  public void processEvent(AWTEvent e) {
    if (e instanceof MouseEvent) {
      switch (e.getID()) {
        case MouseEvent.MOUSE_PRESSED:
        case MouseEvent.MOUSE_RELEASED:
        case MouseEvent.MOUSE_CLICKED:
        case MouseEvent.MOUSE_ENTERED:
        case MouseEvent.MOUSE_EXITED:
          processMouseEvent((MouseEvent) e);
          break;
        case MouseEvent.MOUSE_MOVED:
        case MouseEvent.MOUSE_DRAGGED:
          processMouseMotionEvent((MouseEvent) e);
          break;
      }

    } else if (e instanceof KeyEvent) {
      processKeyEvent((KeyEvent) e);

    } else if (e instanceof ComponentEvent) {
      processComponentEvent((ComponentEvent) e);
    }
  }

  /**
   * Process component events, i.e. events sent by the {@link GLCanvas} displaying the GL image
   * generated by {@link GL#glFlush()}.
   * 
   * Basically invoke {@link this#reshapeMethod} and {@link this#displayMethod} to update canvas
   * upon {@link ComponentEvent#COMPONENT_RESIZED}.
   * 
   * Registering a reshape or display callback is done through
   * 
   * <pre>
   * <code>
   * glut.glutCreateWindow(classInstanceProvidingDisplayAndReshapeMethod);
   * glut.glutDisplayFunc("nameOfTheDisplayMethod"); 
   * glut.glutReshapeFunc("nameOfTheReshapeMethod");
   * </code>
   * </pre>
   * 
   * @param e
   */
  public void processComponentEvent(ComponentEvent e) {
    if (reshapeMethod == null) {
      System.err
          .println("Reshape method is undefined, please call glut.glutReshapeFunc(\"doReshape\")");
      return;
    }
    if (displayMethod == null) {
      System.err
          .println("Reshape method is undefined, please call glut.glutDIsplayFunc(\"doDisplay\")");
      return;
    }

    int id = e.getID();
    switch (id) {
      case ComponentEvent.COMPONENT_RESIZED:
        Object[] arguments = new Object[] {new Integer(JavaComponent.getSize().width),
            new Integer(JavaComponent.getSize().height)};
        try {
          reshapeMethod.invoke(JavaComponent, arguments);
          try {
            displayMethod.invoke(JavaComponent, (Object[]) null);
          } catch (IllegalAccessException ee) {
            System.out.println("IllegalAccessException while DisplayFunc");
            ee.printStackTrace();
          } catch (InvocationTargetException ee) {
            System.out.println("InvocationTargetException while DisplayFunc");
            ee.printStackTrace();
          } catch (NullPointerException ee) {
            // ignore
            ee.printStackTrace();
          }
        } catch (IllegalAccessException ee) {
          System.out.println("IllegalAccessException while ReshapeFunc");
          ee.printStackTrace();
        } catch (InvocationTargetException ee) {
          System.out.println("InvocationTargetException while ReshapeFunc");
          ee.printStackTrace();
        } catch (NullPointerException ee) {
          // ignore
          ee.printStackTrace();
        }
        break;
    }
  }

  private void processKeyModifiers(int mod) {
    keyModifiers = 0;
    if ((mod & InputEvent.ALT_MASK) != 0)
      keyModifiers |= GLUT_ACTIVE_ALT;
    if ((mod & InputEvent.CTRL_MASK) != 0)
      keyModifiers |= GLUT_ACTIVE_CTRL;
    if ((mod & InputEvent.SHIFT_MASK) != 0)
      keyModifiers |= GLUT_ACTIVE_SHIFT;
  }

  private void invokeKeyMethod(Method met, char ret) {
    Object[] arguments = new Object[] {new Character(ret), new Integer(0), new Integer(0)};
    try {
      met.invoke(JavaComponent, arguments);
    } catch (IllegalAccessException ee) {
      System.out.println("IllegalAccessException while Calling Keyboard Related Functions");
      ee.printStackTrace();
    } catch (InvocationTargetException ee) {
      System.out.println("InvocationTargetException while Calling Keyboard Related Functions");
      ee.printStackTrace();
    } catch (NullPointerException ee) {
      // ignore
      ee.printStackTrace();
    }
  }

  private void processKey(Method met, char key) {
    if ((key < 128) && (key != 0))
      invokeKeyMethod(met, key);
  }

  private void processSpecialKey(Method met, int code) {
    switch (code) {
      case KeyEvent.VK_PAGE_UP:
        invokeKeyMethod(met, (char) GLUT_KEY_PAGE_UP);
        break;
      case KeyEvent.VK_PAGE_DOWN:
        invokeKeyMethod(met, (char) GLUT_KEY_PAGE_DOWN);
        break;
      case KeyEvent.VK_END:
        invokeKeyMethod(met, (char) GLUT_KEY_END);
        break;
      case KeyEvent.VK_HOME:
        invokeKeyMethod(met, (char) GLUT_KEY_HOME);
        break;
      case KeyEvent.VK_LEFT:
        invokeKeyMethod(met, (char) GLUT_KEY_LEFT);
        break;
      case KeyEvent.VK_UP:
        invokeKeyMethod(met, (char) GLUT_KEY_UP);
        break;
      case KeyEvent.VK_RIGHT:
        invokeKeyMethod(met, (char) GLUT_KEY_RIGHT);
        break;
      case KeyEvent.VK_DOWN:
        invokeKeyMethod(met, (char) GLUT_KEY_DOWN);
        break;
      case KeyEvent.VK_INSERT:
        invokeKeyMethod(met, (char) GLUT_KEY_INSERT);
        break;
      default:
        if ((code >= KeyEvent.VK_F1) && (code <= KeyEvent.VK_F12))
          invokeKeyMethod(met, (char) (code - KeyEvent.VK_F1 + GLUT_KEY_F1));
        break;
    }
  }

  public void processKeyEvent(KeyEvent e) {
    switch (e.getID()) {
      case KeyEvent.KEY_PRESSED:
        processKeyModifiers(e.getModifiers());
        if (keyMethod != null)
          processKey(keyMethod, (char) e.getKeyChar());
        if (specialKeyMethod != null)
          processSpecialKey(specialKeyMethod, e.getKeyCode());
        break;
      case KeyEvent.KEY_RELEASED:
        processKeyModifiers(e.getModifiers());
        if (keyUpMethod != null)
          processKey(keyUpMethod, (char) e.getKeyChar());
        if (specialKeyUpMethod != null)
          processSpecialKey(specialKeyUpMethod, e.getKeyCode());
        break;
      /*
       * case KeyEvent.KEY_TYPED: invokeKeyMethod (keyMethod, (char)e.getKeyChar()); break;
       */
    }
  }

  public void processMouseEvent(MouseEvent e) {
    if (mouseMethod == null) {
      return;
    }

    int id = e.getID();
    int button = -1;
    int state = -1;

    if (e.isPopupTrigger() && (JavaMenuButton != -1)) {
      currentMenu.glutGetMenu().show(e.getComponent(), e.getX(), e.getY());
      return;
    }

    switch (id) {
      case MouseEvent.MOUSE_PRESSED:
        state = GLUT_DOWN;
        break;
      case MouseEvent.MOUSE_RELEASED:
        state = GLUT_UP;
        break;
    }
    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
      button = GLUT_LEFT_BUTTON;
    else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0)
      button = GLUT_MIDDLE_BUTTON;
    else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
      button = GLUT_RIGHT_BUTTON;
    if ((button != -1) && (state != -1)) {
      // if ((button == JavaMenuButton) && (state == GLUT_DOWN)) {
      // JavaMenu.show (e.getComponent (), e.getX (), e.getY ());
      // } else {
      Object[] arguments = new Object[] {new Integer(button), new Integer(state),
          new Integer(e.getX()), new Integer(e.getY())};
      try {
        mouseMethod.invoke(JavaComponent, arguments);
      } catch (IllegalAccessException ee) {
        System.out.println("IllegalAccessException while MouseFunc");
        ee.printStackTrace();
      } catch (InvocationTargetException ee) {
        System.out.println("InvocationTargetException while MouseFunc");
        ee.printStackTrace();
      } catch (NullPointerException ee) {
        ee.printStackTrace();
      }
    }
    // }
  }

  /**
   * Process mouse motion events.
   * 
   * Basically invoke {@link this#motionMethod} upon {@link MouseEvent#MOUSE_MOVED} and
   * {@link MouseEvent#MOUSE_DRAGGED}.
   * 
   * Registering a reshape or display callback is done through
   * 
   * <pre>
   * <code>
   * glut.glutCreateWindow(classInstanceProvidingDisplayAndReshapeMethod);
   * glut.glutMotionFunc("nameOfTheDisplayMethod"); 
   * </code>
   * </pre>
   * 
   * 
   * @param e
   */
  public void processMouseMotionEvent(MouseEvent e) {
    if (motionMethod == null) {
      // System.err.println("Motion method is undefined, please call
      // glut.glutMotionFunc(\"doMotion\")");
      return;
    }

    int id = e.getID();
    switch (id) {
      case MouseEvent.MOUSE_MOVED:
      case MouseEvent.MOUSE_DRAGGED:
        Object[] arguments = new Object[] {new Integer(e.getX()), new Integer(e.getY())};
        try {
          motionMethod.invoke(JavaComponent, arguments);
        } catch (IllegalAccessException ee) {
          System.out.println("IllegalAccessException while MouseMotionFunc");
          ee.printStackTrace();
        } catch (InvocationTargetException ee) {
          System.out.println("InvocationTargetException while MouseMotionFunc");
          ee.printStackTrace();
        } catch (NullPointerException ee) {
          ee.printStackTrace();
        }
        break;
    }
  }

  private void glut_enable_events(long cap, boolean state) {
    if (JavaComponent instanceof GLCanvas) {
      ((GLCanvas) JavaComponent).glut_enable_events(cap, state);
    } else if (JavaComponent instanceof GLApplet) {
      ((GLApplet) JavaComponent).glut_enable_events(cap, state);
    }
  }


  // ******************************************************** //

  // -------------- REGISTER GLUT CALLBACKS ----------------- //

  // ******************************************************** //

  /**
   * Register the name of the {@link JavaComponent} method that should be called upon mouse motion.
   * 
   * This method should have no argument.
   * 
   * void glutDisplayFunc (void (*func)(void))
   */
  public void glutDisplayFunc(String func) {
    if (func != null) {
      try {
        displayMethod = JavaComponent.getClass().getMethod(func, (Class[]) null);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      displayMethod = null;
    }
  }

  /**
   * Register the name of the {@link JavaComponent} method that should be called upon component
   * resize events.
   * 
   * This method should have two int as arguments.
   * 
   * void glutReshapeFunc (void (*func)(int width, int height))
   */
  public void glutReshapeFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {int.class, int.class};
      Object[] arguments = new Object[] {new Integer(JavaComponent.getSize().width),
          new Integer(JavaComponent.getSize().height)};
      try {
        reshapeMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        reshapeMethod.invoke(JavaComponent, arguments);
        glut_enable_events(AWTEvent.COMPONENT_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.out.println("IllegalAccessException while ReshapeFunc");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        System.out.println("InvocationTargetException while ReshapeFunc");
        e.printStackTrace();
      }
    } else {
      glut_enable_events(AWTEvent.COMPONENT_EVENT_MASK, false);
      reshapeMethod = null;
    }
  }

  /** void glutKeyboardFunc (void (*func)(unsigned char key, int x, int y)) */
  public void glutKeyboardFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {char.class, int.class, int.class};
      try {
        keyMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      if ((keyUpMethod == null) && (specialKeyMethod == null) && (specialKeyUpMethod == null))
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, false);
      keyMethod = null;
    }
  }

  /** void glutKeyboardUpFunc (void (*func)(unsigned char key, int x, int y)) */
  public void glutKeyboardUpFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {char.class, int.class, int.class};
      try {
        keyUpMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      if ((keyMethod == null) && (specialKeyMethod == null) && (specialKeyUpMethod == null))
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, false);
      keyUpMethod = null;
    }
  }

  public void glutSpecialFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {char.class, int.class, int.class};
      try {
        specialKeyMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      if ((keyMethod == null) && (keyUpMethod == null) && (specialKeyUpMethod == null))
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, false);
      specialKeyMethod = null;
    }
  }

  /** void glutSpecialUpFunc (void (*func)(unsigned char key, int x, int y)) */
  public void glutSpecialUpFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {char.class, int.class, int.class};
      try {
        specialKeyUpMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      if ((keyMethod == null) && (keyUpMethod == null) && (specialKeyMethod == null))
        glut_enable_events(AWTEvent.KEY_EVENT_MASK, false);
      specialKeyUpMethod = null;
    }
  }

  /**
   * Register the name of the {@link JavaComponent} method that should be called upon mouse events.
   * 
   * This method should have four int as arguments.
   * 
   * void glutMouseFunc (void (*func)(int button, int state, int x, int y))
   */
  public void glutMouseFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {int.class, int.class, int.class, int.class};
      try {
        mouseMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.MOUSE_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      if (JavaMenuButton == -1)
        glut_enable_events(AWTEvent.MOUSE_EVENT_MASK, false);
      mouseMethod = null;
    }
  }

  /**
   * Register the name of the {@link JavaComponent} method that should be called upon mouse motion.
   * 
   * This method should have two int as arguments.
   * 
   * void glutMotionFunc (void (*func)(int x, int y))
   */
  public void glutMotionFunc(String func) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {int.class, int.class};
      try {
        motionMethod = JavaComponent.getClass().getMethod(func, parameterTypes);
        glut_enable_events(AWTEvent.MOUSE_MOTION_EVENT_MASK, true);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      glut_enable_events(AWTEvent.MOUSE_MOTION_EVENT_MASK, false);
      motionMethod = null;
    }
  }

  /** void glutPassiveMotionFunc (void (*func)(int x, int y)) */
  /** void glutEntryFunc (void (*func)(int state)) */
  /** void glutVisibilityFunc (void (*func)(int state)) */

  /** void glutIdleFunc (void (*func)(void)) */
  public void glutIdleFunc(String func) {
    if (func != null) {
      try {
        idleMethod = JavaComponent.getClass().getMethod(func, (Class[]) null);
        if (JavaThread == null || !JavaThread.isAlive()) {
          JavaThread = new Thread(this);
          JavaThread.start();
        }
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
        e.printStackTrace();
      }
    } else {
      JavaThread = null;
      idleMethod = null;
    }
  }

  public void run() {
    Thread myThread = Thread.currentThread();
    while (JavaThread == myThread) {
      try {
        idleMethod.invoke(JavaComponent, (Object[]) null);
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          System.out.println(e);
        }
      } catch (IllegalAccessException e) {
        System.out.println("IllegalAccessException while IdleFunc");
      } catch (InvocationTargetException e) {
        System.out.println("InvocationTargetException while IdleFunc");
      }
    }
  }

  /**
   * void glutTimerFunc (unsigned int millis, void (*func)(int value), int value)
   */
  public void glutTimerFunc(int millis, final String func, int value) {
    if (func != null) {
      final Class[] parameterTypes = new Class[] {int.class};
      final Object[] timerValue = new Object[] {new Integer(value)};
      Timer JavaTimer = new Timer();
      JavaTimer.schedule(new TimerTask() {
        public void run() {
          try {
            JavaComponent.getClass().getMethod(func, parameterTypes).invoke(JavaComponent,
                timerValue);
          } catch (NoSuchMethodException e) {
            System.out.println("No method named " + func);
          } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException while calling " + func);
          } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException while calling " + func);
          }
        }
      }, millis);
    }
  }

  /** void glutMenuStateFunc (void (*func)(int state)) */

  /* GLUT state retrieval sub-API. */
  /** int glutGetModifiers (void) */
  public int glutGetModifiers() {
    return keyModifiers;
  }


  /* ********************************************************************** */

  /* **************************** CONSTANTS ******************************* */

  /* ********************************************************************** */



  /**
   * glut Functions Java is not C language, not all the glut functions can be implemented, only
   * implement some useful functions.
   */

  /** Constants of glut */
  /* Mouse buttons. */
  public static final int GLUT_LEFT_BUTTON = 0;
  public static final int GLUT_MIDDLE_BUTTON = 1;
  public static final int GLUT_RIGHT_BUTTON = 2;

  /* Mouse button state. */
  public static final int GLUT_DOWN = 0;
  public static final int GLUT_UP = 1;

  /* glutGetModifiers return mask. */
  public static final int GLUT_ACTIVE_SHIFT = 1;
  public static final int GLUT_ACTIVE_CTRL = 2;
  public static final int GLUT_ACTIVE_ALT = 4;

  /* function keys */
  public static final int GLUT_KEY_F1 = 1;
  public static final int GLUT_KEY_F2 = 2;
  public static final int GLUT_KEY_F3 = 3;
  public static final int GLUT_KEY_F4 = 4;
  public static final int GLUT_KEY_F5 = 5;
  public static final int GLUT_KEY_F6 = 6;
  public static final int GLUT_KEY_F7 = 7;
  public static final int GLUT_KEY_F8 = 8;
  public static final int GLUT_KEY_F9 = 9;
  public static final int GLUT_KEY_F10 = 10;
  public static final int GLUT_KEY_F11 = 11;
  public static final int GLUT_KEY_F12 = 12;

  /* directional keys */
  public static final int GLUT_KEY_LEFT = 100;
  public static final int GLUT_KEY_UP = 101;
  public static final int GLUT_KEY_RIGHT = 102;
  public static final int GLUT_KEY_DOWN = 103;
  public static final int GLUT_KEY_PAGE_UP = 104;
  public static final int GLUT_KEY_PAGE_DOWN = 105;
  public static final int GLUT_KEY_HOME = 106;
  public static final int GLUT_KEY_END = 107;
  public static final int GLUT_KEY_INSERT = 108;

}
