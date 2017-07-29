package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

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

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);
        drawLine(gl);
        drawPoints(gl);
        // gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
    }

    public void drawLine(GL gl) {
        gl.glLineWidth(width);
        // gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        // gl.glPolygonOffset(1.0f, 1.0f);
        if (gl.isGL2()) {
            drawLineGL2(gl);
        } else {
            drawLineGLES2();
        }
    }

    public void drawLineGLES2() {
        GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);

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
        GLES2CompatUtils.glEnd();
    }

    public void drawLineGL2(GL gl) {
        gl.getGL2().glBegin(GL.GL_LINE_STRIP);

        
        if(stipple){
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            gl.glEnable(GL2.GL_LINE_STIPPLE);
            gl.getGL2().glLineStipple(1, (short) 0xAAAA);
        }
        
        gl.getGL2().glLineWidth(wfwidth);
        
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
        gl.getGL2().glEnd();
    }

    public void drawPoints(GL gl) {
        if (showPoints) {
            if (gl.isGL2()) {
                drawPointsGL2(gl);
            } else {
                drawPointsGLES2();
            }
        }
    }

    public void drawPointsGLES2() {
        GLES2CompatUtils.glBegin(GL.GL_POINTS);

        for (Point p : points) {
            if (wfcolor == null)
                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
            else
                GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }

        GLES2CompatUtils.glEnd();
    }

    public void drawPointsGL2(GL gl) {
        gl.getGL2().glBegin(GL.GL_POINTS);

        gl.getGL2().glPointSize(wfwidth);
        
        for (Point p : points) {
            if (wfcolor == null)
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
            else
                gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }

        gl.getGL2().glEnd();
    }

    /* */

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (Point p : points) {
            p.xyz = transform.compute(p.xyz);
        }
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        for (Point p : points)
            bbox.add(p);
    }

    public void add(Point point) {
        points.add(point);
        bbox.add(point);
    }

    public void add(Coord3d coord3d) {
        add(new Point(coord3d));
    }

    public void addAll(List<Point> points) {
        for (Point p : points)
            add(p);
    }

    public void addAll(LineStrip strip) {
        addAll(strip.getPoints());
    }
    
    public void clear(){
        points.clear();
        updateBounds();
    }

    public Point get(int p) {
        return points.get(p);
    }
    
    public Point getLastPoint(){
        int last = points.size()-1;
        if(last>=0)
            return points.get(last);
        return null;
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
    
    public boolean isStipple() {
        return stipple;
    }

    public void setStipple(boolean stipple) {
        this.stipple = stipple;
    }

    @Override
    public double getDistance(Camera camera) {
        return getBarycentre().distance(camera.getEye());
    }

    @Override
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

    @Override
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

    @Override
    public String toString(int depth) {
        return (Utils.blanks(depth) + "(LineStrip) #points:" + points.size());
    }

    /**********************************************************************/

    protected List<Point> points;
    protected float width;
    protected boolean showPoints = false;
    protected boolean stipple = false;
}
