package org.jzy3d.plot3d.builder.delaunay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Tessellator;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import il.ac.idc.jdt.DelaunayTriangulation;
import il.ac.idc.jdt.Triangle;

public class DelaunayTessellator extends Tessellator {
  @Override
  public Composite build(List<Coord3d> coordinates) {
    Shape s = new Shape();
    s.add(computePolygons(coordinates));
    return s;
  }

  protected List<Polygon> computePolygons(List<Coord3d> coordinates) {
    // Append all input Jzy3d coordinates in JDT triangulator
    DelaunayTriangulation triangulator = new DelaunayTriangulation();
    for (Coord3d coord : coordinates) {
      triangulator.insertPoint(JDTConverter.toJdtPoint(coord));
    }

    // Retrieve triangles computed by JDT
    List<Polygon> polygons = new ArrayList<Polygon>(triangulator.trianglesSize());
    Iterator<Triangle> it = triangulator.trianglesIterator();
    while (it.hasNext()) {
      Triangle triangle = it.next();
      // isHalfplane indicates a degenerated triangle
      if (triangle.isHalfplane())
        continue;
      polygons.add(JDTConverter.toJzyPolygon(triangle));
    }
    return polygons;
  }

  @Override
  public Composite build(float[] x, float[] y, float[] z) {
    throw new RuntimeException("not called");
  }
}
