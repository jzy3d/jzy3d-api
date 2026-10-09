package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;


public class WeightedAveragePeelingAlgorithm extends AbstractAccumulationDepthPeeling
    implements IDepthPeelingAlgorithm {
  public GLSLProgram glslInit;
  public GLSLProgram glslFinal;

  protected ShaderFilePair shaderBase = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "shade_vertex.glsl", "shade_fragment.glsl");
  protected ShaderFilePair shaderInit = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "wavg_init_vertex.glsl", "wavg_init_fragment.glsl");
  protected ShaderFilePair shaderFinal = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "wavg_final_vertex.glsl", "wavg_final_fragment.glsl");

  public WeightedAveragePeelingAlgorithm() {
    super();
  }

  @Override
  public void display(IPainter painter) {
    resetNumPass();
    doRender(painter);
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
    glslFinal.destroy(painter);
  }

  protected void doRender(IPainter painter) {
    painter.glDisable(GLConstants.GL_DEPTH_TEST);

    // ---------------------------------------------------------------------
    // 1. Accumulate Colors and Depth Complexity
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_accumulationFboId[0]);
    painter.glDrawBuffers(2, g_drawBuffers, 0);

    painter.glClearColor(0, 0, 0, 0);
    painter.glClear(GLConstants.GL_COLOR_BUFFER_BIT);

    painter.glBlendEquation(GLConstants.GL_FUNC_ADD);
    painter.glBlendFunc(GLConstants.GL_ONE, GLConstants.GL_ONE);
    painter.glEnable(GLConstants.GL_BLEND);

    glslInit.bind(painter);
    glslInit.setUniform(painter, "Alpha", g_opacity, 1);

    tasksToRender(painter);

    glslInit.unbind(painter);

    painter.glDisable(GLConstants.GL_BLEND);

    // ---------------------------------------------------------------------
    // 2. Approximate Blending
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, 0);
    painter.glDrawBuffer(GLConstants.GL_BACK);

    glslFinal.bind(painter);
    glslFinal.setUniform(painter, "BackgroundColor", g_backgroundColor, 3);
    glslFinal.bindTextureRECT(painter, "ColorTex0", g_accumulationTexId[0], 0);
    glslFinal.bindTextureRECT(painter, "ColorTex1", g_accumulationTexId[1], 1);
    painter.glCallList(g_quadDisplayList);
    glslFinal.unbind(painter);
  }
}
