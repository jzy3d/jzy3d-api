package org.jzy3d.plot3d.primitives.log;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

public class AxeTransformablePoint extends Point {
	/** Intialize a point at the origin, with a white color and a width of 1. */
	public AxeTransformablePoint(SpaceTransformer transformers) {
		this(Coord3d.ORIGIN, Color.WHITE, 1.0f, transformers);
	}

	/** Intialize a point with a white color and a width of 1. */
	public AxeTransformablePoint(Coord3d xyz, SpaceTransformer transformers) {
		this(xyz, Color.WHITE, 1.0f, transformers);
	}

	/** Intialize a point with a width of 1. */
	public AxeTransformablePoint(Coord3d xyz, Color rgb, SpaceTransformer transformers) {
		this(xyz, rgb, 1.0f, transformers);
	}

	public AxeTransformablePoint(Coord3d xyz, Color rgb, float width, SpaceTransformer transformers)  {
		super(xyz,rgb,width);
		this.transformers = transformers;
		updateBounds();
	}

	/* */

	public void draw(GL gl, GLU glu, Camera cam) {
		doTransform(gl, glu, cam);

		if (gl.isGL2()) {
			gl.getGL2().glPointSize(width);

			gl.getGL2().glBegin(GL2.GL_POINTS);
			gl.getGL2().glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
			GlVertexExecutor.Vertex(gl, new Coord3d(xyz.x, xyz.y, xyz.z), transformers);
			gl.getGL2().glEnd();
		} else {
			GLES2CompatUtils.glPointSize(width);

			GLES2CompatUtils.glBegin(GL2.GL_POINTS);
			GLES2CompatUtils.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
			GLES2CompatUtils.glVertex3f(transformers.getX().compute(xyz.x), transformers.getY().compute(xyz.y), transformers.getZ().compute(xyz.z));
			GLES2CompatUtils.glEnd();
		}
	}
	
	public void updateBounds() {
		bbox.reset();
		if(transformers != null) {
			bbox.add(transformers.compute(this.xyz));
			System.out.println(this.getClass() + "log");
		} else {
			bbox.add(this);
			System.out.println(this.getClass() + "nonlog");
		}
	}
	
	SpaceTransformer transformers;


}
