package org.jzy3d.javafx.offscreen;

import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.maths.Rectangle;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;

public class JavaFXOffscreenChartFactory extends AWTChartFactory {
  public static Rectangle DEFAULT_DIMENSION = new Rectangle(800, 600);

  protected JavaFXOffscreenBinding binding = new JavaFXOffscreenBinding();

  public JavaFXOffscreenChartFactory() {
    super(new JavaFXOffscreenPainterFactory());

    getPainterFactory().setOffscreen(800, 600);
  }

  public JavaFXOffscreenChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);

    getPainterFactory().setOffscreen(DEFAULT_DIMENSION);
  }

  /** Return a JavaFX canvas backed by a Jzy3D {@link OffscreenCanvas} providing pictures
   * to draw in a JavaFX application.
   * 
   */
  public Canvas bindCanvas(AWTNativeChart chart, boolean mouseController, boolean keyController) {
    return binding.bindCanvas(chart, mouseController, keyController);
  }
  
  public Canvas bindCanvas(AWTNativeChart chart) {
    return binding.bindCanvas(chart, true, true);
  }

  public JavaFXOffscreenBinding getBinding() {
    return binding;
  }
  
  /**
   * Use {@link #bindCanvas(AWTNativeChart)} instead.
   * 
   * This will allow retrieving a widget that properly resizes in applications made of other
   * widgets.
   * 
   * It will autonomously monitor the canvas size to resize picture and won't need to call the
   * former {@link #addSceneSizeChangedListener(Chart, Scene)} method.
   */
  @Deprecated
  public ImageView bindImageView(AWTNativeChart chart) {
    return binding.bindImageView(chart);
  }

  @Deprecated
  public void addSceneSizeChangedListener(Chart chart, Scene scene) {
    binding.addSceneSizeChangedListener(chart, scene);
  }


}
