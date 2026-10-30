package org.jzy3d.plot3d.builder;

public abstract class Mapper2D {
  public abstract double f(double x);

  public double[] f(double[] x) {
    double[] y = new double[x.length];

    for (int i = 0; i < x.length; i++)
      y[i] = f(x[i]);
    return y;
  }
}
