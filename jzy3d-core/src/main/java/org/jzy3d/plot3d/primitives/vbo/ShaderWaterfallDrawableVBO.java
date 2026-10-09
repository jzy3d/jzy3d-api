package org.jzy3d.plot3d.primitives.vbo;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.ShaderFilePair;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

public class ShaderWaterfallDrawableVBO extends DrawableVBO implements IMultiColorable {

  private ColorMapper mapper;

  protected int elementName2[] = new int[1];

  public ShaderWaterfallDrawableVBO(ShaderWaterfallVBOBuilder loader, ColorMapper mapper) {
    super(loader);
    this.mapper = mapper;
    this.setGeometry(GLConstants.GL_QUADS);
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
    shaderProgram.setUniform(painter, "min_max",
        new float[] {(float) mapper.getMin(), (float) mapper.getMax(), (float) 0,}, 3);
    int idc = painter.glGetUniformLocation(shaderProgram.getProgramId(), "transfer");
    painter.glUniform1i(idc, 1);
    this.setGeometry(GLConstants.GL_LINES);
    super.draw(painter);
    // this.setGeometry(GLConstants.GL_LINES);
    // bindSecondIndices(painter);
    // super.draw(painter, glu, cam);
    shaderProgram.unbind(painter);
    painter.glEnable(GLConstants.GL_BLEND);

    if (disposed) {
      painter.glDeleteBuffers(1, arrayName, 0);
      painter.glDeleteBuffers(1, elementName, 0);
      return;
    }
  }

  protected void applyVertices(IPainter painter) {
    painter.glDrawElements(GLConstants.GL_LINES,
        ((ShaderWaterfallVBOBuilder) loader).getOutlineIndexSize(), GLConstants.GL_UNSIGNED_INT, 0);
    // painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_LINE);
    shaderProgram.setUniform(painter, "min_max",
        new float[] {(float) mapper.getMin(), (float) mapper.getMax(), (float) 1,}, 3);
    painter.glDrawElements(GLConstants.GL_TRIANGLES,
        ((ShaderWaterfallVBOBuilder) loader).getFillIndexSize(), GLConstants.GL_UNSIGNED_INT,
        ((ShaderWaterfallVBOBuilder) loader).getOutlineIndexSize() * 4);
    doBindGL2(painter);
  }

  @Override
  public void mount(IPainter painter) {
    try {
      loader.load(painter, this);
      hasMountedOnce = true;
      shaderProgram = new GLSLProgram();
      ShaderFilePair sfp = new ShaderFilePair(this.getClass(), "colour_mapped_waterfall.vert",
          "colour_mapped_waterfall.frag");
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
