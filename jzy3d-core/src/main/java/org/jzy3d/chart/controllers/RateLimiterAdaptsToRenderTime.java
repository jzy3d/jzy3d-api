package org.jzy3d.chart.controllers;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

/**
 * This rate limiter keeps an history of past rendering time to auto configure the rate limit
 * and ensure we stick to actual rendering time before doing an action.
 * 
 * This rate limiter is useful to ensure a controller such as mouse or thread won't ask more
 * rendering than possible.
 * 
 * The time limit is computed as follow
 * <ul>
 * <li>Compute the maximum rendering time in the list of the 10 last rendering time
 * <li>Adds a 20ms margin
 * </ul>
 * 
 * This allows adapting the rate limit if the rendering capabilities changes, which may happen
 * according to a changing canvas size, changing screen, changing HiDPI settings, changing content 
 * of chart.
 */
public class RateLimiterAdaptsToRenderTime extends RateLimiterByMilisecond implements RateLimiter {
  protected ICanvas canvas;
  static final int HISTORY_SIZE = 10;
  Queue<Double> renderingTimeHistory = new CircularFifoQueue<Double>(HISTORY_SIZE);
  static final double MARGIN_MS = 20;
  double marginMs = MARGIN_MS;

  public RateLimiterAdaptsToRenderTime(ICanvas canvas) {
    this();
    setCanvas(canvas);
  }

  public RateLimiterAdaptsToRenderTime() {
    super();
  }

  /**
   * Returns true if the canvas is NOT rendering
   */
  @Override
  public boolean rateLimitCheck() {
    adaptRateLimitToRenderTimeHistory();

    boolean allowRendering = super.rateLimitCheck();
    //boolean allowRendering = !canvas.getIsRenderingFlag().get();
    //System.out.println("Allow rendering : " + allowRendering + " current rate ms: " +
    // rateLimitMilis + " | " + renderingTimeHistory);
    return allowRendering;
  }

  protected void adaptRateLimitToRenderTimeHistory() {
    double lastRenderingTimeMs = getLastRenderingTimeFromCanvas();

    if (lastRenderingTimeMs != ICanvas.LAST_RENDER_TIME_UNDEFINED) {
      renderingTimeHistory.add(lastRenderingTimeMs);

      double referenceRenderingTimeMs = max(renderingTimeHistory) + marginMs;

      // Grow rate limit if history consider we are slower
      if (referenceRenderingTimeMs > rateLimitMilis)
        rateLimitMilis = referenceRenderingTimeMs;
      // Shrink rate limit if history consider we are faster
      else if (referenceRenderingTimeMs < rateLimitMilis)
        rateLimitMilis = referenceRenderingTimeMs;
      
      /*
       * if(lastRenderingTimeMs>rateLimitMilis) rateLimitMilis = lastRenderingTimeMs;
       */

    }
  }

  protected double getLastRenderingTimeFromCanvas() {
    if(canvas==null)
      return -1;
    return canvas.getLastRenderingTimeMs();
  }

  protected double max(Queue<Double> renderingTimeHistory) {
    double max = -1;
    for (Double d : renderingTimeHistory) {
      if (d > max) {
        max = d;
      }
    }
    return max;
  }

  public ICanvas getCanvas() {
    return canvas;
  }

  public void setCanvas(ICanvas canvas) {
    this.canvas = canvas;
  }
}
