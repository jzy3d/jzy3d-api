package org.jzy3d.maths.algorithms.interpolation.algorithms;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.algorithms.interpolation.IInterpolator;


public class BernsteinInterpolator implements IInterpolator {
  @Override
  public List<Coord3d> interpolate(List<Coord3d> controlPoints, int resolution) {
    Spline3D spline = new Spline3D(controlPoints);
    return spline.computeVertices(resolution);
  }
}
