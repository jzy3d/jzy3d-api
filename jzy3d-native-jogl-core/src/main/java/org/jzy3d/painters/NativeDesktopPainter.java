package org.jzy3d.painters;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Attenuation;
import org.jzy3d.plot3d.rendering.lights.LightModel;
import org.jzy3d.plot3d.rendering.lights.MaterialProperty;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

public class NativeDesktopPainter extends AbstractPainter implements IPainter {
  static Logger LOGGER = LogManager.getLogger(NativeDesktopPainter.class);
  
  protected GL gl;
  protected GLU glu = new GLU();
  protected GLUT glut = new GLUT();

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
    return getCurrentGL(canvas);
  }

  @Override
  public void releaseGL() {
    getCurrentContext(canvas).release();
  }

  /**
   * Get the current GL context of the canvas and make it current.
   * 
   * This is usefull when needing to get a GL instance outside of the context of the
   * {@link GLEventListener}, e.g. when clicking the frame with mouse.
   */
  public GL getCurrentGL(ICanvas canvas) {
    GLContext context = getCurrentContext(canvas);
    context.makeCurrent();
    return context.getGL();
  }

  public GLContext getCurrentContext(ICanvas canvas) {
    GLAutoDrawable d = ((INativeCanvas) canvas).getDrawable();
    return d.getContext();
  }

  @Override
  public void configureGL(Quality quality) {
    // Activate Depth buffer
    if (quality.isDepthActivated()) {


      gl.glEnable(GL.GL_DEPTH_TEST);
      gl.glDepthFunc(GL.GL_LEQUAL);
    } else {
      gl.glDisable(GL.GL_DEPTH_TEST);
      // gl.glDepthRangef(n, f);
    }



    // Blending
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

    // KEEP THIS DOC!!
    // gl.glBlendFunc(GL.GL_DST_ALPHA, GL.GL_ONE);
    // ONE : make a white screen
    // NONE : make surface look opaque
    // GL_ONE_MINUS_DST_ALPHA : make surface look opaque
    // GL_ONE_MINUS_SRC_ALPHA : make surface look translucent but change coloring
    // GL_SRC_ALPHA : make surface look translucent but change coloring + no wireframe
    // GL_DST_ALPHA : make a white screen
    // glBlendFunc(GL.GL_ONE_MINUS_DST_ALPHA,GL.GL_DST_ALPHA);

    // gl.glBlen
    // ByteBuffer byteBuffer = ByteBuffer.allocate(10);
    // IntBuffer ib = byteBuffer.asIntBuffer();
    // gl.glGetIntegerv(GL.GL_BLEND_SRC_ALPHA, ib);
    // gl.glGetIntegerv(GL.GL_BLEND_DST_ALPHA, ib);
    // System.out.println(ib.array());

    // on/off is handled by each viewport (camera or image)

    // Activate tranparency
    if (quality.isAlphaActivated()) {
      gl.glEnable(GL.GL_BLEND);
      gl.glEnable(GL2.GL_ALPHA_TEST);

      if (quality.isDisableDepthBufferWhenAlpha()) {
        // Disable depth test to keeping pixels of
        // "what's behind a polygon" when drawing with
        // alpha
        gl.glDisable(GL.GL_DEPTH_TEST);
      }
    } else {
      gl.glDisable(GL2.GL_ALPHA_TEST);
    }
    
    // Make smooth colors for polygons (interpolate color between points)
    if(gl.getGLProfile().isGL2()) {
      if (quality.isSmoothColor()) 
        gl.getGL2().glShadeModel(GLLightingFunc.GL_SMOOTH);
      else
        gl.getGL2().glShadeModel(GLLightingFunc.GL_FLAT);
    }
    else {
      LOGGER.warn("Did not configured shade model as we don t have a GL2 context : " + gl.getGLProfile());
    }
    /*else     if(gl.getGLProfile().isGL4()) {
      if (quality.isSmoothColor()) 
        gl.getGL4().glShadeModel(GLLightingFunc.GL_SMOOTH);
      else
        gl.getGL2().glShadeModel(GLLightingFunc.GL_FLAT);
    }*/

    // Make smoothing setting
    if (quality.isSmoothPolygon()) {
      gl.glEnable(GL2.GL_POLYGON_SMOOTH);
      gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL2.GL_POLYGON_SMOOTH);

    if (quality.isSmoothLine()) {
      gl.glEnable(GL.GL_LINE_SMOOTH);
      gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL.GL_LINE_SMOOTH);

    if (quality.isSmoothPoint()) {
      gl.glEnable(GL2.GL_POINT_SMOOTH);
      gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
    } else
      gl.glDisable(GL2.GL_POINT_SMOOTH);
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
    glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);
    return projection;
  }

  @Override
  public float[] getProjectionAsFloat() {
    float projection[] = new float[16];
    glGetFloatv(GLMatrixFunc.GL_PROJECTION_MATRIX, projection, 0);
    return projection;
  }

  @Override
  public double[] getModelViewAsDouble() {
    double modelview[] = new double[16];
    glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);
    return modelview;
  }

  @Override
  public float[] getModelViewAsFloat() {
    float modelview[] = new float[16];
    glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, modelview, 0);
    return modelview;
  }

  /************ OPEN GL Interface **************/

  // GL MATRIX

  @Override
  public void glPushMatrix() {
    gl.getGL2().glPushMatrix();
  }

  @Override
  public void glPopMatrix() {
    gl.getGL2().glPopMatrix();
  }

  @Override
  public void glMatrixMode(int mode) {
    // gl.getGL3bc().glMatrixMode(mode);
    // gl.getGL4bc().glMatrixMode(mode);
    gl.getGL2().glMatrixMode(mode);
  }

  @Override
  public void glLoadIdentity() {
    gl.getGL2().glLoadIdentity();
  }

  @Override
  public void glScalef(float x, float y, float z) {
    gl.getGL2().glScalef(x, y, z);
  }

  @Override
  public void glTranslatef(float x, float y, float z) {
    gl.getGL2().glTranslatef(x, y, z);
  }

  @Override
  public void glRotatef(float angle, float x, float y, float z) {
    gl.getGL2().glRotatef(angle, x, y, z);
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
    gl.getGL2().glPointSize(width);
  }

  @Override
  public void glLineWidth(float width) {
    gl.getGL2().glLineWidth(width);
  }

  @Override
  public void glBegin(int type) {
    gl.getGL2().glBegin(type);
  }

  @Override
  public void glColor3f(float r, float g, float b) {
    gl.getGL2().glColor3f(r, g, b);
  }

  @Override
  public void glColor4f(float r, float g, float b, float a) {
    gl.getGL2().glColor4f(r, g, b, a);
  }

  @Override
  public void glVertex3f(float x, float y, float z) {
    gl.getGL2().glVertex3f(x, y, z);
  }

  @Override
  public void glVertex3d(double x, double y, double z) {
    gl.getGL2().glVertex3d(x, y, z);
  }

  @Override
  public void glEnd() {
    gl.getGL2().glEnd();
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
        return GL2.GL_FILL;
      case LINE:
        return GL2.GL_LINE;
      default:
        throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
    }
  }

  @Override
  public void glPolygonMode(int frontOrBack, int fill) {
    gl.getGL2().glPolygonMode(frontOrBack, fill);
  }

  @Override
  public void glPolygonOffset(float factor, float units) {
    gl.glPolygonOffset(factor, units); // handle stippling
  }

  @Override
  public void glLineStipple(int factor, short pattern) {
    gl.getGL2().glLineStipple(factor, pattern);
  }

  // GL TEXTURE

  @Override
  public void glTexCoord2f(float s, float t) {
    gl.getGL2().glTexCoord2f(s, t);
  }

  @Override
  public void glTexEnvf(int target, int pname, float param) {
    gl.getGL2().glTexEnvf(target, pname, param);
  }

  @Override
  public void glTexEnvi(int target, int pname, int param) {
    gl.getGL2().glTexEnvi(target, pname, param);
  }

  // DRAW IMAGES

  @Override
  public void glRasterPos3f(float x, float y, float z) {
    gl.getGL2().glRasterPos3f(x, y, z);
  }

  @Override
  public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {
    gl.getGL2().glDrawPixels(width, height, format, type, pixels);
  }

  @Override
  public void glPixelZoom(float xfactor, float yfactor) {
    gl.getGL2().glPixelZoom(xfactor, yfactor);
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
    gl.getGL2().glBitmap(width, height, xorig, yorig, xmove, ymove, bitmap, bitmap_offset);
  }


  @Override
  public void drawImage(ByteBuffer imageBuffer, int imageWidth, int imageHeight, Coord2d pixelZoom,
      Coord3d imagePosition) {
    glPixelZoom(pixelZoom.x, pixelZoom.y);
    glRasterPos3f(imagePosition.x, imagePosition.y, imagePosition.z);
    // painter.glRasterPos2f(xpict, ypict);

    synchronized (imageBuffer) { // we don't want to draw image while it is being set by setImage
      glDrawPixels(imageWidth, imageHeight, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
    }
  }

  // elements of GL spec picked in JOGL GL interface
  public static final int GL_RGBA = 0x1908;
  public static final int GL_UNSIGNED_BYTE = 0x1401;


  // STRINGS

  @Override
  public void glutBitmapString(int font, String string) {
    glut.glutBitmapString(font, string);
  }

  @Override
  public void drawText(Font font, String label, Coord3d position, Color color, float rotation) {
    glutBitmapString(font, label, position, color);
  }

  @Override
  public void glutBitmapString(Font axisFont, String label, Coord3d p, Color c) {
    color(c);
    raster(p, null);
    glutBitmapString(axisFont.getCode(), label);
  }

  @Override
  public int glutBitmapLength(int font, String string) {
    return glut.glutBitmapLength(font, string);
  }

  @Override
  public int getTextLengthInPixels(int font, String string) {
    Font fnt = Font.getById(font);

    return getTextLengthInPixels(fnt, string);
  }

  @Override
  public int getTextLengthInPixels(Font font, String string) {

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
    // fallback on glut
    return glutBitmapLength(font.getCode(), string);
  }

  private java.awt.Font toAWT(Font font) {
    return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, font.getHeight());
  }

  // GL LISTS

  @Override
  public int glGenLists(int range) {
    return gl.getGL2().glGenLists(range);
  }

  @Override
  public void glNewList(int list, int mode) {
    gl.getGL2().glNewList(list, mode);
  }

  @Override
  public void glNewList(int list, ListMode mode) {
    switch (mode) {
      case COMPILE:
        glNewList(list, GL2.GL_COMPILE);
      case COMPILE_AND_EXECUTE:
        glNewList(list, GL2.GL_COMPILE_AND_EXECUTE);
    }
  }

  @Override
  public void glEndList() {
    gl.getGL2().glEndList();
  }

  @Override
  public void glCallList(int list) {
    gl.getGL2().glCallList(list);
  }

  @Override
  public boolean glIsList(int list) {
    return gl.getGL2().glIsList(list);
  }

  @Override
  public void glDeleteLists(int list, int range) {
    gl.getGL2().glDeleteLists(list, range);
  }

  // GLU

  @Override
  public void gluDisk(double inner, double outer, int slices, int loops) {
    GLUquadric qobj = glu.gluNewQuadric();
    glu.gluDisk(qobj, inner, outer, slices, loops);
  }

  @Override
  public void glutSolidSphere(double radius, int slices, int stacks) {
    glut.glutSolidSphere(radius, slices, stacks);
  }

  @Override
  public void gluSphere(double radius, int slices, int stacks) {
    GLUquadric qobj = glu.gluNewQuadric();
    glu.gluSphere(qobj, radius, slices, stacks);
  }

  @Override
  public void gluCylinder(double base, double top, double height, int slices, int stacks) {
    GLUquadric qobj = glu.gluNewQuadric();
    glu.gluCylinder(qobj, base, top, height, slices, stacks);
  }

  @Override
  public void glutSolidCube(float size) {
    glut.glutSolidCube(size);
  }

  @Override
  public void glutSolidTeapot(float scale) {
    glut.glutSolidTeapot(scale);
  }

  @Override
  public void glutWireTeapot(float scale) {
    glut.glutWireTeapot(scale);
  }


  // GL FEEDBACK BUFER


  @Override
  public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {
    gl.getGL2().glFeedbackBuffer(size, type, buffer);
  }

  @Override
  public int glRenderMode(int mode) {
    return gl.getGL2().glRenderMode(mode);
  }

  @Override
  public int glRenderMode(RenderMode mode) {
    switch (mode) {
      case RENDER:
        return glRenderMode(GL2.GL_RENDER);
      case SELECT:
        return glRenderMode(GL2.GL_SELECT);
      case FEEDBACK:
        return glRenderMode(GL2.GL_FEEDBACK);
    }
    throw new IllegalArgumentException("Unsupported mode '" + mode + "'");
  }

  @Override
  public void glPassThrough(float token) {
    gl.getGL2().glPassThrough(token);
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
  public void glStencilMask_False(){
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
    gl.getGL2().glOrtho(left, right, bottom, top, near_val, far_val);
  }

  @Override
  public void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
    glu.gluPerspective(fovy, aspect, zNear, zFar);
  }

  @Override
  public void glFrustum(double left, double right, double bottom, double top, double zNear,
      double zFar) {
    gl.getGL2().glFrustum(left, right, bottom, top, zNear, zFar);
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
    //Array.print("NativePainter : glClipPlane : " + plane + " : ", equation);
    gl.getGL2().glClipPlane(plane, equation, 0);
  }

  @Override
  public void glEnable_ClipPlane(int plane) {
    gl.glEnable(clipPlaneId(plane));
  }
  
  /** Return the GL clip plane ID according to an ID in [0;5]*/
  @Override
  public int clipPlaneId(int id) {
    switch (id) {
      case 0:
        return GL2.GL_CLIP_PLANE0;
      case 1:
        return GL2.GL_CLIP_PLANE1;
      case 2:
        return GL2.GL_CLIP_PLANE2;
      case 3:
        return GL2.GL_CLIP_PLANE3;
      case 4:
        return GL2.GL_CLIP_PLANE4;
      case 5:
        return GL2.GL_CLIP_PLANE5;
      default:
        throw new IllegalArgumentException("Expect a plane ID in [0;5]");
    }
  }

  @Override
  public void glDisable_ClipPlane(int plane) {
    gl.glDisable(clipPlaneId(plane));
  }


  @Override
  public boolean gluUnProject(float winX, float winY, float winZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] objPos,
      int objPos_offset) {
    return glu.gluUnProject(winX, winY, winZ, model, model_offset, proj, proj_offset, view,
        view_offset, objPos, objPos_offset);
  }

  @Override
  public boolean gluProject(float objX, float objY, float objZ, float[] model, int model_offset,
      float[] proj, int proj_offset, int[] view, int view_offset, float[] winPos,
      int winPos_offset) {
    return glu.gluProject(objX, objY, objZ, model, model_offset, proj, proj_offset, view,
        view_offset, winPos, winPos_offset);
  }

  // GL GET

  @Override
  public void glGetIntegerv(int pname, int[] data, int data_offset) {
    gl.glGetIntegerv(pname, data, data_offset);
  }

  @Override
  public void glGetDoublev(int pname, double[] params, int params_offset) {
    gl.getGL2().glGetDoublev(pname, params, params_offset);
  }

  @Override
  public void glGetFloatv(int pname, float[] data, int data_offset) {
    gl.glGetFloatv(pname, data, data_offset);
  }

  @Override
  public void glDepthFunc(int func) {
    gl.glDepthFunc(func);
  }

  @Override
  public void glDepthRangef(float near, float far) {
    gl.glDepthRangef(near, far);
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
      gl.getGL2().glShadeModel(GL2.GL_SMOOTH);
    } else if (ColorModel.FLAT.equals(colorModel)) {
      gl.getGL2().glShadeModel(GL2.GL_FLAT);
    } else {
      throw new IllegalArgumentException("Unsupported setting : '" + colorModel + "'");
    }
  }

  @Override
  public void glShadeModel(int mode) {
    gl.getGL2().glShadeModel(mode);
  }

  @Override
  public void glShadeModel_Smooth() {
    gl.getGL2().glShadeModel(GL2.GL_SMOOTH);
  }

  @Override
  public void glShadeModel_Flat() {
    gl.getGL2().glShadeModel(GL2.GL_FLAT);
  }


  @Override
  public void glMaterialfv(int face, int pname, float[] params, int params_offset) {
    gl.getGL2().glMaterialfv(face, pname, params, 0);
  }

  @Override
  public void glNormal3f(float nx, float ny, float nz) {
    gl.getGL2().glNormal3f(nx, ny, nz);
  }



  @Override
  public void glLightModeli(int mode, int value) {
    gl.getGL2().glLightModeli(mode, value);
  }

  @Override
  public void glLightModelfv(int mode, float[] value) {
    gl.getGL2().glLightModelfv(mode, value, 0);
  }

  @Override
  public void glLightModel(LightModel model, boolean value) {
    if (LightModel.LIGHT_MODEL_TWO_SIDE.equals(model)) {
      glLightModeli(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, value ? GL.GL_TRUE : GL.GL_FALSE);
    } else if (LightModel.LIGHT_MODEL_LOCAL_VIEWER.equals(model)) {
      glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, value ? GL.GL_TRUE : GL.GL_FALSE);
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  @Override
  public void glLightModel(LightModel model, Color color) {
    if (LightModel.LIGHT_MODEL_AMBIENT.equals(model)) {
      glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, color.toArray());
    } else {
      throw new IllegalArgumentException("Unsupported model '" + model + "'");
    }
  }

  @Override
  public void glLightf(int light, Attenuation.Type attenuationType, float value) {
    if (Attenuation.Type.CONSTANT.equals(attenuationType)) {
      glLightf(light, GL2.GL_CONSTANT_ATTENUATION, value);
    } else if (Attenuation.Type.LINEAR.equals(attenuationType)) {
      glLightf(light, GL2.GL_LINEAR_ATTENUATION, value);
    } else if (Attenuation.Type.QUADRATIC.equals(attenuationType)) {
      glLightf(light, GL2.GL_QUADRATIC_ATTENUATION, value);
    }
  }

  @Override
  public void glLightf(int light, int pname, float value) {
    gl.getGL2().glLightf(lightId(light), pname, value);
  }

  @Override
  public void glLightfv(int light, int pname, float[] params, int params_offset) {
    gl.getGL2().glLightfv(lightId(light), pname, params, params_offset);
  }

  @Override
  public void glLight_Position(int lightId, float[] positionZero) {
    glLightfv(lightId, GLLightingFunc.GL_POSITION, positionZero, 0);
  }

  @Override
  public void glLight_Ambiant(int lightId, Color ambiantColor) {
    glLightfv(lightId, GLLightingFunc.GL_AMBIENT, ambiantColor.toArray(), 0);
  }

  @Override
  public void glLight_Diffuse(int lightId, Color diffuseColor) {
    glLightfv(lightId, GLLightingFunc.GL_DIFFUSE, diffuseColor.toArray(), 0);
  }

  @Override
  public void glLight_Specular(int lightId, Color specularColor) {
    glLightfv(lightId, GLLightingFunc.GL_SPECULAR, specularColor.toArray(), 0);
  }

  @Override
  public void glLight_Shininess(int lightId, float value) {
    glLightf(lightId, GLLightingFunc.GL_SHININESS, value);
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
        return GLLightingFunc.GL_LIGHT0;
      case 1:
        return GLLightingFunc.GL_LIGHT1;
      case (2):
        return GLLightingFunc.GL_LIGHT2;
      case 3:
        return GLLightingFunc.GL_LIGHT3;
      case 4:
        return GLLightingFunc.GL_LIGHT4;
      case 5:
        return GLLightingFunc.GL_LIGHT5;
      case 6:
        return GLLightingFunc.GL_LIGHT6;
      case 7:
        return GLLightingFunc.GL_LIGHT7;
    }
    throw new IllegalArgumentException("Unsupported light ID '" + id + "'");
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
    gl.getGL2().glInitNames();
  }

  @Override
  public void glLoadName(int name) {
    gl.getGL2().glLoadName(name);
  }

  @Override
  public void glPushName(int name) {
    gl.getGL2().glPushName(name);
  }

  @Override
  public void glPopName() {
    gl.getGL2().glPopName();
  }

  @Override
  public void glSelectBuffer(int size, IntBuffer buffer) {
    gl.getGL2().glSelectBuffer(size, buffer);
  }

  @Override
  public void gluPickMatrix(double x, double y, double delX, double delY, int[] viewport,
      int viewport_offset) {
    glu.gluPickMatrix(x, y, delX, delY, viewport, viewport_offset);
  }

  @Override
  public void glFlush() {
    gl.glFlush();
  }



  @Override
  public void glEvalCoord2f(float u, float v) {
    gl.getGL2().glEvalCoord2f(u, v);
  }

  @Override
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, FloatBuffer points) {
    gl.getGL2().glMap2f(target, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
  }


  /* *********************************************************************** */

  /* ******************** SHORTCUTS TO GL CONSTANTS ************************ */

  /* *********************************************************************** */

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
    glEnable(GL2.GL_POLYGON_OFFSET_LINE);
  }

  @Override
  public void glDisable_PolygonOffsetLine() {
    glDisable(GL2.GL_POLYGON_OFFSET_LINE);
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
    glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
  }

  @Override
  public void glMatrixMode_Projection() {
    glMatrixMode(GLMatrixFunc.GL_PROJECTION);
  }

  @Override
  public void glBegin_Polygon() {
    glBegin(GL2.GL_POLYGON);
  }

  @Override
  public void glBegin_Quad() {
    glBegin(GL2.GL_QUADS);
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
  public void glEnable_LineStipple() {
    glEnable(GL2.GL_LINE_STIPPLE);
  }

  @Override
  public void glDisable_LineStipple() {
    glDisable(GL2.GL_LINE_STIPPLE);
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
  public void glDisable_Lighting() {
    glDisable(GLLightingFunc.GL_LIGHTING);
  }

  @Override
  public void glEnable_Lighting() {
    glEnable(GLLightingFunc.GL_LIGHTING);
  }

  @Override
  public void glEnable_ColorMaterial() {
    glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
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
        return GLLightingFunc.GL_AMBIENT;
      case DIFFUSE:
        return GLLightingFunc.GL_DIFFUSE;
      case SPECULAR:
        return GLLightingFunc.GL_SPECULAR;
      case SHININESS:
        return GLLightingFunc.GL_SHININESS;
    }
    throw new IllegalArgumentException("Unsupported property '" + material + "'");
  }


  @Override
  public void glEnable_PointSmooth() {
    glEnable(GL2.GL_POINT_SMOOTH);
  }

  @Override
  public void glHint_PointSmooth_Nicest() {
    glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
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
    gl.glEnable(GL2.GL_STENCIL_TEST);
  }
  
  @Override
  public void glDisable_Stencil() {
    gl.glDisable(GL2.GL_STENCIL_TEST);
  }


}
