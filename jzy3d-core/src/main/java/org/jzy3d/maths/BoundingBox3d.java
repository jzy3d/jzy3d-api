package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.space.SpaceTransformer;

/**
 * A BoundingBox3d stores a couple of maximal and minimal limit on each dimension (x, y, and z). It
 * provides functions for enlarging the box by adding coordinates, Point3d, Polygon3d or an other
 * BoundingBox3d (that is equivalent to computing the union of the current BoundingBox and another
 * one).
 * 
 * @author Martin Pernollet
 */
public class BoundingBox3d {
  protected float xmin;
  protected float xmax;
  protected float ymin;
  protected float ymax;
  protected float zmin;
  protected float zmax;

  /**
   * Initialize a BoundingBox by calling its reset method, leaving the box in an inconsitent state,
   * with minimums = Float.MAX_VALUE and maximums = -Float.MAX_VALUE. In other words, calling
   * box.valid() will return false.
   */
  public BoundingBox3d() {
    reset();
  }

  public BoundingBox3d(List<Coord3d> coords) {
    reset();
    for (Coord3d c : coords)
      add(c);
  }

  public BoundingBox3d(Polygon polygon) {
    reset();
    for (Point c : polygon.getPoints())
      add(c);
  }

  public BoundingBox3d(Coord3d center, float diameter) {
    this.xmin = center.x - diameter / 2;
    this.xmax = center.x + diameter / 2;
    this.ymin = center.y - diameter / 2;
    this.ymax = center.y + diameter / 2;
    this.zmin = center.z - diameter / 2;
    this.zmax = center.z + diameter / 2;
  }

  public BoundingBox3d(BoundingBox3d box) {
    this.xmin = box.xmin;
    this.xmax = box.xmax;
    this.ymin = box.ymin;
    this.ymax = box.ymax;
    this.zmin = box.zmin;
    this.zmax = box.zmax;
  }

  /** Initialize a BoundingBox with raw values. */
  public BoundingBox3d(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax) {
    this.xmin = xmin;
    this.xmax = xmax;
    this.ymin = ymin;
    this.ymax = ymax;
    this.zmin = zmin;
    this.zmax = zmax;
  }

  public BoundingBox3d(Range xRange, Range yRange, Range zRange) {
    this.xmin = xRange.getMin();
    this.xmax = xRange.getMax();
    this.ymin = yRange.getMin();
    this.ymax = yRange.getMax();
    this.zmin = zRange.getMin();
    this.zmax = zRange.getMax();
  }

  public BoundingBox3d(float[] bounds) {
    this.xmin = bounds[0];
    this.xmax = bounds[1];
    this.ymin = bounds[2];
    this.ymax = bounds[3];
    this.zmin = bounds[4];
    this.zmax = bounds[5];
  }

  public BoundingBox3d(double[] bounds) {
    this.xmin = (float)bounds[0];
    this.xmax = (float)bounds[1];
    this.ymin = (float)bounds[2];
    this.ymax = (float)bounds[3];
    this.zmin = (float)bounds[4];
    this.zmax = (float)bounds[5];
  }
  
  /*********************************************************/

  /**
   * Build an array of 8 coordinates indicating the 8 corners of the bounding box
   * 
   * @return
   */
  public Coord3d[] corners() {
    Coord3d[] corners = new Coord3d[8];
    corners[0] = new Coord3d(xmax, ymax, zmax);
    corners[1] = new Coord3d(xmin, ymax, zmax);
    corners[2] = new Coord3d(xmin, ymin, zmax);
    corners[3] = new Coord3d(xmax, ymax, zmax);
    corners[4] = new Coord3d(xmax, ymax, zmin);
    corners[5] = new Coord3d(xmin, ymax, zmin);
    corners[6] = new Coord3d(xmin, ymin, zmin);
    corners[7] = new Coord3d(xmax, ymax, zmin);
    return corners;
  }

  /**
   * Initialize the bounding box with Float.MAX_VALUE as minimum value, and -Float.MAX_VALUE as
   * maximum value for each dimension.
   */
  public void reset() {
    xmin = Float.MAX_VALUE;
    xmax = -Float.MAX_VALUE;
    ymin = Float.MAX_VALUE;
    ymax = -Float.MAX_VALUE;
    zmin = Float.MAX_VALUE;
    zmax = -Float.MAX_VALUE;
  }

  public boolean isReset() {
    return xmin == Float.MAX_VALUE && xmax == -Float.MAX_VALUE && ymin == Float.MAX_VALUE
        && ymax == -Float.MAX_VALUE && zmin == Float.MAX_VALUE && zmax == -Float.MAX_VALUE;
  }

