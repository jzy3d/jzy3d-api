/*
 * @(#)gl_stipple_line_pixel.java 0.2 99/11/28
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
import jgl.context.gl_pointer;
import jgl.context.gl_vertex;
import jgl.context.clipping.gl_2d_clipping;

/**
 * gl_stipple_line_pixel is the pixel class for stippled line of JavaGL 2.1.
 *
 * @version 0.2, 28 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_stipple_line_pixel extends gl_stipple_pixel {

  private gl_pointer CR;
  private gl_2d_clipping clip;
  private float width[];
  private int repeat;
  private int step;

  /**
   * Calculate the line width for GL_LINE, return du and dv of it. So, the point (x,y) will be a
   * short line from (x+du,y-dv) to (x-du,y+dv)
   */
  private float[] cal_line_size(float dx, float dy) {
    float[] xy = new float[2]; /* 0 for x, 1 for y */
    float wl = CC.Raster.LineWidth / (float) Math.sqrt(dx * dx + dy * dy);

    xy[0] = (float) (0.5 * wl * dy);
    xy[1] = (float) (0.5 * wl * dx);

    return xy;
  }

  /** Initial the stippled line parameters */
  public void init(int dx, int dy) {
    if ((CC.Raster.LineWidth > 1) && ((dx != 0) || (dy != 0))) {
      width = cal_line_size(dx, dy);
    } else {
      width = new float[2];
      width[0] = 0;
      width[1] = 0;
    }
    repeat = CC.Raster.LineStippleRepeat;
    step = 0;
  }

  /** Put a pixel for stippled line */
  public void put_pixel(int x, int y, int color) {
    put_pixel(x, y, 0, color);
  }

  public void put_pixel(int x, int y, float z, int color) {
    int shift = step;

    if (CC.Raster.LineStipple) {
      repeat--;
      if (repeat == 0) {
        repeat = CC.Raster.LineStippleRepeat;
        step++;
        if (step == 16) {
          step = 0;
        }
      }
      if (((1 << shift) & CC.Raster.LineStipplePattern) == 0) {
        return;
      }
    }

    gl_vertex temp[] = new gl_vertex[2];

    temp[0] = new gl_vertex();
    temp[1] = new gl_vertex();

    temp[0].Vertex[0] = x + width[0];
    temp[0].Vertex[1] = y - width[1];
    temp[1].Vertex[0] = x - width[0];
    temp[1].Vertex[1] = y + width[1];

    temp = clip.clip_line(temp);

    if (temp == null) {
      return;
    }

    temp[0].Vertex[2] = z;
    temp[1].Vertex[2] = z;

    CR.line.draw_line(temp[0], temp[1], color);
  }

  public gl_stipple_line_pixel(gl_context cc, gl_pointer cr) {
    super(cc);
    CR = cr;
    clip = new gl_2d_clipping(CC.Viewport.Width - 1, CC.Viewport.Height - 1);
  }

}
