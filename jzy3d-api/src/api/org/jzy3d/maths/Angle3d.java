package org.jzy3d.maths;

/**
 * An Angle3d stores three 3d points, considering the angle is on the second one.
 * An instance may return angle(), cos() and sin().
 */
public class Angle3d {
	
	/** Create an angle, described by three points. 
	 * The angle is supposed to be on p2*/
	public Angle3d(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3){
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.y1 = y1;
		this.y2 = y2;
		this.y3 = y3;
		this.z1 = z1;
		this.z2 = z2;
		this.z3 = z3;
	}
	
	/** Create an angle, described by three coordinates. 
	 * The angle is supposed to be on p2*/
	public Angle3d(Coord3d p1, Coord3d p2, Coord3d p3){
		x1 = p1.x;
		x2 = p2.x;
		x3 = p3.x;
		y1 = p1.y;
		y2 = p2.y;
		y3 = p3.y;
		z1 = p1.z;
		z2 = p2.z;
		z3 = p3.z;
	}
	
	/***********************************************************/

	/** Computes the sinus of the angle, by creating a fourth point
	 * on an orthogonal direction.*/
	public float sin(){
		/*Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2);
		Vector3d v3 = new Vector3d(x3, y3, z3, x2, y2, z2);
		Coord3d  c4 = v1.cross(v3);
		Vector3d v4 = new Vector3d(c4, new Coord3d(x2, y2, z2));*///new Vector3d(x4, y4, z4, x2, y2, z2);

		Coord3d  c2 = new Coord3d(x2, y2, z2);
		Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2);
		Vector3d v3 = new Vector3d(x3, y3, z3, x2, y2, z2);
		Coord3d  c4 = v1.cross(v3).add(c2);
		Vector3d v4 = new Vector3d(c4, c2);	
		
		return ((c4.z>=0)?1:-1) * v4.norm() / (v1.norm()*v3.norm());
	}

	/** Computes cosinus of the angle*/
	public float cos(){
		Vector3d v1 = new Vector3d(x1, y1, z1, x2, y2, z2);
		Vector3d v3 = new Vector3d(x3, y3, z3, x2, y2, z2);
		return v1.dot(v3) / (v1.norm()*v3.norm());
	}
	
	/** Computes the angle at vertex p2 between rays p1,p2 and p3,p2. Returns 0 to PI radians.*/
	public float angle(){
		double lenP1P3 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2) + Math.pow(z1 - z3, 2));
		double lenP1P2 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
		double lenP3P2 = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2) + Math.pow(z3 - z2, 2));
		double numerator = Math.pow(lenP1P2, 2) + Math.pow(lenP3P2, 2) - Math.pow(lenP1P3, 2);
		double denominator = 2 * lenP1P2 * lenP3P2;
		return (float)Math.acos(numerator / denominator);
	}
	
	/***********************************************************/
	
	private float x1;
	private float x2;
	private float x3;
	private float y1;
	private float y2;
	private float y3;
	private float z1;
	private float z2;
	private float z3;
}
