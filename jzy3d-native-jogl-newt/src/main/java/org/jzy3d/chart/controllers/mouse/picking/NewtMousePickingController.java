package org.jzy3d.chart.controllers.mouse.picking;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.NewtMouseUtilities;
import org.jzy3d.chart.controllers.thread.camera.AbstractCameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;

public class NewtMousePickingController extends AbstractCameraController
    implements MouseListener, IMousePickingController {
  public NewtMousePickingController() {
    super();
    picking = new PickingSupport();
  }

  public NewtMousePickingController(Chart chart) {
    super(chart);
    picking = new PickingSupport();
  }

  public NewtMousePickingController(Chart chart, int brushSize) {
    super(chart);
    picking = new PickingSupport(brushSize);
  }

  public NewtMousePickingController(Chart chart, int brushSize, int bufferSize) {
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

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.chart.controllers.mouse.picking.MousePickingController#getPickingSupport()
   */

  @Override
  public PickingSupport getPickingSupport() {
    return picking;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jzy3d.chart.controllers.mouse.picking.MousePickingController#setPickingSupport(org.jzy3d.
   * picking.PickingSupport)
   */
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

  @Override
  public void mouseDragged(MouseEvent e) {}

  /** Compute zoom */
  @Override
  public void mouseWheelMoved(MouseEvent e) {
    if (threadController != null)
      threadController.stop();
    float factor = NewtMouseUtilities.convertWheelRotation(e, 1.0f, 10.0f);
    zoomX(factor);
    zoomY(factor);
    chart.getView().shoot();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    pick(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (handleSlaveThread(e))
      return;
    pick(e);
  }

  protected void pick(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    pick(x, y);
  }

  protected void pick(int x, int y) {
    int yflip = -y + targets.get(0).getCanvas().getRendererHeight();
    prevMouse.x = x;
    prevMouse.y = y;// yflip;
    View view = targets.get(0).getView();
    prevMouse3d = view.projectMouse(x, yflip);

    GL gl = ((NativeDesktopPainter) chart.getView().getPainter()).getCurrentGL(chart.getCanvas());

    Graph graph = getChart().getScene().getGraph();

    // will trigger vertex selection event to those subscribing to
    // PickingSupport.
    picking.pickObjects(chart.getView().getPainter(), view, graph, new IntegerCoord2d(x, yflip));
  }

  public boolean handleSlaveThread(MouseEvent e) {
    if (NewtMouseUtilities.isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }

  /**********************/

  protected float factor = 1;
  protected float lastInc;
  protected Coord3d mouse3d;
  protected Coord3d prevMouse3d;
  protected PickingSupport picking;
  protected GLU glu = new GLU();

  protected Chart chart;

  protected Coord2d prevMouse;
  protected AbstractCameraThreadController threadController;

}
