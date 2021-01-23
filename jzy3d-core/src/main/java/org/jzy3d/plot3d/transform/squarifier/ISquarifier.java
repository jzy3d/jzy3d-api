package org.jzy3d.plot3d.transform.squarifier;

import org.jzy3d.maths.Coord3d;

public interface ISquarifier {

  /**
   * Return a 3D scaling factor to set the aspect ratio of the axes
   * 
   * The parameters are the current ranges of the x,y and z axes, and can be used to connection
   * between the length of the axes and the values of the axes or to change the relative lengths.
   * 
   * @param xRange
   * @param yRange
   * @param zRange
   * @return scaling
   */
  Coord3d scale(float xRange, float yRange, float zRange);

}
