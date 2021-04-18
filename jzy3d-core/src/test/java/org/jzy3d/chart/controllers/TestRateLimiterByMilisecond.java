package org.jzy3d.chart.controllers;

import org.junit.Assert;
import org.junit.Test;

public class TestRateLimiterByMilisecond {
  @Test
  public void rateLimiter() throws InterruptedException {
    int milisecondRateLimit = 100;
    
    RateLimiterByMilisecond rl = new RateLimiterByMilisecond(milisecondRateLimit);
    
    // When / Then
    Assert.assertTrue("Can get an autorisation", rl.rateLimitCheck());

    // When / Then
    Assert.assertFalse("Can't ask a second autorisation", rl.rateLimitCheck());
    
    // When / Then
    Thread.sleep(milisecondRateLimit);
    Assert.assertTrue("Rate limit elapsed allow to ask again", rl.rateLimitCheck());
  }


}
