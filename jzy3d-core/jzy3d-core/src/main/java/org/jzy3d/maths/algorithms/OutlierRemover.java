package org.jzy3d.maths.algorithms;

import org.jzy3d.maths.Array;
import org.jzy3d.maths.Scale;
import org.jzy3d.maths.Statistics;

// warn : cast (float)
public class OutlierRemover {
  public static int[] getOutlierIndices(double[] values, int nVariance) {
    Scale bounds = getInlierBounds(values, nVariance);

    int[] selection = new int[values.length];
    int k = 0;

    for (int i = 0; i < values.length; i++)
      if (!bounds.contains((float) values[i]))
        selection[k++] = i;

    return Array.clone(selection, k);
  }

  public static int[] getInlierIndices(double[] values, int nVariance) {
    Scale bounds = getInlierBounds(values, nVariance);

    int[] selection = new int[values.length];
    int k = 0;

    for (int i = 0; i < values.length; i++)
      if (bounds.contains((float) values[i]))
        selection[k++] = i;

    return Array.clone(selection, k);
  }

  public static double[] getOutlierValues(double[] values, int nVariance) {
    Scale bounds = getInlierBounds(values, nVariance);

    double[] selection = new double[values.length];
    int k = 0;

    for (int i = 0; i < values.length; i++)
      if (!bounds.contains((float) values[i]))
        selection[k++] = values[i];

    return Array.clone(selection, k);
  }

  public static double[] getInlierValues(double[] values, int nVariance) {
    Scale bounds = getInlierBounds(values, nVariance);

    double[] selection = new double[values.length];
    int k = 0;

    for (int i = 0; i < values.length; i++)
      if (bounds.contains((float) values[i]))
        selection[k++] = values[i];

    return Array.clone(selection, k);
  }

  public static Scale getInlierBounds(double[] values, int nVariance) {
    if (values.length == 0)
      return new Scale(Float.NaN, Float.NaN);

    // Compute stdist: median of distances to median
    double[] dists = new double[values.length];
    float med = (float) Statistics.median(values, true);
    float mad = 0;

    for (int i = 0; i < values.length; i++)
      dists[i] = Math.abs(values[i] - med);
    mad = (float) Statistics.median(dists, true);

    // Compute the acceptance region bounds
    float upperBound = med + mad * nVariance;
    float lowerBound = med - mad * nVariance;

    return new Scale(lowerBound, upperBound);
  }
}
