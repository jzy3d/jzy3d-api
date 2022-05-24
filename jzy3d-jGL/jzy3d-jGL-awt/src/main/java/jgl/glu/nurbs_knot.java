/*
 * @(#)nurbs_knot.java 0.1 99/11/2
 *
 * jGL 3-D graphics library for Java Copyright (c) 1999 Robin Bing-Yu Chen
 * (robin@is.s.u-tokyo.ac.jp)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or any later version. the GNU Lesser General Public License should be
 * included with this distribution in the file LICENSE.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package jgl.glu;

import jgl.wt.awt.GLU;

/**
 * nurbs_knot is one of the GLU NURBS class of JavaGL 2.1.
 *
 * @version 0.1, 2 Nov 1999
 * @author Robin Bing-Yu Chen
 */

public class nurbs_knot {

  public float knot[];
  public int nknots = 0;
  public float unified_knot[];
  public int unified_nknots = 0;
  public int order;
  public int t_min;
  public int t_max;
  public int delta_nknots = 0; // number of extra knots
  /*
   * public boolean open_at_begin; public boolean open_at_end;
   */
  public float new_knot[];
  public float alpha[][];

  private void swap(float a[], int i, int j) {
    float T;

    T = a[i];
    a[i] = a[j];
    a[j] = T;
  }

  private void quicksort(float a[], int lo0, int hi0) {
    int lo = lo0;
    int hi = hi0;
    float mid;

    if (hi0 > lo0) {
      mid = a[(lo0 + hi0) / 2];
      while (lo <= hi) {
        while ((lo < hi0) && (a[lo] < mid))
          ++lo;
        while ((hi > lo0) && (a[hi] > mid))
          --hi;
        if (lo <= hi) {
          swap(a, lo, hi);
          ++lo;
          --hi;
        }
      }
      if (lo0 < hi)
        quicksort(a, lo0, hi);
      if (lo < hi0)
        quicksort(a, lo, hi0);
    }
  }

  public int calc_new_ctrl_pts(float ctrl[][], int stride, int dim, float new_ctrl[][][],
      int ctrl_i) {
    int n = nknots - order;
    int m = t_max + 1 - t_min - order;
    int k = t_min;
    int i, j, l;

    new_ctrl[ctrl_i] = new float[m][dim];
    for (j = 0; j < m; j++) {
      for (l = 0; l < dim; l++) {
        new_ctrl[ctrl_i][j][l] = 0;
      }
      for (i = 0; i < n; i++) {
        for (l = 0; l < dim; l++) {
          new_ctrl[ctrl_i][j][l] += alpha[j + k][i] * ctrl[i][l];
        }
      }
    }
    return m;
  }

  // alpha is an m x n matrix to convert n control points to m new points
  public void calc_alphas() {
    float alpha_new[][], tmp_alpha[][];
    int n = nknots - order; // number of control points
    int m = n + delta_nknots; // number of new control points
    int i, j, k;
    float tmp, demon;

    alpha = new float[m][n];
    alpha_new = new float[m][n];
    for (j = 0; j < m; j++) {
      for (i = 0; i < n; i++) {
        if ((knot[i] <= new_knot[j]) && (new_knot[j] < knot[i + 1])) {
          alpha[j][i] = 1;
        } else {
          alpha[j][i] = 0;
        }
      }
    }
    for (k = 1; k < order; k++) {
      for (j = 0; j < m; j++) {
        for (i = 0; i < n; i++) {
          demon = knot[i + k] - knot[i];
          if (demon == 0) {
            tmp = 0;
          } else {
            tmp = (new_knot[j + k] - knot[i]) / demon * alpha[j][i];
          }
          demon = knot[i + k + 1] - knot[i + 1];
          if (demon != 0) {
            tmp += (knot[i + k + 1] - new_knot[j + k]) / demon * alpha[j][i + 1];
          }
          alpha_new[j][i] = tmp;
        }
      }
      tmp_alpha = alpha_new;
      alpha_new = alpha;
      alpha = tmp_alpha;
    }
  }

  public void explode() {
    float k[], tmp;
    int n, i, j, m, n_new_knots = 0;

    if (unified_nknots != 0) {
      k = unified_knot;
      n = unified_nknots;
    } else {
      k = knot;
      n = nknots;
    }
    i = t_min;
    while (i <= t_max) {
      j = 1;
      tmp = k[i];

      // calculate the multiplicity
      while ((j < order) && ((i + j) <= t_max) && (tmp == k[i + j])) {
        j++;
      }

      // if the multiplicity less than order, then add some knots
      n_new_knots += order - j;
      i += j;
    }

    new_knot = new float[n + n_new_knots];
    System.arraycopy(k, 0, new_knot, 0, t_min);
    m = t_min;
    for (i = t_min; i <= t_max; i++) {
      tmp = k[i];
      for (j = 0; j < order; j++) {
        new_knot[m++] = k[i];
        if ((i != t_max) && (tmp == k[i + 1])) {
          i++;
        }
      }
    }
    // then, all the knots in t_min and t_max have been aaaabbbbccccdddd
    System.arraycopy(k, t_max + 1, new_knot, m, n - t_max - 1);
    delta_nknots += n_new_knots;
    t_max += n_new_knots;
  }

  private void set_min_max(float k[], int n, float max_min, float min_max) {
    int min = 0;
    int max = n - 1;

    while ((min < n) && (k[min] != max_min)) {
      min++;
    }
    while ((max > 0) && (k[max] != min_max)) {
      max--;
    }

    t_min = min;
    t_max = max;
  }

  public void set_knot_min_max(float max_min, float min_max) {
    set_min_max(knot, nknots, max_min, min_max);
  }

  public void set_new_min_max(float max_min, float min_max) {
    set_min_max(unified_knot, unified_nknots, max_min, min_max);
  }

  public void collect(nurbs_knot src, float max_min, float min_max) {
    int cnt = unified_nknots;
    int max = t_max;
    int i, j;

    for (i = src.t_min; i <= src.t_max; i++) {
      if ((src.unified_knot[i] > max_min) && (src.unified_knot[i] < min_max)) {
        j = t_min;
        while ((j <= max) && (unified_knot[j] != src.unified_knot[i])) {
          j++;
        }
        if (j > max) { // not in unified_knot, it's a new knot
          unified_knot[cnt++] = src.unified_knot[i];
          t_max++;
          delta_nknots++;
        }
      }
    }
    unified_nknots = cnt;
    quicksort(unified_knot, 0, cnt);
  }

  public int fill(nurbs_obj obj) {
    unified_nknots = 1; // use for testing
    knot = obj.knot;
    nknots = obj.knot_count;
    order = obj.order;
    t_min = order - 1; // the last knot of the first segment
    t_max = nknots - order; // the first knot of the last segment

    if (knot[t_min] == knot[t_max]) {
      return GLU.GLU_NURBS_ERROR3;
    }
    /*
     * if (knot [0] == knot [t_min]) { open_at_begin = true; } else { open_at_begin = false; } if
     * (knot [t_max] == knot [nknots-1]) { open_at_end = true; } else { open_at_end = false; }
     */
    return GLU.GLU_NO_ERROR;
  }

}
