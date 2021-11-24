package org.jzy3d.painters;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.image.AWTImageConvert;
import org.jzy3d.plot3d.rendering.lights.Attenuation;
import org.jzy3d.plot3d.rendering.lights.LightModel;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;
import jgl.context.gl_util;
import jgl.glu.GLUquadricObj;
import jgl.wt.awt.GL;
import jgl.wt.awt.GLCanvas;
import jgl.wt.awt.GLU;
import jgl.wt.awt.GLUT;

public class EmulGLPainter extends AbstractPainter implements IPainter {
  protected GL gl;
  protected GLU glu;
  protected GLUT glut;

  public GL getGL() {
    return gl;
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
  }

  @Override
  public Object acquireGL() {
    return getGL();
  }

  @Override
  public void releaseGL() {
    // in JOGL :
    // getCurrentContext(canvas).release();
  }

  @Override
  public void configureGL(Quality quality) {
    // Activate Depth buffer
    if (quality.isDepthActivated()) {
      glEnable_DepthTest();
      glDepthFunc(DepthFunc.GL_LESS);
    } else {
      glDisable_DepthTest();
    }
    
    // Blending : more beautifull with jGL without this
    /** Default for SRC is 1 : {@link gl_colorbuffer.BlendSrc}*/
    /** Default for DST is 0 : {@link gl_colorbuffer.BlendDst}*/
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    // gl.glBlendFunc(GL.GL_DST_ALPHA, GL.GL_NONE);

    /*
     * gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_DST_ALPHA); gl.glBlendFunc(GL.GL_SRC_ALPHA,
     * GL.GL_ONE_MINUS_SRC_COLOR); gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_DST_COLOR);
     * gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_SRC_ALPHA_SATURATE); gl.glBlendFunc(GL.GL_DST_ALPHA,
     * GL.GL_ONE_MINUS_SRC_ALPHA);
     */


    // GL_SRC_ALPHA_SATURATE
    // on/off is handled by each viewport (camera or image)

    // Activate tranparency
    if (quality.isAlphaActivated()) {
      glEnable_Blend();
      gl.glEnable(GL.GL_ALPHA_TEST);

      if (quality.isDisableDepthBufferWhenAlpha()) {
        // Disable depth test to keeping pixels of
        // "what's behind a polygon" when drawing with
        // alpha
        glDisable_DepthTest();
        //gl.glDisable(GL.GL_DEPTH_TEST);
      }
    } else {
      
      gl.glDisable(GL.GL_ALPHA_TEST);
    }

    // Make smooth colors for polygons (interpolate color between points)
    glShadeModel(quality.getColorModel());
    
    // Make smoothing setting
    if (quality.isSmoothPolygon()) {
      gl.glEnable(GL.GL_POLYGON_SMOOTH);
      // gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_POLYGON_SMOOTH);

    if (quality.isSmoothLine()) {
      gl.glEnable(GL.GL_LINE_SMOOTH);
      // gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_LINE_SMOOTH);

    if (quality.isSmoothPoint()) {
      gl.glEnable(GL.GL_POINT_SMOOTH);
      // gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
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
    gl.glColor3f(r, b, b);
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

  /**
   * @see {@link #glEnable_PolygonOffsetFill()}
   */
  @Override
  public void glPolygonOffset(float factor, float units) {
    // throw new NotImplementedException(OFFSET_FILL_NOT_IMPLEMENTED);
    // gl.glPolygonOffset(factor, units); // handle stippling
  }

  String OFFSET_FILL_NOT_IMPLEMENTED = "not in jGL. \n"
      + "Was added to OpenGL 2 (https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glPolygonOffset.xhtml). \n"
      + "You may desactivate offset fill with drawable.setPolygonOffsetFillEnable(false). \n"
      + "More here : https://github.com/jzy3d/jGL/issues/3";

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

  /**
   * glRasterPos3f not implemented by {@link GL}.
   * 
   * This method will fallback on {@link GL#glRasterPos2f(float, float)} or trigger a
   * {@link NotImplementedException} in case z value is not equal to 0.
   */
  @Override
  public void glRasterPos3f(float x, float y, float z) {
    if (!(z == 0 || Float.isNaN(z)))
      throw new NotImplementedException("z:" + z);
    else
      gl.glRasterPos2f(x, y);
  }

  /**
   * Not working yet.
   */
  @Override
  public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {

    // pixels[x][y][i]

    /*
     * if (s == 8) return get_pixel(x, y, i, (byte[][][]) pixels); <<< if (s == 16) return
     * get_pixel(x, y, i, (short[][][]) pixels); if (s == 32) return get_pixel(x, y, i, (int[][][])
     * pixels); if (s == 64) return get_pixel(x, y, i, (float[][][]) pixels);
     */

    /*
     * Data is read from data as a sequence of signed or unsigned bytes, signed or unsigned shorts,
     * signed or unsigned integers, or single-precision floating-point values, depending on type.
     * 
     * When type is one of GL_UNSIGNED_BYTE, GL_BYTE, GL_UNSIGNED_SHORT, GL_SHORT, GL_UNSIGNED_INT,
     * GL_INT, or GL_FLOAT each of these bytes, shorts, integers, or floating-point values is
     * interpreted as one color or depth component, or one index, depending on format.
     * 
     * When type is one of GL_UNSIGNED_BYTE_3_3_2, GL_UNSIGNED_SHORT_5_6_5,
     * GL_UNSIGNED_SHORT_4_4_4_4, GL_UNSIGNED_SHORT_5_5_5_1, GL_UNSIGNED_INT_8_8_8_8, or
     * GL_UNSIGNED_INT_10_10_10_2, each unsigned value is interpreted as containing all the
     * components for a single pixel, with the color components arranged according to format.
     * 
     * When type is one of GL_UNSIGNED_BYTE_2_3_3_REV, GL_UNSIGNED_SHORT_5_6_5_REV,
     * GL_UNSIGNED_SHORT_4_4_4_4_REV, GL_UNSIGNED_SHORT_1_5_5_5_REV, GL_UNSIGNED_INT_8_8_8_8_REV, or
     * GL_UNSIGNED_INT_2_10_10_10_REV, each unsigned value is interpreted as containing all color
     * components, specified by format, for a single pixel in a reversed order.
     * 
     * Indices are always treated individually.
     * 
     * Color components are treated as groups of one, two, three, or four values, again based on
     * format.
     * 
     * Both individual indices and groups of components are referred to as pixels.
     * 
     * If type is GL_BITMAP, the data must be unsigned bytes, and format must be either
     * GL_COLOR_INDEX or GL_STENCIL_INDEX.
     * 
     * Each unsigned byte is treated as eight 1-bit pixels, with bit ordering determined by
     * GL_UNPACK_LSB_FIRST (see glPixelStore).
     */

    boolean testWithImage = false;

    if (testWithImage) {

      Image img = null;
      try {
        img = ImageIO.read(new File("colorbar-crop.png"));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      int w = img.getWidth(null);
      int h = img.getHeight(null);
      int[] pix = AWTImageConvert.getImagePixels(img, w, h);
      int[][][] pxl = toPixels(w, h, pix);

      gl.glDrawPixels(w, h, format, GL.GL_INT, pxl);

    } else {

      // CALLED WITH : GL.GL_RGBA, GL.GL_UNSIGNED_BYTE
      // pixels.flip();
      byte[] imgBytes = new byte[pixels.remaining()];
      ((ByteBuffer) pixels).get(imgBytes);

      int[][][] pxl = toPixels(width, height, imgBytes);

      // System.out.println("width:"+width+ " w:" + w + " height:"+height+ " h:" + h);
      // pixels.

      // GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE

      // gl.glDrawPixels(width, height, format, GL.GL_INT, pxl);//.array());

      // will break in jGL since it is smaller h/w are smaller than viewport,
      // which was set to higher dimension (600*500) : drawpixel is trying to update
      // the part of the colorbuffer that is in width-w and height-h which fails if
      // width/height
      // are smaller than image
      gl.glDrawPixels(width, height, format, GL.GL_INT, pxl);

      // gl_context.gl_raster_pos n'ayant pas d'implem, le glDrawPixels dessign à
      // partir de 0,0

    }

    // gl.glDrawPixels(width, height, format, type, pixels.array());
  }

  // MOVE FOLLOWING TO GLImage

  /**
   * Convert a pixel array with int Values to a pixel array with splitted color components for jGL
   * glDrawPixels
   */
  private int[][][] toPixels(int w, int h, int[] pix) {
    // height, width
    int[][][] pxl = new int[h][w][4];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        // (j - y) * scansize + (i - x) + off
        int pos = y * w + x;// x*w+y;//

        if (pos < pix.length) {

          int color = pix[pos];

          pxl[y][x][0] = gl_util.ItoR(color);
          pxl[y][x][1] = gl_util.ItoG(color);
          pxl[y][x][2] = gl_util.ItoB(color);
          pxl[y][x][3] = gl_util.ItoA(color);
        }
      }
    }
    return pxl;
  }

  static int R = 0;
  static int G = 1;
  static int B = 2;
  static int A = 3;

  /**
   * 2 soucis la valeur des couleurs est mal récupérée pour les canaux G et B (R est OK, alpha
   * aussi) l'image est à l'envers
   * 
   * @param w
   * @param h
   * @param pix
   * @return
   */
  private int[][][] toPixels(int w, int h, byte[] pix) {
    boolean hasAlpha = true;
    boolean isFlipped = true; // because image in byte array was flipped!!

    // height, width
    int[][][] pxl = new int[h][w][4];

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        if (hasAlpha) {
          int pos = (y * w + x) * 4; // SIZE OF INT THAT FED THE BYTEBUFFER

          int yy = isFlipped ? h - y - 1 : y;

          // GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE

          // Color ID in array is defined based on
          // gl_colorbuffer.draw_pixels
          pxl[yy][x][R] = pix[pos + R];// Math.max((pix[pos+0] & 0xff) ,0);// & 0xff)-128;
          pxl[yy][x][G] = Math.max((pix[pos + G] & 0xff) - 128, 0);
          // green : 127, yellow : 255
          // ((int)pix[pos+1])>>24;//jaune?? devrait être vert
          //

          pxl[yy][x][B] = Math.max((pix[pos + B] & 0xff) - 128, 0);
          pxl[yy][x][A] = pix[pos + A] & 0xff;

          // System.out.println("RED : " + pxl[y][x][R]);
          // System.out.println("GREEN : " + pxl[y][x][G]);
          // System.out.println("BLUE : " + pxl[y][x][B]);
          // System.out.println("ALPHA : " + pxl[y][x][A]);

          // then colorbuffer read array and build single color value with
          // int color = ((a << 24) | (r << 16) | (g << 8) | b);

        } else {
          int pos = (y * w + x) * 3;

          pxl[y][x][R] = pix[pos + 0];
          pxl[y][x][G] = pix[pos + 1];
          pxl[y][x][B] = pix[pos + 2];
          pxl[y][x][A] = 255;// pix[pos+3];

        }
      }
    }
    return pxl;
  }

  /*
   * private int[][][] toPixels(int w, int h, int[] pix) { // height, width int[][][] pxl = new
   * int[w][h][4];
   * 
   * for (int y = 0; y < h; y++) { for (int x = 0; x < w; x++) {
   * 
   * int pos = x*w+y;//y*h+x; if(pos<pix.length) {
   * 
   * int color = pix[pos]; //int color = pix[x*w+y];
   * 
   * pxl[x][y][0] = gl_util.ItoR(color); pxl[x][y][1] = gl_util.ItoG(color); pxl[x][y][2] =
   * gl_util.ItoB(color); pxl[x][y][3] = gl_util.ItoA(color); } } } return pxl; }
   */

  /**
   * glPixelZoom is not implemented by {@link GL}. This method will do nothing but triggering a
   * {@link NotImplementedException} in case x and y zoom factor are not both equal to 1 (i.e. in
   * case a zoom is needed).
   * 
   */
  @Override
  public void glPixelZoom(float xfactor, float yfactor) {
    if (xfactor != 1 || yfactor != 1)
      throw new NotImplementedException("x:" + xfactor + "y:" + yfactor);
    // gl.glPixelZoom(xfactor, yfactor);
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
    }
    throw new IllegalArgumentException("Unsupported mode '" + store + "'");
  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      byte[] bitmap, int bitmap_offset) {
    throw new NotImplementedException();
    // gl.glBitmap(width, height, xorig, yorig, xmove, ymove, bitmap,
    // bitmap_offset);
  }

