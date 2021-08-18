/*
 * @(#)gl_raster.java 0.3 01/03/15
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001 Robin Bing-Yu Chen
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
import jgl.context.gl_list_item;
import jgl.context.gl_util;

/**
 * gl_raster is the rasterization class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_raster {

  /** GL_POINT_SIZE: Point size */
  public float PointSize = 1;

  /** GL_POINT_SMOOTH: Point antialiasing on */
  public boolean PointSmooth = false;

  /** GL_LINE_WIDTH: Line width */
  public float LineWidth = 1;

  /** GL_LINE_SMOOTH: Line antialiasing on */
  public boolean LineSmooth = false;

  /** GL_LINE_STIPPLE_PATTERN: Line stipple */
  public short LineStipplePattern = (short) 0xffff;

  /** GL_LINE_STIPPLE_REPEAT: Line stipple repeat */
  public int LineStippleRepeat = 1;

  /** GL_LINE_STIPPLE: Line stipple enable */
  public boolean LineStipple = false;

  /** GL_CULL_FACE: Polygon culling enabled */
  public boolean CullFace = false;

  /** GL_CULL_FACE_MODE: Cull front-/back-facing polygons */
  public int CullFaceMode = GL.GL_BACK;

  /** GL_FRONT_FACE: Polygon front-face CW/CCW indicator */
  public int FrontFace = GL.GL_CCW;

  /** GL_POLYGON_SMOOTH: Polygon antialiasing on */
  public boolean PolygonSmooth = false;

  /** GL_POLYGON_MODE: Polygon rasterization mode (front and back) */
  public int FrontMode = GL.GL_FILL;
  public int BackMode = GL.GL_FILL;
  // public int PolygonMode = GL.GL_FILL;

  /** GL_POLYGON_STIPPLE: Polygon stipple enable */
  public boolean PolygonStipple = false;

  /** Polygon stipple pattern */
  public byte PolygonStipplePattern[][]; /* initial value is 32x32 1s */

  public void set_polygon_mode(int face, int mode) {
    if ((face == GL.GL_FRONT) || (face == GL.GL_FRONT_AND_BACK)) {
      FrontMode = mode;
    }
    if ((face == GL.GL_BACK) || (face == GL.GL_FRONT_AND_BACK)) {
      BackMode = mode;
    }
    // PolygonMode = mode;
  }

  public void set_polygon_stipple(byte mask[]) {
    int i, j, k = 0;
    PolygonStipplePattern = new byte[32][4];
    for (i = 0; i < 32; i++) {
      for (j = 0; j < 4; j++) {
        PolygonStipplePattern[i][j] = mask[k++];
      }
    }
  }

  public void push_point_attrib(gl_list_item AttribItem) {
    AttribItem.BoolPtr = new boolean[1];
    AttribItem.FloatPtr = new float[1];

    AttribItem.FloatPtr[0] = PointSize;
    AttribItem.BoolPtr[0] = PointSmooth;
  }

  public void pop_point_attrib(gl_list_item AttribItem) {
    PointSize = AttribItem.FloatPtr[0];
    PointSmooth = AttribItem.BoolPtr[0];
  }

  public void push_line_attrib(gl_list_item AttribItem) {
    AttribItem.BoolPtr = new boolean[2];
    AttribItem.IntPtr = new int[2];
    AttribItem.FloatPtr = new float[1];

    AttribItem.FloatPtr[0] = LineWidth;
    AttribItem.BoolPtr[0] = LineSmooth;
    AttribItem.IntPtr[0] = gl_util.StoI(LineStipplePattern);
    AttribItem.IntPtr[1] = LineStippleRepeat;
    AttribItem.BoolPtr[1] = LineStipple;
  }

  public void pop_line_attrib(gl_list_item AttribItem) {
    LineWidth = AttribItem.FloatPtr[0];
    LineSmooth = AttribItem.BoolPtr[0];
    LineStipplePattern = (short) AttribItem.IntPtr[0];
    LineStippleRepeat = AttribItem.IntPtr[1];
    LineStipple = AttribItem.BoolPtr[1];
  }

  public void push_polygon_attrib(gl_list_item AttribItem) {
    AttribItem.BoolPtr = new boolean[3];
    AttribItem.IntPtr = new int[4];

    AttribItem.IntPtr[0] = FrontFace;
    AttribItem.IntPtr[1] = FrontMode;
    AttribItem.IntPtr[2] = BackMode;
    AttribItem.BoolPtr[0] = CullFace;
    AttribItem.IntPtr[3] = CullFaceMode;
    AttribItem.BoolPtr[1] = PolygonSmooth;
    AttribItem.BoolPtr[2] = PolygonStipple;
  }

  public void pop_polygon_attrib(gl_list_item AttribItem) {
    FrontFace = AttribItem.IntPtr[0];
    FrontMode = AttribItem.IntPtr[1];
    BackMode = AttribItem.IntPtr[2];
    CullFace = AttribItem.BoolPtr[0];
    CullFaceMode = AttribItem.IntPtr[3];
    PolygonSmooth = AttribItem.BoolPtr[1];
    PolygonStipple = AttribItem.BoolPtr[2];
  }

  public void push_polygon_stipple_attrib(gl_list_item AttribItem) {
    AttribItem.IntPtr = new int[128];

    int i, j, k = 0;
    for (i = 0; i < 32; i++) {
      for (j = 0; j < 4; j++) {
        AttribItem.IntPtr[k++] = gl_util.BtoI(PolygonStipplePattern[i][j]);
      }
    }
  }

  public void pop_polygon_stipple_attrib(gl_list_item AttribItem) {
    int i, j, k = 0;
    for (i = 0; i < 32; i++) {
      for (j = 0; j < 4; j++) {
        PolygonStipplePattern[i][j] = (byte) AttribItem.IntPtr[k++];
      }
    }
  }

}
