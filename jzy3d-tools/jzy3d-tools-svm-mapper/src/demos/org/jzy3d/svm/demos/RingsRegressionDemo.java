package org.jzy3d.svm.demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.instantsvm.Parameters;
import org.instantsvm.XValResult;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionParameters;
import org.instantsvm.regression.RegressionSVM;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.svm.utils.Conversion;
import libsvm.svm_parameter;

public class RingsRegressionDemo extends Abstract3dDemo {
  public static void main(String[] args) throws IOException {
    RegressionInputs inputs = Conversion.toRegressionInputs(getInputs());
    Parameters params = getParams();

    RegressionSVM svm = new RegressionSVM();
    XValResult r = svm.xval(inputs.getX(), inputs.getY(), params, 3, 0, 10000, 6, 0, 5, 6);
    System.out.println("Best parameters:" + r);
    Array.print(r.errors);

    params.getParam().C = r.bestC;
    params.getParam().gamma = r.bestG;
    svm.train(inputs, params);

    System.out.println("Maybe reused a trained svm!");
    openChart(getRegressionChart(svm, inputs));
  }

  public static Coord3d[] getInputs() {
    double[] radius = {0.2, 0.5, 0.8};
    double[] height = {0, 0.5, -0.5};
    int n[] = {12, 12, 12};
    return Conversion.toArray(generate(radius, height, n));
  }

  public static List<Coord3d> generate(double[] radius, double height[], int[] n) {
    List<Coord3d> rings = new ArrayList<Coord3d>();
    for (int i = 0; i < n.length; i++) {
      double r = radius[i];
      double h = height[i];
      double t = 0;
      double step = 2 * Math.PI / n[i];
      while (t < (2 * Math.PI)) {
        Coord3d c = new Coord3d(t, 0, r).cartesian();
        c.z += h;
        rings.add(c);

        t += step;
      }
    }
    return rings;
  }

  public static Parameters getParams() {
    Parameters params = new RegressionParameters(0.01, 100, 0.5);

    if (false) {
      params.getParam().svm_type = svm_parameter.EPSILON_SVR;
      params.getParam().eps = 0.01;
      params.getParam().C = 1000;
      params.getParam().probability = 1;
      params.getParam().gamma = 0.5;

      // params.getParam().coef0 = 0.5;
      // params.getParam().degree = 10;
      // params.getParam().p = 0.00001;
      // params.getParam().gamma = 1;
      // params.getParam().shrinking = 0;
    }
    return params;
  }


}
