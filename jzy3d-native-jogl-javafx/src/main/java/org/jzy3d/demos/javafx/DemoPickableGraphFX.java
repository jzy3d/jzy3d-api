package org.jzy3d.demos.javafx;

import java.util.List;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IObjectPickedListener;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.colors.Color;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.javafx.JavaFXRenderer3d;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.graphs.IGraph;
import org.jzy3d.maths.graphs.StringGraphGenerator;
import org.jzy3d.plot3d.primitives.graphs.impl.PointGraph2d;
import org.jzy3d.plot3d.primitives.graphs.layout.DefaultGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphFormatter;
import org.jzy3d.plot3d.primitives.graphs.layout.IGraphLayout2d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
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
@SuppressWarnings("restriction")
public class DemoPickableGraphFX extends Application {
  public static int NODES = 500;
  public static int EDGES = 100;
  public static float GL_LAYOUT_SIZE = 10;
  public static float VERTEX_WIDTH = 10;

  public static void main(String[] args) {
    Application.launch(args);
  }

  AWTNativeChart chart;
  IMousePickingController mouse;

  @Override
  public void start(Stage stage) {
    stage.setTitle(DemoPickableGraphFX.class.getSimpleName());

    // Jzy3d
    JavaFXChartFactory factory = new JavaFXChartFactory();
    initChart(factory, "offscreen");
    ImageView imageView = factory.bindImageView(chart);

    ((JavaFXChartController) mouse).setNode(imageView); // allow picking

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

  public void initChart(JavaFXChartFactory factory, String toolkit) {
    // Init graph
    IGraph<String, String> graph = StringGraphGenerator.getGraph(NODES, EDGES);
    IGraphLayout2d<String> layout = StringGraphGenerator.getRandomLayout(graph, GL_LAYOUT_SIZE);
    IGraphFormatter<String, String> formatter = new DefaultGraphFormatter<String, String>();
    formatter.setVertexColor(new Color(0, 64 / 255f, 84 / 255f));
    formatter.setVertexLabelColor(new Color(0, 64 / 255f, 84 / 255f));
    formatter.setEdgeColor(new Color(0, 64 / 255f, 84 / 255f));
    formatter.setHighlightedVertexColor(new Color(247 / 255f, 79 / 255f, 119 / 255f));

    // Setup a chart
    Quality quality = Quality.Advanced();
    // quality.setDepthActivated(false);


    chart = (AWTNativeChart) factory.newChart(quality);
    chart.getView().setAxisDisplayed(false);
    chart.getView().setViewPositionMode(ViewPositionMode.TOP);
    chart.getView().setSquared(false);
    // chart.getView().setMaximized(true);

    // Build a drawable graph
    final PointGraph2d<String, String> dGraph = new PointGraph2d<String, String>();

    mouse = chart.addMousePickingController((int) VERTEX_WIDTH);// new
                                                                // JavaFXMousePickingController(chart,
                                                                // (int)VERTEX_WIDTH);
    mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
      @Override
      public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
        for (Object vertex : vertices) {
          System.out.println("picked: " + vertex);
          dGraph.setVertexHighlighted((String) vertex, true);
        }
        chart.render();
      }
    });

    dGraph.setGraphModel(graph, mouse.getPickingSupport());
    // dGraph.setGraphModel(graph);
    dGraph.setGraphFormatter(formatter);
    dGraph.setGraphLayout(layout);

    chart.getScene().getGraph().add(dGraph);
  }
}
