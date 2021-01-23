package org.jzy3d.maths.algorithms.interpolation.algorithms;

/**
 * Helper class for the spline3d classes in this package. Used to compute subdivision points of the
 * curve.
 * 
 * @author toxi
 * 
 */
public class BernsteinPolynomial {
  public float[] b0, b1, b2, b3;
  public int resolution;

  /**
   * @param res number of subdivision steps between each control point of the spline3d
   */
  public BernsteinPolynomial(int res) {
    resolution = res;
    b0 = new float[res];
    b1 = new float[res];
    b2 = new float[res];
    b3 = new float[res];
    float t = 0;
    float dt = 1.0f / (resolution - 1);
    for (int i = 0; i < resolution; i++) {
      float t1 = 1 - t;
      float t12 = t1 * t1;
      float t2 = t * t;
      b0[i] = t1 * t12;
      b1[i] = 3 * t * t12;
      b2[i] = 3 * t2 * t1;
      b3[i] = t * t2;
      t += dt;
    }
  }
}
