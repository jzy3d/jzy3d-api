package org.jzy3d.chart.factories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class SwingPainterFactory extends AWTPainterFactory {
  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LogManager.getLogger(SwingPainterFactory.class);


  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    boolean traceGL = false;
    boolean debugGL = false;

    return new CanvasSwing(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities(), traceGL, debugGL);
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FrameSwing(chart, bounds, title);
  }


}
