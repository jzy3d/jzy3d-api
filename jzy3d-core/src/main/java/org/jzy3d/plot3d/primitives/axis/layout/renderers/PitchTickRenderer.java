package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axis.layout.providers.PitchTickProvider;

/**
 * Renders ticks to display a note name instead of frequency value.
 * 
 * Expected values are
 * 
 * @see PitchTickProvider
 * @author Martin Pernollet
 */
public class PitchTickRenderer implements ITickRenderer {
  public PitchTickRenderer() {}

  @Override
  public String format(double value) {
    if (value == 27.5) {
      return "A0";
    } else if (value == 55) {
      return "A1";
    } else if (value == 110) {
      return "A2";
    } else if (value == 220) {
      return "A3";
    } else if (value == 440) {
      return "A4";
    } else if (value == 880) {
      return "A5";
    } else if (value == 1760) {
      return "A6";
    } else if (value == 3520) {
      return "A7";
    } else if (value != 0)
      return Utils.num2str(value);
    else
      return "";
  }

  protected String format;
}
