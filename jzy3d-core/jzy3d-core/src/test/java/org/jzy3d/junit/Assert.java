package org.jzy3d.junit;

public class Assert extends org.junit.Assert{
  public static void assertAngleEquals(float expect, float actual, double tolerance) {
    expect = in_2_PI(expect);
    actual = in_2_PI(actual);
    
    //System.out.println(expect);
    //System.out.println(actual);
    
    assertEquals(expect, actual, tolerance);
  }

  public static void assertAngleNotEquals(float expect, float actual, double tolerance) {
    expect = in_2_PI(expect);
    actual = in_2_PI(actual);
    
    assertNotEquals(expect, actual, tolerance);
  }
  
  public static float in_2_PI(float radian) {
    return (float)(radian%(Math.PI*2));
  }
  


}
