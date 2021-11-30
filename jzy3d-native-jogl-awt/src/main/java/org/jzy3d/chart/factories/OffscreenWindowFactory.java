package org.jzy3d.chart.factories;

import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class OffscreenWindowFactory extends AWTPainterFactory {
  @Override
  public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
    Dimension dim = getOffscreenDimension();
    return new OffscreenCanvas(factory, scene, quality,
        ((NativePainterFactory) factory.getPainterFactory()).getCapabilities(), dim.width,
        dim.height);
  }
}
