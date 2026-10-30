package org.jzy3d.plot3d.builder.concrete;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;


public class RingTessellator extends OrthonormalTessellator {
  public RingTessellator(float ringMin, float ringMax, ColorMapper cmap, Color factor) {
    this.ringMin = ringMin;
    this.ringMax = ringMax;
    this.cmap = cmap;
    this.factor = factor;
  }

  @SuppressWarnings("unused")
  private RingTessellator() {
    throw new RuntimeException("Forbidden constructor!");
  }


  @Override
  public Composite build(float[] x, float[] y, float[] z) {
    setData(x, y, z);
    Shape s = new Shape();
    s.add(getInterpolatedRingPolygons());
    return s;
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
  public List<Drawable> getInterpolatedRingPolygons() {
    List<Drawable> polygons = new ArrayList<Drawable>();

    boolean[] isIn;

    for (int xi = 0; xi < x.length - 1; xi++) {
      for (int yi = 0; yi < y.length - 1; yi++) {
        // Compute points surrounding current point
        Point p[] = getRealQuadStandingOnPoint(xi, yi);
        p[0].setColor(cmap.getColor(p[0].xyz));
        p[1].setColor(cmap.getColor(p[1].xyz));
        p[2].setColor(cmap.getColor(p[2].xyz));
        p[3].setColor(cmap.getColor(p[3].xyz));
        p[0].rgb.mul(factor);
        p[1].rgb.mul(factor);
        p[2].rgb.mul(factor);
        p[3].rgb.mul(factor);

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
              intersection.setColor(cmap.getColor(intersection.xyz));
              intersection.rgb.mul(factor);
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
              intersection.setColor(cmap.getColor(intersection.xyz));
              intersection.rgb.mul(factor);
              polygon.add(intersection);

              if (!done[seq[s + 1]]) {
                polygon.add(p[seq[s + 1]]);
                done[seq[s + 1]] = true;
              }
            } // end case 3
          } // end polygon construction loop

          polygons.add(polygon);
        } // end switch quad/polygon
      } // end for y
    } // end for x
    return polygons;
  }


  /** Indicates which point lies inside and outside the given min and max radius. */
  protected boolean[] isInside(Point[] p, float[] radius, float minRadius, float maxRadius) {
    boolean[] isIn = new boolean[4];

    isIn[0] = !Float.isNaN(p[0].xyz.z) && radius[0] < maxRadius && radius[0] >= minRadius;
    isIn[1] = !Float.isNaN(p[1].xyz.z) && radius[1] < maxRadius && radius[1] >= minRadius;
    isIn[2] = !Float.isNaN(p[2].xyz.z) && radius[2] < maxRadius && radius[2] >= minRadius;
    isIn[3] = !Float.isNaN(p[3].xyz.z) && radius[3] < maxRadius && radius[3] >= minRadius;

    return isIn;
  }

  protected float radius2d(Point p) {
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


  /************************************************************************************************/

  protected float ringMin;
  protected float ringMax;
  protected ColorMapper cmap;
  protected Color factor;
}
