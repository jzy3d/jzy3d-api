/*
 * @(#)gle_vertex.java 0.1 02/12/30
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

import jgl.context.gl_vertex;

/**
 * gle_vertex is the extension Vertex class of jGL 2.5.
 *
 * @version 0.1, 30 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_vertex extends gl_vertex {

  public float Normal[]; /* Vertex Normal */

  public gle_vertex(gl_vertex v) {
    Vertex = v.Vertex;
    Color = v.Color;
    TexCoord = v.TexCoord;
  }

}
