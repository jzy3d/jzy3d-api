package org.jzy3d.demos.javafx;
import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// https://openjdk.org/jeps/261#Breaking-encapsulation
//
// Required JVM args
// 
// --module-path /Users/martin/Dev/javafx-sdk-19/lib --add-modules javafx.base --add-opens javafx.base/com.sun.javafx=ALL-UNNAMED --add-modules javafx.controls --add-opens javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED --add-opens javafx.graphics/javafx.stage=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.util=ALL-UNNAMED   --add-opens javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.tk.quantum=ALL-UNNAMED --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED

public class SwingInFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
      
      // -------------------------------
      // Chart content

      Mapper mapper = new Mapper() {
        @Override
        public double f(double x, double y) {
          return x * Math.sin(x * y);
        }
      };

      final Shape surface = new SurfaceBuilder().orthonormal(mapper, new Range(-3, 3), 80);
      surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .75f)));
      surface.setFaceDisplayed(true);
      surface.setWireframeDisplayed(false);

      // -------------------------------
      // Create a chart
      Quality quality = Quality.Advanced();
      //quality.setAnimated(true);
      
      ChartFactory factory = new SwingChartFactory();
      Chart chart = factory.newChart(quality);

      
      JPanel jcanvas = (JPanel)chart.getCanvas();
      chart.getScene().getGraph().add(surface);

      
      // -------------------------------
      // Swing node
      SwingNode swingNode = new SwingNode();
      //swingNode.setContent(jscrollPane);
      swingNode.setContent(jcanvas);
      
      // Camera mouse
      JavaFXCameraMouseController mouse = new JavaFXCameraMouseController(chart);
      mouse.setNode(swingNode);
      
      // Resizable pane for Swing node
      AnchorPane detailPane = new AnchorPane();
      AnchorPane.setTopAnchor(swingNode, 0d);
      AnchorPane.setBottomAnchor(swingNode, 0d);
      AnchorPane.setRightAnchor(swingNode, 0d);
      AnchorPane.setLeftAnchor(swingNode, 0d);  
      detailPane.getChildren().add(swingNode);

            
      Scene scene = new Scene(detailPane, 400, 300);
      primaryStage.setScene(scene);
      primaryStage.show();
    }
}
