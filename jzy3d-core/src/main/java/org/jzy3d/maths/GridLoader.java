package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;



/**
 * The GridLoader utility offers a way to convert coordinates mode. When input data is made of lists
 * of x, y, and z points, and that these lists actually contain data that regularly lie on a squared
 * grid (orthonormal grid), the GridLoader allows a smart retrieving of data by providing:
 * <ul>
 * <li>an 1D array of sorted X coordinates (without doublon).
 * <li>an 1D array of sorted Y coordinates (without doublon).
 * <li>an 2D array of Z coordinates syncronized with arrays X and Y.
 * </ul>
 * <br>
 * If some data are missing for representing this grid, missing data will be considered as NaN. The
 * actual management of missing values is thus left to the object that loads these data. <br>
 * In the following example, the o represent a NaN Z value, meaning that there was no
 * (x[i],y[i],z[i]) triplet for representing it correctly: <br>
 * <code>
 * 11111111 y<br>
 * 11111111  <br>
 * 11o11111  <br>
 * 11111111  <br>
 * &nbsp;    <br>
 * x         <br>
 *           <br>
 * </code> <br>
 *
 * If some points are missing in the input list to generate a complete grid, the output grid will
 * contain Z=NaN values for missing coordinates.
 *
 * @author Martin Pernollet
 */
@Deprecated
public class GridLoader implements IColorMappable {
  static Logger logger = LogManager.getLogger(GridLoader.class);

  /**
   * Initialize a GridLoader by parsing the square grid directly. When the grid is parsed, the
   * GridLoader may return either:
   * <ul>
   * <li>a set of polygon describing a ring with smooth contour
   * <li>
   */
  public GridLoader(float x[], float y[], float z[]) {
    bbox = new BoundingBox3d();
    setData(x, y, z);
  }

  /**********************************************************************/


  /**
   * Return a BoundingBox indicating the bounds of the loaded data.
   * 
   * @return a bounding box.
   */
  public BoundingBox3d getBoundingBox() {
    return bbox;
  }

  /************************************************************************************************/

  /**
   * Set the array of data. X,Y, and Z are arrays that must implicitely represent an orthonormal
   * grid.
   * 
   * If some data are missing for representing this grid, missing data will be considered as NaN.
   * The actual management of missing values is thus left to the object that loads these data.
   * 
   * In the following example, the o represent a NaN Z value, meaning that there was no
   * (x[i],y[i],z[i]) triplet for representing it correctly: <br>
   * <code> 11111111 y<br>
   * 11111111 <br>
   * 11o11111 <br>
   * 11111111 <br>
   * &nbsp; <br>
   * x <br>
   * <br>
   * <code> <br>
   * 
   * While parsing input data, the minimal and maximal Z values are computed, in order to have the
   * loader ready for computing polygons color with a colormap.
   * 
   * @param x list of x coordinates
   * @param y list of y coordinates
   * @param z list of z coordinates
   * @throws an IllegalArgumentException if x, y , and z have not the same size
   */
  protected void setData(float x[], float y[], float z[]) {
    if (x.length != y.length || x.length != z.length)
      throw new IllegalArgumentException("GridLoader: x, y, and z arrays must agree in length.");

    // Initialize loading
    bbox.reset();
    this.x = unique(x);
    this.y = unique(y);
    this.z = new float[this.x.length][this.y.length];

    for (int i = 0; i < this.x.length; i++)
      for (int j = 0; j < this.y.length; j++)
        this.z[i][j] = Float.NaN;

    // Fill Z matrix and set surface minimum and maximum
    boolean found;
    for (int p = 0; p < z.length; p++) {
      found = find(this.x, this.y, x[p], y[p]);
      assert found : "GridLoader: it seems (x[p],y[p]) has not been properly stored into (this.x,this.y)";
      this.z[findxi][findyj] = z[p];
      bbox.add(this.x[findxi], this.y[findyj], this.z[findxi][findyj]);
    }

    colorZmin = bbox.getZmin();
    colorZmax = bbox.getZmax();
  }

