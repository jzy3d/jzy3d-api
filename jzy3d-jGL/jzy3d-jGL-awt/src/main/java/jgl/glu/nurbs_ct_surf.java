/*
 * @(#)nurbs_ct_surf.java 0.1 99/10/31
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
 * nurbs_ct_surf is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 31 Oct 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_ct_surf {

  public float ctrl[][][] = null;
  public int s_stride;
  public int t_stride;
  public float offsets[][][][][] = null;
  public int s_pt_cnt;
  public int t_pt_cnt;

  public void draw(GL JavaGL, nurbs_surface surf, int i, int j) {
    JavaGL.glMap2f(surf.type, 0, 1, s_stride, surf.s.order, 0, 1, t_stride, surf.t.order,
        offsets[i][j]);
  }

  public void augment(int s, int t, nurbs_surface surf) {
    int i, j, k, l;

    t_stride = surf.dim;
    s_stride = t_pt_cnt * t_stride;

    offsets = new float[s][t][surf.s.order][surf.t.order][surf.dim];

    for (i = 0; i < s; i++) {
      for (j = 0; j < t; j++) {
        for (k = 0; k < surf.s.order; k++) {
          for (l = 0; l < surf.t.order; l++) {
            System.arraycopy(ctrl[i * surf.s.order + k][j * surf.t.order + l], 0,
                offsets[i][j][k][l], 0, surf.dim);
          }
        }
      }
    }
  }

  public void convert(nurbs_knot s_knot, nurbs_knot t_knot, nurbs_surface surf) {
    int s_cnt, t_cnt;
    float tmp_ctrl[][][];
    int i, j;

    // the number of control points (ctrlarray)
    s_cnt = surf.s.knot_count - surf.s.order;
    t_cnt = surf.t.knot_count - surf.t.order;

    ctrl = surf.ctrlarray;

    tmp_ctrl = new float[s_cnt][][];
    t_knot.explode();
    t_knot.calc_alphas();
    for (i = 0; i < s_cnt; i++) {
      t_pt_cnt = t_knot.calc_new_ctrl_pts(ctrl[i], t_cnt, surf.dim, tmp_ctrl, i);
    }

    ctrl = new float[t_pt_cnt][s_cnt][surf.dim];
    for (i = 0; i < t_pt_cnt; i++) {
      for (j = 0; j < s_cnt; j++) {
        System.arraycopy(tmp_ctrl[j][i], 0, ctrl[i][j], 0, surf.dim);
      }
    }

    tmp_ctrl = new float[t_pt_cnt][s_cnt][surf.dim];
    s_knot.explode();
    s_knot.calc_alphas();
    for (i = 0; i < t_pt_cnt; i++) {
      s_pt_cnt = s_knot.calc_new_ctrl_pts(ctrl[i], s_cnt, surf.dim, tmp_ctrl, i);
    }
    ctrl = new float[s_pt_cnt][t_pt_cnt][surf.dim];
    for (i = 0; i < s_pt_cnt; i++) {
      for (j = 0; j < t_pt_cnt; j++) {
        System.arraycopy(tmp_ctrl[j][i], 0, ctrl[i][j], 0, surf.dim);
      }
    }
  }

}
