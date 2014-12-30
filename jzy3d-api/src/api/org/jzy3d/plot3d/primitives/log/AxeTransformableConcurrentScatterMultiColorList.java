package org.jzy3d.plot3d.primitives.log;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ConcurrentScatterMultiColorList;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableConcurrentScatterMultiColorList extends
		ConcurrentScatterMultiColorList {
		
	SpaceTransformer transformers;
	
	public AxeTransformableConcurrentScatterMultiColorList(ColorMapper mapper, SpaceTransformer transformers) {
        this(new ArrayList<Coord3d>(), mapper, 1.0f, transformers);
    }

    public AxeTransformableConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, SpaceTransformer transformers) {
        this(coordinates, mapper, 1.0f, transformers);
    }

    public AxeTransformableConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width, SpaceTransformer transformers) {
        super(coordinates, mapper, width);
        this.transformers = transformers;
    }

    /* */

    public void drawGLES2() {
        GLES2CompatUtils.glPointSize(width);
        GLES2CompatUtils.glBegin(GL2.GL_POINTS);

        if (coordinates != null) {
            synchronized (coordinates) {
                for (Coord3d coord : coordinates) {
                    // TODO: should store
                    // result in the
                    // point color?
                    Color color = mapper.getColor(coord);
                    GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
                    GLES2CompatUtils.glVertex3f(transformers.getX().compute(coord.x), transformers.getY().compute(coord.y), transformers.getZ().compute(coord.z));
                }
            }
        }
        GLES2CompatUtils.glEnd();
    }

    public void drawGL2(GL gl) {
        gl.getGL2().glPointSize(width);
        gl.getGL2().glBegin(GL2.GL_POINTS);

        if (coordinates != null) {
            synchronized (coordinates) {
                for (Coord3d coord : coordinates) {
                    // TODO: should store
                    // result in the
                    // point color
                    Color color = mapper.getColor(coord);
                    gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
                    GlVertexExecutor.Vertex(gl, new Coord3d(coord.x, coord.y, coord.z), transformers);
                }
            }
        }
        gl.getGL2().glEnd();
    }
}
