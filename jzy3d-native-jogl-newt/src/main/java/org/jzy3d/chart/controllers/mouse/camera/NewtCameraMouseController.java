package org.jzy3d.chart.controllers.mouse.camera;



import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.NewtMouseUtilities;
import org.jzy3d.maths.Coord2d;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;



public class NewtCameraMouseController extends AbstractCameraController implements MouseListener {

  public NewtCameraMouseController() {}

  public NewtCameraMouseController(Chart chart) {
    register(chart);
    addSlaveThreadController(chart.getFactory().newCameraThreadController(chart));
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addMouseController(this);
  }

  @Override
  public void dispose() {
    for (Chart c : targets) {
      c.getCanvas().removeMouseController(this);
    }
    super.dispose();
  }

  /**
   * Handles toggle between mouse rotation/auto rotation: double-click starts the animated rotation,
   * while simple click stops it.
   */
  @Override
  public void mousePressed(MouseEvent e) {
    //
    if (handleSlaveThread(e))
      return;

    prevMouse.x = e.getX();
    prevMouse.y = e.getY();
  }

  public boolean handleSlaveThread(MouseEvent e) {
    if (isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }

  /** Compute shift or rotate */
  @Override
  public void mouseDragged(MouseEvent e) {
    Coord2d mouse = new Coord2d(e.getX(), e.getY());
    // Rotate
    if (isLeftDown(e)) {
      Coord2d move = mouse.sub(prevMouse).div(100);
      rotate(move);
    }
    // Shift
    else if (isRightDown(e)) {
      Coord2d move = mouse.sub(prevMouse);
      if (move.y != 0)
        shift(move.y / 500);
    }

    prevMouse = mouse;
  }

  public static boolean isLeftDown(MouseEvent e) {
    return (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
  }

  public static boolean isRightDown(MouseEvent e) {
    return (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
  }

  public static boolean isDoubleClick(MouseEvent e) {
    return (e.getClickCount() > 1);
  }

  /** Compute zoom */
  @Override
  public void mouseWheelMoved(MouseEvent e) {
    stopThreadController();

    float factor = NewtMouseUtilities.convertWheelRotation(e, 1.0f, 10.0f);
    zoomZ(factor);
  }

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {}
}
