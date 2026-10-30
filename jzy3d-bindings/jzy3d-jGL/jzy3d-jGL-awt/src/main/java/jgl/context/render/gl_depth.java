/*
 * @(#)gl_depth.java 0.4 99/11/29
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
 * gl_depth is the rendering class for depth of JavaGL 2.1.
 *
 * @version 0.4, 29 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_depth extends gl_render {

  // Members for Line
  protected float LineZ[] = new float[2];
  protected float z;
  protected float dzdx = 0, dzdy = 0;

  protected void init_z(gl_vertex v1, gl_vertex v2) {
    LineZ[0] = v1.Vertex[2];
    LineZ[1] = v2.Vertex[2];
  }

  protected void init(gl_vertex v1, gl_vertex v2) {
    super.init(v1, v2);
    init_z(v1, v2);
  }

  protected void set_first_z() {
    z = LineZ[0];
  }

  protected void set_first_point() {
    super.set_first_point();
    set_first_z();
  }

  protected void init_z_dx(int dx) {
    dzdx = (LineZ[1] - LineZ[0]) / (float) dx;
  }

  protected void init_z_dy(int dy) {
    dzdy = (LineZ[1] - LineZ[0]) / (float) dy;
  }

  protected void init_dx(int dx) {
    init_z_dx(dx);
  }

  protected void init_dy(int dy) {
    init_z_dy(dy);
  }

  protected void z_inc_x() {
    z += dzdx;
  }

  protected void z_dec_x() {
    z -= dzdx;
  }

  protected void z_inc_y() {
    z += dzdy;
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

  protected void put_pixel() {
    pixel.put_pixel(x, y, z, color);
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, z, color);
  }

  /**
   * Draw a flat horizontal line in the Color Buffer with depth value, assume that x1 is in the left
   * side of x2
   */
  protected void draw_horizontal_line(int x1, int x2, int y, float z1) {
    this.LineZ[0] = z1;
    draw_horizontal_line(x1, x2, y);
  }

  // Members for Trangle
  private float TriZ[] = new float[3];
  protected float LeftPointZ;
  private float dzdxy, dzdyy;

  protected void init_z(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    TriZ[0] = v1.Vertex[2];
    TriZ[1] = v2.Vertex[2];
    TriZ[2] = v3.Vertex[2];
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    super.init(v1, v2, v3);
    init_z(v1, v2, v3);
  }

  protected void set_left_z(int pos) {
    LeftPointZ = TriZ[pos];
  }

  protected void set_left(int pos) {
    super.set_left(pos);
    set_left_z(pos);
  }

  protected void init_z_dx_dy(int area, int left, int right, int top) {
    float dl = TriZ[left] - TriZ[top];
    float dr = TriZ[right] - TriZ[top];
    dzdx = (dr * dyl - dl * dyr) / area;
    dzdy = (dl * dxr - dr * dxl) / area;
  }

  protected void init_dx_dy(int area, int left, int right, int top) {
    init_z_dx_dy(area, left, right, top);
  }

  protected void init_z_other(boolean delta, int dy) {
    dzdxy = dzdx * (float) dy + dzdy;
    if (delta) {
      dzdyy = dzdxy - dzdx;
    } else {
      dzdyy = dzdxy + dzdx;
    }
  }

  protected void init_other(boolean delta, int dy) {
    init_z_other(delta, dy);
  }

  protected void z_inc_y_once() {
    LeftPointZ += dzdxy;
  }

  protected void inc_y_once() {
    z_inc_y_once();
  }

  protected void z_inc_y_more() {
    LeftPointZ += dzdyy;
  }

  protected void inc_y_more() {
    z_inc_y_more();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, RightPoint, y, LeftPointZ);
  }

  public gl_depth(gl_context cc) {
    super(cc);
  }

}
