package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

public class ConcurrentLineStrip extends LineStrip {

  public ConcurrentLineStrip() {
    super(100);
  }

  public ConcurrentLineStrip(int n) {
    super(n);
  }

  public ConcurrentLineStrip(List<Coord3d> coords) {
    super(coords);
  }

  public ConcurrentLineStrip(Point c1, Point c2) {
    super(c1, c2);
  }

  @Override
  public void drawLine(IPainter painter) {
    painter.glLineWidth(wireframeWidth);
    // gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    // gl.glPolygonOffset(1.0f, 1.0f);
    synchronized (points) {
      super.drawLine(painter);
    }

  }

  @Override
  public void drawPointsIfEnabled(IPainter painter) {
    synchronized (points) {
      super.drawPointsIfEnabled(painter);
    }
  }

  /* */

  @Override
  public void applyGeometryTransform(Transform transform) {
    synchronized (points) {
      for (Point p : points) {
        p.xyz = transform.compute(p.xyz);
      }
    }
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    synchronized (points) {
      for (Point p : points)
        bbox.add(p);
    }
  }

  @Override
  public void add(Point point) {
    synchronized (points) {
      points.add(point);
    }
    bbox.add(point);
  }

  @Override
  public void clear() {
    synchronized (points) {
      points.clear();
    }
    updateBounds();
  }

  @Override
  public Point getLastPoint() {
    synchronized (points) {
      return super.getLastPoint();
    }
  }
}
