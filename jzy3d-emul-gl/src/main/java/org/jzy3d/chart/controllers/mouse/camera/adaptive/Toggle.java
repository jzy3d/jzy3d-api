package org.jzy3d.chart.controllers.mouse.camera.adaptive;

import org.jzy3d.plot3d.primitives.Wireframeable;

public class Toggle {
  boolean face;
  boolean wire;
  boolean wireColor;
  boolean bounds;
  
  Wireframeable w;

  public Toggle(Wireframeable d) {
    w = d;
    face = d.isFaceDisplayed();
    wire = d.isWireframeDisplayed();
    wireColor = d.isWireframeColorFromPolygonPoints();
    bounds = d.isBoundingBoxDisplayed();
  }

  public void reset() {
    w.setFaceDisplayed(face);
    w.setWireframeDisplayed(wire);
    w.setWireframeColorFromPolygonPoints(wireColor);
    w.setBoundingBoxDisplayed(bounds);
  }
}
