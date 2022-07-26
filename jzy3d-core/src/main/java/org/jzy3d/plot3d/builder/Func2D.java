package org.jzy3d.plot3d.builder;

import java.util.function.Function;

public class Func2D extends Mapper2D {
  Function<Double, Double> function;

  public Func2D(Function<Double, Double> function) {
    this.function = function;
  }

  @Override
  public double f(double x) {
    return function.apply(x);
  }

  public Function<Double, Double> getFunction() {
    return function;
  }

  public void setFunction(Function<Double, Double> function) {
    this.function = function;
  }
}
