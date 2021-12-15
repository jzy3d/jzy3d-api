package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.IPainter;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;


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
    GL2 gl = getGL(painter);
    
    try {
      initDualPeelingRenderTargets(gl, width, height);
    } catch (GLException e) {
      throw new RuntimeException(e);
    }

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

    buildShaders(gl);
    buildFullScreenQuad(gl);
    buildFinish(gl);
  }

  @Override
  public void display(IPainter painter) {
    resetNumPass();
    doRender(painter, getGL(painter));
  }

  @Override
  public void reshape(IPainter painter, int width, int height) {
    deleteDualPeelingRenderTargets(getGL(painter));
    initDualPeelingRenderTargets(getGL(painter), width, height);
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
  protected void buildShaders(GL2 gl) {
    System.err.println("\nloading shaders...\n");

    glslInit = new GLSLProgram();
    // glslInit.loadAndCompileVertexShader(gl, shaderInit.getVertexInputStream(),
    // shaderInit.getVertexURL());
    glslInit.loadAndCompileVertexShader(gl, shaderInit.getVertexStream(),
        shaderInit.getVertexURL());
    glslInit.loadAndCompileFragmentShader(gl, shaderInit.getFragmentStream(),
        shaderInit.getFragmentURL());
    glslInit.link(gl);

    glslPeel = new GLSLProgram();
    glslPeel.loadAndCompileVertexShader(gl, shaderBase.getVertexStream(),
        shaderBase.getVertexURL());
    glslPeel.loadAndCompileVertexShader(gl, shaderPeel.getVertexStream(),
        shaderPeel.getVertexURL());
    glslPeel.loadAndCompileFragmentShader(gl, shaderBase.getFragmentStream(),
        shaderBase.getFragmentURL());
    glslPeel.loadAndCompileFragmentShader(gl, shaderPeel.getFragmentStream(),
        shaderPeel.getFragmentURL());
    glslPeel.link(gl);

    glslBlend = new GLSLProgram();
    glslBlend.loadAndCompileVertexShader(gl, shaderBlend.getVertexStream(),
        shaderBlend.getVertexURL());
    glslBlend.loadAndCompileFragmentShader(gl, shaderBlend.getFragmentStream(),
        shaderBlend.getFragmentURL());
    glslBlend.link(gl);

    glslFinal = new GLSLProgram();
    glslFinal.loadAndCompileVertexShader(gl, shaderFinal.getVertexStream(),
        shaderFinal.getVertexURL());
    glslFinal.loadAndCompileFragmentShader(gl, shaderFinal.getFragmentStream(),
        shaderFinal.getFragmentURL());
    glslFinal.link(gl);
  }

  @Override
  protected void destroyShaders(GL2 gl) {
    glslInit.destroy(gl);
    glslPeel.destroy(gl);
    glslBlend.destroy(gl);
    glslFinal.destroy(gl);
  }

  protected void initDualPeelingRenderTargets(GL2 gl, int g_imageWidth, int g_imageHeight) {
    gl.glGenTextures(2, g_dualDepthTexId, 0);
    gl.glGenTextures(2, g_dualFrontBlenderTexId, 0);
    gl.glGenTextures(2, g_dualBackTempTexId, 0);
    gl.glGenFramebuffers(1, g_dualPeelingSingleFboId, 0);
    for (int i = 0; i < 2; i++) {
      gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[i]);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

      // gl.glEnable( GL2.GL_PIXEL_UNPACK_BUFFER );

      gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RG32F, g_imageWidth,
          g_imageHeight, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);

      
      gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[i]);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GL2.GL_RGBA, GL2.GL_FLOAT, null);

      gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[i]);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GL2.GL_RGBA, GL2.GL_FLOAT, null);
    }

    gl.glGenTextures(1, g_dualBackBlenderTexId, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0]);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGB, g_imageWidth, g_imageHeight, 0,
        GL2.GL_RGB, GL2.GL_FLOAT, null);

    gl.glGenFramebuffers(1, g_dualBackBlenderFboId, 0);
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_dualBackBlenderFboId[0]);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0], 0);

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_dualPeelingSingleFboId[0]);

    int j = 0;
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[j], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[j], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT2,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[j], 0);

    j = 1;
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT3,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualDepthTexId[j], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT4,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualFrontBlenderTexId[j], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT5,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackTempTexId[j], 0);

    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT6,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_dualBackBlenderTexId[0], 0);
  }

  protected void deleteDualPeelingRenderTargets(GL2 gl) {
    gl.glDeleteFramebuffers(1, g_dualBackBlenderFboId, 0);
    gl.glDeleteFramebuffers(1, g_dualPeelingSingleFboId, 0);
    gl.glDeleteTextures(2, g_dualDepthTexId, 0);
    gl.glDeleteTextures(2, g_dualFrontBlenderTexId, 0);
    gl.glDeleteTextures(2, g_dualBackTempTexId, 0);
    gl.glDeleteTextures(1, g_dualBackBlenderTexId, 0);
  }

  protected void doRender(IPainter painter, GL2 gl) {
    gl.glDisable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_BLEND);

    // ---------------------------------------------------------------------
    // 1. Initialize Min-Max Depth Buffer
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_dualPeelingSingleFboId[0]);

    // Render targets 1 and 2 store the front and back colors
    // Clear to 0.0 and use MAX blending to filter written color
    // At most one front color and one back color can be written every pass
    gl.glDrawBuffers(2, g_drawBuffers, 1);
    gl.glClearColor(0, 0, 0, 0);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

    // Render target 0 stores (-minDepth, maxDepth, alphaMultiplier)
    gl.glDrawBuffer(g_drawBuffers[0]);
    gl.glClearColor(-MAX_DEPTH, -MAX_DEPTH, 0, 0);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    gl.glBlendEquation(GL2.GL_MAX);

    glslInit.bind(gl);

    tasksToRender(painter);

    glslInit.unbind(gl);

    // ---------------------------------------------------------------------
    // 2. Dual Depth Peeling + Blending
    // ---------------------------------------------------------------------

    // Since we cannot blend the back colors in the geometry passes,
    // we use another render target to do the alpha blending
    // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, g_dualBackBlenderFboId);
    gl.glDrawBuffer(g_drawBuffers[6]);
    gl.glClearColor(g_backgroundColor[0], g_backgroundColor[1], g_backgroundColor[2], 0);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

    int currId = 0;

    for (int pass = 1; g_useOQ || pass < g_numPasses; pass++) {
      currId = pass % 2;
      int prevId = 1 - currId;
      int bufId = currId * 3;

      // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT,
      // g_dualPeelingFboId[currId]);

      gl.glDrawBuffers(2, g_drawBuffers, bufId + 1);
      gl.glClearColor(0, 0, 0, 0);
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

      gl.glDrawBuffer(g_drawBuffers[bufId + 0]);
      gl.glClearColor(-MAX_DEPTH, -MAX_DEPTH, 0, 0);
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

      // Render target 0: RG32F MAX blending
      // Render target 1: RGBA MAX blending
      // Render target 2: RGBA MAX blending
      gl.glDrawBuffers(3, g_drawBuffers, bufId + 0);
      gl.glBlendEquation(GL2.GL_MAX);

      glslPeel.bind(gl);
      glslPeel.bindTextureRECT(gl, "DepthBlenderTex", g_dualDepthTexId[prevId], 0);
      glslPeel.bindTextureRECT(gl, "FrontBlenderTex", g_dualFrontBlenderTexId[prevId], 1);
      glslPeel.setUniform(gl, "Alpha", g_opacity, 1);

      tasksToRender(painter);

      glslPeel.unbind(gl);

      // Full screen pass to alpha-blend the back color
      gl.glDrawBuffer(g_drawBuffers[6]);

      gl.glBlendEquation(GL2.GL_FUNC_ADD);
      gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

      if (g_useOQ) {
        gl.glBeginQuery(GL2.GL_SAMPLES_PASSED, g_queryId[0]);
      }

      glslBlend.bind(gl);
      glslBlend.bindTextureRECT(gl, "TempTex", g_dualBackTempTexId[currId], 0);
      gl.glCallList(g_quadDisplayList);
      glslBlend.unbind(gl);

      if (g_useOQ) {
        gl.glEndQuery(GL2.GL_SAMPLES_PASSED);
        int[] sample_count = new int[] {0};
        gl.glGetQueryObjectuiv(g_queryId[0], GL2.GL_QUERY_RESULT, sample_count, 0);
        if (sample_count[0] == 0) {
          break;
        }
      }
    }

    gl.glDisable(GL2.GL_BLEND);

    // ---------------------------------------------------------------------
    // 3. Final Pass
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    gl.glDrawBuffer(GL2.GL_BACK);

    glslFinal.bind(gl);
    glslFinal.bindTextureRECT(gl, "FrontBlenderTex", g_dualFrontBlenderTexId[currId], 1);
    glslFinal.bindTextureRECT(gl, "BackBlenderTex", g_dualBackBlenderTexId[0], 2);
    gl.glCallList(g_quadDisplayList);
    glslFinal.unbind(gl);
  }
}
