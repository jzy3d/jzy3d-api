package org.instantsvm.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import org.instantsvm.regression.RegressionInputs;
import au.com.bytecode.opencsv.CSVReader;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class LibSvmIO {
  public static final String DIR_DATASETS = "data/datasets/";
  public static final String DIR_MODELS = "data/models/";
  public static final String DEF_INPUT_NAME = "inputs.lsvm";
  public static final String DEF_CSV_INPUT_NAME = "inputs.csv";
  public static final String DEF_MODEL_NAME = "model.lsvm";

  public static String defaultInputPath(String name) {
    return defaultInputPath(name, false);
  }

  public static String defaultInputPath(String name, boolean isCsv) {
    if (isCsv)
      return defaultInputDirectory(name) + LibSvmIO.DEF_CSV_INPUT_NAME;
    else
      return defaultInputDirectory(name) + LibSvmIO.DEF_INPUT_NAME;
  }

  public static String defaultModelPath(String name) {
    return defaultModelDirectory(name) + LibSvmIO.DEF_MODEL_NAME;
  }

  public static String defaultModelDirectory(String name) {
    return LibSvmIO.DIR_MODELS + name + "/";
  }

  public static String defaultInputDirectory(String name) {
    return LibSvmIO.DIR_DATASETS + name + "/";
  }

  /** Load CSV format if file ends with ".csv", or LIBSVM data format otherwise. */
  public static RegressionInputs loadRegression(String filename) throws IOException {
    if (filename.endsWith(".csv")) {
      return loadRegressionCsv(filename);
    } else {
      return loadRegressionOriginalFormat(filename);
    }
  }

  /**************************************************************/

  public static RegressionInputs loadRegressionOriginalFormat(String filename) throws IOException {
    BufferedReader fp = new BufferedReader(new FileReader(filename));
    Vector<Double> vy = new Vector<Double>();
    Vector<svm_node[]> vx = new Vector<svm_node[]>();

    while (true) {
      String line = fp.readLine();
      if (line == null)
        break;

      StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

      // load y value
      vy.addElement(atof(st.nextToken()));

      // load x vector
      int m = st.countTokens() / 2;
      svm_node[] x = new svm_node[m];
      for (int j = 0; j < m; j++) {
        x[j] = new svm_node();
        x[j].index = atoi(st.nextToken());
        x[j].value = atof(st.nextToken());
      }
      vx.addElement(x);
    }
    fp.close();

    return new RegressionInputs(vx, vy);
  }

  public static svm_model loadModel(String filename) throws IOException {
    return svm.svm_load_model(filename);
  }

  protected static double atof(String s) {
    double d = Double.valueOf(s).doubleValue();
    if (Double.isNaN(d) || Double.isInfinite(d))
      System.err.print("NaN or Infinity in input\n");
    return (d);
  }

  protected static int atoi(String s) {
    return Integer.parseInt(s);
  }

  /******************************************************/

  public static RegressionInputs loadRegressionCsv(String filename) throws IOException {
    CSVReader reader = new CSVReader(new FileReader(filename));
    String[] nextLine;

    Vector<svm_node[]> x = new Vector<svm_node[]>();
    Vector<Double> y = new Vector<Double>();
    while ((nextLine = reader.readNext()) != null) {
      int m = nextLine.length;

      svm_node[] xk = new svm_node[m - 1];
      for (int i = 0; i < m - 1; i++)
        xk[i] = Conversion.toVector(i, Double.parseDouble(nextLine[i]));

      x.add(xk);
      y.add(Double.parseDouble(nextLine[m - 1]));
    }
    reader.close();
    return new RegressionInputs(x, y);
  }

  protected static int readNLines(String filename) throws IOException {
    CSVReader reader = new CSVReader(new FileReader(filename));
    int n = 0;
    while (reader.readNext() != null)
      n++;
    reader.close();
    return n;
  }
}
