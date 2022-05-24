package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;

public class ConcurrentLineStripSplitted extends ConcurrentLineStrip {
  List<Integer> idOff = new ArrayList<Integer>();

  public ConcurrentLineStripSplitted() {
    super(100);
  }

  public ConcurrentLineStripSplitted(int n) {
    super(n);
  }

  public ConcurrentLineStripSplitted(List<Coord3d> coords) {
    super(coords);
  }

  public ConcurrentLineStripSplitted(Point c1, Point c2) {
    super(c1, c2);
  }

  @Override
  public void drawLine(IPainter painter) {
    painter.glLineWidth(wireframeWidth);

    if (wireframeColor == null) {
      drawLineSegmentsByPointColor(painter);
    } else {
      drawLineSegmentsByWireColor(painter);
    }
  }

  public void drawLineSegmentsByPointColor(IPainter painter) {
    int nPt = 0;
    int nOff = 0;
    int nextOff = idOff.size() != 0 ? idOff.get(nOff) : 0;

    painter.glBegin_LineStrip();

    while (nPt <= points.size() - 1) {
      // point
      Point p = points.get(nPt);
      pointColorSelf(painter, p);

      // consume off
      if (nextOff == nPt) {
        painter.glEnd();
        painter.glBegin_LineStrip();

        nOff++;
        if (nOff <= idOff.size() - 1)
          nextOff = idOff.get(nOff);
      }
      nPt++;
    }

    painter.glEnd();
  }

  public void drawLineSegmentsByWireColor(IPainter painter) {
    int nPt = 0;
    int nOff = 0;
    int nextOff = -1;
    if (idOff.size() > 0)
      nextOff = idOff.get(nOff);

    painter.glBegin_LineStrip();

    while (nPt <= points.size() - 1) {
      // point
      Point p = points.get(nPt);
      pointColorWire(painter, p);

      // consume off
      if (nextOff == nPt) {
        painter.glEnd();
        painter.glBegin_LineStrip();

        nOff++;
        if (nOff != -1 && nOff <= idOff.size() - 1)
          nextOff = idOff.get(nOff);
      }
      nPt++;
    }
    painter.glEnd();
  }

  public void pointColorWire(IPainter painter, Point p) {
    painter.color(wireframeColor);
    painter.vertex(p.xyz, spaceTransformer);
  }

  public void pointColorSelf(IPainter painter, Point p) {
    painter.color(p.rgb);
    painter.vertex(p.xyz, spaceTransformer);
  }

  /* */

  public void addAndSplit(Point point) {
    synchronized (points) {
      idOff.add(points.size());
      points.add(point);

    }
    bbox.add(point);
  }

  @Override
  public void add(Point point) {
    synchronized (points) {
      points.add(point);
    }
    bbox.add(point);
  }
}
