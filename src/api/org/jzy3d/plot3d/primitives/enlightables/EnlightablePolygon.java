package org.jzy3d.plot3d.primitives.enlightables;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;


public class EnlightablePolygon extends AbstractEnlightable{

	/** Initializes an empty {@link Polygon} with face status defaulting to true,
	 * and wireframe status defaulting to false.*/
	public EnlightablePolygon(){
		super();
		points = new ArrayList<Point>(4); // use Vector for synchro, or ArrayList for unsyncro.
		bbox   = new BoundingBox3d();	
		center = new Coord3d();
	}
	
	/**********************************************************************/
		
	public void draw(GL2 gl, GLU glu, Camera cam){
	    doTransform(gl, glu, cam);
		
		applyMaterial(gl); // TODO: shall we avoid calling this @ each draw?
		Coord3d norm = Normal.compute(points.get(0).xyz, points.get(1).xyz, points.get(2).xyz);
				
		// Draw content of polygon
		if(facestatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			if(wfstatus){
				gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1.0f, 1.0f);
			}
			
			gl.glBegin(GL2.GL_POLYGON);
			for(Point p: points){
				if(mapper!=null){
					Color c = mapper.getColor(p.xyz); // TODO: should store result in the point color
					gl.glColor4f(c.r, c.g, c.b, c.a);
				}
				else
					gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				gl.glNormal3f(norm.x, norm.y, norm.z);
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
			
			gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, 1);//wfcolor.a);
			gl.glLineWidth(wfwidth);

			gl.glBegin(GL2.GL_POLYGON);
			for(Point p: points){
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
				gl.glNormal3f(norm.x, norm.y, norm.z);
			}
			gl.glEnd();
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		}
		
		/*// Drawbarycenter
			Point b = new Point(getBarycentre(), Color.BLUE);
			b.setWidth(5);
			b.draw(gl,glu,cam);
		*/
			
	}
	
	public void applyGeometryTransform(Transform transform){
        for(Point p: points){
            p.xyz = transform.compute(p.xyz);
        }
        updateBounds();
    }

    
    public void updateBounds(){
        bbox.reset();
        bbox.add(points);
        // recompute center
        updateCenter();
    }

    protected void updateCenter() {
        center = new Coord3d();
        for (Point p : points)
            center = center.add(p.xyz);
        center = center.div(points.size());
    }
	
	/**********************************************************************/
	
	/** Add a point to the polygon.*/
	public void add(Point point){
		if(point.rgb.a < 1f)
			hasAlpha = true;
		points.add(point);
		bbox.add(point);
		
		updateCenter();
	}
	
	//--- experimental code ------
	public boolean hasAlpha(){
		return hasAlpha;
	}
	private boolean hasAlpha = false;
	//----------------------------
	
	@Override
	public Coord3d getBarycentre(){
		return center;
	}
	
	/** Retrieve a point from the {@link Polygon}.
	 * @return a Point3d. 
	 */
	public Point get(int p){
		return points.get(p);
	}
	
	/** Indicates the number of points in this {@link Polygon}.
	 * @return the number of points
	 */	
	public int size(){
		return points.size();
	}
	
	
	
	/**********************************************************************/

	public void setColorMapper(ColorMapper mapper){
		this.mapper = mapper;
		
		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}
	
	public ColorMapper getColorMapper(){
		return mapper;
	}
	
	/*public void setColors(ColorMapper mapper){
		for(Point p: points)
			p.setColor(mapper.getColor(p.xyz));	
	}*/
	
	public void setColor(Color color){
		this.color = color;
		
		for(Point p: points)
			p.setColor(color);
		
		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}
	
	public Color getColor(){
		return color;
	}
	
	/**********************************************************************/
	
	public String toString(int depth){
		return (Utils.blanks(depth) + "(EnlightablePolygon) #points:" + points.size());
	}
	
	/**********************************************************************/
	protected ColorMapper mapper;
	protected List<Point> points;
	protected Color color;
	protected Coord3d center;
}
