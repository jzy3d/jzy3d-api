package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;


public class WeightedSumPeelingAlgorithm extends AbstractAccumulationDepthPeeling
    implements IDepthPeelingAlgorithm {
  public GLSLProgram glslInit;
  public GLSLProgram glslFinal;

  protected ShaderFilePair shaderBase = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "shade_vertex.glsl", "shade_fragment.glsl");
  protected ShaderFilePair shaderInit = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "wsum_init_vertex.glsl", "wsum_init_fragment.glsl");
  protected ShaderFilePair shaderFinal = new ShaderFilePair(WeightedAveragePeelingAlgorithm.class,
      "wsum_final_vertex.glsl", "wsum_final_fragment.glsl");

  public WeightedSumPeelingAlgorithm() {
    super();
  }

  @Override
  public void display(IPainter painter) {
    saveTargetFramebuffer(painter);
    resetNumPass();
    doRender(painter);
  }

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
    glslFinal.loadAndCompileVertexShader(painter, shaderFinal.getVertexURL());
    glslFinal.loadAndCompileFragmentShader(painter, shaderFinal.getFragmentURL());
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
    // 1. Accumulate (alpha * color) and (alpha)
    // ---------------------------------------------------------------------

    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, g_accumulationFboId[0]);
    painter.glDrawBuffer(g_drawBuffers[0]);

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
    // 2. Weighted Sum
    // ---------------------------------------------------------------------

    bindTargetFramebufferAndDrawBuffer(painter);

    glslFinal.bind(painter);
    glslFinal.setUniform(painter, "BackgroundColor", g_backgroundColor, 3);
    glslFinal.bindTextureRECT(painter, "ColorTex", g_accumulationTexId[0], 0);
    painter.glCallList(g_quadDisplayList);
    glslFinal.unbind(painter);
  }
}
