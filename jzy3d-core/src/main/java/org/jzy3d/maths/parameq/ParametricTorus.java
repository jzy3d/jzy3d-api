package org.jzy3d.maths.parameq;

import org.jzy3d.maths.Coord3d;


public class ParametricTorus extends ParametricEquation2 {

  public ParametricTorus(double R, double r) {
    super();
    this.R = R;
    this.r = r;
  }

  @Override
  public Coord3d apply(double t, double u) {
    Coord3d coord = new Coord3d();
    coord.x = (float) (Math.cos(t) * (R + r * Math.cos(u)));
    coord.y = (float) (Math.sin(t) * (R + r * Math.cos(u))); // could extend circle
    coord.z = (float) (r * Math.sin(u));
    return coord;
  }

  protected double R;
  protected double r;
}
