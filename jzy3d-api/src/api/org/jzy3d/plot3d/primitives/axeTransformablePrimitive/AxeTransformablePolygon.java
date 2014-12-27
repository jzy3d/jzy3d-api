package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import java.awt.Polygon;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

public class AxeTransformablePolygon extends AxeTransformableAbstractGeometry {
	
	
	
	 public AxeTransformablePolygon(AxeTransformerSet transformers) {
	        super(transformers);
	   }

	    protected void begin(GL gl) {
	        if (gl.isGL2()) {
	            gl.getGL2().glBegin(GL2.GL_POLYGON);
	        } else {
	            GLES2CompatUtils.glBegin(GL2.GL_POLYGON);
	        }
	    }
}
