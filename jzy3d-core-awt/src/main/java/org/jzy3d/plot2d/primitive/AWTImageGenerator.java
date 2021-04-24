package org.jzy3d.plot2d.primitive;


import java.awt.image.BufferedImage;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.Font;

public interface AWTImageGenerator {
  public BufferedImage toImage(int width, int height);

  public boolean hasBackground();

  public void setHasBackground(boolean hasBackground);

  public Color getBackgroundColor();

  public void setBackgroundColor(Color backgroundColor);

  public Color getForegroundColor();

  public void setForegroundColor(Color foregroundColor);

  public java.awt.Font getAWTFont();

  public void setAWTFont(java.awt.Font font);

  public void setFont(Font font);

  public Font getFont();
}
