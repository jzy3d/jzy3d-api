package org.jzy3d.maths;

import org.junit.Assert;
import org.junit.Test;

public class TestBoundingBox3d {
  @Test
  public void intersect() {
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    
    Assert.assertTrue("Intersect if at least touch all components", bb.intersect(new BoundingBox3d(10,11,10,11,10,11)));
    Assert.assertTrue("Intersect if all component intersect", bb.intersect(new BoundingBox3d(9,11,9,11,9,11)));
    Assert.assertTrue("Intersect if all component intersect", bb.intersect(new BoundingBox3d(-1,1,-1,1,-1,1)));

    Assert.assertFalse("No intersect if only X component 1", bb.intersect(new BoundingBox3d(10,11,100,110,100,110)));
    Assert.assertFalse("No intersect if only X component 2", bb.intersect(new BoundingBox3d(8,12,100,110,100,110)));

    Assert.assertFalse("No intersect if only X, Y component 1", bb.intersect(new BoundingBox3d(10,11,10,11,100,110)));
    Assert.assertFalse("No intersect if only X, Y component 2", bb.intersect(new BoundingBox3d(8,12,10,11,100,110)));

    Assert.assertFalse("No intersect if no component", bb.intersect(new BoundingBox3d(11,12,100,110,100,110)));
  }
  
  @Test
  public void containsBounds() {
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    
    Assert.assertFalse(bb.contains(new BoundingBox3d(9,11,9,11,9,11)));
    Assert.assertTrue(bb.contains(new BoundingBox3d(8,10,8,10,8,10)));
  }
  
  @Test
  public void containsCoord() {
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    
    Assert.assertTrue(bb.contains(new Coord3d(5,5,5)));
    Assert.assertFalse(bb.contains(new Coord3d(15,15,15)));
    Assert.assertFalse(bb.contains(new Coord3d(-5,-5,-5)));
    Assert.assertFalse(bb.contains(new Coord3d(5,5,15)));
  }
  
  @Test
  public void margin() {
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    BoundingBox3d b2 = bb.margin(1);
    
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
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    BoundingBox3d b2 = bb.marginRatio(0.1f);
    
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
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
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
    BoundingBox3d bb = new BoundingBox3d(0,10,0,10,0,10);
    bb.selfMarginRatio(0.1f);
    
    float delta = 0.00001f;
    
    Assert.assertEquals(11f, bb.xmax, delta);
    Assert.assertEquals(-1f, bb.xmin, delta);
    Assert.assertEquals(11f, bb.ymax, delta);
    Assert.assertEquals(-1f, bb.ymin, delta);
    Assert.assertEquals(11f, bb.zmax, delta);
    Assert.assertEquals(-1f, bb.zmin, delta);
  }
}
