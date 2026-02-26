package org.jzy3d.demos.waterfall;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.plot3d.builder.concrete.WaterfallTessellator;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class WaterfallDemo extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    AnalysisLauncher.open(new WaterfallDemo());
  }

  @Override
  public void init() {

    float[] x = new float[80];

    for (int i = 0; i < x.length; i++) {
      x[i] = -3f + 6f * ((float) i / (x.length - 1));
    }

    float[] y = new float[40];

    for (int i = 0; i < y.length; i++) {
      y[i] = -3f + 2f * ((float) i / (y.length - 1));
    }

    float[] z = getZ(x, y);

    WaterfallTessellator waterfall = new WaterfallTessellator();

    Shape build = waterfall.build(x, y, z);
    build.setColorMapper(new ColorMapper(new ColorMapRainbow(), build.getBounds().getZmin(),
        build.getBounds().getZmax(), new Color(1, 1, 1, 1.0f)));

    // Create a chart
    chart = AWTChartFactory.chart(Quality.Intermediate());
    chart.getScene().getGraph().add(build);
    chart.getView();
  }

  private float[] getZ(float[] x, float[] y) {
    float[] z = new float[x.length * y.length];

    for (int i = 0; i < y.length; i++) {
      for (int j = 0; j < x.length; j++) {
        z[j + (x.length * i)] = (float) f((double) x[j], (double) y[i]);
      }
    }
    return z;
  }

  private double f(double x, double y) {
    return x * Math.sin(x * y);
  }
}
