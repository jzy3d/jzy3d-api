package org.jzy3d.chart.controllers.mouse.camera;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.SWTMouseUtilities;
import org.jzy3d.maths.Coord2d;

/**
 * SWT camera mouse controller, modeled on {@code AWTCameraMouseController} but kept to the 3D
 * camera essentials (rotate / shift / zoom).
 *
 * <p>The AWT controller's 2D selection rendering relies on {@code AWTView.addRenderer2d} +
 * {@code AWTGraphicsUtils} which are not available on the PanamaGL/SWT pipeline, so this
 * controller does not embed a 2D rectangle selection. Add your own SWT overlay if needed.
 */
public class SWTCameraMouseController extends AbstractCameraController
    implements MouseListener, MouseMoveListener, MouseWheelListener {

  protected RateLimiter rateLimiter;
  protected Coord2d prevMouse = Coord2d.ORIGIN;

  public SWTCameraMouseController() {}

  public SWTCameraMouseController(Chart chart) {
    register(chart);
    addThread(chart.getFactory().newCameraThreadController(chart));
  }

  public SWTCameraMouseController(Chart chart, RateLimiter limiter) {
    this(chart);
    setRateLimiter(limiter);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addMouseController(this);
  }

  public void unregister(Chart chart) {
    chart.getCanvas().removeMouseController(this);
    super.unregister(chart);
  }

  @Override
  public void dispose() {
    unregister(target);
    super.dispose();
  }

  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  // ----------------------------------------------------------------------------
  // MOUSE EVENT LISTENERS
  // ----------------------------------------------------------------------------

  @Override
  public void mouseDown(MouseEvent e) {
    prevMouse.x = e.x;
    prevMouse.y = e.y;

    if (getChart().getView().is3D()) {
      handleSlaveThread(e);
    }
  }

  @Override
  public void mouseUp(MouseEvent e) {
    // no-op: 2D selection is not supported by this controller
  }

  @Override
  public void mouseDoubleClick(MouseEvent e) {
    // SWT also fires mouseDown with count==2, so the slave-thread toggle runs there.
    // Doing it again here would immediately stop the rotation we just started.
  }

  @Override
  public void mouseMove(MouseEvent e) {
    // SWT delivers mouseMove for both hovering and dragging — we treat the held-button case as
    // a drag, mirroring the AWT MouseMotionListener.mouseDragged path.
    if (!SWTMouseUtilities.isLeftDown(e) && !SWTMouseUtilities.isRightDown(e)) {
      return;
    }

    if (rateLimiter != null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    Coord2d mouse = new Coord2d(e.x, e.y);

    if (getChart().getView().is3D()) {
      if (SWTMouseUtilities.isLeftDown(e)) {
        Coord2d move = mouse.sub(prevMouse).div(100);
        rotate(move);
      } else if (SWTMouseUtilities.isRightDown(e)) {
        Coord2d move = mouse.sub(prevMouse);
        if (move.y != 0) {
          shift(move.y / 500);
        }
      }
    }

    prevMouse = mouse;
  }

  @Override
  public void mouseScrolled(MouseEvent e) {
    if (getChart().getView().is2D()) {
      return;
    }
    if (rateLimiter != null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    stopThreadController();

    // SWT.MouseWheel: e.count > 0 means scroll up (zoom in), e.count < 0 means scroll down.
    // Match the AWT 1-tick-per-step convention by collapsing to the sign of e.count.
    int wheel = (e.count > 0) ? -1 : (e.count < 0) ? 1 : 0;
    if (wheel == 0) {
      return;
    }
    float factor = 1 + (wheel / 10.0f);
    zoomZ(factor);
  }

  // ----------------------------------------------------------------------------
  // SLAVE THREAD
  // ----------------------------------------------------------------------------

  /**
   * Toggle the auto-rotation slave thread the same way as {@code AWTCameraMouseController}:
   * a double-click starts it, any other click stops it. Returns {@code true} if the thread was
   * just started, {@code false} otherwise (stopped or no controller bound).
   */
  protected boolean handleSlaveThread(MouseEvent e) {
    if (SWTMouseUtilities.isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null) {
      threadController.stop();
    }
    return false;
  }
}
