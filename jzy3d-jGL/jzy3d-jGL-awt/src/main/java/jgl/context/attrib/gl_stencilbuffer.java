/*
 * @(#)gl_stencilbuffer.java 0.1 01/06/19
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
import jgl.context.gl_context;
// import jgl.context.gl_list_item;
// import jgl.context.gl_util;

/**
 * gl_stencilbuffer is the stenciling buffer class of jGL 2.4.
 *
 * @version 0.1, 19 Jun 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_stencilbuffer {

  private gl_context CC;

  /** GL_STENCIL_TEST: Stenciling buffer enabled */
  public boolean Enable = false;

  /** GL_STENCIL_FUNC: Stencil function */
  public int Func = GL.GL_ALWAYS;

  /** GL_STENCIL_VALUE_MASK: Stencil mask */
  public int ValueMask = 0xffffffff;

  /** GL_STENCIL_REF: Stencil reference value */
  public int Ref = 0;

  /** GL_STENCIL_FAIL: Stencil fail action */
  public int Fail = GL.GL_KEEP;

  /** GL_STENCIL_PASS_DEPTH_FAIL: Stencil depth buffer fail action */
  public int ZFail = GL.GL_KEEP;

  /** GL_STENCIL_PASS_DEPTH_PASS: Stencil depth buffer pass action */
  public int ZPass = GL.GL_KEEP;

  /** GL_STENCIL_WRITEMASK: Stencil-buffer writing */
  public int Mask = 0xffffffff;

  /** GL_STENCIL_CLEAR_VALUE: Stencil-buffer clear value */
  public int Clear = 0;

  /* the really stencil buffer */
  public int Buffer[];

  /*
   * public boolean Test (float a, int b) { switch (Func) { case GL.GL_NEVER: return false; case
   * GL.GL_LESS: return (a < Buffer [b]); case GL.GL_GEQUAL: return (a >= Buffer [b]); case
   * GL.GL_LEQUAL: return (a <= Buffer [b]); case GL.GL_GREATER: return (a > Buffer [b]); case
   * GL.GL_NOTEQUAL: return (a != Buffer [b]); case GL.GL_EQUAL: return (a == Buffer [b]); case
   * GL.GL_ALWAYS: return true; } return true; }
   */

  public void set_buffer(int size) {
    Buffer = new int[size];
  }

  public void clear_buffer(int size) {
    for (int i = 0; i < size; i++) {
      Buffer[i] = Clear;
    }
  }

  private void set_pixel(int x, int y, byte pixels[][], int value) {
    pixels[x][y] = (byte) value;
  }

  private void set_pixel(int x, int y, short pixels[][], int value) {
    pixels[x][y] = (short) value;
  }

  private void set_pixel(int x, int y, int pixels[][], int value) {
    pixels[x][y] = value;
  }

  private void set_pixel(int x, int y, float pixels[][], int value) {
    pixels[x][y] = (float) value;
  }

  private int get_pixel(int x, int y, byte pixels[][]) {
    return (int) (pixels[x][y] & 0x000000ff);
  }

  private int get_pixel(int x, int y, short pixels[][]) {
    return (int) (pixels[x][y] & 0x0000ffff);
  }

  private int get_pixel(int x, int y, int pixels[][]) {
    return pixels[x][y];
  }

  private int get_pixel(int x, int y, float pixels[][]) {
    return (int) pixels[x][y];
  }

  public void read_pixels(int x, int y, int width, int height, int size, Object pixels) {
    // boolean need_scale = (CC.Pixel.Depth.Bias != 0 ||
    // CC.Pixel.Depth.Scale != 1);
    int i, j, si, sj, Pos = x + CC.Viewport.Width * y;

    int Val;

    si = CC.Pixel.Pack.SkipRows;
    for (i = 0; i < height; i++) {
      sj = CC.Pixel.Pack.SkipPixels;
      Pos += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        // if (need_scale) {
        // Val = CC.Pixel.Depth.apply_bias_scale (Buffer[Pos++]);
        // } else {
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
    // boolean need_scale = (CC.Pixel.Depth.Bias != 0 ||
    // CC.Pixel.Depth.Scale != 1);
    int i, j, si, sj, Pos = 0;

    int Val = 0;

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
        // if (need_scale) {
        // Buffer[Pos++] = CC.Pixel.Depth.apply_bias_scale (Val);
        // } else {
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

  public gl_stencilbuffer(gl_stencilbuffer cc) {
    this.CC = cc.CC;

    this.Enable = cc.Enable;
    this.Func = cc.Func;
    this.ValueMask = cc.ValueMask;
    this.Ref = cc.Ref;
    this.Fail = cc.Fail;
    this.ZFail = cc.ZFail;
    this.ZPass = cc.ZPass;
    this.Mask = cc.Mask;
    this.Clear = cc.Clear;
  }

  public gl_stencilbuffer(gl_context cc) {
    CC = cc;
  }

}
