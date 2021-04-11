package org.jzy3d.plot3d.primitives.interactive.tools;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;


public class PolygonProjection implements IProjection {
  public List<Coord3d> coords;
  public List<Color> colors;

  public PolygonProjection(List<Coord3d> coords, List<Color> colors) {
    this.coords = coords;
    this.colors = colors;
  }

  @Override
  public float deapness() {
    float dist = 1;
    for (Coord3d c : coords) {
      if (c.z < dist)
        dist = c.z;
    }
    return dist;
  }

  public Color getMeanColor() {
    Color mean = new Color(0, 0, 0);
    for (Color c : colors) {
      mean.r += c.r;
      mean.g += c.g;
      mean.b += c.b;
    }
    mean.r /= colors.size();
    mean.g /= colors.size();
    mean.b /= colors.size();
    return mean;
  }
}
