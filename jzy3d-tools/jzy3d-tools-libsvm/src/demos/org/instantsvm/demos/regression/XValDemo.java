package org.instantsvm.demos.regression;

import java.io.IOException;
import org.instantsvm.XValResult;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionParameters;
import org.instantsvm.regression.RegressionSVM;
import org.instantsvm.utils.LibSvmConsole;
import org.instantsvm.utils.LibSvmIO;

public class XValDemo {
  public static void main(String[] args) throws IOException {
    regressionTrial("radial");
  }

  public static void regressionTrial(String name) throws IOException {
    RegressionInputs inputs = LibSvmIO.loadRegression(LibSvmIO.DIR_DATASETS + name + "/inputs.csv");
    RegressionParameters p = new RegressionParameters();

    scale(inputs, 1000, 1000, 1);

    // Train and save
    RegressionSVM svm = new RegressionSVM();
    XValResult r = svm.xval(inputs.getX(), inputs.getY(), p, 3, 0, 10000, 6, 0, 5, 6);
    System.out.println("Best parameters:" + r);
    LibSvmConsole.print(r.errors);
    /*
     * svm.train(inputs, p); svm.save(LibSvmIO.DIR_MODELS + name + "/", "model.lsvm"); svm = null;
     * 
     * // Reload and apply svm = new RegressionSVM(LibSvmIO.DIR_MODELS + name + "/model.lsvm"); //
     * TODO: does not reload params correctly
     * 
     * // Print results and parameters LibSvmConsole.print( svm.apply( inputs.getX() ) );
     * System.out.println(); p.print(); svm.getParameters().print();
     */
  }

  public static void scale(RegressionInputs inputs, float xfact, float yfact, float zfact) {
    for (int i = 0; i < inputs.getX().size(); i++) {
      inputs.getX().get(i)[0].value *= xfact;
      inputs.getX().get(i)[1].value *= yfact;
      inputs.getY().set(i, inputs.getY().get(i) * zfact);
    }
  }
}
