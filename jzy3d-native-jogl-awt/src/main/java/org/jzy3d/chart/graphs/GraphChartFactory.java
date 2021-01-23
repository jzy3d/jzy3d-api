package org.jzy3d.chart.graphs;

import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;

public class GraphChartFactory extends AWTChartFactory {
  public GraphChartFactory() {
    super(new GraphWindowFactory());
  }

  public GraphChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);
  }


}