  /**
   * Compute a sorted array from input, with a unique occurrence of each value. Note: any NaN value
   * will be ignored and won't appear in the output array.
   * 
   * @param data input array.
   * @return a sorted array containing only one occurrence of each input value.
   */
  private float[] unique(float[] data) {
    float[] copy = Array.clone(data);
    Arrays.sort(copy);

    // count unique values
    int nunique = 0;
    float last = Float.NaN;
    for (int i = 0; i < copy.length; i++) {
      if (Float.isNaN(copy[i])) {
        logger.info("Ignoring NaN value at " + i);
      } else if (copy[i] != last) {
        nunique++;
        last = copy[i];
      }
    }

    // Fill a sorted unique array
    float[] result = new float[nunique];
    last = Float.NaN;
    int r = 0;
    for (int d = 0; d < copy.length; d++) {
      if (Float.isNaN(copy[d])) {
        logger.info("Ignoring NaN value at " + d);
      } else if (copy[d] != last) {
        result[r] = copy[d];
        last = copy[d];
        r++;
      }
    }
    return result;
  }

  /**
   * Search in a couple of array a combination of values vx and vy. Positions xi and yi are returned
   * by reference. Function returns true if the couple of data may be retrieved, false otherwise (in
   * this case, xi and yj remain unchanged).
   */
  private boolean find(float[] x, float[] y, float vx, float vy) {

    for (int i = 0; i < x.length; i++)
      for (int j = 0; j < y.length; j++)
        if (x[i] == vx && y[j] == vy) {
          findxi = i;
          findyj = j;
          return true;
        }
    return false;
  }

  /**************************************************************************************/

  public Vector<Polygon> getSquarePolygons(IColorMap cmap, Color colorFactor) {
    Vector<Polygon> polygons = new Vector<Polygon>();
    bbox.reset();

    boolean[] isIn;

    for (int xi = 1; xi < x.length - 1; xi++) {
      for (int yi = 1; yi < y.length - 1; yi++) {
        // Compute points surrounding current point
        Point p[] = new Point[4];

        p[0] = new Point(new Coord3d((x[xi - 1] + x[xi]) / 2, (y[yi + 1] + y[yi]) / 2,
            (z[xi - 1][yi + 1] + z[xi - 1][yi] + z[xi][yi] + z[xi][yi + 1]) / 4));
        p[1] = new Point(new Coord3d((x[xi - 1] + x[xi]) / 2, (y[yi - 1] + y[yi]) / 2,
            (z[xi - 1][yi] + z[xi - 1][yi - 1] + z[xi][yi - 1] + z[xi][yi]) / 4));
        p[2] = new Point(new Coord3d((x[xi + 1] + x[xi]) / 2, (y[yi - 1] + y[yi]) / 2,
            (z[xi][yi] + z[xi][yi - 1] + z[xi + 1][yi - 1] + z[xi + 1][yi]) / 4));
        p[3] = new Point(new Coord3d((x[xi + 1] + x[xi]) / 2, (y[yi + 1] + y[yi]) / 2,
            (z[xi][yi + 1] + z[xi][yi] + z[xi + 1][yi] + z[xi + 1][yi + 1]) / 4));

        p[0].setColor(cmap.getColor(this, p[0].xyz.x, p[0].xyz.y, p[0].xyz.z));
        p[1].setColor(cmap.getColor(this, p[1].xyz.x, p[1].xyz.y, p[1].xyz.z));
        p[2].setColor(cmap.getColor(this, p[2].xyz.x, p[2].xyz.y, p[2].xyz.z));
        p[3].setColor(cmap.getColor(this, p[3].xyz.x, p[3].xyz.y, p[3].xyz.z));

        p[0].rgb.mul(colorFactor);
        p[1].rgb.mul(colorFactor);
        p[2].rgb.mul(colorFactor);
        p[3].rgb.mul(colorFactor);

        float[] radius = new float[p.length];
        for (int i = 0; i < p.length; i++)
          radius[i] = radius2d(p[i]);

        // Compute status of each point according to there radius, or NaN status
        isIn = isInside(p, radius, 0, Float.MAX_VALUE);

        // Ignore polygons that are out
        if (!isIn[0] && !isIn[1] && !isIn[2] && !isIn[3])
          continue;

        // Directly store polygons that have non NaN values for all points
        if (isIn[0] && isIn[1] && isIn[2] && isIn[3]) {
          Polygon quad = new Polygon();
          for (int pi = 0; pi < p.length; pi++)
            quad.add(p[pi]);
          polygons.add(quad);
          bbox.add(quad);
        }

        // Partly inside: generate points that intersect a radius
        else {
        } // end switch quad/polygon
      } // end for y
    } // end for x
    return polygons;
  }

