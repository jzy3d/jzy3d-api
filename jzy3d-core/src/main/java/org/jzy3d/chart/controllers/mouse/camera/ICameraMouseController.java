package org.jzy3d.chart.controllers.mouse.camera;

import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;

public interface ICameraMouseController {
  @Deprecated()
  public void addSlaveThreadController(CameraThreadController thread);
  @Deprecated()
  public CameraThreadController getSlaveThreadController();

  /** Set the camera thread rotation handler */
  public void addThread(CameraThreadController thread);
  /** Returns the camera thread rotation handler */
  public CameraThreadController getThread();

  public boolean isUpdateViewDefault();
  /** Define if the camera controller requires view update after editing camera. */
  public void setUpdateViewDefault(boolean updateViewDefault);
  
  public void setLODPerf(LODPerf perf);
  public LODPerf getLODPerf();

}
