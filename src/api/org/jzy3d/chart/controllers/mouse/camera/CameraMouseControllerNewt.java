package org.jzy3d.chart.controllers.mouse.camera;


import java.awt.event.MouseWheelEvent;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;



public class CameraMouseControllerNewt extends AbstractCameraController implements MouseListener{
	
	public CameraMouseControllerNewt(){
	}
	
	public CameraMouseControllerNewt(Chart chart){
	    register(chart);
		addSlaveThreadController(new CameraThreadController(chart));
	}
	
	public void register(Chart chart){
		super.register(chart);
		ICanvas c = chart.getCanvas();
		if(c instanceof CanvasNewt){
			CanvasNewt cnt = (CanvasNewt)c;
			cnt.getWindow().addMouseListener(this);
		}
		else{
			throw new IllegalArgumentException("Using this camera mouse controller requires a CanvasNewt. Having: " + c.getClass().getSimpleName());
		}
	}
	
	public void dispose(){
		for(Chart c: targets){
			ICanvas ca = c.getCanvas();
			if(ca instanceof CanvasNewt){
				CanvasNewt cnt = (CanvasNewt)ca;
				cnt.getWindow().removeMouseListener(this);
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
	
    public boolean handleSlaveThread(MouseEvent e) {
        if(isDoubleClick(e)){
			if(threadController!=null){
				threadController.start();
				return true;
			}
		}
		if(threadController!=null)
			threadController.stop();
		return false;
    }

	/** Compute shift or rotate*/
	public void mouseDragged(MouseEvent e) {
		Coord2d mouse = new Coord2d(e.getX(),e.getY());
		// Rotate
				if(isLeftDown(e)){
					Coord2d move  = mouse.sub(prevMouse).div(100);
					rotate( move );
				}
				// Shift
				else if(isRightDown(e)){
					Coord2d move  = mouse.sub(prevMouse);
					if(move.y!=0)
						shift(move.y/500);
				}
		
		prevMouse = mouse;
	}
	
	public static boolean isLeftDown(MouseEvent e){
    	return (e.getModifiers() & MouseEvent.BUTTON1_MASK) == MouseEvent.BUTTON1_MASK;
	}

	public static boolean isRightDown(MouseEvent e){
		return (e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK; 
	}
	
	public static boolean isDoubleClick(MouseEvent e){
		//System.out.println(e.getClickCount());
    	return (e.getClickCount() > 1);
	}
	
	
	/** Compute zoom */
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}
	
	public void mouseClicked(MouseEvent e) {}  
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {} 
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		stopThreadController();
		
		float factor = 1 + (e.getWheelRotation()/10.0f);
		zoomZ(factor);
	}
	
	/*********************************************************/
	
	//protected Chart chart;
}
