package org.jzy3d.maths.parameq;

import org.jzy3d.maths.Coord3d;

public class ParametricAbcd extends ParametricEquation {
  public ParametricAbcd(double a, double b, double c, double d, double j, double k) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.j = j;
    this.k = k;
  }

  @Override
  public Coord3d apply(double t) {
    Coord3d coord = new Coord3d();
    coord.x = (float) (Math.cos(a * t) - Math.pow(Math.cos(b * t), j));
    coord.y = (float) (Math.sin(c * t) - Math.pow(Math.sin(d * t), k));
    coord.z = 0;
    return coord;
  }

  protected double a;
  protected double b;
  protected double c;
  protected double d;
  protected double j;
  protected double k;
}
