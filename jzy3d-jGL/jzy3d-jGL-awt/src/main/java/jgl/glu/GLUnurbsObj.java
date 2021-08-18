/*
 * @(#)GLUnurbsObj.java 0.1 99/10/31
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
import jgl.wt.awt.GLU;

/**
 * GLUnurbsObj is NURBS object of the GLU class of JavaGL 2.1.
 *
 * @version 0.1, 31 Oct 1999
 * @author Robin Bing-Yu Chen
 */

public class GLUnurbsObj {

  private GL JavaGL;
  private GLU JavaGLU;

  public static final int GLU_NURBS_CURVE = 0;
  public static final int GLU_NURBS_SURFACE = 1;
  public static final int GLU_NURBS_TRIM = 2;
  public static final int GLU_NURBS_NO_TRIM = 3;
  public static final int GLU_NURBS_TRIM_DONE = 4;
  public static final int GLU_NURBS_NONE = 5;

  public static final int GLU_TRIM_NURBS = 0;
  public static final int GLU_TRIM_PWL = 1;

  public float sampling_tolerance = (float) 50.0;
  public int display_mode = GLU.GLU_FILL;
  public boolean culling = GL.GL_FALSE;
  public boolean auto_load_matrix = GL.GL_TRUE;

  public int error = GLU.GLU_NO_ERROR;
  public int type = GLU_NURBS_NONE;

