package org.jzy3d.plot3d.primitives;

import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;

public class ConcurrentScatterPoint extends ScatterPoint {
    public ConcurrentScatterPoint() {
        super();
    }

    public ConcurrentScatterPoint(List<LightPoint> points, float width) {
        super(points, width);
    }
    
    @Override
	protected void doDrawPoints(Painter painter) {
		painter.glPointSize(width);
        painter.glBegin(GL.GL_POINTS);
        if (points != null) {
            synchronized (points) {
	            for (LightPoint p : points) {
	                painter.color(p.rgb);
	                painter.vertex(p.xyz, spaceTransformer);
	
	            }
            }
        }
        painter.glEnd();
	}

    @Override
    public void applyGeometryTransform(Transform transform) {
        synchronized (points) {
            for (LightPoint p : points) {
                Coord3d c = p.xyz;
                c.set(transform.compute(c));
            }
        }
        updateBounds();
    }

    @Override
    public void add(LightPoint point) {
        synchronized (points) {
            this.points.add(point);
        }
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        synchronized (points) {
            for (LightPoint c : points)
                bbox.add(c.xyz);
        }
    }
}