package org.jzy3d.chart.controllers.mouse.camera;

import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;

public interface ICameraMouseController {
  public void addSlaveThreadController(CameraThreadController thread);
  public CameraThreadController getSlaveThreadController();
  
  public boolean isUpdateViewDefault();
  /** Define if the camera controller requires view update after editing camera. */
  public void setUpdateViewDefault(boolean updateViewDefault);

}
