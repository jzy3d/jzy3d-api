/*
 * @(#)GLUquadricObj.java 0.1 96/08/30
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

package jgl.glu;

import java.lang.reflect.Method;

/**
 * GLUquadricObj is one of the GLU class of JavaGL 1.1.
 *
 * @version 0.1, 30 Aug 1996
 * @author Robin Bing-Yu Chen
 */

public class GLUquadricObj {

  public int DrawStyle; /* GLU_FILL, LINE, SILHOUETTE, or POINT */
  public int Orientation; /* GLU_INSIDE or GLU_OUTSIDE */
  public boolean TextureFlag; /* Generate texture coords? */
  public int Normals; /* GLU_NONE, GLU_FLAT, or GLU_SMOOTH */
  public Method ErrorFunc; /* Error handler callback function */

}
