package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

public class AWTScatterMultiColor extends ScatterMultiColor implements IMultiColorable {

  public AWTScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper,
      float width) {
    super(coordinates, colors, mapper, width);
    // TODO Auto-generated constructor stub
  }

  public AWTScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper) {
    super(coordinates, colors, mapper);
    // TODO Auto-generated constructor stub
  }

  public AWTScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper) {
    super(coordinates, mapper);
    // TODO Auto-generated constructor stub
  }

  public void enableColorBar(ITickProvider provider, ITickRenderer renderer) {
    setLegend(new AWTColorbarLegend(this, provider, renderer));
    setLegendDisplayed(true);
  }
}
