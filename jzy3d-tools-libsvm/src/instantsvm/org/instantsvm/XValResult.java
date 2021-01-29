package org.instantsvm;

public class XValResult {
  public XValResult(double[][] errors, double bestC, double bestG) {
    this.errors = errors;
    this.bestC = bestC;
    this.bestG = bestG;
  }

  @Override
  public String toString() {
    return "C = " + bestC + " gamma=" + bestG;
  }

  public double bestC;
  public double bestG;
  public double[][] errors;

}
