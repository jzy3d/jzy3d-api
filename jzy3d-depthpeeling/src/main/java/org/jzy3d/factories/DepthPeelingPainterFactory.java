package org.jzy3d.factories;

import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.plot3d.rendering.ddp.DepthPeelingRenderer3d;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GLCapabilities;

public class DepthPeelingPainterFactory extends AWTPainterFactory {
  protected PeelingMethod peelingMethod = PeelingMethod.DUAL_PEELING_MODE;
  
  public DepthPeelingPainterFactory() {
    super();
  }
  
  public DepthPeelingPainterFactory(GLCapabilities capabilities) {
    super(capabilities);
  }


  @Override
  public Renderer3d newRenderer3D(View view) {
    return new DepthPeelingRenderer3d(peelingMethod, view, traceGL, debugGL);
  }

  public PeelingMethod getPeelingMethod() {
    return peelingMethod;
  }

  public void setPeelingMethod(PeelingMethod peelingMethod) {
    this.peelingMethod = peelingMethod;
  }
  

}
