package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;

public class ColoredWireframePolygon extends Polygon {


  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    if (mapper != null)
      mapper.preDraw(this);

    // Draw content of polygon
    if (faceDisplayed) {
      painter.glPolygonMode(polygonMode, PolygonFill.FILL);

      if (wireframeDisplayed && polygonOffsetFillEnable)
        polygonOffsetFillEnable(painter);
      callPointsForFace(painter);
      if (wireframeDisplayed && polygonOffsetFillEnable)
        polygonOffsetFillDisable(painter);
    }

    // Draw edge of polygon
    if (wireframeDisplayed) {
      painter.glPolygonMode(polygonMode, PolygonFill.LINE);

      if (polygonOffsetFillEnable)
        polygonOffsetLineEnable(painter);
      callPointForWireframe(painter);
      if (polygonOffsetFillEnable)
        polygonOffsetLineDisable(painter);
    }

    if (mapper != null)
      mapper.postDraw(this);

    doDrawBoundsIfDisplayed(painter);
  }



  @Override
  public void callPointForWireframe(IPainter painter) {
    painter.glLineWidth(wireframeWidth);
    Color c = wireframeColor;

    begin(painter);
    for (Point p : points) {
      if (mapper != null) {
        c = mapper.getColor(p.getCoord().z);
        painter.color(c);
      }
      painter.vertex(p.xyz, spaceTransformer);
    }
    painter.glEnd();
  }

}
