package org.jzy3d.plot3d.primitives.log;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformableScatter extends Scatter{
	SpaceTransformer transformers;
	 public AxeTransformableScatter(SpaceTransformer transformers) {
	        bbox = new BoundingBox3d();
	        this.transformers = transformers;
	        setWidth(1.0f);
	        setColor(Color.BLACK);
	    }

	    public AxeTransformableScatter(Coord3d[] coordinates, SpaceTransformer transformers) {
	        this(coordinates, Color.BLACK, transformers);
	    }

	    public AxeTransformableScatter(Coord3d[] coordinates, Color rgb, SpaceTransformer transformers) {
	        this(coordinates, rgb, 1.0f, transformers);
	    }

	    public AxeTransformableScatter(Coord3d[] coordinates, Color rgb, float width, SpaceTransformer transformers) {
	        bbox = new BoundingBox3d();
	        this.transformers = transformers;
	        setData(coordinates);
	        setWidth(width);
	        setColor(rgb);
	    }

	    public AxeTransformableScatter(Coord3d[] coordinates, Color[] colors, SpaceTransformer transformers) {
	        this(coordinates, colors, 1.0f, transformers);
	    }

	    public AxeTransformableScatter(Coord3d[] coordinates, Color[] colors, float width, SpaceTransformer transformers) {
	        bbox = new BoundingBox3d();
	        this.transformers = transformers;
	        setData(coordinates);
	        setWidth(width);
	        setColors(colors);
	    }
	    

	    public void drawGLES2() {
	        GLES2CompatUtils.glPointSize(width);

	        GLES2CompatUtils.glBegin(GL2.GL_POINTS);
	        if (colors == null)
	            GLES2CompatUtils.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
	        if (coordinates != null) {
	            int k = 0;
	            for (Coord3d c : coordinates) {
	                if (colors != null) {
	                    GLES2CompatUtils.glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
	                    k++;
	                }
	                GLES2CompatUtils.glVertex3f(transformers.getX().compute(c.x), transformers.getY().compute(c.y), transformers.getZ().compute(c.z));
	            }
	        }
	        GLES2CompatUtils.glEnd();
	    }

	    public void drawGL2(GL gl) {
	        gl.getGL2().glPointSize(width);

	        gl.getGL2().glBegin(GL2.GL_POINTS);
	        if (colors == null)
	            gl.getGL2().glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
	        if (coordinates != null) {
	            int k = 0;
	            for (Coord3d c : coordinates) {
	                if (colors != null) {
	                    gl.getGL2().glColor4f(colors[k].r, colors[k].g, colors[k].b, colors[k].a);
	                    k++;
	                }
	                GlVertexExecutor.Vertex(gl, new Coord3d(c.x, c.y, c.z), transformers);
	            }
	        }
	        gl.getGL2().glEnd();
	    }
	    
	    public void updateBounds() {
	        bbox.reset();
	        for (Coord3d c : coordinates)
	        	bbox.add(transformers.compute(c));
	    }
}
