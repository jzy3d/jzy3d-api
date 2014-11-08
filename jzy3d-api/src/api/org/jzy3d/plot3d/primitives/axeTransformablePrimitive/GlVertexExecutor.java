package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;

public abstract class GlVertexExecutor {
	public static void Vertex(GL gl, Coord3d c3d, AxeTransformer trsX, AxeTransformer trsY, AxeTransformer trsZ){
		gl.getGL2().glVertex3d(trsX.compute(c3d.x), trsY.compute(c3d.y), trsZ.compute(c3d.z));
	}
}
