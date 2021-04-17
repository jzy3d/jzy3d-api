package org.jzy3d.chart.controllers;

import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

/**
 * Present mouse to trigger repaint in case canvas is already painting.
 * 
 * Kept for further tests, but actually did not work.
 * 
 * @author martin
 */
public class RateLimiterLock implements RateLimiter{
  protected EmulGLCanvas canvas;
  
  public RateLimiterLock(EmulGLCanvas canvas) {
    this.canvas = canvas;
  }

  /**
   * Returns true if the canvas is NOT rendering
   */
  @Override
  public boolean rateLimitCheck() {
    boolean allowRendering = !canvas.getIsRenderingFlag().get();
    System.out.println("Allow rendering : " + allowRendering);
    return allowRendering;
  }

}
