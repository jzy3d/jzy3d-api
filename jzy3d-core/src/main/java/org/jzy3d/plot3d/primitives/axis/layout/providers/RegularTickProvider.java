package org.jzy3d.plot3d.primitives.axis.layout.providers;


public class RegularTickProvider extends AbstractTickProvider implements ITickProvider {
  public RegularTickProvider() {
    this(3);
  }

  public RegularTickProvider(int steps) {
    this.steps = steps;
  }

  @Override
  public double[] generateTicks(double min, double max, int steps) {
    double[] ticks = new double[steps];
    double step = (max - min) / (steps - 1);

    ticks[0] = min;
    ticks[steps - 1] = max;

    for (int t = 1; t < steps - 1; t++)
      ticks[t] = min + t * step;

    return ticks;
  }

  @Override
  public int getSteps() {
    return steps;
  }

  protected int steps;
}
