package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.EmulGLColorbarLegend;

public class EmulGLChart extends AWTChart{

  public EmulGLChart() {
    super();
  }

  public EmulGLChart(IChartFactory components, Quality quality) {
    super(components, quality);
  }
  
  /*public EmulGLColorbarLegend colorbar(Drawable drawable, AxisLayout layout) {
    EmulGLColorbarLegend colorbar = new EmulGLColorbarLegend(drawable, layout, layout.getMainColor(), view.getBackgroundColor());
    drawable.setLegend(colorbar);
    return colorbar;
  }

  @Override
  public EmulGLColorbarLegend getColorbar() {
    EmulGLColorbarLegend bar = (EmulGLColorbarLegend)super.getColorbar();
    return bar;
  }*/


}
