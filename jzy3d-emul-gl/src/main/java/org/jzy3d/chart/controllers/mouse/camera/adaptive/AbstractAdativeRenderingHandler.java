package org.jzy3d.chart.controllers.mouse.camera.adaptive;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.rendering.scene.Graph;



public abstract class AbstractAdativeRenderingHandler implements AdaptiveRenderingHandler{
  protected abstract void applyOptimisation(Wireframeable w);

  protected Chart chart;
  protected List<Toggle> drawableToRevert;

  public AbstractAdativeRenderingHandler(Chart chart) {
    this.chart = chart;
    this.drawableToRevert = new ArrayList<>();
  }
  

  @Override
  public void apply() {
    Graph graph = chart.getScene().getGraph();
    for (Drawable d : graph.getAll()) {
      if (d instanceof Wireframeable) {
        Wireframeable w = (Wireframeable) d;
        
        drawableToRevert.add(makeToggle(w));
  
        applyOptimisation(w);
      }
    }
  }

  protected Toggle makeToggle(Wireframeable w) {
    return new Toggle(w);
  }

  @Override
  public void revert() {
    for (Toggle s : drawableToRevert) {
      s.reset();
    }
    drawableToRevert.clear();
  }
}
