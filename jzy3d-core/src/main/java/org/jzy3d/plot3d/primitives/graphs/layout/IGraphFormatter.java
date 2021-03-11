package org.jzy3d.plot3d.primitives.graphs.layout;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.text.align.Horizontal;
import org.jzy3d.plot3d.text.align.Vertical;

public interface IGraphFormatter<V, E> {

  public Color getVertexLabelColor();

  public void setVertexLabelColor(Color vertexLabelColor);

  public Color getVertexColor();

  public void setVertexColor(Color vertexColor);

  public int getVertexWidth();

  public void setVertexWidth(int vertexWidth);

  public Color getHighlightedVertexLabelColor();

  public void setHighlightedVertexLabelColor(Color highlightedVertexLabelColor);

  public Color getHighlightedVertexColor();

  public void setHighlightedVertexColor(Color highlightedVertexColor);

  public int getHighlightedVertexWidth();

  public void setHighlightedVertexWidth(int highlightedVertexWidth);

  public Color getEdgeLabelColor();

  public void setEdgeLabelColor(Color edgeLabelColor);

  public Color getEdgeColor();

  public void setEdgeColor(Color edgeColor);

  public int getEdgeWidth();

  public void setEdgeWidth(int edgeWidth);

  public Color getHighlightedEdgeLabelColor();

  public void setHighlightedEdgeLabelColor(Color highlightedEdgeLabelColor);

  public Color getHighlightedEdgeColor();

  public void setHighlightedEdgeColor(Color highlightedEdgeColor);

  public int getHighlightedEdgeWidth();

  public void setHighlightedEdgeWidth(int highlightedEdgeWidth);

  public boolean areVerticesDisplayed();

  public void setVerticesDisplayed(boolean nodeDisplayed);

  public boolean areVertexLabelsDisplayed();

  public void setVertexLabelsDisplayed(boolean nodeLabelDisplayed);

  public boolean areEdgesDisplayed();

  public void setEdgesDisplayed(boolean edgeDisplayed);

  public abstract void setVertexLabelValign(Vertical vertexLabelValign);

  public abstract Vertical getVertexLabelValign();

  public abstract void setVertexLabelHalign(Horizontal vertexLabelHalign);

  public abstract Horizontal getVertexLabelHalign();

}
