package org.jzy3d.plot3d.primitives;

import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
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
    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL.GL_POINTS);

        if (points != null) {
            synchronized (points) {
                for (LightPoint p : points) {
                    GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }
            }
        }
        GLES2CompatUtils.glEnd();
    }

    @Override
    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL.GL_POINTS);

        if (points != null) {
            synchronized (points) {
                for (LightPoint p : points) {
                    gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                    gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                }
            }
        }
        gl.getGL2().glEnd();
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