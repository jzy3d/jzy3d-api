/*
 * @(#)gl_current.java 0.3 06/11/22
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999-2006 Robin Bing-Yu Chen (robin@ntu.edu.tw)
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

// import jgl.context.gl_list_item;
import jgl.context.gl_util;

/**
 * gl_current is the current values and associated data class of jGL 2.4.
 *
 * @version 0.3, 22 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_current {

  /** GL_CURRENT_COLOR: Current Color */
  public float Color[] = {1, 1, 1, 1};

  /* The color in int for Java format */
  public int IntColor = 0xffffffff;

  /** GL_CURRENT_INDEX: Current color index */
  public int Index = 1;

  /* Current vertex, not in the group */
  public float Vertex[] = {0, 0, 0, 0};

  /** GL_CURRENT_TEXTURE_COORDS: Current texture coordinates */
  public float TexCoord[] = {0, 0, 0, 1};

  /** GL_CURRENT_NORMAL: Current normal */
  public float Normal[] = {0, 0, 1};

  /** GL_CURRENT_RASTER_POSITION: Current raster position */
  public float RasterPos[] = {0, 0, 0, 1};

  /** GL_CURRENT_RASTER_DISTANCE: Current raster distance */
  public float RasterDistance = 1;

  /** GL_CURRENT_RASTER_COLOR: Color associated with raster position */
  public float RasterColor[] = {1, 1, 1, 1};

  /** GL_CURRENT_RASTER_INDEX: Color index associated with raster position */
  public float RasterIndex = 1;

  /**
   * GL_CURRENT_RASTER_TEXTURE_COORDS: Texture coordinates associated with raster position
   */
  public float RasterTexCoord[] = {0, 0, 0, 1};

  /** GL_CURRENT_RASTER_POSITION_VALID: Raster position valid bit */
  public boolean RasterPosValid = true;

  /** GL_EDGE_FLAG: Edge flag */
  public boolean EdgeFlag = true;

  public void set_color(float r, float g, float b, float a) {
    Color[0] = r;
    Color[1] = g;
    Color[2] = b;
    Color[3] = a;
    // IntColor = 0xff000000 | ((int)(r * (float)255.0)) << 16 |
    // IntColor = ((int)(a * (float)255.0)) << 24 |
    // ((int)(r * (float)255.0)) << 16 |
    // ((int)(g * (float)255.0)) << 8 |
    // ((int)(b * (float)255.0));
    IntColor = gl_util.RGBAtoI(r, g, b, a);
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [ 2];
   * AttribItem.IntPtr = new int [ 2]; AttribItem.FloatPtr = new float [25];
   * 
   * System.arraycopy(Color, 0, AttribItem.FloatPtr, 0, 4); AttribItem.IntPtr [ 0] = IntColor;
   * AttribItem.IntPtr [ 1] = Index; System.arraycopy(TexCoord, 0, AttribItem.FloatPtr, 4, 4);
   * System.arraycopy(Normal, 0, AttribItem.FloatPtr, 8, 3); System.arraycopy(RasterPos, 0,
   * AttribItem.FloatPtr, 11, 4); AttribItem.FloatPtr [15] = RasterDistance;
   * System.arraycopy(RasterColor, 0, AttribItem.FloatPtr, 16, 4); AttribItem.FloatPtr [20] =
   * RasterIndex; System.arraycopy(RasterTexCoord, 0, AttribItem.FloatPtr, 21, 4);
   * AttribItem.BoolPtr [ 0] = RasterPosValid; AttribItem.BoolPtr [ 1] = EdgeFlag; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { System.arraycopy(AttribItem.FloatPtr, 0,
   * Color, 0, 4); IntColor = AttribItem.IntPtr [ 0]; Index = AttribItem.IntPtr [ 1];
   * System.arraycopy(AttribItem.FloatPtr, 4, TexCoord, 0, 4); System.arraycopy(AttribItem.FloatPtr,
   * 8, Normal, 0, 3); System.arraycopy(AttribItem.FloatPtr, 11, RasterPos, 0, 4); RasterDistance =
   * AttribItem.FloatPtr [15]; System.arraycopy(AttribItem.FloatPtr, 16, RasterColor, 0, 4);
   * RasterIndex = AttribItem.FloatPtr [20]; System.arraycopy(AttribItem.FloatPtr, 21,
   * RasterTexCoord, 0, 4); RasterPosValid = AttribItem.BoolPtr [ 0]; EdgeFlag = AttribItem.BoolPtr
   * [ 1]; }
   */

  public gl_current(gl_current cc) {
    System.arraycopy(cc.Color, 0, this.Color, 0, 4);
    this.IntColor = cc.IntColor;
    this.Index = cc.Index;
    System.arraycopy(cc.TexCoord, 0, this.TexCoord, 0, 4);
    System.arraycopy(cc.Normal, 0, this.Normal, 0, 3);
    System.arraycopy(cc.RasterPos, 0, this.RasterPos, 0, 4);
    this.RasterDistance = cc.RasterDistance;
    System.arraycopy(cc.RasterColor, 0, this.RasterColor, 0, 4);
    this.RasterIndex = cc.RasterIndex;
    System.arraycopy(cc.RasterTexCoord, 0, this.RasterTexCoord, 0, 4);
    this.RasterPosValid = cc.RasterPosValid;
    this.EdgeFlag = cc.EdgeFlag;
  }

  public gl_current() {};

}
