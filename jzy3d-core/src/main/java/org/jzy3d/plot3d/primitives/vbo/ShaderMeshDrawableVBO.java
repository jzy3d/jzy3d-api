package org.jzy3d.plot3d.primitives.vbo;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

public class ShaderMeshDrawableVBO extends DrawableVBO implements IMultiColorable {

  private ColorMapper mapper;

  public ShaderMeshDrawableVBO(ShaderMeshVBOBuilder loader, ColorMapper mapper) {
    super(loader);
    this.mapper = mapper;
    this.setGeometry(GLConstants.GL_TRIANGLES);
  }

  boolean disposed = false;
  private GLSLProgram shaderProgram;
  private ColormapTexture colormapTexure;

  @Override
  public void draw(IPainter painter) {

    if (!hasMountedOnce) {
      mount(painter);
      this.doSetBoundingBox(this.getBounds());
    }

    colormapTexure.update(painter);

    painter.glDisable(GLConstants.GL_BLEND);
    shaderProgram.bind(painter);
    shaderProgram.setUniform(painter, "min_max", new float[] {(float) mapper.getMin(),
        (float) mapper.getMax(), (float) mapper.getMin(), (float) mapper.getMax()}, 4);
    int idc = painter.glGetUniformLocation(shaderProgram.getProgramId(), "transfer");
    painter.glUniform1i(idc, 1);
    super.draw(painter);
    shaderProgram.unbind(painter);
    painter.glEnable(GLConstants.GL_BLEND);

    if (disposed) {
      painter.glDeleteBuffers(1, arrayName, 0);
      painter.glDeleteBuffers(1, elementName, 0);
      return;
    }
  }

  @Override
  public void mount(IPainter painter) {

    try {
      loader.load(painter, this);
      hasMountedOnce = true;
      shaderProgram = new GLSLProgram();
      ShaderFilePair sfp = new ShaderFilePair(this.getClass(), "colour_mapped_surface.vert",
          "colour_mapped_surface.frag");
      shaderProgram.loadAndCompileShaders(painter, sfp);
      shaderProgram.link(painter);
      colormapTexure = new ColormapTexture(mapper, "transfer", shaderProgram.getProgramId());
      colormapTexure.bind(painter);
    } catch (Exception e) {
      e.printStackTrace();
      // Logger.getLogger(DrawableVBO.class).error(e, e);
    }
  }

  @Override
  public void dispose() {
    disposed = true;
  }

  protected void pointers(IPainter painter) {
    painter.glVertexPointer(dimensions, GLConstants.GL_FLOAT, 0, pointer);
    // painter.glVertexPointer(dimensions, GLConstants.GL_FLOAT, byteOffset, pointer);
    // painter.glNormalPointer(GLConstants.GL_FLOAT, byteOffset, normalOffset);
  }

  protected void configure(IPainter painter) {
    // painter.glPolygonMode(GLConstants.GL_FRONT, GLConstants.GL_FILL);
    // painter.glPolygonMode(GLConstants.GL_FRONT, GLConstants.GL_LINE);
    // painter.glColor4f(1f,0f,1f,0.6f);
    // painter.glLineWidth(0.00001f);

    painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);
    painter.color(color);
  }

  @Override
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;
    if (colormapTexure != null)
      colormapTexure.updateColormap(mapper);

  }



}
