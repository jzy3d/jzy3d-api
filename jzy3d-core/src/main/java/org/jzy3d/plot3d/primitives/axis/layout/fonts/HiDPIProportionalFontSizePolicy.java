package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/**
 * This is the greatest {@link IFontSizePolicy} since it scale base font with current pixel scale.
 * 
 * This is only suitable for EmulGL chart, as Native chart often rely on {@link TextBitmapRenderer}
 * which does not support more font than {@link Font} defaults. Using a {@link JOGLTextRenderer}
 * instead allows more font size and styles.
 * 
 * If base font has not been set, it will be based on default {@link IAxisLayout#getFont()}
 * 
 * @author martin
 */
public class HiDPIProportionalFontSizePolicy implements IFontSizePolicy {
  protected View view;
  protected Font baseFont;

  public HiDPIProportionalFontSizePolicy(View view) {
    this.view = view;
  }

  @Override
  public Font apply(IAxisLayout layout) {
    // Fix base font if not defined
    if (baseFont == null)
      baseFont = layout.getFont().clone();
    Font font = baseFont.clone();

    // Scale base font
    Coord2d scale = view.getPixelScale();
    font.setHeight((int) (font.getHeight() * scale.getY()));

    // Set and return
    layout.setFont(font);
    return layout.getFont();
  }

  public Font getBaseFont() {
    return baseFont;
  }

  public void setBaseFont(Font baseFont) {
    this.baseFont = baseFont;
  }


}
