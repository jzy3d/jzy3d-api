package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

/**
 * Supports additional settings
 * 
 * @author Martin Pernollet
 */
public class Polygon extends Geometry {

  /**
   * Initializes an empty {@link Polygon} with face status defaulting to true, and wireframe status
   * defaulting to false.
   */
  public Polygon() {
    super();
  }
  
  public Polygon(Point... points) {
    super(points);
  }

  public Polygon(List<Point> points) {
    super(points);
  }

  public Polygon(Color wire, Point... points) {
    super(wire, points);
  }

  public Polygon(Color wire, boolean wireDisplayed, Point... points) {
    super(wire, wireDisplayed, points);
  }
  
  public Polygon(Color wire, Color face, Coord3d... points) {
    super(wire, face, points);
  }

  public Polygon(Color wire, Color face) {
    setWireframeColor(wire);
    setColor(face);
  }

  @Override
  protected void begin(IPainter painter) {
    painter.glBegin_Polygon();
  }
}
