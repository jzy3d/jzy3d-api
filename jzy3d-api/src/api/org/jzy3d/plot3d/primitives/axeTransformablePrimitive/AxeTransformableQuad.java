package org.jzy3d.plot3d.primitives.axeTransformablePrimitive;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;

public class AxeTransformableQuad extends Quad {
	
	AxeTransformerSet transformers;

	public AxeTransformableQuad(AxeTransformerSet transformers) {
		super();
		this.transformers = transformers;
	}

	public void draw(GL gl, GLU glu, Camera cam) {
		// Execute transformation
		doTransform(gl, glu, cam);

		// Draw content of polygon
		if (gl.isGL2()) {
			if (facestatus) {
				gl.getGL2().glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
				if (wfstatus) {
					gl.getGL2().glEnable(GL2.GL_POLYGON_OFFSET_FILL);
					gl.getGL2().glPolygonOffset(1.0f, 1.0f);
				}
				gl.getGL2().glBegin(GL2.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
						// System.out.println(c);
					} else
						gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
						GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
				}
				gl.getGL2().glEnd();
				
				if (wfstatus)
					gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				gl.getGL2().glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

				gl.getGL2().glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				gl.getGL2().glPolygonOffset(1.0f, 1.0f);

				gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);// wfcolor.a);
				gl.getGL2().glLineWidth(wfwidth);

				gl.getGL2().glBegin(GL2.GL_QUADS);
				for (Point p : points) {
					GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
				}
				gl.getGL2().glEnd();

				gl.getGL2().glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			}
		} else {
			if (facestatus) {
				gl.getGL2().glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
				if (wfstatus) {
					gl.getGL2().glEnable(GL2.GL_POLYGON_OFFSET_FILL);
					gl.getGL2().glPolygonOffset(1.0f, 1.0f);
				}
				gl.getGL2().glBegin(GL2.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
						// System.out.println(c);
					} else
						gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
						GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
				}
				gl.getGL2().glEnd();
				
				if (wfstatus)
					gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				GLES2CompatUtils.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

				GLES2CompatUtils.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				GLES2CompatUtils.glPolygonOffset(1.0f, 1.0f);

				GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);// wfcolor.a);
				GLES2CompatUtils.glLineWidth(wfwidth);

				GLES2CompatUtils.glBegin(GL2.GL_QUADS);
				for (Point p : points) {
					GlVertexExecutor.Vertex(gl, new Coord3d(p.xyz.x, p.xyz.y, p.xyz.z), transformers);
				}
				GLES2CompatUtils.glEnd();

				GLES2CompatUtils.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			}
		}

		/*
		 * Point b = new Point(getBarycentre(), Color.BLUE); b.setWidth(5);
		 * b.draw(gl,glu,cam);
		 */

		doDrawBounds(gl, glu, cam);
	}
}