  public float sampling_model[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
  public float sampling_proj[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
  public int sampling_viewport[] = new int[4];

  public nurbs_surfaces surface = new nurbs_surfaces();
  public nurbs_curves curve = new nurbs_curves();

  // public nurbs_trim trim;

  private void set_new_t_min_t_max(nurbs_knot geom_knot, nurbs_knot color_knot,
      nurbs_knot normal_knot, nurbs_knot texture_knot, float max_min_knot, float min_max_knot) {
    int t_min, t_max, cnt;

    if (geom_knot.unified_nknots != 0) {
      geom_knot.set_new_min_max(max_min_knot, min_max_knot);
    } else {
      if (geom_knot.nknots != 0) {
        geom_knot.set_knot_min_max(max_min_knot, min_max_knot);
      }
    }
    if (color_knot.unified_nknots != 0) {
      color_knot.set_new_min_max(max_min_knot, min_max_knot);
    }
    if (normal_knot.unified_nknots != 0) {
      normal_knot.set_new_min_max(max_min_knot, min_max_knot);
    }
    if (texture_knot.unified_nknots != 0) {
      texture_knot.set_new_min_max(max_min_knot, min_max_knot);
    }
  }

  private void select_knot_working_range(nurbs_knot geom_knot, nurbs_knot color_knot,
      nurbs_knot normal_knot, nurbs_knot texture_knot) {
    int max_nknots;
    float max_min_knot, min_max_knot;
    int i;

    max_nknots = geom_knot.nknots;
    if (color_knot.unified_nknots != 0) {
      max_nknots += color_knot.nknots;
    }
    if (normal_knot.unified_nknots != 0) {
      max_nknots += normal_knot.nknots;
    }
    if (texture_knot.unified_nknots != 0) {
      max_nknots += texture_knot.nknots;
    }
    max_min_knot = geom_knot.knot[geom_knot.t_min];
    min_max_knot = geom_knot.knot[geom_knot.t_max];
    if (max_nknots != geom_knot.nknots) {
      geom_knot.unified_knot = new float[max_nknots];
      geom_knot.unified_nknots = geom_knot.nknots;
      System.arraycopy(geom_knot.knot, 0, geom_knot.unified_knot, 0, geom_knot.nknots);
      if (color_knot.unified_nknots != 0) {
        if (color_knot.knot[color_knot.t_min] > max_min_knot) {
          max_min_knot = color_knot.knot[color_knot.t_min];
        }
        if (color_knot.knot[color_knot.t_max] < min_max_knot) {
          min_max_knot = color_knot.knot[color_knot.t_max];
        }
        color_knot.unified_knot = new float[max_nknots];
        color_knot.unified_nknots = color_knot.nknots;
        System.arraycopy(color_knot.knot, 0, color_knot.unified_knot, 0, color_knot.nknots);
      }
      if (normal_knot.unified_nknots != 0) {
        if (normal_knot.knot[normal_knot.t_min] > max_min_knot) {
          max_min_knot = normal_knot.knot[normal_knot.t_min];
        }
        if (normal_knot.knot[normal_knot.t_max] < min_max_knot) {
          min_max_knot = normal_knot.knot[normal_knot.t_max];
        }
        normal_knot.unified_knot = new float[max_nknots];
        normal_knot.unified_nknots = normal_knot.nknots;
        System.arraycopy(normal_knot.knot, 0, normal_knot.unified_knot, 0, normal_knot.nknots);
      }
      if (texture_knot.unified_nknots != 0) {
        if (texture_knot.knot[texture_knot.t_min] > max_min_knot) {
          max_min_knot = texture_knot.knot[texture_knot.t_min];
        }
        if (texture_knot.knot[texture_knot.t_max] < min_max_knot) {
          min_max_knot = texture_knot.knot[texture_knot.t_max];
        }
        texture_knot.unified_knot = new float[max_nknots];
        texture_knot.unified_nknots = texture_knot.nknots;
        System.arraycopy(texture_knot.knot, 0, texture_knot.unified_knot, 0, texture_knot.nknots);
      }

      if (min_max_knot < max_min_knot) {
        geom_knot.unified_nknots = 0;
        color_knot.unified_nknots = 0;
        normal_knot.unified_nknots = 0;
        texture_knot.unified_nknots = 0;
      } else {
        if (color_knot.unified_nknots != 0) {
          geom_knot.collect(color_knot, max_min_knot, min_max_knot);
        }
        if (normal_knot.unified_nknots != 0) {
          geom_knot.collect(normal_knot, max_min_knot, min_max_knot);
        }
        if (texture_knot.unified_nknots != 0) {
          geom_knot.collect(texture_knot, max_min_knot, min_max_knot);
        }
        // after collect to geom, then feedback to others
        if (color_knot.unified_nknots != 0) {
          color_knot.collect(geom_knot, max_min_knot, min_max_knot);
        }
        if (normal_knot.unified_nknots != 0) {
          normal_knot.collect(geom_knot, max_min_knot, min_max_knot);
        }
        if (texture_knot.unified_nknots != 0) {
          texture_knot.collect(geom_knot, max_min_knot, min_max_knot);
        }
        // now, all the knots are the same
        set_new_t_min_t_max(geom_knot, color_knot, normal_knot, texture_knot, max_min_knot,
            min_max_knot);
      }
    } else {
      geom_knot.unified_nknots = 0;
    }
  }

  private int convert_curves(nurbs_ct_curvs new_ctrl) {
    nurbs_knot geom_knot = new nurbs_knot();
    nurbs_knot color_knot = new nurbs_knot();
    nurbs_knot normal_knot = new nurbs_knot();
    nurbs_knot texture_knot = new nurbs_knot();

    int err = curve.fill_knot(geom_knot, color_knot, normal_knot, texture_knot);
    if (err != GLU.GLU_NO_ERROR) {
      glu_error(err);
      return GLU.GLU_ERROR;
    }

    select_knot_working_range(geom_knot, color_knot, normal_knot, texture_knot);

    new_ctrl.convert(geom_knot, color_knot, normal_knot, texture_knot, curve);

    return GLU.GLU_NO_ERROR;
  }

  private int convert_surfaces(nurbs_ct_surfs new_ctrl) {
    nurbs_knot geom_s_knot = new nurbs_knot();
    nurbs_knot geom_t_knot = new nurbs_knot();
    nurbs_knot color_s_knot = new nurbs_knot();
    nurbs_knot color_t_knot = new nurbs_knot();
    nurbs_knot normal_s_knot = new nurbs_knot();
    nurbs_knot normal_t_knot = new nurbs_knot();
    nurbs_knot texture_s_knot = new nurbs_knot();
    nurbs_knot texture_t_knot = new nurbs_knot();

    int err = surface.fill_knot(geom_s_knot, geom_t_knot, color_s_knot, color_t_knot, normal_s_knot,
        normal_t_knot, texture_s_knot, texture_t_knot);
    if (err != GLU.GLU_NO_ERROR) {
      glu_error(err);
      return GLU.GLU_ERROR;
    }

    select_knot_working_range(geom_s_knot, color_s_knot, normal_s_knot, texture_s_knot);
    select_knot_working_range(geom_t_knot, color_t_knot, normal_t_knot, texture_t_knot);

    new_ctrl.convert(geom_s_knot, geom_t_knot, color_s_knot, color_t_knot, normal_s_knot,
        normal_t_knot, texture_s_knot, texture_t_knot, surface);

    return GLU.GLU_NO_ERROR;
  }

  private int calc_factor(float pts[][], int order, int indx, int dim) {
    double model[] = new double[16];
    double proj[] = new double[16];
    int viewport[] = new int[4];
    double winx1[] = new double[1];
    double winy1[] = new double[1];
    double winx2[] = new double[1];
    double winy2[] = new double[1];
    double winz[] = new double[1];
    double x, y = 0, z = 0, w;
    double dx, dy;
    double len = 0;
    int i;

    JavaGL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);
    JavaGL.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj);
    JavaGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);

