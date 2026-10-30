package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;

/** Force number to be represented with a given number of decimals */
public class FixedDecimalTickRenderer implements ITickRenderer {
  public FixedDecimalTickRenderer() {
    this(6);
  }

  public FixedDecimalTickRenderer(int precision) {
    this.precision = precision;
  }

  @Override
  public String format(double value) {
    return Utils.num2str('f', value, precision);
  }

  protected int precision;
}
