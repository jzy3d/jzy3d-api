package org.jzy3d.plot3d.text.renderers.jogl.style;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import com.jogamp.opengl.util.awt.TextRenderer;

public class GradientTextStyle implements TextRenderer.RenderDelegate {
  private float gradientSize;
  private Color color1;
  private Color color2;

  public GradientTextStyle(float gradientSize, Color color1, Color color2) {
    this.gradientSize = gradientSize;
    this.color1 = color1;
    this.color2 = color2;
  }

  @Override
  public boolean intensityOnly() {
    return false;
  }

  @Override
  public Rectangle2D getBounds(CharSequence str, Font font, FontRenderContext frc) {
    return getBounds(str.toString(), font, frc);
  }

  @Override
  public Rectangle2D getBounds(String str, Font font, FontRenderContext frc) {
    return getBounds(font.createGlyphVector(frc, str), frc);
  }

  @Override
  public Rectangle2D getBounds(GlyphVector gv, FontRenderContext frc) {
    Rectangle2D stringBounds = gv.getPixelBounds(frc, 0, 0);
    return new Rectangle2D.Double(stringBounds.getX(), stringBounds.getY(), stringBounds.getWidth(),
        stringBounds.getHeight());
  }

  @Override
  public void drawGlyphVector(Graphics2D graphics, GlyphVector str, int x, int y) {
    graphics.setColor(Color.WHITE);
    graphics.setPaint(new GradientPaint(x, y, color1, x, y + gradientSize / 2, color2, true));
    graphics.drawGlyphVector(str, x, y);
  }

  @Override
  public void draw(Graphics2D graphics, String str, int x, int y) {
    graphics.setColor(Color.WHITE);
    graphics.setPaint(new GradientPaint(x, y, color1, x, y + gradientSize / 2, color2, true));
    graphics.drawString(str, x, y);
  }
}
