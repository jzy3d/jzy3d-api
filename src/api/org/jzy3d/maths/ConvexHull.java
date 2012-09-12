package org.jzy3d.maths;

import java.util.List;

import utils.Stack;
import algorithms.Point2f;
import convexhull.ConvexHullFunction;
import convexhull.GrahamScan;

public class ConvexHull {
	public static java.awt.Polygon hull(List<Coord3d> cell){
		java.awt.Polygon out = new java.awt.Polygon();
		
		Stack<Point2f> hull = ConvexHull.build2d(cell);
		while (!hull.empty()) {
        	Point2f p = hull.pop();
        	out.addPoint( (int)p.getX(), (int)p.getY());
        }
		return out;
	}
	
	public static Stack<Point2f> build2d(List<Coord3d> input2d){
		int np = input2d.size();		
		Point2f[] data = new Point2f[ np ];		
		for (int i = 0; i < data.length; i++) {
			data[i] = asPoint2f( input2d.get(i) );
		}
		return f.getConvexHull( data );
	}
	
	public static Stack<Point2f> build2d(PolygonArray input2d){
		int np = input2d.length();		
		Point2f[] data = new Point2f[ np ];
		for (int i = 0; i < np; i++) {
			data[i] = new Point2f(input2d.x[i], input2d.y[i]);
		}
		return f.getConvexHull( data );
	}
	
	protected static Point2f asPoint2f(Coord3d c){
		return new Point2f(c.x, c.y);
	}
	
	protected static ConvexHullFunction f = new GrahamScan();//new JarvisMarch();
}
