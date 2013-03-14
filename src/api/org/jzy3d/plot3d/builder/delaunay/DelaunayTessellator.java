package org.jzy3d.plot3d.builder.delaunay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Tessellator;
import org.jzy3d.plot3d.builder.delaunay.jdt.Delaunay_Triangulation;
import org.jzy3d.plot3d.builder.delaunay.jdt.Triangle_dt;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;

public class DelaunayTessellator extends Tessellator{
    public AbstractComposite build(List<Coord3d> coordinates) {
        Shape s = new Shape();
        s.add(computePolygons(coordinates));
        return s;
    }

    protected List<Polygon> computePolygons(List<Coord3d> coordinates) {
        // append all input Jzy3d coordinates in JDT triangulator
        Delaunay_Triangulation triangulator = new Delaunay_Triangulation();
        for (Coord3d coord: coordinates) {
            triangulator.insertPoint(JDTTypeConverter.toJdtPoint(coord));
        }

        // Retrieve triangles computed by JDT
        List<Polygon> polygons = new ArrayList<Polygon>(triangulator.trianglesSize());
        // TODO: three different point classes coord3d, point_dt !!

        Iterator<Triangle_dt> it = triangulator.trianglesIterator();
        while (it.hasNext()) {
            Triangle_dt triangle = it.next();

            if (triangle.isHalfplane()) { /* isHalfplane means a degenerated triangle */
                continue;
            }
            polygons.add(JDTTypeConverter.toJzyPolygon(triangle));
        }
        return polygons;
    }

    @Override
    public AbstractComposite build(float[] x, float[] y, float[] z) {
        throw new RuntimeException("not implemented");
    }
}
