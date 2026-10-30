/*
 * @(#)gl_tex_geo.java 0.4 02/12/17
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999-2002 Robin Bing-Yu Chen
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

package jgl.context.geometry;

import jgl.context.gl_context;
import jgl.context.gl_pointer;
import jgl.context.gl_polygon;
import jgl.context.gl_vertex;

/**
 * gl_tex_geo is the geometry class for texturing of jGL 2.4.
 *
 * @version 0.4, 17 Dec. 2002
 * @author Robin Bing-Yu Chen
 */

public class gl_tex_geo extends gl_geometry {

  /** Data Members */
  protected float TexCoord[][];

  protected void draw_point(float p[], int i) {
    CR.pixel.put_pixel((int) (p[0] + (float) 0.5), (int) (p[1] + (float) 0.5), TexCoord[i][0],
        TexCoord[i][1], TexCoord[i][2]);
  }

  protected gl_vertex[] pack_line(int i, int j) {
    gl_vertex temp[] = super.pack_line(i, j);
    temp[0].TexCoord = TexCoord[i];
    temp[1].TexCoord = TexCoord[j];
    return temp;
  }

  private void draw_line(gl_vertex v[]) {
    CR.render.draw_line(v[0], v[1]);
  }

  protected gl_polygon pack_polygon(int size) {
    gl_polygon tpoly = super.pack_polygon(size);
    for (int i = 0; i < size; i++) {
      tpoly.Polygon[i].TexCoord = TexCoord[i];
    }
    return tpoly;
  }

  private void draw_polygon(gl_polygon p) {
    CR.render.draw_polygon(p);
  }

  protected void set_vertex(int i) {
    super.set_vertex(i);
    if (CC.Texture.is_tex_gen_enabled() != 0) {
      if (!CC.Lighting.Enable) {
        CC.EyeNormal = CC.NormalTransformation(CC.Current.Normal);
      }
      TexCoord[i] = CC.TextureTransformation(
          CC.Texture.get_tex_gen_coord(CC.Current.Vertex, CC.EyeCoord, CC.EyeNormal));
    } else {
      TexCoord[i] = CC.TextureTransformation(CC.Current.TexCoord);
    }
  }

  protected void copy_vertex(int s, int d) {
    super.copy_vertex(s, d);
    TexCoord[d] = TexCoord[s];
  }

  protected void extend_array() {
    super.extend_array();
    float tempTex[][] = new float[VertexSize][3];
    System.arraycopy(TexCoord, 0, tempTex, 0, VertexSize - 5);
    TexCoord = tempTex;
  }

  public void gl_begin() {
    super.gl_begin();
    TexCoord = new float[VertexSize][3];
  }

  public gl_tex_geo(gl_context cc, gl_pointer cr) {
    super(cc, cr);
  }

}
