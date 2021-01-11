package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;

/**
 * An {@link Wireframeable} is a {@link Drawable} that has a wireframe mode for
 * display.
 * 
 * Defining an object as {@link Wireframeable} means this object may have a wireframe
 * mode status (on/off), a wireframe color, and a wireframe width. As a
 * consequence of being {@link Wireframeable}, a 3d object may have his faces displayed
 * or not by {@link Wireframeable#setFaceDisplayed(boolean)}.
 * 
 * @author Martin Pernollet
 */
public abstract class Wireframeable extends Drawable {
	/**
	 * Initialize the wireframeable with a white color and width of 1 for wires,
	 * hidden wireframe, and displayed faces.
	 */
	public Wireframeable() {
		super();
		setWireframeColor(Color.WHITE);
		setWireframeWidth(1.0f);
		setWireframeDisplayed(true);
		setFaceDisplayed(true);
		
		// Apply most supported way of rendering
		// polygon edges cleanly
		setPolygonOffsetFillEnable(true);
		setPolygonWireframeDepthTrick(false);
	}

	/** Set the wireframe color. */
	public void setWireframeColor(Color color) {
		wireframeColor = color;
	}

	/** Set the wireframe display status to on or off. */
	public void setWireframeDisplayed(boolean status) {
		wireframeDisplayed = status;
	}

	/** Set the wireframe width. */
	public void setWireframeWidth(float width) {
		wireframeWidth = width;
	}

	/** Set the face display status to on or off. */
	public void setFaceDisplayed(boolean status) {
		faceDisplayed = status;
	}

	/** Get the wireframe color. */
	public Color getWireframeColor() {
		return wireframeColor;
	}

	/** Get the wireframe display status to on or off. */
	public boolean getWireframeDisplayed() {
		return wireframeDisplayed;
	}

	/** Get the wireframe width. */
	public float getWireframeWidth() {
		return wireframeWidth;
	}

	/** Get the face display status to on or off. */
	public boolean getFaceDisplayed() {
		return faceDisplayed;
	}

	/* ************ POLYGON OFFSET **************** */

	protected void polygonOffseFillEnable(IPainter painter) {
		painter.glEnable_PolygonOffsetFill();
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
	}

	protected void polygonOffsetFillDisable(IPainter painter) {
		painter.glDisable_PolygonOffsetFill();
	}

	protected void polygonOffsetLineEnable(IPainter painter) {
		painter.glEnable_PolygonOffsetLine();
		painter.glPolygonOffset(polygonOffsetFactor, polygonOffsetUnit);
	}

	protected void polygonOffsetLineDisable(IPainter painter) {
		painter.glDisable_PolygonOffsetLine();
	}

	public boolean isPolygonOffsetFillEnable() {
		return polygonOffsetFillEnable;
	}

	/**
	 * Enable offset fill, which let a polygon with a wireframe render cleanly
	 * without weird depth uncertainty between face polygon and wireframe polygon.
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

	/* ************ POLYGON OFFSET **************** */

	/**
	 * May be used as alternative to {@link #setPolygonOffsetFillEnable(boolean)} in
	 * case it is not supported by underlying OpenGL version (Polygon offset appears
	 * as off version 2).
	 */
	public void setPolygonWireframeDepthTrick(boolean polygonWireframeDepthTrick) {
		this.polygonWireframeDepthTrick = polygonWireframeDepthTrick;
	}

	public boolean isPolygonWireframeDepthTrick() {
		return polygonWireframeDepthTrick;
	}

	protected void applyDepthRangeForUnderlying(IPainter painter) {
		painter.glDepthRangef(0.1f, 1f);
	}

	protected void applyDepthRangeForOverlying(IPainter painter) {
		painter.glDepthRangef(0.0f, 0.9f);
	}
	



	protected Color wireframeColor;
	protected float wireframeWidth;
	protected boolean wireframeDisplayed;
	protected boolean faceDisplayed;
	protected boolean polygonWireframeDepthTrick = false;
	protected boolean polygonOffsetFillEnable = true;
	protected float polygonOffsetFactor = 1.0f;
	protected float polygonOffsetUnit = 1.0f;
}
