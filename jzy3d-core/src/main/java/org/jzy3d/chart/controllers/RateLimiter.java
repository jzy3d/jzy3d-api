package org.jzy3d.chart.controllers;



public interface RateLimiter {
  /**
   * Return true if the rate-limited action can be performed, false if it should be bypassed.
   * 
   * @return
   */
  boolean rateLimitCheck();
}
