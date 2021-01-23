package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;


/** Input value is expected to be a number of elapsed seconds */
public class ElapsedTimeTickRenderer implements ITickRenderer {
  public ElapsedTimeTickRenderer() {
    this("mm:ss");
  }

  public ElapsedTimeTickRenderer(String format) {
    this.format = format;
  }

  @Override
  public String format(double value) {
    return Utils.time2str((long) value * 1000);
  }


  protected String format;
}
