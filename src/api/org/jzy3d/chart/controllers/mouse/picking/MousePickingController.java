package org.jzy3d.chart.controllers.mouse.picking;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;

public class MousePickingController<V, E> extends AbstractCameraController
		implements MouseListener, MouseMotionListener, MouseWheelListener {
	public MousePickingController() {
		super();
		picking = new PickingSupport();
	}

	public MousePickingController(Chart chart) {
		super(chart);
		picking = new PickingSupport();
	}

	public MousePickingController(Chart chart, int brushSize) {
		super(chart);
		picking = new PickingSupport(brushSize);
	}

	public MousePickingController(Chart chart, int brushSize, int bufferSize) {
		super(chart);
		picking = new PickingSupport(brushSize, bufferSize);
	}

	public void register(Chart chart) {
		super.register(chart);
		this.chart = chart;
		this.prevMouse = Coord2d.ORIGIN;
		chart.getCanvas().addMouseListener(this);
		chart.getCanvas().addMouseMotionListener(this);
		chart.getCanvas().addMouseWheelListener(this);
	}

	public void dispose() {
		for (Chart c : targets) {
			c.getCanvas().removeMouseListener(this);
			c.getCanvas().removeMouseMotionListener(this);
			c.getCanvas().removeMouseWheelListener(this);
		}

		if (threadController != null)
			threadController.stop();

		super.dispose(); // i.e. target=null
	}

	/****************/

	public PickingSupport getPickingSupport() {
		return picking;
	}

	public void setPickingSupport(PickingSupport picking) {
		this.picking = picking;
	}

	/****************/

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	/** Compute zoom */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (threadController != null)
			threadController.stop();
		System.out.println(e.getWheelRotation());
		float factor = 1 + (e.getWheelRotation() / 10.0f);
		System.out.println(MousePickingController.class.getSimpleName() + "wheel:" + factor * 100);
		zoomX(factor);
		zoomY(factor);
		chart.getView().shoot();
	}

	public void mouseMoved(MouseEvent e) {
	    System.out.println("moved");
		pick(e);
	}

	public void mousePressed(MouseEvent e) {
		if (handleSlaveThread(e))
			return;
		pick(e);
	}

	public void pick(MouseEvent e) {
		int yflip = -e.getY() + targets.get(0).getCanvas().getRendererHeight();
		prevMouse.x = e.getX();
		prevMouse.y = e.getY();// yflip;
		View view = targets.get(0).getView();
		prevMouse3d = view.projectMouse(e.getX(), yflip);

		GL2 gl = chart().getView().getCurrentGL();
		Graph graph = chart().getScene().getGraph();

		// will trigger vertex selection event to those subscribing to
		// PickingSupport.
		picking.pickObjects(gl, glu, view, graph, new IntegerCoord2d(e.getX(),
				yflip));
	}

	public boolean handleSlaveThread(MouseEvent e) {
		if (MouseUtilities.isDoubleClick(e)) {
			if (threadController != null) {
				threadController.start();
				return true;
			}
		}
		if (threadController != null)
			threadController.stop();
		return false;
	}

	/**********************/

	protected float factor = 1;
	protected float lastInc;
	protected Coord3d mouse3d;
	protected Coord3d prevMouse3d;
	protected PickingSupport picking;
	protected GLU glu = new GLU();

	protected Chart chart;
	
	protected Coord2d prevMouse;
	protected CameraThreadController threadController;

}
