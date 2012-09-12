package org.jzy3d.plot3d.rendering.view.controllers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.view.View;


public class ViewMouseController extends ViewCameraController implements
		MouseListener, MouseMotionListener, MouseWheelListener {
	public ViewMouseController() {
		super();
	}

	public ViewMouseController(View view) {
		super();
		addTarget(view);
	}

	public void addMouseSource(IScreenCanvas canvas) {
		this.canvas = canvas;
		this.prevMouse = Coord2d.ORIGIN;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}

	public void dispose() {
		canvas.removeMouseListener(this);
		canvas.removeMouseMotionListener(this);
		canvas.removeMouseWheelListener(this);

		if (threadController != null)
			threadController.stop();
	}

	/*********************************************************/

	public void addSlaveThreadController(CameraThreadController controller) {
		removeSlaveThreadController();
		this.threadController = controller;
	}

	public void removeSlaveThreadController() {
		if (threadController != null) {
			threadController.stop();
			threadController = null;
		}
	}

	/*********************************************************/

	/**
	 * Handles toggle between mouse rotation/auto rotation: double-click starts
	 * the animated rotation, while simple click stops it.
	 */
	public void mousePressed(MouseEvent e) {
		//
		if (MouseUtilities.isDoubleClick(e)) {
			if (threadController != null) {
				threadController.start();
				return;
			}
		}
		if (threadController != null)
			threadController.stop();

		prevMouse.x = e.getX();
		prevMouse.y = e.getY();
	}

	/** Compute shift or rotate */
	public void mouseDragged(MouseEvent e) {
		Coord2d mouse = new Coord2d(e.getX(), e.getY());

		// Rotate
		if (MouseUtilities.isLeftDown(e)) {
			Coord2d move = mouse.sub(prevMouse).div(100);
			rotate(move);
		}
		// Shift
		else if (MouseUtilities.isRightDown(e)) {
			Coord2d move = mouse.sub(prevMouse);
			if (move.y != 0)
				shift(move.y / 500);
		}
		prevMouse = mouse;
	}

	/** Compute zoom */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (threadController != null)
			threadController.stop();

		float factor = 1 + (e.getWheelRotation() / 10.0f);
		zoom(factor);
	}

	public void mouseClicked(MouseEvent e) {
		/*
		 * if(MouseUtilities.isRightClick(e)){ rightClicked(e); }
		 */
		/*
		 * else if(MouseUtilities.isLeftDown(e)){ rightClicked(e); }
		 */
	}

	/*
	 * public void rightClicked(MouseEvent e){
	 * 
	 * }
	 */

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	/*********************************************************/

	protected IScreenCanvas canvas;
	protected Coord2d prevMouse;
	protected CameraThreadController threadController;

	// protected Chart chart;
}
