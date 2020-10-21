package org.jzy3d.plot3d.rendering.view;

import java.nio.ByteBuffer;

import org.jzy3d.painters.GLES2CompatUtils;

import com.jogamp.opengl.GL;

public class ImageRenderer {
	public static void renderImage(GL gl, ByteBuffer image, int imageWidth,
			int imageHeight, int screenWidth, int screenHeight) {
		renderImage(gl, image, imageWidth, imageHeight, screenWidth,
				screenHeight, 0.75f);
	}

	public static void renderImage(GL gl, ByteBuffer image, int imageWidth,
			int imageHeight, int screenWidth, int screenHeight, float z) {
		if (image == null)
			return;

		float xratio = 1;
		float yratio = 1;
		int xpict = 0;
		int ypict = 0;

		if (imageWidth < screenWidth)
			xpict = (int) ((float) screenWidth / 2 - (float) imageWidth / 2);
		else
			xratio = ((float) screenWidth) / ((float) imageWidth);

		if (imageHeight < screenHeight)
			ypict = (int) ((float) screenHeight / 2 - (float) imageHeight / 2);
		else
			yratio = ((float) screenHeight) / ((float) imageHeight);

		// Draw
		if (gl.isGL2()) {
			gl.getGL2().glPixelZoom(xratio, yratio);
			gl.getGL2().glRasterPos3f(xpict, ypict, z);

			synchronized (image) { // we don't want to draw image while it is
									// being set by setImage
				gl.getGL2().glDrawPixels(imageWidth, imageHeight, GL.GL_RGBA,
						GL.GL_UNSIGNED_BYTE, image);
			}
		} else {
			GLES2CompatUtils.glPixelZoom(xratio, yratio);
			GLES2CompatUtils.glRasterPos3f(xpict, ypict, z);

			synchronized (image) { // we don't want to draw image while it is
									// being set by setImage
				GLES2CompatUtils.glDrawPixels(imageWidth, imageHeight, GL.GL_RGBA,
						GL.GL_UNSIGNED_BYTE, image);
			}
		}

		// Copy elsewhere
		// gl.glPixelZoom(1.0f, 1.0f); // x-factor, y-factor
		// gl.glWindowPos2i(500, 0);
		// gl.glCopyPixels(400, 300, 500, 600, GL2.GL_COLOR);
	}
}
