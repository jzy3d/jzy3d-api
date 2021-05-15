package org.jzy3d.chart2d;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;

public class View2d extends AWTView {
  public View2d(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }

  /**
   * TODO : verify we override this and why we do not override {@link #computeSceneScaling(Scene, boolean, org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode, org.jzy3d.maths.BoundingBox3d, org.jzy3d.plot3d.transform.space.SpaceTransformer)}
   */
  @Override
  public Coord3d computeSceneScaling() {
    return squarify();
  }
}
