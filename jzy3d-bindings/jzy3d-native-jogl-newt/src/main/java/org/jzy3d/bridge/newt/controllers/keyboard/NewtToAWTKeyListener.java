package org.jzy3d.bridge.newt.controllers.keyboard;

import com.jogamp.newt.Window;
import jogamp.newt.awt.event.AWTNewtEventFactory;

/**
 *
 * @author cmh
 */
public class NewtToAWTKeyListener implements java.awt.event.KeyListener {

  private final com.jogamp.newt.event.KeyListener keyListener;
  private final com.jogamp.newt.Window source;

  public NewtToAWTKeyListener(Window source, com.jogamp.newt.event.KeyListener keyListener) {
    this.source = source;
    this.keyListener = keyListener;
  }

  protected com.jogamp.newt.event.KeyEvent convertEvent(java.awt.event.KeyEvent event) {
    return AWTNewtEventFactory.createKeyEvent(event, source);
  }

  @Override
  public void keyPressed(java.awt.event.KeyEvent ke) {
    keyListener.keyPressed(convertEvent(ke));
  }

  @Override
  public void keyReleased(java.awt.event.KeyEvent ke) {
    keyListener.keyReleased(convertEvent(ke));
  }

  /**
   * This method will throw a RuntimeException since 'keyType' is no longer supported in JOGL 2.1.
   * 
   * @deprecated
   * @param ke
   * @throws RuntimeException
   */
  @Deprecated
  @Override
  public void keyTyped(java.awt.event.KeyEvent ke) throws RuntimeException {
    throw new RuntimeException(
        "'keyTyped' can not be mapped to NEWT's event model. Please use 'keyReleased' instead!");
  }

}
