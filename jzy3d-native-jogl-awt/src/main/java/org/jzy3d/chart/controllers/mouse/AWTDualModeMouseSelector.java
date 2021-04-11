package org.jzy3d.chart.controllers.mouse;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.selection.AWTAbstractMouseSelector;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;


/**
 * A utility to toggle between
 * <ul>
 * <li>the main default mouse controller {@link AWTCameraMouseController}, used to control viewpoint
 * <li>another custom mouse controller, such as a selection mouse utility
 * </ul>
 * 
 * Uses a {@link KeyListener} to toggle between the two modes.
 * 
 * @author Martin Pernollet
 */
public class AWTDualModeMouseSelector {
  public AWTDualModeMouseSelector(Chart chart, AWTAbstractMouseSelector alternativeMouse) {
    build(chart, alternativeMouse);
  }

  public Chart build(final Chart chart, AWTAbstractMouseSelector alternativeMouse) {
    this.chart = chart;
    this.mouseSelection = alternativeMouse;

    // Create and add controllers
    threadCamera = new CameraThreadController(chart);
    mouseCamera = new AWTCameraMouseController(chart);
    mouseCamera.addSlaveThreadController(threadCamera);
    chart.getCanvas().addKeyController(buildToggleKeyListener(chart));
    releaseCam(); // default mode is selection

    message = MESSAGE_SELECTION_MODE;
    messageRenderer = buildMessageRenderer();
    getAWTChart(chart).addRenderer(messageRenderer);
    return chart;
  }

  private AWTChart getAWTChart(final Chart chart) {
    return (AWTChart) chart;
  }

  public KeyListener buildToggleKeyListener(final Chart chart) {
    return new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
          case 'c':
            releaseCam();
            holding = false;
            message = MESSAGE_SELECTION_MODE;
            break;
          default:
            break;
        }
        chart.render(); // update message display
      }

      @Override
      public void keyTyped(KeyEvent e) {
        if (!holding) {
          switch (e.getKeyChar()) {
            case 'c':
              useCam();
              mouseSelection.clearLastSelection();
              holding = true;
              message = MESSAGE_ROTATION_MODE;
              break;
            default:
              break;
          }
          chart.render();
        }
      }

      protected boolean holding = false;
    };
  }

  public AWTRenderer2d buildMessageRenderer() {
    return new AWTRenderer2d() {
      @Override
      public void paint(Graphics g, int canvasWidth, int canvasHeight) {
        if (displayMessage && message != null) {
          g.setColor(java.awt.Color.RED);
          g.drawString(message, 10, 30);
        }
      }
    };
  }

  protected void useCam() {
    mouseSelection.unregister();
    chart.addController(mouseCamera);
  }

  protected void releaseCam() {
    chart.removeController(mouseCamera);
    mouseSelection.register(chart);
  }

  protected Chart chart;
  protected AWTRenderer2d messageRenderer;

  protected CameraThreadController threadCamera;
  protected AWTCameraMouseController mouseCamera;
  protected AWTAbstractMouseSelector mouseSelection;

  protected boolean displayMessage = true;
  protected String message;

  public static String MESSAGE_SELECTION_MODE =
      "Current mouse mode: selection (hold 'c' to switch to camera mode)";
  public static String MESSAGE_ROTATION_MODE =
      "Current mouse mode: camera (release 'c' to switch to selection mode)";
}
