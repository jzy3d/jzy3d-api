package org.instantsvm.utils;

import libsvm.svm_node;
import libsvm.svm_parameter;

public class LibSvmConsole {
  public static void print(svm_parameter param) {
    System.out.println("svm_type = " + param.svm_type);
    System.out.println("kernel_type = " + param.kernel_type);
    System.out.println("degree = " + param.degree);
    System.out.println("gamma = " + param.gamma); // 1/num_features
    System.out.println("coef0 = " + param.coef0);
    System.out.println("nu = " + param.nu);
    System.out.println("cache_size = " + param.cache_size);
    System.out.println("C = " + param.C);
    System.out.println("eps = " + param.eps);
    System.out.println("p = " + param.p);
    System.out.println("shrinking = " + param.shrinking);
    System.out.println("probability = " + param.probability);
    System.out.println("nr_weight = " + param.nr_weight);

    System.out.print("weight_label = ");
    if (param.weight_label != null)
      LibSvmConsole.print(param.weight_label);
    else
      System.out.println("null");

    System.out.print("weight = ");
    if (param.weight != null)
      LibSvmConsole.print(param.weight);
    else
      System.out.println("null");
  }

  public static void print(svm_node[][] nodes, double value) {
    for (int i = 0; i < nodes.length; i++)
      print(nodes[i]);

    System.out.println("| " + value);
  }

  public static void print(svm_node[][] nodes) {
    for (int i = 0; i < nodes.length; i++)
      println(nodes[i]);
  }

  public static void println(svm_node[] nodes) {
    print(nodes);
    System.out.println();
  }

  public static void print(svm_node[] nodes) {
    for (int i = 0; i < nodes.length; i++)
      System.out.print(nodes[i].value + " | ");
  }

  /*********************************************************************/

  public static void print(double input[]) {
    for (int i = 0; i < input.length; i++) {
      System.out.print(input[i] + "|");
    }
    System.out.println("");
  }

  public static void print(float input[]) {
    for (int i = 0; i < input.length; i++) {
      System.out.print(input[i] + "|");
    }
    System.out.println("");
  }

  public static void print(int input[]) {
    for (int i = 0; i < input.length; i++) {
      System.out.print(input[i] + "|");
    }
    System.out.println("");
  }

  public static void print(double input[][]) {
    for (int i = 0; i < input.length; i++) {
      for (int j = 0; j < input[i].length; j++) {
        System.out.print(input[i][j] + "\t");
      }
      System.out.println();
    }
  }

  public static void print(float input[][]) {
    for (int i = 0; i < input.length; i++) {
      for (int j = 0; j < input[i].length; j++) {
        System.out.print(input[i][j] + "\t");
      }
      System.out.println();
    }
  }

  public static void print(int input[][]) {
    for (int i = 0; i < input.length; i++) {
      for (int j = 0; j < input[i].length; j++) {
        System.out.print(input[i][j] + "\t");
      }
      System.out.println();
    }
  }
}
