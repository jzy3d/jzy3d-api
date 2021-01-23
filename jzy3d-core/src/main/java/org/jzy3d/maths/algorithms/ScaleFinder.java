package org.jzy3d.maths.algorithms;

import org.jzy3d.maths.Scale;
import org.jzy3d.maths.Statistics;

public class ScaleFinder {

  /**
   * Apply an outlier remover on input data ({@link OutlierRemover.getInlierValues}) and retrieve
   * the min and max values of the non-rejected values.
   * 
   * @param values
   * @param nVariance
   * @return
   */
  public static Scale getFilteredScale(double[] values, int nVariance) {
    double[] inliers = OutlierRemover.getInlierValues(values, nVariance);

    return getMinMaxScale(inliers);
  }

  /**
   * Simply returns the min and max values of the input array into a Scale object.
   * 
   * @param values
   * @return
   */
  public static Scale getMinMaxScale(double[] values) {
    if (values.length == 0)
      return new Scale(Float.NaN, Float.NaN);
    return new Scale((float) Statistics.min(values), (float) Statistics.max(values));
  }
}
