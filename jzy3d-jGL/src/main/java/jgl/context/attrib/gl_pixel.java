/*
 * @(#)gl_pixel.java 0.3 01/12/03
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
import jgl.context.attrib.pixel.gl_pixel_pack;
import jgl.context.attrib.pixel.gl_pixel_transfer;

/**
 * gl_pixel is the pixels class of jGL 2.4.
 *
 * @version 0.3, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_pixel {

  /** GL_MAP_COLOR: True if colors are mapped */
  public boolean MapColor = false;

  /** GL_MAP_STENCIL: True if stencil values are mapped */
  public boolean MapStencil = false;

  /** GL_INDEX_SHIFT: Value of GL_INDEX_SHIFT */
  public int IndexShift = 0;

  /** GL_INDEX_OFFSET: Value of GL_INDEX_OFFSET */
  public int IndexOffset = 0;

  /** GL_ZOOM_X: x zoom factor */
  public float ZoomX = 1;

  /** GL_ZOOM_Y: y zoom factor */
  public float ZoomY = 1;

  /** GL_READ_BUFFER: Read source buffer */
  public int ReadBuffer;

  /** GL_UNPACK_xxx: for GL_UNPACK_xxx */
  public gl_pixel_pack Unpack = new gl_pixel_pack();

  /** GL_PACK_xxx: for GL_PACK_xxx */
  public gl_pixel_pack Pack = new gl_pixel_pack();

  /** for GL_RED, GL_GREEN, GL_BLUE, GL_ALPHA, or GL_DEPTH */
  public gl_pixel_transfer Red = new gl_pixel_transfer();
  public gl_pixel_transfer Green = new gl_pixel_transfer();
  public gl_pixel_transfer Blue = new gl_pixel_transfer();
  public gl_pixel_transfer Alpha = new gl_pixel_transfer();
  public gl_pixel_transfer Depth = new gl_pixel_transfer();

  /** GL_PIXEL_MAP_I_TO_I: Pixel map from color index to color index */
  // public gl_pixel_map ItoI = new gl_pixel_map ();

  /** GL_PIXEL_MAP_S_TO_S: Pixel map from stencil index to stencil index */
  // public gl_pixel_map StoS = new gl_pixel_map ();

  /** GL_PIXEL_MAP_I_TO_R: Pixel map from color index to R */
  // public gl_pixel_map ItoR = new gl_pixel_map ();

  /** GL_PIXEL_MAP_I_TO_G: Pixel map from color index to G */
  // public gl_pixel_map ItoG = new gl_pixel_map ();

  /** GL_PIXEL_MAP_I_TO_B: Pixel map from color index to B */
  // public gl_pixel_map ItoB = new gl_pixel_map ();

  /** GL_PIXEL_MAP_I_TO_A: Pixel map from color index to A */
  // public gl_pixel_map ItoA = new gl_pixel_map ();

  /** GL_PIXEL_MAP_R_TO_R: Pixel map from R to R */
  // public gl_pixel_map RtoR = new gl_pixel_map ();

  /** GL_PIXEL_MAP_G_TO_G: Pixel map from G to G */
  // public gl_pixel_map GtoG = new gl_pixel_map ();

  /** GL_PIXEL_MAP_B_TO_B: Pixel map from B to B */
  // public gl_pixel_map BtoB = new gl_pixel_map ();

  /** GL_PIXEL_MAP_A_TO_A: Pixel map from A to A */
  // public gl_pixel_map AtoA = new gl_pixel_map ();

  public void pack_store(int pname, int param) {
    boolean bparam;
    if (param != 0) {
      bparam = true;
    } else {
      bparam = false;
    }
    switch (pname) {
      case GL.GL_PACK_SWAP_BYTES:
        Pack.SwapBytes = bparam;
        break;
      case GL.GL_UNPACK_SWAP_BYTES:
        Unpack.SwapBytes = bparam;
        break;
      case GL.GL_PACK_LSB_FIRST:
        Pack.LsbFirst = bparam;
        break;
      case GL.GL_UNPACK_LSB_FIRST:
        Unpack.LsbFirst = bparam;
        break;
      case GL.GL_PACK_ROW_LENGTH:
        Pack.RowLength = param;
        break;
      case GL.GL_UNPACK_ROW_LENGTH:
        Unpack.RowLength = param;
        break;
      case GL.GL_PACK_IMAGE_HEIGHT:
        Pack.ImageHeight = param;
        break;
      case GL.GL_UNPACK_IMAGE_HEIGHT:
        Unpack.ImageHeight = param;
        break;
      case GL.GL_PACK_SKIP_PIXELS:
        Pack.SkipPixels = param;
        break;
      case GL.GL_UNPACK_SKIP_PIXELS:
        Unpack.SkipPixels = param;
        break;
      case GL.GL_PACK_SKIP_ROWS:
        Pack.SkipRows = param;
        break;
      case GL.GL_UNPACK_SKIP_ROWS:
        Unpack.SkipRows = param;
        break;
      case GL.GL_PACK_SKIP_IMAGES:
        Pack.SkipImages = param;
        break;
      case GL.GL_UNPACK_SKIP_IMAGES:
        Unpack.SkipImages = param;
        break;
      case GL.GL_PACK_ALIGNMENT:
        Pack.Alignment = param;
        break;
      case GL.GL_UNPACK_ALIGNMENT:
        Unpack.Alignment = param;
        break;
    }
  }

  public void pixel_transfer(int pname, float param) {
    boolean bparam;
    if (param != 0) {
      bparam = true;
    } else {
      bparam = false;
    }
    switch (pname) {
      case GL.GL_MAP_COLOR:
        MapColor = bparam;
        break;
      case GL.GL_MAP_STENCIL:
        MapStencil = bparam;
        break;
      case GL.GL_INDEX_SHIFT:
        IndexShift = (int) param;
        break;
      case GL.GL_INDEX_OFFSET:
        IndexOffset = (int) param;
        break;
      case GL.GL_RED_SCALE:
        Red.Scale = param;
        break;
      case GL.GL_RED_BIAS:
        Red.Bias = param;
        break;
      case GL.GL_GREEN_SCALE:
        Green.Scale = param;
        break;
      case GL.GL_GREEN_BIAS:
        Green.Bias = param;
        break;
      case GL.GL_BLUE_SCALE:
        Blue.Scale = param;
        break;
      case GL.GL_BLUE_BIAS:
        Blue.Bias = param;
        break;
      case GL.GL_ALPHA_SCALE:
        Alpha.Scale = param;
        break;
      case GL.GL_ALPHA_BIAS:
        Alpha.Bias = param;
        break;
      case GL.GL_DEPTH_SCALE:
        Depth.Scale = param;
        break;
      case GL.GL_DEPTH_BIAS:
        Depth.Bias = param;
        break;
    }
  }

}
