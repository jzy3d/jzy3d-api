/*
 * @(#)gl_eval.java 0.4 01/03/15
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999-2001 Robin Bing-Yu Chen
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

package jgl.context.attrib;

import jgl.GL;
import jgl.context.gl_util;
// import jgl.context.gl_list_item;
import jgl.context.attrib.eval.gl_eval_map1;
import jgl.context.attrib.eval.gl_eval_map2;

/**
 * gl_eval is the evaluators state class of jGL 2.4.
 *
 * @version 0.4, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_eval {

  /** GL_MAP1_x: 1D map enables: x is map type */
  public boolean Map1Vertex3Enable;
  public boolean Map1Vertex4Enable;
  public boolean Map1IndexEnable;
  public boolean Map1Color4Enable;
  public boolean Map1NormalEnable;
  public boolean Map1TexCoord1Enable;
  public boolean Map1TexCoord2Enable;
  public boolean Map1TexCoord3Enable;
  public boolean Map1TexCoord4Enable;

  /** GL_MAP2_x: 2D map enables: x is map type */
  public boolean Map2Vertex3Enable;
  public boolean Map2Vertex4Enable;
  public boolean Map2IndexEnable;
  public boolean Map2Color4Enable;
  public boolean Map2NormalEnable;
  public boolean Map2TexCoord1Enable;
  public boolean Map2TexCoord2Enable;
  public boolean Map2TexCoord3Enable;
  public boolean Map2TexCoord4Enable;

  /** GL_MAP1_GRID_DOMAIN: 1D grid endpoints */
  public float MapGrid1u1 = 0;
  public float MapGrid1u2 = 1;

  /** GL_MAP1_GRID_SEGMENTS: 1D grid divisions */
  public int MapGrid1un = 1;

  /** GL_MAP2_GRID_DOMAIN: 2D grid endpoints */
  public float MapGrid2u1 = 0;
  public float MapGrid2u2 = 1;
  public float MapGrid2v1 = 0;
  public float MapGrid2v2 = 1;

  /** GL_MAP2_GRID_SEGMENTS: 2D grid divisions */
  public int MapGrid2un = 1;
  public int MapGrid2vn = 1;

  /** GL_AUTO_NORMAL: True if automatic normal generation enabled */
  public boolean AutoNormal = false;

  public gl_eval_map1 Map1Vertex3;
  public gl_eval_map1 Map1Vertex4;
  public gl_eval_map1 Map1Index;
  public gl_eval_map1 Map1Color4;
  public gl_eval_map1 Map1Normal;
  public gl_eval_map1 Map1TexCoord1;
  public gl_eval_map1 Map1TexCoord2;
  public gl_eval_map1 Map1TexCoord3;
  public gl_eval_map1 Map1TexCoord4;

  public gl_eval_map2 Map2Vertex3;
  public gl_eval_map2 Map2Vertex4;
  public gl_eval_map2 Map2Index;
  public gl_eval_map2 Map2Color4;
  public gl_eval_map2 Map2Normal;
  public gl_eval_map2 Map2TexCoord1;
  public gl_eval_map2 Map2TexCoord2;
  public gl_eval_map2 Map2TexCoord3;
  public gl_eval_map2 Map2TexCoord4;

  public int set_map_1(int target, float u1, float u2, int stride, int order, float points[][]) {
    gl_eval_map1 map = new gl_eval_map1(u1, u2, order);
    int dim;
    switch (target) {
      case GL.GL_MAP1_VERTEX_3:
        dim = 3;
        Map1Vertex3 = map;
        break;
      case GL.GL_MAP1_VERTEX_4:
        dim = 4;
        Map1Vertex4 = map;
        break;
      case GL.GL_MAP1_INDEX:
        dim = 1;
        Map1Index = map;
        break;
      case GL.GL_MAP1_COLOR_4:
        dim = 4;
        Map1Color4 = map;
        break;
      case GL.GL_MAP1_NORMAL:
        dim = 3;
        Map1Normal = map;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_1:
        dim = 1;
        Map1TexCoord1 = map;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_2:
        dim = 2;
        Map1TexCoord2 = map;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_3:
        dim = 3;
        Map1TexCoord3 = map;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_4:
        dim = 4;
        Map1TexCoord4 = map;
        break;
      default:
        dim = 0;
        break;
    }
    if (dim != 0) {
      map.Points = new float[order][dim];
      for (int i = 0; i < order; i++) {
        for (int j = 0; j < dim; j++) {
          map.Points[i][j] = points[i][j];
        }
      }
    }
    return dim;
  }

  public int set_map_2(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, float points[][][]) {
    gl_eval_map2 map = new gl_eval_map2(u1, u2, uorder, v1, v2, vorder);
    int dim;
    switch (target) {
      case GL.GL_MAP2_VERTEX_3:
        dim = 3;
        Map2Vertex3 = map;
        break;
      case GL.GL_MAP2_VERTEX_4:
        dim = 4;
        Map2Vertex4 = map;
        break;
      case GL.GL_MAP2_INDEX:
        dim = 1;
        Map2Index = map;
        break;
      case GL.GL_MAP2_COLOR_4:
        dim = 4;
        Map2Color4 = map;
        break;
      case GL.GL_MAP2_NORMAL:
        dim = 3;
        Map2Normal = map;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_1:
        dim = 1;
        Map2TexCoord1 = map;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_2:
        dim = 2;
        Map2TexCoord2 = map;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_3:
        dim = 3;
        Map2TexCoord3 = map;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_4:
        dim = 4;
        Map2TexCoord4 = map;
        break;
      default:
        dim = 0;
        break;
    }
    if (dim != 0) {
      if (ustride > vstride) {
        map.Points = new float[uorder][vorder][dim];
      } else {
        map.Points = new float[vorder][uorder][dim];
      }
      for (int i = 0; i < uorder; i++) {
        for (int j = 0; j < vorder; j++) {
          for (int k = 0; k < dim; k++) {
            if (ustride > vstride) {
              map.Points[i][j][k] = points[i][j][k];
            } else {
              map.Points[j][i][k] = points[i][j][k];
            }
          }
        }
      }
    }
    return dim;
  }

  /*
   * Bezier curve as u varies from 0 to 1
   *
   * n n C(u) = E B(u)*Pi i=0 i
   *
   * Bernstein polynomial of degree n (or order n+1)
   *
   * n (n) B(u) = ( )*u^i*(1-u)^(n-i) i (i)
   */
  private float[] bezier_curv(float p[][], float u, int dim, int odr) {
    float v; // v = 1 - u
    float uu; // uu = u^n (n = 1~(odr-1))
    int bc; // (n i) = n! / (n - i)! * i!
    int i, j;
    float c[] = new float[dim];

    if (odr > 1) {
      bc = odr - 1; // degree = odr - 1
      v = (float) 1.0 - u;

      for (j = 0; j < dim; j++) { // for B0*P0+B1*P1 (will * v^n-1)
        c[j] = v * p[0][j] + bc * u * p[1][j];
      }

      for (i = 2, uu = u * u; i < odr; i++, uu *= u) { // for Pi
        bc *= odr - i;
        bc /= i;

        for (j = 0; j < dim; j++) {
          c[j] = v * c[j] + bc * uu * p[i][j];
        }
      }
    } else { // odr == 1
      for (j = 0; j < dim; j++) {
        c[j] = p[0][j];
      }
    }

    return c;
  }

  /*
   * Bezier surface patch as u and v varies from 0 to 1
   *
   * n m n m S(u,v) = E E B(u)*B(v)*Pij i=0 j=0 i j
   *
   * u and v are independent
   */
  private float[] bezier_surf(float p[][][], float u, float v, int dim, int uodr, int vodr) {
    float w; // w = 1 - u or 1 - v
    float uu; // uu = u^n (n = 1~(odr-1))
    int bc; // (n i) = n! / (n - i)! * i!
    int i, j, k;
    float c[][];

    if (vodr > uodr) {
      if (uodr > 1) {
        c = new float[vodr][dim];
        for (j = 0; j < vodr; j++) {
          c[j] = new float[dim];

          bc = uodr - 1; // degree = odr - 1
          w = (float) 1.0 - u;

          for (k = 0; k < dim; k++) {
            c[j][k] = w * p[0][j][k] + bc * u * p[1][j][k];
          }

          for (i = 2, uu = u * u; i < uodr; i++, uu *= u) {
            bc *= uodr - i;
            bc /= i;

            for (k = 0; k < dim; k++) {
              c[j][k] = w * c[j][k] + bc * uu * p[i][j][k];
            }
          }
        }
      } else { // uodr == 1
        c = p[0];
      }
      return bezier_curv(c, v, dim, vodr);
    } else {
      c = new float[uodr][dim];
      for (j = 0; j < uodr; j++) {
        c[j] = bezier_curv(p[j], v, dim, vodr);
      }
      return bezier_curv(c, u, dim, uodr);
    }
  }

  public float[] gl_eval_coord_1(gl_eval_map1 map, int dim, float u) {
    float uu = (u - map.U1) / (map.U2 - map.U1);
    return bezier_curv(map.Points, uu, dim, map.Order);
  }

  /*
   * Normal analysis of Bezier surface patch as u and v varies from 0 to 1
   *
   * M N = --- |M|
   *
   * dS dS M = -- X -- du dv
   *
   * since n (n) S(u,v) = E ( )*u^i*(1-u)^(n-i)* C(v)i i=0 (i) so dS n-1 (n-1) -- = E (
   * )*u^i*(1-u)^(n-i-1)*(-C(v)i + C(v)i+1) du i=0 ( i )
   *
   * then use Bezier curve function to solve this
   * 
   * n <= n-1 Pi <= -C(v)i + C(v)i+1
   */
  private float[] bezier_normal(float p[][][], float u, float v, int dim, int uodr, int vodr) {
    float w; // w = 1 - u
    float uu; // uu = u^n (n = 1~(odr-1))
    int bc; // (n i) = n! / (n - i)! * i!
    int i, j, k;
    float c[][];
    float du[], dv[];

    // calculate dv first
    if (uodr > 1) {
      c = new float[vodr][dim];

      for (j = 0; j < vodr; j++) {
        c[j] = new float[dim];

        bc = uodr - 1; // degree = odr - 1
        w = (float) 1.0 - u;

        for (k = 0; k < dim; k++) {
          c[j][k] = w * p[0][j][k] + bc * u * p[1][j][k];
        }

        for (i = 2, uu = u * u; i < uodr; i++, uu *= u) {
          bc *= uodr - i;
          bc /= i;

          for (k = 0; k < dim; k++) {
            c[j][k] = w * c[j][k] + bc * uu * p[i][j][k];
          }
        }
      }
    } else { // uodr == 1
      c = p[0];
    }

    for (j = 0; j < vodr - 1; j++) {
      for (k = 0; k < dim; k++) {
        c[j][k] = c[j + 1][k] - c[j][k];
      }
    }

    dv = bezier_curv(c, v, dim, vodr - 1);

    // then calculate du
    c = new float[uodr][dim];
    for (j = 0; j < uodr; j++) {
      c[j] = bezier_curv(p[j], v, dim, vodr);
    }

    for (j = 0; j < uodr - 1; j++) {
      for (k = 0; k < dim; k++) {
        c[j][k] = c[j + 1][k] - c[j][k];
      }
    }

    du = bezier_curv(c, u, dim, uodr - 1);

    if (dim == 4) {
      for (k = 0; k < 3; k++) {
        du[k] /= du[3];
        dv[k] /= dv[3];
      }
    }

    float temp[] = gl_util.cross33(du, dv);
    gl_util.normalize(temp);
    return temp;
  }

  public float[] gl_eval_coord_2(gl_eval_map2 map, int dim, float u, float v) {
    float uu = (u - map.U1) / (map.U2 - map.U1);
    float vv = (v - map.V1) / (map.V2 - map.V1);
    return bezier_surf(map.Points, uu, vv, dim, map.UOrder, map.VOrder);
  }

  public float[] gl_auto_normal(gl_eval_map2 map, int dim, float u, float v) {
    float uu = (u - map.U1) / (map.U2 - map.U1);
    float vv = (v - map.V1) / (map.V2 - map.V1);
    return bezier_normal(map.Points, uu, vv, dim, map.UOrder, map.VOrder);
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [19];
   * AttribItem.IntPtr = new int [ 3]; AttribItem.FloatPtr = new float [ 6];
   * 
   * AttribItem.BoolPtr [ 0] = Map1Vertex3Enable; AttribItem.BoolPtr [ 1] = Map1Vertex4Enable;
   * AttribItem.BoolPtr [ 2] = Map1IndexEnable; AttribItem.BoolPtr [ 3] = Map1Color4Enable;
   * AttribItem.BoolPtr [ 4] = Map1NormalEnable; AttribItem.BoolPtr [ 5] = Map1TexCoord1Enable;
   * AttribItem.BoolPtr [ 6] = Map1TexCoord2Enable; AttribItem.BoolPtr [ 7] = Map1TexCoord3Enable;
   * AttribItem.BoolPtr [ 8] = Map1TexCoord4Enable; AttribItem.BoolPtr [ 9] = Map2Vertex3Enable;
   * AttribItem.BoolPtr [10] = Map2Vertex4Enable; AttribItem.BoolPtr [11] = Map2IndexEnable;
   * AttribItem.BoolPtr [12] = Map2Color4Enable; AttribItem.BoolPtr [13] = Map2NormalEnable;
   * AttribItem.BoolPtr [14] = Map2TexCoord1Enable; AttribItem.BoolPtr [15] = Map2TexCoord2Enable;
   * AttribItem.BoolPtr [16] = Map2TexCoord3Enable; AttribItem.BoolPtr [17] = Map2TexCoord4Enable;
   * AttribItem.FloatPtr [0] = MapGrid1u1; AttribItem.FloatPtr [1] = MapGrid1u2; AttribItem.FloatPtr
   * [2] = MapGrid2u1; AttribItem.FloatPtr [3] = MapGrid2u2; AttribItem.FloatPtr [4] = MapGrid2v1;
   * AttribItem.FloatPtr [5] = MapGrid2v2; AttribItem.IntPtr [0] = MapGrid1un; AttribItem.IntPtr [1]
   * = MapGrid2un; AttribItem.IntPtr [2] = MapGrid2vn; AttribItem.BoolPtr [18] = AutoNormal; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { Map1Vertex3Enable = AttribItem.BoolPtr [ 0];
   * Map1Vertex4Enable = AttribItem.BoolPtr [ 1]; Map1IndexEnable = AttribItem.BoolPtr [ 2];
   * Map1Color4Enable = AttribItem.BoolPtr [ 3]; Map1NormalEnable = AttribItem.BoolPtr [ 4];
   * Map1TexCoord1Enable = AttribItem.BoolPtr [ 5]; Map1TexCoord2Enable = AttribItem.BoolPtr [ 6];
   * Map1TexCoord3Enable = AttribItem.BoolPtr [ 7]; Map1TexCoord4Enable = AttribItem.BoolPtr [ 8];
   * Map2Vertex3Enable = AttribItem.BoolPtr [ 9]; Map2Vertex4Enable = AttribItem.BoolPtr [10];
   * Map2IndexEnable = AttribItem.BoolPtr [11]; Map2Color4Enable = AttribItem.BoolPtr [12];
   * Map2NormalEnable = AttribItem.BoolPtr [13]; Map2TexCoord1Enable = AttribItem.BoolPtr [14];
   * Map2TexCoord2Enable = AttribItem.BoolPtr [15]; Map2TexCoord3Enable = AttribItem.BoolPtr [16];
   * Map2TexCoord4Enable = AttribItem.BoolPtr [17]; MapGrid1u1 = AttribItem.FloatPtr [0]; MapGrid1u2
   * = AttribItem.FloatPtr [1]; MapGrid2u1 = AttribItem.FloatPtr [2]; MapGrid2u2 =
   * AttribItem.FloatPtr [3]; MapGrid2v1 = AttribItem.FloatPtr [4]; MapGrid2v2 = AttribItem.FloatPtr
   * [5]; MapGrid1un = AttribItem.IntPtr [0]; MapGrid2un = AttribItem.IntPtr [1]; MapGrid2vn =
   * AttribItem.IntPtr [2]; AutoNormal = AttribItem.BoolPtr [18]; }
   */

  public gl_eval(gl_eval cc) {
    this.Map1Vertex3Enable = cc.Map1Vertex3Enable;
    this.Map1Vertex4Enable = cc.Map1Vertex4Enable;
    this.Map1IndexEnable = cc.Map1IndexEnable;
    this.Map1Color4Enable = cc.Map1Color4Enable;
    this.Map1NormalEnable = cc.Map1NormalEnable;
    this.Map1TexCoord1Enable = cc.Map1TexCoord1Enable;
    this.Map1TexCoord2Enable = cc.Map1TexCoord2Enable;
    this.Map1TexCoord3Enable = cc.Map1TexCoord3Enable;
    this.Map1TexCoord4Enable = cc.Map1TexCoord4Enable;
    this.Map2Vertex3Enable = cc.Map2Vertex3Enable;
    this.Map2Vertex4Enable = cc.Map2Vertex4Enable;
    this.Map2IndexEnable = cc.Map2IndexEnable;
    this.Map2Color4Enable = cc.Map2Color4Enable;
    this.Map2NormalEnable = cc.Map2NormalEnable;
    this.Map2TexCoord1Enable = cc.Map2TexCoord1Enable;
    this.Map2TexCoord2Enable = cc.Map2TexCoord2Enable;
    this.Map2TexCoord3Enable = cc.Map2TexCoord3Enable;
    this.Map2TexCoord4Enable = cc.Map2TexCoord4Enable;
    this.MapGrid1u1 = cc.MapGrid1u1;
    this.MapGrid1u2 = cc.MapGrid1u2;
    this.MapGrid2u1 = cc.MapGrid2u1;
    this.MapGrid2u2 = cc.MapGrid2u2;
    this.MapGrid2v1 = cc.MapGrid2v1;
    this.MapGrid2v2 = cc.MapGrid2v2;
    this.MapGrid1un = cc.MapGrid1un;
    this.MapGrid2un = cc.MapGrid2un;
    this.MapGrid2vn = cc.MapGrid2vn;
    this.AutoNormal = cc.AutoNormal;
  }

  public gl_eval() {};

}
