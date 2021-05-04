/*
 * @(#)gl_select_render.java 0.2 99/11/29
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

package jgl.context.render;

import jgl.context.gl_context;
import jgl.context.gl_polygon;
import jgl.context.gl_vertex;

/**
 * gl_select_render is the rendering class for selection of JavaGL 2.1.
 *
 * @version 0.2, 29 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_select_render extends gl_render {

  public void draw_line(gl_vertex v1, gl_vertex v2) {
    CC.Select.update_hit_flag(v1.Vertex[2]);
    CC.Select.update_hit_flag(v2.Vertex[2]);
  }

  public void draw_polygon(gl_polygon p) {
    int i;
    for (i = 0; i < p.n; i++) {
      CC.Select.update_hit_flag(p.Polygon[i].Vertex[2]);
    }
  }

  public gl_select_render(gl_context cc) {
    super(cc);
  }

}
