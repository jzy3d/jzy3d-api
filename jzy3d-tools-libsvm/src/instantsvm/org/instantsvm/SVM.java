package org.instantsvm;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import org.instantsvm.utils.LibSvmConsole;
import org.instantsvm.utils.LibSvmIO;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVM {
  public SVM() {}

  public SVM(String filename) throws IOException {
    this.load(filename);
  }

  public SVM(svm_model model) {
    mountModel(model);
  }

  public void train(Vector<svm_node[]> vx, Vector<Double> vy) {
    train(vx, vy, getDefaultParameters());
  }

  public void train(Vector<svm_node[]> vx, Vector<Double> vy, Parameters parameters) {
    this.parameters = parameters;
    this.param = parameters.getParam();

    load(vx, vy);
    model = svm.svm_train(prob, param);
    parameters.setParam(param);
  }

  public XValResult xval(Vector<svm_node[]> vx, Vector<Double> vy, Parameters parameters,
      int nrfold, double cMin, double cMax, int cSteps, double gMin, double gMax, int gSteps) {
    this.parameters = parameters;
    this.param = parameters.getParam();

    load(vx, vy);


    double[][] errors = new double[cSteps][gSteps];
    double cRange = cMax - cMin;
    double gRange = gMax - gMin;
    double cStep = cRange / cSteps;
    double gStep = gRange / gSteps;
    double bestC = 0;
    double bestG = 0;

    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < cSteps; i++) {
      for (int j = 0; j < gSteps; j++) {
        param.C = cMin + cStep * i;
        param.gamma = gMin + gStep * j;

        errors[i][j] = do_cross_validation(nrfold);

        if (errors[i][j] < min) {
          min = errors[i][j];
          bestC = param.C;
          bestG = param.gamma;
        }
      }
    }

    return new XValResult(errors, bestC, bestG);
  }

  public double[] apply(Vector<svm_node[]> x) {
    return predict(x, null, 0);
  }

  public Parameters getParameters() {
    return parameters;
  }

  public svm_node[][] getSupportVectors() {
    return model.SV;
  }

  public double[][] getCoefs() {
    return model.sv_coef;
  }

  public void save(String directory, String filename) throws IOException {
    File dir = new File(directory);
    if (!dir.exists())
      dir.mkdirs();
    svm.svm_save_model(directory + filename, model);
  }

  public void save(String filename) throws IOException {
    svm.svm_save_model(filename, model);
  }

  public void load(String filename) throws IOException {
    mountModel(LibSvmIO.loadModel(filename));
  }

  public void print() {
    System.out.println("Support vectors (" + model.SV.length + "):");
    LibSvmConsole.print(model.SV);
    System.out.println("Coefficients (" + model.sv_coef.length + "):");
    LibSvmConsole.print(model.sv_coef);
  }

  public Parameters getDefaultParameters() {
    return new Parameters();
  }

  /*******************************************************/

  protected void mountModel(svm_model model) {
    this.model = model;
    this.parameters = new Parameters(model.param);
  }

  protected void load(Vector<svm_node[]> vx, Vector<Double> vy) {
    int max_index = 0;

    prob = new svm_problem();
    prob.l = vy.size();
    prob.x = new svm_node[prob.l][];
    for (int i = 0; i < prob.l; i++)
      prob.x[i] = vx.elementAt(i);
    prob.y = new double[prob.l];
    for (int i = 0; i < prob.l; i++)
      prob.y[i] = vy.elementAt(i);

    if (param.gamma == 0 && max_index > 0)
      param.gamma = 1.0 / max_index;

    if (param.kernel_type == svm_parameter.PRECOMPUTED)
      for (int i = 0; i < prob.l; i++) {
        if (prob.x[i][0].index != 0) {
          System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
        }
        if ((int) prob.x[i][0].value <= 0 || (int) prob.x[i][0].value > max_index) {
          System.err.print("Wrong input format: sample_serial_number out of range\n");
        }
      }
  }

  protected double[] predict(Vector<svm_node[]> x, double[] targets, int predict_probability) {
    double[] output = new double[x.size()];

    int correct = 0;
    int total = 0;
    double error = 0;
    double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

    int svm_type = svm.svm_get_svm_type(model);
    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;

    if (predict_probability == 1) {
      if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
        System.out.print(
            "Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
                + svm.svm_get_svr_probability(model) + "\n");
      } else {
        int[] labels = new int[nr_class];
        svm.svm_get_labels(model, labels);
        prob_estimates = new double[nr_class];
        // output.writeBytes("labels");
        // for(int j=0;j<nr_class;j++)
        // output.writeBytes(" "+labels[j]);
        // output.writeBytes("\n");
      }
    }

    for (int i = 0; i < x.size(); i++) {
      double v;
      if (predict_probability == 1
          && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
        v = svm.svm_predict_probability(model, x.get(i), prob_estimates);
        // output.writeBytes(v+" ");
        // for(int j=0;j<nr_class;j++)
        // output.writeBytes(prob_estimates[j]+" ");
        // output.writeBytes("\n");
      } else {
        v = svm.svm_predict(model, x.get(i));
        // output.writeBytes(v+"\n");
        // System.out.println(x.get(i)[0].value + " " + x.get(i)[1].value + " " + v);
      }

      output[i] = v;

      if (targets != null) {
        if (v == targets[i])
          ++correct;
        error += (v - targets[i]) * (v - targets[i]);
        sumv += v;
        sumy += targets[i];
        sumvv += v * v;
        sumyy += targets[i] * targets[i];
        sumvy += v * targets[i];
        ++total;
      }

    }
    if (true) {
      if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
        System.out.print("Mean squared error = " + error / total + " (regression)\n");
        System.out.print("Squared correlation coefficient = "
            + ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy))
                / ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy))
            + " (regression)\n");
      } else
        System.out.print("Accuracy = " + (double) correct / total * 100 + "% (" + correct + "/"
            + total + ") (classification)\n");
    }

    return output;
  }

  protected double do_cross_validation(int nr_fold) {
    int i;
    int total_correct = 0;
    double total_error = 0;
    double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
    double[] target = new double[prob.l];

    svm.svm_cross_validation(prob, param, nr_fold, target);
    if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR) {
      for (i = 0; i < prob.l; i++) {
        double y = prob.y[i];
        double v = target[i];
        total_error += (v - y) * (v - y);
        sumv += v;
        sumy += y;
        sumvv += v * v;
        sumyy += y * y;
        sumvy += v * y;
      }
      System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
      System.out.print("Cross Validation Squared correlation coefficient = "
          + ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
              / ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy))
          + "\n");
      return total_error;
    } else {
      for (i = 0; i < prob.l; i++)
        if (target[i] == prob.y[i])
          ++total_correct;
      System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / prob.l + "%\n");

      return 100.0 * total_correct / prob.l;
    }
  }

  /****************************/

  protected svm_parameter param;
  protected svm_problem prob;
  protected svm_model model;
  // protected int cross_validation;
  // protected int nr_fold;
  protected Parameters parameters;
}
