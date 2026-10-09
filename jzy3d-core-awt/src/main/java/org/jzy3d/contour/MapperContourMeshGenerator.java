package org.jzy3d.contour;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.contour.ContourMesh;


/**
 * Computes the contour matrix of a {@link Mapper}, and returns it as a {@link ContourMesh}.
 * 
 * @author Juan Barandiaran
 * @author Martin Pernollet
 */
public class MapperContourMeshGenerator extends AbstractContourGenerator
    implements IContourMeshGenerator {
  public static int PIXEL_NEIGHBOUR_THRESHOLD = 2;
  public static float LINE_STRIP_WIDTH = 2;
  public static int MERGE_STRIP_DIST = 1;

  public MapperContourMeshGenerator(Mapper mapper, Range xrange, Range yrange) {
    this.mapper = mapper;
    this.xrange = xrange;
    this.yrange = yrange;
  }

  /**
   * Extracts contour lines from the contour dots matrix of nLevels number of contours. Return a
   * MeshContour that cleanly describes
   * 
   **/
  @Override
  public ContourMesh getContourMesh(IContourColoringPolicy policy, int xRes, int yRes, int nLevels,
      float planeAxe, boolean writeText) {
    double[][] contours = computeContour(xRes, yRes, nLevels);
    return computeMesh(policy, xRes, yRes, contours, planeAxe);
  }

  /**
   * Extracts contour lines from the contour dots matrix of contours at user-defined Heigths in
   * sortedLevels
   * 
   **/
  @Override
  public ContourMesh getContourMesh(IContourColoringPolicy policy, int xRes, int yRes,
      double sortedLevels[], float planeAxe, boolean writeText) {
    for (int n = 1; n < sortedLevels.length; n++) {
      if (sortedLevels[n] < sortedLevels[n - 1]) {
        throw new RuntimeException(
            "Levels sent to getContourStrips() are not in order from Min to Max");
      }
    }
    double[][] contours = computeContour(xRes, yRes, sortedLevels);
    return computeMesh(policy, xRes, yRes, contours, planeAxe);
  }

  @Override
  public double[][] getContourMatrix(int xRes, int yRes, int nLevels) {
    return computeContour(xRes, yRes, nLevels);
  }

  /**********************************************/

  protected ContourMesh computeMesh(IContourColoringPolicy policy, int xRes, int yRes,
      double[][] contours, float planeAxe) {
    boolean[][] processed = new boolean[xRes][yRes];
    // List<AbstractDrawable> strips = new ArrayList<AbstractDrawable>();
    ContourMesh mesh = new ContourMesh();

    for (int i = 0; i < contours.length; i++) {
      for (int j = 0; j < contours[i].length; j++) {
        if (!processed[i][j] && (contours[i][j] != NON_CONTOUR)) {
          double level = contours[i][j];

          // Compute a line part for this contour
          LineStrip strip = followContourFrom((DefaultContourColoringPolicy) policy, i, j, contours,
              processed, planeAxe);
          strip.setWidth(LINE_STRIP_WIDTH);
          // Setup mesh with
          mesh.lines.appendLevelLine(level, strip);
          mesh.setLevelLabel(level, Utils.num2str('f', level, 2));
        }
      }
    }
    return mesh;
  }

  protected LineStrip followContourFrom(DefaultContourColoringPolicy policy, int i, int j,
      double[][] contours, boolean[][] processed, float planeAxe) {
    LineStrip strip = new LineStrip(100);

    // add current
    processed[i][j] = true;
    strip.add(new Point(map(i, j, planeAxe, contours)));
    int width = 1;
    IntegerCoord2d next = findNext(i, j, width, contours, processed);
    while (next != null) {
      processed[next.x][next.y] = true;
      Coord3d coord = map(next.x, next.y, planeAxe, contours);
      Color color = policy.getColorMapper().getColor(new Coord3d(0, 0, contours[next.x][next.y]));
      strip.add(new Point(coord, color));
      // follow path
      next = findNext(next.x, next.y, width, contours, processed);
    }
    return strip;
  }

  protected IntegerCoord2d findNext(int i, int j, int width, double[][] contours,
      boolean[][] processed) {
    int iFrom = (i > (width - 1)) ? (i - width) : i;
    int iTo = (i < contours.length - width) ? (i + width) : i;
    int jFrom = (j > (width - 1)) ? (j - width) : j;
    int jTo = (j < contours[0].length - width) ? (j + width) : j;
    int n = 0;

    for (int ii = iFrom; ii <= iTo; ii++) {
      for (int jj = jFrom; jj <= jTo; jj++) {
        n++;
        if ((!processed[ii][jj]) && (contours[ii][jj] != NON_CONTOUR)) {
          return new IntegerCoord2d(ii, jj);
        }
      }
    }
    if (width < PIXEL_NEIGHBOUR_THRESHOLD)
      return findNext(i, j, width + 1, contours, processed);
    return null;
  }

  protected Coord3d map(int i, int j, double[][] contours) {
    return map(i, j, contours[i][j], contours);
  }

  // TODO: we flip back what was flipped during contour build
  protected Coord3d map(int i, int j, double value, double[][] contours) {
    double xrng = xrange.getRange();
    double yrng = yrange.getRange();
    double x = xrange.getMin() + xrng * ((i) / ((double) contours.length));
    double y = yrange.getMin() + yrng * ((contours[0].length - j) / ((double) contours[0].length));
    return new Coord3d(x, y, value);
  }

  /***********************************/

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
