package org.jzy3d.demos.javafx;

import java.util.List;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IObjectPickedListener;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.colors.Color;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import org.jzy3d.javafx.offscreen.JavaFXOffscreenChartFactory;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
    JavaFXOffscreenChartFactory factory = new JavaFXOffscreenChartFactory();
    initChart(factory);
    Canvas canvas = factory.bindCanvas(chart);

    ((JavaFXChartController) mouse).setNode(canvas); // allow picking

    // JavaFX
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    stage.setScene(scene);
    stage.show();
    pane.getChildren().add(canvas);

    stage.setWidth(500);
    stage.setHeight(500);
  }

  public void initChart(JavaFXOffscreenChartFactory factory) {
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
