package org.jzy3d.plot3d.primitives.contour;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jzy3d.plot3d.primitives.LineStrip;


public class ContourLevels {

  public ContourLevel getContourLevel(double level) {
    return lines.get(level);
  }

  public void setLevelLine(double level, LineStrip strip) {
    ContourLevel line = new ContourLevel((float) level);
    line.appendLine(strip);
    lines.put(level, line);
  }

  public void appendLevelLine(double level, LineStrip strip) {
    ContourLevel line = lines.get(level);
    if (line != null)
      line.appendLine(strip);
    else
      setLevelLine(level, strip);
  }

  protected ContourLevel getOrInitContourLevel(double level) {
    ContourLevel line = lines.get(level);
    if (line == null)
      line = new ContourLevel();
    return line;
  }

  public Collection<ContourLevel> getContourLevels() {
    return lines.values();
  }

  Map<Double, ContourLevel> lines = new HashMap<Double, ContourLevel>();
}
