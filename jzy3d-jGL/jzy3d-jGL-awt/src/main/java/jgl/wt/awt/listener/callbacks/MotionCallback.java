package jgl.wt.awt.listener.callbacks;

import java.awt.*;

@FunctionalInterface
public interface MotionCallback {

  /**
   * Set the motion callback for the current window (mouse movement within window while one or more mouse buttons are pressed)
   * void glutMotionFunc (void (*func)(int x, int y))
   *
   * @param target Rendering target component
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  void onMotion(Component target, int x, int y);
}
