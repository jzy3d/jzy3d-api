package org.jzy3d.chart.controllers;

import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

/**
 * Present mouse to trigger repaint in case canvas is already painting
 * @author martin
 *
 */
public class EmulGLMouseRateLimiterLock implements RateLimiter{
  protected EmulGLCanvas canvas;
  
  public EmulGLMouseRateLimiterLock(EmulGLCanvas canvas) {
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
