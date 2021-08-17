/*
 * @(#)gl_render_pixel.java 0.6 03/05/15
 *
 * jGL 3-D graphics library for Java Copyright (c) 1996-2003 Robin Bing-Yu Chen
 * (robin@nis-lab.is.s.u-tokyo.ac.jp)
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

import jgl.context.gl_context;
import jgl.context.gl_util;

/**
 * gl_render_pixel is the basic pixel rendering class of jGL 2.3.
 *
 * @version 0.6, 15 May. 2003
 * @author Robin Bing-Yu Chen
 */

public class gl_render_pixel {

  protected gl_context CC;

  public gl_render_pixel(gl_context cc) {
    CC = cc;
  }


  public static void debug_color_to_console(int color) {
    float r = gl_util.ItoR(color);
    float g = gl_util.ItoG(color);
    float b = gl_util.ItoB(color);
    float a = gl_util.ItoA(color);

    // if(r==0 && g==0 && b==0 && a!=255)
    if (a != 255)
      System.err.println("gl_render_pixel.debug r:" + r + " g:" + g + " b:" + b + " a:" + a);
  }

  /** Put a pixel in the Color Buffer */
  public void put_pixel_by_index(int index, int color) {
    // debug_color_to_console(color);

    CC.ColorBuffer.Buffer[index] = color;
    // if (CC.ColorBuffer.ColorMask != 0xffffffff) {
    // CC.ColorBuffer.Buffer [index] =
    // ((color & CC.ColorBuffer.ColorMask) |
    // (CC.ColorBuffer.Buffer [index] & ~CC.ColorBuffer.ColorMask));
    // } else CC.ColorBuffer.Buffer [index] = color;
  }

  /** Put a pixel in the Color Buffer */
  public void put_pixel(int x, int y, int color) {

    // debug_color_to_console(color);

    CC.ColorBuffer.Buffer[x + CC.Viewport.Width * y] = color;
  }

  /** Put a pixel in the Color Buffer, if the pixel is near View Point */
  public void put_pixel_by_index(int index, float z, int color) {

    if (CC.DepthBuffer.Test(z, index)) {

      // debug_color_to_console(color);

      CC.ColorBuffer.Buffer[index] = color;

      if (CC.DepthBuffer.Mask)
        CC.DepthBuffer.Buffer[index] = z;
    }
  }

  /** Put a pixel in the Color Buffer, if the pixel is near View Point */
  public void put_pixel(int x, int y, float z, int color) {
    int index = x + CC.Viewport.Width * y;

    if (CC.DepthBuffer.Test(z, index)) {

      // debug_color_to_console(color);

      CC.ColorBuffer.Buffer[index] = color;
      if (CC.DepthBuffer.Mask)
        CC.DepthBuffer.Buffer[index] = z;
    }
  }

  /* ********************************************************************** */

  /** Convert the color array to call real put_pixel */
  public void put_pixel_by_index(int index, int c[]) {
    put_pixel_by_index(index, gl_util.RGBAtoI(c[0], c[1], c[2], c[3]));
  }

  /** Convert the color array to call real put_pixel */
  public void put_pixel(int x, int y, int c[]) {
    put_pixel(x, y, gl_util.RGBAtoI(c[0], c[1], c[2], c[3]));
  }

  /** Convert the color array to call real put_pixel */
  public void put_pixel_by_index(int index, float z, int c[]) {
    put_pixel_by_index(index, z, gl_util.RGBAtoI(c[0], c[1], c[2], c[3]));
  }

  /** Convert the color array to call real put_pixel */
  public void put_pixel(int x, int y, float z, int c[]) {
    put_pixel(x, y, z, gl_util.RGBAtoI(c[0], c[1], c[2], c[3]));
  }

  /** Put a texturing pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float s, float t, float r) {
    put_pixel_by_index(index, CC.Texture.tex_vertex(s, t, r));
  }

  /** Put a texturing pixel in the Color Buffer */
  public void put_pixel(int x, int y, float s, float t, float r) {
    put_pixel_by_index(x + CC.Viewport.Width * y, s, t, r);
    // put_pixel (x, y, CC.Texture.tex_vertex (s, t));
  }

