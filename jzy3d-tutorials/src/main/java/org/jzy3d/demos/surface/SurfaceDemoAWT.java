package org.jzy3d.demos.surface;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.AWTPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * Demo an AWT chart using JOGL {@link GLCanvas}.
 * 
 * @author martin
 */
public class SurfaceDemoAWT extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    SurfaceDemoAWT d = new SurfaceDemoAWT();
    AnalysisLauncher.open(d);
  }

  @Override
  public void init() {
    // Define a function to plot
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);
    surface
        .setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);

    // Create a chart
    //GLCapabilities c = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    //IPainterFactory p = new AWTPainterFactory(c);
    IChartFactory f = new AWTChartFactory();

    chart = f.newChart(Quality.Advanced().setHiDPIEnabled(true));
    chart.getScene().getGraph().add(surface);
  }
}
