package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

/**
 * A scatter plot supporting a List<Coord3d> as input.
 * 
 * @author Martin Pernollet
 * 
 */
public class AWTScatterMultiColorList extends ScatterMultiColorList implements IMultiColorable {

  public AWTScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width) {
    super(coordinates, mapper, width);
    // TODO Auto-generated constructor stub
  }

  public AWTScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper) {
    super(coordinates, mapper);
    // TODO Auto-generated constructor stub
  }

  public void enableColorBar(ITickProvider provider, ITickRenderer renderer) {
    setLegend(new AWTColorbarLegend(this, provider, renderer));
    setLegendDisplayed(true);
  }
}
