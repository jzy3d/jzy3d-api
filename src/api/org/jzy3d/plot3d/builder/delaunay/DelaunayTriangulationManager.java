/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jzy3d.plot3d.builder.delaunay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.delaunay.jdt.Point_dt;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;


/**
 *
 * @author Mo
 */
public class DelaunayTriangulationManager {

    public DelaunayTriangulationManager(CoordinateValidator cv, Triangulation triangulator) {
        this.triangulator = triangulator;
        setX(cv.getX());
        setY(cv.getY());
        set_Z_as_fxy(cv.get_Z_as_fxy());
    }

    public AbstractDrawable buildDrawable() {
        Shape s = new Shape();
        s.add(getFacets());
        return s;

    }

    // TODO: three different point classes coord3d, point_dt !!
    List<Polygon> getFacets() {

        int xlen = this.x.length;
        for (int i = 0; i < xlen; i++) {
            Point_dt point_dt = new Point_dt(x[i], y[i], z_as_fxy[i][i]);
            this.triangulator.insertPoint(point_dt);
        }

        List<Polygon> polygons = new ArrayList<Polygon>(this.triangulator.trianglesSize());

        Iterator<Triangle_dt> trianglesIter;
        trianglesIter = this.triangulator.trianglesIterator();

        while (trianglesIter.hasNext()) {
            Triangle_dt triangle = trianglesIter.next();

            if (triangle.isHalfplane()) { /* isHalfplane means a degenerated triangle */
                continue;
            }

            Polygon newPolygon = buildPolygonFrom(triangle);

            polygons.add(newPolygon);

        }

        return polygons;
    }

    private Polygon buildPolygonFrom(Triangle_dt triangle) {
        Coord3d c1 = triangle.p1().getAsCoord3d();
        Coord3d c2 = triangle.p2().getAsCoord3d();
        Coord3d c3 = triangle.p3().getAsCoord3d();

        Polygon polygon = new Polygon();

        polygon.add(new Point(c1));
        polygon.add(new Point(c2));
        polygon.add(new Point(c3));

        return polygon;

    }

    public void setX(float[] x) {
        this.x = x;
    }

    public void setY(float[] y) {
        this.y = y;
    }

    public void set_Z_as_fxy(float[][] z_as_fxy) {
        this.z_as_fxy = z_as_fxy;
    }
    protected float x[];
    protected float y[];
    protected float z_as_fxy[][];
    protected final Triangulation triangulator;
}


