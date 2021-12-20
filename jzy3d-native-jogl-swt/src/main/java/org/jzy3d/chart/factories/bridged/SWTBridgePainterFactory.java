package org.jzy3d.chart.factories.bridged;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.bridge.swt.FrameSWTBridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.chart.factories.SWTPainterFactory;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import com.jogamp.opengl.GLCapabilities;

public class SWTBridgePainterFactory extends SWTPainterFactory implements IPainterFactory {
  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LogManager.getLogger(SWTBridgePainterFactory.class);

  public SWTBridgePainterFactory() {
    super();
  }

  public SWTBridgePainterFactory(GLCapabilities capabilities) {
    super(capabilities);
  }

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    return new CanvasAWT(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities());
  }

  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new AWTCameraMouseController(chart);
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    return new AWTMousePickingController(chart, clickWidth);
  }

  /**
   * Output file of screenshot can be configured using
   * {@link IScreenshotKeyController#setFilename(String)}.
   */
  @Override
  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
    // trigger screenshot on 's' letter
    String file =
        SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
    IScreenshotKeyController screenshot = new AWTScreenshotKeyController(chart, file);

    screenshot.addListener(new IScreenshotEventListener() {
      @Override
      public void failedScreenshot(String file, Exception e) {
        logger.error("Failed to save screenshot to '" + file + "'", e);
      }

      @Override
      public void doneScreenshot(String file) {
        logger.info("Screenshot save to '" + file + "'");
      }
    });
    return screenshot;
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    return new AWTCameraKeyController(chart);
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FrameSWTBridge(chart, bounds, title);
  }
}
