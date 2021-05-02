package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;

public class AxeCrossAnnotation extends AxeLineAnnotation implements AxeAnnotation {
  protected Coord2d value;

  @Override
  public void draw(IPainter painter, AxisBox axe) {
    Range xrange = axe.getBounds().getXRange();
    Range yrange = axe.getBounds().getYRange();

    drawHorizontalLineGL2(painter, xrange, value.y);
    drawVerticalLine(painter, yrange, value.x);
  }

  public synchronized Coord2d getValue() {
    return value;
  }

  public synchronized void setValue(Coord2d value) {
    this.value = value;
  }
}
