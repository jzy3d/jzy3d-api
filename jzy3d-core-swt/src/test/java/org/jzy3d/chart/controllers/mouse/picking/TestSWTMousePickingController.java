package org.jzy3d.chart.controllers.mouse.picking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class TestSWTMousePickingController {
  Display display;
  Shell shell;

  @Before
  public void setup() {
    display = Display.getCurrent();
    if (display == null) {
      display = new Display();
    }
    shell = new Shell(display);
  }

  @After
  public void teardown() {
    if (shell != null && !shell.isDisposed()) {
      shell.dispose();
    }
    if (display != null && !display.isDisposed()) {
      display.dispose();
    }
  }

  @Test
  public void whenMouseDown_ThenPickAtFlippedCoordinates() {
    // Given a chart
    ICanvas canvas = mock(ICanvas.class);
    when(canvas.getRendererHeight()).thenReturn(600);
    IPainter painter = mock(IPainter.class);
    View view = mock(View.class);
    when(view.getPainter()).thenReturn(painter);
    Graph graph = mock(Graph.class);
    Scene scene = mock(Scene.class);
    when(scene.getGraph()).thenReturn(graph);

    Chart chart = mock(Chart.class);
    when(chart.getCanvas()).thenReturn(canvas);
    when(chart.getView()).thenReturn(view);
    when(chart.getScene()).thenReturn(scene);

    SWTMousePickingController controller = new SWTMousePickingController(chart, 7);
    PickingSupport picking = mock(PickingSupport.class);
    controller.setPickingSupport(picking);

    // Then the controller listens to the canvas
    verify(canvas).addMouseController(controller);

    // When pressing the mouse at 100,150 from the top left corner
    Event raw = new Event();
    raw.widget = shell;
    raw.display = display;
    raw.x = 100;
    raw.y = 150;
    controller.mouseDown(new MouseEvent(raw));

    // Then objects are picked at 100,450 from the bottom left corner, as GL expects
    verify(picking).pickObjects(eq(painter), eq(view), eq(graph), any(IntegerCoord2d.class));
    verify(view).projectMouse(100, 450);
    Assert.assertSame(picking, controller.getPickingSupport());
  }
}
