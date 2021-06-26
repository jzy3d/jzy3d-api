package org.jzy3d.demos.surface;

import java.awt.Panel;
import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadControllerWithTime;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.NewtChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.newt.awt.NewtCanvasAWT;

/**
 * Demo an AWT chart using JOGL {@link NewtCanvasAWT} wrapped in an AWT {@link Panel}.
 * 
 * @author martin
 */
public class SurfaceDemoAWTNewt extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    SurfaceDemoAWTNewt d = new SurfaceDemoAWTNewt();
    AnalysisLauncher.open(d);
    // d.getChart().render();
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
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);

    // Create a chart
    IChartFactory f = new NewtChartFactory();

    chart = f.newChart(Quality.Advanced().setAnimated(false));
    chart.getScene().getGraph().add(surface);
    CameraThreadControllerWithTime t = (CameraThreadControllerWithTime)chart.getThread();
    t.setSpeed(60);
  }
}
