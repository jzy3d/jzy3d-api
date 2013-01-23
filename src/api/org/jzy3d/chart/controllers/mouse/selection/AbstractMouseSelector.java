package org.jzy3d.chart.controllers.mouse.selection;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.View;


public abstract class AbstractMouseSelector implements MouseListener, MouseMotionListener, MouseWheelListener{
	public AbstractMouseSelector(){
		in = new IntegerCoord2d(-1,-1);
		last = new IntegerCoord2d(-1,-1);
		out = new IntegerCoord2d(-1,-1);
	}
	
	public void dispose(){
		unregister();
	}
	
	public void register(Chart chart){
		this.chart = chart;
		this.chart.getCanvas().addMouseListener(this);
		this.chart.getCanvas().addMouseMotionListener(this);
		this.chart.getCanvas().addMouseWheelListener(this);
		
		final ICanvas c = chart.getCanvas();
		selectionRenderer = initRenderer2d(c);
		this.chart.getView().addRenderer2d(selectionRenderer);
	}
	
	public void unregister(){
		if(chart!=null){
			chart.getCanvas().removeMouseListener(this);
			chart.getCanvas().removeMouseMotionListener(this);
			chart.getCanvas().removeMouseWheelListener(this);
			chart.getView().removeRenderer2d(selectionRenderer);
		}
	}
	
	protected Renderer2d initRenderer2d(final ICanvas c){
		return new Renderer2d(){
			public void paint(Graphics g) {
				drawSelection((Graphics2D)g, c.getRendererWidth(), c.getRendererHeight());
				updateLast();
			}
		};
	}
	
	
	/*****************************************/

	protected abstract void processSelection(Scene scene, View view, int width, int height);
	
	/** Drawing occurs in the selection renderer which as the dimension of the GL2 scene
	 * viewport. In other words, one should not expect to draw on the entire canvas if
	 * the GL2 scene viewport only covers a slice of the screen.
	 * As an example, the following piece of code will draw a border around the GL2 scene:
	 * 
	 * g2d.drawRect(1, 1, chart.getCanvas().getRendererWidth()-2, chart.getCanvas().getRendererHeight()-2);
	 * 
	 * @see {@link AbstractViewportManager} and {@link Camera}
	 */
	protected abstract void drawSelection(Graphics2D g, int width, int height);
	
    public abstract void clearLastSelection();
	
	protected boolean matchRectangleSelection(IntegerCoord2d in, IntegerCoord2d out, Coord3d projection, int width, int height) {
		return matchRectangleSelection(in, out, projection.x, projection.y, width, height);
	}
	
	/**
	 * @param in The mouse selection start point
	 * @param out The mouse selection end point
	 * @param px A projected point x value
	 * @param py A projected point y value
	 * @param width The canvas dimension
	 * @param height The canvas dimension
	 * @return true if the point (px,py) is inside the (in,out) mouse selection of a canvas having dimensions (width, height)
	 */
	protected boolean matchRectangleSelection(IntegerCoord2d in, IntegerCoord2d out, float px, float py, int width, int height) {
		float flipYProjection = height - py;
		
		// 4|3
		// ---
		// 2|1
		if (in.y < out.y) {
			if (in.x < out.x){
				//System.out.println("1");
				if (in.x <= px && px <= out.x
				 && in.y <= flipYProjection && flipYProjection <= out.y)
					return true;
			}else {
				//System.out.println("2");
				if (out.x <= px && px <= in.x
				 && in.y <= flipYProjection && flipYProjection <= out.y)
					return true;
			}

		} else {
			if (in.x < out.x){
				//System.out.println("3");
				if (in.x <= px && px <= out.x
				 && out.y <= flipYProjection && flipYProjection <= in.y)
					return true;
			}else {
				//System.out.println("4");
				if (out.x <= px && px <= in.x 
				 && out.y <= flipYProjection && flipYProjection <= in.y) // buggy
					return true;
			}
		}
		return false;
	}
	
	protected void drawRectangle(Graphics2D g2d, IntegerCoord2d in, IntegerCoord2d out) {
		g2d.setColor(java.awt.Color.RED);
		if (in.y < out.y) {
			if (in.x < out.x)
				g2d.drawRect(in.x, in.y, out.x - in.x, out.y - in.y);
			else
				g2d.drawRect(out.x, in.y, in.x - out.x, out.y - in.y);
		} else {
			if (in.x < out.x)
				g2d.drawRect(in.x, out.y, out.x - in.x, in.y - out.y);
			else 
				g2d.drawRect(out.x, out.y, in.x - out.x, in.y - out.y);
		}
	}
	
	/*****************************************/

	protected void startSelection(MouseEvent e){
		in = new IntegerCoord2d(e.getX(), e.getY());
		last = new IntegerCoord2d(e.getX(), e.getY());
		out = new IntegerCoord2d(e.getX(), e.getY());
	}
	
	protected void dragSelection(MouseEvent e){
		out.x = e.getX();
		out.y = e.getY();
		chart.render();
	}
	
	protected void releaseSelection(MouseEvent e){
		out.x = e.getX();
		out.y = e.getY();
		
		processSelection(chart.getScene(), chart.getView(), chart.getCanvas().getRendererWidth(), chart.getCanvas().getRendererHeight());
		chart.render(); // calls draw selection
	}
	
	protected void rollOver(MouseEvent e){
		
	}

	protected void updateLast(){
		last.x = out.x;
		last.y = out.y;
	}
	
	/*****************************************/
	
	public void mousePressed(MouseEvent e) {
		dragging = true;
		startSelection(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		if(dragging)
			dragSelection(e);
		//else
		//	rollOver(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		if(dragging)
			releaseSelection(e);
		dragging = false;
	}
	
	public void mouseMoved(MouseEvent e) {
		rollOver(e);
	}
	
	/*****************************************/
	
	public void mouseWheelMoved(MouseWheelEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}	    
	
	/*****************************************/
	
	protected Chart chart;
	protected boolean dragging = false;
	protected IntegerCoord2d in;
	protected IntegerCoord2d out;
	protected IntegerCoord2d last;
	
	protected Renderer2d selectionRenderer;
}
