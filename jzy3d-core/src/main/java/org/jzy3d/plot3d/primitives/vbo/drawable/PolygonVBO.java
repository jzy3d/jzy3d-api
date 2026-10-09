package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.jzy3d.io.IGLLoader;
import org.jzy3d.painters.GLConstants;

public class PolygonVBO extends DrawableVBO {

  public PolygonVBO(IGLLoader<DrawableVBO> loader) {
    super(loader);
    geometry = GLConstants.GL_POLYGON;
  }
}
