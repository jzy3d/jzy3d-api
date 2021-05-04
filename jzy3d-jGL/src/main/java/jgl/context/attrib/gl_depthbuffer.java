/*
 * @(#)gl_depthbuffer.java 0.3 01/03/15
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
import jgl.context.gl_context;
// import jgl.context.gl_list_item;
// import jgl.context.gl_util;

/**
 * gl_depthbuffer is the depth buffer class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_depthbuffer {

  private gl_context CC;

  /** GL_DEPTH_TEST: Depth buffer enabled */
  public boolean Enable = false;

  /** GL_DEPTH_FUNC: Depth buffer test function */
  public int Func = GL.GL_LESS;

  /** GL_DEPTH_WRITEMASK: Depth buffer enabled for writing */
  public boolean Mask = true;

  /** GL_DEPTH_CLEAR_VALUE: Depth-buffer clear value */
  public float Clear = 1;

  /* the really z buffer */
  public float Buffer[];

  public boolean Test(float a, int b) {
    switch (Func) {
      case GL.GL_NEVER:
        return false;
      case GL.GL_LESS:
        return (a < Buffer[b]);
      case GL.GL_GEQUAL:
        return (a >= Buffer[b]);
      case GL.GL_LEQUAL:
        return (a <= Buffer[b]);
      case GL.GL_GREATER:
        return (a > Buffer[b]);
      case GL.GL_NOTEQUAL:
        return (a != Buffer[b]);
      case GL.GL_EQUAL:
        return (a == Buffer[b]);
      case GL.GL_ALWAYS:
        return true;
    }
    return true;
  }

  public void set_buffer(int size) {
    Buffer = new float[size];
  }

  public void clear_buffer(int size) {
    for (int i = 0; i < Math.min(size, Buffer.length); i++) {
      Buffer[i] = Clear;
    }
  }

  private void set_pixel(int x, int y, byte pixels[][], float value) {
    pixels[x][y] = (byte) (value * (float) Byte.MAX_VALUE);
  }

  private void set_pixel(int x, int y, short pixels[][], float value) {
    pixels[x][y] = (short) (value * (float) Short.MAX_VALUE);
  }

  private void set_pixel(int x, int y, int pixels[][], float value) {
    pixels[x][y] = (int) (value * (float) Integer.MAX_VALUE);
  }

  private void set_pixel(int x, int y, float pixels[][], float value) {
    pixels[x][y] = value;
  }

  private float get_pixel(int x, int y, byte pixels[][]) {
    return ((float) pixels[x][y] / (float) Byte.MAX_VALUE);
  }

  private float get_pixel(int x, int y, short pixels[][]) {
    return ((float) pixels[x][y] / (float) Short.MAX_VALUE);
  }

  private float get_pixel(int x, int y, int pixels[][]) {
    return ((float) pixels[x][y] / (float) Integer.MAX_VALUE);
  }

  private float get_pixel(int x, int y, float pixels[][]) {
    return pixels[x][y];
  }

  public void read_pixels(int x, int y, int width, int height, int size, Object pixels) {
    /*
     * boolean need_scale = (CC.Pixel.Depth.Bias != 0 || CC.Pixel.Depth.Scale != 1);
     */
    int i, j, si, sj, Pos = x + CC.Viewport.Width * y;

    float Val;

    si = CC.Pixel.Pack.SkipRows;
    for (i = 0; i < height; i++) {
      sj = CC.Pixel.Pack.SkipPixels;
      Pos += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        /*
         * if (need_scale) { Val = CC.Pixel.Depth.apply_bias_scale (Buffer[Pos++]); } else {
         */
        Val = Buffer[Pos++];
        // }
        if (size == 8) {
          set_pixel(si, sj, (byte[][]) pixels, Val);
        }
        if (size == 16) {
          set_pixel(si, sj, (short[][]) pixels, Val);
        }
        if (size == 32) {
          set_pixel(si, sj, (int[][]) pixels, Val);
        }
        if (size == 64) {
          set_pixel(si, sj, (float[][]) pixels, Val);
        }
        sj++;
      }
      si++;
    }
  }

  public void draw_pixels(int width, int height, int size, Object pixels) {
    /*
     * boolean need_scale = (CC.Pixel.Depth.Bias != 0 || CC.Pixel.Depth.Scale != 1);
     */
    int i, j, si, sj, Pos = 0;

    float Val = 0;

    si = CC.Pixel.Pack.SkipRows;
    for (i = 0; i < height; i++) {
      sj = CC.Pixel.Pack.SkipPixels;
      Pos += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        if (size == 8) {
          Val = get_pixel(si, sj, (byte[][]) pixels);
        }
        if (size == 16) {
          Val = get_pixel(si, sj, (short[][]) pixels);
        }
        if (size == 32) {
          Val = get_pixel(si, sj, (int[][]) pixels);
        }
        if (size == 64) {
          Val = get_pixel(si, sj, (float[][]) pixels);
        }
        /*
         * if (need_scale) { Buffer[Pos++] = CC.Pixel.Depth.apply_bias_scale (Val); } else {
         */
        Buffer[Pos++] = Val;
        // }
        sj++;
      }
      si++;
    }
  }

  public void copy_pixels(int x, int y, int width, int height) {
    int i, j, PosR = x + CC.Viewport.Width * y, PosW = 0;

    for (i = 0; i < height; i++) {
      PosR += CC.Viewport.Width - width;
      PosW += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        Buffer[PosW++] = Buffer[PosR++];
      }
    }
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [2];
   * AttribItem.IntPtr = new int [1]; AttribItem.FloatPtr = new float [1];
   * 
   * AttribItem.BoolPtr [0] = Enable; AttribItem.IntPtr [0] = Func; AttribItem.BoolPtr [1] = Mask;
   * AttribItem.FloatPtr [0] = Clear; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { Enable = AttribItem.BoolPtr [0]; Func =
   * AttribItem.IntPtr [0]; Mask = AttribItem.BoolPtr [1]; Clear = AttribItem.FloatPtr [0]; }
   */

  public gl_depthbuffer(gl_depthbuffer cc) {
    this.CC = cc.CC;

    this.Enable = cc.Enable;
    this.Func = cc.Func;
    this.Mask = cc.Mask;
    this.Clear = cc.Clear;
  }

  public gl_depthbuffer(gl_context cc) {
    CC = cc;
  }

}
