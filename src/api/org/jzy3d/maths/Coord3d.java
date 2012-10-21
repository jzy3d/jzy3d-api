package org.jzy3d.maths;

/**
 * A {@link Coord3d} stores a 3 dimensional coordinate for cartesian or polar
 * mode, and provide few operators.
 * 
 * Operators allow adding, substracting, multiplying and divising coordinate
 * values, as well as computing the distance between two points, and converting
 * polar and cartesian coordinates.
 * 
 * @author Martin Pernollet
 * 
 */
public class Coord3d {

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
     * Create a 3d coordinate. When using polar mode, x represents azimuth, y
     * represents elevation, and z represents range.
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

    public Coord3d(float[] c) {
        x = c[0];
        y = c[1];
        z = c[2];
    }

    /**
     * Create a 3d coordinate. When using polar mode, x is azimuth, y is
     * elevation, and z is range.
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

    /**
     * Add a value to all components of the current Coord and return the result
     * in a new Coord3d.
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
     * Substract a Coord3d to the current one and return the result in a new
     * Coord3d.
     * 
     * @param c2
     * @return the result Coord3d
     */
    public Coord3d sub(Coord3d c2) {
        return new Coord3d(x - c2.x, y - c2.y, z - c2.z);
    }

    public Coord3d subSelf(Coord3d c2) {
        x -= c2.x;
        y -= c2.y;
        z -= c2.z;
        return this;
    }

    /**
     * Substract a value to all components of the current Coord and return the
     * result in a new Coord3d.
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
     * Multiply a Coord3d to the current one and return the result in a new
     * Coord3d.
     * 
     * @param c2
     * @return the result Coord3d
     */
    public Coord3d mul(Coord3d c2) {
        return new Coord3d(x * c2.x, y * c2.y, z * c2.z);
    }

    public void mulSelf(Coord3d c2) {
        x *= c2.x;
        y *= c2.y;
        z *= c2.z;
    }

    /**
     * Multiply all components of the current Coord and return the result in a
     * new Coord3d.
     * 
     * @param value
     * @return the result Coord3d
     */
    public Coord3d mul(float value) {
        return new Coord3d(x * value, y * value, z * value);
    }

    /**
     * Divise a Coord3d to the current one and return the result in a new
     * Coord3d.
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

    /**
     * Divise all components of the current Coord by the same value and return
     * the result in a new Coord3d.
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
     * Converts the current Coord3d into cartesian coordinates and return the
     * result in a new Coord3d.
     * 
     * @return the result Coord3d
     */
    public Coord3d cartesian() {
        return new Coord3d(Math.cos(x) * Math.cos(y) * z, // azimuth
                Math.sin(x) * Math.cos(y) * z, // elevation
                Math.sin(y) * z); // range
        /*
         * return new Coord3d( Math.sin(x) * Math.cos(y) * z, // azimuth
         * Math.sin(x) * Math.sin(y) * z, // elevation Math.cos(x) * z); //
         * range*
         * 
         * return new Coord3d( Math.cos(x) * Math.cos(y) * z, // azimuth
         * Math.sin(x) * Math.cos(y) * z, // elevation Math.sin(y) * z); //
         * range
         */
    }

    /**
     * Converts the current Coord3d into polar coordinates and return the result
     * in a new Coord3d.
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

        /*
         * return new Coord3d( Math.atan(y/x), // azimuth Math.acos(z/r), //
         * elevation r); // range
         */
    }

    /** Compute the distance between two coordinates. */
    public double distance(Coord3d c) {
        return Math.sqrt(distanceSq(c));
    }

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

    public final Coord3d interpolateTo(Coord3d v, float f) {
        return new Coord3d(x + (v.x - x) * f, y + (v.y - y) * f, z + (v.z - z) * f);
    }

    /**************************************************************/

    /** Return a string representation of this coordinate. */
    public String toString() {
        return ("x=" + x + " y=" + y + " z=" + z);
    }

    /** Return an array representation of this coordinate. */
    public float[] toArray() {
        float[] array = { x, y, z };
        return array;
    }

    /**************************************************************/

    @Override
    public boolean equals(Object aThat) {
        // check for self-comparison
        if (this == aThat)
            return true;

        // use instanceof instead of getClass here for two reasons
        // 1. if need be, it can match any supertype, and not just one class;
        // 2. it renders an explict check for "that == null" redundant, since
        // it does the check for null already - "null instanceof [type]" always
        // returns false. (See Effective Java by Joshua Bloch.)
        if (!(aThat instanceof Coord3d))
            return false;
        // Alternative to the above line :
        // if ( aThat == null || aThat.getClass() != this.getClass() ) return
        // false;

        // cast to native object is now safe
        Coord3d that = (Coord3d) aThat;

        // now a proper field-by-field evaluation can be made
        return ((this.x == that.x) && (this.y == that.y) && (this.z == that.z));
    }

    @Override
    public int hashCode() {
        if (fHashCode == 0) {
            fHashCode = (int) (this.x * 100000000.0f + this.y * 10000.0f + this.z);
        }
        return fHashCode;
    }

    /**************************************************************/

    private int fHashCode;
    public float x;
    public float y;
    public float z;
}
