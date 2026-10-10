/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.painters;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.IPanamaGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Attenuation;
import org.jzy3d.plot3d.rendering.lights.LightModel;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import panamagl.canvas.GLCanvas;
import panamagl.offscreen.FBO;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;

public class PanamaGLPainter extends AbstractPainter {
  static Logger logger = LoggerFactory.getLogger(PanamaGLPainter.class);

  protected GL gl;
  protected GLContext context;

  /**
   * Native memory for GL parameters. An automatic arena lets any thread allocate (rendering happens
   * on the toolkit thread, not on the thread building the painter) and releases memory when
   * segments are not referenced anymore.
   */
  protected Arena arena;

  /** The thread on which the GL context is current, i.e. the thread rendering the canvas. */
  protected Thread glThread;

  /** Number of renderings in progress, which may be nested (e.g. init triggering a display). */
  protected int rendering = 0;

  /** The canvas FBO bound by {@link #acquireGL()} out of rendering, to unbind at release. */
  protected FBO acquiredFBO;

  /** Java buffer given to {@link #glSelectBuffer(int, IntBuffer)}, filled by glRenderMode. */
  protected IntBuffer selectBuffer;
  protected MemorySegment selectSegment;

  /** Java buffer given to {@link #glFeedbackBuffer}, filled by glRenderMode. */
  protected FloatBuffer feedbackBuffer;
  protected MemorySegment feedbackSegment;

  public PanamaGLPainter() {
    arena = Arena.ofAuto();
  }

  public GL getGL() {
    return gl;
  }

  public void setGL(GL gl) {
    this.gl = gl;
  }

  public GLContext getContext() {
    return context;
  }

  public void setContext(GLContext context) {
    this.context = context;
  }

  public MemorySegment alloc(double[] value) {
    return arena.allocateFrom(ValueLayout.JAVA_DOUBLE, value);
  }

  public MemorySegment alloc(float[] value) {
    return arena.allocateFrom(ValueLayout.JAVA_FLOAT, value);
  }

  public MemorySegment alloc(int[] value) {
    return arena.allocateFrom(ValueLayout.JAVA_INT, value);
  }

