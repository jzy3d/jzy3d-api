package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.painters.IPainter;
import com.jogamp.opengl.GL2;

public abstract class AbstractAccumulationDepthPeeling extends AbstractDepthPeelingAlgorithm {

  protected int[] g_accumulationTexId = new int[2];
  protected int[] g_accumulationFboId = new int[1];

  public AbstractAccumulationDepthPeeling() {
    super();
  }

  @Override
  public void init(IPainter painter, int width, int height) {
    GL2 gl = getGL(painter);
    
    initAccumulationRenderTargets(gl, width, height);

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

    buildShaders(gl);
    buildFullScreenQuad(gl);
    buildFinish(gl);
  }

  @Override
  public void reshape(IPainter painter, int width, int height) {
    deleteAccumulationRenderTargets(getGL(painter));
    initAccumulationRenderTargets(getGL(painter), width, height);
  }

  protected void initAccumulationRenderTargets(GL2 gl, int g_imageWidth, int g_imageHeight) {
    gl.glGenTextures(2, g_accumulationTexId, 0);

    gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0]);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA16F, g_imageWidth, g_imageHeight, 0,
        GL2.GL_RGBA, GL2.GL_FLOAT, null);

    gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1]);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RG32F, g_imageWidth,
        g_imageHeight, 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);

    // gl.glTexImage2D( GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA16F,
    // g_imageWidth, g_imageHeight, 0, GL2.GL_RGBA, GL2.GL_FLOAT, null);

    gl.glGenFramebuffers(1, g_accumulationFboId, 0);
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_accumulationFboId[0]);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1], 0);

  }

  protected void deleteAccumulationRenderTargets(GL2 gl) {
    gl.glDeleteFramebuffers(1, g_accumulationFboId, 0);
    gl.glDeleteTextures(2, g_accumulationTexId, 0);
  }



}
