package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

/**
 * XZ square, YZ correct aspect ratio
 */
public class ZXSquarifier implements ISquarifier {

  @Override
  public Coord3d scale(float xRange, float yRange, float zRange) {
    return new Coord3d(zRange / xRange, 1, 1);
  }

}
