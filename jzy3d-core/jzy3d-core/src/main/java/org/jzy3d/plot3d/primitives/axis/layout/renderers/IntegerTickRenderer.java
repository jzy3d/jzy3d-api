package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import java.text.NumberFormat;
import java.util.Locale;

public class IntegerTickRenderer implements ITickRenderer {
  boolean separator = false;
  
  public IntegerTickRenderer() {
    this(false);
  }

  public IntegerTickRenderer(boolean separator) {
    this.separator = separator;
  }



  @Override
  public String format(double value) {
    if(separator)
      return NumberFormat.getNumberInstance(Locale.US).format((int) value);
    else
      return ""+(int) value;
  }
}
