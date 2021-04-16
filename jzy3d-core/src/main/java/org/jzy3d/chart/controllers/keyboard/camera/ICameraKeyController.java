package org.jzy3d.chart.controllers.keyboard.camera;

public interface ICameraKeyController {
  public boolean isUpdateViewDefault();
  /** Define if the camera controller requires view update after editing camera. */
  public void setUpdateViewDefault(boolean updateViewDefault);
}
