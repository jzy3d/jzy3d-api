package org.jzy3d.plot3d.primitives.interactive.tools;

import java.util.Comparator;

public class ProjectionComparator implements Comparator<IProjection> {
  @Override
  public int compare(IProjection p1, IProjection p2) {
    float dist1 = p1.deapness();
    float dist2 = p2.deapness();
    if (dist1 == dist2)
      return 0;
    else if (dist1 < dist2)
      return 1;// *Math.max((int)Math.abs(dist1-dist2),1);
    else
      return -1;
  }
}
