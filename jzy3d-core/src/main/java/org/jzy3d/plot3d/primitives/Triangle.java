package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;

/**
 * @author Martin Pernollet
 */
public class Triangle extends Geometry {

  /**
   * Initializes an empty {@link Triangle} with face status defaulting to true, and wireframe status
   * defaulting to false.
   */
  public Triangle() {
    super();
  }

  public Triangle(Color wire, Color face) {
    setWireframeColor(wire);
    setColor(face);
  }

  @Override
  protected void begin(IPainter painter) {
    painter.glBegin_Triangle();
  }
}
