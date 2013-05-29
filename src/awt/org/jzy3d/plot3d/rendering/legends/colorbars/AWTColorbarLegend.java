package org.jzy3d.plot3d.rendering.legends.colorbars;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot2d.primitive.ColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;


public class AWTColorbarLegend extends AWTLegend implements IColorbarLegend{
	public AWTColorbarLegend(AbstractDrawable parent, IAxeLayout layout){
		this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), layout.getMainColor(), null);
	}

	public AWTColorbarLegend(AbstractDrawable parent, IAxeLayout layout, Color foreground){
		this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, null);
	}

	public AWTColorbarLegend(AbstractDrawable parent, IAxeLayout layout, Color foreground, Color background){
		this(parent, layout.getZTickProvider(), layout.getZTickRenderer(), foreground, background);
	}

	
	public AWTColorbarLegend(AbstractDrawable parent, ITickProvider provider, ITickRenderer renderer){
		this(parent, provider, renderer, Color.BLACK, Color.WHITE);
		
	}
	
	public AWTColorbarLegend(AbstractDrawable parent, ITickProvider provider, ITickRenderer renderer, Color foreground, Color background){
		super(parent);
		this.provider = provider;
		this.renderer = renderer;
		this.foreground = foreground;
		this.background = background;
		this.minimumDimension = new Dimension(ColorbarImageGenerator.MIN_BAR_WIDTH, ColorbarImageGenerator.MIN_BAR_HEIGHT);
	}

	@Override
    public void render(GL gl, GLU glu){	
		//gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); 
        gl.glEnable(GL2.GL_BLEND);
		super.render(gl, glu);
	}

	public BufferedImage toImage(int width, int height) {
		if(parent!=null && parent instanceof IMultiColorable){
			IMultiColorable mc = ((IMultiColorable)parent);
			if(mc.getColorMapper() != null){
				// setup generator
				ColorbarImageGenerator bar = new ColorbarImageGenerator(mc.getColorMapper(), provider, renderer);
				if(foreground!=null)
					bar.setForegroundColor(foreground);
				else
					bar.setForegroundColor(Color.BLACK);
				if(background!=null){
					bar.setBackgroundColor(background);
					bar.setHasBackground(true);
				}
				else
					bar.setHasBackground(false);
				
				// render @ given dimensions
				return bar.toImage(Math.max(width-25,1), Math.max(height-25,1));
			}
		}
		return null;
	}

	@Override
    public void drawableChanged(DrawableChangedEvent e) {
		if(e.what() == DrawableChangedEvent.FIELD_COLOR)
			updateImage();
	}
	
	@Override
    public Dimension getMinimumSize(){
		return minimumDimension;
	}
	
	@Override
    public void setMinimumSize(Dimension dimension){
		minimumDimension = dimension;
	}

	protected ITickProvider provider;
	protected ITickRenderer renderer;
	protected Dimension minimumDimension;
	
	protected Color foreground;
	protected Color background;
}
