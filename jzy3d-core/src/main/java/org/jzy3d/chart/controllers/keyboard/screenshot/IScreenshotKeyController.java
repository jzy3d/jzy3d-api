package org.jzy3d.chart.controllers.keyboard.screenshot;

import java.io.IOException;
import org.jzy3d.chart.Chart;

public interface IScreenshotKeyController {

  public String getFilename();

  public void setFilename(String filename);

  public void screenshot(Chart chart, String filename) throws IOException;

  public void addListener(IScreenshotEventListener listener);

  public interface IScreenshotEventListener {
    public void doneScreenshot(String file);

    public void failedScreenshot(String file, Exception e);
  }
}
