package org.jzy3d.plot3d.builder.delaunay;

import java.util.Arrays;

import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coordinates;


/**
 *
 */
public class OrthonormalCoordinateValidator implements CoordinateValidator {

	/**
	 * 
	 * @throws an
	 *             IllegalArgumentException: Any multiple occurrences in x and y
	 *             are removed internally. If subsequently the length of x or y
	 *             differs from z an IllegalArgumentException is thrown.
	 * 
	 */
	public OrthonormalCoordinateValidator(Coordinates coords) {

		// is coords valid?
		if (coords == null) {
			throw new IllegalArgumentException(
					"got NULL value for parameter coords.");
		}

		// are coords members valid?
		if (coords.getX() == null || coords.getY() == null
				|| coords.getZ() == null) {
			throw new IllegalArgumentException("got NULL value after calling: "
					+ "coords.getX()" + coords.getX() + ", coords.getY()"
					+ coords.getY() + ", coord.getZ()" + coords.getZ());
		}

		// x, y and the z array must agreee in their length
		if (coords.getX().length != coords.getY().length
				|| coords.getX().length != coords.getZ().length) {
			throw new IllegalArgumentException(
					"x, y, and z arrays must agree in length." + "x.length="
							+ coords.getX().length + ", y.length="
							+ coords.getY().length + ", z.length="
							+ coords.getZ().length);
		}

		setData(coords);

	}

	protected void setData(Coordinates coords) {

		// Initialize loading
		this.x = makeCoordinatesUnique(coords.getX());
		this.y = makeCoordinatesUnique(coords.getY());

		this.z_as_fxy = new float[this.x.length][this.y.length];

		for (int i = 0; i < this.x.length; i++) {
			for (int j = 0; j < this.y.length; j++) {
				this.z_as_fxy[i][j] = Float.NaN;
			}
		}

		// Fill Z matrix and set surface minimum and maximum
		boolean found;
		for (int p = 0; p < coords.getZ().length; p++) {
			found = find(this.x, this.y, coords.getX()[p], coords.getY()[p]);
			if (!found) {
				throw new RuntimeException(
						"it seems (x[p],y[p]) has not been properly stored into (this.x,this.y)");
			}
			this.z_as_fxy[findxi][findyj] = coords.getZ()[p];
		}
	}

	/**
	 * Search in a couple of array a combination of values vx and vy. Positions
	 * xi and yi are returned by reference. Function returns true if the couple
	 * of data may be retrieved, false otherwise (in this case, xi and yj remain
	 * unchanged).
	 */
	protected boolean find(float[] x, float[] y, float vx, float vy) {
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				if (x[i] == vx && y[j] == vy) {
					findxi = i;
					findyj = j;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compute a sorted array from input, with a unique occurrence of each
	 * value. Note: any NaN value will be ignored and won't appear in the output
	 * array.
	 * 
	 * @param data
	 *            input array.
	 * @return a sorted array containing only one occurrence of each input
	 *         value.
	 * 
	 *         TODO: If x and y get sorted what about z?
	 */
	protected float[] makeCoordinatesUnique(float[] data) {
		float[] copy = Array.clone(data);
		Arrays.sort(copy);

		// count unique values
		int nunique = 0;
		float last = Float.NaN;
		for (int i = 0; i < copy.length; i++) {
			if (Float.isNaN(copy[i])) {
				// System.out.println("Ignoring NaN value at " + i);
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
				// System.out.println("Ignoring NaN value at " + d);
			} else if (copy[d] != last) {
				result[r] = copy[d];
				last = copy[d];
				r++;
			}
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public float[][] get_Z_as_fxy() {
		return this.z_as_fxy;
	}

	/**
	 * 
	 * @return
	 */
	public float[] getX() {
		return this.x;
	}

	/**
	 * 
	 * @return
	 */
	public float[] getY() {
		return this.y;
	}

	/**
	 * 
	 * @return
	 */
	public float[] getZ() {
		return this.z;
	}

	// *************************************************************************
	protected float x[];
	protected float y[];
	protected float z[];
	protected float z_as_fxy[][];

	protected int findxi;
	protected int findyj;

}
