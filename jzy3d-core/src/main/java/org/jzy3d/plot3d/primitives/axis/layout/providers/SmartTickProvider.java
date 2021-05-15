package org.jzy3d.plot3d.primitives.axis.layout.providers;


/**
 * Compute the ticks placements automatically between values min and max.
 * 
 * @author Jerome Kodjabachian
 */
public class SmartTickProvider extends AbstractTickProvider implements ITickProvider {
  public SmartTickProvider() {
    this(5);
  }

  public SmartTickProvider(int steps) {
    this.steps = steps;
  }

  @Override
  public double[] generateTicks(double min, double max, int steps) {
    if (min == max) {
      double[] ticks = new double[1];
      ticks[0] = min;
      return ticks;
    } else if (min > max)
      return new double[0];

    double absscale = Math.floor(Math.log10(max - min));
    double relscale = Math.log10(max - min) - absscale;
    double ticksize = 0;

    if (relscale < Math.log10(0.2 * steps))
      ticksize = (Math.pow(10, absscale) * 0.2);
    else if (relscale < Math.log10(0.5 * steps))
      ticksize = (Math.pow(10, absscale) * 0.5);
    else if (relscale < Math.log10(1 * steps))
      ticksize = Math.pow(10, absscale) * 1;
    else
      ticksize = Math.pow(10, absscale) * 2;

    double start = (int) Math.ceil(min / ticksize);
    double stop = (int) Math.floor(max / ticksize);

    double[] ticks = new double[(int) (stop - start + 1)];

    for (double t = start; t <= stop; t++)
      ticks[(int) (t - start)] = (t * ticksize);

    return ticks;
  }

  @Override
  public int getSteps() {
    return steps;
  }

  protected int steps;
}
