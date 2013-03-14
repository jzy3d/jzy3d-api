package org.jzy3d.plot3d.builder.delaunay;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.delaunay.jdt.Point_dt;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

public class JDTTypeConverter {
    public static Point_dt toJdtPoint(Coord3d coord) {
        return new Point_dt(coord.x, coord.y, coord.z);
    }
    
    public static Coord3d toJzyCoord(Point_dt point) {
        return new Coord3d((float) point.x(), (float)point.y(), (float)point.z());
    }

    // TODO may build an actual triangle
    public static Polygon toJzyPolygon(Triangle_dt triangle) {
        Coord3d c1 = toJzyCoord(triangle.p1());
        Coord3d c2 = toJzyCoord(triangle.p2());
        Coord3d c3 = toJzyCoord(triangle.p3());

        Polygon polygon = new Polygon();
        polygon.add(new Point(c1));
        polygon.add(new Point(c2));
        polygon.add(new Point(c3));
        return polygon;
    }
}
