package org.jzy3d.plot3d.builder.concrete;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;


public class SphereScatterGenerator {
  public static List<Coord3d> generate(float radius, int steps) {
    return generate(null, radius, steps, false);
  }

  public static List<Coord3d> generate(Coord3d center, float radius, int steps) {
    return generate(center, radius, steps, false);
  }

  public static List<Coord3d> generate(Coord3d center, float radius, int steps, boolean half) {
    List<Coord3d> coords = new ArrayList<Coord3d>(1500); // TODO optimize

    double inc = Math.PI / steps;
    double i = 0;
    int jrat = half ? 1 : 2;
    while (i < (2 * Math.PI)) {
      double j = 0;
      while (j < (jrat * Math.PI)) {
        Coord3d c = new Coord3d(i, j, radius).cartesian();
        if (center != null) {
          c.x += center.x;
          c.y += center.y;
          c.z += center.z;
        }
        coords.add(c);
        j += inc;
      }
      i += inc;
    }
    return coords;
  }
}
