

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.NewtChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.io.xls.monitor.MonitorXLS;
import org.jzy3d.maths.Range;
import org.jzy3d.monitor.Monitor;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Demo an AWT chart using jGL {@link GLCanvas} for CPU rendering (instead of GPU rendering).
 * 
 * @author martin
 *
 */
public class SurfaceEmulGL_ReportXLS_Dump {

  static final float ALPHA_FACTOR = 0.55f;// .61f;

  public static void main(String[] args) {
    Shape surface = surface();

    EmulGLChartFactory factory = new EmulGLChartFactory();

    Quality q = Quality.Advanced(); 
    Chart chart = factory.newChart(q);
    chart.add(surface);
    
    EmulGLCanvas c = (EmulGLCanvas) chart.getCanvas();
    c.setProfileDisplayMethod(true);
    c.getGL().setAutoAdaptToHiDPI(true);
    
    
    Monitor monitor = new MonitorXLS();
    c.add(monitor);
    
    FrameAWT frame = (FrameAWT) chart.open();
    
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        try {
          monitor.dump("target/monitor.xls");
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    
    //Psy4j p = new Psy4j();
    

    // --------------------------------
    
    chart.setAnimated(true);


    CameraThreadController rotation = new CameraThreadController(chart);
    rotation.setStep(0.025f);
    rotation.setUpdateViewDefault(true);//!chart.getQuality().isAnimated());

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
    mouse.addSlaveThreadController(rotation);
    mouse.setUpdateViewDefault(!chart.getQuality().isAnimated()); // keep to false otherwise double rendering

    // --------------------------------
    
    ChartFactory monitoringFactory = new NewtChartFactory();

    //Quality q = Quality.Advanced; 
    Chart monitoringChart = monitoringFactory.newChart(q);
    monitoringChart.add(new MonitorScatter(monitor, c));
    
    //monitoringChart.view2d();
    monitoringChart.setAnimated(true);

    monitoringChart.open();

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
