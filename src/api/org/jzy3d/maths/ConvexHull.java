package org.jzy3d.maths;

import java.awt.geom.Point2D;
import java.util.Deque;
import java.util.List;

import org.jzy3d.convexhull.ConvexHullFunction;
import org.jzy3d.convexhull.GrahamScan;

public class ConvexHull {
	public static java.awt.Polygon hull(List<Coord3d> cell){
		java.awt.Polygon out = new java.awt.Polygon();
		
		Deque<Point2D> hull = ConvexHull.build2d(cell);
		while (!hull.isEmpty()) {
		    Point2D p = hull.pop();
        	out.addPoint( (int)p.getX(), (int)p.getY());
        }
		return out;
	}
	
	public static Deque<Point2D> build2d(List<Coord3d> input2d){
		int np = input2d.size();		
		Point2D[] data = new Point2D[ np ];		
		for (int i = 0; i < data.length; i++) {
			data[i] = asPoint2f( input2d.get(i) );
		}
		return f.getConvexHull( data );
	}
	
	public static Deque<Point2D> build2d(PolygonArray input2d){
		int np = input2d.length();		
		Point2D[] data = new Point2D[ np ];
		for (int i = 0; i < np; i++) {
			data[i] = new Point2D.Float(input2d.x[i], input2d.y[i]);
		}
		return f.getConvexHull( data );
	}
	
	protected static Point2D asPoint2f(Coord3d c){
		return new Point2D.Float(c.x, c.y);
	}
	
	protected static ConvexHullFunction f = new GrahamScan();//new JarvisMarch();
}
