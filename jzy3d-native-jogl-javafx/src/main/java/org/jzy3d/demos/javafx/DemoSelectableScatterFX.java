package org.jzy3d.demos.javafx;

import java.util.Random;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.controllers.mouse.AWTDualModeMouseSelector;
import org.jzy3d.chart.controllers.mouse.selection.AWTScatterMouseSelector;
import org.jzy3d.colors.Color;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.javafx.JavaFXRenderer3d;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.selectable.SelectableScatter;
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
 * Keyboard (rotate/shift, etc) Animation (camera rotation with thread)
 * 
 * TODO : Mouse right click shift
 * 
 * @author Martin Pernollet
 */
@SuppressWarnings("restriction")

public class DemoSelectableScatterFX extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle(DemoSelectableScatterFX.class.getSimpleName());

    // Jzy3d
    JavaFXChartFactory factory = new JavaFXChartFactory();
    AWTNativeChart chart = getDemoChart(factory, "offscreen");
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

  private AWTNativeChart getDemoChart(JavaFXChartFactory factory, String toolkit) {
    Quality quality = Quality.Advanced();
    int POINTS = 1000;
    SelectableScatter scatter = generateScatter(POINTS);
    AWTNativeChart chart = (AWTNativeChart) factory.newChart(quality);
    chart.getScene().add(scatter);
    chart.getView().setMaximized(true);

    AWTScatterMouseSelector selector = new AWTScatterMouseSelector(scatter, chart);
    new AWTDualModeMouseSelector(chart, selector);
    return chart;
  }

  protected SelectableScatter generateScatter(int npt) {
    Coord3d[] points = new Coord3d[npt];
    Color[] colors = new Color[npt];
    Random rng = new Random();
    rng.setSeed(0);
    for (int i = 0; i < npt; i++) {
      colors[i] = new Color(0, 64 / 255f, 84 / 255f);
      points[i] = new Coord3d(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
    }
    SelectableScatter dots = new SelectableScatter(points, colors);
    dots.setWidth(1);
    dots.setHighlightColor(Color.YELLOW);
    return dots;
  }
}
