/*
 * @(#)gle_pointer.java 0.1 02/12/30
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

import jgl.context.gl_pointer;
import jgl.context.render.gl_depth;
import jgl.context.render.gl_render;
import jgl.context.render.pixel.gl_render_point;
import jgl.gle.geometry.gle_phong_geo;
import jgl.gle.geometry.gle_phong_z_geo;
import jgl.gle.render.gle_phong;
import jgl.gle.render.gle_phong_z;

/**
 * gle_pointer is the class points to all current extension instants of jGL 2.5.
 *
 * @version 0.1, 30 Dec 2002
 * @author Robin Bing-Yu Chen
 */

public class gle_pointer extends gl_pointer {

  private int gle_status = 0; // default state

  /**
   *
   * Current Status
   *
   * Status --------------- 0 default 1 Phong 2 Bump 3 Environment 4 Solid
   *
   * where ? means independent, - means ignore.
   */

  /*
   * public void gl_clipping (boolean state) { if ((gle_status == 0) || !state) { super.gl_clipping
   * (state); } else { if ((status & 0x00000001) == 0) { clipping = new gl_cp_phong_clipping (CC);
   * status |= 0x00000001; } } }
   */

  public void gl_depth(boolean state) {
    if (gle_status == 0) {
      super.gl_depth(state);
      return;
    }

    if (state) {
      if ((status & 0x00000002) == 0) { // test for depth
        if (gle_status == 1) { // test for phong shading
          geometry = new gle_phong_z_geo((gle_context) CC, this);
          if ((status & 0x00000020) == 0) {
            render = new gle_phong_z((gle_context) CC);
          }
        }
        line = new gl_depth(CC);
        line.set_pixel(basic_pixel);
        status |= 0x00000002;
      }
    } else {
      if ((status & 0x00000002) != 0) {
        if (gle_status == 1) { // test for phong shading
          geometry = new gle_phong_geo((gle_context) CC, this);
          if ((status & 0x00000020) == 0) {
            render = new gle_phong((gle_context) CC);
          }
        }
        line = new gl_render(CC);
        line.set_pixel(basic_pixel);
        status &= 0xfffffffd;
      }
    }
  }

  public void gl_smooth(boolean state) {
    super.gl_smooth(state);
    gle_status = 0;
  }

  public void gl_texture(boolean state) {
    if ((gle_status != 0) || state) {
      super.gl_texture(state);
      return;
    }

    if ((status & 0x00000008) != 0) {
      if (gle_status == 1) {
        if ((status & 0x00000002) != 0) {
          if ((status & 0x00000020) == 0) {
            render = new gle_phong_z((gle_context) CC);
          }
          geometry = new gle_phong_z_geo((gle_context) CC, this);
        } else {
          if ((status & 0x00000020) == 0) {
            render = new gle_phong((gle_context) CC);
          }
          geometry = new gle_phong_geo((gle_context) CC, this);
        }
        // if ((status & 0x00000001) != 0) {
        // clipping = new gl_cp_color_clipping (CC);
        // }
        // vp_clipping = new gl_vp_color_clipping (CC);
      }
      status &= 0xfffffff7;
    }

  }

  public void gl_select(boolean state) {
    if ((gle_status != 0) || (((status & 0x00000008) != 0)) || state) {
      super.gl_select(state);
      return;
    }

    if ((status & 0x00000020) != 0) {
      if (gle_status == 1) {
        if ((status & 0x00000002) != 0) {
          render = new gle_phong_z((gle_context) CC);
          geometry = new gle_phong_z_geo((gle_context) CC, this);
        } else {
          render = new gle_phong((gle_context) CC);
          geometry = new gle_phong_geo((gle_context) CC, this);
        }
        // if ((status & 0x00000001) != 0) {
        // clipping = new gl_cp_color_clipping (CC);
        // }
        // vp_clipping = new gl_vp_color_clipping (CC);
      }

      if ((status & 0x00000010) != 0) {
        pixel = new gl_render_point(CC);
      } else {
        pixel = basic_pixel;
      }
      status &= 0xffffffdf;
    }

  }

  public void gl_phong() {
    if (gle_status != 1) {
      if ((status & 0x00000002) != 0) {
        render = new gle_phong_z((gle_context) CC);
        geometry = new gle_phong_z_geo((gle_context) CC, this);
      } else {
        render = new gle_phong((gle_context) CC);
        geometry = new gle_phong_geo((gle_context) CC, this);
      }
      // clipping = new gl_cp_phong_clipping ((gle_context)CC);
      // vp_clipping = new gl_vp_phong_clipping ((gle_context)CC);
      gle_status = 1;
    }
  }

  public gle_pointer(gle_context cc) {
    super(cc);
  }

}
