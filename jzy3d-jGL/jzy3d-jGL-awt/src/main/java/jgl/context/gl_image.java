/*
 * @(#)gl_image.java 0.4 01/12/03
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

package jgl.context;

import jgl.GL;
import jgl.context.attrib.pixel.gl_pixel_pack;

/**
 * gl_image the Image class of jGL 2.4.
 *
 * @version 0.4, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_image {

  public int Width = 0;
  public int Height = 0;
  public int Depth = 0;
  public byte ImageData[][][][]; /* Image data */

  private byte get_data(int x, int i, byte data[][]) {
    return data[x][i];
  }

  private byte get_data(int x, int i, short data[][]) {
    return (byte) data[x][i];
  }

  private byte get_data(int x, int i, int data[][]) {
    return (byte) data[x][i];
  }

  private byte get_data(int x, int i, float data[][]) {
    return (byte) (data[x][i] * 255.0f);
  }

  private byte get_data(int x, int i, int s, Object data) {
    if (s == 8)
      return get_data(x, i, (byte[][]) data);
    if (s == 16)
      return get_data(x, i, (short[][]) data);
    if (s == 32)
      return get_data(x, i, (int[][]) data);
    if (s == 64)
      return get_data(x, i, (float[][]) data);
    return 0;
  }

  private byte get_data(int x, int y, int i, byte data[][][]) {
    return data[x][y][i];
  }

  private byte get_data(int x, int y, int i, short data[][][]) {
    return (byte) data[x][y][i];
  }

  private byte get_data(int x, int y, int i, int data[][][]) {
    return (byte) data[x][y][i];
  }

  private byte get_data(int x, int y, int i, float data[][][]) {
    return (byte) (data[x][y][i] * 255.0f);
  }

  private byte get_data(int x, int y, int i, int s, Object data) {
    if (s == 8)
      return get_data(x, y, i, (byte[][][]) data);
    if (s == 16)
      return get_data(x, y, i, (short[][][]) data);
    if (s == 32)
      return get_data(x, y, i, (int[][][]) data);
    if (s == 64)
      return get_data(x, y, i, (float[][][]) data);
    return 0;
  }

  private byte get_data(int x, int y, int z, int i, byte data[][][][]) {
    return data[x][y][z][i];
  }

  private byte get_data(int x, int y, int z, int i, short data[][][][]) {
    return (byte) data[x][y][z][i];
  }

  private byte get_data(int x, int y, int z, int i, int data[][][][]) {
    return (byte) data[x][y][z][i];
  }

  private byte get_data(int x, int y, int z, int i, float data[][][][]) {
    return (byte) (data[x][y][z][i] * 255.0f);
  }

  private byte get_data(int x, int y, int z, int i, int s, Object data) {
    if (s == 8)
      return get_data(x, y, z, i, (byte[][][][]) data);
    if (s == 16)
      return get_data(x, y, z, i, (short[][][][]) data);
    if (s == 32)
      return get_data(x, y, z, i, (int[][][][]) data);
    if (s == 64)
      return get_data(x, y, z, i, (float[][][][]) data);
    return 0;
  }

  /*
   * private boolean is_need_scale (gl_pixel_pack p) { if ((p.Red.Scale != 1) || (p.Red.Bias != 0)
   * || (p.Green.Scale != 1) || (p.Green.Bias != 0) || (p.Blue.Scale != 1) || (p.Blue.Bias != 0) ||
   * (p.Alpha.Scale != 1) || (p.Alpha.Bias != 0)) { return true; } else return false; }
   */

  private void get_image(int f, byte pixel[], byte data[]) {
    switch (f) {
      case GL.GL_COLOR_INDEX:
        break;
      case GL.GL_RGBA:
        pixel[3] = data[3];
      case GL.GL_RGB:
        pixel[0] = data[0];
        pixel[1] = data[1];
        pixel[2] = data[2];
        break;
      case GL.GL_LUMINANCE_ALPHA:
        pixel[1] = data[3];
      case GL.GL_LUMINANCE:
      case GL.GL_RED:
        pixel[0] = data[0];
        break;
      case GL.GL_GREEN:
        pixel[0] = data[1];
        break;
      case GL.GL_BLUE:
        pixel[0] = data[2];
        break;
      case GL.GL_ALPHA:
        pixel[0] = data[3];
        break;
    }
  }

  private void get_image(int f, short pixel[], byte data[]) {
    switch (f) {
      case GL.GL_COLOR_INDEX:
        break;
      case GL.GL_RGBA:
        pixel[3] = data[3];
      case GL.GL_RGB:
        pixel[0] = data[0];
        pixel[1] = data[1];
        pixel[2] = data[2];
        break;
      case GL.GL_LUMINANCE_ALPHA:
        pixel[1] = data[3];
      case GL.GL_LUMINANCE:
      case GL.GL_RED:
        pixel[0] = data[0];
        break;
      case GL.GL_GREEN:
        pixel[0] = data[1];
        break;
      case GL.GL_BLUE:
        pixel[0] = data[2];
        break;
      case GL.GL_ALPHA:
        pixel[0] = data[3];
        break;
    }
  }

  private void get_image(int f, int pixel[], byte data[]) {
    switch (f) {
      case GL.GL_COLOR_INDEX:
        break;
      case GL.GL_RGBA:
        pixel[3] = data[3];
      case GL.GL_RGB:
        pixel[0] = data[0];
        pixel[1] = data[1];
        pixel[2] = data[2];
        break;
      case GL.GL_LUMINANCE_ALPHA:
        pixel[1] = data[3];
      case GL.GL_LUMINANCE:
      case GL.GL_RED:
        pixel[0] = data[0];
        break;
      case GL.GL_GREEN:
        pixel[0] = data[1];
        break;
      case GL.GL_BLUE:
        pixel[0] = data[2];
        break;
      case GL.GL_ALPHA:
        pixel[0] = data[3];
        break;
    }
  }

  private void get_image(int f, float pixel[], byte data[]) {
    switch (f) {
      case GL.GL_COLOR_INDEX:
        break;
      case GL.GL_RGBA:
        pixel[3] = (float) data[3] / 255.0f;
      case GL.GL_RGB:
        pixel[0] = (float) data[0] / 255.0f;
        pixel[1] = (float) data[1] / 255.0f;
        pixel[2] = (float) data[2] / 255.0f;
        break;
      case GL.GL_LUMINANCE_ALPHA:
        pixel[1] = (float) data[3] / 255.0f;
      case GL.GL_LUMINANCE:
      case GL.GL_RED:
        pixel[0] = (float) data[0] / 255.0f;
        break;
      case GL.GL_GREEN:
        pixel[0] = (float) data[1] / 255.0f;
        break;
      case GL.GL_BLUE:
        pixel[0] = (float) data[2] / 255.0f;
        break;
      case GL.GL_ALPHA:
        pixel[0] = (float) data[3] / 255.0f;
        break;
    }
  }

  private void get_image(int x, Object pixels, int f, int s) {
    if (s == 8)
      get_image(f, ((byte[][]) pixels)[x], ImageData[x][0][0]);
    if (s == 16)
      get_image(f, ((short[][]) pixels)[x], ImageData[x][0][0]);
    if (s == 32)
      get_image(f, ((int[][]) pixels)[x], ImageData[x][0][0]);
    if (s == 64)
      get_image(f, ((float[][]) pixels)[x], ImageData[x][0][0]);
  }

  private void get_image(int x, int y, Object pixels, int f, int s) {
    if (s == 8)
      get_image(f, ((byte[][][]) pixels)[x][y], ImageData[x][y][0]);
    if (s == 16)
      get_image(f, ((short[][][]) pixels)[x][y], ImageData[x][y][0]);
    if (s == 32)
      get_image(f, ((int[][][]) pixels)[x][y], ImageData[x][y][0]);
    if (s == 64)
      get_image(f, ((float[][][]) pixels)[x][y], ImageData[x][y][0]);
  }

  private void get_image(int x, int y, int z, Object pixels, int f, int s) {
    if (s == 8)
      get_image(f, ((byte[][][][]) pixels)[x][y][z], ImageData[x][y][z]);
    if (s == 16)
      get_image(f, ((short[][][][]) pixels)[x][y][z], ImageData[x][y][z]);
    if (s == 32)
      get_image(f, ((int[][][][]) pixels)[x][y][z], ImageData[x][y][z]);
    if (s == 64)
      get_image(f, ((float[][][][]) pixels)[x][y][z], ImageData[x][y][z]);
  }

  public void get_image(int f, int s, Object pixels) {
    for (int i = 0; i < Width; i++) {
      if (Height != 0) {
        for (int j = 0; j < Height; j++) {
          if (Depth != 0) {
            for (int k = 0; k < Depth; k++) {
              get_image(i, j, k, pixels, s, f);
            }
          } else {
            get_image(i, j, pixels, s, f);
          }
        }
      } else {
        get_image(i, pixels, s, f);
      }
    }
  }

  public void set_sub_image(int x, int w, int f, int s, gl_pixel_pack p, Object data) {
    int i, si;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = 0;
      for (i = x; i < w; i++) {
        switch (f) {
          case GL.GL_COLOR_INDEX:
            break;
          case GL.GL_RGB:
            r = get_data(si, 0, s, data);
            g = get_data(si, 1, s, data);
            b = get_data(si, 2, s, data);
            a = (byte) 255;
            break;
          case GL.GL_RGBA:
            r = get_data(si, 0, s, data);
            g = get_data(si, 1, s, data);
            b = get_data(si, 2, s, data);
            a = get_data(si, 3, s, data);
            break;
          case GL.GL_RED:
            r = get_data(si, 0, s, data);
            g = (byte) 0;
            b = (byte) 0;
            a = (byte) 255;
            break;
          case GL.GL_GREEN:
            r = (byte) 0;
            g = get_data(si, 0, s, data);
            b = (byte) 0;
            a = (byte) 255;
            break;
          case GL.GL_BLUE:
            r = (byte) 0;
            g = (byte) 0;
            b = get_data(si, 0, s, data);
            a = (byte) 255;
            break;
          case GL.GL_ALPHA:
            r = (byte) 0;
            g = (byte) 0;
            b = (byte) 0;
            a = get_data(si, 0, s, data);
            break;
          case GL.GL_LUMINANCE:
            r = get_data(si, 0, s, data);
            g = r;
            b = r;
            a = (byte) 255;
            break;
          case GL.GL_LUMINANCE_ALPHA:
            r = get_data(si, 0, s, data);
            g = r;
            b = r;
            a = get_data(si, 1, s, data);
            break;
        }
        /*
         * switch (c) { case 1: ImageData [i][0][0][0] = r; ImageData [i][0][0][1] = (byte)0;
         * ImageData [i][0][0][2] = (byte)0; ImageData [i][0][0][3] = (byte)255; break; case 2:
         * ImageData [i][0][0][0] = r; ImageData [i][0][0][1] = (byte)0; ImageData [i][0][0][2] =
         * (byte)0; ImageData [i][0][0][3] = a; break; case 3: ImageData [i][0][0][0] = r; ImageData
         * [i][0][0][1] = g; ImageData [i][0][0][2] = b; ImageData [i][0][0][3] = (byte)255; break;
         * case 4:
         */
        ImageData[i][0][0][0] = r;
        ImageData[i][0][0][1] = g;
        ImageData[i][0][0][2] = b;
        ImageData[i][0][0][3] = a;
        /*
         * break; }
         */
        si++;
      }
    }
  }

  public void set_sub_image(int x, int y, int w, int h, int f, int s, gl_pixel_pack p,
      Object data) {
    int i, j, si, sj;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = 0;
      for (i = x; i < w; i++) {
        sj = 0;
        for (j = y; j < h; j++) {
          switch (f) {
            case GL.GL_COLOR_INDEX:
              break;
            case GL.GL_RGB:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = (byte) 255;
              break;
            case GL.GL_RGBA:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = get_data(si, sj, 3, s, data);
              break;
            case GL.GL_RED:
              r = get_data(si, sj, 0, s, data);
              g = (byte) 0;
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_GREEN:
              r = (byte) 0;
              g = get_data(si, sj, 0, s, data);
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_BLUE:
              r = (byte) 0;
              g = (byte) 0;
              b = get_data(si, sj, 0, s, data);
              a = (byte) 255;
              break;
            case GL.GL_ALPHA:
              r = (byte) 0;
              g = (byte) 0;
              b = (byte) 0;
              a = get_data(si, sj, 0, s, data);
              break;
            case GL.GL_LUMINANCE:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = (byte) 255;
              break;
            case GL.GL_LUMINANCE_ALPHA:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = get_data(si, sj, 1, s, data);
              break;
          }
          /*
           * switch (c) { case 1: ImageData [i][j][0][0] = r; ImageData [i][j][0][1] = (byte)0;
           * ImageData [i][j][0][2] = (byte)0; ImageData [i][j][0][3] = (byte)255; break; case 2:
           * ImageData [i][j][0][0] = r; ImageData [i][j][0][1] = (byte)0; ImageData [i][j][0][2] =
           * (byte)0; ImageData [i][j][0][3] = a; break; case 3: ImageData [i][j][0][0] = r;
           * ImageData [i][j][0][1] = g; ImageData [i][j][0][2] = b; ImageData [i][j][0][3] =
           * (byte)255; break; case 4:
           */
          ImageData[i][j][0][0] = r;
          ImageData[i][j][0][1] = g;
          ImageData[i][j][0][2] = b;
          ImageData[i][j][0][3] = a;
          /*
           * break; }
           */
          sj++;
        }
        si++;
      }
    }
  }

  public void set_sub_image(int x, int y, int z, int w, int h, int d, int f, int s, gl_pixel_pack p,
      Object data) {
    int i, j, k, si, sj, sk;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = 0;
      for (i = x; i < w; i++) {
        sj = 0;
        for (j = y; j < h; j++) {
          switch (f) {
            case GL.GL_COLOR_INDEX:
              break;
            case GL.GL_RGB:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = (byte) 255;
              break;
            case GL.GL_RGBA:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = get_data(si, sj, 3, s, data);
              break;
            case GL.GL_RED:
              r = get_data(si, sj, 0, s, data);
              g = (byte) 0;
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_GREEN:
              r = (byte) 0;
              g = get_data(si, sj, 0, s, data);
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_BLUE:
              r = (byte) 0;
              g = (byte) 0;
              b = get_data(si, sj, 0, s, data);
              a = (byte) 255;
              break;
            case GL.GL_ALPHA:
              r = (byte) 0;
              g = (byte) 0;
              b = (byte) 0;
              a = get_data(si, sj, 0, s, data);
              break;
            case GL.GL_LUMINANCE:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = (byte) 255;
              break;
            case GL.GL_LUMINANCE_ALPHA:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = get_data(si, sj, 1, s, data);
              break;
          }
          /*
           * switch (c) { case 1: ImageData [i][j][0][0] = r; ImageData [i][j][0][1] = (byte)0;
           * ImageData [i][j][0][2] = (byte)0; ImageData [i][j][0][3] = (byte)255; break; case 2:
           * ImageData [i][j][0][0] = r; ImageData [i][j][0][1] = (byte)0; ImageData [i][j][0][2] =
           * (byte)0; ImageData [i][j][0][3] = a; break; case 3: ImageData [i][j][0][0] = r;
           * ImageData [i][j][0][1] = g; ImageData [i][j][0][2] = b; ImageData [i][j][0][3] =
           * (byte)255; break; case 4:
           */
          ImageData[i][j][0][0] = r;
          ImageData[i][j][0][1] = g;
          ImageData[i][j][0][2] = b;
          ImageData[i][j][0][3] = a;
          /*
           * break; }
           */
          sj++;
        }
        si++;
      }
    }
  }

  public gl_image(int w, int c, int f, int s, gl_pixel_pack p, Object data) {
    int i, si;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    Width = w - p.SkipPixels;
    Height = 1;
    Depth = 1;
    ImageData = new byte[Width][1][1][4];

    // boolean need_scale = is_need_scale(p);

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = p.SkipPixels;
      for (i = 0; i < Width; i++) {
        switch (f) {
          case GL.GL_COLOR_INDEX:
            break;
          case GL.GL_RGB:
            r = get_data(si, 0, s, data);
            g = get_data(si, 1, s, data);
            b = get_data(si, 2, s, data);
            a = (byte) 255;
            break;
          case GL.GL_RGBA:
            r = get_data(si, 0, s, data);
            g = get_data(si, 1, s, data);
            b = get_data(si, 2, s, data);
            a = get_data(si, 3, s, data);
            break;
          case GL.GL_RED:
            r = get_data(si, 0, s, data);
            g = (byte) 0;
            b = (byte) 0;
            a = (byte) 255;
            break;
          case GL.GL_GREEN:
            r = (byte) 0;
            g = get_data(si, 0, s, data);
            b = (byte) 0;
            a = (byte) 255;
            break;
          case GL.GL_BLUE:
            r = (byte) 0;
            g = (byte) 0;
            b = get_data(si, 0, s, data);
            a = (byte) 255;
            break;
          case GL.GL_ALPHA:
            r = (byte) 0;
            g = (byte) 0;
            b = (byte) 0;
            a = get_data(si, 0, s, data);
            break;
          case GL.GL_LUMINANCE:
            r = get_data(si, 0, s, data);
            g = r;
            b = r;
            a = (byte) 255;
            break;
          case GL.GL_LUMINANCE_ALPHA:
            r = get_data(si, 0, s, data);
            g = r;
            b = r;
            a = get_data(si, 1, s, data);
            break;
        }
        /*
         * if (need_scale) { r = p.Red.apply_bias_scale(r); g = p.Green.apply_bias_scale(g); b =
         * p.Blue.apply_bias_scale(b); a = p.Alpha.apply_bias_scale(a); }
         */
        switch (c) {
          case 1:
            ImageData[i][0][0][0] = r;
            ImageData[i][0][0][1] = (byte) 0;
            ImageData[i][0][0][2] = (byte) 0;
            ImageData[i][0][0][3] = (byte) 255;
            break;
          case 2:
            ImageData[i][0][0][0] = r;
            ImageData[i][0][0][1] = (byte) 0;
            ImageData[i][0][0][2] = (byte) 0;
            ImageData[i][0][0][3] = a;
            break;
          case 3:
            ImageData[i][0][0][0] = r;
            ImageData[i][0][0][1] = g;
            ImageData[i][0][0][2] = b;
            ImageData[i][0][0][3] = (byte) 255;
            break;
          case 4:
            ImageData[i][0][0][0] = r;
            ImageData[i][0][0][1] = g;
            ImageData[i][0][0][2] = b;
            ImageData[i][0][0][3] = a;
            break;
        }
        si++;
      }
    }
  }

  // public gl_image (int w, int h, int c, int f, int t,
  // gl_pixel_pack p, byte data [][][]) {
  public gl_image(int w, int h, int c, int f, int s, gl_pixel_pack p, Object data) {
    int i, j, si, sj;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    Width = w - p.SkipPixels;
    Height = h - p.SkipRows;
    Depth = 1;
    ImageData = new byte[Width][Height][1][4];

    // boolean need_scale = is_need_scale(p);

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = p.SkipPixels;
      for (i = 0; i < Width; i++) {
        sj = p.SkipRows;
        for (j = 0; j < Height; j++) {
          switch (f) {
            case GL.GL_COLOR_INDEX:
              break;
            case GL.GL_RGB:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = (byte) 255;
              break;
            case GL.GL_RGBA:
              r = get_data(si, sj, 0, s, data);
              g = get_data(si, sj, 1, s, data);
              b = get_data(si, sj, 2, s, data);
              a = get_data(si, sj, 3, s, data);
              break;
            case GL.GL_RED:
              r = get_data(si, sj, 0, s, data);
              g = (byte) 0;
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_GREEN:
              r = (byte) 0;
              g = get_data(si, sj, 0, s, data);
              b = (byte) 0;
              a = (byte) 255;
              break;
            case GL.GL_BLUE:
              r = (byte) 0;
              g = (byte) 0;
              b = get_data(si, sj, 0, s, data);
              a = (byte) 255;
              break;
            case GL.GL_ALPHA:
              r = (byte) 0;
              g = (byte) 0;
              b = (byte) 0;
              a = get_data(si, sj, 0, s, data);
              break;
            case GL.GL_LUMINANCE:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = (byte) 255;
              break;
            case GL.GL_LUMINANCE_ALPHA:
              r = get_data(si, sj, 0, s, data);
              g = r;
              b = r;
              a = get_data(si, sj, 1, s, data);
              break;
          }
          /*
           * if (need_scale) { r = p.Red.apply_bias_scale(r); g = p.Green.apply_bias_scale(g); b =
           * p.Blue.apply_bias_scale(b); a = p.Alpha.apply_bias_scale(a); }
           */
          switch (c) {
            case 1:
              ImageData[i][j][0][0] = r;
              ImageData[i][j][0][1] = (byte) 0;
              ImageData[i][j][0][2] = (byte) 0;
              ImageData[i][j][0][3] = (byte) 255;
              break;
            case 2:
              ImageData[i][j][0][0] = r;
              ImageData[i][j][0][1] = (byte) 0;
              ImageData[i][j][0][2] = (byte) 0;
              ImageData[i][j][0][3] = a;
              break;
            case 3:
              ImageData[i][j][0][0] = r;
              ImageData[i][j][0][1] = g;
              ImageData[i][j][0][2] = b;
              ImageData[i][j][0][3] = (byte) 255;
              break;
            case 4:
              ImageData[i][j][0][0] = r;
              ImageData[i][j][0][1] = g;
              ImageData[i][j][0][2] = b;
              ImageData[i][j][0][3] = a;
              break;
          }
          sj++;
        }
        si++;
      }
    }
  }

  public gl_image(int w, int h, int d, int c, int f, int s, gl_pixel_pack p, Object data) {
    int i, j, k, si, sj, sk;
    byte r = (byte) 0;
    byte g = (byte) 0;
    byte b = (byte) 0;
    byte a = (byte) 255;

    Width = w - p.SkipPixels;
    Height = h - p.SkipRows;
    Depth = d - p.SkipImages;
    ImageData = new byte[Width][Height][Depth][4];

    // boolean need_scale = is_need_scale(p);

    if (s == 0) { // data is GL_BITMAP
    } else {
      si = p.SkipPixels;
      for (i = 0; i < Width; i++) {
        sj = p.SkipRows;
        for (j = 0; j < Height; j++) {
          sk = p.SkipImages;
          for (k = 0; k < Depth; k++) {
            switch (f) {
              case GL.GL_COLOR_INDEX:
                break;
              case GL.GL_RGB:
                r = get_data(si, sj, sk, 0, s, data);
                g = get_data(si, sj, sk, 1, s, data);
                b = get_data(si, sj, sk, 2, s, data);
                a = (byte) 255;
                break;
              case GL.GL_RGBA:
                r = get_data(si, sj, sk, 0, s, data);
                g = get_data(si, sj, sk, 1, s, data);
                b = get_data(si, sj, sk, 2, s, data);
                a = get_data(si, sj, sk, 3, s, data);
                break;
              case GL.GL_RED:
                r = get_data(si, sj, sk, 0, s, data);
                g = (byte) 0;
                b = (byte) 0;
                a = (byte) 255;
                break;
              case GL.GL_GREEN:
                r = (byte) 0;
                g = get_data(si, sj, sk, 0, s, data);
                b = (byte) 0;
                a = (byte) 255;
                break;
              case GL.GL_BLUE:
                r = (byte) 0;
                g = (byte) 0;
                b = get_data(si, sj, sk, 0, s, data);
                a = (byte) 255;
                break;
              case GL.GL_ALPHA:
                r = (byte) 0;
                g = (byte) 0;
                b = (byte) 0;
                a = get_data(si, sj, sk, 0, s, data);
                break;
              case GL.GL_LUMINANCE:
                r = get_data(si, sj, sk, 0, s, data);
                g = r;
                b = r;
                a = (byte) 255;
                break;
              case GL.GL_LUMINANCE_ALPHA:
                r = get_data(si, sj, sk, 0, s, data);
                g = r;
                b = r;
                a = get_data(si, sj, sk, 1, s, data);
                break;
            }
            /*
             * if (need_scale) { r = p.Red.apply_bias_scale(r); g = p.Green.apply_bias_scale(g); b =
             * p.Blue.apply_bias_scale(b); a = p.Alpha.apply_bias_scale(a); }
             */
            switch (c) {
              case 1:
                ImageData[i][j][k][0] = r;
                ImageData[i][j][k][1] = (byte) 0;
                ImageData[i][j][k][2] = (byte) 0;
                ImageData[i][j][k][3] = (byte) 255;
                break;
              case 2:
                ImageData[i][j][k][0] = r;
                ImageData[i][j][k][1] = (byte) 0;
                ImageData[i][j][k][2] = (byte) 0;
                ImageData[i][j][k][3] = a;
                break;
              case 3:
                ImageData[i][j][k][0] = r;
                ImageData[i][j][k][1] = g;
                ImageData[i][j][k][2] = b;
                ImageData[i][j][k][3] = (byte) 255;
                break;
              case 4:
                ImageData[i][j][k][0] = r;
                ImageData[i][j][k][1] = g;
                ImageData[i][j][k][2] = b;
                ImageData[i][j][k][3] = a;
                break;
            }
            sk++;
          }
          sj++;
        }
        si++;
      }
    }
  }

}