    if (dim == 4) {
      w = pts[indx][3];
      x = pts[indx][0] / w;
      y = pts[indx][1] / w;
      z = pts[indx][2] / w;
    } else { // dim == 3, there will never be less than 3
      /*
       * if (dim == 3) { z = pts [indx][2]; } if (dim == 2) { y = pts [indx][1]; }
       */
      x = pts[indx][0];
      y = pts[indx][1];
      z = pts[indx][2];
    }

    JavaGLU.gluProject(x, y, z, model, proj, viewport, winx1, winy1, winz);

    for (i = 1; i < order; i++) {
      if (dim == 4) {
        w = pts[indx + i][3];
        x = pts[indx + i][0] / w;
        y = pts[indx + i][1] / w;
        z = pts[indx + i][2] / w;
      } else {
        /*
         * if (dim == 3) { z = pts [indx+i][2]; } if (dim == 2) { y = pts [indx+i][1]; }
         */
        x = pts[indx + i][0];
        y = pts[indx + i][1];
        z = pts[indx + i][2];
      }

      if (JavaGLU.gluProject(x, y, z, model, proj, viewport, winx2, winy2, winz)) {
        dx = winx2[0] - winx1[0];
        dy = winy2[0] - winy1[0];
        len += Math.sqrt(dx * dx + dy * dy);
      }

      winx1[0] = winx2[0];
      winy1[0] = winy2[0];
    }

