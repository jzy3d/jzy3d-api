package org.jzy3d.svm.tesselation;

import java.util.Vector;
import org.instantsvm.Parameters;
import org.instantsvm.SVM;
import org.instantsvm.regression.RegressionSVM;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.svm.utils.Conversion;
import libsvm.svm_node;


public class SvmMapper extends Mapper {

  public SvmMapper(Coord3d[] input) {
    this(input, new Parameters());
  }

  public SvmMapper(Coord3d[] input, Parameters parameters) {
    params = parameters;
    svm = new SVM();
    svm.train(Conversion.copyInputs(input), Conversion.copyTargets(input), params);
  }

  public SvmMapper(RegressionSVM svm) {
    this.svm = svm;
  }

  public SVM getSvm() {
    return svm;
  }

  @Override
  public double f(double x, double y) {
    output = svm.apply(Conversion.toDataset(Conversion.toVector(x, y)));
    return output[0];
  }


  public double[] f(Vector<svm_node[]> nodes) {
    output = svm.apply(nodes);
    return output;
  }

  public double[] getOutput() {
    return output;
  }

  protected double[] output;
  protected Parameters params;
  protected SVM svm;
}
