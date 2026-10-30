package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestMargins {
  @Test
  public void set() {
    Margin margin = new Margin();
    
    margin.set(10, 20);
    
    Assert.assertEquals(5, margin.getLeft(), 0);
    Assert.assertEquals(5, margin.getRight(), 0);
    Assert.assertEquals(10, margin.getTop(), 0);
    Assert.assertEquals(10, margin.getBottom(), 0);

    Assert.assertEquals(10, margin.getWidth(), 0);
    Assert.assertEquals(20, margin.getHeight(), 0);
    
    
    margin.setWidth(100);
    
    Assert.assertEquals(50, margin.getLeft(), 0);
    Assert.assertEquals(50, margin.getRight(), 0);
    Assert.assertEquals(100, margin.getWidth(), 0);

    
    margin.setHeight(200);

    Assert.assertEquals(100, margin.getTop(), 0);
    Assert.assertEquals(100, margin.getBottom(), 0);
    Assert.assertEquals(200, margin.getHeight(), 0);

  }
}
