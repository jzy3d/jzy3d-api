/*
 * @(#)gl_lit_tex.java 0.2 03/05/16
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001-2003 Robin Bing-Yu Chen
 * (robin@nis-lab.is.s.u-tokyo.ac.jp)
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
 * gl_lit_tex is the rendering class for texturing with lighting of jGL 2.4.
 *
 * @version 0.2, 16 May 2003
 * @author Robin Bing-Yu Chen
 */

public class gl_lit_tex extends gl_smooth {

  // Members for Line
  protected float LineW[] = new float[2];
  protected float LineST[][] = new float[2][3];
  protected float w, dwdx = 0, dwdy = 0;
  protected float s, dsdx = 0, dsdy = 0;
  protected float t, dtdx = 0, dtdy = 0;
  protected float r, drdx = 0, drdy = 0;

  protected void init_tex(gl_vertex v1, gl_vertex v2) {
    LineW[0] = ((float) 1.0) / v1.Vertex[3];
    LineW[1] = ((float) 1.0) / v2.Vertex[3];
    LineST[0][0] = v1.TexCoord[0] * LineW[0];
    LineST[0][1] = v1.TexCoord[1] * LineW[0];
    LineST[0][2] = v1.TexCoord[2] * LineW[0];
    LineST[1][0] = v2.TexCoord[0] * LineW[1];
    LineST[1][1] = v2.TexCoord[1] * LineW[1];
    LineST[0][2] = v2.TexCoord[2] * LineW[1];
  }

  protected void init(gl_vertex v1, gl_vertex v2) {
    super.init(v1, v2);
    init_tex(v1, v2);
  }

  protected void set_first_tex() {
    w = LineW[0];
    s = LineST[0][0];
    t = LineST[0][1];
    r = LineST[0][2];
  }

  protected void set_first_point() {
    super.set_first_point();
    set_first_tex();
  }

  protected void init_tex_dx(int dx) {
    dwdx = (LineW[1] - LineW[0]) / (float) dx;
    dsdx = (LineST[1][0] - LineST[0][0]) / (float) dx;
    dtdx = (LineST[1][1] - LineST[0][1]) / (float) dx;
    drdx = (LineST[1][2] - LineST[0][2]) / (float) dx;
  }

  protected void init_tex_dy(int dy) {
    dwdy = (LineW[1] - LineW[0]) / (float) dy;
    dsdy = (LineST[1][0] - LineST[0][0]) / (float) dy;
    dtdy = (LineST[1][1] - LineST[0][1]) / (float) dy;
    drdy = (LineST[1][2] - LineST[0][2]) / (float) dy;
  }

  protected void init_dx(int dx) {
    init_tex_dx(dx);
  }

  protected void init_dy(int dy) {
    init_tex_dy(dy);
  }

  protected void init_dx_dy(int dx, int dy) {
    super.init_dx_dy(dx, dy);
    if (dx != 0) {
      init_dx(dx);
    }
    init_dy(dy);
  }

  protected void tex_inc_x() {
    w += dwdx;
    s += dsdx;
    t += dtdx;
    r += drdx;
  }

  protected void tex_dec_x() {
    w -= dwdx;
    s -= dsdx;
    t -= dtdx;
    r -= drdx;
  }

  protected void tex_inc_y() {
    w += dwdy;
    s += dsdy;
    t += dtdy;
    r += drdx;
  }

  protected void inc_x() {
    super.inc_x();
    tex_inc_x();
  }

  protected void dec_x() {
    super.dec_x();
    tex_dec_x();
  }

  protected void inc_y() {
    super.inc_y();
    tex_inc_y();
  }

  protected void inc_x_inc_y() {
    super.inc_x_inc_y();
    tex_inc_x();
  }

  protected void dec_x_inc_y() {
    super.dec_x_inc_y();
    tex_dec_x();
  }

  protected void put_pixel() {
    pixel.put_pixel(x, y, w, s / w, t / w, r / w, dsdx, dsdy, dtdx, dtdy, drdx, drdy, rgb);
  }

  protected void put_pixel_by_index() {
    pixel.put_pixel_by_index(x, w, s / w, t / w, r / w, dsdx, dsdy, dtdx, dtdy, drdx, drdy, rgb);
  }

  protected void draw_horizontal_line(int x1, int rgb1[], int x2, int rgb2[], int y, float w1,
      float s1, float t1, float r1) {
    this.LineW[0] = w1;
    this.LineST[0][0] = s1;
    this.LineST[0][1] = t1;
    this.LineST[0][2] = r1;
    draw_horizontal_line(x1, rgb1, x2, rgb2, y);
  }

