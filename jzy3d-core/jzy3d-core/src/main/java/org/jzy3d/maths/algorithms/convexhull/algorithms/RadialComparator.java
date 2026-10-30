package org.jzy3d.maths.algorithms.convexhull.algorithms;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.algorithms.convexhull.utils.IComparator;

/**
 * @author Jukka Moisio, Teemu Linkosaari
 */
public class RadialComparator implements IComparator<Coord2d> {

  /**
   * Origo, jonka suhteen verrataan
   */
  private Coord2d origin;

  /**
   * @.pre { origin != null )
   */
  public RadialComparator(Coord2d origin) {
    assert origin != null;
    this.origin = origin;
  }

  @Override
  public int compare(Coord2d p1, Coord2d p2) {
    return polarCompare(origin, p1, p2);
  }

  /**
   *
   */
  public void setOrigin(Coord2d newO) {
    origin = newO;
  }

  private static int polarCompare(Coord2d o, Coord2d p, Coord2d q) {
    double dxp = p.getX() - o.getX();
    double dyp = p.getY() - o.getY();
    double dxq = q.getX() - o.getX();
    double dyq = q.getY() - o.getY();

    int orient = ComputationalGeometry.computeOrientation(o, p, q);

    if (orient == ComputationalGeometry.CounterClockwise) {
      return -1;
    }
    if (orient == ComputationalGeometry.Clockwise) {
      return 1;
    }

    // points are collinear - check distance
    double op = dxp * dxp + dyp * dyp;
    double oq = dxq * dxq + dyq * dyq;
    if (op < oq) {
      return 1;
    }
    if (op > oq) {
      return -1;
    }

    return 0;
  }
}
