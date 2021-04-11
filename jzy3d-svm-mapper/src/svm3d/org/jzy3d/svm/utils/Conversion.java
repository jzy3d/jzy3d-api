package org.jzy3d.svm.utils;

import java.util.List;
import java.util.Vector;
import org.instantsvm.regression.RegressionInputs;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import libsvm.svm_node;

/** Helpers to convert Jzy3d datatypes to libsvm datatypes */
public class Conversion extends org.instantsvm.utils.Conversion {
  public static Vector<svm_node[]> copyInputs(Coord3d[] input) {
    Vector<svm_node[]> inputs = new Vector<svm_node[]>(input.length);
    for (int i = 0; i < input.length; i++)
      inputs.add(toVector(input[i]));
    return inputs;
  }

  public static Vector<Double> copyTargets(Coord3d[] input) {
    Vector<Double> targets = new Vector<Double>(input.length);
    for (int i = 0; i < input.length; i++)
      targets.add((double) input[i].z);
    return targets;
  }

  public static Vector<svm_node[]> toDataset(Range xrange, Range yrange, int xsteps, int ysteps) {
    double xstep = xrange.getRange() / (double) (xsteps - 1);
    double ystep = yrange.getRange() / (double) (ysteps - 1);

    Vector<svm_node[]> output = new Vector<svm_node[]>(xsteps * ysteps);
    for (int xi = 0; xi < xsteps; xi++) {
      for (int yi = 0; yi < ysteps; yi++) {
        double x = xrange.getMin() + xi * xstep;
        double y = yrange.getMin() + yi * ystep;
        output.add(toVector(x, y));
      }
    }
    return output;
  }

  public static BoundingBox3d getBounds(Coord3d[] coords) {
    BoundingBox3d box = new BoundingBox3d();
    for (int i = 0; i < coords.length; i++) {
      box.add(coords[i]);
    }
    return box;
  }

  public static Coord3d[] toArray(List<Coord3d> list) {
    Coord3d[] points = new Coord3d[list.size()];
    int k = 0;
    for (Coord3d c : list) {
      points[k++] = c;
    }
    return points;
  }

  public static RegressionInputs toRegressionInputs(List<Coord3d> list) {
    return new RegressionInputs(toXVector(list), toYVector(list));
  }

  public static RegressionInputs toRegressionInputs(Coord3d[] list) {
    return new RegressionInputs(toXVector(list), toYVector(list));
  }

  public static svm_node[] toVector(Coord3d c) {
    svm_node[] vec = new svm_node[2];
    vec[0] = toVector(0, c.x);
    vec[1] = toVector(1, c.y);
    return vec;
  }

  public static Vector<svm_node[]> toXVector(List<Coord3d> list) {
    Vector<svm_node[]> v = new Vector<svm_node[]>(list.size());
    for (Coord3d c : list)
      v.add(toVector(c));
    return v;
  }

  public static Vector<Double> toYVector(List<Coord3d> list) {
    Vector<Double> v = new Vector<Double>(list.size());
    for (Coord3d c : list)
      v.add((double) c.z);
    return v;
  }

  public static Vector<svm_node[]> toXVector(Coord3d[] list) {
    Vector<svm_node[]> v = new Vector<svm_node[]>(list.length);
    for (Coord3d c : list)
      v.add(toVector(c));
    return v;
  }

  public static Vector<Double> toYVector(Coord3d[] list) {
    Vector<Double> v = new Vector<Double>(list.length);
    for (Coord3d c : list)
      v.add((double) c.z);
    return v;
  }

  public static Coord3d[] toCoord3d(RegressionInputs inputs) {
    Vector<svm_node[]> x = inputs.getX();
    Vector<Double> y = inputs.getY();

    int n = x.size();

    Coord3d[] output = new Coord3d[n];
    for (int i = 0; i < n; i++)
      output[i] = new Coord3d(x.get(i)[0].value, x.get(i)[1].value, y.get(i));
    return output;
  }
}
