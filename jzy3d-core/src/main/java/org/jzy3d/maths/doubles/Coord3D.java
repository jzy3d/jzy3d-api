package org.jzy3d.maths.doubles;

import org.jzy3d.maths.Coord3d;

/**
 * Store coordinates in double precision (whereas {@link Coord3d} store coordinates with float
 * precision)
 * 
 * @author martin
 *
 */
public class Coord3D {
  public double x;
  public double y;
  public double z;

  public Coord3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coord3D(Coord3d c) {
    this.x = c.x;
    this.y = c.y;
    this.z = c.z;
  }

  public Coord3D cartesian() {
    return new Coord3D(Math.cos(x) * Math.cos(y) * z, // azimuth
        Math.sin(x) * Math.cos(y) * z, // elevation
        Math.sin(y) * z); // range
  }

  public Coord3D cartesianSelf() {
    x = (Math.cos(x) * Math.cos(y) * z); // azimuth
    y = (Math.sin(x) * Math.cos(y) * z);// elevation
    z = (Math.sin(y) * z); // range
    return this;
  }

  /**
   * Converts the current Coord3d into polar coordinates and return the result in a new Coord3d.
   * 
   * @return the result Coord3d
   */
  public Coord3D polar() {
    double a;
    double e;
    double r = Math.sqrt(x * x + y * y + z * z);
    double d = Math.sqrt(x * x + y * y);

    // case x=0 and y=0
    if (d == 0 && z > 0)
      return new Coord3D(0, Math.PI / 2, r);
    else if (d == 0 && z <= 0)
      return new Coord3D(0, -Math.PI / 2, r);
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

      return new Coord3D(a, e, r);
    }
  }

  public Coord3D polarSelf() {
    z = Math.sqrt(x * x + y * y + z * z);
    double d = Math.sqrt(x * x + y * y);

    // case x=0 and y=0
    if (d == 0 && z > 0) {
      x = 0;
      y = Math.PI / 2;
      return this;
    } else if (d == 0 && z <= 0) {
      x = 0;
      y = -Math.PI / 2;
      return this;
    }
    // other cases
    else {
      // classical case for azimuth
      if (Math.abs(x / d) < 1)
        x = Math.acos(x / d) * (y > 0 ? 1 : -1);
      // special on each pole for azimuth
      else if (y == 0 && x > 0) // y==0
        x = 0;
      else if (y == 0 && x < 0)
        x = Math.PI;
      else
        x = 0;

      y = Math.atan(z / d);

      return this;
    }
  }

  /**************************************************************/

  /**
   * Add a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3D add(Coord3D c2) {
    return new Coord3D(x + c2.x, y + c2.y, z + c2.z);
  }

  public Coord3D add(double x, double y, double z) {
    return new Coord3D(this.x + x, this.y + y, this.z + z);
  }

  public Coord3D addSelf(Coord3d c2) {
    x += c2.x;
    y += c2.y;
    z += c2.z;
    return this;
  }

  public Coord3D addSelf(float x, float y, float z) {
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
  public Coord3D add(float value) {
    return new Coord3D(x + value, y + value, z + value);
  }

  public Coord3D addSelf(float value) {
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
  public Coord3D sub(Coord3D c2) {
    return new Coord3D(x - c2.x, y - c2.y, z - c2.z);
  }

  public Coord3D subSelf(Coord3D c2) {
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
  public Coord3D sub(double value) {
    return new Coord3D(x - value, y - value, z - value);
  }

  public Coord3D subSelf(double value) {
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
  public Coord3D mul(Coord3D c2) {
    return new Coord3D(x * c2.x, y * c2.y, z * c2.z);
  }

  public Coord3D mul(double x, double y, double z) {
    return new Coord3D(this.x * x, this.y * y, this.z * z);
  }

  /**
   * Multiply all components of the current Coord and return the result in a new Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3D mul(double value) {
    return new Coord3D(x * value, y * value, z * value);
  }

  public void mulSelf(Coord3D c2) {
    x *= c2.x;
    y *= c2.y;
    z *= c2.z;
  }

  /**
   * Divise a Coord3d to the current one and return the result in a new Coord3d.
   * 
   * @param c2
   * @return the result Coord3d
   */
  public Coord3D div(Coord3D c2) {
    return new Coord3D(x / c2.x, y / c2.y, z / c2.z);
  }

  public void divSelf(Coord3D c2) {
    x /= c2.x;
    y /= c2.y;
    z /= c2.z;
  }

  /**
   * Divise all components of the current Coord by the same value and return the result in a new
   * Coord3d.
   * 
   * @param value
   * @return the result Coord3d
   */
  public Coord3D div(double value) {
    return new Coord3D(x / value, y / value, z / value);
  }


  /**************************************************************/

  /** Return a string representation of this coordinate. */
  @Override
  public String toString() {
    return ("x=" + x + " y=" + y + " z=" + z);
  }

  public Coord3d toCoord3d() {
    return new Coord3d(x, y, z);
  }

  /** Return an array representation of this coordinate. */
  public double[] toArray() {
    double[] array = {x, y, z};
    return array;
  }

  /**************************************************************/

  @Override
  public int hashCode() {
    final int prime = 31;
    long result = 1;
    result = prime * result + Double.doubleToLongBits(x);
    result = prime * result + Double.doubleToLongBits(y);
    result = prime * result + Double.doubleToLongBits(z);
    return (int) result;
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
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    if (z != other.z)
      return false;
    return true;
  }
}
