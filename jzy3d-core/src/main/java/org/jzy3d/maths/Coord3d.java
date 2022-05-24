package org.jzy3d.maths;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A {@link Coord3d} stores a 3 dimensional coordinate for cartesian or polar mode, and provide few
 * operators.
 * 
 * Operators allow adding, substracting, multiplying and divising coordinate values, as well as
 * computing the distance between two points, and converting polar and cartesian coordinates.
 * 
 * @author Martin Pernollet
 * 
 */
public class Coord3d implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -1636927109633279805L;

  public float x;
  public float y;
  public float z;
  
  public static List<Coord3d> list(int size) {
    return new ArrayList<Coord3d>(size);
  }

  public static List<Coord3d> list(Coord3d... coords) {
    return Arrays.asList(coords);
  }

  public static Coord3d[] array(Set<Coord3d> coords) {
    Coord3d[] candidates = new Coord3d[coords.size()];
    coords.toArray(candidates);
    return candidates;
  }

  public static Range getZRange(List<Coord3d> coords) {
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;

    for (Coord3d c : coords) {
      if (c.z > max)
        max = c.z;
      if (c.z < min)
        min = c.z;
    }
    return new Range(min, max);
  }

  /** The origin is a Coord3d having value 0 for each dimension. */
  public static final Coord3d ORIGIN = new Coord3d(0.0f, 0.0f, 0.0f);
  /** The origin is a Coord3d having value 1 for each dimension. */
  public static final Coord3d IDENTITY = new Coord3d(1.0f, 1.0f, 1.0f);
  /** An invalid Coord2d has value NaN for each dimension. */
  public static final Coord3d INVALID = new Coord3d(Float.NaN, Float.NaN, Float.NaN);

  /** Create a 3d coordinate with value 0 for each dimension. */
  public Coord3d() {
    x = 0.0f;
    y = 0.0f;
    z = 0.0f;
  }

  /**
   * Create a 3d coordinate. When using polar mode, x represents azimuth, y represents elevation,
   * and z represents range.
   */
  public Coord3d(float xi, float yi, float zi) {
    x = xi;
    y = yi;
    z = zi;
  }

  public Coord3d(Coord2d c, float zi) {
    x = c.x;
    y = c.y;
    z = zi;
  }

  public Coord3d(Coord2d c, double zi) {
    x = c.x;
    y = c.y;
    z = (float) zi;
  }

  public Coord3d(Coord2d c) {
    this(c, 0);
  }

  public Coord3d(float[] c) {
    x = c[0];
    y = c[1];
    z = c[2];
  }

  public Coord3d(double[] c) {
    x = (float)c[0];
    y = (float)c[1];
    z = (float)c[2];
  }

  /**
   * Create a 3d coordinate. When using polar mode, x is azimuth, y is elevation, and z is range.
   */
  public Coord3d(double xi, double yi, double zi) {
    x = (float) xi;
    y = (float) yi;
    z = (float) zi;
  }

  public Coord3d set(Coord3d c2) {
    x = c2.x;
    y = c2.y;
    z = c2.z;
    return this;
  }

  public Coord3d set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  /** Return a duplicate of this 3d coordinate. */
  @Override
  public Coord3d clone() {
    return new Coord3d(x, y, z);
  }

  /** Return the x and y component as a 2d coordinate. */
  public Coord2d getXY() {
    return new Coord2d(x, y);
  }

  /**************************************************************/

  /**
   * Add a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3d add(Coord3d c2) {
    return new Coord3d(x + c2.x, y + c2.y, z + c2.z);
  }

  public Coord3d add(float x, float y, float z) {
    return new Coord3d(this.x + x, this.y + y, this.z + z);
  }

  public Coord3d addSelf(Coord3d c2) {
    x += c2.x;
    y += c2.y;
    z += c2.z;
    return this;
  }

  public Coord3d addSelf(float x, float y, float z) {
    this.x += x;
    this.y += y;
    this.z += z;
    return this;
  }

  /**
   * Add a value to all components of the current Coord and return the result in a new Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3d add(float value) {
    return new Coord3d(x + value, y + value, z + value);
  }

  public Coord3d addSelf(float value) {
    x += value;
    y += value;
    z += value;
    return this;
  }

  /**
   * Substract a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3d sub(Coord3d c2) {
    return new Coord3d(x - c2.x, y - c2.y, z - c2.z);
  }

  public Coord3d sub(float x2, float y2, float z2) {
    return new Coord3d(x - x2, y - y2, z - z2);
  }

  public Coord3d subSelf(Coord3d c2) {
    x -= c2.x;
    y -= c2.y;
    z -= c2.z;
    return this;
  }

  /**
   * Substract a value to all components of the current Coord and return the result in a new
   * Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3d sub(float value) {
    return new Coord3d(x - value, y - value, z - value);
  }

  public Coord3d subSelf(float value) {
    x -= value;
    y -= value;
    z -= value;
    return this;
  }

  /**
   * Multiply a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3d mul(Coord3d c2) {
    return new Coord3d(x * c2.x, y * c2.y, z * c2.z);
  }

  public Coord3d mul(float x, float y, float z) {
    return new Coord3d(this.x * x, this.y * y, this.z * z);
  }

  /**
   * Multiply all components of the current Coord and return the result in a new Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3d mul(float value) {
    return new Coord3d(x * value, y * value, z * value);
  }

  public void mulSelf(Coord3d c2) {
    x *= c2.x;
    y *= c2.y;
    z *= c2.z;
  }

  public void mulSelf(float x, float y, float z) {
    this.x *= x;
    this.y *= y;
    this.z *= z;
  }
  
  public void mulSelf(float value) {
    this.x *= value;
    this.y *= value;
    this.z *= value;
  }

  /**
   * Divise a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3d div(Coord3d c2) {
    return new Coord3d(x / c2.x, y / c2.y, z / c2.z);
  }

  public void divSelf(Coord3d c2) {
    x /= c2.x;
    y /= c2.y;
    z /= c2.z;
  }

  public void divSelf(float value) {
    x /= value;
    y /= value;
    z /= value;
  }

  public void divSelf(double value) {
    x /= value;
    y /= value;
    z /= value;
  }
  
  /**
   * Divise all components of the current Coord by the same value and return the result in a new
   * Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3d div(float value) {
    return new Coord3d(x / value, y / value, z / value);
  }

  public Coord3d negative() {
    return new Coord3d(-x, -y, -z);
  }

  /**
   * Converts the current Coord3d into cartesian coordinates and return the result in a new Coord3d.
   * 
   * Assume that
   * <ul>
   * <li>X represent azimuth
   * <li>Y represent elevation
   * <li>Z represent distance
   * </ul>
   * 
   * @return the result Coord3d
   */
  public Coord3d cartesian() {
    return new Coord3d(Math.cos(x) * Math.cos(y) * z, // azimuth
        Math.sin(x) * Math.cos(y) * z, // elevation
        Math.sin(y) * z); // range
  }

  public Coord3d cartesianSelf() {
    x = (float) (Math.cos(x) * Math.cos(y) * z); // azimuth
    y = (float) (Math.sin(x) * Math.cos(y) * z);// elevation
    z = (float) (Math.sin(y) * z); // range
    return this;
  }

  /**
   * Converts the current Coord3d into polar coordinates and return the result in a new Coord3d.
   * 
   * @return the result Coord3d
   */
  public Coord3d polar() {
    double a;
    double e;
    double r = Math.sqrt(x * x + y * y + z * z);
    double d = Math.sqrt(x * x + y * y);

    // case x=0 and y=0
    if (d == 0 && z > 0)
      return new Coord3d(0, Math.PI / 2, r);
    else if (d == 0 && z <= 0)
      return new Coord3d(0, -Math.PI / 2, r);
    // other cases
    else {
      // classical case for azimuth
      if (Math.abs(x / d) < 1)
        a = Math.acos(x / d) * (y > 0 ? 1 : -1);
      // special on each pole for azimuth
      else if (y == 0 && x > 0) // y==0
        a = 0;
      else if (y == 0 && x < 0)
        a = Math.PI;
      else
        a = 0;

      e = Math.atan(z / d);

      return new Coord3d(a, e, r);
    }
  }

  public Coord3d polarSelf() {
    double a;
    double e;
    double r = Math.sqrt(x * x + y * y + z * z);
    double d = Math.sqrt(x * x + y * y);

    // case x=0 and y=0
    if (d == 0 && z > 0) {
      x = 0;
      y = (float) (Math.PI / 2);
      z = (float) r;
      return this;
      // return new Coord3d(0, Math.PI / 2, r);
    } else if (d == 0 && z <= 0) {
      x = 0;
      y = (float) (-Math.PI / 2);
      z = (float) r;
      return this;
      // return new Coord3d(0, -Math.PI / 2, r);
    }
    // other cases
    else {
      // classical case for azimuth
      if (Math.abs(x / d) < 1)
        a = Math.acos(x / d) * (y > 0 ? 1 : -1);
      // special on each pole for azimuth
      else if (y == 0 && x > 0) // y==0
        a = 0;
      else if (y == 0 && x < 0)
        a = Math.PI;
      else
        a = 0;

      e = Math.atan(z / d);

      x = (float) a;
      y = (float) e;
      z = (float) r;
      return this;
    }
  }

  /** Compute the distance between two coordinates. */
  public double distance(Coord3d c) {
    return Math.sqrt(distanceSq(c));
  }

  /** Compute the square distance between two coordinates. */
  public double distanceSq(Coord3d c) {
    return Math.pow(x - c.x, 2) + Math.pow(y - c.y, 2) + Math.pow(z - c.z, 2);
  }

  /**************************************************************/

  public float magSquared() {
    return x * x + y * y + z * z;
  }

  public Coord3d getNormalizedTo(float len) {
    return clone().normalizeTo(len);
  }

  public Coord3d normalizeTo(float len) {
    float mag = (float) Math.sqrt(x * x + y * y + z * z);
    if (mag > 0) {
      mag = len / mag;
      x *= mag;
      y *= mag;
      z *= mag;
    }
    return this;
  }

  public final float dot(Coord3d v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public final Coord3d cross(Coord3d v) {
    return new Coord3d(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
  }

  /**
   * Applies a rotation represented by the AxisAngle notation using the Rodrigues' rotation formula.
   * <p/>
   * math implemented using http://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula
   *
   * @param angleDeg angle of rotation about the given axis [deg]
   * @param axis unit vector describing an axis of rotation
   * @return rotated copy of the original vector
   */
  public final Coord3d rotate(float angleDeg, Coord3d axis) {
    float angleRad = (float) Math.toRadians(angleDeg);
    float s = (float) Math.sin(angleRad);
    float c = (float) Math.cos(angleRad);
    Coord3d v = this;
    Coord3d k = axis.normalizeTo(1f);

    float kdotv = k.dot(v);
    Coord3d kXv = k.cross(v);
    return new Coord3d(v.x * c + kXv.x * s + k.x * kdotv * (1 - c),
        v.y * c + kXv.y * s + k.y * kdotv * (1 - c), v.z * c + kXv.z * s + k.z * kdotv * (1 - c));
  }

  public final Coord3d interpolateTo(Coord3d v, float f) {
    return new Coord3d(x + (v.x - x) * f, y + (v.y - y) * f, z + (v.z - z) * f);
  }
  
  /**************************************************************/

  /** Return a string representation of this coordinate. */
  @Override
  public String toString() {
    return ("x=" + x + " y=" + y + " z=" + z);
  }

  /** Return an array representation of this coordinate. */
  public float[] toArray() {
    float[] array = {x, y, z};
    return array;
  }
  
  public void toArray(float[] array, int offset) {
    array[offset+0] = x;
    array[offset+1] = y;
    array[offset+2] = z;
  }

  /**************************************************************/

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    result = prime * result + Float.floatToIntBits(z);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;

    if (!(obj instanceof Coord3d))
      return false;

    Coord3d other = (Coord3d) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
      return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
      return false;
    if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
      return false;
    return true;
  }

  /**************************************************************/

  /**
   * Compute the component-wise minimum values of a set of coordinates.
   * 
   * @param coords
   * @return minimum value on each dimension
   */
  public static Coord3d min(List<Coord3d> coords) {
    Coord3d min =
        new Coord3d(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    for (Coord3d c : coords) {
      if (c.x < min.x)
        min.x = c.x;

      if (c.y < min.y)
        min.y = c.y;

      if (c.z < min.z)
        min.z = c.z;
    }
    return min;
  }

  /**
   * Compute the component-wise minimum values of a set of coordinates.
   * 
   * @param coords
   * @return maximum value on each dimension
   */
  public static Coord3d max(List<Coord3d> coords) {
    Coord3d max =
        new Coord3d(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    for (Coord3d c : coords) {
      if (c.x > max.x)
        max.x = c.x;

      if (c.y > max.y)
        max.y = c.y;

      if (c.z > max.z)
        max.z = c.z;
    }
    return max;
  }

  /**
   * Compute the component-wise minimum and maximum values of a set of coordinates.
   * 
   * @param coords
   * @return maximum value on each dimension
   */
  public static Pair<Coord3d, Coord3d> minMax(List<Coord3d> coords) {
    Coord3d min =
        new Coord3d(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    Coord3d max =
        new Coord3d(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    for (Coord3d c : coords) {
      if (c.x < min.x)
        min.x = c.x;
      if (c.y < min.y)
        min.y = c.y;
      if (c.z < min.z)
        min.z = c.z;

      if (c.x > max.x)
        max.x = c.x;
      if (c.y > max.y)
        max.y = c.y;
      if (c.z > max.z)
        max.z = c.z;
    }
    return new Pair<Coord3d, Coord3d>(min, max);
  }

  public static void add(List<Coord3d> coords, float x, float y, float z) {
    add(coords, new Coord3d(x, y, z));
  }

  public static void add(List<Coord3d> coords, Coord3d add) {
    for (Coord3d c : coords) {
      c.addSelf(add);
    }
  }

  public static void sub(List<Coord3d> coords, float x, float y, float z) {
    sub(coords, new Coord3d(x, y, z));
  }

  public static void sub(List<Coord3d> coords, Coord3d add) {
    for (Coord3d c : coords) {
      c.subSelf(add);
    }
  }

  public static void mul(List<Coord3d> coords, float x, float y, float z) {
    mul(coords, new Coord3d(x, y, z));
  }

  public static void mul(List<Coord3d> coords, Coord3d multiplier) {
    for (Coord3d c : coords) {
      c.mulSelf(multiplier);
    }
  }

  public static void div(List<Coord3d> coords, float x, float y, float z) {
    div(coords, new Coord3d(x, y, z));
  }

  public static void div(List<Coord3d> coords, Coord3d div) {
    for (Coord3d c : coords) {
      c.divSelf(div);
    }
  }

  public static List<Coord3d> clone(List<Coord3d> coords) {
    List<Coord3d> clone = new ArrayList<>();
    for (Coord3d c : coords) {
      clone.add(c.clone());
    }
    return clone;
  }
  
  public static List<Coord3d> getCoords(double[] array) {
    List<Coord3d> c = new ArrayList<>(array.length/3);

    for (int j = 0; j < array.length; j+=3) {
      c.add(getCoordAt(array, j));
    }
    return c;
  }
  
  public static Coord3d getCoordAt(double[] array, int i) {
    return new Coord3d(array[i], array[i+1], array[i+2]);
  }
  
  public static List<Coord3d> getCoords(float[] array) {
    List<Coord3d> c = new ArrayList<>(array.length/3);

    for (int j = 0; j < array.length; j+=3) {
      c.add(getCoordAt(array, j));
    }
    return c;
  }
  
  public static Coord3d getCoordAt(float[] array, int i) {
    return new Coord3d(array[i], array[i+1], array[i+2]);
  }


}