  // Members for Trangle
  private float TriW[] = new float[3];
  private float TriST[][] = new float[3][3];
  protected float LeftPointW, LeftPointS, LeftPointT, LeftPointR;
  private float dwdxy, dwdyy;
  private float dsdxy, dsdyy;
  private float dtdxy, dtdyy;
  private float drdxy, drdyy;

  protected void init_tex(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    TriW[0] = ((float) 1.0) / v1.Vertex[3];
    TriW[1] = ((float) 1.0) / v2.Vertex[3];
    TriW[2] = ((float) 1.0) / v3.Vertex[3];
    TriST[0][0] = v1.TexCoord[0] * TriW[0];
    TriST[0][1] = v1.TexCoord[1] * TriW[0];
    TriST[0][2] = v1.TexCoord[2] * TriW[0];
    TriST[1][0] = v2.TexCoord[0] * TriW[1];
    TriST[1][1] = v2.TexCoord[1] * TriW[1];
    TriST[1][2] = v2.TexCoord[2] * TriW[1];
    TriST[2][0] = v3.TexCoord[0] * TriW[2];
    TriST[2][1] = v3.TexCoord[1] * TriW[2];
    TriST[2][2] = v3.TexCoord[2] * TriW[2];
  }

  protected void init(gl_vertex v1, gl_vertex v2, gl_vertex v3) {
    super.init(v1, v2, v3);
    init_tex(v1, v2, v3);
  }

  protected void set_left_tex(int pos) {
    LeftPointW = TriW[pos];
    LeftPointS = TriST[pos][0];
    LeftPointT = TriST[pos][1];
    LeftPointR = TriST[pos][2];
  }

  protected void set_left(int pos) {
    super.set_left(pos);
    set_left_tex(pos);
  }

  protected void init_tex_dx_dy(int area, int left, int right, int top) {
    float dl = TriW[left] - TriW[top];
    float dr = TriW[right] - TriW[top];
    dwdx = (dr * dyl - dl * dyr) / area;
    dwdy = (dl * dxr - dr * dxl) / area;
    dl = TriST[left][0] - TriST[top][0];
    dr = TriST[right][0] - TriST[top][0];
    dsdx = (dr * dyl - dl * dyr) / area;
    dsdy = (dl * dxr - dr * dxl) / area;
    dl = TriST[left][1] - TriST[top][1];
    dr = TriST[right][1] - TriST[top][1];
    dtdx = (dr * dyl - dl * dyr) / area;
    dtdy = (dl * dxr - dr * dxl) / area;
    dl = TriST[left][2] - TriST[top][2];
    dr = TriST[right][2] - TriST[top][2];
    drdx = (dr * dyl - dl * dyr) / area;
    drdy = (dl * dxr - dr * dxl) / area;
  }

  protected void init_dx_dy(int area, int left, int right, int top) {
    init_tex_dx_dy(area, left, right, top);
  }

  protected void init_tex_other(boolean delta, int dy) {
    dwdxy = dwdx * (float) dy + dwdy;
    if (delta) {
      dwdyy = dwdxy - dwdx;
    } else {
      dwdyy = dwdxy + dwdx;
    }
    dsdxy = dsdx * (float) dy + dsdy;
    if (delta) {
      dsdyy = dsdxy - dsdx;
    } else {
      dsdyy = dsdxy + dsdx;
    }
    dtdxy = dtdx * (float) dy + dtdy;
    if (delta) {
      dtdyy = dtdxy - dtdx;
    } else {
      dtdyy = dtdxy + dtdx;
    }
    drdxy = drdx * (float) dy + drdy;
    if (delta) {
      drdyy = drdxy - drdx;
    } else {
      drdyy = drdxy + drdx;
    }
  }

  protected void init_other(boolean delta, int dy) {
    init_tex_other(delta, dy);
  }

  protected void tex_inc_y_once() {
    LeftPointW += dwdxy;
    LeftPointS += dsdxy;
    LeftPointT += dtdxy;
    LeftPointR += drdxy;
  }

  protected void inc_y_once() {
    tex_inc_y_once();
  }

  protected void tex_inc_y_more() {
    LeftPointW += dwdyy;
    LeftPointS += dsdyy;
    LeftPointT += dtdyy;
    LeftPointR += drdyy;
  }

  protected void inc_y_more() {
    tex_inc_y_more();
  }

  protected void draw_horizontal_line(int y) {
    draw_horizontal_line(LeftPoint, LeftPointRGB, RightPoint, RightPointRGB, y, LeftPointW,
        LeftPointS, LeftPointT, LeftPointR);
  }

  public gl_lit_tex(gl_context cc) {
    super(cc);
  }

}
