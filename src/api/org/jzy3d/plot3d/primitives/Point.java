package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;



/** A Point3d is a storage for a Coord3d and a Color that represents
 * a drawable 3d point.
 * <br>
 * The Point3d is used for:
 * <ul>
 * <li>adding a Point3d to a {@link Graph}.
 * <li>providing to other primitives (e.g. {@link Polygon}) 
 * a way to associate a coordinate and a color.
 * </ul>
 * <br>
 * A Point3d is defined by the following methods:
 * <ul>
 * <li>setData() defines the point's position
 * <li>setColor() defines the point's color
 * <li>setWidth() defines the point's width
 * </ul>
 * 
 * @author Martin Pernollet
 *
 */
public class Point extends AbstractDrawable implements ISingleColorable{
	
	/** Intialize a point at the origin, with a white color and a width of 1.*/
	public Point(){
		this(Coord3d.ORIGIN, Color.WHITE, 1.0f);
	}
	
	/** Intialize a point with a white color and a width of 1.*/
	public Point(Coord3d xyz){
		this(xyz, Color.WHITE, 1.0f);
	}
	
	/** Intialize a point with a width of 1.*/
	public Point(Coord3d xyz, Color rgb){
		this(xyz, rgb, 1.0f);
	}
	
	public Point(Coord3d xyz, Color rgb, float width){
		bbox = new BoundingBox3d();
		setData(xyz);
		setWidth(width);
		setColor(rgb);
	}
	
	/* */
	
	public void draw(GL2 gl, GLU glu, Camera cam){
	    doTransform(gl, glu, cam);
		
		gl.glPointSize(width);
		
		gl.glBegin(GL2.GL_POINTS);
		gl.glColor4f(rgb.r, rgb.g, rgb.b, rgb.a);
		gl.glVertex3f(xyz.x, xyz.y, xyz.z);
		gl.glEnd();
	}
	
	public void applyGeometryTransform(Transform transform){
        xyz = transform.compute(xyz);
    }

	/* */
	
	/** 
	 * Set the coordinates of the point.
	 * @param xyz point's coordinates
	 */
	public void setData(Coord3d xyz){
		this.xyz = xyz;
		updateBounds();
	}

    public void updateBounds() {
        bbox.reset();
		bbox.add(this);
    }
		
	public void setColor(Color color){
		this.rgb = color;
		
		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}
	
	public Color getColor(){
		return rgb;
	}
	
	public void setWidth(float width){
		this.width = width;
	}
	
	public double getDistance(Camera camera){
		return xyz.distance(camera.getEye());
	}
	
	public double getShortestDistance(Camera camera){
		return xyz.distance(camera.getEye());
	}
	
	public double getLongestDistance(Camera camera){
		return xyz.distance(camera.getEye());
	}
	
	public Point clone(){
	    Point p = new Point(xyz.clone(), rgb.clone());
	    p.setWidth(width);
	    return p;
	}
	public String toString(int depth){
		return (Utils.blanks(depth) + "(Point) coord=" + xyz + ", color=" + rgb);
	}
	
	
	/* */
	
	public Coord3d getCoord() {
        return xyz;
    }

    public void setCoord(Coord3d xyz) {
        this.xyz = xyz;
    }


    public Coord3d xyz;
	public Color   rgb;
	public float   width;
}


