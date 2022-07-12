package org.jzy3d.junit;

import org.junit.Test;

public class TestAssert {
  float tolerance = 0.001f;

  @Test
  public void assertIn_2_PI() {
    float PI_2 = (float)Math.PI*2;
    
    Assert.assertEquals(0, Assert.in_2_PI(0), tolerance);
    Assert.assertEquals(0, Assert.in_2_PI(PI_2), tolerance);
    
    float offset = 0.01f;
    
    Assert.assertEquals(offset, Assert.in_2_PI(PI_2+offset), tolerance);

    //float angle = 3.15159273147583f;
    //System.out.println(PI_2);
    //Assert.assertEquals(angle - PI_2, Assert.in_2_PI(angle), tolerance);

  }
  
  @Test
  public void assertAngleEqual() {
    Assert.assertAngleEquals(0, 0, 0);
    Assert.assertAngleEquals(0, (float) Math.PI*2, tolerance);

    float offset = 0.01f;
    Assert.assertAngleEquals(offset, offset+(float) Math.PI*2, tolerance);
    
    
   //System.out.println("diff = " + (3.15159273147583f-0.009999999776482582f)); 
   //Assert.assertAngleEquals(0.009999999776482582f, 3.15159273147583f, tolerance);
    


  }
  
  @Test
  public void assertAngleNotEqual() {
    Assert.assertAngleNotEquals(0, (float) Math.PI, tolerance);
  }

}
