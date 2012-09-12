package org.jzy3d.io;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLImage {
	public static ByteBuffer getImageAsGlByteBuffer(Image image, int width, int height){
		int[] px  = GLImage.getImagePixels(image, width, height);
		return GLImage.convertImagePixels(px, width, height, true);
	}	
	
	// FOLLOWING CONVERTER FUNCTIONS HAVE BEEN PICKED FROM:
	// http://www.potatoland.com/code/gl/GLImage.java

	/** Return the image pixels in default Java int ARGB format. */
	public static int[] getImagePixels(Image image, int width, int height) {
		int[] pixels = null;

		if (image != null) {
			pixels = new int[width * height];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height,
					pixels, 0, width);
			try {
				pg.grabPixels();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return pixels;
	}

	/**
	 * Convert ARGB pixels to a ByteBuffer containing RGBA pixels.<BR>
	 * Can be drawn in ORTHO mode using:<BR>
	 * GL2.glDrawPixels(imgW, imgH, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, byteBuffer); <BR>
	 * If flipVertically is true, pixels will be flipped vertically for OpenGL
	 * coord system.
	 * 
	 * @param imgFilename
	 * @return ByteBuffer
	 */
	public static ByteBuffer convertImagePixels(int[] jpixels, int imgw, int imgh, boolean flipVertically) {
		byte[] bytes; // will hold pixels as RGBA bytes
		if (flipVertically) {
			jpixels = flipPixels(jpixels, imgw, imgh); // flip Y axis
		}
		bytes = convertARGBtoRGBA(jpixels);
		return allocBytes(bytes); // convert to ByteBuffer and return
	}

	/**
	 * Flip an array of pixels vertically
	 * 
	 * @param imgPixels
	 * @param imgw
	 * @param imgh
	 * @return int[]
	 */
	public static int[] flipPixels(int[] imgPixels, int imgw, int imgh) {
		int[] flippedPixels = null;
		if (imgPixels != null) {
			flippedPixels = new int[imgw * imgh];
			for (int y = 0; y < imgh; y++) {
				for (int x = 0; x < imgw; x++) {
					flippedPixels[((imgh - y - 1) * imgw) + x] = imgPixels[(y * imgw)
							+ x];
				}
			}
		}
		return flippedPixels;
	}

	/**
	 * Convert pixels from java default ARGB int format to byte array in RGBA
	 * format.
	 * 
	 * @param jpixels
	 * @return
	 */
	public static byte[] convertARGBtoRGBA(int[] jpixels) {
		byte[] bytes = new byte[jpixels.length * 4]; // will hold pixels as RGBA
														// bytes
		int p, r, g, b, a;
		int j = 0;
		for (int i = 0; i < jpixels.length; i++) {
			// int outPixel = 0x00000000; // AARRGGBB
			p = jpixels[i];
			a = (p >> 24) & 0xFF; // get pixel bytes in ARGB order
			r = (p >> 16) & 0xFF;
			g = (p >> 8) & 0xFF;
			b = (p >> 0) & 0xFF;
			bytes[j + 0] = (byte) r; // fill in bytes in RGBA order
			bytes[j + 1] = (byte) g;
			bytes[j + 2] = (byte) b;
			bytes[j + 3] = (byte) a;
			j += 4;
		}
		return bytes;
	}
	
	public static byte[] getRGBABytes(int[] jpixels) {
		byte[] bytes = new byte[jpixels.length * 4]; // will hold pixels as RGBA
														// bytes
		int p, r, g, b, a;
		int j = 0;
		for (int i = 0; i < jpixels.length; i++) {
			// int outPixel = 0x00000000; // AARRGGBB
			p = jpixels[i];
			r = (p >> 24) & 0xFF; // get pixel bytes in RGBA order
			g = (p >> 16) & 0xFF;
			b = (p >> 8) & 0xFF;
			a = (p >> 0) & 0xFF; 
			
			bytes[j + 0] = (byte) r; // fill in bytes in RGBA order
			bytes[j + 1] = (byte) g;
			bytes[j + 2] = (byte) b;
			bytes[j + 3] = (byte) a;
			j += 4;
		}
		return bytes;
	}

	/**
	 * Same function as in GLApp.java. Allocates a ByteBuffer to hold the given
	 * array of bytes.
	 * 
	 * @param bytearray
	 * @return ByteBuffer containing the contents of the byte array
	 */
	public static ByteBuffer allocBytes(byte[] bytearray) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length * SIZE_BYTE)
				.order(ByteOrder.nativeOrder());
		bb.put(bytearray).flip();
		return bb;
	}

	protected static final int SIZE_BYTE = 1;
	
	public static ByteBuffer toByteBuffer(Image img, int imgWidth, int imgHeight) {

		// Create a raster with correct size,
		// and a colorModel and finally a bufImg.
		//
		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, imgWidth, imgHeight, 4, null);
		ComponentColorModel colorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
				true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);
		BufferedImage bufImg = new BufferedImage(colorModel, // color model
				raster, false, // isRasterPremultiplied
				null); // properties

		// Filter img into bufImg and perform
		// Coordinate Transformations on the way.
		//
		Graphics2D g = bufImg.createGraphics();
		AffineTransform gt = new AffineTransform();
		gt.translate(0, imgHeight);
		gt.scale(1, -1d);
		g.transform(gt);
		g.drawImage(img, null, null);
		// Retrieve underlying byte array (imgBuf)
		// from bufImg.
		DataBufferByte imgBuf = (DataBufferByte) raster.getDataBuffer();
		byte[] imgRGBA = imgBuf.getData();

		ByteBuffer bb = ByteBuffer.allocateDirect(imgRGBA.length).order(ByteOrder.nativeOrder());
		bb.put( imgRGBA ).flip();
		return bb;
	}
}
