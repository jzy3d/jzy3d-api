/*
 * @(#)gl_blend_pixel.java 0.1 06/11/20
 *
 * jGL 3-D graphics library for Java Copyright (c) 2006 Robin Bing-Yu Chen (robin@ntu.edu.tw)
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

package jgl.context.render.pixel;

import jgl.GL;
import jgl.context.gl_context;
import jgl.context.gl_util;

/**
 * gl_blend_pixel is the pixel blending class of jGL 2.4.
 * 
 * It overrides gl_render_pixel to have all {@link gl_render_pixel#put_pixel(int, int, int)} methods
 * apply blending first.
 *
 * @version 0.1, 20 Nov. 2006
 * @author Robin Bing-Yu Chen
 */

public class gl_blend_pixel extends gl_render_pixel {

  /**
   * https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendFunc.xhtml
   * 
   * @param rst
   * @param tgt
   * @param src
   * @param dst
   * @param BlendFunc
   */
  private void blend_pixel(float rst[], float tgt[], float src[], float dst[], int BlendFunc) {
    float afa;

    switch (BlendFunc) {
      case GL.GL_ZERO:
        break;
      case GL.GL_ONE:
        rst[0] += tgt[0];
        rst[1] += tgt[1];
        rst[2] += tgt[2];
        rst[3] += tgt[3];
        break;
      case GL.GL_DST_COLOR:
        rst[0] += (tgt[0] * dst[0]);
        rst[1] += (tgt[1] * dst[1]);
        rst[2] += (tgt[2] * dst[2]);
        rst[3] += (tgt[3] * dst[3]);
        break;
      case GL.GL_SRC_COLOR:
        rst[0] += (tgt[0] * src[0]);
        rst[1] += (tgt[1] * src[1]);
        rst[2] += (tgt[2] * src[2]);
        rst[3] += (tgt[3] * src[3]);
        break;
      case GL.GL_ONE_MINUS_DST_COLOR:
        rst[0] += (tgt[0] * (1.0f - dst[0]));
        rst[1] += (tgt[1] * (1.0f - dst[1]));
        rst[2] += (tgt[2] * (1.0f - dst[2]));
        rst[3] += (tgt[3] * (1.0f - dst[3]));
        break;
      case GL.GL_ONE_MINUS_SRC_COLOR:
        rst[0] += (tgt[0] * (1.0f - src[0]));
        rst[1] += (tgt[1] * (1.0f - src[1]));
        rst[2] += (tgt[2] * (1.0f - src[2]));
        rst[3] += (tgt[3] * (1.0f - src[3]));
        break;
      case GL.GL_SRC_ALPHA:
        rst[0] += (tgt[0] * src[3]);
        rst[1] += (tgt[1] * src[3]);
        rst[2] += (tgt[2] * src[3]);
        rst[3] += (tgt[3] * src[3]);
        break;
      case GL.GL_ONE_MINUS_SRC_ALPHA:
        afa = 1.0f - src[3];
        rst[0] += (tgt[0] * afa);
        rst[1] += (tgt[1] * afa);
        rst[2] += (tgt[2] * afa);
        rst[3] += (tgt[3] * afa);
        break;
      case GL.GL_DST_ALPHA:
        rst[0] += (tgt[0] * dst[3]);
        rst[1] += (tgt[1] * dst[3]);
        rst[2] += (tgt[2] * dst[3]);
        rst[3] += (tgt[3] * dst[3]);
        break;
      case GL.GL_ONE_MINUS_DST_ALPHA:
        afa = 1.0f - dst[3];
        rst[0] += (tgt[0] * afa);
        rst[1] += (tgt[1] * afa);
        rst[2] += (tgt[2] * afa);
        rst[3] += (tgt[3] * afa);
        break;
      case GL.GL_SRC_ALPHA_SATURATE:
        afa = Math.min(src[3], (1.0f - dst[3]));
        rst[0] += (tgt[0] * afa);
        rst[1] += (tgt[1] * afa);
        rst[2] += (tgt[2] * afa);
        rst[3] += tgt[3];
        break;
      default:
        break;
    }
  }

  /** Blend the pixel in the Color Buffer with the input color */
  public void put_pixel_by_index(int index, int color) {
    float src[] = gl_util.ItoRGBAf(color);
    float dst[] = gl_util.ItoRGBAf(CC.ColorBuffer.Buffer[index]);
    float rst[] = {0.0f, 0.0f, 0.0f, 0.0f};

    // Apply SRC blend func to mix source and destination color
    // and increment rst with this result
    blend_pixel(rst, src, src, dst, CC.ColorBuffer.BlendSrc);

    // Apply DST blend func to mix source and destination color
    // and increment rst with this result
    blend_pixel(rst, dst, src, dst, CC.ColorBuffer.BlendDst);

    // gl_render_pixel.debug_color_to_console(color);


    // The initial 2.4 release used to compute alpha as follow, without updating the alpha channel
    // of the output pixel, which led to complete dark pixel when input alpha are 0.
    //
    // So we changed the pixel setting to also write output alpha to the color buffer as of 2.5.
    //
    CC.ColorBuffer.Buffer[index] =
        gl_util.RGBAtoI(gl_util.CLAMP(rst[0], 0.0f, 1.0f), gl_util.CLAMP(rst[1], 0.0f, 1.0f),
            gl_util.CLAMP(rst[2], 0.0f, 1.0f), gl_util.CLAMP(rst[3], 0.0f, 1.0f));

    // 2.4 code, which is proved to be erroneous with test GL_renderingAlpha
    // CC.ColorBuffer.Buffer[index] = gl_util.RGBtoI(gl_util.CLAMP(rst[0], 0.0f, 1.0f),
    // gl_util.CLAMP(rst[1], 0.0f, 1.0f),
    // gl_util.CLAMP(rst[2], 0.0f, 1.0f));

  }

  /** Put a pixel in the Color Buffer */
  public void put_pixel(int x, int y, int color) {
    put_pixel_by_index(x + CC.Viewport.Width * y, color);
  }

  /** Put a pixel in the Color Buffer, if the pixel is near View Point */
  public void put_pixel_by_index(int index, float z, int color) {
    if (CC.DepthBuffer.Test(z, index)) {

      put_pixel_by_index(index, color);

      if (CC.DepthBuffer.Mask)
        CC.DepthBuffer.Buffer[index] = z;
    }
  }

  /** Put a pixel in the Color Buffer, if the pixel is near View Point */
  public void put_pixel(int x, int y, float z, int color) {
    int index = x + CC.Viewport.Width * y;

    if (CC.DepthBuffer.Test(z, index)) {

      put_pixel_by_index(index, color);

      if (CC.DepthBuffer.Mask)
        CC.DepthBuffer.Buffer[index] = z;
    }
  }

  public gl_blend_pixel(gl_context cc) {
    super(cc);
  }

}
