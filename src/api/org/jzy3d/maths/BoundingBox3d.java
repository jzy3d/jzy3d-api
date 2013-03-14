package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;


/** A BoundingBox3d stores a couple of maximal and minimal limit on
 * each dimension (x, y, and z). It provides functions for enlarging
 * the box by adding coordinates, Point3d, Polygon3d or an other 
 * BoundingBox3d (that is equivalent to computing the union of the 
 * current BoundingBox and another one).
 *
 * @author Martin Pernollet
 */
public class BoundingBox3d {
	/** Initialize a BoundingBox by calling its reset method, leaving the box in an inconsitent state,
	 * with minimums = Float.MAX_VALUE and maximums = -Float.MAX_VALUE. In other words, calling box.valid()
	 * will return false. */
	public BoundingBox3d(){
		reset();
	}
	
	public BoundingBox3d(List<Coord3d> coords){
		reset();
		for(Coord3d c: coords)
			add( c );
	}
	
	public BoundingBox3d(Polygon polygon){
		reset();
		for(Point c: polygon.getPoints())
			add( c );
	}
		
	public BoundingBox3d(Coord3d center, float edgeLength){
		this.xmin = center.x - edgeLength / 2;
		this.xmax = center.x + edgeLength / 2;
		this.ymin = center.y - edgeLength / 2;
		this.ymax = center.y + edgeLength / 2;
		this.zmin = center.z - edgeLength / 2;
		this.zmax = center.z + edgeLength / 2;
	}
	
	public BoundingBox3d(BoundingBox3d box){
		this.xmin = box.xmin;
		this.xmax = box.xmax;
		this.ymin = box.ymin;
		this.ymax = box.ymax;
		this.zmin = box.zmin;
		this.zmax = box.zmax;
	}
	
	/** Initialize a BoundingBox with raw values.*/
	public BoundingBox3d(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax){
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.zmin = zmin;
		this.zmax = zmax;
	}
	
	/*********************************************************/
	
	/**
	 * Initialize the bounding box with Float.MAX_VALUE as minimum
	 * value, and -Float.MAX_VALUE as maximum value for each dimension.
	 */
	public void reset(){
		xmin =  Float.MAX_VALUE;
		xmax = -Float.MAX_VALUE;
		ymin =  Float.MAX_VALUE;
		ymax = -Float.MAX_VALUE;
		zmin =  Float.MAX_VALUE;
		zmax = -Float.MAX_VALUE;
	}
	
	public boolean valid(){
		return (xmin<=xmax && ymin<=ymax && zmin<=zmax);
	}
		
	/**
	 * Adds an x,y,z point to the bounding box, and enlarge the bounding
	 * box if this points lies outside of it.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void add(float x, float y, float z){
		if(x>xmax) xmax = x;
		if(x<xmin) xmin = x;
		if(y>ymax) ymax = y;
		if(y<ymin) ymin = y;
		if(z>zmax) zmax = z;
		if(z<zmin) zmin = z;
	}
	
	/**
	 * Add a Coord3d to the BoundingBox3d.
	 * A shortcut for: 
	 * <code>add(c.x, c.y, c.z);</code>
	 * @param p
	 */
	public void add(Coord3d c){
		add(c.x, c.y, c.z);
	}
	
	public void add(List<Point> pts){
	    for(Point p: pts)
	        add(p.xyz.x, p.xyz.y, p.xyz.z);
    }
	
	/**
	 * Add a Point3d to the BoundingBox3d.
	 * A shortcut for: 
	 * <code>add(p.x, p.y, p.z);</code>
	 * @param p
	 */
	public void add(Point p){
		add(p.xyz.x, p.xyz.y, p.xyz.z);
	}
	
	/**
	 * Add the points of a Polygon to the BoundingBox3d.
	 * @param p
	 */
	public void add(Polygon p){
		for(int i=0; i<p.size(); i++)
			add(p.get(i).xyz.x, p.get(i).xyz.y, p.get(i).xyz.z);
	}

	/**
	 * Add a BoundingBox3d volume to the current one.
	 * A convenient shortcut for:
	 * <code>
	 * add(b.xmin, b.ymin, b.zmin);
	 * add(b.xmax, b.ymax, b.zmax);
	 * </code>
	 * @param p
	 */
	public void add(BoundingBox3d b){
		add(b.xmin, b.ymin, b.zmin);
		add(b.xmax, b.ymax, b.zmax);
	}

	/*********************************************************/

	/** Compute and return the center point of the BoundingBox3d
	 * @return the center. 
	 */
	public Coord3d getCenter(){
		return new Coord3d((xmin+xmax)/2, (ymin+ymax)/2, (zmin+zmax)/2);
	}
	
