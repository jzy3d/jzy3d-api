/*
 * @(#)gl_feedback.java 0.1 97/02/26
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

package jgl.context;

/**
 * gl_feedback is the FeedBack class of JavaGL 1.0.
 *
 * @version 0.1, 26 Feb 1997
 * @author Robin Bing-Yu Chen
 */

public class gl_feedback {

  public static final int FB_3D = 0x01;
  public static final int FB_4D = 0x02;
  public static final int FB_INDEX = 0x04;
  public static final int FB_COLOR = 0x08;
  public static final int FB_TEXTURE = 0x10;

  public int Type;
  public int Mask;
  public float Buffer[];
  public int BufferSize = 0; /* size of FeedbackBuffer */
  public int BufferCount = 0; /* number of values in FeedbackBuffer */

  public void write_feedback_token(float token) {
    if (BufferCount < BufferSize) {
      Buffer[BufferCount] = token;
    }
    BufferCount++;
  }

  public void write_feedback_vertex(float x, float y, float z, float w, int c) {
    // float color [], float index,
    // float texcoord []) {
    write_feedback_token(x);
    write_feedback_token(y);
    if ((Mask & FB_3D) != 0) {
      write_feedback_token(z);
    }
    if ((Mask & FB_4D) != 0) {
      write_feedback_token(w);
    }
    // if ((Mask & FB_INDEX) != 0 ) { write_feedback_token (index); }
    if ((Mask & FB_COLOR) != 0) {
      float color[] = new float[4];
      color[0] = (float) ((c & 0x00ff0000) >> 16) / (float) 255.0;
      color[1] = (float) ((c & 0x0000ff00) >> 8) / (float) 255.0;
      color[2] = (float) (c & 0x000000ff) / (float) 255.0;
      color[3] = (float) ((c & 0xff000000) >> 24) / (float) 255.0;
      write_feedback_token(color[0]);
      write_feedback_token(color[1]);
      write_feedback_token(color[2]);
      write_feedback_token(color[3]);
    }
    /*
     * if ((Mask & FB_TEXTURE) != 0 ) { write_feedback_token (texcoord [0]); write_feedback_token
     * (texcoord [1]); write_feedback_token (texcoord [2]); write_feedback_token (texcoord [3]); }
     */ }

}
