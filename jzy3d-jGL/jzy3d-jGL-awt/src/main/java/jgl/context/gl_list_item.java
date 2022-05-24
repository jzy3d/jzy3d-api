/*
 * @(#)gl_list_item.java 0.2 01/02/10
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001 Robin Bing-Yu Chen
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
 * gl_list_item is the List Item class of jGL 2.3.
 *
 * @version 0.2, 10 Feb 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_list_item {

  public int NodeKind;
  public int IntPtr[];
  public float FloatPtr[];
  public boolean BoolPtr[];
  public Object ObjPtr;

  public gl_list_item(int node) {
    NodeKind = node;
  }

}
