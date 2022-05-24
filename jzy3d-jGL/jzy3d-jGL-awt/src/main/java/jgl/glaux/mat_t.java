/*
 * @(#)mat_t.java 0.1 96/09/19
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996 Robin Bing-Yu Chen
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

/**
 * mat_t is one of the AUX class of JavaGL 1.1.
 *
 * @version 0.1, 19 Sep 1996
 * @author Robin Bing-Yu Chen
 */

public class mat_t {

  public double mat[][] = new double[4][4];
  public double norm[][] = new double[3][3];

  public mat_t(double m11, double m12, double m13, double m14, double m21, double m22, double m23,
      double m24, double m31, double m32, double m33, double m34, double m41, double m42,
      double m43, double m44, double n11, double n12, double n13, double n21, double n22,
      double n23, double n31, double n32, double n33) {
    mat[0] = new double[4];
    mat[1] = new double[4];
    mat[2] = new double[4];
    mat[3] = new double[4];
    norm[0] = new double[3];
    norm[1] = new double[3];
    norm[2] = new double[3];
    mat[0][0] = m11;
    mat[0][1] = m12;
    mat[0][2] = m13;
    mat[0][3] = m14;
    mat[1][0] = m21;
    mat[1][1] = m22;
    mat[1][2] = m23;
    mat[1][3] = m24;
    mat[2][0] = m31;
    mat[2][1] = m32;
    mat[2][2] = m33;
    mat[2][3] = m34;
    mat[3][0] = m41;
    mat[3][1] = m42;
    mat[3][2] = m43;
    mat[3][3] = m44;
    norm[0][0] = n11;
    norm[0][1] = n12;
    norm[0][2] = n13;
    norm[1][0] = n21;
    norm[1][1] = n22;
    norm[1][2] = n23;
    norm[2][0] = n31;
    norm[2][1] = n32;
    norm[2][2] = n33;
  }

  public mat_t() {
    mat[0] = new double[4];
    mat[1] = new double[4];
    mat[2] = new double[4];
    mat[3] = new double[4];
    norm[0] = new double[3];
    norm[1] = new double[3];
    norm[2] = new double[3];
  }

}
