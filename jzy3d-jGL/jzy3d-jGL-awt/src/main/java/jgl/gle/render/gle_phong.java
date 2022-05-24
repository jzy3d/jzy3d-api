/*
 * @(#)gle_phong.java 0.5 02/12/23
 *
 * jGL 3-D graphics library for Java Copyright (c) 2000-2002 Robin Bing-Yu Chen
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
import jgl.context.render.gl_depth;
import jgl.gle.gle_context;
import jgl.gle.gle_vertex;

/**
 * gle_phong is the rendering class for phong shading as an extension of jGL 2.5.
 *
 * @version 0.5, 23 Dev 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_phong extends gl_depth {
  // Use TexCoord field for storing positions

  // Members for Line
  private float LineN[][] = new float[2][3];
  private float LineP[][] = new float[2][3];
  protected float nx, ny, nz;
  protected float px, py, pz;
  private float dnxdx = 0, dnxdy = 0;
  private float dnydx = 0, dnydy = 0;
  private float dnzdx = 0, dnzdy = 0;
  private float dpxdx = 0, dpxdy = 0;
  private float dpydx = 0, dpydy = 0;
  private float dpzdx = 0, dpzdy = 0;

  protected void init_phong(gle_vertex v1, gle_vertex v2) {
    LineN[0][0] = v1.Normal[0];
    LineN[0][1] = v1.Normal[1];
    LineN[0][2] = v1.Normal[2];
    LineN[1][0] = v2.Normal[0];
    LineN[1][1] = v2.Normal[1];
    LineN[1][2] = v2.Normal[2];
    LineP[0][0] = v1.TexCoord[0];
    LineP[0][1] = v1.TexCoord[1];
    LineP[0][2] = v1.TexCoord[2];
    LineP[1][0] = v2.TexCoord[0];
    LineP[1][1] = v2.TexCoord[1];
    LineP[1][2] = v2.TexCoord[2];
  }

  protected void init(gl_vertex v1, gl_vertex v2) {
    init_xy(v1, v2);
    init_phong((gle_vertex) v1, (gle_vertex) v2);
  }

  protected void set_first_phong() {
    nx = LineN[0][0];
    ny = LineN[0][1];
    nz = LineN[0][2];
    px = LineP[0][0];
    py = LineP[0][1];
    pz = LineP[0][2];
  }

  protected void set_first_point() {
    set_first_xy();
    set_first_phong();
  }

  protected void init_phong_dx(int dx) {
    dnxdx = (LineN[1][0] - LineN[0][0]) / (float) dx;
    dnydx = (LineN[1][1] - LineN[0][1]) / (float) dx;
    dnzdx = (LineN[1][2] - LineN[0][2]) / (float) dx;
    dpxdx = (LineP[1][0] - LineP[0][0]) / (float) dx;
    dpydx = (LineP[1][1] - LineP[0][1]) / (float) dx;
    dpzdx = (LineP[1][2] - LineP[0][2]) / (float) dx;
  }

  protected void init_phong_dy(int dy) {
    dnxdy = (LineN[1][0] - LineN[0][0]) / (float) dy;
    dnydy = (LineN[1][1] - LineN[0][1]) / (float) dy;
    dnzdy = (LineN[1][2] - LineN[0][2]) / (float) dy;
    dpxdy = (LineP[1][0] - LineP[0][0]) / (float) dy;
    dpydy = (LineP[1][1] - LineP[0][1]) / (float) dy;
    dpzdy = (LineP[1][2] - LineP[0][2]) / (float) dy;
  }

  protected void init_dx(int dx) {
    init_phong_dx(dx);
  }

  protected void init_dy(int dy) {
    init_phong_dy(dy);
  }

  protected void phong_inc_x() {
    nx += dnxdx;
    ny += dnydx;
    nz += dnzdx;
    px += dpxdx;
    py += dpydx;
    pz += dpzdx;
  }

  protected void phong_dec_x() {
    nx -= dnxdx;
    ny -= dnydx;
    nz -= dnzdx;
    px -= dpxdx;
    py -= dpydx;
    pz -= dpzdx;
  }

  protected void phong_inc_y() {
    nx += dnxdy;
    ny += dnydy;
    nz += dnzdy;
    px += dpxdy;
    py += dpydy;
    pz += dpzdy;
  }

  protected void inc_x() {
    x_inc_x();
    phong_inc_x();
  }

  protected void dec_x() {
    x_dec_x();
    phong_dec_x();
  }

  protected void inc_y() {
    y_inc_y();
    phong_inc_y();
  }

  protected int get_color() {
    float coord[] = {px, py, pz};
    float norm[] = {nx, ny, nz};
    return ((gle_context) CC).ColorTransformation(coord, norm);
  }

  protected void put_pixel() {
    pixel.put_pixel(x, y, get_color());
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, get_color());
  }

  protected void draw_horizontal_line(int x1, int x2, int y, float nx1, float ny1, float nz1,
      float px1, float py1, float pz1) {
    this.LineN[0][0] = nx1;
    this.LineN[0][1] = ny1;
    this.LineN[0][2] = nz1;
    this.LineP[0][0] = px1;
    this.LineP[0][1] = py1;
    this.LineP[0][2] = pz1;
    draw_horizontal_line(x1, x2, y);
  }

  // Members for Trangle
  private float TriN[][] = new float[3][3];
  private float TriP[][] = new float[3][3];
  protected float LeftPointNX, LeftPointNY, LeftPointNZ;
  protected float LeftPointPX, LeftPointPY, LeftPointPZ;
  private float dnxdxy, dnxdyy;
  private float dnydxy, dnydyy;
  private float dnzdxy, dnzdyy;
  private float dpxdxy, dpxdyy;
  private float dpydxy, dpydyy;
  private float dpzdxy, dpzdyy;

  protected void init_phong(gle_vertex v1, gle_vertex v2, gle_vertex v3) {
    TriN[0][0] = v1.Normal[0];
    TriN[0][1] = v1.Normal[1];
    TriN[0][2] = v1.Normal[2];
    TriN[1][0] = v2.Normal[0];
    TriN[1][1] = v2.Normal[1];
    TriN[1][2] = v2.Normal[2];
    TriN[2][0] = v3.Normal[0];
    TriN[2][1] = v3.Normal[1];
    TriN[2][2] = v3.Normal[2];
    TriP[0][0] = v1.TexCoord[0];
    TriP[0][1] = v1.TexCoord[1];
    TriP[0][2] = v1.TexCoord[2];
    TriP[1][0] = v2.TexCoord[0];
    TriP[1][1] = v2.TexCoord[1];
    TriP[1][2] = v2.TexCoord[2];
    TriP[2][0] = v3.TexCoord[0];
    TriP[2][1] = v3.TexCoord[1];
    TriP[2][2] = v3.TexCoord[2];
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    init_xy(v1, v2, v3);
    init_phong((gle_vertex) v1, (gle_vertex) v2, (gle_vertex) v3);
  }

  protected void set_left_phong(int pos) {
    LeftPointNX = TriN[pos][0];
    LeftPointNY = TriN[pos][1];
    LeftPointNZ = TriN[pos][2];
    LeftPointPX = TriP[pos][0];
    LeftPointPY = TriP[pos][1];
    LeftPointPZ = TriP[pos][2];
  }

  protected void set_left(int pos) {
    set_left_xy(pos);
    set_left_phong(pos);
  }

  protected void init_phong_dx_dy(int area, int left, int right, int top) {
    float dl = TriN[left][0] - TriN[top][0];
    float dr = TriN[right][0] - TriN[top][0];
    dnxdx = (dr * dyl - dl * dyr) / area;
    dnxdy = (dl * dxr - dr * dxl) / area;
    dl = TriN[left][1] - TriN[top][1];
    dr = TriN[right][1] - TriN[top][1];
    dnydx = (dr * dyl - dl * dyr) / area;
    dnydy = (dl * dxr - dr * dxl) / area;
    dl = TriN[left][2] - TriN[top][2];
    dr = TriN[right][2] - TriN[top][2];
    dnzdx = (dr * dyl - dl * dyr) / area;
    dnzdy = (dl * dxr - dr * dxl) / area;
    dl = TriP[left][0] - TriP[top][0];
    dr = TriP[right][0] - TriP[top][0];
    dpxdx = (dr * dyl - dl * dyr) / area;
    dpxdy = (dl * dxr - dr * dxl) / area;
    dl = TriP[left][1] - TriP[top][1];
    dr = TriP[right][1] - TriP[top][1];
    dpydx = (dr * dyl - dl * dyr) / area;
    dpydy = (dl * dxr - dr * dxl) / area;
    dl = TriP[left][2] - TriP[top][2];
    dr = TriP[right][2] - TriP[top][2];
    dpzdx = (dr * dyl - dl * dyr) / area;
    dpzdy = (dl * dxr - dr * dxl) / area;
  }

  protected void init_dx_dy(int area, int left, int right, int top) {
    init_phong_dx_dy(area, left, right, top);
  }

  protected void init_phong_other(boolean delta, int dy) {
    dnxdxy = dnxdx * (float) dy + dnxdy;
    if (delta) {
      dnxdyy = dnxdxy - dnxdx;
    } else {
      dnxdyy = dnxdxy + dnxdx;
    }
    dnydxy = dnydx * (float) dy + dnydy;
    if (delta) {
      dnydyy = dnydxy - dnydx;
    } else {
      dnydyy = dnydxy + dnydx;
    }
    dnzdxy = dnzdx * (float) dy + dnzdy;
    if (delta) {
      dnzdyy = dnzdxy - dnzdx;
    } else {
      dnzdyy = dnzdxy + dnzdx;
    }
    dpxdxy = dpxdx * (float) dy + dpxdy;
    if (delta) {
      dpxdyy = dpxdxy - dpxdx;
    } else {
      dpxdyy = dpxdxy + dpxdx;
    }
    dpydxy = dpydx * (float) dy + dpydy;
    if (delta) {
      dpydyy = dpydxy - dpydx;
    } else {
      dpydyy = dpydxy + dpydx;
    }
    dpzdxy = dpzdx * (float) dy + dpzdy;
    if (delta) {
      dpzdyy = dpzdxy - dpzdx;
    } else {
      dpzdyy = dpzdxy + dpzdx;
    }
  }

  protected void init_other(boolean delta, int dy) {
    init_phong_other(delta, dy);
  }

  protected void phong_inc_y_once() {
    LeftPointNX += dnxdxy;
    LeftPointNY += dnydxy;
    LeftPointNZ += dnzdxy;
    LeftPointPX += dpxdxy;
    LeftPointPY += dpydxy;
    LeftPointPZ += dpzdxy;
  }

  protected void inc_y_once() {
    phong_inc_y_once();
  }

  protected void phong_inc_y_more() {
    LeftPointNX += dnxdyy;
    LeftPointNY += dnydyy;
    LeftPointNZ += dnzdyy;
    LeftPointPX += dpxdyy;
    LeftPointPY += dpydyy;
    LeftPointPZ += dpzdyy;
  }

  protected void inc_y_more() {
    phong_inc_y_more();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, RightPoint, y, LeftPointNX, LeftPointNY, LeftPointNZ,
        LeftPointPX, LeftPointPY, LeftPointPZ);
  }

  public gle_phong(gle_context cc) {
    super(cc);
  }

}
