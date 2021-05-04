/*
 * @(#)gl_lighting.java 0.3 01/03/15
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

package jgl.context.attrib;

import jgl.GL;
import jgl.context.gl_util;
import jgl.context.attrib.lighting.gl_light;
import jgl.context.attrib.lighting.gl_material;

/**
 * gl_lighting is the lighting class of jGL 2.4.
 *
 * @version 0.3, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_lighting {

  /** GL_SHADE_MODEL: glShadeModel() setting */
  public int ShadeModel = GL.GL_SMOOTH;

  /** GL_LIGHTING: True is lighting is enabled, also in gl_enable */
  public boolean Enable = false;

  /** GL_COLOR_MATERIAL: True if color tracking is enabled */
  public boolean ColorMaterial = false;

  /** GL_COLOR_MATERIAL_PARAMETER: Material properties tracking current color */
  public int ColorMaterialParameter = GL.GL_AMBIENT_AND_DIFFUSE;

  /** GL_COLOR_MATERIAL_FACE: Face(s) affected by color tracking */
  public int ColorMaterialFace = 2; // GL.GL_FRONT_AND_BACK;

  /** GL_LIGHT_MODEL_AMBIENT: Ambient scene color */
  public float LightModelAmbient[] = {0.2f, 0.2f, 0.2f, 1};

  /** GL_LIGHT_MODEL_LOCAL_VIEWER: Viewer is local */
  public boolean LightModelLocalViewer = false;

  /** GL_LIGHT_MODEL_TWO_SIDE: Use two-sided lighting */
  public boolean LightModelTwoSide = false;

  /** For material color */
  public gl_material Material[]; /* Material 0 = front, 1 = back */

  /** For light i */
  public gl_light Light[] = new gl_light[8]; // MAX_LIGHTS = 8

  private static final float RAD2DEG = (float) (180.0 / Math.PI);



  public gl_lighting(gl_lighting cc) {
    this.ShadeModel = GL.GL_SMOOTH;
    this.Enable = false;
    this.ColorMaterial = false;
    this.ColorMaterialParameter = GL.GL_AMBIENT_AND_DIFFUSE;
    this.ColorMaterialFace = 2; // GL.GL_FRONT_AND_BACK;
    System.arraycopy(cc.LightModelAmbient, 0, this.LightModelAmbient, 0, 4);
    this.LightModelLocalViewer = false;
    this.LightModelTwoSide = false;
    this.Material = new gl_material[2];
    this.Material[0] = new gl_material(cc.Material[0]);
    this.Material[1] = new gl_material(cc.Material[1]);
    for (int i = 0; i < 8; i++) {
      this.Light[i] = new gl_light(cc.Light[i]);
    }
  }

  public gl_lighting() {
    for (int i = 0; i < 8; i++) {
      Light[i] = new gl_light();
    }
    Light[0].Diffuse[0] = 1;
    Light[0].Diffuse[1] = 1;
    Light[0].Diffuse[2] = 1;
    Light[0].Diffuse[3] = 1;
    Light[0].Specular[0] = 1;
    Light[0].Specular[1] = 1;
    Light[0].Specular[2] = 1;
    Light[0].Specular[3] = 1;
    Material = new gl_material[2];
    Material[0] = new gl_material();
    Material[1] = new gl_material();
  }

  public void set_material(int face, int pname, float params[]) {
    Material[face].set_material(pname, params);
  }

  public float[] get_material(int face, int pname) {
    return Material[face].get_material(pname);
  }

  public void set_color_material(int face, int mode) {
    switch (face) {
      case GL.GL_FRONT:
        ColorMaterialFace = 0;
        break;
      case GL.GL_BACK:
        ColorMaterialFace = 1;
        break;
      case GL.GL_FRONT_AND_BACK:
        ColorMaterialFace = 2;
        break;
      default:
        break;
    }
    ColorMaterialParameter = mode;
  }

  public void set_color(float r, float g, float b, float a) {
    if (ColorMaterial) {
      float temp[] = {r, g, b, a};
      if (ColorMaterialFace == 2) {
        set_material(0, ColorMaterialParameter, temp);
        set_material(1, ColorMaterialParameter, temp);
      } else {
        set_material(ColorMaterialFace, ColorMaterialParameter, temp);
      }
    }
  }

  public void set_light_model(int pname, float params[]) {
    switch (pname) {
      case GL.GL_LIGHT_MODEL_AMBIENT:
        LightModelAmbient = params;
        break;
      case GL.GL_LIGHT_MODEL_LOCAL_VIEWER:
        if (params[0] == 0) {
          LightModelLocalViewer = false;
        } else {
          LightModelLocalViewer = true;
        }
        break;
      case GL.GL_LIGHT_MODEL_TWO_SIDE:
        if (params[0] == 0) {
          LightModelTwoSide = false;
        } else {
          LightModelTwoSide = true;
        }
        break;
      default:
        break;
    }
  }

  public void set_light(int light, int pname, float params[]) {
    Light[light].set_light(pname, params);
  }

  public float[] get_light(int light, int pname) {
    return Light[light].get_light(pname);
  }

  public int color_vertex(float vertex[], float normal[]) {
    float R, G, B, A;
    float norm[] = new float[3];
    float ambient[] = new float[3];
    float diffuse[] = new float[3];
    float specular[] = new float[3];
    float l[] = new float[3]; // unit vector from vertex to light
    float l_dot_norm; // dot product of l and norm
    float attenuation, spotlight_effect, spec_coef;
    float d; // distance from vertex to light
    float dot, t;
    float s[] = new float[3];
    float v[] = new float[3];
    int side, i;

    if (LightModelTwoSide && normal[2] < 0)
      side = 1;
    else
      side = 0;
    // side = 0;

    gl_util.normalize(normal);

    /* Compute color contribution from global lighting */
    R = Material[side].Emission[0] + LightModelAmbient[0] * Material[side].Ambient[0];
    G = Material[side].Emission[1] + LightModelAmbient[1] * Material[side].Ambient[1];
    B = Material[side].Emission[2] + LightModelAmbient[2] * Material[side].Ambient[2];
    A = Material[side].Diffuse[0];

    if (side == 0) { // shade frontside
      norm[0] = normal[0];
      norm[1] = normal[1];
      norm[2] = normal[2];
    } else { // shade backside
      norm[0] = -normal[0];
      norm[1] = -normal[1];
      norm[2] = -normal[2];
    }

    /* Add contribution from each light source */
    for (i = 0; i < 8; i++) { // since MAX_LIGHTS = 8
      if (Light[i].Enable) {
        /* Compute ambient color */
        ambient[0] = Light[i].Ambient[0] * Material[side].Ambient[0];
        ambient[1] = Light[i].Ambient[1] * Material[side].Ambient[1];
        ambient[2] = Light[i].Ambient[2] * Material[side].Ambient[2];

        /* Compute l and attenuation */
        if (Light[i].Position[3] == 0) {
          /* Directional light */
          l[0] = Light[i].NormPosition[0];
          l[1] = Light[i].NormPosition[1];
          l[2] = Light[i].NormPosition[2];
          attenuation = 1;
        } else {
          /* Positional light */
          l[0] = Light[i].Position[0] - vertex[0];
          l[1] = Light[i].Position[1] - vertex[1];
          l[2] = Light[i].Position[2] - vertex[2];
          d = gl_util.normalize(l);
          attenuation = (float) 1.0 / (Light[i].ConstantAttenuation
              + d * (Light[i].LinearAttenuation + d * Light[i].QuadraticAttenuation));
        }
        l_dot_norm = gl_util.dot33(l, norm);

        /* Diffuse and specular terms */
        if (l_dot_norm <= 0) {
          /* Surface faces away from light, no diffuse or specular */
          R += attenuation * ambient[0];
          G += attenuation * ambient[1];
          B += attenuation * ambient[2];
          /* Done with this light */
        } else {
          /* Spotlight factor */
          if (Light[i].SpotCutoff == 180) {
            /* Not a spot light */
            spotlight_effect = 1;
          } else {
            /* v points from light to vertex */
            v[0] = -l[0];
            v[1] = -l[1];
            v[2] = -l[2];
            dot = gl_util.dot33(v, Light[i].SpotDirection);
            if (dot <= 0 || Math.acos(dot) * RAD2DEG > Light[i].SpotCutoff) {
              /* Outside of cone */
              spotlight_effect = 0;
            } else {
              spotlight_effect = (float) Math.pow(dot, Light[i].SpotExponent);
            }
          }

          /* Diffuse term */
          diffuse[0] = l_dot_norm * Light[i].Diffuse[0] * Material[side].Diffuse[0];
          diffuse[1] = l_dot_norm * Light[i].Diffuse[1] * Material[side].Diffuse[1];
          diffuse[2] = l_dot_norm * Light[i].Diffuse[2] * Material[side].Diffuse[2];

          /* Specular term */
          if (LightModelLocalViewer) {
            v[0] = vertex[0];
            v[1] = vertex[1];
            v[2] = vertex[2];
            gl_util.normalize(v);
            s[0] = l[0] - v[0];
            s[1] = l[1] - v[1];
            s[2] = l[2] - v[2];
          } else {
            s[0] = l[0];
            s[1] = l[1];
            s[2] = l[2] + 1;
          }
          dot = gl_util.dot33(s, norm);

          if (dot <= 0) {
            specular[0] = 0;
            specular[1] = 0;
            specular[2] = 0;
          } else {
            dot = dot / (float) Math.sqrt(s[0] * s[0] + s[1] * s[1] + s[2] * s[2]);
            spec_coef = (float) Math.pow(dot, Material[side].Shininess);
            if (spec_coef < 1.0e-10) {
              specular[0] = 0;
              specular[1] = 0;
              specular[2] = 0;
            } else {
              specular[0] = spec_coef * Light[i].Specular[0] * Material[side].Specular[0];
              specular[1] = spec_coef * Light[i].Specular[1] * Material[side].Specular[1];
              specular[2] = spec_coef * Light[i].Specular[2] * Material[side].Specular[2];
            }
          }
          t = attenuation * spotlight_effect;
          R += t * (ambient[0] + diffuse[0] + specular[0]);
          G += t * (ambient[1] + diffuse[1] + specular[1]);
          B += t * (ambient[2] + diffuse[2] + specular[2]);
        }
      }
    }
    if (R < 0) {
      R = 0;
    }
    if (R > 1) {
      R = 1;
    }
    if (G < 0) {
      G = 0;
    }
    if (G > 1) {
      G = 1;
    }
    if (B < 0) {
      B = 0;
    }
    if (B > 1) {
      B = 1;
    }
    return (gl_util.RGBtoI(R, G, B));
  }

}
