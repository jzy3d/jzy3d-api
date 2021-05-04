/*
 * @(#)gl_select_pixel.java 0.2 99/11/28
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

package jgl.context.render.pixel;

import jgl.context.gl_context;

/**
 * gl_select_pixel is the pixel rendering class for selection of JavaGL 2.1.
 *
 * @version 0.2, 28 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_select_pixel extends gl_render_pixel {

  public void put_pixel(int x, int y, float z, int color) {
    CC.Select.update_hit_flag(z);
  }

  public void put_pixel(int x, int y, float z, int c[]) {
    CC.Select.update_hit_flag(z);
  }

  public gl_select_pixel(gl_context cc) {
    super(cc);
  }

}
