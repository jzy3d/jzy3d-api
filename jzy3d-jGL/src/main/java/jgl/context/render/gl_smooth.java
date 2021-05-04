/*
 * @(#)gl_smooth.java 0.5 06/11/23
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2006 Robin Bing-Yu Chen (robin@ntu.edu.tw)
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
import jgl.context.gl_vertex;

/**
 * gl_smooth is the rendering class with smooth shading of jGL 2.4.
 *
 * @version 0.5, 23 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_smooth extends gl_depth {

  // Members for Line
  private int dxy, dxy2;

  private int LineRGB[][] = new int[2][4];
  protected int rgb[] = new int[4];
  private int ergb[] = new int[4];
  private int drgb[] = new int[4];
  private int drgbdyy[] = new int[4];
  private int drgbdy[] = new int[4];

  protected void init_rgb(gl_vertex v1, gl_vertex v2) {
    LineRGB[0] = v1.Color;
    LineRGB[1] = v2.Color;
  }

  protected void init(gl_vertex v1, gl_vertex v2) {
    init_xy(v1, v2);
    init_rgb(v1, v2);
  }

  protected void set_first_rgb() {
    rgb[0] = LineRGB[0][0];
    rgb[1] = LineRGB[0][1];
    rgb[2] = LineRGB[0][2];
    rgb[3] = LineRGB[0][3];
  }

  protected void set_first_point() {
    set_first_xy();
    set_first_rgb();
  }

  protected void init_drgb(int d) {
    if (d == 0) {
      return;
    }

    boolean deltargb[] = new boolean[4];

    dxy = d;
    dxy2 = dxy >> 1;

    if ((dxy & 0x00000001) == 1) {
      ergb[0] = ergb[1] = ergb[2] = ergb[3] = 0;
    } else {
      ergb[0] = ergb[1] = ergb[2] = ergb[3] = 1;
    }

    for (int i = 0; i < 4; i++) {
      drgb[i] = LineRGB[1][i] - LineRGB[0][i];
      deltargb[i] = (drgb[i] < 0);
      if (deltargb[i]) {
        drgb[i] = -drgb[i];
      }
      drgbdy[i] = 0;
      while (drgb[i] > dxy) {
        drgbdy[i]++;
        drgb[i] -= dxy;
      }
      drgbdyy[i] = drgbdy[i] + 1;
      if (deltargb[i]) {
        drgbdy[i] = -drgbdy[i];
        drgbdyy[i] = -drgbdyy[i];
      }
    }
  }

  protected void init_dx_dy(int dx, int dy) {
    if (dx >= dy) {
      init_drgb(dx);
    } else {
      init_drgb(dy);
    }
  }

  protected void inc_rgb() {
    for (int i = 0; i < 4; i++) {
      ergb[i] += drgb[i];
      if (ergb[i] > dxy2) {
        ergb[i] -= dxy;
        rgb[i] += drgbdyy[i];
      } else {
        rgb[i] += drgbdy[i];
      }
    }
  }

  protected void inc_x() {
    x_inc_x();
    inc_rgb();
  }

  protected void dec_x() {
    x_dec_x();
    inc_rgb();
  }

  protected void inc_y() {
    y_inc_y();
    inc_rgb();
  }

  protected void inc_x_inc_y() {
    x_inc_x();
    inc_y();
  }

  protected void dec_x_inc_y() {
    x_dec_x();
    inc_y();
  }

  protected void put_pixel() {
    pixel.put_pixel(x, y, rgb);
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, rgb);
  }

  // public void draw_horizontal_line (int x1, int rgb1 [],
  // int x2, int rgb2 [], int y) {
  protected void draw_horizontal_line(int x1, int rgb1[], int x2, int rgb2[], int y) {
    this.LineRGB[0] = rgb1;
    this.LineRGB[1] = rgb2;
    init_drgb(x2 - x1);
    draw_horizontal_line(x1, x2, y);
  }

  // Members for Triangle
  private int TriRGB[][] = new int[3][4];
  private int drgbl[] = new int[4];
  private int drgbr[] = new int[4];
  private int errrgbl[] = new int[4];
  private int errrgbr[] = new int[4];
  private int drgbdyyl[] = new int[4];
  private int drgbdyyr[] = new int[4];
  private int drgbdyl[] = new int[4];
  private int drgbdyr[] = new int[4];
  protected int RightPointRGB[] = new int[4];
  protected int LeftPointRGB[] = new int[4];

  protected void init_color(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    TriRGB[0] = v1.Color;
    TriRGB[1] = v2.Color;
    TriRGB[2] = v3.Color;
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    init_xy(v1, v2, v3);
    init_color(v1, v2, v3);
  }

  protected void set_left_color(int pos) {
    LeftPointRGB[0] = TriRGB[pos][0];
    LeftPointRGB[1] = TriRGB[pos][1];
    LeftPointRGB[2] = TriRGB[pos][2];
    LeftPointRGB[3] = TriRGB[pos][3];
  }

  protected void set_right_color(int pos) {
    RightPointRGB[0] = TriRGB[pos][0];
    RightPointRGB[1] = TriRGB[pos][1];
    RightPointRGB[2] = TriRGB[pos][2];
    RightPointRGB[3] = TriRGB[pos][3];
  }

  protected void set_left(int pos) {
    set_left_xy(pos);
    set_left_color(pos);
  }

  protected void set_right(int pos) {
    set_right_xy(pos);
    set_right_color(pos);
  }

  protected void init_dx_dy(int area, int left, int right, int top) {}

  protected void init_other(boolean delta, int dy) {}

  protected void init_left_rgb(int down, int top) {
    boolean deltargbl[] = new boolean[4];

    for (int i = 0; i < 4; i++) {
      drgbl[i] = TriRGB[down][i] - TriRGB[top][i];
      deltargbl[i] = (drgbl[i] < 0);
      if (deltargbl[i]) {
        drgbl[i] = -drgbl[i];
      }
      drgbdyl[i] = 0;
      if (dyl == 0) {
        errrgbl[i] = 1;
        drgbdyyl[i] = 1;
      } else {
        if ((dyl & 0x00000001) == 1) {
          errrgbl[i] = 0;
        } else {
          errrgbl[i] = 1;
        }
        while (drgbl[i] > dyl) {
          drgbdyl[i]++;
          drgbl[i] -= dyl;
        }
        drgbdyyl[i] = drgbdyl[i] + 1;
      }
      if (deltargbl[i]) {
        drgbdyl[i] = -drgbdyl[i];
        drgbdyyl[i] = -drgbdyyl[i];
      }
    }
  }

  protected void init_right_rgb(int down, int top) {
    boolean deltargbr[] = new boolean[4];

    for (int i = 0; i < 4; i++) {
      drgbr[i] = TriRGB[down][i] - TriRGB[top][i];
      deltargbr[i] = (drgbr[i] < 0);
      if (deltargbr[i]) {
        drgbr[i] = -drgbr[i];
      }
      drgbdyr[i] = 0;
      if (dyr == 0) {
        errrgbr[i] = 1;
        drgbdyyr[i] = 1;
      } else {
        if ((dyr & 0x00000001) == 1) {
          errrgbr[i] = 0;
        } else {
          errrgbr[i] = 1;
        }
        while (drgbr[i] > dyr) {
          drgbdyr[i]++;
          drgbr[i] -= dyr;
        }
        drgbdyyr[i] = drgbdyr[i] + 1;
      }
      if (deltargbr[i]) {
        drgbdyr[i] = -drgbdyr[i];
        drgbdyyr[i] = -drgbdyyr[i];
      }
    }
  }

  protected void init_left(int down, int top) {
    init_left_xy();
    init_left_rgb(down, top);
  }

  protected void init_right(int down, int top) {
    init_right_xy();
    init_right_rgb(down, top);
  }

  protected void inc_y_once() {}

  protected void inc_y_more() {}

  protected void inc_left_rgb() {
    for (int i = 0; i < 4; i++) {
      errrgbl[i] += drgbl[i];
      if (errrgbl[i] > dyl2) {
        errrgbl[i] -= dyl;
        LeftPointRGB[i] += drgbdyyl[i];
      } else {
        LeftPointRGB[i] += drgbdyl[i];
      }
    }
  }

  protected void inc_right_rgb() {
    for (int i = 0; i < 4; i++) {
      errrgbr[i] += drgbr[i];
      if (errrgbr[i] > dyr2) {
        errrgbr[i] -= dyr;
        RightPointRGB[i] += drgbdyyr[i];
      } else {
        RightPointRGB[i] += drgbdyr[i];
      }
    }
  }

  protected void inc_left() {
    inc_left_xy();
    inc_left_rgb();
  }

  protected void inc_right() {
    inc_right_xy();
    inc_right_rgb();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, LeftPointRGB, RightPoint, RightPointRGB, y);
  }

  public gl_smooth(gl_context cc) {
    super(cc);
  }

}
