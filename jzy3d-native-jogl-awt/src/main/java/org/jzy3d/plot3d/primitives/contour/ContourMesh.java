package org.jzy3d.plot3d.primitives.contour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContourMesh {
  public String getLevelLabel(double level) {
    return labels.get(level);
  }

  public void setLevelLabel(double level, String label) {
    labels.put(level, label);
  }

  public Set<Double> getLevels() {
    return labels.keySet();
  }

  public Collection<String> getLabels() {
    return labels.values();
  }

  public ContourLevels lines = new ContourLevels();
  protected Map<Double, String> labels = new HashMap<Double, String>();
}
