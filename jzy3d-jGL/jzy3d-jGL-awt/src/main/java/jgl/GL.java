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

import java.util.ArrayList;
import java.util.List;
import jgl.context.gl_context;
import jgl.context.gl_list;
import jgl.context.gl_object;
import jgl.context.gl_pointer;
import jgl.context.gl_util;
import jgl.wt.awt.GLUT;

/**
 * GL is the main class of jGL 2.4.
 *
 * @version 0.9, 14 May 2003
 * @author Robin Bing-Yu Chen
 */

public abstract class GL<ImageType, FontType> {

  protected gl_context Context = new gl_context();
  protected gl_object CC = Context;
  protected gl_list List;

  protected int StartX = 0;
  protected int StartY = 0;
  protected List<TextToDraw<FontType>> textsToDraw = new ArrayList<>();
  protected List<ImageToDraw<ImageType>> imageToDraw = new ArrayList<>();

  // settings
  protected int shiftHorizontally = 0;
  /** Activate a hack because of inappropriate glClear command behaviour */
  protected boolean clearBackgroundWithG2d = true;
  /**
   * Disabling OS Font rendering and using direct JVM allows better consistency among chart which
   * improve pixel-wise tests for portability
   */
  protected boolean useOSFontRendering = false;

  /**
   * This property allows to automatically read pixel scale of the parent canvas while getting its
   * Graphics2D context {@link glXSwapBuffers}
   */
  protected boolean autoAdaptToHiDPI = true;
  /**
   * Width queried to gl.glViewport without considering HiDPI, e.g. by simply using the EmulGLCanvas
   * size.
   */
  protected int desiredWidth = 0;
  /**
   * Height queried to gl.glViewport without considering HiDPI, e.g. by simply using the
   * EmulGLCanvas size.
   */
  protected int desiredHeight = 0;
  /** Horizontal pixel scale induced by HiDPI. */
  protected double pixelScaleX = 1;
  /** Vertical pixel scale induced by HiDPI. */
  protected double pixelScaleY = 1;

  protected int desiredX = 0;
  protected int desiredY = 0;

  /** Width after considering pixel scale induced by HiDPI. */
  protected int actualWidth = 0;
  /** Height after considering pixel scale induced by HiDPI. */
  protected int actualHeight = 0;

  protected List<PixelScaleListener> pixelScaleListeners = new ArrayList<>();

  public GL() {}
  
  public void setThrowExceptionOnGLError(boolean status) {
    this.CC.setThrowExceptionOnGLError(status);
  }

  public boolean getThrowExceptionOnGLError() {
    return CC.getThrowExceptionOnGLError();
  }

  public gl_context getContext() {
    return Context;
  }

  public gl_pointer getPointer() {
    return Context.CR;
  }

  public double getPixelScaleX() {
    return pixelScaleX;
  }

  public double getPixelScaleY() {
    return pixelScaleY;
  }

  public int getStartX() {
    return StartX;
  }

  public int getStartY() {
    return StartY;
  }

  public int getDesiredWidth() {
    return desiredWidth;
  }

  public int getDesiredHeight() {
    return desiredHeight;
  }

  /* ******************** PROVIDE IMAGE ********************/

  public void glShadeModel(int mode) {
  	if(CC.Mode != None) {
  	  CC.gl_error(GL_INVALID_OPERATION, "glShadeModel");
  	  return;
  	}
  	CC.gl_shade_model(mode);
  }


  /* ******************** FLUSH IN IMAGE ********************/

  /**
   * Creates a new ImageType based on the current color buffer.
   * 
   */
  public abstract void glFlush();


  public void addPixelScaleListener(PixelScaleListener listener) {
    pixelScaleListeners.add(listener);
  }

  public void removePixelScaleListener(PixelScaleListener listener) {
    pixelScaleListeners.remove(listener);
  }

