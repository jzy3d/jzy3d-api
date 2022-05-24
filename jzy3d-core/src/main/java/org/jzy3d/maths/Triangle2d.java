package org.jzy3d.maths;

public class Triangle2d {
  public Coord2d a;
  public Coord2d b;
  public Coord2d c;

  public Triangle2d() {}

  public Triangle2d(Coord2d a, Coord2d b, Coord2d c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public Coord2d getMedianAB() {
    return a.mean(b);
  }

  public Coord2d getMedianAC() {
    return getMedianCA();
  }

  public Coord2d getMedianBC() {
    return b.mean(c);
  }

  public Coord2d getMedianCA() {
    return c.mean(a);
  }

  public Coord3d[] getSegmentAB(float z) {
    return segment(new Coord3d(a, z), new Coord3d(b, z));
  }
  public Coord3d[] getSegmentBA(float z) {
    return segment(new Coord3d(b, z), new Coord3d(a, z));
  }

  public Coord3d[] getSegmentAC(float z) {
    return segment(new Coord3d(a, z), new Coord3d(c, z));
  }

  public Coord3d[] getSegmentBC(float z) {
    return segment(new Coord3d(b, z), new Coord3d(c, z));
  }

  public static Coord3d[] segment(Coord3d c1, Coord3d c2) {
    Coord3d[] axisSegment = {c1, c2};
    return axisSegment;
  }

  public static Coord3d[] leftMostFirst(Coord3d[] segment) {
    return leftMostFirst(segment, segment[0], segment[1]);
  }

  private static Coord3d[] leftMostFirst(Coord3d[] segment, Coord3d seg0, Coord3d seg1) {
    if (seg0.x < seg1.x) {
      return segment;
    } else {
      return segment(seg1, seg0);
    }
  }

  public static Coord3d[] leftMostFirst(Coord3d seg0, Coord3d seg1) {
    if (seg0.x < seg1.x) {
      return segment(seg0, seg1);
    } else {
      return segment(seg1, seg0);
    }
  }

  public Triangle2d mul(float value) {
    return new Triangle2d(a.mul(value), b.mul(value), c.mul(value));
  }

  public Triangle2d mulSelf(float value) {
    a = a.mul(value);
    b = b.mul(value);
    c = c.mul(value);
    return this;
  }

  /**
   * @return the 2D center of this triangle
   * @see method 3 of https://fr.wikihow.com/calculer-le-centre-de-gravit%C3%A9-d%27un-triangle
   */
  public Coord2d getCenter() {
    return a.add(b).add(c).div(3);
  }
  
  /**
   * @return the 2D center of this triangle
   * @see method 2 of https://fr.wikihow.com/calculer-le-centre-de-gravit%C3%A9-d%27un-triangle
   */
  public Coord2d getCenterAB() {
    return Coord2d.interpolate(c, getMedianAB(), 2f / 3);
  }
  /**
   * @return the 2D center of this triangle
   * @see method 2 of https://fr.wikihow.com/calculer-le-centre-de-gravit%C3%A9-d%27un-triangle
   */
  public Coord2d getCenterAC() {
    return Coord2d.interpolate(b, getMedianAC(), 2f / 3);
  }
  /**
   * @return the 2D center of this triangle
   * @see method 2 of https://fr.wikihow.com/calculer-le-centre-de-gravit%C3%A9-d%27un-triangle
   */
  public Coord2d getCenterBC() {
    return Coord2d.interpolate(a, getMedianBC(), 2f / 3);
  }

  /**
   * Creates the following 2D equilateral triangle with A at (0,0) and C at (side,0).
   * 
   * B yields to (side / 2, side * Math.sqrt(3) / 2)
   * 
   * <pre>
   *       B
   *       /\
   *      /  \
   *     /    \
   *    /      \
   *   /        \
   *   ----------
   *  A           C
   * </pre>
   * 
   * @param side
   */
  public static Triangle2d equilateral(float side) {
    float height = (float) equilateralHeight(side);

    Coord2d a = new Coord2d(0, 0); // left
    Coord2d b = new Coord2d(side / 2, height); // top
    Coord2d c = new Coord2d(side, 0); // right

    Triangle2d triangle = new Triangle2d(a, b, c);
    return triangle;
  }

  protected static double equilateralHeight(double side) {
    return side * Math.sqrt(3) / 2;
  }

}
