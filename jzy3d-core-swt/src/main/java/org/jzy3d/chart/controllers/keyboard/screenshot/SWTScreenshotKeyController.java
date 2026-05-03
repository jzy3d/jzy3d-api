package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.io.IOException;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.jzy3d.chart.Chart;

/**
 * Saves a screenshot in PNG format once the {@code S} key is pressed.
 *
 * SWT counterpart of {@code AWTScreenshotKeyController}.
 */
public class SWTScreenshotKeyController extends AbstractScreenshotKeyController
    implements KeyListener, IScreenshotKeyController {

  public SWTScreenshotKeyController(Chart chart, String outputFile) {
    super(chart, outputFile);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // SWT does not have keyTyped — match the AWT controller by triggering on the lowercase 's'
    // character so the behavior is identical.
    if (e.character == 's') {
      try {
        screenshot(chart, outputFile);
        fireDone(outputFile);
      } catch (IOException ex) {
        fireError(outputFile, ex);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
