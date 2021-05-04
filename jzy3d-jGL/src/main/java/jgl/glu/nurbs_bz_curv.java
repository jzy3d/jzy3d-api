/*
 * @(#)nurbs_bz_curv.java 0.1 99/11/11
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999 Robin Bing-Yu Chen
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

package jgl.glu;

import jgl.GL;

/**
 * nurbs_bz_curv is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 11 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_bz_curv {

  private GL JavaGL;
  private int fac[];
  private int cnt;

  public void map(int s) {
    JavaGL.glMapGrid1f(fac[s], (float) 0.0, (float) 1.0);
    JavaGL.glEvalMesh1(GL.GL_LINE, 0, fac[s]);
  }

  public void set_property(int factors[], int bezier_cnt) {
    fac = factors;
    cnt = bezier_cnt;
  }

  public nurbs_bz_curv() {
    System.out.println("Please call new nurbs_bz_curv (yourGL)");
  }

  public nurbs_bz_curv(GL myGL) {
    JavaGL = myGL;
  }

}
