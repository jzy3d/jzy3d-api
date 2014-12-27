package org.jzy3d.plot2d.primitive;

import java.awt.Font;
import java.awt.image.BufferedImage;

import org.jzy3d.colors.Color;

public interface AWTImageGenerator {
    public BufferedImage toImage(int width, int height);

    public boolean hasBackground();

    public void setHasBackground(boolean hasBackground);

    public Color getBackgroundColor();

    public void setBackgroundColor(Color backgroundColor);

    public Color getForegroundColor();

    public void setForegroundColor(Color foregroundColor);
    
    public Font getFont() ;

    public void setFont(Font font);
    
}