  /**
   * Not implemented yet.
   * 
   * @see {@link #glutBitmapString(Font, String, Coord3d, Color)} as a convenient replacement.
   */
  @Override
  public void glutBitmapString(int font, String string) {
    throw new NotImplementedException();
    // glut.glutBitmapString(font, string);

  }
  
  /**
   * A very failing implementation. SHOULD SUPPORT AWT BufferedImage in EmulGL - or reverse converse
   */
  @Override
  public void drawImage(ByteBuffer imageBuffer, int imageWidth, int imageHeight,
      Coord2d pixelZoom, Coord3d imagePosition) {
    glPixelZoom(pixelZoom.x, pixelZoom.y);
    glRasterPos3f(imagePosition.x, imagePosition.y, 0);
    // painter.glRasterPos2f(xpict, ypict);

    synchronized (imageBuffer) { // we don't want to draw image while it is being set by setImage
      glDrawPixels(imageWidth, imageHeight, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
    }
    
    
   // getGL().appendImageToDraw(legendImage, imagePosition.x, imagePosition.y);

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
    if (font == Font.BITMAP_HELVETICA_12) {
      return 6 * string.length();
    } else if (font == Font.BITMAP_HELVETICA_18) {
      return 9 * string.length();
    } else if (font == Font.BITMAP_TIMES_ROMAN_10) {
      return 5 * string.length();
    } else if (font == Font.BITMAP_TIMES_ROMAN_24) {
      return 12 * string.length();
    } 
    return 6 * string.length();
  }

  boolean allowAutoDetectTextLength = true;

  @Override
  public int getTextLengthInPixels(int font, String string) {
    Font fnt = Font.getById(font);

    return getTextLengthInPixels(fnt, string);
  }

  /**
   * Text length processing based on AWT {@link FontMetrics} obtained
   * by retrieving the graphic context of the {@link GLCanvas}.
   * 
   * In case no graphics is available
   */
  @Override
  public int getTextLengthInPixels(Font font, String string) {
    EmulGLCanvas c = (EmulGLCanvas) getCanvas();
    if (c != null) {
      Graphics g = c.getGraphics();
      if (g != null && font != null) {
        g.setFont(toAWT(font)); // TODO : cache?

        FontMetrics fm = g.getFontMetrics();
        if (fm != null) {
          return fm.stringWidth(string);
        }
      }
    }
    // fallback on glut
    return glutBitmapLength(font.getCode(), string);
  }

  /**
   * Replace {@link #glutBitmapString(int, String) which is the official OpenGL interface.
   * 
   * This alternative interface allows rendering text based on AWT Fonts which are drawn on top of
   * the GL Image.
   */
  @Override
  public void glutBitmapString(Font font, String label, Coord3d position, Color color) {
    glut.glutBitmapString(toAWT(font), label, position.x, position.y, position.z, color.r, color.g,
        color.b, 0);
  }
  
  @Override
  public void drawText(Font font, String label, Coord3d position, Color color, float rotation) {
    glut.glutBitmapString(toAWT(font), label, position.x, position.y, position.z, color.r, color.g,
        color.b, rotation);
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
      case COMPILE_AND_EXECUTE:
        glNewList(list, GL.GL_COMPILE_AND_EXECUTE);
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
    return gl.glIsList(list);
  }

  @Override
  public void glDeleteLists(int list, int range) {
    gl.glDeleteLists(list, range);
  }

  // GLU

  @Override
  public void gluDisk(double inner, double outer, int slices, int loops) {
    GLUquadricObj qobj = glu.gluNewQuadric();
    qobj.Normals = GLU.GLU_NONE; // https://github.com/jzy3d/jzy3d-api/issues/179
    glu.gluDisk(qobj, inner, outer, slices, loops);
  }

  @Override
  public void glutSolidSphere(double radius, int slices, int stacks) {
    glut.glutSolidSphere(radius, slices, stacks);
  }

  @Override
  public void glutSolidTeapot(float scale) {
    glut.glutSolidTeapot(scale);
  }
  
  @Override
  public void glutWireTeapot(float scale) {
    glut.glutWireTeapot(scale);
  }

  @Override
  public void gluSphere(double radius, int slices, int stacks) {
    GLUquadricObj qobj = glu.gluNewQuadric();
    glu.gluSphere(qobj, radius, slices, stacks);
  }

  @Override
  public void gluCylinder(double base, double top, double height, int slices, int stacks) {
    GLUquadricObj qobj = glu.gluNewQuadric();
    glu.gluCylinder(qobj, base, top, height, slices, stacks);
  }

  @Override
  public void glutSolidCube(float size) {
    glut.glutSolidCube(size);
  }

  // GL FEEDBACK BUFER

  @Override
  public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
    throw new NotImplementedException(
        "Not in jGL. Was added to OpenGL 2. https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glFeedbackBuffer.xml");
    // gl.glFeedbackBuffer(size, type, buffer);
  }

