package org.jzy3d.maths.parameq;

import org.jzy3d.maths.Coord3d;


public class ParametricHelix extends ParametricEquation {

  public ParametricHelix(double a, double b) {
    super();
    this.a = a;
    this.b = b;
  }

  @Override
  public Coord3d apply(double t) {
    Coord3d coord = new Coord3d();
    coord.x = (float) (a * Math.cos(t));
    coord.y = (float) (a * Math.sin(t)); // could extend circle
    coord.z = (float) (b * t);
    return coord;
  }

  protected double a;
  protected double b;
}
