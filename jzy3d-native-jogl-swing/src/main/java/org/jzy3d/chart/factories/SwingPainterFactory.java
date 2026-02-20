package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingPainterFactory extends AWTPainterFactory {
  public static String SCREENSHOT_FOLDER = "./data/screenshots/";
  static Logger logger = LoggerFactory.getLogger(SwingPainterFactory.class);


  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    return new CanvasSwing(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities());
  }

  @Override
  public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
    return new FrameSwing(chart, bounds, title);
  }


}
