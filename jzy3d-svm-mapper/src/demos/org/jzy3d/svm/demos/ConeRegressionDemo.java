package org.jzy3d.svm.demos;

import java.io.IOException;
import org.instantsvm.Parameters;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionParameters;
import org.instantsvm.regression.RegressionSVM;
import org.instantsvm.utils.LazySVM;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.concrete.SphereScatterGenerator;
import org.jzy3d.svm.utils.Conversion;
import libsvm.svm_parameter;

public class ConeRegressionDemo extends Abstract3dDemo {
  public static void main(String[] args) throws IOException {
    RegressionInputs inputs = Conversion.toRegressionInputs(getInputs());
    Parameters params = getParams();

    RegressionSVM svm = LazySVM.loadOrTrainRegression("generated", params, inputs);

    System.out.println("Maybe reused a trained svm!");
    openChart(getRegressionChart(svm, inputs));
  }

  public static Coord3d[] getInputs() {
    return Conversion.toArray(SphereScatterGenerator.generate(new Coord3d(0, 0, 0), 1, 20, true));
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
