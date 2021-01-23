package org.jzy3d.colors;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.plot3d.primitives.Shape;

public class OrderingScoreColormapper {
  public static void setup(final Shape surface, final Chart chart) {
    IColorMap colormap = new ColorMapRainbow();
    colormap.setDirection(false);
    ColorMapper colormapper = new OrderingStrategyScoreColorMapper(colormap,
        new AlwaysPrePostDrawPolicy(), chart.getScene().getGraph(), new Color(1, 1, 1, 1f));
    surface.setColorMapper(colormapper);
  }

  public static void standard(final Shape surface) {
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, 1f)));
  }
}
