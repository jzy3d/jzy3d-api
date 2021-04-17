package org.jzy3d.javafx.controllers.mouse;


import java.awt.event.MouseWheelEvent;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.chart.controllers.thread.camera.AbstractCameraThreadController;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class JavaFXMousePickingController extends AbstractCameraController
    implements EventHandler<MouseEvent>, JavaFXChartController, IMousePickingController {
  protected Node node;

  public JavaFXMousePickingController() {
    super();
    picking = new PickingSupport();
  }

  public JavaFXMousePickingController(Chart chart) {
    super(chart);
    chart.getCanvas().addMouseController(this);

    picking = new PickingSupport();
  }

  public JavaFXMousePickingController(Chart chart, int brushSize) {
    super(chart);
    chart.getCanvas().addMouseController(this);
    picking = new PickingSupport(brushSize);
  }

  public JavaFXMousePickingController(Chart chart, int brushSize, int bufferSize) {
    super(chart);
    chart.getCanvas().addMouseController(this);
    picking = new PickingSupport(brushSize, bufferSize);
  }


  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public void setNode(Node node) {
    register(node);
  }

  private void register(Node node) {
    this.node = node;

    if (node == null)
      return;

    node.setOnMousePressed(this);
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
  public void handle(MouseEvent arg0) {
    // TODO Auto-generated method stub
    pick(arg0);
  }


  /** Compute zoom */
  // @Override
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

  // @Override
  public void mousePressed(MouseEvent e) {
    if (handleSlaveThread(e))
      return;
    pick(e);
  }

  public void pick(MouseEvent e) {
    int yflip = -(int) e.getY() + targets.get(0).getCanvas().getRendererHeight();
    prevMouse.x = (int) e.getX();
    prevMouse.y = (int) e.getY();// yflip;
    View view = targets.get(0).getView();
    prevMouse3d = view.projectMouse((int) e.getX(), yflip);

    GL gl = ((NativeDesktopPainter) chart.getView().getPainter()).getCurrentGL(chart.getCanvas());

    Graph graph = getChart().getScene().getGraph();

    // will trigger vertex selection event to those subscribing to
    // PickingSupport.
    picking.pickObjects(chart.getView().getPainter(), view, graph,
        new IntegerCoord2d((int) e.getX(), yflip));
  }

  public boolean handleSlaveThread(MouseEvent e) {
    /*
     * if (AWTMouseUtilities.isDoubleClick(e)) { if (threadController != null) {
     * threadController.start(); return true; } } if (threadController != null)
     * threadController.stop();
     */
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
