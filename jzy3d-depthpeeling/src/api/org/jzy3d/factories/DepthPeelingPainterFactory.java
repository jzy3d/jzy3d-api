package org.jzy3d.factories;

import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingRenderer3d;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingView;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GLCapabilities;

public class DepthPeelingPainterFactory extends AWTPainterFactory {
  public DepthPeelingPainterFactory() {
    super();
  }
  
  public DepthPeelingPainterFactory(GLCapabilities capabilities) {
    super(capabilities);
  }


  @Override
  public Renderer3d newRenderer3D(View view) {
    PeelingMethod method = ((DepthPeelingChartFactory) getChartFactory()).method;
    DepthPeelingRenderer3d r =
        new DepthPeelingRenderer3d(method, (DepthPeelingView) view, traceGL, debugGL);
    return r;
  }
}
