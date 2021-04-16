package org.jzy3d.chart.controllers;

import java.awt.EventQueue;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;

/**
 * A theoretical rate limit would be 40ms. However, the coallesce mecanism in AWT
 * {@link EventQueue#coalesceEvent} thread being not known enough, we remain much higher to ensure
 * picture has time to render.
 * 
 * Le drag reste fluide TANT QUE le temps de rendu est inférieur au rate limit. Quand il est
 * supérieur au rate limit, ça patine. Bien que la gestion d'un event souris ne se termine que
 * quand le rendering est terminé, les repaints n'ont pas lieu, probablement car le deuxième event,
 * quoique SUIVANT le premier, arrive "juste après". Une optim pourrait être d'avoir un temps
 * pendant le quel APRES la gestion du mouse on event, on ne prend pas d'autre demande de mouse.
 * 
 * Une implementation équivalente est le fait d'avoir un mouseRate limiter qui prend le temps max
 * dans l'historique .
 * 
 * Amélioration : utiliser un mouse limiter adaptatif selon le
 * temps de rendu du canvas. Utiliser la propriété Monitorable du canvas pour écouter son temps de
 * calcul.
 * 
 * Extends Monitor, permet en plus d'avoir l'historique des temps de calcul ET d'overrider la taille
 * de cette mémoire
 * 
 */
public class EmulGLMouseRateLimiterAdaptsToRenderTime extends RateLimiterByMilisecond implements RateLimiter {
  protected EmulGLCanvas canvas;
  static final int HISTORY = 25;
  Queue<Double> renderingTimeHistory = new CircularFifoQueue<Double>(HISTORY);
  static final double MARGIN_MS = 20;
  double marginMs = MARGIN_MS;

  public EmulGLMouseRateLimiterAdaptsToRenderTime(EmulGLCanvas canvas) {
    super();
    this.canvas = canvas;
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
    double lastRenderingTimeMs = canvas.getLastRenderingTime();

    if (lastRenderingTimeMs != EmulGLCanvas.LAST_RENDER_TIME_UNDEFINED) {
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

  protected double max(Queue<Double> renderingTimeHistory) {
    double max = -1;
    for (Double d : renderingTimeHistory) {
      if (d > max) {
        max = d;
      }
    }
    return max;
  }

}
