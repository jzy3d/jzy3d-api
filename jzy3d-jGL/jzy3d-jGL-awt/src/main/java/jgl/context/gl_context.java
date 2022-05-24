/*
 * @(#)gl_context.java 0.10 06/11/21
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2006 Robin Bing-Yu Chen (robin@ntu.edu.tw)
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

import java.util.Stack;
import java.util.Vector;
import jgl.GL;
import jgl.context.attrib.gl_colorbuffer;
import jgl.context.attrib.gl_current;
import jgl.context.attrib.gl_depthbuffer;
import jgl.context.attrib.gl_eval;
import jgl.context.attrib.gl_lighting;
import jgl.context.attrib.gl_pixel;
import jgl.context.attrib.gl_raster;
import jgl.context.attrib.gl_stencilbuffer;
import jgl.context.attrib.gl_texture;
import jgl.context.attrib.gl_transform;
import jgl.context.attrib.gl_viewport;
import jgl.context.attrib.texture.gl_texture_obj;

/**
 * gl_context is the context class of jGL 2.4.
 *
 * @version 0.10, 21 Nov 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_context extends gl_object {


  /** GL_MODELVIEW_MATRIX: Modelview matrix stack */
  public float ModelViewMatrix[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
  public float ModelViewInv[] = {1, 0, 0, 0, /* Inverse of ModelViewMatrix */
      0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
  public boolean ModelViewInvValid = true; /* Is inverse matrix valid? */

  /** GL_MODELVIEW_STACK_DEPTH: Modelview matrix stack pointer */
  public Stack<float[]> ModelViewStack = new Stack<float[]>();

  /** GL_PROJECTION_MATRIX: Projection matrix stack */
  public float ProjectionMatrix[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

  /** GL_PROJECTION_STACK_DEPTH: Projection matrix stack pointer */
  public Stack<float[]> ProjectionStack = new Stack<float[]>();

  /** GL_TEXTURE_MATRIX: Texture matrix stack */
  public float TextureMatrix[] = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};

  public boolean IdentityTexMat = true; /* Is TextureMatrix Identity? */

  /** GL_TEXTURE_STACK_DEPTH: Texture matrix stack pointer */
  public Stack<float[]> TextureStack = new Stack<float[]>();

  /** All other classes for state variables */
  public gl_current Current = new gl_current();
  public gl_transform Transform = new gl_transform();
  public gl_viewport Viewport = new gl_viewport();
  // skip fog
  public gl_lighting Lighting = new gl_lighting();
  public gl_raster Raster = new gl_raster();
  public gl_texture Texture = new gl_texture();
  public gl_colorbuffer ColorBuffer = new gl_colorbuffer(this);
  public gl_depthbuffer DepthBuffer = new gl_depthbuffer(this);
  public gl_stencilbuffer StencilBuffer = new gl_stencilbuffer(this);
  // skip accumbuffer
  public gl_pixel Pixel = new gl_pixel();
  // skip hint

  /** All other classes for capabilities */
  public gl_select Select = new gl_select();
  public gl_feedback Feedback = new gl_feedback();

  /** Attrib stack */
  public Stack<gl_list_item> AttribStack = new Stack<gl_list_item>();

  /** Current pointer to clipping, geometry, rendering classes */
  public gl_pointer CR = new gl_pointer(this);

  /** The eye coord of Current.Vertex and Current.Normal */
  public float EyeCoord[];
  public float EyeNormal[];

  /** GL_LIST_BASE: Setting of glListBase () */
  public int ListBase = 0;

  /** GL_LIST_INDEX: Number of display list under construction */
  public int ListIndex = 0;

  /** GL_LIST_MODE: Mode of display list under construction */
  public int ListMode = 0;

  /** the vector of display list group */
  public Vector<gl_list> ListGroup = new Vector<gl_list>();

  /** the vector of texture list */
  public Vector<gl_texture_obj> TexList = new Vector<gl_texture_obj>();

  public gl_context() {
    RenderMode = GL.GL_RENDER;
    Eval = new gl_eval();
  }

  /**
   * Public Member Functions for GL
   */
  public float[] ModelViewTransformation(float ObjCoord[]) {
    return gl_util.mulMatrix41(ModelViewMatrix, ObjCoord);
  }

  public float[] NormalTransformation(float NorCoord[]) {
    float nor[] = gl_util.mulMatrix31(ModelViewMatrix, NorCoord);
    if (Transform.Normalize) {
      gl_util.normalize(nor);
    }
    return nor;
  }

  public float[] VertexTransformation(float EyeCoord[]) {
    return gl_util.mulMatrix41(ProjectionMatrix, EyeCoord);
  }

  public float[] TextureTransformation(float TexCoord[]) {
    return gl_util.mulMatrix41(TextureMatrix, TexCoord);
  }

  public float[] PerspectiveDivision(float ClipCoord[]) {
    if (ClipCoord[3] != 1 && ClipCoord[3] != 0) {
      ClipCoord[0] /= ClipCoord[3];
      ClipCoord[1] /= ClipCoord[3];
      ClipCoord[2] /= ClipCoord[3];
    } else {
      if (ClipCoord[3] == 0) {
        ClipCoord[0] = ClipCoord[1] = ClipCoord[2] = 0;
      }
    }
    return (Viewport.Transformation(ClipCoord));
  }

  public int ColorTransformation() {
    if (Lighting.Enable) {
      // Before call Coloring procedure, remember to prepare
      // EyeCoord of CurrentVertex and CurrentNormal....
      EyeNormal = NormalTransformation(Current.Normal);
      return (Lighting.color_vertex(EyeCoord, EyeNormal));
    } else {
      return (Current.IntColor);
    }
  }

  public void gl_initialize_context() {
    ProjectionMatrix[0] = 1.0f / Viewport.Sx;
    ProjectionMatrix[5] = 1.0f / Viewport.Sy;
    ProjectionMatrix[10] = 1;
    ProjectionMatrix[12] = -1;
    ProjectionMatrix[13] = -1;
    ProjectionMatrix[15] = 1;
  }

  public void gl_initialize_context(int w, int h) {
    gl_viewport(0, 0, w, h);
    gl_initialize_context();
    /*
     * ProjectionMatrix [0] = 1.0f / Viewport.Sx; ProjectionMatrix [5] = 1.0f / Viewport.Sy;
     * ProjectionMatrix [10] = 1; ProjectionMatrix [12] = -1; ProjectionMatrix [13] = -1;
     * ProjectionMatrix [15] = 1;
     */
  }

  public void gl_clear_color(float r, float g, float b, float a) {
    ColorBuffer.set_clear_color(r, g, b, a);
  }

  public void gl_clear_depth_buffer() {
    if (RenderMode != GL.GL_RENDER) {
      return;
    }
    DepthBuffer.clear_buffer(Viewport.Size);
  }

  public void gl_clear_color_buffer() {
    if (RenderMode != GL.GL_RENDER) {
      return;
    }
    ColorBuffer.clear_buffer(Viewport.Size);
  }

  public void gl_clear_stencil_buffer() {
    if (RenderMode != GL.GL_RENDER) {
      return;
    }
    StencilBuffer.clear_buffer(Viewport.Size);
  }

  public void gl_color_mask(boolean red, boolean green, boolean blue, boolean alpha) {
    ColorBuffer.set_color_mask(red, green, blue, alpha);
  }

  public void gl_blend_func(int sfactor, int dfactor) {
    ColorBuffer.BlendSrc = sfactor;
    ColorBuffer.BlendDst = dfactor;
  }

  public void gl_cull_face(int mode) {
    Raster.CullFaceMode = mode;
  }

  public void gl_front_face(int mode) {
    Raster.FrontFace = mode;
  }

  public void gl_point_size(float size) {
    Raster.PointSize = size;
    if (size != 1)
      CR.gl_point_size(true);
    else
      CR.gl_point_size(false);
  }

  public void gl_line_width(float width) {
    Raster.LineWidth = width;
  }

  public void gl_line_stipple(int factor, short pattern) {
    Raster.LineStippleRepeat = factor;
    Raster.LineStipplePattern = pattern;
  }

  public void gl_polygon_mode(int face, int mode) {
    Raster.set_polygon_mode(face, mode);
  }

  public void gl_polygon_stipple(byte mask[]) {
    Raster.set_polygon_stipple(mask);
  }

  public void gl_clip_plane(int plane, float eq[]) {
    if (!ModelViewInvValid) {
      ModelViewInv = gl_util.inverseMatrix44(ModelViewMatrix);
      ModelViewInvValid = true;
    }
    Transform.ClipPlane[plane] = gl_util.mulMatrix41(ModelViewInv, eq);
  }

  public float[] gl_get_clip_plane(int plane) {
    return (Transform.ClipPlane[plane]);
  }

  public void gl_enable(int cap, boolean state) {
    // int i;
    switch (cap) {
      case GL.GL_ALPHA_TEST:
        ColorBuffer.AlphaEnable = state;
        break;
      case GL.GL_AUTO_NORMAL:
        Eval.AutoNormal = state;
        break;
      case GL.GL_BLEND:
        ColorBuffer.BlendEnable = state;
        CR.gl_blending(state);
        break;
      case GL.GL_CLIP_PLANE0:
      case GL.GL_CLIP_PLANE1:
      case GL.GL_CLIP_PLANE2:
      case GL.GL_CLIP_PLANE3:
      case GL.GL_CLIP_PLANE4:
      case GL.GL_CLIP_PLANE5:
        CR.gl_clipping(Transform.clip_enable(cap - GL.GL_CLIP_PLANE0, state));
        break;
      case GL.GL_COLOR_MATERIAL:
        Lighting.ColorMaterial = state;
        break;
      case GL.GL_CULL_FACE:
        Raster.CullFace = state;
        break;
      case GL.GL_DEPTH_TEST:
        DepthBuffer.Enable = state;
        CR.gl_depth(state);
        break;
      case GL.GL_DITHER:
        ColorBuffer.DitherEnable = state;
        break;
      case GL.GL_FOG:
        break;
      case GL.GL_LIGHT0:
      case GL.GL_LIGHT1:
      case GL.GL_LIGHT2:
      case GL.GL_LIGHT3:
      case GL.GL_LIGHT4:
      case GL.GL_LIGHT5:
      case GL.GL_LIGHT6:
      case GL.GL_LIGHT7:
        Lighting.Light[cap - GL.GL_LIGHT0].Enable = state;
        break;
      case GL.GL_LIGHTING:
        Lighting.Enable = state;
        CR.gl_lighting(state);
        break;
      case GL.GL_LINE_SMOOTH:
        Raster.LineSmooth = state;
        break;
      case GL.GL_LINE_STIPPLE:
        Raster.LineStipple = state;
        CR.gl_stipple_line(state);
        break;
      case GL.GL_LOGIC_OP:
        ColorBuffer.LogicOPEnable = state;
        break;
      case GL.GL_MAP1_COLOR_4:
        Eval.Map1Color4Enable = state;
        break;
      case GL.GL_MAP1_INDEX:
        Eval.Map1IndexEnable = state;
        break;
      case GL.GL_MAP1_NORMAL:
        Eval.Map1NormalEnable = state;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_1:
        Eval.Map1TexCoord1Enable = state;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_2:
        Eval.Map1TexCoord2Enable = state;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_3:
        Eval.Map1TexCoord3Enable = state;
        break;
      case GL.GL_MAP1_TEXTURE_COORD_4:
        Eval.Map1TexCoord4Enable = state;
        break;
      case GL.GL_MAP1_VERTEX_3:
        Eval.Map1Vertex3Enable = state;
        break;
      case GL.GL_MAP1_VERTEX_4:
        Eval.Map1Vertex3Enable = state;
        break;
      case GL.GL_MAP2_COLOR_4:
        Eval.Map2Color4Enable = state;
        break;
      case GL.GL_MAP2_INDEX:
        Eval.Map2IndexEnable = state;
        break;
      case GL.GL_MAP2_NORMAL:
        Eval.Map2NormalEnable = state;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_1:
        Eval.Map2TexCoord1Enable = state;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_2:
        Eval.Map2TexCoord2Enable = state;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_3:
        Eval.Map2TexCoord3Enable = state;
        break;
      case GL.GL_MAP2_TEXTURE_COORD_4:
        Eval.Map2TexCoord4Enable = state;
        break;
      case GL.GL_MAP2_VERTEX_3:
        Eval.Map2Vertex3Enable = state;
        break;
      case GL.GL_MAP2_VERTEX_4:
        Eval.Map2Vertex4Enable = state;
        break;
      case GL.GL_NORMALIZE:
        Transform.Normalize = state;
        break;
      case GL.GL_POINT_SMOOTH:
        Raster.PointSmooth = state;
        break;
      case GL.GL_POLYGON_SMOOTH:
        Raster.PolygonSmooth = state;
        break;
      case GL.GL_POLYGON_STIPPLE:
        Raster.PolygonStipple = state;
        CR.gl_stipple_poly(state);
        break;
      case GL.GL_POLYGON_OFFSET_EXT:
      case GL.GL_SCISSOR_TEST:
        break;
      case GL.GL_STENCIL_TEST:
        StencilBuffer.Enable = state;
        // CR.gl_depth (state);
        break;
      case GL.GL_TEXTURE_1D:
      case GL.GL_TEXTURE_2D:
      case GL.GL_TEXTURE_3D:
        CR.gl_texture(Texture.tex_enable(cap, state));
        break;
      case GL.GL_TEXTURE_GEN_Q:
      case GL.GL_TEXTURE_GEN_R:
      case GL.GL_TEXTURE_GEN_S:
      case GL.GL_TEXTURE_GEN_T:
        Texture.tex_gen_enable(cap, state);
        break;
      default:
        break;
    }
  }

  public boolean gl_is_enabled(int cap) {
    switch (cap) {
      case GL.GL_ALPHA_TEST:
        break;
      case GL.GL_AUTO_NORMAL:
        return (Eval.AutoNormal);
      case GL.GL_BLEND:
        return (ColorBuffer.BlendEnable);
      case GL.GL_CLIP_PLANE0:
      case GL.GL_CLIP_PLANE1:
      case GL.GL_CLIP_PLANE2:
      case GL.GL_CLIP_PLANE3:
      case GL.GL_CLIP_PLANE4:
      case GL.GL_CLIP_PLANE5:
        return (Transform.ClipEnable[cap - GL.GL_CLIP_PLANE0]);
      case GL.GL_COLOR_MATERIAL:
        return (Lighting.ColorMaterial);
      case GL.GL_CULL_FACE:
        return (Raster.CullFace);
      case GL.GL_DEPTH_TEST:
        return (DepthBuffer.Enable);
      case GL.GL_DITHER:
      case GL.GL_FOG:
        break;
      case GL.GL_LIGHT0:
      case GL.GL_LIGHT1:
      case GL.GL_LIGHT2:
      case GL.GL_LIGHT3:
      case GL.GL_LIGHT4:
      case GL.GL_LIGHT5:
      case GL.GL_LIGHT6:
      case GL.GL_LIGHT7:
        return (Lighting.Light[cap - GL.GL_LIGHT0].Enable);
      case GL.GL_LIGHTING:
        return (Lighting.Enable);
      case GL.GL_LINE_SMOOTH:
        return (Raster.LineSmooth);
      case GL.GL_LINE_STIPPLE:
        return (Raster.LineStipple);
      case GL.GL_LOGIC_OP:
        break;
      case GL.GL_MAP1_COLOR_4:
        return (Eval.Map1Color4Enable);
      case GL.GL_MAP1_INDEX:
        return (Eval.Map1IndexEnable);
      case GL.GL_MAP1_NORMAL:
        return (Eval.Map1NormalEnable);
      case GL.GL_MAP1_TEXTURE_COORD_1:
        return (Eval.Map1TexCoord1Enable);
      case GL.GL_MAP1_TEXTURE_COORD_2:
        return (Eval.Map1TexCoord2Enable);
      case GL.GL_MAP1_TEXTURE_COORD_3:
        return (Eval.Map1TexCoord3Enable);
      case GL.GL_MAP1_TEXTURE_COORD_4:
        return (Eval.Map1TexCoord4Enable);
      case GL.GL_MAP1_VERTEX_3:
        return (Eval.Map1Vertex3Enable);
      case GL.GL_MAP1_VERTEX_4:
        return (Eval.Map1Vertex4Enable);
      case GL.GL_MAP2_COLOR_4:
        return (Eval.Map2Color4Enable);
      case GL.GL_MAP2_INDEX:
        return (Eval.Map2IndexEnable);
      case GL.GL_MAP2_NORMAL:
        return (Eval.Map2NormalEnable);
      case GL.GL_MAP2_TEXTURE_COORD_1:
        return (Eval.Map2TexCoord1Enable);
      case GL.GL_MAP2_TEXTURE_COORD_2:
        return (Eval.Map2TexCoord2Enable);
      case GL.GL_MAP2_TEXTURE_COORD_3:
        return (Eval.Map2TexCoord3Enable);
      case GL.GL_MAP2_TEXTURE_COORD_4:
        return (Eval.Map2TexCoord4Enable);
      case GL.GL_MAP2_VERTEX_3:
        return (Eval.Map2Vertex3Enable);
      case GL.GL_MAP2_VERTEX_4:
        return (Eval.Map2Vertex4Enable);
      case GL.GL_NORMALIZE:
        return (Transform.Normalize);
      case GL.GL_POINT_SMOOTH:
        return (Raster.PointSmooth);
      case GL.GL_POLYGON_SMOOTH:
        return (Raster.PolygonSmooth);
      case GL.GL_POLYGON_STIPPLE:
        return (Raster.PolygonStipple);
      case GL.GL_POLYGON_OFFSET_EXT:
      case GL.GL_SCISSOR_TEST:
      case GL.GL_STENCIL_TEST:
        break;
      case GL.GL_TEXTURE_1D:
      case GL.GL_TEXTURE_2D:
      case GL.GL_TEXTURE_3D:
        return Texture.is_tex_enabled(cap);
      case GL.GL_TEXTURE_GEN_Q:
      case GL.GL_TEXTURE_GEN_R:
      case GL.GL_TEXTURE_GEN_S:
      case GL.GL_TEXTURE_GEN_T:
        return Texture.is_tex_gen_enabled(cap);
      default:
        break;
    }
    return false;
  }

  private void gl_enable_push_attrib(gl_list_item AttribItem) {
    AttribItem.BoolPtr = new boolean[51];
    AttribItem.IntPtr = new int[2];
    int i;

    AttribItem.BoolPtr[0] = false; // GL_ALPHA_TEST
    AttribItem.BoolPtr[1] = Eval.AutoNormal;
    AttribItem.BoolPtr[2] = ColorBuffer.BlendEnable;
    for (i = 0; i < 6; i++) {
      AttribItem.BoolPtr[i + 3] = Transform.ClipEnable[i];
    }
    AttribItem.BoolPtr[9] = Lighting.ColorMaterial;
    AttribItem.BoolPtr[10] = Raster.CullFace;
    AttribItem.BoolPtr[11] = DepthBuffer.Enable;
    AttribItem.BoolPtr[12] = false; // GL_DITHER
    AttribItem.BoolPtr[13] = false; // GL_FOG
    for (i = 0; i < 8; i++) {
      AttribItem.BoolPtr[i + 14] = Lighting.Light[i].Enable;
    }
    AttribItem.BoolPtr[22] = Lighting.Enable;
    AttribItem.BoolPtr[23] = Raster.LineSmooth;
    AttribItem.BoolPtr[24] = Raster.LineStipple;
    AttribItem.BoolPtr[25] = false; // GL_LOGIC_OP
    AttribItem.BoolPtr[26] = Eval.Map1Vertex3Enable;
    AttribItem.BoolPtr[27] = Eval.Map1Vertex4Enable;
    AttribItem.BoolPtr[28] = Eval.Map1IndexEnable;
    AttribItem.BoolPtr[29] = Eval.Map1Color4Enable;
    AttribItem.BoolPtr[30] = Eval.Map1NormalEnable;
    AttribItem.BoolPtr[31] = Eval.Map1TexCoord1Enable;
    AttribItem.BoolPtr[32] = Eval.Map1TexCoord2Enable;
    AttribItem.BoolPtr[33] = Eval.Map1TexCoord3Enable;
    AttribItem.BoolPtr[34] = Eval.Map1TexCoord4Enable;
    AttribItem.BoolPtr[35] = Eval.Map2Vertex3Enable;
    AttribItem.BoolPtr[36] = Eval.Map2Vertex4Enable;
    AttribItem.BoolPtr[37] = Eval.Map2IndexEnable;
    AttribItem.BoolPtr[38] = Eval.Map2Color4Enable;
    AttribItem.BoolPtr[39] = Eval.Map2NormalEnable;
    AttribItem.BoolPtr[40] = Eval.Map2TexCoord1Enable;
    AttribItem.BoolPtr[41] = Eval.Map2TexCoord2Enable;
    AttribItem.BoolPtr[42] = Eval.Map2TexCoord3Enable;
    AttribItem.BoolPtr[43] = Eval.Map2TexCoord4Enable;
    AttribItem.BoolPtr[44] = Transform.Normalize;
    AttribItem.BoolPtr[45] = Raster.PointSmooth;
    AttribItem.BoolPtr[46] = Raster.PolygonSmooth;
    AttribItem.BoolPtr[47] = Raster.PolygonStipple;
    AttribItem.BoolPtr[48] = false; // GL_POLYGON_OFFSET_EXT
    AttribItem.BoolPtr[49] = false; // GL_SCISSOR_TEST
    AttribItem.BoolPtr[50] = StencilBuffer.Enable;
    AttribItem.IntPtr[0] = Texture.is_tex_enabled();
    AttribItem.IntPtr[1] = Texture.is_tex_gen_enabled();
  }

  private void gl_enable_pop_attrib(gl_list_item AttribItem) {
    int i;
    Eval.AutoNormal = AttribItem.BoolPtr[1];
    for (i = 0; i < 6; i++) {
      Transform.ClipEnable[i] = AttribItem.BoolPtr[i + 3];
    }
    CR.gl_clipping(Transform.check_clip());

    Lighting.ColorMaterial = AttribItem.BoolPtr[9];
    Raster.CullFace = AttribItem.BoolPtr[10];
    DepthBuffer.Enable = AttribItem.BoolPtr[11];
    CR.gl_depth(DepthBuffer.Enable);
    for (i = 0; i < 8; i++) {
      Lighting.Light[i].Enable = AttribItem.BoolPtr[i + 14];
    }
    Lighting.Enable = AttribItem.BoolPtr[22];
    CR.gl_lighting(Lighting.Enable);
    Raster.LineSmooth = AttribItem.BoolPtr[23];
    Raster.LineStipple = AttribItem.BoolPtr[24];
    CR.gl_stipple_line(Raster.LineStipple);
    Eval.Map1Vertex3Enable = AttribItem.BoolPtr[26];
    Eval.Map1Vertex4Enable = AttribItem.BoolPtr[27];
    Eval.Map1IndexEnable = AttribItem.BoolPtr[28];
    Eval.Map1Color4Enable = AttribItem.BoolPtr[29];
    Eval.Map1NormalEnable = AttribItem.BoolPtr[30];
    Eval.Map1TexCoord1Enable = AttribItem.BoolPtr[31];
    Eval.Map1TexCoord2Enable = AttribItem.BoolPtr[32];
    Eval.Map1TexCoord3Enable = AttribItem.BoolPtr[33];
    Eval.Map1TexCoord4Enable = AttribItem.BoolPtr[34];
    Eval.Map2Vertex3Enable = AttribItem.BoolPtr[35];
    Eval.Map2Vertex4Enable = AttribItem.BoolPtr[36];
    Eval.Map2IndexEnable = AttribItem.BoolPtr[37];
    Eval.Map2Color4Enable = AttribItem.BoolPtr[38];
    Eval.Map2NormalEnable = AttribItem.BoolPtr[39];
    Eval.Map2TexCoord1Enable = AttribItem.BoolPtr[40];
    Eval.Map2TexCoord2Enable = AttribItem.BoolPtr[41];
    Eval.Map2TexCoord3Enable = AttribItem.BoolPtr[42];
    Eval.Map2TexCoord4Enable = AttribItem.BoolPtr[43];
    Transform.Normalize = AttribItem.BoolPtr[44];
    Raster.PointSmooth = AttribItem.BoolPtr[45];
    Raster.PolygonSmooth = AttribItem.BoolPtr[46];
    Raster.PolygonStipple = AttribItem.BoolPtr[47];
    CR.gl_stipple_poly(Raster.PolygonStipple);
    StencilBuffer.Enable = AttribItem.BoolPtr[50];
    CR.gl_texture(Texture.tex_enable(AttribItem.IntPtr[0]));
    Texture.tex_gen_enable(AttribItem.IntPtr[1]);
    // Texture.Enable = AttribItem.IntPtr [0];
    // CR.gl_texture (Texture.check_tex ());
  }

  public void gl_push_attrib(int mask) {
    gl_list_item AttribItem;
    int mask_number = 0;

    if ((mask & GL.GL_ACCUM_BUFFER_BIT) != 0) {
    }
    if ((mask & GL.GL_COLOR_BUFFER_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_COLOR_BUFFER_BIT);
      // ColorBuffer.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) ColorBuffer;
      ColorBuffer = new gl_colorbuffer(ColorBuffer);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_CURRENT_BIT) > 0) {
      AttribItem = new gl_list_item(GL.GL_CURRENT_BIT);
      // Current.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) Current;
      Current = new gl_current(Current);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_DEPTH_BUFFER_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_DEPTH_BUFFER_BIT);
      // DepthBuffer.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) DepthBuffer;
      DepthBuffer = new gl_depthbuffer(DepthBuffer);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_ENABLE_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_ENABLE_BIT);
      gl_enable_push_attrib(AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_EVAL_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_EVAL_BIT);
      // Eval.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) Eval;
      Eval = new gl_eval(Eval);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_FOG_BIT) != 0) {
    }
    if ((mask & GL.GL_HINT_BIT) != 0) {
    }
    if ((mask & GL.GL_LIGHTING_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_LIGHTING_BIT);
      // Lighting.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) Lighting;
      Lighting = new gl_lighting(Lighting);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_LINE_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_LINE_BIT);
      Raster.push_line_attrib(AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_LIST_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_LIST_BIT);
      AttribItem.IntPtr = new int[1];
      AttribItem.IntPtr[0] = ListBase;
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_PIXEL_MODE_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_PIXEL_MODE_BIT);
      // Pixel.push_attrib (AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_POINT_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_POINT_BIT);
      Raster.push_point_attrib(AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_POLYGON_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_POLYGON_BIT);
      Raster.push_polygon_attrib(AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_POLYGON_STIPPLE_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_POLYGON_STIPPLE_BIT);
      Raster.push_polygon_stipple_attrib(AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_SCISSOR_BIT) != 0) {
    }
    if ((mask & GL.GL_STENCIL_BUFFER_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_STENCIL_BUFFER_BIT);
      // DepthBuffer.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) StencilBuffer;
      StencilBuffer = new gl_stencilbuffer(StencilBuffer);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_TEXTURE_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_TEXTURE_BIT);
      // Texture.push_attrib (AttribItem);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_TRANSFORM_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_TRANSFORM_BIT);
      // Transform.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) Transform;
      Transform = new gl_transform(Transform);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    if ((mask & GL.GL_VIEWPORT_BIT) != 0) {
      AttribItem = new gl_list_item(GL.GL_VIEWPORT_BIT);
      // Viewport.push_attrib (AttribItem);
      AttribItem.ObjPtr = (Object) Viewport;
      Viewport = new gl_viewport(Viewport);
      AttribStack.push(AttribItem);
      mask_number++;
    }
    AttribItem = new gl_list_item(mask_number);
    AttribStack.push(AttribItem);
  }

  public void gl_pop_attrib() {
    gl_list_item AttribItem;
    int i, mask_number;

    AttribItem = (gl_list_item) AttribStack.pop();
    mask_number = AttribItem.NodeKind;
    for (i = 0; i < mask_number; i++) {
      AttribItem = (gl_list_item) AttribStack.pop();
      switch (AttribItem.NodeKind) {
        case GL.GL_ACCUM_BUFFER_BIT:
          break;
        case GL.GL_COLOR_BUFFER_BIT:
          // ColorBuffer.pop_attrib (AttribItem);
          ColorBuffer = (gl_colorbuffer) AttribItem.ObjPtr;
          break;
        case GL.GL_CURRENT_BIT:
          // Current.pop_attrib (AttribItem);
          Current = (gl_current) AttribItem.ObjPtr;
          break;
        case GL.GL_DEPTH_BUFFER_BIT:
          // DepthBuffer.pop_attrib (AttribItem);
          DepthBuffer = (gl_depthbuffer) AttribItem.ObjPtr;
          CR.gl_depth(DepthBuffer.Enable);
          break;
        case GL.GL_ENABLE_BIT:
          gl_enable_pop_attrib(AttribItem);
          break;
        case GL.GL_EVAL_BIT:
          // Eval.pop_attrib (AttribItem);
          Eval = (gl_eval) AttribItem.ObjPtr;
          break;
        case GL.GL_FOG_BIT:
        case GL.GL_HINT_BIT:
          break;
        case GL.GL_LIGHTING_BIT:
          // Lighting.pop_attrib (AttribItem);
          Lighting = (gl_lighting) AttribItem.ObjPtr;
          CR.gl_smooth(Lighting.ShadeModel == GL.GL_SMOOTH);
          break;
        case GL.GL_LINE_BIT:
          Raster.pop_line_attrib(AttribItem);
          CR.gl_stipple_line(Raster.LineStipple);
          break;
        case GL.GL_LIST_BIT:
          ListBase = AttribItem.IntPtr[0];
          break;
        case GL.GL_PIXEL_MODE_BIT:
          // Pixel.pop_attrib (AttribItem);
          break;
        case GL.GL_POINT_BIT:
          Raster.pop_point_attrib(AttribItem);
          break;
        case GL.GL_POLYGON_BIT:
          Raster.pop_polygon_attrib(AttribItem);
          CR.gl_stipple_poly(Raster.PolygonStipple);
          break;
        case GL.GL_POLYGON_STIPPLE_BIT:
          Raster.pop_polygon_stipple_attrib(AttribItem);
          break;
        case GL.GL_SCISSOR_BIT:
          break;
        case GL.GL_STENCIL_BUFFER_BIT:
          StencilBuffer = (gl_stencilbuffer) AttribItem.ObjPtr;
          // CR.gl_depth (StencilBuffer.Enable);
          break;
        case GL.GL_TEXTURE_BIT:
          // Texture.pop_attrib (AttribItem);
          break;
        case GL.GL_TRANSFORM_BIT:
          // Transform.pop_attrib (AttribItem);
          Transform = (gl_transform) AttribItem.ObjPtr;
          CR.gl_clipping(Transform.ClippingEnable);
          break;
        case GL.GL_VIEWPORT_BIT:
          // Viewport.pop_attrib (AttribItem);
          Viewport = (gl_viewport) AttribItem.ObjPtr;
          break;
      }
    }
  }

  public int gl_render_mode(int mode) {
    int result = 0;
    switch (RenderMode) {
      case GL.GL_RENDER:
        result = 0;
        break;
      case GL.GL_SELECT:
        if (Select.BufferSize == 0) {
          gl_error(GL.GL_INVALID_OPERATION, "glRenderMode");
          return -1;
        }
        if (Select.HitFlag) {
          Select.write_hit_record();
        }
        if (Select.BufferCount > Select.BufferSize) {
          result = -1;
        } else {
          result = Select.Hits;
        }
        Select.BufferCount = 0;
        Select.Hits = 0;
        Select.NameStackDepth = 0;
        break;
      case GL.GL_FEEDBACK:
        if (Feedback.BufferSize == 0) {
          gl_error(GL.GL_INVALID_OPERATION, "glRenderMode");
          return -1;
        }
        if (Feedback.BufferCount > Feedback.BufferSize) {
          result = -1;
        } else {
          result = Feedback.BufferCount;
        }
        Feedback.BufferCount = 0;
        break;
    }
    RenderMode = mode;
    CR.gl_select(mode == GL.GL_SELECT);
    return result;
  }

  public void gl_clear_depth(float depth) {
    DepthBuffer.Clear = depth;
  }

  public void gl_depth_func(int func) {
    DepthBuffer.Func = func;
  }

  public void gl_depth_mask(boolean flag) {
    DepthBuffer.Mask = flag;
  }

  public void gl_depth_range(float n, float f) {
    Viewport.set_depth_range(n, f);
  }

  public void gl_matrix_mode(int mode) {
    Transform.MatrixMode = mode;
  }

  public void gl_mult_matrix(float m[]) {
    switch (Transform.MatrixMode) {
      case GL.GL_MODELVIEW:
        ModelViewMatrix = gl_util.mulMatrix44(ModelViewMatrix, m);
        ModelViewInvValid = false;
        break;
      case GL.GL_PROJECTION:
        ProjectionMatrix = gl_util.mulMatrix44(ProjectionMatrix, m);
        break;
      case GL.GL_TEXTURE:
        TextureMatrix = gl_util.mulMatrix44(TextureMatrix, m);
        break;
    }
  }

  public void gl_viewport(int x, int y, int width, int height) {
    Viewport.set_viewport(x, y, width, height);
    ColorBuffer.set_buffer(Viewport.Size);
    DepthBuffer.set_buffer(Viewport.Size);
    StencilBuffer.set_buffer(Viewport.Size);
    DepthBuffer.clear_buffer(Viewport.Size);
  }

  public void gl_push_matrix() {
    float temp[] = new float[16];
    switch (Transform.MatrixMode) {
      case GL.GL_MODELVIEW:
        System.arraycopy(ModelViewMatrix, 0, temp, 0, 16);
        ModelViewStack.push(temp);
        break;
      case GL.GL_PROJECTION:
        System.arraycopy(ProjectionMatrix, 0, temp, 0, 16);
        ProjectionStack.push(temp);
        break;
      case GL.GL_TEXTURE:
        System.arraycopy(TextureMatrix, 0, temp, 0, 16);
        TextureStack.push(temp);
        break;
    }
  }

  public void gl_pop_matrix() {
    switch (Transform.MatrixMode) {
      case GL.GL_MODELVIEW:
        ModelViewMatrix = (float[]) ModelViewStack.pop();
        ModelViewInvValid = false;
        break;
      case GL.GL_PROJECTION:
        ProjectionMatrix = (float[]) ProjectionStack.pop();
        break;
      case GL.GL_TEXTURE:
        TextureMatrix = (float[]) TextureStack.pop();
        break;
    }
  }

  public void gl_load_identity_matrix() {
    switch (Transform.MatrixMode) {
      case GL.GL_MODELVIEW:
        gl_util.loadMatrix44(ModelViewMatrix, IDENTITY);
        gl_util.loadMatrix44(ModelViewInv, IDENTITY);
        ModelViewInvValid = true;
        break;
      case GL.GL_PROJECTION:
        gl_util.loadMatrix44(ProjectionMatrix, IDENTITY);
        break;
      case GL.GL_TEXTURE:
        gl_util.loadMatrix44(TextureMatrix, IDENTITY);
        break;
    }
  }

  public void gl_load_matrix(float m[]) {
    switch (Transform.MatrixMode) {
      case GL.GL_MODELVIEW:
        gl_util.loadMatrix44(ModelViewMatrix, m);
        ModelViewInvValid = false;
        break;
      case GL.GL_PROJECTION:
        gl_util.loadMatrix44(ProjectionMatrix, m);
        break;
      case GL.GL_TEXTURE:
        gl_util.loadMatrix44(TextureMatrix, m);
        break;
    }
  }

  public void gl_rotate(float angle, float x, float y, float z) {
    float a[] = get_rotate(angle, x, y, z);
    if (a != null) {
      gl_mult_matrix(a);
    }
  }

  public void gl_scale(float x, float y, float z) {
    gl_mult_matrix(get_scale(x, y, z));
  }

  public void gl_translate(float x, float y, float z) {
    gl_mult_matrix(get_translate(x, y, z));
  }

  public boolean gl_is_list(int list) {
    if (ListGroup.size() <= list) {
      return false;
    }
    Object temp = ListGroup.elementAt(list);
    if (temp == null) {
      return false;
    }
    return true;
  }

  public void gl_delete_lists(int list, int range) {
    for (int i = 0; i < range; i++) {
      ListGroup.setElementAt(null, list + i);
    }
  }

  public int gl_gen_lists(int range) {
    int i = 1, j = 0, firstempty = 0;
    Object temp;

    while (i < ListGroup.size() && j < range) {
      temp = ListGroup.elementAt(i);
      if (temp == null) {
        if (j == 0) {
          firstempty = i;
        }
        i++;
        j++;
      } else {
        i++;
        j = 0;
      }
    }
    if (j == range) {
      return firstempty;
    } else {
      return i;
    }
  }

  public void gl_new_list(int list) {
    ListIndex = list;
  }

  public void gl_end_list(gl_list CC) {
    if (ListGroup.size() < ListIndex + 1) {
      ListGroup.setSize(ListIndex + 1);
    }
    ListGroup.setElementAt(CC, ListIndex);
    ListIndex = 0;
  }

  public void gl_call_list(int list) {
    ((gl_list) (ListGroup.elementAt(list))).gl_exec_list(this);
  }

  public void gl_call_offset(int offset) {
    ((gl_list) (ListGroup.elementAt(ListBase + offset))).gl_exec_list(this);
  }

  public void gl_list_base(int base) {
    ListBase = base;
  }

  public void gl_begin(int mode) {
    Mode = mode;
    CR.geometry.gl_begin();
  }

  public void gl_end() {
    CR.geometry.gl_end();
    Mode = GL.None;
  }

  public void gl_vertex(float x, float y, float z, float w) {
    Current.Vertex[0] = x;
    Current.Vertex[1] = y;
    Current.Vertex[2] = z;
    Current.Vertex[3] = w;
    CR.geometry.gl_vertex();
  }

  public void gl_normal(float x, float y, float z) {
    Current.Normal[0] = x;
    Current.Normal[1] = y;
    Current.Normal[2] = z;
  }

  public void gl_index(int c) {
    Current.Index = c;
  }

  public void gl_color(float red, float green, float blue, float alpha) {
    Current.set_color(red, green, blue, alpha);
    Lighting.set_color(red, green, blue, alpha);
  }

  public void gl_tex_coord(float s, float t, float r, float q) {
    Current.TexCoord[0] = s;
    Current.TexCoord[1] = t;
    Current.TexCoord[2] = r;
    Current.TexCoord[3] = q;
  }

  public void gl_raster_pos(float x, float y, float z, float w) {}

  public void gl_shade_model(int mode) {
    Lighting.ShadeModel = mode;
    CR.gl_smooth(Lighting.ShadeModel == GL.GL_SMOOTH);
  }

  public void gl_light(int light, int pname, float params[]) {
    if (pname == GL.GL_POSITION) {
      Lighting.set_light(light, pname, gl_util.mulMatrix41(ModelViewMatrix, params));
      return;
    }
    if (pname == GL.GL_SPOT_DIRECTION) {
      if (!ModelViewInvValid) {
        ModelViewInv = gl_util.inverseMatrix44(ModelViewMatrix);
      }
      Lighting.set_light(light, pname, gl_util.mulMatrix41(ModelViewInv, params));
      return;
    }
    Lighting.set_light(light, pname, params);
  }

  public float[] gl_get_light(int light, int pname) {
    return Lighting.get_light(light, pname);
  }

  public void gl_light_model(int pname, float params[]) {
    Lighting.set_light_model(pname, params);
  }

  public void gl_material(int face, int pname, float params[]) {
    Lighting.set_material(face, pname, params);
  }

  public float[] gl_get_material(int face, int pname) {
    return Lighting.get_material(face, pname);
  }

  public void gl_color_material(int face, int mode) {
    Lighting.set_color_material(face, mode);
  }

  public void gl_pixel_store(int pname, int param) {
    Pixel.pack_store(pname, param);
  }

  public void gl_pixel_transfer(int pname, float param) {
    Pixel.pixel_transfer(pname, param);
  }

  public void gl_read_index_pixels(int x, int y, int width, int height, int size, Object pixels) {
    // TODO
  }

  public void gl_read_color_pixels(int x, int y, int width, int height, int format, int size,
      Object pixels) {
    ColorBuffer.read_pixels(x, y, width, height, format, size, pixels);
  }

  public void gl_read_stencil_pixels(int x, int y, int width, int height, int size, Object pixels) {
    StencilBuffer.read_pixels(x, y, width, height, size, pixels);
  }

  public void gl_read_depth_pixels(int x, int y, int width, int height, int size, Object pixels) {
    DepthBuffer.read_pixels(x, y, width, height, size, pixels);
  }

  public void gl_draw_index_pixels(int width, int height, int size, Object pixels) {
    // TODO
  }

  public void gl_draw_color_pixels(int width, int height, int format, int size, Object pixels) {
    ColorBuffer.draw_pixels(width, height, format, size, pixels);
  }

  public void gl_draw_stencil_pixels(int width, int height, int size, Object pixels) {
    StencilBuffer.draw_pixels(width, height, size, pixels);
  }

  public void gl_draw_depth_pixels(int width, int height, int size, Object pixels) {
    DepthBuffer.draw_pixels(width, height, size, pixels);
  }

  public void gl_copy_color_pixels(int x, int y, int width, int height) {
    ColorBuffer.copy_pixels(x, y, width, height);
  }

  public void gl_copy_stencil_pixels(int x, int y, int width, int height) {
    StencilBuffer.copy_pixels(x, y, width, height);
  }

  public void gl_copy_depth_pixels(int x, int y, int width, int height) {
    DepthBuffer.copy_pixels(x, y, width, height);
  }

  public void gl_stencil_func(int func, int ref, int mask) {
    StencilBuffer.Func = func;
    StencilBuffer.Ref = ref;
    StencilBuffer.ValueMask = mask;
  }

  public void gl_stencil_mask(int mask) {
    StencilBuffer.Mask = mask;
  }

  public void gl_stencil_op(int fail, int zfail, int zpass) {
    StencilBuffer.Fail = fail;
    StencilBuffer.ZFail = zfail;
    StencilBuffer.ZPass = zpass;
  }

  public void gl_clear_stencil(int s) {
    StencilBuffer.Clear = s;
  }

  public void gl_tex_gen_i(int coord, int param) {
    Texture.set_tex_gen_i(coord, param);
  }

  public void gl_tex_gen_f(int coord, int pname, float params[]) {
    Texture.set_tex_gen_f(coord, pname, params);
  }

  public void gl_tex_env_i(int param) {
    Texture.EnvMode = param;
  }

  public void gl_tex_env_f(float params[]) {
    System.arraycopy(params, 0, Texture.EnvColor, 0, 4);
  }

  public void gl_tex_parameter(int target, int pname, float params[]) {
    Texture.set_tex(target, pname, params);
  }

  public void gl_tex_image_1d(int target, int level, int components, int width, int border,
      int format, int size, Object pixels) {
    Texture.set_tex_image(target, level, border,
        new gl_image(width, components, format, size, Pixel.Unpack, pixels));
  }

  public void gl_tex_image_2d(int target, int level, int components, int width, int height,
      int border,
      // int format, int type, byte pixels [][][]) {
      int format, int size, Object pixels) {
    Texture.set_tex_image(target, level, border, new gl_image(width, height, components,
        // format, type, Pixel.Unpack,
        format, size, Pixel.Unpack, pixels));
  }

  public void gl_tex_image_3d(int target, int level, int components, int width, int height,
      int depth, int border, int format, int size, Object pixels) {
    Texture.set_tex_image(target, level, border,
        new gl_image(width, height, depth, components, format, size, Pixel.Unpack, pixels));
  }

  public void gl_get_tex_image(int target, int level, int format, int size, Object pixels) {
    Texture.get_tex_image(target, level).get_image(format, size, pixels);
  }

  public void gl_gen_textures(int n, int textures[]) {
    int j = 0;
    for (int i = 0; i < TexList.size(); i++) {
      if (TexList.elementAt(i) == null) {
        TexList.setElementAt(new gl_texture_obj(), i);
        textures[j++] = i;
      }
    }
    for (; j < n; j++) {
      textures[j] = TexList.size();
      TexList.addElement(new gl_texture_obj());
    }
  }

  public void gl_delete_textures(int n, int textures[]) {
    for (int i = 0; i < n; i++)
      if (textures[i] < TexList.size())
        TexList.setElementAt(null, textures[i]);
  }

  public void gl_bind_texture(int target, int texture) {
    if (texture >= TexList.size())
      return;
    gl_texture_obj tex_obj = (gl_texture_obj) TexList.elementAt(texture);
    if (tex_obj == null)
      return;
    Texture.bind_texture(target, tex_obj);
  }

  public boolean gl_is_texture(int texture) {
    if (texture >= TexList.size())
      return false;
    if (TexList.elementAt(texture) == null)
      return false;
    return true;
  }

  public void gl_tex_sub_image_1d(int target, int level, int xoffset, int width, int format,
      int size, Object pixels) {
    Texture.get_tex_image(target, level).set_sub_image(xoffset, width, format, size, Pixel.Unpack,
        pixels);
  }

  public void gl_tex_sub_image_2d(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int size, Object pixels) {
    Texture.get_tex_image(target, level).set_sub_image(xoffset, yoffset, width, height, format,
        size, Pixel.Unpack, pixels);
  }

  public void gl_tex_sub_image_3d(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int size, Object pixels) {
    Texture.get_tex_image(target, level).set_sub_image(xoffset, yoffset, zoffset, width, height,
        depth, format, size, Pixel.Unpack, pixels);
  }

  public int gl_map_1(int target, float u1, float u2, int stride, int order, float points[][]) {
    return Eval.set_map_1(target, u1, u2, stride, order, points);
  }

  public int gl_map_2(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, float points[][][]) {
    return Eval.set_map_2(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
  }

  /*
   * public void gl_eval_coord_1 (float u) { gl_eval_map1 map; float q [];
   * 
   * if (Eval.Map1Color4Enable) { map = Eval.Map1Color4; q = Eval.gl_eval_coord_1 (map, 4, u);
   * gl_color (q[0], q[1], q[2], q[3]); }
   * 
   * if (Eval.Map1IndexEnable) { map = Eval.Map1Index; q = Eval.gl_eval_coord_1 (map, 1, u);
   * gl_index ((int)q[0]); }
   * 
   * if (Eval.Map1NormalEnable) { map = Eval.Map1Normal; q = Eval.gl_eval_coord_1 (map, 3, u);
   * gl_normal (q[0], q[1], q[2]); }
   * 
   * if (Eval.Map1TexCoord4Enable) { map = Eval.Map1TexCoord4; q = Eval.gl_eval_coord_1 (map, 4, u);
   * gl_tex_coord (q[0], q[1], q[2], q[3]); } else if (Eval.Map1TexCoord3Enable) { map =
   * Eval.Map1TexCoord3; q = Eval.gl_eval_coord_1 (map, 3, u); gl_tex_coord (q[0], q[1], q[2],
   * 1.0f); } else if (Eval.Map1TexCoord2Enable) { map = Eval.Map1TexCoord2; q =
   * Eval.gl_eval_coord_1 (map, 2, u); gl_tex_coord (q[0], q[1], 0.0f, 1.0f); } else if
   * (Eval.Map1TexCoord1Enable) { map = Eval.Map1TexCoord1; q = Eval.gl_eval_coord_1 (map, 1, u);
   * gl_tex_coord (q[0], 0.0f, 0.0f, 1.0f); }
   * 
   * if (Eval.Map1Vertex4Enable) { map = Eval.Map1Vertex4; q = Eval.gl_eval_coord_1 (map, 4, u);
   * gl_vertex (q[0], q[1], q[2], q[3]); } else if (Eval.Map1Vertex3Enable) { map =
   * Eval.Map1Vertex3; q = Eval.gl_eval_coord_1 (map, 3, u); gl_vertex (q[0], q[1], q[2], 1.0f); } }
   * 
   * public void gl_eval_coord_2 (float u, float v) { gl_eval_map2 map; float q [], n[];
   * 
   * if (Eval.Map2Color4Enable) { map = Eval.Map2Color4; q = Eval.gl_eval_coord_2 (map, 4, u, v);
   * gl_color (q[0], q[1], q[2], q[3]); }
   * 
   * if (Eval.Map2IndexEnable) { map = Eval.Map2Index; q = Eval.gl_eval_coord_2 (map, 1, u, v);
   * gl_index ((int)q[0]); }
   * 
   * if (!Eval.AutoNormal || (!Eval.Map2Vertex4Enable && !Eval.Map2Vertex3Enable)) { if
   * (Eval.Map2NormalEnable) { map = Eval.Map2Normal; q = Eval.gl_eval_coord_2 (map, 3, u, v);
   * gl_normal (q[0], q[1], q[2]); } }
   * 
   * if (Eval.Map2TexCoord4Enable) { map = Eval.Map2TexCoord4; q = Eval.gl_eval_coord_2 (map, 4, u,
   * v); gl_tex_coord (q[0], q[1], q[2], q[3]); } else if (Eval.Map2TexCoord3Enable) { map =
   * Eval.Map2TexCoord3; q = Eval.gl_eval_coord_2 (map, 3, u, v); gl_tex_coord (q[0], q[1], q[2],
   * 1.0f); } else if (Eval.Map2TexCoord2Enable) { map = Eval.Map2TexCoord2; q =
   * Eval.gl_eval_coord_2 (map, 2, u, v); gl_tex_coord (q[0], q[1], 0.0f, 1.0f); } else if
   * (Eval.Map2TexCoord1Enable) { map = Eval.Map2TexCoord1; q = Eval.gl_eval_coord_2 (map, 1, u, v);
   * gl_tex_coord (q[0], 0.0f, 0.0f, 1.0f); }
   * 
   * if (Eval.Map2Vertex4Enable) { map = Eval.Map2Vertex4; if (Eval.AutoNormal) { n =
   * Eval.gl_auto_normal (map, 4, u, v); gl_normal (n[0], n[1], n[2]); } q = Eval.gl_eval_coord_2
   * (map, 4, u, v); gl_vertex (q[0], q[1], q[2], q[3]); } else if (Eval.Map2Vertex3Enable) { map =
   * Eval.Map2Vertex3; if (Eval.AutoNormal) { n = Eval.gl_auto_normal (map, 3, u, v); gl_normal
   * (n[0], n[1], n[2]); } q = Eval.gl_eval_coord_2 (map, 3, u, v); gl_vertex (q[0], q[1], q[2],
   * 1.0f); } }
   */

  public void gl_map_grid_1(int un, float u1, float u2) {
    Eval.MapGrid1un = un;
    Eval.MapGrid1u1 = u1;
    Eval.MapGrid1u2 = u2;
  }

  public void gl_map_grid_2(int un, float u1, float u2, int vn, float v1, float v2) {
    Eval.MapGrid2un = un;
    Eval.MapGrid2u1 = u1;
    Eval.MapGrid2u2 = u2;
    Eval.MapGrid2vn = vn;
    Eval.MapGrid2v1 = v1;
    Eval.MapGrid2v2 = v2;
  }

  public void gl_eval_point_1(int i) {
    gl_eval_coord_1(gl_util.interpolate(i, Eval.MapGrid1un, Eval.MapGrid1u1, Eval.MapGrid1u2));
  }

  public void gl_eval_point_2(int i, int j) {
    gl_eval_coord_2(gl_util.interpolate(i, Eval.MapGrid2un, Eval.MapGrid2u1, Eval.MapGrid2u2),
        gl_util.interpolate(j, Eval.MapGrid2vn, Eval.MapGrid2v1, Eval.MapGrid2v2));
  }

  /*
   * public void gl_eval_mesh_1 (int mode, int nu1, int nu2) { float u1 = Eval.MapGrid1u1; float u2
   * = Eval.MapGrid1u2; int nu = Eval.MapGrid1un; float du = (u2 - u1) / (float)nu; int i;
   * 
   * switch (mode) { case GL.GL_POINT: gl_begin (GL.GL_POINTS); break; case GL.GL_LINE: gl_begin
   * (GL.GL_LINE_STRIP); break; default: // never default break; } for (i = nu1; i <= nu2; i++) {
   * gl_eval_coord_1 (gl_util.interpolate (i, nu, u1, u2, du)); } gl_end (); }
   * 
   * 
   * public void gl_eval_mesh_2 (int mode, int nu1, int nu2, int nv1, int nv2) { float u1 =
   * Eval.MapGrid2u1; float u2 = Eval.MapGrid2u2; float v1 = Eval.MapGrid2v1; float v2 =
   * Eval.MapGrid2v2; int nu = Eval.MapGrid2un; int nv = Eval.MapGrid2vn; float du = (u2 - u1) /
   * (float)nu; float dv = (v2 - v1) / (float)nv; int i, j; float tu, tv, tv1;
   * 
   * switch (mode) { case GL.GL_POINT: gl_begin (GL.GL_POINTS); for (i = nu1; i <= nu2; i++) { tu =
   * gl_util.interpolate (i, nu, u1, u2, du); for (j = nv1; j <= nv2; j++) { gl_eval_coord_2 (tu,
   * gl_util.interpolate (j, nv, v1, v2, dv)); } } gl_end (); break; case GL.GL_LINE: for (j = nv1;
   * j <= nv2; j++) { gl_begin (GL.GL_LINE_STRIP); tv = gl_util.interpolate (j, nv, v1, v2, dv); for
   * (i = nu1; i <= nu2; i++) { gl_eval_coord_2 ( gl_util.interpolate (i, nu, u1, u2, du), tv); }
   * gl_end (); } for (i = nu1; i <= nu2; i++) { gl_begin (GL.GL_LINE_STRIP); tu =
   * gl_util.interpolate (i, nu, u1, u2, du); for (j = nv1; j <= nv2; j++) { gl_eval_coord_2 ( tu,
   * gl_util.interpolate (j, nv, v1, v2, dv)); } gl_end (); } break; case GL.GL_FILL: for (j = nv1;
   * j < nv2; j++) { gl_begin (GL.GL_TRIANGLE_STRIP); tv = gl_util.interpolate (j, nv, v1, v2, dv);
   * tv1 = gl_util.interpolate (j+1, nv, v1, v2, dv); for (i = nu1; i <= nu2; i++) { tu =
   * gl_util.interpolate (i, nu, u1, u2, du); gl_eval_coord_2 (tu, tv); gl_eval_coord_2 (tu, tv1); }
   * gl_end (); } break; default: break; } }
   */

  public void gl_feedback_buffer(int size, int type, float buffer[]) {
    switch (type) {
      case GL.GL_2D:
        Feedback.Mask = 0;
        Feedback.Type = type;
        break;
      case GL.GL_3D:
        Feedback.Mask = gl_feedback.FB_3D;
        Feedback.Type = type;
        break;
      case GL.GL_3D_COLOR:
        Feedback.Mask = gl_feedback.FB_3D | gl_feedback.FB_COLOR;
        Feedback.Type = type;
        break;
      case GL.GL_3D_COLOR_TEXTURE:
        Feedback.Mask = gl_feedback.FB_3D | gl_feedback.FB_COLOR | gl_feedback.FB_TEXTURE;
        Feedback.Type = type;
        break;
      case GL.GL_4D_COLOR_TEXTURE:
        Feedback.Mask =
            gl_feedback.FB_3D | gl_feedback.FB_4D | gl_feedback.FB_COLOR | gl_feedback.FB_TEXTURE;
        Feedback.Type = type;
        break;
      default:
        Feedback.Mask = 0;
    }
    Feedback.BufferSize = size;
    Feedback.Buffer = buffer;
    Feedback.BufferCount = 0;
  }

  public void gl_pass_through(float token) {
    if (RenderMode == GL.GL_FEEDBACK) {
      Feedback.write_feedback_token((float) GL.GL_PASS_THROUGH_TOKEN);
      Feedback.write_feedback_token(token);
    }
  }

  public void gl_select_buffer(int size, int buffer[]) {
    Select.set_buffer(size, buffer);
  }

  public void gl_init_names() {
    Select.init_name();
  }

  public void gl_load_name(int name) {
    if (!Select.load_name(name)) {
      gl_error(GL.GL_INVALID_OPERATION, "glLoadName");
    }
  }

  public void gl_push_name(int name) {
    if (!Select.push_name(name)) {
      gl_error(GL.GL_STACK_OVERFLOW, "glPushName");
    }
  }

  public void gl_pop_name() {
    if (!Select.pop_name()) {
      gl_error(GL.GL_STACK_UNDERFLOW, "glPopName");
    }
  }

  /* ********************************************************************** */

  /* **************************** CONSTANTS ******************************* */

  /* ********************************************************************** */


  /** Constant of Context of JavaGL */
  // public static final int MAX_MODELVIEW_STACK_DEPTH = 32;
  // public static final int MAX_PROJECTION_STACK_DEPTH = 32;
  // public static final int MAX_TEXTURE_STACK_DEPTH = 8;
  // public static final int MAX_ATTRIB_STACK_DEPTH = 16;
  // public static final int MAX_DISPLAYLISTS = 2000;
  // public static final int MAX_LIST_NESTING = 64;
  public static final int MAX_LIGHTS = 8;
  public static final int MAX_CLIP_PLANES = 6;
  public static final int MAX_TEXTURE_LEVELS = 10;
  public static final int MAX_TEXTURE_SIZE = 1 << (MAX_TEXTURE_LEVELS - 1);
  public static final int MAX_3D_TEXTURE_LEVELS = 8;
  public static final int MAX_3D_TEXTURE_SIZE = 1 << (MAX_3D_TEXTURE_LEVELS - 1);
  // public static final int MAX_TEXTURE_SIZE = 512;
  // public static final int MAX_PIXEL_MAP_TABLE = 256;
  // public static final int COLOR_BITS = 8;
  // public static final int MAX_AUX_BUFFERS = 0;
  public static final int MAX_EVAL_ORDER = 30;
  public static final int MAX_NAME_STACK_DEPTH = 64;
  public static final float MIN_POINT_SIZE = 1.0f;
  public static final float MAX_POINT_SIZE = 10.0f;
  public static final float POINT_SIZE_GRANULARITY = 0.1f;
  public static final float MIN_LINE_SIZE = 1.0f;
  public static final float MAX_LINE_SIZE = 10.0f;
  public static final float LINE_WIDTH_GRANULARITY = 0.1f;
  // public static final int MAX_WIDTH = 1280;
  // public static final int MAX_HEIGHT = 1024;
  // public static final int ACCUM_BITS = 16;
  // public static final int DEPTH_BITS = 16;
  // public static final int MAX_DEPTH = 0xffff;
  // public static final float MAX_DEPTH = 65535.0f;
  // public static final int STENCIL_BITS = 8;
  // public static final float SUB_PIX_SCALE = 256.0f;
  // public static final int SUB_PIX_SHIFT = 8;

}
