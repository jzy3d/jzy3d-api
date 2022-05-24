package org.jzy3d.plot3d.primitives.axis.layout.providers;

public interface ITickProvider {
  public double[] generateTicks(double min, double max);

  public double[] generateTicks(double min, double max, int steps);

  public int getSteps();
}
