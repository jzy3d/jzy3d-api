package org.jzy3d.plot3d.primitives.axis.layout.providers;

import org.jzy3d.maths.Array;


public class StaticTickProvider extends AbstractTickProvider implements ITickProvider {
  public StaticTickProvider(double[] values) {
    this.values = values;
  }

  public StaticTickProvider(float[] values) {
    this.values = Array.toDouble(values);
  }

  @Override
  public double[] generateTicks(double min, double max, int steps) {
    return values;
  }

  @Override
  public int getSteps() {
    return 0;
  }

  protected double[] values;
}
