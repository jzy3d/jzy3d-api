package org.jzy3d.plot3d.primitives;

import org.jzy3d.plot3d.rendering.view.Camera;

public interface ISortableDraw {
  public double getDistance(Camera camera);

  public double getShortestDistance(Camera camera);

  public double getLongestDistance(Camera camera);
}
