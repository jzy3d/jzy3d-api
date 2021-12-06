package org.jzy3d.factories;

import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingView;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class DepthPeelingChartFactory extends AWTChartFactory {
  public DepthPeelingChartFactory() {
    this(PeelingMethod.DUAL_PEELING_MODE);
  }

  public DepthPeelingChartFactory(PeelingMethod method) {
    super(new DepthPeelingPainterFactory());
    this.method = method;
  }
  
  public DepthPeelingChartFactory(IPainterFactory painterFactory, PeelingMethod method) {
    super(painterFactory);
    this.method = method;
  }
  
  @Override
  public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new DepthPeelingView(factory, scene, canvas, quality);
  }

  public static boolean CHART_CANVAS_AUTOSWAP = false;

  PeelingMethod method;
}
