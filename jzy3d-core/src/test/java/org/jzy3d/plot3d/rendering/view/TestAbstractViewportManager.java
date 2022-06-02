package org.jzy3d.plot3d.rendering.view;

import org.junit.Assert;
import org.junit.Test;

public class TestAbstractViewportManager {
  @Test
  public void testSliceWidth() {
    AbstractViewportManager avm = new AbstractViewportManager() {};

    // ensure we properly round the value
    Assert.assertEquals(220, avm.getSliceWidth(1600, 0.8625f, 1.0f));
  }
}
