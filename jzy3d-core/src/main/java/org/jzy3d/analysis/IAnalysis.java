package org.jzy3d.analysis;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public interface IAnalysis {
  public String getName();

  public String getPitch();

  public Chart getChart() throws Exception;

  public void init() throws Exception;

  public boolean isInitialized();

  public boolean hasOwnChartControllers();

  /** Use a factory to initialize a chart instance */
  public Chart initializeChart();

  public Chart initializeChart(Quality quality);


  public IChartFactory getFactory();

  public void setFactory(IChartFactory factory);
}
