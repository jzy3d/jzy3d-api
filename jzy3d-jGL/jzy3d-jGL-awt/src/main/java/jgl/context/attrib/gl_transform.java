/*
 * @(#)gl_transform.java 0.2 01/03/15
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

package jgl.context.attrib;

import jgl.GL;
// import jgl.context.gl_list_item;

/**
 * gl_transform is the transformation class of jGL 2.4.
 *
 * @version 0.2, 15 Mar 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_transform {

  /** GL_MATRIX_MODE: Current matrix mode */
  public int MatrixMode = GL.GL_MODELVIEW;

  /** GL_NORMALIZE: Current normal normalization, also gl_enable */
  public boolean Normalize = false;

  /** GL_CLIP_PLANEi: User clipping plane coefficients */
  public float ClipPlane[][] =
      {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};

  /** GL_CLIP_PLANEi: ith user clipping plane enabled, also gl_enable */
  public boolean ClipEnable[] = {false, false, false, false, false, false};

  /* is any user clipping plane enabled, not in this group */
  public boolean ClippingEnable = false;

  public boolean check_clip() {
    ClippingEnable = false;
    int i = 0;
    while (i < 6 && ClippingEnable == false) {
      ClippingEnable = ClipEnable[i];
      i++;
    }
    return ClippingEnable;
  }

  public boolean clip_enable(int cap, boolean state) {
    ClipEnable[cap] = state;
    return check_clip();
  }

  /*
   * public void push_attrib (gl_list_item AttribItem) { AttribItem.BoolPtr = new boolean [8];
   * AttribItem.IntPtr = new int [1]; AttribItem.FloatPtr = new float [24];
   * 
   * AttribItem.IntPtr [0] = MatrixMode; AttribItem.BoolPtr [0] = Normalize; for (int i = 0; i < 6;
   * i++) { System.arraycopy (AttribItem.FloatPtr, i*4, ClipPlane [i], 0, 4); AttribItem.BoolPtr
   * [i+1] = ClipEnable [i]; }
   * 
   * AttribItem.BoolPtr [7] = ClippingEnable; }
   * 
   * public void pop_attrib (gl_list_item AttribItem) { MatrixMode = AttribItem.IntPtr [0];
   * Normalize = AttribItem.BoolPtr [0]; for (int i = 0; i < 6; i++) { System.arraycopy
   * (ClipPlane[i], 0, AttribItem.FloatPtr, i*4, 4); ClipEnable [i] = AttribItem.BoolPtr [i+1]; }
   * ClippingEnable = AttribItem.BoolPtr [7]; }
   */

  public gl_transform(gl_transform cc) {
    this.MatrixMode = cc.MatrixMode;
    this.Normalize = cc.Normalize;
    for (int i = 0; i < 6; i++) {
      System.arraycopy(cc.ClipPlane[i], 0, this.ClipPlane[i], 0, 4);
    }
    System.arraycopy(cc.ClipEnable, 0, this.ClipEnable, 0, 6);
    this.ClippingEnable = cc.ClippingEnable;
  }

  public gl_transform() {};

}
