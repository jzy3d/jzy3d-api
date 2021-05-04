/*
 * @(#)gl_stipple_poly_pixel.java 0.1 99/11/30
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
 * gl_stipple_poly_pixel is pixel class for stippled polygon of JavaGL 2.1.
 *
 * @version 0.1, 30 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_stipple_poly_pixel extends gl_stipple_pixel {

  /** Initial the stippled line parameters */
  private boolean test(int x, int y) {
    int shift = x - ((x >> 5) << 5);
    int yindex = y - ((y >> 5) << 5);
    int xindex = shift >> 3;

    shift = shift - (xindex << 3);

    if (CC.Raster.PolygonStipple) {
      if (((1 << shift) & CC.Raster.PolygonStipplePattern[yindex][xindex]) == 0) {
        return false;
      }
    }

    return true;
  }

  /** Put a pixel for stippled polygon */
  public void put_pixel(int x, int y, int color) {
    if (test(x, y)) {
      super.put_pixel(x, y, color);
    }
  }

  public void put_pixel(int x, int y, float z, int color) {
    if (test(x, y)) {
      super.put_pixel(x, y, z, color);
    }
  }

  public gl_stipple_poly_pixel(gl_context cc) {
    super(cc);
  }

}
