package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;

public class AxeYLineAnnotation implements AxeAnnotation {
  protected float value;
  protected Color color = Color.RED;
  protected float width = 3;

  @Override
  public void draw(IPainter painter, AxisBox axe) {
    Range xrange = axe.getBounds().getXRange();
    Range zrange = axe.getBounds().getYRange();

    drawLine(painter, xrange, zrange);
  }

  public synchronized void drawLine(IPainter painter, Range yrange, Range zrange) {
    painter.glLineWidth(width);
    painter.glBegin_LineStrip();
    painter.glColor4f(color.r, color.g, color.b, color.a);
    painter.glVertex3f(value, yrange.getMin() - yrange.getRange() / 30, zrange.getMin() - 2);
    painter.glVertex3f(value, yrange.getMax() + yrange.getRange() / 30, zrange.getMin() - 2);
    painter.glEnd();
  }

  public synchronized float getValue() {
    return value;
  }

  public synchronized void setValue(float value) {
    this.value = value;
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
