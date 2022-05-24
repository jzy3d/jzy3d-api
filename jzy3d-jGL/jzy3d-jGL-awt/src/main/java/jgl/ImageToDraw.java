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

import jgl.GL.ImageLayer;

public final class ImageToDraw<Image> {

	public final int x;
	public final int y;
	public final Image image;
	public final ImageLayer layer;

	public ImageToDraw(int x, int y, Image image, ImageLayer layer) {

			super();
			this.x = x;
			this.y = y;
			this.image = image;
			this.layer = layer;
		}
	}