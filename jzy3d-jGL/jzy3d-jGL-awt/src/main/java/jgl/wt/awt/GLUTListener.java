package jgl.wt.awt;

public interface GLUTListener {

    /**
     * Sets mouse callback for the current window.
     * void glutMouseFunc (void (*func)(int button, int state, int x, int y))
     * @param button Mouse button identifier (GLUT_LEFT_BUTTON, etc.)
     * @param state GLUT_UP or GLUT_DOWN indicating mouse button state cause
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onMouse(int button, int state, int x, int y) {
    }

    /**
     * Set the motion callback for the current window (mouse movement within window while one or more mouse buttons are pressed)
     * void glutMotionFunc (void (*func)(int x, int y))
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onMotion(int x, int y) {
    }

    /**
     * Sets the keyboard callback for the current window.
     * void glutKeyboardFunc (void (*func)(unsigned char key, int x, int y))
     * @param key Generated ASCII character (i.e. 'a' or 'A' depending on SHIFT key)
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onKeyboard(char key, int x, int y) {
    }

    /**
     * Sets the keyboard key up callback for the current window.
     * void glutKeyboardUpFunc (void (*func)(unsigned char key, int x, int y))
     * @param key Generated ASCII character (i.e. 'a' or 'A' depending on SHIFT key)
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onKeyboardUp(char key, int x, int y) {
    }

    /**
     * Sets the special keyboard key callback for the current window.
     * void glutSpecialFunc(void (*func)(int key, int x, int y))
     * @param key Special key code as char - i.e. GLUT_KEY_PAGE_UP
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onSpecialKey(char key, int x, int y) {
    }

    /**
     * Sets the special keyboard key up callback for the current window.
     * void glutSpecialUpFunc (void (*func)(unsigned char key, int x, int y))
     * @param key Special key code as char - i.e. GLUT_KEY_PAGE_UP
     * @param x Window relative coordinate x of mouse when event has occurred
     * @param y Window relative coordinate y of mouse when event has occurred
     */
    default void onSpecialUp(char key, int x, int y) {
    }

    /**
     * Sets the display callback for the current window (occurs when window needs to be redisplayed).
     * void glutDisplayFunc (void (*func)(void))
     */
    default void onDisplay() {
    }

    /**
     * Sets the reshape callback for the current window (triggered when window dimensions are changed)
     * void glutReshapeFunc (void (*func)(int width, int height))
     * @param width New width of window, px
     * @param height New height of window, px
     */
    default void onReshape(int width, int height) {
    }

    /**
     * Sets the global idle callback so that program can perform background processing tasks.
     * void glutIdleFunc (void (*func)(void))
     */
    default void onIdle() {
    }
}
