package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;

/** Formats 1000 to '1.0e3' */
public class ScientificNotationTickRenderer implements ITickRenderer {
  public ScientificNotationTickRenderer() {
    this(1);
  }

  public ScientificNotationTickRenderer(int precision) {
    this.precision = precision;
  }

  @Override
  public String format(double value) {
    return Utils.num2str('e', value, precision);
  }

  protected int precision;
}
