package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * Experimental 3d object.
 * 
 * @author Martin Pernollet
 * 
 */
public class ScatterPoint extends AbstractDrawable implements ISingleColorable {

    public ScatterPoint() {
        bbox = new BoundingBox3d();
        setWidth(1.0f);
        setPoints(new ArrayList<LightPoint>());
    }

    public ScatterPoint(List<LightPoint> points, float width) {
        bbox = new BoundingBox3d();
        setPoints(points);
        setWidth(width);
    }

    public void clear() {
        points.clear();;
        bbox.reset();
    }

    /* */

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (gl.isGL2()) {
            drawGL2(gl);
        } else {
            drawGLES2();
        }

        doDrawBounds(gl, glu, cam);
    }

    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL.GL_POINTS);
        if (points != null) {
            for (LightPoint p : points) {
                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL.GL_POINTS);
        if (points != null) {
            for (LightPoint p : points) {
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
        gl.getGL2().glEnd();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (LightPoint p : points) {
            Coord3d c = p.xyz;
            c.set(transform.compute(c));
        }
        updateBounds();
    }

    /* */

    public void add(LightPoint point) {
        this.points.add(point);
        updateBounds();
    }

    public void setPoints(List<LightPoint> points) {
        this.points = points;
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        for (LightPoint c : points)
            bbox.add(c.xyz);
    }

    public List<LightPoint> getData() {
        return points;
    }

    @Override
    public void setColor(Color color) {
    }

    @Override
    public Color getColor() {
        return null;
    }

    /**
     * Set the width of the point.
     * 
     * @param width
     *            point's width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    public List<LightPoint> points;
    public float width;
}
