package org.jzy3d.maths;

/**
 * An Angle2d stores three 2d points, considering the angle is on the second one. An instance may
 * return angle(), cos() and sin()
 */
public class Angle2d {

  /** Create an angle, described by three points. */
  public Angle2d(float x1, float y1, float x2, float y2, float x3, float y3) {
    this.x1 = x1;
    this.x2 = x2;
    this.x3 = x3;
    this.y1 = y1;
    this.y2 = y2;
    this.y3 = y3;
  }

  /** Create an angle, described by three coordinates. */
  public Angle2d(Coord2d p1, Coord2d p2, Coord2d p3) {
    x1 = p1.x;
    x2 = p2.x;
    x3 = p3.x;
    y1 = p1.y;
    y2 = p2.y;
    y3 = p3.y;
  }

  /***********************************************************/

  /**
   * Computes the sinus of the angle, by creating a fourth point on an orthogonal direction.
   */
  public float sin() {
    float x4 = 0;// (y1-y2)*(z3-z2) - (z1-z2)*(y3-y2);
    float y4 = 0;// (z1-z2)*(x3-x2) - (x1-x2)*(z3-z2);
    float z4 = (x1 - x2) * (y3 - y2) - (y1 - y2) * (x3 - x2);

    Vector3d v1 = new Vector3d(x1, y1, 0, x2, y2, 0);
    Vector3d v3 = new Vector3d(x3, y3, 0, x2, y2, 0);
    Vector3d v4 = new Vector3d(x4, y4, z4, x2, y2, 0);

    return ((z4 >= 0) ? 1 : -1) * v4.norm() / (v1.norm() * v3.norm());
  }

  /** Computes cosinus of the angle */
  public float cos() {
    Vector2d v1 = new Vector2d(x1, y1, x2, y2);
    Vector2d v3 = new Vector2d(x3, y3, x2, y2);
    return v1.dot(v3) / (v1.norm() * v3.norm());
  }

  /** Computes the angle at vertex p2 between vectors p1,p2 and p3,p2. Returns 0 to PI radians. */
  public float angle() {
    double lenP1P3 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
    double lenP1P2 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    double lenP3P2 = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2));
    double numerator = Math.pow(lenP1P2, 2) + Math.pow(lenP3P2, 2) - Math.pow(lenP1P3, 2);
    double denominator = 2 * lenP1P2 * lenP3P2;
    return (float) Math.acos(numerator / denominator);
  }

  /***********************************************************/

  private float x1;
  private float x2;
  private float x3;
  private float y1;
  private float y2;
  private float y3;
}
