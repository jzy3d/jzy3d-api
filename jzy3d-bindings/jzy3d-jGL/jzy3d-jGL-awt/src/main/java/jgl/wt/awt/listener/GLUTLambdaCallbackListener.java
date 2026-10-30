package jgl.wt.awt.listener;

import jgl.wt.awt.listener.callbacks.KeyboardCallback;
import jgl.wt.awt.listener.callbacks.MotionCallback;
import jgl.wt.awt.listener.callbacks.MouseCallback;
import jgl.wt.awt.listener.callbacks.ReshapeCallback;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Newer, lambda based callback listener.
 */
public class GLUTLambdaCallbackListener implements GLUTListener {

  protected ReshapeCallback reshapeMethod;
  protected MouseCallback mouseMethod;
  protected MotionCallback motionMethod;
  protected KeyboardCallback keyMethod;
  protected KeyboardCallback keyUpMethod;
  protected KeyboardCallback specialKeyMethod;
  protected KeyboardCallback specialKeyUpMethod;
  protected Consumer<Component> displayMethod;
  protected Consumer<Component> idleMethod;

  @Override
  public void onMouse(Component target, int button, int state, int x, int y) {
    if (null == mouseMethod) {
      return;
    }

    mouseMethod.onMouse(target, button, state, x, y);
  }

  @Override
  public void onMotion(Component target, int x, int y) {
    if (null == motionMethod) {
      return;
    }

    motionMethod.onMotion(target, x, y);
  }

  @Override
  public void onKeyboard(Component target, char key, int x, int y) {
    if (null == keyMethod) {
      return;
    }

    keyMethod.onKeyboard(target, key, x, y);
  }

  @Override
  public void onKeyboardUp(Component target, char key, int x, int y) {
    if (null == keyUpMethod) {
      return;
    }

    keyUpMethod.onKeyboard(target, key, x, y);
  }

  @Override
  public void onSpecialKey(Component target, char key, int x, int y) {
    if (null == specialKeyMethod) {
      return;
    }

    specialKeyMethod.onKeyboard(target, key, x, y);
  }

  @Override
  public void onSpecialKeyUp(Component target, char key, int x, int y) {
    if (null == specialKeyUpMethod) {
      return;
    }

    specialKeyUpMethod.onKeyboard(target, key, x, y);
  }

  @Override
  public void onDisplay(Component target) {
    if (null == displayMethod) {
      return;
    }

    displayMethod.accept(target);
  }

  @Override
  public void onReshape(Component target, int width, int height) {
    if (null == reshapeMethod) {
      return;
    }

    reshapeMethod.onReshape(target, width, height);
  }

  @Override
  public void onIdle(Component target) {
    if (null == idleMethod) {
      return;
    }

    idleMethod.accept(target);
  }

  @Override
  public boolean hasMouseCallback() {
    return mouseMethod != null;
  }

  @Override
  public boolean hasKeyboardCallback() {
    return (keyUpMethod != null) || (specialKeyMethod != null) || (specialKeyUpMethod != null);
  }

  /*
   * Setters boilerplate below, can be replaced with Lombok.
   */

  public void setReshapeMethod(ReshapeCallback reshapeMethod) {
    this.reshapeMethod = reshapeMethod;
  }

  public void setMouseMethod(MouseCallback mouseMethod) {
    this.mouseMethod = mouseMethod;
  }

  public void setMotionMethod(MotionCallback motionMethod) {
    this.motionMethod = motionMethod;
  }

  public void setKeyMethod(KeyboardCallback keyMethod) {
    this.keyMethod = keyMethod;
  }

  public void setKeyUpMethod(KeyboardCallback keyUpMethod) {
    this.keyUpMethod = keyUpMethod;
  }

  public void setSpecialKeyMethod(KeyboardCallback specialKeyMethod) {
    this.specialKeyMethod = specialKeyMethod;
  }

  public void setSpecialKeyUpMethod(KeyboardCallback specialKeyUpMethod) {
    this.specialKeyUpMethod = specialKeyUpMethod;
  }

  public void setDisplayMethod(Consumer<Component> displayMethod) {
    this.displayMethod = displayMethod;
  }

  public void setIdleMethod(Consumer<Component> idleMethod) {
    this.idleMethod = idleMethod;
  }
}
