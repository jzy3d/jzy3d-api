package org.jzy3d.chart.controllers.keyboard.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.maths.Coord2d;



public class AWTCameraKeyController extends AbstractCameraController
    implements KeyListener, ICameraKeyController {

  protected RateLimiter rateLimiter;

  public AWTCameraKeyController() {}

  public AWTCameraKeyController(Chart chart) {
    register(chart);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addKeyController(this);
  }

  @Override
  public void dispose() {
    for (Chart c : targets) {
      c.getCanvas().removeKeyController(this);
    }

    super.dispose(); // i.e. target=null
  }

  /*********************************************************/

  @Override
  public void keyPressed(KeyEvent e) {
    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if(rateLimiter!=null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    // rotation
    if (!e.isShiftDown()) {
      Coord2d move = new Coord2d();
      float offset = 0.1f;
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN:
          move.y = move.y + offset;
          rotate(move);
          break;
        case KeyEvent.VK_UP:
          move.y = move.y - offset;
          rotate(move);
          break;
        case KeyEvent.VK_LEFT:
          move.x = move.x - offset;
          rotate(move);
          break;
        case KeyEvent.VK_RIGHT:
          move.x = move.x + offset;
          rotate(move);
          break;
        default:
          break;
      }
    }
    // zoom
    else {
      switch (e.getKeyCode()) {
        // shift
        case KeyEvent.VK_DOWN:
          shift(0.1f);
          break;
        case KeyEvent.VK_UP:
          shift(-0.1f);
          break;
        // zoom
        case KeyEvent.VK_LEFT:
          zoomZ(0.9f);
          break;
        case KeyEvent.VK_RIGHT:
          zoomZ(1.1f);
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}

  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }
  
  
}