  public boolean isPoint() {
    return xmin == xmax && ymin == ymax && zmin == zmax;
  }

  public boolean valid() {
    return (xmin <= xmax && ymin <= ymax && zmin <= zmax);
  }

  /**
   * Adds an x,y,z point to the bounding box, and enlarge the bounding box if this points lies
   * outside of it.
   * 
   * @param x
   * @param y
   * @param z
   */
  public void add(float x, float y, float z) {
    if (x > xmax)
      xmax = x;
    if (x < xmin)
      xmin = x;
    if (y > ymax)
      ymax = y;
    if (y < ymin)
      ymin = y;
    if (z > zmax)
      zmax = z;
    if (z < zmin)
      zmin = z;
  }

  public void add(double x, double y, double z) {
    if (x > xmax)
      xmax = (float) x;
    if (x < xmin)
      xmin = (float) x;
    if (y > ymax)
      ymax = (float) y;
    if (y < ymin)
      ymin = (float) y;
    if (z > zmax)
      zmax = (float) z;
    if (z < zmin)
      zmin = (float) z;
  }

  
  /**
   * Add a Coord3d to the BoundingBox3d. A shortcut for: <code>add(c.x, c.y, c.z);</code>
   * 
   * @param p
   */
  public void add(Coord3d c) {
    add(c.x, c.y, c.z);
  }

  public void add(List<Point> pts) {
    for (Point p : pts)
      add(p.xyz.x, p.xyz.y, p.xyz.z);
  }
  
  public void add(Drawable drawable) {
    add(drawable.getBounds());
  }


  /**
   * Add a Point3d to the BoundingBox3d. A shortcut for: <code>add(p.x, p.y, p.z);</code>
   * 
   * @param p
   */
  public void add(Point p) {
    add(p.xyz.x, p.xyz.y, p.xyz.z);
  }

  /**
   * Add the points of a Polygon to the BoundingBox3d.
   * 
   * @param p
   */
  public void add(Polygon p) {
    for (int i = 0; i < p.size(); i++)
      add(p.get(i).xyz.x, p.get(i).xyz.y, p.get(i).xyz.z);
  }

  /**
   * Add a BoundingBox3d volume to the current one. A convenient shortcut for: <code>
   * add(b.xmin, b.ymin, b.zmin);
   * add(b.xmax, b.ymax, b.zmax);
   * </code>
   * 
   * @param p
   */
  public void add(BoundingBox3d b) {
    add(b.xmin, b.ymin, b.zmin);
    add(b.xmax, b.ymax, b.zmax);
  }

  /*********************************************************/

  /**
   * Compute and return the center point of the BoundingBox3d
   * 
   * @return the center.
   */
  public Coord3d getCenter() {
    return new Coord3d((xmin + xmax) / 2, (ymin + ymax) / 2, (zmin + zmax) / 2);
  }

  public Coord3d getTransformedCenter(SpaceTransformer transformers) {
    return new Coord3d((transformers.getX().compute(xmin) + transformers.getX().compute(xmax)) / 2,
        (transformers.getY().compute(ymin) + transformers.getY().compute(ymax)) / 2,
        (transformers.getZ().compute(zmin) + transformers.getZ().compute(zmax)) / 2);
  }

  /**
   * Return the radius of the Sphere containing the Bounding Box, i.e., the distance between the
   * center and the point (xmin, ymin, zmin).
   * 
   * @return the box radius.
   */
  public double getRadius() {
    return getCenter().distance(new Coord3d(xmin, ymin, zmin));
  }

  public double getTransformedRadius(SpaceTransformer transformers) {
    return getTransformedCenter(transformers)
        .distance(transformers.compute(new Coord3d(xmin, ymin, zmin)));
  }

  /**
   * Return a copy of the current bounding box after scaling. Scaling does not modify the current
   * bounding box.
   * 
   * @return the scaled bounding box
   */
  public BoundingBox3d scale(Coord3d scale) {
    BoundingBox3d b = new BoundingBox3d();
    b.xmax = xmax * scale.x;
    b.xmin = xmin * scale.x;
    b.ymax = ymax * scale.y;
    b.ymin = ymin * scale.y;
    b.zmax = zmax * scale.z;
    b.zmin = zmin * scale.z;
    return b;
  }

  public BoundingBox3d shift(Coord3d offset) {
    BoundingBox3d b = new BoundingBox3d();
    b.xmax = xmax + offset.x;
    b.xmin = xmin + offset.x;
    b.ymax = ymax + offset.y;
    b.ymin = ymin + offset.y;
    b.zmax = zmax + offset.z;
    b.zmin = zmin + offset.z;
    return b;
  }

