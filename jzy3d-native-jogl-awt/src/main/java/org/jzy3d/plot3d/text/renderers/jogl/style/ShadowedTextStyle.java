package org.jzy3d.plot3d.text.renderers.jogl.style;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import com.jogamp.opengl.util.awt.TextRenderer;

public class ShadowedTextStyle implements TextRenderer.RenderDelegate {
  private float gradientSize;
  private int dropShadowDepth;
  private Color color1;
  private Color color2;

  public ShadowedTextStyle(float gradientSize, int dropShadowDepth, Color color1, Color color2) {
    this.gradientSize = gradientSize;
    this.dropShadowDepth = dropShadowDepth;
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
    return new Rectangle2D.Double(stringBounds.getX(), stringBounds.getY(),
        stringBounds.getWidth() + dropShadowDepth, stringBounds.getHeight() + dropShadowDepth);
  }

  @Override
  public void drawGlyphVector(Graphics2D graphics, GlyphVector str, int x, int y) {
    // shadow
    graphics.setColor(DROP_SHADOW_COLOR);
    graphics.drawGlyphVector(str, x + dropShadowDepth, y + dropShadowDepth);

    // normal
    graphics.setColor(Color.WHITE);
    graphics.setPaint(new GradientPaint(x, y, color1, x, y + gradientSize / 2, color2, true));
    graphics.drawGlyphVector(str, x, y);
  }

  @Override
  public void draw(Graphics2D graphics, String str, int x, int y) {
    // shadow
    graphics.setColor(DROP_SHADOW_COLOR);
    graphics.drawString(str, x + dropShadowDepth, y + dropShadowDepth);

    // normal
    graphics.setColor(Color.WHITE);
    graphics.setPaint(new GradientPaint(x, y, color1, x, y + gradientSize / 2, color2, true));
    graphics.drawString(str, x, y);
  }

  protected static final Color DROP_SHADOW_COLOR = new Color(0, 0, 0, 0.5f);
}
