package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Reset the {@link IAxisLayout} font to {@link IAxisLayout#getFont(HiDPI)}.
 * 
 * @author martin
 */
public class HiDPITwoFontSizesPolicy implements IFontSizePolicy{
  protected View view;
  
  protected Font fontNoHiDPI = Font.Helvetica_12;
  protected Font fontHiDPI = Font.Helvetica_18;

  
  public HiDPITwoFontSizesPolicy(View view) {
    this.view = view;
  }

  @Override
  public Font apply(IAxisLayout layout) {
    HiDPI hidpi = view.getHiDPI();
    
    
    Font selected = HiDPI.ON.equals(hidpi)?fontHiDPI:fontNoHiDPI;
 
    if(selected==null)
      selected = layout.getFont();

    layout.setFont(selected.clone());
    
    return layout.getFont();
  }

  
  
  public Font getFontNoHiDPI() {
    return fontNoHiDPI;
  }

  public void setFontNoHiDPI(Font fontNoHiDPI) {
    this.fontNoHiDPI = fontNoHiDPI;
  }

  public Font getFontHiDPI() {
    return fontHiDPI;
  }

  public void setFontHiDPI(Font fontHiDPI) {
    this.fontHiDPI = fontHiDPI;
  }
  
  
}
