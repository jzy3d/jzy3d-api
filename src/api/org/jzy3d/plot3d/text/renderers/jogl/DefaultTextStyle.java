package org.jzy3d.plot3d.text.renderers.jogl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

public class DefaultTextStyle implements ITextStyle {
	protected Color color;

	public DefaultTextStyle(Color color) {
		this.color = color;
	}

	public boolean intensityOnly() {
		return false;
	}

	public Rectangle2D getBounds(CharSequence str, Font font,
			FontRenderContext frc) {
		return getBounds(str.toString(), font, frc);
	}

	public Rectangle2D getBounds(String str, Font font, FontRenderContext frc) {
		return getBounds(font.createGlyphVector(frc, str), frc);
	}

	public Rectangle2D getBounds(GlyphVector gv, FontRenderContext frc) {
		Rectangle2D stringBounds = gv.getPixelBounds(frc, 0, 0);
		return new Rectangle2D.Double(stringBounds.getX(), stringBounds.getY(), stringBounds.getWidth(), stringBounds.getHeight());
	}

	public void drawGlyphVector(Graphics2D graphics, GlyphVector str, int x,
			int y) {
		graphics.setColor(Color.WHITE);
		graphics.setPaint(/*new GradientPaint(x, y, color, x, y + gradientSize
				/ 2, color2, true)*/color);
		graphics.drawGlyphVector(str, x, y);
	}

	public void draw(Graphics2D graphics, String str, int x, int y) {
		graphics.setColor(Color.WHITE);
		graphics.setPaint(/*new GradientPaint(x, y, color1, x, y + gradientSize
				/ 2, color2, true)*/color);
		graphics.drawString(str, x, y);
	}
}