  /** Copy the buffer content, from 0 to its capacity, to native memory (heap or direct buffer). */
  public MemorySegment alloc(FloatBuffer value) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_FLOAT, value.capacity());
    for (int i = 0; i < value.capacity(); i++) {
      segment.setAtIndex(ValueLayout.JAVA_FLOAT, i, value.get(i));
    }
    return segment;
  }

  /** Copy the buffer content, from 0 to its capacity, to native memory (heap or direct buffer). */
  public MemorySegment alloc(IntBuffer value) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT, value.capacity());
    for (int i = 0; i < value.capacity(); i++) {
      segment.setAtIndex(ValueLayout.JAVA_INT, i, value.get(i));
    }
    return segment;
  }

  /** Copy the buffer content, from 0 to its capacity, to native memory (heap or direct buffer). */
  public MemorySegment alloc(DoubleBuffer value) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_DOUBLE, value.capacity());
    for (int i = 0; i < value.capacity(); i++) {
      segment.setAtIndex(ValueLayout.JAVA_DOUBLE, i, value.get(i));
    }
    return segment;
  }

  /**
   * Return native memory holding the buffer content from its position to its limit. Direct buffers
   * are used without copy, heap buffers are copied.
   */
  public MemorySegment segment(Buffer buffer) {
    MemorySegment segment = MemorySegment.ofBuffer(buffer);

    if (segment.isNative()) {
      return segment;
    } else {
      MemorySegment copy = arena.allocate(segment.byteSize());
      copy.copyFrom(segment);
      return copy;
    }
  }

  public MemorySegment alloc(String value) {
    return arena.allocateFrom(value);
  }

  protected double[] dbl(float[] values) {
    double[] dbl = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      dbl[i] = values[i];
    }
    return dbl;
  }

  @Override
  public String glGetString(int stringID) {
    return string(gl.glGetString(stringID));
  }

  // GL GET

  @Override
  public void glGetIntegerv(int pname, int[] data, int data_offset) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT, data.length - data_offset);
    gl.glGetIntegerv(pname, segment);
    MemorySegment.copy(segment, ValueLayout.JAVA_INT, 0, data, data_offset,
        data.length - data_offset);
  }

  @Override
  public void glGetDoublev(int pname, double[] params, int params_offset) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_DOUBLE, params.length - params_offset);
    gl.glGetDoublev(pname, segment);
    MemorySegment.copy(segment, ValueLayout.JAVA_DOUBLE, 0, params, params_offset,
        params.length - params_offset);
  }

  @Override
  public void glGetFloatv(int pname, float[] data, int data_offset) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_FLOAT, data.length - data_offset);
    gl.glGetFloatv(pname, segment);
    MemorySegment.copy(segment, ValueLayout.JAVA_FLOAT, 0, data, data_offset,
        data.length - data_offset);
  }

  protected StringBuffer version() {
    return version(true);
  }

  protected StringBuffer version(boolean showExtensions) {
    StringBuffer sb = new StringBuffer();
    sb.append("GL_VENDOR     : " + glGetString(GL.GL_VENDOR) + "\n");
    sb.append("GL_VERSION    : " + glGetString(GL.GL_VERSION) + "\n");

    String ext = glGetString(GL.GL_EXTENSIONS);

    if (ext != null) {
      sb.append("GL_EXTENSIONS : " + "\n");
      if (showExtensions) {
        for (String e : ext.split(" ")) {
          sb.append("\t" + e + "\n");
        }
      } else {
        sb.append(ext.split(" ").length);
      }
    } else {
      sb.append("GL_EXTENSIONS : null\n");
    }

    return sb;
  }

  /////////////////////////////////////////////

  /**
   * PanamaGL keeps its GL context current on the thread rendering the canvas (e.g. the AWT thread
   * for Swing). The context can't be made current on another thread while the canvas is alive.
   * 
   * Out of rendering, the offscreen buffer of the canvas is bound so that GL commands (e.g.
   * picking) target the canvas framebuffer.
   * 
   * @return the GL instance if called from the rendering thread, null otherwise : callers must
   *         then defer their GL work to the rendering thread (see {@link #isGLThread()}).
   */
  @Override
  public Object acquireGL() {
    if (!isGLThread()) {
      return null;
    }

    if (!isRendering() && acquiredFBO == null) {
      FBO fbo = getCanvasFBO();
      if (fbo != null) {
        fbo.bind(gl);
        acquiredFBO = fbo;
      }
    }
    return gl;
  }

  /**
   * Unbind the offscreen buffer bound by {@link #acquireGL()}. The GL context remains current on
   * the rendering thread.
   */
  @Override
  public void releaseGL() {
    if (acquiredFBO != null && isGLThread()) {
      acquiredFBO.unbind(gl);
    }
    acquiredFBO = null;
  }

  /** The offscreen buffer in which the canvas is rendered, or null if not available. */
  protected FBO getCanvasFBO() {
    if (getCanvas() instanceof IPanamaGLCanvas) {
      GLCanvas glCanvas = ((IPanamaGLCanvas) getCanvas()).getGLCanvas();
      if (glCanvas != null && glCanvas.getOffscreenRenderer() != null) {
        return glCanvas.getOffscreenRenderer().getFBO();
      }
    }
    return null;
  }

  /** Return true while the renderer renders the canvas. */
  public boolean isRendering() {
    return rendering > 0;
  }

  /** Indicates the renderer starts rendering the canvas. Invoked by the renderer. */
  public void beginRendering() {
    rendering++;
  }

  /** Indicates the renderer ends rendering the canvas. Invoked by the renderer. */
  public void endRendering() {
    rendering = Math.max(0, rendering - 1);
  }

  /** Return true if the calling thread is the one on which the GL context is current. */
  public boolean isGLThread() {
    return glThread != null && glThread == Thread.currentThread();
  }

  public Thread getGLThread() {
    return glThread;
  }

  /** Register the thread on which the GL context is current. Invoked by the renderer. */
  public void setGLThread(Thread glThread) {
    this.glThread = glThread;
  }

  @Override
  public WindowingToolkit getWindowingToolkit() {
    String name = getCanvas() == null ? "" : getCanvas().getClass().getSimpleName();
    if (name.indexOf("Swing") >= 0) {
      return WindowingToolkit.Swing;
    } else if (name.indexOf("SWT") >= 0) {
      return WindowingToolkit.SWT;
    }
    return WindowingToolkit.UNKOWN;
  }

  @Override
  public void configureGL(Quality quality) {
    // store reference to context in painter!!
    if (getCanvas() instanceof IPanamaGLCanvas) {
      GLCanvas glcanvas = ((IPanamaGLCanvas) getCanvas()).getGLCanvas();
      setContext(glcanvas.getContext());
    }

    // Activate Depth buffer
    if (quality.isDepthActivated()) {
      gl.glEnable(GL.GL_DEPTH_TEST);
      gl.glDepthFunc(GL.GL_LEQUAL);
    } else {
      gl.glDisable(GL.GL_DEPTH_TEST);
    }

    // Blending : more beautifull with jGL without this
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    // Activate tranparency
    if (quality.isAlphaActivated()) {
      gl.glEnable(GL.GL_BLEND);
      gl.glEnable(GL.GL_ALPHA_TEST);

      if (quality.isDisableDepthBufferWhenAlpha()) {
        // Disable depth test to keeping pixels of
        // "what's behind a polygon" when drawing with alpha
        gl.glDisable(GL.GL_DEPTH_TEST);
      }
    } else {
      gl.glDisable(GL.GL_ALPHA_TEST);
    }

    // Make smooth colors for polygons (interpolate color between points)
    glShadeModel(quality.getColorModel());

    // Make smoothing setting
    if (quality.isSmoothPolygon()) {
      gl.glEnable(GL.GL_POLYGON_SMOOTH);
      gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_POLYGON_SMOOTH);

    if (quality.isSmoothLine()) {
      gl.glEnable(GL.GL_LINE_SMOOTH);
      gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_LINE_SMOOTH);

    if (quality.isSmoothPoint()) {
      gl.glEnable(GL.GL_POINT_SMOOTH);
      gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_POINT_SMOOTH);
  }

  @Override
  public int[] getViewPortAsInt() {
    int viewport[] = new int[4];
    glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
    return viewport;
  }

  @Override
  public double[] getProjectionAsDouble() {
    double projection[] = new double[16];
    glGetDoublev(GL.GL_PROJECTION_MATRIX, projection, 0);
    return projection;
  }

  @Override
  public float[] getProjectionAsFloat() {
    float projection[] = new float[16];
    glGetFloatv(GL.GL_PROJECTION_MATRIX, projection, 0);
    return projection;
  }

  @Override
  public double[] getModelViewAsDouble() {
    double modelview[] = new double[16];
    glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview, 0);
    return modelview;
  }

  @Override
  public float[] getModelViewAsFloat() {
    float modelview[] = new float[16];
    glGetFloatv(GL.GL_MODELVIEW_MATRIX, modelview, 0);
    return modelview;
  }

  /************ OPEN GL Interface **************/

  // GL MATRIX

  @Override
  public void glPushMatrix() {
    gl.glPushMatrix();
  }

  @Override
  public void glPopMatrix() {
    gl.glPopMatrix();
  }

  @Override
  public void glMatrixMode(int mode) {
    gl.glMatrixMode(mode);
  }

  @Override
  public void glLoadIdentity() {
    gl.glLoadIdentity();
  }

  @Override
  public void glScalef(float x, float y, float z) {
    gl.glScalef(x, y, z);
  }

  @Override
  public void glTranslatef(float x, float y, float z) {
    gl.glTranslatef(x, y, z);
  }

  @Override
  public void glRotatef(float angle, float x, float y, float z) {
    gl.glRotatef(angle, x, y, z);
  }

  @Override
  public void glEnable(int type) {
    gl.glEnable(type);
  }

  @Override
  public void glDisable(int type) {
    gl.glDisable(type);
  }

  // GL GEOMETRY

  @Override
  public void glPointSize(float width) {
    gl.glPointSize(width);
  }

  @Override
  public void glLineWidth(float width) {
    gl.glLineWidth(width);
  }

  @Override
  public void glBegin(int type) {
    gl.glBegin(type);
  }

  @Override
  public void glColor3f(float r, float g, float b) {
    gl.glColor3f(r, g, b);
  }

  @Override
  public void glColor4f(float r, float g, float b, float a) {
    gl.glColor4f(r, g, b, a);
  }

  @Override
  public void glVertex3f(float x, float y, float z) {
    gl.glVertex3f(x, y, z);
  }

  @Override
  public void glVertex3d(double x, double y, double z) {
    gl.glVertex3d(x, y, z);
  }

  @Override
  public void glEnd() {
    gl.glEnd();
  }

  @Override
  public void glFrontFace(int mode) {
    gl.glFrontFace(mode);
  }

  @Override
  public void glCullFace(int mode) {
    gl.glCullFace(mode);
  }

  @Override
  public void glPolygonMode(PolygonMode mode, PolygonFill fill) {
    int modeValue = polygonModeValue(mode);
    int fillValue = polygonFillValue(fill);

    glPolygonMode(modeValue, fillValue);
  }

  protected int polygonModeValue(PolygonMode mode) {
    switch (mode) {
      case FRONT:
        return GL.GL_FRONT;
      case BACK:
        return GL.GL_BACK;
      case FRONT_AND_BACK:
        return GL.GL_FRONT_AND_BACK;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
    }
  }

  protected int polygonFillValue(PolygonFill mode) {
    switch (mode) {
      case FILL:
        return GL.GL_FILL;
      case LINE:
        return GL.GL_LINE;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
    }
  }

  @Override
  public void glPolygonMode(int frontOrBack, int fill) {
    gl.glPolygonMode(frontOrBack, fill);
  }

  @Override
  public void glPolygonOffset(float factor, float units) {
    gl.glPolygonOffset(factor, units);
  }

  @Override
  public void glLineStipple(int factor, short pattern) {
    gl.glLineStipple(factor, pattern);
  }

  // GL TEXTURE

  @Override
  public void glTexCoord2f(float s, float t) {
    gl.glTexCoord2f(s, t);
  }

  @Override
  public void glTexEnvf(int target, int pname, float param) {
    gl.glTexEnvf(target, pname, param);
  }

  @Override
  public void glTexEnvi(int target, int pname, int param) {
    gl.glTexEnvi(target, pname, param);
  }

  @Override
  public void glRasterPos3f(float x, float y, float z) {
    gl.glRasterPos3f(x, y, z);
  }

  /**
   * Draw pixels of any buffer type. Direct buffers are read without copy, heap buffers are copied
   * to native memory.
   */
  @Override
  public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {
    gl.glDrawPixels(width, height, format, type, segment(pixels));
  }

  @Override
  public void glPixelZoom(float xfactor, float yfactor) {
    gl.glPixelZoom(xfactor, yfactor);
  }

  @Override
  public void glPixelStorei(int pname, int param) {
    gl.glPixelStorei(pname, param);
  }

  @Override
  public void glPixelStore(PixelStore store, int param) {
    switch (store) {
      case PACK_ALIGNMENT:
        gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, param);
        break;
      case UNPACK_ALIGNMENT:
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, param);
        break;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + store + "'");
    }
  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      byte[] bitmap, int bitmap_offset) {
    MemorySegment segment = MemorySegment.NULL;

    if (bitmap != null && bitmap.length > bitmap_offset) {
      segment = arena.allocate(bitmap.length - bitmap_offset);
      MemorySegment.copy(bitmap, bitmap_offset, segment, ValueLayout.JAVA_BYTE, 0,
          bitmap.length - bitmap_offset);
    }
    gl.glBitmap(width, height, xorig, yorig, xmove, ymove, segment);
  }

  @Override
  public void drawImage(ByteBuffer imageBuffer, int imageWidth, int imageHeight, Coord2d pixelZoom,
      Coord3d imagePosition) {
    glPixelZoom(pixelZoom.x, pixelZoom.y);
    glRasterPos3f(imagePosition.x, imagePosition.y, imagePosition.z);

    synchronized (imageBuffer) {
      glDrawPixels(imageWidth, imageHeight, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
    }
  }

  // elements of GL spec picked in JOGL GL interface
  public static final int GL_RGBA = 0x1908;
  public static final int GL_UNSIGNED_BYTE = 0x1401;

  /* ****************************** TEXT *********************************/

  /**
   * Process the given font length to further process alignement.
   *
   * Will only return a valid width for known {@link Font} (Helevetica and Times Roman).
   *
   * Getting text width of any string can be done {@link #getTextLengthInPixels(Font, String)}.
   */
  @Override
  public int glutBitmapLength(int font, String string) {
    return getTextLengthInPixels(font, string);
  }

  boolean allowAutoDetectTextLength = true;

  @Override
  public int getTextLengthInPixels(int font, String string) {
    Font fnt = Font.getById(font);

    return getTextLengthInPixels(fnt, string);
  }

  /**
   * Text length processing based on AWT {@link FontMetrics}.
   *
   * Prefers the live {@link Graphics} of the canvas when it is an AWT {@link Component}
   * (Swing backend) because it reflects the actual display's font rendering. Otherwise
   * (JavaFX, SWT, ...) falls back to a headless {@link java.awt.image.BufferedImage}
   * context that is still able to resolve the same AWT {@link FontMetrics}.
   */
  @Override
  public int getTextLengthInPixels(Font font, String string) {
    if (font == null) {
      return 0;
    }
    java.awt.Font awtFont = toAWT(font);

    Object canvas = getCanvas();
    if (canvas instanceof Component) {
      Graphics g = ((Component) canvas).getGraphics();
      if (g != null) {
        g.setFont(awtFont);
        FontMetrics fm = g.getFontMetrics();
        if (fm != null) {
          return fm.stringWidth(string);
        }
      }
    }

    // Headless fallback: a 1x1 BufferedImage is enough to obtain AWT FontMetrics that
    // matches the font the renderer will use to draw text.
    java.awt.image.BufferedImage img =
        new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();
    try {
      g.setFont(awtFont);
      FontMetrics fm = g.getFontMetrics();
      return fm.stringWidth(string);
    } finally {
      g.dispose();
    }
  }

  /**
   * Draw a string at the current raster position with the current raster color, as GLUT does : the
   * raster position is the left of the text baseline and is moved to the end of the text once
   * drawn.
   * 
   * The text is drawn with AWT in an image, hence any AWT font may be used.
   */
  @Override
  public void glutBitmapString(int font, String string) {
    Font f = Font.getById(font);
    if (f == null || string == null || string.isEmpty()) {
      return;
    }

    float[] color = new float[4];
    glGetFloatv(GL.GL_CURRENT_RASTER_COLOR, color, 0);

    java.awt.Font awtFont = toAWT(f);
    TextImage text = textImage(awtFont, string,
        new java.awt.Color(clamp(color[0]), clamp(color[1]), clamp(color[2]), clamp(color[3])));

    // Move the raster position from the baseline to the bottom of the text image
    gl.glBitmap(0, 0, 0, 0, 0, -text.descent, MemorySegment.NULL);

    gl.glDrawPixels(text.width, text.height, GL_RGBA, GL_UNSIGNED_BYTE, text.pixels);

    // Move the raster position to the end of the text on the baseline
    gl.glBitmap(0, 0, 0, 0, text.advance, text.descent, MemorySegment.NULL);
  }

  /** RGBA pixels of an image (e.g. a text drawn with AWT), bottom row first as expected by GL. */
  protected static class TextImage {
    int width;
    int height;
    int descent;
    int advance;
    MemorySegment pixels;
  }

  protected TextImage textImage(java.awt.Font font, String string, java.awt.Color color) {
    BufferedImage metricsImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D mg = metricsImage.createGraphics();
    FontMetrics fm = mg.getFontMetrics(font);
    mg.dispose();

    TextImage text = new TextImage();
    text.advance = fm.stringWidth(string);
    text.width = Math.max(1, text.advance);
    text.height = Math.max(1, fm.getAscent() + fm.getDescent());
    text.descent = fm.getDescent();

    BufferedImage image = new BufferedImage(text.width, text.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setFont(font);
    g.setColor(color);
    g.drawString(string, 0, fm.getAscent());
    g.dispose();

    text.pixels = pixels(image).pixels;
    return text;
  }

  /** Convert an AWT image to RGBA pixels, bottom row first as expected by OpenGL. */
  protected TextImage pixels(BufferedImage image) {
    TextImage out = new TextImage();
    out.width = image.getWidth();
    out.height = image.getHeight();
    out.advance = out.width;
    out.pixels = arena.allocate((long) out.width * out.height * 4);

    int[] row = new int[out.width];

    for (int y = 0; y < out.height; y++) {
      image.getRGB(0, y, out.width, 1, row, 0, out.width);

      // OpenGL reads rows from bottom to top
      long offset = (long) (out.height - 1 - y) * out.width * 4;

      for (int x = 0; x < out.width; x++) {
        int argb = row[x];
        long i = offset + x * 4L;
        out.pixels.set(ValueLayout.JAVA_BYTE, i, (byte) ((argb >> 16) & 0xFF));
        out.pixels.set(ValueLayout.JAVA_BYTE, i + 1, (byte) ((argb >> 8) & 0xFF));
        out.pixels.set(ValueLayout.JAVA_BYTE, i + 2, (byte) (argb & 0xFF));
        out.pixels.set(ValueLayout.JAVA_BYTE, i + 3, (byte) ((argb >> 24) & 0xFF));
      }
    }
    return out;
  }

  private static float clamp(float v) {
    return Math.max(0, Math.min(1, v));
  }

  /**
   * Render 2D text at the given 3D position, as {@link NativeDesktopPainter} does with JOGL's
   * TextRenderer : the text is drawn with AWT in a texture mapped on a quad in screen coordinates,
   * hence any AWT font can be used, text can be rotated, and text partially out of the viewport is
   * partially drawn.
   * 
   * Rotation is in radian and is applied at the center of the text.
   */
  @Override
  public void drawText(Font font, String label, Coord3d position, Color color, float rotation) {
    if (font == null || label == null || label.isEmpty()) {
      return;
    }

    // Get viewport (and not canvas) dimensions
    int[] viewport = getViewPortAsInt();
    int width = viewport[2];
    int height = viewport[3];

    // Geometric processing for text layout
    float rotationD = -(float) (360 * rotation / (2 * Math.PI));
    Coord3d screen = modelToScreen(position);

    TextImage text = textImage(toAWT(font), label, AWTColor.toAWT(color));

    // Pre-shift text to make it rotate from center of string and not from left point
    int xPreShift = 0, yPreShift = 0;

    if (rotationD != 0) {
      xPreShift = text.advance / 2;
      yPreShift = font.getHeight() / 2;
    }

    // Text baseline is at the origin, shifted to deal with rotation
    drawTexture(text, screen.x + xPreShift, screen.y + yPreShift, rotationD, -xPreShift,
        -yPreShift - text.descent, width, height);
  }

  /**
   * Draw an AWT image in the current viewport, with its bottom left corner at the given position
   * in pixels from the bottom left corner of the viewport.
   * 
   * The image is drawn as a textured quad, so that it can be partially out of the viewport.
   */
  public void drawImage(BufferedImage image, float x, float y) {
    int[] viewport = getViewPortAsInt();
    drawTexture(pixels(image), x, y, 0, 0, 0, viewport[2], viewport[3]);
  }

  /**
   * Draw pixels as a texture mapped on a quad in screen coordinates.
   * 
   * @param x, y the position where the quad origin is translated, in pixels.
   * @param rotationD a rotation around the quad origin, in degrees.
   * @param x0, y0 the position of the bottom left corner of the quad, relative to its origin.
   */
  protected void drawTexture(TextImage image, float x, float y, float rotationD, float x0,
      float y0, int viewportWidth, int viewportHeight) {
    // Nothing is visible in an empty viewport, and glOrtho would fail
    if (viewportWidth <= 0 || viewportHeight <= 0 || image.width <= 0 || image.height <= 0) {
      return;
    }

    gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_TEXTURE_BIT | GL.GL_COLOR_BUFFER_BIT
        | GL.GL_POLYGON_BIT | GL.GL_CURRENT_BIT | GL.GL_TRANSFORM_BIT);

    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glOrtho(0, viewportWidth, 0, viewportHeight, -1, 1);

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glTranslatef(x, y, 0);
    gl.glRotatef(rotationD, 0, 0, 1);

    gl.glDisable(GL.GL_LIGHTING);
    gl.glDisable(GL.GL_DEPTH_TEST);
    gl.glDisable(GL.GL_CULL_FACE);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
    gl.glEnable(GL.GL_TEXTURE_2D);

    MemorySegment textureId = arena.allocate(ValueLayout.JAVA_INT);
    gl.glGenTextures(1, textureId);
    gl.glBindTexture(GL.GL_TEXTURE_2D, textureId.get(ValueLayout.JAVA_INT, 0));
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
    gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 4);
    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, image.width, image.height, 0, GL.GL_RGBA,
        GL.GL_UNSIGNED_BYTE, image.pixels);

    float x1 = x0 + image.width;
    float y1 = y0 + image.height;

    gl.glColor4f(1, 1, 1, 1);
    gl.glBegin(GL.GL_QUADS);
    gl.glTexCoord2f(0, 0);
    gl.glVertex2f(x0, y0);
    gl.glTexCoord2f(1, 0);
    gl.glVertex2f(x1, y0);
    gl.glTexCoord2f(1, 1);
    gl.glVertex2f(x1, y1);
    gl.glTexCoord2f(0, 1);
    gl.glVertex2f(x0, y1);
    gl.glEnd();

    gl.glDeleteTextures(1, textureId);

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glPopMatrix();
    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glPopMatrix();

    gl.glPopAttrib();
  }


  @Override
  public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c) {
    color(c);
    raster(p, null);
    glutBitmapString(axisFont.getCode(), label);
  }

  private java.awt.Font toAWT(Font font) {
    return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, font.getHeight());
  }

  // GL LISTS

  @Override
  public int glGenLists(int range) {
    return gl.glGenLists(range);
  }

  @Override
  public void glNewList(int list, int mode) {
    gl.glNewList(list, mode);
  }

  @Override
  public void glNewList(int list, ListMode mode) {
    switch (mode) {
      case COMPILE:
        glNewList(list, GL.GL_COMPILE);
        break;
      case COMPILE_AND_EXECUTE:
        glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
        break;
    }
  }

  @Override
  public void glEndList() {
    gl.glEndList();
  }

  @Override
  public void glCallList(int list) {
    gl.glCallList(list);
  }

  @Override
  public boolean glIsList(int list) {
    return gl.glIsList(list) != 0;
  }

  @Override
  public void glDeleteLists(int list, int range) {
    gl.glDeleteLists(list, range);
  }

  // GLU

  @Override
  public void gluDisk(double inner, double outer, int slices, int loops) {
    PanamaGLU.disk(gl, inner, outer, slices, loops);
  }

  @Override
  public void glutSolidSphere(double radius, int slices, int stacks) {
    gl.glutSolidSphere(radius, slices, stacks);
  }

  @Override
  public void glutSolidTeapot(float scale) {
    gl.glutSolidTeapot(scale);
  }

  @Override
  public void glutWireTeapot(float scale) {
    gl.glutWireTeapot(scale);
  }

  @Override
  public void gluSphere(double radius, int slices, int stacks) {
    PanamaGLU.sphere(gl, radius, slices, stacks);
  }

  @Override
  public void gluCylinder(double base, double top, double height, int slices, int stacks) {
    PanamaGLU.cylinder(gl, base, top, height, slices, stacks);
  }

  @Override
  public void glutSolidCube(float size) {
    gl.glutSolidCube(size);
  }

  // GL FEEDBACK BUFER

  /**
   * The buffer is filled when leaving the {@link RenderMode#FEEDBACK} mode with
   * {@link #glRenderMode(int)}.
   */
  @Override
  public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
    feedbackBuffer = buffer;
    feedbackSegment = arena.allocate(ValueLayout.JAVA_FLOAT, size);
    gl.glFeedbackBuffer(size, type, feedbackSegment);
  }

  /**
   * Change the render mode and copy the content written by GL in the select and feedback buffers
   * to the Java buffers given to {@link #glSelectBuffer(int, IntBuffer)} and
   * {@link #glFeedbackBuffer(int, int, FloatBuffer)}.
   */
  @Override
  public int glRenderMode(int mode) {
    int result = gl.glRenderMode(mode);

    if (selectBuffer != null) {
      int n = (int) Math.min(selectBuffer.capacity(), selectSegment.byteSize() / Integer.BYTES);
      for (int i = 0; i < n; i++) {
        selectBuffer.put(i, selectSegment.getAtIndex(ValueLayout.JAVA_INT, i));
      }
    }

    if (feedbackBuffer != null) {
      int n = (int) Math.min(feedbackBuffer.capacity(), feedbackSegment.byteSize() / Float.BYTES);
      for (int i = 0; i < n; i++) {
        feedbackBuffer.put(i, feedbackSegment.getAtIndex(ValueLayout.JAVA_FLOAT, i));
      }
    }
    return result;
  }

  @Override
  public int glRenderMode(RenderMode mode) {
    switch (mode) {
      case RENDER:
        return glRenderMode(GL.GL_RENDER);
      case SELECT:
        return glRenderMode(GL.GL_SELECT);
      case FEEDBACK:
        return glRenderMode(GL.GL_FEEDBACK);
    }
    throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
  }

  @Override
  public void glPassThrough(float token) {
    gl.glPassThrough(token);
  }

  // GL STENCIL BUFFER

  @Override
  public void glStencilFunc(StencilFunc func, int ref, int mask) {
    switch (func) {
      case GL_ALWAYS:
        gl.glStencilFunc(GL.GL_ALWAYS, ref, mask);
        break;
      case GL_EQUAL:
        gl.glStencilFunc(GL.GL_EQUAL, ref, mask);
        break;
      case GL_GREATER:
        gl.glStencilFunc(GL.GL_GREATER, ref, mask);
        break;
      case GL_GEQUAL:
        gl.glStencilFunc(GL.GL_GEQUAL, ref, mask);
        break;
      case GL_LEQUAL:
        gl.glStencilFunc(GL.GL_LEQUAL, ref, mask);
        break;
      case GL_LESS:
        gl.glStencilFunc(GL.GL_LESS, ref, mask);
        break;
      case GL_NEVER:
        gl.glStencilFunc(GL.GL_NEVER, ref, mask);
        break;
      case GL_NOTEQUAL:
        gl.glStencilFunc(GL.GL_NOTEQUAL, ref, mask);
        break;

      default:
        throw new IllegalArgumentException("Unknown enum value for StencilFunc: " + func);
    }
  }

  @Override
  public void glStencilMask(int mask) {
    gl.glStencilMask(mask);
  }

  @Override
  public void glStencilMask_True() {
    gl.glStencilMask(GL.GL_TRUE);
  }

  @Override
  public void glStencilMask_False() {
    gl.glStencilMask(GL.GL_FALSE);
  }


  @Override
  public void glStencilOp(StencilOp fail, StencilOp zfail, StencilOp zpass) {
    gl.glStencilOp(toInt(fail), toInt(zfail), toInt(zpass));
  }

  @Override
  public void glClearStencil(int s) {
    gl.glClearStencil(s);
  }

  protected int toInt(StencilOp fail) {
    switch (fail) {
      case GL_DECR:
        return GL.GL_DECR;
      case GL_INCR:
        return GL.GL_INCR;
      case GL_INVERT:
        return GL.GL_INVERT;
      case GL_KEEP:
        return GL.GL_KEEP;
      case GL_REPLACE:
        return GL.GL_REPLACE;
      case GL_ZERO:
        return GL.GL_ZERO;
      default:
        throw new IllegalArgumentException("Unknown enum value for StencilOp: " + fail);
    }
  }

  // GL VIEWPOINT

  @Override
  public void glOrtho(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    gl.glOrtho(left, right, bottom, top, near_val, far_val);
  }

  @Override
  public void gluOrtho2D(double left, double right, double bottom, double top) {
    PanamaGLU.ortho2D(gl, left, right, bottom, top);
  }

  @Override
  public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
    PanamaGLU.perspective(gl, fovy, aspect, zNear, zFar);
  }

  @Override
  public void glFrustum(double left, double right, double bottom, double top, double zNear,
      double zFar) {
    gl.glFrustum(left, right, bottom, top, zNear, zFar);
  }

  @Override
  public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY,
      float centerZ, float upX, float upY, float upZ) {
    PanamaGLU.lookAt(gl, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
  }

  @Override
  public void glViewport(int x, int y, int width, int height) {
    gl.glViewport(x, y, width, height);
  }

  /**
   * @param plane the GL plane id, e.g. GL_CLIP_PLANE0, as given by {@link #clipPlaneId(int)}
   */
  @Override
  public void glClipPlane(int plane, double[] equation) {
    gl.glClipPlane(plane, alloc(equation));
  }

  @Override
  public void glEnable_ClipPlane(int plane) {
    gl.glEnable(clipPlaneId(plane));
  }

  @Override
  public void glDisable_ClipPlane(int plane) {
    gl.glDisable(clipPlaneId(plane));
  }

  /** Return the GL clip plane ID according to an ID in [0;5] */
  @Override
  public int clipPlaneId(int id) {
    switch (id) {
      case 0:
        return GL.GL_CLIP_PLANE0;
      case 1:
        return GL.GL_CLIP_PLANE1;
      case 2:
        return GL.GL_CLIP_PLANE2;
      case 3:
        return GL.GL_CLIP_PLANE3;
      case 4:
        return GL.GL_CLIP_PLANE4;
      case 5:
        return GL.GL_CLIP_PLANE5;
      default:
        throw new IllegalArgumentException("Expect a plane ID in [0;5]");
    }
  }


  @Override
  public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] objPos,
      int objPos_offset) {
    MemorySegment x = arena.allocate(ValueLayout.JAVA_DOUBLE);
    MemorySegment y = arena.allocate(ValueLayout.JAVA_DOUBLE);
    MemorySegment z = arena.allocate(ValueLayout.JAVA_DOUBLE);

    int out = gl.gluUnProject(winX, winY, winZ, matrix(model, model_offset),
        matrix(proj, proj_offset), viewport(view, view_offset), x, y, z);

    objPos[objPos_offset] = (float) x.get(ValueLayout.JAVA_DOUBLE, 0);
    objPos[objPos_offset + 1] = (float) y.get(ValueLayout.JAVA_DOUBLE, 0);
    objPos[objPos_offset + 2] = (float) z.get(ValueLayout.JAVA_DOUBLE, 0);
    return out == GL.GL_TRUE;
  }

  @Override
  public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] winPos,
      int winPos_offset) {
    MemorySegment x = arena.allocate(ValueLayout.JAVA_DOUBLE);
    MemorySegment y = arena.allocate(ValueLayout.JAVA_DOUBLE);
    MemorySegment z = arena.allocate(ValueLayout.JAVA_DOUBLE);

    int out = gl.gluProject(objX, objY, objZ, matrix(model, model_offset),
        matrix(proj, proj_offset), viewport(view, view_offset), x, y, z);

    winPos[winPos_offset] = (float) x.get(ValueLayout.JAVA_DOUBLE, 0);
    winPos[winPos_offset + 1] = (float) y.get(ValueLayout.JAVA_DOUBLE, 0);
    winPos[winPos_offset + 2] = (float) z.get(ValueLayout.JAVA_DOUBLE, 0);
    return out == GL.GL_TRUE;
  }

  /** A 4x4 matrix of doubles, as expected by GLU, read from the float array at the offset. */
  protected MemorySegment matrix(float[] values, int offset) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_DOUBLE, 16);
    for (int i = 0; i < 16; i++) {
      segment.setAtIndex(ValueLayout.JAVA_DOUBLE, i, values[offset + i]);
    }
    return segment;
  }

  /** A viewport of 4 ints read from the array at the offset. */
  protected MemorySegment viewport(int[] values, int offset) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT, 4);
    MemorySegment.copy(values, offset, segment, ValueLayout.JAVA_INT, 0, 4);
    return segment;
  }



  @Override
  public void glDepthFunc(int func) {
    gl.glDepthFunc(func);
  }

  @Override
  public void glDepthRangef(float near, float far) {
    gl.glDepthRange(near, far);
  }

  public void printGLDepthRange() {
    float[] params = new float[2];
    glGetFloatv(GL.GL_DEPTH_RANGE, params, 0);
    Array.print(params);
  }

  @Override
  public void glBlendFunc(int sfactor, int dfactor) {
    gl.glBlendFunc(sfactor, dfactor);
  }

  @Override
  public void glHint(int target, int mode) {
    gl.glHint(target, mode);
  }

  // GL LIGHTS

  @Override
  public void glShadeModel(ColorModel colorModel) {
    if (ColorModel.SMOOTH.equals(colorModel)) {
      gl.glShadeModel(GL.GL_SMOOTH);
    } else if (ColorModel.FLAT.equals(colorModel)) {
      gl.glShadeModel(GL.GL_FLAT);
    } else {
      throw new IllegalArgumentException("Unsupported setting : '" + colorModel + "'");
    }
  }

  @Override
  public void glShadeModel(int mode) {
    gl.glShadeModel(mode);
  }

  @Override
  public void glShadeModel_Smooth() {
    gl.glShadeModel(GL.GL_SMOOTH);
  }

  @Override
  public void glShadeModel_Flat() {
    gl.glShadeModel(GL.GL_FLAT);
  }

  @Override
  public void glMaterialfv(int face, int pname, float[] params, int params_offset) {
    gl.glMaterialfv(face, pname, alloc(params));
  }

  @Override
  public void glNormal3f(float nx, float ny, float nz) {
    gl.glNormal3f(nx, ny, nz);
  }

  @Override
  public void glLightf(int light, Attenuation.Type attenuationType, float value) {
    if (Attenuation.Type.CONSTANT.equals(attenuationType)) {
      glLightf(light, GL.GL_CONSTANT_ATTENUATION, value);
    } else if (Attenuation.Type.LINEAR.equals(attenuationType)) {
      glLightf(light, GL.GL_LINEAR_ATTENUATION, value);
    } else if (Attenuation.Type.QUADRATIC.equals(attenuationType)) {
      glLightf(light, GL.GL_QUADRATIC_ATTENUATION, value);
    }
  }

  @Override
  public void glLightf(int light, int pname, float value) {
    gl.glLightf(lightId(light), pname, value);
  }

  @Override
  public void glLightfv(int light, int pname, float[] params, int params_offset) {
    gl.glLightfv(lightId(light), pname, alloc(params));
  }

  @Override
  public void glLight_Position(int lightId, float[] positionZero) {
    glLightfv(lightId, GL.GL_POSITION, positionZero, 0);
  }

  @Override
  public void glLight_Ambiant(int lightId, Color ambiantColor) {
    glLightfv(lightId, GL.GL_AMBIENT, ambiantColor.toArray(), 0);
  }

  @Override
  public void glLight_Diffuse(int lightId, Color diffuseColor) {
    glLightfv(lightId, GL.GL_DIFFUSE, diffuseColor.toArray(), 0);
  }

  @Override
  public void glLight_Specular(int lightId, Color specularColor) {
    glLightfv(lightId, GL.GL_SPECULAR, specularColor.toArray(), 0);
  }

  @Override
  public void glLight_Shininess(int lightId, float value) {
    glLightf(lightId, GL.GL_SHININESS, value);
  }

  @Override
  public void glEnable_Light(int light) {
    glEnable(lightId(light));
  }

  @Override
  public void glDisable_Light(int light) {
    glDisable(lightId(light));
  }

  protected int lightId(int id) {
    switch (id) {
      case 0:
        return GL.GL_LIGHT0;
      case 1:
        return GL.GL_LIGHT1;
      case (2):
        return GL.GL_LIGHT2;
      case 3:
        return GL.GL_LIGHT3;
      case 4:
        return GL.GL_LIGHT4;
      case 5:
        return GL.GL_LIGHT5;
      case 6:
        return GL.GL_LIGHT6;
      case 7:
        return GL.GL_LIGHT7;
    }
    throw new IllegalArgumentException("Unsupported light ID '" + id + "'");
  }

  @Override
  public void glLightModeli(int mode, int value) {
    gl.glLightModeli(mode, value);
  }

  @Override
  public void glLightModelfv(int mode, float[] value) {
    gl.glLightModelfv(mode, alloc(value));
  }

  @Override
  public void glLightModel(LightModel model, boolean value) {
    if (LightModel.LIGHT_MODEL_TWO_SIDE.equals(model)) {
      glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, value ? 1 : 0);
    } else if (LightModel.LIGHT_MODEL_LOCAL_VIEWER.equals(model)) {
      glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, value ? 1 : 0);
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  @Override
  public void glLightModel(LightModel model, Color color) {
    if (LightModel.LIGHT_MODEL_AMBIENT.equals(model)) {
      glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, color.toArray());
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  // GL OTHER

  @Override
  public void glClearColor(float red, float green, float blue, float alpha) {
    gl.glClearColor(red, green, blue, alpha);
  }

  @Override
  public void glClearDepth(double d) {
    gl.glClearDepth(d);
  }

  @Override
  public void glClear(int mask) {
    gl.glClear(mask);
  }

  @Override
  public void glClearColorAndDepthBuffers() {
    glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
  }

  // GL PICKING

  @Override
  public void glInitNames() {
    gl.glInitNames();
  }

  @Override
  public void glLoadName(int name) {
    gl.glLoadName(name);
  }

  @Override
  public void glPushName(int name) {
    gl.glPushName(name);
  }

  @Override
  public void glPopName() {
    gl.glPopName();
  }

  /**
   * The buffer is filled when leaving the {@link RenderMode#SELECT} mode with
   * {@link #glRenderMode(int)}.
   */
  @Override
  public void glSelectBuffer(int size, IntBuffer buffer) {
    selectBuffer = buffer;
    selectSegment = arena.allocate(ValueLayout.JAVA_INT, size);
    gl.glSelectBuffer(size, selectSegment);
  }

  @Override
  public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport,
      int viewport_offset) {
    PanamaGLU.pickMatrix(gl, x, y, delX, delY, viewport, viewport_offset);
  }

  @Override
  public void glFlush() {
    gl.glFlush();
  }

  @Override
  public void glEvalCoord2f(float u, float v) {
    gl.glEvalCoord2f(u, v);
  }

  @Override
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, FloatBuffer points) {
    gl.glMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, segment(points));
  }

  @Override
  public void glEnable_PolygonOffsetFill() {
    glEnable(GL.GL_POLYGON_OFFSET_FILL);
  }

  @Override
  public void glDisable_PolygonOffsetFill() {
    glDisable(GL.GL_POLYGON_OFFSET_FILL);
  }

  @Override
  public void glEnable_PolygonOffsetLine() {
    glEnable(GL.GL_POLYGON_OFFSET_LINE);
  }

  @Override
  public void glDisable_PolygonOffsetLine() {
    glDisable(GL.GL_POLYGON_OFFSET_LINE);
  }

  @Override
  public void glDisable_Lighting() {
    glDisable(GL.GL_LIGHTING);
  }

  @Override
  public void glEnable_Lighting() {
    glEnable(GL.GL_LIGHTING);
  }

  @Override
  public void glEnable_LineStipple() {
    glEnable(GL.GL_LINE_STIPPLE);
  }

  @Override
  public void glDisable_LineStipple() {
    glDisable(GL.GL_LINE_STIPPLE);
  }

  @Override
  public void glEnable_Blend() {
    glEnable(GL.GL_BLEND);
  }

  @Override
  public void glDisable_Blend() {
    glDisable(GL.GL_BLEND);
  }

  @Override
  public void glMatrixMode_ModelView() {
    glMatrixMode(GL.GL_MODELVIEW);
  }

  @Override
  public void glMatrixMode_Projection() {
    glMatrixMode(GL.GL_PROJECTION);
  }

  @Override
  public void glBegin_Polygon() {
    glBegin(GL.GL_POLYGON);
  }

  @Override
  public void glBegin_Quad() {
    glBegin(GL.GL_QUADS);
  }

  @Override
  public void glBegin_Triangle() {
    glBegin(GL.GL_TRIANGLES);
  }

  @Override
  public void glBegin_Point() {
    glBegin(GL.GL_POINTS);
  }

  @Override
  public void glBegin_LineStrip() {
    glBegin(GL.GL_LINE_STRIP);
  }

  @Override
  public void glBegin_LineLoop() {
    glBegin(GL.GL_LINE_LOOP);
  }

  @Override
  public void glBegin_Line() {
    glBegin(GL.GL_LINES);
  }

  @Override
  public void glEnable_CullFace() {
    glEnable(GL.GL_CULL_FACE);
  }

  @Override
  public void glDisable_CullFace() {
    glDisable(GL.GL_CULL_FACE);
  }

  @Override
  public void glFrontFace_ClockWise() {
    glFrontFace(GL.GL_CCW);
  }

  @Override
  public void glCullFace_Front() {
    glCullFace(GL.GL_FRONT);
  }

  @Override
  public void glEnable_ColorMaterial() {
    glEnable(GL.GL_COLOR_MATERIAL);
  }

  @Override
  public void glMaterial(MaterialProperty material, Color color, boolean isFront) {
    if (isFront) {
      glMaterialfv(GL.GL_FRONT, materialProperty(material), color.toArray(), 0);
    } else {
      glMaterialfv(GL.GL_BACK, materialProperty(material), color.toArray(), 0);
    }
  }

  @Override
  public void glMaterial(MaterialProperty material, float[] color, boolean isFront) {
    if (isFront) {
      glMaterialfv(GL.GL_FRONT, materialProperty(material), color, 0);
    } else {
      glMaterialfv(GL.GL_BACK, materialProperty(material), color, 0);
    }
  }

  protected int materialProperty(MaterialProperty material) {
    switch (material) {
      case AMBIENT:
        return GL.GL_AMBIENT;
      case DIFFUSE:
        return GL.GL_DIFFUSE;
      case SPECULAR:
        return GL.GL_SPECULAR;
      case SHININESS:
        return GL.GL_SHININESS;
    }
    throw new IllegalArgumentException("Unsupported property '" + material + "'");
  }

  @Override
  public void glEnable_PointSmooth() {
    glEnable(GL.GL_POINT_SMOOTH);
  }

  @Override
  public void glHint_PointSmooth_Nicest() {
    glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
  }

  @Override
  public void glDepthFunc(DepthFunc func) {
    switch (func) {
      case GL_ALWAYS:
        gl.glDepthFunc(GL.GL_ALWAYS);
        break;
      case GL_NEVER:
        gl.glDepthFunc(GL.GL_NEVER);
        break;
      case GL_EQUAL:
        gl.glDepthFunc(GL.GL_EQUAL);
        break;
      case GL_GEQUAL:
        gl.glDepthFunc(GL.GL_GEQUAL);
        break;
      case GL_GREATER:
        gl.glDepthFunc(GL.GL_GREATER);
        break;
      case GL_LEQUAL:
        gl.glDepthFunc(GL.GL_LEQUAL);
        break;
      case GL_LESS:
        gl.glDepthFunc(GL.GL_LESS);
        break;
      case GL_NOTEQUAL:
        gl.glDepthFunc(GL.GL_NOTEQUAL);
        break;
      default:
        throw new RuntimeException("Enum value not supported : " + func);
    }
  }

  @Override
  public void glEnable_DepthTest() {
    gl.glEnable(GL.GL_DEPTH_TEST);
  }

  @Override
  public void glDisable_DepthTest() {
    gl.glDisable(GL.GL_DEPTH_TEST);
  }

  @Override
  public void glEnable_Stencil() {
    gl.glEnable(GL.GL_STENCIL_TEST);
  }

  @Override
  public void glDisable_Stencil() {
    gl.glDisable(GL.GL_STENCIL_TEST);
  }

  /* ******************************************************************************************* */
  /* GPU RESOURCES                                                                               */
  /* ******************************************************************************************* */

  /** Native memory for n ints. */
  protected MemorySegment ints(int n) {
    return arena.allocate(ValueLayout.JAVA_INT, Math.max(1, n));
  }

  /** Native memory holding n ints of the array, starting at offset. */
  protected MemorySegment ints(int[] values, int offset, int n) {
    MemorySegment segment = ints(n);
    MemorySegment.copy(values, offset, segment, ValueLayout.JAVA_INT, 0, n);
    return segment;
  }

  /** Copy n ints of the native memory to the array, starting at offset. */
  protected void copy(MemorySegment segment, int[] values, int offset, int n) {
    MemorySegment.copy(segment, ValueLayout.JAVA_INT, 0, values, offset, n);
  }

  /** Native memory holding n floats of the array, starting at offset. */
  protected MemorySegment floats(float[] values, int offset, int n) {
    MemorySegment segment = arena.allocate(ValueLayout.JAVA_FLOAT, Math.max(1, n));
    MemorySegment.copy(values, offset, segment, ValueLayout.JAVA_FLOAT, 0, n);
    return segment;
  }

  /** A buffer as native memory, or NULL if the buffer is null. */
  protected MemorySegment segmentOrNull(Buffer buffer) {
    return buffer == null ? MemorySegment.NULL : segment(buffer);
  }

  /** An offset in a bound buffer object, given to GL as a pointer value. */
  protected MemorySegment offset(long offset) {
    return MemorySegment.ofAddress(offset);
  }

  /** Read a C string returned by GL. */
  protected String string(MemorySegment string) {
    if (string == null || MemorySegment.NULL.equals(string)) {
      return null;
    }
    return string.reinterpret(Long.MAX_VALUE).getString(0);
  }

  // Buffer objects

  @Override
  public void glGenBuffers(int n, int[] buffers, int offset) {
    MemorySegment ids = ints(n);
    gl.glGenBuffers(n, ids);
    copy(ids, buffers, offset, n);
  }

  @Override
  public void glDeleteBuffers(int n, int[] buffers, int offset) {
    gl.glDeleteBuffers(n, ints(buffers, offset, n));
  }

  @Override
  public void glBindBuffer(int target, int buffer) {
    gl.glBindBuffer(target, buffer);
  }

  @Override
  public void glBufferData(int target, long size, Buffer data, int usage) {
    gl.glBufferData(target, size, segmentOrNull(data), usage);
  }

  @Override
  public void glBufferSubData(int target, long offset, long size, Buffer data) {
    gl.glBufferSubData(target, offset, size, segmentOrNull(data));
  }

  // Vertex arrays

  @Override
  public void glEnableClientState(int array) {
    gl.glEnableClientState(array);
  }

  @Override
  public void glDisableClientState(int array) {
    gl.glDisableClientState(array);
  }

  @Override
  public void glVertexPointer(int size, int type, int stride, long pointerOffset) {
    gl.glVertexPointer(size, type, stride, offset(pointerOffset));
  }

  @Override
  public void glNormalPointer(int type, int stride, long pointerOffset) {
    gl.glNormalPointer(type, stride, offset(pointerOffset));
  }

  @Override
  public void glColorPointer(int size, int type, int stride, long pointerOffset) {
    gl.glColorPointer(size, type, stride, offset(pointerOffset));
  }

  @Override
  public void glTexCoordPointer(int size, int type, int stride, long pointerOffset) {
    gl.glTexCoordPointer(size, type, stride, offset(pointerOffset));
  }

  // Drawing

  @Override
  public void glDrawArrays(int mode, int first, int count) {
    gl.glDrawArrays(mode, first, count);
  }

  @Override
  public void glDrawElements(int mode, int count, int type, long indicesOffset) {
    gl.glDrawElements(mode, count, type, offset(indicesOffset));
  }

  @Override
  public void glMultiDrawArrays(int mode, IntBuffer first, IntBuffer count, int drawcount) {
    gl.glMultiDrawArrays(mode, segment(first), segment(count), drawcount);
  }

  @Override
  public void glMultiDrawElements(int mode, IntBuffer count, int type, LongBuffer indicesOffsets,
      int drawcount) {
    // an array of pointers, each being an offset in the bound element buffer
    MemorySegment indices = arena.allocate(ValueLayout.ADDRESS, Math.max(1, drawcount));
    for (int i = 0; i < drawcount; i++) {
      indices.setAtIndex(ValueLayout.ADDRESS, i,
          offset(indicesOffsets.get(indicesOffsets.position() + i)));
    }
    gl.glMultiDrawElements(mode, segment(count), type, indices, drawcount);
  }

  @Override
  public void glPrimitiveRestartIndex(int index) {
    gl.glPrimitiveRestartIndex(index);
  }

  // Shaders

  @Override
  public int glCreateShader(int type) {
    return gl.glCreateShader(type);
  }

  @Override
  public void glShaderSource(int shader, String[] sources) {
    MemorySegment strings = arena.allocate(ValueLayout.ADDRESS, Math.max(1, sources.length));
    for (int i = 0; i < sources.length; i++) {
      strings.setAtIndex(ValueLayout.ADDRESS, i, arena.allocateFrom(sources[i]));
    }
    gl.glShaderSource(shader, sources.length, strings, MemorySegment.NULL);
  }

  @Override
  public void glCompileShader(int shader) {
    gl.glCompileShader(shader);
  }

  @Override
  public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
    MemorySegment out = ints(1);
    gl.glGetShaderiv(shader, pname, out);
    copy(out, params, offset, 1);
  }

  @Override
  public String glGetShaderInfoLog(int shader) {
    int[] length = new int[1];
    glGetShaderiv(shader, GL.GL_INFO_LOG_LENGTH, length, 0);
    if (length[0] <= 0) {
      return "";
    }
    MemorySegment log = arena.allocate(length[0]);
    gl.glGetShaderInfoLog(shader, length[0], MemorySegment.NULL, log);
    return log.getString(0);
  }

  @Override
  public void glDeleteShader(int shader) {
    gl.glDeleteShader(shader);
  }

  @Override
  public int glCreateProgram() {
    return gl.glCreateProgram();
  }

  @Override
  public void glAttachShader(int program, int shader) {
    gl.glAttachShader(program, shader);
  }

  @Override
  public void glDetachShader(int program, int shader) {
    gl.glDetachShader(program, shader);
  }

  @Override
  public void glLinkProgram(int program) {
    gl.glLinkProgram(program);
  }

  @Override
  public void glValidateProgram(int program) {
    gl.glValidateProgram(program);
  }

  @Override
  public void glGetProgramiv(int program, int pname, int[] params, int offset) {
    MemorySegment out = ints(1);
    gl.glGetProgramiv(program, pname, out);
    copy(out, params, offset, 1);
  }

  @Override
  public String glGetProgramInfoLog(int program) {
    int[] length = new int[1];
    glGetProgramiv(program, GL.GL_INFO_LOG_LENGTH, length, 0);
    if (length[0] <= 0) {
      return "";
    }
    MemorySegment log = arena.allocate(length[0]);
    gl.glGetProgramInfoLog(program, length[0], MemorySegment.NULL, log);
    return log.getString(0);
  }

  @Override
  public void glUseProgram(int program) {
    gl.glUseProgram(program);
  }

  @Override
  public void glDeleteProgram(int program) {
    gl.glDeleteProgram(program);
  }

  @Override
  public int glGetUniformLocation(int program, String name) {
    return gl.glGetUniformLocation(program, arena.allocateFrom(name));
  }

  @Override
  public void glUniform1i(int location, int v0) {
    gl.glUniform1i(location, v0);
  }

  @Override
  public void glUniform1f(int location, float v0) {
    gl.glUniform1f(location, v0);
  }

  @Override
  public void glUniform1fv(int location, int count, float[] value, int offset) {
    gl.glUniform1fv(location, count, floats(value, offset, count));
  }

  @Override
  public void glUniform2fv(int location, int count, float[] value, int offset) {
    gl.glUniform2fv(location, count, floats(value, offset, 2 * count));
  }

  @Override
  public void glUniform3fv(int location, int count, float[] value, int offset) {
    gl.glUniform3fv(location, count, floats(value, offset, 3 * count));
  }

  @Override
  public void glUniform4fv(int location, int count, float[] value, int offset) {
    gl.glUniform4fv(location, count, floats(value, offset, 4 * count));
  }

  @Override
  public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value,
      int offset) {
    gl.glUniformMatrix4fv(location, count, (byte) (transpose ? 1 : 0),
        floats(value, offset, 16 * count));
  }

  // Textures

  @Override
  public void glGenTextures(int n, int[] textures, int offset) {
    MemorySegment ids = ints(n);
    gl.glGenTextures(n, ids);
    copy(ids, textures, offset, n);
  }

  @Override
  public void glDeleteTextures(int n, int[] textures, int offset) {
    gl.glDeleteTextures(n, ints(textures, offset, n));
  }

  @Override
  public void glBindTexture(int target, int texture) {
    gl.glBindTexture(target, texture);
  }

  @Override
  public void glActiveTexture(int texture) {
    gl.glActiveTexture(texture);
  }

  @Override
  public void glTexParameteri(int target, int pname, int param) {
    gl.glTexParameteri(target, pname, param);
  }

  @Override
  public void glTexImage1D(int target, int level, int internalFormat, int width, int border,
      int format, int type, Buffer pixels) {
    gl.glTexImage1D(target, level, internalFormat, width, border, format, type,
        segmentOrNull(pixels));
  }

  @Override
  public void glTexImage2D(int target, int level, int internalFormat, int width, int height,
      int border, int format, int type, Buffer pixels) {
    gl.glTexImage2D(target, level, internalFormat, width, height, border, format, type,
        segmentOrNull(pixels));
  }

  @Override
  public void glTexImage3D(int target, int level, int internalFormat, int width, int height,
      int depth, int border, int format, int type, Buffer pixels) {
    gl.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type,
        segmentOrNull(pixels));
  }

  @Override
  public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int type, Buffer pixels) {
    gl.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format,
        type, segmentOrNull(pixels));
  }

  @Override
  public void glTexCoord3f(float s, float t, float r) {
    gl.glTexCoord3f(s, t, r);
  }

  // Framebuffer objects

  @Override
  public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
    MemorySegment ids = ints(n);
    gl.glGenFramebuffers(n, ids);
    copy(ids, framebuffers, offset, n);
  }

  @Override
  public void glDeleteFramebuffers(int n, int[] framebuffers, int offset) {
    gl.glDeleteFramebuffers(n, ints(framebuffers, offset, n));
  }

  @Override
  public void glBindFramebuffer(int target, int framebuffer) {
    gl.glBindFramebuffer(target, framebuffer);
  }

  @Override
  public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture,
      int level) {
    gl.glFramebufferTexture2D(target, attachment, textarget, texture, level);
  }

  @Override
  public int glCheckFramebufferStatus(int target) {
    return gl.glCheckFramebufferStatus(target);
  }

  @Override
  public void glDrawBuffer(int mode) {
    gl.glDrawBuffer(mode);
  }

  @Override
  public void glDrawBuffers(int n, int[] buffers, int offset) {
    gl.glDrawBuffers(n, ints(buffers, offset, n));
  }

  // Queries

  @Override
  public void glGenQueries(int n, int[] ids, int offset) {
    MemorySegment out = ints(n);
    gl.glGenQueries(n, out);
    copy(out, ids, offset, n);
  }

  @Override
  public void glDeleteQueries(int n, int[] ids, int offset) {
    gl.glDeleteQueries(n, ints(ids, offset, n));
  }

  @Override
  public void glBeginQuery(int target, int id) {
    gl.glBeginQuery(target, id);
  }

  @Override
  public void glEndQuery(int target) {
    gl.glEndQuery(target);
  }

  @Override
  public void glGetQueryObjectuiv(int id, int pname, int[] params, int offset) {
    MemorySegment out = ints(1);
    gl.glGetQueryObjectuiv(id, pname, out);
    copy(out, params, offset, 1);
  }

  // Other

  @Override
  public void glAlphaFunc(int func, float ref) {
    gl.glAlphaFunc(func, ref);
  }

  @Override
  public void glBlendEquation(int mode) {
    gl.glBlendEquation(mode);
  }

  @Override
  public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
    gl.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
  }

  @Override
  public int glGetError() {
    return gl.glGetError();
  }

  @Override
  public void glReadPixels(int x, int y, int width, int height, int format, int type,
      Buffer pixels) {
    MemorySegment target = MemorySegment.ofBuffer(pixels);

    if (target.isNative()) {
      gl.glReadPixels(x, y, width, height, format, type, target);
    } else {
      // heap buffers : read in native memory, then copy
      MemorySegment out = arena.allocate(target.byteSize());
      gl.glReadPixels(x, y, width, height, format, type, out);
      target.copyFrom(out);
    }
  }
}
