package jgl.wt.awt.listener;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Old reflection based 'Method' callback based on method name.
 */
public class GLUTReflectiveCallbackListener implements GLUTListener {

  protected Method reshapeMethod;
  protected Method mouseMethod;
  protected Method motionMethod;
  protected Method keyMethod;
  protected Method keyUpMethod;
  protected Method specialKeyMethod;
  protected Method specialKeyUpMethod;
  protected Method displayMethod;
  protected Method idleMethod;

  @Override
  public void onMouse(Component target, int button, int state, int x, int y) {
    invokeReflectively(target, mouseMethod, new Object[]{button, state, x, y}, "MouseFunc");
  }

  @Override
  public void onMotion(Component target, int x, int y) {
    invokeReflectively(target, motionMethod, new Object[]{x, y}, "MotionFunc");
  }

  @Override
  public void onKeyboard(Component target, char key, int x, int y) {
    invokeReflectively(target, keyMethod, new Object[]{key, x, y}, "KeyFunc");
  }

  @Override
  public void onKeyboardUp(Component target, char key, int x, int y) {
    invokeReflectively(target, keyUpMethod, new Object[]{key, x, y}, "KeyUpFunc");
  }

  @Override
  public void onSpecialKey(Component target, char key, int x, int y) {
    invokeReflectively(target, specialKeyMethod, new Object[]{key, x, y}, "SpecialKeyFunc");
  }

  @Override
  public void onSpecialKeyUp(Component target, char key, int x, int y) {
    invokeReflectively(target, specialKeyUpMethod, new Object[]{key, x, y}, "SpecialKeyUpFunc");
  }

  @Override
  public void onDisplay(Component target) {
    invokeReflectively(target, displayMethod, null, "DisplayFunc");
  }

  @Override
  public void onReshape(Component target, int width, int height) {
    invokeReflectively(target, reshapeMethod, new Object[]{width, height}, "ReshapeFunc");
  }

  @Override
  public void onIdle(Component target) {
    invokeReflectively(target, idleMethod, null, "IdleFunc");
  }

  @Override
  public boolean hasMouseCallback() {
    return mouseMethod != null;
  }

  @Override
  public boolean hasKeyboardCallback() {
    return (keyUpMethod != null) || (specialKeyMethod != null) || (specialKeyUpMethod != null);
  }

  private void invokeReflectively(Component target, Method method, Object[] arguments, String methodNameIfError) {
    if (method == null) {
      return;
    }

    try {
      method.invoke(target, arguments);
    } catch (IllegalAccessException e) {
      System.out.println("IllegalAccessException while " + methodNameIfError);
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      System.out.println("InvocationTargetException while " + methodNameIfError);
      e.printStackTrace();
    }
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
