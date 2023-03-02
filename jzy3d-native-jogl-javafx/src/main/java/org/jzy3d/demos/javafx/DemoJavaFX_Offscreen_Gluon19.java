package org.jzy3d.demos.javafx;

import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.GLProfile;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Will require JDK17, Gluon's JavaFX 19 in classpath
 * 
--module-path /Users/martin/Dev/javafx-sdk-19/lib --add-modules javafx.controls -Dnativewindow.debug.JAWT --add-exports=java.desktop/sun.awt=ALL-UNNAMED
 * 
 * 
 * 
 * @author Martin Pernollet
 */
public class DemoJavaFX_Offscreen_Gluon19 extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    GLProfile.initSingleton();
    
    stage.setTitle(DemoJavaFX_Offscreen_Gluon19.class.getSimpleName());

    // Jzy3d
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    AWTNativeChart chart = getDemoChart(factory);
    ImageView imageView = factory.bindImageView(chart);

    // JavaFX
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    stage.show();
    pane.getChildren().add(imageView);


    factory.addSceneSizeChangedListener(chart, scene);

    stage.setWidth(500);
    stage.setHeight(500);
    

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
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // -------------------------------
    // Create a chart
    Quality quality = Quality.Advanced();
    // quality.setSmoothPolygon(true);
    //quality.setAnimated(true);

    // let factory bind mouse and keyboard controllers to JavaFX node
    factory.getPainterFactory().setOffscreen(800, 600);
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    //chart.getView().setAxisDisplayed(false);
    chart.getScene().getGraph().add(surface);
    
    return chart;
  }
}
