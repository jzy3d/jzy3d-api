/*
 * @(#)gl_lit_tex_z.java 0.1 01/02/16
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001 Robin Bing-Yu Chen
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
 * gl_lit_tex_z is the rendering class for texturing with lighting and depth of jGL 2.3.
 *
 * @version 0.1, 16 Feb 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_lit_tex_z extends gl_lit_tex {

  protected void init(gl_vertex v1, gl_vertex v2) {
    super.init(v1, v2);
    init_z(v1, v2);
  }

  protected void set_first_point() {
    super.set_first_point();
    set_first_z();
  }

  protected void init_dx(int dx) {
    super.init_dx(dx);
    init_z_dx(dx);
  }

  protected void init_dy(int dy) {
    super.init_dy(dy);
    init_z_dy(dy);
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
    pixel.put_pixel(x, y, z, w, s / w, t / w, r / w, dsdx, dsdy, dtdx, dtdy, drdx, drdy, rgb);
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, z, w, s / w, t / w, r / w, dsdx, dsdy, dtdx, dtdy, drdx, drdy, rgb);
  }

  /**
   * Draw a flat horizontal line in the Color Buffer with depth value, assume that x1 is in the left
   * side of x2
   */
  private void draw_horizontal_line(int x1, int rgb1[], int x2, int rgb2[], int y, float z1,
      float w1, float s1, float t1, float r1) {
    this.LineZ[0] = z1;
    draw_horizontal_line(x1, rgb1, x2, rgb2, y, w1, s1, t1, r1);
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
    super.init_dx_dy(area, left, right, top);
    init_z_dx_dy(area, left, right, top);
  }

  protected void init_other(boolean delta, int dy) {
    super.init_other(delta, dy);
    init_z_other(delta, dy);
  }

  protected void inc_y_once() {
    super.inc_y_once();
    z_inc_y_once();
  }

  protected void inc_y_more() {
    super.inc_y_more();
    z_inc_y_more();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, LeftPointRGB, RightPoint, RightPointRGB, y, LeftPointZ,
        LeftPointW, LeftPointS, LeftPointT, LeftPointR);
  }

  public gl_lit_tex_z(gl_context cc) {
    super(cc);
  }

}
