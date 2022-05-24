/*
 * @(#)GLU.java 0.4 03/5/12
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2003 Robin Bing-Yu Chen
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

import java.lang.reflect.InvocationTargetException;

import jgl.glu.GLUnurbsObj;
import jgl.glu.GLUquadricObj;

/**
 * GLU is the glu class of jGL 2.4.
 *
 * @version 0.4, 12 May 2003
 * @author Robin Bing-Yu Chen
 *
 *         Modified 11/15/2002 by Jon McCall. Fixed the gluUnProject code. To fix the gluUnProject
 *         method I replaced the inverseMatrix44 with with a different algorithum, it was returning
 *         incorrect results. Also added a method to swap rows and columns (swapRowsCols44) because
 *         the projection and model matrixes are expected to be in this format for the gluUnproject
 *         algoithum to work correctly.
 */

public class GLU {

  /** Constants of GLU */

  /** Boolean */
  public static final boolean GLU_TRUE = true;
  public static final boolean GLU_FALSE = false;

  /** Error codes: */
  public static final int GLU_NO_ERROR = GL.GL_NO_ERROR;
  public static final int GLU_INVALID_ENUM = 100900;
  public static final int GLU_INVALID_VALUE = 100901;
  public static final int GLU_OUT_OF_MEMORY = 100902;
  public static final int GLU_INVALID_OPERATION = 100904;

  /** String names: */
  public static final int GLU_VERSION = 100800;
  public static final int GLU_EXTENSIONS = 100801;

  /** Quadric constants */
  /** Types of normals: */
  public static final int GLU_SMOOTH = 100000;
  public static final int GLU_FLAT = 100001;
  public static final int GLU_NONE = 100002;

  /** DrawStyle types: */
  public static final int GLU_POINT = 100010;
  public static final int GLU_LINE = 100011;
  public static final int GLU_FILL = 100012;
  public static final int GLU_SILHOUETTE = 100013;

  /** Orientation types: */
  public static final int GLU_OUTSIDE = 100020;
  public static final int GLU_INSIDE = 100021;

  public static final int GLU_ERROR = 100103;

  /** NURBS constants */
  /** Property types: */
  public static final int GLU_AUTO_LOAD_MATRIX = 100200;
  public static final int GLU_CULLING = 100201;
  public static final int GLU_PARAMETRIC_TOLERANCE = 100202;
  public static final int GLU_SAMPLING_TOLERANCE = 100203;
  public static final int GLU_DISPLAY_MODE = 100204;
  public static final int GLU_SAMPLING_METHOD = 100205;
  public static final int GLU_U_STEP = 100206;
  public static final int GLU_V_STEP = 100207;

  /** Sampling types: */
  public static final int GLU_PATH_LENGTH = 100215;
  public static final int GLU_PARAMETRIC_ERROR = 100216;
  public static final int GLU_DOMAIN_DISTANCE = 100217;

  /** Trim types: */
  public static final int GLU_MAP1_TRIM_2 = 100210;
  public static final int GLU_MAP1_TRIM_3 = 100211;

  /** Display types: */
  public static final int GLU_OUTLINE_POLYGON = 100240;
  public static final int GLU_OUTLINE_PATCH = 100241;

  /** Error codes: */
  public static final int GLU_NURBS_ERROR1 = 100251;
  public static final int GLU_NURBS_ERROR2 = 100252;
  public static final int GLU_NURBS_ERROR3 = 100253;
  public static final int GLU_NURBS_ERROR4 = 100254;
  public static final int GLU_NURBS_ERROR5 = 100255;
  public static final int GLU_NURBS_ERROR6 = 100256;
  public static final int GLU_NURBS_ERROR7 = 100257;
  public static final int GLU_NURBS_ERROR8 = 100258;
  public static final int GLU_NURBS_ERROR9 = 100259;
  public static final int GLU_NURBS_ERROR10 = 100260;
  public static final int GLU_NURBS_ERROR11 = 100261;
  public static final int GLU_NURBS_ERROR12 = 100262;
  public static final int GLU_NURBS_ERROR13 = 100263;
  public static final int GLU_NURBS_ERROR14 = 100264;
  public static final int GLU_NURBS_ERROR15 = 100265;
  public static final int GLU_NURBS_ERROR16 = 100266;
  public static final int GLU_NURBS_ERROR17 = 100267;
  public static final int GLU_NURBS_ERROR18 = 100268;
  public static final int GLU_NURBS_ERROR19 = 100269;
  public static final int GLU_NURBS_ERROR20 = 100270;
  public static final int GLU_NURBS_ERROR21 = 100271;
  public static final int GLU_NURBS_ERROR22 = 100272;
  public static final int GLU_NURBS_ERROR23 = 100273;
  public static final int GLU_NURBS_ERROR24 = 100274;
  public static final int GLU_NURBS_ERROR25 = 100275;
  public static final int GLU_NURBS_ERROR26 = 100276;
  public static final int GLU_NURBS_ERROR27 = 100277;
  public static final int GLU_NURBS_ERROR28 = 100278;
  public static final int GLU_NURBS_ERROR29 = 100279;
  public static final int GLU_NURBS_ERROR30 = 100280;
  public static final int GLU_NURBS_ERROR31 = 100281;
  public static final int GLU_NURBS_ERROR32 = 100282;
  public static final int GLU_NURBS_ERROR33 = 100283;
  public static final int GLU_NURBS_ERROR34 = 100284;
  public static final int GLU_NURBS_ERROR35 = 100285;
  public static final int GLU_NURBS_ERROR36 = 100286;
  public static final int GLU_NURBS_ERROR37 = 100287;

  /** Private Data Members */
  private final boolean debug = true;
	private GL JavaGL;

