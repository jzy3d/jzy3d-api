/*
 * @(#)gl_texture_img.java 0.3 01/12/03
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

package jgl.context.attrib.texture;

import jgl.context.gl_image;
import jgl.context.gl_util;

/**
 * gl_texture_img is the texturing x-D texture image at LOD i class of jGL 2.4.
 *
 * @version 0.3, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_texture_img {

  /** GL_TEXTURE_WIDTH: x-D texture image i's width */
  public int Width = 0;

  /** GL_TEXTURE_HEIGHT: x-D texture image i's height */
  public int Height = 0;

  /** GL_TEXTURE_DEPTH: x-D texture image i's depth */
  public int Depth = 0;

  /** GL_TEXTURE_BORDER: x-D texture image i's border width */
  public int Border = 0;

  /** GL_TEXTURE_COMPONENTS: Texture image components */
  public int Component = 1;

  /* the image data */
  public gl_image Image;

  /****** will delete the following ******/
  public int WidthLog2; /* = log2(Width) */
  public int HeightLog2; /* = log2(Height) */
  public int DepthLog2; /* = log2(Depth) */
  public int MaxLog2; /* = MAX(WidthLog2, HeightLog2, DepthLog2) */

  public gl_texture_img(int border, gl_image data) {
    Width = data.Width;
    Height = data.Height;
    Depth = data.Depth;
    Border = border;
    Image = data;
    WidthLog2 = gl_util.logbase2(Width - 2 * Border);
    if (Height == 1) /* 1-D texture */
      HeightLog2 = 0;
    else
      HeightLog2 = gl_util.logbase2(Height - 2 * Border);
    if (Depth == 1) /* 2-D texture */
      DepthLog2 = 0;
    else
      DepthLog2 = gl_util.logbase2(Depth - 2 * Border);
    MaxLog2 = Math.max(WidthLog2, HeightLog2);
  }

}
