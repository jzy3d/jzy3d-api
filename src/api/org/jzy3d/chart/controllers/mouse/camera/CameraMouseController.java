package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.Coord2d;



public class CameraMouseController extends AbstractCameraController implements MouseListener, MouseMotionListener, MouseWheelListener{
	
	public CameraMouseController(){
	}
	
	public CameraMouseController(Chart chart){
	    register(chart);
	    addSlaveThreadController(new CameraThreadController(chart));
	}
	
	public void register(Chart chart){
		super.register(chart);
		if(chart.getCanvas() instanceof Component){
			Component c = (Component)chart.getCanvas();
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
			c.addMouseWheelListener(this);
		}
	}
	
	public void dispose(){
		for(Chart chart: targets){
			if(chart.getCanvas() instanceof Component){
				Component c = (Component)chart.getCanvas();
				c.removeMouseListener(this);
				c.removeMouseMotionListener(this);
				c.removeMouseWheelListener(this);
			}
		}
		super.dispose();
	}
	
	/** Handles toggle between mouse rotation/auto rotation: double-click starts the animated
	 * rotation, while simple click stops it.*/
	public void mousePressed(MouseEvent e) {
		// 
		if(handleSlaveThread(e))
		    return;
			
		prevMouse.x  = e.getX();
		prevMouse.y  = e.getY();
	}
	
    /** Compute shift or rotate*/
	public void mouseDragged(MouseEvent e) {
		Coord2d mouse = new Coord2d(e.getX(),e.getY());
		
		// Rotate
		if(MouseUtilities.isLeftDown(e)){
			Coord2d move  = mouse.sub(prevMouse).div(100);
			rotate( move );
		}
		// Shift
		else if(MouseUtilities.isRightDown(e)){
			Coord2d move  = mouse.sub(prevMouse);
			if(move.y!=0)
				shift(move.y/500);
		}
		prevMouse = mouse;
	}
	
	/** Compute zoom */
	public void mouseWheelMoved(MouseWheelEvent e) {
		stopThreadController();		
		float factor = 1 + (e.getWheelRotation()/10.0f);
		zoomZ(factor);
	}
	
	public void mouseClicked(MouseEvent e) {}  
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {} 
	public void mouseMoved(MouseEvent e) {}
	
	public boolean handleSlaveThread(MouseEvent e) {
	    if(MouseUtilities.isDoubleClick(e)){
			if(threadController!=null){
				threadController.start();
				return true;
			}
		}
		if(threadController!=null)
			threadController.stop();
		return false;
	}
}
