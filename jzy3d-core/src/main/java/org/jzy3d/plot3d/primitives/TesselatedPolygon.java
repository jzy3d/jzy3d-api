package org.jzy3d.plot3d.primitives;

import org.jzy3d.painters.Painter;

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
            protected void begin(Painter painter, GL gl) {
				painter.glBegin(GL.GL_TRIANGLES);
			}

			/**
			 * Override default to use a line strip to draw wire, so that the
			 * shared adjacent triangle side is not drawn.
			 */
			@Override
            protected void callPointForWireframe(Painter painter, GL gl) {
				painter.color(wfcolor);
				painter.glLineWidth(wfwidth);
				painter.glBegin(GL.GL_LINE_STRIP);

				for (Point p : points) {
					painter.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				}
				painter.glEnd();
				
			}
		};
	}
}
