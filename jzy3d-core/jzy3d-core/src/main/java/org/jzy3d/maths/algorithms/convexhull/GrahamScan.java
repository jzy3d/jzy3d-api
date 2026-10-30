package org.jzy3d.maths.algorithms.convexhull;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.algorithms.convexhull.algorithms.RadialComparator;
import org.jzy3d.maths.algorithms.convexhull.utils.QuickSort;

/**
 * @author Jukka Moisio
 */
public class GrahamScan implements ConvexHullFunction {

  /**
   * implements interface
   * 
   * @author Jukka Moisio
   */
  @Override
  public Deque<Coord2d> getConvexHull(Coord2d[] pts) {
    preSort(pts);
    Coord2d p;
    RadialComparator c = new RadialComparator(pts[0]);

    // palautettava pino
    Deque<Coord2d> s = new ArrayDeque<Coord2d>();

    s.push(pts[pts.length - 1]);
    s.push(pts[0]);
    s.push(pts[1]);
    for (int i = 2; i < pts.length - 1; i++) {
      p = s.pop();
      c.setOrigin(s.peek());

      while (c.compare(p, pts[i]) > 0) {
        p = s.pop();
        c.setOrigin(s.peek());
      }

      s.push(p);
      s.push(pts[i]);
    }

    // Tarkistetaan viel?, pit??k? viimeiseksi lis?tyn pisteen
    // todella kuulua pinoon.
    p = s.pop();

    c.setOrigin(s.peek());
    if (c.compare(p, pts[pts.length - 1]) <= 0) {
      s.push(p);
    }

    s.push(pts[pts.length - 1]);

    return s;
  }

  private Coord2d[] preSort(Coord2d[] pts) {
    Coord2d t;

    // find the lowest point in the set. If two or more points have
    // the same minimum y coordinate choose the one with the minimu x.
    // This focal point is put in array location pts[0].
    for (int i = 1; i < pts.length; i++) {
      if ((pts[i].getY() < pts[0].getY())
          || ((pts[i].getY() == pts[0].getY()) && (pts[i].getX() < pts[0].getX()))) {
        t = pts[0];
        pts[0] = pts[i];
        pts[i] = t;
      }
    }

    // sort the points radially around the focal point.
    QuickSort.sort(pts, new RadialComparator(pts[0]));

    return pts;
  }
}
