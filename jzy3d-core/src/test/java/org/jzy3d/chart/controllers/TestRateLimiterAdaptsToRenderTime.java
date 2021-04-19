package org.jzy3d.chart.controllers;

import static org.mockito.Mockito.when;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.mockito.Mockito;

public class TestRateLimiterAdaptsToRenderTime {
  @Test
  public void whenRenderingTimeGrows_ThenRateLimitAdapts() {
    double renderingTimeLong = 1000.0;
    double renderingTimeShort = 10.0;
    
    
    ICanvas c = Mockito.mock(ICanvas.class);
    RateLimiterAdaptsToRenderTime rl = new RateLimiterAdaptsToRenderTime(c);

    // Then initialize with default rate limit value
    Assert.assertEquals(RateLimiterByMilisecond.RATE_LIMIT, rl.rateLimitMilis, 0.01);

    // ------------
    // When rendering time grows
    when(c.getLastRenderingTimeMs()).thenReturn(renderingTimeLong);
    rl.rateLimitCheck(); // invoke recalculation of rate which fetch last rendering time

    // Then
    double expect = renderingTimeLong + RateLimiterAdaptsToRenderTime.MARGIN_MS;
    Assert.assertEquals(expect, rl.rateLimitMilis, 0.01);
    
    
    // ------------
    // When rendering time shrinks after N iteration (which kick the LONG rendering out of history)
    when(c.getLastRenderingTimeMs()).thenReturn(renderingTimeShort);
    for (int i = 0; i < RateLimiterAdaptsToRenderTime.HISTORY_SIZE+1; i++) {
      rl.rateLimitCheck(); // invoke recalculation of rate which fetch last rendering time
    }

    // Then
    expect = renderingTimeShort + RateLimiterAdaptsToRenderTime.MARGIN_MS;
    Assert.assertEquals(expect, rl.rateLimitMilis, 0.01);

  }

}
