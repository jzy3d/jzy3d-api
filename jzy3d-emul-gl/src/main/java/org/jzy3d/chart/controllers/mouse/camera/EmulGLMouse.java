package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.event.MouseEvent;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.maths.Coord2d;


/**
 * MUST TRIGGER REPAINT UPON MOVE
 * - LET COMPONENT REGISTER FOR MOUSE EVENTS
 * 
 * Rendering does not happen during mouse dragged. SHould register Mouse dragged in JGL
 * 
 * @author martin
 *
 */
public class EmulGLMouse extends AWTCameraMouseController{
	public EmulGLMouse() {
		super();
	}

	public EmulGLMouse(Chart chart) {
		super(chart);
	}

	/** Compute shift or rotate */
	@Override
	public void mouseDragged(MouseEvent e) {
		//TicToc t = new TicToc();
		//t.tic();
		
		Coord2d mouse = xy(e);

		// Rotate
		if (AWTMouseUtilities.isLeftDown(e)) {
			Coord2d move = mouse.sub(prevMouse).div(100);
			
			/*Thread t = new Thread(new Runnable() {

				@Override
				public void run() {*/
					rotate(move);
				/*}
				
			});
			t.run();*/
		}
		// Shift
		else if (AWTMouseUtilities.isRightDown(e)) {
			Coord2d move = mouse.sub(prevMouse);
			if (move.y != 0)
				shift(move.y / 500);
		}
		prevMouse = mouse;
		
		//t.tocShow("Mouse     took");
	}
}
