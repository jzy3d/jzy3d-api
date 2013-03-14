package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.view.Camera;



/** 
 * A {@link Quad} extends a {@link Polygon} in order to provide a specific {@link draw()}
 * method that relies on a dedicated GL2 call (GL_QUADS), and to ensure the number of points
 * is never greater than 4.
 *
 * @see {@link Polygon}
 * 
 * @author Martin Pernollet
 */
public class Quad extends Polygon{

	/** Initializes an empty {@link Quad} with face status defaulting to true,
	 * and wireframe status defaulting to false.*/
	public Quad(){
		super();
	}
	
	public void draw(GL2 gl, GLU glu, Camera cam){
		// Execute transformation
	    doTransform(gl, glu, cam);
				
		// Draw content of polygon
		if(facestatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			if(wfstatus){
				gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1.0f, 1.0f);
			}
			gl.glBegin(GL2.GL_QUADS); // <<<
			for(Point p: points){
				if(mapper!=null){
					Color c = mapper.getColor(p.xyz); // TODO: should store result in the point color
					gl.glColor4f(c.r, c.g, c.b, c.a);
					//System.out.println(c);
				}
				else
					gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
			if(wfstatus)
				gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		}
		
		// Draw edge of polygon
		if(wfstatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1.0f, 1.0f);
			
			gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);//wfcolor.a);
			gl.glLineWidth(wfwidth);

			gl.glBegin(GL2.GL_QUADS);
			for(Point p: points){
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
			
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		}
		
		/*Point b = new Point(getBarycentre(), Color.BLUE);
		b.setWidth(5);
		b.draw(gl,glu,cam);*/
		
		doDrawBounds(gl, glu, cam);
	}
	
	/** Add a point to the polygon.*/
	public void add(Point point){
		if(points.size()==4)
			throw new RuntimeException("The Quad allready has 4 points registered");
		
		super.add(point);
	}
	
	public double getDistance(Camera camera){
		return getBarycentre().distance(camera.getEye());
	}
	
	public double getShortestDistance(Camera camera){
		double min = Float.MAX_VALUE;
		double dist = 0;
		for(Point point: points){
			dist = point.getDistance(camera);
			if(dist < min)
				min = dist;
		}
		
		dist = getBarycentre().distance(camera.getEye());
		if(dist < min)
			min = dist;
		return min;
	}
	
	public double getLongestDistance(Camera camera){
		double max = 0;
		double dist = 0;
		for(Point point: points){
			dist = point.getDistance(camera);
			if(dist > max)
				max = dist;
		}
		return max;
	}
	
	public String toString(int depth){
		return Utils.blanks(depth)+"(Quad) #points:" + points.size();
	}
}
