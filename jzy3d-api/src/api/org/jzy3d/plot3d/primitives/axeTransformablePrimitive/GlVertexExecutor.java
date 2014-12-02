package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;

public abstract class GlVertexExecutor {
	public static void Vertex(GL gl, Coord3d c3d,AxeTransformerSet transformers){
		gl.getGL2().glVertex3d(transformers.getX().compute(c3d.x), transformers.getY().compute(c3d.y), transformers.getZ().compute(c3d.z));
	}
}
