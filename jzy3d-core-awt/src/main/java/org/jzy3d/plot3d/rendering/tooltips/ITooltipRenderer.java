package org.jzy3d.plot3d.rendering.tooltips;

import java.awt.Graphics2D;
import org.jzy3d.maths.IntegerCoord2d;


public interface ITooltipRenderer {
  public void render(Graphics2D g2d);

  public void updateScreenPosition(IntegerCoord2d position);
}
