package org.jzy3d.bridge.newt.controllers.keyboard;

import java.awt.Component;
import org.jzy3d.bridge.newt.controllers.AWTToNewtUtilities;
import com.jogamp.newt.event.KeyEvent;

/**
 *
 * @author Nils Hoffmann
 */
public class AWTToNewtKeyListener implements com.jogamp.newt.event.KeyListener {

  private final java.awt.event.KeyListener keyListener;
  private final java.awt.Component source;

  public AWTToNewtKeyListener(Component source, java.awt.event.KeyListener keyListener) {
    this.source = source;
    this.keyListener = keyListener;
  }

  protected java.awt.event.KeyEvent convertEvent(KeyEvent event, int eventId) {
    return new java.awt.event.KeyEvent(source, eventId, event.getWhen(),
        AWTToNewtUtilities.mask(event), AWTToNewtUtilities.mapKeyCode(event), event.getKeyChar());
  }

  @Override
  public void keyPressed(KeyEvent ke) {
    keyListener.keyPressed(convertEvent(ke, java.awt.event.KeyEvent.KEY_PRESSED));
  }

  @Override
  public void keyReleased(KeyEvent ke) {
    keyListener.keyReleased(convertEvent(ke, java.awt.event.KeyEvent.KEY_RELEASED));
  }

}
