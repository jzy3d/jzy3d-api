package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import java.util.Date;
import org.jzy3d.maths.Utils;


/**
 * @see {@link Utils.dat2str()} for further details on custom formats.
 */
public class DateTickRenderer implements ITickRenderer {
  public DateTickRenderer() {
    this("dd/MM/yyyy HH:mm:ss");
  }

  public DateTickRenderer(String format) {
    this.format = format;
  }

  @Override
  public String format(double value) {
    Date date = Utils.num2dat((long) value);
    return Utils.dat2str(date, format);
  }

  protected String format;
}
