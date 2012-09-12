package org.jzy3d.plot3d.builder.delaunay;

import java.util.List;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coordinates;
import org.jzy3d.plot3d.builder.Tessellator;
import org.jzy3d.plot3d.builder.delaunay.jdt.Delaunay_Triangulation;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.Shape;

public class DelaunayTessellator extends Tessellator{
    public AbstractComposite build(List<Coord3d> coordinates) {
        CoordinateValidator cv = new DelaunayCoordinateValidator(new Coordinates(coordinates));
        Triangulation dt = new Delaunay_Triangulation();
        DelaunayTriangulationManager tesselator = new DelaunayTriangulationManager(cv, dt);
        return (Shape) tesselator.buildDrawable();
    }
    
    @Override
    public AbstractComposite build(float[] x, float[] y, float[] z) {
        throw new RuntimeException("not implemented");
    }
}
