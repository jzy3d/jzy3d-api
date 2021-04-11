package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import org.jzy3d.chart.Chart;

/**
 * Saves a screenshot in PNG format once key S is pressed.
 */
public class AWTScreenshotKeyController extends AbstractScreenshotKeyController
    implements KeyListener, IScreenshotKeyController {
  public AWTScreenshotKeyController(Chart chart, String outputFile) {
    super(chart, outputFile);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    switch (e.getKeyChar()) {
      case 's':
        try {
          screenshot(chart, outputFile);
          fireDone(outputFile);
        } catch (IOException e1) {
          fireError(outputFile, e1);
        }
      default:
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {}
}
