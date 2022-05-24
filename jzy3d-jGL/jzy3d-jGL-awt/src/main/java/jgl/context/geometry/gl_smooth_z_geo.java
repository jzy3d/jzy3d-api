/*
 * @(#)gl_smooth_z_geo.java 0.4 99/12/04
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-1999 Robin Bing-Yu Chen
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

package jgl.context.geometry;

import jgl.context.gl_context;
import jgl.context.gl_pointer;

/**
 * gl_smooth_z_geo is geometry class for smooth shading with depth of JavaGL 2.1.
 *
 * @version 0.4, 4 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_smooth_z_geo extends gl_smooth_geo {

  protected void draw_point(float p[], int i) {
    CR.pixel.put_pixel((int) (p[0] + (float) 0.5), (int) (p[1] + (float) 0.5), p[2],
        VertexColor[i]);
  }

  public gl_smooth_z_geo(gl_context cc, gl_pointer cr) {
    super(cc, cr);
  }

}
