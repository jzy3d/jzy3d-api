package org.jzy3d.maths.algorithms.convexhull;

import java.util.Deque;
import org.jzy3d.maths.Coord2d;


public interface ConvexHullFunction {

  public Deque<Coord2d> getConvexHull(Coord2d[] pts);
}
