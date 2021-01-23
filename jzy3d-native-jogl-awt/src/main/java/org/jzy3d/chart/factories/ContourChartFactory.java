package org.jzy3d.chart.factories;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.ContourAxisBox;
import org.jzy3d.plot3d.rendering.view.View;

public class ContourChartFactory extends AWTChartFactory {
  @Override
  public AxisBox newAxe(BoundingBox3d box, View view) {
    ContourAxisBox axe = new ContourAxisBox(box);
    axe.setView(view);
    return axe;
  }
}