  @Override
  public int glRenderMode(int mode) {
    return gl.glRenderMode(mode);
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
    switch(func) {
      case GL_ALWAYS: gl.glStencilFunc(GL.GL_ALWAYS, ref, mask); break;
      case GL_EQUAL: gl.glStencilFunc(GL.GL_EQUAL, ref, mask); break;
      case GL_GREATER: gl.glStencilFunc(GL.GL_GREATER, ref, mask); break;
      case GL_GEQUAL: gl.glStencilFunc(GL.GL_GEQUAL, ref, mask); break;
      case GL_LEQUAL: gl.glStencilFunc(GL.GL_LEQUAL, ref, mask); break;
      case GL_LESS: gl.glStencilFunc(GL.GL_LESS, ref, mask); break;
      case GL_NEVER: gl.glStencilFunc(GL.GL_NEVER, ref, mask); break;
      case GL_NOTEQUAL: gl.glStencilFunc(GL.GL_NOTEQUAL, ref, mask); break;

      default: throw new IllegalArgumentException("Unknown enum value for StencilFunc: " + func);
    }
  }

  @Override
  public void glStencilMask(int mask) {
    gl.glStencilMask(mask);
    
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
    switch(fail) {
      case GL_DECR: return GL.GL_DECR;
      case GL_INCR: return GL.GL_INCR;
      case GL_INVERT: return GL.GL_INVERT;
      case GL_KEEP: return GL.GL_KEEP;
      case GL_REPLACE: return GL.GL_REPLACE;
      case GL_ZERO: return GL.GL_ZERO;
      default: throw new IllegalArgumentException("Unknown enum value for StencilOp: " + fail);
    }
  }
  
