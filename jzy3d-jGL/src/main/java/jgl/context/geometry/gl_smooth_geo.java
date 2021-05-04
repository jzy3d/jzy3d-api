/*
 * @(#)gl_smooth_geo.java 0.6 06/11/24
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

package jgl.context.geometry;

import jgl.context.gl_context;
import jgl.context.gl_pointer;
import jgl.context.gl_polygon;
import jgl.context.gl_util;
import jgl.context.gl_vertex;

/**
 * gl_smooth_geo is the geometry class for smooth shading of jGL 2.4.
 *
 * @version 0.6, 24 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_smooth_geo extends gl_geometry {

  protected int VertexColor[];

  public gl_smooth_geo(gl_context cc, gl_pointer cr) {
    super(cc, cr);
  }

  protected void draw_point(float p[], int i) {
    CR.pixel.put_pixel((int) (p[0] + (float) 0.5), (int) (p[1] + (float) 0.5), VertexColor[i]);
  }

  protected gl_vertex[] pack_line(int i, int j) {
    gl_vertex temp[] = super.pack_line(i, j);
    // temp [0].Color = new int [4];
    // temp [1].Color = new int [4];
    // temp [0].Color [0] = (VertexColor [i] & 0x00ff0000) >> 16;
    // temp [0].Color [1] = (VertexColor [i] & 0x0000ff00) >> 8;
    // temp [0].Color [2] = VertexColor [i] & 0x000000ff;
    // temp [1].Color [0] = (VertexColor [j] & 0x00ff0000) >> 16;
    // temp [1].Color [1] = (VertexColor [j] & 0x0000ff00) >> 8;
    // temp [1].Color [2] = VertexColor [j] & 0x000000ff;
    temp[0].Color = gl_util.ItoRGBA(VertexColor[i]);
    temp[1].Color = gl_util.ItoRGBA(VertexColor[j]);
    return temp;
  }

  private void draw_line(gl_vertex v[]) {
    CR.render.draw_line(v[0], v[1]);
  }

  protected gl_polygon pack_polygon(int size) {
    gl_polygon tpoly = super.pack_polygon(size);
    for (int i = 0; i < size; i++) {
      // tpoly.Polygon [i].Color = new int [3];
      // tpoly.Polygon [i].Color [0] = (VertexColor [i] & 0x00ff0000) >> 16;
      // tpoly.Polygon [i].Color [1] = (VertexColor [i] & 0x0000ff00) >> 8;
      // tpoly.Polygon [i].Color [2] = VertexColor [i] & 0x000000ff;
      tpoly.Polygon[i].Color = gl_util.ItoRGBA(VertexColor[i]);
    }
    return tpoly;
  }

  private void draw_polygon(gl_polygon p) {
    CR.render.draw_polygon(p);
  }

  protected void set_vertex(int i) {
    super.set_vertex(i);
    VertexColor[i] = CC.ColorTransformation();
  }

  protected void copy_vertex(int s, int d) {
    super.copy_vertex(s, d);
    VertexColor[d] = VertexColor[s];
  }

  protected void extend_array() {
    super.extend_array();
    int tempColor[] = new int[VertexSize];
    System.arraycopy(VertexColor, 0, tempColor, 0, VertexSize - 5);
    VertexColor = tempColor;
  }

  public void gl_begin() {
    super.gl_begin();
    VertexColor = new int[VertexSize];
  }
}
