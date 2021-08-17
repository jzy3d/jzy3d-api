/*
 * @(#)gl_list.java 0.7 01/12/03
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

import java.util.Vector;
import jgl.GL;

/**
 * gl_list is the list class of jGL 2.4.
 *
 * @version 0.7, 3 Dec 2001
 * @author Robin Bing-Yu Chen
 */

public class gl_list extends gl_object {

  /** Constant of List of JavaGL */
  private static final int NODE_ACCUM = 0;
  private static final int NODE_ALPHA_FUNC = 1;
  private static final int NODE_BEGIN = 2;
  private static final int NODE_BITMAP = 3;
  private static final int NODE_BLEND_COLOR = 4;
  private static final int NODE_BLEND_EQUATION = 5;
  private static final int NODE_BLEND_FUNC = 6;
  private static final int NODE_CALL_LIST = 7;
  // private static final int NODE_CALL_LIST_OFFSET = 8;
  private static final int NODE_CALL_OFFSET = 9;
  // private static final int NODE_CLEAR = 10;
  private static final int NODE_CLEAR_ACCUM = 11;
  private static final int NODE_CLEAR_COLOR = 12;
  private static final int NODE_CLEAR_COLOR_BUFFER = 13;
  private static final int NODE_CLEAR_DEPTH = 14;
  private static final int NODE_CLEAR_DEPTH_BUFFER = 15;
  private static final int NODE_CLEAR_INDEX = 16;
  private static final int NODE_CLEAR_STENCIL = 17;
  private static final int NODE_CLEAR_STENCIL_BUFFER = 95;
  private static final int NODE_CLIP_PLANE = 18;
  private static final int NODE_COLOR = 19;
  private static final int NODE_COLOR_MASK = 20;
  private static final int NODE_COLOR_MATERIAL = 21;
  // private static final int NODE_COPY_PIXELS = 22;
  private static final int NODE_COPY_COLOR_PIXELS = 22;
  private static final int NODE_COPY_STENCIL_PIXELS = 23;
  private static final int NODE_COPY_DEPTH_PIXELS = 24;
  private static final int NODE_CULL_FACE = 25;
  private static final int NODE_DEPTH_FUNC = 26;
  private static final int NODE_DEPTH_MASK = 27;
  private static final int NODE_DEPTH_RANGE = 28;
  private static final int NODE_DISABLE = 29;
  private static final int NODE_DRAW_BUFFER = 30;
  // private static final int NODE_DRAW_PIXELS = 31;
  private static final int NODE_DRAW_INDEX_PIXELS = 31;
  private static final int NODE_DRAW_COLOR_PIXELS = 32;
  private static final int NODE_DRAW_STENCIL_PIXELS = 33;
  private static final int NODE_DRAW_DEPTH_PIXELS = 34;
  private static final int NODE_EDGE_FLAG = 35;
  private static final int NODE_ENABLE = 36;
  private static final int NODE_END = 37;
  /*
   * private static final int NODE_EVAL_COORD_1 = 33; private static final int NODE_EVAL_COORD_2 =
   * 34; private static final int NODE_EVAL_MESH_1 = 35; private static final int NODE_EVAL_MESH_2 =
   * 36; private static final int NODE_EVAL_POINT_1 = 37; private static final int NODE_EVAL_POINT_2
   * = 38;
   */
  private static final int NODE_FOG = 39;
  private static final int NODE_FRONT_FACE = 40;
  private static final int NODE_HINT = 41;
  private static final int NODE_INDEX = 42;
  private static final int NODE_INDEX_MASK = 43;
  private static final int NODE_INIT_NAMES = 44;
  private static final int NODE_LIGHT = 45;
  private static final int NODE_LIGHT_MODEL = 46;
  private static final int NODE_LINE_STIPPLE = 47;
  private static final int NODE_LINE_WIDTH = 48;
  private static final int NODE_LIST_BASE = 49;
  private static final int NODE_LOAD_MATRIX = 50;
  private static final int NODE_LOAD_NAME = 51;
  private static final int NODE_LOGIC_OP = 52;
  /*
   * private static final int NODE_MAP_1 = 53; private static final int NODE_MAP_2 = 54; private
   * static final int NODE_MAP_GRID_1 = 55; private static final int NODE_MAP_GRID_2 = 56;
   */
  private static final int NODE_MATERIAL = 57;
  private static final int NODE_MATRIX_MODE = 58;
  private static final int NODE_MULT_MATRIX = 59;
  private static final int NODE_NORMAL = 60;
  private static final int NODE_PASS_THROUGH = 61;
  private static final int NODE_PIXEL_MAP = 62;
  private static final int NODE_PIXEL_TRANSFER = 63;
  private static final int NODE_PIXEL_ZOOM = 64;
  private static final int NODE_POINT_SIZE = 65;
  private static final int NODE_POLYGON_MODE = 66;
  private static final int NODE_POLYGON_STIPPLE = 67;
  private static final int NODE_POLYGON_OFFSET = 68;
  private static final int NODE_POP_ATTRIB = 69;
  private static final int NODE_POP_MATRIX = 70;
  private static final int NODE_POP_NAME = 71;
  private static final int NODE_PUSH_ATTRIB = 72;
  private static final int NODE_PUSH_MATRIX = 73;
  private static final int NODE_PUSH_NAME = 74;
  private static final int NODE_RASTER_POS = 75;
  private static final int NODE_READ_BUFFER = 76;
  private static final int NODE_SCALE = 77;
  private static final int NODE_SCISSOR = 78;
  private static final int NODE_SHADE_MODEL = 79;
  private static final int NODE_STENCIL_FUNC = 80;
  private static final int NODE_STENCIL_MASK = 81;
  private static final int NODE_STENCIL_OP = 82;
  private static final int NODE_TEX_COORD = 83;
  // private static final int NODE_TEX_GEN = 84;
  private static final int NODE_TEX_GEN_I = 84;
  private static final int NODE_TEX_GEN_F = 85;
  // private static final int NODE_TEX_ENV = 86;
  private static final int NODE_TEX_ENV_I = 86;
  private static final int NODE_TEX_ENV_F = 87;
  private static final int NODE_TEX_PARAMETER = 88;
  private static final int NODE_TEX_IMAGE_1D = 89;
  private static final int NODE_TEX_IMAGE_2D = 90;
  private static final int NODE_TEX_IMAGE_3D = 91;
  private static final int NODE_BIND_TEXTURE = 96;
  private static final int NODE_TEX_SUB_IMAGE_1D = 97;
  private static final int NODE_TEX_SUB_IMAGE_2D = 98;
  private static final int NODE_TEX_SUB_IMAGE_3D = 99;
  private static final int NODE_TRANSLATE = 92;
  private static final int NODE_VERTEX = 93;
  private static final int NODE_VIEWPORT = 94;

