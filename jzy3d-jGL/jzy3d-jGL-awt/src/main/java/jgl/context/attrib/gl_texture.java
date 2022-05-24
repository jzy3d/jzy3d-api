/*
 * @(#)gl_texture.java 0.5 03/05/14
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999-2003 Robin Bing-Yu Chen
 * (robin@nis-lab.is.s.u-tokyo.ac.jp)
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

// import java.lang.System;

import jgl.GL;
import jgl.context.gl_image;
// import jgl.context.gl_list_item;
import jgl.context.gl_util;
import jgl.context.attrib.texture.gl_texture_gen;
import jgl.context.attrib.texture.gl_texture_obj;

/**
 * gl_texture is the texturing class of jGL 2.4.
 *
 * @version 0.5, 14 May 2003
 * @author Robin Bing-Yu Chen
 */

public class gl_texture {

  /*
   * public static final int TEXTURE_1D = 1; public static final int TEXTURE_2D = 2; public static
   * final int TEXTURE_3D = 4;
   * 
   * public static final int Q_BIT = 1; public static final int R_BIT = 2; public static final int
   * S_BIT = 4; public static final int T_BIT = 8;
   */

  /** GL_TEXTURE_x: True if x-D texturing enabled (x is 1D, 2D, or 3D) */
  public boolean Enable1D = false;
  public boolean Enable2D = false;
  public boolean Enable3D = false;

  /** For texture x-D */
  public gl_texture_obj Current1D = new gl_texture_obj();
  public gl_texture_obj Current2D = new gl_texture_obj();
  public gl_texture_obj Current3D = new gl_texture_obj();
  public gl_texture_obj CurrentObj;

  /** GL_TEXTURE_ENV_MODE: Texture application function */
  public int EnvMode = GL.GL_MODULATE;

  /** GL_TEXTURE_ENV_COLOR: Texture environment color */
  public float EnvColor[] = {0, 0, 0, 0};

  /** GL_TEXTURE_GEN_x: Texgen enabled (x is S, T, R, Q) */
  // public int GenEnabled = 0; /* Bitwise-OR of [QRST]_BIT values */

  /** For texgen x */
  public gl_texture_gen CurrentS = new gl_texture_gen();
  public gl_texture_gen CurrentT = new gl_texture_gen();
  public gl_texture_gen CurrentR = new gl_texture_gen();
  public gl_texture_gen CurrentQ = new gl_texture_gen();

  public boolean check_tex() {
    if (Enable3D) {
      CurrentObj = Current3D;
      return true;
    }
    if (Enable2D) {
      CurrentObj = Current2D;
      return true;
    }
    if (Enable1D) {
      CurrentObj = Current1D;
      return true;
    }
    return false;
  }

  public int is_tex_enabled() {
    int TexEnabled = 0;
    if (Enable1D)
      TexEnabled = TexEnabled | 1;
    if (Enable2D)
      TexEnabled = TexEnabled | 2;
    if (Enable3D)
      TexEnabled = TexEnabled | 4;
    return TexEnabled;
  }

  public boolean is_tex_enabled(int cap) {
    switch (cap) {
      case GL.GL_TEXTURE_1D:
        return (Enable1D);
      case GL.GL_TEXTURE_2D:
        return (Enable2D);
      case GL.GL_TEXTURE_3D:
        return (Enable3D);
    }
    return false;
  }

  public boolean tex_enable(int state) {
    if ((state & 1) != 0)
      Enable1D = true;
    else
      Enable1D = false;
    if ((state & 2) != 0)
      Enable2D = true;
    else
      Enable2D = false;
    if ((state & 4) != 0)
      Enable3D = true;
    else
      Enable3D = false;
    return check_tex();
  }

  public boolean tex_enable(int cap, boolean state) {
    switch (cap) {
      case GL.GL_TEXTURE_1D:
        Enable1D = state;
        break;
      case GL.GL_TEXTURE_2D:
        Enable2D = state;
        break;
      case GL.GL_TEXTURE_3D:
        Enable3D = state;
        break;
    }
    return check_tex();
  }

