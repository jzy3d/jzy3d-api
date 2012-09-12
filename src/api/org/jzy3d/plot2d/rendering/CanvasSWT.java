package org.jzy3d.plot2d.rendering;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.jzy3d.colors.Color;


// TODO: handling of color disposing is an enormous mess!

public class CanvasSWT implements Canvas{
	/**
     * Creates a new instance of Pencil2dAWT.
     * 
     * A Pencil2dAWT provides an implementation for drawing wafer sites
     * on an SWT GC (Graphic Context).
     */
	public CanvasSWT(GC graphic){
		target = graphic;
	}
	
	public void dispose(){
		if(bgColor!=null)
			bgColor.dispose();
		if(fgColor!=null)
			fgColor.dispose();
	}
	
	public void drawString( int x, int y, String text ){
		target.setBackground(bgColor);
		target.setForeground(fgColor); //display.getSystemColor(SWT.COLOR_BLUE)
		target.setFont( new Font( Display.getDefault(), "Arial", 8, SWT.NONE ) ); //SWT.BOLD
		target.drawString(text, x, y);
	}
	
	public void drawRect( Color color, int x, int y, int width, int height, boolean border ){
		if(color!=null){
			if(current!=null)
				current.dispose();
			current = swt(color);
			
			target.setBackground(current);
			target.fillRectangle(x, y, width, height);
		}
		
		if(border){
			target.setForeground(fgColor);
			target.drawRectangle(x, y, width, height);
		}
		
		bgColor.dispose();
	}
	
	public void drawRect( Color color, int x, int y, int width, int height ){
		drawRect( color, x, y, width, height, true );
	}
	
	public void drawDot( Color color, int x, int y ){
		if(current!=null)
			current.dispose();
		current = swt(color);
		
		target.setBackground(current);
		target.setForeground(current);
		target.drawRectangle(x-PIXEL_WITH/2, y-PIXEL_WITH/2, PIXEL_WITH, PIXEL_WITH);
		//target.drawRectangle(x, y, PIXEL_WITH, PIXEL_WITH);
	}
	
	public void drawOval( Color color, int x, int y, int width, int height ){
		if(current!=null)
			current.dispose();
		current = swt(color);
		
		target.setBackground(current);
		target.fillOval(x, y, width, height);
		target.setForeground(BLACK);
		target.drawOval(x, y, width, height);
	}

	public void drawBackground( Color color, int width, int heigth ){
		if(bgColor!=null)
			bgColor.dispose();
		bgColor = swt(color);
		
		target.setBackground(bgColor);
		target.setForeground(fgColor);
		target.fillRectangle(0, 0, width, heigth);	
	}
	
	/**********************************************************************************/

	/** Converts a {@link org.jzy3d.colors.Color Imaging Color} 
	 * into a {@link org.eclipse.swt.graphics.Color SWT Color}.
	 *
	 * Note that SWT colors do not have an alpha channel.
	 */
	public static org.eclipse.swt.graphics.Color swt(Color color){
		return new org.eclipse.swt.graphics.Color(Display.getDefault(),(int)(255*color.r),(int)(255*color.g),(int)(255*color.b));
	}
	
	/**********************************************************************************/

	/** Set up an initial reference to the SWT Display.*/
	/*static{
		d = Display.getDefault();
	}
	
	private static Display d;*/
	
	/**********************************************************************************/

	private GC target;
	private org.eclipse.swt.graphics.Color bgColor = new org.eclipse.swt.graphics.Color(Display.getDefault(), 255, 255, 255 ); // default bg
	private org.eclipse.swt.graphics.Color fgColor = new org.eclipse.swt.graphics.Color(Display.getDefault(), 0, 0, 0 ); // default bg
	private org.eclipse.swt.graphics.Color current; 
	
	@SuppressWarnings("unused")  
	private static org.eclipse.swt.graphics.Color WHITE = 
		new org.eclipse.swt.graphics.Color(Display.getDefault(), 255, 255, 255 );
	private static org.eclipse.swt.graphics.Color BLACK = 
		new org.eclipse.swt.graphics.Color(Display.getDefault(), 0, 0, 0 );
	
	private final static int PIXEL_WITH = 1; 
}
