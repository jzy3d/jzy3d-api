package org.jzy3d.plot3d.primitives;


import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;

/**
 * An {@link Wireframeable} is a {@link Drawable} 
 * that has a wireframe mode for display.
 * 
 * Defining an object as Wireframeable means this object may have a wireframe
 * mode status (on/off), a wireframe color, and a wireframe width.
 * As a consequence of being wireframeable, a 3d object may have his faces
 * displayed or not by setFaceDisplayed().
 * 
 * @author Martin Pernollet
 */
public abstract class Wireframeable extends Drawable {
	/** Initialize the wireframeable with a white color and 
	 * width of 1 for wires, hidden wireframe, and displayed faces.*/
	public Wireframeable(){
		super();
		setWireframeColor(Color.WHITE);
		setWireframeWidth(1.0f);
		setWireframeDisplayed(true);
		setFaceDisplayed(true);
		setPolygonOffsetFillEnable(true);
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
	
	protected void polygonOffseFillEnable(Painter painter) {
		painter.glEnable_PolygonOffsetFill();
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
	}

	protected void polygonOffsetFillDisable(Painter painter) {
		painter.glDisable_PolygonOffsetFill();
	}

	protected void polygonOffsetLineEnable(Painter painter) {
		painter.glEnable_PolygonOffsetLine();
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
	}

	protected void polygonOffsetLineDisable(Painter painter) {
		painter.glDisable_PolygonOffsetLine();
	}

	public boolean isPolygonOffsetFillEnable() {
		return polygonOffsetFillEnable;
	}

	/**
	 * Enable offset fill, which let a polygon with a wireframe render cleanly
	 * without weird depth incertainty between face and border.
	 * 
	 * Default value is true.
	 */
	public void setPolygonOffsetFillEnable(boolean polygonOffsetFillEnable) {
		this.polygonOffsetFillEnable = polygonOffsetFillEnable;
	}
	
	public float getPolygonOffsetFactor() {
		return polygonOffsetFactor;
	}

	public void setPolygonOffsetFactor(float polygonOffsetFactor) {
		this.polygonOffsetFactor = polygonOffsetFactor;
	}

	public float getPolygonOffsetUnit() {
		return polygonOffsetUnit;
	}

	public void setPolygonOffsetUnit(float polygonOffsetUnit) {
		this.polygonOffsetUnit = polygonOffsetUnit;
	}

	protected Color   wfcolor;
	protected float   wfwidth;
	protected boolean wfstatus;	
	protected boolean facestatus;
	protected boolean polygonOffsetFillEnable = true;
	protected float polygonOffsetFactor = 1.0f;
	protected float polygonOffsetUnit = 1.0f;
}
