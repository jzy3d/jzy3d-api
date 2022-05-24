package org.jzy3d.performance.polygons;

public interface BenchmarkXLS {
  int TIME = 0;
  int POLYGONS = 1;
  int WIDTH = 2;
  int HEIGHT = 3;
  
  String SHEET_CONFIG = "config";
  String SHEET_BENCHMARK = "benchmark";
  
  String outputFolder = "./data";

}
