/*
 * @(#)gl_pixel_transfer.java 0.1 99/12/17
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

import jgl.context.gl_util;

/**
 * gl_pixel_transfer is the pixel transfer class of JavaGL 2.1.
 *
 * @version 0.1, 17 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_pixel_transfer {

  /** GL_x_SCALE: Value of GL_x_SCALE */
  public float Scale = 1;

  /** GL_x_BIAS: Value of GL_x_BIAS */
  public float Bias = 0;

  public float apply_bias_scale(float value) {
    float temp = value * Scale + Bias;
    return gl_util.CLAMP(temp, 0.0f, 1.0f);
  }

  public byte apply_bias_scale(byte value) {
    float temp = ((float) value / 255.0f) * Scale + Bias;
    temp = gl_util.CLAMP(temp, 0.0f, 1.0f);
    return (byte) (temp * 255.0f);
  }

}
