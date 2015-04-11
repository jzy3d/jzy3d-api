package org.jzy3d.plot3d.primitives.vbo.drawable;

import javax.media.opengl.GL;

import org.jzy3d.io.IGLLoader;

public class BarVBO extends DrawableVBO{
    public BarVBO(IGLLoader<DrawableVBO> loader) {
        super(loader);
        geometry = GL.GL_LINES;
    }
}
