package org.jzy3d.tests.integration;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.pickable.PickablePoint;
import org.jzy3d.plot3d.rendering.view.HiDPI;

/**
 * Click on pickable points and verify the expected point is picked, with JOGL and PanamaGL.
 */
public class ITTest_Picking extends ITTest {

  @Test
  public void whenClickOnPickablePoint_ThenPointIsPicked() throws Exception {
    for (WT toolkit : withPanamaGL(WT.Native_Swing)) {
      whenClickOnPickablePoint_ThenPointIsPicked(toolkit);
    }
  }

  protected void whenClickOnPickablePoint_ThenPointIsPicked(WT toolkit) throws Exception {
    System.out.println("ITTest : picking with " + toolkit);

    // Given two pickable points
    Chart chart = chart(toolkit, HiDPI.OFF);

    PickablePoint left = new PickablePoint(new Coord3d(-0.5, 0, 0), Color.RED, 10);
    PickablePoint right = new PickablePoint(new Coord3d(0.5, 0, 0), Color.BLUE, 10);

    AWTMousePickingController mouse = (AWTMousePickingController) chart.addMousePickingController(5);
    mouse.getPickingSupport().registerPickableObject(left, "left");
    mouse.getPickingSupport().registerPickableObject(right, "right");

    List<Object> picked = new ArrayList<>();
    mouse.getPickingSupport().addObjectPickedListener((objects, picking) -> picked.addAll(objects));

    chart.add(left);
    chart.add(right);
    chart.getView().setBoundsManual(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
    chart.view2d();

    chart.open(toolkit.name(), offscreenDimension.width, offscreenDimension.height);
    chart.render();

    try {
      // When clicking on the left point
      click(chart, mouse, left.getCoord());

      // Then
      Assert.assertEquals(toolkit + " : " + picked, List.of("left"), picked);

      // When clicking on the right point
      picked.clear();
      click(chart, mouse, right.getCoord());

      // Then
      Assert.assertEquals(toolkit + " : " + picked, List.of("right"), picked);
    } finally {
      chart.dispose();
    }
  }

  /** Click where the point is displayed, from the AWT thread, as a mouse listener would. */
  protected void click(Chart chart, AWTMousePickingController mouse, Coord3d point)
      throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      IPainter painter = chart.getPainter();
      painter.acquireGL();
      Coord3d screen = chart.getView().getCamera().modelToScreen(painter, point);
      painter.releaseGL();

      int x = Math.round(screen.x);
      int y = chart.getCanvas().getRendererHeight() - Math.round(screen.y);

      Component c = (Component) chart.getCanvas();
      mouse.pick(new MouseEvent(c, MouseEvent.MOUSE_PRESSED, 0, 0, x, y, 1, false));
    });
  }
}
