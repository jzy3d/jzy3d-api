package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;


public class FrontToBackPeelingAlgorithm extends AbstractDepthPeelingAlgorithm
    implements IDepthPeelingAlgorithm {
  public GLSLProgram glslInit;
  public GLSLProgram glslPeel;
  public GLSLProgram glslBlend;
  public GLSLProgram glslFinal;

  public int[] g_frontFboId = new int[2];
  public int[] g_frontDepthTexId = new int[2];
  public int[] g_frontColorTexId = new int[2];
  public int[] g_frontColorBlenderTexId = new int[1];
  public int[] g_frontColorBlenderFboId = new int[1];

  protected ShaderFilePair shaderBase = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "shade_vertex.glsl", "shade_fragment.glsl");
  protected ShaderFilePair shaderInit = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "front_peeling_init_vertex.glsl", "front_peeling_init_fragment.glsl");
  protected ShaderFilePair shaderPeel = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "front_peeling_peel_vertex.glsl", "front_peeling_peel_fragment.glsl");
  protected ShaderFilePair shaderBlend = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "front_peeling_blend_vertex.glsl", "front_peeling_blend_fragment.glsl");
  protected ShaderFilePair shaderFinal = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "front_peeling_final_vertex.glsl", "front_peeling_final_fragment.glsl");


  @Override
  public void init(IPainter painter, int width, int height) {
    saveTargetFramebuffer(painter);

    initFrontPeelingRenderTargets(painter, width, height);

    bindTargetFramebuffer(painter);

    buildShaders(painter);
    buildFullScreenQuad(painter);
    buildFinish(painter);
  }

  @Override
  public void display(IPainter painter) {
    saveTargetFramebuffer(painter);
    resetNumPass();
    doRender(painter);
  }

  @Override
  public void reshape(IPainter painter, int width, int height) {
    saveTargetFramebuffer(painter);
    deleteFrontPeelingRenderTargets(painter);
    initFrontPeelingRenderTargets(painter, width, height);
    bindTargetFramebuffer(painter);
  }

  /* */

  @Override
  protected void buildShaders(IPainter painter) {
    glslInit = new GLSLProgram();
    glslInit.loadAndCompileVertexShader(painter, shaderBase.getVertexStream(),
        shaderBase.getVertexURL());
    glslInit.loadAndCompileVertexShader(painter, shaderInit.getVertexStream(),
        shaderInit.getVertexURL());
    glslInit.loadAndCompileFragmentShader(painter, shaderBase.getFragmentStream(),
        shaderBase.getFragmentURL());
    glslInit.loadAndCompileFragmentShader(painter, shaderInit.getFragmentStream(),
        shaderInit.getFragmentURL());
    glslInit.link(painter);

    glslPeel = new GLSLProgram();
    glslPeel.loadAndCompileVertexShader(painter, shaderBase.getVertexStream(),
        shaderBase.getVertexURL());
    glslPeel.loadAndCompileVertexShader(painter, shaderPeel.getVertexStream(),
        shaderPeel.getVertexURL());
    glslPeel.loadAndCompileFragmentShader(painter, shaderBase.getFragmentStream(),
        shaderBase.getFragmentURL());
    glslPeel.loadAndCompileFragmentShader(painter, shaderPeel.getFragmentStream(),
        shaderPeel.getFragmentURL());
    glslPeel.link(painter);

    glslBlend = new GLSLProgram();
    glslBlend.loadAndCompileVertexShader(painter, shaderBlend.getVertexStream(),
        shaderBlend.getVertexURL());
    glslBlend.loadAndCompileFragmentShader(painter, shaderBlend.getFragmentStream(),
        shaderBlend.getFragmentURL());
    glslBlend.link(painter);

    glslFinal = new GLSLProgram();
    glslFinal.loadAndCompileVertexShader(painter, shaderFinal.getVertexStream(),
        shaderFinal.getVertexURL());
    glslFinal.loadAndCompileFragmentShader(painter, shaderFinal.getFragmentStream(),
        shaderFinal.getVertexURL());
    glslFinal.link(painter);
  }

  @Override
  protected void destroyShaders(IPainter painter) {
    glslInit.destroy(painter);
    glslPeel.destroy(painter);
    glslBlend.destroy(painter);
    glslFinal.destroy(painter);
  }

  protected void initFrontPeelingRenderTargets(IPainter painter, int g_imageWidth, int g_imageHeight) {
    painter.glGenTextures(2, g_frontDepthTexId, 0);
    painter.glGenTextures(2, g_frontColorTexId, 0);
    painter.glGenFramebuffers(2, g_frontFboId, 0);

    for (int i = 0; i < 2; i++) {
      painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[i]);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
      painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_DEPTH_COMPONENT32F, g_imageWidth,
          g_imageHeight, 0, GLConstants.GL_DEPTH_COMPONENT, GLConstants.GL_FLOAT, null);

      painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontColorTexId[i]);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
      painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

      painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_frontFboId[i]);
      painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_DEPTH_ATTACHMENT,
          GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[i], 0);
      painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
          GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontColorTexId[i], 0);
      checkFramebuffer(painter, "front peeling " + i);
    }

    painter.glGenTextures(1, g_frontColorBlenderTexId, 0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontColorBlenderTexId[0]);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA, g_imageWidth, g_imageHeight, 0,
        GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

    painter.glGenFramebuffers(1, g_frontColorBlenderFboId, 0);
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_DEPTH_ATTACHMENT,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[0], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_frontColorBlenderTexId[0], 0);
    checkFramebuffer(painter, "front color blender");
  }

  protected void deleteFrontPeelingRenderTargets(IPainter painter) {
    painter.glDeleteFramebuffers(2, g_frontFboId, 0);
    painter.glDeleteFramebuffers(1, g_frontColorBlenderFboId, 0);
    painter.glDeleteTextures(2, g_frontDepthTexId, 0);
    painter.glDeleteTextures(2, g_frontColorTexId, 0);
    painter.glDeleteTextures(1, g_frontColorBlenderTexId, 0);
  }

  protected void doRender(IPainter painter) {
    // ---------------------------------------------------------------------
    // 1. Initialize Min Depth Buffer
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
    painter.glDrawBuffer(g_drawBuffers[0]);

    painter.glClearColor(0, 0, 0, 1);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT | GLConstants.GL_DEPTH_BUFFER_BIT);

    painter.glEnable(GLConstants.GL_DEPTH_TEST);

    glslInit.bind(painter);
    glslInit.setUniform(painter, "Alpha", g_opacity, 1);

    tasksToRender(painter);

    glslInit.unbind(painter);

    // ---------------------------------------------------------------------
    // 2. Depth Peeling + Blending
    // ---------------------------------------------------------------------

    int numLayers = (g_numPasses - 1) * 2;
    for (int layer = 1; g_useOQ || layer < numLayers; layer++) {
      int currId = layer % 2;
      int prevId = 1 - currId;

      painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_frontFboId[currId]);
      painter.glDrawBuffer(g_drawBuffers[0]);

      painter.glClearColor(0, 0, 0, 0);
      painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT | GLConstants.GL_DEPTH_BUFFER_BIT);

      painter.glDisable(GLConstants.GL_BLEND);
      painter.glEnable(GLConstants.GL_DEPTH_TEST);

      if (g_useOQ) {
        painter.glBeginQuery(GLConstants.GL_SAMPLES_PASSED, g_queryId[0]);
      }

      glslPeel.bind(painter);
      glslPeel.bindTextureRECT(painter, "DepthTex", g_frontDepthTexId[prevId], 0);
      glslPeel.setUniform(painter, "Alpha", g_opacity, 1);

      tasksToRender(painter);

      glslPeel.unbind(painter);

      if (g_useOQ) {
        painter.glEndQuery(GLConstants.GL_SAMPLES_PASSED);
      }

      painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
      painter.glDrawBuffer(g_drawBuffers[0]);

      painter.glDisable(GLConstants.GL_DEPTH_TEST);
      painter.glEnable(GLConstants.GL_BLEND);

      painter.glBlendEquation(GLConstants.GL_FUNC_ADD);
      painter.glBlendFuncSeparate(GLConstants.GL_DST_ALPHA, GLConstants.GL_ONE, GLConstants.GL_ZERO, GLConstants.GL_ONE_MINUS_SRC_ALPHA);

      glslBlend.bind(painter);
      glslBlend.bindTextureRECT(painter, "TempTex", g_frontColorTexId[currId], 0);
      painter.glCallList(g_quadDisplayList);
      glslBlend.unbind(painter);

      painter.glDisable(GLConstants.GL_BLEND);

      if (g_useOQ) {
        int[] sample_count = new int[] {0};
        painter.glGetQueryObjectuiv(g_queryId[0], GLConstants.GL_QUERY_RESULT, sample_count, 0);
        if (sample_count[0] == 0) {
          break;
        }
      }
    }

    // ---------------------------------------------------------------------
    // 3. Final Pass
    // ---------------------------------------------------------------------

    bindTargetFramebufferAndDrawBuffer(painter);
    painter.glDisable(GLConstants.GL_DEPTH_TEST);

    glslFinal.bind(painter);
    glslFinal.setUniform(painter, "BackgroundColor", g_backgroundColor, 3);
    glslFinal.bindTextureRECT(painter, "ColorTex", g_frontColorBlenderTexId[0], 0);
    painter.glCallList(g_quadDisplayList);
    glslFinal.unbind(painter);
  }
}
