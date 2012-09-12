package org.jzy3d.plot3d.text.overlay;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;


/**
 * Highly experimental text renderer.
 * 
 * The {@link TextOverlay} allows computing the 2d position of a text in the window, according to its required position
 * in the 3d environment (using appenText method).
 * As a 2nd pass, it might be rendered into a java {@link Graphics} context. The interesting thing, compared to the
 * currently used {@link TextBitmapRenderer}, is that the user may select any java Font, whereas the {@link TextBitmapRenderer}
 * only relies on Helvetica font provided by opengl.
 * 
 * Actually no need to urgently correct these following todo: due to the problems related to post/pre rendering (blinking),
 * this text Renderer is not used. An alternative would be to render the java text could be renderer into a Java Image
 * that could in turn be rendered by opengl.
 * 
 * // TODO: Verify alignement constants
 * // TODO: Complete text renderer: select font, size, zoom
 * // TODO: there may be a bug of non-rendering without resizing at init -> check that
 * // TODO: the 1<->N relation TextRenderer<->Canvas won't work because TextRenderer has only one target Component.
 */
public class TextOverlay implements Renderer2d{
	public TextOverlay(ICanvas canvas){
		if(canvas instanceof CanvasAWT)
			initComponent((Component)canvas);
		else if(canvas instanceof CanvasSwing)
			initComponent((Component)canvas);
		else
			throw new RuntimeException("TextRenderer not implemented for this Canvas implementation");
	}
	
	private void initComponent(Component c){
		textList = new ArrayList<TextDescriptor>(50);
		target   = c;
		target.addComponentListener(resizeListener);
		
		targetWidth  = target.getWidth();
		targetHeight = target.getHeight();
	}

	public void dispose(){
		target.removeComponentListener(resizeListener);
	}
	
	/****************************************************************/
	
	public void appendText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, Halign halign, Valign valign, Color color){
		Coord3d posScreen = cam.modelToScreen(gl, glu, position);

		textList.add(new TextDescriptor(s, new Coord2d(posScreen.x, posScreen.y), color, halign, valign));
	}
	
	public void paint(Graphics g){
		int x;
		int y;
		
        
		FontMetrics metric = g.getFontMetrics();
        
        for(TextDescriptor t: textList){
			
			// Convert from GL2 to J2D coordinates mode
			x = (int)t.position.x; 
			y = targetHeight - (int)t.position.y;
			
			// Apply alignment
			Rectangle2D area = metric.getStringBounds(t.str, g);
			
			if(t.halign == Halign.RIGHT)
				;//x = x;
			else if(t.halign == Halign.CENTER)
				x = x - (int)area.getWidth()/2;
			else if(t.halign == Halign.LEFT)
				x = x - (int)area.getWidth();
			
			if(t.valign == Valign.TOP)
				;//y = y;
			else if(t.valign == Valign.CENTER)
				y = y + (int)area.getHeight()/2;
			else if(t.valign == Valign.BOTTOM || t.valign == Valign.GROUND)
				y = y + (int)area.getHeight();
			
			// Perform the actual text rendering
			g.setColor(new java.awt.Color(t.color.r, t.color.g, t.color.b, t.color.a));
			g.drawString(t.str, x, y);
		}
		textList.clear();
	}
	
	/***************************************************************************/
	
	@SuppressWarnings("unused")
	private int targetWidth;
	private int targetHeight;
	private Component target;
	
	private List<TextDescriptor> textList;
	
	private ComponentListener resizeListener = new ComponentListener(){
		public void componentHidden(ComponentEvent e) {
		}
		public void componentMoved(ComponentEvent e) {
		}
		public void componentResized(ComponentEvent e) {
			Component obj = (Component)e.getSource();
			targetWidth  = obj.getWidth();
			targetHeight = obj.getHeight();
		}
		public void componentShown(ComponentEvent e) {
		}
	};
	
	/***************************************************************************/
	
	private class TextDescriptor{
		public TextDescriptor(String str, Coord2d position, Color color, Halign halign, Valign valign){
			this.str      = str;
			this.position = position;
			this.color    = color;
			this.halign   = halign;
			this.valign   = valign;
		}
		
		public String  str;
		public Color   color;
		public Coord2d position;
		public Halign  halign;
		public Valign  valign;
	}
}