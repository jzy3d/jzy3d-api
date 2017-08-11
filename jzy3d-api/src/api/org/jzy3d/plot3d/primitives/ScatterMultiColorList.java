package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

/**
 * A scatter plot supporting a List<Coord3d> as input.
 * 
 * @author Martin Pernollet
 */
public class ScatterMultiColorList extends AbstractDrawable implements IMultiColorable {
    public ScatterMultiColorList(ColorMapper mapper) {
        this(new ArrayList<Coord3d>(), mapper, 1.0f);
    }

    public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper) {
        this(coordinates, mapper, 1.0f);
    }

    public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width) {
        bbox = new BoundingBox3d();
        setData(coordinates);
        setWidth(width);
        setColorMapper(mapper);
    }

    public void clear() {
        coordinates.clear();
        updateBounds();
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
                colorGLES2(mapper.getColor(coord));
                vertexGLES2(coord);
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL.GL_POINTS);

        if (coordinates != null) {
            for (Coord3d coord : coordinates) {
                colorGL2(gl, mapper.getColor(coord));
                vertexGL2(gl, coord);
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
    public void setData(List<Coord3d> coordinates) {
        this.coordinates = coordinates;

        bbox.reset();
        for (Coord3d c : coordinates)
            bbox.add(c);
    }
    
    

    public List<Coord3d> getData() {
        return coordinates;
    }
    
    public void add(Coord3d c){
        coordinates.add(c);
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

    protected List<Coord3d> coordinates;
    protected float width;
    protected ColorMapper mapper;
}
