package org.jzy3d.plot3d.primitives;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;



/** 
 * A {@link Tube} may be used to render cylinders or pyramids, according
 * to its input parameters.
 * <br>
 * The position and shape of a {@link Tube} is defined through 
 * its {@link setData()} method. Moreover, a {@link Tube}
 * is Wireframeable3d and support only one color that is defined
 * trough its {@link setColor()} method.
 * 
 * @author Martin Pernollet
 */
public class Tube extends AbstractWireframeable implements ISingleColorable{

	/** Initialize a Cylinder at the origin.*/
	public Tube(){
		super();
		bbox = new BoundingBox3d();
		setPosition(Coord3d.ORIGIN);
		setVolume(10f, 10f, 30f);
		setSlicing(15, 15);
		setColor(Color.BLACK);
	}
	
	/** Initialize a cylinder with the given parameters.*/
	public Tube(Coord3d position, float radius, float height, int hslicing, int vslicing, Color color){
		super();
		bbox = new BoundingBox3d();
		setPosition(position);
		setVolume(radius, radius, height);
		setSlicing(hslicing, vslicing);
		setColor(color);	
	}
		
	/* */
	
	public void draw(GL2 gl, GLU glu, Camera cam){
	    doTransform(gl, glu, cam);
	    
		gl.glTranslatef(x,y,z);
		
		gl.glLineWidth(wfwidth);
		
		// Draw
		GLUquadric qobj = glu.gluNewQuadric();
		
		if(facestatus){
			if(wfstatus){
				gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1.0f, 1.0f);
			}

			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glColor4f(color.r, color.g, color.b, color.a);
			glu.gluCylinder(qobj, radiusBottom, radiusTop, height, slices, stacks);
			
			if(wfstatus)
				gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

		}
		if(wfstatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
			glu.gluCylinder(qobj, radiusBottom, radiusTop, height, slices, stacks);
		}	
		
		doDrawBounds(gl, glu, cam);
	}
	
	public void applyGeometryTransform(Transform transform){
        Coord3d c = transform.compute(new Coord3d(x,y, z));
        x = c.x;
        y = c.y;
        z = c.z;
        updateBounds();
    }
	
	@Override
    public void updateBounds() {
        bbox.reset();
        bbox.add(x+Math.max(radiusBottom, radiusTop), y+Math.max(radiusBottom, radiusTop), z);
        bbox.add(x-Math.max(radiusBottom, radiusTop), y-Math.max(radiusBottom, radiusTop), z);
        bbox.add(x+Math.max(radiusBottom, radiusTop), y+Math.max(radiusBottom, radiusTop), z+height);
        bbox.add(x-Math.max(radiusBottom, radiusTop), y-Math.max(radiusBottom, radiusTop), z+height);
    }
	
	/* */
	
	/** Set the {@link Tube} data.
	 * @param position cylinder position (may be handled diffrently in future version)
	 * @param radiusBottom radius of the bottom circle 
	 * @param radiusTop radius of the top circle
	 * @param height height of the cylinder
	 * @param slices number of vertical slices (i.e. wireframes) 
	 * @param stacks number of horizontal stacks (i.e. wireframes)
	 */
	public void setData(Coord3d position, float radiusBottom, float radiusTop, float height, int slices, int stacks){
		setPosition(position);
		setVolume(radiusBottom, radiusTop, height);
		setSlicing(slices, stacks);
	}
	
	/** Set the position of the Cylinder and the dimensions of its boundingbox.
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
		
		bbox.reset();
		bbox.add(x+Math.max(radiusBottom, radiusTop), y+Math.max(radiusBottom, radiusTop), z+height);
		bbox.add(x-Math.max(radiusBottom, radiusTop), y-Math.max(radiusBottom, radiusTop), z-height);
	}
	
	/** Set the top and bottom radius of the cylinder, its height, and the dimensions of its boundingbox.
	 * @param radiusBottom
	 * @param radiusTop
	 * @param height
	 */
	public void setVolume(float radiusBottom, float radiusTop, float height){
		this.radiusBottom = radiusBottom;
		this.radiusTop    = radiusTop;
		this.height       = height;
		updateBounds();
	}
	
	/** Set the cylinder slicing parameters, i.e. the subtlety of the circle estimation.
	 * @param verticalWires number of vertical slices
	 * @param horizontalWires number of horizontal slices
	 */
	public void setSlicing(int verticalWires, int horizontalWires){
		this.slices = verticalWires;
		this.stacks = horizontalWires;
	}
	
	/* */

	public void setColor(Color color){
		this.color = color;
		
		fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
	}
	
	public Color getColor(){
		return color;
	}
	
	/* */
	
	private float x;
	private float y;
	private float z;
	private float radiusBottom;
	private float radiusTop;
	private float height;
	
	private int slices;
	private int stacks;	
	
	private Color color;

    
}
