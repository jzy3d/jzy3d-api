package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

public abstract class AbstractGeometry extends AbstractWireframeable implements ISingleColorable, IMultiColorable {
    public enum PolygonMode {
        FRONT, BACK, FRONT_AND_BACK
    }

    /**
     * Initializes an empty {@link AbstractGeometry} with face status defaulting
     * to true, and wireframe status defaulting to false.
     */
    public AbstractGeometry() {
        super();
        points = new ArrayList<Point>(4); // use Vector for synchro, or
                                          // ArrayList for unsyncro.
        bbox = new BoundingBox3d();
        center = new Coord3d();
        polygonOffsetFillEnable = true;
        polygonMode = PolygonMode.FRONT_AND_BACK;
    }

    /* * */

    public void draw(GL gl, GLU glu, Camera cam) {
        doTransform(gl, glu, cam);

        if (mapper != null)
            mapper.preDraw(this);

        // Draw content of polygon
        if (facestatus) {
            applyPolygonModeFill(gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffseFillEnable(gl);
            callPointsForFace(gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffsetFillDisable(gl);
        }

        // Draw edge of polygon
        if (wfstatus) {
            applyPolygonModeLine(gl);
            if (polygonOffsetFillEnable)
                polygonOffseFillEnable(gl);
            callPointForWireframe(gl);
            if (polygonOffsetFillEnable)
                polygonOffsetFillDisable(gl);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBounds(gl, glu, cam);
    }

    protected void callPointForWireframe(GL gl) {
        if (gl.isGL2()) {
            callPointsForWireframeGL2(gl);
        }
    }

    public void callPointsForWireframeGL2(GL gl) {
        gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
        gl.glLineWidth(wfwidth);

        begin(gl);
        for (Point p : points) {
            gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }
        end(gl);
    }

    protected void callPointsForFace(GL gl) {
        if (gl.isGL2()) {
            callPointsForFaceGL2(gl);
        } else {
            callPointsForFaceGLES2(gl);
        }
    }

    public void callPointsForFaceGLES2(GL gl) {
        callPointsForFaceGLES2(gl, points);
    }

    public void callPointsForFaceGLES2(GL gl, List<Point> points) {
        begin(gl);
        for (Point p : points) {
            if (mapper != null) {
                Color c = mapper.getColor(p.xyz); // TODO: should cache
                                                  // result
                                                  // in the point color
                GLES2CompatUtils.glColor4f(c.r, c.g, c.b, c.a);
            } else {
                GLES2CompatUtils.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
            }

            GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }
        end(gl);
    }

    public void callPointsForFaceGL2(GL gl) {
        begin(gl);
        for (Point p : points) {
            if (mapper != null) {
                Color c = mapper.getColor(p.xyz); // TODO: should cache
                                                  // result
                                                  // in the point color
                gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
            } else {
                gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
            }

            gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }
        end(gl);
    }

    protected abstract void begin(GL gl);

    protected void end(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glEnd();
        } else {
            GLES2CompatUtils.glEnd();
        }
    }

    protected void applyPolygonModeLine(GL gl) {
        if (gl.isGL2()) {
            switch (polygonMode) {
            case FRONT:
                gl.getGL2().glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
                break;
            case BACK:
                gl.getGL2().glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
                break;
            case FRONT_AND_BACK:
                gl.getGL2().glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
                break;
            default:
                break;
            }
        } else {
            // glPolygonMode does not exist in opengl es ??

            switch (polygonMode) {
            case FRONT:
                GLES2CompatUtils.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
                break;
            case BACK:
                GLES2CompatUtils.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
                break;
            case FRONT_AND_BACK:
                GLES2CompatUtils.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
                break;
            default:
                break;
            }
        }
    }

    protected void applyPolygonModeFill(GL gl) {
        if (gl.isGL2()) {
            switch (polygonMode) {
            case FRONT:
                gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);
                break;
            case BACK:
                gl.getGL2().glPolygonMode(GL.GL_BACK, GL2.GL_FILL);
                break;
            case FRONT_AND_BACK:
                gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
                break;
            default:
                break;
            }
        } else {

            // glPolygonMode does not exist in opengl es ??
            switch (polygonMode) {
            case FRONT:
                GLES2CompatUtils.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
                break;
            case BACK:
                GLES2CompatUtils.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
                break;
            case FRONT_AND_BACK:
                GLES2CompatUtils.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
                break;
            default:
                break;
            }

        }
    }

    protected void polygonOffseFillEnable(GL gl) {
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(1.0f, 1.0f);
    }

    protected void polygonOffsetFillDisable(GL gl) {
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
    }

    /* DATA */

    public void add(Point point) {
        add(point, true);
    }

    /** Add a point to the polygon. */
    public void add(Point point, boolean updateBounds) {
        points.add(point);
        if (updateBounds) {
            updateBounds();
        }
    }

    public void applyGeometryTransform(Transform transform) {
        for (Point p : points) {
            p.xyz = transform.compute(p.xyz);
        }
        updateBounds();
    }

    public void updateBounds() {
        bbox.reset();
        bbox.add(getPoints());

        // recompute center
        center = new Coord3d();
        for (Point p : points)
            center = center.add(p.xyz);
        center = center.div(points.size());
    }

    @Override
    public Coord3d getBarycentre() {
        return center;
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

    /* DISTANCES */

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

        dist = getBarycentre().distance(camera.getEye());
        if (dist < min)
            min = dist;
        return min;
    }

    public double getLongestDistance(Camera camera) {
        double max = 0;
        double dist = 0;
        for (Point point : points) {
            dist = point.getDistance(camera);
            if (dist > max)
                max = dist;
        }
        return max;
    }

    /* SETTINGS */

    public PolygonMode getPolygonMode() {
        return polygonMode;
    }

    /**
     * A null polygonMode imply no any call to gl.glPolygonMode(...) at
     * rendering
     */
    public void setPolygonMode(PolygonMode polygonMode) {
        this.polygonMode = polygonMode;
    }

    public boolean isPolygonOffsetFillEnable() {
        return polygonOffsetFillEnable;
    }

    /**
     * Enable offset fill, which let a polygon with a wireframe render cleanly
     * without weird depth incertainty between face and border.
     * 
     * Default value is true.
     */
    public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
        this.polygonOffsetFillEnable = polygonOffsetFillEnable;
    }

