package org.jzy3d.tests.manual.layout;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;

public class MTest_Layout_EmulGL {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    MTest_Layout d = new MTest_Layout();
    d.setFactory(new EmulGLChartFactory());
    d.init();

    Chart chart = d.getChart();
    chart.open(800, 600);
    
    DoubleScreenManualScenario scenario = new DoubleScreenManualScenario(chart, MTest_Layout_EmulGL.class.getSimpleName());
    scenario.start();
  }
}
