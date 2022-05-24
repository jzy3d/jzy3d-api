package org.jzy3d.plot3d.primitives.axis.annotations;

import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.AxisBox;

public interface AxeAnnotation {
  public void draw(IPainter painter, AxisBox axe);
}
