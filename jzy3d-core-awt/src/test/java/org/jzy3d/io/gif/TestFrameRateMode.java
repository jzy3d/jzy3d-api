package org.jzy3d.io.gif;

import org.junit.Assert;
import org.junit.Test;

public class TestFrameRateMode {
  @Test
  public void continuous() {
    FrameRate c = FrameRate.ContinuousDuration(100);
    Assert.assertTrue(c.isContinuous());
    Assert.assertEquals(100, c.getDuration(), 0.1);
    Assert.assertEquals(10, c.getRate());

    c = FrameRate.ContinuousRate(40);
    Assert.assertTrue(c.isContinuous());
    Assert.assertEquals(25, c.getDuration(), 0.1);
    Assert.assertEquals(40, c.getRate());

  }

  @Test
  public void variable() {
    FrameRate c = FrameRate.VariableDuration(100);
    Assert.assertFalse(c.isContinuous());
    Assert.assertEquals(100, c.getDuration(), 0.1);
    Assert.assertEquals(10, c.getRate());
    
    c = FrameRate.VariableRate(40);
    Assert.assertFalse(c.isContinuous());
    Assert.assertEquals(25, c.getDuration(), 0.1);
    Assert.assertEquals(40, c.getRate());

  }
}
