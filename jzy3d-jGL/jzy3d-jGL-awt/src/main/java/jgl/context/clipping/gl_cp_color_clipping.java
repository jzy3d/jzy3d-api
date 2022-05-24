/*
 * @(#)gl_cp_color_clipping.java 0.4 99/12/03
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
 * gl_cp_color_clipping is the clipping class for clipping plane with color of JavaGL 2.1.
 *
 * @version 0.4, 3 Dev 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_cp_color_clipping extends gl_cp_clipping {

  protected gl_vertex inter_point(gl_vertex v1, gl_vertex v2, int i, int j) {
    gl_vertex temp = super.inter_point(v1, v2, i, j);
    temp.Color = inter_color(v1, v2);
    return temp;
  }

  public gl_cp_color_clipping(gl_context cc) {
    super(cc);
    // nf_clipping = new gl_nf_color_clipping (CC);
  }

}
