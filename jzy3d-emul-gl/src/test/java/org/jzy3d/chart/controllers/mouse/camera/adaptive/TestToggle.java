package org.jzy3d.chart.controllers.mouse.camera.adaptive;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.plot3d.primitives.Polygon;

public class TestToggle {
  @Test
  public void testToggle_forWireframeWithGlobalColor() {
    
    // Given
    Polygon p = new Polygon();
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    p.setWireframeColorFromPolygonPoints(false); // using global wireframe color
    
    Toggle t = new Toggle(p);
    
    // When toggling wireframe
    p.setFaceDisplayed(false);
    p.setWireframeDisplayed(false);
    p.setWireframeColorFromPolygonPoints(true); // updated
    
    // Then reseting come back to previous settings
    t.reset();
    
    Assert.assertTrue(p.getFaceDisplayed());
    Assert.assertTrue(p.getWireframeDisplayed());
    Assert.assertFalse(p.isWireframeColorFromPolygonPoints());
  }
  
  @Test
  public void testToggle_forWireframeWithGlobalColor2() {
    
    // Given
    Polygon p = new Polygon();
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(false);
    
    Toggle t = new Toggle(p);
    
    // When toggling wireframe
    p.setFaceDisplayed(false);
    p.setWireframeDisplayed(true);
    
    // Then reseting come back to previous settings
    t.reset();
    
    Assert.assertTrue(p.getFaceDisplayed());
    Assert.assertFalse(p.getWireframeDisplayed());
  }
  
  @Test
  public void testToggle_forWireframeWithPointColor() {
    
    // Given
    Polygon p = new Polygon();
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    p.setWireframeColorFromPolygonPoints(true); // using global wireframe color

    Toggle t = new Toggle(p);
    
    // When toggling wireframe
    p.setFaceDisplayed(false);
    p.setWireframeDisplayed(false);
    p.setWireframeColorFromPolygonPoints(true); // do not change
    
    // Then reseting come back to previous settings
    t.reset();
    
    Assert.assertTrue(p.getFaceDisplayed());
    Assert.assertTrue(p.getWireframeDisplayed());
    Assert.assertTrue(p.isWireframeColorFromPolygonPoints());
  }

}
