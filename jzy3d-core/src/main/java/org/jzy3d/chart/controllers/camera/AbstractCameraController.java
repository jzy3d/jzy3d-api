package org.jzy3d.chart.controllers.camera;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.AbstractController;
import org.jzy3d.chart.controllers.ControllerType;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;


public abstract class AbstractCameraController extends AbstractController
    implements ICameraMouseController {

  protected CameraThreadController threadController;
  protected Coord2d prevMouse = Coord2d.ORIGIN;
  protected LODPerf perf;

  
  public AbstractCameraController() {
    super();
  }

  public AbstractCameraController(Chart chart) {
    super(chart);
  }

  @Override
  public void dispose() {
    if (threadController != null)
      threadController.stop();
    super.dispose();
  }

  public boolean isUpdateViewDefault() {
    return updateViewDefault;
  }

  /** Define if the camera controller requires view update after editing camera. */
  public void setUpdateViewDefault(boolean updateViewDefault) {
    this.updateViewDefault = updateViewDefault;
  }

  protected boolean updateViewDefault = false;

  protected void rotate(double azimuth) {
    rotate(new Coord2d(azimuth, 0));
  }
  
  protected void rotate(final Coord2d move) {
    rotate(move, updateViewDefault);
  }

  protected void rotate(final Coord2d move, boolean updateView) {
    for (Chart c : targets)
      c.getView().rotate(move, updateView);
    fireControllerEvent(ControllerType.ROTATE, move);
  }

  protected void shift(final float factor) {
    shift(factor, updateViewDefault);
  }

  protected void shift(final float factor, boolean updateView) {
    for (Chart c : targets)
      c.getView().shift(factor, updateView);
    fireControllerEvent(ControllerType.SHIFT, factor);
  }

  protected void zoomX(final float factor) {
    zoomX(factor, updateViewDefault);
  }

  protected void zoomX(final float factor, boolean updateView) {
    for (Chart c : targets)
      c.getView().zoomX(factor, updateView);
    fireControllerEvent(ControllerType.ZOOM, factor);
  }

  protected void zoomY(final float factor) {
    zoomY(factor, updateViewDefault);
  }

  protected void zoomY(final float factor, boolean updateView) {
    for (Chart c : targets)
      c.getView().zoomY(factor, updateView);
    fireControllerEvent(ControllerType.ZOOM, factor);
  }

  protected void zoomZ(final float factor) {
    zoomZ(factor, updateViewDefault);
  }

  protected void zoomZ(final float factor, boolean updateView) {
    for (Chart c : targets)
      c.getView().zoomZ(factor, updateView);
    fireControllerEvent(ControllerType.ZOOM, factor);
  }


  @Deprecated
  @Override
  public void addSlaveThreadController(CameraThreadController controller) {
   addThread(controller);
  }
  

  @Deprecated
  @Override
  public CameraThreadController getSlaveThreadController() {
    return getThread();
  }

  @Override
  public void addThread(CameraThreadController controller) {
    removeSlaveThreadController();
    this.threadController = controller;
  }
  
  @Override
  public CameraThreadController getThread() {
    return this.threadController;
  }

  public void removeSlaveThreadController() {
    if (threadController != null) {
      threadController.stop();
      threadController = null;
    }
  }

  public void stopThreadController() {
    if (threadController != null)
      threadController.stop();
  }

  public void startThreadController() {
    if (threadController != null) {
      threadController.start();
    }
  }
  
  @Override
  public void setLODPerf(LODPerf perf) {
    this.perf = perf;
  }

  @Override
  public LODPerf getLODPerf() {
    return perf;
  }

}
