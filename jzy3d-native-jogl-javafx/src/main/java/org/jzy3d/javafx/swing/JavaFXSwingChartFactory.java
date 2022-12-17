package org.jzy3d.javafx.swing;

import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class JavaFXSwingChartFactory extends SwingChartFactory{

  public JavaFXSwingChartFactory() {
    super(new JavaFXSwingPainterFactory());
  }
  
  public void open(Chart chart, Stage stage) {
    // JavaFX
    SwingNode swingNode = new SwingNode();
    //swingNode.setContent(jscrollPane);
    swingNode.setContent((JPanel)chart.getCanvas());
    
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
    stage.setScene(scene);
    stage.show();

  }

}
