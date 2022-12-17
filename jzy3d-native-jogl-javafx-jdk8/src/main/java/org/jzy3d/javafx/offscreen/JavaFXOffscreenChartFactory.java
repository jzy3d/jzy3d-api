package org.jzy3d.javafx.offscreen;

import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.maths.Rectangle;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public class JavaFXOffscreenChartFactory extends AWTChartFactory {
  public static Rectangle DEFAULT_DIMENSION = new Rectangle(800,600);
  
  protected JavaFXOffscreenBinding binding = new JavaFXOffscreenBinding();

  public JavaFXOffscreenChartFactory() {
    super(new JavaFXOffscreenPainterFactory());
    
    getPainterFactory().setOffscreen(800, 600);
  }

  public JavaFXOffscreenChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);
    
    getPainterFactory().setOffscreen(DEFAULT_DIMENSION);
  }
  
  
  
  public ImageView bindImageView(AWTNativeChart chart) {
    return binding.bindImageView(chart);
  }
  
  public void addSceneSizeChangedListener(Chart chart, Scene scene) {
    binding.addSceneSizeChangedListener(chart, scene);
  }
  
  public JavaFXOffscreenBinding getBinding() {
    return binding;
  }
}
