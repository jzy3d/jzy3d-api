package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

/**
 * XZ square, XY correct aspect ratio
 */
public class XZSquarifier implements ISquarifier {

  @Override
  public Coord3d scale(float xRange, float yRange, float zRange) {
    return new Coord3d(1, 1, xRange / zRange);
  }

}
