package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;

public abstract class AbstractAccumulationDepthPeeling extends AbstractDepthPeelingAlgorithm {

  protected int[] g_accumulationTexId = new int[2];
  protected int[] g_accumulationFboId = new int[1];

  public AbstractAccumulationDepthPeeling() {
    super();
  }

  @Override
  public void init(IPainter painter, int width, int height) {
    initAccumulationRenderTargets(painter, width, height);

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, 0);

    buildShaders(painter);
    buildFullScreenQuad(painter);
    buildFinish(painter);
  }

  @Override
  public void reshape(IPainter painter, int width, int height) {
    deleteAccumulationRenderTargets(painter);
    initAccumulationRenderTargets(painter, width, height);
  }

  protected void initAccumulationRenderTargets(IPainter painter, int g_imageWidth, int g_imageHeight) {
    painter.glGenTextures(2, g_accumulationTexId, 0);

    painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0]);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA16F, g_imageWidth, g_imageHeight, 0,
        GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

    painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1]);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RG32F, g_imageWidth,
        g_imageHeight, 0, GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

    // painter.glTexImage2D( GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA16F,
    // g_imageWidth, g_imageHeight, 0, GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

    painter.glGenFramebuffers(1, g_accumulationFboId, 0);
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_accumulationFboId[0]);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[0], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT1,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_accumulationTexId[1], 0);

  }

  protected void deleteAccumulationRenderTargets(IPainter painter) {
    painter.glDeleteFramebuffers(1, g_accumulationFboId, 0);
    painter.glDeleteTextures(2, g_accumulationTexId, 0);
  }



}
