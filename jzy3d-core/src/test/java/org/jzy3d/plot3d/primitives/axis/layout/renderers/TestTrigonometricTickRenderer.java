package org.jzy3d.plot3d.primitives.axis.layout.renderers;

import org.junit.Assert;
import org.junit.Test;

public class TestTrigonometricTickRenderer {
  @Test
  public void test2() {
    TrigonometricTickRenderer r = new TrigonometricTickRenderer();
    Assert.assertEquals("3π/2", r.format(Math.PI*3/2));
    Assert.assertEquals("10π", r.format(Math.PI*10));

  }
  
  @Test
  public void test() {
    TrigonometricTickRenderer r = new TrigonometricTickRenderer();

    Assert.assertEquals("0", r.format(0));

    Assert.assertEquals("2π", r.format(Math.PI*2));
    Assert.assertEquals("3π", r.format(Math.PI*3));
    Assert.assertEquals("4π", r.format(Math.PI*4));

    
    Assert.assertEquals("π", r.format(Math.PI));
    Assert.assertEquals("π/2", r.format(Math.PI/2));
    Assert.assertEquals("π/3", r.format(Math.PI/3));
    Assert.assertEquals("π/4", r.format(Math.PI/4));
    Assert.assertEquals("π/5", r.format(Math.PI/5));
    Assert.assertEquals("π/6", r.format(Math.PI/6));
    
    Assert.assertEquals("-4π", r.format(-Math.PI*4));
    Assert.assertEquals("-3π", r.format(-Math.PI*3));
    Assert.assertEquals("-2π", r.format(-Math.PI*2));
    Assert.assertEquals("-π", r.format(-Math.PI));
    Assert.assertEquals("-π/2", r.format(-Math.PI/2));
    Assert.assertEquals("-π/3", r.format(-Math.PI/3));
    Assert.assertEquals("-π/4", r.format(-Math.PI/4));
    Assert.assertEquals("-π/5", r.format(-Math.PI/5));
    Assert.assertEquals("-π/6", r.format(-Math.PI/6));
    
    Assert.assertEquals("2π/3", r.format(Math.PI*2/3));
    Assert.assertEquals("2π/5", r.format(Math.PI*2/5));

    Assert.assertEquals("-2π/3", r.format(-Math.PI*2/3));
    Assert.assertEquals("-2π/5", r.format(-Math.PI*2/5));


    Assert.assertEquals("3π/2", r.format(Math.PI*3/2));
    Assert.assertEquals("-3π/2", r.format(-Math.PI*3/2));

    Assert.assertEquals("4π/3", r.format(Math.PI*4/3));
    Assert.assertEquals("-4π/3", r.format(-Math.PI*4/3));

    
    // fraction not supported with default value
    Assert.assertNotEquals("π/7", r.format(Math.PI/7));
    Assert.assertNotEquals("-π/7", r.format(-Math.PI/7));

    
    // Grow supported fractions
    r = new TrigonometricTickRenderer(8);
    Assert.assertEquals("π/7", r.format(Math.PI/7));
    Assert.assertEquals("π/8", r.format(Math.PI/8));

    Assert.assertEquals("-π/7", r.format(-Math.PI/7));
    Assert.assertEquals("-π/8", r.format(-Math.PI/8));
    
    //Assert.assertEquals("π/9", r.format(Math.PI/9));

  }
}
