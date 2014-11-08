package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractGeometry;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

public abstract class AxeTransformableAbstractGeometry extends AbstractGeometry{
	
	AxeTransformer transformerX;
	AxeTransformer transformerY;
	AxeTransformer transformerZ;
	
	
	
	public AxeTransformableAbstractGeometry(AxeTransformer transformerX,
			AxeTransformer transformerY, AxeTransformer transformerZ) {
		super();
		this.transformerX = transformerX;
		this.transformerY = transformerY;
		this.transformerZ = transformerZ;
	}

	protected void vertexGL2(GL gl, Coord3d c) {
        GlVertexExecutor.Vertex(gl, c, transformerX, transformerY, transformerZ);
    }
	
	protected void vertexGL2(Coord3d c) {
		GLES2CompatUtils.glVertex3f(transformerX.compute(c.x), transformerY.compute(c.y),transformerZ.compute(c.z));
	}
	
}
