package org.jzy3d.plot3d.builder.delaunay;

import java.util.Iterator;

import org.jzy3d.plot3d.builder.delaunay.jdt.Point_dt;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;


/**
 *
 * @author Mo
 */
public interface Triangulation {

    /**
     * insert the point to this Delaunay Triangulation. Note: if p is null or
     * already exist in this triangulation p is ignored.
     *
     * @param p
     * new vertex to be inserted the triangulation.
     */
    public void insertPoint(Point_dt p);

    /**
     * computes the current set (vector) of all triangles and
     * return an iterator to them.
     *
     * @return an iterator to the current set of all triangles.
     */
    public Iterator<Triangle_dt> trianglesIterator();

    /**
     * returns an iterator to the set of points compusing this triangulation.
     * @return iterator to the set of points compusing this triangulation.
     */
    public Iterator<Point_dt> verticesIterator();

    /**
     * @return the number of triangles in the triangulation. <br />
     * Note: includes infinife faces!!.
     */
    public int trianglesSize();
}
