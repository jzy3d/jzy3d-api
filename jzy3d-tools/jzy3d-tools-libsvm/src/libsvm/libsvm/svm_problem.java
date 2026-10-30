package libsvm;

public class svm_problem implements java.io.Serializable {
  private static final long serialVersionUID = -4451389443706847272L;
  public int l;
  public double[] y;
  public svm_node[][] x;
}
