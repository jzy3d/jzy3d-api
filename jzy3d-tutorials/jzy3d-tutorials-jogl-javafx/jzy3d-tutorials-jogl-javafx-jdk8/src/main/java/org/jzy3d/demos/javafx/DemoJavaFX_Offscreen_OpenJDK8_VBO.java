package org.jzy3d.demos.javafx;

import static java.lang.Math.E;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilderListCoord3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.ScatterVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


@SuppressWarnings("restriction")
public class DemoJavaFX_Offscreen_OpenJDK8_VBO extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle(DemoJavaFX_Offscreen_OpenJDK8_VBO.class.getSimpleName());

    // Jzy3d
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    AWTNativeChart chart = getDemoChartVBO(factory);
    Canvas canvas = factory.bindCanvas(chart);

    // JavaFX
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    pane.getChildren().add(canvas);
    stage.show();



    stage.setWidth(500);
    stage.setHeight(500);

  }

  public static int MILION = 1000000;

  private AWTNativeChart getDemoChartVBO(JavaFXOffscreenChartFactory factory) {
    float ratio = 1.0f;
    int size = (int) (ratio * MILION);

    List<Coord3d> coords = getScatter(size);
    ColorMapper coloring = coloring(coords);
    ScatterVBO drawable = new ScatterVBO(new VBOBuilderListCoord3d(coords, coloring));

    // -------------------------------
    // Create a chart
    Quality quality = Quality.Advanced();

    // let factory bind mouse and keyboard controllers to JavaFX node
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    chart.getScene().getGraph().add(drawable);
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
