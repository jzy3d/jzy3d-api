package org.jzy3d.chart.factories;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.primitives.Disk;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.LineStripInterpolated;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.Sphere;
import org.jzy3d.plot3d.primitives.Triangle;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;

public interface IDrawableFactoryDesign {

  /* ****************** PRIMITIVES ****************** */

  public Point newPoint(Coord3d coord);

  public LineStrip newLine(Coord3d... coords);

  public LineStrip newLine(List<Coord3d> coords);

  public LineStripInterpolated newLineInterpolated(List<Coord3d> controlPoints, int resolution);

  public Triangle newTriangle();

  public Quad newQuad();

  public Polygon newPolygon();

  public Disk newDisk();

  public Cylinder newCylinder();

  public Sphere newSphere();

  public SymbolHandler newSymbolHandler();

  /* ****************** CHART BASED ****************** */

  // TernarySurface.shape
  // Surface.shape
  // SurfaceBig.shape
  public Shape newSurface(); // return Surface

  public Scatter newScatter();


}
