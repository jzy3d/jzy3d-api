/*
 * @(#)gle_phong_geo.java 0.1 02/12/30
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

package jgl.gle.geometry;

import jgl.context.gl_polygon;
import jgl.context.gl_vertex;
import jgl.context.geometry.gl_geometry;
import jgl.gle.gle_context;
import jgl.gle.gle_pointer;
import jgl.gle.gle_vertex;

/**
 * gle_phong_geo is the geometry class for phong shading of jGL 2.5.
 *
 * @version 0.1, 30 Dec. 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_phong_geo extends gl_geometry {

  /** Data Members */
  protected float Normal[][];
  protected float TexCoord[][];

  protected int get_color(int i) {
    return ((gle_context) CC).ColorTransformation(TexCoord[i], Normal[i]);
  }

  protected void draw_point(float p[], int i) {
    CR.pixel.put_pixel((int) (p[0] + (float) 0.5), (int) (p[1] + (float) 0.5), get_color(i));
  }

  protected gl_vertex[] pack_line(int i, int j) {
    gl_vertex temp[] = super.pack_line(i, j);
    gle_vertex gle_temp[] = new gle_vertex[2];
    gle_temp[0] = new gle_vertex(temp[0]);
    gle_temp[1] = new gle_vertex(temp[1]);
    gle_temp[0].Normal = Normal[i];
    gle_temp[1].Normal = Normal[j];
    gle_temp[0].TexCoord = TexCoord[i];
    gle_temp[1].TexCoord = TexCoord[j];
    return (gl_vertex[]) gle_temp;
  }

  private void draw_line(gl_vertex v[]) {
    CR.render.draw_line((gle_vertex) v[0], (gle_vertex) v[1]);
  }

  protected gl_polygon pack_polygon(int size) {
    gl_polygon tpoly = super.pack_polygon(size);
    gle_vertex gle_temp[] = new gle_vertex[size];
    for (int i = 0; i < size; i++) {
      gle_temp[i] = new gle_vertex(tpoly.Polygon[i]);
      gle_temp[i].Normal = Normal[i];
      gle_temp[i].TexCoord = TexCoord[i];
    }
    tpoly.Polygon = gle_temp;
    return tpoly;
  }

  private void draw_polygon(gl_polygon p) {
    CR.render.draw_polygon(p);
  }

  protected void set_vertex(int i) {
    super.set_vertex(i);
    Normal[i] = CC.NormalTransformation(CC.Current.Normal);
    TexCoord[i] = CC.EyeCoord;
  }

  protected void copy_vertex(int s, int d) {
    super.copy_vertex(s, d);
    Normal[d] = Normal[s];
    TexCoord[d] = TexCoord[s];
  }

  protected void extend_array() {
    super.extend_array();
    float tempNor[][] = new float[VertexSize][3];
    float tempTex[][] = new float[VertexSize][3];
    System.arraycopy(Normal, 0, tempNor, 0, VertexSize - 5);
    System.arraycopy(TexCoord, 0, tempTex, 0, VertexSize - 5);
    Normal = tempNor;
    TexCoord = tempTex;
  }

  public void gl_begin() {
    super.gl_begin();
    Normal = new float[VertexSize][3];
    TexCoord = new float[VertexSize][3];
  }

  public gle_phong_geo(gle_context cc, gle_pointer cr) {
    super(cc, cr);
  }

}
