package org.jzy3d.plot3d.primitives;


import org.jzy3d.colors.Color;

/**
 * An {@link AbstractWireframeable} is a {@link AbstractDrawable} 
 * that has a wireframe mode for display.
 * 
 * Defining an object as Wireframeable means this object may have a wireframe
 * mode status (on/off), a wireframe color, and a wireframe width.
 * As a consequence of being wireframeable, a 3d object may have his faces
 * displayed or not by setFaceDisplayed().
 * 
 * @author Martin Pernollet
 */
public abstract class AbstractWireframeable extends AbstractDrawable {
	/** Initialize the wireframeable with a white color and 
	 * width of 1 for wires, hidden wireframe, and displayed faces.*/
	public AbstractWireframeable(){
		super();
		setWireframeColor(Color.WHITE);
		setWireframeWidth(1.0f);
		setWireframeDisplayed(true);
		setFaceDisplayed(true);
	}
	
	/**Set the wireframe color.*/
	public void setWireframeColor(Color color){
		wfcolor = color;
	}
	
	/**Set the wireframe display status to on or off.*/
	public void setWireframeDisplayed(boolean status){
		wfstatus = status;
	}
	
	/**Set the wireframe width.*/
	public void setWireframeWidth(float width){
		wfwidth = width;
	}

	/**Set the face display status to on or off.*/
	public void setFaceDisplayed(boolean status){
		facestatus = status;
	}

	/**Get the wireframe color.*/
	public Color getWireframeColor(){
		return wfcolor;
	}
	
	/**Get the wireframe display status to on or off.*/
	public boolean getWireframeDisplayed(){
		return wfstatus;
	}
	
	/**Get the wireframe width.*/
	public float getWireframeWidth(){
		return wfwidth;
	}

	/**Get the face display status to on or off.*/
	public boolean getFaceDisplayed(){
		return facestatus;
	}
	
	protected Color   wfcolor;
	protected float   wfwidth;
	protected boolean wfstatus;	
	protected boolean facestatus;
}
