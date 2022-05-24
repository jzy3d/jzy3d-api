package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.jzy3d.io.IGLLoader;
import com.jogamp.opengl.GL2;

public class TriangleStripVBO extends DrawableVBO {

  public TriangleStripVBO(IGLLoader<DrawableVBO> loader) {
    super(loader);
    geometry = GL2.GL_TRIANGLE_STRIP;
  }
}
