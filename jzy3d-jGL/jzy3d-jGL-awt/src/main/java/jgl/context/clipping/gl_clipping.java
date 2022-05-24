/*
 * @(#)gl_clipping.java 0.6 06/11/24
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

package jgl.context.clipping;

import jgl.context.gl_context;
import jgl.context.gl_polygon;
import jgl.context.gl_vertex;

/**
 * gl_clipping is the clipping class of jGL 2.4.
 *
 * @version 0.6, 24 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_clipping {

  protected gl_context CC;
  protected float t; /* use for interpolate aux data */

  protected gl_vertex inter_point_pos(gl_vertex v1, gl_vertex v2, int xy) {
    return null;
  }

  protected gl_vertex inter_point_neg(gl_vertex v1, gl_vertex v2, int xy) {
    return null;
  }

  protected gl_vertex inter_point(gl_vertex v1, gl_vertex v2, int i, int j) {
    if (j == 0) {
      return inter_point_neg(v1, v2, i);
    }
    return inter_point_pos(v1, v2, i); // j == 1
  }

  protected int[] inter_color(gl_vertex v1, gl_vertex v2) {
    // point v1 is out, point v2 is in....
    int tempColor[] = new int[4];

    tempColor[0] = v2.Color[0] + (int) (t * (float) (v1.Color[0] - v2.Color[0]));
    tempColor[1] = v2.Color[1] + (int) (t * (float) (v1.Color[1] - v2.Color[1]));
    tempColor[2] = v2.Color[2] + (int) (t * (float) (v1.Color[2] - v2.Color[2]));
    tempColor[3] = v2.Color[3] + (int) (t * (float) (v1.Color[3] - v2.Color[3]));
    return tempColor;
  }

  protected float[] inter_tex(gl_vertex v1, gl_vertex v2) {
    // point v1 is out, point v2 is in....
    float tempTexCoord[] = new float[3];

    tempTexCoord[0] = v2.TexCoord[0] + t * (v1.TexCoord[0] - v2.TexCoord[0]);
    tempTexCoord[1] = v2.TexCoord[1] + t * (v1.TexCoord[1] - v2.TexCoord[1]);
    tempTexCoord[2] = v2.TexCoord[2] + t * (v1.TexCoord[2] - v2.TexCoord[2]);
    return tempTexCoord;
  }

  protected boolean IsInside_pos(float p[], int xy) {
    return false;
  }

  protected boolean IsInside_neg(float p[], int xy) {
    return false;
  }

  protected boolean IsInside(float p[], int i, int j) {
    if (j == 0) {
      return IsInside_neg(p, i);
    }
    return IsInside_pos(p, i); // j == 1
  }

  public boolean IsInside(float p[]) {
    if (!(IsInside_neg(p, 0))) {
      return false;
    }
    if (!(IsInside_neg(p, 1))) {
      return false;
    }
    if (!(IsInside_neg(p, 2))) {
      return false;
    }
    if (!(IsInside_pos(p, 0))) {
      return false;
    }
    if (!(IsInside_pos(p, 1))) {
      return false;
    }
    if (!(IsInside_pos(p, 2))) {
      return false;
    }
    return true;
  }

  protected gl_vertex[] clip_line(gl_vertex temp[], int i, int j) {
    if (IsInside(temp[0].Vertex, i, j)) {
      if (!IsInside(temp[1].Vertex, i, j)) {
        temp[1] = inter_point(temp[1], temp[0], i, j);
      }
    } else {
      if (IsInside(temp[1].Vertex, i, j)) {
        temp[0] = inter_point(temp[0], temp[1], i, j);
      } else {
        return null;
      }
    }
    return temp;
  }

  protected gl_vertex[] clip_line(gl_vertex temp[], int i) {
    for (int j = 0; j < 2; j++) {
      temp = clip_line(temp, i, j);
      if (temp == null) {
        return null;
      }
    }
    return temp;
  }

  public gl_vertex[] clip_line(gl_vertex temp[]) {
    for (int i = 0; i < 3; i++) {
      temp = clip_line(temp, i);
      if (temp == null) {
        return null;
      }
    }
    return temp;
  }

  protected gl_polygon clip_polygon(gl_polygon inlist, int i, int j) {
    gl_polygon outlist = new gl_polygon();
    gl_vertex temp[];

    outlist.Polygon = new gl_vertex[inlist.n];
    outlist.n = 0;

    int size = inlist.n;
    int prev = inlist.n - 1;

    if (inlist.n == 0) {
      return inlist;
    }

    for (int curr = 0; curr < inlist.n; curr++) {
      if (IsInside(inlist.Polygon[curr].Vertex, i, j)) {
        if (IsInside(inlist.Polygon[prev].Vertex, i, j)) {
          // both in....just copy....
          if (outlist.n == size) {
            temp = new gl_vertex[size + 5];
            System.arraycopy(outlist.Polygon, 0, temp, 0, size);
            outlist.Polygon = temp;
            size += 5;
          }
          outlist.Polygon[outlist.n++] = inlist.Polygon[curr];
        } else {
          // current is in, but previous is out....
          if (outlist.n > size - 2) {
            temp = new gl_vertex[size + 5];
            System.arraycopy(outlist.Polygon, 0, temp, 0, size);
            outlist.Polygon = temp;
            size += 5;
          }
          outlist.Polygon[outlist.n++] =
              inter_point(inlist.Polygon[prev], inlist.Polygon[curr], i, j);
          outlist.Polygon[outlist.n++] = inlist.Polygon[curr];
        }
      } else {
        if (IsInside(inlist.Polygon[prev].Vertex, i, j)) {
          // current is out, but previous is in....
          if (outlist.n == size) {
            temp = new gl_vertex[size + 5];
            System.arraycopy(outlist.Polygon, 0, temp, 0, size);
            outlist.Polygon = temp;
            size += 5;
          }
          outlist.Polygon[outlist.n++] =
              inter_point(inlist.Polygon[curr], inlist.Polygon[prev], i, j);
        } // else, both out, do nothing....
      }
      prev = curr;
    }
    return outlist;
  }

  protected gl_polygon clip_polygon(gl_polygon inlist, int i) {
    for (int j = 0; j < 2; j++) {
      inlist = clip_polygon(inlist, i, j);
    }
    return inlist;
  }

  public gl_polygon clip_polygon(gl_polygon inlist) {
    for (int i = 0; i < 3; i++) {
      inlist = clip_polygon(inlist, i);
    }
    return inlist;
  }

  public gl_clipping() {}

  public gl_clipping(gl_context cc) {
    CC = cc;
  }

}
