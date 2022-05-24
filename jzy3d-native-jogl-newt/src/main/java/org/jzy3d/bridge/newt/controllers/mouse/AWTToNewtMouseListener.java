package org.jzy3d.bridge.newt.controllers.mouse;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.jzy3d.bridge.newt.controllers.AWTToNewtUtilities;

/**
 * Provides a bridge implementation to map AWT events to NEWT events for easier transition of
 * existing code. Note that it will be more efficient to directly implement the corresponding
 * {@link com.jogamp.newt.event.MouseListener}.
 * 
 * @author Nils Hoffmann
 */
public class AWTToNewtMouseListener implements com.jogamp.newt.event.MouseListener {

  private final java.awt.event.MouseListener mouseListener;
  private final java.awt.event.MouseMotionListener mouseMotionListener;
  private final java.awt.event.MouseWheelListener mouseWheelListener;
  private final java.awt.Component source;

  public AWTToNewtMouseListener(Component source, java.awt.event.MouseListener mouseListener,
      java.awt.event.MouseMotionListener mouseMotionListener,
      java.awt.event.MouseWheelListener mouseWheelListener) {
    this.source = source;
    this.mouseListener = mouseListener;
    this.mouseMotionListener = mouseMotionListener;
    this.mouseWheelListener = mouseWheelListener;
  }

  protected java.awt.event.MouseEvent convertEvent(com.jogamp.newt.event.MouseEvent event, int id) {
    return new java.awt.event.MouseEvent(source, id, event.getWhen(),
        AWTToNewtUtilities.mask(event), event.getX(), event.getY(), event.getClickCount(), false,
        event.getButton());
  }

  protected java.awt.event.MouseWheelEvent convertWheelEvent(com.jogamp.newt.event.MouseEvent event,
      int id) {
    // rotation direction needs to be inverted between NEWT and AWT
    int direction = event.getRotation()[1] > 0 ? -1 : 1;
    int units = Math.round(event.getRotation()[1] * event.getRotationScale());
    return new java.awt.event.MouseWheelEvent(source, id, event.getWhen(),
        AWTToNewtUtilities.mask(event), event.getX(), event.getY(), event.getClickCount(), false,
        MouseWheelEvent.WHEEL_UNIT_SCROLL, units, direction);
  }

  @Override
  public void mouseClicked(com.jogamp.newt.event.MouseEvent me) {
    if (mouseListener != null) {
      mouseListener.mouseClicked(convertEvent(me, MouseEvent.MOUSE_CLICKED));
    }
  }

  @Override
  public void mouseEntered(com.jogamp.newt.event.MouseEvent me) {
    if (mouseListener != null) {
      mouseListener.mouseEntered(convertEvent(me, MouseEvent.MOUSE_ENTERED));
    }
  }

  @Override
  public void mouseExited(com.jogamp.newt.event.MouseEvent me) {
    if (mouseListener != null) {
      mouseListener.mouseExited(convertEvent(me, MouseEvent.MOUSE_EXITED));
    }
  }

  @Override
  public void mousePressed(com.jogamp.newt.event.MouseEvent me) {
    if (mouseListener != null) {
      mouseListener.mousePressed(convertEvent(me, MouseEvent.MOUSE_PRESSED));
    }
  }

  @Override
  public void mouseReleased(com.jogamp.newt.event.MouseEvent me) {
    if (mouseListener != null) {
      mouseListener.mouseReleased(convertEvent(me, MouseEvent.MOUSE_RELEASED));
    }
  }

  @Override
  public void mouseMoved(com.jogamp.newt.event.MouseEvent me) {
    if (mouseMotionListener != null) {
      mouseMotionListener.mouseMoved(convertEvent(me, MouseEvent.MOUSE_MOVED));
    }
  }

  @Override
  public void mouseDragged(com.jogamp.newt.event.MouseEvent me) {
    if (mouseMotionListener != null) {
      mouseMotionListener.mouseDragged(convertEvent(me, MouseEvent.MOUSE_DRAGGED));
    }
  }

  @Override
  public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent me) {
    if (mouseWheelListener != null) {
      mouseWheelListener.mouseWheelMoved(convertWheelEvent(me, MouseEvent.MOUSE_WHEEL));
    }
  }
}
