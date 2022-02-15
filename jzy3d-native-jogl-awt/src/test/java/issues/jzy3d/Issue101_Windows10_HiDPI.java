package issues.jzy3d;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.awt.Rectangle;

/**
 * This reproduce the HiDPI issue on Windows.
 * 
 * This should be ran with a JDK > 8.
 * 
 * https://github.com/jzy3d/jzy3d-api/issues/101
 */
public class Issue101_Windows10_HiDPI extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    Issue101_Windows10_HiDPI d = new Issue101_Windows10_HiDPI();
    d.init();
    IFrame frame = d.getChart().open(600, 600);
    d.getChart().addMouse();
    //AnalysisLauncher.open(d);
    
    FrameAWT f = ((FrameAWT)frame);
    
    /*CanvasAWT canvas = (CanvasAWT)d.getChart().getCanvas();
    
    float[] result = new float[2];
    canvas.getCurrentSurfaceScale(result);
    
    Array.print(result);

    float[] test = {2.0f, 2.0f};
    //canvas.setPixelScale(test);
    
    canvas.getCurrentSurfaceScale(result);
    
    Array.print(result);
    
    System.out.println(canvas.getPixelScale());
    
    canvas.setPixelScale(canvas.getPixelScale());*/

    
    Rectangle r = f.getBounds();
    System.out.println(r);

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
