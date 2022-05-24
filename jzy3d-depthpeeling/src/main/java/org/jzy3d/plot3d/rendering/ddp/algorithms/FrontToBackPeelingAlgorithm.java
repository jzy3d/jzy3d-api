package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.IPainter;
import com.jogamp.opengl.GL2;


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
    GL2 gl = getGL(painter);
    
    initFrontPeelingRenderTargets(gl, width, height);

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
    deleteFrontPeelingRenderTargets(getGL(painter));
    initFrontPeelingRenderTargets(getGL(painter), width, height);
  }

  /* */

  @Override
  protected void buildShaders(GL2 gl) {
    glslInit = new GLSLProgram();
    glslInit.loadAndCompileVertexShader(gl, shaderBase.getVertexStream(),
        shaderBase.getVertexURL());
    glslInit.loadAndCompileVertexShader(gl, shaderInit.getVertexStream(),
        shaderInit.getVertexURL());
    glslInit.loadAndCompileFragmentShader(gl, shaderBase.getFragmentStream(),
        shaderBase.getFragmentURL());
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
        shaderFinal.getVertexURL());
    glslFinal.link(gl);
  }

  @Override
  protected void destroyShaders(GL2 gl) {
    glslInit.destroy(gl);
    glslPeel.destroy(gl);
    glslBlend.destroy(gl);
    glslFinal.destroy(gl);
  }

  protected void initFrontPeelingRenderTargets(GL2 gl, int g_imageWidth, int g_imageHeight) {
    gl.glGenTextures(2, g_frontDepthTexId, 0);
    gl.glGenTextures(2, g_frontColorTexId, 0);
    gl.glGenFramebuffers(2, g_frontFboId, 0);

    for (int i = 0; i < 2; i++) {
      gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[i]);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_DEPTH_COMPONENT32F, g_imageWidth,
          g_imageHeight, 0, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, null);

      gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontColorTexId[i]);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA, g_imageWidth, g_imageHeight, 0,
          GL2.GL_RGBA, GL2.GL_FLOAT, null);

      gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_frontFboId[i]);
      gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
          GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[i], 0);
      gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
          GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontColorTexId[i], 0);
    }

    gl.glGenTextures(1, g_frontColorBlenderTexId, 0);
    gl.glBindTexture(GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontColorBlenderTexId[0]);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_RECTANGLE_ARB, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexImage2D(GL2.GL_TEXTURE_RECTANGLE_ARB, 0, GL2.GL_RGBA, g_imageWidth, g_imageHeight, 0,
        GL2.GL_RGBA, GL2.GL_FLOAT, null);

    gl.glGenFramebuffers(1, g_frontColorBlenderFboId, 0);
    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontDepthTexId[0], 0);
    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
        GL2.GL_TEXTURE_RECTANGLE_ARB, g_frontColorBlenderTexId[0], 0);
  }

  protected void deleteFrontPeelingRenderTargets(GL2 gl) {
    gl.glDeleteFramebuffers(2, g_frontFboId, 0);
    gl.glDeleteFramebuffers(1, g_frontColorBlenderFboId, 0);
    gl.glDeleteTextures(2, g_frontDepthTexId, 0);
    gl.glDeleteTextures(2, g_frontColorTexId, 0);
    gl.glDeleteTextures(1, g_frontColorBlenderTexId, 0);
  }

  protected void doRender(IPainter painter, GL2 gl) {
    // ---------------------------------------------------------------------
    // 1. Initialize Min Depth Buffer
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
    gl.glDrawBuffer(g_drawBuffers[0]);

    gl.glClearColor(0, 0, 0, 1);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    gl.glEnable(GL2.GL_DEPTH_TEST);

    glslInit.bind(gl);
    glslInit.setUniform(gl, "Alpha", g_opacity, 1);

    tasksToRender(painter);

    glslInit.unbind(gl);

    // ---------------------------------------------------------------------
    // 2. Depth Peeling + Blending
    // ---------------------------------------------------------------------

    int numLayers = (g_numPasses - 1) * 2;
    for (int layer = 1; g_useOQ || layer < numLayers; layer++) {
      int currId = layer % 2;
      int prevId = 1 - currId;

      gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_frontFboId[currId]);
      gl.glDrawBuffer(g_drawBuffers[0]);

      gl.glClearColor(0, 0, 0, 0);
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

      gl.glDisable(GL2.GL_BLEND);
      gl.glEnable(GL2.GL_DEPTH_TEST);

      if (g_useOQ) {
        gl.glBeginQuery(GL2.GL_SAMPLES_PASSED, g_queryId[0]);
      }

      glslPeel.bind(gl);
      glslPeel.bindTextureRECT(gl, "DepthTex", g_frontDepthTexId[prevId], 0);
      glslPeel.setUniform(gl, "Alpha", g_opacity, 1);

      tasksToRender(painter);

      glslPeel.unbind(gl);

      if (g_useOQ) {
        gl.glEndQuery(GL2.GL_SAMPLES_PASSED);
      }

      gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_frontColorBlenderFboId[0]);
      gl.glDrawBuffer(g_drawBuffers[0]);

      gl.glDisable(GL2.GL_DEPTH_TEST);
      gl.glEnable(GL2.GL_BLEND);

      gl.glBlendEquation(GL2.GL_FUNC_ADD);
      gl.glBlendFuncSeparate(GL2.GL_DST_ALPHA, GL2.GL_ONE, GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);

      glslBlend.bind(gl);
      glslBlend.bindTextureRECT(gl, "TempTex", g_frontColorTexId[currId], 0);
      gl.glCallList(g_quadDisplayList);
      glslBlend.unbind(gl);

      gl.glDisable(GL2.GL_BLEND);

      if (g_useOQ) {
        int[] sample_count = new int[] {0};
        gl.glGetQueryObjectuiv(g_queryId[0], GL2.GL_QUERY_RESULT, sample_count, 0);
        if (sample_count[0] == 0) {
          break;
        }
      }
    }

    // ---------------------------------------------------------------------
    // 3. Final Pass
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    gl.glDrawBuffer(GL2.GL_BACK);
    gl.glDisable(GL2.GL_DEPTH_TEST);

    glslFinal.bind(gl);
    glslFinal.setUniform(gl, "BackgroundColor", g_backgroundColor, 3);
    glslFinal.bindTextureRECT(gl, "ColorTex", g_frontColorBlenderTexId[0], 0);
    gl.glCallList(g_quadDisplayList);
    glslFinal.unbind(gl);
  }
}
