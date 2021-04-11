package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.jzy3d.io.IGLLoader;
import com.jogamp.opengl.GL;

public class ScatterVBO extends DrawableVBO {

  public ScatterVBO(IGLLoader<DrawableVBO> loader) {
    super(loader);
    geometry = GL.GL_POINTS;
  }
}
