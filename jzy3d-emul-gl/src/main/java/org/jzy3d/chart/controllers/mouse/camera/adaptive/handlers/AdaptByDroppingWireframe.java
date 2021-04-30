package org.jzy3d.chart.controllers.mouse.camera.adaptive.handlers;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AbstractAdativeRenderingHandler;
import org.jzy3d.chart.controllers.mouse.camera.adaptive.AdaptiveRenderingHandler;
import org.jzy3d.plot3d.primitives.Wireframeable;

public class AdaptByDroppingWireframe extends AbstractAdativeRenderingHandler implements AdaptiveRenderingHandler{
  public AdaptByDroppingWireframe(Chart chart) {
    super(chart);
  }
  
  protected void applyOptimisation(Wireframeable w) {
    w.setWireframeDisplayed(false);
  }
}
