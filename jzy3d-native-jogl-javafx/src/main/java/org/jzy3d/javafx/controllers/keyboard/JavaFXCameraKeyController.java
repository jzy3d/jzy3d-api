package org.jzy3d.javafx.controllers.keyboard;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import org.jzy3d.maths.Coord2d;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

public class JavaFXCameraKeyController extends AbstractCameraController
    implements EventHandler<KeyEvent>, ICameraKeyController, JavaFXChartController {
  protected Node node;

  public JavaFXCameraKeyController(Node node) {
    register(node);
  }

  public JavaFXCameraKeyController(Chart chart, Node node) {
    register(chart);
    register(node);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public void setNode(Node node) {
    register(node);
  }

  private void register(Node node) {
    this.node = node;

    if (node == null)
      return;

    node.setOnKeyPressed(this);
  }

  @Override
  public void handle(KeyEvent e) {

    if (!e.isShiftDown()) {
      Coord2d move = new Coord2d();
      float offset = 0.1f;

      switch (e.getCode()) {
        case UP:
          move.y = move.y - offset;
          rotate(move);
          break;
        case DOWN:
          move.y = move.y + offset;
          rotate(move);
          break;
        case LEFT:
          move.x = move.x - offset;
          rotate(move);
          break;
        case RIGHT:
          move.x = move.x + offset;
          rotate(move);
          break;
      }

    } else {
      switch (e.getCode()) {
        case UP:
          shift(-0.1f);
          break;
        case DOWN:
          shift(0.1f);
          break;
        case LEFT:
          zoomZ(0.9f);
          break;
        case RIGHT:
          zoomZ(1.1f);
          break;
      }

    }
  }
}
