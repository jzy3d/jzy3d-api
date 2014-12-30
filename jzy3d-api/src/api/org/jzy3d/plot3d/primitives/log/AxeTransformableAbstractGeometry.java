package org.jzy3d.plot3d.primitives.log;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.log.transformers.LogTransformer;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

public abstract class AxeTransformableAbstractGeometry extends AbstractGeometry{
	
	public AxeTransformableAbstractGeometry(LogTransformer transformers) {
		super();
		this.logTransformer = transformers;
	}

	protected void vertexGL2(GL gl, Coord3d c) {
        GlVertexExecutor.Vertex(gl, c, logTransformer);
    }
	
	protected void vertexGLES2(Coord3d c) {
		GLES2CompatUtils.glVertex3f(logTransformer.getX().compute(c.x), logTransformer.getY().compute(c.y),logTransformer.getZ().compute(c.z));
	}
}
