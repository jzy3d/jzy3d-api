package org.jzy3d.plot3d.primitives.textured;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.GLES2CompatUtils;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.GLU;

public class TranslucentQuad extends Quad implements ITranslucent {
	@Override
    public void draw(Painter painter, GL gl, GLU glu, Camera cam) {
		// Execute transformation
		doTransform(painter, gl, glu, cam);

		if (gl.isGL2()) {

			// Draw content of polygon
			if (facestatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				if (wfstatus) {
					gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.glPolygonOffset(1.0f, 1.0f);
				}
				gl.getGL2().glBegin(GL2GL3.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						callWithAlphaFactor(gl, c, alpha);
					} else
						callWithAlphaFactor(gl, p.rgb, alpha);
					gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				gl.getGL2().glEnd();
				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);

				gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1.0f, 1.0f);

				callWithAlphaFactor(gl, wfcolor, alpha);
				gl.glLineWidth(wfwidth);

				gl.getGL2().glBegin(GL2GL3.GL_QUADS);
				for (Point p : points) {
					gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				gl.getGL2().glEnd();

				gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}
		} else {
			// Draw content of polygon
			if (facestatus) {
				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK,
						GL2GL3.GL_FILL);
				if (wfstatus) {
					gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.glPolygonOffset(1.0f, 1.0f);
				}
				GLES2CompatUtils.glBegin(GL2GL3.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						callWithAlphaFactor(gl, c, alpha);
					} else
						callWithAlphaFactor(gl, p.rgb, alpha);
					GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				GLES2CompatUtils.glEnd();
				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK,
						GL2GL3.GL_LINE);

				gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1.0f, 1.0f);

				callWithAlphaFactor(gl, wfcolor, alpha);
				gl.glLineWidth(wfwidth);

				GLES2CompatUtils.glBegin(GL2GL3.GL_QUADS);
				for (Point p : points) {
					GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				GLES2CompatUtils.glEnd();

				gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}

		}
	}

	@Override
	public void setAlphaFactor(float a) {
		alpha = a;
	}

	protected float alpha = 1;
}
