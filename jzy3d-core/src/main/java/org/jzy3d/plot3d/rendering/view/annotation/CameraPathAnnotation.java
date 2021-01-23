package org.jzy3d.plot3d.rendering.view.annotation;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Record the camera position at regular interval and draws the path based on these point
 * accumulation.
 * 
 * @author Martin
 */
public class CameraPathAnnotation extends LineStrip {
  public CameraPathAnnotation(View view, Color color) {
    super();
    this.view = view;
    setWireframeColor(color);
    setWidth(3);
    timer.tic();
  }

  @Override
  public void draw(IPainter painter) {
    updateCameraPath();

    super.draw(painter);
  }

  public void updateCameraPath() {
    if (timer.toc() > 0.1) {
      Coord3d scaling = view.getLastViewScaling();
      Coord3d xyz = view.getCamera().getEye().div(scaling);
      Point p = new Point(xyz, getWireframeColor());
      add(p); // should synchronize
      timer.tic();
    }
  }

  protected View view;
  protected TicToc timer = new TicToc();
}
