package org.jzy3d.chart.controllers.keyboard.camera;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.maths.Coord2d;

/**
 * SWT camera key controller, mirroring {@code AWTCameraKeyController}.
 *
 * Arrow keys rotate; SHIFT + arrows zoom/shift.
 */
public class SWTCameraKeyController extends AbstractCameraController
    implements KeyListener, ICameraKeyController {

  protected RateLimiter rateLimiter;

  public SWTCameraKeyController() {}

  public SWTCameraKeyController(Chart chart) {
    register(chart);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addKeyController(this);
  }

  @Override
  public void dispose() {
    getChart().getCanvas().removeKeyController(this);
    super.dispose();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (rateLimiter != null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    boolean shiftDown = (e.stateMask & SWT.SHIFT) != 0;

    if (!shiftDown) {
      Coord2d move = new Coord2d();
      float offset = 0.1f;
      switch (e.keyCode) {
        case SWT.ARROW_DOWN:
          move.y = move.y + offset;
          rotate(move);
          break;
        case SWT.ARROW_UP:
          move.y = move.y - offset;
          rotate(move);
          break;
        case SWT.ARROW_LEFT:
          move.x = move.x - offset;
          rotate(move);
          break;
        case SWT.ARROW_RIGHT:
          move.x = move.x + offset;
          rotate(move);
          break;
        default:
          break;
      }
    } else {
      switch (e.keyCode) {
        case SWT.ARROW_DOWN:
          shift(0.1f);
          break;
        case SWT.ARROW_UP:
          shift(-0.1f);
          break;
        case SWT.ARROW_LEFT:
          zoomZ(0.9f);
          break;
        case SWT.ARROW_RIGHT:
          zoomZ(1.1f);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }
}
