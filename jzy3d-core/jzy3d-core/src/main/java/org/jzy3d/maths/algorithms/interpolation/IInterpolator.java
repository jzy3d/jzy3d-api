package org.jzy3d.maths.algorithms.interpolation;

import java.util.List;
import org.jzy3d.maths.Coord3d;

public interface IInterpolator {
  public List<Coord3d> interpolate(List<Coord3d> controlPoints, int resolution);
}
