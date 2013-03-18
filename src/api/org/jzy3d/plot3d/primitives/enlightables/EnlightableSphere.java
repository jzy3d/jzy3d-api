package org.jzy3d.plot3d.primitives.enlightables;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;



/** 
 * A Sphere allows rendering a sphere.
 * <br>
 * The position and shape of a  Sphere3d is defined through 
 * its {@link setData()} method. Moreover, a Sphere3d
 * is Wireframeable3d and support only one color that is defined
 * trough its {@link setColor()} method.
 * 
 * @author Martin Pernollet
 */
public class EnlightableSphere extends AbstractEnlightable implements ISingleColorable{

	/** Initialize a black sphere at the origin with a radius of 10, and slicing of 15.*/
	public EnlightableSphere(){
		this(Coord3d.ORIGIN, 10f, 15, Color.BLACK);
	}
	
	/** Initialize a sphere with the given parameters.*/
	public EnlightableSphere(Coord3d position, float radius, int slicing, Color color){
		super();
		bbox = new BoundingBox3d();
		setPosition(position);
		setVolume(radius);
		setSlicing(slicing, slicing);
		setColor(color);
	}
		
	/********************************************************/
	
	public void draw(GL2 gl, GLU glu, Camera cam){
	    doTransform(gl, glu, cam);
		
		gl.glTranslatef(x,y,z);
		
		applyMaterial(gl); // TODO: shall we avoid calling this @ each draw?
		
		gl.glLineWidth(wfwidth);
		
		// TODO: one may define lights that are authorized to enlight the object
		//gl.glEnable(GL2.GL_LIGHT0);
		
		// Draw
		GLUquadric qobj = glu.gluNewQuadric();
		if(facestatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glColor4f(color.r, color.g, color.b, color.a);
			glu.gluSphere(qobj, radius, slices, stacks);
			//new GLUT().glutSolidSphere(radius, slices, stacks);
		}
		if(wfstatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
			//new GLUT().glutSolidSphere(radius, slices, stacks);
			glu.gluSphere(qobj, radius, slices, stacks);
		}	
		
		//gl.glDisable(GL2.GL_LIGHT0);
	}
	
	@Override
    public void applyGeometryTransform(Transform transform) {
        Coord3d change = transform.compute(new Coord3d(x,y,z));
        x = change.x;
        y = change.y;
        z = change.z;
        updateBounds();
    }
		
	/**********************************************************************/
	
	/** Set the sphere data.
	 * @param position sphere position (may be handled differently in future version)
	 * @param radius radius of the sphere
	 * @param slices number of vertical slices (i.e. wireframes) 
	 * @param stacks number of horizontal stacks (i.e. wireframes)
	 */
	public void setData(Coord3d position, float radius, float height, int slices, int stacks){
		setPosition(position);
		setVolume(radius);
		setSlicing(slices, stacks);
	}
	
	/** Set the position of the Sphere and the dimensions of its boundingbox.
	 * Note that this position will be use to translate the object before drawing
	 * it (meaning a glTranslate(position) is performed right after the 
	 * Translate.execute).
	 * 
	 * @param position
	 */
	public void setPosition(Coord3d position){
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		
		updateBounds();
	}
	
	@Override
    public void updateBounds(){
        bbox.reset();
        bbox.add(x+radius, y+radius, z+radius);
        bbox.add(x-radius, y-radius, z-radius);
    }
	
	/** Set the radius of the sphere, and the dimensions of its boundingbox.
	 * @param radius sphere radius
	 */
	public void setVolume(float radius){
		this.radius = radius;
		
		bbox.reset();
		bbox.add(x+radius, y+radius, z+radius);
		bbox.add(x-radius, y-radius, z-radius);
	}

	/** Set the sphere slicing parameters, i.e. the subtlety of the circle estimation.
	 * @param verticalWires number of vertical slices
	 * @param horizontalWires number of horizontal slices
	 */
	public void setSlicing(int verticalWires, int horizontalWires){
		this.slices = verticalWires;
		this.stacks = horizontalWires;
	}
	
	/********************************************************/

	public void setColor(Color color){
		this.color = color;
		
		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}
	
	public Color getColor(){
		return color;
	}
	
	/********************************************************/

	public String toString(int depth){
		return Utils.blanks(depth)+"(EnlightableSphere) x="+x+" y="+y+" z="+z+" r="+color.r+" g="+color.g+" b="+color.b+" a="+color.a;
	}
	
	/********************************************************/
	
	private float x;
	private float y;
	private float z;
	private float radius;
	
	private int slices;
	private int stacks;	
	
	private Color color;
}
