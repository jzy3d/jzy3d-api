package org.jzy3d.lwjgl;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE0;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE1;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE2;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE3;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE4;
import static org.lwjgl.opengl.GL11.GL_CLIP_PLANE5;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_COMPILE_AND_EXECUTE;
import static org.lwjgl.opengl.GL11.GL_CONSTANT_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FEEDBACK;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_GEQUAL;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_INCR;
import static org.lwjgl.opengl.GL11.GL_INVERT;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHT1;
import static org.lwjgl.opengl.GL11.GL_LIGHT2;
import static org.lwjgl.opengl.GL11.GL_LIGHT3;
import static org.lwjgl.opengl.GL11.GL_LIGHT4;
import static org.lwjgl.opengl.GL11.GL_LIGHT5;
import static org.lwjgl.opengl.GL11.GL_LIGHT6;
import static org.lwjgl.opengl.GL11.GL_LIGHT7;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_LOCAL_VIEWER;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_TWO_SIDE;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_LINE_STIPPLE;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_NEVER;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_LINE;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RENDER;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_SELECT;
import static org.lwjgl.opengl.GL11.GL_SHININESS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.os.WindowingToolkit;
import org.jzy3d.painters.AbstractPainter;
import org.jzy3d.painters.ColorModel;
import org.jzy3d.painters.DepthFunc;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.ListMode;
import org.jzy3d.painters.PixelStore;
import org.jzy3d.painters.RenderMode;
import org.jzy3d.painters.StencilFunc;
import org.jzy3d.painters.StencilOp;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Attenuation;
import org.jzy3d.plot3d.rendering.lights.LightModel;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;
import org.lwjgl.opengl.GL11;
import opengl.glu.GLU;


public class LWJGLPainterAWT extends AbstractPainter implements IPainter {
  static Logger LOGGER = LogManager.getLogger(LWJGLPainterAWT.class);


  /** A 1x1 image used for processing text length in pixel if no context is available */
  protected BufferedImage textLengthFallbackImage;
  protected FontMetrics fontMetricsFallback;

  /*public GL getGL() {
    return gl;
  }

  public GL2 getGL2() {
    return getGL().getGL2();
  }

  public void setGL(GL gl) {
    this.gl = gl;
  }

  public GLU getGLU() {
    return glu;
  }

  public void setGLU(GLU glu) {
    this.glu = glu;
  }

  public GLUT getGLUT() {
    return glut;
  }

  public void setGLUT(GLUT glut) {
    this.glut = glut;
  }*/

  @Override
  public Object acquireGL() {
    return null;//getCurrentGL(canvas);
  }

  @Override
  public void releaseGL() {
    //getCurrentContext(canvas).release();
  }

  /*public GL getCurrentGL(ICanvas canvas) {
    GLContext context = getCurrentContext(canvas);
    context.makeCurrent();
    return context.getGL();
  }

  public GLContext getCurrentContext(ICanvas canvas) {
    GLAutoDrawable d = ((INativeCanvas) canvas).getDrawable();
    return d.getContext();
  }*/

  @Override
  public void configureGL(Quality quality) {
    // Activate Depth buffer
    if (quality.isDepthActivated()) {

      glEnable(GL_DEPTH_TEST);
      glDepthFunc(GL_LEQUAL);
    } else {
      glDisable(GL_DEPTH_TEST);
      // glDepthRangef(n, f);
    }



    // Blending
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // KEEP THIS DOC!!
    // glBlendFunc(GL_DST_ALPHA, GL_ONE);
    // ONE : make a white screen
    // NONE : make surface look opaque
    // GL_ONE_MINUS_DST_ALPHA : make surface look opaque
    // GL_ONE_MINUS_SRC_ALPHA : make surface look translucent but change coloring
    // GL_SRC_ALPHA : make surface look translucent but change coloring + no wireframe
    // GL_DST_ALPHA : make a white screen
    // glBlendFunc(GL_ONE_MINUS_DST_ALPHA,GL_DST_ALPHA);

    // glBlen
    // ByteBuffer byteBuffer = ByteBuffer.allocate(10);
    // IntBuffer ib = byteBuffer.asIntBuffer();
    // glGetIntegerv(GL_BLEND_SRC_ALPHA, ib);
    // glGetIntegerv(GL_BLEND_DST_ALPHA, ib);
    // System.out.println(ib.array());

    // on/off is handled by each viewport (camera or image)

    // Activate tranparency
    if (quality.isAlphaActivated()) {
      glEnable(GL_BLEND);
      glEnable(GL_ALPHA_TEST);

      if (quality.isDisableDepthBufferWhenAlpha()) {
        // Disable depth test to keeping pixels of
        // "what's behind a polygon" when drawing with
        // alpha
        glDisable(GL_DEPTH_TEST);
      }
    } else {
      glDisable(GL_ALPHA_TEST);
    }

    // Make smooth colors for polygons (interpolate color between points)
    //if (getGLProfile().isGL2()) {
      if (quality.isSmoothColor())
        glShadeModel(GL_SMOOTH);
      else
        glShadeModel(GL_FLAT);
   /* } else {
      LOGGER.warn(
          "Did not configured shade model as we don t have a GL2 context : " + getGLProfile());
    }*/
    /*
     * else if(getGLProfile().isGL4()) { if (quality.isSmoothColor())
     * getGL4().glShadeModel(GL_SMOOTH); else
     * glShadeModel(GL_FLAT); }
     */

    // Make smoothing setting
    if (quality.isSmoothPolygon()) {
      glEnable(GL_POLYGON_SMOOTH);
      glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
    } else
      glDisable(GL_POLYGON_SMOOTH);

    if (quality.isSmoothLine()) {
      glEnable(GL_LINE_SMOOTH);
      glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    } else
      glDisable(GL_LINE_SMOOTH);

    if (quality.isSmoothPoint()) {
      glEnable(GL_POINT_SMOOTH);
      glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
    } else
      glDisable(GL_POINT_SMOOTH);
  }

