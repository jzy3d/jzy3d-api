package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;


public class DualDepthPeelingAlgorithm extends AbstractDepthPeelingAlgorithm
    implements IDepthPeelingAlgorithm {
  public int[] g_dualBackBlenderFboId = new int[1];
  public int[] g_dualPeelingSingleFboId = new int[1];
  public int[] g_dualDepthTexId = new int[2];
  public int[] g_dualFrontBlenderTexId = new int[2];
  public int[] g_dualBackTempTexId = new int[2];
  public int[] g_dualBackBlenderTexId = new int[1];

  public GLSLProgram glslInit;
  public GLSLProgram glslPeel;
  public GLSLProgram glslBlend;
  public GLSLProgram glslFinal;

  @Override
  public void init(IPainter painter, int width, int height) {
    try {
      initDualPeelingRenderTargets(painter, width, height);
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, 0);

    buildShaders(painter);
    buildFullScreenQuad(painter);
    buildFinish(painter);
  }

  @Override
  public void display(IPainter painter) {
    resetNumPass();
    doRender(painter);
  }

  @Override
  public void reshape(IPainter painter, int width, int height) {
    deleteDualPeelingRenderTargets(painter);
    initDualPeelingRenderTargets(painter, width, height);
  }

  /* */

  protected ShaderFilePair shaderBase = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "shade_vertex.glsl", "shade_fragment.glsl");
  protected ShaderFilePair shaderInit = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "dual_peeling_init_vertex.glsl", "dual_peeling_init_fragment.glsl");
  protected ShaderFilePair shaderPeel = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "dual_peeling_peel_vertex.glsl", "dual_peeling_peel_fragment.glsl");
  protected ShaderFilePair shaderBlend = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "dual_peeling_blend_vertex.glsl", "dual_peeling_blend_fragment.glsl");
  protected ShaderFilePair shaderFinal = new ShaderFilePair(DualDepthPeelingAlgorithm.class,
      "dual_peeling_final_vertex.glsl", "dual_peeling_final_fragment.glsl");


  @Override
  protected void buildShaders(IPainter painter) {
    System.err.println("\nloading shaders...\n");

    glslInit = new GLSLProgram();
    // glslInit.loadAndCompileVertexShader(painter, shaderInit.getVertexInputStream(),
    // shaderInit.getVertexURL());
    glslInit.loadAndCompileVertexShader(painter, shaderInit.getVertexStream(),
        shaderInit.getVertexURL());
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
        shaderFinal.getFragmentURL());
    glslFinal.link(painter);
  }

  @Override
  protected void destroyShaders(IPainter painter) {
    glslInit.destroy(painter);
    glslPeel.destroy(painter);
    glslBlend.destroy(painter);
    glslFinal.destroy(painter);
  }

  protected void initDualPeelingRenderTargets(IPainter painter, int g_imageWidth, int g_imageHeight) {
    painter.glGenTextures(2, g_dualDepthTexId, 0);
    painter.glGenTextures(2, g_dualFrontBlenderTexId, 0);
    painter.glGenTextures(2, g_dualBackTempTexId, 0);
    painter.glGenFramebuffers(1, g_dualPeelingSingleFboId, 0);
    for (int i = 0; i < 2; i++) {
      painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[i]);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);

      // painter.glEnable( GLConstants.GL_PIXEL_UNPACK_BUFFER );

      painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RG32F, g_imageWidth,
          g_imageHeight, 0, GLConstants.GL_RGB, GLConstants.GL_FLOAT, null);

      
      painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[i]);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
      painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);

      painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[i]);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
      painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
      painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GLConstants.GL_RGBA, GLConstants.GL_FLOAT, null);
    }

    painter.glGenTextures(1, g_dualBackBlenderTexId, 0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0]);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_S, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_WRAP_T, GLConstants.GL_CLAMP);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MIN_FILTER, GLConstants.GL_NEAREST);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_RECTANGLE_ARB, GLConstants.GL_TEXTURE_MAG_FILTER, GLConstants.GL_NEAREST);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_RECTANGLE_ARB, 0, GLConstants.GL_RGB, g_imageWidth, g_imageHeight, 0,
        GLConstants.GL_RGB, GLConstants.GL_FLOAT, null);

    painter.glGenFramebuffers(1, g_dualBackBlenderFboId, 0);
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_dualBackBlenderFboId[0]);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0], 0);

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_dualPeelingSingleFboId[0]);

    int j = 0;
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT0,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[j], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT1,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[j], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT2,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[j], 0);

    j = 1;
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT3,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[j], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT4,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[j], 0);
    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT5,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[j], 0);

    painter.glFramebufferTexture2D(GLConstants.GL_FRAMEBUFFER, GLConstants.GL_COLOR_ATTACHMENT6,
        GLConstants.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0], 0);
  }

  protected void deleteDualPeelingRenderTargets(IPainter painter) {
    painter.glDeleteFramebuffers(1, g_dualBackBlenderFboId, 0);
    painter.glDeleteFramebuffers(1, g_dualPeelingSingleFboId, 0);
    painter.glDeleteTextures(2, g_dualDepthTexId, 0);
    painter.glDeleteTextures(2, g_dualFrontBlenderTexId, 0);
    painter.glDeleteTextures(2, g_dualBackTempTexId, 0);
    painter.glDeleteTextures(1, g_dualBackBlenderTexId, 0);
  }

  protected void doRender(IPainter painter) {
    painter.glDisable(GLConstants.GL_DEPTH_TEST);
    painter.glEnable(GLConstants.GL_BLEND);

    // ---------------------------------------------------------------------
    // 1. Initialize Min-Max Depth Buffer
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_dualPeelingSingleFboId[0]);

    // Render targets 1 and 2 store the front and back colors
    // Clear to 0.0 and use MAX blending to filter written color
    // At most one front color and one back color can be written every pass
    painter.glDrawBuffers(2, g_drawBuffers, 1);
    painter.glClearColor(0, 0, 0, 0);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);

    // Render target 0 stores (-minDepth, maxDepth, alphaMultiplier)
    painter.glDrawBuffer(g_drawBuffers[0]);
    painter.glClearColor(-MAX_DEPTH, -MAX_DEPTH, 0, 0);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);
    painter.glBlendEquation(GLConstants.GL_MAX);

    glslInit.bind(painter);

    tasksToRender(painter);

    glslInit.unbind(painter);

    // ---------------------------------------------------------------------
    // 2. Dual Depth Peeling + Blending
    // ---------------------------------------------------------------------

    // Since we cannot blend the back colors in the geometry passes,
    // we use another render target to do the alpha blending
    // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, g_dualBackBlenderFboId);
    painter.glDrawBuffer(g_drawBuffers[6]);
    painter.glClearColor(g_backgroundColor[0], g_backgroundColor[1], g_backgroundColor[2], 0);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);

    int currId = 0;

    for (int pass = 1; g_useOQ || pass < g_numPasses; pass++) {
      currId = pass % 2;
      int prevId = 1 - currId;
      int bufId = currId * 3;

      // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT,
      // g_dualPeelingFboId[currId]);

      painter.glDrawBuffers(2, g_drawBuffers, bufId + 1);
      painter.glClearColor(0, 0, 0, 0);
      painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);

      painter.glDrawBuffer(g_drawBuffers[bufId + 0]);
      painter.glClearColor(-MAX_DEPTH, -MAX_DEPTH, 0, 0);
      painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);

      // Render target 0: RG32F MAX blending
      // Render target 1: RGBA MAX blending
      // Render target 2: RGBA MAX blending
      painter.glDrawBuffers(3, g_drawBuffers, bufId + 0);
      painter.glBlendEquation(GLConstants.GL_MAX);

      glslPeel.bind(painter);
      glslPeel.bindTextureRECT(painter, "DepthBlenderTex", g_dualDepthTexId[prevId], 0);
      glslPeel.bindTextureRECT(painter, "FrontBlenderTex", g_dualFrontBlenderTexId[prevId], 1);
      glslPeel.setUniform(painter, "Alpha", g_opacity, 1);

      tasksToRender(painter);

      glslPeel.unbind(painter);

      // Full screen pass to alpha-blend the back color
      painter.glDrawBuffer(g_drawBuffers[6]);

      painter.glBlendEquation(GLConstants.GL_FUNC_ADD);
      painter.glBlendFunc(GLConstants.GL_SRC_ALPHA, GLConstants.GL_ONE_MINUS_SRC_ALPHA);

      if (g_useOQ) {
        painter.glBeginQuery(GLConstants.GL_SAMPLES_PASSED, g_queryId[0]);
      }

      glslBlend.bind(painter);
      glslBlend.bindTextureRECT(painter, "TempTex", g_dualBackTempTexId[currId], 0);
      painter.glCallList(g_quadDisplayList);
      glslBlend.unbind(painter);

      if (g_useOQ) {
        painter.glEndQuery(GLConstants.GL_SAMPLES_PASSED);
        int[] sample_count = new int[] {0};
        painter.glGetQueryObjectuiv(g_queryId[0], GLConstants.GL_QUERY_RESULT, sample_count, 0);
        if (sample_count[0] == 0) {
          break;
        }
      }
    }

    painter.glDisable(GLConstants.GL_BLEND);

    // ---------------------------------------------------------------------
    // 3. Final Pass
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, 0);
    painter.glDrawBuffer(GLConstants.GL_BACK);

    glslFinal.bind(painter);
    glslFinal.bindTextureRECT(painter, "FrontBlenderTex", g_dualFrontBlenderTexId[currId], 1);
    glslFinal.bindTextureRECT(painter, "BackBlenderTex", g_dualBackBlenderTexId[0], 2);
    painter.glCallList(g_quadDisplayList);
    glslFinal.unbind(painter);
  }
}
