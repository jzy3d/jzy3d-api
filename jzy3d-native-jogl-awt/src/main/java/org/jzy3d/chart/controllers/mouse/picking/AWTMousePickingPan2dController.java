package org.jzy3d.chart.controllers.mouse.picking;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.ControllerType;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.View;

public class AWTMousePickingPan2dController extends AWTMousePickingController {
  public AWTMousePickingPan2dController() {
    super();
  }

  public AWTMousePickingPan2dController(Chart chart) {
    super(chart);
  }

  public AWTMousePickingPan2dController(Chart chart, int brushSize) {
    super(chart, brushSize);
  }

  public AWTMousePickingPan2dController(Chart chart, int brushSize, int bufferSize) {
    super(chart, brushSize, bufferSize);
  }

  /****************/

  @Override
  public void mouseDragged(MouseEvent e) {
    int yflip = -e.getY() + targets.get(0).getCanvas().getRendererHeight();
    Coord2d mouse = new Coord2d(e.getX(), yflip);
    View view = targets.get(0).getView();
    Coord3d thisMouse3d = view.projectMouse(e.getX(), yflip);

    // 1/2 pan for cleaner rendering
    if (!done) {
      pan(prevMouse3d, thisMouse3d);
      done = true;
    } else
      done = false;
    prevMouse = mouse;
    prevMouse3d = thisMouse3d;
  }

  protected boolean done;

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    lastInc = (e.getWheelRotation() / 10.0f);
    factor = factor + lastInc;

    View view = targets.get(0).getView();
    mouse3d = view.projectMouse(lastMouseX, lastMouseY);

    zoom(1 + lastInc);
  }


  /**********************/

  protected void zoom(final float factor) {
    Chart chart = targets.get(0);
    BoundingBox3d viewBounds = chart.getView().getBounds();
    BoundingBox3d newBounds = viewBounds.scale(new Coord3d(factor, factor, 1));
    chart.getView().setBoundManual(newBounds);
    chart.getView().shoot();

    fireControllerEvent(ControllerType.ZOOM, factor);
  }

  protected void pan(Coord3d from, Coord3d to) {
    Chart chart = targets.get(0);

    BoundingBox3d viewBounds = chart.getView().getBounds();
    Coord3d offset = to.sub(from).div(-PAN_FACTOR);
    BoundingBox3d newBounds = viewBounds.shift(offset);
    chart.getView().setBoundManual(newBounds);
    chart.getView().shoot();

    fireControllerEvent(ControllerType.PAN, offset);
  }

  protected static float PAN_FACTOR = 0.25f;

  protected int lastMouseX = 0;
  protected int lastMouseY = 0;

  /**********************/
}
