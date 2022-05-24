package org.jzy3d.chart.factories;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.NewtScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.NewtMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeViewOverlay;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import com.jogamp.opengl.GLCapabilities;

public class SWTPainterFactory extends NativePainterFactory implements IPainterFactory {
  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LogManager.getLogger(SWTPainterFactory.class);

  public SWTPainterFactory() {
    super();
  }

  public SWTPainterFactory(GLCapabilities capabilities) {
    super(capabilities);
  }

  @Override
  public IViewOverlay newViewOverlay() {
    return new AWTNativeViewOverlay();
  }

  @Override
  public IViewportLayout newViewportLayout() {
    return new ViewAndColorbarsLayout();
  }

  /** Provide AWT Texture loading for screenshots */
  @Override
  public Renderer3d newRenderer3D(View view) {
    return new AWTRenderer3d(view, traceGL, debugGL);
  }

  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    boolean traceGL = false;
    boolean debugGL = false;

    return new CanvasNewtSWT(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities(), traceGL, debugGL);
  }


  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    return new NewtCameraMouseController(chart);
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    return new NewtMousePickingController(chart, clickWidth);
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
    IScreenshotKeyController screenshot = new NewtScreenshotKeyController(chart, file);

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
    return new NewtCameraKeyController(chart);
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return null;// new FrameSWTBridge(chart, bounds, title);
  }

  @Override
  public IFrame newFrame(Chart chart) {
    return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
  }

}
