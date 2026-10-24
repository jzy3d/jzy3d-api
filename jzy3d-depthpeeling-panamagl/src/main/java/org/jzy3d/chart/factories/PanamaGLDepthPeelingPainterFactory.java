package org.jzy3d.chart.factories;

import org.jzy3d.plot3d.rendering.canvas.Renderer3D;
import org.jzy3d.plot3d.rendering.ddp.PanamaGLDepthPeelingRenderer3D;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.View;

/** A PanamaGL Swing painter factory rendering charts with depth peeling. */
public class PanamaGLDepthPeelingPainterFactory extends PanamaGLSwingPainterFactory {
  protected PeelingMethod peelingMethod = PeelingMethod.DUAL_PEELING_MODE;

  public PanamaGLDepthPeelingPainterFactory() {
    super();
  }

  public PanamaGLDepthPeelingPainterFactory(PeelingMethod peelingMethod) {
    super();
    this.peelingMethod = peelingMethod;
  }

  @Override
  public Renderer3D newRenderer3D(View view) {
    return new PanamaGLDepthPeelingRenderer3D(peelingMethod, view);
  }

  public PeelingMethod getPeelingMethod() {
    return peelingMethod;
  }

  public void setPeelingMethod(PeelingMethod peelingMethod) {
    this.peelingMethod = peelingMethod;
  }
}
