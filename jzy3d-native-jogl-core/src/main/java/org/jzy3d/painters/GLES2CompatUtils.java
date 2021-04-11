package org.jzy3d.painters;

import java.nio.Buffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.util.ImmModeSink;
import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.glsl.ShaderState;
import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;
import com.jogamp.opengl.util.glsl.fixedfunc.ShaderSelectionMode;

public class GLES2CompatUtils {

  // TODO lifecycle of pmvMatrix

  private static ImmModeSink immModeSink; // do we need to handle multiple
                                          // instances concurrently ?

  private static PMVMatrix pmvMatrix;

  private static GL gl;

  private static GL2ES1 gl2es1;

  public static void init(GL gl, int initialElementCount, int vComps, int vDataType, int cComps,
      int cDataType, int nComps, int nDataType, int tComps, int tDataType, int glBufferUsage,
      ShaderState shaderState) {

    GLES2CompatUtils.gl = gl;

    immModeSink = ImmModeSink.createGLSL(/* gl, */initialElementCount, vComps, vDataType, cComps,
        cDataType, nComps, nDataType, tComps, tDataType, glBufferUsage, shaderState);

    pmvMatrix = new PMVMatrix();

    gl2es1 = FixedFuncUtil.wrapFixedFuncEmul(gl, ShaderSelectionMode.AUTO, pmvMatrix);

  }

  public static void glTranslatef(float arg0, float arg1, float arg2) {
    pmvMatrix.glTranslatef(arg0, arg1, arg2);
  }

  public static void glLoadIdentity() {
    pmvMatrix.glLoadIdentity();
  }

  public static void glScalef(float x, float y, float z) {
    pmvMatrix.glScalef(x, y, z);
  }

  public static void glRotatef(float angle, float x, float y, float z) {
    pmvMatrix.glRotatef(angle, x, y, z);
  }

  public static void glColor3f(float r, float g, float b) {
    immModeSink.glColor3f(r, g, b);
  }

  public static void glRasterPos3f(float x, float y, float z) {
    // FIXME STUB
  }

  public static void glBitmap(int charWidth, int charHeight, float xorig, float yorig, float xmove,
      float ymove, byte[] nonascii, int i) {
    // FIXME STUB
  }

  public static void glPointSize(float width) {
    gl2es1.glPointSize(width);
  }

  public static void glBegin(int glPoints) {
    immModeSink.glBegin(glPoints);
  }

  public static void glColor4f(float r, float g, float b, float a) {
    immModeSink.glColor4f(r, g, b, a);
  }

  public static void glVertex3f(float x, float y, float z) {
    immModeSink.glVertex3f(x, y, z);
  }

  public static void glEnd() {
    immModeSink.glEnd(gl);
  }

  public static void glShadeModel(int arg0) {
    gl2es1.glShadeModel(arg0);
  }

  public static void glMatrixMode(int glModelview) {
    pmvMatrix.glMatrixMode(glModelview);
  }

  public static void glLightfv(int glLight0, int glPosition, float[] positionZero, int i) {
    gl2es1.glLightfv(glLight0, glPosition, positionZero, i);

  }


  public static void glBindBuffer(int glArrayBuffer, int i) {
    gl2es1.glBindBuffer(glArrayBuffer, i);
  }

  public static void glVertexPointer(int dimensions, int glFloat, int byteOffset, int pointer) {
    gl2es1.glVertexPointer(dimensions, glFloat, byteOffset, pointer);
  }

  public static void glNormalPointer(int glFloat, int byteOffset, int normalOffset) {
    gl2es1.glNormalPointer(glFloat, byteOffset, normalOffset);

  }

  public static void glEnableClientState(int glVertexArray) {
    gl2es1.glEnableClientState(glVertexArray);

  }

  public static void glDrawElements(int geometry, int size, int glUnsignedInt, int pointer) {
    gl2es1.glDrawElements(geometry, size, glUnsignedInt, pointer);

  }

  public static void glGetDoublev(int glProjectionMatrix, double[] projection, int i) {
    // pmvMatrix handles only floats

    // FIXME use array tool lib ?
    float[] projectionf = new float[projection.length];
    for (int j = 0; i < projection.length; i++) {
      projectionf[i] = (float) projection[i];
    }

    pmvMatrix.glGetFloatv(glProjectionMatrix, projectionf, i);

  }

  public static void glPushMatrix() {
    pmvMatrix.glPushMatrix();
  }

  public static void glPopMatrix() {
    pmvMatrix.glPopMatrix();
  }

  public static void glOrtho(float f, float g, float h, float i, float near, float far) {
    // FIXME : Check usage integration ???
    // pmvMatrix.glOrthof(arg0, arg1, arg2, arg3, arg4, arg5)
    gl2es1.glOrtho(f, g, h, i, near, far);

  }

  public static void glViewport(int screenXoffset, int screenYoffset, int screenWidth,
      int screenHeight) {
    gl2es1.glViewport(screenXoffset, screenYoffset, screenWidth, screenHeight);

  }

  public static void glLineWidth(float f) {
    gl2es1.glLineWidth(f);

  }

  public static void glTexCoord2f(float left, float bottom) {
    immModeSink.glTexCoord2f(left, bottom);

  }

  public static void glTexEnvi(int glTexture2d, int glTextureEnvMode, int glReplace) {
    gl2es1.glTexEnvf(glTexture2d, glTextureEnvMode, glReplace);

  }

  public static void glEnable(int glPolygonOffsetFill) {
    gl2es1.glEnable(glPolygonOffsetFill);

  }

  public static void glPolygonOffset(float f, float g) {
    gl2es1.glPolygonOffset(f, g);

  }

  public static void glDisable(int glPolygonOffsetFill) {
    gl2es1.glDisable(glPolygonOffsetFill);

  }


  public static void glMaterialfv(int glFront, int glAmbient, float[] array, int i) {
    gl2es1.glMaterialfv(glFront, glAmbient, array, i);

  }

  public static void glNormal3f(float x, float y, float z) {
    gl2es1.glNormal3f(x, y, z);

  }

  public static void glPolygonMode(int glFrontAndBack, int glLine) {
    // FIXME STUB
    // In OpenGL ES, GL_FILL is the only available polygon mode ??
  }

  public static void glPixelZoom(float xratio, float yratio) {
    // FIXME STUB
    // use matrix transform ??
  }

  public static void glDrawPixels(int imageWidth, int imageHeight, int glRgba, int glUnsignedByte,
      Buffer image) {
    // FIXME STUB
    // The fastest way to upload textures is to write to a texture bound to
    // an FBO.
    // But what you need to do is generate your pixels as geometry
    // (optimised as much as possible) and then draw them to the FBO using
    // an Orth matrix, GL_POINTS and
    // glDrawArrays.

  }

  public static void glPassThrough(float q) {
    // used only in feedback pseudo render mode
    // FIXME ANDROID OPEN GL ES
    // throw new UnsupportedOperationException();

  }

  public static void glVertex3d(double d, float f, float g) {
    throw new UnsupportedOperationException();
    // FIXME ANDROID OPEN7 GL ES
    // cf http://pandorawiki.org/Porting_to_GLES_from_GL
  }

  public static void glLineStipple(int i, short s) {
    throw new UnsupportedOperationException();
    // FIXME ANDROID OPEN GL ES
    // cf
    // http://stackoverflow.com/questions/1806028/how-to-draw-a-dotted-line-using-opengl-es-1

  }

}
