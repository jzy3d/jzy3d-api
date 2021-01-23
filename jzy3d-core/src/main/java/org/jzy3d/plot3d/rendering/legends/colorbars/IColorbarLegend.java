package org.jzy3d.plot3d.rendering.legends.colorbars;

import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.IPainter;

public interface IColorbarLegend {
  public void render(IPainter painter);

  public void drawableChanged(DrawableChangedEvent e);

  public Dimension getMinimumSize();

  public void setMinimumSize(Dimension dimension);
}
