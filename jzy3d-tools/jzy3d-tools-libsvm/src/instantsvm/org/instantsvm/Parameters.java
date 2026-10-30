package org.instantsvm;

import org.instantsvm.utils.LibSvmConsole;
import libsvm.svm_parameter;

/**
 * options: -s svm_type : set type of SVM (default 0) 0 -- C-SVC 1 -- nu-SVC 2 -- one-class SVM 3 --
 * epsilon-SVR 4 -- nu-SVR -t kernel_type : set type of kernel function (default 2) 0 -- linear:
 * u'*v 1 -- polynomial: (gamma*u'*v + coef0)^degree 2 -- radial basis function: exp(-gamma*|u-v|^2)
 * 3 -- sigmoid: tanh(gamma*u'*v + coef0) 4 -- precomputed kernel (kernel values in
 * training_set_file) -d degree : set degree in kernel function (default 3) -g gamma : set gamma in
 * kernel function (default 1/num_features) -r coef0 : set coef0 in kernel function (default 0) -c
 * cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1) -n nu : set the
 * parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5) -p epsilon : set the epsilon in
 * loss function of epsilon-SVR (default 0.1) -m cachesize : set cache memory size in MB (default
 * 100) -e epsilon : set tolerance of termination criterion (default 0.001) -h shrinking : whether
 * to use the shrinking heuristics, 0 or 1 (default 1) -b probability_estimates : whether to train a
 * SVC or SVR model for probability estimates, 0 or 1 (default 0) -wi weight : set the parameter C
 * of class i to weight*C, for C-SVC (default 1) -v n : n-fold cross validation mode -q : quiet mode
 * (no outputs)
 */
public class Parameters {
  public Parameters() {
    param = new svm_parameter();
    defaults();
  }

  public Parameters(svm_parameter param) {
    this.param = param;
  }

  public Parameters(double eps, double C, double gamma) {
    param = new svm_parameter();
    defaults();
    param.eps = eps;
    param.C = C;
    param.gamma = gamma;
  }

  public void setParam(svm_parameter param) {
    this.param = param;
  }

  public svm_parameter getParam() {
    return param;
  }

  protected void defaults() {
    param.svm_type = svm_parameter.C_SVC;
    param.kernel_type = svm_parameter.RBF;
    param.degree = 3;
    param.gamma = 0; // 1/num_features
    param.coef0 = 0;
    param.nu = 0.5;
    param.cache_size = 100;
    param.C = 1;
    param.eps = 1e-3;
    param.p = 0.1;
    param.shrinking = 0;
    param.probability = 0;
    param.nr_weight = 0;
    param.weight_label = new int[0];
    param.weight = new double[0];
  }

  public void print() {
    System.out.println(this.getClass().getSimpleName() + ":");
    LibSvmConsole.print(param);
  }

  protected svm_parameter param;
}