  /** Return true if b2 is contained by this. */
  public boolean contains(BoundingBox3d b2) {
    return xmin <= b2.xmin && b2.xmax <= xmax && ymin <= b2.ymin && b2.ymax <= ymax
        && zmin <= b2.zmin && b2.zmax <= zmax;
  }

  public boolean contains(Coord3d c) {
    if (c.x < getXmin())
      return false;
    else if (c.x > getXmax())
      return false;
    else if (c.y < getYmin())
      return false;
    else if (c.y > getYmax())
      return false;
    else if (c.z < getZmin())
      return false;
    else if (c.z > getZmax())
      return false;
    return true;
  }

  /** Return true if intersect b2. */
  public boolean intersect(BoundingBox3d b2) {
    return ((xmin <= b2.xmin && b2.xmin <= xmax) || (xmin <= b2.xmax && b2.xmax <= xmax))
        && ((ymin <= b2.ymin && b2.ymin <= ymax) || (ymin <= b2.ymax && b2.ymax <= ymax))
        && ((zmin <= b2.zmin && b2.zmin <= zmax) || (zmin <= b2.zmax && b2.zmax <= zmax));
  }

  /*********************************************************/

  /**
   * Add a margin to max values and substract a margin to min values
   * @return a new bounding box
   */
  public BoundingBox3d margin(float margin) {
    BoundingBox3d b = new BoundingBox3d();
    b.xmax = xmax + margin;
    b.xmin = xmin - margin;
    b.ymax = ymax + margin;
    b.ymin = ymin - margin;
    b.zmax = zmax + margin;
    b.zmin = zmin - margin;
    return b;
  }

  /**
   * Add a margin to max values and substract a margin to min values, where the margin is ratio of the current range of each dimension.
   * 
   * Adding a margin of 10% for each dimension is done with {@link #marginRatio(0.1)}
   * 
   * @return a new bounding box
   */
  public BoundingBox3d marginRatio(float marginRatio) {
    float xMargin = (xmax-xmin)*marginRatio;
    float yMargin = (ymax-ymin)*marginRatio;
    float zMargin = (zmax-zmin)*marginRatio;

    BoundingBox3d b = new BoundingBox3d();
    b.xmax = xmax + xMargin;
    b.xmin = xmin - xMargin;
    b.ymax = ymax + yMargin;
    b.ymin = ymin - yMargin;
    b.zmax = zmax + zMargin;
    b.zmin = zmin - zMargin;
    return b;
  }

  public BoundingBox3d selfMargin(float margin) {
    xmax += margin;
    xmin -= margin;
    ymax += margin;
    ymin -= margin;
    zmax += margin;
    zmin -= margin;
    return this;
  }

  public BoundingBox3d selfMarginRatio(float marginRatio) {
    float xMargin = (xmax-xmin)*marginRatio;
    float yMargin = (ymax-ymin)*marginRatio;
    float zMargin = (zmax-zmin)*marginRatio;
    
    xmax += xMargin;
    xmin -= xMargin;
    ymax += yMargin;
    ymin -= yMargin;
    zmax += zMargin;
    zmin -= zMargin;
    return this;
  }

  /**
   * Return range of X dimension.
   * 
   * @see {@link getRange}
   */
  public Range getXRange() {
    return new Range(xmin, xmax);
  }

  public Range getYRange() {
    return new Range(ymin, ymax);
  }

  public Range getZRange() {
    return new Range(zmin, zmax);
  }

  /**
   * Return range of each dimension.
   * 
   * @see {@link getXRange}
   */
  public Coord3d getRange() {
    return new Coord3d(getXRange().getRange(), getYRange().getRange(), getZRange().getRange());
  }

  public float getXmin() {
    return xmin;
  }

  public void setXmin(float value) {
    xmin = value;
  }

  public float getXmax() {
    return xmax;
  }

  public void setXmax(float value) {
    xmax = value;
  }

  public float getYmin() {
    return ymin;
  }

  public void setYmin(float value) {
    ymin = value;
  }

  public float getYmax() {
    return ymax;
  }

  public void setYmax(float value) {
    ymax = value;
  }

  public float getZmin() {
    return zmin;
  }

  public void setZmin(float value) {
    zmin = value;
  }

  public float getZmax() {
    return zmax;
  }

  public void setZmax(float value) {
    zmax = value;
  }

