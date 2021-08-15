/*
 * @(#)nurbs_surfaces.java 0.1 99/11/5
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

import jgl.wt.awt.GLU;

/**
 * nurbs_surfaces is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 5 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_surfaces {

  public nurbs_surface geom = new nurbs_surface();
  public nurbs_surface color = new nurbs_surface();
  public nurbs_surface normal = new nurbs_surface();
  public nurbs_surface texture = new nurbs_surface();

  public int fill_knot(nurbs_knot geom_s_knot, nurbs_knot geom_t_knot, nurbs_knot color_s_knot,
      nurbs_knot color_t_knot, nurbs_knot normal_s_knot, nurbs_knot normal_t_knot,
      nurbs_knot texture_s_knot, nurbs_knot texture_t_knot) {
    int err;

    err = geom_s_knot.fill(geom.s);
    if (err != GLU.GLU_NO_ERROR) {
      return err;
    }
    err = geom_t_knot.fill(geom.t);
    if (err != GLU.GLU_NO_ERROR) {
      return err;
    }

    if (color.type != GLU.GLU_INVALID_ENUM) {
      err = color_s_knot.fill(color.s);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
      err = color_t_knot.fill(color.t);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
    }

    if (normal.type != GLU.GLU_INVALID_ENUM) {
      err = normal_s_knot.fill(normal.s);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
      err = normal_t_knot.fill(normal.t);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
    }

    if (texture.type != GLU.GLU_INVALID_ENUM) {
      err = texture_s_knot.fill(texture.s);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
      err = texture_t_knot.fill(texture.t);
      if (err != GLU.GLU_NO_ERROR) {
        return err;
      }
    }

    return GLU.GLU_NO_ERROR;
  }

  public int test() {
    int err = geom.test();
    if (err != GLU.GLU_NO_ERROR) {
      return (err);
    }
    if (color.type != GLU.GLU_INVALID_ENUM) {
      err = color.test();
      if (err != GLU.GLU_NO_ERROR) {
        return (err);
      }
    }
    if (normal.type != GLU.GLU_INVALID_ENUM) {
      err = normal.test();
      if (err != GLU.GLU_NO_ERROR) {
        return (err);
      }
    }
    if (texture.type != GLU.GLU_INVALID_ENUM) {
      err = texture.test();
      if (err != GLU.GLU_NO_ERROR) {
        return (err);
      }
    }
    return GLU.GLU_NO_ERROR;
  }

}
