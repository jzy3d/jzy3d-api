/*
 * @(#)nurbs_ct_surfs.java 0.1 99/11/5
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
 * nurbs_ct_surfs is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 5 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_ct_surfs {

  public nurbs_ct_surf geom = new nurbs_ct_surf();
  public nurbs_ct_surf color = new nurbs_ct_surf();
  public nurbs_ct_surf normal = new nurbs_ct_surf();
  public nurbs_ct_surf texture = new nurbs_ct_surf();

  public int s_bezier_cnt = 0;
  public int t_bezier_cnt = 0;

  public void draw(GLUnurbsObj nobj, GL JavaGL, int display_mode, nurbs_surfaces surface,
      int sfactors[][], int tfactors[][]) {
    nurbs_bz_surf n_map_b = new nurbs_bz_surf(JavaGL);
    boolean do_color, do_normal, do_texture;
    int i, j;

    n_map_b.set_property(display_mode, sfactors, tfactors, s_bezier_cnt, t_bezier_cnt);

    JavaGL.glEnable(surface.geom.type);
    if (color.ctrl != null) {
      JavaGL.glEnable(surface.color.type);
      do_color = true;
    } else {
      do_color = false;
    }
    if (normal.ctrl != null) {
      JavaGL.glEnable(surface.normal.type);
      do_normal = true;
    } else {
      do_normal = false;
    }
    if (texture.ctrl != null) {
      JavaGL.glEnable(surface.texture.type);
      do_texture = true;
    } else {
      do_texture = false;
    }

    for (j = 0; j < s_bezier_cnt; j++) {
      for (i = 0; i < t_bezier_cnt; i++) {
        if (!nobj.culling_test_3d(geom.offsets[j][i], surface.geom.s.order, surface.geom.t.order,
            geom.s_stride, geom.t_stride, surface.geom.dim)) {
          geom.draw(JavaGL, surface.geom, j, i);
          if (do_color) {
            color.draw(JavaGL, surface.color, j, i);
          }
          if (do_normal) {
            normal.draw(JavaGL, surface.normal, j, i);
          }
          if (do_texture) {
            texture.draw(JavaGL, surface.texture, j, i);
          }
          n_map_b.map(j, i);
        }
      }
    }
  }

  public void augment(nurbs_surfaces surface) {
    s_bezier_cnt = geom.s_pt_cnt / surface.geom.s.order;
    t_bezier_cnt = geom.t_pt_cnt / surface.geom.t.order;

    geom.augment(s_bezier_cnt, t_bezier_cnt, surface.geom);

    if (color.ctrl != null) {
      color.augment(s_bezier_cnt, t_bezier_cnt, surface.color);
    }
    if (normal.ctrl != null) {
      normal.augment(s_bezier_cnt, t_bezier_cnt, surface.normal);
    }
    if (texture.ctrl != null) {
      texture.augment(s_bezier_cnt, t_bezier_cnt, surface.texture);
    }
  }

  public void convert(nurbs_knot geom_s_knot, nurbs_knot geom_t_knot, nurbs_knot color_s_knot,
      nurbs_knot color_t_knot, nurbs_knot normal_s_knot, nurbs_knot normal_t_knot,
      nurbs_knot texture_s_knot, nurbs_knot texture_t_knot, nurbs_surfaces surface) {
    geom.convert(geom_s_knot, geom_t_knot, surface.geom);

    // Yes, if xxx_s_knot has been set, xxx_t_knot must be set, too.
    if (color_s_knot.unified_nknots != 0) {
      color.convert(color_s_knot, color_t_knot, surface.color);
    }
    if (normal_s_knot.unified_nknots != 0) {
      normal.convert(normal_s_knot, normal_t_knot, surface.normal);
    }
    if (texture_s_knot.unified_nknots != 0) {
      texture.convert(texture_s_knot, texture_t_knot, surface.texture);
    }
  }

}
