/*
 * @(#)gl_object.java 0.7 01/12/03
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2001 Robin Bing-Yu Chen
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

import jgl.GL;
import jgl.context.attrib.gl_eval;
import jgl.context.attrib.eval.gl_eval_map1;
import jgl.context.attrib.eval.gl_eval_map2;

/**
 * gl_object is the super class of context and list of jGL 2.4.
 *
 * @version 0.7, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public abstract class gl_object {

  /**
   * If true, will notify of any OpenGL error according to the configuration of
   * {@link gl_object#throwExceptionOnGLError}
   */
  protected final boolean debug = true;
  /**
   * If true, will trigger a {@link RuntimeException} upon any OpenGL error, otherwise send message
   * to System.err.println(). In case {@link gl_object#debug} is set to false, all OpenGL errors are
   * muted.
   */
  protected boolean throwExceptionOnGLError = true;

  public static final float IDENTITY[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

  /** Miscellaneous */
  /** GL_RENDER_MODE: glRenderMode() setting */
  public int RenderMode;
  public int Mode; /* glBegin primitive or GL_BITMAP */

  public boolean DBflag; /* Is frame buffer double buffered? */
  public int ErrorValue;

  public gl_eval Eval;

  /** Protected Member Functions */
  protected float[] get_rotate(float angle, float x, float y, float z) {
    float a[] = new float[16];
    float mag, s, c;
    float xx, yy, zz, xy, yz, zx, xs, ys, zs, one_c;

    s = (float) Math.sin(angle * (Math.PI / 180.0f));
    c = (float) Math.cos(angle * (Math.PI / 180.0f));

    mag = (float) Math.sqrt(x * x + y * y + z * z);
    if (mag == 0) {
      return null;
    }

    x /= mag;
    y /= mag;
    z /= mag;

    xx = x * x;
    yy = y * y;
    zz = z * z;
    xy = x * y;
    yz = y * z;
    zx = z * x;
    xs = x * s;
    ys = y * s;
    zs = z * s;
    one_c = 1.0f - c;

    a[0] = one_c * xx + c;
    a[1] = one_c * xy + zs;
    a[2] = one_c * zx - ys;
    a[4] = one_c * xy - zs;
    a[5] = one_c * yy + c;
    a[6] = one_c * yz + xs;
    a[8] = one_c * zx + ys;
    a[9] = one_c * yz - xs;
    a[10] = one_c * zz + c;
    a[15] = 1;

    return a;
  }

  protected float[] get_scale(float x, float y, float z) {
    float a[] = new float[16];
    a[0] = x;
    a[5] = y;
    a[10] = z;
    a[15] = 1;
    return a;
  }

  protected float[] get_translate(float x, float y, float z) {
    float a[] = new float[16];
    a[0] = 1;
    a[12] = x;
    a[5] = 1;
    a[13] = y;
    a[10] = 1;
    a[14] = z;
    a[15] = 1;
    return a;
  }

  /** Public Member Functions */
  public void gl_error(int error, String s) {

    if (debug) {
      StringBuffer sb = new StringBuffer();
      sb.append("jGL Error (");

      switch (error) {
        case GL.GL_NO_ERROR:
          sb.append("GL_NO_ERROR");
          break;
        case GL.GL_INVALID_VALUE:
          sb.append("GL_INVALID_VALUE");
          break;
        case GL.GL_INVALID_ENUM:
          sb.append("GL_INVALID_ENUM");
          break;
        case GL.GL_INVALID_OPERATION:
          sb.append("GL_INVALID_OPERATION");
          break;
        case GL.GL_STACK_OVERFLOW:
          sb.append("GL_STACK_OVERFLOW");
          break;
        case GL.GL_STACK_UNDERFLOW:
          sb.append("GL_STACK_UNDERFLOW");
          break;
        case GL.GL_OUT_OF_MEMORY:
          sb.append("GL_OUT_OF_MEMORY");
          break;
        default:
          sb.append("unknown");
          break;
      }
      // System.out.println("): " + s);
      sb.append("): " + s);

      if (throwExceptionOnGLError) {
        throw new RuntimeException(sb.toString());
      } else {
        System.err.println(sb.toString());
      }
    }
    if (ErrorValue == GL.GL_NO_ERROR) {
      ErrorValue = error;
    }
  }
  
  public void setThrowExceptionOnGLError(boolean status) {
    this.throwExceptionOnGLError = status;
  }

  public boolean getThrowExceptionOnGLError() {
    return throwExceptionOnGLError;
  }


  public void gl_eval_coord_1(float u) {
    gl_eval_map1 map;
    float q[];

    if (Eval.Map1Color4Enable) {
      map = Eval.Map1Color4;
      q = Eval.gl_eval_coord_1(map, 4, u);
      gl_color(q[0], q[1], q[2], q[3]);
    }

    if (Eval.Map1IndexEnable) {
      map = Eval.Map1Index;
      q = Eval.gl_eval_coord_1(map, 1, u);
      gl_index((int) q[0]);
    }

    if (Eval.Map1NormalEnable) {
      map = Eval.Map1Normal;
      q = Eval.gl_eval_coord_1(map, 3, u);
      gl_normal(q[0], q[1], q[2]);
    }

    if (Eval.Map1TexCoord4Enable) {
      map = Eval.Map1TexCoord4;
      q = Eval.gl_eval_coord_1(map, 4, u);
      gl_tex_coord(q[0], q[1], q[2], q[3]);
    } else if (Eval.Map1TexCoord3Enable) {
      map = Eval.Map1TexCoord3;
      q = Eval.gl_eval_coord_1(map, 3, u);
      gl_tex_coord(q[0], q[1], q[2], 1.0f);
    } else if (Eval.Map1TexCoord2Enable) {
      map = Eval.Map1TexCoord2;
      q = Eval.gl_eval_coord_1(map, 2, u);
      gl_tex_coord(q[0], q[1], 0.0f, 1.0f);
    } else if (Eval.Map1TexCoord1Enable) {
      map = Eval.Map1TexCoord1;
      q = Eval.gl_eval_coord_1(map, 1, u);
      gl_tex_coord(q[0], 0.0f, 0.0f, 1.0f);
    }

    if (Eval.Map1Vertex4Enable) {
      map = Eval.Map1Vertex4;
      q = Eval.gl_eval_coord_1(map, 4, u);
      gl_vertex(q[0], q[1], q[2], q[3]);
    } else if (Eval.Map1Vertex3Enable) {
      map = Eval.Map1Vertex3;
      q = Eval.gl_eval_coord_1(map, 3, u);
      gl_vertex(q[0], q[1], q[2], 1.0f);
    }
  }

  public void gl_eval_coord_2(float u, float v) {
    gl_eval_map2 map;
    float q[], n[];

    if (Eval.Map2Color4Enable) {
      map = Eval.Map2Color4;
      q = Eval.gl_eval_coord_2(map, 4, u, v);
      gl_color(q[0], q[1], q[2], q[3]);
    }

    if (Eval.Map2IndexEnable) {
      map = Eval.Map2Index;
      q = Eval.gl_eval_coord_2(map, 1, u, v);
      gl_index((int) q[0]);
    }

    if (!Eval.AutoNormal || (!Eval.Map2Vertex4Enable && !Eval.Map2Vertex3Enable)) {
      if (Eval.Map2NormalEnable) {
        map = Eval.Map2Normal;
        q = Eval.gl_eval_coord_2(map, 3, u, v);
        gl_normal(q[0], q[1], q[2]);
      }
    }

    if (Eval.Map2TexCoord4Enable) {
      map = Eval.Map2TexCoord4;
      q = Eval.gl_eval_coord_2(map, 4, u, v);
      gl_tex_coord(q[0], q[1], q[2], q[3]);
    } else if (Eval.Map2TexCoord3Enable) {
      map = Eval.Map2TexCoord3;
      q = Eval.gl_eval_coord_2(map, 3, u, v);
      gl_tex_coord(q[0], q[1], q[2], 1.0f);
    } else if (Eval.Map2TexCoord2Enable) {
      map = Eval.Map2TexCoord2;
      q = Eval.gl_eval_coord_2(map, 2, u, v);
      gl_tex_coord(q[0], q[1], 0.0f, 1.0f);
    } else if (Eval.Map2TexCoord1Enable) {
      map = Eval.Map2TexCoord1;
      q = Eval.gl_eval_coord_2(map, 1, u, v);
      gl_tex_coord(q[0], 0.0f, 0.0f, 1.0f);
    }

    if (Eval.Map2Vertex4Enable) {
      map = Eval.Map2Vertex4;
      if (Eval.AutoNormal) {
        n = Eval.gl_auto_normal(map, 4, u, v);
        gl_normal(n[0], n[1], n[2]);
      }
      q = Eval.gl_eval_coord_2(map, 4, u, v);
      gl_vertex(q[0], q[1], q[2], q[3]);
    } else if (Eval.Map2Vertex3Enable) {
      map = Eval.Map2Vertex3;
      if (Eval.AutoNormal) {
        n = Eval.gl_auto_normal(map, 3, u, v);
        gl_normal(n[0], n[1], n[2]);
      }
      q = Eval.gl_eval_coord_2(map, 3, u, v);
      gl_vertex(q[0], q[1], q[2], 1.0f);
    }
  }

  public void gl_eval_mesh_1(int mode, int nu1, int nu2) {
    float u1 = Eval.MapGrid1u1;
    float u2 = Eval.MapGrid1u2;
    int nu = Eval.MapGrid1un;
    float du = (u2 - u1) / (float) nu;
    int i;

    switch (mode) {
      case GL.GL_POINT:
        gl_begin(GL.GL_POINTS);
        break;
      case GL.GL_LINE:
        gl_begin(GL.GL_LINE_STRIP);
        break;
      default: // never default
        break;
    }
    for (i = nu1; i <= nu2; i++) {
      gl_eval_coord_1(gl_util.interpolate(i, nu, u1, u2, du));
    }
    gl_end();
  }

  public void gl_eval_mesh_2(int mode, int nu1, int nu2, int nv1, int nv2) {
    float u1 = Eval.MapGrid2u1;
    float u2 = Eval.MapGrid2u2;
    float v1 = Eval.MapGrid2v1;
    float v2 = Eval.MapGrid2v2;
    int nu = Eval.MapGrid2un;
    int nv = Eval.MapGrid2vn;
    float du = (u2 - u1) / (float) nu;
    float dv = (v2 - v1) / (float) nv;
    int i, j;
    float tu, tv, tv1;

    switch (mode) {
      case GL.GL_POINT:
        gl_begin(GL.GL_POINTS);
        for (i = nu1; i <= nu2; i++) {
          tu = gl_util.interpolate(i, nu, u1, u2, du);
          for (j = nv1; j <= nv2; j++) {
            gl_eval_coord_2(tu, gl_util.interpolate(j, nv, v1, v2, dv));
          }
        }
        gl_end();
        break;
      case GL.GL_LINE:
        for (j = nv1; j <= nv2; j++) {
          gl_begin(GL.GL_LINE_STRIP);
          tv = gl_util.interpolate(j, nv, v1, v2, dv);
          for (i = nu1; i <= nu2; i++) {
            gl_eval_coord_2(gl_util.interpolate(i, nu, u1, u2, du), tv);
          }
          gl_end();
        }
        for (i = nu1; i <= nu2; i++) {
          gl_begin(GL.GL_LINE_STRIP);
          tu = gl_util.interpolate(i, nu, u1, u2, du);
          for (j = nv1; j <= nv2; j++) {
            gl_eval_coord_2(tu, gl_util.interpolate(j, nv, v1, v2, dv));
          }
          gl_end();
        }
        break;
      case GL.GL_FILL:
        for (j = nv1; j < nv2; j++) {
          gl_begin(GL.GL_TRIANGLE_STRIP);
          tv = gl_util.interpolate(j, nv, v1, v2, dv);
          tv1 = gl_util.interpolate(j + 1, nv, v1, v2, dv);
          for (i = nu1; i <= nu2; i++) {
            tu = gl_util.interpolate(i, nu, u1, u2, du);
            gl_eval_coord_2(tu, tv);
            gl_eval_coord_2(tu, tv1);
          }
          gl_end();
        }
        break;
      default:
        break;
    }
  }

  public abstract void gl_clear_color(float red, float green, float blue, float alpha);

  public abstract void gl_clear_depth_buffer();

  public abstract void gl_clear_color_buffer();

  public abstract void gl_clear_stencil_buffer();

  public abstract void gl_color_mask(boolean red, boolean green, boolean blue, boolean alpha);

  public abstract void gl_blend_func(int sfactor, int dfactor);

  public abstract void gl_cull_face(int mode);

  public abstract void gl_front_face(int mode);

  public abstract void gl_point_size(float size);

  public abstract void gl_line_width(float width);

  public abstract void gl_line_stipple(int factor, short pattern);

  public abstract void gl_polygon_mode(int face, int mode);

  public abstract void gl_polygon_stipple(byte mask[]);

  public abstract void gl_clip_plane(int plane, float equation[]);

  public abstract float[] gl_get_clip_plane(int plane);

  public abstract void gl_enable(int cap, boolean state);

  public abstract boolean gl_is_enabled(int cap);

  public abstract int gl_render_mode(int mode);

  public abstract void gl_clear_depth(float depth);

  public abstract void gl_depth_func(int func);

  public abstract void gl_depth_mask(boolean flag);

  public abstract void gl_depth_range(float n, float f);

  public abstract void gl_matrix_mode(int mode);

  public abstract void gl_mult_matrix(float m[]);

  public abstract void gl_viewport(int x, int y, int width, int height);

  public abstract void gl_push_matrix();

  public abstract void gl_pop_matrix();

  public abstract void gl_load_identity_matrix();

  public abstract void gl_load_matrix(float m[]);

  public abstract void gl_rotate(float angle, float x, float y, float z);

  public abstract void gl_scale(float x, float y, float z);

  public abstract void gl_translate(float x, float y, float z);

  public abstract void gl_call_list(int list);

  public abstract void gl_call_offset(int offset);

  public abstract void gl_list_base(int base);

  public abstract void gl_begin(int mode);

  public abstract void gl_end();

  public abstract void gl_vertex(float x, float y, float z, float w);

  public abstract void gl_normal(float x, float y, float z);

  public abstract void gl_index(int c);

  public abstract void gl_color(float red, float green, float blue, float alpha);

  public abstract void gl_tex_coord(float s, float t, float r, float q);

  public abstract void gl_raster_pos(float x, float y, float z, float w);

  public abstract void gl_shade_model(int mode);

  public abstract void gl_light(int light, int pname, float params[]);

  public abstract float[] gl_get_light(int light, int pname);

  public abstract void gl_light_model(int pname, float params[]);

  public abstract void gl_material(int face, int pname, float params[]);

  public abstract float[] gl_get_material(int face, int pname);

  public abstract void gl_color_material(int face, int mode);

  public abstract void gl_pixel_store(int pname, int param);

  public abstract void gl_pixel_transfer(int pname, float param);

  public abstract void gl_read_index_pixels(int x, int y, int width, int height, int size,
      Object pixels);

  public abstract void gl_read_color_pixels(int x, int y, int width, int height, int format,
      int size, Object pixels);

  public abstract void gl_read_stencil_pixels(int x, int y, int width, int height, int size,
      Object pixels);

  public abstract void gl_read_depth_pixels(int x, int y, int width, int height, int size,
      Object pixels);

  public abstract void gl_draw_index_pixels(int width, int height, int size, Object pixels);

  public abstract void gl_draw_color_pixels(int width, int height, int format, int size,
      Object pixels);

  public abstract void gl_draw_stencil_pixels(int width, int height, int size, Object pixels);

  public abstract void gl_draw_depth_pixels(int width, int height, int size, Object pixels);

  public abstract void gl_copy_color_pixels(int x, int y, int width, int height);

  public abstract void gl_copy_stencil_pixels(int x, int y, int width, int height);

  public abstract void gl_copy_depth_pixels(int x, int y, int width, int height);

  public abstract void gl_stencil_func(int func, int ref, int mask);

  public abstract void gl_stencil_mask(int mask);

  public abstract void gl_stencil_op(int fail, int zfail, int zpass);

  public abstract void gl_clear_stencil(int s);

  public abstract void gl_tex_gen_i(int coord, int param);

  public abstract void gl_tex_gen_f(int coord, int pname, float params[]);

  public abstract void gl_tex_env_i(int param);

  public abstract void gl_tex_env_f(float params[]);

  public abstract void gl_tex_parameter(int target, int pname, float params[]);

  public abstract void gl_tex_image_1d(int target, int level, int components, int width, int border,
      int format, int size, Object pixels);

  public abstract void gl_tex_image_2d(int target, int level, int components, int width, int height,
      int border, int format, int size, Object pixels);

  public abstract void gl_tex_image_3d(int target, int level, int components, int width, int height,
      int depth, int border, int format, int size, Object pixels);

  public abstract void gl_bind_texture(int target, int texture);

  public abstract void gl_tex_sub_image_1d(int target, int level, int xoffset, int width,
      int format, int size, Object pixels);

  public abstract void gl_tex_sub_image_2d(int target, int level, int xoffset, int yoffset,
      int width, int height, int format, int size, Object pixels);

  public abstract void gl_tex_sub_image_3d(int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int size, Object pixels);

  /*
   * public abstract void gl_copy_tex_image_1d (int target, int level, int internalformat, int x,
   * int y, int width, int border); public abstract void gl_copy_tex_image_2d (int target, int
   * level, int internalformat, int x, int y, int width, int height, int border); public abstract
   * void gl_copy_tex_sub_image_1d (int target, int level, int xoffset, int yoffset, int x, int y,
   * int width); public abstract void gl_copy_tex_sub_image_2d (int target, int level, int xoffset,
   * int yoffset, int x, int y, int width, int height); public abstract void
   * gl_copy_tex_sub_image_3d (int target, int level, int xoffset, int yoffset, int zoffset, int x,
   * int y, int width, int height);
   */
  public abstract void gl_feedback_buffer(int size, int type, float buffer[]);

  public abstract void gl_pass_through(float token);

  public abstract void gl_select_buffer(int size, int buffer[]);

  public abstract void gl_init_names();

  public abstract void gl_load_name(int name);

  public abstract void gl_push_name(int name);

  public abstract void gl_pop_name();

}
