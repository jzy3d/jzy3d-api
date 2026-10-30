package org.jzy3d.plot3d.builder;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coordinates;
import org.jzy3d.plot3d.primitives.Composite;


public abstract class Tessellator {
  public Tessellator() {}

  public Composite build(List<Coord3d> coordinates) {
    Coordinates coords = new Coordinates(coordinates);
    return build(coords.getX(), coords.getY(), coords.getZ());
  }

  public abstract Composite build(float[] x, float[] y, float[] z);
}
