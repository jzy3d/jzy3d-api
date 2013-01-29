package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;


/** 
 * A Composite gathers several Drawable and provides default methods
 * for rendering them all in one call.
 * <br>
 *
 * @author Martin Pernollet
 *
 */
public abstract class AbstractComposite extends AbstractWireframeable implements ISingleColorable, IMultiColorable{
	public AbstractComposite(){	
		super();
		components = new ArrayList<AbstractDrawable>();
	}

	/****************************************************************/
	
	/** Append a list of Drawables to this composite.*/
	public void add(List<? extends AbstractDrawable> drawables){
		components.addAll(drawables);
	}
	
	/** Clear the list of Drawables from this composite.*/
	public void clear(){
		components.clear();
	}
	
	/** Add a Drawable to this composite.*/
	public void add(AbstractDrawable drawable){
		components.add(drawable);
	}
	
	/** Remove a Drawable from this composite.*/
	public void remove(AbstractDrawable drawable){
		components.remove(drawable);
	}
	
	/** Get a Drawable stored by this composite.*/
	public AbstractDrawable get(int p){
		return components.get(p);
	}
	
	public List<AbstractDrawable> getDrawables(){
		return components;
	}
	
	/** Return the number of Drawable stored by this composite.*/
	public int size(){
		return components.size();
	}
	
	/****************************************************************/

	/** Delegate rendering iteratively to all Drawable of this composite.*/
	public void draw(GL2 gl, GLU glu, Camera camera){
	    if(mapper!=null)
	        mapper.preDraw(this);

		synchronized(components){
			for(AbstractDrawable c: components){
				if(c!=null)
					c.draw(gl, glu, camera);
			}			
		}
		
		if(mapper!=null)
            mapper.postDraw(this);
		
		doDrawBounds(gl, glu, camera);
	}
		
	/** Delegate transforming iteratively to all Drawable of this composite 
	 * and stores the given transform for keeping the ability of retrieving it.*/
	public void setTransform(Transform transform){
		this.transform = transform;
		
		synchronized(components){
			for(AbstractDrawable c: components){
				if(c!=null)
					c.setTransform(transform);
			}
		}
	}

	/** Return the transform that was affected to this composite.*/
	public Transform getTransform(){
		return transform;
	}	
	
	/** Creates and return a BoundingBox3d that embed all available Drawable bounds.*/
	public BoundingBox3d getBounds(){
	    updateBounds();
		return bbox;
	}

    public void updateBounds() {
        BoundingBox3d box = new BoundingBox3d();
		
		for(AbstractDrawable c: components){
			if(c!=null && c.getBounds()!=null)
				box.add(c.getBounds());
		}
		bbox = box;
    }
	
    public void applyGeometryTransform(Transform transform){
        for(AbstractDrawable c: components){
            c.applyGeometryTransform(transform);
        }
        // updateBounds(); no need, as computed by getBounds()
    }

	
	/****************************************************************/
	
	public void setWireframeColor(Color color){
		super.setWireframeColor(color);
		
		if(components != null)
			for(AbstractDrawable c: components){
				if(c!=null && c instanceof AbstractWireframeable)
					((AbstractWireframeable)c).setWireframeColor(color);
			}
	}
	
	public void setWireframeDisplayed(boolean status){
		super.setWireframeDisplayed(status);
		
		if(components != null)
			for(AbstractDrawable c: components){
				if(c!=null && c instanceof AbstractWireframeable)
					((AbstractWireframeable)c).setWireframeDisplayed(status);
			}
	}
	
	public void setWireframeWidth(float width){
		super.setWireframeWidth(width);
		
		if(components != null)
			for(AbstractDrawable c: components){
				if(c!=null && c instanceof AbstractWireframeable)
					((AbstractWireframeable)c).setWireframeWidth(width);
			}
	}

	public void setFaceDisplayed(boolean status){
		super.setFaceDisplayed(status);
		
		if(components != null)
			for(AbstractDrawable c: components){
				if(c!=null && c instanceof AbstractWireframeable)
					((AbstractWireframeable)c).setFaceDisplayed(status);
			}
	}
	
	public void setDisplayed(boolean status){
		super.setDisplayed(status);
		
		if(components != null)
			for(AbstractDrawable c: components){
				if(c!=null && c instanceof AbstractWireframeable)
					((AbstractWireframeable)c).setDisplayed(status);
			}
	}
	
	/****************************************************************/

	public void setColorMapper(ColorMapper mapper){
		this.mapper = mapper;
		
		if(components != null){
			for(AbstractDrawable d: components){
				if(d instanceof IMultiColorable)
					((IMultiColorable)d).setColorMapper(mapper);
				else if(d instanceof ISingleColorable)
					((ISingleColorable)d).setColor(mapper.getColor(d.getBarycentre()));
			}
			
			fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
		}
		
	}
	
	public ColorMapper getColorMapper(){
		return mapper;
	}
	
	public void setColor(Color color){
		this.color = color;
		
		if(components != null){
			for(AbstractDrawable d: components)
				if(d instanceof ISingleColorable)
					((ISingleColorable)d).setColor(color);
			
			fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
		}		
	}
	
	public Color getColor(){
		return color;
	}
	
	/****************************************************************/
	
	/** Print out information concerning all Drawable of this composite.*/
	public String toString(){
		return toString(0);
	}
	
	public String toString(int depth){
		String output = Utils.blanks(depth) + "(Composite3d) #elements:"+ components.size()+ " | isDisplayed=" + isDisplayed();
	
		if(detailedToString){
			int k = 0;
			for(AbstractDrawable c: components){
				if(c!=null){
					if(c instanceof AbstractComposite)
						output += "\n" + ((AbstractComposite)c).toString(depth+1);
					else
						output += "\n" + Utils.blanks(depth+1) + " Composite element["+ (k++) +"]:" + c.toString();
				}
				else
					output += Utils.blanks(depth+1) + "(null)\n";
			}
		}
		
		return output;
	}
	
	public boolean isDetailedToString() {
		return detailedToString;
	}

	/** When to true, the {@link toString()} method will give the detail of each element 
	 * of this composite object in a tree like layout.
	 */
	public void setDetailedToString(boolean detailedToString) {
		this.detailedToString = detailedToString;
	}



	/****************************************************************/
	
	protected List<AbstractDrawable> components = Collections.synchronizedList(new ArrayList<AbstractDrawable>());
	protected Transform transform;
	
	protected ColorMapper mapper;
	protected Color color;
	protected boolean detailedToString = false;
}
