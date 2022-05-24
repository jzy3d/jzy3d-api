package jgl.wt.awt.listener.callbacks;

import java.awt.*;

@FunctionalInterface
public interface KeyboardCallback {

  /**
   * Sets the keyboard callback for the current window.
   * void glutKeyboardFunc (void (*func)(unsigned char key, int x, int y))
   *
   * @param target Rendering target component
   * @param key    Generated ASCII character (i.e. 'a' or 'A' depending on SHIFT key)
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  void onKeyboard(Component target, char key, int x, int y);
}
