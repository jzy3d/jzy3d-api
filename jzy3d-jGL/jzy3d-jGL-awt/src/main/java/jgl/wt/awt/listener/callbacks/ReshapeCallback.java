package jgl.wt.awt.listener.callbacks;

import java.awt.*;

@FunctionalInterface
public interface ReshapeCallback {

  /**
   * Sets the reshape callback for the current window (triggered when window dimensions are changed)
   * void glutReshapeFunc (void (*func)(int width, int height))
   *
   * @param target Rendering target component
   * @param width  New width of window, px
   * @param height New height of window, px
   */
  void onReshape(Component target, int width, int height);
}
