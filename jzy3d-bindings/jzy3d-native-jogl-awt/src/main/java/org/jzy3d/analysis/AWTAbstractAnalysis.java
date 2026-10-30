package org.jzy3d.analysis;

import org.jzy3d.chart.factories.AWTChartFactory;

public abstract class AWTAbstractAnalysis extends AbstractAnalysis {
  public AWTAbstractAnalysis() {
    super(new AWTChartFactory());
  }

}
