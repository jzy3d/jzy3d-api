package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.legends.colorbars.IColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;

public class AWTChart extends Chart {
  public AWTChart(IChartFactory components, Quality quality) {
    super(components, quality);
  }

  protected AWTChart() {
    super();
  }

  public void add(AWTRenderer2d renderer2d) {
    addRenderer(renderer2d);
  }

  public void addRenderer(AWTRenderer2d renderer2d) {
    getView().addRenderer2d(renderer2d);
  }
  
  public void remove(AWTRenderer2d renderer2d) {
    removeRenderer(renderer2d);
  }


  public void removeRenderer(AWTRenderer2d renderer2d) {
    getView().removeRenderer2d(renderer2d);
  }

  public AWTColorbarLegend colorbar(Drawable drawable) {
    return colorbar(drawable, getView().getAxis().getLayout());
  }

  public AWTColorbarLegend colorbar(Drawable drawable, AxisLayout layout) {
    AWTColorbarLegend colorbar = new AWTColorbarLegend(drawable, layout, layout.getMainColor(), view.getBackgroundColor());
    drawable.setLegend(colorbar);
    return colorbar;
  }

  @Override
  public AWTColorbarLegend getColorbar() {
    AWTColorbarLegend bar = (AWTColorbarLegend)super.getColorbar();
    return bar;
  }

  
  @Override
  public AWTView getView() {
    return (AWTView)super.getView();
  }
}