    len /= sampling_tolerance;
    return (int) (len + 1);
  }

  private void sampling_2d(nurbs_ct_curvs new_ctrl, int factors[]) {
    float ctrl[][];
    int tmp_factor;
    int i;

    glu_set_sampling_matrices();
    ctrl = new_ctrl.geom.ctrl;
    for (i = 0; i < new_ctrl.bezier_cnt; i++) {
      tmp_factor = calc_factor(new_ctrl.geom.ctrl, curve.geom.c.order, i * curve.geom.c.order,
          curve.geom.dim);
      if (tmp_factor == 0) {
        factors[i] = 1;
      } else {
        factors[i] = tmp_factor;
      }
    }
    glu_revert_sampling_matrices();
  }

  private void sampling_3d(nurbs_ct_surfs new_ctrl, int ufactors[][], int vfactors[][]) {
    float ctrl[][][];
    int tmp_factor1, tmp_factor2;
    int i, j;

    glu_set_sampling_matrices();
    ctrl = new_ctrl.geom.ctrl;
    for (j = 0; j < new_ctrl.t_bezier_cnt; j++) {
      vfactors[j][1] = tmp_factor1 =
          calc_factor(ctrl[0], surface.geom.t.order, j * surface.geom.t.order, surface.geom.dim);
      for (i = 1; i < new_ctrl.s_bezier_cnt; i++) {
        tmp_factor2 = calc_factor(ctrl[i * surface.geom.s.order], surface.geom.t.order,
            j * surface.geom.t.order, surface.geom.dim);
        if (tmp_factor2 > tmp_factor1) {
          tmp_factor1 = tmp_factor2;
        }
      }
      vfactors[j][2] = tmp_factor2 = calc_factor(ctrl[i * surface.geom.s.order - 1],
          surface.geom.t.order, j * surface.geom.t.order, surface.geom.dim);
      if (tmp_factor2 > tmp_factor1) {
        vfactors[j][0] = tmp_factor2;
      } else {
        vfactors[j][0] = tmp_factor1;
      }
    }

    ctrl = new float[new_ctrl.geom.t_pt_cnt][new_ctrl.geom.s_pt_cnt][surface.geom.dim];
    for (i = 0; i < new_ctrl.geom.s_pt_cnt; i++) {
      for (j = 0; j < new_ctrl.geom.t_pt_cnt; j++) {
        ctrl[j][i] = new_ctrl.geom.ctrl[i][j];
      }
    }

    for (j = 0; j < new_ctrl.t_bezier_cnt; j++) {
      ufactors[j][1] = tmp_factor1 =
          calc_factor(ctrl[0], surface.geom.s.order, j * surface.geom.s.order, surface.geom.dim);
      for (i = 1; i < new_ctrl.s_bezier_cnt; i++) {
        tmp_factor2 = calc_factor(ctrl[i * surface.geom.t.order], surface.geom.s.order,
            j * surface.geom.s.order, surface.geom.dim);
        if (tmp_factor2 > tmp_factor1) {
          tmp_factor1 = tmp_factor2;
        }
      }
      ufactors[j][2] = tmp_factor2 = calc_factor(ctrl[i * surface.geom.t.order - 1],
          surface.geom.s.order, j * surface.geom.s.order, surface.geom.dim);
      if (tmp_factor2 > tmp_factor1) {
        ufactors[j][0] = tmp_factor2;
      } else {
        ufactors[j][0] = tmp_factor1;
      }
    }
    glu_revert_sampling_matrices();
  }

  private boolean point_in_viewport(float pt[], int dim) {
    double model[] = new double[16];
    double proj[] = new double[16];
    int viewport[] = new int[4];
    double winx[] = new double[1];
    double winy[] = new double[1];
    double winz[] = new double[1];
    double x, y, z, w;

    JavaGL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, model);
    JavaGL.glGetDoublev(GL.GL_PROJECTION_MATRIX, proj);
    JavaGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);
    if (dim == 3) {
      x = pt[0];
      y = pt[1];
      z = pt[2];
      JavaGLU.gluProject(x, y, z, model, proj, viewport, winx, winy, winz);
    } else {
      w = pt[3];
      x = pt[0] / w;
      y = pt[1] / w;
      z = pt[2] / w;
      JavaGLU.gluProject(x, y, z, model, proj, viewport, winx, winy, winz);
    }
    if ((winx[0] >= viewport[0]) && (winx[0] < viewport[2]) && (winy[0] >= viewport[1])
        && (winy[0] < viewport[3])) {
      return true;
    } else {
      return false;
    }
  }

  public boolean culling_test_2d(float pts[][], int cnt, int stride, int dim) {
    int i;

    if (culling == false) {
      return false;
    }
    glu_set_sampling_matrices();
    for (i = 0; i < cnt; i++) {
      if (point_in_viewport(pts[i], dim)) {
        return false;
      }
    }
    glu_revert_sampling_matrices();
    return true;
  }

  public boolean culling_test_3d(float pts[][][], int s_cnt, int t_cnt, int s_stride, int t_stride,
      int dim) {
    int i, j;

    if (culling == false) {
      return false;
    }
    glu_set_sampling_matrices();
    for (i = 0; i < s_cnt; i++) {
      for (j = 0; j < t_cnt; j++) {
        if (point_in_viewport(pts[i][j], dim)) {
          return false;
        }
      }
    }
    glu_revert_sampling_matrices();
    return true;
  }

  private void do_curve() {
    int factors[];
    nurbs_ct_curvs new_ctrl = new nurbs_ct_curvs();

    int err = curve.test();
    if (err != GLU.GLU_NO_ERROR) {
      glu_error(err);
      return;
    }
    if (convert_curves(new_ctrl) != GLU.GLU_NO_ERROR) {
      return;
    }
    new_ctrl.augment(curve);
    factors = new int[new_ctrl.bezier_cnt];
    sampling_2d(new_ctrl, factors);
    new_ctrl.draw(this, JavaGL, curve, factors);
  }

  private void do_surface() {
    int sfactors[][];
    int tfactors[][];
    nurbs_ct_surfs new_ctrl = new nurbs_ct_surfs();

    int err = surface.test();
    if (err != GLU.GLU_NO_ERROR) {
      glu_error(err);
      return;
    }
    if (convert_surfaces(new_ctrl) != GLU.GLU_NO_ERROR) {
      return;
    }
    new_ctrl.augment(surface);
    sfactors = new int[new_ctrl.s_bezier_cnt][3];
    tfactors = new int[new_ctrl.t_bezier_cnt][3];
    sampling_3d(new_ctrl, sfactors, tfactors);
    new_ctrl.draw(this, JavaGL, display_mode, surface, sfactors, tfactors);
    /*
     * switch (display_mode) { case GLU.GLU_FILL: new_ctrl.draw (this, JavaGL, GLU.GLU_FILL,
     * surface, sfactors, tfactors); break; case GLU.GLU_OUTLINE_POLYGON: new_ctrl.draw (this,
     * JavaGL, GLU.GLU_OUTLINE_POLYGON, surface, sfactors, tfactors); break; case
     * GLU.GLU_OUTLINE_PATCH: new_ctrl.draw (this, JavaGL, GLU.GLU_OUTLINE_PATCH, surface, sfactors,
     * tfactors); break; }
     */
  }

  public void glu_error(int err) {
    error = err;
    System.out.print("NURBS error " + err + " ");
    GLU.gluErrorString(err);
  }

  public void glu_load_sampling_matrices(float modelMatrix[], float projMatrix[], int viewport[]) {
    System.arraycopy(modelMatrix, 0, sampling_model, 0, 16);
    System.arraycopy(projMatrix, 0, sampling_proj, 0, 16);
    System.arraycopy(viewport, 0, sampling_viewport, 0, 4);
  }

  public void glu_set_sampling_matrices() {
    if (auto_load_matrix == false) {
      JavaGL.glPushAttrib(GL.GL_VIEWPORT_BIT | GL.GL_TRANSFORM_BIT);
      JavaGL.glViewport(sampling_viewport[0], sampling_viewport[1], sampling_viewport[2],
          sampling_viewport[3]);
      JavaGL.glMatrixMode(GL.GL_PROJECTION);
      JavaGL.glPushMatrix();
      JavaGL.glLoadMatrixf(sampling_proj);
      JavaGL.glMatrixMode(GL.GL_MODELVIEW);
      JavaGL.glPushMatrix();
      JavaGL.glLoadMatrixf(sampling_model);
    }
  }

  public void glu_revert_sampling_matrices() {
    if (auto_load_matrix == false) {
      JavaGL.glMatrixMode(GL.GL_MODELVIEW);
      JavaGL.glPopMatrix();
      JavaGL.glMatrixMode(GL.GL_PROJECTION);
      JavaGL.glPopMatrix();
      JavaGL.glPopAttrib();
    }
  }

  public void glu_set_sampling_tolerance(float value) {
    if (value <= 0) {
      glu_error(GLU.GLU_INVALID_VALUE);
      return;
    }
    sampling_tolerance = value;
  }

  public void glu_set_display_mode(int value) {
    if (value != GLU.GLU_FILL && value != GLU.GLU_OUTLINE_POLYGON
        && value != GLU.GLU_OUTLINE_PATCH) {
      glu_error(GLU.GLU_INVALID_ENUM);
      return;
    }
    if (type == GLU_NURBS_CURVE) {
      glu_error(GLU.GLU_NURBS_ERROR26);
      return;
    }
    display_mode = value;
  }

  public void glu_set_culling(boolean value) {
    culling = value;
  }

  public void glu_set_auto_load_matrix(boolean value) {
    auto_load_matrix = value;
  }

  public void glu_begin_curve() {
    switch (type) {
      case GLU_NURBS_NONE:
        type = GLU_NURBS_CURVE;
        curve.geom.type = GLU.GLU_INVALID_ENUM;
        curve.color.type = GLU.GLU_INVALID_ENUM;
        curve.normal.type = GLU.GLU_INVALID_ENUM;
        curve.texture.type = GLU.GLU_INVALID_ENUM;
        break;
      case GLU_NURBS_CURVE:
        glu_error(GLU.GLU_NURBS_ERROR6);
    }
  }

  public void glu_end_curve() {
    switch (type) {
      case GLU_NURBS_NONE:
        glu_error(GLU.GLU_NURBS_ERROR7);
        break;
      default:
        JavaGL.glPushAttrib(GL.GL_EVAL_BIT | GL.GL_ENABLE_BIT);
        JavaGL.glDisable(GL.GL_MAP1_VERTEX_3);
        JavaGL.glDisable(GL.GL_MAP1_VERTEX_4);
        JavaGL.glDisable(GL.GL_MAP1_INDEX);
        JavaGL.glDisable(GL.GL_MAP1_COLOR_4);
        JavaGL.glDisable(GL.GL_MAP1_NORMAL);
        JavaGL.glDisable(GL.GL_MAP1_TEXTURE_COORD_1);
        JavaGL.glDisable(GL.GL_MAP1_TEXTURE_COORD_2);
        JavaGL.glDisable(GL.GL_MAP1_TEXTURE_COORD_3);
        JavaGL.glDisable(GL.GL_MAP1_TEXTURE_COORD_4);
        do_curve();
        JavaGL.glPopAttrib();
        type = GLU_NURBS_NONE;
    }
  }

  public void glu_nurbs_curve(int knot_count, float knot[], int stride, float ctlarray[][],
      int order, int surtype) {
    if (type == GLU_NURBS_NO_TRIM || type == GLU_NURBS_TRIM || type == GLU_NURBS_TRIM_DONE) {
      if (surtype == GL.GL_MAP1_VERTEX_3 || surtype == GL.GL_MAP1_VERTEX_4) {
        glu_error(GLU.GLU_NURBS_ERROR8);
        return;
      }
    } else {
      if (type != GLU_NURBS_CURVE) {
        glu_error(GLU.GLU_NURBS_ERROR11);
        return;
      }
    }
    switch (surtype) {
      case GL.GL_MAP1_VERTEX_3:
      case GL.GL_MAP1_VERTEX_4:
        curve.geom.fill(knot_count, knot, stride, ctlarray, order, surtype);
        type = GLU_NURBS_NO_TRIM;
        break;
      case GL.GL_MAP1_INDEX:
      case GL.GL_MAP1_COLOR_4:
        curve.color.fill(knot_count, knot, stride, ctlarray, order, surtype);
        break;
      case GL.GL_MAP1_NORMAL:
        curve.normal.fill(knot_count, knot, stride, ctlarray, order, surtype);
        break;
      case GL.GL_MAP1_TEXTURE_COORD_1:
      case GL.GL_MAP1_TEXTURE_COORD_2:
      case GL.GL_MAP1_TEXTURE_COORD_3:
      case GL.GL_MAP1_TEXTURE_COORD_4:
        curve.texture.fill(knot_count, knot, stride, ctlarray, order, surtype);
        break;
      default:
        glu_error(GLU.GLU_INVALID_ENUM);
        return;
    }
  }

  public void glu_begin_surface() {
    switch (type) {
      case GLU_NURBS_NONE:
        type = GLU_NURBS_SURFACE;
        surface.geom.type = GLU.GLU_INVALID_ENUM;
        surface.color.type = GLU.GLU_INVALID_ENUM;
        surface.normal.type = GLU.GLU_INVALID_ENUM;
        surface.texture.type = GLU.GLU_INVALID_ENUM;
        break;
      case GLU_NURBS_TRIM:
        glu_error(GLU.GLU_NURBS_ERROR16);
        break;
      case GLU_NURBS_SURFACE:
      case GLU_NURBS_NO_TRIM:
      case GLU_NURBS_TRIM_DONE:
        glu_error(GLU.GLU_NURBS_ERROR27);
        break;
      case GLU_NURBS_CURVE:
        glu_error(GLU.GLU_NURBS_ERROR6);
    }
  }

  public void glu_end_surface() {
    switch (type) {
      case GLU_NURBS_NONE:
        glu_error(GLU.GLU_NURBS_ERROR13);
        break;
      case GLU_NURBS_TRIM:
        glu_error(GLU.GLU_NURBS_ERROR12);
        break;
      case GLU_NURBS_NO_TRIM:
      case GLU_NURBS_TRIM_DONE:
        JavaGL.glPushAttrib(GL.GL_EVAL_BIT | GL.GL_ENABLE_BIT);
        JavaGL.glDisable(GL.GL_MAP2_VERTEX_3);
        JavaGL.glDisable(GL.GL_MAP2_VERTEX_4);
        JavaGL.glDisable(GL.GL_MAP2_INDEX);
        JavaGL.glDisable(GL.GL_MAP2_COLOR_4);
        JavaGL.glDisable(GL.GL_MAP2_NORMAL);
        JavaGL.glDisable(GL.GL_MAP2_TEXTURE_COORD_1);
        JavaGL.glDisable(GL.GL_MAP2_TEXTURE_COORD_2);
        JavaGL.glDisable(GL.GL_MAP2_TEXTURE_COORD_3);
        JavaGL.glDisable(GL.GL_MAP2_TEXTURE_COORD_4);
        do_surface();
        JavaGL.glPopAttrib();
        break;
      default:
        glu_error(GLU.GLU_NURBS_ERROR8);
    }
    type = GLU_NURBS_NONE;
  }

  public void glu_nurbs_surface(int sknot_count, float sknot[], int tknot_count, float tknot[],
      int s_stride, int t_stride, float ctlarray[][][], int sorder, int torder, int surtype) {
    if (type == GLU_NURBS_NO_TRIM || type == GLU_NURBS_TRIM || type == GLU_NURBS_TRIM_DONE) {
      if (surtype == GL.GL_MAP2_VERTEX_3 || surtype == GL.GL_MAP2_VERTEX_4) {
        glu_error(GLU.GLU_NURBS_ERROR8);
        return;
      }
    } else {
      if (type != GLU_NURBS_SURFACE) {
        glu_error(GLU.GLU_NURBS_ERROR11);
        return;
      }
    }
    switch (surtype) {
      case GL.GL_MAP2_VERTEX_3:
      case GL.GL_MAP2_VERTEX_4:
        surface.geom.fill(sknot_count, sknot, tknot_count, tknot, s_stride, t_stride, ctlarray,
            sorder, torder, surtype);
        type = GLU_NURBS_NO_TRIM;
        break;
      case GL.GL_MAP2_INDEX:
      case GL.GL_MAP2_COLOR_4:
        surface.color.fill(sknot_count, sknot, tknot_count, tknot, s_stride, t_stride, ctlarray,
            sorder, torder, surtype);
        break;
      case GL.GL_MAP2_NORMAL:
        surface.normal.fill(sknot_count, sknot, tknot_count, tknot, s_stride, t_stride, ctlarray,
            sorder, torder, surtype);
        break;
      case GL.GL_MAP2_TEXTURE_COORD_1:
      case GL.GL_MAP2_TEXTURE_COORD_2:
      case GL.GL_MAP2_TEXTURE_COORD_3:
      case GL.GL_MAP2_TEXTURE_COORD_4:
        surface.texture.fill(sknot_count, sknot, tknot_count, tknot, s_stride, t_stride, ctlarray,
            sorder, torder, surtype);
        break;
      default:
        glu_error(GLU.GLU_INVALID_ENUM);
        return;
    }
  }

  public GLUnurbsObj() {
    System.out.println("Please call new GLUnurbsObj (yourGL, yourGLU)");
  }

  public GLUnurbsObj(GL myGL, GLU myGLU) {
    JavaGL = myGL;
    JavaGLU = myGLU;

    JavaGL.glGetIntegerv(GL.GL_VIEWPORT, sampling_viewport);
  }

}
