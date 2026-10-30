/*
 * @(#)gle_phong_z.java 0.1 02/12/31
 *
 * jGL 3-D graphics library for Java Copyright (c) 2002 Robin Bing-Yu Chen
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

package jgl.gle.render;

import jgl.context.gl_vertex;
import jgl.gle.gle_context;

/**
 * gle_phong_z is the rendering class for phong shading with depth of jGL 2.5.
 *
 * @version 0.1, 31 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_phong_z extends gle_phong {

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
    pixel.put_pixel(x, y, z, get_color());
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, z, get_color());
  }

  private void draw_horizontal_line(int x1, int x2, int y, float z1, float nx1, float ny1,
      float nz1, float px1, float py1, float pz1) {
    this.LineZ[0] = z1;
    draw_horizontal_line(x1, x2, y, nx1, ny1, nz1, px1, py1, pz1);
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
    draw_horizontal_line(LeftPoint, RightPoint, y, LeftPointZ, LeftPointNX, LeftPointNY,
        LeftPointNZ, LeftPointPX, LeftPointPY, LeftPointPZ);
  }

  public gle_phong_z(gle_context cc) {
    super(cc);
  }

}
