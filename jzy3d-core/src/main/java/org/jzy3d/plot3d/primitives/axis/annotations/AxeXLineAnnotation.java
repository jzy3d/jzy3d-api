package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;

public class AxeXLineAnnotation extends AxeLineAnnotation implements AxeAnnotation {
  protected float value;

  @Override
  public void draw(IPainter painter, AxisBox axe) {
    Range yrange = axe.getBounds().getYRange();

    drawVerticalLine(painter, yrange, value);
  }

  public synchronized float getValue() {
    return value;
  }

  public synchronized void setValue(float value) {
    this.value = value;
  }
}
