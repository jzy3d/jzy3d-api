package org.jzy3d.chart.factories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OffscreenChartFactory extends AWTChartFactory {
  static Logger logger = LogManager.getLogger(OffscreenChartFactory.class);

  int width;
  int height;

  public OffscreenChartFactory() {
    this(800,600);
  }
  
  public OffscreenChartFactory(int width, int height) {
    super(new OffscreenWindowFactory());
    this.width = width;
    this.height = height;
    
    getPainterFactory().setOffscreen(width, height);
  }
}