	/** Return the radius of the Sphere containing the Bounding Box, 
	 * i.e., the distance between the center and the point (xmin, ymin, zmin).
	 * @return the box radius.
	 */
	public double getRadius(){
		return getCenter().distance(new Coord3d(xmin, ymin, zmin));
	}
	
	/** Return a copy of the current bounding box after scaling.
	 * Scaling does not modify the current bounding box.
	 * @return the scaled bounding box
	 */
	public BoundingBox3d scale(Coord3d scale){
		BoundingBox3d b = new BoundingBox3d();
		b.xmax = xmax*scale.x;
		b.xmin = xmin*scale.x;
		b.ymax = ymax*scale.y;
		b.ymin = ymin*scale.y;
		b.zmax = zmax*scale.z;
		b.zmin = zmin*scale.z;
		return b;
	}
	
	public BoundingBox3d shift(Coord3d offset){
		BoundingBox3d b = new BoundingBox3d();
		b.xmax = xmax+offset.x;
		b.xmin = xmin+offset.x;
		b.ymax = ymax+offset.y;
		b.ymin = ymin+offset.y;
		b.zmax = zmax+offset.z;
		b.zmin = zmin+offset.z;
		return b;
	}
	
	/** Return true if b2 is contained by this.*/
	public boolean contains(BoundingBox3d b2){
		return xmin <= b2.xmin && b2.xmax <= xmax
		    && ymin <= b2.ymin && b2.ymax <= ymax
		    && zmin <= b2.zmin && b2.zmax <= zmax;
	}
	
	/** Return true if intersect b2.*/
	public boolean intersect(BoundingBox3d b2){
		return xmin <= b2.xmin && b2.xmin <= xmax
		    || xmin <= b2.xmax && b2.xmax <= xmax
		    || ymin <= b2.ymin && b2.ymin <= ymax
		    || ymin <= b2.ymax && b2.ymax <= ymax
		    || zmin <= b2.zmin && b2.zmin <= zmax
		    || zmin <= b2.zmax && b2.zmax <= zmax;
	}
	
	/*********************************************************/

	public BoundingBox3d margin(float margin){
		BoundingBox3d b = new BoundingBox3d();
		b.xmax = xmax + margin;
		b.xmin = xmin + margin;
		b.ymax = ymax + margin;
		b.ymin = ymin + margin;
		b.zmax = zmax + margin;
		b.zmin = zmin + margin;
		return b;
	}
	
	public BoundingBox3d selfMargin(float margin){
		xmax += margin;
		xmin -= margin;
		ymax += margin;
		ymin -= margin;
		zmax += margin;
		zmin -= margin;
		return this;
	}
	
	/*********************************************************/
	
	public Range getXRange(){
		return new Range(xmin, xmax);
	}
	
	public Range getYRange(){
		return new Range(ymin, ymax);
	}
	
	public Range getZRange(){
		return new Range(zmin, zmax);
	}
	
	public float getXmin(){
		return xmin;
	}
	
	public void setXmin(float value){
		xmin = value;
	}
		
	public float getXmax(){
		return xmax;
	}
	
	public void setXmax(float value){
		xmax = value;
	}
	
	public float getYmin(){
		return ymin;
	}
	
	public void setYmin(float value){
		ymin = value;
	}
	
	public float getYmax(){
		return ymax;
	}
	
	public void setYmax(float value){
		ymax = value;
	}
	
	public float getZmin(){
		return zmin;
	}
	
	public void setZmin(float value){
		zmin = value;
	}
	
	public float getZmax(){
		return zmax;
	}
	
	public void setZmax(float value){
		zmax = value;
	}
	
	public List<Coord3d> getVertices(){
		List<Coord3d> edges = new ArrayList<Coord3d>(8);		
		edges.add( new Coord3d(xmin, ymin, zmin) );
		edges.add( new Coord3d(xmin, ymax, zmin) );
		edges.add( new Coord3d(xmax, ymax, zmin) );
		edges.add( new Coord3d(xmax, ymin, zmin) );
		edges.add( new Coord3d(xmin, ymin, zmax) );
		edges.add( new Coord3d(xmin, ymax, zmax) );
		edges.add( new Coord3d(xmax, ymax, zmax) );
		edges.add( new Coord3d(xmax, ymin, zmax) );
		return edges;
	}
	
	/*********************************************************/

	public static BoundingBox3d newBoundsAtOrigin(){
		return new BoundingBox3d(Coord3d.ORIGIN, 0);
	}
	
	/*********************************************************/
	
	public String toString(){
		return toString(0);
	}
	
	public String toString(int depth){
		return Utils.blanks(depth) + "(BoundingBox3d)" + xmin + "<=x<=" + xmax + " | " + ymin + "<=y<=" + ymax + " | " + zmin + "<=z<=" + zmax;
	}
	
	
	/*********************************************************/
	
	private float xmin;
	private float xmax;
	private float ymin;
	private float ymax;
	private float zmin;
	private float zmax;
}
