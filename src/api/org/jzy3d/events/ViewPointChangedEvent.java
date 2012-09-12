package org.jzy3d.events;

import java.util.EventObject;

import org.jzy3d.maths.Coord3d;


public class ViewPointChangedEvent extends EventObject{

	public ViewPointChangedEvent(Object source, Coord3d viewPoint){
		super(source);
		this.viewPoint = viewPoint;
	}
	
	public Coord3d getViewPoint(){
		return viewPoint;
	}
	
	/***********************************************************/
	
	private Coord3d viewPoint;
	private static final long serialVersionUID = 6472340198525925419L;
}
