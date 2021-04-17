package org.jzy3d.chart.controllers;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLAnimator;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.TicToc;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.view.Camera;

/**
 * The {@link CameraThreadControllerWithTime} handle rotation of a {@link Camera} according to a
 * speed given in second, which is the time required to make a complete 360° rotation.
 * 
 * If the chart is configured to repaint on demand, then the thread will adapt the amount of
 * rendering request according to the current computer capabilities w.r.t. the configuration
 * constraints (size, HiDPI, etc). The rate limiter may be disabled by {@link #setRateLimiter(RateLimiterAdaptsToRenderTime)}
 * 
 * In case the angle steps for the rotation are too high, then one should let the chart work with
 * less demanding processing
 * <ul>
 * <li>Disable HiDPI {@link Quality#setPreserveViewportSize(true)}
 * <li>Use smaller canvas size.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public class CameraThreadControllerWithTime extends CameraThreadController implements Runnable {
  protected double speed = 10; // seconds to make a complete revolution

  /**
   * A rate limiter to avoid flooding AWT Event thread. It adapts according to rendering time to
   * ensure rate limit is not to high (chart rendering capabilities may change over time due to many
   * parameters (chart size, number of drawable, availability of CPU, etc)
   */
  protected RateLimiter rateLimiter;

  /** Used in case no rate limiter is used */
  protected static final int MIN_LOOP_PAUSE_MS = 100;

  public CameraThreadControllerWithTime() {}

  public CameraThreadControllerWithTime(Chart chart) {
    this(chart, 10);
  }

  /**
   * @param chart
   * @param secondsToCompleteSpin time in second to let the chart rotate of 2xPI
   */
  public CameraThreadControllerWithTime(Chart chart, double secondsToCompleteSpin) {
    register(chart);
    setRateLimiter(new RateLimiterAdaptsToRenderTime());
    speed = secondsToCompleteSpin;
  }

  /**
   * Will only update screen if {@link CameraThreadControllerWithTime#isUpdateViewDefault()},
   * otherwise the thread only move camera and let the {@link EmulGLAnimator} trigger continuous
   * repaints.
   */
  @Override
  protected void doRun() {
    TicToc t = new TicToc();
    t.tic();

    while (process != null) {
      try {
        // ---------------------------------
        // loop on rate limit chick to avoid flooding with repaint
        // this will tend to optimal rendering updates according to the
        // current capacity and workload for rendering as the limiter
        // consider the history of past rendering time.
        if (rateLimiter != null) {
          while (!rateLimiter.rateLimitCheck()) {
            Thread.sleep(100); // wait 10 ms between each check
          }
        } else {
          Thread.sleep(MIN_LOOP_PAUSE_MS);
        }

        // ---------------------------------
        // compute the rotation to apply to the chart according to the elapsed time
        // since last rendering AND the speed configured for this rotation.
        t.toc();
        double elapsedRatio = t.elapsedSecond() / speed;
        double rotation = elapsedRatio * Math.PI * 2;
        rotate(-rotation);
        //System.out.println("Rotate after " + rateLimiter.getLastRenderingTimeFromCanvas()
        //    + " move ratio : " + elapsedRatio + " rotation:" + rotation);

        // ---------------------------------
        // restart time counter for next loop
        t.tic();
      }

      catch (InterruptedException e) {
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

  public RateLimiter getRateLimiter() {
    return rateLimiter;
  }

  public void setRateLimiter(RateLimiter rateLimiter) {
    if(rateLimiter!=null && rateLimiter instanceof RateLimiterAdaptsToRenderTime) {
      ((RateLimiterAdaptsToRenderTime)rateLimiter).setCanvas((EmulGLCanvas) getChart().getCanvas());
    }
    this.rateLimiter = rateLimiter;
  }
}
