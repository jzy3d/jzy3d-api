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
package jgl.wt.awt;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.RenderedImage;

import jgl.ImageToDraw;
import jgl.TextToDraw;
import jgl.context.gl_util;

public final class GL extends jgl.GL<BufferedImage, Font> {

	protected Component canvas;
	protected BufferedImage glImage;
	protected boolean renderedOnce = false;

	/**
	 * Draws the image buffer that was built by {@link GL#glFlush()} with the caller {@link Graphics}
	 * context
	 * 
	 * OpenGL equivalent:
	 * 
	 * <code>void glXSwapBuffers (Display *dpy, GLXDrawable drawable)</code>
	 */
	public void glXSwapBuffers(Graphics g, ImageObserver o) {

		g.drawImage(glImage, StartX, StartY, desiredWidth, desiredHeight, o);
	}

	public void glXSwapBuffers(Graphics g, Applet o) {

		glXSwapBuffers(g, (ImageObserver)o);
	}

	@Override
	public void glFlush() {

		if(Context.RenderMode != GL_RENDER) {
			return;
		}
		// DEBUG
		// checkColorBuffer();
		
		// ------------------------------------------
		// Create an image producer based on
		// colorbuffer into which GL draws
		MemoryImageSource producer = new MemoryImageSource(Context.Viewport.Width, Context.Viewport.Height, Context.ColorBuffer.Buffer, 0, Context.Viewport.Width);
		// producer.setAnimated(true);
		// producer.setFullBufferUpdates(true);
		// Generates an image from the toolkit to use this producer
		
		Image jGLColorBuffer = canvas.createImage(producer);
		
		// ------------------------------------------
		// Write GL content in a temporary image
		// and then to the image returned to Canvas
		glImage = new BufferedImage(jGLColorBuffer.getWidth(null), jGLColorBuffer.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)glImage.getGraphics();
		configureRenderingHints(g2d);
		
		// Hack background
		if(clearBackgroundWithG2d)
			hackClearColorWithG2DfillRect(g2d);
		
		// Text that should appear BEHIND the scene's polygons
		drawTexts(g2d);
		
		// Images that should appear BEHIND the scene's polygons
		// drawImagesAndClearBuffer(g2d);
		drawImages(g2d, ImageLayer.BACKGROUND);
		
		// Color buffer
		g2d.drawImage(jGLColorBuffer, shiftHorizontally, 0, null);
		// Text that should appear ON TOP of the scene's polygons
		// ...
		
		// Images that should appear ON TOP of the scene's polygons
		drawImages(g2d, ImageLayer.FOREGROUND);
		clearImagesBuffer();
		// debugWriteImageTo("target/jGL.glFlush.png", (RenderedImage)JavaImage);
	}

	public BufferedImage getRenderedImage() {

		return glImage;
	}

	@Override
	public void applyViewport() {

		// Update pixel scale to guess if HiDPI
		if(canvas != null && canvas.getGraphics() != null)
			updatePixelScale(canvas.getGraphics());
		super.applyViewport();
	}

	/** Because this is for Java, use true color and double buffer default */
	/** Bool glXMakeCurrent (Display *dpy, GLXDrawable drawable, GLXcontext ctx) */
	// public boolean glXMakeCurrent (Applet o, int x, int y) {
	public boolean glXMakeCurrent(Component o, int x, int y) {

		// JavaApplet = o;
		canvas = o;
		StartX = x;
		StartY = y;
		// Context.gl_initialize_context (o.getSize().width, o.getSize().height);
		glViewport(x, y, o.getSize().width, o.getSize().height);
		Context.gl_initialize_context();
		return GL_TRUE;
	}

	public Component glJGetComponent() {

		return canvas;
	}

	public void updatePixelScale(Graphics g) {

		Graphics2D g2d = (Graphics2D)g;
		// printGlobalScale(g2d);
		// produce 2.0 factory on MacOS with Retina
		// produce 1.5 factor on Win10 HiDPI on the same Apple hardware as above
		// We will read pixel scale from G2D while swapping images. This means
		// we may be late of 1 image to adapt to an HiDPI change.
		if(autoAdaptToHiDPI) {
			getPixelScaleFromG2D(g2d);
			if(!renderedOnce) {
				// no event was sent has the default values
				// are 1,1 so we force an event
				if(pixelScaleX == 1 && pixelScaleY == 1)
					firePixelScaleChanged(1, 1);
				renderedOnce = true;
			}
		} else {
			resetPixelScale();
			if(!renderedOnce) {
				firePixelScaleChanged(1, 1);
				renderedOnce = true;
			}
		}
	}

