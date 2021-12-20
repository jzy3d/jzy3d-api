package org.jzy3d.chart.factories;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.bridge.awt.FrameAWT;
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
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeViewOverlay;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.NativeViewAndColorbarsLayout;
import com.jogamp.opengl.GLCapabilities;

public class AWTPainterFactory extends NativePainterFactory implements IPainterFactory {

  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LogManager.getLogger(AWTPainterFactory.class);

  public AWTPainterFactory() {
    super();
  }

  public AWTPainterFactory(GLCapabilities capabilities) {
    super(capabilities);
  }

  @Override
  public IViewOverlay newViewOverlay() {
    return new AWTNativeViewOverlay();
  }

  @Override
  public IViewportLayout newViewportLayout() {
    return new NativeViewAndColorbarsLayout();
    //return new ViewAndColorbarsLayout();
  }

  /** Provide AWT Texture loading for screenshots */
  @Override
  public Renderer3d newRenderer3D(View view) {
    return new AWTRenderer3d(view, traceGL, debugGL);
  }

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {

    if (isOffscreen()) {
      return newOffscreenCanvas(factory, scene, quality);
    } else {

      return new CanvasAWT(factory, scene, quality, getCapabilities());
    }
  }

  protected ICanvas newOffscreenCanvas(IChartFactory factory, Scene scene, Quality quality) {
    Dimension dim = getOffscreenDimension();
    return new OffscreenCanvas(factory, scene, quality, getCapabilities(), dim.width, dim.height);
  }



  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new AWTCameraMouseController(chart);
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    return new AWTMousePickingController(chart, clickWidth);
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    return new AWTCameraKeyController(chart);
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FrameAWT(chart, bounds, title, null);
  }

  @Override
  public IFrame newFrame(Chart chart) {
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
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
}