  @Override
  public WindowingToolkit getWindowingToolkit() {
    String name = getCanvas().getClass().getSimpleName();
    if (name.indexOf("CanvasAWT") >= 0) {
      return WindowingToolkit.AWT;
    } else if (name.indexOf("CanvasSwing") >= 0) {
      return WindowingToolkit.Swing;
    } else if (name.indexOf("CanvasNewtSWT") >= 0) {
      return WindowingToolkit.SWT;
    } else if (name.indexOf("OffscreenCanvas") >= 0) {
      return WindowingToolkit.Offscreen;
    }
    return WindowingToolkit.UNKOWN;
  }

  @Override
  public int[] getViewPortAsInt() {
    int viewport[] = new int[4];
    GL11.glGetIntegerv(GL_VIEWPORT, viewport);
    return viewport;
  }

  @Override
  public double[] getProjectionAsDouble() {
    double projection[] = new double[16];
    GL11.glGetDoublev(GL_PROJECTION_MATRIX, projection);
    return projection;
  }

  @Override
  public float[] getProjectionAsFloat() {
    float projection[] = new float[16];
    GL11.glGetFloatv(GL_PROJECTION_MATRIX, projection);
    return projection;
  }

  @Override
  public double[] getModelViewAsDouble() {
    double modelview[] = new double[16];
    GL11.glGetDoublev(GL_MODELVIEW_MATRIX, modelview);
    return modelview;
  }

  @Override
  public float[] getModelViewAsFloat() {
    float modelview[] = new float[16];
    GL11.glGetFloatv(GL_MODELVIEW_MATRIX, modelview);
    return modelview;
  }

  /************ OPEN GL Interface **************/

  // GL MATRIX

  @Override
  public void glPushMatrix() {
    GL11.glPushMatrix();
  }

  @Override
  public void glPopMatrix() {
    GL11.glPopMatrix();
  }

  @Override
  public void glMatrixMode(int mode) {
    GL11.glMatrixMode(mode);
  }

  @Override
  public void glLoadIdentity() {
    GL11.glLoadIdentity();
  }

  @Override
  public void glScalef(float x, float y, float z) {
    GL11.glScalef(x, y, z);
  }

  @Override
  public void glTranslatef(float x, float y, float z) {
    GL11.glTranslatef(x, y, z);
  }

  @Override
  public void glRotatef(float angle, float x, float y, float z) {
    GL11.glRotatef(angle, x, y, z);
  }

  @Override
  public void glEnable(int type) {
    GL11.glEnable(type);
  }

  @Override
  public void glDisable(int type) {
    GL11.glDisable(type);
  }

  // GL GEOMETRY

  @Override
  public void glPointSize(float width) {
    GL11.glPointSize(width);
  }

  @Override
  public void glLineWidth(float width) {
    GL11.glLineWidth(width);
  }

  @Override
  public void glBegin(int type) {
    GL11.glBegin(type);
  }

  @Override
  public void glColor3f(float r, float g, float b) {
    GL11.glColor3f(r, g, b);
  }

  @Override
  public void glColor4f(float r, float g, float b, float a) {
    GL11.glColor4f(r, g, b, a);
  }

  @Override
  public void glVertex3f(float x, float y, float z) {
    GL11.glVertex3f(x, y, z);
  }

  @Override
  public void glVertex3d(double x, double y, double z) {
    GL11.glVertex3d(x, y, z);
  }

  @Override
  public void glEnd() {
    GL11.glEnd();
  }

  @Override
  public void glFrontFace(int mode) {
    GL11.glFrontFace(mode);
  }

  @Override
  public void glCullFace(int mode) {
    GL11.glCullFace(mode);
  }

