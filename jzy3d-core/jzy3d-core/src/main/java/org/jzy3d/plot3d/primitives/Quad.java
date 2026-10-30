package org.jzy3d.plot3d.primitives;

import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;

/**
 * A {@link Quad} extends a {@link Polygon} in order to provide a specific {@link draw()} method
 * that relies on a dedicated GL2 call (GL_QUADS), and to ensure the number of points is never
 * greater than 4.
 * 
 * @see {@link Polygon}
 * 
 * @author Martin Pernollet
 */
public class Quad extends Polygon {
  /**
   * Initializes an empty {@link Quad} with face status defaulting to true, and wireframe status
   * defaulting to false.
   */
  public Quad() {
    super();
  }

  @Override
  protected void begin(IPainter painter) {
    painter.glBegin_Quad();
  }


  /** Add a point to the polygon. */
  @Override
  public void add(Point point) {
    if (points.size() == 4)
      throw new RuntimeException("The Quad allready has 4 points registered");

    super.add(point);
  }

  @Override
  public String toString(int depth) {
    return Utils.blanks(depth) + "(Quad) #points:" + points.size();
  }
}