  public List<PixelScaleListener> getPixelScaleListeners() {
    return pixelScaleListeners;
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (PixelScaleListener listener : pixelScaleListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
  }

  public interface PixelScaleListener {
    public void pixelScaleChanged(double pixelScaleX, double pixelScaleY);
  }


  public void setPixelScaleX(double pixelScaleX) {
    this.pixelScaleX = pixelScaleX;
  }

  public void setPixelScaleY(double pixelScaleY) {
    this.pixelScaleY = pixelScaleY;
  }

  /** Reset pixel scale to (1,1) */
  protected void resetPixelScale() {
    if (pixelScaleX != 1 || pixelScaleY != 1) {
      firePixelScaleChanged(1, 1);
    }

    pixelScaleX = 1;
    pixelScaleY = 1;
  }

  /**
   * Print statistics about color buffer content for debugging.
   */
  @SuppressWarnings("unused")
  protected void checkColorBuffer() {
    int a0 = 0;
    int a255 = 0;
    int black = 0;
    int notBlack = 0;

    for (int i = 0; i < Context.ColorBuffer.Buffer.length; i++) {
      int color = Context.ColorBuffer.Buffer[i];

      int r = gl_util.ItoR(color);
      int g = gl_util.ItoG(color);
      int b = gl_util.ItoB(color);
      int a = gl_util.ItoA(color);

      // CHECK NUMBER OF BLACK PIXEL == ALPHA PIXELS

      if ((r + g + b) == 0)
        black++;
      if ((r + g + b) > 0)
        notBlack++;

      if (a == 0)
        a0++;
      if (a == 255)
        a255++;
    }

    System.out.println("glFlush() #translucent = " + a0 + " #opaque = " + a255 + " #black=" + black
        + " #notBlack=" + notBlack);
  }

  /**
   * @see {@link #setShiftHoritontally(int)}
   */
  public int getShiftHorizontally() {
    return shiftHorizontally;
  }

  	/**
	 * Allows shifting the image of the 3d scene and all images that have been append with
	 * {@link #appendImageToDraw(Object)}
	 * 
	 * @param shift
	 *                  to the right if value is positive, to the left if the value is negative.
	 */
  public void setShiftHorizontally(int shiftHorizontally) {
    this.shiftHorizontally = shiftHorizontally;
  }

  /**
   * @see {@link #setClearBackgroundWithG2d(boolean)}
   */
  public boolean isClearBackgroundWithG2d() {
    return clearBackgroundWithG2d;
  }

  /**
   * If true, will clear the background with a rectangle colored with the current GL clear color.
   */
  public void setClearBackgroundWithG2d(boolean clearBackgroundWithG2d) {
    this.clearBackgroundWithG2d = clearBackgroundWithG2d;
  }

  public boolean isUseOSFontRendering() {
    return useOSFontRendering;
  }

  	/**
	 * If true, will use the OS for font rendering of all texts that have been append with
	 * {@link #appendTextToDraw(Object, String, int, int)} otherwise use a JVM based font rendering.
	 */
  public void setUseOSFontRendering(boolean useOSFontRendering) {
    this.useOSFontRendering = useOSFontRendering;
  }

  public boolean isAutoAdaptToHiDPI() {
    return autoAdaptToHiDPI;
  }

  /**
   * If true, will consider pixel resolution of an HiDPI device to render chart with a better look.
   * Texts render smaller as the font size is given in pixel. To avoid this, either use bigger font
   * size or disable HiDPI adaptation.
   */
  public void setAutoAdaptToHiDPI(boolean autoAdaptToHiDPI) {
    this.autoAdaptToHiDPI = autoAdaptToHiDPI;
  }

  /* ********************** IMAGE OVERLAY WITH AWT ************************/

  public enum ImageLayer {
    FOREGROUND, BACKGROUND
  }

	public void appendImageToDraw(ImageType image) {

		appendImageToDraw(image, 0, 0);
	}

	public void appendImageToDraw(ImageType image, int x, int y) {

		appendImageToDraw(image, x, y, ImageLayer.BACKGROUND);
	}

	public void appendImageToDraw(ImageType image, int x, int y, ImageLayer layer) {

		synchronized(imageToDraw) {
			imageToDraw.add(new ImageToDraw<>(x, y, image, layer));
			// System.out.println(imageToDraw.size() + " images to draw");
		}
	}

	public abstract ImageType getRenderedImage();

  protected void clearImagesBuffer() {
    synchronized (imageToDraw) {
      imageToDraw.clear(); // empty image buffer
    }
  }

  /* ********************** TEXT MANAGEMENT WITH AWT ************************/



  	/**
	 * To be called by {@link GLUT#glutBitmapString(Object, String, float, float)} to append text to a
	 * list of text to render at {@link GL#glFlush()} step.
	 */
	public void appendTextToDraw(FontType font, String string, int x, int y) {
    synchronized (textsToDraw) {
			textsToDraw.add(new TextToDraw<>(font, string, x, y));
    }
  }

  	/**
	 * To be called by {@link GLUT#glutBitmapString(Object, String, float, float)} to append text to a
	 * list of text to render at {@link GL#glFlush()} step.
	 */
	public void appendTextToDraw(FontType font, String string, int x, int y, float r, float g, float b,
      float rotate) {
    synchronized (textsToDraw) {
		textsToDraw.add(new TextToDraw<>(font, string, x, y, r, g, b, rotate));
    }
  }


  /**
   *
   * Data types Because Java is architecture IN-dependent use the original data types of Java
   * instead of OpenGL
   *
   * Java type GL type Number of Bits Description
   * ---------------------------------------------------------------
   * 
   * <pre>
   * void GLvoid 0 bit 
   * boolean GLboolean 1 bit 
   * Boolean byte GLbyte 8 bits 
   * signed binary integer byte GLubyte 8 bits 
   * unsigned binary integer short GLshort 16 bits 
   * signed binary integer short GLushort 16 bits
   * unsigned binary integer int GLint 32 bits 
   * signed binary integer int GLuint 32 bits
   * unsigned binary integer int GLsizei 32 bits 
   * Non-negative binary integer size int GLenum 32 bits
   * Enumerated binary integer value int GLbitfield 32 bits 
   * Bit field float GLfloat 32 bits 
   * Floating-point value float GLclampf 32 bits 
   * Floating-point value clamped to [0,1] double GLdouble 64 bits 
   * Floating-point value double GLclampd 64 bits
   * Floating-point value clamped to [0,1] Object GLvoid * ?? bits 
   * Pointer to arbitrary data types
   * </pre>
   */

  /* only called by glEnable and glDisable */
  private void gl_enable(int cap, boolean state) {
    if (CC.Mode != None) {
      if (state) {
        CC.gl_error(GL_INVALID_OPERATION, "glEnable");
      } else {
        CC.gl_error(GL_INVALID_OPERATION, "glDisable");
      }
      return;
    }
    switch (cap) {
      case GL_ALPHA_TEST:
        // case GL_AUTO_NORMAL:
      case GL_BLEND:
      case GL_CLIP_PLANE0:
      case GL_CLIP_PLANE1:
      case GL_CLIP_PLANE2:
      case GL_CLIP_PLANE3:
      case GL_CLIP_PLANE4:
      case GL_CLIP_PLANE5:
      case GL_COLOR_MATERIAL:
      case GL_CULL_FACE:
      case GL_DEPTH_TEST:
      case GL_DITHER:
      case GL_FOG:
      case GL_LIGHT0:
      case GL_LIGHT1:
      case GL_LIGHT2:
      case GL_LIGHT3:
      case GL_LIGHT4:
      case GL_LIGHT5:
      case GL_LIGHT6:
      case GL_LIGHT7:
      case GL_LIGHTING:
      case GL_LINE_SMOOTH:
      case GL_LINE_STIPPLE:
      case GL_LOGIC_OP:
        /*
         * case GL_MAP1_COLOR_4: case GL_MAP1_INDEX: case GL_MAP1_NORMAL: case
         * GL_MAP1_TEXTURE_COORD_1: case GL_MAP1_TEXTURE_COORD_2: case GL_MAP1_TEXTURE_COORD_3: case
         * GL_MAP1_TEXTURE_COORD_4: case GL_MAP1_VERTEX_3: case GL_MAP1_VERTEX_4: case
         * GL_MAP2_COLOR_4: case GL_MAP2_INDEX: case GL_MAP2_NORMAL: case GL_MAP2_TEXTURE_COORD_1:
         * case GL_MAP2_TEXTURE_COORD_2: case GL_MAP2_TEXTURE_COORD_3: case GL_MAP2_TEXTURE_COORD_4:
         * case GL_MAP2_VERTEX_3: case GL_MAP2_VERTEX_4:
         */
      case GL_NORMALIZE:
      case GL_POINT_SMOOTH:
      case GL_POLYGON_SMOOTH:
      case GL_POLYGON_STIPPLE:
      case GL_POLYGON_OFFSET_EXT:
      case GL_SCISSOR_TEST:
      case GL_STENCIL_TEST:
      case GL_TEXTURE_1D:
      case GL_TEXTURE_2D:
      case GL_TEXTURE_3D:
      case GL_TEXTURE_GEN_Q:
      case GL_TEXTURE_GEN_R:
      case GL_TEXTURE_GEN_S:
      case GL_TEXTURE_GEN_T:
        CC.gl_enable(cap, state);
        break;
      case GL_AUTO_NORMAL:
      case GL_MAP1_COLOR_4:
      case GL_MAP1_INDEX:
      case GL_MAP1_NORMAL:
      case GL_MAP1_TEXTURE_COORD_1:
      case GL_MAP1_TEXTURE_COORD_2:
      case GL_MAP1_TEXTURE_COORD_3:
      case GL_MAP1_TEXTURE_COORD_4:
      case GL_MAP1_VERTEX_3:
      case GL_MAP1_VERTEX_4:
      case GL_MAP2_COLOR_4:
      case GL_MAP2_INDEX:
      case GL_MAP2_NORMAL:
      case GL_MAP2_TEXTURE_COORD_1:
      case GL_MAP2_TEXTURE_COORD_2:
      case GL_MAP2_TEXTURE_COORD_3:
      case GL_MAP2_TEXTURE_COORD_4:
      case GL_MAP2_VERTEX_3:
      case GL_MAP2_VERTEX_4:
        Context.gl_enable(cap, state);
        break;
      default:
        if (state) {
          CC.gl_error(GL_INVALID_ENUM, "glEnable(cap)");
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glDisable(cap)");
        }
    }
  }

  /* used to get the size of type */
  private int size_of(int type) {
    switch (type) {
      case GL_BITMAP:
        return 0;
      case GL_UNSIGNED_BYTE:
      case GL_BYTE:
        return 8;
      case GL_UNSIGNED_SHORT:
      case GL_SHORT:
        return 16;
      case GL_UNSIGNED_INT:
      case GL_INT:
        return 32;
      case GL_FLOAT:
        // float has the same bit-number as int, set 64 to distinguish
        return 64;
      default:
        return -1;
    }
  }

  /* used to get bytes of type */
  private int bytes_of(int type) {
    switch (type) {
      case GL.GL_COLOR_INDEX:
        return 1;
      case GL.GL_RGB:
        return 3;
      case GL.GL_RGBA:
        return 4;
      case GL.GL_RED:
        return 1;
      case GL.GL_GREEN:
        return 1;
      case GL.GL_BLUE:
        return 1;
      case GL.GL_ALPHA:
        return 1;
      case GL.GL_LUMINANCE:
        return 1;
      case GL.GL_LUMINANCE_ALPHA:
        return 2;
      default:
        return -1;
    }
  }

  /* only used by glCallLists */
  private int translate_id(int n, int type, byte lists[]) {
    switch (type) {
      case GL_BYTE:
      case GL_UNSIGNED_BYTE:
        return lists[n];
      case GL_SHORT:
      case GL_UNSIGNED_SHORT:
      case GL_2_BYTES:
        return (lists[n << 1]) << 8 | (lists[(n << 1) | 1]);
      case GL_3_BYTES:
        return (lists[(n << 1) + n]) << 16 | (lists[(n << 1) + n + 1]) << 8
            | (lists[(n << 1) + n + 2]);
      case GL_INT:
      case GL_UNSIGNED_INT:
      case GL_4_BYTES:
        return (lists[n << 2]) << 24 | (lists[(n << 2) | 1]) << 16
            | (lists[(n << 2) | 2]) << 8 | (lists[(n << 2) | 3]);
      case GL_FLOAT:
        return (int) ((lists[n << 2]) * 16777216.0f + (lists[(n << 2) | 1]) * 65536.0f
            + (lists[(n << 2) | 2]) * 256.0f + (lists[(n << 2) | 3]));
      default:
        return 0;
    }
  }

  /*
   * Miscellaneous
   */

  /**
   * Sets the clear value for the color buffers in RGBA mode. Each of the specified components is
   * clamped to [0,1] and converted to fixed-point.
   * 
   * @param red the red component.
   * @param green the green component.
   * @param blue the blue component.
   * @param alpha the alpha component.
   * @see #glClear(int)
   */
  /*
   * GLvoid glClearColor (GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha)
   */
  public void glClearColor(float red, float green, float blue, float alpha) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glClearColor");
      return;
    }
    CC.gl_clear_color(gl_util.CLAMP(red, 0.0f, 1.0f), gl_util.CLAMP(green, 0.0f, 1.0f),
        gl_util.CLAMP(blue, 0.0f, 1.0f), gl_util.CLAMP(alpha, 0.0f, 1.0f));
  }

  /**
   * Clears the specified buffers to their current clearing values. The <i>mask</i> argument is the
   * bitwise-ORed combination of a number of values indicating which buffers are to be cleared. The
   * values are GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_STENCIL_BUFFER_BIT, and
   * GL_ACCUM_BUFFER_BIT.
   * 
   * @param mask the bitwise-ORed of a number of values.
   * @see #GL_COLOR_BUFFER_BIT
   * @see #GL_DEPTH_BUFFER_BIT
   * @see #GL_STENCIL_BUFFER_BIT
   * @see #GL_ACCUM_BUFFER_BIT
   * @see #glClearColor(float, float, float, float)
   * @see #glClearDepth(double)
   * @see #glClearStencil(int)
   * @see #glClearAccum(float, float, float, float)
   */
  /* GLvoid glClear (GLbitfield mask) */
  public void glClear(int mask) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glClearColor");
      return;
    }
    if ((mask & GL_COLOR_BUFFER_BIT) != 0) {
      CC.gl_clear_color_buffer();
    }
    if ((mask & GL_DEPTH_BUFFER_BIT) != 0) {
      CC.gl_clear_depth_buffer();
    }
    if ((mask & GL_STENCIL_BUFFER_BIT) != 0) {
      CC.gl_clear_stencil_buffer();
    }
    if ((mask & GL_ACCUM_BUFFER_BIT) != 0) {
      // CC.gl_clear_accum_buffer ();
    }
  }

  /**
   * GLvoid glColorMask (GLboolean red, GLboolean green, GLboolean blue, GLboolean alpha )
   */
  public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glColorMask");
      return;
    }
    CC.gl_color_mask(red, green, blue, alpha);
  }

  private boolean check_blend_factor(int factor) {
    switch (factor) {
      case GL_ZERO:
      case GL_ONE:
      case GL_DST_COLOR:
      case GL_SRC_COLOR:
      case GL_ONE_MINUS_DST_COLOR:
      case GL_ONE_MINUS_SRC_COLOR:
      case GL_SRC_ALPHA:
      case GL_ONE_MINUS_SRC_ALPHA:
      case GL_DST_ALPHA:
      case GL_ONE_MINUS_DST_ALPHA:
      case GL_SRC_ALPHA_SATURATE:
        return true;
      default:
        return false;
    }
  }

  /** GLvoid glBlendFunc (GLenum sfactor, GLenum dfactor) */
  public void glBlendFunc(int sfactor, int dfactor) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glBlendFunc");
      return;
    }
    if (!check_blend_factor(sfactor)) {
      CC.gl_error(GL_INVALID_ENUM, "glBlendFunc(sfactor)");
      return;
    }
    if (!check_blend_factor(dfactor)) {
      CC.gl_error(GL_INVALID_ENUM, "glBlendFunc(dfactor)");
      return;
    }
    CC.gl_blend_func(sfactor, dfactor);
  }

  /** GLvoid glCullFace (GLenum mode) */
  public void glCullFace(int mode) {
    if ((mode != GL_FRONT) && (mode != GL_BACK) && (mode != GL_FRONT_AND_BACK)) {
      CC.gl_error(GL_INVALID_ENUM, "glCullFace(mode)");
      return;
    }
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glCullFace");
      return;
    }
    CC.gl_cull_face(mode);
  }

  /** GLvoid glFrontFace (GLenum mode) */
  public void glFrontFace(int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glFrontFace");
      return;
    }
    if ((mode != GL_CW) && (mode != GL_CCW)) {
      CC.gl_error(GL_INVALID_ENUM, "glFrontFace(mode)");
      return;
    }
    CC.gl_front_face(mode);
  }

  /**
   * Sets the width in pixels for rendered points. <i>size</i> must be greater than 0.0 and by
   * default is 1.0.
   * 
   * @param size the width or diameter of a point.
   */
  /* GLvoid glPointSize (GLfloat size) */
  public void glPointSize(float size) {
    if (size <= 0) {
      CC.gl_error(GL_INVALID_VALUE, "glPointSize(size)");
      return;
    }
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPointSize");
      return;
    }
    CC.gl_point_size(gl_util.CLAMP(size, gl_context.MIN_POINT_SIZE, gl_context.MAX_POINT_SIZE));
  }

  /**
   * Sets the width in pixels for rendered lines. <i>width</i> must be greater than 0.0 and by
   * default is 1.0.
   * 
   * @param width the width of a line.
   */
  /* GLvoid glLineWidth (GLfloat width) */
  public void glLineWidth(float width) {
    if (width <= 0) {
      CC.gl_error(GL_INVALID_VALUE, "glLineWidth(width)");
      return;
    }
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLineWidth");
      return;
    }
    CC.gl_line_width(gl_util.CLAMP(width, gl_context.MIN_LINE_SIZE, gl_context.MAX_LINE_SIZE));
  }

  /**
   * Sets the current stippling patterm for lines. The <i>pattern</i> argument is a 16-bit series of
   * 0s and 1s, and it's repeated as necessary to stipple a given line. A 1 indicates that drawing
   * occurs, and 0 that it does not, on a pixel-by-pixel basis, beginning with the low-order bits of
   * the pattern. The pattern can be stretched out by using <i>factor</i>, which multiplies each
   * subseries of consecutive 1s and 0s.
   * 
   * @param factor the count.
   * @param pattern pattern.
   * @see #GL_LINE_STIPPLE
   */
  /* GLvoid glLineStipple (GLint factor, GLushort pattern) */
  public void glLineStipple(int factor, short pattern) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLineStipple");
      return;
    }
    CC.gl_line_stipple(gl_util.CLAMP(factor, 1, 256), pattern);
  }

  /** GLvoid glPolygonMode (GLenum face, GLenum mode ) */
  public void glPolygonMode(int face, int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPolygonMode");
      return;
    }
    if ((face != GL_FRONT) && (face != GL_BACK) && (face != GL_FRONT_AND_BACK)) {
      CC.gl_error(GL_INVALID_ENUM, "glPolygonMode(face)");
      return;
    }
    if ((mode != GL_POINT) && (mode != GL_LINE) && (mode != GL_FILL)) {
      CC.gl_error(GL_INVALID_ENUM, "glPolygonMode(mode)");
      return;
    }
    CC.gl_polygon_mode(face, mode);
  }

  /**
   * Defines the current stipple pattern for filled polygons. The argument <i>mask</i> is a 32x32
   * bitmap that's interpreted as a mask of 0s and 1s. Where a 1 appears, the corresponding pixel in
   * the polygon is drawn, and where a 0 appears, nothing is drawn.
   * 
   * @param mask the mask bitmap.
   * @see #GL_POLYGON_STIPPLE
   */
  /* GLvoid glPolygonStipple (const GLubyte *mask) */
  public void glPolygonStipple(byte mask[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPolygonStipple");
      return;
    }
    CC.gl_polygon_stipple(mask);
  }

  /** GLvoid glClipPlane (GLenum plane, const GLdouble *equation) */
  public void glClipPlane(int plane, double equation[]) {
    float fe[] =
        {(float) equation[0], (float) equation[1], (float) equation[2], (float) equation[3]};
    if (plane < GL_CLIP_PLANE0 || plane - GL_CLIP_PLANE0 >= gl_context.MAX_CLIP_PLANES) {
      CC.gl_error(GL_INVALID_ENUM, "glClipPlane");
      return;
    }
    CC.gl_clip_plane(plane - GL_CLIP_PLANE0, fe);
  }

  /** GLvoid glGetClipPlane (GLenum plane, GLdouble *equation) */
  public void glGetClipPlane(int plane, double equation[]) {
    float fe[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetClipPlane");
      return;
    }
    if (plane < GL_CLIP_PLANE0 || plane - GL_CLIP_PLANE0 >= gl_context.MAX_CLIP_PLANES) {
      CC.gl_error(GL_INVALID_ENUM, "glGetClipPlane(plane)");
      return;
    }
    fe = CC.gl_get_clip_plane(plane - GL_CLIP_PLANE0);
    equation[0] = fe[0];
    equation[1] = fe[1];
    equation[2] = fe[2];
    equation[3] = fe[3];
  }

  /** GLvoid glEnable (GLenum cap) */
  public void glEnable(int cap) {
    gl_enable(cap, true);
  }

  /** GLvoid glDisable (GLenum cap) */
  public void glDisable(int cap) {
    gl_enable(cap, false);
  }

  /** GLboolean glIsEnabled (GLenum cap) */
  public boolean glIsEnabled(int cap) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glIsEnabled");
      return false;
    }
    switch (cap) {
      case GL_ALPHA_TEST:
      case GL_AUTO_NORMAL:
      case GL_BLEND:
      case GL_CLIP_PLANE0:
      case GL_CLIP_PLANE1:
      case GL_CLIP_PLANE2:
      case GL_CLIP_PLANE3:
      case GL_CLIP_PLANE4:
      case GL_CLIP_PLANE5:
      case GL_COLOR_MATERIAL:
      case GL_CULL_FACE:
      case GL_DEPTH_TEST:
      case GL_DITHER:
      case GL_FOG:
      case GL_LIGHT0:
      case GL_LIGHT1:
      case GL_LIGHT2:
      case GL_LIGHT3:
      case GL_LIGHT4:
      case GL_LIGHT5:
      case GL_LIGHT6:
      case GL_LIGHT7:
      case GL_LIGHTING:
      case GL_LINE_SMOOTH:
      case GL_LINE_STIPPLE:
      case GL_LOGIC_OP:
      case GL_MAP1_COLOR_4:
      case GL_MAP1_INDEX:
      case GL_MAP1_NORMAL:
      case GL_MAP1_TEXTURE_COORD_1:
      case GL_MAP1_TEXTURE_COORD_2:
      case GL_MAP1_TEXTURE_COORD_3:
      case GL_MAP1_TEXTURE_COORD_4:
      case GL_MAP1_VERTEX_3:
      case GL_MAP1_VERTEX_4:
      case GL_MAP2_COLOR_4:
      case GL_MAP2_INDEX:
      case GL_MAP2_NORMAL:
      case GL_MAP2_TEXTURE_COORD_1:
      case GL_MAP2_TEXTURE_COORD_2:
      case GL_MAP2_TEXTURE_COORD_3:
      case GL_MAP2_TEXTURE_COORD_4:
      case GL_MAP2_VERTEX_3:
      case GL_MAP2_VERTEX_4:
      case GL_NORMALIZE:
      case GL_POINT_SMOOTH:
      case GL_POLYGON_SMOOTH:
      case GL_POLYGON_STIPPLE:
      case GL_POLYGON_OFFSET_EXT:
      case GL_SCISSOR_TEST:
      case GL_STENCIL_TEST:
      case GL_TEXTURE_1D:
      case GL_TEXTURE_2D:
      case GL_TEXTURE_3D:
      case GL_TEXTURE_GEN_Q:
      case GL_TEXTURE_GEN_R:
      case GL_TEXTURE_GEN_S:
      case GL_TEXTURE_GEN_T:
        return (CC.gl_is_enabled(cap));
      default:
        CC.gl_error(GL_INVALID_ENUM, "glIsEnabled(cap)");
        return false;
    }
  }

  /** GLvoid glGetBooleanv (GLenum pname, GLboolean *params) */
  public void glGetBooleanv(int pname, boolean params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetBooleanv");
      return;
    }
    switch (pname) {
      case GL_CURRENT_RASTER_POSITION_VALID:
      case GL_EDGE_FLAG:
        break;
      case GL_LIGHT_MODEL_LOCAL_VIEWER:
        params[0] = Context.Lighting.LightModelLocalViewer;
        break;
      case GL_LIGHT_MODEL_TWO_SIDE:
        params[0] = Context.Lighting.LightModelTwoSide;
        break;
      case GL_COLOR_WRITEMASK:
        params[0] = (gl_util.ItoR(Context.ColorBuffer.ColorMask) != 0);
        params[1] = (gl_util.ItoG(Context.ColorBuffer.ColorMask) != 0);
        params[2] = (gl_util.ItoB(Context.ColorBuffer.ColorMask) != 0);
        params[3] = (gl_util.ItoA(Context.ColorBuffer.ColorMask) != 0);
        break;
      case GL_DEPTH_WRITEMASK:
        params[0] = Context.DepthBuffer.Mask;
        break;
      case GL_UNPACK_SWAP_BYTES:
        params[0] = Context.Pixel.Unpack.SwapBytes;
        break;
      case GL_UNPACK_LSB_FIRST:
        params[0] = Context.Pixel.Unpack.LsbFirst;
        break;
      case GL_PACK_SWAP_BYTES:
        params[0] = Context.Pixel.Pack.SwapBytes;
        break;
      case GL_PACK_LSB_FIRST:
        params[0] = Context.Pixel.Pack.LsbFirst;
        break;
      case GL_MAP_COLOR:
        params[0] = Context.Pixel.MapColor;
        break;
      case GL_MAP_STENCIL:
        params[0] = Context.Pixel.MapStencil;
        break;
      case GL_AUX_BUFFERS:
        break;
      case GL_RGBA_MODE:
        params[0] = true;
        break;
      case GL_INDEX_MODE:
        break;
      case GL_DOUBLEBUFFER:
        params[0] = true;
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetBooleanv(pname)");
    }
  }

  /** GLvoid glGetDoublev (GLenum pname, GLdouble *params) */
  public void glGetDoublev(int pname, double params[]) {
    int i;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetdoublev");
      return;
    }
    switch (pname) {
      case GL_CURRENT_COLOR:
      case GL_CURRENT_INDEX:
      case GL_CURRENT_TEXTURE_COORDS:
      case GL_CURRENT_NORMAL:
      case GL_CURRENT_RASTER_POSITION:
      case GL_CURRENT_RASTER_DISTANCE:
      case GL_CURRENT_RASTER_COLOR:
      case GL_CURRENT_RASTER_INDEX:
      case GL_CURRENT_RASTER_TEXTURE_COORDS:
      case GL_MODELVIEW_MATRIX:
      case GL_PROJECTION_MATRIX:
      case GL_TEXTURE_MATRIX:
      case GL_DEPTH_RANGE:
      case GL_FOG_COLOR:
      case GL_FOG_INDEX:
      case GL_FOG_DENSITY:
      case GL_FOG_START:
      case GL_FOG_END:
      case GL_FOG_MODE:
      case GL_LIGHT_MODEL_AMBIENT:
      case GL_COLOR_INDEXES:
      case GL_POINT_SIZE:
      case GL_LINE_WIDTH:
      case GL_COLOR_CLEAR_VALUE:
      case GL_INDEX_CLEAR_VALUE:
      case GL_ACCUM_CLEAR_VALUE:
      case GL_RED_SCALE:
      case GL_GREEN_SCALE:
      case GL_BLUE_SCALE:
      case GL_ALPHA_SCALE:
      case GL_DEPTH_SCALE:
      case GL_RED_BIAS:
      case GL_GREEN_BIAS:
      case GL_BLUE_BIAS:
      case GL_ALPHA_BIAS:
      case GL_DEPTH_BIAS:
      case GL_ZOOM_X:
      case GL_ZOOM_Y:
      case GL_MAP1_GRID_DOMAIN:
      case GL_MAP2_GRID_DOMAIN:
      case GL_MAP1_GRID_SEGMENTS:
      case GL_MAP2_GRID_SEGMENTS:
      case GL_STEREO:
      case GL_POINT_SIZE_RANGE:
      case GL_POINT_SIZE_GRANULARITY:
      case GL_LINE_WIDTH_RANGE:
      case GL_LINE_WIDTH_GRANULARITY:
        float temp[] = new float[params.length];
        glGetFloatv(pname, temp);
        for (i = 0; i < params.length; i++) {
          params[i] = temp[i];
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetDoublev(pname)");
    }
  }

  /** GLvoid glGetFloatv (GLenum pname, GLfloat *params) */
  public void glGetFloatv(int pname, float params[]) {
    int i;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetFloatv");
      return;
    }
    switch (pname) {
      case GL_CURRENT_COLOR:
        params[0] = Context.Current.Color[0];
        params[1] = Context.Current.Color[1];
        params[2] = Context.Current.Color[2];
        params[3] = Context.Current.Color[3];
        break;
      case GL_CURRENT_INDEX:
        params[0] = Context.Current.Index;
        break;
      case GL_CURRENT_TEXTURE_COORDS:
        params[0] = Context.Current.TexCoord[0];
        params[1] = Context.Current.TexCoord[1];
        params[2] = Context.Current.TexCoord[2];
        params[3] = Context.Current.TexCoord[3];
        break;
      case GL_CURRENT_NORMAL:
        params[0] = Context.Current.Normal[0];
        params[1] = Context.Current.Normal[1];
        params[2] = Context.Current.Normal[2];
        break;
      case GL_CURRENT_RASTER_POSITION:
      case GL_CURRENT_RASTER_DISTANCE:
      case GL_CURRENT_RASTER_COLOR:
      case GL_CURRENT_RASTER_INDEX:
      case GL_CURRENT_RASTER_TEXTURE_COORDS:
        break;
      case GL_MODELVIEW_MATRIX:
        for (i = 0; i < 16; i++) {
          params[i] = Context.ModelViewMatrix[i];
        }
        break;
      case GL_PROJECTION_MATRIX:
        for (i = 0; i < 16; i++) {
          params[i] = Context.ProjectionMatrix[i];
        }
        break;
      case GL_TEXTURE_MATRIX:
        for (i = 0; i < 16; i++) {
          params[i] = Context.TextureMatrix[i];
        }
        break;
      case GL_DEPTH_RANGE:
        params[0] = Context.Viewport.Near;
        params[1] = Context.Viewport.Far;
        break;
      case GL_FOG_COLOR:
      case GL_FOG_INDEX:
      case GL_FOG_DENSITY:
      case GL_FOG_START:
      case GL_FOG_END:
      case GL_FOG_MODE:
        break;
      case GL_LIGHT_MODEL_AMBIENT:
        params[0] = Context.Lighting.LightModelAmbient[0];
        params[1] = Context.Lighting.LightModelAmbient[1];
        params[2] = Context.Lighting.LightModelAmbient[2];
        params[3] = Context.Lighting.LightModelAmbient[3];
        break;
      case GL_COLOR_INDEXES:
      case GL_POINT_SIZE:
      case GL_LINE_WIDTH:
        break;
      case GL_COLOR_CLEAR_VALUE:
        params[0] = Context.ColorBuffer.ClearColor[0];
        params[1] = Context.ColorBuffer.ClearColor[1];
        params[2] = Context.ColorBuffer.ClearColor[2];
        params[3] = Context.ColorBuffer.ClearColor[3];
        break;
      case GL_INDEX_CLEAR_VALUE:
      case GL_ACCUM_CLEAR_VALUE:
        break;
      case GL_RED_SCALE:
        params[0] = Context.Pixel.Red.Scale;
        break;
      case GL_GREEN_SCALE:
        params[0] = Context.Pixel.Green.Scale;
        break;
      case GL_BLUE_SCALE:
        params[0] = Context.Pixel.Blue.Scale;
        break;
      case GL_ALPHA_SCALE:
        params[0] = Context.Pixel.Alpha.Scale;
        break;
      case GL_DEPTH_SCALE:
        params[0] = Context.Pixel.Depth.Scale;
        break;
      case GL_RED_BIAS:
        params[0] = Context.Pixel.Red.Bias;
        break;
      case GL_GREEN_BIAS:
        params[0] = Context.Pixel.Green.Bias;
        break;
      case GL_BLUE_BIAS:
        params[0] = Context.Pixel.Blue.Bias;
        break;
      case GL_ALPHA_BIAS:
        params[0] = Context.Pixel.Alpha.Bias;
        break;
      case GL_DEPTH_BIAS:
        params[0] = Context.Pixel.Depth.Bias;
        break;
      case GL_ZOOM_X:
      case GL_ZOOM_Y:
      case GL_MAP1_GRID_DOMAIN:
      case GL_MAP2_GRID_DOMAIN:
      case GL_MAP1_GRID_SEGMENTS:
      case GL_MAP2_GRID_SEGMENTS:
      case GL_STEREO:
        break;
      case GL_POINT_SIZE_RANGE:
        params[0] = Context.MIN_POINT_SIZE;
        params[0] = Context.MAX_POINT_SIZE;
        break;
      case GL_POINT_SIZE_GRANULARITY:
        params[0] = Context.POINT_SIZE_GRANULARITY;
        break;
      case GL_LINE_WIDTH_RANGE:
        params[0] = Context.MIN_LINE_SIZE;
        params[0] = Context.MAX_LINE_SIZE;
        break;
      case GL_LINE_WIDTH_GRANULARITY:
        params[0] = Context.LINE_WIDTH_GRANULARITY;
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetFloatv(pname)");
    }
  }

  /** GLvoid glGetIntegerv (GLenum pname, GLint *params) */
  public void glGetIntegerv(int pname, int params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION,
          "glGetIntegerv - current mode is " + CC.Mode + ", expected " + None);
      return;
    }
    switch (pname) {
      case GL_CURRENT_COLOR:
        params[0] = gl_util.FtoI(Context.Current.Color[0]);
        params[1] = gl_util.FtoI(Context.Current.Color[1]);
        params[2] = gl_util.FtoI(Context.Current.Color[2]);
        params[3] = gl_util.FtoI(Context.Current.Color[3]);
        break;
      case GL_CURRENT_INDEX:
        params[0] = Context.Current.Index;
        break;
      case GL_CURRENT_RASTER_COLOR:
      case GL_CURRENT_RASTER_INDEX:
        break;
      case GL_VIEWPORT:
        params[0] = Context.Viewport.X;
        params[1] = Context.Viewport.Y;
        params[2] = Context.Viewport.Width;
        params[3] = Context.Viewport.Height;
        break;
      case GL_MODELVIEW_STACK_DEPTH:
        params[0] = Context.ModelViewStack.size();
        break;
      case GL_PROJECTION_STACK_DEPTH:
        params[0] = Context.ProjectionStack.size();
        break;
      case GL_TEXTURE_STACK_DEPTH:
        params[0] = Context.TextureStack.size();
        break;
      case GL_MATRIX_MODE:
        params[0] = Context.Transform.MatrixMode;
        break;
      case GL_FOG_MODE:
        break;
      case GL_SHADE_MODEL:
        params[0] = Context.Lighting.ShadeModel;
        break;
      case GL_COLOR_MATERIAL_PARAMETER:
        params[0] = Context.Lighting.ColorMaterialParameter;
        break;
      case GL_COLOR_MATERIAL_FACE:
        params[0] = Context.Lighting.ColorMaterialFace;
        break;
      case GL_LINE_STIPPLE_PATTERN:
      case GL_LINE_STIPPLE_REPEAT:
      case GL_CULL_FACE_MODE:
        params[0] = Context.Raster.CullFaceMode;
        break;
      case GL_FRONT_FACE:
        params[0] = Context.Raster.FrontFace;
        break;
      case GL_POLYGON_MODE:
        params[0] = Context.Raster.FrontMode;
        params[1] = Context.Raster.BackMode;
        break;
      case GL_STENCIL_FUNC:
        params[0] = Context.StencilBuffer.Func;
        break;
      case GL_STENCIL_VALUE_MASK:
        params[0] = Context.StencilBuffer.ValueMask;
        break;
      case GL_STENCIL_REF:
        params[0] = Context.StencilBuffer.Ref;
        break;
      case GL_STENCIL_FAIL:
        params[0] = Context.StencilBuffer.Fail;
        break;
      case GL_STENCIL_PASS_DEPTH_FAIL:
        params[0] = Context.StencilBuffer.ZFail;
        break;
      case GL_STENCIL_PASS_DEPTH_PASS:
        params[0] = Context.StencilBuffer.ZPass;
        break;
      case GL_ALPHA_TEST_FUNC:
      case GL_ALPHA_TEST_REF:
        break;
      case GL_DEPTH_FUNC:
        params[0] = Context.DepthBuffer.Func;
        break;
      case GL_BLEND_SRC:
        params[0] = Context.ColorBuffer.BlendSrc;
        break;
      case GL_BLEND_DST:
        params[0] = Context.ColorBuffer.BlendDst;
        break;
      case GL_LOGIC_OP_MODE:
      case GL_DRAW_BUFFER:
      case GL_INDEX_WRITEMASK:
        break;
      case GL_STENCIL_WRITEMASK:
        params[0] = Context.StencilBuffer.Mask;
        break;
      case GL_DEPTH_CLEAR_VALUE:
        params[0] = (int) Context.DepthBuffer.Clear;
        break;
      case GL_STENCIL_CLEAR_VALUE:
        params[0] = Context.StencilBuffer.Clear;
        break;
      case GL_UNPACK_IMAGE_HEIGHT:
        params[0] = Context.Pixel.Unpack.ImageHeight;
        break;
      case GL_UNPACK_ROW_LENGTH:
        params[0] = Context.Pixel.Unpack.RowLength;
        break;
      case GL_UNPACK_SKIP_IMAGES:
        params[0] = Context.Pixel.Unpack.SkipImages;
        break;
      case GL_UNPACK_SKIP_ROWS:
        params[0] = Context.Pixel.Unpack.SkipRows;
        break;
      case GL_UNPACK_SKIP_PIXELS:
        params[0] = Context.Pixel.Unpack.SkipPixels;
        break;
      case GL_UNPACK_ALIGNMENT:
        params[0] = Context.Pixel.Unpack.Alignment;
        break;
      case GL_PACK_IMAGE_HEIGHT:
        params[0] = Context.Pixel.Pack.ImageHeight;
        break;
      case GL_PACK_ROW_LENGTH:
        params[0] = Context.Pixel.Pack.RowLength;
        break;
      case GL_PACK_SKIP_IMAGES:
        params[0] = Context.Pixel.Pack.SkipImages;
        break;
      case GL_PACK_SKIP_ROWS:
        params[0] = Context.Pixel.Pack.SkipRows;
        break;
      case GL_PACK_SKIP_PIXELS:
        params[0] = Context.Pixel.Pack.SkipPixels;
        break;
      case GL_PACK_ALIGNMENT:
        params[0] = Context.Pixel.Pack.Alignment;
        break;
      case GL_INDEX_SHIFT:
        params[0] = Context.Pixel.IndexShift;
        break;
      case GL_INDEX_OFFSET:
        params[0] = Context.Pixel.IndexOffset;
        break;
      case GL_PIXEL_MAP_A_TO_A_SIZE:
      case GL_PIXEL_MAP_B_TO_B_SIZE:
      case GL_PIXEL_MAP_G_TO_G_SIZE:
      case GL_PIXEL_MAP_I_TO_A_SIZE:
      case GL_PIXEL_MAP_I_TO_B_SIZE:
      case GL_PIXEL_MAP_I_TO_G_SIZE:
      case GL_PIXEL_MAP_I_TO_I_SIZE:
      case GL_PIXEL_MAP_I_TO_R_SIZE:
      case GL_PIXEL_MAP_R_TO_R_SIZE:
      case GL_PIXEL_MAP_S_TO_S_SIZE:
      case GL_READ_BUFFER:
      case GL_PERSPECTIVE_CORRECTION_HINT:
      case GL_POINT_SMOOTH_HINT:
      case GL_LINE_SMOOTH_HINT:
      case GL_POLYGON_SMOOTH_HINT:
      case GL_FOG_HINT:
        break;
      case GL_MAX_LIGHTS:
        params[0] = Context.MAX_LIGHTS;
        break;
      case GL_MAX_CLIP_PLANES:
        params[0] = Context.MAX_CLIP_PLANES;
        break;
      case GL_MAX_MODELVIEW_STACK_DEPTH:
      case GL_MAX_PROJECTION_STACK_DEPTH:
      case GL_MAX_TEXTURE_STACK_DEPTH:
        params[0] = -1;
        break;
      case GL_SUBPIXEL_BITS:
        break;
      case GL_MAX_TEXTURE_SIZE:
        params[0] = Context.MAX_TEXTURE_SIZE;
        break;
      case GL_MAX_3D_TEXTURE_SIZE:
        params[0] = Context.MAX_3D_TEXTURE_SIZE;
        break;
      case GL_MAX_PIXEL_MAP_TABLE:
        break;
      case GL_MAX_NAME_STACK_DEPTH:
        params[0] = Context.MAX_NAME_STACK_DEPTH;
        break;
      case GL_MAX_LIST_NESTING:
        break;
      case GL_MAX_EVAL_ORDER:
        params[0] = Context.MAX_EVAL_ORDER;
        break;
      case GL_MAX_VIEWPORT_DIMS:
      case GL_MAX_ATTRIB_STACK_DEPTH:
      case GL_RED_BITS:
      case GL_GREEN_BITS:
      case GL_BLUE_BITS:
      case GL_ALPHA_BITS:
      case GL_INDEX_BITS:
      case GL_DEPTH_BITS:
      case GL_STENCIL_BITS:
      case GL_ACCUM_RED_BITS:
      case GL_ACCUM_GREEN_BITS:
      case GL_ACCUM_BLUE_BITS:
      case GL_ACCUM_ALPHA_BITS:
        break;
      case GL_LIST_BASE:
        params[0] = Context.ListBase;
        break;
      case GL_LIST_INDEX:
      case GL_LIST_MODE:
      case GL_ATTRIB_STACK_DEPTH:
        break;
      case GL_NAME_STACK_DEPTH:
        params[0] = Context.Select.NameStackDepth;
        break;
      case GL_RENDER_MODE:
        params[0] = Context.RenderMode;
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetIntegerv(pname)");
    }
  }

  /** GLvoid glPushAttrib (GLbitfield mask) */
  public void glPushAttrib(int mask) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPushAttrib");
      return;
    }
    Context.gl_push_attrib(mask);
  }

  /** GLvoid glPopAttrib (GLvoid) */
  public void glPopAttrib() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPopAttrib");
      return;
    }
    Context.gl_pop_attrib();
  }

  /** GLint glRenderMode (GLenum mode) */
  public int glRenderMode(int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRenderMode");
      return -1;
    }

    switch (mode) {
      case GL_RENDER:
      case GL_SELECT:
      case GL_FEEDBACK:
        return CC.gl_render_mode(mode);
      default:
        CC.gl_error(GL_INVALID_ENUM, "glRenderMode(mode)");
    }

    return -1;
  }

  /** const GLubyte *glGetString (GLenum name) */
  public String glGetString(int name) {
    String vendor = new String("Robin Bing-Yu Chen, Martin Pernollet");
    String renderer = new String("jGL");
    String version = new String("2.5");
    String extensions = new String("no supported extensions now");
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetString");
      return null;
    }
    switch (name) {
      case GL_VENDOR:
        return vendor;
      case GL_RENDERER:
        return renderer;
      case GL_VERSION:
        return version;
      case GL_EXTENSIONS:
        return extensions;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetString(name)");
        return null;
    }
  }

  /**
   * Depth Buffer
   */

  /**
   * Sets the window <i>z</i> value. The <i>depth</i> is a floating point value that is clamped to
   * the range [0,1] and converted to fixed-point.
   * 
   * @param depth the window <i>z</i> value.
   * @see #glClear(int)
   */
  /* GLvoid glClearDepth (GLclampd depth) */
  public void glClearDepth(double depth) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glClearDepth");
      return;
    }
    CC.gl_clear_depth(gl_util.CLAMP(depth, 0.0, 1.0));
  }

  /** GLvoid glDepthFunc (GLenum func) */
  public void glDepthFunc(int func) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDepthFunc");
      return;
    }
    switch (func) {
      case GL_NEVER:
      case GL_LESS:
      case GL_GEQUAL:
      case GL_LEQUAL:
      case GL_GREATER:
      case GL_NOTEQUAL:
      case GL_EQUAL:
      case GL_ALWAYS:
        CC.gl_depth_func(func);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glDepthFunc(func)");
    }
  }

  /** GLvoid glDepthMask (GLboolean flag) */
  public void glDepthMask(boolean flag) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDepthMask");
      return;
    }
    CC.gl_depth_mask(flag);
  }

  /** GLvoid glDepthRange (GLclampd near_val, GLclampd far_val) */
  public void glDepthRange(double near_val, double far_val) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDepthRange");
      return;
    }
    CC.gl_depth_range(gl_util.CLAMP(near_val, 0.0, 1.0), gl_util.CLAMP(far_val, 0.0, 1.0));
  }

  /**
   * Transformation
   */

  /** GLvoid glMatrixMode (GLenum mode) */
  public void glMatrixMode(int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMatrixMode");
      return;
    }
    switch (mode) {
      case GL_MODELVIEW:
      case GL_PROJECTION:
      case GL_TEXTURE:
        CC.gl_matrix_mode(mode);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glMatrixMode(mode)");
    }
  }

  /**
   * GLvoid glOrtho (GLdouble left, GLdouble right, GLdouble bottom, GLdouble top, GLdouble
   * near_val, GLdouble far_val)
   */
  public void glOrtho(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    float m[] = new float[16];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glOrtho");
      return;
    }
    m[0] = (float) (2.0 / (right - left));
    m[5] = (float) (2.0 / (top - bottom));
    m[10] = (float) (-2.0 / (far_val - near_val));
    m[12] = (float) (-(right + left) / (right - left));
    m[13] = (float) (-(top + bottom) / (top - bottom));
    m[14] = (float) (-(far_val + near_val) / (far_val - near_val));
    m[15] = 1;
    CC.gl_mult_matrix(m);
  }

  /**
   * GLvoid glFrustum (GLdouble left, GLdouble right, GLdouble bottom, GLdouble top, GLdouble
   * near_val, GLdouble far_val)
   */
  public void glFrustum(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    float m[] = new float[16];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glFrustum");
      return;
    }
    if (near_val <= 0 || far_val <= 0) {
      CC.gl_error(GL_INVALID_VALUE, "glFrustum(near or far)");
    }
    m[0] = (float) ((2.0 * near_val) / (right - left));
    m[5] = (float) ((2.0 * near_val) / (top - bottom));
    m[8] = (float) ((right + left) / (right - left));
    m[9] = (float) ((top + bottom) / (top - bottom));
    m[10] = (float) (-(far_val + near_val) / (far_val - near_val));
    m[11] = -1;
    m[14] = (float) (-(2.0 * far_val * near_val) / (far_val - near_val));
    CC.gl_mult_matrix(m);
  }

  /** GLvoid glViewport (GLint x, GLint y, GLsizei width, GLsizei height); */
  public void glViewport(int x, int y, int width, int height) {
    if (width < 0 || height < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glViewport(width=" + width + ", height=" + height + ")");
      return;
    }
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION,
          "glViewport - current mode is " + CC.Mode + ", expected " + None);
      return;
    }
    if (width < 1) {
      width = 1;
    }
    if (height < 1) {
      height = 1;
    }

    desiredX = x;
    desiredY = y;
    desiredWidth = width;
    desiredHeight = height;

    applyViewport();
  }

  /**
   * Apply viewport according to the latest known expected width/height and the latest known pixel
   * scales.
   */
  public void applyViewport() {
    if (autoAdaptToHiDPI) {
      actualWidth = (int) (desiredWidth * pixelScaleX);
      actualHeight = (int) (desiredHeight * pixelScaleY);
    } else {
      actualWidth = desiredWidth;
      actualHeight = desiredHeight;
    }
    CC.gl_viewport(desiredX, desiredY, actualWidth, actualHeight);
  }

  /** GLvoid glPushMatrix (GLvoid) */
  public void glPushMatrix() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION,
          "glPushMatrix - current mode is " + CC.Mode + ", expected " + None);
      return;
    }
    CC.gl_push_matrix();
  }

  /** GLvoid glPopMatrix (GLvoid) */
  public void glPopMatrix() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION,
          "glPopMatrix - current mode is " + CC.Mode + ", expected " + None);
      return;
    }
    CC.gl_pop_matrix();
  }

  /** GLvoid glLoadIdentity (GLvoid) */
  public void glLoadIdentity() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLoadIdentity");
      return;
    }
    CC.gl_load_identity_matrix();
  }

  /** GLvoid glLoadMatrixd (const GLdouble *m) */
  public void glLoadMatrixd(double m[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLoadMatrixd");
      return;
    }
    float temp[] = {(float) m[0], (float) m[1], (float) m[2], (float) m[3], (float) m[4],
        (float) m[5], (float) m[6], (float) m[7], (float) m[8], (float) m[9], (float) m[10],
        (float) m[11], (float) m[12], (float) m[13], (float) m[14], (float) m[15]};
    CC.gl_load_matrix(temp);
  }

  /** GLvoid glLoadMatrixf (const GLfloat *m) */
  public void glLoadMatrixf(float m[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLoadMatrixf");
      return;
    }
    CC.gl_load_matrix(m);
  }

  /** GLvoid glMultMatrixd (const GLdouble *m) */
  public void glMultMatrixd(double m[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMultMatrixd");
      return;
    }
    float temp[] = {(float) m[0], (float) m[1], (float) m[2], (float) m[3], (float) m[4],
        (float) m[5], (float) m[6], (float) m[7], (float) m[8], (float) m[9], (float) m[10],
        (float) m[11], (float) m[12], (float) m[13], (float) m[14], (float) m[15]};
    CC.gl_mult_matrix(temp);
  }

  /** GLvoid glMultMatrixf (const GLfloat *m) */
  public void glMultMatrixf(float m[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMultMatrixf");
      return;
    }
    CC.gl_mult_matrix(m);
  }

  /** GLvoid glRotated (GLdouble angle, GLdouble x, GLdouble y, GLdouble z) */
  public void glRotated(double angle, double x, double y, double z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRotated");
      return;
    }
    CC.gl_rotate((float) angle, (float) x, (float) y, (float) z);
  }

  /** GLvoid glRotatef (GLfloat angle, GLfloat x, GLfloat y, GLfloat z) */
  public void glRotatef(float angle, float x, float y, float z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRotatef");
      return;
    }
    CC.gl_rotate(angle, x, y, z);
  }

  /** GLvoid glScaled (GLdouble x, GLdouble y, GLdouble z) */
  public void glScaled(double x, double y, double z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glScaled");
      return;
    }
    CC.gl_scale((float) x, (float) y, (float) z);
  }

  /** GLvoid glScalef (GLfloat x, GLfloat y, GLfloat z) */
  public void glScalef(float x, float y, float z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glScalef");
      return;
    }
    CC.gl_scale(x, y, z);
  }

  /** GLvoid glTranslated (GLdouble x, GLdouble y, GLdouble z) */
  public void glTranslated(double x, double y, double z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTranslated");
      return;
    }
    CC.gl_translate((float) x, (float) y, (float) z);
  }

  /** GLvoid glTranslatef (GLfloat x, GLfloat y, GLfloat z) */
  public void glTranslatef(float x, float y, float z) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTranslatef");
      return;
    }
    CC.gl_translate(x, y, z);
  }

  /**
   * Display Lists
   */

  /** GLboolean glIsList (GLuint list) */
  public boolean glIsList(int list) {
    return Context.gl_is_list(list);
  }

  /** GLvoid glDeleteLists (GLuint list, GLsizei range) */
  public void glDeleteLists(int list, int range) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDeleteLists");
      return;
    }
    if (range < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glDeleteLists(range)");
      return;
    }
    Context.gl_delete_lists(list, range);
  }

  /** GLuint glGenLists (GLsizei range) */
  public int glGenLists(int range) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGenLists");
      return 0;
    }
    if (range < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glGenLists(range)");
      return 0;
    }
    return Context.gl_gen_lists(range);
  }

  /** GLvoid glNewList (GLuint list, GLenum mode) */
  public void glNewList(int list, int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNewList");
      return;
    }
    if (list == 0) {
      CC.gl_error(GL_INVALID_VALUE, "glNewList(list)");
      return;
    }
    if (mode != GL_COMPILE && mode != GL_COMPILE_AND_EXECUTE) {
      CC.gl_error(GL_INVALID_ENUM, "glNewList(mode)");
      return;
    }
    if (Context.ListIndex != 0) {
      /* already compiling a display list */
      CC.gl_error(GL_INVALID_OPERATION, "glNewList");
      return;
    }
    Context.gl_new_list(list);
    if (mode == GL_COMPILE) {
      // CC = new gl_list ();
      CC = new gl_list(Context, false);
    } else { // GL_COMPILE_AND_EXECUTE
      CC = new gl_list(Context, true);
    }
  }

  /** GLvoid glEndList (GLvoid) */
  public void glEndList() {
    /* Check that a list is under construction */
    if (Context.ListIndex == 0) {
      CC.gl_error(GL_INVALID_OPERATION, "glEndList");
      return;
    }
    Context.gl_end_list((gl_list) CC);
    CC = Context;
  }

  /** GLvoid glCallList (GLuint list) */
  public void glCallList(int list) {
    if (list <= 0) {
      CC.gl_error(GL_INVALID_VALUE, "glCallList(list)");
      return;
    }
    CC.gl_call_list(list);
  }

  /** GLvoid glCallLists (sizei n, GLenum type, const GLvoid *lists) */
  public void glCallLists(int n, int type, byte lists[]) {
    int i, list;

    for (i = 0; i < n; i++) {
      CC.gl_call_offset(translate_id(i, type, lists));
    }
  }

  /** GLvoid glListBase (GLuint base) */
  public void glListBase(int base) {
    CC.gl_list_base(base);
  }

  /**
   * Drawing Functions
   */

  /** GLvoid glBegin (GLenum mode) */
  public void glBegin(int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glBegin can not be called as a geometry is already in progress : " + CC.Mode);
      return;
    }
    switch (mode) {
      case GL_POINTS:
      case GL_LINES:
      case GL_LINE_STRIP:
      case GL_LINE_LOOP:
      case GL_TRIANGLES:
      case GL_TRIANGLE_STRIP:
      case GL_TRIANGLE_FAN:
      case GL_QUADS:
      case GL_QUAD_STRIP:
      case GL_POLYGON:
        CC.gl_begin(mode);
        break;
      default:
        CC.gl_error(GL.GL_INVALID_ENUM, "glBegin(mode)");
    }
  }
  
  static final String NEED_GL_BEGIN = " is called while no geometry was initialized with glBegin(geom).";

  /** GLvoid glEnd (GLvoid) */
  public void glEnd() {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glEnd" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_end();
  }

  /** GLvoid glVertex2d (GLdouble x, GLdouble y) */
  public void glVertex2d(double x, double y) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2d" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) x, (float) y, 0.0f, 1.0f);
  }

  /** GLvoid glVertex2f (GLfloat x, GLfloat y) */
  public void glVertex2f(float x, float y) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2f" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, 0.0f, 1.0f);
  }

  /** GLvoid glVertex2i (GLint x, GLint y) */
  public void glVertex2i(int x, int y) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2i" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, 0.0f, 1.0f);
  }

  /** GLvoid glVertex2s (GLshort x, GLshort y) */
  public void glVertex2s(short x, short y) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2s" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, 0.0f, 1.0f);
  }

  /** GLvoid glVertex3d (GLdouble x, GLdouble y, GLdouble z) */
  public void glVertex3d(double x, double y, double z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3d" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) x, (float) y, (float) z, 1.0f);
  }

  /** GLvoid glVertex3f (GLfloat x, GLfloat y, GLfloat z) */
  public void glVertex3f(float x, float y, float z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3f" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, 1.0f);
  }

  /** GLvoid glVertex3i (GLint x, GLint y, GLint z) */
  public void glVertex3i(int x, int y, int z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3i" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, 1.0f);
  }

  /** GLvoid glVertex3s (GLshort x, GLshort y, GLshort z) */
  public void glVertex3s(short x, short y, short z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3s" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, 1.0f);
  }

  /** GLvoid glVertex4d (GLdouble x, GLdouble y, GLdouble z, GLdouble w) */
  public void glVertex4d(double x, double y, double z, double w) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4d" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) x, (float) y, (float) z, (float) w);
  }

  /** GLvoid glVertex4f (GLfloat x, GLfloat y, GLfloat z, GLfloat w) */
  public void glVertex4f(float x, float y, float z, float w) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4f" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, w);
  }

  /** GLvoid glVertex4i (GLint x, GLint y, GLint z, GLint w) */
  public void glVertex4i(int x, int y, int z, int w) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4i" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, w);
  }

  /** GLvoid glVertex4s (GLshort x, GLshort y, GLshort z, GLshort w) */
  public void glVertex4s(short x, short y, short z, short w) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4s" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(x, y, z, w);
  }

  /** GLvoid glVertex2dv (GLdouble *v) */
  public void glVertex2dv(double v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2dv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) v[0], (float) v[1], 0.0f, 1.0f);
  }

  /** GLvoid glVertex2fv (GLfloat *v) */
  public void glVertex2fv(float v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2fv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glVertex2iv (GLint *v) */
  public void glVertex2iv(int v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2iv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glVertex2sv (GLshort *v) */
  public void glVertex2sv(short v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex2sv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glVertex3dv (GLdouble *v) */
  public void glVertex3dv(double v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3dv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) v[0], (float) v[1], (float) v[2], 1.0f);
  }

  /** GLvoid glVertex3fv (GLfloat *v) */
  public void glVertex3fv(float v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3fv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glVertex3iv (GLint *v) */
  public void glVertex3iv(int v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3iv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glVertex3sv (GLshort *v) */
  public void glVertex3sv(short v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex3sv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glVertex4dv (GLdouble *v) */
  public void glVertex4dv(double v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4dv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex((float) v[0], (float) v[1], (float) v[2], (float) v[3]);
  }

  /** GLvoid glVertex4fv (GLfloat *v) */
  public void glVertex4fv(float v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4fv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glVertex4iv (GLint *v) */
  public void glVertex4iv(int v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4iv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glVertex4sv (GLshort *v) */
  public void glVertex4sv(short v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glVertex4sv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_vertex(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glNormal3b (GLbyte x, GLbyte y, GLbyte z) */
  public void glNormal3b(byte x, byte y, byte z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3b" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(x, y, z);
  }

  /** GLvoid glNormal3d (GLdouble x, GLdouble y, GLdouble z) */
  public void glNormal3d(double x, double y, double z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3d" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal((float) x, (float) y, (float) z);
  }

  /** GLvoid glNormal3f (GLfloat x, GLfloat y, GLfloat z) */
  public void glNormal3f(float x, float y, float z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3f" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(x, y, z);
  }

  /** GLvoid glNormal3i (GLint x, GLint y, GLint z) */
  public void glNormal3i(int x, int y, int z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3i" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(x, y, z);
  }

  /** GLvoid glNormal3s (GLshort x, GLshort y, GLshort z) */
  public void glNormal3s(short x, short y, short z) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3s" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(x, y, z);
  }

  /** GLvoid glNormal3bv (GLbyte *v) */
  public void glNormal3bv(byte v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3bv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(v[0], v[1], v[2]);
  }

  /** GLvoid glNormal3dv (GLdouble *v) */
  public void glNormal3dv(double v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3dv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal((float) v[0], (float) v[1], (float) v[2]);
  }

  /** GLvoid glNormal3fv (GLfloat *v) */
  public void glNormal3fv(float v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3fv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(v[0], v[1], v[2]);
  }

  /** GLvoid glNormal3iv (GLint *v) */
  public void glNormal3iv(int v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3iv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(v[0], v[1], v[2]);
  }

  /** GLvoid glNormal3sv (GLshort *v) */
  public void glNormal3sv(short v[]) {
    if (CC.Mode == None) {
      CC.gl_error(GL_INVALID_OPERATION, "glNormal3sv" + NEED_GL_BEGIN);
      return;
    }
    CC.gl_normal(v[0], v[1], v[2]);
  }

  /** GLvoid glIndexd (GLdouble c) */
  public void glIndexd(double c) {
    CC.gl_index((int) c);
  }

  /** GLvoid glIndexf (GLfloat c) */
  public void glIndexf(float c) {
    CC.gl_index((int) c);
  }

  /** GLvoid glIndexi (GLint c) */
  public void glIndexi(int c) {
    CC.gl_index(c);
  }

  /** GLvoid glIndexs (GLshort c) */
  public void glIndexs(short c) {
    CC.gl_index(c & 0xffff);
  }

  /** GLvoid glIndexub (GLubyte c) */
  public void glIndexub(byte c) {
    CC.gl_index(c & 0xff);
  }

  /** GLvoid glIndexdv (GLdouble *c) */
  public void glIndexdv(double c[]) {
    CC.gl_index((int) c[0]);
  }

  /** GLvoid glIndexfv (GLfloat *c) */
  public void glIndexfv(float c[]) {
    CC.gl_index((int) c[0]);
  }

  /** GLvoid glIndexiv (GLint *c) */
  public void glIndexiv(int c[]) {
    CC.gl_index(c[0]);
  }

  /** GLvoid glIndexsv (GLshort *c) */
  public void glIndexsv(short c[]) {
    CC.gl_index(c[0] & 0xffff);
  }

  /** GLvoid glIndexubv (GLubyte *c) */
  public void glIndexubv(byte c[]) {
    CC.gl_index(c[0] & 0xff);
  }

  /** GLvoid glColor3b (GLbyte red, GLbyte green, GLbyte blue) */
  public void glColor3b(byte red, byte green, byte blue) {
    CC.gl_color(gl_util.BtoF(red), gl_util.BtoF(green), gl_util.BtoF(blue), 1.0f);
  }

  /** GLvoid glColor3d (GLdouble red, GLdouble green, GLdouble blue) */
  public void glColor3d(double red, double green, double blue) {
    CC.gl_color(gl_util.CLAMP(red, 0.0, 1.0), gl_util.CLAMP(green, 0.0, 1.0),
        gl_util.CLAMP(blue, 0.0, 1.0), 1.0f);
  }

  /** GLvoid glColor3f (GLfloat red, GLfloat green, GLfloat blue) */
  public void glColor3f(float red, float green, float blue) {
    CC.gl_color(gl_util.CLAMP(red, 0.0, 1.0), gl_util.CLAMP(green, 0.0, 1.0),
        gl_util.CLAMP(blue, 0.0, 1.0), 1.0f);
  }

  /** GLvoid glColor3i (GLint red, GLint green, GLint blue) */
  public void glColor3i(int red, int green, int blue) {
    CC.gl_color(gl_util.ItoF(gl_util.CLAMP(red, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(green, 0, 255)), gl_util.ItoF(gl_util.CLAMP(blue, 0, 255)),
        1.0f);
  }

  /** GLvoid glColor3s (GLshort red, GLshort green, GLshort blue) */
  public void glColor3s(short red, short green, short blue) {
    CC.gl_color(gl_util.ItoF(gl_util.CLAMP(red, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(green, 0, 255)), gl_util.ItoF(gl_util.CLAMP(blue, 0, 255)),
        1.0f);
  }

  /** GLvoid glColor3ub (GLubyte red, GLubyte green, GLubyte blue) */
  public void glColor3ub(byte red, byte green, byte blue) {
    glColor3b(red, green, blue);
  }

  /** GLvoid glColor3ui (GLuint red, GLuint green, GLuint blue) */
  public void glColor3ui(int red, int green, int blue) {
    glColor3i(red, green, blue);
  }

  /** GLvoid glColor3us (GLushort red, GLushort green, GLushort blue) */
  public void glColor3us(short red, short green, short blue) {
    glColor3s(red, green, blue);
  }

  /** GLvoid glColor4b (GLbyte red, GLbyte green, GLbyte blue, GLbyte alpha) */
  public void glColor4b(byte red, byte green, byte blue, byte alpha) {
    CC.gl_color(gl_util.BtoF(red), gl_util.BtoF(green), gl_util.BtoF(blue), gl_util.BtoF(alpha));
  }

  /**
   * GLvoid glColor4d (GLdouble red, GLdouble green, GLdouble blue, GLdouble alpha)
   */
  public void glColor4d(double red, double green, double blue, double alpha) {
    CC.gl_color(gl_util.CLAMP(red, 0.0, 1.0), gl_util.CLAMP(green, 0.0, 1.0),
        gl_util.CLAMP(blue, 0.0, 1.0), gl_util.CLAMP(alpha, 0.0, 1.0));
  }

  /**
   * GLvoid glColor4f (GLfloat red, GLfloat green, GLfloat blue, GLfloat alpha)
   */
  public void glColor4f(float red, float green, float blue, float alpha) {
    CC.gl_color(gl_util.CLAMP(red, 0.0, 1.0), gl_util.CLAMP(green, 0.0, 1.0),
        gl_util.CLAMP(blue, 0.0, 1.0), gl_util.CLAMP(alpha, 0.0, 1.0));
  }

  /** GLvoid glColor4i (GLint red, GLint green, GLint blue, GLint alpha) */
  public void glColor4i(int red, int green, int blue, int alpha) {
    CC.gl_color(gl_util.ItoF(gl_util.CLAMP(red, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(green, 0, 255)), gl_util.ItoF(gl_util.CLAMP(blue, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(alpha, 0, 255)));
  }

  /**
   * GLvoid glColor4s (GLshort red, GLshort green, GLshort blue, GLshort alpha)
   */
  public void glColor4s(short red, short green, short blue, short alpha) {
    CC.gl_color(gl_util.ItoF(gl_util.CLAMP(red, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(green, 0, 255)), gl_util.ItoF(gl_util.CLAMP(blue, 0, 255)),
        gl_util.ItoF(gl_util.CLAMP(alpha, 0, 255)));
  }

  /**
   * GLvoid glColor4ub (GLubyte red, GLubyte green, GLubyte blue, GLubyte alpha)
   */
  public void glColor4ub(byte red, byte green, byte blue, byte alpha) {
    glColor4b(red, green, blue, alpha);
  }

  /** GLvoid glColor4ui (GLuint red, GLuint green, GLuint blue, GLuint alpha) */
  public void glColor4ui(int red, int green, int blue, int alpha) {
    glColor4i(red, green, blue, alpha);
  }

  /**
   * GLvoid glColor4us (GLushort red, GLushort green, GLushort blue, GLushort alpha)
   */
  public void glColor4us(short red, short green, short blue, short alpha) {
    glColor4s(red, green, blue, alpha);
  }

  /** GLvoid glColor3bv (GLbyte *v) */
  public void glColor3bv(byte v[]) {
    glColor3b(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3dv (GLdouble *v) */
  public void glColor3dv(double v[]) {
    glColor3d(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3fv (GLfloat *v) */
  public void glColor3fv(float v[]) {
    glColor3f(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3iv (GLint *v) */
  public void glColor3iv(int v[]) {
    glColor3i(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3sv (GLshort *v) */
  public void glColor3sv(short v[]) {
    glColor3s(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3ubv (GLubyte *v) */
  public void glColor3ubv(byte v[]) {
    glColor3b(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3uiv (GLuint *v) */
  public void glColor3uiv(int v[]) {
    glColor3i(v[0], v[1], v[2]);
  }

  /** GLvoid glColor3usv (GLushort *v) */
  public void glColor3usv(short v[]) {
    glColor3s(v[0], v[1], v[2]);
  }

  /** GLvoid glColor4bv (GLbyte *v) */
  public void glColor4bv(byte v[]) {
    glColor4b(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4dv (GLdouble *v) */
  public void glColor4dv(double v[]) {
    glColor4d(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4fv (GLfloat *v) */
  public void glColor4fv(float v[]) {
    glColor4f(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4iv (GLint *v) */
  public void glColor4iv(int v[]) {
    glColor4i(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4sv (GLshort *v) */
  public void glColor4sv(short v[]) {
    glColor4s(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4ubv (GLubyte *v) */
  public void glColor4ubv(byte v[]) {
    glColor4b(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4uiv (GLuint *v) */
  public void glColor4uiv(int v[]) {
    glColor4i(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glColor4usv (GLushort *v) */
  public void glColor4usv(short v[]) {
    glColor4s(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glTexCoord1d (GLdouble s) */
  public void glTexCoord1d(double s) {
    CC.gl_tex_coord((float) s, 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord1f (GLfloat s) */
  public void glTexCoord1f(float s) {
    CC.gl_tex_coord(s, 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord1i (GLint s) */
  public void glTexCoord1i(int s) {
    CC.gl_tex_coord(s, 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord1s (GLshort s) */
  public void glTexCoord1s(short s) {
    CC.gl_tex_coord(s, 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2d (GLdouble s, GLdouble t) */
  public void glTexCoord2d(double s, double t) {
    CC.gl_tex_coord((float) s, (float) t, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2f (GLfloat s, GLfloat t) */
  public void glTexCoord2f(float s, float t) {
    CC.gl_tex_coord(s, t, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2i (GLint s, GLint t) */
  public void glTexCoord2i(int s, int t) {
    CC.gl_tex_coord(s, t, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2s (GLshort s, GLshort t) */
  public void glTexCoord2s(short s, short t) {
    CC.gl_tex_coord(s, t, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord3d (GLdouble s, GLdouble t, GLdouble r) */
  public void glTexCoord3d(double s, double t, double r) {
    CC.gl_tex_coord((float) s, (float) t, (float) r, 1.0f);
  }

  /** GLvoid glTexCoord3f (GLfloat s, GLfloat t, GLfloat r) */
  public void glTexCoord3f(float s, float t, float r) {
    CC.gl_tex_coord(s, t, r, 1.0f);
  }

  /** GLvoid glTexCoord3i (GLint s, GLint t, GLint r) */
  public void glTexCoord3i(int s, int t, int r) {
    CC.gl_tex_coord(s, t, r, 1.0f);
  }

  /** GLvoid glTexCoord3s (GLshort s, GLshort t, GLshort r) */
  public void glTexCoord3s(short s, short t, short r) {
    CC.gl_tex_coord(s, t, r, 1.0f);
  }

  /** GLvoid glTexCoord4d (GLdouble s, GLdouble t, GLdouble r, GLdouble q) */
  public void glTexCoord4d(double s, double t, double r, double q) {
    CC.gl_tex_coord((float) s, (float) t, (float) r, (float) q);
  }

  /** GLvoid glTexCoord4f (GLfloat s, GLfloat t, GLfloat r, GLfloat q) */
  public void glTexCoord4f(float s, float t, float r, float q) {
    CC.gl_tex_coord(s, t, r, q);
  }

  /** GLvoid glTexCoord4i (GLint s, GLint t, GLint r, GLint q ) */
  public void glTexCoord4i(int s, int t, int r, int q) {
    CC.gl_tex_coord(s, t, r, q);
  }

  /** GLvoid glTexCoord4s (GLshort s, GLshort t, GLshort r, GLshort q) */
  public void glTexCoord4s(short s, short t, short r, short q) {
    CC.gl_tex_coord(s, t, r, q);
  }

  /** GLvoid glTexCoord1dv (const GLdouble *v) */
  public void glTexCoord1dv(double v[]) {
    CC.gl_tex_coord((float) v[0], (float) 0.0, (float) 0.0, (float) 1.0);
  }

  /** GLvoid glTexCoord1fv (const GLfloat *v) */
  public void glTexCoord1fv(float v[]) {
    CC.gl_tex_coord(v[0], 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord1iv (const GLint *v) */
  public void glTexCoord1iv(int v[]) {
    CC.gl_tex_coord(v[0], 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord1sv (const GLshort *v) */
  public void glTexCoord1sv(short v[]) {
    CC.gl_tex_coord(v[0], 0.0f, 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2dv (const GLdouble *v) */
  public void glTexCoord2dv(double v[]) {
    CC.gl_tex_coord((float) v[0], (float) v[1], 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2fv (const GLfloat *v) */
  public void glTexCoord2fv(float v[]) {
    CC.gl_tex_coord(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2iv (const GLint *v) */
  public void glTexCoord2iv(int v[]) {
    CC.gl_tex_coord(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord2sv (const GLshort *v) */
  public void glTexCoord2sv(short v[]) {
    CC.gl_tex_coord(v[0], v[1], 0.0f, 1.0f);
  }

  /** GLvoid glTexCoord3dv (const GLdouble *v) */
  public void glTexCoord3dv(double v[]) {
    CC.gl_tex_coord((float) v[0], (float) v[1], (float) v[2], 1.0f);
  }

  /** GLvoid glTexCoord3fv (const GLfloat *v) */
  public void glTexCoord3fv(float v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glTexCoord3iv (const GLint *v) */
  public void glTexCoord3iv(int v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glTexCoord3sv (const GLshort *v) */
  public void glTexCoord3sv(short v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], 1.0f);
  }

  /** GLvoid glTexCoord4dv (const GLdouble *v) */
  public void glTexCoord4dv(double v[]) {
    CC.gl_tex_coord((float) v[0], (float) v[1], (float) v[2], (float) v[3]);
  }

  /** GLvoid glTexCoord4fv (const GLfloat *v) */
  public void glTexCoord4fv(float v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glTexCoord4iv (const GLint *v) */
  public void glTexCoord4iv(int v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glTexCoord4sv (const GLshort *v) */
  public void glTexCoord4sv(short v[]) {
    CC.gl_tex_coord(v[0], v[1], v[2], v[3]);
  }

  /** GLvoid glRasterPos2d (GLdouble x, GLdouble y) */
  public void glRasterPos2d(double x, double y) {
    CC.gl_raster_pos((float) x, (float) y, 0.0f, 1.0f);
  }

  /** GLvoid glRasterPos2f (GLfloat x, GLfloat y) */
  public void glRasterPos2f(float x, float y) {
    CC.gl_raster_pos(x, y, 0.0f, 1.0f);
  }

  /** GLvoid glRasterPos2i (GLint x, GLint y) */
  public void glRasterPos2i(int x, int y) {
    CC.gl_raster_pos(x, y, 0.0f, 1.0f);
  }

  /** GLvoid glRasterPos2s (GLshort x, GLshort y) */

  /** GLvoid glRectd (GLdouble x1, GLdouble y1, GLdouble x2, GLdouble y2) */
  public void glRectd(double x1, double y1, double x2, double y2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectd");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2d(x1, y1);
    glVertex2d(x2, y1);
    glVertex2d(x2, y2);
    glVertex2d(x1, y2);
    glEnd();
  }

  /** GLvoid glRectf (GLfloat x1, GLfloat y1, GLfloat x2, GLfloat y2) */
  public void glRectf(float x1, float y1, float x2, float y2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectf");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2f(x1, y1);
    glVertex2f(x2, y1);
    glVertex2f(x2, y2);
    glVertex2f(x1, y2);
    glEnd();
  }

  /** GLvoid glRecti (GLint x1, GLint y1, GLint x2, GLint y2) */
  public void glRecti(int x1, int y1, int x2, int y2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRecti");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2i(x1, y1);
    glVertex2i(x2, y1);
    glVertex2i(x2, y2);
    glVertex2i(x1, y2);
    glEnd();
  }

  /** GLvoid glRects (GLshort x1, GLshort y1, GLshort x2, GLshort y2) */
  public void glRects(short x1, short y1, short x2, short y2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRects");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2s(x1, y1);
    glVertex2s(x2, y1);
    glVertex2s(x2, y2);
    glVertex2s(x1, y2);
    glEnd();
  }

  /** GLvoid glRectdv (GLdouble *v1, GLdouble *v2) */
  public void glRectdv(double v1[], double v2[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectdv");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2d(v1[0], v1[1]);
    glVertex2d(v2[0], v1[1]);
    glVertex2d(v2[0], v2[1]);
    glVertex2d(v1[0], v2[1]);
    glEnd();
  }

  /** GLvoid glRectfv (GLfloat *v1, GLfloat *v2) */
  public void glRectfv(float v1[], float v2[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectfv");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2f(v1[0], v1[1]);
    glVertex2f(v2[0], v1[1]);
    glVertex2f(v2[0], v2[1]);
    glVertex2f(v1[0], v2[1]);
    glEnd();
  }

  /** GLvoid glRectiv (GLint *v1, GLint *v2) */
  public void glRectiv(int v1[], int v2[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectiv");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2i(v1[0], v1[1]);
    glVertex2i(v2[0], v1[1]);
    glVertex2i(v2[0], v2[1]);
    glVertex2i(v1[0], v2[1]);
    glEnd();
  }

  /** GLvoid glRectsv (GLshort *v1, GLshort *v2) */
  public void glRectsv(short v1[], short v2[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glRectsv");
      return;
    }
    glBegin(GL_QUADS);
    glVertex2s(v1[0], v1[1]);
    glVertex2s(v2[0], v1[1]);
    glVertex2s(v2[0], v2[1]);
    glVertex2s(v1[0], v2[1]);
    glEnd();
  }

  /**
   * Lighting
   */

  /** GLvoid glLightf (GLenum light, GLenum pname, GLfloat param) */
  public void glLightf(int light, int pname, float param) {
    float params[] = new float[1];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLightf");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glLightf(light)");
      return;
    }
    switch (pname) {
      case GL_SPOT_EXPONENT:
        if (param < 0 || param > 128) {
          CC.gl_error(GL_INVALID_VALUE, "glLightf(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_SPOT_CUTOFF:
        if ((param < 0 || param > 90) && param != 180) {
          CC.gl_error(GL_INVALID_VALUE, "glLightf(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_CONSTANT_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightf(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_LINEAR_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightf(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_QUADRATIC_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightf(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightf(pname)");
    }
  }

  /** GLvoid glLighti (GLenum light, GLenum pname, GLint param) */
  public void glLighti(int light, int pname, int param) {
    float params[] = new float[1];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLighti");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glLighti(light)");
      return;
    }
    switch (pname) {
      case GL_SPOT_EXPONENT:
        if (param < 0 || param > 128) {
          CC.gl_error(GL_INVALID_VALUE, "glLighti(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_SPOT_CUTOFF:
        if ((param < 0 || param > 90) && param != 180) {
          CC.gl_error(GL_INVALID_VALUE, "glLighti(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_CONSTANT_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLighti(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_LINEAR_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLighti(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      case GL_QUADRATIC_ATTENUATION:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLighti(param)");
          return;
        }
        params[0] = param;
        CC.gl_light(light - GL_LIGHT0, pname, params);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLighti(pname)");
    }
  }

  /** GLvoid glLightfv (GLenum light, GLenum pname, const GLfloat *params) */
  public void glLightfv(int light, int pname, float params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLightfv");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glLightfv(light)");
      return;
    }
    temp = new float[4];
    System.arraycopy(params, 0, temp, 0, 4);
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_POSITION:
      case GL_SPOT_DIRECTION:
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_SPOT_EXPONENT:
        if (params[0] < 0 || params[0] > 128) {
          CC.gl_error(GL_INVALID_VALUE, "glLightfv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_SPOT_CUTOFF:
        if ((params[0] < 0 || params[0] > 90) && params[0] != 180) {
          CC.gl_error(GL_INVALID_VALUE, "glLightfv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_CONSTANT_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightfv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_LINEAR_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightfv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_QUADRATIC_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightfv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightfv(pname)");
    }
  }

  /** GLvoid glLightiv (GLenum light, GLenum pname, const GLint *params) */
  public void glLightiv(int light, int pname, int params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLightiv");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glLightiv(light)");
      return;
    }
    temp = new float[4];
    temp[0] = params[0];
    temp[1] = params[1];
    temp[2] = params[2];
    temp[3] = params[3];
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_POSITION:
      case GL_SPOT_DIRECTION:
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_SPOT_EXPONENT:
        if (params[0] < 0 || params[0] > 128) {
          CC.gl_error(GL_INVALID_VALUE, "glLightiv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_SPOT_CUTOFF:
        if ((params[0] < 0 || params[0] > 90) && params[0] != 180) {
          CC.gl_error(GL_INVALID_VALUE, "glLightiv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_CONSTANT_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightiv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_LINEAR_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightiv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      case GL_QUADRATIC_ATTENUATION:
        if (params[0] < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glLightiv(param)");
          return;
        }
        CC.gl_light(light - GL_LIGHT0, pname, temp);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightiv(pname)");
    }
  }

  /** GLvoid glGetLightfv (GLenum light, GLenum pname, GLfloat *params) */
  public void glGetLightfv(int light, int pname, float params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetLightfv");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glGetLightfv(light)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_POSITION:
      case GL_SPOT_DIRECTION:
        temp = CC.gl_get_light(light - GL_LIGHT0, pname);
        params[0] = temp[0];
        params[1] = temp[1];
        params[2] = temp[2];
        params[3] = temp[3];
        break;
      case GL_SPOT_EXPONENT:
      case GL_SPOT_CUTOFF:
      case GL_CONSTANT_ATTENUATION:
      case GL_LINEAR_ATTENUATION:
      case GL_QUADRATIC_ATTENUATION:
        temp = CC.gl_get_light(light - GL_LIGHT0, pname);
        params[0] = temp[0];
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetLightfv(pname)");
    }
  }

  /** GLvoid glGetLightiv (GLenum light, GLenum pname, GLint *params) */
  public void glGetLightiv(int light, int pname, int params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetLightiv");
      return;
    }
    if (light < GL_LIGHT0 || light - GL_LIGHT0 >= gl_context.MAX_LIGHTS) {
      CC.gl_error(GL_INVALID_ENUM, "glGetLightiv(light)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_POSITION:
      case GL_SPOT_DIRECTION:
        temp = CC.gl_get_light(light - GL_LIGHT0, pname);
        params[0] = (int) temp[0];
        params[1] = (int) temp[1];
        params[2] = (int) temp[2];
        params[3] = (int) temp[3];
        break;
      case GL_SPOT_EXPONENT:
      case GL_SPOT_CUTOFF:
      case GL_CONSTANT_ATTENUATION:
      case GL_LINEAR_ATTENUATION:
      case GL_QUADRATIC_ATTENUATION:
        temp = CC.gl_get_light(light - GL_LIGHT0, pname);
        params[0] = (int) temp[0];
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetLightiv(pname)");
    }
  }

  /** GLvoid glLightModelf (GLenum pname, GLfloat param) */
  public void glLightModelf(int pname, float param) {
    float params[] = new float[1];
    switch (pname) {
      case GL_LIGHT_MODEL_LOCAL_VIEWER:
      case GL_LIGHT_MODEL_TWO_SIDE:
        params[0] = param;
        CC.gl_light_model(pname, params);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightModelf(pname)");
    }
  }

  public void glLightModelf(int pname, boolean param) {
    // used for some boolean parameters
    if (param)
      glLightModelf(pname, 1.0f);
    else
      glLightModelf(pname, 0.0f);
  }

  /** GLvoid glLightModeli (GLenum pname, GLint param) */
  public void glLightModeli(int pname, int param) {
    float params[] = new float[1];
    switch (pname) {
      case GL_LIGHT_MODEL_LOCAL_VIEWER:
      case GL_LIGHT_MODEL_TWO_SIDE:
        params[0] = param;
        CC.gl_light_model(pname, params);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightModeli(pname)");
    }
  }

  public void glLightModeli(int pname, boolean param) {
    // used for some boolean parameters
    if (param)
      glLightModeli(pname, 1);
    else
      glLightModeli(pname, 0);
  }

  /** GLvoid glLightModelfv (GLenum pname, const GLfloat *params) */
  public void glLightModelfv(int pname, float params[]) {
    float temp[];
    switch (pname) {
      case GL_LIGHT_MODEL_AMBIENT:
        temp = new float[4];
        System.arraycopy(params, 0, temp, 0, 4);
        CC.gl_light_model(pname, temp);
        break;
      case GL_LIGHT_MODEL_LOCAL_VIEWER:
      case GL_LIGHT_MODEL_TWO_SIDE:
        temp = new float[1];
        temp[0] = params[0];
        CC.gl_light_model(pname, temp);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightModelfv(pname)");
    }
  }

  public void glLightModelfv(int pname, boolean param[]) {
    // used for some boolean parameters
    float temp[] = new float[param.length];
    for (int i = 0; i < param.length; i++) {
      if (param[i])
        temp[i] = 1.0f;
      else
        temp[i] = 0.0f;
    }
    glLightModelfv(pname, temp);
  }

  /** GLvoid glLightModeliv (GLenum pname, const GLint *params) */
  public void glLightModeliv(int pname, int params[]) {
    float temp[];
    switch (pname) {
      case GL_LIGHT_MODEL_AMBIENT:
        temp = new float[4];
        temp[0] = params[0];
        temp[1] = params[1];
        temp[2] = params[2];
        temp[3] = params[3];
        CC.gl_light_model(pname, temp);
        break;
      case GL_LIGHT_MODEL_LOCAL_VIEWER:
      case GL_LIGHT_MODEL_TWO_SIDE:
        temp = new float[1];
        temp[0] = params[0];
        CC.gl_light_model(pname, temp);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glLightModeliv(pname)");
    }
  }

  public void glLightModeliv(int pname, boolean param[]) {
    // used for some boolean parameters
    int temp[] = new int[param.length];
    for (int i = 0; i < param.length; i++) {
      if (param[i])
        temp[i] = 1;
      else
        temp[i] = 0;
    }
    glLightModeliv(pname, temp);
  }

  /** GLvoid glMaterialf (GLenum face, GLenum pname, GLfloat param) */
  public void glMaterialf(int face, int pname, float param) {
    float params[] = new float[1];
    if (face != GL_FRONT && face != GL_BACK && face != GL_FRONT_AND_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glMaterialf(face)");
      return;
    }
    if (pname == GL_SHININESS) {
      params[0] = gl_util.CLAMP(param, 0.0, 180.0);
      switch (face) {
        case GL_FRONT:
          CC.gl_material(0, pname, params);
          break;
        case GL_BACK:
          CC.gl_material(1, pname, params);
          break;
        case GL_FRONT_AND_BACK:
          CC.gl_material(0, pname, params);
          CC.gl_material(1, pname, params);
          break;
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glMaterialf(pname)");
    }
  }

  /** GLvoid glMateriali (GLenum face, GLenum pname, GLint param) */
  public void glMateriali(int face, int pname, int param) {
    float params[] = new float[1];
    if (face != GL_FRONT && face != GL_BACK && face != GL_FRONT_AND_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glMateriali(face)");
      return;
    }
    if (pname == GL_SHININESS) {
      params[0] = gl_util.CLAMP(param, 0, 180);
      switch (face) {
        case GL_FRONT:
          CC.gl_material(0, pname, params);
          break;
        case GL_BACK:
          CC.gl_material(1, pname, params);
          break;
        case GL_FRONT_AND_BACK:
          CC.gl_material(0, pname, params);
          CC.gl_material(1, pname, params);
          break;
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glMateriali(pname)");
    }
  }

  /** GLvoid glMaterialfv (GLenum face, GLenum pname, const GLfloat *params) */
  public void glMaterialfv(int face, int pname, float params[]) {
    float temp[];
    if (face != GL_FRONT && face != GL_BACK && face != GL_FRONT_AND_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glMaterialfv(face)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_EMISSION:
      case GL_AMBIENT_AND_DIFFUSE:
        temp = new float[4];
        System.arraycopy(params, 0, temp, 0, 4);
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      case GL_SHININESS:
        temp = new float[1];
        temp[0] = gl_util.CLAMP(params[0], 0.0, 180.0);
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      case GL_COLOR_INDEXES: // only 3 entry....
        temp = new float[3];
        System.arraycopy(params, 0, temp, 0, 3);
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glMaterialfv(pname)");
    }
  }

  /** GLvoid glMaterialiv (GLenum face, GLenum pname, const GLint *params) */
  public void glMaterialiv(int face, int pname, int params[]) {
    float temp[];
    if (face != GL_FRONT && face != GL_BACK && face != GL_FRONT_AND_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glMaterialiv(face)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_EMISSION:
      case GL_AMBIENT_AND_DIFFUSE:
        temp = new float[4];
        temp[0] = params[0];
        temp[1] = params[1];
        temp[2] = params[2];
        temp[3] = params[3];
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      case GL_SHININESS:
        temp = new float[1];
        temp[0] = gl_util.CLAMP(params[0], 0, 180);
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      case GL_COLOR_INDEXES:
        temp = new float[3];
        temp[0] = params[0];
        temp[1] = params[1];
        temp[2] = params[2];
        switch (face) {
          case GL_FRONT:
            CC.gl_material(0, pname, temp);
            break;
          case GL_BACK:
            CC.gl_material(1, pname, temp);
            break;
          case GL_FRONT_AND_BACK:
            CC.gl_material(0, pname, temp);
            CC.gl_material(1, pname, temp);
            break;
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glMaterialfi(pname)");
    }
  }

  /** GLvoid glGetMaterialfv (GLenum face, GLenum pname, GLfloat *params) */
  public void glGetMaterialfv(int face, int pname, float params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetMaterialfv");
      return;
    }
    if (face != GL_FRONT && face != GL_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glGetMaterialfv(face)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_EMISSION:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = temp[0];
        params[1] = temp[1];
        params[2] = temp[2];
        params[3] = temp[3];
        break;
      case GL_SHININESS:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = temp[0];
        break;
      case GL_COLOR_INDEXES:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = temp[0];
        params[1] = temp[1];
        params[2] = temp[2];
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetMaterialfv(pname)");
    }
  }

  /** GLvoid glGetMaterialiv (GLenum face, GLenum pname, GLint *params) */
  public void glGetMaterialiv(int face, int pname, int params[]) {
    float temp[];
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGetMaterialiv");
      return;
    }
    if (face != GL_FRONT && face != GL_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glGetMaterialiv(face)");
      return;
    }
    switch (pname) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_EMISSION:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = (int) temp[0];
        params[1] = (int) temp[1];
        params[2] = (int) temp[2];
        params[3] = (int) temp[3];
        break;
      case GL_SHININESS:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = (int) temp[0];
        break;
      case GL_COLOR_INDEXES:
        if (face == GL_FRONT) {
          temp = CC.gl_get_material(0, pname);
        } else { // face == GL_BACK
          temp = CC.gl_get_material(1, pname);
        }
        params[0] = (int) temp[0];
        params[1] = (int) temp[1];
        params[2] = (int) temp[2];
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetMaterialiv(pname)");
    }
  }

  /** GLvoid glColorMaterial (GLenum face, GLenum mode) */
  public void glColorMaterial(int face, int mode) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glColorMaterial");
      return;
    }
    if (face != GL_FRONT && face != GL_BACK && face != GL_FRONT_AND_BACK) {
      CC.gl_error(GL_INVALID_ENUM, "glColorMaterial(face)");
      return;
    }
    switch (mode) {
      case GL_AMBIENT:
      case GL_DIFFUSE:
      case GL_SPECULAR:
      case GL_EMISSION:
      case GL_AMBIENT_AND_DIFFUSE:
        CC.gl_color_material(face, mode);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glColorMaterial(mode)");
    }
  }

  /**
   * Raster Functions
   */

  /** GLvoid glPixelStoref (GLenum pname, GLfloat param) */
  public void glPixelStoref(int pname, float param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPixelStoref");
      return;
    }
    switch (pname) {
      case GL_PACK_SWAP_BYTES:
      case GL_UNPACK_SWAP_BYTES:
      case GL_PACK_LSB_FIRST:
      case GL_UNPACK_LSB_FIRST:
        CC.gl_pixel_store(pname, (int) param);
        break;
      case GL_PACK_ROW_LENGTH:
      case GL_UNPACK_ROW_LENGTH:
      case GL_PACK_IMAGE_HEIGHT:
      case GL_UNPACK_IMAGE_HEIGHT:
      case GL_PACK_SKIP_PIXELS:
      case GL_UNPACK_SKIP_PIXELS:
      case GL_PACK_SKIP_ROWS:
      case GL_UNPACK_SKIP_ROWS:
      case GL_PACK_SKIP_IMAGES:
      case GL_UNPACK_SKIP_IMAGES:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glPixelStoref(param)");
          return;
        }
        CC.gl_pixel_store(pname, (int) param);
        break;
      case GL_PACK_ALIGNMENT:
      case GL_UNPACK_ALIGNMENT:
        if (param == 1 || param == 2 || param == 4 || param == 8) {
          CC.gl_pixel_store(pname, (int) param);
        } else {
          CC.gl_error(GL_INVALID_VALUE, "glPixelStoref(param)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glPixelStoref(pname");
    }
  }

  /** GLvoid glPixelStorei (GLenum pname, GLint param) */
  public void glPixelStorei(int pname, int param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPixelStorei");
      return;
    }
    switch (pname) {
      case GL_PACK_SWAP_BYTES:
      case GL_UNPACK_SWAP_BYTES:
      case GL_PACK_LSB_FIRST:
      case GL_UNPACK_LSB_FIRST:
        CC.gl_pixel_store(pname, param);
        break;
      case GL_PACK_ROW_LENGTH:
      case GL_UNPACK_ROW_LENGTH:
      case GL_PACK_IMAGE_HEIGHT:
      case GL_UNPACK_IMAGE_HEIGHT:
      case GL_PACK_SKIP_PIXELS:
      case GL_UNPACK_SKIP_PIXELS:
      case GL_PACK_SKIP_ROWS:
      case GL_UNPACK_SKIP_ROWS:
      case GL_PACK_SKIP_IMAGES:
      case GL_UNPACK_SKIP_IMAGES:
        if (param < 0) {
          CC.gl_error(GL_INVALID_VALUE, "glPixelStorei(param)");
          return;
        }
        CC.gl_pixel_store(pname, param);
        break;
      case GL_PACK_ALIGNMENT:
      case GL_UNPACK_ALIGNMENT:
        if (param == 1 || param == 2 || param == 4 || param == 8) {
          CC.gl_pixel_store(pname, param);
        } else {
          CC.gl_error(GL_INVALID_VALUE, "glPixelStorei(param)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glPixelStorei(pname)");
    }
  }

  /** GLvoid glPixelTransferf (GLenum pname, GLfloat param) */
  public void glPixelTransferf(int pname, float param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPixelTransferf");
      return;
    }
    switch (pname) {
      case GL_MAP_COLOR:
      case GL_MAP_STENCIL:
      case GL_INDEX_SHIFT:
      case GL_INDEX_OFFSET:
      case GL_RED_SCALE:
      case GL_RED_BIAS:
      case GL_GREEN_SCALE:
      case GL_GREEN_BIAS:
      case GL_BLUE_SCALE:
      case GL_BLUE_BIAS:
      case GL_ALPHA_SCALE:
      case GL_ALPHA_BIAS:
      case GL_DEPTH_SCALE:
      case GL_DEPTH_BIAS:
        CC.gl_pixel_transfer(pname, param);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glPixelTransferf(pname)");
    }
  }

  /** GLvoid glPixelTransferi (GLenum pname, GLint param) */
  public void glPixelTransferi(int pname, int param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPixelTransferi");
      return;
    }
    switch (pname) {
      case GL_MAP_COLOR:
      case GL_MAP_STENCIL:
      case GL_INDEX_SHIFT:
      case GL_INDEX_OFFSET:
      case GL_RED_SCALE:
      case GL_RED_BIAS:
      case GL_GREEN_SCALE:
      case GL_GREEN_BIAS:
      case GL_BLUE_SCALE:
      case GL_BLUE_BIAS:
      case GL_ALPHA_SCALE:
      case GL_ALPHA_BIAS:
      case GL_DEPTH_SCALE:
      case GL_DEPTH_BIAS:
        CC.gl_pixel_transfer(pname, param);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glPixelTransferi(pname)");
    }
  }

  /**
   * GLvoid glReadPixels (GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum
   * type, GLvoid *pixels)
   */
  public void glReadPixels(int x, int y, int width, int height, int format, int type,
      Object pixels) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glReadPixels");
      return;
    }
    int size = size_of(type);
    if (size <= 0) {
      CC.gl_error(GL_INVALID_ENUM, "glReadPixels(type)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
        CC.gl_read_index_pixels(x, y, width, height, size, pixels);
        break;
      case GL_RGB:
      case GL_RGBA:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        CC.gl_read_color_pixels(x, y, width, height, format, size, pixels);
        break;
      case GL_STENCIL_INDEX:
        CC.gl_read_stencil_pixels(x, y, width, height, size, pixels);
        break;
      case GL_DEPTH_COMPONENT:
        CC.gl_read_depth_pixels(x, y, width, height, size, pixels);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glReadPixels(format)");
    }
  }

  /**
   * GLvoid glDrawPixels (GLsizei width, GLsizei height, GLenum format, GLenum type, const GLvoid
   * *pixels)
   */
  public void glDrawPixels(int width, int height, int format, int type, Object pixels) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDrawPixels");
      return;
    }
    if ((width < 0) || (height < 0)) {
      CC.gl_error(GL_INVALID_ENUM, "glDrawPixels(width or height)");
      return;
    }
    int size = size_of(type);
    if (size <= 0) {
      CC.gl_error(GL_INVALID_ENUM, "glDrawPixels(type)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
        CC.gl_draw_index_pixels(width, height, size, pixels);
        break;
      case GL_RGB:
      case GL_RGBA:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        CC.gl_draw_color_pixels(width, height, format, size, pixels);
        break;
      case GL_STENCIL_INDEX:
        CC.gl_draw_stencil_pixels(width, height, size, pixels);
        break;
      case GL_DEPTH_COMPONENT:
        CC.gl_draw_depth_pixels(width, height, size, pixels);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glDrawPixels(format)");
    }
  }

  /**
   * GLvoid glCopyPixels (GLint x, GLint y, GLsizei width, GLsizei height, GLenum type)
   */
  public void glCopyPixels(int x, int y, int width, int height, int type) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glCopyPixels");
      return;
    }
    if ((width < 0) || (height < 0)) {
      CC.gl_error(GL_INVALID_ENUM, "glCopyPixels(width or height)");
      return;
    }
    switch (type) {
      case GL_COLOR:
        // TODO: for color-index
        CC.gl_copy_color_pixels(x, y, width, height);
        break;
      case GL_STENCIL:
        CC.gl_copy_stencil_pixels(x, y, width, height);
        break;
      case GL_DEPTH:
        CC.gl_copy_depth_pixels(x, y, width, height);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glCopyPixels(type)");
    }
  }

  /**
   * Stenciling
   */

  /** GLvoid glStencilFunc (GLenum func, GLint ref, GLuint mask); */
  public void glStencilFunc(int func, int ref, int mask) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glStencilFunc");
      return;
    }
    switch (func) {
      case GL_NEVER:
      case GL_LESS:
      case GL_GEQUAL:
      case GL_LEQUAL:
      case GL_GREATER:
      case GL_NOTEQUAL:
      case GL_EQUAL:
      case GL_ALWAYS:
        CC.gl_stencil_func(func, ref, mask);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glStencilFunc(func)");
    }
  }

  /** GLvoid glStencilMask (GLuint mask); */
  public void glStencilMask(int mask) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glStencilMask");
      return;
    }
    CC.gl_stencil_mask(mask);
  }

  private boolean check_stencil_op(int op) {
    switch (op) {
      case GL_KEEP:
      case GL_ZERO:
      case GL_REPLACE:
      case GL_INCR:
      case GL_DECR:
      case GL_INVERT:
        return true;
      default:
        return false;
    }
  }

  /** GLvoid glStencilOp (GLenum fail, GLenum zfail, GLenum zpass); */
  public void glStencilOp(int fail, int zfail, int zpass) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glStencilOp");
      return;
    }
    if (!check_stencil_op(fail)) {
      CC.gl_error(GL_INVALID_ENUM, "glStencilOp(fail)");
      return;
    }
    if (!check_stencil_op(zfail)) {
      CC.gl_error(GL_INVALID_ENUM, "glStencilOp(zfail)");
      return;
    }
    if (!check_stencil_op(zpass)) {
      CC.gl_error(GL_INVALID_ENUM, "glStencilOp(zpass)");
      return;
    }
    CC.gl_stencil_op(fail, zfail, zpass);
  }

  /** GLvoid glClearStencil (GLint s); */
  public void glClearStencil(int s) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glClearStencil");
      return;
    }
    CC.gl_clear_stencil(s);
  }

  /**
   * Texture Mapping
   */

  /** GLvoid glTexGend (GLenum coord, GLenum pname, GLdouble param); */
  public void glTexGenf(int coord, int pname, double param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGend");
      return;
    }
    if (pname == GL_TEXTURE_GEN_MODE) {
      int mode = (int) param;
      if ((coord == GL_S) || (coord == GL_T)) {
        if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR) || (mode == GL_SPHERE_MAP)) {
          CC.gl_tex_gen_i(coord, mode);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGend(param)");
        }
      } else if ((coord == GL_R) || (coord == GL_Q)) {
        if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR)) {
          CC.gl_tex_gen_i(coord, mode);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGend(param)");
        }
      } else {
        CC.gl_error(GL_INVALID_ENUM, "glTexGend(coord)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexGend(pname)");
    }
  }

  /** GLvoid glTexGenf (GLenum coord, GLenum pname, GLfloat param); */
  public void glTexGenf(int coord, int pname, float param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGenf");
      return;
    }
    if (pname == GL_TEXTURE_GEN_MODE) {
      int mode = (int) param;
      if ((coord == GL_S) || (coord == GL_T)) {
        if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR) || (mode == GL_SPHERE_MAP)) {
          CC.gl_tex_gen_i(coord, mode);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGenf(param)");
        }
      } else if ((coord == GL_R) || (coord == GL_Q)) {
        if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR)) {
          CC.gl_tex_gen_i(coord, mode);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGenf(param)");
        }
      } else {
        CC.gl_error(GL_INVALID_ENUM, "glTexGenf(coord)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexGenf(pname)");
    }
  }

  /** GLvoid glTexGeni (GLenum coord, GLenum pname, GLint param); */
  public void glTexGeni(int coord, int pname, int param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGeni");
      return;
    }
    if (pname == GL_TEXTURE_GEN_MODE) {
      if ((coord == GL_S) || (coord == GL_T)) {
        if ((param == GL_OBJECT_LINEAR) || (param == GL_EYE_LINEAR) || (param == GL_SPHERE_MAP)) {
          CC.gl_tex_gen_i(coord, param);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGeni(param)");
        }
      } else if ((coord == GL_R) || (coord == GL_Q)) {
        if ((param == GL_OBJECT_LINEAR) || (param == GL_EYE_LINEAR)) {
          CC.gl_tex_gen_i(coord, param);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGeni(param)");
        }
      } else {
        CC.gl_error(GL_INVALID_ENUM, "glTexGeni(coord)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexGeni(pname)");
    }
  }

  /** GLvoid glTexGendv (GLenum coord, GLenum pname, const GLdouble *params); */
  public void glTexGendv(int coord, int pname, double params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGendv");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        int mode = (int) params[0];
        if ((coord == GL_S) || (coord == GL_T)) {
          if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR) || (mode == GL_SPHERE_MAP)) {
            CC.gl_tex_gen_i(coord, mode);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGendv(params)");
          }
        } else if ((coord == GL_R) || (coord == GL_Q)) {
          if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR)) {
            CC.gl_tex_gen_i(coord, mode);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGendv(params)");
          }
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGendv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
      case GL_EYE_PLANE:
        if ((coord == GL_S) || (coord == GL_T) || (coord == GL_R) || (coord == GL_Q)) {
          float fparams[] = new float[4];
          fparams[0] = (float) params[0];
          fparams[1] = (float) params[1];
          fparams[2] = (float) params[2];
          fparams[3] = (float) params[3];
          CC.gl_tex_gen_f(coord, pname, fparams);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGendv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexGendv(pname)");
    }
  }

  /** GLvoid glTexGenfv (GLenum coord, GLenum pname, const GLfloat *params); */
  public void glTexGenfv(int coord, int pname, float params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGenfv");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        int mode = (int) params[0];
        if ((coord == GL_S) || (coord == GL_T)) {
          if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR) || (mode == GL_SPHERE_MAP)) {
            CC.gl_tex_gen_i(coord, mode);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGenfv(params)");
          }
        } else if ((coord == GL_R) || (coord == GL_Q)) {
          if ((mode == GL_OBJECT_LINEAR) || (mode == GL_EYE_LINEAR)) {
            CC.gl_tex_gen_i(coord, mode);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGenfv(params)");
          }
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGenfv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
      case GL_EYE_PLANE:
        if ((coord == GL_S) || (coord == GL_T) || (coord == GL_R) || (coord == GL_Q)) {
          CC.gl_tex_gen_f(coord, pname, params);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGenfv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexGenfv(pname)");
    }
  }

  /** GLvoid glTexGeniv (GLenum coord, GLenum pname, const GLint *params); */
  public void glTexGeniv(int coord, int pname, int params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexGeniv");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        if ((coord == GL_S) || (coord == GL_T)) {
          if ((params[0] == GL_OBJECT_LINEAR) || (params[0] == GL_EYE_LINEAR)
              || (params[0] == GL_SPHERE_MAP)) {
            CC.gl_tex_gen_i(coord, params[0]);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGeniv(params)");
          }
        } else if ((coord == GL_R) || (coord == GL_Q)) {
          if ((params[0] == GL_OBJECT_LINEAR) || (params[0] == GL_EYE_LINEAR)) {
            CC.gl_tex_gen_i(coord, params[0]);
          } else {
            CC.gl_error(GL_INVALID_ENUM, "glTexGeniv(params)");
          }
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGeniv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
      case GL_EYE_PLANE:
        if ((coord == GL_S) || (coord == GL_T) || (coord == GL_R) || (coord == GL_Q)) {
          float fparams[] = new float[4];
          fparams[0] = params[0];
          fparams[1] = params[1];
          fparams[2] = params[2];
          fparams[3] = params[3];
          CC.gl_tex_gen_f(coord, pname, fparams);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexGeniv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexGeniv(pname)");
    }
  }

  /** GLvoid glGetTexGendv (GLenum coord, GLenum pname, GLdouble *params); */
  public void glGetTexGendv(int coord, int pname, double params[]) {
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.Mode;
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.Mode;
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.Mode;
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.Mode;
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGendv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.ObjectLinear[0];
            params[1] = Context.Texture.CurrentS.ObjectLinear[1];
            params[2] = Context.Texture.CurrentS.ObjectLinear[2];
            params[3] = Context.Texture.CurrentS.ObjectLinear[3];
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.ObjectLinear[0];
            params[1] = Context.Texture.CurrentT.ObjectLinear[1];
            params[2] = Context.Texture.CurrentT.ObjectLinear[2];
            params[3] = Context.Texture.CurrentT.ObjectLinear[3];
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.ObjectLinear[0];
            params[1] = Context.Texture.CurrentR.ObjectLinear[1];
            params[2] = Context.Texture.CurrentR.ObjectLinear[2];
            params[3] = Context.Texture.CurrentR.ObjectLinear[3];
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.ObjectLinear[0];
            params[1] = Context.Texture.CurrentQ.ObjectLinear[1];
            params[2] = Context.Texture.CurrentQ.ObjectLinear[2];
            params[3] = Context.Texture.CurrentQ.ObjectLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGendv(coord)");
        }
        break;
      case GL_EYE_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.EyeLinear[0];
            params[1] = Context.Texture.CurrentS.EyeLinear[1];
            params[2] = Context.Texture.CurrentS.EyeLinear[2];
            params[3] = Context.Texture.CurrentS.EyeLinear[3];
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.EyeLinear[0];
            params[1] = Context.Texture.CurrentT.EyeLinear[1];
            params[2] = Context.Texture.CurrentT.EyeLinear[2];
            params[3] = Context.Texture.CurrentT.EyeLinear[3];
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.EyeLinear[0];
            params[1] = Context.Texture.CurrentR.EyeLinear[1];
            params[2] = Context.Texture.CurrentR.EyeLinear[2];
            params[3] = Context.Texture.CurrentR.EyeLinear[3];
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.EyeLinear[0];
            params[1] = Context.Texture.CurrentQ.EyeLinear[1];
            params[2] = Context.Texture.CurrentQ.EyeLinear[2];
            params[3] = Context.Texture.CurrentQ.EyeLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGendv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexGendv(pname)");
    }
  }

  /** GLvoid glGetTexGenfv (GLenum coord, GLenum pname, GLfloat *params); */
  public void glGetTexGenfv(int coord, int pname, float params[]) {
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.Mode;
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.Mode;
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.Mode;
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.Mode;
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGenfv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.ObjectLinear[0];
            params[1] = Context.Texture.CurrentS.ObjectLinear[1];
            params[2] = Context.Texture.CurrentS.ObjectLinear[2];
            params[3] = Context.Texture.CurrentS.ObjectLinear[3];
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.ObjectLinear[0];
            params[1] = Context.Texture.CurrentT.ObjectLinear[1];
            params[2] = Context.Texture.CurrentT.ObjectLinear[2];
            params[3] = Context.Texture.CurrentT.ObjectLinear[3];
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.ObjectLinear[0];
            params[1] = Context.Texture.CurrentR.ObjectLinear[1];
            params[2] = Context.Texture.CurrentR.ObjectLinear[2];
            params[3] = Context.Texture.CurrentR.ObjectLinear[3];
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.ObjectLinear[0];
            params[1] = Context.Texture.CurrentQ.ObjectLinear[1];
            params[2] = Context.Texture.CurrentQ.ObjectLinear[2];
            params[3] = Context.Texture.CurrentQ.ObjectLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGenfv(coord)");
        }
        break;
      case GL_EYE_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.EyeLinear[0];
            params[1] = Context.Texture.CurrentS.EyeLinear[1];
            params[2] = Context.Texture.CurrentS.EyeLinear[2];
            params[3] = Context.Texture.CurrentS.EyeLinear[3];
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.EyeLinear[0];
            params[1] = Context.Texture.CurrentT.EyeLinear[1];
            params[2] = Context.Texture.CurrentT.EyeLinear[2];
            params[3] = Context.Texture.CurrentT.EyeLinear[3];
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.EyeLinear[0];
            params[1] = Context.Texture.CurrentR.EyeLinear[1];
            params[2] = Context.Texture.CurrentR.EyeLinear[2];
            params[3] = Context.Texture.CurrentR.EyeLinear[3];
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.EyeLinear[0];
            params[1] = Context.Texture.CurrentQ.EyeLinear[1];
            params[2] = Context.Texture.CurrentQ.EyeLinear[2];
            params[3] = Context.Texture.CurrentQ.EyeLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGenfv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexGenfv(pname)");
    }
  }

  /** GLvoid glGetTexGeniv (GLenum coord, GLenum pname, GLint *params); */
  public void glGetTexGeniv(int coord, int pname, int params[]) {
    switch (pname) {
      case GL_TEXTURE_GEN_MODE:
        switch (coord) {
          case GL_S:
            params[0] = Context.Texture.CurrentS.Mode;
            break;
          case GL_T:
            params[0] = Context.Texture.CurrentT.Mode;
            break;
          case GL_R:
            params[0] = Context.Texture.CurrentR.Mode;
            break;
          case GL_Q:
            params[0] = Context.Texture.CurrentQ.Mode;
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGeniv(coord)");
        }
        break;
      case GL_OBJECT_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = (int) Context.Texture.CurrentS.ObjectLinear[0];
            params[1] = (int) Context.Texture.CurrentS.ObjectLinear[1];
            params[2] = (int) Context.Texture.CurrentS.ObjectLinear[2];
            params[3] = (int) Context.Texture.CurrentS.ObjectLinear[3];
            break;
          case GL_T:
            params[0] = (int) Context.Texture.CurrentT.ObjectLinear[0];
            params[1] = (int) Context.Texture.CurrentT.ObjectLinear[1];
            params[2] = (int) Context.Texture.CurrentT.ObjectLinear[2];
            params[3] = (int) Context.Texture.CurrentT.ObjectLinear[3];
            break;
          case GL_R:
            params[0] = (int) Context.Texture.CurrentR.ObjectLinear[0];
            params[1] = (int) Context.Texture.CurrentR.ObjectLinear[1];
            params[2] = (int) Context.Texture.CurrentR.ObjectLinear[2];
            params[3] = (int) Context.Texture.CurrentR.ObjectLinear[3];
            break;
          case GL_Q:
            params[0] = (int) Context.Texture.CurrentQ.ObjectLinear[0];
            params[1] = (int) Context.Texture.CurrentQ.ObjectLinear[1];
            params[2] = (int) Context.Texture.CurrentQ.ObjectLinear[2];
            params[3] = (int) Context.Texture.CurrentQ.ObjectLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGeniv(coord)");
        }
        break;
      case GL_EYE_PLANE:
        switch (coord) {
          case GL_S:
            params[0] = (int) Context.Texture.CurrentS.EyeLinear[0];
            params[1] = (int) Context.Texture.CurrentS.EyeLinear[1];
            params[2] = (int) Context.Texture.CurrentS.EyeLinear[2];
            params[3] = (int) Context.Texture.CurrentS.EyeLinear[3];
            break;
          case GL_T:
            params[0] = (int) Context.Texture.CurrentT.EyeLinear[0];
            params[1] = (int) Context.Texture.CurrentT.EyeLinear[1];
            params[2] = (int) Context.Texture.CurrentT.EyeLinear[2];
            params[3] = (int) Context.Texture.CurrentT.EyeLinear[3];
            break;
          case GL_R:
            params[0] = (int) Context.Texture.CurrentR.EyeLinear[0];
            params[1] = (int) Context.Texture.CurrentR.EyeLinear[1];
            params[2] = (int) Context.Texture.CurrentR.EyeLinear[2];
            params[3] = (int) Context.Texture.CurrentR.EyeLinear[3];
            break;
          case GL_Q:
            params[0] = (int) Context.Texture.CurrentQ.EyeLinear[0];
            params[1] = (int) Context.Texture.CurrentQ.EyeLinear[1];
            params[2] = (int) Context.Texture.CurrentQ.EyeLinear[2];
            params[3] = (int) Context.Texture.CurrentQ.EyeLinear[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexGeniv(coord)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexGeniv(pname)");
    }
  }

  /** GLvoid glTexEnvf (GLenum target, GLenum pname, GLfloat param); */
  public void glTexEnvf(int target, int pname, float param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexEnvf");
      return;
    }
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnvf(target)");
      return;
    }
    if (pname == GL_TEXTURE_ENV_MODE) {
      int iparam = (int) param;
      if ((iparam == GL_MODULATE) || (iparam == GL_BLEND) || (iparam == GL_DECAL)
          || (iparam == GL_REPLACE)) {
        CC.gl_tex_env_i(iparam);
      } else {
        CC.gl_error(GL_INVALID_ENUM, "glTexEnvf(param)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnvf(pname)");
    }
  }

  /** GLvoid glTexEnvi (GLenum target, GLenum pname, GLint param); */
  public void glTexEnvi(int target, int pname, int param) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexEnvi");
      return;
    }
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnvi(target)");
      return;
    }
    if (pname == GL_TEXTURE_ENV_MODE) {
      if ((param == GL_MODULATE) || (param == GL_BLEND) || (param == GL_DECAL)
          || (param == GL_REPLACE)) {
        CC.gl_tex_env_i(param);
      } else {
        CC.gl_error(GL_INVALID_ENUM, "glTexEnvf(param)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnvf(pname)");
    }
  }

  /** GLvoid glTexEnvfv (GLenum target, GLenum pname, const GLfloat *params); */
  public void glTexEnvfv(int target, int pname, float params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexEnvfv");
      return;
    }
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnvfv(target)");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_ENV_MODE:
        int iparam = (int) params[0];
        if ((iparam == GL_MODULATE) || (iparam == GL_BLEND) || (iparam == GL_DECAL)
            || (iparam == GL_REPLACE)) {
          CC.gl_tex_env_i(iparam);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexEnvfv(params)");
        }
        break;
      case GL_TEXTURE_ENV_COLOR:
        float fparams[] = new float[4];
        fparams[0] = gl_util.CLAMP(params[0], 0.0, 1.0);
        fparams[1] = gl_util.CLAMP(params[1], 0.0, 1.0);
        fparams[2] = gl_util.CLAMP(params[2], 0.0, 1.0);
        fparams[3] = gl_util.CLAMP(params[3], 0.0, 1.0);
        CC.gl_tex_env_f(fparams);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexEnvfv(pname)");
    }
  }

  /** GLvoid glTexEnviv (GLenum target, GLenum pname, const GLint *params); */
  public void glTexEnviv(int target, int pname, int params[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexEnviv");
      return;
    }
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glTexEnviv(target)");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_ENV_MODE:
        if ((params[0] == GL_MODULATE) || (params[0] == GL_BLEND) || (params[0] == GL_DECAL)
            || (params[0] == GL_REPLACE)) {
          CC.gl_tex_env_i(params[0]);
        } else {
          CC.gl_error(GL_INVALID_ENUM, "glTexEnviv(params)");
        }
        break;
      case GL_TEXTURE_ENV_COLOR:
        float fparams[] = new float[4];
        fparams[0] = gl_util.ItoF(gl_util.CLAMP(params[0], 0, 255));
        fparams[1] = gl_util.ItoF(gl_util.CLAMP(params[1], 0, 255));
        fparams[2] = gl_util.ItoF(gl_util.CLAMP(params[2], 0, 255));
        fparams[3] = gl_util.ItoF(gl_util.CLAMP(params[3], 0, 255));
        CC.gl_tex_env_f(fparams);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexEnviv(pname)");
    }
  }

  /** GLvoid glGetTexEnviv (GLenum target, GLenum pname, GLint *params); */
  public void glGetTexEnviv(int target, int pname, int params[]) {
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glGetTexEnviv(target)");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_ENV_MODE:
        params[0] = Context.Texture.EnvMode;
        break;
      case GL_TEXTURE_ENV_COLOR:
        params[0] = gl_util.FtoI(Context.Texture.EnvColor[0]);
        params[1] = gl_util.FtoI(Context.Texture.EnvColor[1]);
        params[2] = gl_util.FtoI(Context.Texture.EnvColor[2]);
        params[3] = gl_util.FtoI(Context.Texture.EnvColor[3]);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexEnviv(pname)");
    }
  }

  /** GLvoid glGetTexEnvfv (GLenum target, GLenum pname, GLfloat *params); */
  public void glGetTexEnviv(int target, int pname, float params[]) {
    if (target != GL_TEXTURE_ENV) {
      CC.gl_error(GL_INVALID_ENUM, "glGetTexEnvfv(target)");
      return;
    }
    switch (pname) {
      case GL_TEXTURE_ENV_MODE:
        params[0] = Context.Texture.EnvMode;
        break;
      case GL_TEXTURE_ENV_COLOR:
        params[0] = Context.Texture.EnvColor[0];
        params[1] = Context.Texture.EnvColor[1];
        params[2] = Context.Texture.EnvColor[2];
        params[3] = Context.Texture.EnvColor[3];
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexEnvfv(pname)");
    }
  }

  /** GLvoid glTexParameterf (GLenum target, GLenum pname, GLfloat param) */
  public void glTexParameterf(int target, int pname, float param) {
    int iparam = (int) param;
    float params[] = new float[1];
    params[0] = param;
    if (target == GL_TEXTURE_1D || target == GL_TEXTURE_2D || target == GL_TEXTURE_3D) {
      switch (pname) {
        case GL_TEXTURE_MIN_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR || iparam == GL_NEAREST_MIPMAP_NEAREST
              || iparam == GL_LINEAR_MIPMAP_NEAREST || iparam == GL_NEAREST_MIPMAP_LINEAR
              || iparam == GL_LINEAR_MIPMAP_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterf(param)");
          }
          break;
        case GL_TEXTURE_MAG_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterf(param)");
          }
          break;
        case GL_TEXTURE_WRAP_S:
        case GL_TEXTURE_WRAP_T:
        case GL_TEXTURE_WRAP_R:
          if (iparam == GL_CLAMP || iparam == GL_REPEAT) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterf(param)");
          }
          break;
        case GL_TEXTURE_BORDER_COLOR:
          CC.gl_error(GL_INVALID_VALUE, "glTexParameterf(param)");
          break;
        default:
          CC.gl_error(GL_INVALID_ENUM, "glTexParameterf(pname)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexParameterf(target)");
    }
  }

  /** GLvoid glTexParameteri (GLenum target, GLenum pname, GLint param) */
  public void glTexParameteri(int target, int pname, int param) {
    float params[] = new float[1];
    params[0] = param;
    if (target == GL_TEXTURE_1D || target == GL_TEXTURE_2D || target == GL_TEXTURE_3D) {
      switch (pname) {
        case GL_TEXTURE_MIN_FILTER:
          if (param == GL_NEAREST || param == GL_LINEAR || param == GL_NEAREST_MIPMAP_NEAREST
              || param == GL_LINEAR_MIPMAP_NEAREST || param == GL_NEAREST_MIPMAP_LINEAR
              || param == GL_LINEAR_MIPMAP_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteri(param)");
          }
          break;
        case GL_TEXTURE_MAG_FILTER:
          if (param == GL_NEAREST || param == GL_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteri(param)");
          }
          break;
        case GL_TEXTURE_WRAP_S:
        case GL_TEXTURE_WRAP_T:
        case GL_TEXTURE_WRAP_R:
          if (param == GL_CLAMP || param == GL_REPEAT) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteri(param)");
          }
          break;
        case GL_TEXTURE_BORDER_COLOR:
          CC.gl_error(GL_INVALID_VALUE, "glTexParameteri(param)");
          break;
        default:
          CC.gl_error(GL_INVALID_ENUM, "glTexParameteri(pname)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexParameteri(target)");
    }
  }

  /**
   * GLvoid glTexParameterfv (GLenum target, GLenum pname, const GLfloat *params)
   */
  public void glTexParameterfv(int target, int pname, float params[]) {
    int iparam = (int) params[0];
    if (target == GL_TEXTURE_1D || target == GL_TEXTURE_2D || target == GL_TEXTURE_3D) {
      switch (pname) {
        case GL_TEXTURE_MIN_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR || iparam == GL_NEAREST_MIPMAP_NEAREST
              || iparam == GL_LINEAR_MIPMAP_NEAREST || iparam == GL_NEAREST_MIPMAP_LINEAR
              || iparam == GL_LINEAR_MIPMAP_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterfv(param)");
          }
          break;
        case GL_TEXTURE_MAG_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterfv(param)");
          }
          break;
        case GL_TEXTURE_WRAP_S:
        case GL_TEXTURE_WRAP_T:
        case GL_TEXTURE_WRAP_R:
          if (iparam == GL_CLAMP || iparam == GL_REPEAT) {
            CC.gl_tex_parameter(target, pname, params);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameterfv(param)");
          }
          break;
        case GL_TEXTURE_BORDER_COLOR:
          params[0] = gl_util.CLAMP(params[0], 0.0, 1.0);
          params[1] = gl_util.CLAMP(params[1], 0.0, 1.0);
          params[2] = gl_util.CLAMP(params[2], 0.0, 1.0);
          params[3] = gl_util.CLAMP(params[3], 0.0, 1.0);
          CC.gl_tex_parameter(target, pname, params);
          break;
        default:
          CC.gl_error(GL_INVALID_ENUM, "glTexParameterfv(pname)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexParameterfv(target)");
    }
  }

  /**
   * GLvoid glTexParameteriv (GLenum target, GLenum pname, const GLint *params)
   */
  public void glTexParameteriv(int target, int pname, int params[]) {
    int iparam = params[0];
    float fparams[] = new float[1];
    fparams[0] = params[0];
    if (target == GL_TEXTURE_1D || target == GL_TEXTURE_2D || target == GL_TEXTURE_3D) {
      switch (pname) {
        case GL_TEXTURE_MIN_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR || iparam == GL_NEAREST_MIPMAP_NEAREST
              || iparam == GL_LINEAR_MIPMAP_NEAREST || iparam == GL_NEAREST_MIPMAP_LINEAR
              || iparam == GL_LINEAR_MIPMAP_LINEAR) {
            CC.gl_tex_parameter(target, pname, fparams);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteriv(param)");
          }
          break;
        case GL_TEXTURE_MAG_FILTER:
          if (iparam == GL_NEAREST || iparam == GL_LINEAR) {
            CC.gl_tex_parameter(target, pname, fparams);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteriv(param)");
          }
          break;
        case GL_TEXTURE_WRAP_S:
        case GL_TEXTURE_WRAP_T:
        case GL_TEXTURE_WRAP_R:
          if (iparam == GL_CLAMP || iparam == GL_REPEAT) {
            CC.gl_tex_parameter(target, pname, fparams);
          } else {
            CC.gl_error(GL_INVALID_VALUE, "glTexParameteriv(param)");
          }
          break;
        case GL_TEXTURE_BORDER_COLOR:
          fparams = new float[4];
          fparams[0] = gl_util.CLAMP(params[0], 0, 255) / 255.0f;
          fparams[1] = gl_util.CLAMP(params[1], 0, 255) / 255.0f;
          fparams[2] = gl_util.CLAMP(params[2], 0, 255) / 255.0f;
          fparams[3] = gl_util.CLAMP(params[3], 0, 255) / 255.0f;
          CC.gl_tex_parameter(target, pname, fparams);
          break;
        default:
          CC.gl_error(GL_INVALID_ENUM, "glTexParameteriv(pname)");
      }
    } else {
      CC.gl_error(GL_INVALID_ENUM, "glTexParameteri(target)");
    }
  }

  /** GLvoid glGetTexParameterfv (GLenum target, GLenum pname, GLfloat *params) */
  void glGetTexParameterfv(int target, int pname, float params[]) {
    switch (target) {
      case GL_TEXTURE_1D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current1D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current1D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current1D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current1D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current1D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = (Context.Texture.Current1D.BorderColor[0]) / (255.0f);
            params[1] = (Context.Texture.Current1D.BorderColor[1]) / (255.0f);
            params[2] = (Context.Texture.Current1D.BorderColor[2]) / (255.0f);
            params[3] = (Context.Texture.Current1D.BorderColor[3]) / (255.0f);
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameterfv(pname)");
        }
        break;
      case GL_TEXTURE_2D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current2D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current2D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current2D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current2D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current2D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = (Context.Texture.Current2D.BorderColor[0]) / (255.0f);
            params[1] = (Context.Texture.Current2D.BorderColor[1]) / (255.0f);
            params[2] = (Context.Texture.Current2D.BorderColor[2]) / (255.0f);
            params[3] = (Context.Texture.Current2D.BorderColor[3]) / (255.0f);
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameterfv(pname)");
        }
        break;
      case GL_TEXTURE_3D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current3D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current3D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current3D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current3D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current3D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = (Context.Texture.Current3D.BorderColor[0]) / (255.0f);
            params[1] = (Context.Texture.Current3D.BorderColor[1]) / (255.0f);
            params[2] = (Context.Texture.Current3D.BorderColor[2]) / (255.0f);
            params[3] = (Context.Texture.Current3D.BorderColor[3]) / (255.0f);
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameterfv(pname)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexParameterfv(target)");
    }
  }

  /** GLvoid glGetTexParameteriv (GLenum target, GLenum pname, GLint *params) */
  void glGetTexParameteriv(int target, int pname, int params[]) {
    switch (target) {
      case GL_TEXTURE_1D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current1D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current1D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current1D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current1D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current1D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = Context.Texture.Current1D.BorderColor[0];
            params[1] = Context.Texture.Current1D.BorderColor[1];
            params[2] = Context.Texture.Current1D.BorderColor[2];
            params[3] = Context.Texture.Current1D.BorderColor[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameteriv(pname)");
        }
        break;
      case GL_TEXTURE_2D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current2D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current2D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current2D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current2D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current2D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = Context.Texture.Current2D.BorderColor[0];
            params[1] = Context.Texture.Current2D.BorderColor[1];
            params[2] = Context.Texture.Current2D.BorderColor[2];
            params[3] = Context.Texture.Current2D.BorderColor[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameteriv(pname)");
        }
        break;
      case GL_TEXTURE_3D:
        switch (pname) {
          case GL_TEXTURE_MAG_FILTER:
            params[0] = Context.Texture.Current3D.MagFilter;
            break;
          case GL_TEXTURE_MIN_FILTER:
            params[0] = Context.Texture.Current3D.MinFilter;
            break;
          case GL_TEXTURE_WRAP_S:
            params[0] = Context.Texture.Current3D.WrapS;
            break;
          case GL_TEXTURE_WRAP_T:
            params[0] = Context.Texture.Current3D.WrapT;
            break;
          case GL_TEXTURE_WRAP_R:
            params[0] = Context.Texture.Current3D.WrapR;
            break;
          case GL_TEXTURE_BORDER_COLOR:
            params[0] = Context.Texture.Current3D.BorderColor[0];
            params[1] = Context.Texture.Current3D.BorderColor[1];
            params[2] = Context.Texture.Current3D.BorderColor[2];
            params[3] = Context.Texture.Current3D.BorderColor[3];
            break;
          default:
            CC.gl_error(GL_INVALID_ENUM, "glGetTexParameteriv(pname)");
        }
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexParameteriv(target)");
    }
  }

  /**
   * GLvoid glTexImage1D (GLenum target, GLint level, GLint components, GLsizei width, GLint border,
   * GLenum format, GLenum type, const GLvoid *pixels)
   */
  public void glTexImage1D(int target, int level, int components, int width, int border, int format,
      int type, Object pixels) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexImage1D");
      return;
    }
    if (target != GL_TEXTURE_1D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage1D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage1D(level)");
      return;
    }
    int comp = components;
    if (comp < 1 || comp > 4)
      comp = bytes_of(comp);
    if (comp < 1 || comp > 4) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage1D(components)");
      return;
    }
    if (width < 2 * border || width > 2 + gl_context.MAX_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage1D(width)");
      return;
    }
    if (border != 0 && border != 1) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage1D(border)");
      return;
    }
    if (gl_util.logbase2(width - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage1D(width or border)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexImage1D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage1D(type)");
      return;
    }
    CC.gl_tex_image_1d(target, level, comp, width, border, format, size, pixels);
  }

  /**
   * GLvoid glTexImage2D (GLenum target, GLint level, GLint components, GLsizei width, GLsizei
   * height, GLint border, GLenum format, GLenum type, const GLvoid *pixels)
   */
  public void glTexImage2D(int target, int level, int components, int width, int height, int border,
      // int format, int type, byte pixels [][][]) {
      int format, int type, Object pixels) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexImage2D");
      return;
    }
    if (target != GL_TEXTURE_2D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage2D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(level)");
      return;
    }
    int comp = components;
    if (comp < 1 || comp > 4)
      comp = bytes_of(comp);
    if (comp < 1 || comp > 4) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(components)");
      return;
    }
    if (width < 2 * border || width > 2 + gl_context.MAX_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(width)");
      return;
    }
    if (height < 2 * border || height > 2 + gl_context.MAX_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(height)");
      return;
    }
    if (border != 0 && border != 1) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(border)");
      return;
    }
    if (gl_util.logbase2(width - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(width or border)");
      return;
    }
    if (gl_util.logbase2(height - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage2D(height or border)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexImage2D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage2D(type)");
      return;
    }
    CC.gl_tex_image_2d(target, level, comp, width, height, border, format, size, pixels);
  }

  /**
   * GLvoid glTexImage3D (GLenum target, GLint level, GLint components, GLsizei width, GLsizei
   * height, GLsizei depth, GLint border, GLenum format, GLenum type, const GLvoid *pixels)
   */
  public void glTexImage3D(int target, int level, int components, int width, int height, int depth,
      int border, int format, int type, Object pixels) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glTexImage3D");
      return;
    }
    if (target != GL_TEXTURE_3D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage3D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(level)");
      return;
    }
    int comp = components;
    if (comp < 1 || comp > 4)
      comp = bytes_of(comp);
    if (comp < 1 || comp > 4) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(components)");
      return;
    }
    if (width < 2 * border || width > 2 + gl_context.MAX_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(width)");
      return;
    }
    if (height < 2 * border || height > 2 + gl_context.MAX_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(height)");
      return;
    }
    if (depth < 2 * border || depth > 2 + gl_context.MAX_3D_TEXTURE_SIZE) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(depth)");
      return;
    }
    if (border != 0 && border != 1) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(border)");
      return;
    }
    if (gl_util.logbase2(width - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(width or border)");
      return;
    }
    if (gl_util.logbase2(height - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(height or border)");
      return;
    }
    if (gl_util.logbase2(depth - 2 * border) < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexImage3D(depth or border)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexImage3D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexImage3D(type)");
      return;
    }
    CC.gl_tex_image_3d(target, level, comp, width, height, depth, border, format, size, pixels);
  }

  /**
   * GLvoid glGetTexImage (GLenum target, GLint level, GLenum format, GLenum type, GLvoid *pixels)
   */
  public void glGetTexImage(int target, int level, int format, int type, Object pixels) {
    switch (target) {
      case GL_TEXTURE_1D:
      case GL_TEXTURE_2D:
      case GL_TEXTURE_3D:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexImage(target)");
        return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glGetTexImage(level)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glGetTexImage(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glGetTexImage(type)");
      return;
    }
    Context.gl_get_tex_image(target, level, format, size, pixels);
  }

  /** GLvoid glGenTextures (GLsizei n, GLuint *textures) */
  public void glGenTextures(int n, int textures[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glGenTextures");
      return;
    }
    if (n < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glGenTextures(n)");
      return;
    }
    Context.gl_gen_textures(n, textures);
  }

  /** GLvoid glDeleteTextures (GLsizei n, GLuint *textures) */
  public void glDeleteTextures(int n, int textures[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glDeleteTextures");
      return;
    }
    Context.gl_delete_textures(n, textures);
  }

  /** GLvoid glBindTexture (GLenum target, GLuint texture) */
  public void glBindTexture(int target, int texture) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glBindTexture");
      return;
    }
    switch (target) {
      case GL_TEXTURE_1D:
      case GL_TEXTURE_2D:
      case GL_TEXTURE_3D:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glBindTexture(target)");
        return;
    }
    CC.gl_bind_texture(target, texture);
  }

  /**
   * GLvoid glPrioritizeTextures (GLsizei n, const GLuint *textures, const GLclampf *priorities)
   */
  public void glPrioritizeTextures(int n, int textures[], float priorities[]) {}

  /**
   * GLboolean glAreTexturesResident (GLsizei n, const GLuint *textures, GLboolean *residences)
   */
  public boolean glAreTexturesResident(int n, int textures[], boolean residences[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glAreTexturesResident");
      return false;
    }
    if (n < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glAreTexturesResident(n)");
      return false;
    }
    boolean allresidence = true;
    for (int i = 0; i < n; i++) {
      residences[i] = Context.gl_is_texture(textures[i]);
      allresidence |= residences[i];
    }
    return allresidence;
  }

  /** GLboolean glIsTexture (GLuint texture) */
  public boolean glIsTexture(int texture) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glIsTexture");
      return false;
    }
    return Context.gl_is_texture(texture);
  }

  /**
   * GLvoid glTexSubImage1D (GLenum target, GLint level, GLint xoffset, GLsizei width, GLenum
   * format, GLenum type, const GLvoid *pixels)
   */
  public void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type,
      Object pixels) {
    if (target != GL_TEXTURE_1D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage1D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage1D(level)");
      return;
    }
    if (width < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage1D(width)");
      return;
    }
    int b = Context.Texture.Current1D.Image[level].Border;
    if (xoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage1D(xoffset)");
      return;
    }
    int w = Context.Texture.Current1D.Image[level].Width;
    if (xoffset + width > w + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage1D(xoffset+width)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexSubImage1D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage1D(type)");
      return;
    }
    CC.gl_tex_sub_image_1d(target, level, xoffset + b, width + xoffset + b, format, size, pixels);
  }

  /**
   * GLvoid glTexSubImage2D (GLenum target, GLint level, GLint xoffset, GLint yoffset, GLsizei
   * width, GLsizei height, GLenum format, GLenum type, const GLvoid *pixels)
   */
  public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int type, Object pixels) {
    if (target != GL_TEXTURE_2D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage2D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(level)");
      return;
    }
    if (width < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(width)");
      return;
    }
    if (height < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(height)");
      return;
    }
    int b = Context.Texture.Current2D.Image[level].Border;
    if (xoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(xoffset)");
      return;
    }
    if (yoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(yoffset)");
      return;
    }
    int w = Context.Texture.Current2D.Image[level].Width;
    if (xoffset + width > w + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(xoffset+width)");
      return;
    }
    int h = Context.Texture.Current2D.Image[level].Height;
    if (yoffset + height > h + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage2D(yoffset+height)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexSubImage2D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage2D(type)");
      return;
    }
    CC.gl_tex_sub_image_2d(target, level, xoffset + b, yoffset + b, width + xoffset + b,
        height + yoffset + b, format, size, pixels);
  }

  /**
   * GLvoid glTexSubImage3D (GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint
   * zoffset, GLsizei width, GLsizei height, GLsizei depth, GLenum format, GLenum type, const GLvoid
   * *pixels)
   */
  public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int type, Object pixels) {
    if (target != GL_TEXTURE_3D) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage3D(target)");
      return;
    }
    if (level < 0 || level >= gl_context.MAX_TEXTURE_LEVELS) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(level)");
      return;
    }
    if (width < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(width)");
      return;
    }
    if (height < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(height)");
      return;
    }
    if (depth < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(depth)");
      return;
    }
    int b = Context.Texture.Current3D.Image[level].Border;
    if (xoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(xoffset)");
      return;
    }
    if (yoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(yoffset)");
      return;
    }
    if (zoffset < -b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(zoffset)");
      return;
    }
    int w = Context.Texture.Current3D.Image[level].Width;
    if (xoffset + width > w + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(xoffset+width)");
      return;
    }
    int h = Context.Texture.Current3D.Image[level].Height;
    if (yoffset + height > h + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(yoffset+height)");
      return;
    }
    int d = Context.Texture.Current3D.Image[level].Depth;
    if (zoffset + depth > d + b) {
      CC.gl_error(GL_INVALID_VALUE, "glTexSubImage3D(zoffset+depth)");
      return;
    }
    switch (format) {
      case GL_COLOR_INDEX:
      case GL_RED:
      case GL_GREEN:
      case GL_BLUE:
      case GL_ALPHA:
      case GL_RGB:
      case GL_RGBA:
      case GL_LUMINANCE:
      case GL_LUMINANCE_ALPHA:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glTexSubImage3D(format)");
        return;
    }
    int size = size_of(type);
    if (size < 0) {
      CC.gl_error(GL_INVALID_ENUM, "glTexSubImage3D(type)");
      return;
    }
    CC.gl_tex_sub_image_3d(target, level, xoffset + b, yoffset + b, zoffset + b,
        width + xoffset + b, height + yoffset + b, depth + zoffset + b, format, size, pixels);
  }

  /**
   * GLvoid glCopyTexImage1D (GLenum target, GLint level, GLenum internalformat, GLint x, GLint y,
   * GLsizei width, GLint border)
   */
  public void glCopyTexImage1D(int target, int level, int internalformat, int x, int y, int width,
      int border) {}

  /**
   * GLvoid glCopyTexImage2D (GLenum target, GLint level, GLenum internalformat, GLint x, GLint y,
   * GLsizei width, GLsizei height, GLint border)
   */
  public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width,
      int height, int border) {}

  /**
   * GLvoid glCopyTexSubImage1D (GLenum target, GLint level, GLint xoffset, GLint x, GLint y,
   * GLsizei width)
   */
  public void glCopyTexSubImage1D(int target, int level, int xoffset, int yoffset, int x, int y,
      int width) {}

  /**
   * GLvoid glCopyTexSubImage2D (GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint x,
   * GLint y, GLsizei width, GLsizei height)
   */
  public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y,
      int width, int height) {}

  /**
   * GLvoid glCopyTexSubImage3D (GLenum target, GLint level, GLint xoffset, GLint yoffset, GLint
   * zoffset, GLint x, GLint y, GLsizei width, GLsizei height)
   */
  public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int x, int y, int width, int height) {}

  /**
   * Evaluators
   */

  /**
   * GLvoid glMap1d (GLenum target, GLdouble u1, GLdouble u2, GLint stride, GLint order, const
   * GLdouble *points)
   */
  public void glMap1d(int target, double u1, double u2, int stride, int order, double points[][]) {
    float np[][];
    int i, j, dim;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMap1d");
      return;
    }
    if (u1 == u2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1d(u1 or u2)");
      return;
    }
    if (order < 1 || order > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1d(order)");
      return;
    }
    switch (target) {
      case GL_MAP1_VERTEX_4:
      case GL_MAP1_COLOR_4:
      case GL_MAP1_TEXTURE_COORD_4:
        dim = 4;
        break;
      case GL_MAP1_VERTEX_3:
      case GL_MAP1_NORMAL:
      case GL_MAP1_TEXTURE_COORD_3:
        dim = 3;
        break;
      case GL_MAP1_TEXTURE_COORD_2:
        dim = 2;
        break;
      case GL_MAP1_INDEX:
      case GL_MAP1_TEXTURE_COORD_1:
        dim = 1;
        break;
      default:
        dim = 0;
    }
    if (dim == 0) {
      CC.gl_error(GL_INVALID_ENUM, "glMap1d(target)");
      return;
    } else {
      np = new float[order][dim];
      for (i = 0; i < order; i++) {
        np[i] = new float[dim];
        for (j = 0; j < dim; j++) {
          np[i][j] = (float) points[i][j];
        }
      }
      // CC.gl_map_1 (target, (float)u1, (float)u2, stride, order, np);
      Context.gl_map_1(target, (float) u1, (float) u2, stride, order, np);
    }
    if (stride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1d(stride)");
      return;
    }
  }

  /**
   * GLvoid glMap1f (GLenum target, GLfloat u1, GLfloat u2, GLint stride, GLint order, const GLfloat
   * *points)
   */
  public void glMap1f(int target, float u1, float u2, int stride, int order, float points[][]) {
    int dim;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMap1f");
      return;
    }
    if (u1 == u2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1f(u1 or u2)");
      return;
    }
    if (order < 1 || order > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1f(order)");
      return;
    }
    switch (target) {
      case GL_MAP1_VERTEX_3:
      case GL_MAP1_VERTEX_4:
      case GL_MAP1_INDEX:
      case GL_MAP1_COLOR_4:
      case GL_MAP1_NORMAL:
      case GL_MAP1_TEXTURE_COORD_1:
      case GL_MAP1_TEXTURE_COORD_2:
      case GL_MAP1_TEXTURE_COORD_3:
      case GL_MAP1_TEXTURE_COORD_4:
        // dim = CC.gl_map_1 (target, u1, u2, stride, order, points);
        dim = Context.gl_map_1(target, u1, u2, stride, order, points);
        break;
      default:
        dim = 0;
    }
    if (dim == 0) {
      CC.gl_error(GL_INVALID_ENUM, "glMap1f(target)");
      return;
    }
    if (stride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap1f(stride)");
      return;
    }
  }

  /**
   * GLvoid glMap2d (GLenum target, GLdouble u1, GLdouble u2, GLint ustride, GLint uorder, GLdouble
   * v1, GLdouble v2, GLint vstride, GLint vorder, const GLdouble *points)
   */
  public void glMap2d(int target, double u1, double u2, int ustride, int uorder, double v1,
      double v2, int vstride, int vorder, double points[][][]) {
    float np[][][];
    int i, j, k, dim;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMap2d");
      return;
    }
    if (u1 == u2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(u1 or u2)");
      return;
    }
    if (v1 == v2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(v1 or v2)");
      return;
    }
    if (uorder < 1 || uorder > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(uorder)");
      return;
    }
    if (vorder < 1 || vorder > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(vorder)");
      return;
    }
    switch (target) {
      case GL_MAP2_VERTEX_4:
      case GL_MAP2_COLOR_4:
      case GL_MAP2_TEXTURE_COORD_4:
        dim = 4;
        break;
      case GL_MAP2_VERTEX_3:
      case GL_MAP2_NORMAL:
      case GL_MAP2_TEXTURE_COORD_3:
        dim = 3;
        break;
      case GL_MAP2_TEXTURE_COORD_2:
        dim = 2;
        break;
      case GL_MAP2_INDEX:
      case GL_MAP2_TEXTURE_COORD_1:
        dim = 1;
        break;
      default:
        dim = 0;
    }
    if (dim == 0) {
      CC.gl_error(GL_INVALID_ENUM, "glMap2d(target)");
      return;
    } else {
      np = new float[uorder][vorder][dim];
      for (i = 0; i < uorder; i++) {
        np[i] = new float[vorder][dim];
        for (j = 0; j < vorder; j++) {
          np[i][j] = new float[dim];
          for (k = 0; k < dim; k++) {
            np[i][j][k] = (float) points[i][j][k];
          }
        }
      }
      // CC.gl_map_2 (target, (float)u1, (float)u2, ustride, uorder,
      Context.gl_map_2(target, (float) u1, (float) u2, ustride, uorder, (float) v1, (float) v2,
          vstride, vorder, np);
    }
    if (ustride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(ustride)");
      return;
    }
    if (vstride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2d(vstride)");
      return;
    }
  }

  /**
   * GLvoid glMap2f (GLenum target, GLfloat u1, GLfloat u2, GLint ustride, GLint uorder, GLfloat v1,
   * GLfloat v2, GLint vstride, GLint vorder, const GLfloat *points)
   */
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, float points[][][]) {
    int dim;
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMap2f");
      return;
    }
    if (u1 == u2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(u1 or u2)");
      return;
    }
    if (v1 == v2) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(v1 or v2)");
      return;
    }
    if (uorder < 1 || uorder > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(uorder)");
      return;
    }
    if (vorder < 1 || vorder > Context.MAX_EVAL_ORDER) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(vorder)");
      return;
    }
    switch (target) {
      case GL_MAP2_VERTEX_3:
      case GL_MAP2_VERTEX_4:
      case GL_MAP2_INDEX:
      case GL_MAP2_COLOR_4:
      case GL_MAP2_NORMAL:
      case GL_MAP2_TEXTURE_COORD_1:
      case GL_MAP2_TEXTURE_COORD_2:
      case GL_MAP2_TEXTURE_COORD_3:
      case GL_MAP2_TEXTURE_COORD_4:
        // dim = CC.gl_map_2 (target, u1, u2, ustride, uorder,
        dim = Context.gl_map_2(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
        break;
      default:
        dim = 0;
    }
    if (dim == 0) {
      CC.gl_error(GL_INVALID_ENUM, "glMap2f(target)");
      return;
    }
    if (ustride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(ustride)");
      return;
    }
    if (vstride < dim) {
      CC.gl_error(GL_INVALID_VALUE, "glMap2f(vstride)");
      return;
    }
  }

  /** GLvoid glEvalCoord1d (GLdouble u) */
  public void glEvalCoord1d(double u) {
    CC.gl_eval_coord_1((float) u);
  }

  /** GLvoid glEvalCoord1f (GLfloat u) */
  public void glEvalCoord1f(float u) {
    CC.gl_eval_coord_1(u);
  }

  /** GLvoid glEvalCoord1dv (const GLdouble *u) */
  public void glEvalCoord1dv(double u[]) {
    CC.gl_eval_coord_1((float) u[0]);
  }

  /** GLvoid glEvalCoord1fv (const GLfloat *u) */
  public void glEvalCoord1fv(float u[]) {
    CC.gl_eval_coord_1(u[0]);
  }

  /** GLvoid glEvalCoord2d (GLdouble u, GLdouble v) */
  public void glEvalCoord2d(double u, double v) {
    CC.gl_eval_coord_2((float) u, (float) v);
  }

  /** GLvoid glEvalCoord2f (GLfloat u, GLfloat v) */
  public void glEvalCoord2f(float u, float v) {
    CC.gl_eval_coord_2(u, v);
  }

  /** GLvoid glEvalCoord2dv (const GLdouble *u) */
  public void glEvalCoord2dv(double u[]) {
    CC.gl_eval_coord_2((float) u[0], (float) u[1]);
  }

  /** GLvoid glEvalCoord2fv (const GLfloat *u) */
  public void glEvalCoord2fv(float u[]) {
    CC.gl_eval_coord_2(u[0], u[1]);
  }

  /** GLvoid glMapGrid1d (GLint un, GLdouble u1, GLdouble u2) */
  public void glMapGrid1d(int un, double u1, double u2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMapGrid1d");
      return;
    }
    if (un < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid1d(un)");
      return;
    }
    // CC.gl_map_grid_1 (un, (float)u1, (float)u2);
    Context.gl_map_grid_1(un, (float) u1, (float) u2);
  }

  /** GLvoid glMapGrid1f (GLint un, GLfloat u1, GLfloat u2) */
  public void glMapGrid1f(int un, float u1, float u2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMapGrid1f");
      return;
    }
    if (un < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid1f(un)");
      return;
    }
    // CC.gl_map_grid_1 (un, u1, u2);
    Context.gl_map_grid_1(un, u1, u2);
  }

  /**
   * GLvoid glMapGrid2d (GLint un, GLdouble u1, GLdouble u2, GLint vn, GLdouble v1, GLdouble v2)
   */
  public void glMapGrid2d(int un, double u1, double u2, int vn, double v1, double v2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMapGrid2d");
      return;
    }
    if (un < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid2d(un)");
      return;
    }
    if (vn < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid2d(vn)");
      return;
    }
    // CC.gl_map_grid_2 (un, (float)u1, (float)u2,
    Context.gl_map_grid_2(un, (float) u1, (float) u2, vn, (float) v1, (float) v2);
  }

  /**
   * GLvoid glMapGrid2f (GLint un, GLfloat u1, GLfloat u2, GLint vn, GLfloat v1, GLfloat v2)
   */
  public void glMapGrid2f(int un, float u1, float u2, int vn, float v1, float v2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glMapGrid2f");
      return;
    }
    if (un < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid2f(un)");
      return;
    }
    if (vn < 1) {
      CC.gl_error(GL_INVALID_VALUE, "glMapGrid2f(vn)");
      return;
    }
    // CC.gl_map_grid_2 (un, u1, u2, vn, v1, v2);
    Context.gl_map_grid_2(un, u1, u2, vn, v1, v2);
  }

  /** GLvoid glEvalPoint1 (GLint i) */
  public void glEvalPoint1(int i) {
    // CC.gl_eval_point_1 (i);
    Context.gl_eval_point_1(i);
  }

  /** GLvoid glEvalPoint2 (GLint i, GLint j) */
  public void glEvalPoint2(int i, int j) {
    // CC.gl_eval_point_2 (i, j);
    Context.gl_eval_point_2(i, j);
  }

  /** GLvoid glEvalMesh1 (GLenum mode, GLint p1, GLint p2) */
  public void glEvalMesh1(int mode, int p1, int p2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glEvalMesh1");
      return;
    }
    switch (mode) {
      case GL_POINT:
      case GL_LINE:
        CC.gl_eval_mesh_1(mode, p1, p2);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glEvalMesh1(mode)");
    }
  }

  /**
   * GLvoid glEvalMesh2 (GLenum mode, GLint p1, GLint p2, GLint q1, GLint q2 )
   */
  public void glEvalMesh2(int mode, int p1, int p2, int q1, int q2) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glEvalMesh2");
      return;
    }
    switch (mode) {
      case GL_POINT:
      case GL_LINE:
      case GL_FILL:
        CC.gl_eval_mesh_2(mode, p1, p2, q1, q2);
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glEvalMesh2(mode)");
    }
  }

  /**
   * Selection and Feedback
   */

  /** GLvoid glFeedbackBuffer (GLsizei size, GLenum type, GLfloat *buffer) */
  public void glFeedbackBuffer(int size, int type, float buffer[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glFeedbackBuffer");
      return;
    }
    if (CC.RenderMode == GL_FEEDBACK) {
      CC.gl_error(GL_INVALID_OPERATION, "glFeedbackBuffer");
      return;
    }
    if (size < 0) {
      CC.gl_error(GL_INVALID_VALUE, "glFeedbackBuffer(size)");
      return;
    }
    switch (type) {
      case GL_2D:
      case GL_3D:
      case GL_3D_COLOR:
      case GL_3D_COLOR_TEXTURE:
      case GL_4D_COLOR_TEXTURE:
        break;
      default:
        CC.gl_error(GL_INVALID_ENUM, "glFeedbackBuffer(type)");
    }
    CC.gl_feedback_buffer(size, type, buffer);
  }

  /** GLvoid glPassThrough (GLfloat token) */
  public void glPassThrough(float token) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPassThrough");
      return;
    }
    CC.gl_pass_through(token);
  }

  /** GLvoid glSelectBuffer (GLsizei size, GLuint *buffer) */
  public void glSelectBuffer(int size, int buffer[]) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glSelectBuffer");
      return;
    }
    if (CC.RenderMode == GL_SELECT) {
      CC.gl_error(GL_INVALID_OPERATION, "glSelectBuffer");
      return;
    }
    CC.gl_select_buffer(size, buffer);
  }

  /** GLvoid glInitNames (GLvoid) */
  public void glInitNames() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glInitNames");
      return;
    }
    CC.gl_init_names();
  }

  /** GLvoid glLoadName (GLuint name) */
  public void glLoadName(int name) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glLoadName");
      return;
    }
    if (CC.RenderMode != GL_SELECT) {
      return;
    }
    CC.gl_load_name(name);
  }

  /** GLvoid glPushName (GLuint name) */
  public void glPushName(int name) {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPushName");
      return;
    }
    if (CC.RenderMode != GL_SELECT) {
      return;
    }
    CC.gl_push_name(name);
  }

  /** GLvoid glPopName (GLvoid) */
  public void glPopName() {
    if (CC.Mode != None) {
      CC.gl_error(GL_INVALID_OPERATION, "glPopName");
      return;
    }
    if (CC.RenderMode != GL_SELECT) {
      return;
    }
    CC.gl_pop_name();
  }

  /**
   * GLX Functions Java is not X-Window system, the following functions are just like GLX functions
   * not the same.
   */


  /* ********************************************************************** */

  /* **************************** CONSTANTS ******************************* */

  /* ********************************************************************** */

  /* Constant of jGL */
  /* Null values */
  public static final int None = 0;

  /* Boolean values */
  public static final boolean GL_FALSE = false;
  public static final boolean GL_TRUE = true;

  /* Data types */
  public static final int GL_BYTE = 0x1400;
  public static final int GL_UNSIGNED_BYTE = 0x1401;
  public static final int GL_SHORT = 0x1402;
  public static final int GL_UNSIGNED_SHORT = 0x1403;
  public static final int GL_INT = 0x1404;
  public static final int GL_UNSIGNED_INT = 0x1405;
  public static final int GL_FLOAT = 0x1406;
  public static final int GL_2_BYTES = 0x1407;
  public static final int GL_3_BYTES = 0x1408;
  public static final int GL_4_BYTES = 0x1409;

  /* Primitives */
  public static final int GL_POINTS = 0x0001;
  public static final int GL_LINES = 0x0002;
  public static final int GL_LINE_STRIP = 0x0003;
  public static final int GL_LINE_LOOP = 0x0004;
  public static final int GL_TRIANGLES = 0x0005;
  public static final int GL_TRIANGLE_STRIP = 0x0006;
  public static final int GL_TRIANGLE_FAN = 0x0007;
  public static final int GL_QUADS = 0x0008;
  public static final int GL_QUAD_STRIP = 0x0009;
  public static final int GL_POLYGON = 0x000A;
  public static final int GL_EDGE_FLAG = 0x0B43;

  /* Matrix Mode */
  public static final int GL_MATRIX_MODE = 0x0BA0;
  public static final int GL_MODELVIEW = 0x1700;
  public static final int GL_PROJECTION = 0x1701;
  public static final int GL_TEXTURE = 0x1702;

  /* Points */
  public static final int GL_POINT_SMOOTH = 0x0B10;
  public static final int GL_POINT_SIZE = 0x0B11;
  public static final int GL_POINT_SIZE_GRANULARITY = 0x0B13;
  public static final int GL_POINT_SIZE_RANGE = 0x0B12;

  /* Lines */
  public static final int GL_LINE_SMOOTH = 0x0B20;
  public static final int GL_LINE_STIPPLE = 0x0B24;
  public static final int GL_LINE_STIPPLE_PATTERN = 0x0B25;
  public static final int GL_LINE_STIPPLE_REPEAT = 0x0B26;
  public static final int GL_LINE_WIDTH = 0x0B21;
  public static final int GL_LINE_WIDTH_GRANULARITY = 0x0B23;
  public static final int GL_LINE_WIDTH_RANGE = 0x0B22;

  /* Polygons */
  public static final int GL_POINT = 0x1B00;
  public static final int GL_LINE = 0x1B01;
  public static final int GL_FILL = 0x1B02;
  public static final int GL_CCW = 0x0901;
  public static final int GL_CW = 0x0900;
  public static final int GL_FRONT = 0x0404;
  public static final int GL_BACK = 0x0405;
  public static final int GL_CULL_FACE = 0x0B44;
  public static final int GL_CULL_FACE_MODE = 0x0B45;
  public static final int GL_POLYGON_SMOOTH = 0x0B41;
  public static final int GL_POLYGON_STIPPLE = 0x0B42;
  public static final int GL_FRONT_FACE = 0x0B46;
  public static final int GL_POLYGON_MODE = 0x0B40;

  /* Display Lists */
  public static final int GL_COMPILE = 0x1300;
  public static final int GL_COMPILE_AND_EXECUTE = 0x1301;
  public static final int GL_LIST_BASE = 0x0B32;
  public static final int GL_LIST_INDEX = 0x0B33;
  public static final int GL_LIST_MODE = 0x0B30;

  /* Depth buffer */
  public static final int GL_NEVER = 0x0200;
  public static final int GL_LESS = 0x0201;
  public static final int GL_GEQUAL = 0x0206;
  public static final int GL_LEQUAL = 0x0203;
  public static final int GL_GREATER = 0x0204;
  public static final int GL_NOTEQUAL = 0x0205;
  public static final int GL_EQUAL = 0x0202;
  public static final int GL_ALWAYS = 0x0207;
  public static final int GL_DEPTH_TEST = 0x0B71;
  public static final int GL_DEPTH_BITS = 0x0D56;
  public static final int GL_DEPTH_CLEAR_VALUE = 0x0B73;
  public static final int GL_DEPTH_FUNC = 0x0B74;
  public static final int GL_DEPTH_RANGE = 0x0B70;
  public static final int GL_DEPTH_WRITEMASK = 0x0B72;
  public static final int GL_DEPTH_COMPONENT = 0x1902;

  /* Lighting */
  public static final int GL_LIGHTING = 0x0B50;
  public static final int GL_LIGHT0 = 0x4000;
  public static final int GL_LIGHT1 = 0x4001;
  public static final int GL_LIGHT2 = 0x4002;
  public static final int GL_LIGHT3 = 0x4003;
  public static final int GL_LIGHT4 = 0x4004;
  public static final int GL_LIGHT5 = 0x4005;
  public static final int GL_LIGHT6 = 0x4006;
  public static final int GL_LIGHT7 = 0x4007;
  public static final int GL_SPOT_EXPONENT = 0x1205;
  public static final int GL_SPOT_CUTOFF = 0x1206;
  public static final int GL_CONSTANT_ATTENUATION = 0x1207;
  public static final int GL_LINEAR_ATTENUATION = 0x1208;
  public static final int GL_QUADRATIC_ATTENUATION = 0x1209;
  public static final int GL_AMBIENT = 0x1200;
  public static final int GL_DIFFUSE = 0x1201;
  public static final int GL_SPECULAR = 0x1202;
  public static final int GL_SHININESS = 0x1601;
  public static final int GL_EMISSION = 0x1600;
  public static final int GL_POSITION = 0x1203;
  public static final int GL_SPOT_DIRECTION = 0x1204;
  public static final int GL_AMBIENT_AND_DIFFUSE = 0x1602;
  public static final int GL_COLOR_INDEXES = 0x1603;
  public static final int GL_LIGHT_MODEL_TWO_SIDE = 0x0B52;
  public static final int GL_LIGHT_MODEL_LOCAL_VIEWER = 0x0B51;
  public static final int GL_LIGHT_MODEL_AMBIENT = 0x0B53;
  public static final int GL_FRONT_AND_BACK = 0x0408;
  public static final int GL_SHADE_MODEL = 0x0B54;
  public static final int GL_FLAT = 0x1D00;
  public static final int GL_SMOOTH = 0x1D01;
  public static final int GL_COLOR_MATERIAL = 0x0B57;
  public static final int GL_COLOR_MATERIAL_FACE = 0x0B55;
  public static final int GL_COLOR_MATERIAL_PARAMETER = 0x0B56;
  public static final int GL_NORMALIZE = 0x0BA1;

  /* User clipping planes */
  public static final int GL_CLIP_PLANE0 = 0x3000;
  public static final int GL_CLIP_PLANE1 = 0x3001;
  public static final int GL_CLIP_PLANE2 = 0x3002;
  public static final int GL_CLIP_PLANE3 = 0x3003;
  public static final int GL_CLIP_PLANE4 = 0x3004;
  public static final int GL_CLIP_PLANE5 = 0x3005;

  /* Accumulation buffer */
  public static final int GL_ACCUM_RED_BITS = 0x0D58;
  public static final int GL_ACCUM_GREEN_BITS = 0x0D59;
  public static final int GL_ACCUM_BLUE_BITS = 0x0D5A;
  public static final int GL_ACCUM_ALPHA_BITS = 0x0D5B;
  public static final int GL_ACCUM_CLEAR_VALUE = 0x0B80;
  public static final int GL_ACCUM = 0x0100;
  public static final int GL_ADD = 0x0104;
  public static final int GL_LOAD = 0x0101;
  public static final int GL_MULT = 0x0103;
  public static final int GL_RETURN = 0x0102;

  /* Alpha testing */
  public static final int GL_ALPHA_TEST = 0x0BC0;
  public static final int GL_ALPHA_TEST_REF = 0x0BC2;
  public static final int GL_ALPHA_TEST_FUNC = 0x0BC1;

  /* Blending */
  public static final int GL_BLEND = 0x0BE2;
  public static final int GL_BLEND_SRC = 0x0BE1;
  public static final int GL_BLEND_DST = 0x0BE0;
  public static final int GL_ZERO = 0;
  public static final int GL_ONE = 1;
  public static final int GL_SRC_COLOR = 0x0300;
  public static final int GL_ONE_MINUS_SRC_COLOR = 0x0301;
  public static final int GL_DST_COLOR = 0x0306;
  public static final int GL_ONE_MINUS_DST_COLOR = 0x0307;
  public static final int GL_SRC_ALPHA = 0x0302;
  public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
  public static final int GL_DST_ALPHA = 0x0304;
  public static final int GL_ONE_MINUS_DST_ALPHA = 0x0305;
  public static final int GL_SRC_ALPHA_SATURATE = 0x0308;

  /* Render Mode */
  public static final int GL_FEEDBACK = 0x1C01;
  public static final int GL_RENDER = 0x1C00;
  public static final int GL_SELECT = 0x1C02;

  /* Feedback */
  public static final int GL_2D = 0x0600;
  public static final int GL_3D = 0x0601;
  public static final int GL_3D_COLOR = 0x0602;
  public static final int GL_3D_COLOR_TEXTURE = 0x0603;
  public static final int GL_4D_COLOR_TEXTURE = 0x0604;
  public static final int GL_POINT_TOKEN = 0x0701;
  public static final int GL_LINE_TOKEN = 0x0702;
  public static final int GL_LINE_RESET_TOKEN = 0x0707;
  public static final int GL_POLYGON_TOKEN = 0x0703;
  public static final int GL_BITMAP_TOKEN = 0x0704;
  public static final int GL_DRAW_PIXEL_TOKEN = 0x0705;
  public static final int GL_COPY_PIXEL_TOKEN = 0x0706;
  public static final int GL_PASS_THROUGH_TOKEN = 0x0700;

  /* Fog */
  public static final int GL_FOG = 0x0B60;
  public static final int GL_FOG_MODE = 0x0B65;
  public static final int GL_FOG_DENSITY = 0x0B62;
  public static final int GL_FOG_COLOR = 0x0B66;
  public static final int GL_FOG_INDEX = 0x0B61;
  public static final int GL_FOG_START = 0x0B63;
  public static final int GL_FOG_END = 0x0B64;
  public static final int GL_LINEAR = 0x2601;
  public static final int GL_EXP = 0x0800;
  public static final int GL_EXP2 = 0x0801;

  /* Logic Ops */
  public static final int GL_LOGIC_OP = 0x0BF1;
  public static final int GL_LOGIC_OP_MODE = 0x0BF0;
  public static final int GL_CLEAR = 0x1500;
  public static final int GL_SET = 0x150F;
  public static final int GL_COPY = 0x1503;
  public static final int GL_COPY_INVERTED = 0x150C;
  public static final int GL_NOOP = 0x1505;
  public static final int GL_INVERT = 0x150A;
  public static final int GL_AND = 0x1501;
  public static final int GL_NAND = 0x150E;
  public static final int GL_OR = 0x1507;
  public static final int GL_NOR = 0x1508;
  public static final int GL_XOR = 0x1506;
  public static final int GL_EQUIV = 0x1509;
  public static final int GL_AND_REVERSE = 0x1502;
  public static final int GL_AND_INVERTED = 0x1504;
  public static final int GL_OR_REVERSE = 0x150B;
  public static final int GL_OR_INVERTED = 0x150D;

  /* Stencil */
  public static final int GL_STENCIL_TEST = 0x0B90;
  public static final int GL_STENCIL_WRITEMASK = 0x0B98;
  public static final int GL_STENCIL_BITS = 0x0D57;
  public static final int GL_STENCIL_FUNC = 0x0B92;
  public static final int GL_STENCIL_VALUE_MASK = 0x0B93;
  public static final int GL_STENCIL_REF = 0x0B97;
  public static final int GL_STENCIL_FAIL = 0x0B94;
  public static final int GL_STENCIL_PASS_DEPTH_PASS = 0x0B96;
  public static final int GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95;
  public static final int GL_STENCIL_CLEAR_VALUE = 0x0B91;
  public static final int GL_STENCIL_INDEX = 0x1901;
  public static final int GL_KEEP = 0x1E00;
  public static final int GL_REPLACE = 0x1E01;
  public static final int GL_INCR = 0x1E02;
  public static final int GL_DECR = 0x1E03;

  /* Buffers; Pixel Drawing/Reading */
  public static final int GL_NONE = 0;
  public static final int GL_LEFT = 0x0406;
  public static final int GL_RIGHT = 0x0407;
  /* public static final int GL_FRONT = 0x0404; */
  /* public static final int GL_BACK = 0x0405; */
  /* public static final int GL_FRONT_AND_BACK = 0x0408; */
  public static final int GL_FRONT_LEFT = 0x0400;
  public static final int GL_FRONT_RIGHT = 0x0401;
  public static final int GL_BACK_LEFT = 0x0402;
  public static final int GL_BACK_RIGHT = 0x0403;
  public static final int GL_AUX0 = 0x0409;
  public static final int GL_AUX1 = 0x040A;
  public static final int GL_AUX2 = 0x040B;
  public static final int GL_AUX3 = 0x040C;
  public static final int GL_COLOR_INDEX = 0x1900;
  public static final int GL_RED = 0x1903;
  public static final int GL_GREEN = 0x1904;
  public static final int GL_BLUE = 0x1905;
  public static final int GL_ALPHA = 0x1906;
  public static final int GL_LUMINANCE = 0x1909;
  public static final int GL_LUMINANCE_ALPHA = 0x190A;
  public static final int GL_ALPHA_BITS = 0x0D55;
  public static final int GL_RED_BITS = 0x0D52;
  public static final int GL_GREEN_BITS = 0x0D53;
  public static final int GL_BLUE_BITS = 0x0D54;
  public static final int GL_INDEX_BITS = 0x0D51;
  public static final int GL_SUBPIXEL_BITS = 0x0D50;
  public static final int GL_AUX_BUFFERS = 0x0C00;
  public static final int GL_READ_BUFFER = 0x0C02;
  public static final int GL_DRAW_BUFFER = 0x0C01;
  public static final int GL_DOUBLEBUFFER = 0x0C32;
  public static final int GL_STEREO = 0x0C33;
  public static final int GL_BITMAP = 0x1A00;
  public static final int GL_COLOR = 0x1800;
  public static final int GL_DEPTH = 0x1801;
  public static final int GL_STENCIL = 0x1802;
  public static final int GL_DITHER = 0x0BD0;
  public static final int GL_RGB = 0x1907;
  public static final int GL_RGBA = 0x1908;

  /* Implementation limits */
  public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 0x0D36;
  public static final int GL_MAX_PROJECTION_STACK_DEPTH = 0x0D38;
  public static final int GL_MAX_TEXTURE_STACK_DEPTH = 0x0D39;
  public static final int GL_MAX_ATTRIB_STACK_DEPTH = 0x0D35;
  public static final int GL_MAX_NAME_STACK_DEPTH = 0x0D37;
  public static final int GL_MAX_LIST_NESTING = 0x0B31;
  public static final int GL_MAX_LIGHTS = 0x0D31;
  public static final int GL_MAX_CLIP_PLANES = 0x0D32;
  public static final int GL_MAX_VIEWPORT_DIMS = 0x0D3A;
  public static final int GL_MAX_PIXEL_MAP_TABLE = 0x0D34;
  public static final int GL_MAX_EVAL_ORDER = 0x0D30;
  public static final int GL_MAX_TEXTURE_SIZE = 0x0D33;
  public static final int GL_MAX_3D_TEXTURE_SIZE = 0x8073;

  /* Gets */
  public static final int GL_ATTRIB_STACK_DEPTH = 0x0BB0;
  public static final int GL_COLOR_CLEAR_VALUE = 0x0C22;
  public static final int GL_COLOR_WRITEMASK = 0x0C23;
  public static final int GL_CURRENT_INDEX = 0x0B01;
  public static final int GL_CURRENT_COLOR = 0x0B00;
  public static final int GL_CURRENT_NORMAL = 0x0B02;
  public static final int GL_CURRENT_RASTER_COLOR = 0x0B04;
  public static final int GL_CURRENT_RASTER_DISTANCE = 0x0B09;
  public static final int GL_CURRENT_RASTER_INDEX = 0x0B05;
  public static final int GL_CURRENT_RASTER_POSITION = 0x0B07;
  public static final int GL_CURRENT_RASTER_TEXTURE_COORDS = 0x0B06;
  public static final int GL_CURRENT_RASTER_POSITION_VALID = 0x0B08;
  public static final int GL_CURRENT_TEXTURE_COORDS = 0x0B03;
  public static final int GL_INDEX_CLEAR_VALUE = 0x0C20;
  public static final int GL_INDEX_MODE = 0x0C30;
  public static final int GL_INDEX_WRITEMASK = 0x0C21;
  public static final int GL_MODELVIEW_MATRIX = 0x0BA6;
  public static final int GL_MODELVIEW_STACK_DEPTH = 0x0BA3;
  public static final int GL_NAME_STACK_DEPTH = 0x0D70;
  public static final int GL_PROJECTION_MATRIX = 0x0BA7;
  public static final int GL_PROJECTION_STACK_DEPTH = 0x0BA4;
  public static final int GL_RENDER_MODE = 0x0C40;
  public static final int GL_RGBA_MODE = 0x0C31;
  public static final int GL_TEXTURE_MATRIX = 0x0BA8;
  public static final int GL_TEXTURE_STACK_DEPTH = 0x0BA5;
  public static final int GL_VIEWPORT = 0x0BA2;

  /* Evaluators */
  public static final int GL_AUTO_NORMAL = 0x0D80;
  public static final int GL_MAP1_COLOR_4 = 0x0D90;
  public static final int GL_MAP1_GRID_DOMAIN = 0x0DD0;
  public static final int GL_MAP1_GRID_SEGMENTS = 0x0DD1;
  public static final int GL_MAP1_INDEX = 0x0D91;
  public static final int GL_MAP1_NORMAL = 0x0D92;
  public static final int GL_MAP1_TEXTURE_COORD_1 = 0x0D93;
  public static final int GL_MAP1_TEXTURE_COORD_2 = 0x0D94;
  public static final int GL_MAP1_TEXTURE_COORD_3 = 0x0D95;
  public static final int GL_MAP1_TEXTURE_COORD_4 = 0x0D96;
  public static final int GL_MAP1_VERTEX_3 = 0x0D97;
  public static final int GL_MAP1_VERTEX_4 = 0x0D98;
  public static final int GL_MAP2_COLOR_4 = 0x0DB0;
  public static final int GL_MAP2_GRID_DOMAIN = 0x0DD2;
  public static final int GL_MAP2_GRID_SEGMENTS = 0x0DD3;
  public static final int GL_MAP2_INDEX = 0x0DB1;
  public static final int GL_MAP2_NORMAL = 0x0DB2;
  public static final int GL_MAP2_TEXTURE_COORD_1 = 0x0DB3;
  public static final int GL_MAP2_TEXTURE_COORD_2 = 0x0DB4;
  public static final int GL_MAP2_TEXTURE_COORD_3 = 0x0DB5;
  public static final int GL_MAP2_TEXTURE_COORD_4 = 0x0DB6;
  public static final int GL_MAP2_VERTEX_3 = 0x0DB7;
  public static final int GL_MAP2_VERTEX_4 = 0x0DB8;
  public static final int GL_COEFF = 0x0A00;
  public static final int GL_DOMAIN = 0x0A02;
  public static final int GL_ORDER = 0x0A01;

  /* Hints */
  public static final int GL_FOG_HINT = 0x0C54;
  public static final int GL_LINE_SMOOTH_HINT = 0x0C52;
  public static final int GL_PERSPECTIVE_CORRECTION_HINT = 0x0C50;
  public static final int GL_POINT_SMOOTH_HINT = 0x0C51;
  public static final int GL_POLYGON_SMOOTH_HINT = 0x0C53;
  public static final int GL_DONT_CARE = 0x1100;
  public static final int GL_FASTEST = 0x1101;
  public static final int GL_NICEST = 0x1102;

  /* Scissor box */
  public static final int GL_SCISSOR_TEST = 0x0C11;
  public static final int GL_SCISSOR_BOX = 0x0C10;

  /* Pixel Mode / Transfer */
  public static final int GL_MAP_COLOR = 0x0D10;
  public static final int GL_MAP_STENCIL = 0x0D11;
  public static final int GL_INDEX_SHIFT = 0x0D12;
  public static final int GL_INDEX_OFFSET = 0x0D13;
  public static final int GL_RED_SCALE = 0x0D14;
  public static final int GL_RED_BIAS = 0x0D15;
  public static final int GL_GREEN_SCALE = 0x0D18;
  public static final int GL_GREEN_BIAS = 0x0D19;
  public static final int GL_BLUE_SCALE = 0x0D1A;
  public static final int GL_BLUE_BIAS = 0x0D1B;
  public static final int GL_ALPHA_SCALE = 0x0D1C;
  public static final int GL_ALPHA_BIAS = 0x0D1D;
  public static final int GL_DEPTH_SCALE = 0x0D1E;
  public static final int GL_DEPTH_BIAS = 0x0D1F;
  public static final int GL_PIXEL_MAP_S_TO_S_SIZE = 0x0CB1;
  public static final int GL_PIXEL_MAP_I_TO_I_SIZE = 0x0CB0;
  public static final int GL_PIXEL_MAP_I_TO_R_SIZE = 0x0CB2;
  public static final int GL_PIXEL_MAP_I_TO_G_SIZE = 0x0CB3;
  public static final int GL_PIXEL_MAP_I_TO_B_SIZE = 0x0CB4;
  public static final int GL_PIXEL_MAP_I_TO_A_SIZE = 0x0CB5;
  public static final int GL_PIXEL_MAP_R_TO_R_SIZE = 0x0CB6;
  public static final int GL_PIXEL_MAP_G_TO_G_SIZE = 0x0CB7;
  public static final int GL_PIXEL_MAP_B_TO_B_SIZE = 0x0CB8;
  public static final int GL_PIXEL_MAP_A_TO_A_SIZE = 0x0CB9;
  public static final int GL_PIXEL_MAP_S_TO_S = 0x0C71;
  public static final int GL_PIXEL_MAP_I_TO_I = 0x0C70;
  public static final int GL_PIXEL_MAP_I_TO_R = 0x0C72;
  public static final int GL_PIXEL_MAP_I_TO_G = 0x0C73;
  public static final int GL_PIXEL_MAP_I_TO_B = 0x0C74;
  public static final int GL_PIXEL_MAP_I_TO_A = 0x0C75;
  public static final int GL_PIXEL_MAP_R_TO_R = 0x0C76;
  public static final int GL_PIXEL_MAP_G_TO_G = 0x0C77;
  public static final int GL_PIXEL_MAP_B_TO_B = 0x0C78;
  public static final int GL_PIXEL_MAP_A_TO_A = 0x0C79;
  public static final int GL_PACK_ALIGNMENT = 0x0D05;
  public static final int GL_PACK_LSB_FIRST = 0x0D01;
  public static final int GL_PACK_ROW_LENGTH = 0x0D02;
  public static final int GL_PACK_IMAGE_HEIGHT = 0x806C;
  public static final int GL_PACK_SKIP_PIXELS = 0x0D04;
  public static final int GL_PACK_SKIP_ROWS = 0x0D03;
  public static final int GL_PACK_SKIP_IMAGES = 0x806B;
  public static final int GL_PACK_SWAP_BYTES = 0x0D00;
  public static final int GL_UNPACK_ALIGNMENT = 0x0CF5;
  public static final int GL_UNPACK_LSB_FIRST = 0x0CF1;
  public static final int GL_UNPACK_ROW_LENGTH = 0x0CF2;
  public static final int GL_UNPACK_IMAGE_HEIGHT = 0x806E;
  public static final int GL_UNPACK_SKIP_PIXELS = 0x0CF4;
  public static final int GL_UNPACK_SKIP_ROWS = 0x0CF3;
  public static final int GL_UNPACK_SKIP_IMAGES = 0x806D;
  public static final int GL_UNPACK_SWAP_BYTES = 0x0CF0;
  public static final int GL_ZOOM_X = 0x0D16;
  public static final int GL_ZOOM_Y = 0x0D17;

  /* Texture mapping */
  public static final int GL_TEXTURE_ENV = 0x2300;
  public static final int GL_TEXTURE_ENV_MODE = 0x2200;
  public static final int GL_TEXTURE_1D = 0x0DE0;
  public static final int GL_TEXTURE_2D = 0x0DE1;
  public static final int GL_TEXTURE_3D = 0x806F;
  public static final int GL_TEXTURE_WRAP_S = 0x2802;
  public static final int GL_TEXTURE_WRAP_T = 0x2803;
  public static final int GL_TEXTURE_WRAP_R = 0x8072;
  public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
  public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
  public static final int GL_TEXTURE_ENV_COLOR = 0x2201;
  public static final int GL_TEXTURE_GEN_S = 0x0C60;
  public static final int GL_TEXTURE_GEN_T = 0x0C61;
  public static final int GL_TEXTURE_GEN_R = 0x0C62;
  public static final int GL_TEXTURE_GEN_Q = 0x0C63;
  public static final int GL_TEXTURE_GEN_MODE = 0x2500;
  public static final int GL_TEXTURE_BORDER_COLOR = 0x1004;
  public static final int GL_TEXTURE_WIDTH = 0x1000;
  public static final int GL_TEXTURE_HEIGHT = 0x1001;
  public static final int GL_TEXTURE_DEPTH = 0x8071;
  public static final int GL_TEXTURE_BORDER = 0x1005;
  public static final int GL_TEXTURE_COMPONENTS = 0x1003;
  public static final int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
  public static final int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
  public static final int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
  public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
  public static final int GL_OBJECT_LINEAR = 0x2401;
  public static final int GL_OBJECT_PLANE = 0x2501;
  public static final int GL_EYE_LINEAR = 0x2400;
  public static final int GL_EYE_PLANE = 0x2502;
  public static final int GL_SPHERE_MAP = 0x2402;
  public static final int GL_DECAL = 0x2101;
  public static final int GL_MODULATE = 0x2100;
  public static final int GL_NEAREST = 0x2600;
  public static final int GL_REPEAT = 0x2901;
  public static final int GL_CLAMP = 0x2900;
  public static final int GL_S = 0x2000;
  public static final int GL_T = 0x2001;
  public static final int GL_R = 0x2002;
  public static final int GL_Q = 0x2003;

  /* Utility */
  public static final int GL_VENDOR = 0x1F00;
  public static final int GL_RENDERER = 0x1F01;
  public static final int GL_VERSION = 0x1F02;
  public static final int GL_EXTENSIONS = 0x1F03;

  /* Errors */
  /**
   * <i>enum</i> argument out of range.
   */
  public static final int GL_INVALID_ENUM = 0x0500;

  /**
   * Numeric argument out of range.
   */
  public static final int GL_INVALID_VALUE = 0x0501;

  /**
   * Operation illegal in current state.
   */
  public static final int GL_INVALID_OPERATION = 0x0502;

  /**
   * Command would cause a stack overflow.
   */
  public static final int GL_STACK_OVERFLOW = 0x0503;

  /**
   * Command would cause a stack underflow.
   */
  public static final int GL_STACK_UNDERFLOW = 0x0504;

  /**
   * Not enough memory left to execute command.
   */
  public static final int GL_OUT_OF_MEMORY = 0x0505;

  /**
   * The specified table is too large.
   */
  public static final int GL_TABLE_TOO_LARGE = 0x0506;

  /* Extensions */
  public static final int GL_CONSTANT_COLOR_EXT = 0x8001;
  public static final int GL_ONE_MINUS_CONSTANT_COLOR_EXT = 0x8002;
  public static final int GL_CONSTANT_ALPHA_EXT = 0x8003;
  public static final int GL_ONE_MINUS_CONSTANT_ALPHA_EXT = 0x8004;
  public static final int GL_BLEND_EQUATION_EXT = 0x8009;
  public static final int GL_MIN_EXT = 0x8007;
  public static final int GL_MAX_EXT = 0x8008;
  public static final int GL_FUNC_ADD_EXT = 0x8006;
  public static final int GL_FUNC_SUBTRACT_EXT = 0x800A;
  public static final int GL_FUNC_REVERSE_SUBTRACT_EXT = 0x800B;
  public static final int GL_BLEND_COLOR_EXT = 0x8005;
  public static final int GL_REPLACE_EXT = 0x8062;
  public static final int GL_POLYGON_OFFSET_EXT = 0x8037;
  public static final int GL_POLYGON_OFFSET_FACTOR_EXT = 0x8038;
  public static final int GL_POLYGON_OFFSET_BIAS_EXT = 0x8039;

  /* GL_NO_ERROR must be zero */
  public static final int GL_NO_ERROR = 0;

  /* GLbitfield */
  public static final int GL_CURRENT_BIT = 0x00000001;
  public static final int GL_POINT_BIT = 0x00000002;
  public static final int GL_LINE_BIT = 0x00000004;
  public static final int GL_POLYGON_BIT = 0x00000008;
  public static final int GL_POLYGON_STIPPLE_BIT = 0x00000010;
  public static final int GL_PIXEL_MODE_BIT = 0x00000020;
  public static final int GL_LIGHTING_BIT = 0x00000040;
  public static final int GL_FOG_BIT = 0x00000080;
  public static final int GL_DEPTH_BUFFER_BIT = 0x00000100;
  public static final int GL_ACCUM_BUFFER_BIT = 0x00000200;
  public static final int GL_STENCIL_BUFFER_BIT = 0x00000400;
  public static final int GL_VIEWPORT_BIT = 0x00000800;
  public static final int GL_TRANSFORM_BIT = 0x00001000;
  public static final int GL_ENABLE_BIT = 0x00002000;
  public static final int GL_COLOR_BUFFER_BIT = 0x00004000;
  public static final int GL_HINT_BIT = 0x00008000;
  public static final int GL_EVAL_BIT = 0x00010000;
  public static final int GL_LIST_BIT = 0x00020000;
  public static final int GL_TEXTURE_BIT = 0x00040000;
  public static final int GL_SCISSOR_BIT = 0x00080000;
  public static final int GL_ALL_ATTRIB_BIT = 0x000fffff;

  /* Constant of GLX of OpenGL */
  /* Tokens for glXChooseVisual and glXGetConfig */
  public static final int GLX_USE_GL = 1;
  public static final int GLX_BUFFER_SIZE = 2;
  public static final int GLX_LEVEL = 3;
  public static final int GLX_RGBA = 4;
  public static final int GLX_DOUBLEBUFFER = 5;
  public static final int GLX_STEREO = 6;
  public static final int GLX_AUX_BUFFERS = 7;
  public static final int GLX_RED_SIZE = 8;
  public static final int GLX_GREEN_SIZE = 9;
  public static final int GLX_BLUE_SIZE = 10;
  public static final int GLX_ALPHA_SIZE = 11;
  public static final int GLX_DEPTH_SIZE = 12;
  public static final int GLX_STENCIL_SIZE = 13;
  public static final int GLX_ACCUM_RED_SIZE = 14;
  public static final int GLX_ACCUM_GREEN_SIZE = 15;
  public static final int GLX_ACCUM_BLUE_SIZE = 16;
  public static final int GLX_ACCUM_ALPHA_SIZE = 17;
  public static final int GLX_SAMPLES_SGIS = 100000; /* SGI extension */
  public static final int GLX_SAMPLES_BUFFER_SGIS = 100001; /* SGI extension */

  /* Error codes returned by glXGetConfig */
  public static final int GLX_BAD_SCREEN = 1;
  public static final int GLX_BAD_ATTRIBUTE = 2;
  public static final int GLX_NO_EXTENSION = 3;
  public static final int GLX_BAD_VISUAL = 4;
  public static final int GLX_BAD_CONTEXT = 5;
  public static final int GLX_BAD_VALUE = 6;
  public static final int GLX_BAD_ENUM = 7;
	/* Constant of GLE */
	public static final int GL_PHONG = GL_SMOOTH + 1;

}
