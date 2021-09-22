package org.jzy3d.chart.controllers.mouse.camera.adaptive;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Polygon;

public class TestToggle {
  @Test
  public void testToggle_forWireframeWithGlobalColor() {
    
    // Given
    Polygon p = new Polygon();
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    p.setWireframeColor(Color.WHITE);
    p.setWireframeColorFromPolygonPoints(false); // using global wireframe color
    p.setBoundingBoxDisplayed(false);
    
    Toggle t = new Toggle(p);
    
    // When toggling wireframe
    p.setFaceDisplayed(false);
    p.setWireframeDisplayed(false);
    p.setWireframeColor(Color.RED);
    p.setWireframeColorFromPolygonPoints(true); // updated
    p.setBoundingBoxDisplayed(true); // updated
    
    // Then reseting come back to previous settings
    t.reset();
    
    Assert.assertTrue(p.isFaceDisplayed());
    Assert.assertTrue(p.isWireframeDisplayed());
    Assert.assertEquals(Color.WHITE, p.getWireframeColor());
    Assert.assertFalse(p.isWireframeColorFromPolygonPoints());
    Assert.assertFalse(p.isBoundingBoxDisplayed());
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
    
    Assert.assertTrue(p.isFaceDisplayed());
    Assert.assertFalse(p.isWireframeDisplayed());
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
    
    Assert.assertTrue(p.isFaceDisplayed());
    Assert.assertTrue(p.isWireframeDisplayed());
    Assert.assertTrue(p.isWireframeColorFromPolygonPoints());
  }

}
