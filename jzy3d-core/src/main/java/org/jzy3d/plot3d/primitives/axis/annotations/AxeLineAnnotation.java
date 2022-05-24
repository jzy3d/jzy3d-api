package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;

public class AxeLineAnnotation {
  protected Color color = Color.RED;
  protected float width = 3;

  public synchronized void drawVerticalLine(IPainter painter, Range yrange, float value) {
    painter.glLineWidth(width);
    painter.glBegin_LineStrip();
    painter.glColor4f(color.r, color.g, color.b, color.a);
    painter.glVertex3f(value, yrange.getMin() - yrange.getRange() / 30, 0);
    painter.glVertex3f(value, yrange.getMax() + yrange.getRange() / 30, 0);
    painter.glEnd();
  }

  public synchronized void drawHorizontalLineGL2(IPainter painter, Range xrange, float value) {
    painter.glLineWidth(width);
    painter.glBegin_LineStrip();
    painter.glColor4f(color.r, color.g, color.b, color.a);
    painter.glVertex3f(xrange.getMin() - xrange.getRange() / 30, value, 0);
    painter.glVertex3f(xrange.getMax() + xrange.getRange() / 30, value, 0);
    painter.glEnd();
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }
}
