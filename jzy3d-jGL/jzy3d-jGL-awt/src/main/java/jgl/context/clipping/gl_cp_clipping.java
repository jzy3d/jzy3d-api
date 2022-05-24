/*
 * @(#)gl_cg_clipping.java 0.4 99/12/03
 *
 * jGL 3-D graphics library for Java Copyright (c) 1997-1999 Robin Bing-Yu Chen
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

package jgl.context.clipping;

import jgl.context.gl_context;
import jgl.context.gl_polygon;
import jgl.context.gl_vertex;

/**
 * gl_cp_clipping is the clipping class for clipping plane of JavaGL 2.1.
 *
 * @version 0.4, 3 Dev 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_cp_clipping extends gl_clipping {

  // protected gl_nf_clipping nf_clipping;

  private float mulMatrix11(float a[], float b[]) {
    return (a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3]);
  }

  protected gl_vertex inter_point(gl_vertex v1, gl_vertex v2, float pl[]) {
    // point v1 is out, point v2 is in....
    float dvertex[] = new float[4];
    float dd;
    gl_vertex temp = new gl_vertex();

    dvertex[0] = v1.Vertex[0] - v2.Vertex[0];
    dvertex[1] = v1.Vertex[1] - v2.Vertex[1];
    dvertex[2] = v1.Vertex[2] - v2.Vertex[2];
    dvertex[3] = v1.Vertex[3] - v2.Vertex[3];
    dd = mulMatrix11(dvertex, pl);
    if (dd == 0) {
      t = 0;
    } else {
      t = -(mulMatrix11(v2.Vertex, pl)) / dd;
      if (t > 1) {
        t = 1;
      }
    }
    temp.Vertex[0] = v2.Vertex[0] + t * dvertex[0];
    temp.Vertex[1] = v2.Vertex[1] + t * dvertex[1];
    temp.Vertex[2] = v2.Vertex[2] + t * dvertex[2];
    temp.Vertex[3] = v2.Vertex[3] + t * dvertex[3];
    return temp;
  }

  protected gl_vertex inter_point(gl_vertex v1, gl_vertex v2, int xy, int j) {
    return inter_point(v1, v2, CC.Transform.ClipPlane[xy]);
  }

  protected boolean IsInside(float p[], int i, int j) {
    if (mulMatrix11(CC.Transform.ClipPlane[i], p) < 0) {
      return false;
    }
    return true;
  }

  public boolean IsInside(float p[]) {
    for (int i = 0; i < gl_context.MAX_CLIP_PLANES; i++) {
      if (CC.Transform.ClipEnable[i]) {
        if (!IsInside(p, i, 0)) {
          return false;
        }
      }
    }
    // return nf_clipping.IsInside (p);
    return true;
  }

  public gl_vertex[] clip_line(gl_vertex temp[]) {
    for (int i = 0; i < gl_context.MAX_CLIP_PLANES; i++) {
      if (CC.Transform.ClipEnable[i]) {
        temp = clip_line(temp, i, 0);
        if (temp == null) {
          return null;
        }
      }
    }
    // return nf_clipping.clip_line (temp);
    return temp;
  }

  public gl_polygon clip_polygon(gl_polygon inlist) {
    for (int i = 0; i < gl_context.MAX_CLIP_PLANES; i++) {
      if (CC.Transform.ClipEnable[i]) {
        inlist = clip_polygon(inlist, i, 0);
      }
    }
    // return nf_clipping.clip_polygon (inlist);
    return inlist;
  }

  public gl_cp_clipping(gl_context cc) {
    super(cc);
    // nf_clipping = new gl_nf_clipping (CC);
  }

}
