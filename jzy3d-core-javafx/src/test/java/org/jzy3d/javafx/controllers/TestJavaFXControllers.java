package org.jzy3d.javafx.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.picking.PickingSupport;
import org.jzy3d.javafx.controllers.keyboard.JavaFXScreenshotKeyController;
import org.jzy3d.javafx.controllers.mouse.JavaFXMousePickingController;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TestJavaFXControllers {

  @BeforeClass
  public static void initJavaFX() {
    try {
      Platform.startup(() -> {});
    } catch (IllegalStateException alreadyRunning) {
      // Another test already started it
    } catch (UnsupportedOperationException noDisplay) {
      org.junit.Assume.assumeNoException(noDisplay);
    }
  }

  @Test
  public void whenTypingS_ThenScreenshotIsSaved() throws Exception {
    Chart chart = mockChart();
    Pane node = new Pane();

    new JavaFXScreenshotKeyController(chart, node, "target/screenshot.png");

    runOnFxThreadAndWait(() -> Event.fireEvent(node, new KeyEvent(KeyEvent.KEY_TYPED, "s", "",
        KeyCode.UNDEFINED, false, false, false, false)));

    verify(chart).screenshot(new File("target/screenshot.png"));
  }

  @Test
  public void whenMousePressed_ThenPickAndKeepOtherHandlers() throws Exception {
    Chart chart = mockChart();
    Pane node = new Pane();

    // A camera controller set its handler before picking
    AtomicBoolean cameraHandled = new AtomicBoolean(false);
    node.setOnMousePressed(e -> cameraHandled.set(true));

    JavaFXMousePickingController controller = new JavaFXMousePickingController(chart, 5);
    PickingSupport picking = mock(PickingSupport.class);
    controller.setPickingSupport(picking);
    controller.setNode(node);

    runOnFxThreadAndWait(() -> Event.fireEvent(node,
        new MouseEvent(MouseEvent.MOUSE_PRESSED, 100, 150, 100, 150, MouseButton.PRIMARY, 1,
            false, false, false, false, true, false, false, false, false, false, null)));

    View view = chart.getView();
    Graph graph = chart.getScene().getGraph();

    // Then objects are picked at 100,450 from the bottom left corner, as GL expects
    verify(picking).pickObjects(any(IPainter.class), eq(view), eq(graph),
        any(IntegerCoord2d.class));
    verify(view).projectMouse(100, 450);
    Assert.assertTrue("camera handler must still be invoked", cameraHandled.get());
  }

  protected Chart mockChart() {
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.getRendererHeight()).thenReturn(600);
    View view = mock(View.class);
    when(view.getPainter()).thenReturn(mock(IPainter.class));
    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(mock(Graph.class));

    Chart chart = mock(Chart.class);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getView()).thenReturn(view);
    when(chart.getScene()).thenReturn(scene);
    return chart;
  }

  private static void runOnFxThreadAndWait(Runnable body) throws Exception {
    Throwable[] err = new Throwable[1];
    CountDownLatch done = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        body.run();
      } catch (Throwable t) {
        err[0] = t;
      } finally {
        done.countDown();
      }
    });
    done.await();
    if (err[0] != null) {
      throw new RuntimeException(err[0]);
    }
  }
}
