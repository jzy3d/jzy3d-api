package org.jzy3d.plot3d.rendering.ordering;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;


public class PointOrderingStrategy implements Comparator<Coord3d> {
  protected Camera camera;

  public void sort(List<Coord3d> points, Camera cam) {
    this.camera = cam;
    Collections.sort(points, this);
  }

  @Override
  public int compare(Coord3d o1, Coord3d o2) {
    if (o1.equals(o2))
      return 0;
    else {
      double d1 = camera.getEye().distance(o1);
      double d2 = camera.getEye().distance(o2);
      if (d1 < d2)
        return 1;
      else if (d2 > d1)
        return -1;
      else
        return 0;
    }
  }
}
