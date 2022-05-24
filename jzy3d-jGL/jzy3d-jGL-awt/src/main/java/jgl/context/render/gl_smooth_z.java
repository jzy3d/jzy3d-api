/*
 * @(#)gl_smooth_z.java 0.4 99/11/30
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

package jgl.context.render;

import jgl.context.gl_context;
import jgl.context.gl_vertex;

/**
 * gl_smooth_z is the rendering class for smooth shading and depth of JavaGL 2.1.
 *
 * @version 0.4, 30 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_smooth_z extends gl_smooth {

  protected void init(gl_vertex v1, gl_vertex v2) {
    super.init(v1, v2);
    init_z(v1, v2);
  }

  protected void set_first_point() {
    super.set_first_point();
    set_first_z();
  }

  protected void init_dx_dy(int dx, int dy) {
    super.init_dx_dy(dx, dy);
    if (dx != 0) {
      init_dx(dx);
    }
    init_dy(dy);
  }

  protected void inc_x() {
    super.inc_x();
    z_inc_x();
  }

  protected void dec_x() {
    super.dec_x();
    z_dec_x();
  }

  protected void inc_y() {
    super.inc_y();
    z_inc_y();
  }

  protected void inc_x_inc_y() {
    super.inc_x_inc_y();
    z_inc_x();
  }

  protected void dec_x_inc_y() {
    super.dec_x_inc_y();
    z_dec_x();
  }

  protected void put_pixel() {
    pixel.put_pixel(x, y, z, rgb);
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, z, rgb);
  }

  private void draw_horizontal_line(int x1, int rgb1[], int x2, int rgb2[], int y, float z1) {
    this.LineZ[0] = z1;
    draw_horizontal_line(x1, rgb1, x2, rgb2, y);
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    super.init(v1, v2, v3);
    init_z(v1, v2, v3);
  }

  protected void set_left(int pos) {
    super.set_left(pos);
    set_left_z(pos);
  }

  protected void init_dx_dy(int area, int left, int right, int top) {
    init_z_dx_dy(area, left, right, top);
  }

  protected void init_other(boolean delta, int dy) {
    init_z_other(delta, dy);
  }

  protected void inc_y_once() {
    z_inc_y_once();
  }

  protected void inc_y_more() {
    z_inc_y_more();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, LeftPointRGB, RightPoint, RightPointRGB, y, LeftPointZ);
  }

  public gl_smooth_z(gl_context cc) {
    super(cc);
  }

}
