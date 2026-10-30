package org.jzy3d.plot3d.rendering.view;

public abstract class AbstractAWTRenderer2d implements AWTRenderer2d{
  protected AWTView view;

  public AWTView getView() {
    return view;
  }

  public void setView(AWTView view) {
    this.view = view;
  }
}
