package org.jzy3d.plot3d.primitives.log;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public abstract class AxeTransformableAbstractGeometry extends AbstractGeometry{
	
	public AxeTransformableAbstractGeometry(SpaceTransformer transformers) {
		super();
		this.spaceTransformer = transformers;
	}

	protected void vertexGL2(GL gl, Coord3d c) {
        GlVertexExecutor.Vertex(gl, c, spaceTransformer);
    }
	
	protected void vertexGLES2(Coord3d c) {
		GLES2CompatUtils.glVertex3f(spaceTransformer.getX().compute(c.x), spaceTransformer.getY().compute(c.y),spaceTransformer.getZ().compute(c.z));
	}
}
