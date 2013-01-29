package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

/**
 * Color works as follow:
 * <ul>
 * <li>If wireframe color is null (default), uses each point color and performs
 * color interpolation
 * <li>Otherwise apply a uniform wireframe color.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class LineStrip extends AbstractWireframeable {
    public LineStrip() {
        this(2);
    }

    public LineStrip(int n) {
        points = new ArrayList<Point>(n);
        bbox = new BoundingBox3d();
        for (Point p : points)
            bbox.add(p);
        setWireframeColor(null);
    }

    public LineStrip(List<Coord3d> coords) {
        this();
        for (Coord3d c : coords) {
            Point p = new Point(c);
            add(p);
        }
    }

    public LineStrip(Point c1, Point c2) {
        this();
        add(c1);
        add(c2);
    }

    /* */

    public void draw(GL2 gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);
        drawLine(gl);
        drawPoints(gl);
        // gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
    }

    public void drawLine(GL2 gl) {
        gl.glLineWidth(width);
        // gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        // gl.glPolygonOffset(1.0f, 1.0f);
        gl.glBegin(GL.GL_LINE_STRIP);

        if (wfcolor == null) {
            for (Point p : points) {
                gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        } else {
            for (Point p : points) {
                gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
        }
        gl.glEnd();
    }

    public void drawPoints(GL2 gl) {
        if (showPoints) {
            gl.glBegin(GL.GL_POINTS);
            
            for (Point p : points) {
                if (wfcolor == null)
                    gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
                else
                    gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
                gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
            }
            
            gl.glEnd();
        }
    }

    /* */
    
    public void applyGeometryTransform(Transform transform){
        for(Point p: points){
            p.xyz = transform.compute(p.xyz);
        }
        updateBounds();
    }

    public void updateBounds() {
        bbox.reset();
        for (Point p : points)
            bbox.add(p);
    }

    public void add(Point point) {
        points.add(point);
        bbox.add(point);
    }

    public void addAll(List<Point> points) {
        for (Point p : points)
            add(p);
    }

    public void addAll(LineStrip strip) {
        addAll(strip.getPoints());
    }

    public Point get(int p) {
        return points.get(p);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public boolean isShowPoints() {
        return showPoints;
    }

    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public double getDistance(Camera camera) {
        return getBarycentre().distance(camera.getEye());
    }

    public double getShortestDistance(Camera camera) {
        double min = Float.MAX_VALUE;
        double dist = 0;
        for (Point point : points) {
            dist = point.getDistance(camera);
            if (dist < min)
                min = dist;
        }
        return min;
    }

    public double getLongestDistance(Camera camera) {
        double max = 0;
        double dist = 0;
        for (Point point : points) {
            dist = point.getDistance(camera);
            if (dist < max)
                max = dist;
        }
        return max;
    }

    /* */

    /**
     * Merge lines by selecting the most relevant connection point: A-B to C-D
     * if distance BC is shorter than distance DA C-D to A-B
     */
    public static LineStrip merge(LineStrip strip1, LineStrip strip2) {
        Coord3d a = strip1.get(0).xyz;
        Coord3d b = strip1.get(strip1.size() - 1).xyz;
        Coord3d c = strip2.get(0).xyz;
        Coord3d d = strip2.get(strip2.size() - 1).xyz;

        double bc = b.distance(c);
        double da = d.distance(a);

        if (bc > da) {
            strip1.addAll(strip2);
            return strip1;
        } else {
            strip2.addAll(strip1);
            return strip2;
        }
    }

    /**********************************************************************/

    public String toString(int depth) {
        return (Utils.blanks(depth) + "(LineStrip) #points:" + points.size());
    }

    /**********************************************************************/

    protected List<Point> points;
    protected float width;
    protected boolean showPoints = false;
}
