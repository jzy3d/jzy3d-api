package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.jzy3d.io.IGLLoader;
import org.jzy3d.painters.GLConstants;

public class TriangleStripVBO extends DrawableVBO {

  public TriangleStripVBO(IGLLoader<DrawableVBO> loader) {
    super(loader);
    geometry = GLConstants.GL_TRIANGLE_STRIP;
  }
}
