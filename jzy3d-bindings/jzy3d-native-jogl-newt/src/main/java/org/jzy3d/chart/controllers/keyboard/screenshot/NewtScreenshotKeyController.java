package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.io.IOException;
import org.jzy3d.chart.Chart;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/**
 * Saves a screenshot in PNG format once key S is pressed.
 * 
 */
public class NewtScreenshotKeyController extends AbstractScreenshotKeyController
    implements KeyListener, IScreenshotKeyController {
  public NewtScreenshotKeyController(Chart chart, String outputFile) {
    super(chart, outputFile);
  }

  @Override
  public void keyReleased(KeyEvent e) {
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
  public void keyPressed(KeyEvent e) {}
}
