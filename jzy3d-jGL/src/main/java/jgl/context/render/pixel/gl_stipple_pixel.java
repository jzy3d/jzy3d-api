/*
 * @(#)gl_stipple_pixel.java 0.1 99/12/03
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

package jgl.context.render.pixel;

import jgl.context.gl_context;

/**
 * gl_stipple_pixel is pixel class for stipple of JavaGL 2.1.
 *
 * @version 0.1, 3 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public abstract class gl_stipple_pixel extends gl_render_pixel {

  /** Put a pixel for stippled polygon */
  public void put_pixel_by_index(int index, int color) {
    int y = index / CC.Viewport.Width;
    int x = index - CC.Viewport.Width * y;
    put_pixel(x, y, color);
  }

  public void put_pixel_by_index(int index, float z, int color) {
    int y = index / CC.Viewport.Width;
    int x = index - CC.Viewport.Width * y;
    put_pixel(x, y, z, color);
  }

  public gl_stipple_pixel(gl_context cc) {
    super(cc);
  }

}
