package org.instantsvm.regression;

import java.io.IOException;
import java.util.Vector;
import org.instantsvm.utils.LibSvmIO;
import libsvm.svm_node;

public class RegressionInputs {
  public RegressionInputs() {}

  public RegressionInputs(String filename) throws IOException {
    load(filename);
  }

  public RegressionInputs(Vector<svm_node[]> x, Vector<Double> y) {
    setValues(x, y);
  }

  public void setValues(Vector<svm_node[]> x, Vector<Double> y) {
    this.x = x;
    this.y = y;
  }

  public Vector<svm_node[]> getX() {
    return x;
  }

  public Vector<Double> getY() {
    return y;
  }

  public void scale(float xfact, float yfact, float zfact) {
    for (int i = 0; i < x.size(); i++) {
      x.get(i)[0].value *= xfact;
      x.get(i)[1].value *= yfact;
      if (zfact != 1)
        y.set(i, y.get(i) * zfact);
    }
  }



  /** @see {@link LibSvmIO.loadRegression(String filename). */
  public void load(String filename) throws IOException {
    RegressionInputs i = LibSvmIO.loadRegression(filename);
    x = i.x;
    y = i.y;
    i = null;
  }

  protected Vector<svm_node[]> x;
  protected Vector<Double> y;
}
