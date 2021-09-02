package org.jzy3d.performance.polygons;

public class BenchmarkUtils {
  public static String getExcelFilename(String outputFolder, int stepMin, int stepMax,
      String info) {
    return outputFolder + "/bench-" + stepMin + "-" + stepMax + "-" + info + ".xlsx";
  }
}
