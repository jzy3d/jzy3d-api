package org.jzy3d.plot3d.primitives.graphs;

import java.util.HashMap;
import java.util.Map;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.graphs.layout.DefaultGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphLayout2d;
import org.jzy3d.plot3d.primitives.pickable.Pickable;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;


/**
 * An implementation based on {@link Pickable} objects should register mapping between vertex model
 * and vertex representation through {@link PickSupport.registerPickableObject(IPickable pickable, V
 * v)}
 * 
 * Registration should be done by overriding setGraphModel.
 * 
 * One can later listen to picking events through:
 * {@link PickSupport.addVertexPickListener(IVertexPickListener<V> listener)}
 */
public abstract class AbstractDrawableGraph2d<V, E> extends Drawable
    implements IDrawableGraph2d<V, E> {
  public AbstractDrawableGraph2d() {
    super();
    formatter = new DefaultGraphFormatter<V, E>();
  }

  @Override
  public IGraphFormatter<V, E> getGraphFormatter() {
    return formatter;
  }

  @Override
  public IGraphLayout2d<V> getGraphLayout() {
    return layout;
  }

  @Override
  public IGraph<V, E> getGraphModel() {
    return graph;
  }

  @Override
  public void setGraphFormatter(IGraphFormatter<V, E> formatter) {
    this.formatter = formatter;
  }

  @Override
  public void setGraphLayout(IGraphLayout2d<V> layout) {
    this.layout = layout;
    bbox.reset();
    for (Coord2d c : layout.values())
      bbox.add(c.x, c.y, Z);
  }

  @Override
  public void setGraphModel(IGraph<V, E> graph, PickingSupport picking) {
    setGraphModel(graph);
  }

  @Override
  public void setGraphModel(IGraph<V, E> graph) {
    this.graph = graph;
    for (V v : graph.getVertices())
      highlights.put(v, false);
  }

  /*******************************************************/

  @Override
  public void draw(IPainter painter) {
    if (layout == null)
      throw new RuntimeException("missing vertex mapping");

    doTransform(painter);

    // TODO move to graph view init
    painter.glEnable_PointSmooth();// (GL2ES1.GL_POINT_SMOOTH);
    painter.glHint_PointSmooth_Nicest();
    // painter.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);

    if (formatter.areEdgesDisplayed())
      drawEdges(painter);
    if (formatter.areVerticesDisplayed())
      drawVertices(painter);
    if (formatter.areVertexLabelsDisplayed())
      drawVertexLabels(painter);
  }

  protected abstract void drawVertices(IPainter painter);

  protected abstract void drawVertexLabels(IPainter painter);

  protected abstract void drawEdges(IPainter painter);

  /*******************************************************/

  public Coord2d getLabelScreenOffset() {
    return labelScreenOffset;
  }

  public void setLabelScreenOffset(Coord2d labelOffset) {
    this.labelScreenOffset = labelOffset;
  }

  public Coord3d getLabelSceneOffset() {
    return labelSceneOffset;
  }

  public void setLabelSceneOffset(Coord3d labelSceneOffset) {
    this.labelSceneOffset = labelSceneOffset;
  }

  /*******************************************************/
  // HIGHLIGHTS

  @Override
  public boolean isEdgeHighlighted(E e) {
    return false;
  }

  @Override
  public boolean isVertexHighlighted(V v) {
    return highlights.get(v);
  }

  @Override
  public void setVertexHighlighted(V v, boolean nodeDisplayed) {
    highlights.put(v, nodeDisplayed);
  }

  @Override
  public void clearHighlighted() {
    for (@SuppressWarnings("unused")
    Boolean h : highlights.values())
      h = true;
  }

  protected Map<V, Boolean> highlights = new HashMap<V, Boolean>();

  /*******************************************************/

  protected IGraph<V, E> graph;
  protected IGraphFormatter<V, E> formatter;
  protected IGraphLayout2d<V> layout;

  protected Coord2d labelScreenOffset;
  protected Coord3d labelSceneOffset;
  protected static float Z = 0;


  protected TextBitmapRenderer txt = new TextBitmapRenderer();
  // protected TextRenderer txtRenderer;


}
