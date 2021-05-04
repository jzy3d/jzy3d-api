/*
 * @(#)gl_colorbuffer.java 0.3 01/03/15
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

/**
 * gl_colorbuffer is the color buffer class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_colorbuffer {

  private gl_context CC;

  /** GL_DRAW_BUFFER: Buffers selected for drawing */
  public int DrawBuffer;

  /** GL_INDEX_WRITEMASK: Color-index writemask */
  public int IndexMask = 0xffffffff;

  /** GL_COLOR_WRITEMASK: Color write enabled; R, G, B, or A */
  /*
   * public boolean RedMask = true; public boolean GreenMask = true; public boolean BlueMask = true;
   * public boolean AlphaMask = true;
   */
  public int ColorMask = 0xffffffff;

  /** GL_COLOR_CLEAR_VALUE: Color-buffer clear value (RGBA mode) */
  public float ClearColor[] = {0, 0, 0, 0};

  /** The clear color in int for Java format */
  public int IntClearColor = 0xff000000;

  /** GL_INDEX_CLEAR_VALUE: Color-buffer clear value (color-index mode) */
  public int ClearIndex = 0;

  /** GL_ALPHA_TEST: Alpha test enabled */
  public boolean AlphaEnable = false;

  /** GL_ALPHA_TEST_FUNC: Alpha test function */
  public int AlphaFunc = GL.GL_ALWAYS;

  /** GL_ALPHA_TEST_REF: Alpha test reference value */
  public int AlphaRef = 0;

  /** GL_BLEND: Blending enabled */
  public boolean BlendEnable = false;

  /** GL_BLEND_SRC: Blending source function */
  public int BlendSrc = GL.GL_ONE;

  /** GL_BLEND_DST: Blending destination function */
  public int BlendDst = GL.GL_ZERO;

  /** GL_LOGIC_OP: Logical operation enabled */
  public boolean LogicOPEnable = false;

  /** GL_LOGIC_OP_MODE: Logical operation function */
  public int LogicOPMode = GL.GL_COPY;

  /** GL_DITHER: Dithering enabled */
  public boolean DitherEnable = true;

  /** The really color buffer */
  public int Buffer[];

  public void set_clear_color(float r, float g, float b, float a) {
    ClearColor[0] = r;
    ClearColor[1] = g;
    ClearColor[2] = b;
    ClearColor[3] = a;
    IntClearColor = // 0xff000000 // ALPHA IS FORCED TO 255 !
        ((int) (a * (float) 255.0)) << 24 | ((int) (r * (float) 255.0)) << 16
            | ((int) (g * (float) 255.0)) << 8 | ((int) (b * (float) 255.0));
  }

  public void set_color_mask(boolean r, boolean g, boolean b, boolean a) {
    /*
     * RedMask = r; GreenMask = g; BlueMask = b; AlphaMask = a;
     */
    if (r)
      ColorMask |= 0x00ff0000;
    else
      ColorMask &= 0xff00ffff;
    if (g)
      ColorMask |= 0x0000ff00;
    else
      ColorMask &= 0xffff00ff;
    if (b)
      ColorMask |= 0x000000ff;
    else
      ColorMask &= 0xffffff00;
    if (a)
      ColorMask |= 0xff000000;
    else
      ColorMask &= 0x00ffffff;
    // ColorMask = r || g || b || a;
  }

  public void set_buffer(int size) {
    Buffer = new int[size];
  }

  public void clear_buffer(int size) {
    // System.err.println("gl_colorbuffer.clear_buffer " + size + " pixels");

    // gl_render_pixel.debug_color_to_console(IntClearColor);

    for (int i = 0; i < size; i++) {
      Buffer[i] = IntClearColor;
    }
  }

  private void set_pixel(int x, int y, int i, byte pixels[][][], byte value) {
    pixels[x][y][i] = value;
  }

  private void set_pixel(int x, int y, int i, short pixels[][][], byte value) {
    pixels[x][y][i] = (short) (value & 0x00ff);
  }

  private void set_pixel(int x, int y, int i, int pixels[][][], byte value) {
    pixels[x][y][i] = (int) (value & 0x000000ff);
  }

  private void set_pixel(int x, int y, int i, float pixels[][][], byte value) {
    pixels[x][y][i] = ((float) value) / 255.0f;
  }

  private void set_pixel(int x, int y, int i, int s, Object pixels, byte value) {
    if (s == 8)
      set_pixel(x, y, i, (byte[][][]) pixels, value);
    if (s == 16)
      set_pixel(x, y, i, (short[][][]) pixels, value);
    if (s == 32)
      set_pixel(x, y, i, (int[][][]) pixels, value);
    if (s == 64)
      set_pixel(x, y, i, (float[][][]) pixels, value);
  }

  private byte get_pixel(int x, int y, int i, byte pixels[][][]) {
    return pixels[x][y][i];
  }

  private byte get_pixel(int x, int y, int i, short pixels[][][]) {
    return (byte) pixels[x][y][i];
  }

  private byte get_pixel(int x, int y, int i, int pixels[][][]) {
    return (byte) pixels[x][y][i];
  }

  private byte get_pixel(int x, int y, int i, float pixels[][][]) {
    return (byte) (pixels[x][y][i] * 255.0f);
  }

  private byte get_pixel(int x, int y, int i, int s, Object pixels) {
    if (s == 8)
      return get_pixel(x, y, i, (byte[][][]) pixels);
    if (s == 16)
      return get_pixel(x, y, i, (short[][][]) pixels);
    if (s == 32)
      return get_pixel(x, y, i, (int[][][]) pixels);
    if (s == 64)
      return get_pixel(x, y, i, (float[][][]) pixels);
    return 0;
  }

  private byte cal_lum(byte r, byte g, byte b) {
    return (byte) (((int) r + (int) g + (int) b) / 3);
  }

  public void read_pixels(int x, int y, int width, int height, int format, int size,
      Object pixels) {
    /*
     * boolean need_scale = false;
     * 
     * if ((p.Red.Scale != 1) || (p.Red.Bias != 0) || (p.Green.Scale != 1) || (p.Green.Bias != 0) ||
     * (p.Blue.Scale != 1) || (p.Blue.Bias != 0) || (p.Alpha.Scale != 1) || (p.Alpha.Bias != 0)) {
     * need_scale = true; }
     */
    int i, j, si, sj, Pos = x + CC.Viewport.Width * y;

    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    si = CC.Pixel.Pack.SkipRows;
    for (i = 0; i < height; i++) {
      sj = CC.Pixel.Pack.SkipPixels;
      Pos += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        r = (byte) ((Buffer[Pos] & 0x00ff0000) >> 16);
        g = (byte) ((Buffer[Pos] & 0x0000ff00) >> 8);
        b = (byte) (Buffer[Pos] & 0x000000ff);
        a = (byte) ((Buffer[Pos] & 0xff000000) >> 24);
        /*
         * if (need_scale) { r = CC.Pixel.Red.apply_bias_scale (r); g =
         * CC.Pixel.Green.apply_bias_scale (g); b = CC.Pixel.Blue.apply_bias_scale (b); a =
         * CC.Pixel.Alpha.apply_bias_scale (a); }
         */
        switch (format) {
          case GL.GL_RGB:
            set_pixel(si, sj, 0, size, pixels, r);
            set_pixel(si, sj, 1, size, pixels, g);
            set_pixel(si, sj, 2, size, pixels, b);
            break;
          case GL.GL_RGBA:
            set_pixel(si, sj, 0, size, pixels, r);
            set_pixel(si, sj, 1, size, pixels, g);
            set_pixel(si, sj, 2, size, pixels, b);
            set_pixel(si, sj, 3, size, pixels, a);
            break;
          case GL.GL_RED:
            set_pixel(si, sj, 0, size, pixels, r);
            break;
          case GL.GL_GREEN:
            set_pixel(si, sj, 0, size, pixels, g);
            break;
          case GL.GL_BLUE:
            set_pixel(si, sj, 0, size, pixels, b);
            break;
          case GL.GL_ALPHA:
            set_pixel(si, sj, 0, size, pixels, a);
            break;
          case GL.GL_LUMINANCE:
            set_pixel(si, sj, 0, size, pixels, cal_lum(r, g, b));
            break;
          case GL.GL_LUMINANCE_ALPHA:
            set_pixel(si, sj, 0, size, pixels, cal_lum(r, g, b));
            set_pixel(si, sj, 1, size, pixels, a);
            break;
        }
        Pos++;
        sj++;
      }
      si++;
    }
  }

  public void draw_pixels(int width, int height, int format, int size, Object pixels) {
    /*
     * boolean need_scale = false;
     * 
     * if ((p.Red.Scale != 1) || (p.Red.Bias != 0) || (p.Green.Scale != 1) || (p.Green.Bias != 0) ||
     * (p.Blue.Scale != 1) || (p.Blue.Bias != 0) || (p.Alpha.Scale != 1) || (p.Alpha.Bias != 0)) {
     * need_scale = true; }
     */
    int i, j, si, sj, Pos = 0;

    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    si = CC.Pixel.Pack.SkipRows;
    for (i = 0; i < height; i++) {
      sj = CC.Pixel.Pack.SkipPixels;
      Pos += CC.Viewport.Width - width;
      for (j = 0; j < width; j++) {
        switch (format) {
          case GL.GL_RGB:
            r = get_pixel(si, sj, 0, size, pixels);
            g = get_pixel(si, sj, 1, size, pixels);
            b = get_pixel(si, sj, 2, size, pixels);
            break;
          case GL.GL_RGBA:
            r = get_pixel(si, sj, 0, size, pixels);
            g = get_pixel(si, sj, 1, size, pixels);
            b = get_pixel(si, sj, 2, size, pixels);
            a = get_pixel(si, sj, 3, size, pixels);
            break;
          case GL.GL_RED:
            r = get_pixel(si, sj, 0, size, pixels);
            break;
          case GL.GL_GREEN:
            g = get_pixel(si, sj, 0, size, pixels);
            break;
          case GL.GL_BLUE:
            b = get_pixel(si, sj, 0, size, pixels);
            break;
          case GL.GL_ALPHA:
            a = get_pixel(si, sj, 0, size, pixels);
            break;
          case GL.GL_LUMINANCE:
            r = get_pixel(si, sj, 0, size, pixels);
            g = r;
            b = r;
            break;
          case GL.GL_LUMINANCE_ALPHA:
            r = get_pixel(si, sj, 0, size, pixels);
            g = r;
            b = r;
            a = get_pixel(si, sj, 1, size, pixels);
            break;
        }
        /*
         * if (need_scale) { r = CC.Pixel.Red.apply_bias_scale (r); g =
         * CC.Pixel.Green.apply_bias_scale (g); b = CC.Pixel.Blue.apply_bias_scale (b); a =
         * CC.Pixel.Alpha.apply_bias_scale (a); }
         */

        int color = ((a << 24) | (r << 16) | (g << 8) | b);
        Buffer[Pos++] = color;

        // gl_render_pixel.debug_color_to_console(color);

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
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [4];
   * AttribItem.IntPtr = new int [9]; AttribItem.FloatPtr = new float [4];
   * 
   * AttribItem.IntPtr [0] = DrawBuffer; AttribItem.IntPtr [1] = ClearIndex;
   * System.arraycopy(ClearColor, 0, AttribItem.FloatPtr, 0, 4); AttribItem.IntPtr [2] =
   * IntClearColor; AttribItem.IntPtr [3] = IndexMask; AttribItem.IntPtr [4] = ColorMask;
   * AttribItem.BoolPtr [0] = AlphaEnable; AttribItem.IntPtr [5] = AlphaFunc; AttribItem.IntPtr [6]
   * = AlphaRef; AttribItem.BoolPtr [1] = BlendEnable; AttribItem.IntPtr [7] = BlendSrc;
   * AttribItem.IntPtr [8] = BlendDst; AttribItem.BoolPtr [2] = LogicOPEnable; AttribItem.IntPtr [9]
   * = LogicOPMode; AttribItem.BoolPtr [3] = DitherEnable; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { DrawBuffer = AttribItem.IntPtr [0];
   * ClearIndex = AttribItem.IntPtr [1]; System.arraycopy(AttribItem.FloatPtr, 0, ClearColor, 0, 4);
   * IntClearColor = AttribItem.IntPtr [2]; IndexMask = AttribItem.IntPtr [3]; ColorMask =
   * AttribItem.IntPtr [4]; AlphaEnable = AttribItem.BoolPtr [0]; AlphaFunc = AttribItem.IntPtr [5];
   * AlphaRef = AttribItem.IntPtr [6]; BlendEnable = AttribItem.BoolPtr [1]; BlendSrc =
   * AttribItem.IntPtr [7]; BlendDst = AttribItem.IntPtr [8]; LogicOPEnable = AttribItem.BoolPtr
   * [2]; LogicOPMode = AttribItem.IntPtr [9]; DitherEnable = AttribItem.BoolPtr [3]; }
   */

  public gl_colorbuffer(gl_colorbuffer cc) {
    this.CC = cc.CC;

    this.DrawBuffer = cc.DrawBuffer;
    this.ClearIndex = cc.ClearIndex;
    System.arraycopy(cc.ClearColor, 0, this.ClearColor, 0, 4);
    this.IndexMask = cc.IndexMask;
    this.ColorMask = cc.ColorMask;
    this.AlphaEnable = cc.AlphaEnable;
    this.AlphaFunc = cc.AlphaFunc;
    this.AlphaRef = cc.AlphaRef;
    this.BlendEnable = cc.BlendEnable;
    this.BlendSrc = cc.BlendSrc;
    this.BlendDst = cc.BlendDst;
    this.LogicOPEnable = cc.LogicOPEnable;
    this.LogicOPMode = cc.LogicOPMode;
    this.DitherEnable = cc.DitherEnable;
  }

  public gl_colorbuffer(gl_context cc) {
    CC = cc;
  }

}