  @Override
  public void glStencilMask_True() {
    gl.glStencilMask(1);
  }

  @Override
  public void glStencilMask_False(){
    gl.glStencilMask(0);    
  }


  // GL VIEWPOINT

  @Override
  public void glOrtho(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    gl.glOrtho(left, right, bottom, top, near_val, far_val);
  }

  @Override
  public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
    glu.gluPerspective(fovy, aspect, zNear, zFar);
  }

  @Override
  public void glFrustum(double left, double right, double bottom, double top, double zNear,
      double zFar) {
    gl.glFrustum(left, right, bottom, top, zNear, zFar);
  }


  @Override
  public void gluLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY,
      float centerZ, float upX, float upY, float upZ) {
    glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
  }

  @Override
  public void glViewport(int x, int y, int width, int height) {
    gl.glViewport(x, y, width, height);
  }
  
  @Override
  public void glClipPlane(int plane, double[] equation) {
    gl.glClipPlane(GL.GL_CLIP_PLANE0, equation); 
  }
  
  @Override
  public void glEnable_ClipPlane(int plane) {
    gl.glEnable(clipPlaneId(plane));
  }

  @Override
  public void glDisable_ClipPlane(int plane) {
    gl.glDisable(clipPlaneId(plane));
  }
  
  /** Return the GL clip plane ID according to an ID in [0;5]*/
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
    // throw new NotImplementedException();

    double objX[] = new double[1];
    double objY[] = new double[1];
    double objZ[] = new double[1];

    boolean st = glu.gluUnProject(winX, winY, winZ, dbl(model),
        dbl(proj), view, objX, objY, objZ);

    objPos[0] = (float) objX[0];
    objPos[1] = (float) objY[0];
    objPos[2] = (float) objZ[0];

    return st;
  }

  protected double[] dbl(float[] values) {
    double[] dbl = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      dbl[i] = values[i];
    }
    return dbl;
  }

  @Override
  public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] winPos,
      int winPos_offset) {
    // throw new NotImplementedException();
    // glu.gluProject(objx, objy, objz, model, proj, viewport, winx, winy, winz)

    double[] modelD = new double[model.length];
    for (int i = 0; i < model.length; i++) {
      modelD[i] = model[i];
    }

    // double[] winy = new double[1];
    // double[] winz = new double[1];

    double[] projD = new double[proj.length];
    for (int i = 0; i < proj.length; i++) {
      projD[i] = proj[i];
    }

    double[] winx = new double[1];
    double[] winy = new double[1];
    double[] winz = new double[1];

    boolean out = glu.gluProject(objX, objY, objZ, modelD, projD, view,
        winx, winy, winz);

    // winPos[0], winPos[1], winPos[2];
    winPos[0] = (float) winx[0];
    winPos[1] = (float) winy[0];
    winPos[2] = (float) winz[0];

    return out;
  }

  // GL GET

  @Override
  public void glGetIntegerv(int pname, int[] data, int data_offset) {
    gl.glGetIntegerv(pname, data);
  }

  @Override
  public void glGetDoublev(int pname, double[] params, int params_offset) {
    gl.glGetDoublev(pname, params);
  }

  @Override
  public void glGetFloatv(int pname, float[] data, int data_offset) {
    gl.glGetFloatv(pname, data);
  }

  @Override
  public void glDepthFunc(int func) {
    gl.glDepthFunc(func);
  }


  @Override
  public void glDepthRangef(float near, float far) {
    //printGLDepthRange();
    gl.glDepthRange(near, far);
  }

  public void printGLDepthRange() {
    float[] params = new float[2];
    gl.glGetFloatv(GL.GL_DEPTH_RANGE, params);
    //Logger.getLogger(EmulGLPainter.class).info();
    Array.print(params);
  }


  @Override
  public void glBlendFunc(int sfactor, int dfactor) {
    gl.glBlendFunc(sfactor, dfactor);
  }

  @Override
  public void glHint(int target, int mode) {
    throw new NotImplementedException(
        "not in jGL. https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glHint.xml");
    // gl.glHint(target, mode);

  }

  // GL LIGHTS

  @Override
  public void glShadeModel(ColorModel colorModel) {
    if(ColorModel.SMOOTH.equals(colorModel)) {
      gl.glShadeModel(GL.GL_SMOOTH);
    }
    else if(ColorModel.FLAT.equals(colorModel)) {
      gl.glShadeModel(GL.GL_FLAT);
    }
    else {
      throw new IllegalArgumentException("Unsupported setting : '"+colorModel + "'");
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
    gl.glMaterialfv(face, pname, params);
  }

  @Override
  public void glNormal3f(float nx, float ny, float nz) {
    gl.glNormal3f(nx, ny, nz);
  }

  @Override
  public void glLightf(int light, Attenuation.Type attenuationType, float value) {
    if(Attenuation.Type.CONSTANT.equals(attenuationType)) {
      glLightf(light, GL.GL_CONSTANT_ATTENUATION, value);
    }
    else if(Attenuation.Type.LINEAR.equals(attenuationType)) {
      glLightf(light, GL.GL_LINEAR_ATTENUATION, value);
    }
    else if(Attenuation.Type.QUADRATIC.equals(attenuationType)) {
      glLightf(light, GL.GL_QUADRATIC_ATTENUATION, value);
    }
  }
  
  @Override
  public void glLightf(int light, int pname, float value) {
    gl.glLightf(lightId(light), pname, value);
  }
  
  @Override
  public void glLightfv(int light, int pname, float[] params, int params_offset) {
    gl.glLightfv(lightId(light), pname, params);
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
    glEnable(lightId(light));
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
    gl.glLightModelfv(mode, value);
  }

  @Override
  public void glLightModel(LightModel model, boolean value) {
    if (LightModel.LIGHT_MODEL_TWO_SIDE.equals(model)) {
      glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, value ? 1 : 0);// value?GL.GL_TRUE:GL.GL_FALSE);
    } else if (LightModel.LIGHT_MODEL_LOCAL_VIEWER.equals(model)) {
      glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, value ? 1 : 0);// value?GL.GL_TRUE:GL.GL_FALSE);
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
    // AVOID CALLING CLEAR AS IT DOES NOT WORK WELL AND TAKE ERASING COLORBUFFER ??
    gl.glClear(mask);
  }

  @Override
  public void glClearColorAndDepthBuffers() {
    // glClear(GL.GL_COLOR_BUFFER_BIT);
    // gl.glClear(GL.GL_COLOR_BUFFER_BIT);

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

  @Override
  public void glSelectBuffer(int size, IntBuffer buffer) {
    gl.glSelectBuffer(size, buffer.array());
  }

  @Override
  public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport,
      int viewport_offset) {
    glu.gluPickMatrix(x, y, delX, delY, viewport);
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
    throw new NotImplementedException("NEED TO CONVERT FloatBuffer to float[][][]");
    // gl.glMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
    // (target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
  }

  /* ***************** SHORTCUTS TO GL CONSTANTS *************************** */

  /**
   * NOT SUPPORTED in jGL wich emulate OpenGL 1 only.
   * 
   * Note that {@lin NotImplementedException} are NOT triggered to ease compatibility with
   * geometries that have the polygon offset fill setting enabled by default.
   * 
   * Was added to OpenGL 2
   * (https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glPolygonOffset.xhtml).
   * 
   * You may desactivate offset fill with drawable.setPolygonOffsetFillEnable(false).
   * 
   * @see https://github.com/jzy3d/jGL/issues/3
   */
  @Override
  public void glEnable_PolygonOffsetFill() {
    // throw new NotImplementedException(OFFSET_FILL_NOT_IMPLEMENTED);
  }

  /**
   * @see {@link #glEnable_PolygonOffsetFill()}
   */
  @Override
  public void glDisable_PolygonOffsetFill() {
    // throw new NotImplementedException(OFFSET_FILL_NOT_IMPLEMENTED);
  }

  /**
   * @see {@link #glEnable_PolygonOffsetFill()}
   */
  @Override
  public void glEnable_PolygonOffsetLine() {
    // throw new NotImplementedException(OFFSET_FILL_NOT_IMPLEMENTED);
  }

  /**
   * @see {@link #glEnable_PolygonOffsetFill()}
   */
  @Override
  public void glDisable_PolygonOffsetLine() {
    // throw new NotImplementedException(OFFSET_FILL_NOT_IMPLEMENTED);
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
    switch(func) {
      case GL_ALWAYS: gl.glDepthFunc(GL.GL_ALWAYS); break;
      case GL_NEVER: gl.glDepthFunc(GL.GL_NEVER); break;
      case GL_EQUAL: gl.glDepthFunc(GL.GL_EQUAL); break;
      case GL_GEQUAL: gl.glDepthFunc(GL.GL_GEQUAL); break;
      case GL_GREATER: gl.glDepthFunc(GL.GL_GREATER); break;
      case GL_LEQUAL: gl.glDepthFunc(GL.GL_LEQUAL); break;
      case GL_LESS: gl.glDepthFunc(GL.GL_LESS); break;
      case GL_NOTEQUAL: gl.glDepthFunc(GL.GL_NOTEQUAL); break;
      default: throw new RuntimeException("Enum value not supported : " + func);
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
    gl.glEnable(GL.GL_STENCIL);
  }
  
  @Override
  public void glDisable_Stencil() {
    gl.glDisable(GL.GL_STENCIL);
  }


}
