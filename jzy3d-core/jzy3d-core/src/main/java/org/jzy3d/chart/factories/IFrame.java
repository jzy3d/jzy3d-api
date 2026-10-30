package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;

public interface IFrame {

  void initialize(Chart chart, Rectangle bounds, String title);

  void initialize(Chart chart, Rectangle bounds, String title, String message);

}