  @Override
  public void glPolygonMode(PolygonMode mode, PolygonFill fill) {
    int modeValue = polygonModeValue(mode);
    int fillValue = polygonFillValue(fill);

    GL11.glPolygonMode(modeValue, fillValue);
  }

  protected int polygonModeValue(PolygonMode mode) {
    switch (mode) {
      case FRONT:
        return GL_FRONT;
      case BACK:
        return GL_BACK;
      case FRONT_AND_BACK:
        return GL_FRONT_AND_BACK;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
    }
  }

  protected int polygonFillValue(PolygonFill mode) {
    switch (mode) {
      case FILL:
        return GL_FILL;
      case LINE:
        return GL_LINE;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
    }
  }

  @Override
  public void glPolygonMode(int frontOrBack, int fill) {
    GL11.glPolygonMode(frontOrBack, fill);
  }

  @Override
  public void glPolygonOffset(float factor, float units) {
    GL11.glPolygonOffset(factor, units); // handle stippling
  }

  @Override
  public void glLineStipple(int factor, short pattern) {
    GL11.glLineStipple(factor, pattern);
  }

  // GL TEXTURE

  @Override
  public void glTexCoord2f(float s, float t) {
    GL11.glTexCoord2f(s, t);
  }

  @Override
  public void glTexEnvf(int target, int pname, float param) {
    GL11.glTexEnvf(target, pname, param);
  }

  @Override
  public void glTexEnvi(int target, int pname, int param) {
    GL11.glTexEnvi(target, pname, param);
  }

  // DRAW IMAGES

  @Override
  public void glRasterPos3f(float x, float y, float z) {
    GL11.glRasterPos3f(x, y, z);
  }

  @Override
  public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {
    GL11.glDrawPixels(width, height, format, type, (IntBuffer)pixels);
  }

  @Override
  public void glPixelZoom(float xfactor, float yfactor) {
    GL11.glPixelZoom(xfactor, yfactor);
  }

  @Override
  public void glPixelStorei(int pname, int param) {
    GL11.glPixelStorei(pname, param);
  }

