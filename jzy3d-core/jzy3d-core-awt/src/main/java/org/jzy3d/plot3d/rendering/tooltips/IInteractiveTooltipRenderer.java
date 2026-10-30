package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Rectangle;

public interface IInteractiveTooltipRenderer extends ITooltipRenderer {
  public Rectangle getLastBounds();
}
