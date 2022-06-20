package org.jzy3d.plot3d.rendering.view.layout;

import org.jzy3d.chart.Chart;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.view.View;

public interface IViewportLayout {
  public void update(Chart chart);
  public void render(IPainter painter, Chart chart);
}
