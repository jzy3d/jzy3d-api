/*
 * @(#)gl_pixel_pack.java 0.3 01/12/03
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

package jgl.context.attrib.pixel;

/**
 * gl_pixel_pack is the pixel pack and unpack class of jGL 2.4.
 *
 * @version 0.3, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_pixel_pack {

  /** GL_xxPACK_SWAP_BYTES: Value of GL_xxPACK_SWAP_BYTES */
  public boolean SwapBytes = false;

  /** GL_xxPACK_LSB_FIRST: Value of GL_xxPACK_LSB_FIRST */
  public boolean LsbFirst = false;

  /** GL_xxPACK_IMAGE_HEIGHT: Value of GL_xxPACK_IMAGE_HEIGHT */
  public int ImageHeight = 0;

  /** GL_xxPACK_ROW_LENGTH: Value of GL_xxPACK_ROW_LENGTH */
  public int RowLength = 0;

  /** GL_xxPACK_SKIP_IMAGES: Value of GL_xxPACK_SKIP_IMAGES */
  public int SkipImages = 0;

  /** GL_xxPACK_SKIP_ROWS: Value of GL_xxPACK_SKIP_ROWS */
  public int SkipRows = 0;

  /** GL_xxPACK_SKIP_PIXELS: Value of GL_xxPACK_SKIP_PIXELS */
  public int SkipPixels = 0;

  /** GL_xxPACK_ALIGNMENT: Value of GL_xxPACK_ALIGNMENT */
  public int Alignment = 4;
  // but, Alignment seems to be necessary in jGL, IGNORE IT

}
