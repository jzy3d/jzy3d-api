package org.jzy3d.plot2d.primitives;

import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Histogram;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.StaticTickProvider;

public class Histogram2d {
  protected Histogram model;
  protected Composite drawable;
  String ylabel = "Count";
  String xlabel = "Value";

  public Histogram2d(Histogram model) {
    setModel(model);
  }

  public Histogram2d(Histogram model, String xlabel, String ylabel) {
    this(model);
    this.ylabel = ylabel;
    this.xlabel = xlabel;
  }

  /** Set global chart view settings to best draw this histogram. */
  public void layout(Chart chart) {
    IAxisLayout layout = chart.getAxisLayout();
    int ymax = getModel().computeMaxCount();
    double[] ticks = {0, ymax / 4, ymax / 2, ymax / 2 + ymax / 4, ymax};
    layout.setYTickProvider(new StaticTickProvider(ticks));
    layout.setYAxisLabel(ylabel);
    layout.setXAxisLabel(xlabel);
  }

  public void addTo(Chart chart) {
    chart.add(drawable);
    layout(chart);
  }

  public void setModel(Histogram model) {
    this.model = model;
    this.drawable = buildDrawable(model);
  }

  public Histogram getModel() {
    return model;
  }

  public Composite getDrawable() {
    return drawable;
  }

  protected Composite buildDrawable(Histogram model) {
    Composite c = new Composite() {};
    for (int i = 0; i < model.ranges().length; i++) {
      Range range = model.ranges()[i];
      int count = model.getCount(i);

      Polygon p = makeCountBar(range, count);
      c.add(p);
    }
    return c;
  }

  private Polygon makeCountBar(Range range, int count) {
    float z = 0;

    Coord3d c1 = new Coord3d(range.getMin(), 0, z);
    Coord3d c2 = new Coord3d(range.getMin(), count, z);
    Coord3d c3 = new Coord3d(range.getMax(), count, z);
    Coord3d c4 = new Coord3d(range.getMax(), 0, z);

    Polygon p = new Polygon();
    p.add(new Point(c1));
    p.add(new Point(c2));
    p.add(new Point(c3));
    p.add(new Point(c4));
    p.setColor(Color.MAGENTA);
    p.setWireframeColor(Color.WHITE);
    return p;
  }
}
