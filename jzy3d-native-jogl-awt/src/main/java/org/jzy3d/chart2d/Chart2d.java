package org.jzy3d.chart2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ElapsedTimeTickRenderer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

// TODO:
// AXEBOX ticks too long
// AXEBOX Y label sur le côté
// X Labels centrés
//
// Interface de LineSerie fournie par Chart2d package, using x, y float args

public class Chart2d extends AWTNativeChart {
  protected Map<String, Serie2d> series = new HashMap<String, Serie2d>();

  public void layout2d() {
    IAxisLayout axe = getAxisLayout();
    axe.setZAxeLabelDisplayed(false);
    axe.setTickLineDisplayed(false);

    View view = getView();
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.setSquared(true);
    view.getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
  }

  public void asTimeChart(float timeMax, float ymin, float ymax, String xlabel, String ylabel) {
    IAxisLayout axe = getAxisLayout();
    axe.setYAxisLabel(ylabel);
    axe.setXAxisLabel(xlabel);
    axe.setXTickRenderer(new ElapsedTimeTickRenderer());

    View view = getView();
    view.setBoundManual(new BoundingBox3d(0, timeMax, ymin, ymax, -1, 1));
  }

  public Serie2d getSerie(String name, Serie2d.Type type) {
    Serie2d serie = null;
    if (!series.keySet().contains(name)) {
      serie = factory.newSerie(name, type);
      add(serie.getDrawable());
      series.put(name, serie);
    } else {
      serie = series.get(name);
    }
    return serie;
  }

  public void setSerie(Serie2d serie) {
    this.series.put(serie.getName(), serie);
  }

  public void setSeries(Collection<Serie2d> series) {
    for (Serie2d serie : series) {
      setSerie(serie);
    }
  }

  public void setSeries(Map<String, Serie2d> series) {
    this.series.putAll(series);
  }

  /* */

  public Chart2d() {
    this(new Chart2dFactory(), Quality.Advanced().setAnimated(true));
  }

  public Chart2d(IChartFactory factory, Quality quality) {
    super(factory, quality);
    layout2d();
  }
}
