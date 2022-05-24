package org.jzy3d.maths;

import java.util.Arrays;
import java.util.List;

public class Statistics {
  /**
   * Computes the mean value of an array of doubles. NaN values are ignored during the computation.
   * 
   * @param values
   * @return the mean value
   */
  public static double mean(double[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    double sum = 0;
    int count = 0;
    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i])) {
        sum += values[i];
        count++;
      }
    return sum / count;
  }

  public static float mean(float[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float sum = 0;
    int count = 0;
    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i])) {
        sum += values[i];
        count++;
      }
    return sum / count;
  }

  public static float min(List<Float> values) {
    if (values.size() == 0)
      throw new IllegalArgumentException("Input list must have a length greater than 0");

    float minv = Float.POSITIVE_INFINITY;

    for (int i = 0; i < values.size(); i++) {
      float f = values.get(i);
      if (!Float.isNaN(f))
        if (f < minv)
          minv = f;
    }
    return minv;
  }


  /**
   * Computes the minimum value of an array of doubles. NaN values are ignored during the
   * computation.
   * 
   * @param values
   * @return the minimum value
   */
  public static double min(double[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    double minv = Double.POSITIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i]))
        if (values[i] < minv)
          minv = values[i];
    return minv;
  }

  public static float min(float[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float minv = Float.POSITIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      if (!Float.isNaN(values[i]))
        if (values[i] < minv)
          minv = values[i];
    return minv;
  }

  public static float min(float[][] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float minv = Float.POSITIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < values[i].length; j++)
        if (!Float.isNaN(values[i][j]))
          if (values[i][j] < minv)
            minv = values[i][j];
    return minv;
  }

  public static int min(int[][] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    int minv = Integer.MAX_VALUE;

    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < values[i].length; j++)
        if (values[i][j] < minv)
          minv = values[i][j];
    return minv;
  }

  /** Returns the id where the minimal value stands. */
  public static int minId(double[] values) {
    double minv = Double.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < values.length; i++)
      if (values[i] < minv) {
        minv = values[i];
        index = i;
      }
    return index;
  }

  public static int minId(float[] values) {
    float minv = Float.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < values.length; i++)
      if (values[i] < minv) {
        minv = values[i];
        index = i;
      }
    return index;
  }

  public static int minId(int[] values) {
    int minv = Integer.MAX_VALUE;
    int index = -1;

    for (int i = 0; i < values.length; i++)
      if (values[i] < minv) {
        minv = values[i];
        index = i;
      }
    return index;
  }

  public static float max(List<Float> values) {
    if (values.size() == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float maxv = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < values.size(); i++) {
      float f = values.get(i);
      if (!Float.isNaN(f))
        if (f > maxv)
          maxv = f;
    }
    return maxv;
  }


  /**
   * Computes the maximum value of an array of doubles. NaN values are ignored during the
   * computation.
   * 
   * @param values
   * @return the maximum value
   */
  public static double max(double[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    double maxv = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i]))
        if (values[i] > maxv)
          maxv = values[i];
    return maxv;
  }

  public static float max(float[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float maxv = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      if (!Float.isNaN(values[i]))
        if (values[i] > maxv)
          maxv = values[i];
    return maxv;
  }

  public static float max(float[][] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float maxv = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < values[i].length; j++)
        if (!Float.isNaN(values[i][j]))
          if (values[i][j] > maxv)
            maxv = values[i][j];
    return maxv;
  }

  public static int max(int[][] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    int maxv = Integer.MIN_VALUE;

    for (int i = 0; i < values.length; i++)
      for (int j = 0; j < values[i].length; j++)
        if (values[i][j] > maxv)
          maxv = values[i][j];
    return maxv;
  }

  public static int maxId(int[] values) {
    int maxv = Integer.MIN_VALUE;
    int index = -1;

    for (int i = 0; i < values.length; i++)
      if (values[i] > maxv) {
        maxv = values[i];
        index = i;
      }
    return index;
  }
  
  
  public static Range minmax(float[] values) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    float minv = Float.POSITIVE_INFINITY;
    float maxv = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < values.length; i++)
      if (!Float.isNaN(values[i])) {
        if (values[i] < minv)
          minv = values[i];
        if (values[i] > maxv)
          maxv = values[i];
      }
    return new Range(minv, maxv);
  }



  /**
   * Computes the mad statistic, that is the median of all distances to the median of input values.
   * If the input array is empty, the output value is Double.NaN
   * 
   * @param values
   * @return
   */
  public static double mad(double[] values) {
    if (values.length == 0)
      return Double.NaN;

    double[] dists = new double[values.length];
    double median = median(values, true);

    for (int i = 0; i < values.length; i++)
      dists[i] = Math.abs(values[i] - median);

    return median(dists, true);
  }


  /**
   * Computes the standard deviation of an array of doubles. NaN values are ignored during the
   * computation. If the input array is empty, the output value is Double.NaN
   * 
   * @param values
   * @return the standard deviation
   */
  public static double std(double[] values) {
    if (values.length == 0)
      return Double.NaN;

    return Math.sqrt(variance(values));
  }

  /**
   * Compute the variance of an array of doubles. {@link variance} normalizes the output by N-1 if
   * N>1, where N is the sample size. This is an unbiased estimator of the variance of the
   * population For N=1, the output is 0.
   * 
   * @param values
   * @return
   */
  public static double variance(double[] values) {
    double mean = mean(values);
    double sum = 0;
    int count = 0;

    for (int i = 0; i < values.length; i++)
      if (!Double.isNaN(values[i])) {
        sum += Math.pow(values[i] - mean, 2);
        count++;
      }

    if (count == 0)
      return Double.NaN;
    else if (count == 1)
      return 0;
    else
      return sum / (count - 1);
  }

  /*****************************************************************************/

  /**
   * Computes the quantiles of an array of doubles. This method assumes the array has at least one
   * element. NaN values are ignored during the computation.
   * 
   * @param values
   * @param levels a list of levels that must belong to [0;100]
   * @param interpolated computes an interpolation of quantile when required quantile is not an
   *        exact vector id.
   * @return the quantiles
   * 
   * @throws an IllegalArgumentException if a level is out of the [0;100] bounds.
   */
  public static double[] quantile(double[] values, double[] levels, boolean interpolated) {
    if (values.length == 0)
      return new double[0];

    double[] quantiles = new double[levels.length];
    double[] sorted = new double[values.length];
    System.arraycopy(values, 0, sorted, 0, values.length);
    Arrays.sort(sorted);

    double quantileIdx;
    double quantileIdxCeil;
    double quantileIdxFloor;

    for (int i = 0; i < levels.length; i++) {
      if (levels[i] > 100 || levels[i] < 0)
        throw new IllegalArgumentException(
            "input level " + levels[i] + " is out of bounds [0;100].");

      quantileIdx = (sorted.length - 1) * levels[i] / 100;

      if (quantileIdx == (int) quantileIdx) // exactly find the quantile
        quantiles[i] = sorted[(int) quantileIdx];
      else {
        quantileIdxCeil = Math.ceil(quantileIdx);
        quantileIdxFloor = Math.floor(quantileIdx);

        if (interpolated) { // generate an interpolated quantile
          quantiles[i] = sorted[(int) quantileIdxFloor] * (quantileIdxCeil - quantileIdx)
              + sorted[(int) quantileIdxCeil] * (quantileIdx - quantileIdxFloor);
        } else { // return the quantile corresponding to the closest value
          if (quantileIdx - quantileIdxFloor < quantileIdxCeil - quantileIdx)
            quantiles[i] = sorted[(int) quantileIdxFloor];
          else
            quantiles[i] = sorted[(int) quantileIdxCeil];
        }
      }
    }
    return quantiles;
  }

  /**
   * A convenient shortcut for:
   * 
   * quantile(values, levels, true);
   * 
   * @param values
   * @param levels
   * @return
   */
  public static double[] quantile(double[] values, double[] levels) {
    return quantile(values, levels, true);
  }

  /**
   * Computes the median value of an array of doubles.
   * 
   * @param values
   * @param interpolated
   * @return the mean value
   */
  public static double median(double[] values, boolean interpolated) {
    if (values.length == 0)
      throw new IllegalArgumentException("Input array must have a length greater than 0");

    double[] med = {50};
    double[] out = quantile(values, med, interpolated);

    return out[0];
  }

}