  /**************************************************************************************/

  public List<Polygon> getExtrapolatedRingPolygons(float ringMax, IColorMap cmap,
      Color colorFactor) {
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

    List<Polygon> polygons = getInterpolatedRingPolygons(0, ringMax, cmap, colorFactor);

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


  /**************************************************************************************/

  /**
   * Load data standing on an orthonormal grid. <br>
   * Each input point (i.e. the association of x[i], y[j], z[i][j]) will be represented by a polygon
   * centered on this point. The default coordinates of this polygon will be:
   * <ul>
   * <li>x[i-1], y[j+1], z[i-1][j+1]
   * <li>x[i-1], y[j-1], z[i-1][j-1]
   * <li>x[i+1], y[j-1], z[i+1][j-1]
   * <li>x[i+1], y[j+1], z[i+1][j+1]
   * </ul>
   * There are thus three types of polygons:
   * <ul>
   * <li>those that stand completely inside the ringMin and ringMax radius and that have the
   * previous coordinates.
   * <li>those that stand completely outside the ringMin and ringMax radius and that won't be added
   * to the list of polygons.
   * <li>those that have some points in and some points out of the ringMin and ringMax radius. These
   * polygons are recomputed so that "out" points are replaced by two points that make the smooth
   * contour. According to the number of "out" points, the modified polygon will gather 3, 4, or 5
   * points. <br>
   * As a consequence, it is suggested to provide data ranging outside of ringMin and ringMax, in
   * order to be sure to have a perfect round surface.
   * 
   * @param ringMin the minimum radius of this ring.
   * @param ringMax the maximum radius of this ring.
   * @param colorFactor a weighting factor for the color returned by the Colormap.
   */
  public List<Polygon> getInterpolatedRingPolygons(float ringMin, float ringMax, IColorMap cmap,
      Color colorFactor) {
    List<Polygon> polygons = new ArrayList<Polygon>();
    bbox.reset();

    boolean[] isIn;

    for (int xi = 1; xi < x.length - 1; xi++) {
      for (int yi = 1; yi < y.length - 1; yi++) {
        // Compute points surrounding current point
        Point p[] = new Point[4];

        p[0] = new Point(new Coord3d((x[xi - 1] + x[xi]) / 2, (y[yi + 1] + y[yi]) / 2,
            (z[xi - 1][yi + 1] + z[xi - 1][yi] + z[xi][yi] + z[xi][yi + 1]) / 4));
        p[1] = new Point(new Coord3d((x[xi - 1] + x[xi]) / 2, (y[yi - 1] + y[yi]) / 2,
            (z[xi - 1][yi] + z[xi - 1][yi - 1] + z[xi][yi - 1] + z[xi][yi]) / 4));
        p[2] = new Point(new Coord3d((x[xi + 1] + x[xi]) / 2, (y[yi - 1] + y[yi]) / 2,
            (z[xi][yi] + z[xi][yi - 1] + z[xi + 1][yi - 1] + z[xi + 1][yi]) / 4));
        p[3] = new Point(new Coord3d((x[xi + 1] + x[xi]) / 2, (y[yi + 1] + y[yi]) / 2,
            (z[xi][yi + 1] + z[xi][yi] + z[xi + 1][yi] + z[xi + 1][yi + 1]) / 4));

        p[0].setColor(cmap.getColor(this, p[0].xyz.x, p[0].xyz.y, p[0].xyz.z));
        p[1].setColor(cmap.getColor(this, p[1].xyz.x, p[1].xyz.y, p[1].xyz.z));
        p[2].setColor(cmap.getColor(this, p[2].xyz.x, p[2].xyz.y, p[2].xyz.z));
        p[3].setColor(cmap.getColor(this, p[3].xyz.x, p[3].xyz.y, p[3].xyz.z));

        p[0].rgb.r *= colorFactor.r;
        p[0].rgb.g *= colorFactor.g;
        p[0].rgb.b *= colorFactor.b;
        p[0].rgb.a *= colorFactor.a;
        p[1].rgb.r *= colorFactor.r;
        p[1].rgb.g *= colorFactor.g;
        p[1].rgb.b *= colorFactor.b;
        p[1].rgb.a *= colorFactor.a;
        p[2].rgb.r *= colorFactor.r;
        p[2].rgb.g *= colorFactor.g;
        p[2].rgb.b *= colorFactor.b;
        p[2].rgb.a *= colorFactor.a;
        p[3].rgb.r *= colorFactor.r;
        p[3].rgb.g *= colorFactor.g;
        p[3].rgb.b *= colorFactor.b;
        p[3].rgb.a *= colorFactor.a;

        float[] radius = new float[p.length];
        for (int i = 0; i < p.length; i++)
          radius[i] = radius2d(p[i]);

        // Compute status of each point according to there radius, or NaN status
        isIn = isInside(p, radius, ringMin, ringMax);

        // Ignore polygons that are out
        if (!isIn[0] && !isIn[1] && !isIn[2] && !isIn[3])
          continue;

        // Directly store polygons that have non NaN values for all points
        if (isIn[0] && isIn[1] && isIn[2] && isIn[3]) {
          Polygon quad = new Polygon();
          for (int pi = 0; pi < p.length; pi++)
            quad.add(p[pi]);
          polygons.add(quad);
          bbox.add(quad);
        }

        // Partly inside: generate points that intersect a radius
        else {
          Polygon polygon = new Polygon();
          Point intersection; // generated point
          float ringRadius;

          int[] seq = {0, 1, 2, 3, 0};
          boolean[] done = new boolean[4];
          for (int pi = 0; pi < done.length; pi++)
            done[pi] = false;

          // Handle all square edges and shift "out" points
          for (int s = 0; s < seq.length - 1; s++) {
            // Case of point s "in" and point s+1 "in"
            if (isIn[seq[s]] && isIn[seq[s + 1]]) {
              if (!done[seq[s]]) {
                polygon.add(p[seq[s]]);
                done[seq[s]] = true;
              }
              if (!done[seq[s + 1]]) {
                polygon.add(p[seq[s + 1]]);
                done[seq[s + 1]] = true;
              }
            }

            // Case of point s "in" and point s+1 "out"
            else if (isIn[seq[s]] && !isIn[seq[s + 1]]) {
              if (!done[seq[s]]) {
                polygon.add(p[seq[s]]);
                done[seq[s]] = true;
              }
              // Select the radius on which the point is supposed to stand
              if (Math.abs(radius[seq[s + 1]] - ringMin) < Math.abs(radius[seq[s + 1]] - ringMax))
                ringRadius = ringMin;
              else
                ringRadius = ringMax;

              // Generate a point on the circle that replaces s+1
              intersection = findPoint(p[seq[s]], p[seq[s + 1]], ringRadius);
              intersection.setColor(
                  cmap.getColor(this, intersection.xyz.x, intersection.xyz.y, intersection.xyz.z));
              intersection.rgb.r *= colorFactor.r;
              intersection.rgb.g *= colorFactor.g;
              intersection.rgb.b *= colorFactor.b;
              intersection.rgb.a *= colorFactor.a;
              polygon.add(intersection);
            }

            // Case of point s "out" and point s+1 "in"
            else if (!isIn[seq[s]] && isIn[seq[s + 1]]) {
              // Select the radius on which the point is supposed to stand
              if (Math.abs(radius[seq[s + 1]] - ringMin) < Math.abs(radius[seq[s + 1]] - ringMax))
                ringRadius = ringMin;
              else
                ringRadius = ringMax;

              // Generate a point on the circle that replaces s
              intersection = findPoint(p[seq[s]], p[seq[s + 1]], ringRadius);
              intersection.setColor(
                  cmap.getColor(this, intersection.xyz.x, intersection.xyz.y, intersection.xyz.z));
              intersection.rgb.r *= colorFactor.r;
              intersection.rgb.g *= colorFactor.g;
              intersection.rgb.b *= colorFactor.b;
              intersection.rgb.a *= colorFactor.a;
              polygon.add(intersection);

              if (!done[seq[s + 1]]) {
                polygon.add(p[seq[s + 1]]);
                done[seq[s + 1]] = true;
              }
            } // end case 3
          } // end polygon construction loop

          polygons.add(polygon);
          bbox.add(polygon);
        } // end switch quad/polygon
      } // end for y
    } // end for x
    return polygons;
  }

  /** Indicates which points lie inside and outside the given min and max radius. */
  private boolean[] isInside(Point[] p, float[] radius, float minRadius, float maxRadius) {
    boolean[] isIn = new boolean[4];

    isIn[0] = !Float.isNaN(p[0].xyz.z) && radius[0] < maxRadius && radius[0] >= minRadius;
    isIn[1] = !Float.isNaN(p[1].xyz.z) && radius[1] < maxRadius && radius[1] >= minRadius;
    isIn[2] = !Float.isNaN(p[2].xyz.z) && radius[2] < maxRadius && radius[2] >= minRadius;
    isIn[3] = !Float.isNaN(p[3].xyz.z) && radius[3] < maxRadius && radius[3] >= minRadius;

    return isIn;
  }

  private float radius2d(Point p) {
    return (float) Math.sqrt(p.xyz.x * p.xyz.x + p.xyz.y * p.xyz.y);
  }

  /**
   * Return a point that is the intersection between a segment and a circle
   * 
   * @throws ArithmeticException if points do not stand on an squared (orthonormal) grid.
   */
  private Point findPoint(Point p1, Point p2, float ringRadius) {
    // We know that the seeked point is on a horizontal or vertial line
    float x3, y3, z3;
    float w1, w2;
    double alpha;

    // We know x3 and radius and seek y3, using intermediate alpha
    if (p1.xyz.x == p2.xyz.x) {
      x3 = p1.xyz.x;
      alpha = Math.acos(x3 / ringRadius);

      if (p1.xyz.y < 0 && p2.xyz.y < 0)
        y3 = -(float) Math.sin(alpha) * ringRadius;
      else if (p1.xyz.y > 0 && p2.xyz.y > 0)
        y3 = (float) Math.sin(alpha) * ringRadius;
      else if (p1.xyz.y == -p2.xyz.y)
        y3 = 0; // ne peut pas arriver
      else
        throw new ArithmeticException(("no alignement between p1(" + p1.xyz.x + "," + p1.xyz.y + ","
            + p1.xyz.z + ") and p2(" + p2.xyz.x + "," + p2.xyz.y + "," + p2.xyz.z + ")"));

      // and now get z3
      if (!Float.isNaN(p1.xyz.z) && Float.isNaN(p2.xyz.z))
        z3 = p1.xyz.z;
      else if (Float.isNaN(p1.xyz.z) && !Float.isNaN(p2.xyz.z))
        z3 = p2.xyz.z;
      else if (!Float.isNaN(p1.xyz.z) && !Float.isNaN(p2.xyz.z)) {
        w2 = (float) (Math
            .sqrt((x3 - p1.xyz.x) * (x3 - p1.xyz.x) + (y3 - p1.xyz.y) * (y3 - p1.xyz.y))
            / Math.sqrt((p2.xyz.x - p1.xyz.x) * (p2.xyz.x - p1.xyz.x)
                + (p2.xyz.y - p1.xyz.y) * (p2.xyz.y - p1.xyz.y)));
        w1 = 1 - w2;
        z3 = w1 * p1.xyz.z + w2 * p2.xyz.z;
      } else
        throw new ArithmeticException(("can't compute z3 with p1(" + p1.xyz.x + "," + p1.xyz.y
            + ") and p2(" + p2.xyz.x + "," + p2.xyz.y + ")"));
    }
    // We know y3 and radius and seek x3, using intermediate alpha
    else if (p1.xyz.y == p2.xyz.y) {
      y3 = p1.xyz.y;
      alpha = Math.asin(y3 / ringRadius);

      if (p1.xyz.x < 0 && p2.xyz.x < 0)
        x3 = -(float) Math.cos(alpha) * ringRadius;
      else if (p1.xyz.x > 0 && p2.xyz.x > 0)
        x3 = (float) Math.cos(alpha) * ringRadius;
      else if (p1.xyz.x == -p2.xyz.x)
        x3 = 0; // ne peut pas arriver
      else
        throw new ArithmeticException(("no alignement between p1(" + p1.xyz.x + "," + p1.xyz.y + ","
            + p1.xyz.z + ") and p2(" + p2.xyz.x + "," + p2.xyz.y + "," + p2.xyz.z + ")"));

      // and now get z3
      if (!Float.isNaN(p1.xyz.z) && Float.isNaN(p2.xyz.z))
        z3 = p1.xyz.z;
      else if (Float.isNaN(p1.xyz.z) && !Float.isNaN(p2.xyz.z))
        z3 = p2.xyz.z;
      else if (!Float.isNaN(p1.xyz.z) && !Float.isNaN(p2.xyz.z)) {
        w2 = (float) (Math
            .sqrt((x3 - p1.xyz.x) * (x3 - p1.xyz.x) + (y3 - p1.xyz.y) * (y3 - p1.xyz.y))
            / Math.sqrt((p2.xyz.x - p1.xyz.x) * (p2.xyz.x - p1.xyz.x)
                + (p2.xyz.y - p1.xyz.y) * (p2.xyz.y - p1.xyz.y)));
        w1 = 1 - w2;
        z3 = w1 * p1.xyz.z + w2 * p2.xyz.z;
      } else
        throw new ArithmeticException(("can't compute z3 with p1(" + p1.xyz.x + "," + p1.xyz.y
            + ") and p2(" + p2.xyz.x + "," + p2.xyz.y + ")"));
    } else
      throw new ArithmeticException(("no alignement between p1(" + p1.xyz.x + "," + p1.xyz.y
          + ") and p2(" + p2.xyz.x + "," + p2.xyz.y + ")"));

    return new Point(new Coord3d(x3, y3, z3));
  }

  /**********************************************************************/

  @Override
  public double getMin() {
    return colorZmin;
  }

  @Override
  public double getMax() {
    return colorZmax;
  }

  @Override
  public void setMin(double zmin) {
    colorZmin = (float) zmin;
  }

  @Override
  public void setMax(double zmax) {
    colorZmax = (float) zmax;
  }

  /**********************************************************************/

  private float colorZmin = Float.NaN;
  private float colorZmax = Float.NaN;

  public float x[];
  public float y[];
  public float z[][];

  private BoundingBox3d bbox;

  private int findxi;
  private int findyj;
}
