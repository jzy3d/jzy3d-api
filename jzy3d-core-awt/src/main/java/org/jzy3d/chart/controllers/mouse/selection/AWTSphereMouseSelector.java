package org.jzy3d.chart.controllers.mouse.selection;

import java.awt.Graphics2D;
import java.util.Deque;
import java.util.List;
import org.jzy3d.maths.ConvexHull;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.selectable.SelectableSphere;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class AWTSphereMouseSelector extends AWTAbstractMouseSelector {
  public AWTSphereMouseSelector(SelectableSphere scatter) {
    this.sphere = scatter;
  }

  /** Make projection and match points belonging to selection. */
  @Override
  protected void processSelection(Scene scene, View view, int width, int height) {
    view.project();
    projection = sphere.getProjection();
    sphere.setHighlighted(false);
    for (Coord3d p : projection)
      if (matchRectangleSelection(in, out, p, width, height)) {
        sphere.setHighlighted(true);
      }
  }

  @Override
  protected void drawSelection(Graphics2D g2d, int width, int height) {
    this.width = width;
    this.height = height;

    if (dragging)
      drawRectangle(g2d, in, out);

    if (projection != null && sphere.isHighlighted()) {
      hull = ConvexHull.build2d(projection);
      Coord2d prev = hull.pop();
      Coord2d next;
      g2d.setColor(java.awt.Color.GREEN);
      while (!hull.isEmpty()) {
        next = hull.pop();
        g2d.drawOval((int) prev.getX() - 4, (int) prev.getY() - 4, 8, 8);
        g2d.drawLine((int) prev.getX(), (int) prev.getY(), (int) next.getX(), (int) next.getY());
        prev = next;
      }
      g2d.setColor(java.awt.Color.BLUE);
      for (Coord3d c1 : projection) {
        g2d.drawOval((int) c1.x - 2, (int) c1.y - 2, 4, 4);
      }
    }
  }

  @Override
  public void clearLastSelection() {
    sphere.setHighlighted(false);
    projection = null;
  }

  protected SelectableSphere sphere;
  protected int width;
  protected int height;
  protected List<Coord3d> projection;
  protected Deque<Coord2d> hull;
}
