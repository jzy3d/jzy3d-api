package org.jzy3d.maths.parameq;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;

public abstract class ParametricEquation {
  public List<Coord3d> apply(double tmin, double tmax, int steps) {
    List<Coord3d> coords = new ArrayList<Coord3d>(steps);
    double step = (tmax - tmin) / steps;
    for (int t = 0; t < steps; t++) {
      double value = tmin + t * step;
      coords.add(apply(value));
    }
    return coords;
  }

  public abstract Coord3d apply(double t);
}
