package org.jzy3d.plot3d.rendering.canvas;

import javax.media.opengl.GLDrawable;

import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.util.texture.TextureData;
//import com.jogamp.newt.event.MouseMotionListener;
//import com.jogamp.newt.event.MouseWheelListener;
//import java.awt.image.BufferedImage;


/**
 * A {@link ICanvas} represent the target component for rendering OpenGL.
 * 
 * It might be displayed on the screen in a GUI ({@link IScreenCanvas}), 
 * or simply be an offscreen component able to export an image {@link OffscreenCanvas}.
 * 
 * @see {@link IScreenCanvas}
 * 
 * @author Martin Pernollet
 */
public interface ICanvas {
	/** Returns a reference to the held view.*/
	public View getView();
        
    /** Returns the GLDrawable associated with the canvas */
    public GLDrawable getDrawable();

	/** Returns the renderer's width, i.e. the display width.*/
	public int getRendererWidth();
	
	/** Returns the renderer's height, i.e. the display height.*/
	public int getRendererHeight();	
	
	public Renderer3d getRenderer();
	
	/** Invoked when a user requires the Canvas to be repainted (e.g. a non 3d layer has changed).
	 */
	public void forceRepaint();
	
	/** Returns an image with the current renderer's size */
	public TextureData screenshot();
	
	/** Performs all required cleanup when destroying a Canvas.*/
	public void dispose();
	
	/* */
	
	public void addMouseListener(MouseListener listener);
	public void removeMouseListener(MouseListener listener);
	
//	public void addMouseWheelListener(MouseWheelListener listener);
//	public void removeMouseWheelListener(MouseWheelListener listener);
//	
//	public void addMouseMotionListener(MouseMotionListener listener);
//	public void removeMouseMotionListener(MouseMotionListener listener);
	
	public void addKeyListener(KeyListener listener);
	public void removeKeyListener(KeyListener listener);
	
	public String getDebugInfo();
}
