package org.jzy3d.javafx.controllers.mouse;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.javafx.controllers.JavaFXChartController;
import org.jzy3d.maths.Coord2d;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class JavaFXCameraMouseController extends AbstractCameraController
    implements JavaFXChartController {

  protected Node node;

  public JavaFXCameraMouseController(Node node) {
    super();
    updateViewDefault = true;
    register(node);
  }

  public JavaFXCameraMouseController(Chart chart, Node node) {
    super(chart);
    register(node);
    register(chart);

    CameraThreadController threadCam = new JavaFXCameraThreadController(chart);
    threadCam.setStep(0.005f);
    addSlaveThreadController(threadCam);
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public void setNode(Node node) {
    register(node);
  }

  protected void register(Node node) {
    this.node = node;

    if (node == null)
      return;

    // ON MOUSE PRESS
    node.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        node.setCursor(Cursor.MOVE);
        mousePressed(mouseEvent);
        // console(mouseEvent);
        handleSlaveThread(mouseEvent);
      }

    });

    // ON MOUSE DRAG
    node.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        mouseDragged(mouseEvent);
        // console(mouseEvent);
      }
    });

    node.setOnScroll(new EventHandler<ScrollEvent>() {
      @Override
      public void handle(ScrollEvent event) {
        mouseWheelMoved(event);
      }
    });
  }

  /* ################################### */

  protected void mousePressed(MouseEvent e) {
    if (handleSlaveThread(e))
      return;
    prevMouse.x = (float) e.getX();
    prevMouse.y = (float) e.getY();
  }

  protected void mouseDragged(MouseEvent e) {
    Coord2d mouse = new Coord2d(e.getX(), e.getY());
    // Rotate
    if (isLeftDown(e)) {
      Coord2d move = mouse.sub(prevMouse).div(100);
      rotate(move);
      for (Chart chart : targets) {
        chart.render();
      }
    }
    // Shift
    else if (isRightDown(e)) {
      Coord2d move = mouse.sub(prevMouse);
      if (move.y != 0)
        shift(move.y / 500);
    }
    prevMouse = mouse;
  }

  protected void mouseWheelMoved(ScrollEvent e) {
    stopThreadController();
    float factor = 1 + (float) e.getDeltaY() / 150;
    zoomZ(factor);
  }

  public boolean handleSlaveThread(MouseEvent e) {
    if (isDoubleClick(e)) {
      if (threadController != null) {
        threadController.start();
        return true;
      }
    }
    if (threadController != null)
      threadController.stop();
    return false;
  }

  public static boolean isLeftDown(MouseEvent e) {
    return true;// (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
  }

  public static boolean isRightDown(MouseEvent e) {
    return false;// (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
  }

  public static boolean isDoubleClick(MouseEvent e) {
    // System.out.println(e.getClickCount());
    return (e.getClickCount() > 1);
  }

  public static void console(MouseEvent mouseEvent) {
    System.out.println(JavaFXCameraMouseController.class.getName() + " " + mouseEvent.getX() + ", "
        + mouseEvent.getY());
  }
}