  public int is_tex_gen_enabled() {
    int GenEnabled = 0;
    if (CurrentQ.Enable)
      GenEnabled = GenEnabled | 1;
    if (CurrentR.Enable)
      GenEnabled = GenEnabled | 2;
    if (CurrentS.Enable)
      GenEnabled = GenEnabled | 4;
    if (CurrentT.Enable)
      GenEnabled = GenEnabled | 8;
    return GenEnabled;
  }

  public boolean is_tex_gen_enabled(int cap) {
    switch (cap) {
      case GL.GL_TEXTURE_GEN_Q:
        return (CurrentQ.Enable);
      case GL.GL_TEXTURE_GEN_R:
        return (CurrentR.Enable);
      case GL.GL_TEXTURE_GEN_S:
        return (CurrentS.Enable);
      case GL.GL_TEXTURE_GEN_T:
        return (CurrentT.Enable);
    }
    return false;
  }

  public void tex_gen_enable(int state) {
    if ((state & 1) != 0)
      CurrentQ.Enable = true;
    else
      CurrentQ.Enable = false;
    if ((state & 2) != 0)
      CurrentR.Enable = true;
    else
      CurrentR.Enable = false;
    if ((state & 4) != 0)
      CurrentS.Enable = true;
    else
      CurrentS.Enable = false;
    if ((state & 8) != 0)
      CurrentT.Enable = true;
    else
      CurrentT.Enable = false;
  }

  public void tex_gen_enable(int cap, boolean state) {
    switch (cap) {
      case GL.GL_TEXTURE_GEN_Q:
        CurrentQ.Enable = state;
        break;
      case GL.GL_TEXTURE_GEN_R:
        CurrentR.Enable = state;
        break;
      case GL.GL_TEXTURE_GEN_S:
        CurrentS.Enable = state;
        break;
      case GL.GL_TEXTURE_GEN_T:
        CurrentT.Enable = state;
        break;
    }
  }

  public void set_tex(int target, int pname, float params[]) {
    switch (target) {
      case GL.GL_TEXTURE_1D:
        Current1D.set_tex(pname, params);
        break;
      case GL.GL_TEXTURE_2D:
        Current2D.set_tex(pname, params);
        break;
      case GL.GL_TEXTURE_3D:
        Current3D.set_tex(pname, params);
        break;
    }
  }

  public void set_tex_image(int target, int level, int border, gl_image img) {
    switch (target) {
      case GL.GL_TEXTURE_1D:
        Current1D.set_tex_image(level, border, img);
        break;
      case GL.GL_TEXTURE_2D:
        Current2D.set_tex_image(level, border, img);
        break;
      case GL.GL_TEXTURE_3D:
        Current3D.set_tex_image(level, border, img);
        break;
    }
  }

  public gl_image get_tex_image(int target, int level) {
    switch (target) {
      case GL.GL_TEXTURE_1D:
        return Current1D.get_tex_image(level);
      case GL.GL_TEXTURE_2D:
        return Current2D.get_tex_image(level);
      case GL.GL_TEXTURE_3D:
        return Current3D.get_tex_image(level);
      default:
        return null;
    }
  }

  public void set_tex_gen_i(int coord, int param) {
    switch (coord) {
      case GL.GL_Q:
        CurrentQ.Mode = param;
        break;
      case GL.GL_R:
        CurrentR.Mode = param;
        break;
      case GL.GL_S:
        CurrentS.Mode = param;
        break;
      case GL.GL_T:
        CurrentT.Mode = param;
        break;
    }
  }

  public void set_tex_gen_f(int coord, int pname, float params[]) {
    switch (coord) {
      case GL.GL_Q:
        CurrentQ.set_tex_gen(pname, params);
        break;
      case GL.GL_R:
        CurrentR.set_tex_gen(pname, params);
        break;
      case GL.GL_S:
        CurrentS.set_tex_gen(pname, params);
        break;
      case GL.GL_T:
        CurrentT.set_tex_gen(pname, params);
        break;
    }
  }

