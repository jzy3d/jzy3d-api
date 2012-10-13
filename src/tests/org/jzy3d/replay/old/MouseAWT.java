package org.jzy3d.replay.old;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;

public class MouseAWT {
	/**
	 *  static int 	BUTTON1
          Indicates mouse button #1; used by getButton().
static int 	BUTTON2
          Indicates mouse button #2; used by getButton().
static int 	BUTTON3
          Indicates mouse button #3; used by getButton().
static int 	MOUSE_CLICKED
          The "mouse clicked" event.
static int 	MOUSE_DRAGGED
          The "mouse dragged" event.
static int 	MOUSE_ENTERED
          The "mouse entered" event.
static int 	MOUSE_EXITED
          The "mouse exited" event.
static int 	MOUSE_FIRST
          The first number in the range of ids used for mouse events.
static int 	MOUSE_LAST
          The last number in the range of ids used for mouse events.
static int 	MOUSE_MOVED
          The "mouse moved" event.
static int 	MOUSE_PRESSED
          The "mouse pressed" event.
static int 	MOUSE_RELEASED
          The "mouse released" event.
static int 	MOUSE_WHEEL
          The "mouse wheel" event.
static int 	NOBUTTON
          Indicates no mouse buttons; used by getButton().
          
          
MouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) 
              source - the Component that originated the event
    id - the integer that identifies the event
    when - a long int that gives the time the event occurred
    modifiers - the modifier keys down during event (e.g. shift, ctrl, alt, meta) Either extended _DOWN_MASK or old _MASK modifiers should be used, but both models should not be mixed in one event. Use of the extended modifiers is preferred.
    x - the horizontal x coordinate for the mouse location
    y - the vertical y coordinate for the mouse location
    clickCount - the number of mouse clicks associated with event
    popupTrigger - a boolean, true if this event is a trigger for a popup menu
    button - which of the mouse buttons has changed state. NOBUTTON, BUTTON1, BUTTON2 or BUTTON3. 
          
          
	 * @param chart
	 */
	public void mouse(Chart chart){
		CanvasAWT canvas = (CanvasAWT)chart.getCanvas();

		//(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) 
		/*for(MouseListener ml: tree.getMouseListeners()){
		    ml.mousePressed(me);
		}*/
		int max = 100;
		int sleep = 2;
		go(canvas, max, sleep);
	}
	
	public void go(CanvasAWT c, int max, int sleep){
		int start = 30;
		getRobot().mouseMove(start, start);
		getRobot().mousePress(InputEvent.BUTTON1_MASK);
		

		//c.triggerMouseEvent(mouseClicked(c, 1, 1));
		for (int i = start; i < max; i++) {
			getRobot().mouseMove(i, i);
			//c.triggerMouseMotionEvent(mouseDragged(c, i, i));
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				new RuntimeException(e);
			}
		}
		getRobot().mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public MouseEvent mouseClicked(Component c, int x, int y){
		MouseEvent me = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, now(), 0, x, y, 1, false, MouseEvent.BUTTON1);
		return me;
	}

	public MouseEvent mouseDragged(Component c, int x, int y){
		MouseEvent me = new MouseEvent(c, MouseEvent.MOUSE_DRAGGED, now(), 0, x, y, 1, false);
		return me;
	}

	public long now(){
		return new Date().getTime();
	}
	
	public void mouseClicked(Component c, int x, int y, List<MouseEvent> seq){
		seq.add(mouseClicked(c, x, y));
	}
	
	public Robot getRobot(){
		if(robot==null)
			try {
				robot = new Robot();
			} catch (AWTException e) {
				throw new RuntimeException(e);
			}
		return robot;
	}
	
	Robot robot;
}
