/*
 * @(#)gle_phong_z_geo.java 0.1 02/12/31
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

package jgl.gle.geometry;

import jgl.gle.gle_context;
import jgl.gle.gle_pointer;

/**
 * gle_phong_z_geo is the geometry class for phong shading with depth value of jGL 2.5.
 *
 * @version 0.1, 31 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_phong_z_geo extends gle_phong_geo {

  protected void draw_point(float p[], int i) {
    CR.pixel.put_pixel((int) (p[0] + (float) 0.5), (int) (p[1] + (float) 0.5), p[2], get_color(i));
  }

  public gle_phong_z_geo(gle_context cc, gle_pointer cr) {
    super(cc, cr);
  }

}
