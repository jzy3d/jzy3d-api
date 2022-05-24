package org.jzy3d.chart;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.maths.Rectangle;

public class ChartLauncher {
  static Logger logger = LogManager.getLogger(ChartLauncher.class);

  public static String SCREENSHOT_FOLDER = "./data/screenshots/";

  public static ICameraMouseController openChart(Chart chart) {
    return openChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d", true);
  }

  public static ICameraMouseController openChart(Chart chart, Rectangle rectangle) {
    return openChart(chart, rectangle, "Jzy3d", true);
  }

  public static ICameraMouseController openChart(Chart chart, String title) {
    return openChart(chart, new Rectangle(0, 0, 800, 600), title, true);
  }

  public static ICameraMouseController openChart(Chart chart, Rectangle bounds, String title) {
    return openChart(chart, bounds, title, true);
  }

  public static ICameraMouseController openChart(Chart chart, Rectangle bounds, String title,
      boolean allowSlaveThreadOnDoubleClick) {
    return openChart(chart, bounds, title, allowSlaveThreadOnDoubleClick, false);
  }

  public static ICameraMouseController openChart(final Chart chart, Rectangle bounds,
      final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
    ICameraMouseController mouse =
        configureControllers(chart, title, allowSlaveThreadOnDoubleClick, startThreadImmediatly);
    chart.render();
    chart.open(title, bounds);
    return mouse;
  }

  public static ICameraMouseController configureControllers(final Chart chart, final String title,
      boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
    chart.addKeyboardCameraController();
    chart.addKeyboardScreenshotController();
    return chart.addMouseCameraController();
  }

  public static void openStaticChart(Chart chart) {
    openStaticChart(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
  }

  public static void openStaticChart(Chart chart, Rectangle bounds, String title) {
    chart.render();
    chart.open(title, bounds);
  }

  public static void instructions() {
    System.out.println(makeInstruction());
    System.out.println("------------------------------------");
  }

  public static String makeInstruction() {
    StringBuffer sb = new StringBuffer();
    sb.append("Rotate     : Left click and drag mouse\n");
    sb.append("Scale      : Roll mouse wheel\n");
    sb.append("Z Shift    : Right click and drag mouse\n");
    sb.append("Animate    : Double left click\n");
    sb.append("Screenshot : Press 's'\n");
    return sb.toString();
  }

  /* SCREENSHOT */

  public static void screenshot(Chart chart, String filename) throws IOException {
    chart.screenshot(new File(filename));
    logger.info("Dumped screenshot in: " + filename);
  }
}
