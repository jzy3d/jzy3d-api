package org.jzy3d.plot3d.primitives.selectable;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Polygon2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * A Scatter that supports an "highlighted status" to change selected point color
 * 
 * @author Martin Pernollet
 * 
 */
public class SelectableScatter extends Scatter implements ISingleColorable, Selectable {
  public SelectableScatter(Coord3d[] coordinates, Color[] colors) {
    super(coordinates, colors);
  }

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    painter.glPointSize(width);
    painter.glBegin_Point();

    if (colors == null)
      painter.color(rgb);

    if (coordinates != null) {
      int k = 0;
      for (Coord3d c : coordinates) {
        if (colors != null) {
          if (isHighlighted[k]) // Selection coloring goes here
            painter.color(highlightColor);
          else
            painter.color(colors[k]);
          k++;
        }

        painter.vertex(c, spaceTransformer);
      }
    }
    painter.glEnd();

  }

  @Override
  public void project(IPainter painter, Camera cam) {
    projection = cam.modelToScreen(painter, getData());
  }

  public Coord3d[] getProjection() {
    return projection;
  }

  public Color getHighlightColor() {
    return highlightColor;
  }

  public void setHighlightColor(Color highlightColor) {
    this.highlightColor = highlightColor;
  }

  public void setHighlighted(int id, boolean value) {
    isHighlighted[id] = value;
  }

  public boolean getHighlighted(int id) {
    return isHighlighted[id];
  }

  public void resetHighlighting() {
    this.isHighlighted = new boolean[coordinates.length];
  }

  /* */

  /**
   * Set the coordinates of the point.
   * 
   * @param xyz point's coordinates
   */
  @Override
  public void setData(Coord3d[] coordinates) {
    this.coordinates = coordinates;
    this.isHighlighted = new boolean[coordinates.length];

    bbox.reset();
    for (Coord3d c : coordinates)
      bbox.add(c);
  }

  @Override
  public Coord3d[] getData() {
    return coordinates;
  }

  @Override
  public Polygon2d getHull2d() {
    throw new RuntimeException("not implemented");
  }

  @Override
  public List<Coord3d> getLastProjection() {
    throw new RuntimeException("not implemented");
  }

  /**********************************************************************/

  protected boolean[] isHighlighted;
  protected Color highlightColor = Color.RED.clone();

  protected Coord3d[] projection;

}
