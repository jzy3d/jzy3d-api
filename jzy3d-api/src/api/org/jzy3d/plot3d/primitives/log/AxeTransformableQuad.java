package org.jzy3d.plot3d.primitives.log;

import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class AxeTransformableQuad extends AxeTransformableAbstractGeometry {

	public AxeTransformableQuad(SpaceTransformer transformers) {
	    super(transformers);
	}

    @Override
    protected void begin(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glBegin(GL2.GL_QUADS);
        } else {
            GLES2CompatUtils.glBegin(GL2.GL_QUADS);
        }
    }
}
