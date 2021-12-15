package org.jzy3d.plot3d.rendering.ddp.algorithms;

import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.IPainter;
import com.jogamp.opengl.GL2;


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
    doRender(painter, getGL(painter));
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
    glslFinal.destroy(gl);
  }

  protected void doRender(IPainter painter, GL2 gl) {
    gl.glDisable(GL2.GL_DEPTH_TEST);

    // ---------------------------------------------------------------------
    // 1. Accumulate Colors and Depth Complexity
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, g_accumulationFboId[0]);
    gl.glDrawBuffers(2, g_drawBuffers, 0);

    gl.glClearColor(0, 0, 0, 0);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

    gl.glBlendEquation(GL2.GL_FUNC_ADD);
    gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
    gl.glEnable(GL2.GL_BLEND);

    glslInit.bind(gl);
    glslInit.setUniform(gl, "Alpha", g_opacity, 1);

    tasksToRender(painter);

    glslInit.unbind(gl);

    gl.glDisable(GL2.GL_BLEND);

    // ---------------------------------------------------------------------
    // 2. Approximate Blending
    // ---------------------------------------------------------------------

    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
    gl.glDrawBuffer(GL2.GL_BACK);

    glslFinal.bind(gl);
    glslFinal.setUniform(gl, "BackgroundColor", g_backgroundColor, 3);
    glslFinal.bindTextureRECT(gl, "ColorTex0", g_accumulationTexId[0], 0);
    glslFinal.bindTextureRECT(gl, "ColorTex1", g_accumulationTexId[1], 1);
    gl.glCallList(g_quadDisplayList);
    glslFinal.unbind(gl);
  }
}
