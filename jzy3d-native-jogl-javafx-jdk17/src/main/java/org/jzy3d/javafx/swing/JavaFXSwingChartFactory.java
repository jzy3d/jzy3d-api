package org.jzy3d.javafx.swing;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class JavaFXSwingChartFactory extends SwingChartFactory{

  public JavaFXSwingChartFactory() {
    this(new JavaFXSwingPainterFactory());
  }
  
  public JavaFXSwingChartFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }



  public void open(Chart chart, Stage stage) {
    // JavaFX
    SwingNode swingNode = new SwingNode();

    // Fill node with canvas
    ICanvas canvas = chart.getCanvas();

    if(canvas instanceof JComponent)
      swingNode.setContent((JComponent)canvas);
    else if(canvas instanceof Component) {
      JPanel embedding = new JPanel();
      embedding.add((Component)canvas);
      swingNode.setContent(embedding);

    }
      
    
    
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