  /** Private Data Members */
  private gl_context Context;
  private gl_list_item ListItem;
  private boolean ExecuteFlag;
  private Vector<gl_list_item> ThisList = new Vector<gl_list_item>();

  public void gl_exec_list(gl_context CC) {
    int i;

    for (i = 0; i < ThisList.size(); i++) {
      ListItem = (gl_list_item) ThisList.elementAt(i);
      switch (ListItem.NodeKind) {
        case NODE_CLEAR_COLOR:
          CC.gl_clear_color(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2],
              ListItem.FloatPtr[3]);
          break;
        case NODE_CLEAR_DEPTH_BUFFER:
          CC.gl_clear_depth_buffer();
          break;
        case NODE_CLEAR_COLOR_BUFFER:
          CC.gl_clear_color_buffer();
          break;
        case NODE_CLEAR_STENCIL_BUFFER:
          CC.gl_clear_stencil_buffer();
          break;
        case NODE_COLOR_MASK:
          CC.gl_color_mask(ListItem.BoolPtr[0], ListItem.BoolPtr[1], ListItem.BoolPtr[2],
              ListItem.BoolPtr[3]);
          break;
        case NODE_BLEND_FUNC:
          CC.gl_blend_func(ListItem.IntPtr[0], ListItem.IntPtr[1]);
          break;
        case NODE_CULL_FACE:
          CC.gl_cull_face(ListItem.IntPtr[0]);
          break;
        case NODE_FRONT_FACE:
          CC.gl_front_face(ListItem.IntPtr[0]);
          break;
        case NODE_POINT_SIZE:
          CC.gl_point_size(ListItem.FloatPtr[0]);
          break;
        case NODE_LINE_WIDTH:
          CC.gl_line_width(ListItem.FloatPtr[0]);
          break;
        case NODE_LINE_STIPPLE:
          CC.gl_line_stipple(ListItem.IntPtr[0], (short) ListItem.IntPtr[1]);
          break;
        case NODE_POLYGON_MODE:
          CC.gl_polygon_mode(ListItem.IntPtr[0], ListItem.IntPtr[1]);
          break;
        case NODE_POLYGON_STIPPLE:
          CC.gl_polygon_stipple((byte[]) ListItem.ObjPtr);
          break;
        case NODE_CLIP_PLANE:
          CC.gl_clip_plane(ListItem.IntPtr[0], ListItem.FloatPtr);
          break;
        case NODE_ENABLE:
          CC.gl_enable(ListItem.IntPtr[0], ListItem.BoolPtr[0]);
          break;
        case NODE_CLEAR_DEPTH:
          CC.gl_clear_depth(ListItem.FloatPtr[0]);
          break;
        case NODE_DEPTH_FUNC:
          CC.gl_depth_func(ListItem.IntPtr[0]);
          break;
        case NODE_DEPTH_MASK:
          CC.gl_depth_mask(ListItem.BoolPtr[0]);
          break;
        case NODE_DEPTH_RANGE:
          CC.gl_depth_range(ListItem.FloatPtr[0], ListItem.FloatPtr[1]);
          break;
        case NODE_MATRIX_MODE:
          CC.gl_matrix_mode(ListItem.IntPtr[0]);
          break;
        case NODE_MULT_MATRIX:
          CC.gl_mult_matrix(ListItem.FloatPtr);
          break;
        case NODE_VIEWPORT:
          CC.gl_viewport(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3]);
          break;
        case NODE_PUSH_MATRIX:
          CC.gl_push_matrix();
          break;
        case NODE_POP_MATRIX:
          CC.gl_pop_matrix();
          break;
        case NODE_CALL_LIST:
          CC.gl_call_list(ListItem.IntPtr[0]);
          break;
        case NODE_CALL_OFFSET:
          CC.gl_call_offset(ListItem.IntPtr[0]);
          break;
        case NODE_LIST_BASE:
          CC.gl_list_base(ListItem.IntPtr[0]);
          break;
        case NODE_BEGIN:
          CC.gl_begin(ListItem.IntPtr[0]);
          break;
        case NODE_END:
          CC.gl_end();
          break;
        case NODE_VERTEX:
          CC.gl_vertex(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2],
              ListItem.FloatPtr[3]);
          break;
        case NODE_NORMAL:
          CC.gl_normal(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2]);
          break;
        case NODE_INDEX:
          CC.gl_index(ListItem.IntPtr[0]);
          break;
        case NODE_COLOR:
          CC.gl_color(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2],
              ListItem.FloatPtr[3]);
          break;
        case NODE_TEX_COORD:
          CC.gl_tex_coord(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2],
              ListItem.FloatPtr[3]);
          break;
        /*
         * case NODE_MAP_1: CC.gl_map_1 (ListItem.IntPtr[0], ListItem.FloatPtr[0],
         * ListItem.FloatPtr[1], ListItem.IntPtr[1], ListItem.IntPtr[2],
         * (float[][])ListItem.ObjPtr); break; case NODE_MAP_2: CC.gl_map_2 (ListItem.IntPtr[0],
         * ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.IntPtr[1], ListItem.IntPtr[2],
         * ListItem.FloatPtr[2], ListItem.FloatPtr[3], ListItem.IntPtr[3], ListItem.IntPtr[4],
         * (float[][][])ListItem.ObjPtr); break; case NODE_EVAL_COORD_1: CC.gl_eval_coord_1
         * (ListItem.FloatPtr[0]); break; case NODE_EVAL_COORD_2: CC.gl_eval_coord_2
         * (ListItem.FloatPtr[0], ListItem.FloatPtr[1]); break; case NODE_MAP_GRID_1:
         * CC.gl_map_grid_1 (ListItem.IntPtr[0], ListItem.FloatPtr[0], ListItem.FloatPtr[1]); break;
         * case NODE_MAP_GRID_2: CC.gl_map_grid_2 (ListItem.IntPtr[0], ListItem.FloatPtr[0],
         * ListItem.FloatPtr[1], ListItem.IntPtr[1], ListItem.FloatPtr[2], ListItem.FloatPtr[3]);
         * break; case NODE_EVAL_POINT_1: CC.gl_eval_point_1 (ListItem.IntPtr[0]); break; case
         * NODE_EVAL_POINT_2: CC.gl_eval_point_2 (ListItem.IntPtr[0], ListItem.IntPtr[1]); break;
         * case NODE_EVAL_MESH_1: CC.gl_eval_mesh_1 (ListItem.IntPtr[0], ListItem.IntPtr[1],
         * ListItem.IntPtr[2]); break; case NODE_EVAL_MESH_2: CC.gl_eval_mesh_2 (ListItem.IntPtr[0],
         * ListItem.IntPtr[1], ListItem.IntPtr[2], ListItem.IntPtr[3], ListItem.IntPtr[4]); break;
         */
        case NODE_RASTER_POS:
          CC.gl_raster_pos(ListItem.FloatPtr[0], ListItem.FloatPtr[1], ListItem.FloatPtr[2],
              ListItem.FloatPtr[3]);
          break;
        case NODE_SHADE_MODEL:
          CC.gl_shade_model(ListItem.IntPtr[0]);
          break;
        case NODE_LIGHT:
          CC.gl_light(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.FloatPtr);
          break;
        case NODE_LIGHT_MODEL:
          CC.gl_light_model(ListItem.IntPtr[0], ListItem.FloatPtr);
          break;
        case NODE_MATERIAL:
          CC.gl_material(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.FloatPtr);
          break;
        case NODE_COLOR_MATERIAL:
          CC.gl_color_material(ListItem.IntPtr[0], ListItem.IntPtr[1]);
          break;
        case NODE_DRAW_INDEX_PIXELS:
          CC.gl_draw_index_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.ObjPtr);
          break;
        case NODE_DRAW_COLOR_PIXELS:
          CC.gl_draw_color_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.ObjPtr);
          break;
        case NODE_DRAW_STENCIL_PIXELS:
          CC.gl_draw_stencil_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.ObjPtr);
          break;
        case NODE_DRAW_DEPTH_PIXELS:
          CC.gl_draw_depth_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.ObjPtr);
          break;
        case NODE_COPY_COLOR_PIXELS:
          CC.gl_copy_color_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3]);
          break;
        case NODE_COPY_STENCIL_PIXELS:
          CC.gl_copy_stencil_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3]);
          break;
        case NODE_COPY_DEPTH_PIXELS:
          CC.gl_copy_depth_pixels(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3]);
          break;
        case NODE_STENCIL_FUNC:
          CC.gl_stencil_func(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2]);
          break;
        case NODE_STENCIL_MASK:
          CC.gl_stencil_mask(ListItem.IntPtr[0]);
          break;
        case NODE_STENCIL_OP:
          CC.gl_stencil_op(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2]);
          break;
        case NODE_CLEAR_STENCIL:
          CC.gl_clear_stencil(ListItem.IntPtr[0]);
          break;
        case NODE_TEX_GEN_I:
          CC.gl_tex_gen_i(ListItem.IntPtr[0], ListItem.IntPtr[1]);
          break;
        case NODE_TEX_GEN_F:
          CC.gl_tex_gen_f(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.FloatPtr);
          break;
        case NODE_TEX_ENV_I:
          CC.gl_tex_env_i(ListItem.IntPtr[0]);
          break;
        case NODE_TEX_ENV_F:
          CC.gl_tex_env_f(ListItem.FloatPtr);
          break;
        case NODE_TEX_PARAMETER:
          CC.gl_tex_parameter(ListItem.IntPtr[0], ListItem.IntPtr[1], (float[]) ListItem.ObjPtr);
          break;
        case NODE_TEX_IMAGE_1D:
          CC.gl_tex_image_1d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.IntPtr[6],
              ListItem.ObjPtr);
          break;
        case NODE_TEX_IMAGE_2D:
          CC.gl_tex_image_2d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.IntPtr[6],
              ListItem.IntPtr[7], ListItem.ObjPtr);
          break;
        case NODE_TEX_IMAGE_3D:
          CC.gl_tex_image_3d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.IntPtr[6],
              ListItem.IntPtr[7], ListItem.IntPtr[8], ListItem.ObjPtr);
          break;
        case NODE_BIND_TEXTURE:
          CC.gl_bind_texture(ListItem.IntPtr[0], ListItem.IntPtr[1]);
          break;
        case NODE_TEX_SUB_IMAGE_1D:
          CC.gl_tex_sub_image_1d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.ObjPtr);
          break;
        case NODE_TEX_SUB_IMAGE_2D:
          CC.gl_tex_sub_image_2d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.IntPtr[6],
              ListItem.IntPtr[7], ListItem.ObjPtr);
          break;
        case NODE_TEX_SUB_IMAGE_3D:
          CC.gl_tex_sub_image_3d(ListItem.IntPtr[0], ListItem.IntPtr[1], ListItem.IntPtr[2],
              ListItem.IntPtr[3], ListItem.IntPtr[4], ListItem.IntPtr[5], ListItem.IntPtr[6],
              ListItem.IntPtr[7], ListItem.IntPtr[8], ListItem.IntPtr[9], ListItem.ObjPtr);
          break;
        case NODE_PASS_THROUGH:
          CC.gl_pass_through(ListItem.FloatPtr[0]);
          break;
        case NODE_INIT_NAMES:
          CC.gl_init_names();
          break;
        case NODE_LOAD_NAME:
          CC.gl_load_name(ListItem.IntPtr[0]);
          break;
        case NODE_PUSH_NAME:
          CC.gl_push_name(ListItem.IntPtr[0]);
          break;
        case NODE_POP_NAME:
          CC.gl_pop_name();
          break;
        default:
          break;
      }
    }
  }

  public void gl_clear_color(float red, float green, float blue, float alpha) {
    ListItem = new gl_list_item(NODE_CLEAR_COLOR);
    ListItem.FloatPtr = new float[4];
    ListItem.FloatPtr[0] = red;
    ListItem.FloatPtr[1] = green;
    ListItem.FloatPtr[2] = blue;
    ListItem.FloatPtr[3] = alpha;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_color(red, green, blue, alpha);
    }
  }

  public void gl_clear_depth_buffer() {
    ListItem = new gl_list_item(NODE_CLEAR_DEPTH_BUFFER);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_depth_buffer();
    }
  }

  public void gl_clear_color_buffer() {
    ListItem = new gl_list_item(NODE_CLEAR_COLOR_BUFFER);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_color_buffer();
    }
  }

  public void gl_clear_stencil_buffer() {
    ListItem = new gl_list_item(NODE_CLEAR_STENCIL_BUFFER);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_stencil_buffer();
    }
  }

  public void gl_color_mask(boolean red, boolean green, boolean blue, boolean alpha) {
    ListItem = new gl_list_item(NODE_COLOR_MASK);
    ListItem.BoolPtr = new boolean[4];
    ListItem.BoolPtr[0] = red;
    ListItem.BoolPtr[1] = green;
    ListItem.BoolPtr[2] = blue;
    ListItem.BoolPtr[3] = alpha;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_color_mask(red, green, blue, alpha);
    }
  }

  public void gl_blend_func(int sfactor, int dfactor) {
    ListItem = new gl_list_item(NODE_BLEND_FUNC);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = sfactor;
    ListItem.IntPtr[1] = dfactor;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_blend_func(sfactor, dfactor);
    }
  }

  public void gl_cull_face(int mode) {
    ListItem = new gl_list_item(NODE_CULL_FACE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_cull_face(mode);
    }
  }

  public void gl_front_face(int mode) {
    ListItem = new gl_list_item(NODE_FRONT_FACE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_front_face(mode);
    }
  }

  public void gl_point_size(float size) {
    ListItem = new gl_list_item(NODE_POINT_SIZE);
    ListItem.FloatPtr = new float[1];
    ListItem.FloatPtr[0] = size;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_point_size(size);
    }
  }

  public void gl_line_width(float width) {
    ListItem = new gl_list_item(NODE_LINE_WIDTH);
    ListItem.FloatPtr = new float[1];
    ListItem.FloatPtr[0] = width;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_line_width(width);
    }
  }

  public void gl_line_stipple(int factor, short pattern) {
    ListItem = new gl_list_item(NODE_LINE_STIPPLE);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = factor;
    ListItem.IntPtr[1] = (int) (pattern & 0x0000ffff);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_line_stipple(factor, pattern);
    }
  }

  public void gl_polygon_mode(int face, int mode) {
    ListItem = new gl_list_item(NODE_POLYGON_MODE);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = face;
    ListItem.IntPtr[1] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_polygon_mode(face, mode);
    }
  }

  public void gl_polygon_stipple(byte mask[]) {
    ListItem = new gl_list_item(NODE_POLYGON_STIPPLE);
    ListItem.ObjPtr = (Object) mask;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_polygon_stipple(mask);
    }
  }

  public void gl_clip_plane(int plane, float equation[]) {
    ListItem = new gl_list_item(NODE_CLIP_PLANE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = plane;
    ListItem.FloatPtr = equation;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clip_plane(plane, equation);
    }
  }

  public float[] gl_get_clip_plane(int plane) {
    return (Context.gl_get_clip_plane(plane));
  }

  public void gl_enable(int cap, boolean state) {
    ListItem = new gl_list_item(NODE_ENABLE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = cap;
    ListItem.BoolPtr = new boolean[1];
    ListItem.BoolPtr[0] = state;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_enable(cap, state);
    }
  }

  public boolean gl_is_enabled(int cap) {
    return (Context.gl_is_enabled(cap));
  }

  public int gl_render_mode(int mode) {
    return (Context.gl_render_mode(mode));
  }

  public void gl_clear_depth(float depth) {
    ListItem = new gl_list_item(NODE_CLEAR_DEPTH);
    ListItem.FloatPtr = new float[1];
    ListItem.FloatPtr[0] = depth;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_depth(depth);
    }
  }

  public void gl_depth_func(int func) {
    ListItem = new gl_list_item(NODE_DEPTH_FUNC);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = func;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_depth_func(func);
    }
  }

  public void gl_depth_mask(boolean flag) {
    ListItem = new gl_list_item(NODE_DEPTH_MASK);
    ListItem.BoolPtr = new boolean[1];
    ListItem.BoolPtr[0] = flag;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_depth_mask(flag);
    }
  }

  public void gl_depth_range(float n, float f) {
    ListItem = new gl_list_item(NODE_DEPTH_RANGE);
    ListItem.FloatPtr = new float[2];
    ListItem.FloatPtr[0] = n;
    ListItem.FloatPtr[1] = f;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_depth_range(n, f);
    }
  }

  public void gl_matrix_mode(int mode) {
    ListItem = new gl_list_item(NODE_MATRIX_MODE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_matrix_mode(mode);
    }
  }

  public void gl_mult_matrix(float m[]) {
    ListItem = new gl_list_item(NODE_MULT_MATRIX);
    ListItem.FloatPtr = m;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_mult_matrix(m);
    }
  }

  public void gl_viewport(int x, int y, int width, int height) {
    ListItem = new gl_list_item(NODE_VIEWPORT);
    ListItem.IntPtr = new int[4];
    ListItem.IntPtr[0] = x;
    ListItem.IntPtr[1] = y;
    ListItem.IntPtr[2] = width;
    ListItem.IntPtr[3] = height;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_viewport(x, y, width, height);
    }
  }

  public void gl_push_matrix() {
    ListItem = new gl_list_item(NODE_PUSH_MATRIX);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_push_matrix();
    }
  }

  public void gl_pop_matrix() {
    ListItem = new gl_list_item(NODE_POP_MATRIX);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_pop_matrix();
    }
  }

  public void gl_load_identity_matrix() {
    ListItem = new gl_list_item(NODE_LOAD_MATRIX);
    ListItem.FloatPtr = IDENTITY;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_load_identity_matrix();
    }
  }

  public void gl_load_matrix(float m[]) {
    ListItem = new gl_list_item(NODE_LOAD_MATRIX);
    ListItem.FloatPtr = m;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_load_matrix(m);
    }
  }

  public void gl_rotate(float angle, float x, float y, float z) {
    float a[] = get_rotate(angle, x, y, z);
    if (a == null) {
      return;
    }
    ListItem = new gl_list_item(NODE_MULT_MATRIX);
    ListItem.FloatPtr = a;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_mult_matrix(a);
    }
  }

  public void gl_scale(float x, float y, float z) {
    float a[] = get_scale(x, y, z);
    ListItem = new gl_list_item(NODE_MULT_MATRIX);
    ListItem.FloatPtr = a;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_mult_matrix(a);
    }
  }

  public void gl_translate(float x, float y, float z) {
    float a[] = get_translate(x, y, z);
    ListItem = new gl_list_item(NODE_MULT_MATRIX);
    ListItem.FloatPtr = a;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_mult_matrix(a);
    }
  }

  public void gl_call_list(int list) {
    ListItem = new gl_list_item(NODE_CALL_LIST);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = list;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_call_list(list);
    }
  }

  public void gl_call_offset(int offset) {
    ListItem = new gl_list_item(NODE_CALL_OFFSET);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = offset;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_call_offset(offset);
    }
  }

  public void gl_list_base(int base) {
    ListItem = new gl_list_item(NODE_LIST_BASE);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = base;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_list_base(base);
    }
  }

  public void gl_begin(int mode) {
    Mode = mode;
    ListItem = new gl_list_item(NODE_BEGIN);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_begin(mode);
    }
  }

  public void gl_end() {
    Mode = GL.None;
    ListItem = new gl_list_item(NODE_END);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_end();
    }
  }

  public void gl_vertex(float x, float y, float z, float w) {
    ListItem = new gl_list_item(NODE_VERTEX);
    ListItem.FloatPtr = new float[4];
    ListItem.FloatPtr[0] = x;
    ListItem.FloatPtr[1] = y;
    ListItem.FloatPtr[2] = z;
    ListItem.FloatPtr[3] = w;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_vertex(x, y, z, w);
    }
  }

  public void gl_normal(float x, float y, float z) {
    ListItem = new gl_list_item(NODE_NORMAL);
    ListItem.FloatPtr = new float[3];
    ListItem.FloatPtr[0] = x;
    ListItem.FloatPtr[1] = y;
    ListItem.FloatPtr[2] = z;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_normal(x, y, z);
    }
  }

  public void gl_index(int c) {
    ListItem = new gl_list_item(NODE_INDEX);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = c;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_index(c);
    }
  }

  public void gl_color(float red, float green, float blue, float alpha) {
    ListItem = new gl_list_item(NODE_COLOR);
    ListItem.FloatPtr = new float[4];
    ListItem.FloatPtr[0] = red;
    ListItem.FloatPtr[1] = green;
    ListItem.FloatPtr[2] = blue;
    ListItem.FloatPtr[3] = alpha;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_color(red, green, blue, alpha);
    }
  }

  public void gl_tex_coord(float s, float t, float r, float q) {
    ListItem = new gl_list_item(NODE_TEX_COORD);
    ListItem.FloatPtr = new float[4];
    ListItem.FloatPtr[0] = s;
    ListItem.FloatPtr[1] = t;
    ListItem.FloatPtr[2] = r;
    ListItem.FloatPtr[3] = q;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_coord(s, t, r, q);
    }
  }

  public void gl_raster_pos(float x, float y, float z, float w) {
    ListItem = new gl_list_item(NODE_RASTER_POS);
    ListItem.FloatPtr = new float[4];
    ListItem.FloatPtr[0] = x;
    ListItem.FloatPtr[1] = y;
    ListItem.FloatPtr[2] = z;
    ListItem.FloatPtr[3] = w;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_raster_pos(x, y, z, w);
    }
  }

  public void gl_shade_model(int mode) {
    ListItem = new gl_list_item(NODE_SHADE_MODEL);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_shade_model(mode);
    }
  }

  public void gl_light(int light, int pname, float params[]) {
    ListItem = new gl_list_item(NODE_LIGHT);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = light;
    ListItem.IntPtr[1] = pname;
    ListItem.FloatPtr = params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_light(light, pname, params);
    }
  }

  public float[] gl_get_light(int light, int pname) {
    return (Context.gl_get_light(light, pname));
  }

  public void gl_light_model(int pname, float params[]) {
    ListItem = new gl_list_item(NODE_LIGHT_MODEL);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = pname;
    ListItem.FloatPtr = params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_light_model(pname, params);
    }
  }

  public void gl_material(int face, int pname, float params[]) {
    ListItem = new gl_list_item(NODE_MATERIAL);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = face;
    ListItem.IntPtr[1] = pname;
    ListItem.FloatPtr = params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_material(face, pname, params);
    }
  }

  public float[] gl_get_material(int face, int pname) {
    return (Context.gl_get_material(face, pname));
  }

  public void gl_color_material(int face, int mode) {
    ListItem = new gl_list_item(NODE_COLOR_MATERIAL);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = face;
    ListItem.IntPtr[1] = mode;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_color_material(face, mode);
    }
  }

  public void gl_pixel_store(int pname, int param) {
    Context.gl_pixel_store(pname, param);
  }

  public void gl_pixel_transfer(int pname, float param) {
    Context.gl_pixel_transfer(pname, param);
  }

  public void gl_read_index_pixels(int x, int y, int width, int height, int size, Object pixels) {
    Context.gl_read_index_pixels(x, y, width, height, size, pixels);
  }

  public void gl_read_color_pixels(int x, int y, int width, int height, int format, int size,
      Object pixels) {
    Context.gl_read_color_pixels(x, y, width, height, format, size, pixels);
  }

  public void gl_read_stencil_pixels(int x, int y, int width, int height, int size, Object pixels) {
    Context.gl_read_stencil_pixels(x, y, width, height, size, pixels);
  }

  public void gl_read_depth_pixels(int x, int y, int width, int height, int size, Object pixels) {
    Context.gl_read_depth_pixels(x, y, width, height, size, pixels);
  }

  public void gl_draw_index_pixels(int width, int height, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_DRAW_INDEX_PIXELS);
    ListItem.IntPtr = new int[3];
    ListItem.IntPtr[0] = width;
    ListItem.IntPtr[1] = height;
    ListItem.IntPtr[2] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_draw_index_pixels(width, height, size, pixels);
    }
  }

  public void gl_draw_color_pixels(int width, int height, int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_DRAW_COLOR_PIXELS);
    ListItem.IntPtr = new int[4];
    ListItem.IntPtr[0] = width;
    ListItem.IntPtr[1] = height;
    ListItem.IntPtr[2] = format;
    ListItem.IntPtr[3] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_draw_color_pixels(width, height, format, size, pixels);
    }
  }

  public void gl_draw_stencil_pixels(int width, int height, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_DRAW_COLOR_PIXELS);
    ListItem.IntPtr = new int[3];
    ListItem.IntPtr[0] = width;
    ListItem.IntPtr[1] = height;
    ListItem.IntPtr[2] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_draw_stencil_pixels(width, height, size, pixels);
    }
  }

  public void gl_draw_depth_pixels(int width, int height, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_DRAW_DEPTH_PIXELS);
    ListItem.IntPtr = new int[3];
    ListItem.IntPtr[0] = width;
    ListItem.IntPtr[1] = height;
    ListItem.IntPtr[2] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_draw_depth_pixels(width, height, size, pixels);
    }
  }

  public void gl_copy_color_pixels(int x, int y, int width, int height) {
    ListItem = new gl_list_item(NODE_COPY_COLOR_PIXELS);
    ListItem.IntPtr = new int[4];
    ListItem.IntPtr[0] = x;
    ListItem.IntPtr[1] = y;
    ListItem.IntPtr[2] = width;
    ListItem.IntPtr[3] = height;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_copy_color_pixels(x, y, width, height);
    }
  }

  public void gl_copy_stencil_pixels(int x, int y, int width, int height) {
    ListItem = new gl_list_item(NODE_COPY_STENCIL_PIXELS);
    ListItem.IntPtr = new int[4];
    ListItem.IntPtr[0] = x;
    ListItem.IntPtr[1] = y;
    ListItem.IntPtr[2] = width;
    ListItem.IntPtr[3] = height;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_copy_stencil_pixels(x, y, width, height);
    }
  }

  public void gl_copy_depth_pixels(int x, int y, int width, int height) {
    ListItem = new gl_list_item(NODE_COPY_DEPTH_PIXELS);
    ListItem.IntPtr = new int[4];
    ListItem.IntPtr[0] = x;
    ListItem.IntPtr[1] = y;
    ListItem.IntPtr[2] = width;
    ListItem.IntPtr[3] = height;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_copy_depth_pixels(x, y, width, height);
    }
  }

  public void gl_stencil_func(int func, int ref, int mask) {
    ListItem = new gl_list_item(NODE_STENCIL_FUNC);
    ListItem.IntPtr = new int[3];
    ListItem.IntPtr[0] = func;
    ListItem.IntPtr[1] = ref;
    ListItem.IntPtr[2] = mask;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_stencil_func(func, ref, mask);
    }
  }

  public void gl_stencil_mask(int mask) {
    ListItem = new gl_list_item(NODE_STENCIL_MASK);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = mask;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_stencil_mask(mask);
    }
  }

  public void gl_stencil_op(int fail, int zfail, int zpass) {
    ListItem = new gl_list_item(NODE_STENCIL_OP);
    ListItem.IntPtr = new int[3];
    ListItem.IntPtr[0] = fail;
    ListItem.IntPtr[1] = zfail;
    ListItem.IntPtr[2] = zpass;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_stencil_op(fail, zfail, zpass);
    }
  }

  public void gl_clear_stencil(int s) {
    ListItem = new gl_list_item(NODE_CLEAR_STENCIL);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = s;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_clear_stencil(s);
    }
  }

  public void gl_tex_gen_i(int coord, int param) {
    ListItem = new gl_list_item(NODE_TEX_GEN_I);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = coord;
    ListItem.IntPtr[1] = param;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_gen_i(coord, param);
    }
  }

  public void gl_tex_gen_f(int coord, int pname, float params[]) {
    ListItem = new gl_list_item(NODE_TEX_GEN_F);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = coord;
    ListItem.IntPtr[1] = pname;
    ListItem.FloatPtr = params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_gen_f(coord, pname, params);
    }
  }

  public void gl_tex_env_i(int param) {
    ListItem = new gl_list_item(NODE_TEX_ENV_I);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = param;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_env_i(param);
    }
  }

  public void gl_tex_env_f(float params[]) {
    ListItem = new gl_list_item(NODE_TEX_ENV_F);
    ListItem.FloatPtr = params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_env_f(params);
    }
  }

  public void gl_tex_parameter(int target, int pname, float params[]) {
    ListItem = new gl_list_item(NODE_TEX_PARAMETER);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = pname;
    ListItem.ObjPtr = (Object) params;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_parameter(target, pname, params);
    }
  }

  public void gl_tex_image_1d(int target, int level, int components, int width, int border,
      int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_IMAGE_1D);
    ListItem.IntPtr = new int[7];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = components;
    ListItem.IntPtr[3] = width;
    ListItem.IntPtr[4] = border;
    ListItem.IntPtr[5] = format;
    ListItem.IntPtr[6] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_image_1d(target, level, components, width, border, format, size, pixels);
    }
  }

  public void gl_tex_image_2d(int target, int level, int components, int width, int height,
      int border,
      // int format, int type, byte pixels [][][]) {
      int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_IMAGE_2D);
    ListItem.IntPtr = new int[8];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = components;
    ListItem.IntPtr[3] = width;
    ListItem.IntPtr[4] = height;
    ListItem.IntPtr[5] = border;
    ListItem.IntPtr[6] = format;
    ListItem.IntPtr[7] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_image_2d(target, level, components, width, height, border, format, size,
          pixels);
    }
  }

  public void gl_tex_image_3d(int target, int level, int components, int width, int height,
      int depth, int border, int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_IMAGE_3D);
    ListItem.IntPtr = new int[9];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = components;
    ListItem.IntPtr[3] = width;
    ListItem.IntPtr[4] = height;
    ListItem.IntPtr[5] = depth;
    ListItem.IntPtr[6] = border;
    ListItem.IntPtr[7] = format;
    ListItem.IntPtr[8] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_image_3d(target, level, components, width, height, depth, border, format, size,
          pixels);
    }
  }

  public void gl_bind_texture(int target, int texture) {
    ListItem = new gl_list_item(NODE_BIND_TEXTURE);
    ListItem.IntPtr = new int[2];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = texture;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_bind_texture(target, texture);
    }
  }

  public void gl_tex_sub_image_1d(int target, int level, int xoffset, int width, int format,
      int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_SUB_IMAGE_1D);
    ListItem.IntPtr = new int[6];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = xoffset;
    ListItem.IntPtr[3] = width;
    ListItem.IntPtr[4] = format;
    ListItem.IntPtr[5] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_sub_image_1d(target, level, xoffset, width, format, size, pixels);
    }
  }

  public void gl_tex_sub_image_2d(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_SUB_IMAGE_2D);
    ListItem.IntPtr = new int[8];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = xoffset;
    ListItem.IntPtr[3] = yoffset;
    ListItem.IntPtr[4] = width;
    ListItem.IntPtr[5] = height;
    ListItem.IntPtr[6] = format;
    ListItem.IntPtr[7] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_sub_image_2d(target, level, xoffset, yoffset, width, height, format, size,
          pixels);
    }
  }

  public void gl_tex_sub_image_3d(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int size, Object pixels) {
    ListItem = new gl_list_item(NODE_TEX_SUB_IMAGE_3D);
    ListItem.IntPtr = new int[10];
    ListItem.IntPtr[0] = target;
    ListItem.IntPtr[1] = level;
    ListItem.IntPtr[2] = xoffset;
    ListItem.IntPtr[3] = yoffset;
    ListItem.IntPtr[4] = zoffset;
    ListItem.IntPtr[5] = width;
    ListItem.IntPtr[6] = height;
    ListItem.IntPtr[7] = depth;
    ListItem.IntPtr[8] = format;
    ListItem.IntPtr[9] = size;
    ListItem.ObjPtr = pixels;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_tex_sub_image_3d(target, level, xoffset, yoffset, zoffset, width, height, depth,
          format, size, pixels);
    }
  }

  /*
   * public int gl_map_1 (int target, float u1, float u2, int stride, int order, float points [][])
   * { ListItem = new gl_list_item (NODE_MAP_1); ListItem.IntPtr = new int [3]; ListItem.IntPtr [0]
   * = target; ListItem.IntPtr [1] = stride; ListItem.IntPtr [2] = order; ListItem.FloatPtr = new
   * float [2]; ListItem.FloatPtr [0] = u1; ListItem.FloatPtr [1] = u2;
   * 
   * int i, j, dim; switch (target) { case GL.GL_MAP1_VERTEX_3: dim = 3; break; case
   * GL.GL_MAP1_VERTEX_4: dim = 4; break; case GL.GL_MAP1_INDEX: dim = 1; break; case
   * GL.GL_MAP1_COLOR_4: dim = 4; break; case GL.GL_MAP1_NORMAL: dim = 3; break; case
   * GL.GL_MAP1_TEXTURE_COORD_1: dim = 1; break; case GL.GL_MAP1_TEXTURE_COORD_2: dim = 2; break;
   * case GL.GL_MAP1_TEXTURE_COORD_3: dim = 3; break; case GL.GL_MAP1_TEXTURE_COORD_4: dim = 4;
   * break; default: dim = 0; }
   * 
   * float temp [][] = new float [order][dim]; for (i = 0; i < order; i++) for (j = 0; j < dim; j++)
   * temp[i][j] = points[i][j];
   * 
   * ListItem.ObjPtr = (Object)temp; ThisList.addElement (ListItem); if (ExecuteFlag) {
   * Context.gl_map_1 (target, u1, u2, stride, order, points); } return dim; }
   * 
   * public int gl_map_2 (int target, float u1, float u2, int ustride, int uorder, float v1, float
   * v2, int vstride, int vorder, float points [][][]) { ListItem = new gl_list_item (NODE_MAP_2);
   * ListItem.IntPtr = new int [5]; ListItem.IntPtr [0] = target; ListItem.IntPtr [1] = ustride;
   * ListItem.IntPtr [2] = uorder; ListItem.IntPtr [3] = vstride; ListItem.IntPtr [4] = vorder;
   * ListItem.FloatPtr = new float [4]; ListItem.FloatPtr [0] = u1; ListItem.FloatPtr [1] = u2;
   * ListItem.FloatPtr [2] = v1; ListItem.FloatPtr [3] = v2;
   * 
   * int i, j, k, dim; switch (target) { case GL.GL_MAP2_VERTEX_3: dim = 3; break; case
   * GL.GL_MAP2_VERTEX_4: dim = 4; break; case GL.GL_MAP2_INDEX: dim = 1; break; case
   * GL.GL_MAP2_COLOR_4: dim = 4; break; case GL.GL_MAP2_NORMAL: dim = 3; break; case
   * GL.GL_MAP2_TEXTURE_COORD_1: dim = 1; break; case GL.GL_MAP2_TEXTURE_COORD_2: dim = 2; break;
   * case GL.GL_MAP2_TEXTURE_COORD_3: dim = 3; break; case GL.GL_MAP2_TEXTURE_COORD_4: dim = 4;
   * break; default: dim = 0; }
   * 
   * float temp [][][] = new float [uorder][vorder][dim]; for (i = 0; i < uorder; i++) for (j = 0; j
   * < vorder; j++) for (k = 0; k < dim; k++) temp[i][j][k] = points[i][j][k];
   * 
   * ListItem.ObjPtr = (Object)temp; ThisList.addElement (ListItem); if (ExecuteFlag) {
   * Context.gl_map_2 (target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points); } return
   * dim; }
   * 
   * public void gl_eval_coord_1 (float u) { ListItem = new gl_list_item (NODE_EVAL_COORD_1);
   * ListItem.FloatPtr = new float [1]; ListItem.FloatPtr [0] = u; ThisList.addElement (ListItem);
   * if (ExecuteFlag) { Context.gl_eval_coord_1 (u); } }
   * 
   * public void gl_eval_coord_2 (float u, float v) { ListItem = new gl_list_item
   * (NODE_EVAL_COORD_2); ListItem.FloatPtr = new float [2]; ListItem.FloatPtr [0] = u;
   * ListItem.FloatPtr [1] = v; ThisList.addElement (ListItem); if (ExecuteFlag) {
   * Context.gl_eval_coord_2 (u, v); } }
   * 
   * public void gl_map_grid_1 (int un, float u1, float u2) { ListItem = new gl_list_item
   * (NODE_MAP_GRID_1); ListItem.IntPtr = new int [1]; ListItem.IntPtr [0] = un; ListItem.FloatPtr =
   * new float [2]; ListItem.FloatPtr [0] = u1; ListItem.FloatPtr [1] = u2; ThisList.addElement
   * (ListItem); if (ExecuteFlag) { Context.gl_map_grid_1 (un, u1, u2); } }
   * 
   * public void gl_map_grid_2 (int un, float u1, float u2, int vn, float v1, float v2) { ListItem =
   * new gl_list_item (NODE_MAP_GRID_2); ListItem.IntPtr = new int [2]; ListItem.IntPtr [0] = un;
   * ListItem.IntPtr [1] = vn; ListItem.FloatPtr = new float [4]; ListItem.FloatPtr [0] = u1;
   * ListItem.FloatPtr [1] = u2; ListItem.FloatPtr [2] = v1; ListItem.FloatPtr [3] = v2;
   * ThisList.addElement (ListItem); if (ExecuteFlag) { Context.gl_map_grid_2 (un, u1, u2, vn, v1,
   * v2); } }
   * 
   * public void gl_eval_point_1 (int i) { ListItem = new gl_list_item (NODE_EVAL_POINT_1);
   * ListItem.IntPtr = new int [1]; ListItem.IntPtr [0] = i; ThisList.addElement (ListItem); if
   * (ExecuteFlag) { Context.gl_eval_point_1 (i); } }
   * 
   * public void gl_eval_point_2 (int i, int j) { ListItem = new gl_list_item (NODE_EVAL_POINT_2);
   * ListItem.IntPtr = new int [2]; ListItem.IntPtr [0] = i; ListItem.IntPtr [1] = j;
   * ThisList.addElement (ListItem); if (ExecuteFlag) { Context.gl_eval_point_2 (i, j); } }
   * 
   * public void gl_eval_mesh_1 (int mode, int nu1, int nu2) { ListItem = new gl_list_item
   * (NODE_EVAL_MESH_1); ListItem.IntPtr = new int [3]; ListItem.IntPtr [0] = mode; ListItem.IntPtr
   * [1] = nu1; ListItem.IntPtr [2] = nu2; ThisList.addElement (ListItem); if (ExecuteFlag) {
   * Context.gl_eval_mesh_1 (mode, nu1, nu2); } }
   * 
   * public void gl_eval_mesh_2 (int mode, int nu1, int nu2, int nv1, int nv2) { ListItem = new
   * gl_list_item (NODE_EVAL_MESH_2); ListItem.IntPtr = new int [5]; ListItem.IntPtr [0] = mode;
   * ListItem.IntPtr [1] = nu1; ListItem.IntPtr [2] = nu2; ListItem.IntPtr [3] = nv1;
   * ListItem.IntPtr [4] = nv2; ThisList.addElement (ListItem); if (ExecuteFlag) {
   * Context.gl_eval_mesh_2 (mode, nu1, nu2, nv1, nv2); } }
   */

  public void gl_feedback_buffer(int size, int type, float buffer[]) {
    Context.gl_feedback_buffer(size, type, buffer);
  }

  public void gl_pass_through(float token) {
    ListItem = new gl_list_item(NODE_PASS_THROUGH);
    ListItem.FloatPtr = new float[1];
    ListItem.FloatPtr[0] = token;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_pass_through(token);
    }
  }

  public void gl_select_buffer(int size, int buffer[]) {
    Context.gl_select_buffer(size, buffer);
  }

  public void gl_init_names() {
    ListItem = new gl_list_item(NODE_INIT_NAMES);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_init_names();
    }
  }

  public void gl_load_name(int name) {
    ListItem = new gl_list_item(NODE_LOAD_NAME);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = name;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_load_name(name);
    }
  }

  public void gl_push_name(int name) {
    ListItem = new gl_list_item(NODE_PUSH_NAME);
    ListItem.IntPtr = new int[1];
    ListItem.IntPtr[0] = name;
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_push_name(name);
    }
  }

  public void gl_pop_name() {
    ListItem = new gl_list_item(NODE_POP_NAME);
    ThisList.addElement(ListItem);
    if (ExecuteFlag) {
      Context.gl_pop_name();
    }
  }

  /*
   * public gl_list () { ExecuteFlag = false; }
   */

  public gl_list(gl_context context, boolean exec) {
    // ExecuteFlag = true;
    ExecuteFlag = exec;
    Context = context;
    RenderMode = Context.RenderMode;
    Mode = Context.Mode;
    Eval = Context.Eval;
  }

}
