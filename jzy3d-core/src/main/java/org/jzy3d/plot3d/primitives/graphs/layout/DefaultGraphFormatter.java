package org.jzy3d.plot3d.primitives.graphs.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;

public class DefaultGraphFormatter<V, E> implements IGraphFormatter<V, E> {
  public DefaultGraphFormatter() {
    vertexLabelColor = Color.BLACK;
    vertexColor = Color.BLACK;
    vertexWidth = 10;

    highlightedVertexLabelColor = Color.RED;
    highlightedVertexColor = Color.RED;
    highlightedVertexWidth = 10;

    edgeLabelColor = Color.BLACK;
    edgeColor = Color.BLACK;
    edgeWidth = 1;

    highlightedEdgeLabelColor = Color.RED;
    highlightedEdgeColor = Color.RED;
    highlightedEdgeWidth = 2;
  }

  public DefaultGraphFormatter(Color normal, Color highlight) {
    vertexLabelColor = normal;
    vertexColor = normal;
    vertexWidth = 10;

    highlightedVertexLabelColor = highlight;
    highlightedVertexColor = highlight;
    highlightedVertexWidth = 10;

    edgeLabelColor = normal;
    edgeColor = normal;
    edgeWidth = 1;

    highlightedEdgeLabelColor = highlight;
    highlightedEdgeColor = highlight;
    highlightedEdgeWidth = 2;

    vertexLabelHalign = Horizontal.CENTER;
    vertexLabelValign = Vertical.BOTTOM;
  }


  @Override
  public Color getVertexLabelColor() {
    return vertexLabelColor;
  }

  @Override
  public void setVertexLabelColor(Color vertexLabelColor) {
    this.vertexLabelColor = vertexLabelColor;
  }

  @Override
  public Color getVertexColor() {
    return vertexColor;
  }

  @Override
  public void setVertexColor(Color vertexColor) {
    this.vertexColor = vertexColor;
  }

  @Override
  public int getVertexWidth() {
    return vertexWidth;
  }

  @Override
  public void setVertexWidth(int vertexWidth) {
    this.vertexWidth = vertexWidth;
  }

  @Override
  public Color getHighlightedVertexLabelColor() {
    return highlightedVertexLabelColor;
  }

  @Override
  public void setHighlightedVertexLabelColor(Color highlightedVertexLabelColor) {
    this.highlightedVertexLabelColor = highlightedVertexLabelColor;
  }

  @Override
  public Color getHighlightedVertexColor() {
    return highlightedVertexColor;
  }

  @Override
  public void setHighlightedVertexColor(Color highlightedVertexColor) {
    this.highlightedVertexColor = highlightedVertexColor;
  }

  @Override
  public int getHighlightedVertexWidth() {
    return highlightedVertexWidth;
  }

  @Override
  public void setHighlightedVertexWidth(int highlightedVertexWidth) {
    this.highlightedVertexWidth = highlightedVertexWidth;
  }

  @Override
  public Color getEdgeLabelColor() {
    return edgeLabelColor;
  }

  @Override
  public void setEdgeLabelColor(Color edgeLabelColor) {
    this.edgeLabelColor = edgeLabelColor;
  }

  @Override
  public Color getEdgeColor() {
    return edgeColor;
  }

  @Override
  public void setEdgeColor(Color edgeColor) {
    this.edgeColor = edgeColor;
  }

  @Override
  public int getEdgeWidth() {
    return edgeWidth;
  }

  @Override
  public void setEdgeWidth(int edgeWidth) {
    this.edgeWidth = edgeWidth;
  }

  @Override
  public Color getHighlightedEdgeLabelColor() {
    return highlightedEdgeLabelColor;
  }

  @Override
  public void setHighlightedEdgeLabelColor(Color highlightedEdgeLabelColor) {
    this.highlightedEdgeLabelColor = highlightedEdgeLabelColor;
  }

  @Override
  public Color getHighlightedEdgeColor() {
    return highlightedEdgeColor;
  }

  @Override
  public void setHighlightedEdgeColor(Color highlightedEdgeColor) {
    this.highlightedEdgeColor = highlightedEdgeColor;
  }

  @Override
  public int getHighlightedEdgeWidth() {
    return highlightedEdgeWidth;
  }

  @Override
  public void setHighlightedEdgeWidth(int highlightedEdgeWidth) {
    this.highlightedEdgeWidth = highlightedEdgeWidth;
  }

  @Override
  public boolean areVerticesDisplayed() {
    return nodeDisplayed;
  }

  @Override
  public void setVerticesDisplayed(boolean nodeDisplayed) {
    this.nodeDisplayed = nodeDisplayed;
  }

  @Override
  public boolean areVertexLabelsDisplayed() {
    return nodeLabelDisplayed;
  }

  @Override
  public void setVertexLabelsDisplayed(boolean nodeLabelDisplayed) {
    this.nodeLabelDisplayed = nodeLabelDisplayed;
  }

  @Override
  public boolean areEdgesDisplayed() {
    return edgeDisplayed;
  }

  @Override
  public void setEdgesDisplayed(boolean edgeDisplayed) {
    this.edgeDisplayed = edgeDisplayed;
  }

  @Override
  public Horizontal getVertexLabelHalign() {
    return vertexLabelHalign;
  }

  @Override
  public void setVertexLabelHalign(Horizontal vertexLabelHalign) {
    this.vertexLabelHalign = vertexLabelHalign;
  }

  @Override
  public Vertical getVertexLabelValign() {
    return vertexLabelValign;
  }

  @Override
  public void setVertexLabelValign(Vertical vertexLabelValign) {
    this.vertexLabelValign = vertexLabelValign;
  }


  protected Color vertexLabelColor;
  protected Color vertexColor;
  protected int vertexWidth;

  protected Color highlightedVertexLabelColor;
  protected Color highlightedVertexColor;
  protected int highlightedVertexWidth;

  protected Color edgeLabelColor;
  protected Color edgeColor;
  protected int edgeWidth;

  protected Color highlightedEdgeLabelColor;
  protected Color highlightedEdgeColor;
  protected int highlightedEdgeWidth;

  protected boolean nodeDisplayed = true;
  protected boolean nodeLabelDisplayed = true;
  protected boolean edgeDisplayed = true;

  protected Horizontal vertexLabelHalign;
  protected Vertical vertexLabelValign;
}
