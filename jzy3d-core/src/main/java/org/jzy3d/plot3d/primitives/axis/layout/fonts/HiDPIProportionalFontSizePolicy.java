package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * This is the greatest {@link IFontSizePolicy} since it scale base font with current pixel scale.
 * 
 * This is mainly useful to keep the same text size visually when a chart is moved from a HiDPI
 * screen to a non HiDPI screen.
 * 
 * The base font is based on {@link IAxisLayout#getFont()}.
 * 
 * @author Martin Pernollet
 */
public class HiDPIProportionalFontSizePolicy implements IFontSizePolicy {
  protected View view;
  protected Font baseFont;

  public HiDPIProportionalFontSizePolicy(View view) {
    this.view = view;
  }

  /**
   * Modifies the {@link IAxisLayout} font according to the pixel scale returned by the {@link View}
   * and the font that was returned by {@link IAxisLayout} at the first call to this method.
   */
  @Override
  public Font apply(IAxisLayout layout) {
    // Fix base font if not defined
    if (baseFont == null)
      baseFont = layout.getFont().clone();
    Font font = baseFont.clone();

    // Scale base font
    Coord2d scale = view.getPixelScale();

    if (!Float.isNaN(scale.getY())) {
      int height = (int) (font.getHeight() * scale.getY());
      font.setHeight(height);
    }

    // Set and return
    layout.setFont(font);
    return layout.getFont();
  }
}
