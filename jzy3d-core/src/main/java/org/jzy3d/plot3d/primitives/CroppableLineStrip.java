package org.jzy3d.plot3d.primitives;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;

public class CroppableLineStrip extends LineStrip implements Croppable {
  boolean[] filter; // true = show, false = hide

  @Override
  public void filter(BoundingBox3d bounds) {
    filter = new boolean[points.size()];
    for (int i = 0; i < filter.length; i++)
      filter[i] = bounds.contains(points.get(i).xyz);
  }

  @Override
  public void resetFilter() {
    filter = null;
  }

  @Override
  public void drawLine(IPainter painter) {
    painter.glBegin_LineStrip();

    painter.glLineWidth(wireframeWidth);

    if (filter == null)
      doDrawAllLines(painter);
    else
      doDrawLinesFiltered(painter);
    painter.glEnd();
  }

  @Override
  public void drawPoints(IPainter painter) {
    painter.glBegin_Point();

    if (filter == null)
      doDrawAllPoints(painter);
    else
      doDrawPointsFiltered(painter);
    painter.glEnd();
  }

  private void doDrawAllLines(IPainter painter) {
    if (wireframeColor == null) {
      for (Point p : points) {
        painter.color(p.rgb);
        painter.vertex(p.xyz, spaceTransformer);
      }
    } else {
      for (Point p : points) {
        painter.color(wireframeColor);
        painter.vertex(p.xyz, spaceTransformer);
      }
    }
  }

  private void doDrawLinesFiltered(IPainter painter) {
    for (int i = 0; i < filter.length; i++) {
      if (filter[i]) {
        Point p = points.get(i);
        if (wireframeColor == null)
          painter.color(p.rgb);
        else
          painter.color(wireframeColor);
        painter.vertex(p.xyz, spaceTransformer);
      }
    }
  }

  private void doDrawAllPoints(IPainter painter) {
    for (Point p : points) {
      if (wireframeColor == null)
        painter.color(p.rgb);
      else
        painter.color(wireframeColor);
      painter.vertex(p.xyz, spaceTransformer);
    }
  }

  private void doDrawPointsFiltered(IPainter painter) {
    for (int i = 0; i < filter.length; i++) {
      if (filter[i]) {
        Point p = points.get(i);
        if (wireframeColor == null)
          painter.color(p.rgb);
        else
          painter.color(wireframeColor);
        painter.vertex(p.xyz, spaceTransformer);
      }
    }
  }

}
