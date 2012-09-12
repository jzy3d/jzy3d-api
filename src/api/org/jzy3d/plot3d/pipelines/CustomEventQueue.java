package org.jzy3d.plot3d.pipelines;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;


/** Utility for debugging event queue related to 3d in AWT.*/
public class CustomEventQueue extends EventQueue {
	protected void dispatchEvent(AWTEvent event){
		if(event instanceof PaintEvent)
			;//System.out.println("Dispatch [PAINT]: "+event);
		else if(event instanceof MouseEvent)
			;//System.out.println("Dispatch [MOUSE]: "+event);
		else if(event instanceof InvocationEvent){
			;//System.out.println("Dispatch [INVOC]: "+event);
		}
		else
			System.out.println("Dispatch [UNKNO]: "+event);
		
		super.dispatchEvent(event);
	}
	
	public static void setCustomEventQueue(){
		if(!customQueueSet)
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(new CustomEventQueue());
	}
	
	
	/*********************************************************/
	
	private static boolean customQueueSet = false;
}
