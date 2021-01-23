package org.jzy3d.plot3d.primitives;

import org.jzy3d.painters.IPainter;

/**
 * A polygon made of two triangles with no wireframe on their adjacent side.
 * 
 * By being an {@link Composite}, this polygon can be decomposed and is thus more flexible with a
 * sorting algorithm.
 * 
 * @author Martin Pernollet
 */
public class TesselatedPolygon extends Composite {
  public TesselatedPolygon(Point[] points) {
    Polygon p1 = newTriangle();
    p1.add(points[0]);
    p1.add(points[1]);
    p1.add(points[2]);
    add(p1);

    Polygon p2 = newTriangle();
    p2.add(points[2]);
    p2.add(points[3]);
    p2.add(points[0]);
    add(p2);
  }

  protected Polygon newTriangle() {
    return new Polygon() {
      @Override
      protected void begin(IPainter painter) {
        painter.glBegin_Triangle();
      }

      /**
       * Override default to use a line strip to draw wire, so that the shared adjacent triangle
       * side is not drawn.
       */
      @Override
      protected void callPointForWireframe(IPainter painter) {
        painter.color(wireframeColor);
        painter.glLineWidth(wireframeWidth);
        painter.glBegin_LineStrip();

        for (Point p : points) {
          painter.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        }
        painter.glEnd();

      }
    };
  }
}