  /** Private Member Functions */
  private double[] mulMatrix44(double a[], double b[]) {
    // assume a = 4x4, b = 4x4
    double temp[] = new double[16];
    int i, j, k, x, y, z;
    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) {
        z = (i << 2) | j;
        for (k = 0; k < 4; k++) {
          x = (i << 2) | k;
          y = (k << 2) | j;
          if (a[x] != 0 && b[y] != 0) {
            if (a[x] == 1) {
              temp[z] += b[y];
            } else if (b[y] == 1) {
              temp[z] += a[x];
            } else {
              temp[z] += a[x] * b[y];
            }
          }
        }
      }
    }
    return temp;
  }

  private double[] mulMatrix41(double a[], double b[]) {
    // assume a = 4x4, b = 4x1
    int i, j, x;
    double temp[] = new double[4];
    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) {
        x = i << 2 | j;
        if (a[x] != 0 && b[j] != 0) {
          if (a[x] == 1) {
            temp[i] += b[j];
          } else if (b[j] == 1) {
            temp[i] += a[x];
          } else {
            temp[i] += a[x] * b[j];
          }
        }
      }
    }
    return temp;
  }

  private double det22(double a, double b, double c, double d) {
    return (a * d - b * c);
  }

  private double det33(double a1, double a2, double a3, double b1, double b2, double b3, double c1,
      double c2, double c3) {
    return (a1 * det22(b2, b3, c2, c3) - b1 * det22(a2, a3, c2, c3) + c1 * det22(a2, a3, b2, b3));
  }

  private double det44(double a1, double a2, double a3, double a4, double b1, double b2, double b3,
      double b4, double c1, double c2, double c3, double c4, double d1, double d2, double d3,
      double d4) {
    return (a1 * det33(b2, b3, b4, c2, c3, c4, d2, d3, d4)
        - b1 * det33(a2, a3, a4, c2, c3, c4, d2, d3, d4)
        + c1 * det33(a2, a3, a4, b2, b3, b4, d2, d3, d4)
        - d1 * det33(a2, a3, a4, b2, b3, b4, c2, c3, c4));
  }

  private double[] adjoint44(double a[]) {
    double m[] = new double[16];
    m[0] = det33(a[5], a[6], a[7], a[9], a[10], a[11], a[13], a[14], a[15]);
    m[1] = -det33(a[4], a[6], a[7], a[8], a[10], a[11], a[12], a[14], a[15]);
    m[2] = det33(a[4], a[5], a[7], a[8], a[9], a[11], a[12], a[13], a[15]);
    m[3] = -det33(a[4], a[5], a[6], a[8], a[9], a[10], a[12], a[13], a[14]);
    m[4] = -det33(a[1], a[2], a[3], a[9], a[10], a[11], a[13], a[14], a[15]);
    m[5] = det33(a[0], a[2], a[3], a[8], a[10], a[11], a[12], a[14], a[15]);
    m[6] = -det33(a[0], a[1], a[3], a[8], a[9], a[10], a[12], a[13], a[15]);
    m[7] = det33(a[0], a[1], a[2], a[8], a[9], a[10], a[12], a[13], a[14]);
    m[9] = -det33(a[0], a[2], a[3], a[4], a[6], a[7], a[12], a[14], a[15]);
    m[10] = det33(a[0], a[1], a[3], a[4], a[5], a[7], a[12], a[13], a[15]);
    m[11] = -det33(a[0], a[1], a[2], a[4], a[5], a[6], a[12], a[13], a[14]);
    m[12] = -det33(a[1], a[2], a[3], a[5], a[6], a[7], a[9], a[10], a[11]);
    m[13] = det33(a[0], a[2], a[3], a[4], a[6], a[7], a[8], a[10], a[11]);
    m[14] = -det33(a[0], a[1], a[3], a[4], a[5], a[7], a[8], a[9], a[11]);
    m[15] = det33(a[0], a[1], a[2], a[4], a[5], a[6], a[8], a[9], a[10]);
    return m;
  }

  /**
   * return the inverse of a 4x4 matrix. This method replaced by Jon McCall, 11/15/2002 since it was
   * returning incorrect results.
   */
  private double[] inverseMatrix44(double a[]) {
    // create a new identity matrix, becomes our output
    double[] out = new double[16];
    for (int i = 0; i < 4; i++) {
      out[4 * i + i] = 1;
    }

    for (int i = 0; i < 4; i++) {
      double d = a[4 * i + i];
      if (d != 1) {
        for (int j = 0; j < 4; j++) {
          out[4 * i + j] /= d;
          a[4 * i + j] /= d;
        }
      }

      for (int j = 0; j < 4; j++) {
        if (j != i) {
          if (a[4 * j + i] != 0) {
            double mult = a[4 * j + i];
            for (int k = 0; k < 4; k++) {
              a[4 * j + k] -= mult * a[4 * i + k];
              out[4 * j + k] -= mult * out[4 * i + k];
            }
          }
        }

      }
    }
    return out;
  }

  /**
   * switch the matrix rows and columns (matrix is a double[16])
   */
  private double[] swapRowsCols44(double[] matrix) {
    double[] temp = new double[16];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        temp[j * 4 + i] = matrix[i * 4 + j];
      }
    }
    return temp;
  }

  public GL gluGetGL() {
    return JavaGL;
  }

  /**
   * Miscellaneous
   */

  /**
   * GLvoid gluOrtho2D (GLdouble left, GLdouble right, GLdouble bottom, GLdouble top)
   */
  public void gluOrtho2D(double left, double right, double bottom, double top) {
    JavaGL.glOrtho((float) left, (float) right, (float) bottom, (float) top, (float) -1.0,
        (float) 1.0);
  }

  /**
   * GLvoid gluPerspective (GLdouble fovy, GLdouble aspect, GLdouble zNear, GLdouble zFar)
   */
  public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
    double xmin, xmax, ymin, ymax;

    ymax = zNear * Math.tan(fovy * Math.PI / 360.0);
    ymin = -ymax;

    xmin = ymin * aspect;
    xmax = ymax * aspect;

    JavaGL.glFrustum((float) xmin, (float) xmax, (float) ymin, (float) ymax, (float) zNear,
        (float) zFar);
  }

  /**
   * GLvoid gluPickMatrix (GLdouble x, GLdouble y, GLdouble width, GLdouble height, GLint
   * viewport[4])
   */
  public void gluPickMatrix(double x, double y, double width, double height, int viewport[]) {
    float m[] = new float[16];
    float sx, sy;
    float tx, ty;

    sx = (float) ((double) viewport[2] / width);
    sy = (float) ((double) viewport[3] / height);
    tx = (float) (((double) viewport[2] + 2.0 * ((double) viewport[0] - x)) / width);
    ty = (float) (((double) viewport[3] + 2.0 * ((double) viewport[1] - y)) / height);

    m[0] = sx;
    m[5] = sy;
    m[10] = (float) 1.0;
    m[12] = tx;
    m[13] = ty;
    m[15] = (float) 1.0;

    JavaGL.glMultMatrixf(m);
  }

  /**
   * GLint gluProject (GLdouble objx, GLdouble objy, GLdouble objz, const GLdouble model [16], const
   * GLdouble proj [16], const GLint viewport [4], GLdouble *winx, GLdouble *winy, GLdouble *winz)
   */
  public boolean gluProject(double objx, double objy, double objz, double model[], double proj[],
      int viewport[], double winx[], double winy[], double winz[]) {
    /* change *win? to be win? [], and the size of win? must be 1 */
    double in[] = new double[4];
    double out[];

    in[0] = objx;
    in[1] = objy;
    in[2] = objz;
    in[3] = 1.0;

    proj = swapRowsCols44(proj);
    model = swapRowsCols44(model);

    out = mulMatrix41(model, in);
    in = mulMatrix41(proj, out);

    in[0] /= in[3];
    in[1] /= in[3];
    in[2] /= in[3];

    winx[0] = viewport[0] + (1 + in[0]) * viewport[2] / 2;
    winy[0] = viewport[1] + (1 + in[1]) * viewport[3] / 2;
    winz[0] = (1 + in[2]) / 2;
    return true;
  }

  /**
   * GLint gluUnProject (GLdouble winx, GLdouble winy, GLdouble winz, const GLdouble model [16],
   * const GLdouble proj [16], const GLint viewport [4], GLdouble *objx, GLdouble *objy, GLdouble
   * *objz)
   */
  public boolean gluUnProject(double winx, double winy, double winz, double model[], double proj[],
      int viewport[], double objx[], double objy[], double objz[]) {
    /* change *obj? to be obj? [], and the size of obj? must be 1 */
    double m[] = new double[16];
    double A[] = new double[16];
    double in[] = new double[4];
    double out[] = new double[4];

    in[0] = (winx - viewport[0]) * 2 / viewport[2] - 1.0;
    in[1] = (winy - viewport[1]) * 2 / viewport[3] - 1.0;
    in[2] = 2 * winz - 1.0;
    in[3] = 1.0;

    // the projection and model matrixes need to be ordered by column not row
    proj = swapRowsCols44(proj);
    model = swapRowsCols44(model);

    A = mulMatrix44(proj, model);
    m = inverseMatrix44(A);

    out = mulMatrix41(m, in);
    if (out[3] == 0)
      return false;

    objx[0] = out[0] / out[3];
    objy[0] = out[1] / out[3];
    objz[0] = out[2] / out[3];
    return true;
  }

  /**
   * GLvoid gluLookAt (GLdouble eyex, GLdouble eyey, GLdouble eyez, GLdouble centerx, GLdouble
   * centery, GLdouble centerz, GLdouble upx, GLdouble upy, GLdouble upz)
   */
  public void gluLookAt(double eyex, double eyey, double eyez, double centerx, double centery,
      double centerz, double upx, double upy, double upz) {
    double m[] = new double[16];
    double x[] = new double[3];
    double y[] = new double[3];
    double z[] = new double[3];
    double mag;

    /* Make rotation matrix */

    /* Z vector */
    z[0] = eyex - centerx;
    z[1] = eyey - centery;
    z[2] = eyez - centerz;
    mag = Math.sqrt(z[0] * z[0] + z[1] * z[1] + z[2] * z[2]);
    if (mag != 0) { /* mpichler, 19950515 */
      z[0] /= mag;
      z[1] /= mag;
      z[2] /= mag;
    }

    /* Y vector */
    y[0] = upx;
    y[1] = upy;
    y[2] = upz;

    /* X vector = Y cross Z */
    x[0] = y[1] * z[2] - y[2] * z[1];
    x[1] = -y[0] * z[2] + y[2] * z[0];
    x[2] = y[0] * z[1] - y[1] * z[0];

    /* Recompute Y = Z cross X */
    y[0] = z[1] * x[2] - z[2] * x[1];
    y[1] = -z[0] * x[2] + z[2] * x[0];
    y[2] = z[0] * x[1] - z[1] * x[0];

    /* mpichler, 19950515 */
    /*
     * cross product gives area of parallelogram, which is < 1.0 for non-perpendicular unit-length
     * vectors; so normalize x, y here
     */

    mag = Math.sqrt(x[0] * x[0] + x[1] * x[1] + x[2] * x[2]);
    if (mag != 0) {
      x[0] /= mag;
      x[1] /= mag;
      x[2] /= mag;
    }

    mag = Math.sqrt(y[0] * y[0] + y[1] * y[1] + y[2] * y[2]);
    if (mag != 0) {
      y[0] /= mag;
      y[1] /= mag;
      y[2] /= mag;
    }

    m[0] = x[0];
    m[1] = y[0];
    m[2] = z[0];
    m[4] = x[1];
    m[5] = y[1];
    m[6] = z[1];
    m[8] = x[2];
    m[9] = y[2];
    m[10] = z[2];
    m[15] = 1.0;

    JavaGL.glMultMatrixd(m);

    /* Translate Eye to Origin */
    JavaGL.glTranslated(-eyex, -eyey, -eyez);
  }

  /** const GLubyte* gluErrorString (GLenum errorCode) */
  public static String gluErrorString(int errorCode) {
    String tess_error[] = {"missing gluEndPolygon", "missing gluBeginPolygon",
        "misoriented contour", "vertex/edge intersection", "misoriented or self-intersecting loops",
        "coincident vertices", "colinear vertices", "intersecting edges", "not coplanar contours"};

    String nurbs_error[] = {"spline order un-supported", "too few knots",
        "valid knot range is empty", "decreasing knot sequence knot",
        "knot multiplicity greater than order of spline", "endcurve() must follow bgncurve()",
        "bgncurve() must precede endcurve()", "missing or extra geometric data",
        "can't draw pwlcurves", "missing bgncurve()", "missing bgnsurface()",
        "endtrim() must precede endsurface()", "bgnsurface() must precede endsurface()",
        "curve of improper type passed as trim curve", "bgnsurface() must precede bgntrim()",
        "endtrim() must follow bgntrim()", "bgntrim() must precede endtrim()",
        "invalid or missing trim curve", "bgntrim() must precede pwlcurve()",
        "pwlcurve referenced twice", "pwlcurve and nurbscurve mixed",
        "improper usage of trim data type", "nurbscurve referenced twice", "invalid property",
        "endsurface() must follow bgnsurface()", "misoriented trim curves",
        "intersecting trim curves", "UNUSED", "unconnected trim curves", "unknown knot error",
        "negative vertex count encountered", "negative byte-stride encounteed",
        "unknown type descriptor", "null control array or knot vector",
        "duplicate point on pwlcurve"};

    switch (errorCode) {
      case GL.GL_NO_ERROR:
        return new String("no error");
      case GL.GL_INVALID_VALUE:
        return new String("invalid value");
      case GL.GL_INVALID_ENUM:
        return new String("invalid enum");
      case GL.GL_INVALID_OPERATION:
        return new String("invalid operation");
      case GL.GL_STACK_OVERFLOW:
        return new String("stack overflow");
      case GL.GL_STACK_UNDERFLOW:
        return new String("stack underflow");
      case GL.GL_OUT_OF_MEMORY:
        return new String("out of memory");
      // case GLU_NO_ERROR:
      // return new String ("no error");
      case GLU_INVALID_ENUM:
        return new String("invalid enum");
      case GLU_INVALID_VALUE:
        return new String("invalid value");
      case GLU_OUT_OF_MEMORY:
        return new String("out of memory");
      // case GLU_INCOMPATIBLE_GL_VERSION:
      // return new String ("incompatible GL version");
      default:
        // if (errorCode >= GLU_TESS_ERROR1 &&
        // errorCode <= GLU_TESS_ERROR9) {
        // return tess_error [errorCode - GLU_TESS_ERROR1];
        // } else {
        if (errorCode >= GLU_NURBS_ERROR1 && errorCode <= GLU_NURBS_ERROR37) {
          return nurbs_error[errorCode - GLU_NURBS_ERROR1];
        }
        // }
    }
    return null;
  }

  /**
   * Quadrics
   */

  private void quadric_error(GLUquadricObj qobj, int error, String msg) {
    /* Call the error call back function if any */
    if (qobj.ErrorFunc != null) {
      Object[] arguments = new Object[] {new Integer(error)};
      try {
        qobj.ErrorFunc.invoke(JavaGL.glJGetComponent(), arguments);
      } catch (IllegalAccessException e) {
        System.out.println("IllegalAccessException while GLUquadricObj.ErrorFunc");
      } catch (InvocationTargetException e) {
        System.out.println("InvocationTargetException while GLUquadricObj.ErrorFunc");
      }
    }
  }

  /** GLUquadricObj* gluNewQuadric (GLvoid) */
  public GLUquadricObj gluNewQuadric() {
    GLUquadricObj q = new GLUquadricObj();
    q.DrawStyle = GLU_FILL;
    q.Orientation = GLU_OUTSIDE;
    q.TextureFlag = GL.GL_FALSE;
    q.Normals = GLU_SMOOTH;
    q.ErrorFunc = null;
    return q;
  }

  /** void gluDeleteQuadric (GLUquadricObj *state) */
  public void gluDeleteQuadric(GLUquadricObj state) {
    /*
     * // just do nothing! if (state != null) { }
     */
  }

  /** GLvoid gluQuadricDrawStyle (GLUquadricObj *quadObject, GLenum drawStyle) */
  public void gluQuadricDrawStyle(GLUquadricObj quadObject, int drawStyle) {
    if (drawStyle == GLU_FILL || drawStyle == GLU_LINE || drawStyle == GLU_SILHOUETTE
        || drawStyle == GLU_POINT) {
      quadObject.DrawStyle = drawStyle;
    } else {
      quadric_error(quadObject, GLU_INVALID_ENUM, "qluQuadricDrawStyle");
    }
  }

  /**
   * GLvoid gluQuadricOrientation (GLUquadricObj *quadObject, GLenum orientation)
   */
  public void gluQuadricOrientation(GLUquadricObj quadObject, int orientation) {
    if (orientation == GLU_INSIDE || orientation == GLU_OUTSIDE) {
      quadObject.Orientation = orientation;
    } else {
      quadric_error(quadObject, GLU_INVALID_ENUM, "qluQuadricOrientation");
    }
  }

  /**
   * GLvoid gluQuadricCallback (GLUquadricObj *qobj, GLenum which, void (GLCALLBACK * fn) ())
   */
  public void gluQuadricCallback(GLUquadricObj qobj, int which, String fn) {
    if (qobj != null && which == GLU_ERROR) {
      Class[] parameterTypes = new Class[] {int.class};
      try {
        qobj.ErrorFunc = JavaGL.glJGetComponent().getClass().getMethod(fn, parameterTypes);
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + fn);
      }
    }
  }

  /** GLvoid gluQuadricNormals (GLUquadricObj *quadObject, GLenum normals) */
  public void gluQuadricNormals(GLUquadricObj quadObject, int normals) {
    if (normals == GLU_NONE || normals == GLU_FLAT || normals == GLU_SMOOTH) {
      quadObject.Normals = normals;
    } else {
      quadric_error(quadObject, GLU_INVALID_ENUM, "qluQuadricNormals");
    }
  }

  /**
   * GLvoid gluQuadricTexture (GLUquadricObj *quadObject, GLboolean textureCoords)
   */
  public void gluQuadricTexture(GLUquadricObj quadObject, boolean textureCoords) {
    quadObject.TextureFlag = textureCoords;
  }

  /**
   * GLvoid gluCylinder (GLUquadricObj *qobj, GLdouble baseRadius, GLdouble topRadius, GLdouble
   * height, GLint slices, GLint stacks)
   */
  public void gluCylinder(GLUquadricObj qobj, double baseRadius, double topRadius, double height,
      int slices, int stacks) {
    double da, r, dr, dz;
    float x, y, z, nz, nsign;
    boolean normal_state;
    int i, j;

    normal_state = JavaGL.glIsEnabled(GL.GL_NORMALIZE);
    JavaGL.glEnable(GL.GL_NORMALIZE);

    if (qobj.Orientation == GLU_INSIDE) {
      nsign = (float) -1.0;
    } else {
      nsign = (float) 1.0;
    }

    da = 2.0 * Math.PI / (double) slices;
    dr = (topRadius - baseRadius) / (double) stacks;
    dz = height / (double) stacks;
    /* Z component of normal vectors */
    nz = (float) ((baseRadius - topRadius) / height);

    if (qobj.DrawStyle == GLU_POINT) {
      JavaGL.glBegin(GL.GL_POINTS);
      for (i = 0; i < slices; i++) {
        x = (float) (Math.cos((double) i * da));
        y = (float) (Math.sin((double) i * da));
        JavaGL.glNormal3f(x * nsign, y * nsign, nz * nsign);

        z = (float) 0.0;
        r = baseRadius;
        for (j = 0; j <= stacks; j++) {
          JavaGL.glVertex3f(x * (float) r, y * (float) r, z);
          z += (float) dz;
          r += dr;
        }
      }
      JavaGL.glEnd();
    } else if (qobj.DrawStyle == GLU_LINE || qobj.DrawStyle == GLU_SILHOUETTE) {
      /* Draw rings */
      if (qobj.DrawStyle == GLU_LINE) {
        z = (float) 0.0;
        r = baseRadius;
        for (j = 0; j <= stacks; j++) {
          JavaGL.glBegin(GL.GL_LINE_LOOP);
          for (i = 0; i < slices; i++) {
            x = (float) (Math.cos((double) i * da));
            y = (float) (Math.sin((double) i * da));
            JavaGL.glNormal3f(x * nsign, y * nsign, nz * nsign);
            JavaGL.glVertex3f(x * (float) r, y * (float) r, z);
          }
          JavaGL.glEnd();
          z += (float) dz;
          r += dr;
        }
      } else {
        /* draw one ring at each end */
        if (baseRadius != 0.0) {
          JavaGL.glBegin(GL.GL_LINE_LOOP);
          for (i = 0; i < slices; i++) {
            x = (float) (Math.cos((double) i * da));
            y = (float) (Math.sin((double) i * da));
            JavaGL.glNormal3f(x * nsign, y * nsign, nz * nsign);
            JavaGL.glVertex3f(x * (float) baseRadius, y * (float) baseRadius, (float) 0.0);
          }
          JavaGL.glEnd();
          JavaGL.glBegin(GL.GL_LINE_LOOP);
          for (i = 0; i < slices; i++) {
            x = (float) (Math.cos((double) i * da));
            y = (float) (Math.sin((double) i * da));
            JavaGL.glNormal3f(x * nsign, y * nsign, nz * nsign);
            JavaGL.glVertex3f(x * (float) topRadius, y * (float) topRadius, (float) height);
          }
          JavaGL.glEnd();
        }
      }
      /* draw length lines */
      JavaGL.glBegin(GL.GL_LINES);
      for (i = 0; i < slices; i++) {
        x = (float) (Math.cos((double) i * da));
        y = (float) (Math.sin((double) i * da));
        JavaGL.glNormal3f(x * nsign, y * nsign, nz * nsign);
        JavaGL.glVertex3f(x * (float) baseRadius, y * (float) baseRadius, (float) 0.0);
        JavaGL.glVertex3f(x * (float) topRadius, y * (float) topRadius, (float) height);
      }
      JavaGL.glEnd();
    } else if (qobj.DrawStyle == GLU_FILL) {
      for (i = 0; i < slices; i++) {
        float x1 = (float) (Math.cos((double) i * da));
        float y1 = (float) (Math.sin((double) i * da));
        float x2 = (float) (Math.cos((double) (i + 1) * da));
        float y2 = (float) (Math.sin((double) (i + 1) * da));
        z = (float) 0.0;
        r = baseRadius;
        JavaGL.glBegin(GL.GL_QUAD_STRIP);
        for (j = 0; j <= stacks; j++) {
          if (nsign == 1.0) {
            JavaGL.glNormal3f(x1 * nsign, y1 * nsign, nz * nsign);
            // if (qobj.TextureFlag) JavaGL.glTexCoord2f(s, t);
            JavaGL.glVertex3f(x1 * (float) r, y1 * (float) r, z);
            JavaGL.glNormal3f(x2 * nsign, y2 * nsign, nz * nsign);
            // if (qobj.TextureFlag) JavaGL.glTexCoord2f(s, t);
            JavaGL.glVertex3f(x2 * (float) r, y2 * (float) r, z);
          } else {
            JavaGL.glNormal3f(x2 * nsign, y2 * nsign, nz * nsign);
            // if (qobj.TextureFlag) JavaGL.glTexCoord2f(s, t);
            JavaGL.glVertex3f(x2 * (float) r, y2 * (float) r, z);
            JavaGL.glNormal3f(x1 * nsign, y1 * nsign, nz * nsign);
            // if (qobj.TextureFlag) JavaGL.glTexCoord2f(s, t);
            JavaGL.glVertex3f(x1 * (float) r, y1 * (float) r, z);
          }
          z += (float) dz;
          r += dr;
        }
        JavaGL.glEnd();
      }
    }

    if (normal_state == false) {
      JavaGL.glDisable(GL.GL_NORMALIZE);
    }
  }

  /**
   * GLvoid gluSphere (GLUquadricObj *qobj, GLdouble radius, GLint slices, GLint stacks)
   */
  public void gluSphere(GLUquadricObj qobj, double radius, int slices, int stacks) {
    float rho, drho, theta, dtheta;
    float x, y, z;
    int i, j;
    boolean normals;
    float nsign;

    if (qobj.Normals == GLU_NONE) {
      normals = false;
    } else {
      normals = true;
    }
    if (qobj.Orientation == GLU_INSIDE) {
      nsign = (float) -1.0;
    } else {
      nsign = (float) 1.0;
    }

    drho = (float) Math.PI / (float) stacks;
    dtheta = (float) 2.0 * (float) Math.PI / (float) slices;

    if (qobj.DrawStyle == GLU_FILL) {
      /* draw +Z end as a triangle fan */
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glNormal3f((float) 0.0, (float) 0.0, (float) 1.0);
      JavaGL.glVertex3f((float) 0.0, (float) 0.0, nsign * (float) radius);
      for (j = 0; j <= slices; j++) {
        if (j == slices) {
          theta = (float) 0.0;
        } else {
          theta = (float) j * dtheta;
        }
        x = (float) (Math.cos(theta) * Math.sin(drho));
        y = (float) (Math.sin(theta) * Math.sin(drho));
        z = nsign * (float) Math.cos(drho);
        if (normals) {
          JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
        }
        JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
      }
      JavaGL.glEnd();

      /* draw intermediate stacks as quad strips */
      for (i = 1; i < stacks - 1; i++) {
        rho = i * drho;
        JavaGL.glBegin(GL.GL_QUAD_STRIP);
        for (j = 0; j <= slices; j++) {
          if (j == slices) {
            theta = (float) 0.0;
          } else {
            theta = (float) j * dtheta;
          }
          x = (float) (Math.cos(theta) * Math.sin(rho));
          y = (float) (Math.sin(theta) * Math.sin(rho));
          z = nsign * (float) Math.cos(rho);
          if (normals) {
            JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
          }
          JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
          x = (float) (Math.cos(theta) * Math.sin(rho + drho));
          y = (float) (Math.sin(theta) * Math.sin(rho + drho));
          z = nsign * (float) Math.cos(rho + drho);
          if (normals) {
            JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
          }
          JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
        }
        JavaGL.glEnd();
      }

      /* draw -Z end as a triangle fan */
      JavaGL.glBegin(GL.GL_TRIANGLE_FAN);
      JavaGL.glNormal3f((float) 0.0, (float) 0.0, (float) -1.0);
      JavaGL.glVertex3f((float) 0.0, (float) 0.0, (float) -radius * nsign);
      rho = (float) Math.PI - drho;
      for (j = slices; j >= 0; j--) {
        if (j == slices) {
          theta = (float) 0.0;
        } else {
          theta = (float) j * dtheta;
        }
        x = (float) (Math.cos(theta) * Math.sin(rho));
        y = (float) (Math.sin(theta) * Math.sin(rho));
        z = nsign * (float) Math.cos(rho);
        if (normals) {
          JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
        }
        JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
      }
      JavaGL.glEnd();
    } else if (qobj.DrawStyle == GLU_LINE || qobj.DrawStyle == GLU_SILHOUETTE) {
      /* draw stack lines */
      for (i = 1; i < stacks - 1; i++) {
        rho = (float) i * drho;
        JavaGL.glBegin(GL.GL_LINE_LOOP);
        for (j = 0; j < slices; j++) {
          theta = (float) j * dtheta;
          x = (float) (Math.cos(theta) * Math.sin(rho));
          y = (float) (Math.sin(theta) * Math.sin(rho));
          z = (float) Math.cos(rho);
          if (normals) {
            JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
          }
          JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
        }
        JavaGL.glEnd();
      }

      /* draw slice lines */
      for (j = 0; j < slices; j++) {
        theta = (float) j * dtheta;
        JavaGL.glBegin(GL.GL_LINE_STRIP);
        for (i = 0; i <= stacks; i++) {
          rho = (float) i * drho;
          x = (float) (Math.cos(theta) * Math.sin(rho));
          y = (float) (Math.sin(theta) * Math.sin(rho));
          z = (float) Math.cos(rho);
          if (normals) {
            JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
          }
          JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
        }
        JavaGL.glEnd();
      }
    } else if (qobj.DrawStyle == GLU_POINT) {
      /* top and bottom-most points */
      JavaGL.glBegin(GL.GL_POINTS);
      if (normals) {
        JavaGL.glNormal3f((float) 0.0, (float) 0.0, nsign);
      }
      JavaGL.glVertex3f((float) 0.0, (float) 0.0, (float) radius);
      if (normals) {
        JavaGL.glNormal3f((float) 0.0, (float) 0.0, -nsign);
      }
      JavaGL.glVertex3f((float) 0.0, (float) 0.0, (float) -radius);

      /* loop over stacks */
      for (i = 1; i < stacks - 1; i++) {
        rho = (float) i * drho;
        for (j = 0; j < slices; j++) {
          theta = (float) j * dtheta;
          x = (float) (Math.cos(theta) * Math.sin(rho));
          y = (float) (Math.sin(theta) * Math.sin(rho));
          z = (float) Math.cos(rho);
          if (normals) {
            JavaGL.glNormal3f(x * (float) nsign, y * (float) nsign, z * (float) nsign);
          }
          JavaGL.glVertex3f(x * (float) radius, y * (float) radius, z * (float) radius);
        }
      }
      JavaGL.glEnd();
    }
  }

  /**
   * GLvoid gluDisk (GLUquadricObj *qobj, GLdouble innerRadius, GLdouble outerRadius, GLint slices,
   * GLint loops)
   */
  public void gluDisk(GLUquadricObj qobj, double innerRadius, double outerRadius, int slices,
      int loops) {
    float da, dr;
    int l, s;

    /* Normal vectors */
    if (qobj.Normals != GLU_NONE) {
      if (qobj.Orientation == GLU_OUTSIDE) {
        JavaGL.glNormal3f(0.0f, 0.0f, 1.0f);
      } else {
        JavaGL.glNormal3f(0.0f, 0.0f, -1.0f);
      }
    }

    da = (float) 2.0 * (float) Math.PI / (float) slices;
    dr = (float) (outerRadius - innerRadius) / (float) loops;

    switch (qobj.DrawStyle) {
      case GLU_FILL:
        /*
         * texture of a gluDisk is a cut out of the texture unit square x, y in [-outerRadius,
         * +outerRadius]; s, t in [0, 1] (linear mapping)
         */
        float dtc = 2.0f * (float) outerRadius;
        float sa, ca;
        float r1 = (float) innerRadius;
        // int l;
        for (l = 0; l < loops; l++) {
          float r2 = r1 + dr;
          if (qobj.Orientation == GLU_OUTSIDE) {
            // int s;
            JavaGL.glBegin(GL.GL_QUAD_STRIP);
            for (s = 0; s <= slices; s++) {
              float a;
              if (s == slices)
                a = 0.0f;
              else
                a = s * da;
              sa = (float) Math.sin(a);
              ca = (float) Math.cos(a);
              if (qobj.TextureFlag)
                JavaGL.glTexCoord2f(0.5f + sa * r2 / dtc, 0.5f + ca * r2 / dtc);
              JavaGL.glVertex2f(r2 * sa, r2 * ca);
              if (qobj.TextureFlag)
                JavaGL.glTexCoord2f(0.5f + sa * r1 / dtc, 0.5f + ca * r1 / dtc);
              JavaGL.glVertex2f(r1 * sa, r1 * ca);
            }
            JavaGL.glEnd();
          } else {
            // int s;
            JavaGL.glBegin(GL.GL_QUAD_STRIP);
            for (s = slices; s >= 0; s--) {
              float a;
              if (s == slices)
                a = 0.0f;
              else
                a = s * da;
              sa = (float) Math.sin(a);
              ca = (float) Math.cos(a);
              if (qobj.TextureFlag)
                JavaGL.glTexCoord2f(0.5f - sa * r2 / dtc, 0.5f + ca * r2 / dtc);
              JavaGL.glVertex2f(r2 * sa, r2 * ca);
              if (qobj.TextureFlag)
                JavaGL.glTexCoord2f(0.5f - sa * r1 / dtc, 0.5f + ca * r1 / dtc);
              JavaGL.glVertex2f(r1 * sa, r1 * ca);
            }
            JavaGL.glEnd();
          }
          r1 = r2;
        }
        break;
      case GLU_LINE:
        // int l, s;
        /* draw loops */
        for (l = 0; l <= loops; l++) {
          float r = (float) innerRadius + l * dr;
          JavaGL.glBegin(GL.GL_LINE_LOOP);
          for (s = 0; s < slices; s++) {
            float a = s * da;
            JavaGL.glVertex2f(r * (float) Math.sin(a), r * (float) Math.cos(a));
          }
          JavaGL.glEnd();
        }
        /* draw spokes */
        for (s = 0; s < slices; s++) {
          float a = s * da;
          float x = (float) Math.sin(a);
          float y = (float) Math.cos(a);
          JavaGL.glBegin(GL.GL_LINE_STRIP);
          for (l = 0; l <= loops; l++) {
            float r = (float) innerRadius + l * dr;
            JavaGL.glVertex2f(r * x, r * y);
          }
          JavaGL.glEnd();
        }
        break;
      case GLU_POINT:
        // int s;
        JavaGL.glBegin(GL.GL_POINTS);
        for (s = 0; s < slices; s++) {
          float a = s * da;
          float x = (float) Math.sin(a);
          float y = (float) Math.cos(a);
          // int l;
          for (l = 0; l <= loops; l++) {
            float r = (float) innerRadius * l * dr;
            JavaGL.glVertex2f(r * x, r * y);
          }
        }
        JavaGL.glEnd();
        break;
      case GLU_SILHOUETTE:
        if (innerRadius != 0.0) {
          float a;
          JavaGL.glBegin(GL.GL_LINE_LOOP);
          for (a = 0.0f; a < (float) (2.0 * Math.PI); a += da) {
            float x = (float) (innerRadius * Math.sin(a));
            float y = (float) (innerRadius * Math.cos(a));
            JavaGL.glVertex2f(x, y);
          }
          JavaGL.glEnd();
        }
        float a;
        JavaGL.glBegin(GL.GL_LINE_LOOP);
        for (a = 0.0f; a < (float) (2.0 * Math.PI); a += da) {
          float x = (float) (outerRadius * Math.sin(a));
          float y = (float) (outerRadius * Math.cos(a));
          JavaGL.glVertex2f(x, y);
        }
        JavaGL.glEnd();
        break;
    }
  }

  /**
   * GLvoid gluPartialDisk (GLUquadricObj *qobj, GLdouble innerRadius, GLdouble outerRadius, GLint
   * slices, GLint loops, GLdouble startAngle, GLdouble sweepAngle)
   */
  public void gluPartialDisk(GLUquadricObj qobj, double innerRadius, double outerRadius, int slices,
      int loops, double startAngle, double sweepAngle) {
    if (qobj.Normals != GLU_NONE) {
      if (qobj.Orientation == GLU_OUTSIDE) {
        JavaGL.glNormal3f(0.0f, 0.0f, 1.0f);
      } else {
        JavaGL.glNormal3f(0.0f, 0.0f, -1.0f);
      }
    }

    if (qobj.DrawStyle == GLU_POINT) {
      int loop, slice;
      double radius, delta_radius;
      double angle, delta_angle;
      delta_radius = (outerRadius - innerRadius) / (double) (loops - 1);
      delta_angle = ((sweepAngle) / (double) (slices - 1)) * (Math.PI / 180.0);
      JavaGL.glBegin(GL.GL_POINTS);
      radius = innerRadius;
      for (loop = 0; loop < loops; loop++) {
        angle = startAngle * Math.PI / 180.0;
        for (slice = 0; slice < slices; slice++) {
          JavaGL.glVertex2d(radius * Math.sin(angle), radius * Math.cos(angle));
          angle += delta_angle;
        }
        radius += delta_radius;
      }
      JavaGL.glEnd();
    } else if (qobj.DrawStyle == GLU_LINE) {
      int loop, slice;
      double radius, delta_radius;
      double angle, delta_angle;
      delta_radius = (outerRadius - innerRadius) / (double) loops;
      delta_angle = (sweepAngle / (double) slices) * (Math.PI / 180.0);
      /* draw rings */
      radius = innerRadius;
      for (loop = 0; loop < loops; loop++) {
        angle = startAngle * Math.PI / 180.0;
        JavaGL.glBegin(GL.GL_LINE_STRIP);
        for (slice = 0; slice <= slices; slice++) {
          JavaGL.glVertex2d(radius * Math.sin(angle), radius * Math.cos(angle));
          angle += delta_angle;
        }
        JavaGL.glEnd();
        radius += delta_radius;
      }
      /* draw spokes */
      angle = startAngle * Math.PI / 180.0;
      for (slice = 0; slice <= slices; slice++) {
        radius = innerRadius;
        JavaGL.glBegin(GL.GL_LINE_STRIP);
        for (loop = 0; loop < loops; loop++) {
          JavaGL.glVertex2d(radius * Math.sin(angle), radius * Math.cos(angle));
          radius += delta_radius;
        }
        JavaGL.glEnd();
        angle += delta_angle;
      }
    } else if (qobj.DrawStyle == GLU_SILHOUETTE) {
      int slice;
      double angle, delta_angle;
      delta_angle = (sweepAngle / (double) slices) * (Math.PI / 180.0);
      /* draw outer ring */
      JavaGL.glBegin(GL.GL_LINE_STRIP);
      angle = startAngle * Math.PI / 180.0;
      for (slice = 0; slice <= slices; slice++) {
        JavaGL.glVertex2d(outerRadius * Math.sin(angle), outerRadius * Math.cos(angle));
        angle += delta_angle;
      }
      JavaGL.glEnd();
      /* draw inner ring */
      if (innerRadius > 0.0) {
        JavaGL.glBegin(GL.GL_LINE_STRIP);
        angle = startAngle * Math.PI / 180.0;
        for (slice = 0; slice < slices; slice++) {
          JavaGL.glVertex2d(innerRadius * Math.sin(angle), innerRadius * Math.cos(angle));
          angle += delta_angle;
        }
        JavaGL.glEnd();
      }
      /* draw spokes */
      if (sweepAngle < 360.0) {
        double stopAngle = startAngle + sweepAngle;
        JavaGL.glBegin(GL.GL_LINES);
        JavaGL.glVertex2d(innerRadius * Math.sin(startAngle * Math.PI / 180.0),
            innerRadius * Math.cos(startAngle * Math.PI / 180.0));
        JavaGL.glVertex2d(outerRadius * Math.sin(startAngle * Math.PI / 180.0),
            outerRadius * Math.cos(startAngle * Math.PI / 180.0));
        JavaGL.glVertex2d(innerRadius * Math.sin(stopAngle * Math.PI / 180.0),
            innerRadius * Math.cos(stopAngle * Math.PI / 180.0));
        JavaGL.glVertex2d(outerRadius * Math.sin(stopAngle * Math.PI / 180.0),
            outerRadius * Math.cos(stopAngle * Math.PI / 180.0));
        JavaGL.glEnd();
      }
    } else if (qobj.DrawStyle == GLU_FILL) {
      int loop, slice;
      double radius, delta_radius;
      double angle, delta_angle;
      delta_radius = (outerRadius - innerRadius) / (double) loops;
      delta_angle = (sweepAngle / (double) slices) * (Math.PI / 180.0);
      radius = innerRadius;
      for (loop = 0; loop < loops; loop++) {
        JavaGL.glBegin(GL.GL_QUAD_STRIP);
        angle = startAngle * Math.PI / 180.0;
        for (slice = 0; slice <= slices; slice++) {
          if (qobj.Orientation == GLU_OUTSIDE) {
            JavaGL.glVertex2d((radius + delta_radius) * Math.sin(angle),
                (radius + delta_radius) * Math.cos(angle));
            JavaGL.glVertex2d(radius * Math.sin(angle), radius * Math.cos(angle));
          } else {
            JavaGL.glVertex2d(radius * Math.sin(angle), radius * Math.cos(angle));
            JavaGL.glVertex2d((radius + delta_radius) * Math.sin(angle),
                (radius + delta_radius) * Math.cos(angle));
          }
          angle += delta_angle;
        }
        JavaGL.glEnd();
        radius += delta_radius;
      }
    }
  }

  /**
   * NURBS
   */

  /** GLUnurbsObj *gluNewNurbsRenderer (GLvoid) */
  public GLUnurbsObj gluNewNurbsRenderer() {
    return new GLUnurbsObj(JavaGL, this);
  }

  /** GLvoid gluDeleteNurbsRenderer (GLUnurbsObj *nobj) */
  public void gluDeleteNurbsRenderer(GLUnurbsObj nobj) {}

  /**
   * GLvoid gluLoadSamplingMatrices (GLUnurbsObj *nobj, const GLfloat modelMatrix [16], const
   * GLfloat projMatrix [16], const GLint viewport [4])
   */
  public void gluLoadSamplingMatrices(GLUnurbsObj nobj, float modelMatrix[], float projMatrix[],
      int viewport[]) {
    nobj.glu_load_sampling_matrices(modelMatrix, projMatrix, viewport);
  }

  /**
   * GLvoid gluNurbsProperty (GLUnurbsObj *nobj, GLenum property, GLfloat value)
   */
  public void gluNurbsProperty(GLUnurbsObj nobj, int property, float value) {
    int val;

    switch (property) {
      case GLU_SAMPLING_TOLERANCE:
        nobj.glu_set_sampling_tolerance(value);
        break;
      case GLU_DISPLAY_MODE:
        val = (int) value;
        nobj.glu_set_display_mode(val);
        break;
      case GLU_CULLING:
        val = (int) value;
        switch (val) {
          case 0:
            nobj.glu_set_culling(GL.GL_FALSE);
            break;
          case 1:
            nobj.glu_set_culling(GL.GL_TRUE);
            break;
          default:
            nobj.glu_error(GLU_INVALID_ENUM);
            return;
        }
        break;
      case GLU_AUTO_LOAD_MATRIX:
        val = (int) value;
        switch (val) {
          case 0:
            nobj.glu_set_auto_load_matrix(GL.GL_FALSE);
            break;
          case 1:
            nobj.glu_set_auto_load_matrix(GL.GL_TRUE);
            break;
          default:
            nobj.glu_error(GLU_INVALID_ENUM);
            return;
        }
        break;
      default:
        nobj.glu_error(GLU_NURBS_ERROR26);
    }
  }

  /**
   * GLvoid gluGetNurbsProperty (GLUnurbsObj *nobj, GLenum property, GLfloat *value)
   */
  public void gluGetNurbsProperty(GLUnurbsObj nobj, int property, float value[]) {
    switch (property) {
      case GLU_SAMPLING_TOLERANCE:
        value[0] = nobj.sampling_tolerance;
        break;
      case GLU_DISPLAY_MODE:
        value[0] = nobj.display_mode;
        break;
      case GLU_CULLING:
        if (nobj.culling) {
          value[0] = (float) 1.0;
        } else {
          value[0] = (float) 0.0;
        }
        break;
      case GLU_AUTO_LOAD_MATRIX:
        if (nobj.auto_load_matrix) {
          value[0] = (float) 1.0;
        } else {
          value[0] = (float) 0.0;
        }
        break;
      default:
        nobj.glu_error(GLU_INVALID_ENUM);
    }
  }

  /** GLvoid gluBeginCurve (GLUnurbsObj *nobj) */
  public void gluBeginCurve(GLUnurbsObj nobj) {
    nobj.glu_begin_curve();
  }

  /** GLvoid gluEndCurve (GLUnurbsObj *nobj) */
  public void gluEndCurve(GLUnurbsObj nobj) {
    nobj.glu_end_curve();
  }

  /**
   * GLvoid gluNurbsCurve (GLUnurbsObj *nobj, GLint knot_count, GLfloat *knot, GLint stride, GLfloat
   * *ctlarray, GLint order, GLenum type)
   */
  public void gluNurbsCurve(GLUnurbsObj nobj, int knot_count, float knot[], int stride,
      float ctlarray[][], int order, int type) {
    nobj.glu_nurbs_curve(knot_count, knot, stride, ctlarray, order, type);
  }

  /** GLvoid gluBeginSurface (GLUnurbsObj *nobj) */
  public void gluBeginSurface(GLUnurbsObj nobj) {
    nobj.glu_begin_surface();
  }

  /** GLvoid gluEndSurface (GLUnurbsObj *nobj) */
  public void gluEndSurface(GLUnurbsObj nobj) {
    nobj.glu_end_surface();
  }

  /**
   * GLvoid gluNurbsSurface (GLUnurbsObj *nobj, GLint sknot_count, GLfloat *sknot, GLint
   * tknot_count, GLfloat *tknot, GLint s_stride, GLint t_stride, GLfloat *ctlarray, GLint sorder,
   * GLint torder, GLenum type)
   */
  public void gluNurbsSurface(GLUnurbsObj nobj, int sknot_count, float sknot[], int tknot_count,
      float tknot[], int s_stride, int t_stride, float ctlarray[][][], int sorder, int torder,
      int type) {
    nobj.glu_nurbs_surface(sknot_count, sknot, tknot_count, tknot, s_stride, t_stride, ctlarray,
        sorder, torder, type);
  }

  /*
   * public GLU() { System.out.println("Please call new GLU (yourGL)"); }
   */

	public GLU(GL myGL) {
    JavaGL = myGL;
  }

}
