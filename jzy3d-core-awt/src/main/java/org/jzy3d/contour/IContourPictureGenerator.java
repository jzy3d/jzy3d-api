package org.jzy3d.contour;

import java.awt.image.BufferedImage;

public interface IContourPictureGenerator extends IContourGenerator {
  public BufferedImage getContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      int nLevels);

  public BufferedImage getContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      double[] sortedLevels);

  public BufferedImage getFilledContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      int nLevels);

  public BufferedImage getHeightMap(IContourColoringPolicy policy, int xRes, int yRes, int nLevels);
}
