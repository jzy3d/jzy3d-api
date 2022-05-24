/*
 * @(#)gl_vp_color_clipping.java 0.4 99/12/03
 *
 * jGL 3-D graphics library for Java Copyright (c) 1997-1999 Robin Bing-Yu Chen
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

package jgl.context.clipping;

import jgl.context.gl_context;
import jgl.context.gl_vertex;

/**
 * gl_vp_color_clipping is the clipping class for viewport with color of JavaGL 2.1.
 *
 * @version 0.4, 3 Dev 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_vp_color_clipping extends gl_vp_clipping {

  protected gl_vertex inter_point_pos(gl_vertex v1, gl_vertex v2, int xy) {
    gl_vertex temp = super.inter_point_pos(v1, v2, xy);
    temp.Color = inter_color(v1, v2);
    return temp;
  }

  protected gl_vertex inter_point_neg(gl_vertex v1, gl_vertex v2, int xy) {
    gl_vertex temp = super.inter_point_neg(v1, v2, xy);
    temp.Color = inter_color(v1, v2);
    return temp;
  }

  public gl_vp_color_clipping(gl_context cc) {
    super(cc);
  }

}
