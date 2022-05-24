package org.instantsvm.demos.regression;

import java.io.IOException;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionParameters;
import org.instantsvm.regression.RegressionSVM;
import org.instantsvm.utils.LibSvmConsole;
import org.instantsvm.utils.LibSvmIO;

public class RegressionDemo {
  public static void main(String[] args) throws IOException {
    regressionTrial("bodyfat");
  }

  public static void regressionTrial(String name) throws IOException {
    RegressionInputs inputs =
        LibSvmIO.loadRegression(LibSvmIO.DIR_DATASETS + name + "/inputs.lsvm");
    RegressionParameters p = new RegressionParameters();

    // Train and save
    RegressionSVM svm = new RegressionSVM();
    svm.train(inputs, p);
    svm.save(LibSvmIO.DIR_MODELS + name + "/", "model.lsvm");
    svm = null;

    // Reload and apply
    svm = new RegressionSVM(LibSvmIO.DIR_MODELS + name + "/model.lsvm"); // TODO: does not reload
                                                                         // params correctly

    // Print results and parameters
    LibSvmConsole.print(svm.apply(inputs.getX()));
    System.out.println();
    p.print();
    svm.getParameters().print();
  }
}
