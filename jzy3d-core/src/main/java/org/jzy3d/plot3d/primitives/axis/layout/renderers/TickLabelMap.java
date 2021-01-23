package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link ITickRenderer} that can store a list of labels for given axis values.
 * 
 * @author Martin Pernollet
 */
public class TickLabelMap implements ITickRenderer {
  public void register(double value, String string) {
    tickValues.put(value, string);
  }

  public boolean contains(double value) {
    return tickValues.containsKey(value);
  }

  public Map<Double, String> getMap() {
    return tickValues;
  }

  @Override
  public String format(double value) {
    if (tickValues.get(value) != null)
      return tickValues.get(value);
    else
      return "";
  }

  protected Map<Double, String> tickValues = new HashMap<Double, String>();
}
