package org.jzy3d.chart.controllers.keyboard.camera;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.maths.Coord2d;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;


public class NewtCameraKeyController extends AbstractCameraController
    implements KeyListener, ICameraKeyController {

  public NewtCameraKeyController() {}

  public NewtCameraKeyController(Chart chart) {
    register(chart);
  }

  @Override
  public void register(Chart chart) {
    super.register(chart);
    chart.getCanvas().addKeyController(this);
  }

  @Override
  public void dispose() {
    for (Chart c : targets) {
      c.getCanvas().removeKeyController(this);
    }
    super.dispose();
  }

  /*********************************************************/

  @Override
  public void keyPressed(KeyEvent e) {
    // rotation
    if (!e.isShiftDown()) {
      Coord2d move = new Coord2d();
      float offset = 0.1f;
      switch (e.getKeyCode()) {
        case KeyEvent.VK_DOWN:
          move.y = move.y + offset;
          rotate(move);
          break;
        case KeyEvent.VK_UP:
          move.y = move.y - offset;
          rotate(move);
          break;
        case KeyEvent.VK_LEFT:
          move.x = move.x - offset;
          rotate(move);
          break;
        case KeyEvent.VK_RIGHT:
          move.x = move.x + offset;
          rotate(move);
          break;
        default:
          break;
      }
    }
    // zoom
    else {
      switch (e.getKeyCode()) {
        // shift
        case KeyEvent.VK_DOWN:
          shift(0.1f);
          break;
        case KeyEvent.VK_UP:
          shift(-0.1f);
          break;
        // zoom
        case KeyEvent.VK_LEFT:
          zoomZ(0.9f);
          break;
        case KeyEvent.VK_RIGHT:
          zoomZ(1.1f);
          break;
        default:
          break;
      }
    }
  }

  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}
}
