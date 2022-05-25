package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestVector3d {
  @Test
  public void dot() {
    // sample
    Vector3d v1 = new Vector3d(1,3,-5);
    Vector3d v2 = new Vector3d(4,-2,-1);
    
    Assert.assertEquals(3, v1.dot(v2), 0.00000001);
    
    // perpendicular
    v1 = new Vector3d(1, 0, 0);
    v2 = new Vector3d(0, 1, 0);
    
    Assert.assertEquals(0, v1.dot(v2), 0.00000001);


  }
}
