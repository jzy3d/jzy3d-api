package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

/**
 * XY square, XZ correct aspect ratio
 */
public class YXSquarifier implements ISquarifier {

  @Override
  public Coord3d scale(float xRange, float yRange, float zRange) {
    return new Coord3d(yRange / xRange, 1, 1);
  }

}
