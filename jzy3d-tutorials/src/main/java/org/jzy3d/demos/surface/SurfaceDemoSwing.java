package org.jzy3d.demos.surface;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.awt.GLJPanel;

/**
 * Demo an Swing chart using JOGL {@link GLJPanel}.
 * 
 * @author martin
 */
public class SurfaceDemoSwing extends AbstractAnalysis {
  public SurfaceDemoSwing() {
    super(new SwingChartFactory());
  }

  public static void main(String[] args) throws Exception {
    SurfaceDemoSwing d = new SurfaceDemoSwing();
    AnalysisLauncher.open(d);
  }

  @Override
  public void init() {
    // Define a function to plot
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // Create a chart
    chart = new SwingChartFactory().newChart(Quality.Advanced());
    chart.add(surface);
  }
}
