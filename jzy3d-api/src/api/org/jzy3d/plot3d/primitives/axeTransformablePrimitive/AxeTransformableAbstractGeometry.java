package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

public abstract class AxeTransformableAbstractGeometry extends AbstractGeometry{
	
	AxeTransformerSet transformers;
	
	
	
	public AxeTransformableAbstractGeometry(AxeTransformerSet transformers) {
		super();
		this.transformers = transformers;
	}

	protected void vertexGL2(GL gl, Coord3d c) {
        GlVertexExecutor.Vertex(gl, c, transformers);
    }
	
	protected void vertexGL2(Coord3d c) {
		GLES2CompatUtils.glVertex3f(transformers.getX().compute(c.x), transformers.getY().compute(c.y),transformers.getZ().compute(c.z));
	}
	
}
