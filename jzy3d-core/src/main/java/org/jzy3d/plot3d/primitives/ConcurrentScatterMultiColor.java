package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;

public class ConcurrentScatterMultiColor extends ScatterMultiColor implements IMultiColorable {
    public ConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper) {
        this(coordinates, colors, mapper, 1.0f);
    }

    public ConcurrentScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper) {
        this(coordinates, null, mapper, 1.0f);
    }

    public ConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper, float width) {
        super(coordinates, colors, mapper, width);
    }

    @Override
    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL.GL_POINTS);

        if (coordinates != null) {
            synchronized (coordinates) {
                for (Coord3d coord : coordinates) {
                    Color color = mapper.getColor(coord); // TODO: should store
                                                          // result in the
                                                          // point color
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
                    Color color = mapper.getColor(coord); // TODO: should store
                                                          // result in the
                                                          // point color
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

}