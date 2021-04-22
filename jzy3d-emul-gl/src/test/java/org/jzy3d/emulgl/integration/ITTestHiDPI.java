package org.jzy3d.emulgl.integration;

import java.awt.Toolkit;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.EmulGLPainterFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;


/**
 * Test non reg of colorbar and text layout with HiDPI EmulGL charts.
 * 
 * Seem to require Java 9 whereas main build is intentionaly frozen at Java 8 for compatibility
 * reason to existing software integrating the library.
 * 
 * For this reason, the @Test is commented and this is a runnable program.
 */
public class ITTestHiDPI {

  //@Test
  public static void main(String[] a) {
    int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
    System.out.println("dpi : "+dpi);
    
    Shape surface = surface();

    IPainterFactory painter = new EmulGLPainterFactory();
    painter.setOffscreen(800, 600);
    ChartFactory factory = new EmulGLChartFactory(painter);
    

    Quality q = Quality.Advanced(); 
    q.setPreserveViewportSize(false); // Enable HiDPI if available on computer
    
    Chart chart = factory.newChart(q);
    chart.add(surface);
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    TextBitmapRenderer tbr = ((TextBitmapRenderer)((AxisBox)chart.getView().getAxis()).getTextRenderer());
    tbr.setFont(Font.TimesRoman_24); 
    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // --------------------------------
    ((EmulGLCanvas)chart.getCanvas()).getGL().setAutoAdaptToHiDPI(true);
    chart.open(800, 600);
    chart.setAnimated(true);
    chart.addMouseCameraController();
    


 //   ChartTester tester = new ChartTester();
  //  tester.assertSimilar(chart, ChartTester.EXPECTED_IMAGE_FOLDER + this.getClass().getSimpleName() + ".png");
  }


  private static Shape surface() {
    SurfaceBuilder builder = new SurfaceBuilder();
    
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps), mapper);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, 0.5f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }

}
