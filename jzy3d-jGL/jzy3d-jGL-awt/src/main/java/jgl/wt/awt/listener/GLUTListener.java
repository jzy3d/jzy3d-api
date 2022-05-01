package jgl.wt.awt.listener;

import java.awt.*;

public interface GLUTListener {

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
  default void onMouse(Component target, int button, int state, int x, int y) {
  }

  /**
   * Set the motion callback for the current window (mouse movement within window while one or more mouse buttons are pressed)
   * void glutMotionFunc (void (*func)(int x, int y))
   *
   * @param target Rendering target component
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  default void onMotion(Component target, int x, int y) {
  }

  /**
   * Sets the keyboard callback for the current window.
   * void glutKeyboardFunc (void (*func)(unsigned char key, int x, int y))
   *
   * @param target Rendering target component
   * @param key    Generated ASCII character (i.e. 'a' or 'A' depending on SHIFT key)
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  default void onKeyboard(Component target, char key, int x, int y) {
  }

  /**
   * Sets the keyboard key up callback for the current window.
   * void glutKeyboardUpFunc (void (*func)(unsigned char key, int x, int y))
   *
   * @param target Rendering target component
   * @param key    Generated ASCII character (i.e. 'a' or 'A' depending on SHIFT key)
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  default void onKeyboardUp(Component target, char key, int x, int y) {
  }

  /**
   * Sets the special keyboard key callback for the current window.
   * void glutSpecialFunc(void (*func)(int key, int x, int y))
   *
   * @param target Rendering target component
   * @param key    Special key code as char - i.e. GLUT_KEY_PAGE_UP
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  default void onSpecialKey(Component target, char key, int x, int y) {
  }

  /**
   * Sets the special keyboard key up callback for the current window.
   * void glutSpecialUpFunc (void (*func)(unsigned char key, int x, int y))
   *
   * @param target Rendering target component
   * @param key    Special key code as char - i.e. GLUT_KEY_PAGE_UP
   * @param x      Window relative coordinate x of mouse when event has occurred
   * @param y      Window relative coordinate y of mouse when event has occurred
   */
  default void onSpecialKeyUp(Component target, char key, int x, int y) {
  }

  /**
   * Sets the display callback for the current window (occurs when window needs to be redisplayed).
   * void glutDisplayFunc (void (*func)(void))
   *
   * @param target Rendering target component
   */
  default void onDisplay(Component target) {
  }

  /**
   * Sets the reshape callback for the current window (triggered when window dimensions are changed)
   * void glutReshapeFunc (void (*func)(int width, int height))
   *
   * @param target Rendering target component
   * @param width  New width of window, px
   * @param height New height of window, px
   */
  default void onReshape(Component target, int width, int height) {
  }

  /**
   * Sets the global idle callback so that program can perform background processing tasks.
   * void glutIdleFunc (void (*func)(void))
   *
   * @param target Rendering target component
   */
  default void onIdle(Component target) {
  }

  /**
   * Indicates whether the listener has usable {@link this#onMouse(Component, int, int, int, int)}
   */
  default boolean hasMouseCallback() {
    return true;
  }

  /**
   * Indicates whether the listener has any of usable
   * {@link this#onKeyboard(Component, char, int, int)}
   * {@link this#onSpecialKey(Component, char, int, int)}
   * {@link this#onSpecialKeyUp(Component, char, int, int)} (Component, char, int, int)}
   */
  default boolean hasKeyboardCallback() {
    return true;
  }
}
