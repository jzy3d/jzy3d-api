package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.Transform;

public class ConcurrentScatterMultiColorList extends ScatterMultiColorList implements IMultiColorable {
    public ConcurrentScatterMultiColorList(ColorMapper mapper) {
        this(new ArrayList<Coord3d>(), mapper, 1.0f);
    }

    public ConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper) {
        this(coordinates, mapper, 1.0f);
    }

    public ConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width) {
        super(coordinates, mapper, width);
    }

    /* */

    @Override
    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL.GL_POINTS);

        if (coordinates != null) {
            synchronized (coordinates) {
                for (Coord3d coord : coordinates) {
                    // TODO: should store
                    // result in the
                    // point color?
                    Color color = mapper.getColor(coord);
                    GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
                    GLES2CompatUtils.glVertex3f(coord.x, coord.y, coord.z);
                }
            }
        }
        GLES2CompatUtils.glEnd();
    }

    @Override
    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL.GL_POINTS);

        if (coordinates != null) {
            synchronized (coordinates) {
                for (Coord3d coord : coordinates) {
                    // TODO: should store
                    // result in the
                    // point color
                    Color color = mapper.getColor(coord);
                    gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
                    gl.getGL2().glVertex3f(coord.x, coord.y, coord.z);
                }
            }
        }
        gl.getGL2().glEnd();
    }

    @Override
    public void applyGeometryTransform(Transform transform) {
        synchronized (coordinates) {
            for (Coord3d c : coordinates) {
                c.set(transform.compute(c));
            }
        }
        updateBounds();
    }

    @Override
    public void updateBounds() {
        bbox.reset();
        synchronized (coordinates) {
            for (Coord3d c : coordinates)
                bbox.add(c);
        }
    }

    @Override
    public void add(Coord3d c) {
        synchronized (coordinates) {
            coordinates.add(c);
        }
    }

    @Override
    public void clear() {
        synchronized (coordinates) {
            coordinates.clear();
        }
        updateBounds();
    }
}
