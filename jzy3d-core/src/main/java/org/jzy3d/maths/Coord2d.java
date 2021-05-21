package org.jzy3d.maths;

import java.io.Serializable;


/**
 * A {@link Coord2d} stores a 2 dimensional coordinate for cartesian (x,y) or polar (a,r) mode, and
 * provide operators allowing to add, substract, multiply and divises coordinate values, as well as
 * computing the distance between two points, and converting polar and cartesian coordinates.
 * 
 * @author Martin Pernollet
 */
public class Coord2d implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 3968428005200709871L;
  /** The origin is a Coord2d having value 0 for each dimension. */
  public static final Coord2d ORIGIN = new Coord2d(0.0f, 0.0f);
  /** An invalid Coord2d has value NaN for each dimension. */
  public static final Coord2d INVALID = new Coord2d(Float.NaN, Float.NaN);

  /** Creates a 2d coordinate with the value 0 for each dimension. */
  public Coord2d() {
    x = 0.0f;
    y = 0.0f;
  }

  /**
   * Creates a 2d coordinate. When using polar mode, x represents angle, and y represents distance.
   */
  public Coord2d(float xi, float yi) {
    x = xi;
    y = yi;
  }

  /**
   * Creates a 2d coordinate. When using polar mode, x represents angle, and y represents distance.
   */
  public Coord2d(double xi, double yi) {
    x = (float) xi;
    y = (float) yi;
  }

  public Coord2d(double xi) {
    this(xi, 0);
  }

  /** Return a duplicate of this 3d coordinate. */
  @Override
  public Coord2d clone() {
    return new Coord2d(x, y);
  }

  public void set(Coord2d c) {
    this.x = c.x;
    this.y = c.y;
  }

  public void set(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Add a Coord2d to the current one and return the result in a new Coord2d.
   * 
   * @param c2
   * @return the result Coord2d
   */
  public Coord2d add(Coord2d c2) {
    return new Coord2d(x + c2.x, y + c2.y);
  }

  public void addSelf(Coord2d c2) {
    x += c2.x;
    y += c2.y;
  }

  public void addSelf(float x, float y) {
    this.x += x;
    this.y += y;
  }

  public void addSelfX(float x) {
    this.x += x;
  }

  public void addSelfY(float y) {
    this.y += y;
  }

  /**
   * Add a value to all components of the current Coord and return the result in a new Coord2d.
   * 
   * @param value
   * @return the result Coord2d
   */
  public Coord2d add(float value) {
    return new Coord2d(x + value, y + value);
  }

  public Coord2d add(float x, float y) {
    return new Coord2d(this.x + x, this.y + y);
  }

  /**
   * Substract a Coord2d to the current one and return the result in a new Coord2d.
   * 
   * @param c2
   * @return the result Coord2d
   */
  public Coord2d sub(Coord2d c2) {
    return new Coord2d(x - c2.x, y - c2.y);
  }

  /**
   * Substract a value to all components of the current Coord and return the result in a new
   * Coord2d.
   * 
   * @param value
   * @return the result Coord2d
   */
  public Coord2d sub(float value) {
    return new Coord2d(x - value, y - value);
  }

  public Coord2d sub(float x, float y) {
    return new Coord2d(this.x - x, this.y - y);
  }

  /**
   * Multiply a Coord2d to the current one and return the result in a new Coord2d.
   * 
   * @param c2
   * @return the result Coord2d
   */
  public Coord2d mul(Coord2d c2) {
    return new Coord2d(x * c2.x, y * c2.y);
  }

  public Coord2d mul(float x, float y) {
    return new Coord2d(this.x * x, this.y * y);
  }

  /**
   * Multiply all components of the current Coord and return the result in a new Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord2d mul(float value) {
    return new Coord2d(x * value, y * value);
  }
  
  public Coord2d mulSelf(Coord2d c) {
    return mulSelf(c.x, c.y);
  }

  
  public Coord2d mulSelf(float x, float y) {
    this.x *= x;
    this.y *= y;
    return this;
  }

  /**
   * Divise a Coord2d to the current one and return the result in a new Coord2d.
   * 
   * @param c2
   * @return the result Coord2d
   */
  public Coord2d div(Coord2d c2) {
    return new Coord2d(x / c2.x, y / c2.y);
  }

  /**
   * Divise all components of the current Coord by the same value and return the result in a new
   * Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord2d div(float value) {
    return new Coord2d(x / value, y / value);
  }

  public Coord2d div(float x, float y) {
    return new Coord2d(this.x / x, this.y / y);
  }

  public void divSelf(float value) {
    x /= value;
    y /= value;
  }

  /**
   * Converts the current Coord3d into cartesian coordinates and return the result in a new Coord3d.
   * 
   * @return the result Coord3d
   */
  public Coord2d cartesian() {
    return new Coord2d(Math.cos(x) * y, Math.sin(x) * y);
  }

  /**
   * Converts the current {@link Coord2d} into polar coordinates and return the result in a new
   * {@link Coord2d}.
   */
  public Coord2d polar() {
    return new Coord2d(Math.atan(y / x), Math.sqrt(x * x + y * y));
  }

  /**
   * Return a real polar value, with an angle in the range [0;2*PI]
   * http://fr.wikipedia.org/wiki/Coordonn%C3%A9es_polaires
   */
  public Coord2d fullPolar() {
    double radius = Math.sqrt(x * x + y * y);

    if (x < 0) {
      return new Coord2d(Math.atan(y / x) + Math.PI, radius);
    } else if (x > 0) {
      if (y >= 0)
        return new Coord2d(Math.atan(y / x), radius);
      else
        return new Coord2d(Math.atan(y / x) + 2 * Math.PI, radius);
    } else { // x==0
      if (y > 0)
        return new Coord2d(Math.PI / 2, radius);
      else if (y < 0)
        return new Coord2d(3 * Math.PI / 2, radius);
      else // y==0
        return new Coord2d(0, 0);
    }
  }

  /** Compute the distance between two coordinates. */
  public double distance(Coord2d c) {
    return Math.sqrt(Math.pow(x - c.x, 2) + Math.pow(y - c.y, 2));
  }

  public double distanceSq(Coord2d c) {
    return Math.pow(x - c.x, 2) + Math.pow(y - c.y, 2);
  }

  /**
   * Returns an interpolated point between the current and given point, according to an input ratio
   * in [0;1] that indicates how near to the current point the new point will stand.
   * 
   * A value of 1 will return a copy of the current point.
   */
  public Coord2d interpolation(Coord2d c, float ratio) {
    float inv = 1 - ratio;
    float xx = x * ratio + c.x * inv;
    float yy = y * ratio + c.y * inv;
    return new Coord2d(xx, yy);
  }

  public Coord2d mean(Coord2d b) {
    Coord2d mean = new Coord2d();
    mean.x = (x + b.x) / 2;
    mean.y = (y + b.y) / 2;
    return mean;
  }

  public static Coord2d addWeighted(Coord2d a, Coord2d b, float weigthA, float weightB) {
    Coord2d mean = new Coord2d();
    mean.x = a.x * weigthA + b.x * weightB;
    mean.y = a.y * weigthA + b.y * weightB;
    return mean;
  }

  public static Coord2d interpolate(Coord2d a, Coord2d b, float weigthA) {
    return addWeighted(a, b, weigthA, 1f - weigthA);
  }

  /** Return a string representation of this coordinate. */
  @Override
  public String toString() {
    return ("x=" + x + " y=" + y);
  }

  /** Return an array representation of this coordinate. */
  public float[] toArray() {
    float[] array = new float[2];
    array[0] = x;
    array[1] = y;

    return array;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float x;
  public float y;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(x);
    result = prime * result + Float.floatToIntBits(y);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;

    if (!(obj instanceof Coord2d))
      return false;

    Coord2d other = (Coord2d) obj;
    if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
      return false;
    if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
      return false;
    return true;
  }

  public Coord3d to3d() {
    return new Coord3d(this);
  }
}
