/*
 * @(#)MODELPTR.java 0.1 96/09/02
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996 Robin Bing-Yu Chen
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

package jgl.glaux;

/**
 * MODELPTR is one of the AUX class of JavaGL 1.1.
 *
 * @version 0.1, 2 Sep 1996
 * @author Robin Bing-Yu Chen
 */

/** structure for each geometric object */
public class MODELPTR {

  public int list; /* display list to render object */
  public MODELPTR ptr; /* pointer to next object */
  public int numParam; /* # of parameters */
  public double params[]; /* array with parameters */

}
