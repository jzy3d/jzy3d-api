package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

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
        points = new ArrayList<Point>(4); 
        bbox = new BoundingBox3d();
        center = new Coord3d();
        polygonOffsetFillEnable = true;
        polygonMode = PolygonMode.FRONT_AND_BACK;
    }

    /* * */

    @Override
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
            
            gl.getGL2().glBegin(GL2.GL_LINE_LOOP);
            
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

    /** Drawing the point list in wireframe mode if GL2 profile is available */
    protected void callPointForWireframe(GL gl) {
        if (gl.isGL2()) {
            callPointsForWireframeGL2(gl);
        }
    }

    /** Drawing the point list in wireframe mode */
    public void callPointsForWireframeGL2(GL gl) {
        colorGL2(gl, wfcolor);
        gl.glLineWidth(wfwidth);

        begin(gl);
        for (Point p : points) {
            vertexGL2(gl, p.xyz);
        }
        end(gl);
    }

    /** Drawing the point list in face mode (polygon content) */
    protected void callPointsForFace(GL gl) {
        if (gl.isGL2()) {
            callPointsForFaceGL2(gl);
        } else {
            callPointsForFaceGLES2(gl);
        }
    }

    /** Drawing the point list in face mode (polygon content) with GLES2 profile */
    public void callPointsForFaceGLES2(GL gl) {
        callPointsForFaceGLES2(gl, points);
    }

    /** Drawing the point list in face mode (polygon content) with GLES2 profile */
    public void callPointsForFaceGLES2(GL gl, List<Point> points) {
        begin(gl);
        for (Point p : points) {
            if (mapper != null) {
                Color c = mapper.getColor(p.xyz);
                colorGLES2(c);
            } else {
                colorGLES2(p.rgb);
            }
            vertexGLES2(p.xyz);
        }
        end(gl);
    }

    /** Drawing the point list in face mode (polygon content) with GL2 profile */
    public void callPointsForFaceGL2(GL gl) {
        begin(gl);
        for (Point p : points) {
            if (mapper != null) {
                Color c = mapper.getColor(p.xyz);
                colorGL2(gl, c);
            } else {
                colorGL2(gl, p.rgb);
            }
            vertexGL2(gl, p.xyz);
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
            applyPolygonModeLineGL2(gl);
        } else {
            applyPolygonModeLineGLES2();
        }
    }

    protected void applyPolygonModeLineGLES2() {
        switch (polygonMode) {
        case FRONT:
            GLES2CompatUtils.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_LINE);
            break;
        case BACK:
            GLES2CompatUtils.glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            break;
        case FRONT_AND_BACK:
            GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
            break;
        default:
            break;
        }
    }

    protected void applyPolygonModeLineGL2(GL gl) {
        switch (polygonMode) {
        case FRONT:
            gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2GL3.GL_LINE);
            break;
        case BACK:
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            break;
        case FRONT_AND_BACK:
            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
            break;
        default:
            break;
        }
    }

    protected void applyPolygonModeFill(GL gl) {
        if (gl.isGL2()) {
            applyPolygonModeFillGL2(gl);
        } else {
            applyPolygonModeFillGLES2();
        }
    }

    public void applyPolygonModeFillGLES2() {
        switch (polygonMode) {
        case FRONT:
            GLES2CompatUtils.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
            break;
        case BACK:
            GLES2CompatUtils.glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
            break;
        case FRONT_AND_BACK:
            GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
            break;
        default:
            break;
        }
    }

    public void applyPolygonModeFillGL2(GL gl) {
        switch (polygonMode) {
        case FRONT:
            gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
            break;
        case BACK:
            gl.getGL2().glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
            break;
        case FRONT_AND_BACK:
            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
            break;
        default:
            break;
        }
    }

    protected void polygonOffseFillEnable(GL gl) {
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
    }

    protected void polygonOffsetFillDisable(GL gl) {
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
    }
    
    /* DATA */

    public void add(float x, float y, float z) {
        add(new Coord3d(x,y,z));
    }
    
    public void add(Coord3d coord) {
        add(new Point(coord, wfcolor), true);
    }

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

        dist = getBarycentre().distance(camera.getEye());
        if (dist < min)
            min = dist;
        return min;
    }

    @Override
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
    
    public float getPolygonOffsetFactor() {
        return polygonOffsetFactor;
    }

    public void setPolygonOffsetFactor(float polygonOffsetFactor) {
        this.polygonOffsetFactor = polygonOffsetFactor;
    }

    public float getPolygonOffsetUnit() {
        return polygonOffsetUnit;
    }

    public void setPolygonOffsetUnit(float polygonOffsetUnit) {
        this.polygonOffsetUnit = polygonOffsetUnit;
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

    @Override
    public void setColorMapper(ColorMapper mapper) {
        this.mapper = mapper;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public ColorMapper getColorMapper() {
        return mapper;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;

        for (Point p : points)
            p.setColor(color);

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString(int depth) {
        return (Utils.blanks(depth) + "(" + this.getClass().getSimpleName() + ") #points:" + points.size());
    }

    /* */

    protected PolygonMode polygonMode;
    protected boolean polygonOffsetFillEnable = true;
    protected float polygonOffsetFactor = 1.0f;
    protected float polygonOffsetUnit = 1.0f;


    protected ColorMapper mapper;
    protected List<Point> points;
    protected Color color;
    protected Coord3d center;
}
