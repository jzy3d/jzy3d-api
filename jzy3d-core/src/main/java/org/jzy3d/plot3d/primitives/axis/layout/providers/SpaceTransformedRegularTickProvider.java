package org.jzy3d.plot3d.primitives.axis.layout.providers;

/**
 * Build a sequence of regularly spaced ticks in case of space transformed charts (e.g. log charts).
 *
 * @author martin
 */
public class SpaceTransformedRegularTickProvider extends RegularTickProvider {
  public SpaceTransformedRegularTickProvider() {
    super();
  }

  public SpaceTransformedRegularTickProvider(int steps) {
    super(steps);
  }

  /**
   * Able to generate ticks that will visually look equally spaced, despite an existing space
   * transform in the chart. 
   * 
   * The idea is that without knowing the space transform, neither the reverse of this function,
   * we deduce it from the min-max range by computing the <i>stepFactor</i> that can be used to get
   * the following tick sequence :
   * <ul>
   * <li>Z0 = min
   * <li>Z1 = Z0 * stepFactor
   * <li>Z2 = Z1 * stepFactor
   * <li>Z3 = max
   * </ul>
   * 
   * Solve factor equation below
   * <ul>
   * <li>zmax / Math.pow(stepFactor, numberOfTicks) = zmin, equivalent to 
   * <li>Math.pow(stepFactor, numberOfTicks) = zmax/zmin, equivalent to 
   * <li>stepFactor = Math.pow(zmax/zmin, 1/steps) 
   * <li>as Math.pow(x,n)=a is reversed by x=Math.pow(a,1/n) 
   * </ul>
   */
  @Override
  public double[] generateTicks(double min, double max, int steps) {
    steps = steps-1;
    
    // escape 0 divisor
    if(min==0)
      min = 1;
    
    double stepFactor = Math.pow(max / min, 1D / steps);


    double[] ticks = new double[steps + 1];
    ticks[0] = min;
    for (int i = 1; i < ticks.length; i++) {
      ticks[i] = ticks[i - 1] * stepFactor;
    }
    //System.out.println("max:"+max + " min:" + min + " ratio:" + max / min + " factor:" + stepFactor);
    return ticks;
  }
}
