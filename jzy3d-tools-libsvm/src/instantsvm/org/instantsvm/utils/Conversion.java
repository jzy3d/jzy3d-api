package org.instantsvm.utils;

import java.util.Vector;
import libsvm.svm_node;

public class Conversion {
  public static Vector<svm_node[]> toDataset(svm_node[] node) {
    Vector<svm_node[]> v = new Vector<svm_node[]>(1);
    v.add(node);
    return v;
  }

  public static svm_node[] toVector(double x, double y) {
    svm_node[] vec = new svm_node[2];
    vec[0] = toVector(0, x);
    vec[1] = toVector(1, y);
    return vec;
  }

  public static svm_node toVector(int index, double value) {
    svm_node node = new svm_node();
    node.index = index;
    node.value = value;
    return node;
  }

}
