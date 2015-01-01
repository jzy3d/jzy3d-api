package org.jzy3d.plot3d.primitives.log;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.LightPoint;
import org.jzy3d.plot3d.primitives.ScatterPoint;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableScatterPoint extends ScatterPoint{

	SpaceTransformer transformers;
	
    public AxeTransformableScatterPoint(SpaceTransformer transformers) {
        bbox = new BoundingBox3d();
        setWidth(1.0f);
        setPoints(new ArrayList<LightPoint>());
        this.transformers = transformers;
    }

    public AxeTransformableScatterPoint(List<LightPoint> points, float width, SpaceTransformer transformers) {
        bbox = new BoundingBox3d();
        setPoints(points);
        setWidth(width);
        this.transformers = transformers;
    }

 
    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL2.GL_POINTS);
        if (points != null) {
            for (LightPoint p : points) {
                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                GLES2CompatUtils.glVertex3f(transformers.getX().compute(p.xyz.x), transformers.getY().compute(p.xyz.y), transformers.getZ().compute(p.xyz.z));
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL2.GL_POINTS);
        if (points != null) {
            for (LightPoint p : points) {
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
            }
        }
        gl.getGL2().glEnd();
    }
    
    public void updateBounds() {
        bbox.reset();
        for (LightPoint c : points)
            bbox.add(transformers.compute(c.xyz));
    }

}
