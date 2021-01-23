package org.jzy3d.maths;

import java.util.List;

/**
 * A BoundingBox2d stores a couple of maximal and minimal limit on each dimension (x, y). It
 * provides functions for enlarging the box by adding coordinates or an other BoundingBox2d (that is
 * equivalent to computing the union of the current BoundingBox and another one).
 *
 * @author Martin Pernollet
 */
public class BoundingBox2d {

  /** Initialize a BoundingBox by calling its reset method. */
  public BoundingBox2d() {
    reset();
  }

  public BoundingBox2d(List<Coord2d> list) {
    reset();
    for (Coord2d c : list)
      add(c);
  }

  /** Initialize a BoundingBox with raw values. */
  public BoundingBox2d(float xmin, float xmax, float ymin, float ymax) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.xmax = xmax;
  }

  /*********************************************************/

  /**
   * Initialize the bounding box with Float.MAX_VALUE as minimum value, and -Float.MAX_VALUE as
   * maximum value for each dimension.
   */
  public void reset() {
    xmin = Float.MAX_VALUE;
    xmax = -Float.MAX_VALUE;
    ymin = Float.MAX_VALUE;
    ymax = -Float.MAX_VALUE;
  }

  /**
   * Adds an x,y point to the bounding box, and enlarge the bounding box if this points lies outside
   * of it.
   * 
   * @param x
   * @param y
   */
  public void add(float x, float y) {
    if (x > xmax)
      xmax = x;
    if (x < xmin)
      xmin = x;
    if (y > ymax)
      ymax = y;
    if (y < ymin)
      ymin = y;
  }

  /**
   * Add a Point3d to the BoundingBox3d. A shortcut for: <code>add(p.x, p.y);</code>
   * 
   * @param p
   */
  public void add(Coord2d p) {
    add(p.x, p.y);
  }

  /**
   * Add a BoundingBox2d volume to the current one. A convenient shortcut for: <code>
   * add(b.xmin, b.ymin);
   * add(b.xmax, b.ymax);
   * </code>
   * 
   * @param p
   */
  public void add(BoundingBox2d b) {
    add(b.xmin, b.ymin);
    add(b.xmax, b.ymax);
  }

  /*********************************************************/

  /**
   * Compute and return the center point of the BoundingBox3d
   * 
   * @return the center.
   */
  public Coord2d getCenter() {
    return new Coord2d((xmin + xmax) / 2, (ymin + ymax) / 2);
  }

  /**
   * Return the radius of the Sphere containing the Bounding Box, i.e., the distance between the
   * center and the point (xmin, ymin, zmin).
   * 
   * @return the box radius.
   */
  public double getRadius() {
    return getCenter().distance(new Coord2d(xmin, ymin));
  }

  /**
   * Return a copy of the current bounding box after scaling. Scaling does not modify the current
   * bounding box.
   * 
   * @return the scaled bounding box
   */
  public BoundingBox2d scale(Coord3d scale) {
    BoundingBox2d b = new BoundingBox2d();
    b.xmax = xmax * scale.x;
    b.xmin = xmin * scale.x;
    b.ymax = ymax * scale.y;
    b.ymin = ymin * scale.y;
    return b;
  }

  /**
   * Return true if b2 is contained by this box. Note: if b1.contains(b2), then b1.intersect(b2) as
   * well.
   */
  public boolean contains(BoundingBox2d b2) {
    return xmin <= b2.xmin && b2.xmax <= xmax && ymin <= b2.ymin && b2.ymax <= ymax;
  }

  /** Return true if coordinates is contained by this box. */
  public boolean contains(Coord2d c) {
    return xmin <= c.x && c.x <= xmax && ymin <= c.y && c.y <= ymax;
  }

  /** Return true if intersect b2. */
  public boolean intersect(BoundingBox2d b2) {
    return xmin <= b2.xmin && b2.xmin <= xmax || xmin <= b2.xmax && b2.xmax <= xmax
        || ymin <= b2.ymin && b2.ymin <= ymax || ymin <= b2.ymax && b2.ymax <= ymax;
  }

  /*********************************************************/

  /**
   * Return the minimum x value.
   * 
   * @return
   */
  public float xmin() {
    return xmin;
  }

  /**
   * Return the maximum x value.
   * 
   * @return
   */
  public float xmax() {
    return xmax;
  }

  /**
   * Return the minimum y value.
   * 
   * @return
   */
  public float ymin() {
    return ymin;
  }

  /**
   * Return the maximum y value.
   * 
   * @return
   */
  public float ymax() {
    return ymax;
  }

  /*********************************************************/

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(int depth) {
    return Utils.blanks(depth) + "(BoundingBox2d)" + xmin + "<=x<=" + xmax + " | " + ymin + "<=y<="
        + ymax;
  }

  /*********************************************************/

  private float xmin;
  private float xmax;
  private float ymin;
  private float ymax;
}
