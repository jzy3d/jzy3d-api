package org.jzy3d.demos.javafx;

import static java.lang.Math.E;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.javafx.JavaFXRenderer3d;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilderListCoord3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.ScatterVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Showing how to pipe an offscreen Jzy3d chart image to a JavaFX ImageView.
 * 
 * {@link JavaFXChartFactory} delivers dedicated {@link JavaFXCameraMouseController} and
 * {@link JavaFXRenderer3d}
 * 
 * Support Rotation control with left mouse button hold+drag Scaling scene using mouse wheel
 * Animation (camera rotation with thread)
 * 
 * TODO : Mouse right click shift Keyboard support (rotate/shift, etc)
 * 
 * @author Martin Pernollet
 */
public class DemoJzy3dFXProblem extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle(DemoJzy3dFXProblem.class.getSimpleName());

    // Jzy3d
    JavaFXChartFactory factory = new JavaFXChartFactory();
    AWTNativeChart chart = getDemoChartVBO(factory, "offscreen");
    ImageView imageView = factory.bindImageView(chart);

    // JavaFX
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    stage.show();
    pane.getChildren().add(imageView);


    stage.setWidth(500);
    stage.setHeight(500);

    factory.addSceneSizeChangedListener(chart, scene);
  }

  public static int MILION = 1000000;

  private AWTNativeChart getDemoChartVBO(JavaFXChartFactory factory, String toolkit) {
    float ratio = 1.0f;
    int size = (int) (ratio * MILION);

    List<Coord3d> coords = getScatter(size);
    ColorMapper coloring = coloring(coords);
    ScatterVBO drawable = new ScatterVBO(new VBOBuilderListCoord3d(coords, coloring));

    // -------------------------------
    // Create a chart
    Quality quality = Quality.Advanced();
    // quality.setSmoothPolygon(true);
    // quality.setAnimated(true);

    // let factory bind mouse and keyboard controllers to JavaFX node
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    chart.getScene().getGraph().add(drawable);
    return chart;
  }

  private AWTChart getDemoChart(JavaFXChartFactory factory, String toolkit) {
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
    // quality.setAnimated(true);

    // let factory bind mouse and keyboard controllers to JavaFX node
    AWTChart chart = (AWTChart) factory.newChart(quality);
    chart.getScene().getGraph().add(surface);
    return chart;
  }


  public static List<Coord3d> getScatter(int size) {
    List<Coord3d> coords = new ArrayList<Coord3d>(size);
    Random r = new Random();
    r.setSeed(0);
    double x;
    double y;
    double z;
    for (int i = 0; i < size; i++) {
      x = 2.0 * r.nextDouble();
      y = 2.0 * r.nextDouble();
      double s = r.nextDouble();

      if (s > 0.75) {
        x *= -1;
        y *= -1;
      }
      if (s > 0.5 && s <= 0.75) {
        x *= -1;
      }
      if (s > 0.25 && s <= 0.5) {
        y *= -1;
      }

      // exp( -(x**2 + y**2) ) * cos(x/4)*sin(y) * cos(2*(x**2+y**2))
      double f1 = cos(2 * (x * x + y * y));
      double f2 = cos(x / 4) * sin(y);
      double f3 = x * x + y * y;
      z = pow(E, -f3) * f2 * f1;

      coords.add(new Coord3d(x, y, z));
    }
    return coords;
  }

  public static ColorMapper coloring(List<Coord3d> coords) {
    Range zrange = Coord3d.getZRange(coords);
    ColorMapper coloring = new ColorMapper(new ColorMapRainbow(), zrange.getMin(), zrange.getMax(),
        new Color(1, 1, 1, .5f));
    return coloring;
  }
}