  /** Put a texturing pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float s, float t, float r) {
    put_pixel_by_index(index, z, CC.Texture.tex_vertex(s, t, r));
  }

  /** Put a texturing pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float s, float t, float r) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, s, t, r);
    // put_pixel (x, y, z, CC.Texture.tex_vertex (s, t));
  }

  /** Put a mip-mapped pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy) {
    put_pixel_by_index(index,
        CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy));
  }

  /** Put a mip-mapped pixel in the Color Buffer */
  public void put_pixel(int x, int y, float w, float s, float t, float r, float dsdx, float dsdy,
      float dtdx, float dtdy, float drdx, float drdy) {
    put_pixel_by_index(x + CC.Viewport.Width * y, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx, drdy);
    // put_pixel (x, y, CC.Texture.tex_vertex (s, t, w, dsdx, dsdy, dtdx, dtdy));
  }

  /** Put a mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy) {
    put_pixel_by_index(index, z,
        CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy));
  }

  /** Put a mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx,
        drdy);
    // put_pixel (x, y, z, CC.Texture.tex_vertex (s, t, w, dsdx,dsdy,dtdx,dtdy));
  }

  protected int light_pixel(int t, int c) {
    return light_pixel(t, gl_util.ItoRGB(c));
  }

  protected int light_pixel(int t, int c[]) {
    int tex[] = gl_util.ItoRGB(t);
    tex[0] = (int) ((float) tex[0] * gl_util.ItoF(c[0]));
    tex[1] = (int) ((float) tex[1] * gl_util.ItoF(c[1]));
    tex[2] = (int) ((float) tex[2] * gl_util.ItoF(c[2]));
    return gl_util.RGBtoI(tex[0], tex[1], tex[2]);
  }

  /** Put a lighting texturing pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float s, float t, float r, int c) {
    put_pixel_by_index(index, light_pixel(CC.Texture.tex_vertex(s, t, r), c));
  }

  /** Put a lighting texturing pixel in the Color Buffer */
  public void put_pixel(int x, int y, float s, float t, float r, int c) {
    put_pixel_by_index(x + CC.Viewport.Width * y, s, t, r, c);
  }

  /** Put a lighting texturing pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float s, float t, float r, int c) {
    put_pixel_by_index(index, z, light_pixel(CC.Texture.tex_vertex(s, t, r), c));
  }

  /** Put a texturing pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float s, float t, float r, int c) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, s, t, r, c);
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c) {
    put_pixel_by_index(index,
        light_pixel(CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy), c));
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer */
  public void put_pixel(int x, int y, float w, float s, float t, float r, float dsdx, float dsdy,
      float dtdx, float dtdy, float drdx, float drdy, int c) {
    put_pixel_by_index(x + CC.Viewport.Width * y, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx, drdy,
        c);
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c) {
    put_pixel_by_index(index, z,
        light_pixel(CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy), c));
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx, drdy,
        c);
  }

  /** Put a lighting texturing pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float s, float t, float r, int c[]) {
    put_pixel_by_index(index, light_pixel(CC.Texture.tex_vertex(s, t, r), c));
  }

  /** Put a lighting texturing pixel in the Color Buffer */
  public void put_pixel(int x, int y, float s, float t, float r, int c[]) {
    put_pixel_by_index(x + CC.Viewport.Width * y, s, t, r, c);
  }

  /** Put a lighting texturing pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float s, float t, float r, int c[]) {
    put_pixel_by_index(index, z, light_pixel(CC.Texture.tex_vertex(s, t, r), c));
  }

  /** Put a texturing pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float s, float t, float r, int c[]) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, s, t, r, c);
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer */
  public void put_pixel_by_index(int index, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c[]) {
    put_pixel_by_index(index,
        light_pixel(CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy), c));
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer */
  public void put_pixel(int x, int y, float w, float s, float t, float r, float dsdx, float dsdy,
      float dtdx, float dtdy, float drdx, float drdy, int c[]) {
    put_pixel_by_index(x + CC.Viewport.Width * y, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx, drdy,
        c);
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel_by_index(int index, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c[]) {
    put_pixel_by_index(index, z,
        light_pixel(CC.Texture.tex_vertex(s, t, r, w, dsdx, dsdy, dtdx, dtdy, drdx, drdy), c));
  }

  /** Put a lighting mip-mapped pixel in the Color Buffer with depth value */
  public void put_pixel(int x, int y, float z, float w, float s, float t, float r, float dsdx,
      float dsdy, float dtdx, float dtdy, float drdx, float drdy, int c[]) {
    put_pixel_by_index(x + CC.Viewport.Width * y, z, w, s, t, r, dsdx, dsdy, dtdx, dtdy, drdx, drdy,
        c);
  }

  /* just for stipple_line */
  public void init(int dx, int dy) {}
}
