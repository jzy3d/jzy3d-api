package org.instantsvm.utils;

import java.io.File;
import java.io.IOException;
import org.instantsvm.Parameters;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionSVM;

/**
 * LazySVM either reload an existing model, or train and save a model in InstantSVM library.
 * 
 * @author Martin Pernollet
 */
public class LazySVM {
  public static RegressionSVM loadOrTrainRegression(String name, Parameters params)
      throws IOException {
    return loadOrTrainRegression(name, params,
        new RegressionInputs(LibSvmIO.defaultInputPath(name)));
  }

  public static RegressionSVM loadOrTrainRegression(String name, Parameters params,
      RegressionInputs inputs) throws IOException {
    String modelFile = LibSvmIO.defaultModelPath(name);
    File f = new File(modelFile);

    if (f.exists())
      return new RegressionSVM(modelFile);
    else
      return trainAndSaveRegression(name, inputs);
  }

  public static RegressionSVM trainAndSaveRegression(String name, RegressionInputs inputs)
      throws IOException {
    RegressionSVM svm = new RegressionSVM();
    svm.train(inputs);
    svm.save(LibSvmIO.DIR_MODELS + name + "/", LibSvmIO.DEF_MODEL_NAME);
    return svm;
  }

  public static RegressionSVM trainAndSaveRegression(String name, Parameters params,
      RegressionInputs inputs) throws IOException {
    RegressionSVM svm = new RegressionSVM();
    svm.train(inputs, params);
    svm.save(LibSvmIO.DIR_MODELS + name + "/", LibSvmIO.DEF_MODEL_NAME);
    return svm;
  }
}
