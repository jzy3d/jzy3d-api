package org.jzy3d.plot3d.text.align;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import org.jzy3d.maths.Coord2d;

public class AWTTextLayout extends TextLayout{

  public Coord2d getBounds(String str, Font font, FontRenderContext frc) {
    return getBounds(font.createGlyphVector(frc, str));
  }

  public Coord2d getBounds(GlyphVector gv) {
    Rectangle2D bounds = gv.getPixelBounds(null, 0, 0);
    return new Coord2d(bounds.getWidth(), bounds.getHeight());
  }

}
