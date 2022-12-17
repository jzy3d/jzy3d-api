package org.jzy3d.demos.javafx;

import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.events.IViewEventListener;
import org.jzy3d.events.ViewIsVerticalEvent;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory;
import org.jzy3d.javafx.offscreen.JavaFXOffscreenRenderer3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Showing how to pipe an offscreen Jzy3d chart image to a JavaFX ImageView.
 * 
 * {@link JavaFXOffscreenChartFactory} delivers dedicated {@link JavaFXCameraMouseController} and
 * {@link JavaFXOffscreenRenderer3d}
 * 
 * Support Rotation control with left mouse button hold+drag Scaling scene using mouse wheel
 * Animation (camera rotation with thread)
 * 
 * TODO : Mouse right click shift Keyboard support (rotate/shift, etc)
 * 
 * 
 * Will run on OpenJDK8, require jxfrt.jar to be available
 * 
 * @author Martin Pernollet
 */
public class DemoJzy3dFX_OpenJDK8 extends Application {
  public static Application app;
  
  public static void main(String[] args) {
    Application.launch(args);
    
    
  }
     

  @Override
  public void start(Stage stage) {
    app = this;
    
    stage.setTitle(DemoJzy3dFX_OpenJDK8.class.getSimpleName());

    // Jzy3d
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    AWTNativeChart chart = getDemoChart(factory);
    ImageView imageView = factory.bindImageView(chart);

    // JavaFX
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    pane.getChildren().add(imageView);
    stage.show();

    factory.addSceneSizeChangedListener(chart, scene);
    

    // DO THIS AFTER START, at this step, window is not displayed!
    stage.setWidth(500);
    stage.setHeight(500);
    stage.setWidth(500);
    stage.setHeight(700);
    
    //chart.getView().setMa
    //stage.set
    //stage.setWidth(499);
    
    //chart.render(100);

    chart.getView().addViewEventListener(new IViewEventListener() {
      @Override
      public void viewFirstRenderStarts() {
      }
      @Override
      public void viewVerticalReached(ViewIsVerticalEvent e) {
      }
      @Override
      public void viewVerticalLeft(ViewIsVerticalEvent e) {
      }
      
    });


  }

  private AWTNativeChart getDemoChart(JavaFXOffscreenChartFactory factory) {
    // -------------------------------
    // Define a function to plot
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface = new SurfaceBuilder().orthonormal(mapper, range, steps);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .95f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // -------------------------------
    // Create a chart
    Quality quality = Quality.Advanced();
    //quality.setAnimated(true);
    
    //System.out.println(quality.isAnimated());

    // let factory bind mouse and keyboard controllers to JavaFX node
    factory.getPainterFactory().setOffscreen(800, 600);
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    chart.getScene().getGraph().add(surface);
    return chart;
  }
}
