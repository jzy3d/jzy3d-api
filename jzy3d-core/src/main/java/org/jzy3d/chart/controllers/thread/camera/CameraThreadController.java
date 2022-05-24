package org.jzy3d.chart.controllers.thread.camera;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;

/**
 * The {@link CameraThreadController} provides a {@link Thread} for controlling the {@link Camera}
 * and make it turn around the view point along its the azimuth dimension.
 * 
 * @author Martin Pernollet
 */
public class CameraThreadController extends AbstractCameraThreadController implements Runnable {

  protected Coord2d move;
  protected int sleep = 1;// 1000/25; // nb milisecond wait between two frames
  protected float step = 0.0005f;

  public CameraThreadController() {}

  public CameraThreadController(Chart chart) {
    register(chart);
  }

  protected void doRun() {
    move = new Coord2d(step, 0);

    while (process != null) {
      try {
        rotate(move);
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        process = null;
      }
    }
  }

  public float getStep() {
    return step;
  }

  public void setStep(float step) {
    this.step = step;
  }


}
