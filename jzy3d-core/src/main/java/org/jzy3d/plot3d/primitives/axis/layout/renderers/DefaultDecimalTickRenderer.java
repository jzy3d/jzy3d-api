package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;

/** Force number to be represented with a given number of decimals */
public class DefaultDecimalTickRenderer implements ITickRenderer {
  public DefaultDecimalTickRenderer() {
    this(6);
  }

  public DefaultDecimalTickRenderer(int precision) {
    this.precision = precision;
  }

  @Override
  public String format(double value) {
    return Utils.num2str('g', value, precision);
  }

  protected int precision;
}