  @Override
  public void glPixelStore(PixelStore store, int param) {
    switch (store) {
      case PACK_ALIGNMENT:
        GL11.glPixelStorei(GL_PACK_ALIGNMENT, param);
        break;
      case UNPACK_ALIGNMENT:
        GL11.glPixelStorei(GL_UNPACK_ALIGNMENT, param);
        break;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + store + "'");
    }
  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      byte[] bitmap, int bitmap_offset) {
    GL11.glBitmap(width, height, xorig, yorig, xmove, ymove, ByteBuffer.wrap(bitmap));
    
    if(bitmap_offset!=0)
      System.err.println("LWJGLPainterAWT.glBitmap() does not support offset != 0");
  }


  @Override
  public void drawImage(ByteBuffer imageBuffer, int imageWidth, int imageHeight, Coord2d pixelZoom,
      Coord3d imagePosition) {
    GL11.glPixelZoom(pixelZoom.x, pixelZoom.y);
    GL11.glRasterPos3f(imagePosition.x, imagePosition.y, imagePosition.z);
    // painter.glRasterPos2f(xpict, ypict);

    synchronized (imageBuffer) { // we don't want to draw image while it is being set by setImage
      GL11.glDrawPixels(imageWidth, imageHeight, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
    }
  }

  // elements of GL spec picked in JOGL GL interface
  public static final int GL_RGBA = 0x1908;
  public static final int GL_UNSIGNED_BYTE = 0x1401;


  // STRINGS

  @Override
  public void glutBitmapString(int font, String string) {
    //glutBitmapString(font, string);
  }

  /**
   * Render 2D text at the given 3D position.
   * 
   * The {@link Font} can be any font name and size supported by AWT.
   * 
   * Rotation is in radian and is applied at the center of the text to avoid messing up text layout.
   * 
   * 
   * @see {@link #glutBitmapString(int, String)}, an alternative way of rendering text with simpler
   *      parameters and smaller font name and size set.
   */
  @Override
  public void drawText(Font font, String label, Coord3d position, Color color, float rotation) {
    // Get viewport (and not canvas) dimensions
    int[] viewport = getViewPortAsInt();
    int width = viewport[2];
    int height = viewport[3];

    // Reset to a polygon mode suitable for rendering the texture handling the text
    glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

    // Geometric processing for text layout
    float rotationD = -(float) (360 * rotation / (2 * Math.PI));
    Coord3d screen = modelToScreen(position);

    //TextRenderer renderer = getOrCreateTextRenderer(font);
    //renderer.setColor(color.r, color.g, color.b, color.a);
    // indicates current viewport (which may be smaller than canvas)
    //renderer.beginRendering(width, height);

    // Pre-shift text to make it rotate from center
    // of string and not from left point
    int xPreShift = 0, yPreShift = 0;

    if (rotationD != 0) {
      xPreShift = getTextLengthInPixels(font, label) / 2;
      yPreShift = font.getHeight() / 2;
    }

    GL11.glMatrixMode(GL_MODELVIEW);
    GL11.glPushMatrix();
    GL11.glTranslatef(screen.x + xPreShift, screen.y + yPreShift, 0);
    GL11.glScalef(1, 1, 1);
    GL11.glRotatef(rotationD, 0, 0, 1);

    // Shifting text to deal with rotation
    int xPostShift = -xPreShift;
    int yPostShift = -yPreShift;

    /*renderer.draw(label, xPostShift, yPostShift);
    renderer.endRendering();
    renderer.flush();*/

    GL11.glPopMatrix();

  }

  /*protected TextRenderer getOrCreateTextRenderer(Font font) {
    // See discussion
    //
    // https://forum.jogamp.org/TextRenderer-crash-the-JVM-after-removing-then-adding-a-canvas-from-a-AWT-or-Swing-layout-td4041660.html
    //
    // Despite not efficient, the need to deal with removing/adding a canvas requires not keeping a
    // cache of the already defined strings. Indeed, in case the GLContext get destroyed, the
    // inherent textures are lost and text is replaced by black pixels.
    //
    // As there is no way to invalidate the TextRenderer cache (stringLocations is private), we
    // simply re-create
    // a new TextRenderer as soon as we want draw a text.

    TextRenderer renderer = null;
    // TextRenderer renderer = txtRendererMap.get(font);

    if (renderer == null) {
      renderer = new TextRenderer(toAWT(font), true, true, null);
      // renderer.setSmoothing(false);// some GPU do not handle smoothing well
      // renderer.setUseVertexArrays(false); // some GPU do not handle VBO properly

      // txtRendererMap.put(font, renderer);
    }
    return renderer;
  }*/

  // protected Map<Font, TextRenderer> txtRendererMap = new HashMap<>();


  /**
   * Render 2D text at the given 3D position using a font as supported by
   * {@link #glutBitmapString(int, String)}.
   * 
   * @see {@link Font} to know the set of font name and size supported by this method.
   */
  @Override
  public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c) {
    color(c);
    raster(p, null);
    glutBitmapString(axisFont.getCode(), label);
  }

  @Override
  public int glutBitmapLength(int font, String string) {
    return 0;//glutBitmapLength(font, string);
  }

  @Override
  public int getTextLengthInPixels(int font, String string) {
    return getTextLengthInPixels(Font.getById(font), string);
  }

  @Override
  public int getTextLengthInPixels(Font font, String string) {
    
    // Try to get text width using onscreen graphics
    ICanvas c = getCanvas();
    if (c instanceof Component) {
      Graphics g = ((Component) c).getGraphics();
      if (g != null && font != null) {
        g.setFont(toAWT(font));

        FontMetrics fm = g.getFontMetrics();
        if (fm != null) {
          return fm.stringWidth(string);
        }
      }
    }
    
    // Try to get text width using text renderer offscreen
    //if (gl != null && getContext().isCurrent()) {
    //  TextRenderer renderer = getOrCreateTextRenderer(font);
    //  Rectangle2D r = renderer.getBounds(string);
    //  return (int) r.getWidth();
    //} 

    // Otherwise use a fallback image to get a graphic context
    
    if (textLengthFallbackImage == null) {
      textLengthFallbackImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
      fontMetricsFallback = textLengthFallbackImage.createGraphics().getFontMetrics();
    }
    if (fontMetricsFallback != null) {
      return fontMetricsFallback.stringWidth(string);
    } else {
      return -1;
    }
    
  }


  private java.awt.Font toAWT(Font font) {
    return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, font.getHeight());
  }

  // GL LISTS

  @Override
  public int glGenLists(int range) {
    return GL11.glGenLists(range);
  }

  @Override
  public void glNewList(int list, int mode) {
    GL11.glNewList(list, mode);
  }

  @Override
  public void glNewList(int list, ListMode mode) {
    switch (mode) {
      case COMPILE:
        GL11.glNewList(list, GL_COMPILE);
      case COMPILE_AND_EXECUTE:
        GL11.glNewList(list, GL_COMPILE_AND_EXECUTE);
    }
  }

  @Override
  public void glEndList() {
    GL11.glEndList();
  }

  @Override
  public void glCallList(int list) {
    GL11.glCallList(list);
  }

  @Override
  public boolean glIsList(int list) {
    return GL11.glIsList(list);
  }

  @Override
  public void glDeleteLists(int list, int range) {
    GL11.glDeleteLists(list, range);
  }

  // GLU

  @Override
  public void gluDisk(double inner, double outer, int slices, int loops) {
    //GLUquadric qobj = gluNewQuadric();
    //gluDisk(qobj, inner, outer, slices, loops);
  }

  @Override
  public void glutSolidSphere(double radius, int slices, int stacks) {
    //glutSolidSphere(radius, slices, stacks);
  }

  @Override
  public void gluSphere(double radius, int slices, int stacks) {
    //GLUquadric qobj = gluNewQuadric();
   // gluSphere(qobj, radius, slices, stacks);
    throw new NotImplementedException("GLU not in LWJGL");
  }

  @Override
  public void gluCylinder(double base, double top, double height, int slices, int stacks) {
    //Cylinder qobj = new Cylinder();
    //gluCylinder(null, base, top, height, slices, stacks);
    throw new NotImplementedException("GLU not in LWJGL");

  }

  @Override
  public void glutSolidCube(float size) {
    //glutSolidCube(size);
    throw new NotImplementedException("GLUT not in LWJGL");

  }

  @Override
  public void glutSolidTeapot(float scale) {
    //glutSolidTeapot(scale);
    throw new NotImplementedException("GLUT not in LWJGL");

  }

  @Override
  public void glutWireTeapot(float scale) {
    //glutWireTeapot(scale);
    throw new NotImplementedException("GLUT not in LWJGL");
  }


  // GL FEEDBACK BUFER


  @Override
  public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
    GL11.glFeedbackBuffer(type, buffer);
  }

  @Override
  public int glRenderMode(int mode) {
    return GL11.glRenderMode(mode);
  }

  @Override
  public int glRenderMode(RenderMode mode) {
    switch (mode) {
      case RENDER:
        return glRenderMode(GL_RENDER);
      case SELECT:
        return glRenderMode(GL_SELECT);
      case FEEDBACK:
        return glRenderMode(GL_FEEDBACK);
    }
    throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
  }

  @Override
  public void glPassThrough(float token) {
    GL11.glPassThrough(token);
  }

  // GL STENCIL BUFFER

  @Override
  public void glStencilFunc(StencilFunc func, int ref, int mask) {
    switch (func) {
      case GL_ALWAYS:
        GL11.glStencilFunc(GL_ALWAYS, ref, mask);
        break;
      case GL_EQUAL:
        GL11.glStencilFunc(GL_EQUAL, ref, mask);
        break;
      case GL_GREATER:
        GL11.glStencilFunc(GL_GREATER, ref, mask);
        break;
      case GL_GEQUAL:
        GL11.glStencilFunc(GL_GEQUAL, ref, mask);
        break;
      case GL_LEQUAL:
        GL11.glStencilFunc(GL_LEQUAL, ref, mask);
        break;
      case GL_LESS:
        GL11.glStencilFunc(GL_LESS, ref, mask);
        break;
      case GL_NEVER:
        GL11.glStencilFunc(GL_NEVER, ref, mask);
        break;
      case GL_NOTEQUAL:
        GL11.glStencilFunc(GL_NOTEQUAL, ref, mask);
        break;

      default:
        throw new IllegalArgumentException("Unknown enum value for StencilFunc: " + func);
    }
  }

  @Override
  public void glStencilMask(int mask) {
    GL11.glStencilMask(mask);
  }

  @Override
  public void glStencilMask_True() {
    GL11.glStencilMask(GL_TRUE);
  }

  @Override
  public void glStencilMask_False() {
    GL11.glStencilMask(GL_FALSE);
  }


  @Override
  public void glStencilOp(StencilOp fail, StencilOp zfail, StencilOp zpass) {
    GL11.glStencilOp(toInt(fail), toInt(zfail), toInt(zpass));
  }

  @Override
  public void glClearStencil(int s) {
    GL11.glClearStencil(s);
  }

  protected int toInt(StencilOp fail) {
    switch (fail) {
      case GL_DECR:
        return GL_DECR;
      case GL_INCR:
        return GL_INCR;
      case GL_INVERT:
        return GL_INVERT;
      case GL_KEEP:
        return GL_KEEP;
      case GL_REPLACE:
        return GL_REPLACE;
      case GL_ZERO:
        return GL_ZERO;
      default:
        throw new IllegalArgumentException("Unknown enum value for StencilOp: " + fail);
    }
  }


  // GL VIEWPOINT

  @Override
  public void glOrtho(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    GL11.glOrtho(left, right, bottom, top, near_val, far_val);
  }

  @Override
  public void gluOrtho2D(double left, double right, double bottom, double top) {
    GLU.gluOrtho2D((float)left, (float)right, (float)bottom, (float)top);
  }

  @Override
  public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
    GLU.gluPerspective((float)fovy, (float)aspect, (float)zNear, (float)zFar);
  }

  @Override
  public void glFrustum(double left, double right, double bottom, double top, double zNear,
      double zFar) {
    GL11.glFrustum(left, right, bottom, top, zNear, zFar);
  }

  @Override
  public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY,
      float centerZ, float upX, float upY, float upZ) {
    GLU.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
  }

  @Override
  public void glViewport(int x, int y, int width, int height) {
    GL11.glViewport(x, y, width, height);
  }

  @Override
  public void glClipPlane(int plane, double[] equation) {
    // Array.print("NativePainter : glClipPlane : " + plane + " : ", equation);
    GL11.glClipPlane(plane, equation);
  }

  @Override
  public void glEnable_ClipPlane(int plane) {
    glEnable(clipPlaneId(plane));
  }

  /** Return the GL clip plane ID according to an ID in [0;5] */
  @Override
  public int clipPlaneId(int id) {
    switch (id) {
      case 0:
        return GL_CLIP_PLANE0;
      case 1:
        return GL_CLIP_PLANE1;
      case 2:
        return GL_CLIP_PLANE2;
      case 3:
        return GL_CLIP_PLANE3;
      case 4:
        return GL_CLIP_PLANE4;
      case 5:
        return GL_CLIP_PLANE5;
      default:
        throw new IllegalArgumentException("Expect a plane ID in [0;5]");
    }
  }

  @Override
  public void glDisable_ClipPlane(int plane) {
    glDisable(clipPlaneId(plane));
  }


  @Override
  public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] objPos,
      int objPos_offset) {
    
    FloatBuffer modelB = FloatBuffer.wrap(model);
    FloatBuffer projB = FloatBuffer.wrap(proj);
    FloatBuffer objPosB = FloatBuffer.wrap(objPos);
    IntBuffer viewB = IntBuffer.wrap(view);
    
    return GLU.gluUnProject(winX, winY, winZ, modelB, projB, viewB, objPosB);
  }

  @Override
  public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] winPos,
      int winPos_offset) {
    
    FloatBuffer modelB = FloatBuffer.wrap(model);
    FloatBuffer projB = FloatBuffer.wrap(proj);
    FloatBuffer winPosB = FloatBuffer.wrap(winPos);
    IntBuffer viewB = IntBuffer.wrap(view);

    return GLU.gluProject(objX, objY, objZ, modelB, projB, viewB, winPosB);
  }

  // GL GET

  @Override
  public void glGetIntegerv(int pname, int[] data, int data_offset) {
    GL11.glGetIntegerv(pname, data);//, data_offset);
    
    if(data_offset!=0)
      System.err.println("LWJGLPainter.glGet*: non zero offset not supported. Got " + data_offset);
  }

  @Override
  public void glGetDoublev(int pname, double[] params, int params_offset) {
    GL11.glGetDoublev(pname, params);//, params_offset);
    
    if(params_offset!=0)
      System.err.println("LWJGLPainter.glGet*: non zero offset not supported. Got " + params_offset);

  }

  @Override
  public void glGetFloatv(int pname, float[] data, int data_offset) {
    GL11.glGetFloatv(pname, data);//, data_offset);
    
    if(data_offset!=0)
      System.err.println("LWJGLPainter.glGet*: non zero offset not supported. Got " + data_offset);

  }

  @Override
  public void glDepthFunc(int func) {
    GL11.glDepthFunc(func);
  }

  @Override
  public void glDepthRangef(float near, float far) {
    GL11.glDepthRange(near, far);
  }

  @Override
  public void glBlendFunc(int sfactor, int dfactor) {
    GL11.glBlendFunc(sfactor, dfactor);
  }


  @Override
  public void glHint(int target, int mode) {
    GL11.glHint(target, mode);
  }

  // GL LIGHTS

  @Override
  public void glShadeModel(ColorModel colorModel) {
    if (ColorModel.SMOOTH.equals(colorModel)) {
      GL11.glShadeModel(GL_SMOOTH);
    } else if (ColorModel.FLAT.equals(colorModel)) {
      GL11.glShadeModel(GL_FLAT);
    } else {
      throw new IllegalArgumentException("Unsupported setting : '" + colorModel + "'");
    }
  }

  @Override
  public void glShadeModel(int mode) {
    GL11.glShadeModel(mode);
  }

  @Override
  public void glShadeModel_Smooth() {
    GL11.glShadeModel(GL_SMOOTH);
  }

  @Override
  public void glShadeModel_Flat() {
    GL11.glShadeModel(GL_FLAT);
  }


  @Override
  public void glMaterialfv(int face, int pname, float[] params, int params_offset) {
    GL11.glMaterialfv(face, pname, params);
  }

  @Override
  public void glNormal3f(float nx, float ny, float nz) {
    GL11.glNormal3f(nx, ny, nz);
  }



  @Override
  public void glLightModeli(int mode, int value) {
    GL11.glLightModeli(mode, value);
  }

  @Override
  public void glLightModelfv(int mode, float[] value) {
    GL11.glLightModelfv(mode, value);
  }

  @Override
  public void glLightModel(LightModel model, boolean value) {
    if (LightModel.LIGHT_MODEL_TWO_SIDE.equals(model)) {
      GL11.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, value ? GL_TRUE : GL_FALSE);
    } else if (LightModel.LIGHT_MODEL_LOCAL_VIEWER.equals(model)) {
      GL11.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, value ? GL_TRUE : GL_FALSE);
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  @Override
  public void glLightModel(LightModel model, Color color) {
    if (LightModel.LIGHT_MODEL_AMBIENT.equals(model)) {
      GL11.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, color.toArray());
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  @Override
  public void glLightf(int light, Attenuation.Type attenuationType, float value) {
    if (Attenuation.Type.CONSTANT.equals(attenuationType)) {
      GL11.glLightf(light, GL_CONSTANT_ATTENUATION, value);
    } else if (Attenuation.Type.LINEAR.equals(attenuationType)) {
      GL11.glLightf(light, GL_LINEAR_ATTENUATION, value);
    } else if (Attenuation.Type.QUADRATIC.equals(attenuationType)) {
      GL11.glLightf(light, GL_QUADRATIC_ATTENUATION, value);
    }
  }

  @Override
  public void glLightf(int light, int pname, float value) {
    GL11.glLightf(lightId(light), pname, value);
  }

  @Override
  public void glLightfv(int light, int pname, float[] params, int params_offset) {
    GL11.glLightfv(lightId(light), pname, params);//, params_offset);
  }

  @Override
  public void glLight_Position(int lightId, float[] positionZero) {
    glLightfv(lightId, GL_POSITION, positionZero, 0);
  }

  @Override
  public void glLight_Ambiant(int lightId, Color ambiantColor) {
    glLightfv(lightId, GL_AMBIENT, ambiantColor.toArray(), 0);
  }

  @Override
  public void glLight_Diffuse(int lightId, Color diffuseColor) {
    glLightfv(lightId, GL_DIFFUSE, diffuseColor.toArray(), 0);
  }

  @Override
  public void glLight_Specular(int lightId, Color specularColor) {
    glLightfv(lightId, GL_SPECULAR, specularColor.toArray(), 0);
  }

  @Override
  public void glLight_Shininess(int lightId, float value) {
    glLightf(lightId, GL_SHININESS, value);
  }

  @Override
  public void glEnable_Light(int light) {
    glEnable(lightId(light));
  }

  @Override
  public void glDisable_Light(int light) {
    glEnable(lightId(light));
  }

  protected int lightId(int id) {
    switch (id) {
      case 0:
        return GL_LIGHT0;
      case 1:
        return GL_LIGHT1;
      case (2):
        return GL_LIGHT2;
      case 3:
        return GL_LIGHT3;
      case 4:
        return GL_LIGHT4;
      case 5:
        return GL_LIGHT5;
      case 6:
        return GL_LIGHT6;
      case 7:
        return GL_LIGHT7;
    }
    throw new IllegalArgumentException("Unsupported light ID '" + id + "'");
  }

  // GL OTHER

  @Override
  public void glClearColor(float red, float green, float blue, float alpha) {
    GL11.glClearColor(red, green, blue, alpha);
  }

  @Override
  public void glClearDepth(double d) {
    GL11.glClearDepth(d);
  }

  @Override
  public void glClear(int mask) {
    GL11.glClear(mask);
  }

  @Override
  public void glClearColorAndDepthBuffers() {
    GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }


  // GL PICKING


  @Override
  public void glInitNames() {
    GL11.glInitNames();
  }

  @Override
  public void glLoadName(int name) {
    GL11.glLoadName(name);
  }

  @Override
  public void glPushName(int name) {
    GL11.glPushName(name);
  }

  @Override
  public void glPopName() {
    GL11.glPopName();
  }

  @Override
  public void glSelectBuffer(int size, IntBuffer buffer) {
    GL11.glSelectBuffer(buffer);
  }

  @Override
  public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport,
      int viewport_offset) {
    
    GLU.gluPickMatrix((float)x, (float)y, (float)delX, (float)delY, IntBuffer.wrap(viewport));
  }

  @Override
  public void glFlush() {
    GL11.glFlush();
  }



  @Override
  public void glEvalCoord2f(float u, float v) {
    GL11.glEvalCoord2f(u, v);
  }

  @Override
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, FloatBuffer points) {
    GL11.glMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
  }


  /* *********************************************************************** */

  /* ******************** SHORTCUTS TO GL CONSTANTS ************************ */

  /* *********************************************************************** */

  @Override
  public void glEnable_PolygonOffsetFill() {
    GL11.glEnable(GL_POLYGON_OFFSET_FILL);
  }

  @Override
  public void glDisable_PolygonOffsetFill() {
    GL11.glDisable(GL_POLYGON_OFFSET_FILL);
  }

  @Override
  public void glEnable_PolygonOffsetLine() {
    GL11.glEnable(GL_POLYGON_OFFSET_LINE);
  }

  @Override
  public void glDisable_PolygonOffsetLine() {
    GL11.glDisable(GL_POLYGON_OFFSET_LINE);
  }

  @Override
  public void glEnable_Blend() {
    GL11.glEnable(GL_BLEND);
  }

  @Override
  public void glDisable_Blend() {
    GL11.glDisable(GL_BLEND);
  }

  @Override
  public void glMatrixMode_ModelView() {
    GL11.glMatrixMode(GL_MODELVIEW);
  }

  @Override
  public void glMatrixMode_Projection() {
    GL11.glMatrixMode(GL_PROJECTION);
  }

  @Override
  public void glBegin_Polygon() {
    GL11.glBegin(GL_POLYGON);
  }

  @Override
  public void glBegin_Quad() {
    GL11.glBegin(GL_QUADS);
  }

  @Override
  public void glBegin_Triangle() {
    GL11.glBegin(GL_TRIANGLES);
  }

  @Override
  public void glBegin_Point() {
    GL11.glBegin(GL_POINTS);
  }

  @Override
  public void glBegin_LineStrip() {
    GL11.glBegin(GL_LINE_STRIP);
  }

  @Override
  public void glBegin_LineLoop() {
    GL11.glBegin(GL_LINE_LOOP);
  }

  @Override
  public void glBegin_Line() {
    GL11.glBegin(GL_LINES);
  }

  @Override
  public void glEnable_LineStipple() {
    GL11.glEnable(GL_LINE_STIPPLE);
  }

  @Override
  public void glDisable_LineStipple() {
    GL11.glDisable(GL_LINE_STIPPLE);
  }


  @Override
  public void glEnable_CullFace() {
    GL11.glEnable(GL_CULL_FACE);
  }

  @Override
  public void glDisable_CullFace() {
    GL11.glDisable(GL_CULL_FACE);
  }

  @Override
  public void glFrontFace_ClockWise() {
    GL11.glFrontFace(GL_CCW);
  }

  @Override
  public void glCullFace_Front() {
    GL11.glCullFace(GL_FRONT);
  }

  @Override
  public void glDisable_Lighting() {
    GL11.glDisable(GL_LIGHTING);
  }

  @Override
  public void glEnable_Lighting() {
    GL11.glEnable(GL_LIGHTING);
  }

  @Override
  public void glEnable_ColorMaterial() {
    GL11.glEnable(GL_COLOR_MATERIAL);
  }


  @Override
  public void glMaterial(MaterialProperty material, Color color, boolean isFront) {
    if (isFront) {
      GL11.glMaterialfv(GL_FRONT, materialProperty(material), color.toArray());
    } else {
      GL11.glMaterialfv(GL_BACK, materialProperty(material), color.toArray());
    }
  }

  @Override
  public void glMaterial(MaterialProperty material, float[] color, boolean isFront) {
    if (isFront) {
      GL11.glMaterialfv(GL_FRONT, materialProperty(material), color);
    } else {
      GL11.glMaterialfv(GL_BACK, materialProperty(material), color);
    }
  }


  protected int materialProperty(MaterialProperty material) {
    switch (material) {
      case AMBIENT:
        return GL_AMBIENT;
      case DIFFUSE:
        return GL_DIFFUSE;
      case SPECULAR:
        return GL_SPECULAR;
      case SHININESS:
        return GL_SHININESS;
    }
    throw new IllegalArgumentException("Unsupported property '" + material + "'");
  }


  @Override
  public void glEnable_PointSmooth() {
    GL11.glEnable(GL_POINT_SMOOTH);
  }

  @Override
  public void glHint_PointSmooth_Nicest() {
    GL11.glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
  }

  @Override
  public void glDepthFunc(DepthFunc func) {
    switch (func) {
      case GL_ALWAYS:
        GL11.glDepthFunc(GL_ALWAYS);
        break;
      case GL_NEVER:
        GL11.glDepthFunc(GL_NEVER);
        break;
      case GL_EQUAL:
        GL11.glDepthFunc(GL_EQUAL);
        break;
      case GL_GEQUAL:
        GL11.glDepthFunc(GL_GEQUAL);
        break;
      case GL_GREATER:
        GL11.glDepthFunc(GL_GREATER);
        break;
      case GL_LEQUAL:
        GL11.glDepthFunc(GL_LEQUAL);
        break;
      case GL_LESS:
        GL11.glDepthFunc(GL_LESS);
        break;
      case GL_NOTEQUAL:
        GL11.glDepthFunc(GL_NOTEQUAL);
        break;
      default:
        throw new RuntimeException("Enum value not supported : " + func);
    }
  }

  @Override
  public void glEnable_DepthTest() {
    GL11.glEnable(GL_DEPTH_TEST);
  }

  @Override
  public void glDisable_DepthTest() {
    GL11.glDisable(GL_DEPTH_TEST);
  }

  @Override
  public void glEnable_Stencil() {
    GL11.glEnable(GL_STENCIL_TEST);
  }

  @Override
  public void glDisable_Stencil() {
    GL11.glDisable(GL_STENCIL_TEST);
  }

}
