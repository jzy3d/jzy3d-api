package org.jzy3d.contour;

public interface IContourGenerator {
  public double[][] getContourMatrix(int xRes, int yRes, int nLevels);
}
