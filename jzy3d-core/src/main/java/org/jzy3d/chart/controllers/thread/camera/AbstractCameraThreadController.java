package org.jzy3d.chart.controllers.thread.camera;



import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;

public abstract class AbstractCameraThreadController extends AbstractCameraController implements Runnable {

  protected Thread process = null;
  protected static int id = 0;

  public AbstractCameraThreadController() {
    super();
  }

  public AbstractCameraThreadController(Chart chart) {
    super(chart);
  }

  @Override
  public void dispose() {
    stop();
    super.dispose();
  }

  /** Start the camera rotation . */
  public void start() {
    if (process == null) {
      process = new Thread(this);
      process.setName(this.getClass().getSimpleName() + " (automatic rotation)" + (id++));
      process.start();
    }
  }

  /** Stop the rotation. */
  public void stop() {
    if (process != null) {
      process.interrupt();
      process = null;
    }
  }

  /** Run the animation. */
  @Override
  public void run() {
    doRun();
  }
  
  protected abstract void doRun();

}
