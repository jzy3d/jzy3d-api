package org.jzy3d.plot3d.primitives;

import org.jzy3d.maths.BoundingBox3d;

public interface Croppable {
  public void filter(BoundingBox3d bounds);

  public void resetFilter();
}
