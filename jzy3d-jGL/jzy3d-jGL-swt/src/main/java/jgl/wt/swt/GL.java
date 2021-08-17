/*******************************************************************************
 * (c) 2021 Läubisoft GmbH, Christoph Läubrich
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
package jgl.wt.swt;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

public class GL extends jgl.GL<Image, Font> {

	private Image image;
	private Device device;
	private boolean disposed;

	public GL(Device device) {

		this.device = device;
	}

	@Override
	public void glFlush() {

		if(disposed) {
			return;
		}

		int[] buffer = Context.ColorBuffer.Buffer;
		int width = Context.Viewport.Width;
		int height = Context.Viewport.Height;
		PaletteData palette = new PaletteData(0xFF0000, 0xFF00, 0xFF);
		ImageData imageData = new ImageData(width, height, 24, palette);
		imageData.setPixels(0, 0, buffer.length, buffer, 0);
		byte[] alphaBytes = new byte[buffer.length];
		for(int i = 0; i < alphaBytes.length; i++) {
			alphaBytes[i] = (byte)((buffer[i] >> 24) & 0xFF);
		}
		imageData.setAlphas(0, 0, alphaBytes.length, alphaBytes, 0);
		Image oldImage = image;
		image = new Image(device, imageData);
		if(oldImage != null) {
			oldImage.dispose();
		}
	}

	@Override
	public Image getRenderedImage() {

		return image;
	}

	/**
	 * Disposes any cached data
	 */
	public void dispose() {

		disposed = true;
		if(image != null) {
			image.dispose();
			image = null;
		}
	}
}
