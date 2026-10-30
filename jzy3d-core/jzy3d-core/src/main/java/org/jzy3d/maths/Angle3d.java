package org.jzy3d.maths;

import java.util.List;
import org.jzy3d.plot3d.primitives.Point;

/**
 * An Angle3d stores three 3d points, considering the angle is on the second one. An instance may
 * return angle(), cos() and sin().
 */
public class Angle3d {
  public static final double DEGREE_90_D = Math.PI / 2;
  public static final float DEGREE_90 = (float) DEGREE_90_D;

  public static final double DEGREE_45_D = Math.PI / 4;
  public static final float DEGREE_45 = (float) DEGREE_45_D;

  protected float x1;
  protected float x2;
  protected float x3;
  protected float y1;
  protected float y2;
  protected float y3;
  protected float z1;
  protected float z2;
  protected float z3;


  /**
   * Create an angle, described by three points. The angle is supposed to be on p2
   */
  public Angle3d(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
      float z3) {
    this.x1 = x1;
    this.x2 = x2;
    this.x3 = x3;
    this.y1 = y1;
    this.y2 = y2;
    this.y3 = y3;
    this.z1 = z1;
    this.z2 = z2;
    this.z3 = z3;
  }

  /**
   * Create an angle, described by three coordinates. The angle is supposed to be on p2
   */
  public Angle3d(Coord3d p1, Coord3d p2, Coord3d p3) {
    x1 = p1.x;
    x2 = p2.x;
    x3 = p3.x;
    y1 = p1.y;
    y2 = p2.y;
    y3 = p3.y;
    z1 = p1.z;
    z2 = p2.z;
    z3 = p3.z;
  }

  /***********************************************************/

  /**
   * Computes the sinus of the angle, by creating a fourth point on an orthogonal direction.
   */
  public float sin() {
    /*
     * Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2); Vector3d v3 = new Vector3d(x3, y3, z3,
     * x2, y2, z2); Coord3d c4 = v1.cross(v3); Vector3d v4 = new Vector3d(c4, new Coord3d(x2, y2,
     * z2));
     */
    // new Vector3d(x4, y4, z4, x2, y2, z2);

    Coord3d c2 = new Coord3d(x2, y2, z2);
    Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2);
    Vector3d v3 = new Vector3d(x3, y3, z3, x2, y2, z2);
    Coord3d c4 = v1.cross(v3).add(c2);
    Vector3d v4 = new Vector3d(c4, c2);

    return ((c4.z >= 0) ? 1 : -1) * v4.norm() / (v1.norm() * v3.norm());
  }

  /** Computes cosinus of the angle */
  public float cos() {
    Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2);
    Vector3d v3 = new Vector3d(x3, y3, z3, x2, y2, z2);
    return v1.dot(v3) / (v1.norm() * v3.norm());
  }

  /** Computes the angle at vertex p2 between rays p1,p2 and p3,p2. Returns 0 to PI radians. */
  public float angle() {
    return (float) angleD();
  }
  
  /** Computes the angle at vertex p2 between rays p1,p2 and p3,p2. Returns 0 to PI radians. */
  public double angleD() {
    double lenP1P3 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2) + Math.pow(z1 - z3, 2));
    double lenP1P2 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    double lenP3P2 = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2) + Math.pow(z3 - z2, 2));
    double numerator = Math.pow(lenP1P2, 2) + Math.pow(lenP3P2, 2) - Math.pow(lenP1P3, 2);
    double denominator = 2 * lenP1P2 * lenP3P2;
    return Math.acos(numerator / denominator);
  }
  
  
  /**
   * Compute the sum of all angles in the input coordinate list.
   * 
   * If input contains point A, B, C, this method will compute angles ABC, BCA, CAB.
   * 
   * An error is thrown if there are less than 3 points.
   * 
   * 
   * 
   */
  public static double angleSum(List<Coord3d> coords) {
    if(coords.size()<3) {
      throw new IllegalArgumentException("Can not compute an angle with less than 3 points");
    }
    
    Angle3d[] angles = new Angle3d[coords.size()];
    
    for (int i = 0; i < coords.size()-2; i++) {
      angles[i] = new Angle3d(coords.get(i), coords.get(i+1), coords.get(i+2));
    }
    angles[coords.size()-2] = new Angle3d(coords.get(coords.size()-2), coords.get(coords.size()-1), coords.get(0));
    angles[coords.size()-1] = new Angle3d(coords.get(coords.size()-1), coords.get(0), coords.get(1));

    
    double cumulated = 0;
    for(Angle3d angle: angles) {
      cumulated+=angle.angleD();
    }
    return cumulated;
  }
  
  public static double angleSumFromPoints(List<Point> coords) {
    if(coords.size()<3) {
      throw new IllegalArgumentException("Can not compute an angle with less than 3 points");
    }
    
    Angle3d[] angles = new Angle3d[coords.size()];
    
    for (int i = 0; i < coords.size()-2; i++) {
      angles[i] = new Angle3d(coords.get(i).xyz, coords.get(i+1).xyz, coords.get(i+2).xyz);
    }
    angles[coords.size()-2] = new Angle3d(coords.get(coords.size()-2).xyz, coords.get(coords.size()-1).xyz, coords.get(0).xyz);
    angles[coords.size()-1] = new Angle3d(coords.get(coords.size()-1).xyz, coords.get(0).xyz, coords.get(1).xyz);

    
    double cumulated = 0;
    for(Angle3d angle: angles) {
      cumulated+=angle.angleD();
    }
    return cumulated;
  }
  
  /**
   * Returns the expected sum of all angles of a polygon given its number of points.
   * 
   * The polygon is supposed to not have any edge crossing another edge.
   * 
   * @param n
   * @return
   */
  public static double angleSumOfNonIntersectingPolygon(int n) {
    if(n<3)
      return 0;
    else
      return (n-2)*180;
  }
  
  public static double angleSumOfNonIntersectingPolygonRad(int n) {
    if(n<3)
      return 0;
    else
      return (n-2)*Math.PI;
  }
  
  public static boolean angleSumFromPointsOfNonIntersectingPolygon(List<Point> coords) {
    double diff= Math.abs(angleSumFromPoints(coords)-angleSumOfNonIntersectingPolygonRad(coords.size()));
    return diff<0.0001;
  }
}
