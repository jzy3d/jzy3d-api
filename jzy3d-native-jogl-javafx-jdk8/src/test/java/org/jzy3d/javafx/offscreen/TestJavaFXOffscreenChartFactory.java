package org.jzy3d.javafx.offscreen;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;

public class TestJavaFXOffscreenChartFactory {
  double delta = 0.00001;

  @Test
  public void init() {
    
    
    // Given an offscreen javafx factory
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    
    // Then it is configured offscreen
    Assert.assertTrue(factory.getPainterFactory().isOffscreen());
    
    // ------------------------------
    // Given a chart with content
    AWTNativeChart chart = getDemoChart(factory);
    
    // Then we can bind an image view
    ImageView imageView = factory.bindImageView(chart);
    
    Assert.assertNotNull(imageView);
    
    // Then the offscreen canvas is properly sized
    OffscreenCanvas canvas = (OffscreenCanvas)chart.getCanvas();
    
    Dimension d = canvas.getDimension();
    
    Assert.assertEquals(factory.getPainterFactory().getOffscreenDimension(), d);
        
    
    
    
    // Check animation status
    
    // Check screenshot available
    
    
    // Mouse simply wraps the rest

  }
  
  @Test
  public void resize() {
    
    
    // Given an offscreen javafx factory and related components
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    AWTNativeChart chart = getDemoChart(factory);
    ImageView imageView = factory.bindImageView(chart);
    
    OffscreenCanvas canvas = (OffscreenCanvas)chart.getCanvas();
    JavaFXOffscreenRenderer3d jfxRenderer = (JavaFXOffscreenRenderer3d)canvas.getRenderer();

    // Then the offscreen canvas is properly sized
    Dimension d = canvas.getDimension();
    Assert.assertEquals(factory.getPainterFactory().getOffscreenDimension(), d);
    
    // ------------------------------
    // Given a resize 
    Dimension newDim = new Dimension(1220, 660);
    factory.getBinding().resetTo(chart, newDim.width, newDim.height);
    
    // Then canvas is resized
    Assert.assertEquals(newDim, canvas.getDimension());

    
    // Then javafximage is resized as well
    
    Image image = jfxRenderer.getLastJavaFXScreenshotImage();
    
    Assert.assertEquals(newDim.width, image.getWidth(), delta);
    Assert.assertEquals(newDim.height, image.getHeight(), delta);
    
    // Check animation status
    
    // Check screenshot available
    
    
    // Mouse simply wraps the rest

  }
  
  class MockableJFxScene extends Scene{
    public MockableJFxScene() {
      super(null);
    }

    public MockableJFxScene(Parent root, double width, double height, boolean depthBuffer,
        SceneAntialiasing antiAliasing) {
      super(root, width, height, depthBuffer, antiAliasing);
    }
    public MockableJFxScene(Parent root, double width, double height, boolean depthBuffer) {
      super(root, width, height, depthBuffer);
    }
    public MockableJFxScene(Parent root, double width, double height, Paint fill) {
      super(root, width, height, fill);
    }
    public MockableJFxScene(Parent root, double width, double height) {
      super(root, width, height);
    }
    public MockableJFxScene(Parent root, Paint fill) {
      super(root, fill);
    }
    public MockableJFxScene(Parent root) {
      super(root);
    }
    
  }
  
  
  
  private AWTNativeChart getDemoChart(JavaFXOffscreenChartFactory factory) {

    // Define a function to plot
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Create the object to represent the function over the given range.
    final Shape surface = new SurfaceBuilder().orthonormal(mapper, new Range(-3, 3), 80);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .95f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // Create a chart
    Quality quality = Quality.Advanced();
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    chart.getScene().getGraph().add(surface);
    return chart;
  }

}
