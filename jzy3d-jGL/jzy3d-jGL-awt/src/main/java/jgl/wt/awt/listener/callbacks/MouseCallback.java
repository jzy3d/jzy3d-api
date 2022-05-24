package jgl.wt.awt.listener.callbacks;

import java.awt.*;

@FunctionalInterface
public interface MouseCallback {

  /**
   * Sets mouse callback for the current window.
   * void glutMouseFunc (void (*func)(int button, int state, int x, int y))
   *
   * @param target Rendering target component
   * @param button Mouse button identifier (GLUT_LEFT_BUTTON, etc.)
   * @param state  GLUT_UP or GLUT_DOWN indicating mouse button state cause
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  void onMouse(Component target, int button, int state, int x, int y);
}
