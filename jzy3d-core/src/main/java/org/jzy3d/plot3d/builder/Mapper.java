package org.jzy3d.plot3d.builder;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;

public abstract class Mapper {
  public abstract double f(double x, double y);

  /**
   * Update the shape by remapping its polygon points' z coordinate according to f(x,y)
   */
  public void remap(Composite shape) {
    List<Drawable> polygons = shape.getDrawables();
    for (Drawable d : polygons) {
      remapDrawable(d);
    }
  }

  public void remapDrawable(Drawable d) {
    if (d instanceof Polygon) {
      Polygon p = (Polygon) d;
      remapPolygon(p);
    }
  }

  public void remapPolygon(Polygon p) {
    for (int i = 0; i < p.size(); i++) {
      Point pt = p.get(i);
      Coord3d c = pt.xyz;
      c.z = (float) f(c.x, c.y);
    }
  }

  public double[] f(double[] x, double[] y) {
    double[] z = new double[x.length];

    for (int i = 0; i < x.length; i++)
      z[i] = f(x[i], y[i]);
    return z;
  }

  public double[] f(double[][] xy) {
    double[] z = new double[xy.length];

    for (int i = 0; i < xy.length; i++)
      z[i] = f(xy[i][0], xy[i][1]);
    return z;
  }

  public float[] fAsFloat(double[] x, double[] y) {
    float[] z = new float[x.length];

    for (int i = 0; i < x.length; i++)
      z[i] = (float) f(x[i], y[i]);
    return z;
  }

  public float[] fAsFloat(float[] x, float[] y) {
    float[] z = new float[x.length];

    for (int i = 0; i < x.length; i++)
      z[i] = (float) f(x[i], y[i]);
    return z;
  }

  public float[] fAsFloat(double[][] xy) {
    float[] z = new float[xy.length];

    for (int i = 0; i < xy.length; i++)
      z[i] = (float) f(xy[i][0], xy[i][1]);
    return z;
  }

  public float[] fAsFloat(float[][] xy) {
    float[] z = new float[xy.length];

    for (int i = 0; i < xy.length; i++)
      z[i] = (float) f(xy[i][0], xy[i][1]);
    return z;
  }
}
