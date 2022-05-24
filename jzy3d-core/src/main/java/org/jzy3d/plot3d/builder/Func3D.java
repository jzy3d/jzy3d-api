package org.jzy3d.plot3d.builder;

import java.util.function.BiFunction;

public class Func3D extends Mapper{
  BiFunction<Double,Double,Double> function;
  
  public Func3D(BiFunction<Double,Double,Double> function) {
    this.function = function;
  }

  @Override
  public double f(double x, double y) {
    return function.apply(x, y);
  }

  public BiFunction<Double, Double, Double> getFunction() {
    return function;
  }

  public void setFunction(BiFunction<Double, Double, Double> function) {
    this.function = function;
  }
}
