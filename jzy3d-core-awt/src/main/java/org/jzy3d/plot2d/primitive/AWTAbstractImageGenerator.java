package org.jzy3d.plot2d.primitive;


import java.awt.Graphics2D;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.painters.Font;

public abstract class AWTAbstractImageGenerator implements AWTImageGenerator {
  protected java.awt.Font awtFont;
  protected Font font;

  protected int textSize;

  protected boolean hasBackground = false;
  protected Color backgroundColor;
  protected Color foregroundColor = Color.BLACK;


  public void configureText(Graphics2D graphic) {
    graphic.setFont(awtFont); // Text for the numbers in the ColorBar is Size=12
  }

  public void drawBackground(int width, int height, Graphics2D graphic) {
    if (hasBackground) {
      graphic.setColor(AWTColor.toAWT(backgroundColor));
      graphic.fillRect(0, 0, width, height);
    }
  }


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
    graphic.setColor(AWTColor.toAWT(foregroundColor));
    graphic.drawRect(0, 0, width - 1, height - 1);
  }

  @Override
  public java.awt.Font getAWTFont() {
    return awtFont;
  }

  @Override
  public void setAWTFont(java.awt.Font font) {
    this.awtFont = font;
  }

  @Override
  public void setFont(Font font) {
    // reset font only if necessary
    if (this.font == null || !this.font.equals(font)) {
      this.font = font;
      this.textSize = font.getHeight();

      setAWTFont(AWTFont.toAWT(font));
    }
  }

  @Override
  public Font getFont() {
    return font;
  }
}
