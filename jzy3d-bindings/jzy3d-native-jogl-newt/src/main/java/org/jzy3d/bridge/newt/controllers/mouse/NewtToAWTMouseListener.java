package org.jzy3d.bridge.newt.controllers.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.MouseListener;
import jogamp.newt.awt.event.AWTNewtEventFactory;

/**
 * @author cmh
 */
public class NewtToAWTMouseListener implements java.awt.event.MouseListener,
    java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {

  private final com.jogamp.newt.event.MouseListener mouseListener;
  private final com.jogamp.newt.Window source;

  public NewtToAWTMouseListener(Window source, MouseListener mouseListener) {
    this.source = source;
    this.mouseListener = mouseListener;
  }

  protected com.jogamp.newt.event.MouseEvent convertEvent(java.awt.event.MouseEvent event) {
    return AWTNewtEventFactory.createMouseEvent(event, source);
  }

  protected com.jogamp.newt.event.MouseEvent convertEvent(java.awt.event.MouseWheelEvent event) {
    return AWTNewtEventFactory.createMouseEvent(event, source);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    mouseListener.mouseWheelMoved(convertEvent(e));
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouseListener.mouseDragged(convertEvent(e));
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    mouseListener.mouseMoved(convertEvent(e));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    mouseListener.mouseClicked(convertEvent(e));
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseListener.mousePressed(convertEvent(e));
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mouseListener.mouseReleased(convertEvent(e));
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    mouseListener.mouseEntered(convertEvent(e));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    mouseListener.mouseExited(convertEvent(e));
  }

}
