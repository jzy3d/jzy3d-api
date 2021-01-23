package org.jzy3d.contour;

/**
 * The AbstractContourGenerator provides various utility fonctions to compute core contour matrices.
 * A concrete generator should implement {@link computeHeightMatrix()} according to the input object
 * on which one desire to compute a contour ({@link Mapper}s, {@link Shape}s, etc).
 * 
 * @author Juan Barandiaran
 */
public abstract class AbstractContourGenerator {

  protected static double NON_CONTOUR = -Double.MAX_VALUE;
  protected double minValue;
  protected double maxValue;

  protected abstract void computeHeightMatrix(double matrix[][], int xRes, int yRes);

  /**
   * Calculates the points in the XY plane that belong to a contour
   * 
   **/
  protected double[][] computeContour(int xRes, int yRes, int nLevels) {
    double matrix[][] = new double[xRes][yRes];
    double contours[][] = new double[xRes][yRes];

    // Calculates the Height of the surface for each point in the XY plane
    computeHeightMatrix(matrix, xRes, yRes);

    // Quantizes the heights in nLevels levels
    quantizeMatrix(matrix, minValue, maxValue, nLevels);

    // Leaves only the contours
    // TODO: could be great to only modify "matrix" instead of copying into
    // "contours"
    extractCountours(contours, matrix, xRes, yRes);

    return contours;
  }

  /**
   * Calculates the points in the XY plane that belong to a contour whose height is defined by the
   * user in an array
   * 
   **/
  protected double[][] computeContour(int xRes, int yRes, double sortedLevels[]) {
    double matrix[][] = new double[xRes][yRes];
    double contours[][] = new double[xRes][yRes];

    // Calculates the Height of the surface for each point in the XY plane
    computeHeightMatrix(matrix, xRes, yRes);

    // Quantizes the heights in nLevels levels
    quantizeMatrix(matrix, sortedLevels);

    // Leaves only the contours
    // TODO: could be great to only modify "matrix" instead of copying into
    // "contours"
    extractCountours(contours, matrix, xRes, yRes);

    return contours;
  }

  /**
   * To paint the contours in the XY plane filling with color between contour and contour
   * 
   **/
  protected double[][] computeFilledContour(int xRes, int yRes, int nLevels) {
    double matrix[][] = new double[xRes][yRes];

    // Calculates the Height of the surface for each point in the XY plane
    computeHeightMatrix(matrix, xRes, yRes);

    // Quantizes the heights in nLevels levels
    quantizeMatrix(matrix, minValue, maxValue, nLevels);
    return matrix;
  }

  /**
   * Having the colors of the surface on the XY plane gives a lot of info in some surfaces
   * 
   **/
  protected double[][] computeXYColors(int xRes, int yRes, int nLevels) {
    double matrix[][] = new double[xRes][yRes];
    // Calculates the Height of the surface for each point in the XY plane
    computeHeightMatrix(matrix, xRes, yRes);
    return matrix;
  }


  /**
   * Force the input matrix to have the minimum bigger value of the user provided sortedLevels list
   **/
  protected void quantizeMatrix(double matrix[][], double sortedLevels[]) {
    double value;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        value = matrix[i][j];
        matrix[i][j] = NON_CONTOUR;
        for (int n = 0; n < sortedLevels.length; n++) {
          if (value >= sortedLevels[n]) {
            matrix[i][j] = sortedLevels[n];
          } else
            n = sortedLevels.length; // To speed it up a little bit
        }
      }
    }
  }

  /**
   * Force the input matrix to have values rounded to one of the N steps in the range [min;max].
   **/
  protected void quantizeMatrix(double matrix[][], double min, double max, int n) {
    double step = (max - min) / n;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrix[i][j] = step * (int) (matrix[i][j] / step);
      }
    }
  }

  /**
   * A point belongs to the contour if it is bigger than anyone of the surrounding points of the
   * Quantized matrix It is NOT part of the contour if it is equal (or smaller) to all the
   * surrounding points
   */
  protected void extractCountours(double contours[][], double matrix[][], int xSpan, int ySpan) {
    for (int x = 1; x < xSpan - 1; x++) {
      for (int y = 1; y < ySpan - 1; y++) {
        if ((matrix[x][y] > matrix[x - 1][y - 1]) || (matrix[x][y] > matrix[x][y - 1])
            || (matrix[x][y] > matrix[x + 1][y - 1]) || (matrix[x][y] > matrix[x - 1][y])
            || (matrix[x][y] > matrix[x + 1][y]) || (matrix[x][y] > matrix[x - 1][y + 1])
            || (matrix[x][y] > matrix[x][y + 1]) || (matrix[x][y] > matrix[x + 1][y + 1])) {
          contours[x][y] = matrix[x][y];
        } else {
          contours[x][y] = NON_CONTOUR;
        }
      }
    }
    // Now fillup the borders to make it look a little better.
    // Copy the pixel in the second row (or column) only if it is "alone",
    // meaning that there is a contour line escaping the matrix
    for (int x = 1; x < xSpan - 1; x++) {
      if ((contours[x][1] != NON_CONTOUR) && (contours[x - 1][1] == NON_CONTOUR)
          && (contours[x + 1][1] == NON_CONTOUR)) {
        contours[x][0] = contours[x][1];
      } else
        contours[x][0] = NON_CONTOUR;
      if ((contours[x][ySpan - 2] != NON_CONTOUR) && (contours[x - 1][ySpan - 2] == NON_CONTOUR)
          && (contours[x + 1][ySpan - 2] == NON_CONTOUR)) {
        contours[x][ySpan - 1] = contours[x][ySpan - 2];
      } else
        contours[x][ySpan - 1] = NON_CONTOUR;
    }

    for (int y = 1; y < ySpan - 1; y++) {
      if ((contours[1][y] != NON_CONTOUR) && (contours[1][y - 1] == NON_CONTOUR)
          && (contours[1][y + 1] == NON_CONTOUR)) {
        contours[0][y] = contours[1][y];
      } else
        contours[0][y] = NON_CONTOUR;
      if ((contours[xSpan - 2][y] != NON_CONTOUR) && (contours[xSpan - 2][y - 1] == NON_CONTOUR)
          && (contours[xSpan - 2][y + 1] == NON_CONTOUR)) {
        contours[xSpan - 1][y] = contours[xSpan - 2][y];
      } else
        contours[xSpan - 1][y] = NON_CONTOUR;
    }
  }

}
