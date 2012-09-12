package org.jzy3d.maths;

public class Normal {
	public static Coord3d compute(Coord3d p0, Coord3d p1, Coord3d p2){
		Vector3d v1 = new Vector3d(p0, p1);
		Vector3d v2 = new Vector3d(p1, p2);
		Coord3d norm = v1.cross(v2);
		double d = norm.distance( Coord3d.ORIGIN );
		norm.x /= d;
		norm.y /= d;
		norm.z /= d;
		return norm;
	}
}
