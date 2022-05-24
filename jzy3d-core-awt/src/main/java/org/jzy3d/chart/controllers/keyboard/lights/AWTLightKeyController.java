package org.jzy3d.chart.controllers.keyboard.lights;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.lights.Light;

public class AWTLightKeyController implements KeyListener, ILightKeyController {

  public AWTLightKeyController(Chart chart) {
    super();
    this.chart = chart;
  }

  public AWTLightKeyController(Chart chart, int lightId) {
    super();
    this.chart = chart;
    this.lightId = lightId;
  }

  public AWTLightKeyController(Chart chart, Light light) {
    super();
    this.chart = chart;
    this.lightId = light.getId();
  }


  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyChar()) {
      case KeyEvent.VK_2:
        chart.getScene().getLightSet().get(lightId).getPosition().x -= 10;
        chart.render();
        break;
      case KeyEvent.VK_8:
        chart.getScene().getLightSet().get(lightId).getPosition().x += 10;
        chart.render();
        break;
      case KeyEvent.VK_4:
        chart.getScene().getLightSet().get(lightId).getPosition().y -= 10;
        chart.render();
        break;
      case KeyEvent.VK_6:
        chart.getScene().getLightSet().get(lightId).getPosition().y += 10;
        chart.render();
        break;
      case KeyEvent.VK_9:
        chart.getScene().getLightSet().get(lightId).getPosition().z += 10;
        chart.render();
        break;
      case KeyEvent.VK_7:
        chart.getScene().getLightSet().get(lightId).getPosition().z -= 10;
        chart.render();
        break;

      // KeyEvent.;
      default:
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}

  protected Chart chart;
  protected int lightId = 0;

}
