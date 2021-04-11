package org.jzy3d.maths.algorithms.interpolation.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jzy3d.maths.Coord3d;

/**
 * <p>
 * This is a generic 3D B-Spline class for curves of arbitrary length, control handles and patches
 * are created and joined automatically as described here:
 * <a href="http://www.ibiblio.org/e-notes/Splines/Bint.htm">ibiblio.org/e-notes/
 * Splines/Bint.htm</a>
 * </p>
 * 
 * <p>
 * Thanks to a bug report by Aaron Meyers (http://universaloscillation.com) the
 * {@linkplain #computeVertices(int)} method has a slightly changed behaviour from version 0014
 * onwards. In earlier versions erroneous duplicate points would be added near each given control
 * point, which lead to various weird results.
 * </p>
 * 
 * <p>
 * The new behaviour of the curve interpolation/computation is described in the docs for the
 * {@linkplain #computeVertices(int)} method below.
 * </p>
 * 
 * @version 0014 Added user adjustable curve tightness control
 * @version 0015 Added JAXB annotations and List support for dynamic building of spline
 */
public class Spline3D {

  public static final float DEFAULT_TIGHTNESS = 0.25f;

  protected Coord3d[] points;
  public List<Coord3d> pointList = new ArrayList<Coord3d>();
  public List<Coord3d> vertices;
  public BernsteinPolynomial bernstein;
  public Coord3d[] delta;
  public Coord3d[] coeffA;
  public float[] bi;
  protected float tightness;
  protected float invTightness;
  protected int numP;

  /**
   * Constructs an empty spline container with default curve tightness. You need to populate the
   * spline manually by using {@link #add(Coord3d)}.
   */
  public Spline3D() {
    setTightness(DEFAULT_TIGHTNESS);
  }

  /**
   * @param rawPoints list of control point vectors
   */
  public Spline3D(List<Coord3d> rawPoints) {
    this(rawPoints, null, DEFAULT_TIGHTNESS);
  }

  /**
   * @param rawPoints list of control point vectors
   * @param b predefined Bernstein polynomial (good for reusing)
   * @param tightness default curve tightness used for the interpolated vertices
   *        {@linkplain #setTightness(float)}
   */
  public Spline3D(List<Coord3d> rawPoints, BernsteinPolynomial b, float tightness) {
    pointList.addAll(rawPoints);
    bernstein = b;
    setTightness(tightness);
  }

  public Spline3D(Coord3d[] pointArray) {
    this(pointArray, null, DEFAULT_TIGHTNESS);
  }

  /**
   * @param pointArray array of control point vectors
   * @param b predefined Bernstein polynomial (good for reusing)
   * @param tightness default curve tightness used for the interpolated vertices
   *        {@linkplain #setTightness(float)}
   */
  public Spline3D(Coord3d[] pointArray, BernsteinPolynomial b, float tightness) {
    this(Arrays.asList(pointArray), b, tightness);
  }

  public Spline3D add(float x, float y, float z) {
    return add(new Coord3d(x, y, z));
  }

  /**************/

  /**
   * Adds the given point to the list of control points.
   * 
   * @param p
   * @return itself
   */
  public Spline3D add(Coord3d p) {
    pointList.add(p);
    // pointList.add(p.copy());
    return this;
  }

  /**
   * <p>
   * Computes all curve vertices based on the resolution/number of subdivisions requested. The
   * higher, the more vertices are computed:
   * </p>
   * <p>
   * <strong>(number of control points - 1) * resolution + 1</strong>
   * </p>
   * <p>
   * Since version 0014 the automatic placement of the curve handles can also be manipulated via the
   * {@linkplain #setTightness(float)} method.
   * </p>
   * 
   * @param res the number of vertices to be computed per segment between original control points
   *        (incl. control point always at the start of each segment)
   * @return list of Coord3d vertices along the curve
   */
  public List<Coord3d> computeVertices(int res) {
    updateCoefficients();
    res++;
    if (bernstein == null || bernstein.resolution != res) {
      bernstein = new BernsteinPolynomial(res);
    }
    if (vertices == null) {
      vertices = new ArrayList<Coord3d>();
    } else {
      vertices.clear();
    }
    findCPoints();
    Coord3d deltaP = new Coord3d();
    Coord3d deltaQ = new Coord3d();
    res--;
    for (int i = 0; i < numP - 1; i++) {
      Coord3d p = points[i];
      Coord3d q = points[i + 1];
      deltaP.set(delta[i]).addSelf(p);
      deltaQ.set(q).subSelf(delta[i + 1]);
      for (int k = 0; k < res; k++) {
        float x = p.x * bernstein.b0[k] + deltaP.x * bernstein.b1[k] + deltaQ.x * bernstein.b2[k]
            + q.x * bernstein.b3[k];
        float y = p.y * bernstein.b0[k] + deltaP.y * bernstein.b1[k] + deltaQ.y * bernstein.b2[k]
            + q.y * bernstein.b3[k];
        float z = p.z * bernstein.b0[k] + deltaP.z * bernstein.b1[k] + deltaQ.z * bernstein.b2[k]
            + q.z * bernstein.b3[k];
        vertices.add(new Coord3d(x, y, z));
      }
    }
    vertices.add(points[points.length - 1]);
    return vertices;
  }

