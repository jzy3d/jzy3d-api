package org.jzy3d.chart.factories;

import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.newt.opengl.GLWindow;

public class SWTChartFactory extends ChartFactory {
  // private static final Logger logger = Logger.getLogger(SWTChartFactory.class);

  protected Composite canvas;

  public SWTChartFactory() {}

  public SWTChartFactory(Composite canvas) {
    super(new SWTPainterFactory());

    this.canvas = canvas;
  }

  public SWTChartFactory(Composite canvas, IPainterFactory painterFactory) {
    super(painterFactory);
    this.canvas = canvas;
  }

  public static Chart chart(Composite parent) {
    SWTChartFactory f = new SWTChartFactory(parent);
    return f.newChart(Quality.Intermediate());
  }

  public static Chart chart(Composite parent, Quality quality) {
    SWTChartFactory f = new SWTChartFactory(parent);
    return f.newChart(quality);
  }

  /* */

  /**
   */
  @Override
  public Chart newChart(IChartFactory factory, Quality quality) {
    return new SWTChart(canvas, factory, quality);
  }

  /** Dedicated to {@link CanvasNewtSWT} implementation. */
  public NativeAnimator newAnimator(GLWindow canvas) {
    return new NativeAnimator(canvas);
  }

  public Composite getComposite() {
    return canvas;
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
