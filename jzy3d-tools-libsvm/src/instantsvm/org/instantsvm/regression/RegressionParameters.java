package org.instantsvm.regression;

import org.instantsvm.Parameters;
import libsvm.svm_parameter;

public class RegressionParameters extends Parameters {
  public RegressionParameters() {
    super();
  }

  public RegressionParameters(double eps, double C, double gamma) {
    super(eps, C, gamma);
  }

  @Override
  protected void defaults() {
    super.defaults();
    param.svm_type = svm_parameter.NU_SVR; // epsilon-SVR, nu-SVR
    param.kernel_type = 2;
    param.gamma = 0.5;
    param.C = 10;
    param.eps = 0.1;
  }
}
