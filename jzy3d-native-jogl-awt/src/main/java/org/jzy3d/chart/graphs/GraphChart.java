package org.jzy3d.chart.graphs;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class GraphChart extends Chart {
  public GraphChart(Quality quality) {
    super(new GraphChartFactory(), quality);
  }
  /*
   * protected ChartScene initializeScene(boolean graphsort){ return new ChartScene(graphsort){
   * protected GraphView initializeChartView(Scene scene, ICanvas canvas, Quality quality){ return
   * new GraphView(scene, canvas, quality); } }; }
   */
}
