package org.jzy3d.maths;

import java.util.Deque;
import java.util.List;
import org.jzy3d.maths.algorithms.convexhull.ConvexHullFunction;
import org.jzy3d.maths.algorithms.convexhull.GrahamScan;

public class ConvexHull {
  public static Polygon2d hull(List<Coord3d> cell) {

    Deque<Coord2d> hull = ConvexHull.build2d(cell);
    Polygon2d out = new Polygon2d(hull.size());
    while (!hull.isEmpty()) {
      Coord2d p = hull.pop();
      out.add(p);
    }
    return out;
  }

  public static Deque<Coord2d> build2d(List<Coord3d> input2d) {
    int np = input2d.size();
    Coord2d[] data = new Coord2d[np];
    for (int i = 0; i < data.length; i++) {
      data[i] = asPoint2f(input2d.get(i));
    }
    return f.getConvexHull(data);
  }

  public static Deque<Coord2d> build2d(PolygonArray input2d) {
    int np = input2d.length();
    Coord2d[] data = new Coord2d[np];
    for (int i = 0; i < np; i++) {
      data[i] = new Coord2d(input2d.x[i], input2d.y[i]);
    }
    return f.getConvexHull(data);
  }

  protected static Coord2d asPoint2f(Coord3d c) {
    return new Coord2d(c.x, c.y);
  }

  protected static ConvexHullFunction f = new GrahamScan();// new JarvisMarch();
}
