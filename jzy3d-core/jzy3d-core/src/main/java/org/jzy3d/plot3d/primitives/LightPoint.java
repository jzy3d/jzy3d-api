package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

public class LightPoint {
  public Coord3d xyz;
  public Color rgb;

  public LightPoint(Coord3d xyz, Color rgb) {
    this.xyz = xyz;
    this.rgb = rgb;
  }
}
