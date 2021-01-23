package org.jzy3d.maths.algorithms.convexhull.algorithms;

import org.jzy3d.maths.Coord2d;

public class ComputationalGeometry {

  public static final int CounterClockwise = 1;
  public static final int Clockwise = -1;
  public static final int Collinear = 0;

  /// <summary>
  /// Computes the orientation of a point q to the directed line segment p1-p2.
  /// The orientation of a point relative to a directed line segment indicates
  /// which way you turn to get to q after travelling from p1 to p2.
  /// </summary>
  /// <param name="p1"></param>
  /// <param name="p2"></param>
  /// <param name="q"></param>
  /// <returns>
  /// 1 if q is counter-clockwise from p1-p2,
  /// -1 if q is clockwise from p1-p2,
  /// 0 if q is collinear with p1-p2-
  /// </returns>
  public static int computeOrientation(Coord2d p1, Coord2d p2, Coord2d q) {
    return orientationIndex(p1, p2, q);
  }

  private static int orientationIndex(Coord2d p1, Coord2d p2, Coord2d q) {
    // travelling along p1->p2, turn counter clockwise to get to q return 1,
    // travelling along p1->p2, turn clockwise to get to q return -1,
    // p1, p2 and q are colinear return 0.
    double dx1 = p2.getX() - p1.getX();
    double dy1 = p2.getY() - p1.getY();
    double dx2 = q.getX() - p2.getX();
    double dy2 = q.getY() - p2.getY();
    return RobustDeterminant.signOfDet2x2(dx1, dy1, dx2, dy2);
  }
}
