package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

/**
 * ZY square, XY correct aspect ratio
 */
public class YZSquarifier implements ISquarifier {

  @Override
  public Coord3d scale(float xRange, float yRange, float zRange) {
    return new Coord3d(1, zRange / yRange, 1);
  }

}