    /**
     * A utility to change polygon offset fill status of a
     * {@link AbstractComposite} containing {@link AbstractGeometry}s.
     * 
     * @param composite
     * @param polygonOffsetFillEnable
     *            status
     */
    public static void setPolygonOffsetFillEnable(AbstractComposite composite, boolean polygonOffsetFillEnable) {
        for (AbstractDrawable d : composite.getDrawables()) {
            if (d instanceof AbstractGeometry) {
                ((AbstractGeometry) d).setPolygonOffsetFillEnable(polygonOffsetFillEnable);
            } else if (d instanceof AbstractComposite) {
                setPolygonOffsetFillEnable(((AbstractComposite) d), polygonOffsetFillEnable);
            }
        }
    }

    /* COLOR */

    public void setColorMapper(ColorMapper mapper) {
        this.mapper = mapper;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    public ColorMapper getColorMapper() {
        return mapper;
    }

    public void setColor(Color color) {
        this.color = color;

        for (Point p : points)
            p.setColor(color);

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    public Color getColor() {
        return color;
    }

    public String toString(int depth) {
        return (Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ") #points:" + points.size());
    }

    /* */

    protected PolygonMode polygonMode;
    protected boolean polygonOffsetFillEnable = true;

    protected ColorMapper mapper;
    protected List<Point> points;
    protected Color color;
    protected Coord3d center;
}
