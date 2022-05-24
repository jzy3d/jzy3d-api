package org.jzy3d.chart.fallback;

import java.awt.GridLayout;
import java.util.Collection;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.ui.views.ImagePanel;

/** A frame to show a list of charts */
public class FallbackChartFrameSwing extends FallbackChartFrameAbstract {
  private static final long serialVersionUID = 7519209038396190502L;

  public FallbackChartFrameSwing(Chart... charts) {
    super(charts);
  }

  public FallbackChartFrameSwing(Collection<? extends Chart> charts) {
    super(charts);
  }

  protected void setLayout(Collection<? extends Chart> charts) {
    setLayout(new GridLayout(charts.size(), 1));

    for (Chart c : charts) {
      addChartLayout(c);
    }
  }


  protected void addChartLayout(Chart chart) {
    if (chart instanceof FallbackChart) {
      ImagePanel chartPanel = ((FallbackChart) chart).getImagePanel();// new ImagePanel();

      // Register image panel to chart changes
      FallbackChartFactory.bind(chartPanel, (AWTChart) chart);
      FallbackChartFactory.addPanelSizeChangedListener(chartPanel, chart);

      add(chartPanel);

    } else {
      throw new IllegalArgumentException("Expecting a FallbackChart instance");
    }
  }
}
