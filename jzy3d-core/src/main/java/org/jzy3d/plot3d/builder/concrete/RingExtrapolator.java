package org.jzy3d.plot3d.builder.concrete;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Shape;


public class RingExtrapolator extends OrthonormalTessellator {
  public RingExtrapolator(float ringMax, ColorMapper cmap, Color factor) {
    this.ringMax = ringMax;
    this.cmap = cmap;
    this.factor = factor;
    this.interpolator = new RingTessellator(0, ringMax, cmap, factor);
  }

  @SuppressWarnings("unused")
  private RingExtrapolator() {
    throw new RuntimeException("Forbidden constructor!");
  }

  @Override
  public Composite build(float[] x, float[] y, float[] z) {
    setData(x, y, z);
    Shape s = new Shape();
    s.add(getExtrapolatedRingPolygons());
    return s;
  }

  /******************************************************************************/

  public List<Drawable> getExtrapolatedRingPolygons() {
    // backup current coords and extrapolate
    float[] xbackup = x;
    float[] ybackup = y;
    float[][] zbackup = z;

    // compute required extrapolation
    float step = x[1] - x[0];
    int nstep = x.length;

    int ENLARGE = 2;
    int required = (int) (Math.ceil((ringMax * 2 - step * nstep) / step));
    required = required < 0 ? ENLARGE : required + ENLARGE;

    if (required > 0)
      extrapolate(required);

    interpolator.x = x;
    interpolator.y = y;
    interpolator.z = z;
    List<Drawable> polygons = interpolator.getInterpolatedRingPolygons();

    // get back to previous grid
    x = xbackup;
    y = ybackup;
    z = zbackup;

    return polygons;
  }

  /**
   * Add extrapolated points on the grid. If the grid is too small for extrapolation, the arrays are
   * maximized
   */
  public void extrapolate(int n) {
    float[] xnew = new float[x.length + n * 2];
    float[] ynew = new float[y.length + n * 2];
    float[][] znew = new float[x.length + n * 2][y.length + n * 2];

    // assume x and y grid are allready sorted and create new grids
    float xmin = x[0];
    float xmax = x[x.length - 1];
    float xgap = x[1] - x[0];
    float ymin = y[0];
    float ymax = y[y.length - 1];
    float ygap = y[1] - y[0];

    for (int i = 0; i < xnew.length; i++) {
      // --- x grid ---
      if (i < n) // fill before
        xnew[i] = xmin - (n - i) * xgap;
      else if (i >= n && i < x.length + n) // copy content
        xnew[i] = x[i - n];
      else if (i >= x.length + n) // fill after
        xnew[i] = xmax + (i - (x.length + n) + 1) * xgap;

      // --- y grid ---
      for (int j = 0; j < ynew.length; j++) {
        if (j < n) { // fill before
          ynew[j] = ymin - (n - j) * ygap;
          znew[i][j] = Float.NaN;
        } else if (j >= n && j < (y.length + n)) { // copy content
          ynew[j] = y[j - n];

          // copy z grid
          if (i >= n && i < x.length + n)
            znew[i][j] = z[i - n][j - n];
          else
            znew[i][j] = Float.NaN;
        } else if (j >= (y.length + n)) { // fill after
          ynew[j] = ymax + (j - (y.length + n) + 1) * ygap;
          znew[i][j] = Float.NaN;
        }
      }
    }

    // extrapolation
    float olddiameter = xgap * (x.length) / 2;
    float newdiameter = xgap * (x.length - 1 + n * 2) / 2;
    olddiameter *= olddiameter;
    newdiameter *= newdiameter;

    int xmiddle = (xnew.length - 1) / 2; // assume it is an uneven grid
    int ymiddle = (ynew.length - 1) / 2; // assume it is an uneven grid

    // start from center, and add extrapolated values iteratively on each quadrant
    for (int i = xmiddle; i < xnew.length; i++) {
      for (int j = ymiddle; j < ynew.length; j++) {
        float sqrad = xnew[i] * xnew[i] + ynew[j] * ynew[j]; // distance to center

        // ignore existing values
        if (sqrad < olddiameter)
          continue;

        // ignore existing values
        else if (sqrad < newdiameter && sqrad >= olddiameter) {

          int xopp = i - 2 * (i - xmiddle);
          int yopp = j - 2 * (j - ymiddle);

          znew[i][j] = getExtrapolatedZ(znew, i, j); // right up quadrant
          znew[xopp][j] = getExtrapolatedZ(znew, xopp, j); // left up
          znew[i][yopp] = getExtrapolatedZ(znew, i, yopp); // right down
          znew[xopp][yopp] = getExtrapolatedZ(znew, xopp, yopp); // left down
        }

        // ignore values standing outside desired diameter
        else // if(sqrad > newdiameter)
          znew[i][j] = Float.NaN;
      }
    }

    // store result
    x = xnew;
    y = ynew;
    z = znew;
  }

  private float getExtrapolatedZ(float[][] grid, int currentXi, int currentYi) {
    int left = currentXi - 1 > 0 ? currentXi - 1 : currentXi;
    int right = currentXi + 1 < grid.length ? currentXi + 1 : currentXi;
    int bottom = currentYi - 1 > 0 ? currentYi - 1 : currentYi;
    int up = currentYi + 1 < grid[0].length ? currentYi + 1 : currentYi;

    float cumval = 0;
    int nval = 0;

    for (int u = left; u <= right; u++)
      for (int v = bottom; v <= up; v++)
        if (!Float.isNaN(grid[u][v])) {
          cumval += grid[u][v];
          nval++;
        }

    if (nval > 0)
      return cumval / nval;
    else
      return Float.NaN;
  }

  /*************************************************************************************/

  protected float ringMax;
  protected ColorMapper cmap;
  protected Color factor;

  protected RingTessellator interpolator;
}
