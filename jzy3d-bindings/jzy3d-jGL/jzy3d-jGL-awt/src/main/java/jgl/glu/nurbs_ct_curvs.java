/*
 * @(#)nurbs_ct_curvs.java 0.1 99/11/11
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
 * nurbs_ct_curvs is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 11 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_ct_curvs {

  public nurbs_ct_curv geom = new nurbs_ct_curv();
  public nurbs_ct_curv color = new nurbs_ct_curv();
  public nurbs_ct_curv normal = new nurbs_ct_curv();
  public nurbs_ct_curv texture = new nurbs_ct_curv();

  public int bezier_cnt = 0;

  public void draw(GLUnurbsObj nobj, GL JavaGL, nurbs_curves curve, int factors[]) {
    nurbs_bz_curv n_map_b = new nurbs_bz_curv(JavaGL);
    boolean do_color, do_normal, do_texture;
    int i;

    n_map_b.set_property(factors, bezier_cnt);

    JavaGL.glEnable(curve.geom.type);
    if (color.ctrl != null) {
      JavaGL.glEnable(curve.color.type);
      do_color = true;
    } else {
      do_color = false;
    }
    if (normal.ctrl != null) {
      JavaGL.glEnable(curve.normal.type);
      do_normal = true;
    } else {
      do_normal = false;
    }
    if (texture.ctrl != null) {
      JavaGL.glEnable(curve.texture.type);
      do_texture = true;
    } else {
      do_texture = false;
    }

    for (i = 0; i < bezier_cnt; i++) {
      if (!nobj.culling_test_2d(geom.offsets[i], curve.geom.c.order, geom.stride, curve.geom.dim)) {
        geom.draw(JavaGL, curve.geom, i);
        if (do_color) {
          color.draw(JavaGL, curve.color, i);
        }
        if (do_normal) {
          normal.draw(JavaGL, curve.normal, i);
        }
        if (do_texture) {
          texture.draw(JavaGL, curve.texture, i);
        }
        n_map_b.map(i);
      }
    }
  }

  public void augment(nurbs_curves curve) {
    bezier_cnt = geom.pt_cnt / curve.geom.c.order;

    geom.augment(bezier_cnt, curve.geom);

    if (color.ctrl != null) {
      color.augment(bezier_cnt, curve.color);
    }
    if (normal.ctrl != null) {
      normal.augment(bezier_cnt, curve.normal);
    }
    if (texture.ctrl != null) {
      texture.augment(bezier_cnt, curve.texture);
    }
  }

  public void convert(nurbs_knot geom_knot, nurbs_knot color_knot, nurbs_knot normal_knot,
      nurbs_knot texture_knot, nurbs_curves curve) {
    geom.convert(geom_knot, curve.geom);

    if (color_knot.unified_nknots != 0) {
      color.convert(color_knot, curve.color);
    }
    if (normal_knot.unified_nknots != 0) {
      normal.convert(normal_knot, curve.normal);
    }
    if (texture_knot.unified_nknots != 0) {
      texture.convert(texture_knot, curve.texture);
    }
  }

}
