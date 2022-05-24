/*
 * @(#)gl_texture_gen.java 0.2 01/02/13
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

import jgl.GL;
import jgl.context.gl_util;

/**
 * gl_texture_gen is the texgen class of jGL 2.3.
 *
 * @version 0.2, 13 Feb 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_texture_gen {

  /** GL_TEXTURE_GEN_x: Texgen enabled */
  public boolean Enable = false;

  /** GL_EYE_LINEAR: Texgen plane equation coefficients */
  public float EyeLinear[] = new float[4];

  /** GL_OBJECT_LINEAR: Texgen object linear coefficients */
  public float ObjectLinear[] = new float[4];

  /** GL_TEXTURE_GEN_MODE: Function used for texgen */
  public int Mode = GL.GL_EYE_LINEAR;

  public void set_tex_gen(int pname, float params[]) {
    if (pname == GL.GL_OBJECT_PLANE) {
      System.arraycopy(params, 0, ObjectLinear, 0, 4);
    } else { // GL.GL_EYE_PLANE:
      System.arraycopy(params, 0, EyeLinear, 0, 4);
    }
  }

  public float get_tex_gen_coord(float obj[], float eye[]) {
    switch (Mode) {
      case GL.GL_OBJECT_LINEAR:
        return gl_util.dot44(obj, ObjectLinear);
      case GL.GL_EYE_LINEAR:
        return gl_util.dot44(eye, EyeLinear);
      default:
        return 0;
    }
  }

  public float get_tex_gen_coord(float obj[], float eye[], float nor[]) {
    switch (Mode) {
      case GL.GL_OBJECT_LINEAR:
        return gl_util.dot44(obj, ObjectLinear);
      case GL.GL_EYE_LINEAR:
        return gl_util.dot44(eye, EyeLinear);
      case GL.GL_SPHERE_MAP:
        float u[] = new float[3];
        System.arraycopy(eye, 0, u, 0, 3);
        gl_util.normalize(u);
        float nu = 2.0f * gl_util.dot33(nor, u);
        float fx = u[0] - nor[0] * nu;
        float fy = u[1] - nor[1] * nu;
        float fz = u[2] - nor[2] * nu + 1.0f;
        float m = 2.0f * (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        if (m == 0)
          return 0.5f;
        else
          return (fx / m + 0.5F);
      default:
        return 0;
    }
  }

}