  public void bind_texture(int target, gl_texture_obj obj) {
    switch (target) {
      case GL.GL_TEXTURE_1D:
        Current1D = obj;
        break;
      case GL.GL_TEXTURE_2D:
        Current2D = obj;
        break;
      case GL.GL_TEXTURE_3D:
        Current3D = obj;
        break;
    }
    check_tex();
  }

  /****** will delete the following ******/

  private int get_map_nearest_index(float s, int w, int WrapS) {
    int i = (int) Math.floor(s * (w - 1));
    if (i < 0)
      i = 0;

    if (WrapS == GL.GL_REPEAT) {
      i = i & (w - 1);
    } else { // WrapS == GL.GL_CLAMP
      if (i >= w)
        i = w - 1;
    }

    return i;
  }

  private int tex_map_nearest_1d(float s, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i = get_map_nearest_index(s, img.Width, CurrentObj.WrapS);

    return (0xff000000 | (img.ImageData[i][0][0][0] & 0x000000ff) << 16
        | (img.ImageData[i][0][0][1] & 0x000000ff) << 8 | (img.ImageData[i][0][0][2] & 0x000000ff));
  }

  private int tex_map_nearest_2d(float s, float t, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i = get_map_nearest_index(s, img.Width, CurrentObj.WrapS);
    int j = get_map_nearest_index(t, img.Height, CurrentObj.WrapT);

    return (0xff000000 | (img.ImageData[i][j][0][0] & 0x000000ff) << 16
        | (img.ImageData[i][j][0][1] & 0x000000ff) << 8 | (img.ImageData[i][j][0][2] & 0x000000ff));
  }

  private int tex_map_nearest_3d(float s, float t, float r, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i = get_map_nearest_index(s, img.Width, CurrentObj.WrapS);
    int j = get_map_nearest_index(t, img.Height, CurrentObj.WrapT);
    int k = get_map_nearest_index(r, img.Depth, CurrentObj.WrapR);

    return (0xff000000 | (img.ImageData[i][j][k][0] & 0x000000ff) << 16
        | (img.ImageData[i][j][k][1] & 0x000000ff) << 8 | (img.ImageData[i][j][k][2] & 0x000000ff));
  }

  private float get_map_linear_index(float s, int w, int WrapS, int i[]) {
    float u = s * w - 0.5f;

    if (WrapS == GL.GL_REPEAT) {
      i[0] = ((int) Math.floor(u)) & (w - 1);
      i[1] = (i[0] + 1) & (w - 1);
    } else { // CurrentObj.WrapS == GL.GL_CLAMP
      i[0] = (int) Math.floor(u);
      if (i[0] < 0)
        i[0] = 0;
      if (i[0] >= (w - 1))
        i[0] = w - 2;
      i[1] = i[0] + 1;
    }

    return gl_util.frac(u);
  }

