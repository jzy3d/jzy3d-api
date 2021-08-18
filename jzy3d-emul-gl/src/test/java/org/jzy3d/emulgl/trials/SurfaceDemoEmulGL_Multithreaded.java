package org.jzy3d.emulgl.trials;

import java.io.File;
import java.io.IOException;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.EmulGLPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.MultithreadedGraph;
import org.jzy3d.plot3d.rendering.scene.Scene;


/**
 * Demo an AWT chart using jGL {@link GLCanvas} for CPU rendering (instead of GPU rendering).
 * 
 * Multithreaded rendering yield to inconsistent draw queries of multiple drawing tasks to a single GL instance
 * 
 * one may create GL instances where each owns its state, consistency, and execution of a geometry draw.
 * 
 * but a shared color/depth buffer.
 * 
 * 
 * @author martin
 *
 */
public class SurfaceDemoEmulGL_Multithreaded {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    Shape surface = surface();

    //protected gl_context Context = new gl_context();

    
    EmulGLPainterFactory painter = new EmulGLPainterFactory() {
      @Override
      protected EmulGLCanvas newEmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
        return new EmulGLCanvas(factory, scene, quality) {
          //myGL = new GL();
          
        };
      }

    };
    
    // Using a multi-threaded scene graph
    ChartFactory factory = new EmulGLChartFactory() {
      @Override
      public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
        return new MultithreadedGraph(scene, strategy, sort);
      }

    };

    // --------------------------------

    Quality q = Quality.Advanced(); 
    Chart chart = factory.newChart(q);
    chart.add(surface);
    
    EmulGLCanvas c = (EmulGLCanvas) chart.getCanvas();
    c.setProfileDisplayMethod(true);
    c.getGL().setAutoAdaptToHiDPI(true);
    chart.getAxisLayout().setFont(Font.TimesRoman_10);
    chart.open();

    // --------------------------------
    
    chart.setAnimated(true);


    CameraThreadController rotation = new CameraThreadController(chart);
    rotation.setStep(0.025f);
    rotation.setUpdateViewDefault(true);//!chart.getQuality().isAnimated());

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
    mouse.addSlaveThreadController(rotation);
    mouse.setUpdateViewDefault(!chart.getQuality().isAnimated()); // keep to false otherwise double rendering

    try {
      chart.screenshot(new File("target/" + SurfaceDemoEmulGL_Multithreaded.class.getSimpleName() + ".png"));
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
