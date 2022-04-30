package jgl.wt.awt.listener;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Old reflection based 'Method' callback based on method name.
 */
public class GLUTReflectiveCallbackListener implements GLUTListener {

    private Method reshapeMethod;
    private Method mouseMethod;
    private Method motionMethod;
    private Method keyMethod;
    private Method keyUpMethod;
    private Method specialKeyMethod;
    private Method specialKeyUpMethod;
    private Method displayMethod;
    private Method idleMethod;

    @Override
    public void onMouse(Component target, int button, int state, int x, int y) {
        GLUTListener.super.onMouse(target, button, state, x, y);
    }

    @Override
    public void onMotion(Component target, int x, int y) {
        GLUTListener.super.onMotion(target, x, y);
    }

    @Override
    public void onKeyboard(Component target, char key, int x, int y) {
        GLUTListener.super.onKeyboard(target, key, x, y);
    }

    @Override
    public void onKeyboardUp(Component target, char key, int x, int y) {
        GLUTListener.super.onKeyboardUp(target, key, x, y);
    }

    @Override
    public void onSpecialKey(Component target, char key, int x, int y) {
        GLUTListener.super.onSpecialKey(target, key, x, y);
    }

    @Override
    public void onSpecialUp(Component target, char key, int x, int y) {
        GLUTListener.super.onSpecialUp(target, key, x, y);
    }

    @Override
    public void onDisplay(Component target) {
        if (displayMethod != null) {
            try {
                displayMethod.invoke(target, (Object[]) null);
            } catch (IllegalAccessException e) {
                System.out.println("IllegalAccessException while DisplayFunc");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.out.println("InvocationTargetException while DisplayFunc");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReshape(Component target, int width, int height) {
        GLUTListener.super.onReshape(target, width, height);
    }

    @Override
    public void onIdle(Component target) {
        GLUTListener.super.onIdle(target);
    }

    /*
     * Setters boilerplate below, can be replaced with Lombok.
     */

    public void setReshapeMethod(Method reshapeMethod) {
        this.reshapeMethod = reshapeMethod;
    }

    public void setMouseMethod(Method mouseMethod) {
        this.mouseMethod = mouseMethod;
    }

    public void setMotionMethod(Method motionMethod) {
        this.motionMethod = motionMethod;
    }

    public void setKeyMethod(Method keyMethod) {
        this.keyMethod = keyMethod;
    }

    public void setKeyUpMethod(Method keyUpMethod) {
        this.keyUpMethod = keyUpMethod;
    }

    public void setSpecialKeyMethod(Method specialKeyMethod) {
        this.specialKeyMethod = specialKeyMethod;
    }

    public void setSpecialKeyUpMethod(Method specialKeyUpMethod) {
        this.specialKeyUpMethod = specialKeyUpMethod;
    }

    public void setDisplayMethod(Method displayMethod) {
        this.displayMethod = displayMethod;
    }

    public void setIdleMethod(Method idleMethod) {
        this.idleMethod = idleMethod;
    }
}
