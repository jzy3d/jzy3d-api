package org.jzy3d.demos;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.SWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.newt.swt.NewtCanvasSWT;

/**
 * Demo an AWT chart using JOGL {@link NewtCanvasSWT} wrapped in a SWT {@link Composite}.
 * 
 * 
 * @author martin
 */
public class SurfaceDemoSWT {

  public static void main(String[] args) {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // Create a chart

    Settings.getInstance().setHardwareAccelerated(true);

    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());

    SWTChartFactory f = new SWTChartFactory(shell);
    Quality q = Quality.Advanced();
    q.setHiDPIEnabled(true);
    Chart chart = f.newChart(q);
    
    // Chart chart = SWTChartFactory.chart(shell);
    chart.getScene().getGraph().add(surface);

    ChartLauncher.openChart(chart);

    shell.setText("name");
    shell.setSize(800, 600);
    shell.open();

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    chart.stopAnimation();
    display.dispose();
  }
}
