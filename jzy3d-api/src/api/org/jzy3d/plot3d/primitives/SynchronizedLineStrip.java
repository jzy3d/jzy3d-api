package org.jzy3d.plot3d.primitives;

import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.Transform;

public class SynchronizedLineStrip extends LineStrip {

    public SynchronizedLineStrip() {
        super(100);
    }

    public SynchronizedLineStrip(int n) {
        super(n);
    }

    public SynchronizedLineStrip(List<Coord3d> coords) {
        super(coords);
    }

    public SynchronizedLineStrip(Point c1, Point c2) {
        super(c1, c2);
    }

    public void drawLine(GL gl) {
        gl.glLineWidth(width);
        // gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        // gl.glPolygonOffset(1.0f, 1.0f);
        if (gl.isGL2()) {
            gl.getGL2().glBegin(GL.GL_LINE_STRIP);

            synchronized (points) {
                if (wfcolor == null) {
                    for (Point p : points) {
                        gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                        gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                } else {
                    for (Point p : points) {
                        gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                        gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                }
            }
            gl.getGL2().glEnd();
        } else {
            GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);

            synchronized (points) {

                if (wfcolor == null) {
                    for (Point p : points) {
                        GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                        GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                } else {
                    for (Point p : points) {
                        GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                        GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                }
            }
            GLES2CompatUtils.glEnd();
        }
    }

    public void drawPoints(GL gl) {
        if (showPoints) {
            if (gl.isGL2()) {
                gl.getGL2().glBegin(GL.GL_POINTS);
                synchronized (points) {

                    for (Point p : points) {
                        if (wfcolor == null)
                            gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                        else
                            gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                        gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                }

                gl.getGL2().glEnd();
            } else {
                GLES2CompatUtils.glBegin(GL.GL_POINTS);

                synchronized (points) {

                    for (Point p : points) {
                        if (wfcolor == null)
                            GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                        else
                            GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                        GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
                    }
                }
                GLES2CompatUtils.glEnd();
            }
        }
    }

    /* */

    public void applyGeometryTransform(Transform transform) {
        synchronized (points) {

            for (Point p : points) {
                p.xyz = transform.compute(p.xyz);
            }
        }
        updateBounds();
    }

    public void updateBounds() {
        bbox.reset();
        synchronized (points) {

            for (Point p : points)
                bbox.add(p);
        }
    }

    public void add(Point point) {
        synchronized (points) {
            points.add(point);
        }
        bbox.add(point);
    }
}
