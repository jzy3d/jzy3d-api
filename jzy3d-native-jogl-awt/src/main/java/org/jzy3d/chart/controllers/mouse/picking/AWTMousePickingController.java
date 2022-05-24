package org.jzy3d.chart.controllers.mouse.picking;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.chart.controllers.thread.camera.AbstractCameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;

public class AWTMousePickingController extends AbstractCameraController
    implements MouseListener, MouseWheelListener, IMousePickingController {
  protected float factor = 1;
  protected float lastInc;
  protected Coord3d mouse3d;
  protected Coord3d prevMouse3d;
  protected PickingSupport picking;

  protected Chart chart;

  protected Coord2d prevMouse;
  protected AbstractCameraThreadController threadController;


  public AWTMousePickingController() {
    super();
    picking = new PickingSupport();
  }

  public AWTMousePickingController(Chart chart) {
    super(chart);
    picking = new PickingSupport();
  }

  public AWTMousePickingController(Chart chart, int brushSize) {
    super(chart);
    picking = new PickingSupport(brushSize);
  }

  public AWTMousePickingController(Chart chart, int brushSize, int bufferSize) {
    super(chart);
    picking = new PickingSupport(brushSize, bufferSize);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    this.chart = chart;
    this.prevMouse = Coord2d.ORIGIN;
    chart.getCanvas().addMouseController(this);
  }

  @Override
  public void dispose() {
    for (Chart c : targets) {
      c.getCanvas().removeMouseController(this);
    }

    if (threadController != null)
      threadController.stop();

    super.dispose(); // i.e. target=null
  }

  /****************/

  @Override
  public PickingSupport getPickingSupport() {
    return picking;
  }

  @Override
  public void setPickingSupport(PickingSupport picking) {
    this.picking = picking;
  }

  /****************/

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  public void mouseDragged(MouseEvent e) {}

  /** Compute zoom */
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (threadController != null)
      threadController.stop();
    float factor = 1 + (e.getWheelRotation() / 10.0f);
    zoomX(factor);
    zoomY(factor);
    chart.getView().shoot();
  }

  public void mouseMoved(MouseEvent e) {
    pick(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (handleSlaveThread(e))
      return;
    pick(e);
  }

  public void pick(MouseEvent e) {
    int yflip = -e.getY() + targets.get(0).getCanvas().getRendererHeight();
    prevMouse.x = e.getX();
    prevMouse.y = e.getY();// yflip;
    View view = targets.get(0).getView();
    prevMouse3d = view.projectMouse(e.getX(), yflip);

    Graph graph = getChart().getScene().getGraph();

    // will trigger vertex selection event to those subscribing to PickingSupport
    picking.pickObjects(chart.getView().getPainter(), view, graph,
        new IntegerCoord2d(e.getX(), yflip));
  }

  public boolean handleSlaveThread(MouseEvent e) {
    if (AWTMouseUtilities.isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }
}
