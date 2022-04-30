package jgl.wt.awt.listener;

public final class GLUTNoopListener implements GLUTListener {

  public static final GLUTNoopListener INSTANCE = new GLUTNoopListener();

  @Override
  public boolean hasMouseCallback() {
    return false;
  }

  @Override
  public boolean hasKeyboardCallback() {
    return false;
  }
}
