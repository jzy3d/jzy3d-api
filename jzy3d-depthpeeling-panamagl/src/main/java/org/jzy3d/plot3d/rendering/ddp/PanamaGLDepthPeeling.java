package org.jzy3d.plot3d.rendering.ddp;

import org.jzy3d.chart.factories.APanamaGLPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;

/**
 * Render the charts of any PanamaGL chart factory with depth peeling, whatever its toolkit (Swing,
 * JavaFX, SWT) or offscreen.
 * 
 * <pre>
 * <code>
 * ChartFactory factory = PanamaGLDepthPeeling.enable(new PanamaGLJavaFXChartFactory(), PeelingMethod.DUAL_PEELING_MODE);
 * Chart chart = factory.newChart();
 * </code>
 * </pre>
 * 
 * {@link org.jzy3d.chart.factories.PanamaGLDepthPeelingChartFactory} does the same for Swing.
 */
public class PanamaGLDepthPeeling {
  /**
   * Make the charts created afterward by this factory render with depth peeling.
   * 
   * @throws IllegalArgumentException if the factory does not render with PanamaGL.
   */
  public static <T extends IChartFactory> T enable(T factory, PeelingMethod method) {
    IPainterFactory painterFactory = factory.getPainterFactory();

    if (!(painterFactory instanceof APanamaGLPainterFactory)) {
      throw new IllegalArgumentException("Depth peeling requires a PanamaGL chart factory, not "
          + factory.getClass().getName());
    }

    ((APanamaGLPainterFactory) painterFactory)
        .setRenderer3DFactory(view -> new PanamaGLDepthPeelingRenderer3D(method, view));
    return factory;
  }

  /** Make the charts created afterward by this factory render without depth peeling. */
  public static <T extends IChartFactory> T disable(T factory) {
    if (factory.getPainterFactory() instanceof APanamaGLPainterFactory) {
      ((APanamaGLPainterFactory) factory.getPainterFactory()).setRenderer3DFactory(null);
    }
    return factory;
  }
}
