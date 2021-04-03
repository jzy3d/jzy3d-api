package org.jzy3d.demos.surface;

import java.io.File;
import java.io.IOException;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;
import jgl.GLCanvas;


/**
 * Demo an AWT chart using jGL {@link GLCanvas} for CPU rendering (instead of GPU rendering).
 * 
 * @author martin
 *
 */
public class SurfaceDemoEmulGL {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    Shape surface = surface();

    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced; 
    Chart chart = factory.newChart(q);
    chart.add(surface);
    
    EmulGLCanvas c = (EmulGLCanvas) chart.getCanvas();
    c.setProfileDisplayMethod(true);
    c.getGL().setAutoAdaptToHiDPI(true);
    
    //chart.getAxisLayout().setFont(Font.Helvetica_18);
    ((TextBitmapRenderer)((AxisBox)chart.getView().getAxis()).getTextRenderer()).setFont(Font.TimesRoman_10);
    chart.open();

    // --------------------------------
    
    chart.setAnimated(true);
    chart.addMouseCameraController();


    try {
      chart.screenshot(new File("target/" + SurfaceDemoEmulGL.class.getSimpleName() + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
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

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, ALPHA_FACTOR));// 0.65f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    surface.setWireframeWidth(1);
    return surface;
  }

}
