package org.jzy3d.mocks.jogl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera.Ortho;
import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3ES3;
import com.jogamp.opengl.GL3bc;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4ES3;
import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLArrayData;
import com.jogamp.opengl.GLBufferStorage;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLES1;
import com.jogamp.opengl.GLES2;
import com.jogamp.opengl.GLES3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLUniformData;

public class GL2Mock implements GL2 {

  @Override
  public void glAlphaFunc(int func, float ref) {


  }

  @Override
  public void glFogf(int pname, float param) {


  }

  @Override
  public void glFogfv(int pname, FloatBuffer params) {


  }

  @Override
  public void glFogfv(int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetLightfv(int light, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetLightfv(int light, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetMaterialfv(int face, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMaterialfv(int face, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetTexEnvfv(int tenv, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetTexEnvfv(int tenv, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glLightModelf(int pname, float param) {


  }

  @Override
  public void glLightModelfv(int pname, FloatBuffer params) {


  }

  @Override
  public void glLightModelfv(int pname, float[] params, int params_offset) {


  }

  @Override
  public void glLightf(int light, int pname, float param) {


  }

  @Override
  public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {


  }

  @Override
  public void glNormal3f(float nx, float ny, float nz) {


  }

  @Override
  public void glPointParameterf(int pname, float param) {


  }

  @Override
  public void glPointParameterfv(int pname, FloatBuffer params) {


  }

  @Override
  public void glPointParameterfv(int pname, float[] params, int params_offset) {


  }

  @Override
  public void glPointSize(float size) {


  }

  @Override
  public void glTexEnvf(int target, int pname, float param) {


  }

  @Override
  public void glTexEnvfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glTexEnvfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glClientActiveTexture(int texture) {


  }

  @Override
  public void glColor4ub(byte red, byte green, byte blue, byte alpha) {


  }

  @Override
  public void glGetTexEnviv(int tenv, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexEnviv(int tenv, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glLogicOp(int opcode) {


  }

  @Override
  public void glTexEnvi(int target, int pname, int param) {


  }

  @Override
  public void glTexEnviv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTexEnviv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glOrtho(double left, double right, double bottom, double top, double near_val,
      double far_val) {
    Ortho o = new Ortho();
    o.update(left, right, bottom, top, near_val, far_val);
    orthos.add(o);
  }

  @Override
  public void glFrustum(double left, double right, double bottom, double top, double zNear,
      double zFar) {


  }

  @Override
  public void glDrawElements(int mode, int count, int type, Buffer indices) {


  }

  @Override
  public void glActiveTexture(int texture) {


  }

  @Override
  public void glBindBuffer(int target, int buffer) {


  }

  @Override
  public void glBindFramebuffer(int target, int framebuffer) {


  }

  @Override
  public void glBindRenderbuffer(int target, int renderbuffer) {


  }

  @Override
  public void glBindTexture(int target, int texture) {


  }

  @Override
  public void glBlendEquation(int mode) {


  }

  @Override
  public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {


  }

  @Override
  public void glBlendFunc(int sfactor, int dfactor) {


  }

  @Override
  public void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha,
      int dfactorAlpha) {


  }

  @Override
  public void glBufferData(int target, long size, Buffer data, int usage) {


  }

  @Override
  public void glBufferSubData(int target, long offset, long size, Buffer data) {


  }

  @Override
  public int glCheckFramebufferStatus(int target) {

    return 0;
  }

  @Override
  public void glClear(int mask) {


  }

  @Override
  public void glClearColor(float red, float green, float blue, float alpha) {


  }

  @Override
  public void glClearDepthf(float d) {


  }

  @Override
  public void glClearStencil(int s) {


  }

  @Override
  public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {


  }

  @Override
  public void glCompressedTexImage2D(int target, int level, int internalformat, int width,
      int height, int border, int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexImage2D(int target, int level, int internalformat, int width,
      int height, int border, int imageSize, long data_buffer_offset) {


  }

  @Override
  public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int imageSize, long data_buffer_offset) {


  }

  @Override
  public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width,
      int height, int border) {


  }

  @Override
  public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y,
      int width, int height) {


  }

  @Override
  public void glCullFace(int mode) {


  }

  @Override
  public void glDeleteBuffers(int n, IntBuffer buffers) {


  }

  @Override
  public void glDeleteBuffers(int n, int[] buffers, int buffers_offset) {


  }

  @Override
  public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {


  }

  @Override
  public void glDeleteFramebuffers(int n, int[] framebuffers, int framebuffers_offset) {


  }

  @Override
  public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {


  }

  @Override
  public void glDeleteRenderbuffers(int n, int[] renderbuffers, int renderbuffers_offset) {


  }

  @Override
  public void glDeleteTextures(int n, IntBuffer textures) {


  }

  @Override
  public void glDeleteTextures(int n, int[] textures, int textures_offset) {


  }

  @Override
  public void glDepthFunc(int func) {


  }

  @Override
  public void glDepthMask(boolean flag) {


  }

  @Override
  public void glDepthRangef(float n, float f) {


  }

  @Override
  public void glDisable(int cap) {


  }

  @Override
  public void glDrawArrays(int mode, int first, int count) {


  }

  @Override
  public void glDrawElements(int mode, int count, int type, long indices_buffer_offset) {


  }

  @Override
  public void glEnable(int cap) {


  }

  @Override
  public void glFinish() {


  }

  @Override
  public void glFlush() {


  }

  @Override
  public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
      int renderbuffer) {


  }

  @Override
  public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture,
      int level) {


  }

  @Override
  public void glFrontFace(int mode) {


  }

  @Override
  public void glGenBuffers(int n, IntBuffer buffers) {


  }

  @Override
  public void glGenBuffers(int n, int[] buffers, int buffers_offset) {


  }

  @Override
  public void glGenerateMipmap(int target) {


  }

  @Override
  public void glGenFramebuffers(int n, IntBuffer framebuffers) {


  }

  @Override
  public void glGenFramebuffers(int n, int[] framebuffers, int framebuffers_offset) {


  }

  @Override
  public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {


  }

  @Override
  public void glGenRenderbuffers(int n, int[] renderbuffers, int renderbuffers_offset) {


  }

  @Override
  public void glGenTextures(int n, IntBuffer textures) {


  }

  @Override
  public void glGenTextures(int n, int[] textures, int textures_offset) {


  }

  @Override
  public void glGetBooleanv(int pname, ByteBuffer data) {


  }

  @Override
  public void glGetBooleanv(int pname, byte[] data, int data_offset) {


  }

  @Override
  public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetBufferParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public int glGetError() {

    return 0;
  }

  @Override
  public void glGetFloatv(int pname, FloatBuffer data) {


  }

  @Override
  public void glGetFloatv(int pname, float[] data, int data_offset) {


  }

  @Override
  public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glGetIntegerv(int pname, IntBuffer data) {


  }

  @Override
  public void glGetIntegerv(int pname, int[] data, int data_offset) {


  }

  @Override
  public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetRenderbufferParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public String glGetString(int name) {

    return null;
  }

  @Override
  public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetTexParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetTexParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glHint(int target, int mode) {


  }

  @Override
  public boolean glIsBuffer(int buffer) {

    return false;
  }

  @Override
  public boolean glIsEnabled(int cap) {

    return false;
  }

  @Override
  public boolean glIsFramebuffer(int framebuffer) {

    return false;
  }

  @Override
  public boolean glIsRenderbuffer(int renderbuffer) {

    return false;
  }

  @Override
  public boolean glIsTexture(int texture) {

    return false;
  }

  @Override
  public void glLineWidth(float width) {


  }

  @Override
  public void glPixelStorei(int pname, int param) {


  }

  @Override
  public void glPolygonOffset(float factor, float units) {


  }

  @Override
  public void glReadPixels(int x, int y, int width, int height, int format, int type,
      Buffer pixels) {


  }

  @Override
  public void glReadPixels(int x, int y, int width, int height, int format, int type,
      long pixels_buffer_offset) {


  }

  @Override
  public void glRenderbufferStorage(int target, int internalformat, int width, int height) {


  }

  @Override
  public void glSampleCoverage(float value, boolean invert) {


  }

  @Override
  public void glScissor(int x, int y, int width, int height) {


  }

  @Override
  public void glStencilFunc(int func, int ref, int mask) {


  }

  @Override
  public void glStencilMask(int mask) {


  }

  @Override
  public void glStencilOp(int fail, int zfail, int zpass) {


  }

  @Override
  public void glTexImage2D(int target, int level, int internalformat, int width, int height,
      int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTexImage2D(int target, int level, int internalformat, int width, int height,
      int border, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTexParameterf(int target, int pname, float param) {


  }

  @Override
  public void glTexParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glTexParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glTexParameteri(int target, int pname, int param) {


  }

  @Override
  public void glTexParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTexParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width,
      int height, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glViewport(int x, int y, int width, int height) {


  }

  @Override
  public void glTexStorage1D(int target, int levels, int internalformat, int width) {


  }

  @Override
  public void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {


  }

  @Override
  public void glTexStorage3D(int target, int levels, int internalformat, int width, int height,
      int depth) {


  }

  @Override
  public void glTextureStorage1DEXT(int texture, int target, int levels, int internalformat,
      int width) {


  }

  @Override
  public void glTextureStorage2DEXT(int texture, int target, int levels, int internalformat,
      int width, int height) {


  }

  @Override
  public void glTextureStorage3DEXT(int texture, int target, int levels, int internalformat,
      int width, int height, int depth) {


  }

  @Override
  public ByteBuffer glMapBuffer(int target, int access) {

    return null;
  }

  @Override
  public boolean glUnmapBuffer(int target) {

    return false;
  }

  @Override
  public void glRenderbufferStorageMultisample(int target, int samples, int internalformat,
      int width, int height) {


  }

  @Override
  public ByteBuffer glMapBufferRange(int target, long offset, long length, int access) {

    return null;
  }

  @Override
  public void glFlushMappedBufferRange(int target, long offset, long length) {


  }

  @Override
  public int glGetGraphicsResetStatus() {

    return 0;
  }

  @Override
  public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize,
      Buffer data) {


  }

  @Override
  public void glGetnUniformfv(int program, int location, int bufSize, FloatBuffer params) {


  }

  @Override
  public void glGetnUniformfv(int program, int location, int bufSize, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetnUniformiv(int program, int location, int bufSize, IntBuffer params) {


  }

  @Override
  public void glGetnUniformiv(int program, int location, int bufSize, int[] params,
      int params_offset) {


  }

  @Override
  public boolean isGL() {

    return false;
  }

  @Override
  public boolean isGL4bc() {

    return false;
  }

  @Override
  public boolean isGL4() {

    return false;
  }

  @Override
  public boolean isGL3bc() {

    return false;
  }

  @Override
  public boolean isGL3() {

    return false;
  }

  @Override
  public boolean isGL2() {

    return false;
  }

  @Override
  public boolean isGLES1() {

    return false;
  }

  @Override
  public boolean isGLES2() {

    return false;
  }

  @Override
  public boolean isGLES3() {

    return false;
  }

  @Override
  public boolean isGLES() {

    return false;
  }

  @Override
  public boolean isGL2ES1() {

    return false;
  }

  @Override
  public boolean isGL2ES2() {

    return false;
  }

  @Override
  public boolean isGL2ES3() {

    return false;
  }

  @Override
  public boolean isGL3ES3() {

    return false;
  }

  @Override
  public boolean isGL4ES3() {

    return false;
  }

  @Override
  public boolean isGL2GL3() {

    return false;
  }

  @Override
  public boolean isGL4core() {

    return false;
  }

  @Override
  public boolean isGL3core() {

    return false;
  }

  @Override
  public boolean isGLcore() {

    return false;
  }

  @Override
  public boolean isGLES2Compatible() {

    return false;
  }

  @Override
  public boolean isGLES3Compatible() {

    return false;
  }

  @Override
  public boolean isGLES31Compatible() {

    return false;
  }

  @Override
  public boolean isGLES32Compatible() {

    return false;
  }

  @Override
  public boolean hasGLSL() {

    return false;
  }

  @Override
  public GL getDownstreamGL() throws GLException {

    return null;
  }

  @Override
  public GL getRootGL() throws GLException {

    return null;
  }

  @Override
  public GL getGL() throws GLException {

    return null;
  }

  @Override
  public GL4bc getGL4bc() throws GLException {

    return null;
  }

  @Override
  public GL4 getGL4() throws GLException {

    return null;
  }

  @Override
  public GL3bc getGL3bc() throws GLException {

    return null;
  }

  @Override
  public GL3 getGL3() throws GLException {

    return null;
  }

  @Override
  public GL2 getGL2() throws GLException {

    return null;
  }

  @Override
  public GLES1 getGLES1() throws GLException {

    return null;
  }

  @Override
  public GLES2 getGLES2() throws GLException {

    return null;
  }

  @Override
  public GLES3 getGLES3() throws GLException {

    return null;
  }

  @Override
  public GL2ES1 getGL2ES1() throws GLException {

    return null;
  }

  @Override
  public GL2ES2 getGL2ES2() throws GLException {

    return null;
  }

  @Override
  public GL2ES3 getGL2ES3() throws GLException {

    return null;
  }

  @Override
  public GL3ES3 getGL3ES3() throws GLException {

    return null;
  }

  @Override
  public GL4ES3 getGL4ES3() throws GLException {

    return null;
  }

  @Override
  public GL2GL3 getGL2GL3() throws GLException {

    return null;
  }

  @Override
  public GLProfile getGLProfile() {

    return null;
  }

  @Override
  public GLContext getContext() {

    return null;
  }

  @Override
  public boolean isFunctionAvailable(String glFunctionName) {

    return false;
  }

  @Override
  public boolean isExtensionAvailable(String glExtensionName) {

    return false;
  }

  @Override
  public boolean hasBasicFBOSupport() {

    return false;
  }

  @Override
  public boolean hasFullFBOSupport() {

    return false;
  }

  @Override
  public int getMaxRenderbufferSamples() {

    return 0;
  }

  @Override
  public boolean isNPOTTextureAvailable() {

    return false;
  }

  @Override
  public boolean isTextureFormatBGRA8888Available() {

    return false;
  }

  @Override
  public void setSwapInterval(int interval) throws GLException {


  }

  @Override
  public int getSwapInterval() {

    return 0;
  }

  @Override
  public Object getPlatformGLExtensions() {

    return null;
  }

  @Override
  public Object getExtension(String extensionName) {

    return null;
  }

  @Override
  public void glClearDepth(double depth) {


  }

  @Override
  public void glDepthRange(double zNear, double zFar) {


  }

  @Override
  public int getBoundBuffer(int target) {

    return 0;
  }

  @Override
  public GLBufferStorage getBufferStorage(int bufferName) {

    return null;
  }

  @Override
  public GLBufferStorage mapBuffer(int target, int access) throws GLException {

    return null;
  }

  @Override
  public GLBufferStorage mapBufferRange(int target, long offset, long length, int access)
      throws GLException {

    return null;
  }

  @Override
  public boolean isVBOArrayBound() {

    return false;
  }

  @Override
  public boolean isVBOElementArrayBound() {

    return false;
  }

  @Override
  public int getBoundFramebuffer(int target) {

    return 0;
  }

  @Override
  public int getDefaultDrawFramebuffer() {

    return 0;
  }

  @Override
  public int getDefaultReadFramebuffer() {

    return 0;
  }

  @Override
  public int getDefaultReadBuffer() {

    return 0;
  }

  @Override
  public void glMatrixMode(int mode) {


  }

  @Override
  public void glPushMatrix() {


  }

  @Override
  public void glPopMatrix() {


  }

  @Override
  public void glLoadIdentity() {


  }

  @Override
  public void glLoadMatrixf(FloatBuffer m) {


  }

  @Override
  public void glLoadMatrixf(float[] m, int m_offset) {


  }

  @Override
  public void glMultMatrixf(FloatBuffer m) {


  }

  @Override
  public void glMultMatrixf(float[] m, int m_offset) {


  }

  @Override
  public void glTranslatef(float x, float y, float z) {


  }

  @Override
  public void glRotatef(float angle, float x, float y, float z) {


  }

  @Override
  public void glScalef(float x, float y, float z) {


  }

  @Override
  public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
    Ortho o = new Ortho();
    o.update(left, right, bottom, top, zNear, zFar);
    orthos.add(o);
  }

  List<Ortho> orthos = new ArrayList<Ortho>();

  public List<Ortho> getOrthos() {
    return orthos;
  }

  public Ortho getOrthoLast() {
    return orthos.get(orthos.size() - 1);
  }


  @Override
  public void glFrustumf(float left, float right, float bottom, float top, float zNear,
      float zFar) {


  }

  @Override
  public void glEnableClientState(int arrayName) {


  }

  @Override
  public void glDisableClientState(int arrayName) {


  }

  @Override
  public void glVertexPointer(GLArrayData array) {


  }

  @Override
  public void glVertexPointer(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glVertexPointer(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glColorPointer(GLArrayData array) {


  }

  @Override
  public void glColorPointer(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glColorPointer(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glColor4f(float red, float green, float blue, float alpha) {


  }

  @Override
  public void glNormalPointer(GLArrayData array) {


  }

  @Override
  public void glNormalPointer(int type, int stride, Buffer pointer) {


  }

  @Override
  public void glNormalPointer(int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glTexCoordPointer(GLArrayData array) {


  }

  @Override
  public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glTexCoordPointer(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glLightfv(int light, int pname, FloatBuffer params) {


  }

  @Override
  public void glLightfv(int light, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glMaterialf(int face, int pname, float param) {


  }

  @Override
  public void glMaterialfv(int face, int pname, FloatBuffer params) {


  }

  @Override
  public void glMaterialfv(int face, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glShadeModel(int mode) {


  }

  @Override
  public void glPolygonMode(int face, int mode) {


  }

  @Override
  public void glDrawBuffer(int mode) {


  }

  @Override
  public void glGetDoublev(int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetDoublev(int pname, double[] params, int params_offset) {


  }

  @Override
  public void glPixelStoref(int pname, float param) {


  }

  @Override
  public void glTexImage1D(int target, int level, int internalFormat, int width, int border,
      int format, int type, Buffer pixels) {


  }

  @Override
  public void glTexImage1D(int target, int level, int internalFormat, int width, int border,
      int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glGetTexImage(int target, int level, int format, int type, Buffer pixels) {


  }

  @Override
  public void glGetTexImage(int target, int level, int format, int type,
      long pixels_buffer_offset) {


  }

  @Override
  public void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type,
      Buffer pixels) {


  }

  @Override
  public void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type,
      long pixels_buffer_offset) {


  }

  @Override
  public void glCopyTexImage1D(int target, int level, int internalformat, int x, int y, int width,
      int border) {


  }

  @Override
  public void glCopyTexSubImage1D(int target, int level, int xoffset, int x, int y, int width) {


  }

  @Override
  public void glCompressedTexImage1D(int target, int level, int internalformat, int width,
      int border, int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexImage1D(int target, int level, int internalformat, int width,
      int border, int imageSize, long data_buffer_offset) {


  }

  @Override
  public void glCompressedTexSubImage1D(int target, int level, int xoffset, int width, int format,
      int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexSubImage1D(int target, int level, int xoffset, int width, int format,
      int imageSize, long data_buffer_offset) {


  }

  @Override
  public void glGetCompressedTexImage(int target, int level, Buffer img) {


  }

  @Override
  public void glGetCompressedTexImage(int target, int level, long img_buffer_offset) {


  }

  @Override
  public void glMultiDrawArrays(int mode, IntBuffer first, IntBuffer count, int drawcount) {


  }

  @Override
  public void glMultiDrawArrays(int mode, int[] first, int first_offset, int[] count,
      int count_offset, int drawcount) {


  }

  @Override
  public void glMultiDrawElements(int mode, IntBuffer count, int type, PointerBuffer indices,
      int drawcount) {


  }

  @Override
  public void glPointParameteri(int pname, int param) {


  }

  @Override
  public void glPointParameteriv(int pname, IntBuffer params) {


  }

  @Override
  public void glPointParameteriv(int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetBufferSubData(int target, long offset, long size, Buffer data) {


  }

  @Override
  public void glGetVertexAttribdv(int index, int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetVertexAttribdv(int index, int pname, double[] params, int params_offset) {


  }

  @Override
  public void glVertexAttrib1d(int index, double x) {


  }

  @Override
  public void glVertexAttrib1dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib1dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib1s(int index, short x) {


  }

  @Override
  public void glVertexAttrib1sv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib1sv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2d(int index, double x, double y) {


  }

  @Override
  public void glVertexAttrib2dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib2dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2s(int index, short x, short y) {


  }

  @Override
  public void glVertexAttrib2sv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib2sv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3d(int index, double x, double y, double z) {


  }

  @Override
  public void glVertexAttrib3dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib3dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3s(int index, short x, short y, short z) {


  }

  @Override
  public void glVertexAttrib3sv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib3sv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Nbv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4Nbv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Niv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4Niv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Nsv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4Nsv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Nub(int index, byte x, byte y, byte z, byte w) {


  }

  @Override
  public void glVertexAttrib4Nubv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4Nubv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Nuiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4Nuiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4Nusv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4Nusv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4bv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4bv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4d(int index, double x, double y, double z, double w) {


  }

  @Override
  public void glVertexAttrib4dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib4dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4iv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4iv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4s(int index, short x, short y, short z, short w) {


  }

  @Override
  public void glVertexAttrib4sv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4sv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4ubv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4ubv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4uiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4uiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4usv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4usv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glClampColor(int target, int clamp) {


  }

  @Override
  public void glVertexAttribI1i(int index, int x) {


  }

  @Override
  public void glVertexAttribI2i(int index, int x, int y) {


  }

  @Override
  public void glVertexAttribI3i(int index, int x, int y, int z) {


  }

  @Override
  public void glVertexAttribI1ui(int index, int x) {


  }

  @Override
  public void glVertexAttribI2ui(int index, int x, int y) {


  }

  @Override
  public void glVertexAttribI3ui(int index, int x, int y, int z) {


  }

  @Override
  public void glVertexAttribI1iv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI1iv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI2iv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI2iv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI3iv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI3iv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI1uiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI1uiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI2uiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI2uiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI3uiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI3uiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4bv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttribI4bv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4sv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribI4sv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4ubv(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttribI4ubv(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4usv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribI4usv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glBindFragDataLocation(int program, int color, String name) {


  }

  @Override
  public void glFramebufferTexture1D(int target, int attachment, int textarget, int texture,
      int level) {


  }

  @Override
  public void glPrimitiveRestartIndex(int index) {


  }

  @Override
  public void glGetActiveUniformName(int program, int uniformIndex, int bufSize, IntBuffer length,
      ByteBuffer uniformName) {


  }

  @Override
  public void glGetActiveUniformName(int program, int uniformIndex, int bufSize, int[] length,
      int length_offset, byte[] uniformName, int uniformName_offset) {


  }

  @Override
  public void glProvokingVertex(int mode) {


  }

  @Override
  public void glDrawTransformFeedback(int mode, int id) {


  }

  @Override
  public void glDrawTransformFeedbackStream(int mode, int id, int stream) {


  }

  @Override
  public void glBeginQueryIndexed(int target, int index, int id) {


  }

  @Override
  public void glEndQueryIndexed(int target, int index) {


  }

  @Override
  public void glGetQueryIndexediv(int target, int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetQueryIndexediv(int target, int index, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramUniform1d(int program, int location, double v0) {


  }

  @Override
  public void glProgramUniform1dv(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform1dv(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2d(int program, int location, double v0, double v1) {


  }

  @Override
  public void glProgramUniform2dv(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform2dv(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3d(int program, int location, double v0, double v1, double v2) {


  }

  @Override
  public void glProgramUniform3dv(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform3dv(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4d(int program, int location, double v0, double v1, double v2,
      double v3) {


  }

  @Override
  public void glProgramUniform4dv(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform4dv(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x3dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x3dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x2dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x2dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x4dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x4dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x2dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x2dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x4dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x4dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x3dv(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x3dv(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glVertexAttribL1d(int index, double x) {


  }

  @Override
  public void glVertexAttribL2d(int index, double x, double y) {


  }

  @Override
  public void glVertexAttribL3d(int index, double x, double y, double z) {


  }

  @Override
  public void glVertexAttribL4d(int index, double x, double y, double z, double w) {


  }

  @Override
  public void glVertexAttribL1dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttribL1dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL2dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttribL2dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL3dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttribL3dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL4dv(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttribL4dv(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribLPointer(int index, int size, int type, int stride,
      long pointer_buffer_offset) {


  }

  @Override
  public void glGetVertexAttribLdv(int index, int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetVertexAttribLdv(int index, int pname, double[] params, int params_offset) {


  }

  @Override
  public void glGetActiveAtomicCounterBufferiv(int program, int bufferIndex, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetActiveAtomicCounterBufferiv(int program, int bufferIndex, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glClearBufferData(int target, int internalformat, int format, int type, Buffer data) {


  }

  @Override
  public void glClearBufferSubData(int target, int internalformat, long offset, long size,
      int format, int type, Buffer data) {


  }

  @Override
  public void glGetInternalformati64v(int target, int internalformat, int pname, int bufSize,
      LongBuffer params) {


  }

  @Override
  public void glGetInternalformati64v(int target, int internalformat, int pname, int bufSize,
      long[] params, int params_offset) {


  }

  @Override
  public void glInvalidateTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth) {


  }

  @Override
  public void glInvalidateTexImage(int texture, int level) {


  }

  @Override
  public void glInvalidateBufferSubData(int buffer, long offset, long length) {


  }

  @Override
  public void glInvalidateBufferData(int buffer) {


  }

  @Override
  public void glGetnCompressedTexImage(int target, int lod, int bufSize, Buffer pixels) {


  }

  @Override
  public void glGetnTexImage(int target, int level, int format, int type, int bufSize,
      Buffer pixels) {


  }

  @Override
  public void glGetnUniformdv(int program, int location, int bufSize, DoubleBuffer params) {


  }

  @Override
  public void glGetnUniformdv(int program, int location, int bufSize, double[] params,
      int params_offset) {


  }

  @Override
  public void glBufferPageCommitmentARB(int target, long offset, long size, boolean commit) {


  }

  @Override
  public void glNamedBufferPageCommitmentEXT(int buffer, long offset, long size, boolean commit) {


  }

  @Override
  public void glNamedBufferPageCommitmentARB(int buffer, long offset, long size, boolean commit) {


  }

  @Override
  public void glTexPageCommitmentARB(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, boolean commit) {


  }

  @Override
  public void glDebugMessageEnableAMD(int category, int severity, int count, IntBuffer ids,
      boolean enabled) {


  }

  @Override
  public void glDebugMessageEnableAMD(int category, int severity, int count, int[] ids,
      int ids_offset, boolean enabled) {


  }

  @Override
  public void glDebugMessageInsertAMD(int category, int severity, int id, int length, String buf) {


  }

  @Override
  public int glGetDebugMessageLogAMD(int count, int bufsize, IntBuffer categories,
      IntBuffer severities, IntBuffer ids, IntBuffer lengths, ByteBuffer message) {

    return 0;
  }

  @Override
  public int glGetDebugMessageLogAMD(int count, int bufsize, int[] categories,
      int categories_offset, int[] severities, int severities_offset, int[] ids, int ids_offset,
      int[] lengths, int lengths_offset, byte[] message, int message_offset) {

    return 0;
  }

  @Override
  public void glGetUniformui64vNV(int program, int location, LongBuffer params) {


  }

  @Override
  public void glGetUniformui64vNV(int program, int location, long[] params, int params_offset) {


  }

  @Override
  public void glMultiDrawArraysIndirectAMD(int mode, Buffer indirect, int primcount, int stride) {


  }

  @Override
  public void glMultiDrawElementsIndirectAMD(int mode, int type, Buffer indirect, int primcount,
      int stride) {


  }

  @Override
  public void glSetMultisamplefvAMD(int pname, int index, FloatBuffer val) {


  }

  @Override
  public void glSetMultisamplefvAMD(int pname, int index, float[] val, int val_offset) {


  }

  @Override
  public void glStencilOpValueAMD(int face, int value) {


  }

  @Override
  public void glTessellationFactorAMD(float factor) {


  }

  @Override
  public void glTessellationModeAMD(int mode) {


  }

  @Override
  public long glImportSyncEXT(int external_sync_type, long external_sync, int flags) {

    return 0;
  }

  @Override
  public void glMakeBufferResidentNV(int target, int access) {


  }

  @Override
  public void glMakeBufferNonResidentNV(int target) {


  }

  @Override
  public boolean glIsBufferResidentNV(int target) {

    return false;
  }

  @Override
  public void glMakeNamedBufferResidentNV(int buffer, int access) {


  }

  @Override
  public void glMakeNamedBufferNonResidentNV(int buffer) {


  }

  @Override
  public boolean glIsNamedBufferResidentNV(int buffer) {

    return false;
  }

  @Override
  public void glGetBufferParameterui64vNV(int target, int pname, LongBuffer params) {


  }

  @Override
  public void glGetBufferParameterui64vNV(int target, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glGetNamedBufferParameterui64vNV(int buffer, int pname, LongBuffer params) {


  }

  @Override
  public void glGetNamedBufferParameterui64vNV(int buffer, int pname, long[] params,
      int params_offset) {


  }

  @Override
  public void glGetIntegerui64vNV(int value, LongBuffer result) {


  }

  @Override
  public void glGetIntegerui64vNV(int value, long[] result, int result_offset) {


  }

  @Override
  public void glUniformui64NV(int location, long value) {


  }

  @Override
  public void glUniformui64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniformui64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformui64NV(int program, int location, long value) {


  }

  @Override
  public void glProgramUniformui64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniformui64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glTexImage2DMultisampleCoverageNV(int target, int coverageSamples, int colorSamples,
      int internalFormat, int width, int height, boolean fixedSampleLocations) {


  }

  @Override
  public void glTexImage3DMultisampleCoverageNV(int target, int coverageSamples, int colorSamples,
      int internalFormat, int width, int height, int depth, boolean fixedSampleLocations) {


  }

  @Override
  public void glTextureImage2DMultisampleNV(int texture, int target, int samples,
      int internalFormat, int width, int height, boolean fixedSampleLocations) {


  }

  @Override
  public void glTextureImage3DMultisampleNV(int texture, int target, int samples,
      int internalFormat, int width, int height, int depth, boolean fixedSampleLocations) {


  }

  @Override
  public void glTextureImage2DMultisampleCoverageNV(int texture, int target, int coverageSamples,
      int colorSamples, int internalFormat, int width, int height, boolean fixedSampleLocations) {


  }

  @Override
  public void glTextureImage3DMultisampleCoverageNV(int texture, int target, int coverageSamples,
      int colorSamples, int internalFormat, int width, int height, int depth,
      boolean fixedSampleLocations) {


  }

  @Override
  public void glBufferAddressRangeNV(int pname, int index, long address, long length) {


  }

  @Override
  public void glVertexFormatNV(int size, int type, int stride) {


  }

  @Override
  public void glNormalFormatNV(int type, int stride) {


  }

  @Override
  public void glColorFormatNV(int size, int type, int stride) {


  }

  @Override
  public void glIndexFormatNV(int type, int stride) {


  }

  @Override
  public void glTexCoordFormatNV(int size, int type, int stride) {


  }

  @Override
  public void glEdgeFlagFormatNV(int stride) {


  }

  @Override
  public void glSecondaryColorFormatNV(int size, int type, int stride) {


  }

  @Override
  public void glFogCoordFormatNV(int type, int stride) {


  }

  @Override
  public void glVertexAttribFormatNV(int index, int size, int type, boolean normalized,
      int stride) {


  }

  @Override
  public void glVertexAttribIFormatNV(int index, int size, int type, int stride) {


  }

  @Override
  public void glGetIntegerui64i_vNV(int value, int index, LongBuffer result) {


  }

  @Override
  public void glGetIntegerui64i_vNV(int value, int index, long[] result, int result_offset) {


  }

  @Override
  public void glReadBuffer(int mode) {


  }

  @Override
  public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetTexLevelParameterfv(int target, int level, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexLevelParameteriv(int target, int level, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glDrawRangeElements(int mode, int start, int end, int count, int type,
      long indices_buffer_offset) {


  }

  @Override
  public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix2x3fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix3x2fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix2x4fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix4x2fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix3x4fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix4x3fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {


  }

  @Override
  public void glGetBooleani_v(int target, int index, ByteBuffer data) {


  }

  @Override
  public void glGetBooleani_v(int target, int index, byte[] data, int data_offset) {


  }

  @Override
  public void glGetIntegeri_v(int target, int index, IntBuffer data) {


  }

  @Override
  public void glGetIntegeri_v(int target, int index, int[] data, int data_offset) {


  }

  @Override
  public void glEnablei(int target, int index) {


  }

  @Override
  public void glDisablei(int target, int index) {


  }

  @Override
  public boolean glIsEnabledi(int target, int index) {

    return false;
  }

  @Override
  public void glBeginTransformFeedback(int primitiveMode) {


  }

  @Override
  public void glEndTransformFeedback() {


  }

  @Override
  public void glBindBufferRange(int target, int index, int buffer, long offset, long size) {


  }

  @Override
  public void glBindBufferBase(int target, int index, int buffer) {


  }

  @Override
  public void glTransformFeedbackVaryings(int program, int count, String[] varyings,
      int bufferMode) {


  }

  @Override
  public void glGetTransformFeedbackVarying(int program, int index, int bufSize, IntBuffer length,
      IntBuffer size, IntBuffer type, ByteBuffer name) {


  }

  @Override
  public void glGetTransformFeedbackVarying(int program, int index, int bufSize, int[] length,
      int length_offset, int[] size, int size_offset, int[] type, int type_offset, byte[] name,
      int name_offset) {


  }

  @Override
  public void glBeginConditionalRender(int id, int mode) {


  }

  @Override
  public void glEndConditionalRender() {


  }

  @Override
  public void glVertexAttribIPointer(int index, int size, int type, int stride,
      long pointer_buffer_offset) {


  }

  @Override
  public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribIiv(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribIuiv(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glVertexAttribI4i(int index, int x, int y, int z, int w) {


  }

  @Override
  public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {


  }

  @Override
  public void glVertexAttribI4iv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI4iv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4uiv(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI4uiv(int index, int[] v, int v_offset) {


  }

  @Override
  public void glGetUniformuiv(int program, int location, IntBuffer params) {


  }

  @Override
  public void glGetUniformuiv(int program, int location, int[] params, int params_offset) {


  }

  @Override
  public int glGetFragDataLocation(int program, String name) {

    return 0;
  }

  @Override
  public void glUniform1ui(int location, int v0) {


  }

  @Override
  public void glUniform2ui(int location, int v0, int v1) {


  }

  @Override
  public void glUniform3ui(int location, int v0, int v1, int v2) {


  }

  @Override
  public void glUniform4ui(int location, int v0, int v1, int v2, int v3) {


  }

  @Override
  public void glUniform1uiv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform1uiv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform2uiv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform2uiv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform3uiv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform3uiv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform4uiv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform4uiv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {


  }

  @Override
  public void glClearBufferiv(int buffer, int drawbuffer, int[] value, int value_offset) {


  }

  @Override
  public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {


  }

  @Override
  public void glClearBufferuiv(int buffer, int drawbuffer, int[] value, int value_offset) {


  }

  @Override
  public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {


  }

  @Override
  public void glClearBufferfv(int buffer, int drawbuffer, float[] value, int value_offset) {


  }

  @Override
  public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {


  }

  @Override
  public String glGetStringi(int name, int index) {

    return null;
  }

  @Override
  public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0,
      int dstX1, int dstY1, int mask, int filter) {


  }

  @Override
  public void glFramebufferTextureLayer(int target, int attachment, int texture, int level,
      int layer) {


  }

  @Override
  public void glBindVertexArray(int array) {


  }

  @Override
  public void glDeleteVertexArrays(int n, IntBuffer arrays) {


  }

  @Override
  public void glDeleteVertexArrays(int n, int[] arrays, int arrays_offset) {


  }

  @Override
  public void glGenVertexArrays(int n, IntBuffer arrays) {


  }

  @Override
  public void glGenVertexArrays(int n, int[] arrays, int arrays_offset) {


  }

  @Override
  public boolean glIsVertexArray(int array) {

    return false;
  }

  @Override
  public void glDrawArraysInstanced(int mode, int first, int count, int instancecount) {


  }

  @Override
  public void glDrawElementsInstanced(int mode, int count, int type, long indices_buffer_offset,
      int instancecount) {


  }

  @Override
  public void glTexBuffer(int target, int internalformat, int buffer) {


  }

  @Override
  public void glCopyBufferSubData(int readTarget, int writeTarget, long readOffset,
      long writeOffset, long size) {


  }

  @Override
  public void glGetUniformIndices(int program, int uniformCount, String[] uniformNames,
      IntBuffer uniformIndices) {


  }

  @Override
  public void glGetUniformIndices(int program, int uniformCount, String[] uniformNames,
      int[] uniformIndices, int uniformIndices_offset) {


  }

  @Override
  public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices,
      int pname, IntBuffer params) {


  }

  @Override
  public void glGetActiveUniformsiv(int program, int uniformCount, int[] uniformIndices,
      int uniformIndices_offset, int pname, int[] params, int params_offset) {


  }

  @Override
  public int glGetUniformBlockIndex(int program, String uniformBlockName) {

    return 0;
  }

  @Override
  public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize,
      IntBuffer length, ByteBuffer uniformBlockName) {


  }

  @Override
  public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize,
      int[] length, int length_offset, byte[] uniformBlockName, int uniformBlockName_offset) {


  }

  @Override
  public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {


  }

  @Override
  public void glVertexAttribDivisor(int index, int divisor) {


  }

  @Override
  public void glMinSampleShading(float value) {


  }

  @Override
  public void glBlendEquationi(int buf, int mode) {


  }

  @Override
  public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {


  }

  @Override
  public void glBlendFunci(int buf, int src, int dst) {


  }

  @Override
  public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {


  }

  @Override
  public void glBindTransformFeedback(int target, int id) {


  }

  @Override
  public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {


  }

  @Override
  public void glDeleteTransformFeedbacks(int n, int[] ids, int ids_offset) {


  }

  @Override
  public void glGenTransformFeedbacks(int n, IntBuffer ids) {


  }

  @Override
  public void glGenTransformFeedbacks(int n, int[] ids, int ids_offset) {


  }

  @Override
  public boolean glIsTransformFeedback(int id) {

    return false;
  }

  @Override
  public void glPauseTransformFeedback() {


  }

  @Override
  public void glResumeTransformFeedback() {


  }

  @Override
  public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize,
      IntBuffer params) {


  }

  @Override
  public void glGetInternalformativ(int target, int internalformat, int pname, int bufSize,
      int[] params, int params_offset) {


  }

  @Override
  public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer,
      int access, int format) {


  }

  @Override
  public void glMemoryBarrier(int barriers) {


  }

  @Override
  public void glFramebufferParameteri(int target, int pname, int param) {


  }

  @Override
  public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetFramebufferParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {


  }

  @Override
  public void glInvalidateFramebuffer(int target, int numAttachments, int[] attachments,
      int attachments_offset) {


  }

  @Override
  public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments,
      int x, int y, int width, int height) {


  }

  @Override
  public void glInvalidateSubFramebuffer(int target, int numAttachments, int[] attachments,
      int attachments_offset, int x, int y, int width, int height) {


  }

  @Override
  public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width,
      int height, boolean fixedsamplelocations) {


  }

  @Override
  public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width,
      int height, int depth, boolean fixedsamplelocations) {


  }

  @Override
  public void glGetnUniformuiv(int program, int location, int bufSize, IntBuffer params) {


  }

  @Override
  public void glGetnUniformuiv(int program, int location, int bufSize, int[] params,
      int params_offset) {


  }

  @Override
  public void glFramebufferTextureEXT(int target, int attachment, int texture, int level) {


  }

  @Override
  public boolean isPBOPackBound() {

    return false;
  }

  @Override
  public boolean isPBOUnpackBound() {

    return false;
  }

  @Override
  public void glAttachShader(int program, int shader) {


  }

  @Override
  public void glBindAttribLocation(int program, int index, String name) {


  }

  @Override
  public void glBlendColor(float red, float green, float blue, float alpha) {


  }

  @Override
  public void glCompileShader(int shader) {


  }

  @Override
  public int glCreateProgram() {

    return 0;
  }

  @Override
  public int glCreateShader(int type) {

    return 0;
  }

  @Override
  public void glDeleteProgram(int program) {


  }

  @Override
  public void glDeleteShader(int shader) {


  }

  @Override
  public void glDetachShader(int program, int shader) {


  }

  @Override
  public void glDisableVertexAttribArray(int index) {


  }

  @Override
  public void glEnableVertexAttribArray(int index) {


  }

  @Override
  public void glGetActiveAttrib(int program, int index, int bufSize, IntBuffer length,
      IntBuffer size, IntBuffer type, ByteBuffer name) {


  }

  @Override
  public void glGetActiveAttrib(int program, int index, int bufSize, int[] length,
      int length_offset, int[] size, int size_offset, int[] type, int type_offset, byte[] name,
      int name_offset) {


  }

  @Override
  public void glGetActiveUniform(int program, int index, int bufSize, IntBuffer length,
      IntBuffer size, IntBuffer type, ByteBuffer name) {


  }

  @Override
  public void glGetActiveUniform(int program, int index, int bufSize, int[] length,
      int length_offset, int[] size, int size_offset, int[] type, int type_offset, byte[] name,
      int name_offset) {


  }

  @Override
  public void glGetAttachedShaders(int program, int maxCount, IntBuffer count, IntBuffer shaders) {


  }

  @Override
  public void glGetAttachedShaders(int program, int maxCount, int[] count, int count_offset,
      int[] shaders, int shaders_offset) {


  }

  @Override
  public int glGetAttribLocation(int program, String name) {

    return 0;
  }

  @Override
  public void glGetProgramiv(int program, int pname, IntBuffer params) {


  }

  @Override
  public void glGetProgramiv(int program, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetProgramInfoLog(int program, int bufSize, IntBuffer length, ByteBuffer infoLog) {


  }

  @Override
  public void glGetProgramInfoLog(int program, int bufSize, int[] length, int length_offset,
      byte[] infoLog, int infoLog_offset) {


  }

  @Override
  public void glGetShaderiv(int shader, int pname, IntBuffer params) {


  }

  @Override
  public void glGetShaderiv(int shader, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetShaderInfoLog(int shader, int bufSize, IntBuffer length, ByteBuffer infoLog) {


  }

  @Override
  public void glGetShaderInfoLog(int shader, int bufSize, int[] length, int length_offset,
      byte[] infoLog, int infoLog_offset) {


  }

  @Override
  public void glGetShaderSource(int shader, int bufSize, IntBuffer length, ByteBuffer source) {


  }

  @Override
  public void glGetShaderSource(int shader, int bufSize, int[] length, int length_offset,
      byte[] source, int source_offset) {


  }

  @Override
  public void glGetUniformfv(int program, int location, FloatBuffer params) {


  }

  @Override
  public void glGetUniformfv(int program, int location, float[] params, int params_offset) {


  }

  @Override
  public void glGetUniformiv(int program, int location, IntBuffer params) {


  }

  @Override
  public void glGetUniformiv(int program, int location, int[] params, int params_offset) {


  }

  @Override
  public int glGetUniformLocation(int program, String name) {

    return 0;
  }

  @Override
  public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetVertexAttribfv(int index, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribiv(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public boolean glIsProgram(int program) {

    return false;
  }

  @Override
  public boolean glIsShader(int shader) {

    return false;
  }

  @Override
  public void glLinkProgram(int program) {


  }

  @Override
  public void glShaderSource(int shader, int count, String[] string, IntBuffer length) {


  }

  @Override
  public void glShaderSource(int shader, int count, String[] string, int[] length,
      int length_offset) {


  }

  @Override
  public void glStencilFuncSeparate(int face, int func, int ref, int mask) {


  }

  @Override
  public void glStencilMaskSeparate(int face, int mask) {


  }

  @Override
  public void glStencilOpSeparate(int face, int sfail, int dpfail, int dppass) {


  }

  @Override
  public void glUniform1f(int location, float v0) {


  }

  @Override
  public void glUniform1fv(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform1fv(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform1i(int location, int v0) {


  }

  @Override
  public void glUniform1iv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform1iv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform2f(int location, float v0, float v1) {


  }

  @Override
  public void glUniform2fv(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform2fv(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform2i(int location, int v0, int v1) {


  }

  @Override
  public void glUniform2iv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform2iv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform3f(int location, float v0, float v1, float v2) {


  }

  @Override
  public void glUniform3fv(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform3fv(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform3i(int location, int v0, int v1, int v2) {


  }

  @Override
  public void glUniform3iv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform3iv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform4f(int location, float v0, float v1, float v2, float v3) {


  }

  @Override
  public void glUniform4fv(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform4fv(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform4i(int location, int v0, int v1, int v2, int v3) {


  }

  @Override
  public void glUniform4iv(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform4iv(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUseProgram(int program) {


  }

  @Override
  public void glValidateProgram(int program) {


  }

  @Override
  public void glVertexAttrib1f(int index, float x) {


  }

  @Override
  public void glVertexAttrib1fv(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib1fv(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2f(int index, float x, float y) {


  }

  @Override
  public void glVertexAttrib2fv(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib2fv(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3f(int index, float x, float y, float z) {


  }

  @Override
  public void glVertexAttrib3fv(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib3fv(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4f(int index, float x, float y, float z, float w) {


  }

  @Override
  public void glVertexAttrib4fv(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib4fv(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
      long pointer_buffer_offset) {


  }

  @Override
  public void glTexImage2DMultisample(int target, int samples, int internalformat, int width,
      int height, boolean fixedsamplelocations) {


  }

  @Override
  public void glTexImage3DMultisample(int target, int samples, int internalformat, int width,
      int height, int depth, boolean fixedsamplelocations) {


  }

  @Override
  public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {


  }

  @Override
  public void glGetMultisamplefv(int pname, int index, float[] val, int val_offset) {


  }

  @Override
  public void glSampleMaski(int index, int mask) {


  }

  @Override
  public void glDebugMessageControl(int source, int type, int severity, int count, IntBuffer ids,
      boolean enabled) {


  }

  @Override
  public void glDebugMessageControl(int source, int type, int severity, int count, int[] ids,
      int ids_offset, boolean enabled) {


  }

  @Override
  public void glDebugMessageInsert(int source, int type, int id, int severity, int length,
      String buf) {


  }

  @Override
  public int glGetDebugMessageLog(int count, int bufSize, IntBuffer sources, IntBuffer types,
      IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {

    return 0;
  }

  @Override
  public int glGetDebugMessageLog(int count, int bufSize, int[] sources, int sources_offset,
      int[] types, int types_offset, int[] ids, int ids_offset, int[] severities,
      int severities_offset, int[] lengths, int lengths_offset, byte[] messageLog,
      int messageLog_offset) {

    return 0;
  }

  @Override
  public void glPushDebugGroup(int source, int id, int length, ByteBuffer message) {


  }

  @Override
  public void glPushDebugGroup(int source, int id, int length, byte[] message, int message_offset) {


  }

  @Override
  public void glPopDebugGroup() {


  }

  @Override
  public void glObjectLabel(int identifier, int name, int length, ByteBuffer label) {


  }

  @Override
  public void glObjectLabel(int identifier, int name, int length, byte[] label, int label_offset) {


  }

  @Override
  public void glGetObjectLabel(int identifier, int name, int bufSize, IntBuffer length,
      ByteBuffer label) {


  }

  @Override
  public void glGetObjectLabel(int identifier, int name, int bufSize, int[] length,
      int length_offset, byte[] label, int label_offset) {


  }

  @Override
  public void glObjectPtrLabel(Buffer ptr, int length, ByteBuffer label) {


  }

  @Override
  public void glObjectPtrLabel(Buffer ptr, int length, byte[] label, int label_offset) {


  }

  @Override
  public void glGetObjectPtrLabel(Buffer ptr, int bufSize, IntBuffer length, ByteBuffer label) {


  }

  @Override
  public void glGetObjectPtrLabel(Buffer ptr, int bufSize, int[] length, int length_offset,
      byte[] label, int label_offset) {


  }

  @Override
  public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY,
      int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ,
      int srcWidth, int srcHeight, int srcDepth) {


  }

  @Override
  public void glGetProgramBinary(int program, int bufSize, IntBuffer length, IntBuffer binaryFormat,
      Buffer binary) {


  }

  @Override
  public void glGetProgramBinary(int program, int bufSize, int[] length, int length_offset,
      int[] binaryFormat, int binaryFormat_offset, Buffer binary) {


  }

  @Override
  public void glProgramBinary(int program, int binaryFormat, Buffer binary, int length) {


  }

  @Override
  public void glTexImage3D(int target, int level, int internalformat, int width, int height,
      int depth, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTexImage3D(int target, int level, int internalformat, int width, int height,
      int depth, int border, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int width, int height, int depth, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset,
      int x, int y, int width, int height) {


  }

  @Override
  public void glCompressedTexImage3D(int target, int level, int internalformat, int width,
      int height, int depth, int border, int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexImage3D(int target, int level, int internalformat, int width,
      int height, int depth, int border, int imageSize, long data_buffer_offset) {


  }

  @Override
  public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int imageSize, Buffer data) {


  }

  @Override
  public void glCompressedTexSubImage3D(int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int imageSize,
      long data_buffer_offset) {


  }

  @Override
  public void glFramebufferTexture3D(int target, int attachment, int textarget, int texture,
      int level, int zoffset) {


  }

  @Override
  public void glTexParameterIiv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTexParameterIiv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glTexParameterIuiv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTexParameterIuiv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexParameterIiv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexParameterIuiv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {


  }

  @Override
  public void glSamplerParameterIiv(int sampler, int pname, int[] param, int param_offset) {


  }

  @Override
  public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {


  }

  @Override
  public void glSamplerParameterIuiv(int sampler, int pname, int[] param, int param_offset) {


  }

  @Override
  public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {


  }

  @Override
  public void glGetSamplerParameterIiv(int sampler, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {


  }

  @Override
  public void glGetSamplerParameterIuiv(int sampler, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glDrawArraysInstancedBaseInstance(int mode, int first, int count, int instancecount,
      int baseinstance) {


  }

  @Override
  public void glDrawElementsInstancedBaseInstance(int mode, int count, int type,
      long indices_buffer_offset, int instancecount, int baseinstance) {


  }

  @Override
  public void glDrawElementsInstancedBaseVertexBaseInstance(int mode, int count, int type,
      long indices_buffer_offset, int instancecount, int basevertex, int baseinstance) {


  }

  @Override
  public void glGenQueries(int n, IntBuffer ids) {


  }

  @Override
  public void glGenQueries(int n, int[] ids, int ids_offset) {


  }

  @Override
  public void glDeleteQueries(int n, IntBuffer ids) {


  }

  @Override
  public void glDeleteQueries(int n, int[] ids, int ids_offset) {


  }

  @Override
  public boolean glIsQuery(int id) {

    return false;
  }

  @Override
  public void glBeginQuery(int target, int id) {


  }

  @Override
  public void glEndQuery(int target) {


  }

  @Override
  public void glQueryCounter(int id, int target) {


  }

  @Override
  public void glGetQueryiv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetQueryiv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetQueryObjectiv(int id, int pname, IntBuffer params) {


  }

  @Override
  public void glGetQueryObjectiv(int id, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {


  }

  @Override
  public void glGetQueryObjectuiv(int id, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetQueryObjecti64v(int id, int pname, LongBuffer params) {


  }

  @Override
  public void glGetQueryObjecti64v(int id, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glGetQueryObjectui64v(int id, int pname, LongBuffer params) {


  }

  @Override
  public void glGetQueryObjectui64v(int id, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glActiveShaderProgram(int pipeline, int program) {


  }

  @Override
  public void glBindProgramPipeline(int pipeline) {


  }

  @Override
  public int glCreateShaderProgramv(int type, int count, String[] strings) {

    return 0;
  }

  @Override
  public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {


  }

  @Override
  public void glDeleteProgramPipelines(int n, int[] pipelines, int pipelines_offset) {


  }

  @Override
  public void glGenProgramPipelines(int n, IntBuffer pipelines) {


  }

  @Override
  public void glGenProgramPipelines(int n, int[] pipelines, int pipelines_offset) {


  }

  @Override
  public void glGetProgramPipelineInfoLog(int pipeline, int bufSize, IntBuffer length,
      ByteBuffer infoLog) {


  }

  @Override
  public void glGetProgramPipelineInfoLog(int pipeline, int bufSize, int[] length,
      int length_offset, byte[] infoLog, int infoLog_offset) {


  }

  @Override
  public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {


  }

  @Override
  public void glGetProgramPipelineiv(int pipeline, int pname, int[] params, int params_offset) {


  }

  @Override
  public boolean glIsProgramPipeline(int pipeline) {

    return false;
  }

  @Override
  public void glProgramParameteri(int program, int pname, int value) {


  }

  @Override
  public void glProgramUniform1f(int program, int location, float v0) {


  }

  @Override
  public void glProgramUniform1fv(int program, int location, int count, FloatBuffer value) {


  }

  @Override
  public void glProgramUniform1fv(int program, int location, int count, float[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform1i(int program, int location, int v0) {


  }

  @Override
  public void glProgramUniform1iv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform1iv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2f(int program, int location, float v0, float v1) {


  }

  @Override
  public void glProgramUniform2fv(int program, int location, int count, FloatBuffer value) {


  }

  @Override
  public void glProgramUniform2fv(int program, int location, int count, float[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2i(int program, int location, int v0, int v1) {


  }

  @Override
  public void glProgramUniform2iv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform2iv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {


  }

  @Override
  public void glProgramUniform3fv(int program, int location, int count, FloatBuffer value) {


  }

  @Override
  public void glProgramUniform3fv(int program, int location, int count, float[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {


  }

  @Override
  public void glProgramUniform3iv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform3iv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4f(int program, int location, float v0, float v1, float v2,
      float v3) {


  }

  @Override
  public void glProgramUniform4fv(int program, int location, int count, FloatBuffer value) {


  }

  @Override
  public void glProgramUniform4fv(int program, int location, int count, float[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {


  }

  @Override
  public void glProgramUniform4iv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform4iv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glUseProgramStages(int pipeline, int stages, int program) {


  }

  @Override
  public void glValidateProgramPipeline(int pipeline) {


  }

  @Override
  public void glProgramUniform1ui(int program, int location, int v0) {


  }

  @Override
  public void glProgramUniform2ui(int program, int location, int v0, int v1) {


  }

  @Override
  public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {


  }

  @Override
  public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {


  }

  @Override
  public void glProgramUniform1uiv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform1uiv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2uiv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform2uiv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3uiv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform3uiv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4uiv(int program, int location, int count, IntBuffer value) {


  }

  @Override
  public void glProgramUniform4uiv(int program, int location, int count, int[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x3fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x2fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x4fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x2fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x4fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose,
      FloatBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x3fv(int program, int location, int count, boolean transpose,
      float[] value, int value_offset) {


  }

  @Override
  public void glApplyFramebufferAttachmentCMAAINTEL() {


  }

  @Override
  public void glDrawBuffers(int n, IntBuffer bufs) {


  }

  @Override
  public void glDrawBuffers(int n, int[] bufs, int bufs_offset) {


  }

  @Override
  public void glReleaseShaderCompiler() {


  }

  @Override
  public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary,
      int length) {


  }

  @Override
  public void glShaderBinary(int n, int[] shaders, int shaders_offset, int binaryformat,
      Buffer binary, int length) {


  }

  @Override
  public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range,
      IntBuffer precision) {


  }

  @Override
  public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, int[] range,
      int range_offset, int[] precision, int precision_offset) {


  }

  @Override
  public void glVertexAttribPointer(GLArrayData array) {


  }

  @Override
  public void glUniform(GLUniformData data) {


  }

  @Override
  public void glClearIndex(float c) {


  }

  @Override
  public void glIndexMask(int mask) {


  }

  @Override
  public void glLineStipple(int factor, short pattern) {


  }

  @Override
  public void glPolygonStipple(ByteBuffer mask) {


  }

  @Override
  public void glPolygonStipple(byte[] mask, int mask_offset) {


  }

  @Override
  public void glPolygonStipple(long mask_buffer_offset) {


  }

  @Override
  public void glGetPolygonStipple(ByteBuffer mask) {


  }

  @Override
  public void glGetPolygonStipple(byte[] mask, int mask_offset) {


  }

  @Override
  public void glGetPolygonStipple(long mask_buffer_offset) {


  }

  @Override
  public void glEdgeFlag(boolean flag) {


  }

  @Override
  public void glEdgeFlagv(ByteBuffer flag) {


  }

  @Override
  public void glEdgeFlagv(byte[] flag, int flag_offset) {


  }

  @Override
  public void glClipPlane(int plane, DoubleBuffer equation) {


  }

  @Override
  public void glClipPlane(int plane, double[] equation, int equation_offset) {


  }

  @Override
  public void glGetClipPlane(int plane, DoubleBuffer equation) {


  }

  @Override
  public void glGetClipPlane(int plane, double[] equation, int equation_offset) {


  }

  @Override
  public void glPushAttrib(int mask) {


  }

  @Override
  public void glPopAttrib() {


  }

  @Override
  public int glRenderMode(int mode) {

    return 0;
  }

  @Override
  public void glClearAccum(float red, float green, float blue, float alpha) {


  }

  @Override
  public void glAccum(int op, float value) {


  }

  @Override
  public void glLoadMatrixd(DoubleBuffer m) {


  }

  @Override
  public void glLoadMatrixd(double[] m, int m_offset) {


  }

  @Override
  public void glMultMatrixd(DoubleBuffer m) {


  }

  @Override
  public void glMultMatrixd(double[] m, int m_offset) {


  }

  @Override
  public void glRotated(double angle, double x, double y, double z) {


  }

  @Override
  public void glScaled(double x, double y, double z) {


  }

  @Override
  public void glTranslated(double x, double y, double z) {


  }

  @Override
  public boolean glIsList(int list) {

    return false;
  }

  @Override
  public void glDeleteLists(int list, int range) {


  }

  @Override
  public int glGenLists(int range) {

    return 0;
  }

  @Override
  public void glNewList(int list, int mode) {


  }

  @Override
  public void glEndList() {


  }

  @Override
  public void glCallList(int list) {


  }

  @Override
  public void glCallLists(int n, int type, Buffer lists) {


  }

  @Override
  public void glListBase(int base) {


  }

  @Override
  public void glBegin(int mode) {


  }

  @Override
  public void glEnd() {


  }

  @Override
  public void glVertex2d(double x, double y) {


  }

  @Override
  public void glVertex2f(float x, float y) {


  }

  @Override
  public void glVertex2i(int x, int y) {


  }

  @Override
  public void glVertex2s(short x, short y) {


  }

  @Override
  public void glVertex3d(double x, double y, double z) {


  }

  @Override
  public void glVertex3f(float x, float y, float z) {
    vertex3f.add(new Coord3d(x, y, z));
  }

  List<Coord3d> vertex3f = new ArrayList<>();

  public List<Coord3d> getVertex3f() {
    return vertex3f;
  }


  @Override
  public void glVertex3i(int x, int y, int z) {


  }

  @Override
  public void glVertex3s(short x, short y, short z) {


  }

  @Override
  public void glVertex4d(double x, double y, double z, double w) {


  }

  @Override
  public void glVertex4f(float x, float y, float z, float w) {


  }

  @Override
  public void glVertex4i(int x, int y, int z, int w) {


  }

  @Override
  public void glVertex4s(short x, short y, short z, short w) {


  }

  @Override
  public void glVertex2dv(DoubleBuffer v) {


  }

  @Override
  public void glVertex2dv(double[] v, int v_offset) {


  }

  @Override
  public void glVertex2fv(FloatBuffer v) {


  }

  @Override
  public void glVertex2fv(float[] v, int v_offset) {


  }

  @Override
  public void glVertex2iv(IntBuffer v) {


  }

  @Override
  public void glVertex2iv(int[] v, int v_offset) {


  }

  @Override
  public void glVertex2sv(ShortBuffer v) {


  }

  @Override
  public void glVertex2sv(short[] v, int v_offset) {


  }

  @Override
  public void glVertex3dv(DoubleBuffer v) {


  }

  @Override
  public void glVertex3dv(double[] v, int v_offset) {


  }

  @Override
  public void glVertex3fv(FloatBuffer v) {


  }

  @Override
  public void glVertex3fv(float[] v, int v_offset) {


  }

  @Override
  public void glVertex3iv(IntBuffer v) {


  }

  @Override
  public void glVertex3iv(int[] v, int v_offset) {


  }

  @Override
  public void glVertex3sv(ShortBuffer v) {


  }

  @Override
  public void glVertex3sv(short[] v, int v_offset) {


  }

  @Override
  public void glVertex4dv(DoubleBuffer v) {


  }

  @Override
  public void glVertex4dv(double[] v, int v_offset) {


  }

  @Override
  public void glVertex4fv(FloatBuffer v) {


  }

  @Override
  public void glVertex4fv(float[] v, int v_offset) {


  }

  @Override
  public void glVertex4iv(IntBuffer v) {


  }

  @Override
  public void glVertex4iv(int[] v, int v_offset) {


  }

  @Override
  public void glVertex4sv(ShortBuffer v) {


  }

  @Override
  public void glVertex4sv(short[] v, int v_offset) {


  }

  @Override
  public void glNormal3b(byte nx, byte ny, byte nz) {


  }

  @Override
  public void glNormal3d(double nx, double ny, double nz) {


  }

  @Override
  public void glNormal3i(int nx, int ny, int nz) {


  }

  @Override
  public void glNormal3s(short nx, short ny, short nz) {


  }

  @Override
  public void glNormal3bv(ByteBuffer v) {


  }

  @Override
  public void glNormal3bv(byte[] v, int v_offset) {


  }

  @Override
  public void glNormal3dv(DoubleBuffer v) {


  }

  @Override
  public void glNormal3dv(double[] v, int v_offset) {


  }

  @Override
  public void glNormal3fv(FloatBuffer v) {


  }

  @Override
  public void glNormal3fv(float[] v, int v_offset) {


  }

  @Override
  public void glNormal3iv(IntBuffer v) {


  }

  @Override
  public void glNormal3iv(int[] v, int v_offset) {


  }

  @Override
  public void glNormal3sv(ShortBuffer v) {


  }

  @Override
  public void glNormal3sv(short[] v, int v_offset) {


  }

  @Override
  public void glIndexd(double c) {


  }

  @Override
  public void glIndexf(float c) {


  }

  @Override
  public void glIndexi(int c) {


  }

  @Override
  public void glIndexs(short c) {


  }

  @Override
  public void glIndexdv(DoubleBuffer c) {


  }

  @Override
  public void glIndexdv(double[] c, int c_offset) {


  }

  @Override
  public void glIndexfv(FloatBuffer c) {


  }

  @Override
  public void glIndexfv(float[] c, int c_offset) {


  }

  @Override
  public void glIndexiv(IntBuffer c) {


  }

  @Override
  public void glIndexiv(int[] c, int c_offset) {


  }

  @Override
  public void glIndexsv(ShortBuffer c) {


  }

  @Override
  public void glIndexsv(short[] c, int c_offset) {


  }

  @Override
  public void glColor3b(byte red, byte green, byte blue) {


  }

  @Override
  public void glColor3d(double red, double green, double blue) {


  }

  @Override
  public void glColor3f(float red, float green, float blue) {


  }

  @Override
  public void glColor3i(int red, int green, int blue) {


  }

  @Override
  public void glColor3s(short red, short green, short blue) {


  }

  @Override
  public void glColor3ub(byte red, byte green, byte blue) {


  }

  @Override
  public void glColor3ui(int red, int green, int blue) {


  }

  @Override
  public void glColor3us(short red, short green, short blue) {


  }

  @Override
  public void glColor4b(byte red, byte green, byte blue, byte alpha) {


  }

  @Override
  public void glColor4d(double red, double green, double blue, double alpha) {


  }

  @Override
  public void glColor4i(int red, int green, int blue, int alpha) {


  }

  @Override
  public void glColor4s(short red, short green, short blue, short alpha) {


  }

  @Override
  public void glColor4ui(int red, int green, int blue, int alpha) {


  }

  @Override
  public void glColor4us(short red, short green, short blue, short alpha) {


  }

  @Override
  public void glColor3bv(ByteBuffer v) {


  }

  @Override
  public void glColor3bv(byte[] v, int v_offset) {


  }

  @Override
  public void glColor3dv(DoubleBuffer v) {


  }

  @Override
  public void glColor3dv(double[] v, int v_offset) {


  }

  @Override
  public void glColor3fv(FloatBuffer v) {


  }

  @Override
  public void glColor3fv(float[] v, int v_offset) {


  }

  @Override
  public void glColor3iv(IntBuffer v) {


  }

  @Override
  public void glColor3iv(int[] v, int v_offset) {


  }

  @Override
  public void glColor3sv(ShortBuffer v) {


  }

  @Override
  public void glColor3sv(short[] v, int v_offset) {


  }

  @Override
  public void glColor3ubv(ByteBuffer v) {


  }

  @Override
  public void glColor3ubv(byte[] v, int v_offset) {


  }

  @Override
  public void glColor3uiv(IntBuffer v) {


  }

  @Override
  public void glColor3uiv(int[] v, int v_offset) {


  }

  @Override
  public void glColor3usv(ShortBuffer v) {


  }

  @Override
  public void glColor3usv(short[] v, int v_offset) {


  }

  @Override
  public void glColor4bv(ByteBuffer v) {


  }

  @Override
  public void glColor4bv(byte[] v, int v_offset) {


  }

  @Override
  public void glColor4dv(DoubleBuffer v) {


  }

  @Override
  public void glColor4dv(double[] v, int v_offset) {


  }

  @Override
  public void glColor4fv(FloatBuffer v) {


  }

  @Override
  public void glColor4fv(float[] v, int v_offset) {


  }

  @Override
  public void glColor4iv(IntBuffer v) {


  }

  @Override
  public void glColor4iv(int[] v, int v_offset) {


  }

  @Override
  public void glColor4sv(ShortBuffer v) {


  }

  @Override
  public void glColor4sv(short[] v, int v_offset) {


  }

  @Override
  public void glColor4ubv(ByteBuffer v) {


  }

  @Override
  public void glColor4ubv(byte[] v, int v_offset) {


  }

  @Override
  public void glColor4uiv(IntBuffer v) {


  }

  @Override
  public void glColor4uiv(int[] v, int v_offset) {


  }

  @Override
  public void glColor4usv(ShortBuffer v) {


  }

  @Override
  public void glColor4usv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord1d(double s) {


  }

  @Override
  public void glTexCoord1f(float s) {


  }

  @Override
  public void glTexCoord1i(int s) {


  }

  @Override
  public void glTexCoord1s(short s) {


  }

  @Override
  public void glTexCoord2d(double s, double t) {


  }

  @Override
  public void glTexCoord2f(float s, float t) {


  }

  @Override
  public void glTexCoord2i(int s, int t) {


  }

  @Override
  public void glTexCoord2s(short s, short t) {


  }

  @Override
  public void glTexCoord3d(double s, double t, double r) {


  }

  @Override
  public void glTexCoord3f(float s, float t, float r) {


  }

  @Override
  public void glTexCoord3i(int s, int t, int r) {


  }

  @Override
  public void glTexCoord3s(short s, short t, short r) {


  }

  @Override
  public void glTexCoord4d(double s, double t, double r, double q) {


  }

  @Override
  public void glTexCoord4f(float s, float t, float r, float q) {


  }

  @Override
  public void glTexCoord4i(int s, int t, int r, int q) {


  }

  @Override
  public void glTexCoord4s(short s, short t, short r, short q) {


  }

  @Override
  public void glTexCoord1dv(DoubleBuffer v) {


  }

  @Override
  public void glTexCoord1dv(double[] v, int v_offset) {


  }

  @Override
  public void glTexCoord1fv(FloatBuffer v) {


  }

  @Override
  public void glTexCoord1fv(float[] v, int v_offset) {


  }

  @Override
  public void glTexCoord1iv(IntBuffer v) {


  }

  @Override
  public void glTexCoord1iv(int[] v, int v_offset) {


  }

  @Override
  public void glTexCoord1sv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord1sv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord2dv(DoubleBuffer v) {


  }

  @Override
  public void glTexCoord2dv(double[] v, int v_offset) {


  }

  @Override
  public void glTexCoord2fv(FloatBuffer v) {


  }

  @Override
  public void glTexCoord2fv(float[] v, int v_offset) {


  }

  @Override
  public void glTexCoord2iv(IntBuffer v) {


  }

  @Override
  public void glTexCoord2iv(int[] v, int v_offset) {


  }

  @Override
  public void glTexCoord2sv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord2sv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord3dv(DoubleBuffer v) {


  }

  @Override
  public void glTexCoord3dv(double[] v, int v_offset) {


  }

  @Override
  public void glTexCoord3fv(FloatBuffer v) {


  }

  @Override
  public void glTexCoord3fv(float[] v, int v_offset) {


  }

  @Override
  public void glTexCoord3iv(IntBuffer v) {


  }

  @Override
  public void glTexCoord3iv(int[] v, int v_offset) {


  }

  @Override
  public void glTexCoord3sv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord3sv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord4dv(DoubleBuffer v) {


  }

  @Override
  public void glTexCoord4dv(double[] v, int v_offset) {


  }

  @Override
  public void glTexCoord4fv(FloatBuffer v) {


  }

  @Override
  public void glTexCoord4fv(float[] v, int v_offset) {


  }

  @Override
  public void glTexCoord4iv(IntBuffer v) {


  }

  @Override
  public void glTexCoord4iv(int[] v, int v_offset) {


  }

  @Override
  public void glTexCoord4sv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord4sv(short[] v, int v_offset) {


  }

  @Override
  public void glRasterPos2d(double x, double y) {


  }

  @Override
  public void glRasterPos2f(float x, float y) {


  }

  @Override
  public void glRasterPos2i(int x, int y) {


  }

  @Override
  public void glRasterPos2s(short x, short y) {


  }

  @Override
  public void glRasterPos3d(double x, double y, double z) {


  }

  @Override
  public void glRasterPos3f(float x, float y, float z) {


  }

  @Override
  public void glRasterPos3i(int x, int y, int z) {


  }

  @Override
  public void glRasterPos3s(short x, short y, short z) {


  }

  @Override
  public void glRasterPos4d(double x, double y, double z, double w) {


  }

  @Override
  public void glRasterPos4f(float x, float y, float z, float w) {


  }

  @Override
  public void glRasterPos4i(int x, int y, int z, int w) {


  }

  @Override
  public void glRasterPos4s(short x, short y, short z, short w) {


  }

  @Override
  public void glRasterPos2dv(DoubleBuffer v) {


  }

  @Override
  public void glRasterPos2dv(double[] v, int v_offset) {


  }

  @Override
  public void glRasterPos2fv(FloatBuffer v) {


  }

  @Override
  public void glRasterPos2fv(float[] v, int v_offset) {


  }

  @Override
  public void glRasterPos2iv(IntBuffer v) {


  }

  @Override
  public void glRasterPos2iv(int[] v, int v_offset) {


  }

  @Override
  public void glRasterPos2sv(ShortBuffer v) {


  }

  @Override
  public void glRasterPos2sv(short[] v, int v_offset) {


  }

  @Override
  public void glRasterPos3dv(DoubleBuffer v) {


  }

  @Override
  public void glRasterPos3dv(double[] v, int v_offset) {


  }

  @Override
  public void glRasterPos3fv(FloatBuffer v) {


  }

  @Override
  public void glRasterPos3fv(float[] v, int v_offset) {


  }

  @Override
  public void glRasterPos3iv(IntBuffer v) {


  }

  @Override
  public void glRasterPos3iv(int[] v, int v_offset) {


  }

  @Override
  public void glRasterPos3sv(ShortBuffer v) {


  }

  @Override
  public void glRasterPos3sv(short[] v, int v_offset) {


  }

  @Override
  public void glRasterPos4dv(DoubleBuffer v) {


  }

  @Override
  public void glRasterPos4dv(double[] v, int v_offset) {


  }

  @Override
  public void glRasterPos4fv(FloatBuffer v) {


  }

  @Override
  public void glRasterPos4fv(float[] v, int v_offset) {


  }

  @Override
  public void glRasterPos4iv(IntBuffer v) {


  }

  @Override
  public void glRasterPos4iv(int[] v, int v_offset) {


  }

  @Override
  public void glRasterPos4sv(ShortBuffer v) {


  }

  @Override
  public void glRasterPos4sv(short[] v, int v_offset) {


  }

  @Override
  public void glRectd(double x1, double y1, double x2, double y2) {


  }

  @Override
  public void glRectf(float x1, float y1, float x2, float y2) {


  }

  @Override
  public void glRecti(int x1, int y1, int x2, int y2) {


  }

  @Override
  public void glRects(short x1, short y1, short x2, short y2) {


  }

  @Override
  public void glRectdv(DoubleBuffer v1, DoubleBuffer v2) {


  }

  @Override
  public void glRectdv(double[] v1, int v1_offset, double[] v2, int v2_offset) {


  }

  @Override
  public void glRectfv(FloatBuffer v1, FloatBuffer v2) {


  }

  @Override
  public void glRectfv(float[] v1, int v1_offset, float[] v2, int v2_offset) {


  }

  @Override
  public void glRectiv(IntBuffer v1, IntBuffer v2) {


  }

  @Override
  public void glRectiv(int[] v1, int v1_offset, int[] v2, int v2_offset) {


  }

  @Override
  public void glRectsv(ShortBuffer v1, ShortBuffer v2) {


  }

  @Override
  public void glRectsv(short[] v1, int v1_offset, short[] v2, int v2_offset) {


  }

  @Override
  public void glLighti(int light, int pname, int param) {


  }

  @Override
  public void glLightiv(int light, int pname, IntBuffer params) {


  }

  @Override
  public void glLightiv(int light, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetLightiv(int light, int pname, IntBuffer params) {


  }

  @Override
  public void glGetLightiv(int light, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glLightModeli(int pname, int param) {


  }

  @Override
  public void glLightModeliv(int pname, IntBuffer params) {


  }

  @Override
  public void glLightModeliv(int pname, int[] params, int params_offset) {


  }

  @Override
  public void glMateriali(int face, int pname, int param) {


  }

  @Override
  public void glMaterialiv(int face, int pname, IntBuffer params) {


  }

  @Override
  public void glMaterialiv(int face, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetMaterialiv(int face, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMaterialiv(int face, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glColorMaterial(int face, int mode) {


  }

  @Override
  public void glPixelZoom(float xfactor, float yfactor) {


  }

  @Override
  public void glPixelTransferf(int pname, float param) {


  }

  @Override
  public void glPixelTransferi(int pname, int param) {


  }

  @Override
  public void glPixelMapfv(int map, int mapsize, FloatBuffer values) {


  }

  @Override
  public void glPixelMapfv(int map, int mapsize, float[] values, int values_offset) {


  }

  @Override
  public void glPixelMapfv(int map, int mapsize, long values_buffer_offset) {


  }

  @Override
  public void glPixelMapuiv(int map, int mapsize, IntBuffer values) {


  }

  @Override
  public void glPixelMapuiv(int map, int mapsize, int[] values, int values_offset) {


  }

  @Override
  public void glPixelMapuiv(int map, int mapsize, long values_buffer_offset) {


  }

  @Override
  public void glPixelMapusv(int map, int mapsize, ShortBuffer values) {


  }

  @Override
  public void glPixelMapusv(int map, int mapsize, short[] values, int values_offset) {


  }

  @Override
  public void glPixelMapusv(int map, int mapsize, long values_buffer_offset) {


  }

  @Override
  public void glGetPixelMapfv(int map, FloatBuffer values) {


  }

  @Override
  public void glGetPixelMapfv(int map, float[] values, int values_offset) {


  }

  @Override
  public void glGetPixelMapfv(int map, long values_buffer_offset) {


  }

  @Override
  public void glGetPixelMapuiv(int map, IntBuffer values) {


  }

  @Override
  public void glGetPixelMapuiv(int map, int[] values, int values_offset) {


  }

  @Override
  public void glGetPixelMapuiv(int map, long values_buffer_offset) {


  }

  @Override
  public void glGetPixelMapusv(int map, ShortBuffer values) {


  }

  @Override
  public void glGetPixelMapusv(int map, short[] values, int values_offset) {


  }

  @Override
  public void glGetPixelMapusv(int map, long values_buffer_offset) {


  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      ByteBuffer bitmap) {


  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      byte[] bitmap, int bitmap_offset) {


  }

  @Override
  public void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove,
      long bitmap_buffer_offset) {


  }

  @Override
  public void glDrawPixels(int width, int height, int format, int type, Buffer pixels) {


  }

  @Override
  public void glDrawPixels(int width, int height, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glCopyPixels(int x, int y, int width, int height, int type) {


  }

  @Override
  public void glTexGend(int coord, int pname, double param) {


  }

  @Override
  public void glTexGenf(int coord, int pname, float param) {


  }

  @Override
  public void glTexGeni(int coord, int pname, int param) {


  }

  @Override
  public void glTexGendv(int coord, int pname, DoubleBuffer params) {


  }

  @Override
  public void glTexGendv(int coord, int pname, double[] params, int params_offset) {


  }

  @Override
  public void glTexGenfv(int coord, int pname, FloatBuffer params) {


  }

  @Override
  public void glTexGenfv(int coord, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glTexGeniv(int coord, int pname, IntBuffer params) {


  }

  @Override
  public void glTexGeniv(int coord, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetTexGendv(int coord, int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetTexGendv(int coord, int pname, double[] params, int params_offset) {


  }

  @Override
  public void glGetTexGenfv(int coord, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetTexGenfv(int coord, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetTexGeniv(int coord, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTexGeniv(int coord, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glMap1d(int target, double u1, double u2, int stride, int order,
      DoubleBuffer points) {


  }

  @Override
  public void glMap1d(int target, double u1, double u2, int stride, int order, double[] points,
      int points_offset) {


  }

  @Override
  public void glMap1f(int target, float u1, float u2, int stride, int order, FloatBuffer points) {


  }

  @Override
  public void glMap1f(int target, float u1, float u2, int stride, int order, float[] points,
      int points_offset) {


  }

  @Override
  public void glMap2d(int target, double u1, double u2, int ustride, int uorder, double v1,
      double v2, int vstride, int vorder, DoubleBuffer points) {


  }

  @Override
  public void glMap2d(int target, double u1, double u2, int ustride, int uorder, double v1,
      double v2, int vstride, int vorder, double[] points, int points_offset) {


  }

  @Override
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, FloatBuffer points) {


  }

  @Override
  public void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2,
      int vstride, int vorder, float[] points, int points_offset) {


  }

  @Override
  public void glGetMapdv(int target, int query, DoubleBuffer v) {


  }

  @Override
  public void glGetMapdv(int target, int query, double[] v, int v_offset) {


  }

  @Override
  public void glGetMapfv(int target, int query, FloatBuffer v) {


  }

  @Override
  public void glGetMapfv(int target, int query, float[] v, int v_offset) {


  }

  @Override
  public void glGetMapiv(int target, int query, IntBuffer v) {


  }

  @Override
  public void glGetMapiv(int target, int query, int[] v, int v_offset) {


  }

  @Override
  public void glEvalCoord1d(double u) {


  }

  @Override
  public void glEvalCoord1f(float u) {


  }

  @Override
  public void glEvalCoord1dv(DoubleBuffer u) {


  }

  @Override
  public void glEvalCoord1dv(double[] u, int u_offset) {


  }

  @Override
  public void glEvalCoord1fv(FloatBuffer u) {


  }

  @Override
  public void glEvalCoord1fv(float[] u, int u_offset) {


  }

  @Override
  public void glEvalCoord2d(double u, double v) {


  }

  @Override
  public void glEvalCoord2f(float u, float v) {


  }

  @Override
  public void glEvalCoord2dv(DoubleBuffer u) {


  }

  @Override
  public void glEvalCoord2dv(double[] u, int u_offset) {


  }

  @Override
  public void glEvalCoord2fv(FloatBuffer u) {


  }

  @Override
  public void glEvalCoord2fv(float[] u, int u_offset) {


  }

  @Override
  public void glMapGrid1d(int un, double u1, double u2) {


  }

  @Override
  public void glMapGrid1f(int un, float u1, float u2) {


  }

  @Override
  public void glMapGrid2d(int un, double u1, double u2, int vn, double v1, double v2) {


  }

  @Override
  public void glMapGrid2f(int un, float u1, float u2, int vn, float v1, float v2) {


  }

  @Override
  public void glEvalPoint1(int i) {


  }

  @Override
  public void glEvalPoint2(int i, int j) {


  }

  @Override
  public void glEvalMesh1(int mode, int i1, int i2) {


  }

  @Override
  public void glEvalMesh2(int mode, int i1, int i2, int j1, int j2) {


  }

  @Override
  public void glFogi(int pname, int param) {


  }

  @Override
  public void glFogiv(int pname, IntBuffer params) {


  }

  @Override
  public void glFogiv(int pname, int[] params, int params_offset) {


  }

  @Override
  public void glFeedbackBuffer(int size, int type, FloatBuffer buffer) {


  }

  @Override
  public void glPassThrough(float token) {


  }

  @Override
  public void glSelectBuffer(int size, IntBuffer buffer) {


  }

  @Override
  public void glInitNames() {


  }

  @Override
  public void glLoadName(int name) {


  }

  @Override
  public void glPushName(int name) {


  }

  @Override
  public void glPopName() {


  }

  @Override
  public void glIndexub(byte c) {


  }

  @Override
  public void glIndexubv(ByteBuffer c) {


  }

  @Override
  public void glIndexubv(byte[] c, int c_offset) {


  }

  @Override
  public void glPushClientAttrib(int mask) {


  }

  @Override
  public void glPopClientAttrib() {


  }

  @Override
  public void glIndexPointer(int type, int stride, Buffer ptr) {


  }

  @Override
  public void glEdgeFlagPointer(int stride, Buffer ptr) {


  }

  @Override
  public void glEdgeFlagPointer(int stride, long ptr_buffer_offset) {


  }

  @Override
  public void glArrayElement(int i) {


  }

  @Override
  public void glInterleavedArrays(int format, int stride, Buffer pointer) {


  }

  @Override
  public void glInterleavedArrays(int format, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glPrioritizeTextures(int n, IntBuffer textures, FloatBuffer priorities) {


  }

  @Override
  public void glPrioritizeTextures(int n, int[] textures, int textures_offset, float[] priorities,
      int priorities_offset) {


  }

  @Override
  public boolean glAreTexturesResident(int n, IntBuffer textures, ByteBuffer residences) {

    return false;
  }

  @Override
  public boolean glAreTexturesResident(int n, int[] textures, int textures_offset,
      byte[] residences, int residences_offset) {

    return false;
  }

  @Override
  public void glMultiTexCoord1d(int target, double s) {


  }

  @Override
  public void glMultiTexCoord1dv(int target, DoubleBuffer v) {


  }

  @Override
  public void glMultiTexCoord1dv(int target, double[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord1f(int target, float s) {


  }

  @Override
  public void glMultiTexCoord1fv(int target, FloatBuffer v) {


  }

  @Override
  public void glMultiTexCoord1fv(int target, float[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord1i(int target, int s) {


  }

  @Override
  public void glMultiTexCoord1iv(int target, IntBuffer v) {


  }

  @Override
  public void glMultiTexCoord1iv(int target, int[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord1s(int target, short s) {


  }

  @Override
  public void glMultiTexCoord1sv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord1sv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord2d(int target, double s, double t) {


  }

  @Override
  public void glMultiTexCoord2dv(int target, DoubleBuffer v) {


  }

  @Override
  public void glMultiTexCoord2dv(int target, double[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord2f(int target, float s, float t) {


  }

  @Override
  public void glMultiTexCoord2fv(int target, FloatBuffer v) {


  }

  @Override
  public void glMultiTexCoord2fv(int target, float[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord2i(int target, int s, int t) {


  }

  @Override
  public void glMultiTexCoord2iv(int target, IntBuffer v) {


  }

  @Override
  public void glMultiTexCoord2iv(int target, int[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord2s(int target, short s, short t) {


  }

  @Override
  public void glMultiTexCoord2sv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord2sv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord3d(int target, double s, double t, double r) {


  }

  @Override
  public void glMultiTexCoord3dv(int target, DoubleBuffer v) {


  }

  @Override
  public void glMultiTexCoord3dv(int target, double[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord3f(int target, float s, float t, float r) {


  }

  @Override
  public void glMultiTexCoord3fv(int target, FloatBuffer v) {


  }

  @Override
  public void glMultiTexCoord3fv(int target, float[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord3i(int target, int s, int t, int r) {


  }

  @Override
  public void glMultiTexCoord3iv(int target, IntBuffer v) {


  }

  @Override
  public void glMultiTexCoord3iv(int target, int[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord3s(int target, short s, short t, short r) {


  }

  @Override
  public void glMultiTexCoord3sv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord3sv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord4d(int target, double s, double t, double r, double q) {


  }

  @Override
  public void glMultiTexCoord4dv(int target, DoubleBuffer v) {


  }

  @Override
  public void glMultiTexCoord4dv(int target, double[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord4fv(int target, FloatBuffer v) {


  }

  @Override
  public void glMultiTexCoord4fv(int target, float[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord4i(int target, int s, int t, int r, int q) {


  }

  @Override
  public void glMultiTexCoord4iv(int target, IntBuffer v) {


  }

  @Override
  public void glMultiTexCoord4iv(int target, int[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord4s(int target, short s, short t, short r, short q) {


  }

  @Override
  public void glMultiTexCoord4sv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord4sv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glLoadTransposeMatrixf(FloatBuffer m) {


  }

  @Override
  public void glLoadTransposeMatrixf(float[] m, int m_offset) {


  }

  @Override
  public void glLoadTransposeMatrixd(DoubleBuffer m) {


  }

  @Override
  public void glLoadTransposeMatrixd(double[] m, int m_offset) {


  }

  @Override
  public void glMultTransposeMatrixf(FloatBuffer m) {


  }

  @Override
  public void glMultTransposeMatrixf(float[] m, int m_offset) {


  }

  @Override
  public void glMultTransposeMatrixd(DoubleBuffer m) {


  }

  @Override
  public void glMultTransposeMatrixd(double[] m, int m_offset) {


  }

  @Override
  public void glFogCoordf(float coord) {


  }

  @Override
  public void glFogCoordfv(FloatBuffer coord) {


  }

  @Override
  public void glFogCoordfv(float[] coord, int coord_offset) {


  }

  @Override
  public void glFogCoordd(double coord) {


  }

  @Override
  public void glFogCoorddv(DoubleBuffer coord) {


  }

  @Override
  public void glFogCoorddv(double[] coord, int coord_offset) {


  }

  @Override
  public void glFogCoordPointer(int type, int stride, Buffer pointer) {


  }

  @Override
  public void glFogCoordPointer(int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glSecondaryColor3b(byte red, byte green, byte blue) {


  }

  @Override
  public void glSecondaryColor3bv(ByteBuffer v) {


  }

  @Override
  public void glSecondaryColor3bv(byte[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3d(double red, double green, double blue) {


  }

  @Override
  public void glSecondaryColor3dv(DoubleBuffer v) {


  }

  @Override
  public void glSecondaryColor3dv(double[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3f(float red, float green, float blue) {


  }

  @Override
  public void glSecondaryColor3fv(FloatBuffer v) {


  }

  @Override
  public void glSecondaryColor3fv(float[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3i(int red, int green, int blue) {


  }

  @Override
  public void glSecondaryColor3iv(IntBuffer v) {


  }

  @Override
  public void glSecondaryColor3iv(int[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3s(short red, short green, short blue) {


  }

  @Override
  public void glSecondaryColor3sv(ShortBuffer v) {


  }

  @Override
  public void glSecondaryColor3sv(short[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3ub(byte red, byte green, byte blue) {


  }

  @Override
  public void glSecondaryColor3ubv(ByteBuffer v) {


  }

  @Override
  public void glSecondaryColor3ubv(byte[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3ui(int red, int green, int blue) {


  }

  @Override
  public void glSecondaryColor3uiv(IntBuffer v) {


  }

  @Override
  public void glSecondaryColor3uiv(int[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColor3us(short red, short green, short blue) {


  }

  @Override
  public void glSecondaryColor3usv(ShortBuffer v) {


  }

  @Override
  public void glSecondaryColor3usv(short[] v, int v_offset) {


  }

  @Override
  public void glSecondaryColorPointer(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glSecondaryColorPointer(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glWindowPos2d(double x, double y) {


  }

  @Override
  public void glWindowPos2dv(DoubleBuffer v) {


  }

  @Override
  public void glWindowPos2dv(double[] v, int v_offset) {


  }

  @Override
  public void glWindowPos2f(float x, float y) {


  }

  @Override
  public void glWindowPos2fv(FloatBuffer v) {


  }

  @Override
  public void glWindowPos2fv(float[] v, int v_offset) {


  }

  @Override
  public void glWindowPos2i(int x, int y) {


  }

  @Override
  public void glWindowPos2iv(IntBuffer v) {


  }

  @Override
  public void glWindowPos2iv(int[] v, int v_offset) {


  }

  @Override
  public void glWindowPos2s(short x, short y) {


  }

  @Override
  public void glWindowPos2sv(ShortBuffer v) {


  }

  @Override
  public void glWindowPos2sv(short[] v, int v_offset) {


  }

  @Override
  public void glWindowPos3d(double x, double y, double z) {


  }

  @Override
  public void glWindowPos3dv(DoubleBuffer v) {


  }

  @Override
  public void glWindowPos3dv(double[] v, int v_offset) {


  }

  @Override
  public void glWindowPos3f(float x, float y, float z) {


  }

  @Override
  public void glWindowPos3fv(FloatBuffer v) {


  }

  @Override
  public void glWindowPos3fv(float[] v, int v_offset) {


  }

  @Override
  public void glWindowPos3i(int x, int y, int z) {


  }

  @Override
  public void glWindowPos3iv(IntBuffer v) {


  }

  @Override
  public void glWindowPos3iv(int[] v, int v_offset) {


  }

  @Override
  public void glWindowPos3s(short x, short y, short z) {


  }

  @Override
  public void glWindowPos3sv(ShortBuffer v) {


  }

  @Override
  public void glWindowPos3sv(short[] v, int v_offset) {


  }

  @Override
  public void glClearNamedBufferData(int buffer, int internalformat, int format, int type,
      Buffer data) {


  }

  @Override
  public void glClearNamedBufferSubData(int buffer, int internalformat, long offset, long size,
      int format, int type, Buffer data) {


  }

  @Override
  public void glNamedFramebufferParameteri(int framebuffer, int pname, int param) {


  }

  @Override
  public void glGetNamedFramebufferParameteriv(int framebuffer, int pname, IntBuffer param) {


  }

  @Override
  public void glGetNamedFramebufferParameteriv(int framebuffer, int pname, int[] param,
      int param_offset) {


  }

  @Override
  public void glGetnMapdv(int target, int query, int bufSize, DoubleBuffer v) {


  }

  @Override
  public void glGetnMapdv(int target, int query, int bufSize, double[] v, int v_offset) {


  }

  @Override
  public void glGetnMapfv(int target, int query, int bufSize, FloatBuffer v) {


  }

  @Override
  public void glGetnMapfv(int target, int query, int bufSize, float[] v, int v_offset) {


  }

  @Override
  public void glGetnMapiv(int target, int query, int bufSize, IntBuffer v) {


  }

  @Override
  public void glGetnMapiv(int target, int query, int bufSize, int[] v, int v_offset) {


  }

  @Override
  public void glGetnPixelMapfv(int map, int bufSize, FloatBuffer values) {


  }

  @Override
  public void glGetnPixelMapfv(int map, int bufSize, float[] values, int values_offset) {


  }

  @Override
  public void glGetnPixelMapuiv(int map, int bufSize, IntBuffer values) {


  }

  @Override
  public void glGetnPixelMapuiv(int map, int bufSize, int[] values, int values_offset) {


  }

  @Override
  public void glGetnPixelMapusv(int map, int bufSize, ShortBuffer values) {


  }

  @Override
  public void glGetnPixelMapusv(int map, int bufSize, short[] values, int values_offset) {


  }

  @Override
  public void glGetnPolygonStipple(int bufSize, ByteBuffer pattern) {


  }

  @Override
  public void glGetnPolygonStipple(int bufSize, byte[] pattern, int pattern_offset) {


  }

  @Override
  public void glGetnColorTable(int target, int format, int type, int bufSize, Buffer table) {


  }

  @Override
  public void glGetnConvolutionFilter(int target, int format, int type, int bufSize, Buffer image) {


  }

  @Override
  public void glGetnSeparableFilter(int target, int format, int type, int rowBufSize, Buffer row,
      int columnBufSize, Buffer column, Buffer span) {


  }

  @Override
  public void glGetnHistogram(int target, boolean reset, int format, int type, int bufSize,
      Buffer values) {


  }

  @Override
  public void glGetnMinmax(int target, boolean reset, int format, int type, int bufSize,
      Buffer values) {


  }

  @Override
  public void glProgramStringARB(int target, int format, int len, String string) {


  }

  @Override
  public void glBindProgramARB(int target, int program) {


  }

  @Override
  public void glDeleteProgramsARB(int n, IntBuffer programs) {


  }

  @Override
  public void glDeleteProgramsARB(int n, int[] programs, int programs_offset) {


  }

  @Override
  public void glGenProgramsARB(int n, IntBuffer programs) {


  }

  @Override
  public void glGenProgramsARB(int n, int[] programs, int programs_offset) {


  }

  @Override
  public void glProgramEnvParameter4dARB(int target, int index, double x, double y, double z,
      double w) {


  }

  @Override
  public void glProgramEnvParameter4dvARB(int target, int index, DoubleBuffer params) {


  }

  @Override
  public void glProgramEnvParameter4dvARB(int target, int index, double[] params,
      int params_offset) {


  }

  @Override
  public void glProgramEnvParameter4fARB(int target, int index, float x, float y, float z,
      float w) {


  }

  @Override
  public void glProgramEnvParameter4fvARB(int target, int index, FloatBuffer params) {


  }

  @Override
  public void glProgramEnvParameter4fvARB(int target, int index, float[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParameter4dARB(int target, int index, double x, double y, double z,
      double w) {


  }

  @Override
  public void glProgramLocalParameter4dvARB(int target, int index, DoubleBuffer params) {


  }

  @Override
  public void glProgramLocalParameter4dvARB(int target, int index, double[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParameter4fARB(int target, int index, float x, float y, float z,
      float w) {


  }

  @Override
  public void glProgramLocalParameter4fvARB(int target, int index, FloatBuffer params) {


  }

  @Override
  public void glProgramLocalParameter4fvARB(int target, int index, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramEnvParameterdvARB(int target, int index, DoubleBuffer params) {


  }

  @Override
  public void glGetProgramEnvParameterdvARB(int target, int index, double[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramEnvParameterfvARB(int target, int index, FloatBuffer params) {


  }

  @Override
  public void glGetProgramEnvParameterfvARB(int target, int index, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramLocalParameterdvARB(int target, int index, DoubleBuffer params) {


  }

  @Override
  public void glGetProgramLocalParameterdvARB(int target, int index, double[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramLocalParameterfvARB(int target, int index, FloatBuffer params) {


  }

  @Override
  public void glGetProgramLocalParameterfvARB(int target, int index, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramivARB(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetProgramivARB(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetProgramStringARB(int target, int pname, Buffer string) {


  }

  @Override
  public boolean glIsProgramARB(int program) {

    return false;
  }



  @Override
  public void glColorTable(int target, int internalformat, int width, int format, int type,
      Buffer table) {


  }

  @Override
  public void glColorTable(int target, int internalformat, int width, int format, int type,
      long table_buffer_offset) {


  }

  @Override
  public void glColorTableParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glColorTableParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glColorTableParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glColorTableParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glCopyColorTable(int target, int internalformat, int x, int y, int width) {


  }

  @Override
  public void glGetColorTable(int target, int format, int type, Buffer table) {


  }

  @Override
  public void glGetColorTable(int target, int format, int type, long table_buffer_offset) {


  }

  @Override
  public void glGetColorTableParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetColorTableParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetColorTableParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetColorTableParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glColorSubTable(int target, int start, int count, int format, int type, Buffer data) {


  }

  @Override
  public void glColorSubTable(int target, int start, int count, int format, int type,
      long data_buffer_offset) {


  }

  @Override
  public void glCopyColorSubTable(int target, int start, int x, int y, int width) {


  }

  @Override
  public void glConvolutionFilter1D(int target, int internalformat, int width, int format, int type,
      Buffer image) {


  }

  @Override
  public void glConvolutionFilter1D(int target, int internalformat, int width, int format, int type,
      long image_buffer_offset) {


  }

  @Override
  public void glConvolutionFilter2D(int target, int internalformat, int width, int height,
      int format, int type, Buffer image) {


  }

  @Override
  public void glConvolutionFilter2D(int target, int internalformat, int width, int height,
      int format, int type, long image_buffer_offset) {


  }

  @Override
  public void glConvolutionParameterf(int target, int pname, float params) {


  }

  @Override
  public void glConvolutionParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glConvolutionParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glConvolutionParameteri(int target, int pname, int params) {


  }

  @Override
  public void glConvolutionParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glConvolutionParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glCopyConvolutionFilter1D(int target, int internalformat, int x, int y, int width) {


  }

  @Override
  public void glCopyConvolutionFilter2D(int target, int internalformat, int x, int y, int width,
      int height) {


  }

  @Override
  public void glGetConvolutionFilter(int target, int format, int type, Buffer image) {


  }

  @Override
  public void glGetConvolutionFilter(int target, int format, int type, long image_buffer_offset) {


  }

  @Override
  public void glGetConvolutionParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetConvolutionParameterfv(int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetConvolutionParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetConvolutionParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetSeparableFilter(int target, int format, int type, Buffer row, Buffer column,
      Buffer span) {


  }

  @Override
  public void glGetSeparableFilter(int target, int format, int type, long row_buffer_offset,
      long column_buffer_offset, long span_buffer_offset) {


  }

  @Override
  public void glSeparableFilter2D(int target, int internalformat, int width, int height, int format,
      int type, Buffer row, Buffer column) {


  }

  @Override
  public void glSeparableFilter2D(int target, int internalformat, int width, int height, int format,
      int type, long row_buffer_offset, long column_buffer_offset) {


  }

  @Override
  public void glGetHistogram(int target, boolean reset, int format, int type, Buffer values) {


  }

  @Override
  public void glGetHistogram(int target, boolean reset, int format, int type,
      long values_buffer_offset) {


  }

  @Override
  public void glGetHistogramParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetHistogramParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetHistogramParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetHistogramParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetMinmax(int target, boolean reset, int format, int type, Buffer values) {


  }

  @Override
  public void glGetMinmax(int target, boolean reset, int format, int type,
      long values_buffer_offset) {


  }

  @Override
  public void glGetMinmaxParameterfv(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMinmaxParameterfv(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetMinmaxParameteriv(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMinmaxParameteriv(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glHistogram(int target, int width, int internalformat, boolean sink) {


  }

  @Override
  public void glMinmax(int target, int internalformat, boolean sink) {


  }

  @Override
  public void glResetHistogram(int target) {


  }

  @Override
  public void glResetMinmax(int target) {


  }

  @Override
  public void glCurrentPaletteMatrixARB(int index) {


  }

  @Override
  public void glMatrixIndexubvARB(int size, ByteBuffer indices) {


  }

  @Override
  public void glMatrixIndexubvARB(int size, byte[] indices, int indices_offset) {


  }

  @Override
  public void glMatrixIndexusvARB(int size, ShortBuffer indices) {


  }

  @Override
  public void glMatrixIndexusvARB(int size, short[] indices, int indices_offset) {


  }

  @Override
  public void glMatrixIndexuivARB(int size, IntBuffer indices) {


  }

  @Override
  public void glMatrixIndexuivARB(int size, int[] indices, int indices_offset) {


  }

  @Override
  public void glMatrixIndexPointerARB(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glMatrixIndexPointerARB(int size, int type, int stride, long pointer_buffer_offset) {


  }



  @Override
  public void glDeleteObjectARB(long obj) {


  }

  @Override
  public long glGetHandleARB(int pname) {

    return 0;
  }

  @Override
  public void glDetachObjectARB(long containerObj, long attachedObj) {


  }

  @Override
  public long glCreateShaderObjectARB(int shaderType) {

    return 0;
  }

  @Override
  public void glShaderSourceARB(long shaderObj, int count, String[] string, IntBuffer length) {


  }

  @Override
  public void glShaderSourceARB(long shaderObj, int count, String[] string, int[] length,
      int length_offset) {


  }

  @Override
  public void glCompileShaderARB(long shaderObj) {


  }

  @Override
  public long glCreateProgramObjectARB() {

    return 0;
  }

  @Override
  public void glAttachObjectARB(long containerObj, long obj) {


  }

  @Override
  public void glLinkProgramARB(long programObj) {


  }

  @Override
  public void glUseProgramObjectARB(long programObj) {


  }

  @Override
  public void glValidateProgramARB(long programObj) {


  }

  @Override
  public void glUniform1fARB(int location, float v0) {


  }

  @Override
  public void glUniform2fARB(int location, float v0, float v1) {


  }

  @Override
  public void glUniform3fARB(int location, float v0, float v1, float v2) {


  }

  @Override
  public void glUniform4fARB(int location, float v0, float v1, float v2, float v3) {


  }

  @Override
  public void glUniform1iARB(int location, int v0) {


  }

  @Override
  public void glUniform2iARB(int location, int v0, int v1) {


  }

  @Override
  public void glUniform3iARB(int location, int v0, int v1, int v2) {


  }

  @Override
  public void glUniform4iARB(int location, int v0, int v1, int v2, int v3) {


  }

  @Override
  public void glUniform1fvARB(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform1fvARB(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform2fvARB(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform2fvARB(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform3fvARB(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform3fvARB(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform4fvARB(int location, int count, FloatBuffer value) {


  }

  @Override
  public void glUniform4fvARB(int location, int count, float[] value, int value_offset) {


  }

  @Override
  public void glUniform1ivARB(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform1ivARB(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform2ivARB(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform2ivARB(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform3ivARB(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform3ivARB(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniform4ivARB(int location, int count, IntBuffer value) {


  }

  @Override
  public void glUniform4ivARB(int location, int count, int[] value, int value_offset) {


  }

  @Override
  public void glUniformMatrix2fvARB(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix2fvARB(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix3fvARB(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix3fvARB(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glUniformMatrix4fvARB(int location, int count, boolean transpose, FloatBuffer value) {


  }

  @Override
  public void glUniformMatrix4fvARB(int location, int count, boolean transpose, float[] value,
      int value_offset) {


  }

  @Override
  public void glGetObjectParameterfvARB(long obj, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetObjectParameterfvARB(long obj, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetObjectParameterivARB(long obj, int pname, IntBuffer params) {


  }

  @Override
  public void glGetObjectParameterivARB(long obj, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetInfoLogARB(long obj, int maxLength, IntBuffer length, ByteBuffer infoLog) {


  }

  @Override
  public void glGetInfoLogARB(long obj, int maxLength, int[] length, int length_offset,
      byte[] infoLog, int infoLog_offset) {


  }

  @Override
  public void glGetAttachedObjectsARB(long containerObj, int maxCount, IntBuffer count,
      LongBuffer obj) {


  }

  @Override
  public void glGetAttachedObjectsARB(long containerObj, int maxCount, int[] count,
      int count_offset, long[] obj, int obj_offset) {


  }

  @Override
  public int glGetUniformLocationARB(long programObj, String name) {

    return 0;
  }

  @Override
  public void glGetActiveUniformARB(long programObj, int index, int maxLength, IntBuffer length,
      IntBuffer size, IntBuffer type, ByteBuffer name) {


  }

  @Override
  public void glGetActiveUniformARB(long programObj, int index, int maxLength, int[] length,
      int length_offset, int[] size, int size_offset, int[] type, int type_offset, byte[] name,
      int name_offset) {


  }

  @Override
  public void glGetUniformfvARB(long programObj, int location, FloatBuffer params) {


  }

  @Override
  public void glGetUniformfvARB(long programObj, int location, float[] params, int params_offset) {


  }

  @Override
  public void glGetUniformivARB(long programObj, int location, IntBuffer params) {


  }

  @Override
  public void glGetUniformivARB(long programObj, int location, int[] params, int params_offset) {


  }

  @Override
  public void glGetShaderSourceARB(long obj, int maxLength, IntBuffer length, ByteBuffer source) {


  }

  @Override
  public void glGetShaderSourceARB(long obj, int maxLength, int[] length, int length_offset,
      byte[] source, int source_offset) {


  }

  @Override
  public void glWeightbvARB(int size, ByteBuffer weights) {


  }

  @Override
  public void glWeightbvARB(int size, byte[] weights, int weights_offset) {


  }

  @Override
  public void glWeightsvARB(int size, ShortBuffer weights) {


  }

  @Override
  public void glWeightsvARB(int size, short[] weights, int weights_offset) {


  }

  @Override
  public void glWeightivARB(int size, IntBuffer weights) {


  }

  @Override
  public void glWeightivARB(int size, int[] weights, int weights_offset) {


  }

  @Override
  public void glWeightfvARB(int size, FloatBuffer weights) {


  }

  @Override
  public void glWeightfvARB(int size, float[] weights, int weights_offset) {


  }

  @Override
  public void glWeightdvARB(int size, DoubleBuffer weights) {


  }

  @Override
  public void glWeightdvARB(int size, double[] weights, int weights_offset) {


  }

  @Override
  public void glWeightubvARB(int size, ByteBuffer weights) {


  }

  @Override
  public void glWeightubvARB(int size, byte[] weights, int weights_offset) {


  }

  @Override
  public void glWeightusvARB(int size, ShortBuffer weights) {


  }

  @Override
  public void glWeightusvARB(int size, short[] weights, int weights_offset) {


  }

  @Override
  public void glWeightuivARB(int size, IntBuffer weights) {


  }

  @Override
  public void glWeightuivARB(int size, int[] weights, int weights_offset) {


  }

  @Override
  public void glWeightPointerARB(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glWeightPointerARB(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glVertexBlendARB(int count) {


  }

  @Override
  public void glVertexAttrib1dARB(int index, double x) {


  }

  @Override
  public void glVertexAttrib1dvARB(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib1dvARB(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib1fARB(int index, float x) {


  }

  @Override
  public void glVertexAttrib1fvARB(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib1fvARB(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib1sARB(int index, short x) {


  }

  @Override
  public void glVertexAttrib1svARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib1svARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2dARB(int index, double x, double y) {


  }

  @Override
  public void glVertexAttrib2dvARB(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib2dvARB(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2fARB(int index, float x, float y) {


  }

  @Override
  public void glVertexAttrib2fvARB(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib2fvARB(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2sARB(int index, short x, short y) {


  }

  @Override
  public void glVertexAttrib2svARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib2svARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3dARB(int index, double x, double y, double z) {


  }

  @Override
  public void glVertexAttrib3dvARB(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib3dvARB(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3fARB(int index, float x, float y, float z) {


  }

  @Override
  public void glVertexAttrib3fvARB(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib3fvARB(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3sARB(int index, short x, short y, short z) {


  }

  @Override
  public void glVertexAttrib3svARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib3svARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NbvARB(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4NbvARB(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NivARB(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4NivARB(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NsvARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4NsvARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NubARB(int index, byte x, byte y, byte z, byte w) {


  }

  @Override
  public void glVertexAttrib4NubvARB(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4NubvARB(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NuivARB(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4NuivARB(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4NusvARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4NusvARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4bvARB(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4bvARB(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4dARB(int index, double x, double y, double z, double w) {


  }

  @Override
  public void glVertexAttrib4dvARB(int index, DoubleBuffer v) {


  }

  @Override
  public void glVertexAttrib4dvARB(int index, double[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4fARB(int index, float x, float y, float z, float w) {


  }

  @Override
  public void glVertexAttrib4fvARB(int index, FloatBuffer v) {


  }

  @Override
  public void glVertexAttrib4fvARB(int index, float[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4ivARB(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4ivARB(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4sARB(int index, short x, short y, short z, short w) {


  }

  @Override
  public void glVertexAttrib4svARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4svARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4ubvARB(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttrib4ubvARB(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4uivARB(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttrib4uivARB(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4usvARB(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4usvARB(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribPointerARB(int index, int size, int type, boolean normalized,
      int stride, Buffer pointer) {


  }

  @Override
  public void glVertexAttribPointerARB(int index, int size, int type, boolean normalized,
      int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glEnableVertexAttribArrayARB(int index) {


  }

  @Override
  public void glDisableVertexAttribArrayARB(int index) {


  }

  @Override
  public void glGetVertexAttribdvARB(int index, int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetVertexAttribdvARB(int index, int pname, double[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribfvARB(int index, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetVertexAttribfvARB(int index, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribivARB(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribivARB(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glBlendBarrier() {


  }

  @Override
  public void glMultiTexCoord1bOES(int texture, byte s) {


  }

  @Override
  public void glMultiTexCoord1bvOES(int texture, ByteBuffer coords) {


  }

  @Override
  public void glMultiTexCoord1bvOES(int texture, byte[] coords, int coords_offset) {


  }

  @Override
  public void glMultiTexCoord2bOES(int texture, byte s, byte t) {


  }

  @Override
  public void glMultiTexCoord2bvOES(int texture, ByteBuffer coords) {


  }

  @Override
  public void glMultiTexCoord2bvOES(int texture, byte[] coords, int coords_offset) {


  }

  @Override
  public void glMultiTexCoord3bOES(int texture, byte s, byte t, byte r) {


  }

  @Override
  public void glMultiTexCoord3bvOES(int texture, ByteBuffer coords) {


  }

  @Override
  public void glMultiTexCoord3bvOES(int texture, byte[] coords, int coords_offset) {


  }

  @Override
  public void glMultiTexCoord4bOES(int texture, byte s, byte t, byte r, byte q) {


  }

  @Override
  public void glMultiTexCoord4bvOES(int texture, ByteBuffer coords) {


  }

  @Override
  public void glMultiTexCoord4bvOES(int texture, byte[] coords, int coords_offset) {


  }

  @Override
  public void glTexCoord1bOES(byte s) {


  }

  @Override
  public void glTexCoord1bvOES(ByteBuffer coords) {


  }

  @Override
  public void glTexCoord1bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glTexCoord2bOES(byte s, byte t) {


  }

  @Override
  public void glTexCoord2bvOES(ByteBuffer coords) {


  }

  @Override
  public void glTexCoord2bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glTexCoord3bOES(byte s, byte t, byte r) {


  }

  @Override
  public void glTexCoord3bvOES(ByteBuffer coords) {


  }

  @Override
  public void glTexCoord3bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glTexCoord4bOES(byte s, byte t, byte r, byte q) {


  }

  @Override
  public void glTexCoord4bvOES(ByteBuffer coords) {


  }

  @Override
  public void glTexCoord4bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glVertex2bOES(byte x, byte y) {


  }

  @Override
  public void glVertex2bvOES(ByteBuffer coords) {


  }

  @Override
  public void glVertex2bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glVertex3bOES(byte x, byte y, byte z) {


  }

  @Override
  public void glVertex3bvOES(ByteBuffer coords) {


  }

  @Override
  public void glVertex3bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public void glVertex4bOES(byte x, byte y, byte z, byte w) {


  }

  @Override
  public void glVertex4bvOES(ByteBuffer coords) {


  }

  @Override
  public void glVertex4bvOES(byte[] coords, int coords_offset) {


  }

  @Override
  public int glQueryMatrixxOES(IntBuffer mantissa, IntBuffer exponent) {

    return 0;
  }

  @Override
  public int glQueryMatrixxOES(int[] mantissa, int mantissa_offset, int[] exponent,
      int exponent_offset) {

    return 0;
  }

  @Override
  public void glClipPlanef(int plane, FloatBuffer equation) {


  }

  @Override
  public void glClipPlanef(int plane, float[] equation, int equation_offset) {


  }

  @Override
  public void glGetClipPlanef(int plane, FloatBuffer equation) {


  }

  @Override
  public void glGetClipPlanef(int plane, float[] equation, int equation_offset) {


  }

  @Override
  public void glBlendFuncIndexedAMD(int buf, int src, int dst) {


  }

  @Override
  public void glBlendFuncSeparateIndexedAMD(int buf, int srcRGB, int dstRGB, int srcAlpha,
      int dstAlpha) {


  }

  @Override
  public void glBlendEquationIndexedAMD(int buf, int mode) {


  }

  @Override
  public void glBlendEquationSeparateIndexedAMD(int buf, int modeRGB, int modeAlpha) {


  }

  @Override
  public void glUniform1i64NV(int location, long x) {


  }

  @Override
  public void glUniform2i64NV(int location, long x, long y) {


  }

  @Override
  public void glUniform3i64NV(int location, long x, long y, long z) {


  }

  @Override
  public void glUniform4i64NV(int location, long x, long y, long z, long w) {


  }

  @Override
  public void glUniform1i64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform1i64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform2i64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform2i64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform3i64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform3i64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform4i64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform4i64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform1ui64NV(int location, long x) {


  }

  @Override
  public void glUniform2ui64NV(int location, long x, long y) {


  }

  @Override
  public void glUniform3ui64NV(int location, long x, long y, long z) {


  }

  @Override
  public void glUniform4ui64NV(int location, long x, long y, long z, long w) {


  }

  @Override
  public void glUniform1ui64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform1ui64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform2ui64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform2ui64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform3ui64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform3ui64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glUniform4ui64vNV(int location, int count, LongBuffer value) {


  }

  @Override
  public void glUniform4ui64vNV(int location, int count, long[] value, int value_offset) {


  }

  @Override
  public void glGetUniformi64vNV(int program, int location, LongBuffer params) {


  }

  @Override
  public void glGetUniformi64vNV(int program, int location, long[] params, int params_offset) {


  }

  @Override
  public void glProgramUniform1i64NV(int program, int location, long x) {


  }

  @Override
  public void glProgramUniform2i64NV(int program, int location, long x, long y) {


  }

  @Override
  public void glProgramUniform3i64NV(int program, int location, long x, long y, long z) {


  }

  @Override
  public void glProgramUniform4i64NV(int program, int location, long x, long y, long z, long w) {


  }

  @Override
  public void glProgramUniform1i64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform1i64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2i64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform2i64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3i64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform3i64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4i64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform4i64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform1ui64NV(int program, int location, long x) {


  }

  @Override
  public void glProgramUniform2ui64NV(int program, int location, long x, long y) {


  }

  @Override
  public void glProgramUniform3ui64NV(int program, int location, long x, long y, long z) {


  }

  @Override
  public void glProgramUniform4ui64NV(int program, int location, long x, long y, long z, long w) {


  }

  @Override
  public void glProgramUniform1ui64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform1ui64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2ui64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform2ui64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3ui64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform3ui64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4ui64vNV(int program, int location, int count, LongBuffer value) {


  }

  @Override
  public void glProgramUniform4ui64vNV(int program, int location, int count, long[] value,
      int value_offset) {


  }

  @Override
  public void glVertexAttribParameteriAMD(int index, int pname, int param) {


  }

  @Override
  public void glGenNamesAMD(int identifier, int num, IntBuffer names) {


  }

  @Override
  public void glGenNamesAMD(int identifier, int num, int[] names, int names_offset) {


  }

  @Override
  public void glDeleteNamesAMD(int identifier, int num, IntBuffer names) {


  }

  @Override
  public void glDeleteNamesAMD(int identifier, int num, int[] names, int names_offset) {


  }

  @Override
  public boolean glIsNameAMD(int identifier, int name) {

    return false;
  }

  @Override
  public void glQueryObjectParameteruiAMD(int target, int id, int pname, int param) {


  }

  @Override
  public void glGetPerfMonitorGroupsAMD(IntBuffer numGroups, int groupsSize, IntBuffer groups) {


  }

  @Override
  public void glGetPerfMonitorGroupsAMD(int[] numGroups, int numGroups_offset, int groupsSize,
      int[] groups, int groups_offset) {


  }

  @Override
  public void glGetPerfMonitorCountersAMD(int group, IntBuffer numCounters,
      IntBuffer maxActiveCounters, int counterSize, IntBuffer counters) {


  }

  @Override
  public void glGetPerfMonitorCountersAMD(int group, int[] numCounters, int numCounters_offset,
      int[] maxActiveCounters, int maxActiveCounters_offset, int counterSize, int[] counters,
      int counters_offset) {


  }

  @Override
  public void glGetPerfMonitorGroupStringAMD(int group, int bufSize, IntBuffer length,
      ByteBuffer groupString) {


  }

  @Override
  public void glGetPerfMonitorGroupStringAMD(int group, int bufSize, int[] length,
      int length_offset, byte[] groupString, int groupString_offset) {


  }

  @Override
  public void glGetPerfMonitorCounterStringAMD(int group, int counter, int bufSize,
      IntBuffer length, ByteBuffer counterString) {


  }

  @Override
  public void glGetPerfMonitorCounterStringAMD(int group, int counter, int bufSize, int[] length,
      int length_offset, byte[] counterString, int counterString_offset) {


  }

  @Override
  public void glGetPerfMonitorCounterInfoAMD(int group, int counter, int pname, Buffer data) {


  }

  @Override
  public void glGenPerfMonitorsAMD(int n, IntBuffer monitors) {


  }

  @Override
  public void glGenPerfMonitorsAMD(int n, int[] monitors, int monitors_offset) {


  }

  @Override
  public void glDeletePerfMonitorsAMD(int n, IntBuffer monitors) {


  }

  @Override
  public void glDeletePerfMonitorsAMD(int n, int[] monitors, int monitors_offset) {


  }

  @Override
  public void glSelectPerfMonitorCountersAMD(int monitor, boolean enable, int group,
      int numCounters, IntBuffer counterList) {


  }

  @Override
  public void glSelectPerfMonitorCountersAMD(int monitor, boolean enable, int group,
      int numCounters, int[] counterList, int counterList_offset) {


  }

  @Override
  public void glBeginPerfMonitorAMD(int monitor) {


  }

  @Override
  public void glEndPerfMonitorAMD(int monitor) {


  }

  @Override
  public void glGetPerfMonitorCounterDataAMD(int monitor, int pname, int dataSize, IntBuffer data,
      IntBuffer bytesWritten) {


  }

  @Override
  public void glGetPerfMonitorCounterDataAMD(int monitor, int pname, int dataSize, int[] data,
      int data_offset, int[] bytesWritten, int bytesWritten_offset) {


  }

  @Override
  public void glTexStorageSparseAMD(int target, int internalFormat, int width, int height,
      int depth, int layers, int flags) {


  }

  @Override
  public void glTextureStorageSparseAMD(int texture, int target, int internalFormat, int width,
      int height, int depth, int layers, int flags) {


  }

  @Override
  public void glBufferParameteri(int target, int pname, int param) {


  }

  @Override
  public int glObjectPurgeableAPPLE(int objectType, int name, int option) {

    return 0;
  }

  @Override
  public int glObjectUnpurgeableAPPLE(int objectType, int name, int option) {

    return 0;
  }

  @Override
  public void glGetObjectParameterivAPPLE(int objectType, int name, int pname, IntBuffer params) {


  }

  @Override
  public void glGetObjectParameterivAPPLE(int objectType, int name, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glTextureRangeAPPLE(int target, int length, Buffer pointer) {


  }

  @Override
  public void glVertexArrayRangeAPPLE(int length, Buffer pointer) {


  }

  @Override
  public void glFlushVertexArrayRangeAPPLE(int length, Buffer pointer) {


  }

  @Override
  public void glVertexArrayParameteriAPPLE(int pname, int param) {


  }

  @Override
  public void glEnableVertexAttribAPPLE(int index, int pname) {


  }

  @Override
  public void glDisableVertexAttribAPPLE(int index, int pname) {


  }

  @Override
  public boolean glIsVertexAttribEnabledAPPLE(int index, int pname) {

    return false;
  }

  @Override
  public void glMapVertexAttrib1dAPPLE(int index, int size, double u1, double u2, int stride,
      int order, DoubleBuffer points) {


  }

  @Override
  public void glMapVertexAttrib1dAPPLE(int index, int size, double u1, double u2, int stride,
      int order, double[] points, int points_offset) {


  }

  @Override
  public void glMapVertexAttrib1fAPPLE(int index, int size, float u1, float u2, int stride,
      int order, FloatBuffer points) {


  }

  @Override
  public void glMapVertexAttrib1fAPPLE(int index, int size, float u1, float u2, int stride,
      int order, float[] points, int points_offset) {


  }

  @Override
  public void glMapVertexAttrib2dAPPLE(int index, int size, double u1, double u2, int ustride,
      int uorder, double v1, double v2, int vstride, int vorder, DoubleBuffer points) {


  }

  @Override
  public void glMapVertexAttrib2dAPPLE(int index, int size, double u1, double u2, int ustride,
      int uorder, double v1, double v2, int vstride, int vorder, double[] points,
      int points_offset) {


  }

  @Override
  public void glMapVertexAttrib2fAPPLE(int index, int size, float u1, float u2, int ustride,
      int uorder, float v1, float v2, int vstride, int vorder, FloatBuffer points) {


  }

  @Override
  public void glMapVertexAttrib2fAPPLE(int index, int size, float u1, float u2, int ustride,
      int uorder, float v1, float v2, int vstride, int vorder, float[] points, int points_offset) {


  }

  @Override
  public void glDrawBuffersATI(int n, IntBuffer bufs) {


  }

  @Override
  public void glDrawBuffersATI(int n, int[] bufs, int bufs_offset) {


  }

  @Override
  public void glPNTrianglesiATI(int pname, int param) {


  }

  @Override
  public void glPNTrianglesfATI(int pname, float param) {


  }

  @Override
  public void glUniformBufferEXT(int program, int location, int buffer) {


  }

  @Override
  public int glGetUniformBufferSizeEXT(int program, int location) {

    return 0;
  }

  @Override
  public long glGetUniformOffsetEXT(int program, int location) {

    return 0;
  }

  @Override
  public void glLockArraysEXT(int first, int count) {


  }

  @Override
  public void glUnlockArraysEXT() {


  }

  @Override
  public void glCullParameterdvEXT(int pname, DoubleBuffer params) {


  }

  @Override
  public void glCullParameterdvEXT(int pname, double[] params, int params_offset) {


  }

  @Override
  public void glCullParameterfvEXT(int pname, FloatBuffer params) {


  }

  @Override
  public void glCullParameterfvEXT(int pname, float[] params, int params_offset) {


  }

  @Override
  public void glDepthBoundsEXT(double zmin, double zmax) {


  }

  @Override
  public void glMatrixLoadfEXT(int mode, FloatBuffer m) {


  }

  @Override
  public void glMatrixLoadfEXT(int mode, float[] m, int m_offset) {


  }

  @Override
  public void glMatrixLoaddEXT(int mode, DoubleBuffer m) {


  }

  @Override
  public void glMatrixLoaddEXT(int mode, double[] m, int m_offset) {


  }

  @Override
  public void glMatrixMultfEXT(int mode, FloatBuffer m) {


  }

  @Override
  public void glMatrixMultfEXT(int mode, float[] m, int m_offset) {


  }

  @Override
  public void glMatrixMultdEXT(int mode, DoubleBuffer m) {


  }

  @Override
  public void glMatrixMultdEXT(int mode, double[] m, int m_offset) {


  }

  @Override
  public void glMatrixLoadIdentityEXT(int mode) {


  }

  @Override
  public void glMatrixRotatefEXT(int mode, float angle, float x, float y, float z) {


  }

  @Override
  public void glMatrixRotatedEXT(int mode, double angle, double x, double y, double z) {


  }

  @Override
  public void glMatrixScalefEXT(int mode, float x, float y, float z) {


  }

  @Override
  public void glMatrixScaledEXT(int mode, double x, double y, double z) {


  }

  @Override
  public void glMatrixTranslatefEXT(int mode, float x, float y, float z) {


  }

  @Override
  public void glMatrixTranslatedEXT(int mode, double x, double y, double z) {


  }

  @Override
  public void glMatrixFrustumEXT(int mode, double left, double right, double bottom, double top,
      double zNear, double zFar) {


  }

  @Override
  public void glMatrixOrthoEXT(int mode, double left, double right, double bottom, double top,
      double zNear, double zFar) {


  }

  @Override
  public void glMatrixPopEXT(int mode) {


  }

  @Override
  public void glMatrixPushEXT(int mode) {


  }

  @Override
  public void glClientAttribDefaultEXT(int mask) {


  }

  @Override
  public void glPushClientAttribDefaultEXT(int mask) {


  }

  @Override
  public void glTextureParameterfEXT(int texture, int target, int pname, float param) {


  }

  @Override
  public void glTextureParameterfvEXT(int texture, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glTextureParameterfvEXT(int texture, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glTextureParameteriEXT(int texture, int target, int pname, int param) {


  }

  @Override
  public void glTextureParameterivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTextureParameterivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width,
      int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width,
      int border, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width,
      int height, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width,
      int height, int border, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width,
      int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width,
      int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset,
      int width, int height, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset,
      int width, int height, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glCopyTextureImage1DEXT(int texture, int target, int level, int internalformat, int x,
      int y, int width, int border) {


  }

  @Override
  public void glCopyTextureImage2DEXT(int texture, int target, int level, int internalformat, int x,
      int y, int width, int height, int border) {


  }

  @Override
  public void glCopyTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int x,
      int y, int width) {


  }

  @Override
  public void glCopyTextureSubImage2DEXT(int texture, int target, int level, int xoffset,
      int yoffset, int x, int y, int width, int height) {


  }

  @Override
  public void glGetTextureImageEXT(int texture, int target, int level, int format, int type,
      Buffer pixels) {


  }

  @Override
  public void glGetTextureParameterfvEXT(int texture, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetTextureParameterfvEXT(int texture, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetTextureParameterivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTextureParameterivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetTextureLevelParameterfvEXT(int texture, int target, int level, int pname,
      FloatBuffer params) {


  }

  @Override
  public void glGetTextureLevelParameterfvEXT(int texture, int target, int level, int pname,
      float[] params, int params_offset) {


  }

  @Override
  public void glGetTextureLevelParameterivEXT(int texture, int target, int level, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetTextureLevelParameterivEXT(int texture, int target, int level, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width,
      int height, int depth, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width,
      int height, int depth, int border, int format, int type, long pixels_buffer_offset) {


  }

  @Override
  public void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {


  }

  @Override
  public void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int type,
      long pixels_buffer_offset) {


  }

  @Override
  public void glCopyTextureSubImage3DEXT(int texture, int target, int level, int xoffset,
      int yoffset, int zoffset, int x, int y, int width, int height) {


  }

  @Override
  public void glBindMultiTextureEXT(int texunit, int target, int texture) {


  }

  @Override
  public void glMultiTexCoordPointerEXT(int texunit, int size, int type, int stride,
      Buffer pointer) {


  }

  @Override
  public void glMultiTexEnvfEXT(int texunit, int target, int pname, float param) {


  }

  @Override
  public void glMultiTexEnvfvEXT(int texunit, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glMultiTexEnvfvEXT(int texunit, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexEnviEXT(int texunit, int target, int pname, int param) {


  }

  @Override
  public void glMultiTexEnvivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glMultiTexEnvivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexGendEXT(int texunit, int coord, int pname, double param) {


  }

  @Override
  public void glMultiTexGendvEXT(int texunit, int coord, int pname, DoubleBuffer params) {


  }

  @Override
  public void glMultiTexGendvEXT(int texunit, int coord, int pname, double[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexGenfEXT(int texunit, int coord, int pname, float param) {


  }

  @Override
  public void glMultiTexGenfvEXT(int texunit, int coord, int pname, FloatBuffer params) {


  }

  @Override
  public void glMultiTexGenfvEXT(int texunit, int coord, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexGeniEXT(int texunit, int coord, int pname, int param) {


  }

  @Override
  public void glMultiTexGenivEXT(int texunit, int coord, int pname, IntBuffer params) {


  }

  @Override
  public void glMultiTexGenivEXT(int texunit, int coord, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexEnvfvEXT(int texunit, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMultiTexEnvfvEXT(int texunit, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexEnvivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMultiTexEnvivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexGendvEXT(int texunit, int coord, int pname, DoubleBuffer params) {


  }

  @Override
  public void glGetMultiTexGendvEXT(int texunit, int coord, int pname, double[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexGenfvEXT(int texunit, int coord, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMultiTexGenfvEXT(int texunit, int coord, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexGenivEXT(int texunit, int coord, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMultiTexGenivEXT(int texunit, int coord, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexParameteriEXT(int texunit, int target, int pname, int param) {


  }

  @Override
  public void glMultiTexParameterivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glMultiTexParameterivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexParameterfEXT(int texunit, int target, int pname, float param) {


  }

  @Override
  public void glMultiTexParameterfvEXT(int texunit, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glMultiTexParameterfvEXT(int texunit, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat,
      int width, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat,
      int width, int height, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width,
      int format, int type, Buffer pixels) {


  }

  @Override
  public void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset,
      int width, int height, int format, int type, Buffer pixels) {


  }

  @Override
  public void glCopyMultiTexImage1DEXT(int texunit, int target, int level, int internalformat,
      int x, int y, int width, int border) {


  }

  @Override
  public void glCopyMultiTexImage2DEXT(int texunit, int target, int level, int internalformat,
      int x, int y, int width, int height, int border) {


  }

  @Override
  public void glCopyMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int x,
      int y, int width) {


  }

  @Override
  public void glCopyMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset,
      int yoffset, int x, int y, int width, int height) {


  }

  @Override
  public void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type,
      Buffer pixels) {


  }

  @Override
  public void glGetMultiTexParameterfvEXT(int texunit, int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMultiTexParameterfvEXT(int texunit, int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexParameterivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMultiTexParameterivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexLevelParameterfvEXT(int texunit, int target, int level, int pname,
      FloatBuffer params) {


  }

  @Override
  public void glGetMultiTexLevelParameterfvEXT(int texunit, int target, int level, int pname,
      float[] params, int params_offset) {


  }

  @Override
  public void glGetMultiTexLevelParameterivEXT(int texunit, int target, int level, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetMultiTexLevelParameterivEXT(int texunit, int target, int level, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat,
      int width, int height, int depth, int border, int format, int type, Buffer pixels) {


  }

  @Override
  public void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {


  }

  @Override
  public void glCopyMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset,
      int yoffset, int zoffset, int x, int y, int width, int height) {


  }

  @Override
  public void glEnableClientStateIndexedEXT(int array, int index) {


  }

  @Override
  public void glDisableClientStateIndexedEXT(int array, int index) {


  }

  @Override
  public void glGetFloatIndexedvEXT(int target, int index, FloatBuffer data) {


  }

  @Override
  public void glGetFloatIndexedvEXT(int target, int index, float[] data, int data_offset) {


  }

  @Override
  public void glGetDoubleIndexedvEXT(int target, int index, DoubleBuffer data) {


  }

  @Override
  public void glGetDoubleIndexedvEXT(int target, int index, double[] data, int data_offset) {


  }

  @Override
  public void glEnableIndexed(int target, int index) {


  }

  @Override
  public void glDisableIndexed(int target, int index) {


  }

  @Override
  public boolean glIsEnabledIndexed(int target, int index) {

    return false;
  }

  @Override
  public void glGetIntegerIndexedv(int target, int index, IntBuffer data) {


  }

  @Override
  public void glGetIntegerIndexedv(int target, int index, int[] data, int data_offset) {


  }

  @Override
  public void glGetBooleanIndexedv(int target, int index, ByteBuffer data) {


  }

  @Override
  public void glGetBooleanIndexedv(int target, int index, byte[] data, int data_offset) {


  }

  @Override
  public void glCompressedTextureImage3DEXT(int texture, int target, int level, int internalformat,
      int width, int height, int depth, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedTextureImage2DEXT(int texture, int target, int level, int internalformat,
      int width, int height, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedTextureImage1DEXT(int texture, int target, int level, int internalformat,
      int width, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedTextureSubImage3DEXT(int texture, int target, int level, int xoffset,
      int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize,
      Buffer bits) {


  }

  @Override
  public void glCompressedTextureSubImage2DEXT(int texture, int target, int level, int xoffset,
      int yoffset, int width, int height, int format, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedTextureSubImage1DEXT(int texture, int target, int level, int xoffset,
      int width, int format, int imageSize, Buffer bits) {


  }

  @Override
  public void glGetCompressedTextureImageEXT(int texture, int target, int lod, Buffer img) {


  }

  @Override
  public void glCompressedMultiTexImage3DEXT(int texunit, int target, int level, int internalformat,
      int width, int height, int depth, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedMultiTexImage2DEXT(int texunit, int target, int level, int internalformat,
      int width, int height, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedMultiTexImage1DEXT(int texunit, int target, int level, int internalformat,
      int width, int border, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset,
      int yoffset, int zoffset, int width, int height, int depth, int format, int imageSize,
      Buffer bits) {


  }

  @Override
  public void glCompressedMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset,
      int yoffset, int width, int height, int format, int imageSize, Buffer bits) {


  }

  @Override
  public void glCompressedMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset,
      int width, int format, int imageSize, Buffer bits) {


  }

  @Override
  public void glGetCompressedMultiTexImageEXT(int texunit, int target, int lod, Buffer img) {


  }

  @Override
  public void glMatrixLoadTransposefEXT(int mode, FloatBuffer m) {


  }

  @Override
  public void glMatrixLoadTransposefEXT(int mode, float[] m, int m_offset) {


  }

  @Override
  public void glMatrixLoadTransposedEXT(int mode, DoubleBuffer m) {


  }

  @Override
  public void glMatrixLoadTransposedEXT(int mode, double[] m, int m_offset) {


  }

  @Override
  public void glMatrixMultTransposefEXT(int mode, FloatBuffer m) {


  }

  @Override
  public void glMatrixMultTransposefEXT(int mode, float[] m, int m_offset) {


  }

  @Override
  public void glMatrixMultTransposedEXT(int mode, DoubleBuffer m) {


  }

  @Override
  public void glMatrixMultTransposedEXT(int mode, double[] m, int m_offset) {


  }

  @Override
  public void glNamedBufferDataEXT(int buffer, long size, Buffer data, int usage) {


  }

  @Override
  public void glNamedBufferSubDataEXT(int buffer, long offset, long size, Buffer data) {


  }

  @Override
  public ByteBuffer glMapNamedBufferEXT(int buffer, int access) {

    return null;
  }

  @Override
  public boolean glUnmapNamedBufferEXT(int buffer) {

    return false;
  }

  @Override
  public void glGetNamedBufferParameterivEXT(int buffer, int pname, IntBuffer params) {


  }

  @Override
  public void glGetNamedBufferParameterivEXT(int buffer, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetNamedBufferSubDataEXT(int buffer, long offset, long size, Buffer data) {


  }

  @Override
  public void glTextureBufferEXT(int texture, int target, int internalformat, int buffer) {


  }

  @Override
  public void glMultiTexBufferEXT(int texunit, int target, int internalformat, int buffer) {


  }

  @Override
  public void glTextureParameterIivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTextureParameterIivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glTextureParameterIuivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glTextureParameterIuivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetTextureParameterIivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTextureParameterIivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetTextureParameterIuivEXT(int texture, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetTextureParameterIuivEXT(int texture, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexParameterIivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glMultiTexParameterIivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glMultiTexParameterIuivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glMultiTexParameterIuivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexParameterIivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMultiTexParameterIivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMultiTexParameterIuivEXT(int texunit, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMultiTexParameterIuivEXT(int texunit, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParameters4fvEXT(int program, int target, int index, int count,
      FloatBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParameters4fvEXT(int program, int target, int index, int count,
      float[] params, int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParameterI4iEXT(int program, int target, int index, int x, int y,
      int z, int w) {


  }

  @Override
  public void glNamedProgramLocalParameterI4ivEXT(int program, int target, int index,
      IntBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParameterI4ivEXT(int program, int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParametersI4ivEXT(int program, int target, int index, int count,
      IntBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParametersI4ivEXT(int program, int target, int index, int count,
      int[] params, int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParameterI4uiEXT(int program, int target, int index, int x, int y,
      int z, int w) {


  }

  @Override
  public void glNamedProgramLocalParameterI4uivEXT(int program, int target, int index,
      IntBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParameterI4uivEXT(int program, int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParametersI4uivEXT(int program, int target, int index, int count,
      IntBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParametersI4uivEXT(int program, int target, int index, int count,
      int[] params, int params_offset) {


  }

  @Override
  public void glGetNamedProgramLocalParameterIivEXT(int program, int target, int index,
      IntBuffer params) {


  }

  @Override
  public void glGetNamedProgramLocalParameterIivEXT(int program, int target, int index,
      int[] params, int params_offset) {


  }

  @Override
  public void glGetNamedProgramLocalParameterIuivEXT(int program, int target, int index,
      IntBuffer params) {


  }

  @Override
  public void glGetNamedProgramLocalParameterIuivEXT(int program, int target, int index,
      int[] params, int params_offset) {


  }

  @Override
  public void glEnableClientStateiEXT(int array, int index) {


  }

  @Override
  public void glDisableClientStateiEXT(int array, int index) {


  }

  @Override
  public void glGetFloati_vEXT(int pname, int index, FloatBuffer params) {


  }

  @Override
  public void glGetFloati_vEXT(int pname, int index, float[] params, int params_offset) {


  }

  @Override
  public void glGetDoublei_vEXT(int pname, int index, DoubleBuffer params) {


  }

  @Override
  public void glGetDoublei_vEXT(int pname, int index, double[] params, int params_offset) {


  }

  @Override
  public void glGetPointeri_vEXT(int pname, int index, PointerBuffer params) {


  }

  @Override
  public void glNamedProgramStringEXT(int program, int target, int format, int len, Buffer string) {


  }

  @Override
  public void glNamedProgramLocalParameter4dEXT(int program, int target, int index, double x,
      double y, double z, double w) {


  }

  @Override
  public void glNamedProgramLocalParameter4dvEXT(int program, int target, int index,
      DoubleBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParameter4dvEXT(int program, int target, int index,
      double[] params, int params_offset) {


  }

  @Override
  public void glNamedProgramLocalParameter4fEXT(int program, int target, int index, float x,
      float y, float z, float w) {


  }

  @Override
  public void glNamedProgramLocalParameter4fvEXT(int program, int target, int index,
      FloatBuffer params) {


  }

  @Override
  public void glNamedProgramLocalParameter4fvEXT(int program, int target, int index, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetNamedProgramLocalParameterdvEXT(int program, int target, int index,
      DoubleBuffer params) {


  }

  @Override
  public void glGetNamedProgramLocalParameterdvEXT(int program, int target, int index,
      double[] params, int params_offset) {


  }

  @Override
  public void glGetNamedProgramLocalParameterfvEXT(int program, int target, int index,
      FloatBuffer params) {


  }

  @Override
  public void glGetNamedProgramLocalParameterfvEXT(int program, int target, int index,
      float[] params, int params_offset) {


  }

  @Override
  public void glGetNamedProgramivEXT(int program, int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetNamedProgramivEXT(int program, int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetNamedProgramStringEXT(int program, int target, int pname, Buffer string) {


  }

  @Override
  public void glNamedRenderbufferStorageEXT(int renderbuffer, int internalformat, int width,
      int height) {


  }

  @Override
  public void glGetNamedRenderbufferParameterivEXT(int renderbuffer, int pname, IntBuffer params) {


  }

  @Override
  public void glGetNamedRenderbufferParameterivEXT(int renderbuffer, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glNamedRenderbufferStorageMultisampleEXT(int renderbuffer, int samples,
      int internalformat, int width, int height) {


  }

  @Override
  public void glNamedRenderbufferStorageMultisampleCoverageEXT(int renderbuffer,
      int coverageSamples, int colorSamples, int internalformat, int width, int height) {


  }

  @Override
  public int glCheckNamedFramebufferStatusEXT(int framebuffer, int target) {

    return 0;
  }

  @Override
  public void glNamedFramebufferTexture1DEXT(int framebuffer, int attachment, int textarget,
      int texture, int level) {


  }

  @Override
  public void glNamedFramebufferTexture2DEXT(int framebuffer, int attachment, int textarget,
      int texture, int level) {


  }

  @Override
  public void glNamedFramebufferTexture3DEXT(int framebuffer, int attachment, int textarget,
      int texture, int level, int zoffset) {


  }

  @Override
  public void glNamedFramebufferRenderbufferEXT(int framebuffer, int attachment,
      int renderbuffertarget, int renderbuffer) {


  }

  @Override
  public void glGetNamedFramebufferAttachmentParameterivEXT(int framebuffer, int attachment,
      int pname, IntBuffer params) {


  }

  @Override
  public void glGetNamedFramebufferAttachmentParameterivEXT(int framebuffer, int attachment,
      int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGenerateTextureMipmapEXT(int texture, int target) {


  }

  @Override
  public void glGenerateMultiTexMipmapEXT(int texunit, int target) {


  }

  @Override
  public void glFramebufferDrawBufferEXT(int framebuffer, int mode) {


  }

  @Override
  public void glFramebufferDrawBuffersEXT(int framebuffer, int n, IntBuffer bufs) {


  }

  @Override
  public void glFramebufferDrawBuffersEXT(int framebuffer, int n, int[] bufs, int bufs_offset) {


  }

  @Override
  public void glFramebufferReadBufferEXT(int framebuffer, int mode) {


  }

  @Override
  public void glGetFramebufferParameterivEXT(int framebuffer, int pname, IntBuffer params) {


  }

  @Override
  public void glGetFramebufferParameterivEXT(int framebuffer, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glNamedCopyBufferSubDataEXT(int readBuffer, int writeBuffer, long readOffset,
      long writeOffset, long size) {


  }

  @Override
  public void glNamedFramebufferTextureEXT(int framebuffer, int attachment, int texture,
      int level) {


  }

  @Override
  public void glNamedFramebufferTextureLayerEXT(int framebuffer, int attachment, int texture,
      int level, int layer) {


  }

  @Override
  public void glNamedFramebufferTextureFaceEXT(int framebuffer, int attachment, int texture,
      int level, int face) {


  }

  @Override
  public void glTextureRenderbufferEXT(int texture, int target, int renderbuffer) {


  }

  @Override
  public void glMultiTexRenderbufferEXT(int texunit, int target, int renderbuffer) {


  }

  @Override
  public void glVertexArrayVertexOffsetEXT(int vaobj, int buffer, int size, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArrayColorOffsetEXT(int vaobj, int buffer, int size, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArrayEdgeFlagOffsetEXT(int vaobj, int buffer, int stride, long offset) {


  }

  @Override
  public void glVertexArrayIndexOffsetEXT(int vaobj, int buffer, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArrayNormalOffsetEXT(int vaobj, int buffer, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArrayTexCoordOffsetEXT(int vaobj, int buffer, int size, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArrayMultiTexCoordOffsetEXT(int vaobj, int buffer, int texunit, int size,
      int type, int stride, long offset) {


  }

  @Override
  public void glVertexArrayFogCoordOffsetEXT(int vaobj, int buffer, int type, int stride,
      long offset) {


  }

  @Override
  public void glVertexArraySecondaryColorOffsetEXT(int vaobj, int buffer, int size, int type,
      int stride, long offset) {


  }

  @Override
  public void glVertexArrayVertexAttribOffsetEXT(int vaobj, int buffer, int index, int size,
      int type, boolean normalized, int stride, long offset) {


  }

  @Override
  public void glVertexArrayVertexAttribIOffsetEXT(int vaobj, int buffer, int index, int size,
      int type, int stride, long offset) {


  }

  @Override
  public void glEnableVertexArrayEXT(int vaobj, int array) {


  }

  @Override
  public void glDisableVertexArrayEXT(int vaobj, int array) {


  }

  @Override
  public void glEnableVertexArrayAttribEXT(int vaobj, int index) {


  }

  @Override
  public void glDisableVertexArrayAttribEXT(int vaobj, int index) {


  }

  @Override
  public void glGetVertexArrayIntegervEXT(int vaobj, int pname, IntBuffer param) {


  }

  @Override
  public void glGetVertexArrayIntegervEXT(int vaobj, int pname, int[] param, int param_offset) {


  }

  @Override
  public void glGetVertexArrayPointervEXT(int vaobj, int pname, PointerBuffer param) {


  }

  @Override
  public void glGetVertexArrayIntegeri_vEXT(int vaobj, int index, int pname, IntBuffer param) {


  }

  @Override
  public void glGetVertexArrayIntegeri_vEXT(int vaobj, int index, int pname, int[] param,
      int param_offset) {


  }

  @Override
  public void glGetVertexArrayPointeri_vEXT(int vaobj, int index, int pname, PointerBuffer param) {


  }

  @Override
  public ByteBuffer glMapNamedBufferRangeEXT(int buffer, long offset, long length, int access) {

    return null;
  }

  @Override
  public void glFlushMappedNamedBufferRangeEXT(int buffer, long offset, long length) {


  }

  @Override
  public void glNamedBufferStorageEXT(int buffer, long size, Buffer data, int flags) {


  }

  @Override
  public void glProgramUniform1dEXT(int program, int location, double x) {


  }

  @Override
  public void glProgramUniform2dEXT(int program, int location, double x, double y) {


  }

  @Override
  public void glProgramUniform3dEXT(int program, int location, double x, double y, double z) {


  }

  @Override
  public void glProgramUniform4dEXT(int program, int location, double x, double y, double z,
      double w) {


  }

  @Override
  public void glProgramUniform1dvEXT(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform1dvEXT(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform2dvEXT(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform2dvEXT(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform3dvEXT(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform3dvEXT(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniform4dvEXT(int program, int location, int count, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniform4dvEXT(int program, int location, int count, double[] value,
      int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2dvEXT(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2dvEXT(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3dvEXT(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3dvEXT(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4dvEXT(int program, int location, int count, boolean transpose,
      DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4dvEXT(int program, int location, int count, boolean transpose,
      double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x3dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x3dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix2x4dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix2x4dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x2dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x2dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix3x4dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix3x4dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x2dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x2dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glProgramUniformMatrix4x3dvEXT(int program, int location, int count,
      boolean transpose, DoubleBuffer value) {


  }

  @Override
  public void glProgramUniformMatrix4x3dvEXT(int program, int location, int count,
      boolean transpose, double[] value, int value_offset) {


  }

  @Override
  public void glTextureBufferRangeEXT(int texture, int target, int internalformat, int buffer,
      long offset, long size) {


  }

  @Override
  public void glTextureStorage2DMultisampleEXT(int texture, int target, int samples,
      int internalformat, int width, int height, boolean fixedsamplelocations) {


  }

  @Override
  public void glTextureStorage3DMultisampleEXT(int texture, int target, int samples,
      int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {


  }

  @Override
  public void glVertexArrayBindVertexBufferEXT(int vaobj, int bindingindex, int buffer, long offset,
      int stride) {


  }

  @Override
  public void glVertexArrayVertexAttribFormatEXT(int vaobj, int attribindex, int size, int type,
      boolean normalized, int relativeoffset) {


  }

  @Override
  public void glVertexArrayVertexAttribIFormatEXT(int vaobj, int attribindex, int size, int type,
      int relativeoffset) {


  }

  @Override
  public void glVertexArrayVertexAttribLFormatEXT(int vaobj, int attribindex, int size, int type,
      int relativeoffset) {


  }

  @Override
  public void glVertexArrayVertexAttribBindingEXT(int vaobj, int attribindex, int bindingindex) {


  }

  @Override
  public void glVertexArrayVertexBindingDivisorEXT(int vaobj, int bindingindex, int divisor) {


  }

  @Override
  public void glVertexArrayVertexAttribLOffsetEXT(int vaobj, int buffer, int index, int size,
      int type, int stride, long offset) {


  }

  @Override
  public void glTexturePageCommitmentEXT(int texture, int level, int xoffset, int yoffset,
      int zoffset, int width, int height, int depth, boolean commit) {


  }

  @Override
  public void glVertexArrayVertexAttribDivisorEXT(int vaobj, int index, int divisor) {


  }

  @Override
  public void glColorMaskIndexed(int index, boolean r, boolean g, boolean b, boolean a) {


  }

  @Override
  public void glProgramEnvParameters4fvEXT(int target, int index, int count, FloatBuffer params) {


  }

  @Override
  public void glProgramEnvParameters4fvEXT(int target, int index, int count, float[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParameters4fvEXT(int target, int index, int count, FloatBuffer params) {


  }

  @Override
  public void glProgramLocalParameters4fvEXT(int target, int index, int count, float[] params,
      int params_offset) {


  }

  @Override
  public void glIndexFuncEXT(int func, float ref) {


  }

  @Override
  public void glIndexMaterialEXT(int face, int mode) {


  }

  @Override
  public void glApplyTextureEXT(int mode) {


  }

  @Override
  public void glTextureLightEXT(int pname) {


  }

  @Override
  public void glTextureMaterialEXT(int face, int mode) {


  }

  @Override
  public void glPixelTransformParameteriEXT(int target, int pname, int param) {


  }

  @Override
  public void glPixelTransformParameterfEXT(int target, int pname, float param) {


  }

  @Override
  public void glPixelTransformParameterivEXT(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glPixelTransformParameterivEXT(int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glPixelTransformParameterfvEXT(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glPixelTransformParameterfvEXT(int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glGetPixelTransformParameterivEXT(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetPixelTransformParameterivEXT(int target, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetPixelTransformParameterfvEXT(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetPixelTransformParameterfvEXT(int target, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glPolygonOffsetClampEXT(float factor, float units, float clamp) {


  }

  @Override
  public void glProvokingVertexEXT(int mode) {


  }

  @Override
  public void glRasterSamplesEXT(int samples, boolean fixedsamplelocations) {


  }

  @Override
  public void glStencilClearTagEXT(int stencilTagBits, int stencilClearTag) {


  }

  @Override
  public void glActiveStencilFaceEXT(int face) {


  }

  @Override
  public void glClearColorIi(int red, int green, int blue, int alpha) {


  }

  @Override
  public void glClearColorIui(int red, int green, int blue, int alpha) {


  }

  @Override
  public void glTextureNormalEXT(int mode) {


  }

  @Override
  public void glGetQueryObjecti64vEXT(int id, int pname, LongBuffer params) {


  }

  @Override
  public void glGetQueryObjecti64vEXT(int id, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glGetQueryObjectui64vEXT(int id, int pname, LongBuffer params) {


  }

  @Override
  public void glGetQueryObjectui64vEXT(int id, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glBeginVertexShaderEXT() {


  }

  @Override
  public void glEndVertexShaderEXT() {


  }

  @Override
  public void glBindVertexShaderEXT(int id) {


  }

  @Override
  public int glGenVertexShadersEXT(int range) {

    return 0;
  }

  @Override
  public void glDeleteVertexShaderEXT(int id) {


  }

  @Override
  public void glShaderOp1EXT(int op, int res, int arg1) {


  }

  @Override
  public void glShaderOp2EXT(int op, int res, int arg1, int arg2) {


  }

  @Override
  public void glShaderOp3EXT(int op, int res, int arg1, int arg2, int arg3) {


  }

  @Override
  public void glSwizzleEXT(int res, int in, int outX, int outY, int outZ, int outW) {


  }

  @Override
  public void glWriteMaskEXT(int res, int in, int outX, int outY, int outZ, int outW) {


  }

  @Override
  public void glInsertComponentEXT(int res, int src, int num) {


  }

  @Override
  public void glExtractComponentEXT(int res, int src, int num) {


  }

  @Override
  public int glGenSymbolsEXT(int datatype, int storagetype, int range, int components) {

    return 0;
  }

  @Override
  public void glSetInvariantEXT(int id, int type, Buffer addr) {


  }

  @Override
  public void glSetLocalConstantEXT(int id, int type, Buffer addr) {


  }

  @Override
  public void glVariantbvEXT(int id, ByteBuffer addr) {


  }

  @Override
  public void glVariantbvEXT(int id, byte[] addr, int addr_offset) {


  }

  @Override
  public void glVariantsvEXT(int id, ShortBuffer addr) {


  }

  @Override
  public void glVariantsvEXT(int id, short[] addr, int addr_offset) {


  }

  @Override
  public void glVariantivEXT(int id, IntBuffer addr) {


  }

  @Override
  public void glVariantivEXT(int id, int[] addr, int addr_offset) {


  }

  @Override
  public void glVariantfvEXT(int id, FloatBuffer addr) {


  }

  @Override
  public void glVariantfvEXT(int id, float[] addr, int addr_offset) {


  }

  @Override
  public void glVariantdvEXT(int id, DoubleBuffer addr) {


  }

  @Override
  public void glVariantdvEXT(int id, double[] addr, int addr_offset) {


  }

  @Override
  public void glVariantubvEXT(int id, ByteBuffer addr) {


  }

  @Override
  public void glVariantubvEXT(int id, byte[] addr, int addr_offset) {


  }

  @Override
  public void glVariantusvEXT(int id, ShortBuffer addr) {


  }

  @Override
  public void glVariantusvEXT(int id, short[] addr, int addr_offset) {


  }

  @Override
  public void glVariantuivEXT(int id, IntBuffer addr) {


  }

  @Override
  public void glVariantuivEXT(int id, int[] addr, int addr_offset) {


  }

  @Override
  public void glVariantPointerEXT(int id, int type, int stride, Buffer addr) {


  }

  @Override
  public void glVariantPointerEXT(int id, int type, int stride, long addr_buffer_offset) {


  }

  @Override
  public void glEnableVariantClientStateEXT(int id) {


  }

  @Override
  public void glDisableVariantClientStateEXT(int id) {


  }

  @Override
  public int glBindLightParameterEXT(int light, int value) {

    return 0;
  }

  @Override
  public int glBindMaterialParameterEXT(int face, int value) {

    return 0;
  }

  @Override
  public int glBindTexGenParameterEXT(int unit, int coord, int value) {

    return 0;
  }

  @Override
  public int glBindTextureUnitParameterEXT(int unit, int value) {

    return 0;
  }

  @Override
  public int glBindParameterEXT(int value) {

    return 0;
  }

  @Override
  public boolean glIsVariantEnabledEXT(int id, int cap) {

    return false;
  }

  @Override
  public void glGetVariantBooleanvEXT(int id, int value, ByteBuffer data) {


  }

  @Override
  public void glGetVariantBooleanvEXT(int id, int value, byte[] data, int data_offset) {


  }

  @Override
  public void glGetVariantIntegervEXT(int id, int value, IntBuffer data) {


  }

  @Override
  public void glGetVariantIntegervEXT(int id, int value, int[] data, int data_offset) {


  }

  @Override
  public void glGetVariantFloatvEXT(int id, int value, FloatBuffer data) {


  }

  @Override
  public void glGetVariantFloatvEXT(int id, int value, float[] data, int data_offset) {


  }

  @Override
  public void glGetInvariantBooleanvEXT(int id, int value, ByteBuffer data) {


  }

  @Override
  public void glGetInvariantBooleanvEXT(int id, int value, byte[] data, int data_offset) {


  }

  @Override
  public void glGetInvariantIntegervEXT(int id, int value, IntBuffer data) {


  }

  @Override
  public void glGetInvariantIntegervEXT(int id, int value, int[] data, int data_offset) {


  }

  @Override
  public void glGetInvariantFloatvEXT(int id, int value, FloatBuffer data) {


  }

  @Override
  public void glGetInvariantFloatvEXT(int id, int value, float[] data, int data_offset) {


  }

  @Override
  public void glGetLocalConstantBooleanvEXT(int id, int value, ByteBuffer data) {


  }

  @Override
  public void glGetLocalConstantBooleanvEXT(int id, int value, byte[] data, int data_offset) {


  }

  @Override
  public void glGetLocalConstantIntegervEXT(int id, int value, IntBuffer data) {


  }

  @Override
  public void glGetLocalConstantIntegervEXT(int id, int value, int[] data, int data_offset) {


  }

  @Override
  public void glGetLocalConstantFloatvEXT(int id, int value, FloatBuffer data) {


  }

  @Override
  public void glGetLocalConstantFloatvEXT(int id, int value, float[] data, int data_offset) {


  }

  @Override
  public void glVertexWeightfEXT(float weight) {


  }

  @Override
  public void glVertexWeightfvEXT(FloatBuffer weight) {


  }

  @Override
  public void glVertexWeightfvEXT(float[] weight, int weight_offset) {


  }

  @Override
  public void glVertexWeightPointerEXT(int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glVertexWeightPointerEXT(int size, int type, int stride, long pointer_buffer_offset) {


  }

  @Override
  public void glFrameTerminatorGREMEDY() {


  }

  @Override
  public void glStringMarkerGREMEDY(int len, Buffer string) {


  }

  @Override
  public void glSyncTextureINTEL(int texture) {


  }

  @Override
  public void glUnmapTexture2DINTEL(int texture, int level) {


  }

  @Override
  public ByteBuffer glMapTexture2DINTEL(int texture, int level, int access, IntBuffer stride,
      IntBuffer layout) {

    return null;
  }

  @Override
  public ByteBuffer glMapTexture2DINTEL(int texture, int level, int access, int[] stride,
      int stride_offset, int[] layout, int layout_offset) {

    return null;
  }

  @Override
  public void glBeginPerfQueryINTEL(int queryHandle) {


  }

  @Override
  public void glCreatePerfQueryINTEL(int queryId, IntBuffer queryHandle) {


  }

  @Override
  public void glCreatePerfQueryINTEL(int queryId, int[] queryHandle, int queryHandle_offset) {


  }

  @Override
  public void glDeletePerfQueryINTEL(int queryHandle) {


  }

  @Override
  public void glEndPerfQueryINTEL(int queryHandle) {


  }

  @Override
  public void glGetFirstPerfQueryIdINTEL(IntBuffer queryId) {


  }

  @Override
  public void glGetFirstPerfQueryIdINTEL(int[] queryId, int queryId_offset) {


  }

  @Override
  public void glGetNextPerfQueryIdINTEL(int queryId, IntBuffer nextQueryId) {


  }

  @Override
  public void glGetNextPerfQueryIdINTEL(int queryId, int[] nextQueryId, int nextQueryId_offset) {


  }

  @Override
  public void glGetPerfCounterInfoINTEL(int queryId, int counterId, int counterNameLength,
      ByteBuffer counterName, int counterDescLength, ByteBuffer counterDesc,
      IntBuffer counterOffset, IntBuffer counterDataSize, IntBuffer counterTypeEnum,
      IntBuffer counterDataTypeEnum, LongBuffer rawCounterMaxValue) {


  }

  @Override
  public void glGetPerfCounterInfoINTEL(int queryId, int counterId, int counterNameLength,
      byte[] counterName, int counterName_offset, int counterDescLength, byte[] counterDesc,
      int counterDesc_offset, int[] counterOffset, int counterOffset_offset, int[] counterDataSize,
      int counterDataSize_offset, int[] counterTypeEnum, int counterTypeEnum_offset,
      int[] counterDataTypeEnum, int counterDataTypeEnum_offset, long[] rawCounterMaxValue,
      int rawCounterMaxValue_offset) {


  }

  @Override
  public void glGetPerfQueryDataINTEL(int queryHandle, int flags, int dataSize, Buffer data,
      IntBuffer bytesWritten) {


  }

  @Override
  public void glGetPerfQueryDataINTEL(int queryHandle, int flags, int dataSize, Buffer data,
      int[] bytesWritten, int bytesWritten_offset) {


  }

  @Override
  public void glGetPerfQueryIdByNameINTEL(ByteBuffer queryName, IntBuffer queryId) {


  }

  @Override
  public void glGetPerfQueryIdByNameINTEL(byte[] queryName, int queryName_offset, int[] queryId,
      int queryId_offset) {


  }

  @Override
  public void glGetPerfQueryInfoINTEL(int queryId, int queryNameLength, ByteBuffer queryName,
      IntBuffer dataSize, IntBuffer noCounters, IntBuffer noInstances, IntBuffer capsMask) {


  }

  @Override
  public void glGetPerfQueryInfoINTEL(int queryId, int queryNameLength, byte[] queryName,
      int queryName_offset, int[] dataSize, int dataSize_offset, int[] noCounters,
      int noCounters_offset, int[] noInstances, int noInstances_offset, int[] capsMask,
      int capsMask_offset) {


  }

  @Override
  public void glBeginConditionalRenderNVX(int id) {


  }

  @Override
  public void glEndConditionalRenderNVX() {


  }

  @Override
  public void glMultiDrawArraysIndirectBindlessNV(int mode, Buffer indirect, int drawCount,
      int stride, int vertexBufferCount) {


  }

  @Override
  public void glMultiDrawElementsIndirectBindlessNV(int mode, int type, Buffer indirect,
      int drawCount, int stride, int vertexBufferCount) {


  }

  @Override
  public void glMultiDrawArraysIndirectBindlessCountNV(int mode, Buffer indirect, int drawCount,
      int maxDrawCount, int stride, int vertexBufferCount) {


  }

  @Override
  public void glMultiDrawElementsIndirectBindlessCountNV(int mode, int type, Buffer indirect,
      int drawCount, int maxDrawCount, int stride, int vertexBufferCount) {


  }

  @Override
  public void glCreateStatesNV(int n, IntBuffer states) {


  }

  @Override
  public void glCreateStatesNV(int n, int[] states, int states_offset) {


  }

  @Override
  public void glDeleteStatesNV(int n, IntBuffer states) {


  }

  @Override
  public void glDeleteStatesNV(int n, int[] states, int states_offset) {


  }

  @Override
  public boolean glIsStateNV(int state) {

    return false;
  }

  @Override
  public void glStateCaptureNV(int state, int mode) {


  }

  @Override
  public int glGetCommandHeaderNV(int tokenID, int size) {

    return 0;
  }

  @Override
  public short glGetStageIndexNV(int shadertype) {

    return 0;
  }

  @Override
  public void glDrawCommandsNV(int primitiveMode, int buffer, PointerBuffer indirects,
      IntBuffer sizes, int count) {


  }

  @Override
  public void glDrawCommandsNV(int primitiveMode, int buffer, PointerBuffer indirects, int[] sizes,
      int sizes_offset, int count) {


  }

  @Override
  public void glDrawCommandsAddressNV(int primitiveMode, LongBuffer indirects, IntBuffer sizes,
      int count) {


  }

  @Override
  public void glDrawCommandsAddressNV(int primitiveMode, long[] indirects, int indirects_offset,
      int[] sizes, int sizes_offset, int count) {


  }

  @Override
  public void glDrawCommandsStatesNV(int buffer, PointerBuffer indirects, IntBuffer sizes,
      IntBuffer states, IntBuffer fbos, int count) {


  }

  @Override
  public void glDrawCommandsStatesNV(int buffer, PointerBuffer indirects, int[] sizes,
      int sizes_offset, int[] states, int states_offset, int[] fbos, int fbos_offset, int count) {


  }

  @Override
  public void glDrawCommandsStatesAddressNV(LongBuffer indirects, IntBuffer sizes, IntBuffer states,
      IntBuffer fbos, int count) {


  }

  @Override
  public void glDrawCommandsStatesAddressNV(long[] indirects, int indirects_offset, int[] sizes,
      int sizes_offset, int[] states, int states_offset, int[] fbos, int fbos_offset, int count) {


  }

  @Override
  public void glCreateCommandListsNV(int n, IntBuffer lists) {


  }

  @Override
  public void glCreateCommandListsNV(int n, int[] lists, int lists_offset) {


  }

  @Override
  public void glDeleteCommandListsNV(int n, IntBuffer lists) {


  }

  @Override
  public void glDeleteCommandListsNV(int n, int[] lists, int lists_offset) {


  }

  @Override
  public boolean glIsCommandListNV(int list) {

    return false;
  }

  @Override
  public void glListDrawCommandsStatesClientNV(int list, int segment, PointerBuffer indirects,
      IntBuffer sizes, IntBuffer states, IntBuffer fbos, int count) {


  }

  @Override
  public void glListDrawCommandsStatesClientNV(int list, int segment, PointerBuffer indirects,
      int[] sizes, int sizes_offset, int[] states, int states_offset, int[] fbos, int fbos_offset,
      int count) {


  }

  @Override
  public void glCommandListSegmentsNV(int list, int segments) {


  }

  @Override
  public void glCompileCommandListNV(int list) {


  }

  @Override
  public void glCallCommandListNV(int list) {


  }

  @Override
  public void glSubpixelPrecisionBiasNV(int xbits, int ybits) {


  }

  @Override
  public void glConservativeRasterParameterfNV(int pname, float value) {


  }

  @Override
  public void glCopyImageSubDataNV(int srcName, int srcTarget, int srcLevel, int srcX, int srcY,
      int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int width,
      int height, int depth) {


  }

  @Override
  public void glDrawTextureNV(int texture, int sampler, float x0, float y0, float x1, float y1,
      float z, float s0, float t0, float s1, float t1) {


  }

  @Override
  public void glMapControlPointsNV(int target, int index, int type, int ustride, int vstride,
      int uorder, int vorder, boolean packed, Buffer points) {


  }

  @Override
  public void glMapParameterivNV(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glMapParameterivNV(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glMapParameterfvNV(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glMapParameterfvNV(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetMapControlPointsNV(int target, int index, int type, int ustride, int vstride,
      boolean packed, Buffer points) {


  }

  @Override
  public void glGetMapParameterivNV(int target, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMapParameterivNV(int target, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetMapParameterfvNV(int target, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMapParameterfvNV(int target, int pname, float[] params, int params_offset) {


  }

  @Override
  public void glGetMapAttribParameterivNV(int target, int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetMapAttribParameterivNV(int target, int index, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetMapAttribParameterfvNV(int target, int index, int pname, FloatBuffer params) {


  }

  @Override
  public void glGetMapAttribParameterfvNV(int target, int index, int pname, float[] params,
      int params_offset) {


  }

  @Override
  public void glEvalMapsNV(int target, int mode) {


  }

  @Override
  public void glGetMultisamplefvNV(int pname, int index, FloatBuffer val) {


  }

  @Override
  public void glGetMultisamplefvNV(int pname, int index, float[] val, int val_offset) {


  }

  @Override
  public void glSampleMaskIndexedNV(int index, int mask) {


  }

  @Override
  public void glTexRenderbufferNV(int target, int renderbuffer) {


  }

  @Override
  public void glFragmentCoverageColorNV(int color) {


  }

  @Override
  public void glCoverageModulationTableNV(int n, FloatBuffer v) {


  }

  @Override
  public void glCoverageModulationTableNV(int n, float[] v, int v_offset) {


  }

  @Override
  public void glGetCoverageModulationTableNV(int bufsize, FloatBuffer v) {


  }

  @Override
  public void glGetCoverageModulationTableNV(int bufsize, float[] v, int v_offset) {


  }

  @Override
  public void glCoverageModulationNV(int components) {


  }

  @Override
  public void glRenderbufferStorageMultisampleCoverageNV(int target, int coverageSamples,
      int colorSamples, int internalformat, int width, int height) {


  }

  @Override
  public void glProgramVertexLimitNV(int target, int limit) {


  }

  @Override
  public void glFramebufferTextureFaceEXT(int target, int attachment, int texture, int level,
      int face) {


  }

  @Override
  public void glProgramLocalParameterI4iNV(int target, int index, int x, int y, int z, int w) {


  }

  @Override
  public void glProgramLocalParameterI4ivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glProgramLocalParameterI4ivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParametersI4ivNV(int target, int index, int count, IntBuffer params) {


  }

  @Override
  public void glProgramLocalParametersI4ivNV(int target, int index, int count, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParameterI4uiNV(int target, int index, int x, int y, int z, int w) {


  }

  @Override
  public void glProgramLocalParameterI4uivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glProgramLocalParameterI4uivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramLocalParametersI4uivNV(int target, int index, int count, IntBuffer params) {


  }

  @Override
  public void glProgramLocalParametersI4uivNV(int target, int index, int count, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramEnvParameterI4iNV(int target, int index, int x, int y, int z, int w) {


  }

  @Override
  public void glProgramEnvParameterI4ivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glProgramEnvParameterI4ivNV(int target, int index, int[] params, int params_offset) {


  }

  @Override
  public void glProgramEnvParametersI4ivNV(int target, int index, int count, IntBuffer params) {


  }

  @Override
  public void glProgramEnvParametersI4ivNV(int target, int index, int count, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramEnvParameterI4uiNV(int target, int index, int x, int y, int z, int w) {


  }

  @Override
  public void glProgramEnvParameterI4uivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glProgramEnvParameterI4uivNV(int target, int index, int[] params, int params_offset) {


  }

  @Override
  public void glProgramEnvParametersI4uivNV(int target, int index, int count, IntBuffer params) {


  }

  @Override
  public void glProgramEnvParametersI4uivNV(int target, int index, int count, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramLocalParameterIivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glGetProgramLocalParameterIivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramLocalParameterIuivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glGetProgramLocalParameterIuivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramEnvParameterIivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glGetProgramEnvParameterIivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramEnvParameterIuivNV(int target, int index, IntBuffer params) {


  }

  @Override
  public void glGetProgramEnvParameterIuivNV(int target, int index, int[] params,
      int params_offset) {


  }

  @Override
  public void glProgramSubroutineParametersuivNV(int target, int count, IntBuffer params) {


  }

  @Override
  public void glProgramSubroutineParametersuivNV(int target, int count, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetProgramSubroutineParameteruivNV(int target, int index, IntBuffer param) {


  }

  @Override
  public void glGetProgramSubroutineParameteruivNV(int target, int index, int[] param,
      int param_offset) {


  }

  @Override
  public void glVertex2h(short x, short y) {


  }

  @Override
  public void glVertex2hv(ShortBuffer v) {


  }

  @Override
  public void glVertex2hv(short[] v, int v_offset) {


  }

  @Override
  public void glVertex3h(short x, short y, short z) {


  }

  @Override
  public void glVertex3hv(ShortBuffer v) {


  }

  @Override
  public void glVertex3hv(short[] v, int v_offset) {


  }

  @Override
  public void glVertex4h(short x, short y, short z, short w) {


  }

  @Override
  public void glVertex4hv(ShortBuffer v) {


  }

  @Override
  public void glVertex4hv(short[] v, int v_offset) {


  }

  @Override
  public void glNormal3h(short nx, short ny, short nz) {


  }

  @Override
  public void glNormal3hv(ShortBuffer v) {


  }

  @Override
  public void glNormal3hv(short[] v, int v_offset) {


  }

  @Override
  public void glColor3h(short red, short green, short blue) {


  }

  @Override
  public void glColor3hv(ShortBuffer v) {


  }

  @Override
  public void glColor3hv(short[] v, int v_offset) {


  }

  @Override
  public void glColor4h(short red, short green, short blue, short alpha) {


  }

  @Override
  public void glColor4hv(ShortBuffer v) {


  }

  @Override
  public void glColor4hv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord1h(short s) {


  }

  @Override
  public void glTexCoord1hv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord1hv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord2h(short s, short t) {


  }

  @Override
  public void glTexCoord2hv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord2hv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord3h(short s, short t, short r) {


  }

  @Override
  public void glTexCoord3hv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord3hv(short[] v, int v_offset) {


  }

  @Override
  public void glTexCoord4h(short s, short t, short r, short q) {


  }

  @Override
  public void glTexCoord4hv(ShortBuffer v) {


  }

  @Override
  public void glTexCoord4hv(short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord1h(int target, short s) {


  }

  @Override
  public void glMultiTexCoord1hv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord1hv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord2h(int target, short s, short t) {


  }

  @Override
  public void glMultiTexCoord2hv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord2hv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord3h(int target, short s, short t, short r) {


  }

  @Override
  public void glMultiTexCoord3hv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord3hv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glMultiTexCoord4h(int target, short s, short t, short r, short q) {


  }

  @Override
  public void glMultiTexCoord4hv(int target, ShortBuffer v) {


  }

  @Override
  public void glMultiTexCoord4hv(int target, short[] v, int v_offset) {


  }

  @Override
  public void glFogCoordh(short fog) {


  }

  @Override
  public void glFogCoordhv(ShortBuffer fog) {


  }

  @Override
  public void glFogCoordhv(short[] fog, int fog_offset) {


  }

  @Override
  public void glSecondaryColor3h(short red, short green, short blue) {


  }

  @Override
  public void glSecondaryColor3hv(ShortBuffer v) {


  }

  @Override
  public void glSecondaryColor3hv(short[] v, int v_offset) {


  }

  @Override
  public void glVertexWeighth(short weight) {


  }

  @Override
  public void glVertexWeighthv(ShortBuffer weight) {


  }

  @Override
  public void glVertexWeighthv(short[] weight, int weight_offset) {


  }

  @Override
  public void glVertexAttrib1h(int index, short x) {


  }

  @Override
  public void glVertexAttrib1hv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib1hv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib2h(int index, short x, short y) {


  }

  @Override
  public void glVertexAttrib2hv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib2hv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib3h(int index, short x, short y, short z) {


  }

  @Override
  public void glVertexAttrib3hv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib3hv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttrib4h(int index, short x, short y, short z, short w) {


  }

  @Override
  public void glVertexAttrib4hv(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttrib4hv(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribs1hv(int index, int n, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribs1hv(int index, int n, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribs2hv(int index, int n, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribs2hv(int index, int n, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribs3hv(int index, int n, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribs3hv(int index, int n, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribs4hv(int index, int n, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribs4hv(int index, int n, short[] v, int v_offset) {


  }

  @Override
  public void glGenOcclusionQueriesNV(int n, IntBuffer ids) {


  }

  @Override
  public void glGenOcclusionQueriesNV(int n, int[] ids, int ids_offset) {


  }

  @Override
  public void glDeleteOcclusionQueriesNV(int n, IntBuffer ids) {


  }

  @Override
  public void glDeleteOcclusionQueriesNV(int n, int[] ids, int ids_offset) {


  }

  @Override
  public boolean glIsOcclusionQueryNV(int id) {

    return false;
  }

  @Override
  public void glBeginOcclusionQueryNV(int id) {


  }

  @Override
  public void glEndOcclusionQueryNV() {


  }

  @Override
  public void glGetOcclusionQueryivNV(int id, int pname, IntBuffer params) {


  }

  @Override
  public void glGetOcclusionQueryivNV(int id, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetOcclusionQueryuivNV(int id, int pname, IntBuffer params) {


  }

  @Override
  public void glGetOcclusionQueryuivNV(int id, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glProgramBufferParametersfvNV(int target, int bindingIndex, int wordIndex, int count,
      FloatBuffer params) {


  }

  @Override
  public void glProgramBufferParametersfvNV(int target, int bindingIndex, int wordIndex, int count,
      float[] params, int params_offset) {


  }

  @Override
  public void glProgramBufferParametersIivNV(int target, int bindingIndex, int wordIndex, int count,
      IntBuffer params) {


  }

  @Override
  public void glProgramBufferParametersIivNV(int target, int bindingIndex, int wordIndex, int count,
      int[] params, int params_offset) {


  }

  @Override
  public void glProgramBufferParametersIuivNV(int target, int bindingIndex, int wordIndex,
      int count, IntBuffer params) {


  }

  @Override
  public void glProgramBufferParametersIuivNV(int target, int bindingIndex, int wordIndex,
      int count, int[] params, int params_offset) {


  }

  @Override
  public void glPixelDataRangeNV(int target, int length, Buffer pointer) {


  }

  @Override
  public void glFlushPixelDataRangeNV(int target) {


  }

  @Override
  public void glPrimitiveRestartNV() {


  }

  @Override
  public void glPrimitiveRestartIndexNV(int index) {


  }

  @Override
  public void glFramebufferSampleLocationsfvNV(int target, int start, int count, FloatBuffer v) {


  }

  @Override
  public void glFramebufferSampleLocationsfvNV(int target, int start, int count, float[] v,
      int v_offset) {


  }

  @Override
  public void glNamedFramebufferSampleLocationsfvNV(int framebuffer, int start, int count,
      FloatBuffer v) {


  }

  @Override
  public void glNamedFramebufferSampleLocationsfvNV(int framebuffer, int start, int count,
      float[] v, int v_offset) {


  }

  @Override
  public void glResolveDepthValuesNV() {


  }

  @Override
  public void glTextureBarrierNV() {


  }

  @Override
  public void glBindTransformFeedbackNV(int target, int id) {


  }

  @Override
  public void glDeleteTransformFeedbacksNV(int n, IntBuffer ids) {


  }

  @Override
  public void glDeleteTransformFeedbacksNV(int n, int[] ids, int ids_offset) {


  }

  @Override
  public void glGenTransformFeedbacksNV(int n, IntBuffer ids) {


  }

  @Override
  public void glGenTransformFeedbacksNV(int n, int[] ids, int ids_offset) {


  }

  @Override
  public boolean glIsTransformFeedbackNV(int id) {

    return false;
  }

  @Override
  public void glPauseTransformFeedbackNV() {


  }

  @Override
  public void glResumeTransformFeedbackNV() {


  }

  @Override
  public void glDrawTransformFeedbackNV(int mode, int id) {


  }

  @Override
  public void glVDPAUInitNV(Buffer vdpDevice, Buffer getProcAddress) {


  }

  @Override
  public void glVDPAUFiniNV() {


  }

  @Override
  public long glVDPAURegisterVideoSurfaceNV(Buffer vdpSurface, int target, int numTextureNames,
      IntBuffer textureNames) {

    return 0;
  }

  @Override
  public long glVDPAURegisterVideoSurfaceNV(Buffer vdpSurface, int target, int numTextureNames,
      int[] textureNames, int textureNames_offset) {

    return 0;
  }

  @Override
  public long glVDPAURegisterOutputSurfaceNV(Buffer vdpSurface, int target, int numTextureNames,
      IntBuffer textureNames) {

    return 0;
  }

  @Override
  public long glVDPAURegisterOutputSurfaceNV(Buffer vdpSurface, int target, int numTextureNames,
      int[] textureNames, int textureNames_offset) {

    return 0;
  }

  @Override
  public boolean glVDPAUIsSurfaceNV(long surface) {

    return false;
  }

  @Override
  public void glVDPAUUnregisterSurfaceNV(long surface) {


  }

  @Override
  public void glVDPAUGetSurfaceivNV(long surface, int pname, int bufSize, IntBuffer length,
      IntBuffer values) {


  }

  @Override
  public void glVDPAUGetSurfaceivNV(long surface, int pname, int bufSize, int[] length,
      int length_offset, int[] values, int values_offset) {


  }

  @Override
  public void glVDPAUSurfaceAccessNV(long surface, int access) {


  }

  @Override
  public void glVDPAUMapSurfacesNV(int numSurfaces, PointerBuffer surfaces) {


  }

  @Override
  public void glVDPAUUnmapSurfacesNV(int numSurface, PointerBuffer surfaces) {


  }

  @Override
  public void glVertexAttribL1i64NV(int index, long x) {


  }

  @Override
  public void glVertexAttribL2i64NV(int index, long x, long y) {


  }

  @Override
  public void glVertexAttribL3i64NV(int index, long x, long y, long z) {


  }

  @Override
  public void glVertexAttribL4i64NV(int index, long x, long y, long z, long w) {


  }

  @Override
  public void glVertexAttribL1i64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL1i64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL2i64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL2i64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL3i64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL3i64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL4i64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL4i64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL1ui64NV(int index, long x) {


  }

  @Override
  public void glVertexAttribL2ui64NV(int index, long x, long y) {


  }

  @Override
  public void glVertexAttribL3ui64NV(int index, long x, long y, long z) {


  }

  @Override
  public void glVertexAttribL4ui64NV(int index, long x, long y, long z, long w) {


  }

  @Override
  public void glVertexAttribL1ui64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL1ui64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL2ui64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL2ui64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL3ui64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL3ui64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribL4ui64vNV(int index, LongBuffer v) {


  }

  @Override
  public void glVertexAttribL4ui64vNV(int index, long[] v, int v_offset) {


  }

  @Override
  public void glGetVertexAttribLi64vNV(int index, int pname, LongBuffer params) {


  }

  @Override
  public void glGetVertexAttribLi64vNV(int index, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribLui64vNV(int index, int pname, LongBuffer params) {


  }

  @Override
  public void glGetVertexAttribLui64vNV(int index, int pname, long[] params, int params_offset) {


  }

  @Override
  public void glVertexAttribLFormatNV(int index, int size, int type, int stride) {


  }

  @Override
  public void glVertexAttribI1iEXT(int index, int x) {


  }

  @Override
  public void glVertexAttribI2iEXT(int index, int x, int y) {


  }

  @Override
  public void glVertexAttribI3iEXT(int index, int x, int y, int z) {


  }

  @Override
  public void glVertexAttribI4iEXT(int index, int x, int y, int z, int w) {


  }

  @Override
  public void glVertexAttribI1uiEXT(int index, int x) {


  }

  @Override
  public void glVertexAttribI2uiEXT(int index, int x, int y) {


  }

  @Override
  public void glVertexAttribI3uiEXT(int index, int x, int y, int z) {


  }

  @Override
  public void glVertexAttribI4uiEXT(int index, int x, int y, int z, int w) {


  }

  @Override
  public void glVertexAttribI1ivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI1ivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI2ivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI2ivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI3ivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI3ivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4ivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI4ivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI1uivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI1uivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI2uivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI2uivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI3uivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI3uivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4uivEXT(int index, IntBuffer v) {


  }

  @Override
  public void glVertexAttribI4uivEXT(int index, int[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4bvEXT(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttribI4bvEXT(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4svEXT(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribI4svEXT(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4ubvEXT(int index, ByteBuffer v) {


  }

  @Override
  public void glVertexAttribI4ubvEXT(int index, byte[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribI4usvEXT(int index, ShortBuffer v) {


  }

  @Override
  public void glVertexAttribI4usvEXT(int index, short[] v, int v_offset) {


  }

  @Override
  public void glVertexAttribIPointerEXT(int index, int size, int type, int stride, Buffer pointer) {


  }

  @Override
  public void glGetVertexAttribIivEXT(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribIivEXT(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glGetVertexAttribIuivEXT(int index, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVertexAttribIuivEXT(int index, int pname, int[] params, int params_offset) {


  }

  @Override
  public void glBeginVideoCaptureNV(int video_capture_slot) {


  }

  @Override
  public void glBindVideoCaptureStreamBufferNV(int video_capture_slot, int stream, int frame_region,
      long offset) {


  }

  @Override
  public void glBindVideoCaptureStreamTextureNV(int video_capture_slot, int stream,
      int frame_region, int target, int texture) {


  }

  @Override
  public void glEndVideoCaptureNV(int video_capture_slot) {


  }

  @Override
  public void glGetVideoCaptureivNV(int video_capture_slot, int pname, IntBuffer params) {


  }

  @Override
  public void glGetVideoCaptureivNV(int video_capture_slot, int pname, int[] params,
      int params_offset) {


  }

  @Override
  public void glGetVideoCaptureStreamivNV(int video_capture_slot, int stream, int pname,
      IntBuffer params) {


  }

  @Override
  public void glGetVideoCaptureStreamivNV(int video_capture_slot, int stream, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glGetVideoCaptureStreamfvNV(int video_capture_slot, int stream, int pname,
      FloatBuffer params) {


  }

  @Override
  public void glGetVideoCaptureStreamfvNV(int video_capture_slot, int stream, int pname,
      float[] params, int params_offset) {


  }

  @Override
  public void glGetVideoCaptureStreamdvNV(int video_capture_slot, int stream, int pname,
      DoubleBuffer params) {


  }

  @Override
  public void glGetVideoCaptureStreamdvNV(int video_capture_slot, int stream, int pname,
      double[] params, int params_offset) {


  }

  @Override
  public int glVideoCaptureNV(int video_capture_slot, IntBuffer sequence_num,
      LongBuffer capture_time) {

    return 0;
  }

  @Override
  public int glVideoCaptureNV(int video_capture_slot, int[] sequence_num, int sequence_num_offset,
      long[] capture_time, int capture_time_offset) {

    return 0;
  }

  @Override
  public void glVideoCaptureStreamParameterivNV(int video_capture_slot, int stream, int pname,
      IntBuffer params) {


  }

  @Override
  public void glVideoCaptureStreamParameterivNV(int video_capture_slot, int stream, int pname,
      int[] params, int params_offset) {


  }

  @Override
  public void glVideoCaptureStreamParameterfvNV(int video_capture_slot, int stream, int pname,
      FloatBuffer params) {


  }

  @Override
  public void glVideoCaptureStreamParameterfvNV(int video_capture_slot, int stream, int pname,
      float[] params, int params_offset) {


  }

  @Override
  public void glVideoCaptureStreamParameterdvNV(int video_capture_slot, int stream, int pname,
      DoubleBuffer params) {


  }

  @Override
  public void glVideoCaptureStreamParameterdvNV(int video_capture_slot, int stream, int pname,
      double[] params, int params_offset) {


  }

  @Override
  public void glFramebufferTextureMultiviewOVR(int target, int attachment, int texture, int level,
      int baseViewIndex, int numViews) {


  }

  @Override
  public void glFinishTextureSUNX() {


  }

  @Override
  public GLBufferStorage mapNamedBufferEXT(int bufferName, int access) throws GLException {

    return null;
  }

  @Override
  public GLBufferStorage mapNamedBufferRangeEXT(int bufferName, long offset, long length,
      int access) throws GLException {

    return null;
  }

  @Override
  public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride,
      Buffer ptr) {


  }

  @Override
  public void glDrawElementsInstanced(int mode, int count, int type, Buffer indices,
      int instancecount) {


  }

  @Override
  public void glDrawRangeElements(int mode, int start, int end, int count, int type,
      Buffer indices) {


  }

  @Override
  public void glVertexAttribIPointer(int index, int size, int type, int stride, Buffer pointer) {


  }


}
