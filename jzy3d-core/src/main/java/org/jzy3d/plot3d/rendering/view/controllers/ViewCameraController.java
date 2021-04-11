package org.jzy3d.plot3d.rendering.view.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.jzy3d.chart.controllers.ControllerType;
import org.jzy3d.events.ControllerEvent;
import org.jzy3d.events.ControllerEventListener;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.view.View;


/** Supports a View as target, instead of a chart. */
public abstract class ViewCameraController {
  public ViewCameraController() {
    targets = new ArrayList<View>();
  }

  public void addTarget(View target) {
    targets.add(target);
  }

  public void removeTarget(View target) {
    targets.remove(target);
  }

  public void dispose() {
    targets.clear();
  }

  /***************************************************************************/

  protected void rotate(final Coord2d move) {
    for (View view : targets)
      view.rotate(move);
    fireControllerEvent(ControllerType.ROTATE, move);
  }

  protected void shift(final float factor) {
    for (View view : targets)
      view.shift(factor);
    fireControllerEvent(ControllerType.SHIFT, factor);
  }

  protected void zoom(final float factor) {
    for (View view : targets)
      view.zoom(factor);
    fireControllerEvent(ControllerType.ZOOM, factor);
  }

  /*************************************************************/

  public void addControllerEventListener(ControllerEventListener listener) {
    controllerListeners.add(listener);
  }

  public void removeControllerEventListener(ControllerEventListener listener) {
    controllerListeners.remove(listener);
  }

  protected void fireControllerEvent(ControllerType type, Object value) {
    ControllerEvent e = new ControllerEvent(this, type, value);
    for (ControllerEventListener listener : controllerListeners)
      listener.controllerEventFired(e);
  }

  /*************************************************************/

  protected List<View> targets;
  protected Vector<ControllerEventListener> controllerListeners =
      new Vector<ControllerEventListener>(1);
}
