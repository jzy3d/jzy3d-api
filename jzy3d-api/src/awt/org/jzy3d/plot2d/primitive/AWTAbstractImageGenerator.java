package org.jzy3d.plot2d.primitive;

import java.awt.Font;
import java.awt.Graphics2D;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorAWT;

public abstract class AWTAbstractImageGenerator implements AWTImageGenerator{
    public void configureText(Graphics2D graphic) {
        graphic.setFont(font); //Text for the numbers in the ColorBar is Size=12
    }

    public void drawBackground(int width, int height, Graphics2D graphic) {
        if(hasBackground){
            graphic.setColor(ColorAWT.toAWT(backgroundColor));
            graphic.fillRect(0, 0, width, height);
        }
    }
    protected boolean hasBackground = false;

    @Override
    public boolean hasBackground() {
    	return hasBackground;
    }

    @Override
    public void setHasBackground(boolean hasBackground) {
    	this.hasBackground = hasBackground;
    }

    @Override
    public Color getBackgroundColor() {
    	return backgroundColor;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
    	this.backgroundColor = backgroundColor;
    }

    @Override
    public Color getForegroundColor() {
    	return foregroundColor;
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
    	this.foregroundColor = foregroundColor;
    }
    
    public void drawLegendBorder(Graphics2D graphic, int width, int height) {
        graphic.setColor(ColorAWT.toAWT(foregroundColor));
        graphic.drawRect(0, 0, width - 1, height-1);
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }
    protected Font font;
    protected int textSize;

    protected Color backgroundColor;
    protected Color foregroundColor = Color.BLACK;
    public static final int MIN_BAR_WIDTH = 100;
    public static final int MIN_BAR_HEIGHT = 100;
}