  public List<Coord3d> getVertices() {
    List<Coord3d> edges = new ArrayList<Coord3d>(8);
    edges.add(new Coord3d(xmin, ymin, zmin));
    edges.add(new Coord3d(xmin, ymax, zmin));
    edges.add(new Coord3d(xmax, ymax, zmin));
    edges.add(new Coord3d(xmax, ymin, zmin));
    edges.add(new Coord3d(xmin, ymin, zmax));
    edges.add(new Coord3d(xmin, ymax, zmax));
    edges.add(new Coord3d(xmax, ymax, zmax));
    edges.add(new Coord3d(xmax, ymin, zmax));
    return edges;
  }
  
  public void apply(Transform transform) {
    Corners c = getCorners();
    
    Coord3d min = transform.compute(c.getXminYminZmin());
        
    Coord3d max = transform.compute(c.getXmaxYmaxZmax());
    
    setXmin(min.x);
    setXmax(max.x);

    setYmin(min.y);
    setYmax(max.y);

    setZmin(min.z);
    setZmax(max.z);

  }

  /*********************************************************/

  public static BoundingBox3d newBoundsAtOrigin() {
    return new BoundingBox3d(Coord3d.ORIGIN, 0);
  }

  /*********************************************************/

  @Override
  public String toString() {
    return toString(0);
  }

  public String toString(int depth) {
    return Utils.blanks(depth) + "(BoundingBox3d)" + xmin + "<=x<=" + xmax + " | " + ymin + "<=y<="
        + ymax + " | " + zmin + "<=z<=" + zmax;
  }

  /*********************************************************/

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(xmax);
    result = prime * result + Float.floatToIntBits(xmin);
    result = prime * result + Float.floatToIntBits(ymax);
    result = prime * result + Float.floatToIntBits(ymin);
    result = prime * result + Float.floatToIntBits(zmax);
    result = prime * result + Float.floatToIntBits(zmin);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof BoundingBox3d))
      return false;

    BoundingBox3d other = (BoundingBox3d) obj;
    if (Float.floatToIntBits(xmax) != Float.floatToIntBits(other.xmax))
      return false;
    if (Float.floatToIntBits(xmin) != Float.floatToIntBits(other.xmin))
      return false;
    if (Float.floatToIntBits(ymax) != Float.floatToIntBits(other.ymax))
      return false;
    if (Float.floatToIntBits(ymin) != Float.floatToIntBits(other.ymin))
      return false;
    if (Float.floatToIntBits(zmax) != Float.floatToIntBits(other.zmax))
      return false;
    if (Float.floatToIntBits(zmin) != Float.floatToIntBits(other.zmin))
      return false;

    return true;
  }

  @Override
  public BoundingBox3d clone() {
    return new BoundingBox3d(xmin, xmax, ymin, ymax, zmin, zmax);
  }

  /**
   * Clone bounding box and apply transform to it
   * 
   * @param transformers
   * @return
   */
  public BoundingBox3d transform(SpaceTransformer transformers) {
    return new BoundingBox3d(transformers.getX().compute(xmin), transformers.getX().compute(xmax),
        transformers.getY().compute(ymin), transformers.getY().compute(ymax),
        transformers.getZ().compute(zmin), transformers.getZ().compute(zmax));
  }

  public Corners getCorners() {
    return new Corners(this);
  }

  /* */



  public class Corners {
    BoundingBox3d box;

    public Corners(BoundingBox3d box) {
      this.box = box;
    }

    public Coord3d getXminYminZmin() {
      return new Coord3d(box.xmin, box.ymin, box.zmin);
    }

    public Coord3d getXminYminZmax() {
      return new Coord3d(box.xmin, box.ymin, box.zmax);
    }

    public Coord3d getXminYmaxZmin() {
      return new Coord3d(box.xmin, box.ymax, box.zmin);
    }

    public Coord3d getXminYmaxZmax() {
      return new Coord3d(box.xmin, box.ymax, box.zmax);
    }

    public Coord3d getXmaxYminZmin() {
      return new Coord3d(box.xmax, box.ymin, box.zmin);
    }

    public Coord3d getXmaxYminZmax() {
      return new Coord3d(box.xmax, box.ymin, box.zmax);
    }

    public Coord3d getXmaxYmaxZmin() {
      return new Coord3d(box.xmax, box.ymax, box.zmin);
    }

    public Coord3d getXmaxYmaxZmax() {
      return new Coord3d(box.xmax, box.ymax, box.zmax);
    }

    public List<Coord3d> getAll() {
      List<Coord3d> all = new ArrayList<>();

      all.add(getXminYminZmin());
      all.add(getXminYminZmax());

      all.add(getXminYmaxZmin());
      all.add(getXminYmaxZmax());

      all.add(getXmaxYminZmin());
      all.add(getXmaxYminZmax());

      all.add(getXmaxYmaxZmin());
      all.add(getXmaxYmaxZmax());

      return all;
    }

  }


}