  protected void findCPoints() {
    bi[1] = -tightness;
    coeffA[1].set((points[2].x - points[0].x - delta[0].x) * tightness,
        (points[2].y - points[0].y - delta[0].y) * tightness,
        (points[2].z - points[0].z - delta[0].z) * tightness);
    for (int i = 2; i < numP - 1; i++) {
      bi[i] = -1 / (invTightness + bi[i - 1]);
      coeffA[i].set(-(points[i + 1].x - points[i - 1].x - coeffA[i - 1].x) * bi[i],
          -(points[i + 1].y - points[i - 1].y - coeffA[i - 1].y) * bi[i],
          -(points[i + 1].z - points[i - 1].z - coeffA[i - 1].z) * bi[i]);
    }
    for (int i = numP - 2; i > 0; i--) {
      delta[i].set(coeffA[i].x + delta[i + 1].x * bi[i], coeffA[i].y + delta[i + 1].y * bi[i],
          coeffA[i].z + delta[i + 1].z * bi[i]);
    }
  }

  public List<Coord3d> getDecimatedVertices(float step) {
    List<Coord3d> steps = new ArrayList<Coord3d>();
    int num = vertices.size();
    int i = 0;
    float segLen;
    Coord3d a = null;
    Coord3d b = vertices.get(0);
    Coord3d curr = b.clone();
    Coord3d dir, stepDir;
    while (i < num - 1) {
      a = b;
      b = vertices.get(i + 1);
      dir = b.sub(a);
      segLen = 1f / dir.magSquared();
      stepDir = dir.getNormalizedTo(step);
      curr.set(a.interpolateTo(b, curr.sub(a).dot(dir) * segLen));
      while (curr.sub(a).dot(dir) / segLen <= 1) {
        steps.add(curr.clone());
        curr.addSelf(stepDir);
      }
      i++;
    }
    return steps;
  }

  /**
   * Returns the number of key points.
   * 
   * @return the numP
   */
  public int getNumPoints() {
    return numP;
  }

  /**
   * @return the pointList
   */
  public List<Coord3d> getPointList() {
    return pointList;
  }

  /**
   * @see #setTightness(float)
   * @return the spline3d tightness
   * @since 0014 (rev.216)
   */
  public float getTightness() {
    return tightness;
  }

  /**
   * @param pointList the pointList to set
   */
  public Spline3D setPointList(List<Coord3d> plist) {
    pointList.clear();
    for (Coord3d p : plist) {
      add(p);
    }
    return this;
  }

  /**
   * Sets the tightness for future curve interpolation calls. Default value is 0.25. A value of 0.0
   * equals linear interpolation between the given curve input points. Curve behaviour for values
   * outside the 0.0 .. 0.5 interval is unspecified and becomes increasingly less intuitive.
   * Negative values are possible too and create interesting results (in some cases).
   * 
   * @param tightness the tightness value used for the next call to {@link #computeVertices(int)}
   * @since 0014 (rev. 216)
   */
  public Spline3D setTightness(float tightness) {
    this.tightness = tightness;
    this.invTightness = 1f / tightness;
    return this;
  }

  public void updateCoefficients() {
    numP = pointList.size();
    if (points == null || (points != null && points.length != numP)) {
      coeffA = new Coord3d[numP];
      delta = new Coord3d[numP];
      bi = new float[numP];
      for (int i = 0; i < numP; i++) {
        coeffA[i] = new Coord3d();
        delta[i] = new Coord3d();
      }
      setTightness(tightness);
    }
    points = pointList.toArray(new Coord3d[0]);
  }
}
