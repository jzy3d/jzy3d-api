package org.jzy3d.plot3d.rendering.view.lod;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.Bounds;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.FaceColor;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting.WireColor;

public class TestLODSetting {
  @Test
  public void whenLOD_BoundsOnly() {
    LODSetting s = new LODSetting("0", Bounds.ON);
    
    Polygon p = new Polygon();
    p.setBoundingBoxDisplayed(false);
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    
    s.apply(p);
    
    Assert.assertTrue(p.isBoundingBoxDisplayed());
    Assert.assertFalse(p.isFaceDisplayed());
    Assert.assertFalse(p.isWireframeDisplayed());
    
    Assert.assertTrue(s.isBoundsOnly());
    
  }

  @Test
  public void whenLOD_FaceSmoothWireUniform() {
    LODSetting s = new LODSetting("0", FaceColor.ON, WireColor.UNIFORM);
    
    Polygon p = new Polygon();
    p.setBoundingBoxDisplayed(false);
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    
    s.apply(p);
    
    Assert.assertFalse(p.isBoundingBoxDisplayed());
    Assert.assertTrue(p.isFaceDisplayed());
    Assert.assertTrue(p.isWireframeDisplayed());
    
    Assert.assertFalse(s.isBoundsOnly());
    
  }

}
