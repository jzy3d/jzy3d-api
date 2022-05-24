package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestBoundingBox2d {
  @Test
  public void intersect() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10);
    
    Assert.assertTrue("Intersect if at least touch all components", bb.intersect(new BoundingBox2d(10,11,10,11)));
    Assert.assertTrue("Intersect if all component intersect", bb.intersect(new BoundingBox2d(9,11,9,11)));
    Assert.assertTrue("Intersect if all component intersect", bb.intersect(new BoundingBox2d(-1,1,-1,1)));

    Assert.assertFalse("No intersect if only X component 1", bb.intersect(new BoundingBox2d(10,11,100,110)));
    Assert.assertFalse("No intersect if only X component 2", bb.intersect(new BoundingBox2d(8,12,100,110)));

    Assert.assertFalse("No intersect if no component", bb.intersect(new BoundingBox2d(11,12,100,110)));
  }
  
  @Test
  public void containsBounds() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10);
    
    Assert.assertFalse(bb.contains(new BoundingBox2d(9,11,9,11)));
    Assert.assertTrue(bb.contains(new BoundingBox2d(8,10,8,10)));
  }
  
  @Test
  public void containsCoord() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10);
    
    Assert.assertTrue(bb.contains(new Coord2d(5,5)));
    Assert.assertFalse(bb.contains(new Coord2d(15,15)));
    Assert.assertFalse(bb.contains(new Coord2d(-5,-5)));
    Assert.assertFalse(bb.contains(new Coord2d(5,15)));
  }
  
  /*@Test
  public void margin() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10);
    BoundingBox2d b2 = bb.margin(1);
    
    float delta = 0.00001f;
    
    Assert.assertEquals(11f, b2.xmax, delta);
    Assert.assertEquals(-1f, b2.xmin, delta);
    Assert.assertEquals(11f, b2.ymax, delta);
    Assert.assertEquals(-1f, b2.ymin, delta);
    Assert.assertEquals(11f, b2.zmax, delta);
    Assert.assertEquals(-1f, b2.zmin, delta);
  }
  
  @Test
  public void marginRatio() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10,0,10);
    BoundingBox2d b2 = bb.marginRatio(0.1f);
    
    float delta = 0.00001f;
    
    Assert.assertEquals(11f, b2.xmax, delta);
    Assert.assertEquals(-1f, b2.xmin, delta);
    Assert.assertEquals(11f, b2.ymax, delta);
    Assert.assertEquals(-1f, b2.ymin, delta);
    Assert.assertEquals(11f, b2.zmax, delta);
    Assert.assertEquals(-1f, b2.zmin, delta);
  }
  
  @Test
  public void marginSelf() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10,0,10);
    bb.selfMargin(1);
    
    float delta = 0.00001f;
    
    Assert.assertEquals(11f, bb.xmax, delta);
    Assert.assertEquals(-1f, bb.xmin, delta);
    Assert.assertEquals(11f, bb.ymax, delta);
    Assert.assertEquals(-1f, bb.ymin, delta);
    Assert.assertEquals(11f, bb.zmax, delta);
    Assert.assertEquals(-1f, bb.zmin, delta);
  }
  
  @Test
  public void marginRatioSelf() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10,0,10);
    bb.selfMarginRatio(0.1f);
    
    float delta = 0.00001f;
    
    Assert.assertEquals(11f, bb.xmax, delta);
    Assert.assertEquals(-1f, bb.xmin, delta);
    Assert.assertEquals(11f, bb.ymax, delta);
    Assert.assertEquals(-1f, bb.ymin, delta);
    Assert.assertEquals(11f, bb.zmax, delta);
    Assert.assertEquals(-1f, bb.zmin, delta);
  }*/
  
  @Test
  public void shift() {
    BoundingBox2d bb = new BoundingBox2d(0,10,0,10);
    bb = bb.shift(new Coord2d(5,10));
    
    float delta = 0.00001f;
    
    Assert.assertEquals(5f, bb.xmin, delta);
    Assert.assertEquals(15f, bb.xmax, delta);
    Assert.assertEquals(10f, bb.ymin, delta);
    Assert.assertEquals(20f, bb.ymax, delta);
  }

}
