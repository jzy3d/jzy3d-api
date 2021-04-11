package org.jzy3d.plot3d.text.drawable;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;

public class TextImageRenderer {
  protected static int OFFSET_CONSTANT = 13;

  public TextImageRenderer(String txt, Font font) {
    this.text = txt;
    this.font = font;

  }

  public BufferedImageTexture getImage() {
    return getImage(1);
  }

  public BufferedImageTexture getImage(float ratio) {
    IntegerCoord2d c = guessImageDimension(text);
    c.x += OFFSET_CONSTANT;

    BufferedImage img = new BufferedImage(((int) (c.x * ratio)), ((int) (c.y * ratio)),
        BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();
    // g.drawRect(0, 0, c.x-1, c.y-1);
    g.setFont(font);
    g.drawString(text, 0, c.y - 1);
    return new BufferedImageTexture(img);
  }

  public IntegerCoord2d guessImageDimension(String text) {
    BufferedImage img = new BufferedImage(100, 10, BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();
    FontMetrics fm = g.getFontMetrics();
    return new IntegerCoord2d(fm.stringWidth(text), fm.getHeight());
  }

  protected String text;
  protected Font font;
}
