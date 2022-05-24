package org.jzy3d.chart.fallback;

import java.awt.Dimension;
import java.util.Collection;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import net.miginfocom.swing.MigLayout;

/** A frame to show a list of charts */
public class FallbackChartFrameMiglayout extends FallbackChartFrameAbstract {
  private static final long serialVersionUID = 7519209038396190502L;

  public FallbackChartFrameMiglayout(Chart... charts) {
    super(charts);
  }

  public FallbackChartFrameMiglayout(Collection<? extends Chart> charts) {
    super(charts);
  }

  protected void setLayout(Collection<? extends Chart> charts) {
    // setLayout(new GridLayout(charts.size(), 1));

    String lines = "[100%]";
    String columns = "[100%]";
    setLayout(new MigLayout("", columns, lines));

    for (Chart c : charts) {
      addChartLayout(c);
    }
  }

  int id = 0;

  protected void addChartLayout(Chart chart) {
    if (chart instanceof IFallbackChart) {
      FallbackChartImagePanel chartPanel = ((IFallbackChart) chart).getImagePanel();// new
                                                                                    // ImagePanel();
      chartPanel.setMinimumSize(new Dimension(0, 0));
      chartPanel.setMaximumSize(new Dimension(10000, 10000));

      // Debug border
      Border b = BorderFactory.createLineBorder(java.awt.Color.black);
      chartPanel.setBorder(b);

      // Register image panel to chart changes
      FallbackChartFactory.bind(chartPanel, (AWTChart) chart);
      FallbackChartFactory.addPanelSizeChangedListener(chartPanel, chart);

      add(chartPanel, "cell 0 " + (id++) + ", grow");
    } else {
      throw new IllegalArgumentException("Expecting a FallbackChart instance");
    }
  }
}
