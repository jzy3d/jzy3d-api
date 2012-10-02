package org.jzy3d.plot3d.rendering.canvas;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;


/**
 * A {@link ICanvas} holds a {@link Renderer3d} and a {@link View}. It moreover allows
 * adding and removing listeners for handling mouse or key events.
 * 
 * In the case a user wants to override a concrete {@link ICanvas} painting policy, 
 * such as introducing layers in a {@link CanvasAWT} or {@link CanvasSwing}, 
 * he must be aware that the canvas may be repainted either by forceRepaint() or
 * by its "original" paint function (that is, the paint function provided by the 
 * Component it extends (GLJpanel, GLCanvas, etc)).
 * 
 * @author Martin Pernollet
 *
 */
public interface ICanvas {
	/** Returns a reference to the held view.*/
	public View getView();
	
	/** Returns the renderer's width, i.e. the display width.*/
	public int getRendererWidth();
	
	/** Returns the renderer's height, i.e. the display height.*/
	public int getRendererHeight();	
	
	public Renderer3d getRenderer();
	
	/** Invoked when a user requires the Canvas to be repainted (e.g. a non 3d layer has changed).
	 */
	public void forceRepaint();
	
	/** Returns an image with the current renderer's size */
	public BufferedImage screenshot();
	
	/** Performs all required cleanup when destroying a Canvas.*/
	public void dispose();
	
	/* */
	
	public void addMouseListener(MouseListener listener);
	public void removeMouseListener(MouseListener listener);
	
	public void addMouseWheelListener(MouseWheelListener listener);
	public void removeMouseWheelListener(MouseWheelListener listener);
	
	public void addMouseMotionListener(MouseMotionListener listener);
	public void removeMouseMotionListener(MouseMotionListener listener);
	
	public void addKeyListener(KeyListener listener);
	public void removeKeyListener(KeyListener listener);
	
	public String getDebugInfo();
}
