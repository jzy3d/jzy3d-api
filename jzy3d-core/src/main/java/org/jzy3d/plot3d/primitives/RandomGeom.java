package org.jzy3d.plot3d.primitives;

import java.util.Random;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;

public class RandomGeom {
  Random r = new Random();
  boolean alpha = false;

  public RandomGeom() {}

  public RandomGeom(long seed) {
    r.setSeed(seed);
  }

  public boolean isAlpha() {
    return alpha;
  }

  public void setAlpha(boolean alpha) {
    this.alpha = alpha;
  }

  
  // GENERATOR
  
  
  public Coord3d coord() {
    return new Coord3d(aFloat(), aFloat(), aFloat());
  }

  public Coord3d coord(float xrange, float yrange, float zrange) {
    return new Coord3d(aFloat(xrange), aFloat(yrange), aFloat(zrange));
  }

  public Color color() {
    return new Color(aFloat(), aFloat(), aFloat(), alpha ? aFloat() : 1f);
  }

  public Color color(Coord3d coord, float xrange, float yrange, float zrange) {
    return new Color(aFloat() * coord.x / xrange, aFloat() * coord.y / xrange,
        aFloat() * coord.x / zrange, alpha ? aFloat() : 1f);
  }

  /**
   * Build a color based on the input coord values. 
   */
  public Color color(Coord3d coord) {
    return new Color(coord.x, coord.y, coord.z, aFloat());
  }

  /** Generate a float between 0 and 1 */
  public float aFloat() {
    return r.nextFloat();
  }

  /** Generate a float between 0 and range */
  public float aFloat(float range) {
    return aFloat() * range;
  }

}
