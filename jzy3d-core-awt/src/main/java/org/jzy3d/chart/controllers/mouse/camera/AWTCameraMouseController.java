package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.mouse.AWTMouseUtilities;
import org.jzy3d.maths.Coord2d;


public class AWTCameraMouseController extends AbstractCameraController
    implements MouseListener, MouseWheelListener, MouseMotionListener {

  protected RateLimiter rateLimiter;

  public AWTCameraMouseController() {}

  public AWTCameraMouseController(Chart chart) {
    register(chart);
    addSlaveThreadController(chart.getFactory().newCameraThreadController(chart));
  }
  
  public AWTCameraMouseController(Chart chart, RateLimiter limiter) {
    this(chart);
    setRateLimiter(limiter);
  }
  
  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addMouseController(this);
  }

  @Override
  public void dispose() {
    for (Chart chart : targets) {
      chart.getCanvas().removeMouseController(this);
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

    prevMouse.x = x(e);
    prevMouse.y = y(e);
  }



  /** Compute shift or rotate */
  @Override
  public void mouseDragged(MouseEvent e) {
    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if(rateLimiter!=null && !rateLimiter.rateLimitCheck()) {
      return;
    }

    // Apply mouse drag
    Coord2d mouse = xy(e);

    // Rotate if left button down
    if (AWTMouseUtilities.isLeftDown(e)) {
      Coord2d move = mouse.sub(prevMouse).div(100);
      rotate(move);
    }

    // Shift if right button down
    else if (AWTMouseUtilities.isRightDown(e)) {
      Coord2d move = mouse.sub(prevMouse);
      if (move.y != 0)
        shift(move.y / 500);
    }
    prevMouse = mouse;
  }

  /** Compute zoom */
  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    // Check if mouse rate limiter wish to forbid this mouse drag instruction
    if(rateLimiter!=null && !rateLimiter.rateLimitCheck()) {
      return;
    }
    
    stopThreadController();
    float factor = 1 + (e.getWheelRotation() / 10.0f);
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

  protected boolean handleSlaveThread(MouseEvent e) {
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

  protected Coord2d xy(MouseEvent e) {
    return new Coord2d(x(e), y(e));
  }

  protected int y(MouseEvent e) {
    return e.getY();
  }

  protected int x(MouseEvent e) {
    return e.getX();
  }
}
