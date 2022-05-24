package org.jzy3d.maths.parameq;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;

public abstract class ParametricEquation2 {
  public List<Coord3d> apply(double tmin, double tmax, int tsteps, double umin, double umax,
      int usteps) {
    List<Coord3d> coords = new ArrayList<Coord3d>(tsteps);
    double tstep = (tmax - tmin) / tsteps;
    double ustep = (umax - umin) / usteps;
    for (int t = 0; t < tsteps; t++) {
      double tvalue = tmin + t * tstep;
      for (int u = 0; u < usteps; u++) {
        double uvalue = umin + u * ustep;
        coords.add(apply(tvalue, uvalue));
      }
    }
    return coords;
  }

  public abstract Coord3d apply(double t, double u);
}
