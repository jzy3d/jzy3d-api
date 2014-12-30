package org.jzy3d.plot3d.primitives.log;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ConcurrentScatterPoint;
import org.jzy3d.plot3d.primitives.LightPoint;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableConcuurentScatterPoint extends
		ConcurrentScatterPoint {
	
	SpaceTransformer transformers;
	
	
	public AxeTransformableConcuurentScatterPoint(SpaceTransformer transformers) {
        super();
        this.transformers = transformers;
    }

    public AxeTransformableConcuurentScatterPoint(List<LightPoint> points, float width, SpaceTransformer transformers) {
        super(points, width);
        this.transformers = transformers;
    }

    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL2.GL_POINTS);

        if (points != null) {
            synchronized (points) {
                for (LightPoint p : points) {
                    GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    GLES2CompatUtils.glVertex3f(transformers.getX().compute(p.xyz.x), transformers.getY().compute(p.xyz.y), transformers.getZ().compute(p.xyz.z));
                }
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL2.GL_POINTS);

        if (points != null) {
            synchronized (points) {
                for (LightPoint p : points) {
                    gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
                }
            }
        }
        gl.getGL2().glEnd();
    }
	
}
