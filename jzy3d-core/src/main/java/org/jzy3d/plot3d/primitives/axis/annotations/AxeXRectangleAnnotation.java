package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;

public class AxeXRectangleAnnotation extends AxeXLineAnnotation implements AxeAnnotation {
  PolygonMode polygonMode = PolygonMode.FRONT_AND_BACK;

  public synchronized void drawLine(IPainter painter, Range yrange, Range zrange) {
    // painter.glLineWidth(width);
    painter.glBegin_Polygon();
    painter.glColor4f(color.r, color.g, color.b, color.a);

    painter.glPolygonMode(polygonMode, PolygonFill.FILL);
    painter.glPolygonMode(polygonMode, PolygonFill.LINE);

    float ymin = yrange.getMin() - yrange.getRange() / 30;
    float ymax = yrange.getMax() + yrange.getRange() / 30;
    float z = zrange.getMin() - 2;

    painter.glVertex3f(value - width, ymin, z);
    painter.glVertex3f(value - width, ymax, z);
    painter.glVertex3f(value, ymax, z);
    painter.glVertex3f(value, ymin, z);

    painter.glEnd();
  }
}
