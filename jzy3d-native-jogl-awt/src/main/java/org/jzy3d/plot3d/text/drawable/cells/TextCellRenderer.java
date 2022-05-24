package org.jzy3d.plot3d.text.drawable.cells;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.painters.AWTFont;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.drawable.TextImageRenderer;

/**
 * Experimental Wraps a Text into a cell with an assigned number of char width.
 * 
 * @author Martin
 *
 */
public class TextCellRenderer extends TextImageRenderer {
  protected static int OFFSET_CONSTANT = 13;

  public TextCellRenderer(int n, String txt, Font font) {
    this(n, txt, font, Horizontal.LEFT, true);
  }

  public TextCellRenderer(int n, String txt, Font font, Horizontal halign, boolean drawBorder) {
    super(txt, AWTFont.toAWT(font));
    this.n = n;
    this.h = halign;
    this.drawBorder = drawBorder;
    this.drawText = true;
    this.borderColor = Color.BLACK;
    this.textColor = Color.BLACK;
  }

  @Override
  public BufferedImageTexture getImage(float ratio) {
    IntegerCoord2d c = guessImageDimension(n, font);
    c.x += OFFSET_CONSTANT;

    BufferedImage img = new BufferedImage(((int) (c.x * ratio)), ((int) (c.y * ratio)),
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) img.getGraphics();
    g.scale(ratio, ratio);

    // background
    g.setColor(backgroundColor);
    g.fillRect(0, 0, c.x, c.y);

    // border
    if (drawBorder) {
      g.setColor(borderColor);
      g.drawRect(0, 0, c.x - 1, c.y - 1);
    }

    if (drawText) {
      // int width =
      // (int)(g.getFontMetrics().getMaxCharBounds(g).getWidth() * n);
      g.setFont(font);
      int width = g.getFontMetrics().stringWidth(text);
      int height = g.getFontMetrics().getHeight();
      g.setColor(textColor);
      if (h == Horizontal.LEFT)
        g.drawString(text, 1, c.y - 1 - height / 5);
      else if (h == Horizontal.RIGHT)
        g.drawString(text, c.x - width, c.y - 1 - height / 5);
      else if (h == Horizontal.CENTER)
        g.drawString(text, c.x / 2 - width / 2, c.y - 1 - height / 5);
    }

    return new BufferedImageTexture(img);
  }

  /***************/

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public Color getTextColor() {
    return textColor;
  }

  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }

  public boolean isBorderDisplayed() {
    return drawBorder;
  }

  public void setBorderDisplayed(boolean drawBorder) {
    this.drawBorder = drawBorder;
  }

  public boolean isTextDisplayed() {
    return drawText;
  }

  public void setTextDisplayed(boolean drawText) {
    this.drawText = drawText;
  }

  public int getCharacterWidth() {
    return n;
  }

  public void setCharacterWidth(int n) {
    this.n = n;
  }

  public Horizontal getHorizontalAlignement() {
    return h;
  }

  public void setHorizontalAlignement(Horizontal h) {
    this.h = h;
  }

  public Coord3d getSceneOffset() {
    return sceneOffset;
  }

  public void setSceneOffset(Coord3d sceneOffset) {
    this.sceneOffset = sceneOffset;
  }

  /***************/

  protected IntegerCoord2d guessImageDimension(int n, java.awt.Font font) {
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();
    g.setFont(font);
    FontMetrics fm = g.getFontMetrics();
    Rectangle2D r = fm.getMaxCharBounds(g);
    return new IntegerCoord2d((int) (n * r.getWidth()), fm.getHeight());
  }

  protected int n;
  protected Horizontal h = Horizontal.LEFT;
  protected Color borderColor = Color.RED;
  protected Color backgroundColor = Color.WHITE;
  protected Color textColor = Color.BLACK;
  protected boolean drawBorder = true;
  protected boolean drawText = true;

  protected Coord3d sceneOffset;
}
