/*
 * @(#)gl_pointer.java 0.3 06/11/21
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

package jgl.context;

import jgl.context.clipping.gl_clipping;
import jgl.context.clipping.gl_cp_clipping;
import jgl.context.clipping.gl_cp_color_clipping;
import jgl.context.clipping.gl_cp_lit_tex_clipping;
import jgl.context.clipping.gl_cp_tex_clipping;
import jgl.context.clipping.gl_vp_clipping;
import jgl.context.clipping.gl_vp_color_clipping;
import jgl.context.clipping.gl_vp_lit_tex_clipping;
import jgl.context.clipping.gl_vp_tex_clipping;
import jgl.context.geometry.gl_depth_geo;
import jgl.context.geometry.gl_geometry;
import jgl.context.geometry.gl_lit_tex_geo;
import jgl.context.geometry.gl_lit_tex_z_geo;
import jgl.context.geometry.gl_smooth_geo;
import jgl.context.geometry.gl_smooth_z_geo;
import jgl.context.geometry.gl_tex_geo;
import jgl.context.geometry.gl_tex_z_geo;
import jgl.context.render.gl_depth;
import jgl.context.render.gl_lit_tex;
import jgl.context.render.gl_lit_tex_z;
import jgl.context.render.gl_render;
import jgl.context.render.gl_select_render;
import jgl.context.render.gl_smooth;
import jgl.context.render.gl_smooth_z;
import jgl.context.render.gl_tex;
import jgl.context.render.gl_tex_z;
import jgl.context.render.pixel.gl_blend_pixel;
import jgl.context.render.pixel.gl_render_pixel;
import jgl.context.render.pixel.gl_render_point;
import jgl.context.render.pixel.gl_select_pixel;
import jgl.context.render.pixel.gl_stipple_line_pixel;
import jgl.context.render.pixel.gl_stipple_poly_pixel;

/**
 * gl_pointer is the class points to all current instants of jGL 2.4.
 *
 * @version 0.3, 21 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_pointer {

  protected gl_context CC;


  /**
   *
   * Current Status The status of GL about selection, point size, texturing, smooth or flat shading,
   * depth buffer, and clip-plane clipping.
   *
   * <pre>
   * Status Select Point Texture Smooth Depth Clipping
   * --------------------------------------------------- 
   * ??00?0 no no no 
   * ??00?1 no no yes 
   * 0?000? no no no no 
   * 0?001? no no no yes 
   * ??01?0 no yes no 
   * ??01?1 no yes yes 
   * 0?010? no no yes no 
   * 0?011? no no yes yes 
   * ??10?0 yes no no 
   * ??10?1 yes no yes 
   * 0?100? no yes no no 
   * 0?101? no yes no yes 
   * ??11?0 yes yes no 
   * ??11?1 yes yes yes 
   * 0?110? no yes yes no 
   * 0?111? no yes yes yes 
   * 00???? no no 
   * 01???? no yes 
   * 1----? yes 
   * --- --- --- ---
   *
   * where ? means independent, - means ignore.
   * 
   * </pre>
   */
  protected int status = 4; // default state "000100"

  // set to default state, no depth, smooth shading, no texturing, no clipping,
  // no selection, no stipple
  public gl_clipping clipping;
  public gl_vp_clipping vp_clipping;

  public gl_geometry geometry;
  public gl_render render;
  public gl_render line;

  public gl_render_pixel basic_pixel;
  public gl_render_pixel pixel;
  public gl_render_pixel line_pixel;
  public gl_render_pixel poly_pixel;

  public gl_pointer(gl_context cc) {
    CC = cc;
    // clipping = new gl_nf_color_clipping (CC);
    clipping = null;
    vp_clipping = new gl_vp_color_clipping(CC);

    geometry = new gl_smooth_geo(CC, this);
    render = new gl_smooth(CC);
    //render = new gl_depth(CC);
    line = new gl_render(CC);
    //line = new gl_smooth(CC);

    basic_pixel = new gl_render_pixel(CC);
    pixel = basic_pixel;
    line_pixel = basic_pixel;
    poly_pixel = basic_pixel;
    line.set_pixel(basic_pixel);
  }

  public void gl_stipple_line(boolean state) {
    if (state) {
      line_pixel = new gl_stipple_line_pixel(CC, this);
    } else {
      line_pixel = basic_pixel;
    }
  }

  public void gl_stipple_poly(boolean state) {
    if (state) {
      poly_pixel = new gl_stipple_poly_pixel(CC);
    } else {
      poly_pixel = basic_pixel;
    }
  }

  public void gl_clipping(boolean state) {
    if (state) {
      if ((status & 0x00000001) == 0) {
        if ((status & 0x00000008) != 0) {
          if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
            clipping = new gl_cp_lit_tex_clipping(CC);
          } else {
            clipping = new gl_cp_tex_clipping(CC);
          }
          // clipping = new gl_cp_tex_clipping (CC);
        } else {
          if ((status & 0x00000004) != 0) {
            clipping = new gl_cp_color_clipping(CC);
          } else {
            clipping = new gl_cp_clipping(CC);
          }
        }
        status |= 0x00000001;
      }
    } else {
      if ((status & 0x00000001) != 0) {
        /*
         * if ((status & 0x00000008) != 0) { clipping = new gl_nf_tex_clipping (CC); } else { if
         * ((status & 0x00000004) != 0) { clipping = new gl_nf_color_clipping (CC); } else {
         * clipping = new gl_nf_clipping (CC); } }
         */
        clipping = null;
        status &= 0xfffffffe;
      }
    }
  }

  public void gl_depth(boolean state) {
    if (state) {
      if ((status & 0x00000002) == 0) { // test for depth
        if ((status & 0x00000008) != 0) { // test for texture
          if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
            geometry = new gl_lit_tex_z_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex_z(CC);
            }
          } else {
            geometry = new gl_tex_z_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_tex_z(CC);
            }
          }
          /*
           * geometry = new gl_tex_z_geo (CC, this); if ((status & 0x00000020) == 0) { // test for
           * select render = new gl_tex_z (CC); }
           */
        } else {
          if ((status & 0x00000004) != 0) { // test for smooth
            geometry = new gl_smooth_z_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth_z(CC);
            }
          } else {
            geometry = new gl_depth_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_depth(CC);
            }
          }
        }
        line = new gl_depth(CC);
        line.set_pixel(basic_pixel);
        status |= 0x00000002;
      }
    } else {
      if ((status & 0x00000002) != 0) {
        if ((status & 0x00000008) != 0) {
          if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
            geometry = new gl_lit_tex_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex(CC);
            }
          } else {
            geometry = new gl_tex_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_tex(CC);
            }
          }
          /*
           * geometry = new gl_tex_geo (CC, this); if ((status & 0x00000020) == 0) { render = new
           * gl_tex (CC); }
           */
        } else {
          if ((status & 0x00000004) != 0) {
            geometry = new gl_smooth_geo(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth(CC);
            }
          } else {
            geometry = new gl_geometry(CC, this);
            if ((status & 0x00000020) == 0) {
              render = new gl_render(CC);
            }
          }
        }
        line = new gl_render(CC);
        line.set_pixel(basic_pixel);
        status &= 0xfffffffd;
      }
    }
  }

  public void gl_smooth(boolean state) {
    if (state) {
      if ((status & 0x00000004) == 0) {
        if ((status & 0x00000008) == 0) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth_z(CC);
            }
            geometry = new gl_smooth_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth(CC);
            }
            geometry = new gl_smooth_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_color_clipping(CC);
          }
          /*
           * else { clipping = new gl_nf_color_clipping (CC); }
           */
          vp_clipping = new gl_vp_color_clipping(CC);
        } else if (CC.Lighting.Enable) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex_z(CC);
            }
            geometry = new gl_lit_tex_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex(CC);
            }
            geometry = new gl_lit_tex_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_lit_tex_clipping(CC);
          }
          vp_clipping = new gl_vp_lit_tex_clipping(CC);
        }
        status |= 0x00000004;
      }
    } else {
      if ((status & 0x00000004) != 0) {
        if ((status & 0x00000008) == 0) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_depth(CC);
            }
            geometry = new gl_depth_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_render(CC);
            }
            geometry = new gl_geometry(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_clipping(CC);
          }
          /*
           * else { clipping = new gl_nf_clipping (CC); }
           */
          vp_clipping = new gl_vp_clipping(CC);
        } else {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_tex_z(CC);
            }
            geometry = new gl_tex_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_tex(CC);
            }
            geometry = new gl_tex_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_tex_clipping(CC);
          }
          vp_clipping = new gl_vp_tex_clipping(CC);
        }
        status &= 0xfffffffb;
      }
    }
  }

  public void gl_texture(boolean state) {
    if (state) {
      if ((status & 0x00000008) == 0) {
        if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex_z(CC);
            }
            geometry = new gl_lit_tex_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex(CC);
            }
            geometry = new gl_lit_tex_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_lit_tex_clipping(CC);
          }
          vp_clipping = new gl_vp_lit_tex_clipping(CC);
        } else {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_tex_z(CC);
            }
            geometry = new gl_tex_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_tex(CC);
            }
            geometry = new gl_tex_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_tex_clipping(CC);
          }
          vp_clipping = new gl_vp_tex_clipping(CC);
        }
        /*
         * if ((status & 0x00000002) != 0) { if ((status & 0x00000020) == 0) { render = new gl_tex_z
         * (CC); } geometry = new gl_tex_z_geo (CC, this); } else { if ((status & 0x00000020) == 0)
         * { render = new gl_tex (CC); } geometry = new gl_tex_geo (CC, this); } if ((status &
         * 0x00000001) != 0) { clipping = new gl_cp_tex_clipping (CC); }
         */
        /*
         * else { clipping = new gl_nf_tex_clipping (CC); } vp_clipping = new gl_vp_tex_clipping
         * (CC);
         */
        status |= 0x00000008;
      }
    } else {
      if ((status & 0x00000008) != 0) {
        if ((status & 0x00000004) != 0) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth_z(CC);
            }
            geometry = new gl_smooth_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_smooth(CC);
            }
            geometry = new gl_smooth_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_color_clipping(CC);
          }
          /*
           * else { clipping = new gl_nf_color_clipping (CC); }
           */
          vp_clipping = new gl_vp_color_clipping(CC);
        } else {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_depth(CC);
            }
            geometry = new gl_depth_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_render(CC);
            }
            geometry = new gl_geometry(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_clipping(CC);
          }
          /*
           * else { clipping = new gl_nf_clipping (CC); }
           */
          vp_clipping = new gl_vp_clipping(CC);
        }
        status &= 0xfffffff7;
      }
    }
  }

  public void gl_point_size(boolean state) {
    if (state) {
      if ((status & 0x00000010) == 0) {
        if ((status & 0x00000020) == 0) {
          pixel = new gl_render_point(CC);
        }
        status |= 0x00000010;
      }
    } else {
      if ((status & 0x00000010) != 0) {
        if ((status & 0x00000020) == 0) {
          pixel = basic_pixel;
        }
        status &= 0xffffffef;
      }
    }
  }

  public void gl_select(boolean state) {
    if (state) {
      if ((status & 0x00000020) == 0) {
        render = new gl_select_render(CC);
        pixel = new gl_select_pixel(CC);
        status |= 0x00000020;
      }
    } else {
      if ((status & 0x00000020) != 0) {
        if ((status & 0x00000008) != 0) {
          if ((status & 0x00000002) != 0) {
            if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
              render = new gl_lit_tex_z(CC);
            } else {
              render = new gl_tex_z(CC);
            }
          } else {
            if (CC.Lighting.Enable && ((status & 0x00000004) != 0)) {
              render = new gl_lit_tex(CC);
            } else {
              render = new gl_tex(CC);
            }
          }
          /*
           * if ((status & 0x00000002) != 0) { render = new gl_tex_z (CC); } else { render = new
           * gl_tex (CC); }
           */
        } else {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000004) != 0) {
              render = new gl_smooth_z(CC);
            } else {
              render = new gl_depth(CC);
            }
          } else {
            if ((status & 0x00000004) != 0) {
              render = new gl_smooth(CC);
            } else {
              render = new gl_render(CC);
            }
          }
        }
        if ((status & 0x00000010) != 0) {
          pixel = new gl_render_point(CC);
        } else {
          pixel = basic_pixel;
        }
        status &= 0xffffffdf;
      }
    }
  }

  public void gl_lighting(boolean state) {
    if (state) {
      if ((status & 0x00000008) != 0) {
        if ((status & 0x00000004) != 0) {
          if ((status & 0x00000002) != 0) {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex_z(CC);
            }
            geometry = new gl_lit_tex_z_geo(CC, this);
          } else {
            if ((status & 0x00000020) == 0) {
              render = new gl_lit_tex(CC);
            }
            geometry = new gl_lit_tex_geo(CC, this);
          }
          if ((status & 0x00000001) != 0) {
            clipping = new gl_cp_lit_tex_clipping(CC);
          }
          vp_clipping = new gl_vp_lit_tex_clipping(CC);
        }
      }
    } else {
      if ((status & 0x00000008) != 0) {
        if ((status & 0x00000002) != 0) {
          if ((status & 0x00000020) == 0) {
            render = new gl_tex_z(CC);
          }
          geometry = new gl_tex_z_geo(CC, this);
        } else {
          if ((status & 0x00000020) == 0) {
            render = new gl_tex(CC);
          }
          geometry = new gl_tex_geo(CC, this);
        }
        if ((status & 0x00000001) != 0) {
          clipping = new gl_cp_tex_clipping(CC);
        }
        vp_clipping = new gl_vp_tex_clipping(CC);
      }
    }
  }

  public void gl_blending(boolean state) {
    if (state) {
      line_pixel = new gl_blend_pixel(CC);
      poly_pixel = line_pixel;
      line.set_pixel(line_pixel);
    } else {
      line_pixel = basic_pixel;
      poly_pixel = basic_pixel;
      line.set_pixel(basic_pixel);
    }
  }
}
