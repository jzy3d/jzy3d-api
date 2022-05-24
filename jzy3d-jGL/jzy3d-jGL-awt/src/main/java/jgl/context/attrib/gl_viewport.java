/*
 * @(#)gl_viewport.java 0.2 01/03/15
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

// import jgl.context.gl_list_item;

/**
 * gl_viewport is the viewport class of jGL 2.4.
 *
 * @version 0.2, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_viewport {

  /** GL_VIEWPORT: Viewport origin and extent */
  /* position */
  public int X;
  public int Y;

  /* size */
  public int Width;
  public int Height;

  /* the real buffer size */
  public int Size;
  /** GL_DEPTH_RANGE: Depth range near and far */
  public float Near = 0;
  public float Far = 1;

  /* NDC to WinCoord scaling */
  public float Sx;
  public float Sy;
  public float Sz = 0.5f;

  /* NDC to WinCoord translation */
  public float Tx;
  public float Ty;
  public float Tz = 0.5f;

  public void set_viewport(int x, int y, int width, int height) {
    X = x;
    Y = y;
    Width = width;
    Height = height;
    Sx = (float) (width - 1) / 2;
    Sy = (float) (height - 1) / 2;
    Tx = Sx + x;
    Ty = Sy + y;
    Size = width * height;
  }

  public void set_depth_range(float n, float f) {
    Near = n;
    Far = f;
    Sz = (f - n) / (float) 2.0;
    Tz = Sz + n;
  }

  /** Viewport Transformation from normalized dievice coord. to win. coord. */
  public float[] Transformation(float NDC[]) {
    float WinCoord[] = new float[4];

    if (Sx == 0 || NDC[0] == 0) {
      WinCoord[0] = Tx;
    } else {
      WinCoord[0] = Sx * NDC[0] + Tx;
    }
    if (Sy == 0 || NDC[1] == 0) {
      WinCoord[1] = Ty;
    } else {
      WinCoord[1] = -Sy * NDC[1] + Ty;
    }
    if (Sz == 0 || NDC[2] == 0) {
      WinCoord[2] = Tz;
    } else {
      WinCoord[2] = Sz * NDC[2] + Tz;
    }
    WinCoord[3] = NDC[3];

    return WinCoord;
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.IntPtr = new int [4];
   * AttribItem.FloatPtr = new float [8];
   * 
   * AttribItem.IntPtr [0] = X; AttribItem.IntPtr [1] = Y; AttribItem.IntPtr [2] = Width;
   * AttribItem.IntPtr [3] = Height; AttribItem.FloatPtr [0] = Near; AttribItem.FloatPtr [1] = Far;
   * 
   * AttribItem.FloatPtr [2] = Sx; AttribItem.FloatPtr [3] = Sy; AttribItem.FloatPtr [4] = Sz;
   * AttribItem.FloatPtr [5] = Tx; AttribItem.FloatPtr [6] = Ty; AttribItem.FloatPtr [7] = Tz; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { X = AttribItem.IntPtr [0]; Y =
   * AttribItem.IntPtr [1]; Width = AttribItem.IntPtr [2]; Height = AttribItem.IntPtr [3]; Near =
   * AttribItem.FloatPtr [0]; Far = AttribItem.FloatPtr [1];
   * 
   * Sx = AttribItem.FloatPtr [2]; Sy = AttribItem.FloatPtr [3]; Sz = AttribItem.FloatPtr [4]; Tx =
   * AttribItem.FloatPtr [5]; Ty = AttribItem.FloatPtr [6]; Tz = AttribItem.FloatPtr [7]; }
   */

  public gl_viewport(gl_viewport cc) {
    this.X = cc.X;
    this.Y = cc.Y;
    this.Width = cc.Width;
    this.Height = cc.Height;
    this.Size = cc.Size;
    this.Near = cc.Near;
    this.Far = cc.Far;
    this.Sx = cc.Sx;
    this.Sy = cc.Sy;
    this.Sz = cc.Sz;
    this.Tx = cc.Tx;
    this.Ty = cc.Ty;
    this.Tz = cc.Tz;
  }

  public gl_viewport() {};

}
