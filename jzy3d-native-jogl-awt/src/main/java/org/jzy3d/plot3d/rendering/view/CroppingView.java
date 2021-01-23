package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.Croppable;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class CroppingView extends AWTView {
  public CroppingView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }

  @Override
  public void setBoundManual(BoundingBox3d bounds) {
    super.setBoundManual(bounds);
    filter = bounds;
    applyCropFilter();
  }

  @Override
  public void renderSceneGraph(boolean light) {
    synchronized (scene.getGraph()) {
      super.renderSceneGraph(light);
    }
  }

  private void applyCropFilter() {
    synchronized (scene.getGraph()) {
      for (Drawable d : scene.getGraph().getAll()) {
        if (d instanceof Croppable) {
          Croppable c = (Croppable) d;
          c.filter(filter);
        }
      }
    }
  }

  BoundingBox3d filter;
}
