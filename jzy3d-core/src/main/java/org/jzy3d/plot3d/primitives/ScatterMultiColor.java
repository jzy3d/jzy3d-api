package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * A scatter plot supporting a colormap for shading each dot color and alpha.
 * 
 * @author Martin Pernollet
 * 
 */
public class ScatterMultiColor extends AbstractDrawable implements IMultiColorable {
    public ScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper) {
        this(coordinates, colors, mapper, 1.0f);
    }

    public ScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper) {
        this(coordinates, null, mapper, 1.0f);
    }

    public ScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper, float width) {
        bbox = new BoundingBox3d();
        setData(coordinates);
        setColors(colors);
        setWidth(width);
        setColorMapper(mapper);
    }

    public void clear() {
        coordinates = null;
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

        if (coordinates != null) {
            for (Coord3d coord : coordinates) {
                Color color = mapper.getColor(coord); // TODO: should store
                                                      // result in the
                                                      // point color
                GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
                GLES2CompatUtils.glVertex3f(coord.x, coord.y, coord.z);
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL.GL_POINTS);

        if (coordinates != null) {
            for (Coord3d coord : coordinates) {
                Color color = mapper.getColor(coord); // TODO: should store
                                                      // result in the
                                                      // point color
                gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
                gl.getGL2().glVertex3f(coord.x, coord.y, coord.z);
            }
        }
        gl.getGL2().glEnd();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        for (Coord3d c : coordinates) {
            c.set(transform.compute(c));
        }
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        for (Coord3d c : coordinates)
            bbox.add(c);
    }

    /* */

    /**
     * Set the coordinates of the point.
     * 
     * @param xyz
     *            point's coordinates
     */
    public void setData(Coord3d[] coordinates) {
        this.coordinates = coordinates;

        bbox.reset();
        for (Coord3d c : coordinates)
            bbox.add(c);
    }

    public Coord3d[] getData() {
        return coordinates;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;

        fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
    }

    @Override
    public ColorMapper getColorMapper() {
        return mapper;
    }

    @Override
    public void setColorMapper(ColorMapper mapper) {
        this.mapper = mapper;
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

    /* */

    protected Coord3d[] coordinates;
    protected Color[] colors;
    protected float width;
    protected ColorMapper mapper;

}