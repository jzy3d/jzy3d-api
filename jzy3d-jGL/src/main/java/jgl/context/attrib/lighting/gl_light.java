/*
 * @(#)gl_light.java 0.3 01/03/15
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
import jgl.context.gl_util;

/**
 * gl_light is the lighting light class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_light {

  /** GL_AMBIENT: Ambient intensity of light i */
  public float Ambient[] = {0, 0, 0, 1};

  /** GL_DIFFUSE: Diffuse intensity of light i */
  public float Diffuse[] = {0, 0, 0, 1};

  /** GL_SPECULAR: Specular intensity of light i */
  public float Specular[] = {0, 0, 0, 1};

  /** GL_POSITION: Position of light i */
  public float Position[] = {0, 0, 1, 0};

  /* unit vector from origin to pos */
  public float NormPosition[] = {0, 0, 1, 0};

  /** GL_CONSTANT_ATTENUATION: Constant attenuation factor */
  public float ConstantAttenuation = 1;

  /** GL_LINEAR_ATTENUATION: Linear attenuation factor */
  public float LinearAttenuation = 0;

  /** GL_QUADRATIC_ATTENUATION: Quadratic attenuation factor */
  public float QuadraticAttenuation = 0;

  /** GL_SPOT_DIRECTION: Spotlight direction of light i */
  public float SpotDirection[] = {0, 0, -1};

  /** GL_SPOT_EXPONENT: Spotlight exponent of light i */
  public float SpotExponent = 0;

  /** GL_SPOT_CUTOFF: Spotlight angle of light i */
  public float SpotCutoff = 180;

  /** GL_LIGHTi: Ture if light i enabled, also in gl_enable */
  public boolean Enable = false;

  public void set_light(int pname, float params[]) {
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
      case GL.GL_POSITION:
        Position = params;
        System.arraycopy(Position, 0, NormPosition, 0, 3);
        gl_util.normalize(NormPosition);
        break;
      case GL.GL_SPOT_DIRECTION:
        SpotDirection = params;
        break;
      case GL.GL_SPOT_EXPONENT:
        SpotExponent = params[0];
        break;
      case GL.GL_SPOT_CUTOFF:
        SpotCutoff = params[0];
        break;
      case GL.GL_CONSTANT_ATTENUATION:
        ConstantAttenuation = params[0];
        break;
      case GL.GL_LINEAR_ATTENUATION:
        LinearAttenuation = params[0];
        break;
      case GL.GL_QUADRATIC_ATTENUATION:
        QuadraticAttenuation = params[0];
        break;
      default:
        break;
    }
  }

  public float[] get_light(int pname) {
    float temp[] = new float[1];
    switch (pname) {
      case GL.GL_AMBIENT:
        return Ambient;
      case GL.GL_DIFFUSE:
        return Diffuse;
      case GL.GL_SPECULAR:
        return Specular;
      case GL.GL_POSITION:
        return Position;
      case GL.GL_SPOT_DIRECTION:
        return SpotDirection;
      case GL.GL_SPOT_EXPONENT:
        temp[0] = SpotExponent;
        break;
      case GL.GL_SPOT_CUTOFF:
        temp[0] = SpotCutoff;
        break;
      case GL.GL_CONSTANT_ATTENUATION:
        temp[0] = ConstantAttenuation;
        break;
      case GL.GL_LINEAR_ATTENUATION:
        temp[0] = LinearAttenuation;
        break;
      case GL.GL_QUADRATIC_ATTENUATION:
        temp[0] = QuadraticAttenuation;
        break;
      default:
        return null;
    }
    return temp;
  }

  public gl_light(gl_light cc) {
    System.arraycopy(cc.Ambient, 0, this.Ambient, 0, 4);
    System.arraycopy(cc.Diffuse, 0, this.Diffuse, 0, 4);
    System.arraycopy(cc.Specular, 0, this.Specular, 0, 4);
    System.arraycopy(cc.Position, 0, this.Position, 0, 4);
    System.arraycopy(cc.NormPosition, 0, this.NormPosition, 0, 4);
    this.ConstantAttenuation = cc.ConstantAttenuation;
    this.LinearAttenuation = cc.LinearAttenuation;
    this.QuadraticAttenuation = cc.QuadraticAttenuation;
    System.arraycopy(cc.SpotDirection, 0, this.SpotDirection, 0, 3);
    this.SpotExponent = cc.SpotExponent;
    this.SpotCutoff = cc.SpotCutoff;
    this.Enable = cc.Enable;
  }

  public gl_light() {};

}
