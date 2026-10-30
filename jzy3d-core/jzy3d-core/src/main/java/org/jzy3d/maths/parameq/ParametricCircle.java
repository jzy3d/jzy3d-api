package org.jzy3d.maths.parameq;

import org.jzy3d.maths.Coord3d;

public class ParametricCircle extends ParametricEquation {
  @Override
  public Coord3d apply(double t) {
    Coord3d coord = new Coord3d();
    coord.x = (float) Math.cos(t);
    coord.y = (float) Math.sin(t);
    coord.z = 0;
    return coord;
  }
}
