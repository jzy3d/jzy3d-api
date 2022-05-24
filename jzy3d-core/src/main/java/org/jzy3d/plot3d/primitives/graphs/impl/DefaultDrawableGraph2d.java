package org.jzy3d.plot3d.primitives.graphs.impl;

import org.apache.logging.log4j.LogManager;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.graphs.AbstractDrawableGraph2d;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;
import org.jzy3d.plot3d.transform.Transform;

public class DefaultDrawableGraph2d<V, E> extends AbstractDrawableGraph2d<V, E> {
  public DefaultDrawableGraph2d() {
    super();
    bbox = new BoundingBox3d();
  }

  /*******************************************************/

  @Override
  protected void drawVertices(IPainter painter) {
    painter.glPointSize(formatter.getVertexWidth());
    painter.glBegin_Point();
    for (V v : graph.getVertices()) {
      if (highlights.get(v))
        drawVertexNode(painter, v, layout.get(v), formatter.getHighlightedVertexColor());
      else
        drawVertexNode(painter, v, layout.get(v), formatter.getVertexColor());
    }
    painter.glEnd();
  }

  @Override
  protected void drawVertexLabels(IPainter painter) {
    for (V v : graph.getVertices()) {
      if (highlights.get(v))
        drawVertexLabel(painter, v, layout.get(v), formatter.getHighlightedVertexColor());
      else
        drawVertexLabel(painter, v, layout.get(v), formatter.getVertexLabelColor());
    }
  }

  @Override
  protected void drawEdges(IPainter painter) {
    for (E e : graph.getEdges()) {
      V v1 = graph.getEdgeStartVertex(e);
      V v2 = graph.getEdgeStopVertex(e);
      drawEdge(painter, e, layout.get(v1), layout.get(v2), formatter.getEdgeColor());
    }
  }

  /*******************************************************/

  protected void drawVertexNode(IPainter painter, V v, Coord2d coord, Color color) {
    painter.color(color);
    painter.glVertex3f(coord.x, coord.y, Z);
  }

  protected void drawVertexLabel(IPainter painter, V v, Coord2d coord, Color color) {
    Coord3d textPosition = new Coord3d(coord, Z);
    txt.drawText(painter, Font.Helvetica_12, v.toString(), textPosition, Horizontal.CENTER, Vertical.BOTTOM, color);
  }

  protected void drawEdge(IPainter painter, E e, Coord2d c1, Coord2d c2, Color color) {
    painter.glBegin_LineStrip();
    painter.color(color);
    painter.glVertex3f(c1.x, c1.y, Z);
    painter.glVertex3f(c2.x, c2.y, Z);
    painter.glEnd();
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    LogManager.getLogger(DefaultDrawableGraph2d.class).warn("not implemented");
  }

  @Override
  public void updateBounds() {
    LogManager.getLogger(DefaultDrawableGraph2d.class).warn("not implemented");
  }

}
