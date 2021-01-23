package org.jzy3d.plot3d.primitives.graphs;

import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphLayout2d;

public interface IDrawableGraph2d<V, E> {
  public IGraphFormatter<V, E> getGraphFormatter();

  public IGraphLayout2d<V> getGraphLayout();

  public IGraph<V, E> getGraphModel();

  public void setGraphModel(IGraph<V, E> graph);

  public void setGraphModel(IGraph<V, E> graph, PickingSupport picking);

  public void setGraphLayout(IGraphLayout2d<V> mapping);

  public void setGraphFormatter(IGraphFormatter<V, E> formatter);

  public boolean isVertexHighlighted(V v);

  public boolean isEdgeHighlighted(E e);

  public void setVertexHighlighted(V v, boolean nodeDisplayed);

  public void clearHighlighted();
}
