package org.jzy3d.chart.controllers.mouse.camera;

import org.jzy3d.chart.controllers.RateLimiter;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;

/**
 * A collection of settings to allow a controller to adapt rendering quality in order to
 * optimize performances of the chart.
 * 
 * The actual use of these parameter is defined in the controllers such as {@link AdaptiveMouseController}.
 * 
 * @author Martin Pernollet
 */
public class AdaptiveRenderingPolicy {
  /**
   * the rate limiter will ensure the canvas do not look frozen when too much mouse events. It may
   * do it in an adaptive manner (e.g. when using {@link RateLimiterAdaptsToRenderTime}. 
   * 
   * It however won't optimize performance while rotating, but mainly prevent the chart from
   * flooding AWT Event thread to ensure display will occur for all intermediate rotation steps. 
   * 
   * Optimizing performance is done by other parameters.
   */
  public RateLimiter renderingRateLimiter = null;

  /**
   * The canvas rendering time threshold above which the mouse controller will apply the enabled
   * optimization policies. No optimization is applied at all if the rendering time remains below
   * this threshold.
   */
  public double optimizeForRenderingTimeLargerThan = 100;

  /**
   * If true, the polygon wireframe will be desactivated between mouse pressed and mouse release.
   * 
   * Defaults to false
   */
  public boolean optimizeByDroppingWireframeOnly = false;
  
  /**
   * If true, the polygon face will be desactivated and wireframe will be colored according to face colors
   * between mouse pressed and mouse release.
   * 
   * Defaults to false
   */
  public boolean optimizeByDroppingFaceAndKeepingWireframeWithColor = false;
  

  /**
   * If true, the polygon face will be desactivated between mouse pressed and mouse release.
   * 
   * Defaults to true
   */
  public boolean optimizeByDroppingFaceAndKeepingWireframe = true;
  
  /**
   * If true, HiDPI will be desactivated between mouse pressed and mouse release.
   * 
   * Defaults to false
   */
  public boolean optimizeByDroppingHiDPI = false;
}
