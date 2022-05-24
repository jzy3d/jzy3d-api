/*
 * @(#)GL.java 0.9 03/05/14
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
package jgl;

public final class TextToDraw<Font> {

	public final Font font;
	public final String string;
	public final int x;
	public final int y;
	public final float r;
	public final float g;
	public final float b;
	public final float rotate;

    public TextToDraw(Font font, String string, int x, int y) {
      this(font, string, x, y, -1, -1, -1, 0);
    }

    public TextToDraw(Font font, String string, int x, int y, float r, float g, float b,
        float rotate) {
      super();
      this.font = font;
      this.string = string;
      this.x = x;
      this.y = y;
      this.r = r;
      this.g = g;
      this.b = b;
      this.rotate = rotate;
    }
  }