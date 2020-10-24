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
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
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
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
        doTransform(painter, gl, glu, cam);

        if (mapper != null)
            mapper.preDraw(this);

        // Draw content of polygon
        if (facestatus) {
            applyPolygonModeFill(painter, gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffseFillEnable(painter, gl);
            callPointsForFace(painter, gl);
            if (wfstatus && polygonOffsetFillEnable)
                polygonOffsetFillDisable(painter, gl);
        }

        // Draw edge of polygon
        if (wfstatus) {
            applyPolygonModeLine(painter, gl);
            
            //gl.getGL2().glBegin(GL2.GL_LINE_LOOP);
            //gl.getGL2().glLineWidth(getWireframeWidth());
            
            if (polygonOffsetFillEnable)
                polygonOffseFillEnable(painter, gl);
            callPointForWireframe(painter, gl);
            if (polygonOffsetFillEnable)
                polygonOffsetFillDisable(painter, gl);
        }

        if (mapper != null)
            mapper.postDraw(this);

        doDrawBounds(painter, gl, glu, cam);
    }

    /** Drawing the point list in wireframe mode
     */
    protected void callPointForWireframe(Painter painter, GL gl) {
    	painter.color(wfcolor);
        painter.glLineWidth(wfwidth);

        begin(painter, gl);
        for (Point p : points) {
            painter.vertex(p.xyz, spaceTransformer);
        }
        painter.glEnd();
    }


    /** Drawing the point list in face mode (polygon content) 
     * @param painter TODO*/
    protected void callPointsForFace(Painter painter, GL gl) {
        begin(painter, gl);
        for (Point p : points) {
            if (mapper != null) {
                Color c = mapper.getColor(p.xyz);
                painter.color(c);
            } else {
                painter.color(p.rgb);
            }
            painter.vertex(p.xyz, spaceTransformer);
        }
        painter.glEnd();
    }

    protected abstract void begin(Painter painter, GL gl);


    protected void applyPolygonModeLine(Painter painter, GL gl) {
        switch (polygonMode) {
        case FRONT:
            painter.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_LINE);
            break;
        case BACK:
            painter.glPolygonMode(GL.GL_BACK, GL2GL3.GL_LINE);
            break;
        case FRONT_AND_BACK:
            painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
            break;
        default:
            break;
        }
    }

    protected void applyPolygonModeFill(Painter painter, GL gl) {
        switch (polygonMode) {
        case FRONT:
            painter.glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
            break;
        case BACK:
            painter.glPolygonMode(GL.GL_BACK, GL2GL3.GL_FILL);
            break;
        case FRONT_AND_BACK:
            painter.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
            break;
        default:
            break;
        }

    }

    protected void polygonOffseFillEnable(Painter painter, GL gl) {
        painter.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
    }

    protected void polygonOffsetFillDisable(Painter painter, GL gl) {
    	painter.glDisable(GL.GL_POLYGON_OFFSET_FILL);
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
