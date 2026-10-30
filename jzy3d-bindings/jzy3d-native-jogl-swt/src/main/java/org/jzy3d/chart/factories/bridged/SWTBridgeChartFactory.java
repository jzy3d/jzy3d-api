package org.jzy3d.chart.factories.bridged;

import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.chart.factories.CanvasNewtSWT;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SWTChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.newt.opengl.GLWindow;

public class SWTBridgeChartFactory extends SWTChartFactory {
  public SWTBridgeChartFactory() {
    super();
    setPainterFactory(new SWTBridgePainterFactory());
  }

  /* */

  /** Dedicated to {@link CanvasNewtSWT} implementation. */
  public NativeAnimator newAnimator(GLWindow canvas) {
    return new NativeAnimator(canvas);
  }

  /**
   * The AWTView support Java2d defined components (tooltips, background images)
   */
  @Override
  public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new AWTView(factory, scene, canvas, quality);
  }

  @Override
  public IChartFactory getFactory() {
    return this;
  }
}
