package org.jzy3d.chart.controllers.mouse.camera;

import org.jzy3d.maths.TicToc;

/**
 * @author martin
 *
 */
public class MouseRateLimiter {
  /**
   * A theoretical rate limit would be 40ms. However, the coallesce mecanism in AWT Event thread being not
   * known enough, we remain much higher to ensure picture has time to render.
   * 
   * Le drag reste fluide TANT QUE le temps de rendu est inférieur au rate limit.
   * 
   * Quand il est supérieur au rate limit, ça patine.
   * 
   * Amélioration : utiliser un mouse limiter adaptatif selon le temps de rendu du canvas.
   * 
   * Utiliser la propriété Monitorable du canvas pour écouter son temps de calcul.
   * 
   * Extends Monitor, permet en plus d'avoir l'historique des temps de calcul ET d'overrider la taille de cette mémoire
   * 
   */
  protected double rateLimitMilis = 40;//ms
  protected boolean rateLimitStarted = false;
  
  protected static final double RATE_LIMIT = 110;
  
  TicToc t = new TicToc();

  public MouseRateLimiter() {
    this(RATE_LIMIT);
  }

  
  
  public MouseRateLimiter(double rateLimitMilis) {
    super();
    this.rateLimitMilis = rateLimitMilis;
  }



  /**
   * Return true if a mouse drag update is accepted according to rate limiter.
   * 
   * @return
   */
  public boolean rateLimitCheck() {
    if(!rateLimitStarted) {
      t.tic();
      rateLimitStarted = true;
      return true;
    }
    else {
      t.toc();
      rateLimitStarted = false;
      
      if(t.elapsedMilisecond()>rateLimitMilis) {
        t.tic();
        return true;
      }
      else {
        // bypass
        return false;
      }
    }
  }
  

}
