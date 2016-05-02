package org.jzy3d.plot3d.primitives;

import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

import com.jogamp.opengl.GL;

/**
 * A polygon made of two triangles with no wireframe on their adjacent side.
 * 
 * By being an {@link AbstractComposite}, this polygon can be decomposed and is
 * thus more flexible with a sorting algorithm.
 * 
 * @author Martin Pernollet
 */
public class TesselatedPolygon extends AbstractComposite {
	public TesselatedPolygon(Point[] points) {
		Polygon p1 = newTriangle();
		p1.add(points[0]);
		p1.add(points[1]);
		p1.add(points[2]);
		add(p1);

		Polygon p2 = newTriangle();
		p2.add(points[2]);
		p2.add(points[3]);
		p2.add(points[0]);
		add(p2);
	}

	protected Polygon newTriangle() {
		return new Polygon() {
			@Override
            protected void begin(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glBegin(GL.GL_TRIANGLES);
				} else {
					GLES2CompatUtils.glBegin(GL.GL_TRIANGLES);
				}
			}

			/**
			 * Override default to use a line strip to draw wire, so that the
			 * shared adjacent triangle side is not drawn.
			 */
			@Override
            protected void callPointForWireframe(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b,
							wfcolor.a);
					gl.glLineWidth(wfwidth);

					beginWireWithLineStrip(gl); // <
					for (Point p : points) {
						gl.getGL2().glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
					}
					end(gl);
				} else {
					GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g,
							wfcolor.b, wfcolor.a);
					gl.glLineWidth(wfwidth);

					beginWireWithLineStrip(gl); // <
					for (Point p : points) {
						GLES2CompatUtils.glVertex3f(p.xyz.x, p.xyz.y,
								p.xyz.z);
					}
					end(gl);
				}
			}

			protected void beginWireWithLineStrip(GL gl) {
				if (gl.isGL2()) {
					gl.getGL2().glBegin(GL.GL_LINE_STRIP);
				} else {
					GLES2CompatUtils.glBegin(GL.GL_LINE_STRIP);
				}
			}

		};
	}
}
