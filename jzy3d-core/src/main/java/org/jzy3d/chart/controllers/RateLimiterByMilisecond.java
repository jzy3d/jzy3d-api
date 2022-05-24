package org.jzy3d.chart.controllers;

import org.jzy3d.maths.TicToc;

/**
 * @author martin
 *
 */
public class RateLimiterByMilisecond implements RateLimiter {
  protected static final double RATE_LIMIT = 110;

  protected double rateLimitMilis = RATE_LIMIT;//ms
  protected boolean rateLimitStarted = false;
  protected TicToc t = new TicToc();

  public RateLimiterByMilisecond() {
    this(RATE_LIMIT);
  }
  
  public RateLimiterByMilisecond(double rateLimitMilis) {
    super();
    this.rateLimitMilis = rateLimitMilis;
  }

  /**
   * Return true if the action is allowed according to elapsed time.
   * 
   * Time starts counting at the first call to this method, so first call
   * to a rate limit check will always succeed.
   * 
   * @return
   */
  @Override
  public boolean rateLimitCheck() {
    if(!rateLimitStarted) { // first time
      t.tic();
      rateLimitStarted = true;
      return true;
    }
    else { // all other time
      t.toc();
      
      if(t.elapsedMilisecond()>rateLimitMilis) {
        t.tic(); // reset time if necessary
        return true;
      }
      else {
        // bypass
        return false;
      }
    }
  }
  

}
