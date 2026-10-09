package org.jzy3d.contour;

import java.awt.image.BufferedImage;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;


/**
 * Computes the contour matrix of a {@link Mapper}, and returns it as a {@link BufferedImage}.
 * 
 * @author Juan Barandiaran
 * @author Martin Pernollet
 */
public class MapperContourPictureGenerator extends AbstractContourGenerator
    implements IContourPictureGenerator {
  public static int PIXEL_NEIGHBOUR_THRESHOLD = 2;
  public static float LINE_STRIP_WIDTH = 2;
  public static int MERGE_STRIP_DIST = 1;

  public MapperContourPictureGenerator(Mapper mapper, Range xrange, Range yrange) {
    this.mapper = mapper;
    this.xrange = xrange;
    this.yrange = yrange;
  }

  /**********************************************/

  @Override
  public double[][] getContourMatrix(int xRes, int yRes, int nLevels) {
    return computeContour(xRes, yRes, nLevels);
  }

  @Override
  public BufferedImage getContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      int nLevels) {
    double[][] contours = computeContour(xRes, yRes, nLevels);
    return buildImage(xRes, yRes, contours, policy);
  }

  @Override
  public BufferedImage getContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      double[] sortedLevels) {
    double[][] contours = computeContour(xRes, yRes, sortedLevels);
    return buildImage(xRes, yRes, contours, policy);
  }

  @Override
  public BufferedImage getFilledContourImage(IContourColoringPolicy policy, int xRes, int yRes,
      int nLevels) {
    double[][] contours = computeFilledContour(xRes, yRes, nLevels);
    return buildImage(xRes, yRes, contours, policy);
  }

  @Override
  public BufferedImage getHeightMap(IContourColoringPolicy policy, int xRes, int yRes,
      int nLevels) {
    double[][] contours = computeXYColors(xRes, yRes, nLevels);
    return buildImage(xRes, yRes, contours, policy);
  }

  /**********************************************/

  protected BufferedImage buildImage(int xRes, int yRes, double[][] contours,
      IContourColoringPolicy policy) {
    // Build an image with a size
    BufferedImage image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < xRes; x++)
      for (int y = 0; y < yRes; y++)
        image.setRGB(x, y, policy.getRGB(contours[x][y]));
    return image;
  }

  /**********************************************/

  /**
   * Calculates the Height of the surface for each point in the XY plane
   * 
   **/
  @Override
  protected void computeHeightMatrix(double matrix[][], int xRes, int yRes) {
    double value;
    minValue = Double.MAX_VALUE;
    maxValue = -Double.MAX_VALUE;

    // TODO: This matrix filling could be done at the same time the surface
    // is computed to save time and avoid calling the mapper.f() two times
    // for each point.
    double xstep = xrange.getRange() / (double) (xRes - 1);
    double ystep = yrange.getRange() / (double) (yRes - 1);
    for (int xi = 0; xi < xRes; xi++) {
      for (int yi = 0; yi < yRes; yi++) {
        double x = xrange.getMin() + xi * xstep;
        double y = yrange.getMin() + yi * ystep;

        value = mapper.f(x, y);

        // Y coordinate in the matrix is inverted because
        // DrawableTexture
        // is handled like this
        matrix[xi][yRes - 1 - yi] = value;
        if (value < minValue)
          minValue = value;
        if (value > maxValue)
          maxValue = value;
      }
    }
  }

  protected Mapper mapper;
  protected Range xrange;
  protected Range yrange;

}
