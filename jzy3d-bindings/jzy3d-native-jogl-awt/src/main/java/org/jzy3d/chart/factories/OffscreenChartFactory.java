package org.jzy3d.chart.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffscreenChartFactory extends AWTChartFactory {
  static Logger logger = LoggerFactory.getLogger(OffscreenChartFactory.class);

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
