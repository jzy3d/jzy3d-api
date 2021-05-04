/*
 * @(#)gl_eval_map1.java 0.2 99/12/16
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

package jgl.context.attrib.eval;

/**
 * gl_eval_map1 is the evaluator 1D map class of JavaGL 2.1.
 *
 * @version 0.2, 16 Dec 1999
 * @author Robin Bing-Yu Chen
 */

public class gl_eval_map1 {

  /** GL_ORDER: 1D map order */
  public int Order = 1;

  /** GL_COEFF: 1D control points */
  public float Points[][];

  /** GL_DOMAIN: 1D domain endpoints */
  public float U1, U2;

  public gl_eval_map1(float u1, float u2, int order) {
    U1 = u1;
    U2 = u2;
    Order = order;
  }

}
