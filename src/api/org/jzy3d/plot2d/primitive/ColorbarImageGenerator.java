package org.jzy3d.plot2d.primitive;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;


/** 
 * @author Martin Pernollet
 */
public class ColorbarImageGenerator {
	public ColorbarImageGenerator(IColorMap map, float min, float max, ITickProvider provider, ITickRenderer renderer){
		mapper = new ColorMapper(map, min, max);
		this.min = min;
		this.max = max;
		this.provider = provider;
		this.renderer = renderer;
	}
	
	public ColorbarImageGenerator(ColorMapper mapper, ITickProvider provider, ITickRenderer renderer){
	    this.mapper = mapper;
        this.provider = provider;
        this.renderer = renderer;
        this.min = mapper.getMin();
        this.max = mapper.getMax();
		//this(mapper.getColorMap(), mapper.getMin(), mapper.getMax(), provider, renderer);
	}

    public BufferedImage toImage(int width, int height) {
        return toImage(width, height, 20);
    }
    
	/** Renders the {@link ColorbarImageGenerator} to an image. */
	public BufferedImage toImage(int width, int height, int barWidth){
		if(barWidth>width) 
			return null;
		
		// Init image output
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphic = image.createGraphics();
		
		int txtSize=12;
		graphic.setFont(new java.awt.Font("Arial",0,txtSize)); //Text for the numbers in the ColorBar is Size=12
		
		// Draw background
		if(hasBackground){
			graphic.setColor(backgroundColor);
			graphic.fillRect(0, 0, width, height);
		}
		
		// Draw colorbar centering in half the Legend text height
		for(int h=txtSize/2; h<=(height-txtSize/2); h++){
			// Compute value & color
			double v = min + (max-min) * ((float)h)/((float)(height-txtSize));
			Color c = mapper.getColor(v);  //To allow the Color to be a variable independent of the coordinates
			
			// Draw line
			graphic.setColor(c.awt());
			graphic.drawLine(0, height-h, barWidth, height-h);
		}
		
		// Contour of bar
		graphic.setColor(foregroundColor);
		graphic.drawRect(0, txtSize/2, barWidth, height-txtSize);
		
		// Text annotation
		if(provider!=null){
    		double[] ticks = provider.generateTicks(min, max);
    		int ypos;
    		String txt;
    		for(int t=0; t<ticks.length; t++){
    //			ypos = (int)(height-height*((ticks[t]-min)/(max-min)));
    			ypos = (int)(txtSize+(height-txtSize-(height-txtSize)*((ticks[t]-min)/(max-min)))); //Making sure that the first and last tick appear in the colorbar
    			txt = renderer.format(ticks[t]);
    			graphic.drawString(txt, barWidth+1, ypos);
    		}
		}
		return image;
	}
	
	/*********************************************************************/
	
	public boolean hasBackground() {
		return hasBackground;
	}

	public void setHasBackground(boolean hasBackground) {
		this.hasBackground = hasBackground;
	}

	public Color getBackgroundColor() {
		return new Color(backgroundColor);
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor.awt();
	}
	
	public Color getForegroundColor() {
		return new Color(foregroundColor);
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor.awt();
	}

	/*********************************************************************/
	
	protected ColorMapper mapper;
	protected ITickProvider provider;
	protected ITickRenderer renderer;
	protected double min;
	protected double max;
	protected boolean hasBackground = false;
	protected java.awt.Color backgroundColor;
	protected java.awt.Color foregroundColor = java.awt.Color.BLACK;
	
	public static final int MIN_BAR_WIDTH  = 100;
	public static final int MIN_BAR_HEIGHT = 100;
}
