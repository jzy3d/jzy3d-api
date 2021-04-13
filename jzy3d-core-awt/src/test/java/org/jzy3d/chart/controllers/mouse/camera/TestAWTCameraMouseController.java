package org.jzy3d.chart.controllers.mouse.camera;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import org.junit.Test;

public class TestAWTCameraMouseController {
  @Test
  public void rateLimiter() {
    
    AWTCameraMouseController c = new AWTCameraMouseController();
    
    // Mock xy() method to verify it is bypassed in case of rate limit and mouse move rate below rate limit
    
    
    // --------------------------------
    // When
    Component sourceCanvas = new JPanel();///*(Component) chart.getCanvas()*/
    
    int x = 300;
    int y = 300;
    //MouseEvent e = mouseEvent(sourceCanvas, x, y);

    
    c.mouseDragged(mouseEvent(sourceCanvas, x, y));
  }

  private MouseEvent mouseEvent(Component sourceCanvas, int x, int y) {
    MouseEvent e =
        new MouseEvent(sourceCanvas, 0, 0, 0, x, y, 100, 100, 1, false, 0);
    return e;
  }

}
