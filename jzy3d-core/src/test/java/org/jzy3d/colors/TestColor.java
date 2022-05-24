package org.jzy3d.colors;

import org.junit.Assert;
import org.junit.Test;

public class TestColor {
  @Test
  public void testColorDistance() {
    double delta = 0.000001;
    
    Assert.assertEquals(3.0, Color.BLACK.distanceSq(Color.WHITE), delta);
    
    Assert.assertEquals(1.0, Color.BLACK.distanceSq(Color.RED), delta);
    Assert.assertEquals(1.0, Color.BLACK.distanceSq(Color.GREEN), delta);
    Assert.assertEquals(1.0, Color.BLACK.distanceSq(Color.BLUE), delta);

    Assert.assertEquals(2.0, Color.WHITE.distanceSq(Color.RED), delta);
    Assert.assertEquals(2.0, Color.WHITE.distanceSq(Color.GREEN), delta);
    Assert.assertEquals(2.0, Color.WHITE.distanceSq(Color.BLUE), delta);

    Assert.assertEquals(2.0, Color.RED.distanceSq(Color.BLUE), delta);
    Assert.assertEquals(2.0, Color.RED.distanceSq(Color.GREEN), delta);
    Assert.assertEquals(2.0, Color.BLUE.distanceSq(Color.GREEN), delta);
    
  }
}
