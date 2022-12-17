package org.jzy3d.javafx;

import java.util.Date;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.javafx.controllers.keyboard.JavaFXCameraKeyController;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.javafx.controllers.mouse.JavaFXMousePickingController;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class AbstractJavaFXPainterFactory extends AWTPainterFactory {
  
  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    Dimension dim = getOffscreenDimension();
    return new OffscreenCanvas(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities(), dim.width,
        dim.height);
  }


  @Override
  public ICameraMouseController newMouseCameraController(Chart chart) {
    ICameraMouseController mouse = new JavaFXCameraMouseController(chart, null);
    return mouse;
  }

  @Override
  public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
    IMousePickingController mouse = new JavaFXMousePickingController(chart, clickWidth);
    return mouse;
  }

  @Override
  public ICameraKeyController newKeyboardCameraController(Chart chart) {
    ICameraKeyController key = new JavaFXCameraKeyController(chart, null);
    return key;
  }

  /** TODO : replace by a JavaFXScreenshotKeyController */
  @Override
  public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
    // trigger screenshot on 's' letter
    String file =
        SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
    IScreenshotKeyController screenshot;

    // if (!chart.getWindowingToolkit().equals("newt"))
    screenshot = new AWTScreenshotKeyController(chart, file);
    // else
    // screenshot = new NewtScreenshotKeyController(chart, file);

    screenshot.addListener(new IScreenshotEventListener() {
      @Override
      public void failedScreenshot(String file, Exception e) {
        System.out.println("Failed to save screenshot:");
        e.printStackTrace();
      }

      @Override
      public void doneScreenshot(String file) {
        System.out.println("Screenshot: " + file);
      }
    });
    return screenshot;
  }

  public static String SCREENSHOT_FOLDER = "./data/screenshots/";

}
