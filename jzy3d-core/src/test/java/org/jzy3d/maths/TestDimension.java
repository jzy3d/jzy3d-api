package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestDimension {
  @Test
  public void eq() {
    Assert.assertEquals(new Dimension(400,300), new Dimension(400,300));
    Assert.assertNotEquals(new Dimension(300,400), new Dimension(400,300));
  }
}
