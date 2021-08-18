/*
 * @(#)gl_pixel_map.java 0.1 99/12/17
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999 Robin Bing-Yu Chen
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
 * gl_pixel_map is the pixel map class of JavaGL 2.1.
 *
 * @version 0.1, 17 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_pixel_map {

  /** GL_x: glPixelMap() translation tables */
  public float Table[] = new float[256]; // MAX_PIXEL_MAP_TABLE = 256

  /** GL_x_SIZE: Size of table x */
  public int Size = 1;

}
