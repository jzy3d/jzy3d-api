package org.jzy3d.chart.controllers.mouse.camera.adaptive;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.plot3d.primitives.Polygon;

public class TestToggle {
  @Test
  public void testToggle() {
    Polygon p = new Polygon();
    p.setFaceDisplayed(true);
    p.setWireframeDisplayed(true);
    p.setWireframeColorFromPolygonPoints(false);
    // 
    Toggle t = new Toggle(p);
    
    // When toggling wireframe
    p.setFaceDisplayed(false);
    p.setWireframeDisplayed(false);
    p.setWireframeColorFromPolygonPoints(true);
    
    // Then reseting come back to previous settings
    t.reset();
    
    Assert.assertTrue(p.getFaceDisplayed());
    Assert.assertTrue(p.getWireframeDisplayed());
    Assert.assertFalse(p.isWireframeColorFromPolygonPoints());
  }

}