	/** Pixel scale is used to model the pixel ratio introduced by HiDPI */
	protected void getPixelScaleFromG2D(Graphics2D g2d) {

		AffineTransform globalTransform = g2d.getTransform();
		double oldPixelScaleX = pixelScaleX;
		double oldPixelScaleY = pixelScaleY;
		double newPixelScaleX = globalTransform.getScaleX();
		double newPixelScaleY = globalTransform.getScaleY();
		setPixelScaleX(newPixelScaleX);
		setPixelScaleY(newPixelScaleY);
		if(newPixelScaleX != oldPixelScaleX || newPixelScaleY != oldPixelScaleY) {
			firePixelScaleChanged(newPixelScaleX, newPixelScaleY);
		}
	}

	protected void printGlobalScale(Graphics2D g2d) {

		AffineTransform globalTransform = g2d.getTransform();
		double globalScaleX = globalTransform.getScaleX();
		double globalScaleY = globalTransform.getScaleY();
		System.out.println("globalScaleX:" + globalScaleX + " globalScaleY:" + globalScaleY);
	}

	protected void configureRenderingHints(Graphics2D g2d) {

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
	}

	protected void hackClearColorWithG2DfillRect(Graphics2D g2d) {

		Color backgroundColor = getClearColorAWT();
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, Context.Viewport.Width, Context.Viewport.Height);
	}

	/**
	 * Convert a color given as an integer to an AWT Color.
	 */
	public static Color glIntToColor(int color) {

		float r = gl_util.ItoR(color);
		float g = gl_util.ItoG(color);
		float b = gl_util.ItoB(color);
		// float a = gl_util.ItoA(color);
		return new Color(r / 255, g / 255, b / 255);
	}

	/**
	 * @return the clear color as an AWT Color.
	 */
	public Color getClearColorAWT() {

		int clearColor = getContext().ColorBuffer.IntClearColor;
		return glIntToColor(clearColor);
	}



	protected void drawImagesAndClearBuffer(Graphics2D g2d) {

		synchronized(imageToDraw) {
			for(ImageToDraw<BufferedImage> img : imageToDraw) {
				g2d.drawImage(img.image, img.x + shiftHorizontally, img.y, null);
			}
			imageToDraw.clear(); // empty image buffer
		}
	}

	protected void drawImages(Graphics2D g2d, ImageLayer layer) {

		synchronized(imageToDraw) {
			for(ImageToDraw<BufferedImage> img : imageToDraw) {
				if(img.layer == null || img.layer.equals(layer)) {
					g2d.drawImage(img.image, img.x + shiftHorizontally, img.y, null);
				}
			}
		}
	}

	/**
	 * Renders appended text to given {@link Graphics2D} context.
	 * 
	 * @param g2d
	 */
	protected void drawTexts(Graphics2D g2d) {

		synchronized(textsToDraw) {
			for(TextToDraw<Font> text : textsToDraw) {
				doDrawString(g2d, text);
			}
			textsToDraw.clear(); // empty text buffer
		}
	}

	protected void doDrawString(Graphics2D g2d, TextToDraw<Font> text) {

		// AffineTransform orig = g2d.getTransform();
		// g2d.rotate(Math.PI/2);
		if(text.r >= 0) {
			g2d.setColor(new Color(text.r, text.g, text.b));
		} else {
			g2d.setColor(Color.BLACK);
		}
		int x = text.x + shiftHorizontally;
		int y = text.y;
		float rotate = text.rotate;
		int textWidth = 0;
		g2d.setFont(text.font);
		FontMetrics fm = g2d.getFontMetrics();
		if(fm != null) {
			textWidth = fm.stringWidth(text.string);
		}
		preRotateFromLeftPoint(g2d, x, y, rotate, textWidth);
		// rotation point / position of text
		// g2d.fillRect(-1, -1, 2, 2);
		if(useOSFontRendering) {
			g2d.drawString(text.string, 0, 0);
		} else {
			FontRenderContext frc = g2d.getFontRenderContext();
			GlyphVector gv = text.font.createGlyphVector(frc, text.string);
			g2d.drawGlyphVector(gv, 0, 0);
		}
		postRotateFromLeftPoint(g2d, x, y, rotate, textWidth);
	}

	protected void preRotateFromLeftPoint(Graphics2D g2d, int x, int y, float rotate, int textWidth) {

		if(rotate != 0) {
			g2d.translate(textWidth / 2, 0);
		}
		g2d.translate(x, y);
		if(rotate != 0) {
			g2d.rotate(rotate);
			g2d.translate(-textWidth / 2, 0);
		}
	}

	protected void postRotateFromLeftPoint(Graphics2D g2d, int x, int y, float rotate, int textWidth) {

		if(rotate != 0) {
			g2d.translate(textWidth / 2, 0);
			g2d.rotate(-rotate);
		}
		g2d.translate(-x, -y);
		if(rotate != 0) {
			g2d.translate(-textWidth / 2, 0);
		}
	}

}