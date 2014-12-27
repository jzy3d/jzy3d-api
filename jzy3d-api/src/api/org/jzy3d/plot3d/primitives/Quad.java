package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * A {@link Quad} extends a {@link Polygon} in order to provide a specific
 * {@link draw()} method that relies on a dedicated GL2 call (GL_QUADS), and to
 * ensure the number of points is never greater than 4.
 * 
 * @see {@link Polygon}
 * 
 * @author Martin Pernollet
 */
public class Quad extends Polygon {

	/**
	 * Initializes an empty {@link Quad} with face status defaulting to true,
	 * and wireframe status defaulting to false.
	 */
	public Quad() {
		super();
	}

	@Override
    public void draw(GL gl, GLU glu, Camera cam) {
		// Execute transformation
		doTransform(gl, glu, cam);

		// Draw content of polygon
		if (gl.isGL2()) {
			if (facestatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				if (wfstatus) {
					gl.getGL2().glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.getGL2().glPolygonOffset(1.0f, 1.0f);
				}
				gl.getGL2().glBegin(GL2GL3.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
						// System.out.println(c);
					} else
						gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
					gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				gl.getGL2().glEnd();
				
				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);

				gl.getGL2().glEnable(GL.GL_POLYGON_OFFSET_FILL);
				gl.getGL2().glPolygonOffset(1.0f, 1.0f);

				gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);// wfcolor.a);
				gl.getGL2().glLineWidth(wfwidth);

				gl.getGL2().glBegin(GL2GL3.GL_QUADS);
				for (Point p : points) {
					gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				gl.getGL2().glEnd();

				gl.getGL2().glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}
		} else {
			if (facestatus) {
				gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				if (wfstatus) {
					gl.getGL2().glEnable(GL.GL_POLYGON_OFFSET_FILL);
					gl.getGL2().glPolygonOffset(1.0f, 1.0f);
				}
				gl.getGL2().glBegin(GL2GL3.GL_QUADS); // <<<
				for (Point p : points) {
					if (mapper != null) {
						Color c = mapper.getColor(p.xyz); // TODO: should store
															// result in the
															// point color
						gl.getGL2().glColor4f(c.r, c.g, c.b, c.a);
						// System.out.println(c);
					} else
						gl.getGL2().glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
					gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				gl.getGL2().glEnd();
				
				if (wfstatus)
					gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}

			// Draw edge of polygon
			if (wfstatus) {
				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);

				GLES2CompatUtils.glEnable(GL.GL_POLYGON_OFFSET_FILL);
				GLES2CompatUtils.glPolygonOffset(1.0f, 1.0f);

				GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);// wfcolor.a);
				GLES2CompatUtils.glLineWidth(wfwidth);

				GLES2CompatUtils.glBegin(GL2GL3.GL_QUADS);
				for (Point p : points) {
					GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				GLES2CompatUtils.glEnd();

				GLES2CompatUtils.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			}
		}

		/*
		 * Point b = new Point(getBarycentre(), Color.BLUE); b.setWidth(5);
		 * b.draw(gl,glu,cam);
		 */

		doDrawBounds(gl, glu, cam);
	}

	/** Add a point to the polygon. */
	@Override
    public void add(Point point) {
		if (points.size() == 4)
			throw new RuntimeException(
					"The Quad allready has 4 points registered");

		super.add(point);
	}

	@Override
    public double getDistance(Camera camera) {
		return getBarycentre().distance(camera.getEye());
	}

	@Override
    public double getShortestDistance(Camera camera) {
		double min = Float.MAX_VALUE;
		double dist = 0;
		for (Point point : points) {
			dist = point.getDistance(camera);
			if (dist < min)
				min = dist;
		}

		dist = getBarycentre().distance(camera.getEye());
		if (dist < min)
			min = dist;
		return min;
	}

	@Override
    public double getLongestDistance(Camera camera) {
		double max = 0;
		double dist = 0;
		for (Point point : points) {
			dist = point.getDistance(camera);
			if (dist > max)
				max = dist;
		}
		return max;
	}

	@Override
    public String toString(int depth) {
		return Utils.blanks(depth) + "(Quad) #points:" + points.size();
	}
}
