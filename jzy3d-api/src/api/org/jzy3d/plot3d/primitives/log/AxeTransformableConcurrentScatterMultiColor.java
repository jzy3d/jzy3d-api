package org.jzy3d.plot3d.primitives.log;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ConcurrentScatterMultiColor;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableConcurrentScatterMultiColor extends
		ConcurrentScatterMultiColor {
	
	SpaceTransformer transformers;
	
	  public AxeTransformableConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper, SpaceTransformer transformers) {
	        this(coordinates, colors, mapper, 1.0f, transformers);
	    }

	    public AxeTransformableConcurrentScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper, SpaceTransformer transformers) {
	        this(coordinates, null, mapper, 1.0f, transformers);
	    }

	    public AxeTransformableConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper, float width, SpaceTransformer transformers) {
	        super(coordinates, colors, mapper, width);
	        this.transformers = transformers;
	    }

	    public void drawGLES2() {
	        GLES2CompatUtils.glPointSize(width);
	        GLES2CompatUtils.glBegin(GL2.GL_POINTS);

	        if (coordinates != null) {
	            synchronized (coordinates) {
	                for (Coord3d coord : coordinates) {
	                    Color color = mapper.getColor(coord); // TODO: should store
	                                                          // result in the
	                                                          // point color
	                    GLES2CompatUtils.glColor4f(color.r, color.g, color.b, color.a);
	                    GLES2CompatUtils.glVertex3f(transformers.getX().compute(coord.x), transformers.getY().compute(coord.y) , transformers.getZ().compute(coord.z));
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
	                    Color color = mapper.getColor(coord); // TODO: should store
	                                                          // result in the
	                                                          // point color
	                    gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
	                    GlVertexExecutor.Vertex(gl, new Coord3d(coord.x, coord.y, coord.z), transformers);
	                }
	            }
	        }
	        gl.getGL2().glEnd();
	    }
}
