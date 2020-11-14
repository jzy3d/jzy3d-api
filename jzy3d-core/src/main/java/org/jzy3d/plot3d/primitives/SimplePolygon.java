package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.painters.Painter;

/**
 * A {@link SimplePolygon} makes the simplest possible GL rendering with
 * especially no:
 * <ul>
 * <li>gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
 * <li>gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class SimplePolygon extends Polygon implements ISingleColorable, IMultiColorable {
	public SimplePolygon() {
		super();
	}

	@Override
	public void draw(Painter painter) {
		doTransform(painter);
		doDrawPolygon(painter);
		doDrawBoundsIfDisplayed(painter);
	}

	private void doDrawPolygon(Painter painter) {
		// Draw content of polygon
		if (facestatus) {
			// if(wfstatus)
			// polygonOffseFillEnable(gl);

			painter.glBegin_Polygon();
			for (Point p : points) {
				if (mapper != null) {
					Color c = mapper.getColor(p.xyz);
					painter.color(c);
				} else {
					painter.color(p.rgb);
				}
				painter.vertex(p.xyz);
			}
			painter.glEnd();
			// if(wfstatus)
			// polygonOffsetFillDisable(gl);
		}

		// Draw edge of polygon
		if (wfstatus) {
			painter.glBegin_Polygon();
			painter.color(wfcolor);
			painter.glLineWidth(wfwidth);
			for (Point p : points) {
				painter.vertex(p.xyz);
			}
			painter.glEnd();
		}
	}
}
