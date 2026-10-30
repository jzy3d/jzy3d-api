package org.jzy3d.plot3d.builder.delaunay;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Polygon;
import il.ac.idc.jdt.Point;
import il.ac.idc.jdt.Triangle;

public class JDTConverter {
  public static Point toJdtPoint(double x, double y, double z) {
    return new Point(x, y, z);
  }

  public static Point toJdtPoint(Coord3d coord) {
    return new Point(coord.x, coord.y, coord.z);
  }

  public static Coord3d toJzyCoord(Point point) {
    return new Coord3d((float) point.getX(), (float) point.getY(), (float) point.getZ());
  }

  // TODO may build an actual triangle
  public static Polygon toJzyPolygon(Triangle triangle) {
    Coord3d c1 = toJzyCoord(triangle.getA());
    Coord3d c2 = toJzyCoord(triangle.getB());
    Coord3d c3 = toJzyCoord(triangle.getC());
    Polygon polygon = new Polygon();
    polygon.add(new org.jzy3d.plot3d.primitives.Point(c1));
    polygon.add(new org.jzy3d.plot3d.primitives.Point(c2));
    polygon.add(new org.jzy3d.plot3d.primitives.Point(c3));
    return polygon;
  }
}
