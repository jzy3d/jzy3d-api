package org.jzy3d.mocks.jogl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jzy3d.maths.Coord3d;
import com.jogamp.opengl.GL;
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
import com.jogamp.opengl.GLBufferStorage;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLES1;
import com.jogamp.opengl.GLES2;
import com.jogamp.opengl.GLES3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;

public class GLMock implements GL {
  static boolean IS_GL2 = true;

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
    return IS_GL2;
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
  public GL2Mock getGL2() throws GLException {
    return gl2;
  }

  GL2Mock gl2 = new GL2Mock();

  public GL2Mock getGL2Mock() {
    return gl2;
  }

  public boolean vertex3f_contains_z(float value) {
    for (Coord3d c : getGL2Mock().getVertex3f()) {
      if (c.z == value) {
        return true;
      }
    }
    return false;
  }

  public boolean vertex3f_contains(float x, float y, float z) {
    return vertex3f_contains(new Coord3d(x, y, z));
  }

  public boolean vertex3f_contains(Coord3d value) {
    for (Coord3d c : getGL2Mock().getVertex3f()) {
      if (c.equals(value)) {
        return true;
      }
    }
    return false;
  }

  public void vertex3f_print() {
    for (Coord3d c : getGL2Mock().getVertex3f()) {
      System.out.println(c);
    }
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

}
