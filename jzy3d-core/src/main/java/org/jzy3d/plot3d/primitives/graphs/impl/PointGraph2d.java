package org.jzy3d.plot3d.primitives.graphs.impl;

import java.util.HashMap;
import java.util.Map;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.pickable.PickablePoint;

public class PointGraph2d<V, E> extends DefaultDrawableGraph2d<V, E> {
  public PointGraph2d() {
    super();
    bbox = new BoundingBox3d();
    labelScreenOffset = new Coord2d(0, -2);
  }

  @Override
  public void setGraphModel(IGraph<V, E> graph, PickingSupport picking) {
    super.setGraphModel(graph, picking);

    for (V v : graph.getVertices()) {
      PickablePoint p = newPoint(v);
      p.setWidth(formatter.getVertexWidth());
      vertexObjects.put(v, p);
      picking.registerDrawableObject(p, v);
    }
  }

  @Override
  public void setGraphModel(IGraph<V, E> graph) {
    super.setGraphModel(graph);

    for (V v : graph.getVertices()) {
      PickablePoint p = newPoint(v);
      p.setWidth(formatter.getVertexWidth());
      vertexObjects.put(v, p);
    }
  }

  protected PickablePoint newPoint(V vertex) {
    PickablePoint p =
        new PickablePoint(Coord3d.ORIGIN, formatter.getVertexColor(), formatter.getVertexWidth());
    return p;
  }

  /*******************************************************/

  @Override
  protected void drawVertices(IPainter painter) {
    for (V v : graph.getVertices()) {
      if (highlights.get(v))
        drawVertexNode(painter, v, layout.get(v), formatter.getHighlightedVertexColor());
      else
        drawVertexNode(painter, v, layout.get(v), formatter.getVertexColor());
    }
  }

  @Override
  protected void drawVertexNode(IPainter painter, V v, Coord2d coord, Color color) {
    PickablePoint pt = vertexObjects.get(v);
    pt.setData(new Coord3d(coord, Z));
    pt.setColor(color);
    pt.draw(painter);
  }

  /*******************************************************/


  protected Map<V, PickablePoint> vertexObjects = new HashMap<V, PickablePoint>();
}
