package org.jzy3d.chart.factories;

import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;

/**
 * A PanamaGL Swing chart factory rendering charts with depth peeling.
 * 
 * Drawables are not sorted by the scene's {@link Graph} since depth peeling makes it useless.
 */
public class PanamaGLDepthPeelingChartFactory extends PanamaGLSwingChartFactory {
  public PanamaGLDepthPeelingChartFactory() {
    this(new PanamaGLDepthPeelingPainterFactory());
  }

  public PanamaGLDepthPeelingChartFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }

  @Override
  public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
    Graph graph = super.newGraph(scene, strategy, sort);
    graph.setSort(false);
    return graph;
  }
}
