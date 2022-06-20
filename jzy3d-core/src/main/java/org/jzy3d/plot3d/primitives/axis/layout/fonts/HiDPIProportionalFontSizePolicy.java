package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
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

  protected boolean apply_WindowsHiDPI_Workaround = false;

  public HiDPIProportionalFontSizePolicy(View view) {
    this.view = view;
  }
  
  public Font getBaseFont() {
    return baseFont;
  }

  public void setBaseFont(Font baseFont) {
    this.baseFont = baseFont;
  }



  /**
   * Modifies the {@link IAxisLayout} font according to the pixel scale returned by the {@link View}
   * and the font that was returned by {@link IAxisLayout} at the first call to this method.
   */
  @Override
  public Font apply(AxisLayout layout) {
    // Fix base font if not defined
    if (baseFont == null)
      baseFont = layout.getFont().clone();
    Font font = baseFont.clone();

    // Scale base font
    Coord2d scale = view.getPixelScale();

    if (apply_WindowsHiDPI_Workaround) {
      IPainter painter = view.getPainter();

      // Workaround for https://github.com/jzy3d/jogl/issues/8
      if (painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT() && painter.getCanvas().isNative()) {
        // We here scale the viewport by either 1 or by the ratio indicated by the JVM
        // if only the JVM is able to detect the pixel ratio and if JOGL
        // can't guess it (which is the case for Windows 10).
        Coord2d scaleHardware = painter.getCanvas().getPixelScale();
        Coord2d scaleJVM = painter.getCanvas().getPixelScaleJVM();

        // System.out.println("HiDPI : " + isHiDPIEnabled);
        System.out.println("GPU   : " + scaleHardware);
        System.out.println("JVM   : " + scaleJVM);

        if (painter.isJVMScaleLargerThanNativeScale(scaleHardware, scaleJVM)) {

          scale = scaleJVM.div(scaleHardware);
          
          System.out.println("Processed scale" + scale.y);
        }
      }
    }

    // Only if scale known
    if (!Float.isNaN(scale.getY())) {

      int height = Math.round(font.getHeight() * scale.getY());
      font.setHeight(height);
    }

    //System.out.println("HiDPIPropPolicy : " + font.getHeight());
    
    // Set and return
    layout.setFont(font);
    return layout.getFont();
  }
}
