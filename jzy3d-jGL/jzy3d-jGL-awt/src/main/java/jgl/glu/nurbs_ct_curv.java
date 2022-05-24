/*
 * @(#)nurbs_ct_curv.java 0.1 99/11/11
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
 * nurbs_ct_curv is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 11 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_ct_curv {

  public float ctrl[][] = null;
  public int stride;
  public float offsets[][][] = null;
  public int pt_cnt;

  public void draw(GL JavaGL, nurbs_curve curv, int i) {
    JavaGL.glMap1f(curv.type, 0, 1, stride, curv.c.order, offsets[i]);
  }

  public void augment(int s, nurbs_curve curv) {
    int i, j;

    stride = curv.dim;

    offsets = new float[s][curv.c.order][curv.dim];

    for (i = 0; i < s; i++) {
      for (j = 0; j < curv.c.order; j++) {
        System.arraycopy(ctrl[i * curv.c.order + j], 0, offsets[i][j], 0, curv.dim);
      }
    }
  }

  public void convert(nurbs_knot knot, nurbs_curve curv) {
    int cnt;
    float tmp_ctrl[][][];
    int i;

    // the number of control points (ctrlarray)
    cnt = curv.c.knot_count - curv.c.order;

    ctrl = curv.ctrlarray;

    tmp_ctrl = new float[1][cnt][curv.dim];
    knot.explode();
    knot.calc_alphas();
    for (i = 0; i < cnt; i++) {
      pt_cnt = knot.calc_new_ctrl_pts(ctrl, cnt, curv.dim, tmp_ctrl, i);
    }

    ctrl = new float[pt_cnt][curv.dim];
    for (i = 0; i < pt_cnt; i++) {
      System.arraycopy(tmp_ctrl[0][i], 0, ctrl[i], 0, curv.dim);
    }
  }

}
