/*
 * @(#)gle_context.java 0.1 02/12/30
 *
 * jGL 3-D graphics library for Java Copyright (c) 2002 Robin Bing-Yu Chen
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

package jgl.gle;

import jgl.context.gl_context;

/**
 * gle_context is the extension context class of jGL 2.5.
 *
 * @version 0.1, 30 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_context extends gl_context {

  public int ColorTransformation(float coord[], float normal[]) {
    if (Lighting.Enable) {
      return (Lighting.color_vertex(coord, normal));
    } else {
      return (Current.IntColor);
    }
  }

  public void gl_shade_model(int mode) {
    Lighting.ShadeModel = mode;
    ((gle_pointer) CR).gl_phong();
  }

  public gle_context() {
    CR = new gle_pointer(this);
  }

}
