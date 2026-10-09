package org.jzy3d.chart.controllers.mouse.picking;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;

/**
 * Pick {@link org.jzy3d.plot3d.primitives.pickable.Pickable} objects when the mouse is pressed on
 * an SWT canvas, and notify listeners of the {@link PickingSupport}.
 */
public class SWTMousePickingController extends AbstractCameraController
    implements MouseListener, IMousePickingController {
  protected PickingSupport picking;
  protected Coord3d prevMouse3d;

  public SWTMousePickingController() {
    super();
    picking = new PickingSupport();
  }

  public SWTMousePickingController(Chart chart) {
    this(chart, PickingSupport.BRUSH_SIZE);
  }

  public SWTMousePickingController(Chart chart, int brushSize) {
    super();
    picking = new PickingSupport(brushSize);
    register(chart);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addMouseController(this);
  }

  @Override
  public void dispose() {
    getChart().getCanvas().removeMouseController(this);
    super.dispose();
  }

  @Override
  public PickingSupport getPickingSupport() {
    return picking;
  }

  @Override
  public void setPickingSupport(PickingSupport picking) {
    this.picking = picking;
  }

  @Override
  public void mouseDown(MouseEvent e) {
    pick(e.x, e.y);
  }

  @Override
  public void mouseUp(MouseEvent e) {}

  @Override
  public void mouseDoubleClick(MouseEvent e) {}

  /**
   * Pick objects at the given mouse coordinates, in pixels from the top left of the canvas. Must be
   * invoked by the thread rendering the canvas.
   */
  public void pick(int x, int y) {
    int yflip = -y + getChart().getCanvas().getRendererHeight();

    View view = getChart().getView();
    prevMouse3d = view.projectMouse(x, yflip);

    Graph graph = getChart().getScene().getGraph();

    // will trigger vertex selection event to those subscribing to PickingSupport
    picking.pickObjects(view.getPainter(), view, graph, new IntegerCoord2d(x, yflip));
  }
}
