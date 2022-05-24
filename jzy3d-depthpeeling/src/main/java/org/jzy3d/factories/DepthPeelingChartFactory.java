package org.jzy3d.factories;

import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;

/**
 * The sole purpose of this factory is to disable sorting drawable by scene's {@link Graph} since
 * depth peeling make it useless. This is only to improve performances.
 * 
 * Using this factory is not a requirement. It remains possible to enable depth peeling with any native chart factory, e.g. :
 * 
 * <code>
 * AWTChartFactory f = new AWTChartFactory(new DepthPeelingPainterFactory());
 * Chart chart = f.newChart();
 * </code>
 */
public class DepthPeelingChartFactory extends AWTChartFactory {
  public DepthPeelingChartFactory() {
    this(new DepthPeelingPainterFactory());
  }

  public DepthPeelingChartFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }

  @Override
  public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
    Graph graph = super.newGraph(scene, strategy, sort);
    graph.setSort(false);
    return graph;
  }
}
