/*
 * @(#)GLE.java 0.1 02/12/23
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

package jgl;

import jgl.context.gl_object;
import jgl.gle.gle_context;

/**
 * GLE is the extension class of jGL 2.5.
 *
 * @version 0.1, 23 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class GLE extends GL {

  /* Constant of GLE */
  public static final int GL_PHONG = GL_SMOOTH + 1;

  public void glShadeModel(int mode) {
    if (mode != GL_PHONG)
      super.glShadeModel(mode);
    else
      CC.gl_shade_model(mode);
  }

  public GLE(GL myGL) {
    Context = new gle_context();
    CC = (gl_object) Context;
    myGL.Context = Context;
    myGL.CC = CC;
    List = myGL.List;
    canvas = myGL.canvas;
    glImage = myGL.glImage;
    StartX = myGL.StartX;
    StartY = myGL.StartY;
  }

}
