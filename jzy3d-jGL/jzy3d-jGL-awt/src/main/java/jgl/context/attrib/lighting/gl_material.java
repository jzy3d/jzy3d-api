/*
 * @(#)gl_material.java 0.3 01/03/15
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2001 Robin Bing-Yu Chen
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

package jgl.context.attrib.lighting;

import jgl.GL;

/**
 * gl_material is the lighting masterial color class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_material {

  /** GL_AMBIENT: Ambient material color */
  public float Ambient[] = {0.2f, 0.2f, 0.2f, 1};

  /** GL_DEFFUSE: Diffuse material color */
  public float Diffuse[] = {0.8f, 0.8f, 0.8f, 1};

  /** GL_SPECULAR: Specular material color */
  public float Specular[] = {0, 0, 0, 1};

  /** GL_EMISSION: Emissive material color */
  public float Emission[] = {0, 0, 0, 1};

  /** GL_SHININESS: Specular exponent of material */
  public float Shininess = 0;

  /** GL_COLOR_INDEXES: Ca, Cd, Cs for color-index lighting, also in gl_enable */
  public float ColorIndexAmbient = 0;
  public float ColorIndexDiffuse = 1;
  public float ColorIndexSpecular = 1;

  public void set_material(int pname, float params[]) {
    switch (pname) {
      case GL.GL_AMBIENT:
        Ambient = params;
        break;
      case GL.GL_DIFFUSE:
        Diffuse = params;
        break;
      case GL.GL_SPECULAR:
        Specular = params;
        break;
      case GL.GL_EMISSION:
        Emission = params;
        break;
      case GL.GL_SHININESS:
        Shininess = params[0];
        break;
      case GL.GL_AMBIENT_AND_DIFFUSE:
        Ambient = params;
        Diffuse = params;
        break;
      case GL.GL_COLOR_INDEXES:
        ColorIndexAmbient = params[0];
        ColorIndexDiffuse = params[1];
        ColorIndexSpecular = params[2];
        break;
      default:
        break;
    }
  }

  public float[] get_material(int pname) {
    float temp[];
    switch (pname) {
      case GL.GL_AMBIENT:
        return Ambient;
      case GL.GL_DIFFUSE:
        return Diffuse;
      case GL.GL_SPECULAR:
        return Specular;
      case GL.GL_EMISSION:
        return Emission;
      case GL.GL_SHININESS:
        temp = new float[1];
        temp[0] = Shininess;
        return temp;
      case GL.GL_COLOR_INDEXES:
        temp = new float[3];
        temp[0] = ColorIndexAmbient;
        temp[1] = ColorIndexDiffuse;
        temp[2] = ColorIndexSpecular;
        return temp;
    }
    return null;
  }

  public gl_material(gl_material cc) {
    System.arraycopy(cc.Ambient, 0, this.Ambient, 0, 4);
    System.arraycopy(cc.Diffuse, 0, this.Diffuse, 0, 4);
    System.arraycopy(cc.Specular, 0, this.Specular, 0, 4);
    System.arraycopy(cc.Emission, 0, this.Emission, 0, 4);
    this.Shininess = cc.Shininess;
    this.ColorIndexAmbient = cc.ColorIndexAmbient;
    this.ColorIndexDiffuse = cc.ColorIndexDiffuse;
    this.ColorIndexSpecular = cc.ColorIndexSpecular;
  }

  public gl_material() {};

}
