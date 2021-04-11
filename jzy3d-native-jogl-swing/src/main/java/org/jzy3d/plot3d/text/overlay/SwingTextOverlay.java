package org.jzy3d.plot3d.text.overlay;

import java.awt.Component;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;


public class SwingTextOverlay extends AWTTextOverlay {
  public SwingTextOverlay(ICanvas canvas) {
    super(canvas);
  }

  @Override
  protected void init(ICanvas canvas) {
    if (canvas instanceof CanvasSwing)
      initComponent((Component) canvas);
    else
      super.init(canvas);
  }
}
