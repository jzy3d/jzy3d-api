package org.jzy3d.javafx.controllers.keyboard;

import java.io.IOException;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.screenshot.AbstractScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/** Save a screenshot of the chart when the 's' key is typed on the JavaFX node of the chart. */
public class JavaFXScreenshotKeyController extends AbstractScreenshotKeyController
    implements EventHandler<KeyEvent>, IScreenshotKeyController, JavaFXChartController {
  protected Node node;

  public JavaFXScreenshotKeyController(Chart chart, Node node, String outputFile) {
    super(chart, outputFile);
    setNode(node);
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public void setNode(Node node) {
    this.node = node;
    if (node != null) {
      node.addEventHandler(KeyEvent.KEY_TYPED, this);
    }
  }

  @Override
  public void handle(KeyEvent e) {
    if ("s".equals(e.getCharacter())) {
      try {
        screenshot(chart, outputFile);
        fireDone(outputFile);
      } catch (IOException ex) {
        fireError(outputFile, ex);
      }
    }
  }
}