  private int tex_map_linear_1d(float s, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i[] = new int[2];

    float a = get_map_linear_index(s, img.Width, CurrentObj.WrapS, i);

    float w000 = (1 - a);
    float w001 = a;

    int rgb[] = new int[3];

    rgb[0] = (int) (((float) (img.ImageData[i[0]][0][0][0] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][0][0][0] & 0x000000ff)) * w001);
    rgb[1] = (int) (((float) (img.ImageData[i[0]][0][0][1] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][0][0][1] & 0x000000ff)) * w001);
    rgb[2] = (int) (((float) (img.ImageData[i[0]][0][0][2] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][0][0][2] & 0x000000ff)) * w001);

    return gl_util.RGBtoI(rgb[0], rgb[1], rgb[2]);
  }

  private int tex_map_linear_2d(float s, float t, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i[] = new int[2];
    int j[] = new int[2];

    float a = get_map_linear_index(s, img.Width, CurrentObj.WrapS, i);
    float b = get_map_linear_index(t, img.Height, CurrentObj.WrapT, j);

    float w000 = (1 - a) * (1 - b);
    float w001 = a * (1 - b);
    float w010 = (1 - a) * b;
    float w011 = a * b;

    int rgb[] = new int[3];

    rgb[0] = (int) (((float) (img.ImageData[i[0]][j[0]][0][0] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][0][0] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][0][0] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][0][0] & 0x000000ff)) * w011);
    rgb[1] = (int) (((float) (img.ImageData[i[0]][j[0]][0][1] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][0][1] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][0][1] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][0][1] & 0x000000ff)) * w011);
    rgb[2] = (int) (((float) (img.ImageData[i[0]][j[0]][0][2] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][0][2] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][0][2] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][0][2] & 0x000000ff)) * w011);

    return gl_util.RGBtoI(rgb[0], rgb[1], rgb[2]);
  }

  private int tex_map_linear_3d(float s, float t, float r, int l) {
    gl_image img = CurrentObj.Image[l].Image;

    int i[] = new int[2];
    int j[] = new int[2];
    int k[] = new int[2];

    float a = get_map_linear_index(s, img.Width, CurrentObj.WrapS, i);
    float b = get_map_linear_index(t, img.Height, CurrentObj.WrapT, j);
    float c = get_map_linear_index(r, img.Depth, CurrentObj.WrapR, k);

    float w000 = (1 - a) * (1 - b) * (1 - c);
    float w001 = a * (1 - b) * (1 - c);
    float w010 = (1 - a) * b * (1 - c);
    float w011 = a * b * (1 - c);
    float w100 = (1 - a) * (1 - b) * c;
    float w101 = a * (1 - b) * c;
    float w110 = (1 - a) * b * c;
    float w111 = a * b * c;

    int rgb[] = new int[3];

    rgb[0] = (int) (((float) (img.ImageData[i[0]][j[0]][k[0]][0] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][k[0]][0] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][k[0]][0] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][k[0]][0] & 0x000000ff)) * w011
        + ((float) (img.ImageData[i[0]][j[0]][k[1]][0] & 0x000000ff)) * w100
        + ((float) (img.ImageData[i[1]][j[0]][k[1]][0] & 0x000000ff)) * w101
        + ((float) (img.ImageData[i[0]][j[1]][k[1]][0] & 0x000000ff)) * w110
        + ((float) (img.ImageData[i[1]][j[1]][k[1]][0] & 0x000000ff)) * w111);
    rgb[1] = (int) (((float) (img.ImageData[i[0]][j[0]][k[0]][1] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][k[0]][1] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][k[0]][1] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][k[0]][1] & 0x000000ff)) * w011
        + ((float) (img.ImageData[i[0]][j[0]][k[1]][1] & 0x000000ff)) * w100
        + ((float) (img.ImageData[i[1]][j[0]][k[1]][1] & 0x000000ff)) * w101
        + ((float) (img.ImageData[i[0]][j[1]][k[1]][1] & 0x000000ff)) * w110
        + ((float) (img.ImageData[i[1]][j[1]][k[1]][1] & 0x000000ff)) * w111);
    rgb[2] = (int) (((float) (img.ImageData[i[0]][j[0]][k[0]][2] & 0x000000ff)) * w000
        + ((float) (img.ImageData[i[1]][j[0]][k[0]][2] & 0x000000ff)) * w001
        + ((float) (img.ImageData[i[0]][j[1]][k[0]][2] & 0x000000ff)) * w010
        + ((float) (img.ImageData[i[1]][j[1]][k[0]][2] & 0x000000ff)) * w011
        + ((float) (img.ImageData[i[0]][j[0]][k[1]][2] & 0x000000ff)) * w100
        + ((float) (img.ImageData[i[1]][j[0]][k[1]][2] & 0x000000ff)) * w101
        + ((float) (img.ImageData[i[0]][j[1]][k[1]][2] & 0x000000ff)) * w110
        + ((float) (img.ImageData[i[1]][j[1]][k[1]][2] & 0x000000ff)) * w111);

    return gl_util.RGBtoI(rgb[0], rgb[1], rgb[2]);
  }

  private int tex_map(float s, float t, float r) {
    if (CurrentObj.MagFilter == GL.GL_NEAREST) {
      if (Enable3D)
        return tex_map_nearest_3d(s, t, r, 0);
      if (Enable2D)
        return tex_map_nearest_2d(s, t, 0);
      if (Enable1D)
        return tex_map_nearest_1d(s, 0);
    } else { // CurrentObj.MagFilter == GL.GL_LINEAR
      if (Enable3D)
        return tex_map_linear_3d(s, t, r, 0);
      if (Enable2D)
        return tex_map_linear_2d(s, t, 0);
      if (Enable1D)
        return tex_map_linear_1d(s, 0);
    }
    return 0;
  }

  private int tex_mid_nearest_nearest(float s, float t, float r, float l) {
    int level;
    if (l <= 0.5) {
      level = 0;
    } else {
      level = (int) (l + 0.499999);
      if (level > CurrentObj.Image[0].WidthLog2) {
        level = CurrentObj.Image[0].WidthLog2;
      }
    }
    if (Enable3D)
      return tex_map_nearest_3d(s, t, r, level);
    if (Enable2D)
      return tex_map_nearest_2d(s, t, level);
    if (Enable1D)
      return tex_map_nearest_1d(s, level);
    return 0;
  }

  private int tex_mid_linear_nearest(float s, float t, float r, float l) {
    int level;
    if (l <= 0.5) {
      level = 0;
    } else {
      level = (int) (l + 0.499999);
      if (level > CurrentObj.Image[0].WidthLog2) {
        level = CurrentObj.Image[0].WidthLog2;
      }
    }
    if (Enable3D)
      return tex_map_linear_3d(s, t, r, level);
    if (Enable2D)
      return tex_map_linear_2d(s, t, level);
    if (Enable1D)
      return tex_map_linear_1d(s, level);
    return 0;
  }

  private int get_mid_linear_color(int c1, int c2, float l) {
    float f = gl_util.frac(l);
    int rgb[][] = new int[2][3];

    rgb[0][2] = c1 & 0x00ff0000;
    rgb[0][1] = c1 & 0x0000ff00;
    rgb[0][0] = c1 & 0x000000ff;
    rgb[1][2] = c2 & 0x00ff0000;
    rgb[1][1] = c2 & 0x0000ff00;
    rgb[1][0] = c2 & 0x000000ff;

    rgb[0][0] = (int) (((float) rgb[1][0]) * ((float) 1.0 - f) + ((float) rgb[0][0]) * f);
    rgb[0][1] = (int) (((float) rgb[1][1]) * ((float) 1.0 - f) + ((float) rgb[0][1]) * f);
    rgb[0][2] = (int) (((float) rgb[1][2]) * ((float) 1.0 - f) + ((float) rgb[0][2]) * f);

    return gl_util.RGBtoI(rgb[0][0], rgb[0][1], rgb[0][2]);
  }

  private int tex_mid_nearest_linear_1d(float s, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_nearest_1d(s, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_nearest_1d(s, level - 1), tex_map_nearest_1d(s, level),
          l);
    }
  }

  private int tex_mid_nearest_linear_2d(float s, float t, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_nearest_2d(s, t, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_nearest_2d(s, t, level - 1),
          tex_map_nearest_2d(s, t, level), l);
    }
  }

  private int tex_mid_nearest_linear_3d(float s, float t, float r, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_nearest_3d(s, t, r, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_nearest_3d(s, t, r, level - 1),
          tex_map_nearest_3d(s, t, r, level), l);
    }
  }

  private int tex_mid_linear_linear_1d(float s, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_linear_1d(s, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_linear_1d(s, level - 1), tex_map_linear_1d(s, level), l);
    }
  }

  private int tex_mid_linear_linear_2d(float s, float t, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_linear_2d(s, t, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_linear_2d(s, t, level - 1),
          tex_map_linear_2d(s, t, level), l);
    }
  }

  private int tex_mid_linear_linear_3d(float s, float t, float r, float l) {
    int max = CurrentObj.Image[0].MaxLog2;
    if (l >= max) {
      return tex_map_linear_3d(s, t, r, max);
    } else {
      int level = (int) (l + 1.0);
      if (level < 1) {
        level = 1;
      }
      if (level > max) {
        level = max;
      }

      return get_mid_linear_color(tex_map_linear_3d(s, t, r, level - 1),
          tex_map_linear_3d(s, t, r, level), l);
    }
  }

  private int tex_mid(float s, float t, float r, float l) {
    float c;

    if (CurrentObj.MagFilter == GL.GL_LINEAR
        && (CurrentObj.MinFilter == GL.GL_NEAREST_MIPMAP_NEAREST
            || CurrentObj.MinFilter == GL.GL_LINEAR_MIPMAP_NEAREST)) {
      c = (float) 0.5;
    } else {
      c = 0;
    }

    if (l > c) {
      switch (CurrentObj.MinFilter) {
        case GL.GL_NEAREST_MIPMAP_NEAREST:
          return tex_mid_nearest_nearest(s, t, r, l);
        case GL.GL_LINEAR_MIPMAP_NEAREST:
          return tex_mid_linear_nearest(s, t, r, l);
        case GL.GL_NEAREST_MIPMAP_LINEAR:
          if (Enable3D)
            return tex_mid_nearest_linear_3d(s, t, r, l);
          if (Enable2D)
            return tex_mid_nearest_linear_2d(s, t, l);
          if (Enable1D)
            return tex_mid_nearest_linear_1d(s, l);
        case GL.GL_LINEAR_MIPMAP_LINEAR:
          if (Enable3D)
            tex_mid_linear_linear_3d(s, t, r, l);
          if (Enable2D)
            tex_mid_linear_linear_2d(s, t, l);
          if (Enable1D)
            tex_mid_linear_linear_1d(s, l);
        default:
          return 0;
      }
    } else {
      return tex_map(s, t, r);
    }
  }

  // private float lambda (float w,float dsdx,float dsdy,float dtdx,float dtdy) {
  private float lambda(float w, float dsdx, float dsdy, float dtdx, float dtdy, float drdx,
      float drdy) {
    // float dudx, dudy, dvdx, dvdy, r1, r2, rho;
    float dudx, dudy, dvdx, dvdy, dwdx, dwdy, r1, r2, r3, rho;

    if (dsdx < 0) {
      dudx = -dsdx;
    } else {
      dudx = dsdx;
    }
    if (dsdy < 0) {
      dudy = -dsdy;
    } else {
      dudy = dsdy;
    }
    if (dtdx < 0) {
      dvdx = -dtdx;
    } else {
      dvdx = dtdx;
    }
    if (dtdy < 0) {
      dvdy = -dtdy;
    } else {
      dvdy = dtdy;
    }
    if (drdx < 0) {
      dwdx = -drdx;
    } else {
      dwdx = drdx;
    }
    if (drdy < 0) {
      dwdy = -drdy;
    } else {
      dwdy = drdy;
    }

    r1 = Math.max(dudx, dudy) * CurrentObj.Image[0].Width;
    r2 = Math.max(dvdx, dvdy) * CurrentObj.Image[0].Height;
    r3 = Math.max(dwdx, dwdy) * CurrentObj.Image[0].Depth;
    // rho = Math.max (r1, r2) / w;
    rho = Math.max(Math.max(r1, r2), r3) / w;

    if (rho < 0) {
      rho = -rho;
    }
    if (rho == 0) {
      return 0;
    }

    return ((float) (Math.log(rho) * 1.442695)); // 1.442695=1/log2
  }

  // public int tex_vertex (float s, float t, float w, float dsdx, float dsdy,
  // float dtdx, float dtdy) {
  public int tex_vertex(float s, float t, float r, float w, float dsdx, float dsdy, float dtdx,
      float dtdy, float drdx, float drdy) {
    if (CurrentObj.MinFilter == GL.GL_NEAREST || CurrentObj.MinFilter == GL.GL_LINEAR) {
      return tex_map(s, t, r);
    } else {
      return tex_mid(s, t, r, lambda(w, dsdx, dsdy, dtdx, dtdy, drdx, drdy));
    }
  }

  public int tex_vertex(float s, float t, float r) {
    return tex_map(s, t, r);
  }

  public float[] get_tex_gen_coord(float obj[], float eye[], float nor[]) {
    float TexCoord[] = new float[4];
    if (CurrentS.Enable)
      TexCoord[0] = CurrentS.get_tex_gen_coord(obj, eye, nor);
    else
      TexCoord[0] = 0;
    if (CurrentT.Enable)
      TexCoord[1] = CurrentT.get_tex_gen_coord(obj, eye, nor);
    else
      TexCoord[1] = 0;
    if (CurrentR.Enable)
      TexCoord[2] = CurrentR.get_tex_gen_coord(obj, eye);
    else
      TexCoord[2] = 0;
    if (CurrentQ.Enable)
      TexCoord[3] = CurrentQ.get_tex_gen_coord(obj, eye);
    else
      TexCoord[3] = 1;
    return TexCoord;
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [4];
   * AttribItem.IntPtr = new int [1]; AttribItem.FloatPtr = new float [4];
   * 
   * public gl_texture_obj Current1D = new gl_texture_obj (); public gl_texture_obj Current2D = new
   * gl_texture_obj (); public gl_texture_obj Current3D = new gl_texture_obj (); public int EnvMode
   * = GL.GL_MODULATE; public float EnvColor [] = {0, 0, 0, 0}; public gl_texture_gen CurrentS = new
   * gl_texture_gen (); public gl_texture_gen CurrentT = new gl_texture_gen (); public
   * gl_texture_gen CurrentR = new gl_texture_gen (); public gl_texture_gen CurrentQ = new
   * gl_texture_gen ();
   * 
   * AttribItem.IntPtr [0] = ClearIndex; System.arraycopy(ClearColor, 0, AttribItem.FloatPtr, 0, 4);
   * AttribItem.IntPtr [1] = IntClearColor; AttribItem.IntPtr [2] = IndexMask; AttribItem.IntPtr [3]
   * = ColorMask; AttribItem.BoolPtr [0] = AlphaEnable; AttribItem.IntPtr [4] = AlphaFunc;
   * AttribItem.IntPtr [5] = AlphaRef; AttribItem.BoolPtr [1] = BlendEnable; AttribItem.IntPtr [6] =
   * BlendSrc; AttribItem.IntPtr [7] = BlendDst; AttribItem.BoolPtr [2] = LogicOPEnable;
   * AttribItem.IntPtr [8] = LogicOPMode; AttribItem.BoolPtr [3] = DitherEnable; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { ClearIndex = AttribItem.IntPtr [0];
   * System.arraycopy(AttribItem.FloatPtr, 0, ClearColor, 0, 4); IntClearColor = AttribItem.IntPtr
   * [1]; IndexMask = AttribItem.IntPtr [2]; ColorMask = AttribItem.IntPtr [3]; AlphaEnable =
   * AttribItem.BoolPtr [0]; AlphaFunc = AttribItem.IntPtr [4]; AlphaRef = AttribItem.IntPtr [5];
   * BlendEnable = AttribItem.BoolPtr [1]; BlendSrc = AttribItem.IntPtr [6]; BlendDst =
   * AttribItem.IntPtr [7]; LogicOPEnable = AttribItem.BoolPtr [2]; LogicOPMode = AttribItem.IntPtr
   * [8]; DitherEnable = AttribItem.BoolPtr [3]; }
   */

}
