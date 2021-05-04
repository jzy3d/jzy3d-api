/*
 * @(#)gl_vertex.java 0.2 99/07/11
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-1999 Robin Bing-Yu Chen
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

package jgl.context;

/**
 * gl_vertex is the Vertex class of JavaGL 2.0.
 *
 * @version 0.2, 11 Jul 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_vertex {

  public float Vertex[] = new float[4]; /* Vertex Position */
  public int Color[]; /* Vertex Color */
  public float TexCoord[]; /* Texture Coord */

  public gl_vertex() {}
}
