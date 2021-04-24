package org.jzy3d.svm.demos;

import org.instantsvm.Parameters;
import org.instantsvm.regression.RegressionInputs;
import org.instantsvm.regression.RegressionSVM;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.SwingChartLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalTessellator;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.svm.editors.RegressionParamsEditor;
import org.jzy3d.svm.tesselation.SvmGrid;
import org.jzy3d.svm.tesselation.SvmMapper;
import org.jzy3d.svm.utils.Conversion;

public abstract class Abstract3dDemo {
  public static void openChart(Chart chart) {
    ChartLauncher.openChart(chart, "Svm3D ");
  }

  public static void openChart(Chart chart, Parameters params) {
    ChartLauncher.openChart(chart, "InstantSVM & Svm3D ");
    SwingChartLauncher.openPanel(new RegressionParamsEditor(params),
        new Rectangle(100, 100, 800, 600), "Regression params");
  }

  public static void scale(Coord3d[] pts, float xfact, float yfact, float zfact) {
    for (int i = 0; i < pts.length; i++) {
      pts[i].x *= xfact;
      pts[i].y *= yfact;
      pts[i].y *= zfact;
    }
  }

  /****************** CHART GENERATION ********************/

  public static Chart getRegressionChart(Coord3d[] values, Parameters parameters) {
    SvmMapper mapper = new SvmMapper(values, parameters);

    mapper.getSvm().getParameters().print();
    return getRegressionChart(mapper, values);
  }

  public static Chart getRegressionChart(RegressionSVM svm, RegressionInputs inputs) {
    Coord3d[] values = Conversion.toCoord3d(inputs);
    SvmMapper mapper = new SvmMapper(svm);
    return getRegressionChart(mapper, values);
  }

  protected static Chart getRegressionChart(SvmMapper mapper, Coord3d[] values) {
    Quality q = Quality.Advanced();
    q.setSmoothPoint(true);
    Chart chart = new AWTChartFactory().newChart(q);

    // shape
    BoundingBox3d b = Conversion.getBounds(values);
    System.out.println(b);
    final Shape s = getRingSurface(mapper, b.getXRange(), b.getYRange(), b.getZRange(), 50);
    chart.getScene().getGraph().add(s);

    // scatter
    loadScatter(chart, values);

    return chart;
  }

  /* 3d OBJECTS GENERATION */

  public static Scatter loadScatter(Chart chart, Coord3d[] coords) {
    Scatter scatter = new Scatter(coords, Color.GREEN.mulSelf(1.2f), 20);
    chart.getScene().getGraph().add(scatter);
    return scatter;
  }

  public static Shape getRingSurface(Mapper mapper, Range xrange, Range yrange, Range zrange,
      int steps) {
    ColorMapper cmapper = new ColorMapper(new ColorMapRainbow(), zrange.getMin(), zrange.getMax(),
        new Color(1, 1, 1, .5f));
    RingInterpolator tesselator =
        new RingInterpolator(0, xrange.getMax(), cmapper, new Color(1f, 1f, 1f));
    SvmGrid grid = new SvmGrid(xrange, steps, yrange, steps);

    Shape surface = (Shape) tesselator.build(grid.apply(mapper));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

  public static Shape getSquareSurface(Chart chart, Mapper mapper, Range xrange, Range yrange,
      int steps) {
    OrthonormalTessellator tesselator = new OrthonormalTessellator();
    SvmGrid grid = new SvmGrid(xrange, steps, yrange, steps);
    Shape surface = (Shape) tesselator.build(grid.apply(mapper));

    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }
}
